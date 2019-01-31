package com.ctrip.platform.dal.cluster.meta;

import com.ctrip.platform.dal.cluster.*;
import com.ctrip.platform.dal.cluster.context.*;
import com.ctrip.platform.dal.cluster.parameter.IndexedSqlParameters;
import com.ctrip.platform.dal.cluster.parameter.NamedSqlParameters;
import com.ctrip.platform.dal.cluster.parameter.NamedSqlParametersImp;
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
    private DatabaseCategory databaseCategory;
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
    public int insert(String logicTableName, NamedSqlParameters insertRow) throws SQLException {
        Set<DatabaseShardContext> dbCtxs = shard(logicTableName, insertRow);
        DatabaseShardContext dbCtx = dbCtxs.iterator().next();
        DataSource ds = dbCtx.getDataSource(OperationType.INSERT);
        TableShardContext tbCtx = dbCtx.getTableShardContexts().iterator().next();
        String targetTableName = tbCtx.getTargetTableName();
        String sql = databaseCategory.buildInsertSql(targetTableName, insertRow.getParamNames());
        PreparedStatement ps = stmtCreator.prepareStatement(ds.getConnection(), sql, insertRow);
        return ps.executeUpdate();
    }

    @Override
    public int[] insert(String logicTableName, NamedSqlParameters[] insertRows) throws SQLException {
        return new int[0];
    }

    @Override
    public int combinedInsert(String logicTableName, NamedSqlParameters[] insertRows) throws SQLException {
        return 0;
    }

    @Override
    public int[] batchInsert(String logicTableName, NamedSqlParameters[] insertRows) throws SQLException {
        Set<DatabaseShardContext> dbCtxs = shard(logicTableName, insertRows);
        for (DatabaseShardContext dbCtx : dbCtxs) {
            DataSource ds = dbCtx.getDataSource(OperationType.INSERT);
            for (TableShardContext tbCtx : dbCtx.getTableShardContexts()) {
                String targetTableName = tbCtx.getTargetTableName();
                String sql = databaseCategory.buildInsertSql(targetTableName, insertRows[0].getParamNames());
                Map<Integer, NamedSqlParameters> tbRowMap = tbCtx.getIndexedRowSet();
                PreparedStatement ps = stmtCreator.prepareStatement(ds.getConnection(), sql, Arrays.asList(insertRows));
                ps.executeBatch();
            }
        }
        return new int[0];
    }

    @Override
    public void query(String logicTableName, String[] selectColumns, String paramName, Object paramValue) throws SQLException {
        NamedSqlParametersImp parameters = new NamedSqlParametersImp();
        parameters.add(paramName, paramValue);
        Set<DatabaseShardContext> dbCtxs = shard(logicTableName, parameters);
        DatabaseShardContext dbCtx = dbCtxs.iterator().next();
        DataSource ds = dbCtx.getDataSource(OperationType.QUERY);
        TableShardContext tbCtx = dbCtx.getTableShardContexts().iterator().next();
        String targetTableName = tbCtx.getTargetTableName();
        String sql = databaseCategory.buildQuerySql(targetTableName, selectColumns, parameters);
        PreparedStatement ps = stmtCreator.prepareStatement(ds.getConnection(), sql, parameters);
        ps.executeQuery();
    }

    @Override
    public void query(String logicTableName, String[] selectColumns, String paramName, Object paramValue, RouteHints routeHints) throws SQLException {

    }

    @Override
    public void query(String logicTableName, String[] selectColumns, NamedSqlParameters params) throws SQLException {
        Set<DatabaseShardContext> dbCtxs = shard(logicTableName, params);
        DatabaseShardContext dbCtx = dbCtxs.iterator().next();
        DataSource ds = dbCtx.getDataSource(OperationType.QUERY);
        TableShardContext tbCtx = dbCtx.getTableShardContexts().iterator().next();
        String targetTableName = tbCtx.getTargetTableName();
        String sql = databaseCategory.buildQuerySql(targetTableName, selectColumns, params);
        PreparedStatement ps = stmtCreator.prepareStatement(ds.getConnection(), sql, params);
        ps.executeQuery();
    }

    @Override
    public void query(String logicTableName, String[] selectColumns, NamedSqlParameters params, RouteHints routeHints) throws SQLException {

    }

    @Override
    public void query(String sqlTemplate, IndexedSqlParameters params, RouteHints routeHints) throws SQLException {

    }

    private Set<DatabaseShardContext> shard(String logicTableName, NamedSqlParameters[] rowSet) {
        ShardRule dbShardRule = getDbShardRule(logicTableName);
        ShardRule tableShardRule = getTableShardRule(logicTableName);
        Map<String, Map<String, Map<Integer, NamedSqlParameters>>> shuffled = new HashMap<>();
        int rowIndex = 0;
        for (NamedSqlParameters row : rowSet) {
            String dbShardId = dbShardRule.shardByFields(row);
            String tbShardId = tableShardRule.shardByFields(row);
            Map<String, Map<Integer, NamedSqlParameters>> tbShards = shuffled.get(dbShardId);
            if (tbShards == null) {
                tbShards = new HashMap<>();
                shuffled.put(dbShardId, tbShards);
            }
            Map<Integer, NamedSqlParameters> tbShard = tbShards.get(tbShardId);
            if (tbShard == null) {
                tbShard = new HashMap<>();
                tbShards.put(tbShardId, tbShard);
            }
            tbShard.put(rowIndex++, row);
        }
        return buildContext(shuffled, logicTableName);
    }

    private Set<DatabaseShardContext> shard(String logicTableName, NamedSqlParameters params) {
        ShardRule dbShardRule = getDbShardRule(logicTableName);
        ShardRule tableShardRule = getTableShardRule(logicTableName);
        Map<String, Map<String, Map<Integer, NamedSqlParameters>>> shuffled = new HashMap<>();
        String dbShardId = dbShardRule.shardByFields(params);
        String tbShardId = tableShardRule.shardByFields(params);
        Map<String, Map<Integer, NamedSqlParameters>> tbShards = shuffled.get(dbShardId);
        if (tbShards == null) {
            tbShards = new HashMap<>();
            shuffled.put(dbShardId, tbShards);
        }
        Map<Integer, NamedSqlParameters> tbShard = tbShards.get(tbShardId);
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

    private Set<DatabaseShardContext> buildContext(Map<String, Map<String, Map<Integer, NamedSqlParameters>>> shuffled,
                                                   String logicTableName) {
        Set<DatabaseShardContext> ctx = new HashSet<>();
        TableNamePattern pattern = getTableNamePattern(logicTableName);
        for (String dbShardId : shuffled.keySet()) {
            DatabaseShardContextImp dbCtx = new DatabaseShardContextImp();
            dbCtx.setDatabaseShard(databaseShards.get(dbShardId));
            Map<String, Map<Integer, NamedSqlParameters>> tbShards = shuffled.get(dbShardId);
            for (String tbShardId : tbShards.keySet()) {
                TableShardContextImp tbCtx = new TableShardContextImp();
                tbCtx.setTargetTableName(pattern.getTargetTableName(logicTableName, tbShardId));
                Map<Integer, NamedSqlParameters> tbShard = tbShards.get(tbShardId);
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

    public void setDatabaseCategory(DatabaseCategory databaseCategory) {
        this.databaseCategory = databaseCategory;
    }

}
