package com.ctrip.platform.dal.cluster.meta;

import com.ctrip.platform.dal.cluster.*;
import com.ctrip.platform.dal.cluster.context.*;
import com.ctrip.platform.dal.cluster.strategy.rule.ShardRule;
import com.ctrip.platform.dal.cluster.strategy.rule.TableNamePattern;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * @author c7ch23en
 */
public class DalCluster implements Cluster {

    private final String clusterName;
    private DatabaseCategory category;
    private Map<String, DatabaseShard> databaseShards = new HashMap<>();
    private Map<String, LogicTable> logicTables = new HashMap<>();
    private ShardRule dbShardRule;
    private ShardRule tableShardRule;
    private TableNamePattern tableNamePattern;
    private StatementCreator stmtCreator = new StatementCreator();

    public DalCluster(String clusterName) {
        this.clusterName = clusterName;
    }

    @Override
    public String getClusterName() {
        return clusterName;
    }

    @Override
    public void execute(String logicTableName, SQLData rowData, SingleAction action) throws SQLException {
        List<SQLData> rowDataList = new ArrayList<>();
        rowDataList.add(rowData);
        Set<DatabaseShardContext> dbCtxs = shard(logicTableName, rowDataList);
        DatabaseShardContext dbCtx = dbCtxs.iterator().next();
        DataSource ds = dbCtx.getDataSource(action.getOperationType());
        TableShardContext tbCtx = dbCtx.getTableShardContexts().iterator().next();

        PreparedSQLContext sqlCtx = action.prepareSQLContext(tbCtx.getTargetTableName(), rowData);

        PreparedStatement ps = stmtCreator.prepareStatement(ds.getConnection(), sqlCtx.getSql(), sqlCtx.getParameters());
        ps.executeUpdate();
    }

    @Override
    public void execute(String logicTableName, List<SQLData> rowDataList, CombinedAction action) throws SQLException {
        Set<DatabaseShardContext> dbCtxs = shard(logicTableName, rowDataList);
        for (DatabaseShardContext dbCtx : dbCtxs) {
            DataSource ds = dbCtx.getDataSource(action.getOperationType());
            for (TableShardContext tbCtx : dbCtx.getTableShardContexts()) {
                Map<Integer, SQLData> tbRowMap = tbCtx.getIndexedRowSet();
                List<SQLData> tbRowList = new ArrayList<>(tbRowMap.values());
                PreparedSQLContext sqlCtx = action.prepareSQLContext(tbCtx.getTargetTableName(), tbRowList);
                PreparedStatement ps = stmtCreator.prepareStatement(ds.getConnection(), sqlCtx.getSql(), sqlCtx.getParameters());
                ps.executeUpdate();
            }
        }
    }

    @Override
    public void execute(String logicTableName, List<SQLData> rowDataList, BatchAction action) throws SQLException {
        Set<DatabaseShardContext> dbCtxs = shard(logicTableName, rowDataList);
        for (DatabaseShardContext dbCtx : dbCtxs) {
            DataSource ds = dbCtx.getDataSource(action.getOperationType());
            for (TableShardContext tbCtx : dbCtx.getTableShardContexts()) {
                Map<Integer, SQLData> tbRowMap = tbCtx.getIndexedRowSet();
                List<SQLData> tbRowList = new ArrayList<>(tbRowMap.values());
                PreparedBatchSQLContext sqlCtx = action.prepareSQLContext(tbCtx.getTargetTableName(), tbRowList);
                PreparedStatement ps = stmtCreator.prepareStatement(ds.getConnection(), sqlCtx.getSql(), sqlCtx.getParametersList());
                ps.executeBatch();
            }
        }
    }

    public Set<DatabaseShardContext> shard(String logicTableName, List<SQLData> rowList) {
        ShardRule dbShardRule = getDbShardRule(logicTableName);
        ShardRule tableShardRule = getTableShardRule(logicTableName);
        Map<String, Map<String, Map<Integer, SQLData>>> shuffled = new HashMap<>();
        for (int i = 0; i < rowList.size(); i++) {
            SQLData row = rowList.get(i);
            String dbShardId = dbShardRule.shardByFields(row);
            String tbShardId = tableShardRule.shardByFields(row);
            Map<String, Map<Integer, SQLData>> tbShards = shuffled.get(dbShardId);
            if (tbShards == null) {
                tbShards = new HashMap<>();
                shuffled.put(dbShardId, tbShards);
            }
            Map<Integer, SQLData> tbShard = tbShards.get(tbShardId);
            if (tbShard == null) {
                tbShard = new HashMap<>();
                tbShards.put(tbShardId, tbShard);
            }
            tbShard.put(i, row);
        }
        return buildContext(shuffled, logicTableName);
    }

    private ShardRule getDbShardRule(String logicTableName) {
        ShardRule rule = null;
        LogicTable table = logicTables.get(logicTableName);
        if (table != null) {
            rule = table.getDbShardRule();
        }
        return rule != null ? rule : dbShardRule;
    }

    private ShardRule getTableShardRule(String logicTableName) {
        ShardRule rule = null;
        LogicTable table = logicTables.get(logicTableName);
        if (table != null) {
            rule = table.getTableShardRule();
        }
        return rule != null ? rule : tableShardRule;
    }

    private TableNamePattern getTableNamePattern(String logicTableName) {
        TableNamePattern pattern = null;
        LogicTable table = logicTables.get(logicTableName);
        if (table != null) {
            pattern = table.getTableNamePattern();
        }
        return pattern != null ? pattern : tableNamePattern;
    }

    private Set<DatabaseShardContext> buildContext(Map<String, Map<String, Map<Integer, SQLData>>> shuffled,
                                                   String logicTableName) {
        Set<DatabaseShardContext> ctx = new HashSet<>();
        TableNamePattern pattern = getTableNamePattern(logicTableName);
        for (String dbShardId : shuffled.keySet()) {
            DatabaseShardContextImp dbCtx = new DatabaseShardContextImp();
            dbCtx.setDatabaseShard(databaseShards.get(dbShardId));
            Map<String, Map<Integer, SQLData>> tbShards = shuffled.get(dbShardId);
            for (String tbShardId : tbShards.keySet()) {
                TableShardContextImp tbCtx = new TableShardContextImp();
                tbCtx.setTargetTableName(pattern.getTargetTableName(logicTableName, tbShardId));
                Map<Integer, SQLData> tbShard = tbShards.get(tbShardId);
                for (Integer index : tbShard.keySet())
                    tbCtx.addRow(index, tbShard.get(index));
                dbCtx.addTableShardResult(tbCtx);
            }
            ctx.add(dbCtx);
        }
        return ctx;
    }

/*
    @Override
    public ShardResultContext shard(ShardRequestContext requestCtx) {
        return new ClusterShardResultContext();
    }
*/

    public DatabaseShard addDatabaseShard(DatabaseShard databaseShard) {
        return databaseShards.put(databaseShard.getShardIndex(), databaseShard);
    }

    public LogicTable addLogicTable(LogicTable logicTable) {
        return logicTables.put(logicTable.getLogicTableName(), logicTable);
    }

    public void setDbShardRule(ShardRule dbShardRule) {
        this.dbShardRule = dbShardRule;
    }

    public void setTableShardRule(ShardRule tableShardRule) {
        this.tableShardRule = tableShardRule;
    }

    public void setTableNamePattern(TableNamePattern tableNamePattern) {
        this.tableNamePattern = tableNamePattern;
    }

}
