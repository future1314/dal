package com.ctrip.platform.dal.cluster.context;

import com.ctrip.platform.dal.cluster.SQLData;
import com.ctrip.platform.dal.cluster.parameter.NamedSqlParameters;

import java.util.Map;

/**
 * @author c7ch23en
 */
public interface TableShardContext {

    int getShardIndex();

    String getTargetTableName();

    Map<Integer, NamedSqlParameters> getIndexedRowSet();

}
