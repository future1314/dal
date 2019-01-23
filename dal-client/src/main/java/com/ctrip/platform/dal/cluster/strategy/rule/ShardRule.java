package com.ctrip.platform.dal.cluster.strategy.rule;

import com.ctrip.platform.dal.cluster.SQLData;

import java.util.Map;

/**
 * @author c7ch23en
 */
public interface ShardRule extends Forkable<ShardRule> {

    String shardByValue(Object value);

    String shardByFields(SQLData fields);

}
