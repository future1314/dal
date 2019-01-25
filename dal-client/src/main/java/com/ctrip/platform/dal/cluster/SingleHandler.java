package com.ctrip.platform.dal.cluster;

/**
 * @author c7ch23en
 */
public interface SingleHandler extends SQLHandler {

    PreparedSQLContext prepareSQLContext(String targetTableName, SQLData rowData);

}
