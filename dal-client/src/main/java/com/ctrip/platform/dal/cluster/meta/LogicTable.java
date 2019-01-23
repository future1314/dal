package com.ctrip.platform.dal.cluster.meta;

import com.ctrip.platform.dal.cluster.strategy.rule.ShardRule;
import com.ctrip.platform.dal.cluster.strategy.rule.TableNamePattern;

/**
 * @author c7ch23en
 */
public interface LogicTable {

    String getLogicTableName();

    ShardRule getDbShardRule();

    ShardRule getTableShardRule();

    TableNamePattern getTableNamePattern();

}
