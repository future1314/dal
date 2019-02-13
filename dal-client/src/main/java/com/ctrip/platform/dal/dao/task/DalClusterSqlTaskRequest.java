package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.cluster.Cluster;
import com.ctrip.platform.dal.cluster.hint.RouteHints;
import com.ctrip.platform.dal.cluster.hint.RouteHintsBuilder;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.SqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.TableSqlBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author c7ch23en
 */
public class DalClusterSqlTaskRequest<T> implements DalClusterRequest<T> {

    private Cluster cluster;
    private DalHints hints;
    private SqlBuilder builder;
    private StatementParameters parameters;
    private SqlTask<T> task;
    private ResultMerger<T> merger;

    public DalClusterSqlTaskRequest(Cluster cluster, SqlBuilder builder, DalHints hints, SqlTask<T> task, ResultMerger<T> merger) {
        this.cluster = cluster;
        this.hints = hints;
        this.builder = builder;
        this.task = task;
        this.merger = merger;
        parameters = builder.buildParameters();
    }

    @Override
    public void validateAndPrepare() throws SQLException {
    }

    @Override
    public T execute() throws SQLException {
        if (builder instanceof SelectSqlBuilder) {
            SelectSqlBuilder selectSqlBuilder = (SelectSqlBuilder) builder;
            String[] selectColumns = selectSqlBuilder.getSelectColumns();
            String logicTableName = selectSqlBuilder.getTableName();
            ResultSet rs = cluster.query(logicTableName, selectColumns, parameters);
            DalResultSetExtractor<T> extractor = selectSqlBuilder.getResultExtractor(hints);
            return extractor.extract(rs);
        } else if (builder instanceof FreeSelectSqlBuilder) {
            String sql = builder.build();
            StatementParameters parameters = builder.buildParameters();
            RouteHints routeHints = new RouteHintsBuilder().setDbShards(hints.getShardId()).build();
            Map<String, ResultSet> results = cluster.query(sql, parameters, routeHints);
        }
        return null;
    }
}
