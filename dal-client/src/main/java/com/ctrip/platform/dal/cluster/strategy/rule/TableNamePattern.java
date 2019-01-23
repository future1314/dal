package com.ctrip.platform.dal.cluster.strategy.rule;

/**
 * @author c7ch23en
 */
public interface TableNamePattern extends Forkable<TableNamePattern> {

    String getTargetTableName(String logicTableName, String shardId);

}
