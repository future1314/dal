package com.ctrip.platform.dal.cluster;

import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.ResultMerger;

import java.sql.SQLException;
import java.util.List;

/**
 * @author c7ch23en
 */
public interface Cluster {

    String getClusterName();

    // 集合并发可变性
    // 考虑用数组
    // 回调命名
    void insert(String logicTableName, SQLHandler handler, SQLData... rowSet) throws SQLException;
    void insert(String logicTableName, Iterable<SQLData> rowSet, SQLHandler handler) throws SQLException;

    void batchInsert(String logicTableName, Iterable<SQLData> rowSet, SQLHandler handler) throws SQLException;

    <T> T query(String logicTableName, NamedSQLParameters params, SQLHandler handler,
                ResultMerger<T> merger, DalResultSetExtractor<T> extractor) throws SQLException;

    <T> T query(String logicTableName, SQLHandler handler,
                ResultMerger<T> merger, DalResultSetExtractor<T> extractor, String columnName, Object... columnValues) throws SQLException;

    /**
     * 自定义SQL，指定Shard
     * 部分Case自动计算分片
     */


}
