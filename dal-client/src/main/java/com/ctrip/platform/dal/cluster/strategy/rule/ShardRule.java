package com.ctrip.platform.dal.cluster.strategy.rule;

import com.ctrip.platform.dal.cluster.SQLData;
import com.ctrip.platform.dal.cluster.parameter.NamedSqlParameters;

import java.util.Map;

/**
 * @author c7ch23en
 */
public interface ShardRule extends Forkable<ShardRule> {

    String shardByValue(Object value);

    String shardByColumnValue(String columnName, Object columnValue);

    String shardByFields(NamedSqlParameters fields);

}
