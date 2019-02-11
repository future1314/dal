package com.ctrip.platform.dal.dao.log;

public class DalLogType {
    public static final String DAL = "DAL";
    public static final String DAL_VALIDATION = "DAL.validation";

//    connection
    public static final String DATASOURCE_GET_CONNECTION = "DataSource.getConnection";
    public static final String DATASOURCE_BORROW_IDLE_CONNECTION = "DataSource.borrowIdleConnection";
    public static final String DATASOURCE_CREATE_CONNECTION = "DataSource.createConnection";
    public static final String DATASOURCE_RELEASE_CONNECTION = "DataSource.releaseConnection";
    public static final String DATASOURCE_ABANDON_CONNECTION = "DataSource.abandonConnection";
    public static final String DATASOURCE_VALIDATE_CONNECTION = "DataSource.validateConnection";

//    datasource
    public static final String DATASOURCE_CREATE_DATASOURCE = "DataSource.createDataSource";
    public static final String DATASOURCE_CLOSE_DATASOURCE = "DataSource.closeDataSource";

//    config
    public static final String DATASOURCE_CONNECTION_STRING = "DataSource.connectionString";
    public static final String DATASOURCE_POOL_PROPERTIES = "DataSource.poolProperties";
    public static final String DATASOURCE_IP_DOMAIN = "DataSource.iPDomainStatus";
    public static final String DATASOURCE_CONFIGURE = "DataSource.configure";
}
