package com.ctrip.platform.dal.cluster.meta;

import com.ctrip.platform.dal.cluster.*;
import com.ctrip.platform.dal.cluster.context.*;
import com.ctrip.platform.dal.cluster.strategy.rule.ShardRule;
import com.ctrip.platform.dal.cluster.strategy.rule.TableNamePattern;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.ResultMerger;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    public void insert(String logicTableName, Iterable<SQLData> rowSet, SQLHandler handler) throws SQLException {
        Set<DatabaseShardContext> dbCtxs = shard(logicTableName, rowSet);
        for (DatabaseShardContext dbCtx : dbCtxs) {
            DataSource ds = dbCtx.getDataSource(OperationType.INSERT);
            for (TableShardContext tbCtx : dbCtx.getTableShardContexts()) {
                Map<Integer, SQLData> tbRowMap = tbCtx.getIndexedRowSet();
                List<SQLData> tbRowList = new ArrayList<>(tbRowMap.values());
                PreparedSQLContext sqlCtx = handler.prepare(tbCtx.getTargetTableName(), tbRowList);
                PreparedStatement ps = stmtCreator.prepareStatement(ds.getConnection(), sqlCtx.getSql(), sqlCtx.getParamsList().get(0));
                ps.executeUpdate();
            }
        }
    }

    @Override
    public void batchInsert(String logicTableName, Iterable<SQLData> rowSet, SQLHandler handler) throws SQLException {
        Set<DatabaseShardContext> dbCtxs = shard(logicTableName, rowSet);
        for (DatabaseShardContext dbCtx : dbCtxs) {
            DataSource ds = dbCtx.getDataSource(OperationType.INSERT);
            for (TableShardContext tbCtx : dbCtx.getTableShardContexts()) {
                Map<Integer, SQLData> tbRowMap = tbCtx.getIndexedRowSet();
                List<SQLData> tbRowList = new ArrayList<>(tbRowMap.values());
                PreparedSQLContext sqlCtx = handler.prepare(tbCtx.getTargetTableName(), tbRowList);
                PreparedStatement ps = stmtCreator.prepareStatement(ds.getConnection(), sqlCtx.getSql(), sqlCtx.getParamsList());
                ps.executeBatch();
            }
        }
    }

    @Override
    public <T> T query(String logicTableName, NamedSQLParameters params, SQLHandler handler,
                       ResultMerger<T> merger, DalResultSetExtractor<T> extractor) throws SQLException {
        Set<DatabaseShardContext> dbCtxs = shard(logicTableName, params);
        for (DatabaseShardContext dbCtx : dbCtxs) {
            DataSource ds = dbCtx.getDataSource(OperationType.QUERY);
            for (TableShardContext tbCtx : dbCtx.getTableShardContexts()) {
                Map<Integer, SQLData> tbRowMap = tbCtx.getIndexedRowSet();
                List<SQLData> tbRowList = new ArrayList<>(tbRowMap.values());
                PreparedSQLContext sqlCtx = handler.prepare(tbCtx.getTargetTableName(), (NamedSQLParameters) tbRowList.get(0));
                PreparedStatement ps = stmtCreator.prepareStatement(ds.getConnection(), sqlCtx.getSql(), sqlCtx.getParamsList().get(0));
                ResultSet rs = ps.executeQuery();
                T result = extractor.extract(rs);
                merger.addPartial(dbCtx.getShardId() + tbCtx.getTargetTableName(), result);
            }
        }
        return merger.merge();
    }

    private Set<DatabaseShardContext> shard(String logicTableName, Iterable<SQLData> rowSet) {
        ShardRule dbShardRule = getDbShardRule(logicTableName);
        ShardRule tableShardRule = getTableShardRule(logicTableName);
        Map<String, Map<String, Map<Integer, SQLData>>> shuffled = new HashMap<>();
        Iterator<SQLData> iterator = rowSet.iterator();
        int rowIndex = 0;
        while (iterator.hasNext()) {
            SQLData row = iterator.next();
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
            tbShard.put(rowIndex++, row);
        }
        return buildContext(shuffled, logicTableName);
    }

    private Set<DatabaseShardContext> shard(String logicTableName, NamedSQLParameters params) {
        ShardRule dbShardRule = getDbShardRule(logicTableName);
        ShardRule tableShardRule = getTableShardRule(logicTableName);
        Map<String, Map<String, Map<Integer, SQLData>>> shuffled = new HashMap<>();
        String dbShardId = dbShardRule.shardByFields(params);
        String tbShardId = tableShardRule.shardByFields(params);
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
        tbShard.put(0, params);
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
