package com.ctrip.platform.dal.cluster.meta;

import com.ctrip.platform.dal.cluster.strategy.rule.ShardRule;
import com.ctrip.platform.dal.cluster.strategy.rule.TableNamePattern;

import java.util.Properties;

/**
 * @author c7ch23en
 */
public class DefaultLogicTable implements LogicTable {

    private final String logicTableName;
    private ShardRule dbShardRule;
    private ShardRule tableShardRule;
    private TableNamePattern tableNamePattern;

    public DefaultLogicTable(String logicTableName) {
        this.logicTableName = logicTableName;
    }

    @Override
    public String getLogicTableName() {
        return logicTableName;
    }

    @Override
    public ShardRule getDbShardRule() {
        return dbShardRule;
    }

    @Override
    public ShardRule getTableShardRule() {
        return tableShardRule;
    }

    @Override
    public TableNamePattern getTableNamePattern() {
        return tableNamePattern;
    }

    public void setDbShardRule(Properties properties, ShardRule ref) {
        this.dbShardRule = ref.fork(properties);
    }

    public void setTableShardRule(Properties properties, ShardRule ref) {
        this.tableShardRule = ref.fork(properties);
    }

    public void setTableNamePattern(Properties properties, TableNamePattern ref) {
        this.tableNamePattern = ref.fork(properties);
    }

}
