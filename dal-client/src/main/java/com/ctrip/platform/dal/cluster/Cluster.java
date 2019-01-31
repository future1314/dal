package com.ctrip.platform.dal.cluster;

import com.ctrip.platform.dal.cluster.hint.ShardHints;
import com.ctrip.platform.dal.cluster.parameter.IndexedSqlParameters;
import com.ctrip.platform.dal.cluster.parameter.NamedSqlParameters;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.ResultMerger;

import java.sql.SQLException;
import java.util.List;

/**
 * @author c7ch23en
 */
public interface Cluster {

    String getClusterName();

    int insert(String logicTableName, NamedSqlParameters insertRow) throws SQLException;

    int[] insert(String logicTableName, NamedSqlParameters[] insertRows) throws SQLException;

    int combinedInsert(String logicTableName, NamedSqlParameters[] insertRows) throws SQLException;

    int[] batchInsert(String logicTableName, NamedSqlParameters[] insertRows) throws SQLException;

    void query(String logicTableName, String[] selectColumns, String paramName, Object paramValue) throws SQLException;

    void query(String logicTableName, String[] selectColumns, String paramName, Object paramValue, RouteHints routeHints) throws SQLException;

    void query(String logicTableName, String[] selectColumns, NamedSqlParameters params) throws SQLException;

    void query(String logicTableName, String[] selectColumns, NamedSqlParameters params, RouteHints routeHints) throws SQLException;

    void query(String sqlTemplate, IndexedSqlParameters params, RouteHints routeHints) throws SQLException;

}
