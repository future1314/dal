package com.ctrip.platform.dal.cluster;

import java.util.List;

/**
 * @author c7ch23en
 */
public interface BatchHandler extends SQLHandler {

    PreparedBatchSQLContext prepareSQLContext(String targetTableName, List<SQLData> rowData);

}
