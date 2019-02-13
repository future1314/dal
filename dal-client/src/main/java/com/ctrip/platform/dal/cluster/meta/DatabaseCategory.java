package com.ctrip.platform.dal.cluster.meta;

import com.ctrip.platform.dal.cluster.parameter.NamedSqlParameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/**
 * @author c7ch23en
 */
public enum DatabaseCategory {

    MYSQL("mysql") {
        @Override
        protected String quote(String column) {
            return "`" + column + "`";
        }
    },

    SQLSERVER("sqlserver") {
        @Override
        protected String quote(String column) {
            return "[" + column + "]";
        }
    };

    private static final String INSERT_SQL_TEMPLATE = "INSERT INTO %s (%s) VALUES (%s)";
    private static final String QUERY_SQL_TEMPLATE = "SELECT %s FROM %s WHERE %s";
    private static final String COLUMN_SEPARATOR = ", ";
    private static final String VALUE_PLACEHOLDER = "?";
    private static final String CONDITION_AND_SEPARATOR = " AND ";
    private static final String COLUMNS_ALL = "*";

    private String name;

    DatabaseCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String buildInsertSql(String table, Collection<String> columns) {
        String strColumns = combineColumns(columns, COLUMN_SEPARATOR);
        String strValues = combineValues(VALUE_PLACEHOLDER, columns.size(), COLUMN_SEPARATOR);
        return String.format(INSERT_SQL_TEMPLATE, table, strColumns, strValues);
    }

    public String buildQuerySql(String table, String[] selectColumns, NamedSqlParameters parameters) {
        String strColumns = combineColumns(Arrays.asList(selectColumns), COLUMN_SEPARATOR);
        String whereConditions = combineConditions(parameters, VALUE_PLACEHOLDER, CONDITION_AND_SEPARATOR);
        return String.format(QUERY_SQL_TEMPLATE, strColumns, table, whereConditions);
    }

    protected String combineColumns(Collection<String> columns, String separator) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String column : columns) {
            if (column.trim().equals(COLUMNS_ALL))
                sb.append(column);
            else
                sb.append(quote(column));
            if (++i < columns.size())
                sb.append(separator);
        }
        return sb.toString();
    }

    protected String combineValues(String value, int count, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= count; i++) {
            sb.append(value);
            if (i < count)
                sb.append(separator);
        }
        return sb.toString();
    }

    protected String combineConditions(NamedSqlParameters parameters, String placeHolder, String separator) {
        StringBuilder sb = new StringBuilder();
        Set<String> columns = parameters.getParamNames();
        int i = 0;
        for (String column : columns) {
            sb.append(quote(column));
            sb.append(" = ");
            sb.append(placeHolder);
            if (++i < columns.size())
                sb.append(separator);
        }
        return sb.toString();
    }

    protected abstract String quote(String column);

}
