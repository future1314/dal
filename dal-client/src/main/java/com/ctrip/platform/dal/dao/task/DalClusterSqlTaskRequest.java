package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.cluster.Cluster;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.sqlbuilder.SqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.TableSqlBuilder;

import java.sql.SQLException;

/**
 * @author c7ch23en
 */
public class DalClusterSqlTaskRequest<T> implements DalClusterRequest {

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
    public void execute() throws SQLException {
        if (task instanceof QuerySqlTask) {
            ((QuerySqlTask) task).setSql(builder.build());
            TableSqlBuilder tableBuilder = (TableSqlBuilder) builder;
            String logicTableName = tableBuilder.getTableName();
            cluster.query(logicTableName, parameters, (QuerySqlTask) task, merger, ((QuerySqlTask) task).getExtractor());
        }
    }
}
