package com.ctrip.platform.dal.cluster.meta;

/**
 * @author c7ch23en
 */
public class MySqlDatabase extends CommonDatabase {

    private static final String URL_PATTERN = "jdbc:mysql://%s:%d/%s";
    private static final String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";

    public MySqlDatabase(DatabaseRole role) {
        super(role);
    }

    @Override
    protected String getUrl() {
        return String.format(URL_PATTERN, server, port, dbName);
    }

    @Override
    protected String getDriverClassName() {
        return DRIVER_CLASS_NAME;
    }

}
