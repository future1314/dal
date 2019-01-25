package com.ctrip.platform.dal.cluster;

import java.util.List;

/**
 * @author c7ch23en
 */
public interface CombinedAction extends SQLAction {

    PreparedSQLContext prepareSQLContext(String targetTableName, List<SQLData> rowData);

}
