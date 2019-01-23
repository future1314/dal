package com.ctrip.platform.dal.cluster.context;

import com.ctrip.platform.dal.cluster.SQLData;

import java.util.Map;

/**
 * @author c7ch23en
 */
public interface TableShardContext {

    String getTargetTableName();

    Map<Integer, SQLData> getIndexedRowSet();

}
