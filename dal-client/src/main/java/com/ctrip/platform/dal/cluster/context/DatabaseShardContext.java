package com.ctrip.platform.dal.cluster.context;

import com.ctrip.platform.dal.cluster.OperationType;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;

/**
 * @author c7ch23en
 */
public interface DatabaseShardContext {

    int getShardIndex();

    DataSource getDataSource(OperationType operation);

    Set<TableShardContext> getTableShardContexts();

}
