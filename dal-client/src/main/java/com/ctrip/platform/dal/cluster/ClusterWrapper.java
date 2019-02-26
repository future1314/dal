package com.ctrip.platform.dal.cluster;

import com.ctrip.platform.dal.cluster.hint.RouteHints;
import com.ctrip.platform.dal.cluster.parameter.NamedSqlParameters;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author c7ch23en
 */
public class ClusterWrapper implements Cluster {

    private AtomicReference<Cluster> cluster = new AtomicReference<>();

    public ClusterWrapper(Cluster cluster) {
        getAndSet(cluster);
    }

    public Cluster getAndSet(Cluster cluster) {
        return this.cluster.getAndSet(cluster);
    }

    @Override
    public String getClusterName() {
        return getCluster().getClusterName();
    }

    @Override
    public int insert(String logicTableName, NamedSqlParameters insertRow) throws SQLException {
        return getCluster().insert(logicTableName, insertRow);
    }

    @Override
    public int[] insert(String logicTableName, NamedSqlParameters[] insertRows) throws SQLException {
        return getCluster().insert(logicTableName, insertRows);
    }

    @Override
    public int combinedInsert(String logicTableName, NamedSqlParameters[] insertRows) throws SQLException {
        return getCluster().combinedInsert(logicTableName, insertRows);
    }

    @Override
    public int[] batchInsert(String logicTableName, NamedSqlParameters[] insertRows) throws SQLException {
        return getCluster().batchInsert(logicTableName, insertRows);
    }

    @Override
    public void query(String logicTableName, String[] selectColumns, String paramName, Object paramValue, ResultHandler rh) throws SQLException {
        getCluster().query(logicTableName, selectColumns, paramName, paramValue, rh);
    }

    @Override
    public void query(String logicTableName, String[] selectColumns, String paramName, Object paramValue, RouteHints routeHints, ResultHandler rh) throws SQLException {
        getCluster().query(logicTableName, selectColumns, paramName, paramValue, routeHints, rh);
    }

    @Override
    public void query(String logicTableName, String[] selectColumns, NamedSqlParameters params, ResultHandler rh) throws SQLException {
        getCluster().query(logicTableName, selectColumns, params, rh);
    }

    @Override
    public void query(String logicTableName, String[] selectColumns, NamedSqlParameters params, RouteHints routeHints, ResultHandler rh) throws SQLException {
        getCluster().query(logicTableName, selectColumns, params, routeHints, rh);
    }

    @Override
    public void query(String sql, Object[] paramValues, RouteHints routeHints, ResultHandler rh) throws SQLException {
        getCluster().query(sql, paramValues, routeHints, rh);
    }

    @Override
    public void query(String sql, Object[] paramValues, int[] paramTypes, RouteHints routeHints, ResultHandler rh) throws SQLException {
        getCluster().query(sql, paramValues, paramTypes, routeHints, rh);
    }

    private Cluster getCluster() {
        return cluster.get();
    }

}
