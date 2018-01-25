package com.zach.sqlautomodel;

import java.sql.*;
import java.math.BigDecimal;

public enum JavaToSQLTypes {

    TINYINT     (JDBCType.TINYINT, Byte.class),
    SMALLINT    (JDBCType.SMALLINT, Short.class),
    INTEGER     (JDBCType.INTEGER, Integer.class),
    BIGINT      (JDBCType.BIGINT, Long.class),
    DECIMAL     (JDBCType.DECIMAL, BigDecimal.class),
    NUMERIC     (JDBCType.NUMERIC, BigDecimal.class),
    REAL        (JDBCType.REAL, Float.class),
    DOUBLE      (JDBCType.DOUBLE, Double.class),
    CHAR        (JDBCType.CHAR, String.class),
    VARCHAR     (JDBCType.VARCHAR, String.class),
    BINARY      (JDBCType.BINARY, byte[].class),
    VARBINARY   (JDBCType.VARBINARY, byte[].class),
    CLOB        (JDBCType.CLOB, Clob.class),
    BLOB        (JDBCType.BLOB, Blob.class),
    ROWID       (JDBCType.ROWID, RowId.class),
    XML         (JDBCType.SQLXML, SQLXML.class),
    DATE        (JDBCType.DATE, Date.class),
    TIME        (JDBCType.TIME, Time.class),
    TIMESTAMP   (JDBCType.TIMESTAMP, Timestamp.class);

    private final JDBCType sqlType;
    private final Class javaType;

    JavaToSQLTypes(JDBCType sqlType, Class javaType) {
        this.sqlType = sqlType;
        this.javaType = javaType;
    }

    public JDBCType sqlType() {
        return sqlType;
    }

    public Class javaClass() {
        return javaType;
    }

    /**
     * Lookup the Java class for an SQL type.
     *
     * @author zach-bright
     * @param type JDBCType The SQL type to look for.
     * @return The class corresponding to the type or null if none found.
     */
    public static Class lookup(JDBCType type) {
        for (JavaToSQLTypes jType : JavaToSQLTypes.values()) {
            if (jType.sqlType() == type) {
                return jType.javaClass();
            }
        }
        return null;
    }

    /**
     * Lookup the Java class given the string name of an SQL type.
     *
     * @author zach-bright
     * @param type String String name of an SQL type.
     * @return The class corresponding to the type or null if none found.
     */
    public static Class lookup(String type) {
        // INT is a valid alias for INTEGER, but is not in JDBCType.
        if (type.equals("INT")) {
            return Integer.class;
        }
        
        for (JDBCType jdbcType : JDBCType.class.getEnumConstants()) {
            if (jdbcType.getName().equals(type)) {
                return JavaToSQLTypes.lookup(jdbcType);
            }
        }
        return null;
    }

}
