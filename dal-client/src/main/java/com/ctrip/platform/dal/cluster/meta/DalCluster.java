package com.ctrip.platform.dal.cluster.meta;

import com.ctrip.platform.dal.cluster.*;
import com.ctrip.platform.dal.cluster.context.*;
import com.ctrip.platform.dal.cluster.hint.AllShards;
import com.ctrip.platform.dal.cluster.hint.RouteHints;
import com.ctrip.platform.dal.cluster.hint.Shards;
import com.ctrip.platform.dal.cluster.hint.UserDefinedShards;
import com.ctrip.platform.dal.cluster.parameter.IndexedSqlParameters;
import com.ctrip.platform.dal.cluster.parameter.NamedSqlParameters;
import com.ctrip.platform.dal.cluster.parameter.NamedSqlParametersImp;
import com.ctrip.platform.dal.cluster.strategy.rule.ShardRule;
import com.ctrip.platform.dal.cluster.strategy.rule.TableNamePattern;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.ResultMerger;

import javax.jws.soap.SOAPBinding;
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
    private Map<Integer, DatabaseShard> databaseShards = new HashMap<>();
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
    public void query(String logicTableName, String[] selectColumns, String paramName, Object paramValue, ResultHandler rh) throws SQLException {
        NamedSqlParametersImp parameters = new NamedSqlParametersImp();
        parameters.add(paramName, paramValue);
        Set<DatabaseShardContext> dbCtxs = shard(logicTableName, parameters);
        DatabaseShardContext dbCtx = dbCtxs.iterator().next();
        DataSource ds = dbCtx.getDataSource(OperationType.QUERY);
        TableShardContext tbCtx = dbCtx.getTableShardContexts().iterator().next();
        String targetTableName = tbCtx.getTargetTableName();
        String sql = databaseCategory.buildQuerySql(targetTableName, selectColumns, parameters);
        PreparedStatement ps = stmtCreator.prepareStatement(ds.getConnection(), sql, parameters);
        rh.execute(dbCtx.getShardIndex(), tbCtx.getShardIndex(), ps.executeQuery());
        rh.complete();
    }

    @Override
    public void query(String logicTableName, String[] selectColumns, String paramName, Object paramValue, RouteHints routeHints, ResultHandler rh) throws SQLException {
    }

    @Override
    public void query(String logicTableName, String[] selectColumns, NamedSqlParameters params, ResultHandler rh) throws SQLException {
        Set<DatabaseShardContext> dbCtxs = shard(logicTableName, params);
        DatabaseShardContext dbCtx = dbCtxs.iterator().next();
        DataSource ds = dbCtx.getDataSource(OperationType.QUERY);
        TableShardContext tbCtx = dbCtx.getTableShardContexts().iterator().next();
        String targetTableName = tbCtx.getTargetTableName();
        String sql = databaseCategory.buildQuerySql(targetTableName, selectColumns, params);
        PreparedStatement ps = stmtCreator.prepareStatement(ds.getConnection(), sql, params);
        rh.execute(dbCtx.getShardIndex(), tbCtx.getShardIndex(), ps.executeQuery());
        rh.complete();
    }

    @Override
    public void query(String logicTableName, String[] selectColumns, NamedSqlParameters params, RouteHints routeHints, ResultHandler rh) throws SQLException {
    }

    @Override
    public void query(String sql, Object[] paramValues, RouteHints routeHints, ResultHandler rh) throws SQLException {
        query(sql, paramValues, null, routeHints, rh);
    }

    @Override
    public void query(String sql, Object[] paramValues, int[] paramTypes, RouteHints routeHints, ResultHandler rh) throws SQLException {
        Shards shards = routeHints.getDbShards();
        if (shards instanceof UserDefinedShards) {
            UserDefinedShards definedShards = (UserDefinedShards) shards;
            Set<Integer> shardIds = definedShards.getShards();
            for (int shardId : shardIds) {
                DataSource ds = databaseShards.get(shardId).selectSlave().getDataSource();
                PreparedStatement ps = stmtCreator.prepareStatement(ds.getConnection(), sql, paramValues, paramTypes);
                ResultSet rs = ps.executeQuery();
                rh.execute(shardId, -1, rs);
            }
            rh.complete();
        } else if (shards instanceof AllShards) {
            for (int shardId : databaseShards.keySet()) {
                DataSource ds = databaseShards.get(shardId).selectSlave().getDataSource();
                PreparedStatement ps = stmtCreator.prepareStatement(ds.getConnection(), sql, paramValues, paramTypes);
                ResultSet rs = ps.executeQuery();
                rh.execute(shardId, -1, rs);
            }
            rh.complete();
        }
    }

    private Set<DatabaseShardContext> shard(String logicTableName, NamedSqlParameters[] rowSet) {
        ShardRule dbShardRule = getDbShardRule(logicTableName);
        ShardRule tableShardRule = getTableShardRule(logicTableName);
        Map<Integer, Map<Integer, Map<Integer, NamedSqlParameters>>> shuffled = new HashMap<>();
        int rowIndex = 0;
        for (NamedSqlParameters row : rowSet) {
            int dbShardId = dbShardRule.shardByFields(row);
            int tbShardId = tableShardRule.shardByFields(row);
            Map<Integer, Map<Integer, NamedSqlParameters>> tbShards = shuffled.get(dbShardId);
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
        Map<Integer, Map<Integer, Map<Integer, NamedSqlParameters>>> shuffled = new HashMap<>();
        int dbShardId = dbShardRule.shardByFields(params);
        int tbShardId = tableShardRule.shardByFields(params);
        Map<Integer, Map<Integer, NamedSqlParameters>> tbShards = shuffled.get(dbShardId);
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

    private Set<DatabaseShardContext> buildContext(Map<Integer, Map<Integer, Map<Integer, NamedSqlParameters>>> shuffled,
                                                   String logicTableName) {
        Set<DatabaseShardContext> ctx = new HashSet<>();
        TableNamePattern pattern = getTableNamePattern(logicTableName);
        for (int dbShardId : shuffled.keySet()) {
            DatabaseShardContextImp dbCtx = new DatabaseShardContextImp();
            dbCtx.setDatabaseShard(databaseShards.get(dbShardId));
            Map<Integer, Map<Integer, NamedSqlParameters>> tbShards = shuffled.get(dbShardId);
            for (int tbShardId : tbShards.keySet()) {
                TableShardContextImp tbCtx = new TableShardContextImp();
                tbCtx.setShardIndex(tbShardId);
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

    public void setDatabaseCategory(String strCategory) {
        if (DatabaseCategory.MYSQL.getName().equalsIgnoreCase(strCategory))
            databaseCategory = DatabaseCategory.MYSQL;
        else if (DatabaseCategory.SQLSERVER.getName().equalsIgnoreCase(strCategory))
            databaseCategory = DatabaseCategory.MYSQL;
    }

}
