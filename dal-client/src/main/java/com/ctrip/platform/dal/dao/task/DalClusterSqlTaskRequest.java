package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.cluster.Cluster;
import com.ctrip.platform.dal.cluster.ResultHandler;
import com.ctrip.platform.dal.cluster.hint.RouteHints;
import com.ctrip.platform.dal.cluster.hint.RouteHintsBuilder;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.dao.StatementParameter;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.SqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.TableSqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private DalLogger logger;

    public DalClusterSqlTaskRequest(Cluster cluster, SqlBuilder builder, DalHints hints, SqlTask<T> task, ResultMerger<T> merger) {
        this.cluster = cluster;
        this.hints = hints;
        this.builder = builder;
        this.task = task;
        this.merger = merger;
        parameters = builder.buildParameters();

        logger = DalClientFactory.getDalLogger();
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
            QueryResultHandler<T> handler = new QueryResultHandler<>(selectSqlBuilder.<T>getResultExtractor(hints));
            cluster.query(logicTableName, selectColumns, parameters, handler);
            while (!handler.isCompleted());
        } else if (builder instanceof FreeSelectSqlBuilder) {
            String sql = builder.build();
            StatementParameters parameters = builder.buildParameters();
            RouteHints routeHints;
            if (hints.isAllShards())
                routeHints = new RouteHintsBuilder().setAllDbShards().build();
            else if (hints.isInShards())
                routeHints = new RouteHintsBuilder().setDbShards(buildShards(hints.getShards())).build();
            else
                routeHints = new RouteHintsBuilder().build();
            QueryResultHandler<T> handler = new QueryResultHandler<>(((FreeSelectSqlBuilder) builder).<T>getResultExtractor(hints));
            cluster.query(sql, buildParamValues(parameters), buildParamTypes(parameters), routeHints, handler);
            while (!handler.isCompleted());
        }
        return null;
    }

    private Object[] buildParamValues(StatementParameters parameters) {
        List<StatementParameter> values = parameters.values();
        Object[] paramValues = new Object[values.size()];
        for (int i = 0; i < values.size(); i++) {
            paramValues[i] = values.get(i).getParamValue();
        }
        return paramValues;
    }

    private int[] buildParamTypes(StatementParameters parameters) {
        List<StatementParameter> values = parameters.values();
        int[] paramTypes = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).isDefaultType()) return null;
            paramTypes[i] = values.get(i).getSqlType();
        }
        return paramTypes;
    }

    private Integer[] buildShards(Set<String> shards) {
        String[] shardArray = (String[]) shards.toArray();
        Integer[] shardIds = new Integer[shardArray.length];
        for (int i = 0; i < shards.size(); i++) {
            shardIds[i] = Integer.parseInt(shardArray[i]);
        }
        return shardIds;
    }


    private static class QueryResultHandler<T> implements ResultHandler {

        private volatile boolean completed = false;
        private DalResultSetExtractor<T> extractor;

        private Logger logger = LoggerFactory.getLogger(QueryResultHandler.class);

        public QueryResultHandler(DalResultSetExtractor<T> extractor) {
            this.extractor = extractor;
        }

        @Override
        public void execute(int dbShardIndex, int tableShardIndex, ResultSet rs) throws SQLException {
            logger.info(String.format("DB shard index: %d", dbShardIndex));
            if (tableShardIndex >= 0)
                logger.info(String.format("Table shard index: %d", tableShardIndex));
            T result = extractor.extract(rs);
            logger.info(result.toString());
        }

        @Override
        public void complete() {
            completed = true;
        }

        public boolean isCompleted() {
            return completed;
        }

    }

}
