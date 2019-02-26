package com.ctrip.platform.dal.cluster;

import com.ctrip.platform.dal.cluster.hint.RouteHints;
import com.ctrip.platform.dal.cluster.hint.ShardHints;
import com.ctrip.platform.dal.cluster.parameter.IndexedSqlParameters;
import com.ctrip.platform.dal.cluster.parameter.NamedSqlParameters;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.ResultMerger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author c7ch23en
 */
public interface Cluster {

    String getClusterName();

    /*
     * 1.SQL构建接口定义，实现类注册
     * 2.Query异步接口
     * 3.Indexed params使用数组结构传参
     * 4.update/delete/replace接口
     * 5.返回主键
     * 6.combinedInsert事务说明
     */

    int insert(String logicTableName, NamedSqlParameters insertRow) throws SQLException;

    int[] insert(String logicTableName, NamedSqlParameters[] insertRows) throws SQLException;

    int combinedInsert(String logicTableName, NamedSqlParameters[] insertRows) throws SQLException;

    int[] batchInsert(String logicTableName, NamedSqlParameters[] insertRows) throws SQLException;

    void query(String logicTableName, String[] selectColumns, String paramName, Object paramValue, ResultHandler rh) throws SQLException;

    void query(String logicTableName, String[] selectColumns, String paramName, Object paramValue, RouteHints routeHints, ResultHandler rh) throws SQLException;

    void query(String logicTableName, String[] selectColumns, NamedSqlParameters params, ResultHandler rh) throws SQLException;

    void query(String logicTableName, String[] selectColumns, NamedSqlParameters params, RouteHints routeHints, ResultHandler rh) throws SQLException;

    void query(String sql, Object[] paramValues, RouteHints routeHints, ResultHandler rh) throws SQLException;

    void query(String sql, Object[] paramValues, int[] paramTypes, RouteHints routeHints, ResultHandler rh) throws SQLException;

}
