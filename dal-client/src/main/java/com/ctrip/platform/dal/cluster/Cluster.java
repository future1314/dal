package com.ctrip.platform.dal.cluster;

import com.ctrip.platform.dal.cluster.context.DatabaseShardContext;
import com.ctrip.platform.dal.cluster.context.Row;
import com.ctrip.platform.dal.cluster.context.ShardRequestContext;
import com.ctrip.platform.dal.cluster.context.ShardResultContext;

import java.sql.SQLException;
import java.util.List;

/**
 * @author c7ch23en
 */
public interface Cluster {

    String getClusterName();

//    ShardResultContext shard(ShardRequestContext requestCtx);

    // set
//    List<DatabaseShardContext> shard(String logicTableName, List<Row> rowList);

    void execute(String logicTableName, SQLData rowData, SingleAction action) throws SQLException;

    void execute(String logicTableName, List<SQLData> rowDataList, CombinedAction action) throws SQLException;

    void execute(String logicTableName, List<SQLData> rowDataList, BatchAction action) throws SQLException;

}
