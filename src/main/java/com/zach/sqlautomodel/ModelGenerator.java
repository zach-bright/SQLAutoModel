package com.zach.sqlautomodel;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;

/**
 * Generates Models by parsing an SQL dump file.
 *
 * @author zach-bright
 */
public class ModelGenerator
{

    /**
     * Generates simple models given a destination and a dump file.
     *
     * @author zach-bright
     * @param packageName String Name of the package the models are for.
     * @param srcFile File SQL dump file to generate models from.
     * @param destDir File Directory to write the models to.
     * @throws IOException If parsing the SQL dump fails or if the src/dest files are not valid.
     * @throws ClassNotFoundException If a column type in the dump file has no corresponding Java type.
     */
    public void generate(String packageName, File srcFile, File destDir) throws IOException, ClassNotFoundException {
        if (!srcFile.exists()) {
            throw new FileNotFoundException("SQL source file does not exist.");
        } else if (!srcFile.isFile()) {
            throw new FileNotFoundException("Source is not a file.");
        }
        if (!destDir.exists()) {
            throw new FileNotFoundException("Destination directory does not exist.");
        } else if (!destDir.isDirectory()) {
            throw new FileNotFoundException("Destination is not a directory.");
        }

        // Parse list of tables from the dump file.
        List<String> tableStatementList = this.parseSQLDumpFile(srcFile);

        // Get all the columns for each string and send to the generator function.
        Map<String, JavaFile> modelFiles = new HashMap<>();
        for (String tableStatement : tableStatementList) {
            String[] firstTwo = tableStatement.split("\\(", 2);
            String createStart = firstTwo[0];
            String columnDeclarations = firstTwo[1];
            int closingParen = columnDeclarations.lastIndexOf(")");
            String closingStatement = columnDeclarations.substring(closingParen + 1, columnDeclarations.length());
            columnDeclarations = columnDeclarations.substring(0, closingParen + 1);

            // Analyze first part of CREATE statement (createStart).
            String tableName = this.findBacktickedName(createStart);

            // Tokenize columns and analyze each.
            StringTokenizer st = new StringTokenizer(columnDeclarations, ",");
            List<FieldSpec> fieldList = new ArrayList<>();
            while (st.hasMoreTokens()) {
                fieldList.add(this.buildFieldFromColumnString(st.nextToken()));
            }

            // Analyze last part of CREATE statement (closingStatement).
            // todo: add closing stuff as comment or metadata for model.

            // Create the table class and JavaFile object.
            TypeSpec tableClass = TypeSpec.classBuilder(tableName)
                    .superclass(AbstractModel.class)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addFields(fieldList)
                    .build();

            // Map each JavaFile to the table name.
            JavaFile jFile = JavaFile.builder(packageName, tableClass).build();
            modelFiles.put(tableName, jFile);
        }

        // Create each model file from the map.
        modelFiles.forEach((name, file) -> {
            File destFile = new File(destDir.getPath() + file);
            try {
                file.writeTo(destFile);
            } catch (IOException e) {
                System.err.println("Model " + name + " failed to write: " + e.getMessage());
            }
        });
    }

    /**
     * Parses an SQL dump file to find all CREATE TABLE statements.
     *
     * @author zach-bright
     * @param srcFile File SQL dump file to parse.
     * @return A list of CREATE TABLE statements.
     * @throws IOException If parsing the dump fails.
     */
    private List<String> parseSQLDumpFile(File srcFile) throws IOException {
        List<String> tableStatementList = new ArrayList<>();
        try (BufferedReader sourceReader = new BufferedReader(new FileReader(srcFile))) {
            String curLine;
            while ((curLine = sourceReader.readLine()) != null) {
                // We only care if its a table creation statement.
                int createIndex = curLine.contains("CREATE TABLE")
                        ? curLine.indexOf("CREATE TABLE")
                        : curLine.indexOf("create table");
                if (createIndex == -1 && !curLine.contains("\\*")) {
                    continue;
                }

                // Write everything into a builder up to and including the CREATE ending string: ).*;
                StringBuilder statement = new StringBuilder(curLine.substring(createIndex));
                while (!(curLine = sourceReader.readLine()).matches("\\).*;")) {
                    statement.append(curLine.trim());
                }
                statement.append(curLine.substring(0, curLine.lastIndexOf(';') + 1));
                tableStatementList.add(statement.toString());
            }
        }
        return tableStatementList;
    }

    /**
     * Builds a field object from the column string.
     *
     * @author zach-bright
     * @param token String Column string to analyze.
     * @return FieldSpec built from the token.
     * @throws ClassNotFoundException If the class in the column string has no corresponding Java class.
     */
    private FieldSpec buildFieldFromColumnString(String token) throws ClassNotFoundException {
        String columnName = this.findBacktickedName(token);
        String typeString = token.split(" ", 3)[1].replaceAll("\\(.*\\)", "");
        Class columnClass = JavaToSQLTypes.lookup(typeString.toUpperCase());
        if (columnClass == null) {
            throw new ClassNotFoundException("No corresponding Java class found for SQL typestring " + typeString);
        }
        return FieldSpec.builder(columnClass, columnName).addModifiers(Modifier.PUBLIC).build();
    }

    /**
     * Utility function to pull a backtick-delimited column or table reference.
     *
     * @author zach-bright
     * @param str String String containing a backtick-delimited reference.
     * @return Reference inside the backticks, or empty string if none found.
     */
    private String findBacktickedName(String str) {
        Matcher backtick = Pattern.compile("`(.+)`").matcher(str);
        if (backtick.find()) {
            return backtick.group(1);
        }
        return "";
    }

}
