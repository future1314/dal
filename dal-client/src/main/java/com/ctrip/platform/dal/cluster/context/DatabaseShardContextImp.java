package com.ctrip.platform.dal.cluster.context;

import com.ctrip.platform.dal.cluster.OperationType;
import com.ctrip.platform.dal.cluster.meta.DatabaseShard;

import javax.sql.DataSource;
import java.util.*;

/**
 * @author c7ch23en
 */
public class DatabaseShardContextImp implements DatabaseShardContext {

    private DatabaseShard databaseShard;
    private Set<TableShardContext> tableShardContexts = new HashSet<>();

    @Override
    public int getShardIndex() {
        return databaseShard.getShardIndex();
    }

    @Override
    public DataSource getDataSource(OperationType operation) {
        if (operation == OperationType.QUERY)
            return databaseShard.selectSlave().getDataSource();
        else
            return databaseShard.selectMaster().getDataSource();
    }

    @Override
    public Set<TableShardContext> getTableShardContexts() {
        return tableShardContexts;
    }

    public void setDatabaseShard(DatabaseShard databaseShard) {
        this.databaseShard = databaseShard;
    }

    public void addTableShardResult(TableShardContext tableShardContext) {
        tableShardContexts.add(tableShardContext);
    }

}
