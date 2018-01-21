package com.zach.sqlautomodel;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

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
     * Dump file must contain
     *
     * @author zach-bright
     * @param srcFile File SQL dump file to generate models from.
     * @param destDir File Directory to write the models to.
     * @throws FileNotFoundException If dest or src don't exist, or if dest is not directory, or if src is not file.
     * @throws IOException If parsing the SQL dump fails.
     */
    public void generate(File srcFile, File destDir) throws FileNotFoundException, IOException {
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
        List<String> tableStatementList = new ArrayList<>();
        try (BufferedReader sourceReader = new BufferedReader(new FileReader(srcFile))) {
            String curLine = null;
            while ((curLine = sourceReader.readLine()) != null) {
                // We only care if its a table creation statement.
                int createIndex = curLine.contains("CREATE TABLE")
                    ? curLine.indexOf("CREATE TABLE")
                    : curLine.indexOf("create table");
                if (createIndex == -1) {
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

        for (String s : tableStatementList) System.out.println(s);
    }
}
