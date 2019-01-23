package com.ctrip.platform.dal.cluster;

import java.util.List;

/**
 * @author c7ch23en
 */
public interface BatchAction extends SQLAction {

    PreparedBatchSQLContext prepareSQLContext(String targetTableName, List<SQLData> rowData);

}
