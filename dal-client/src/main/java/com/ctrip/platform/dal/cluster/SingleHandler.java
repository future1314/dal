package com.ctrip.platform.dal.cluster;

import java.util.List;

/**
 * @author c7ch23en
 */
public interface SingleAction extends SQLAction {

    PreparedSQLContext prepareSQLContext(String targetTableName, SQLData rowData);

}
