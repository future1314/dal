package com.ctrip.platform.dal.cluster.hint;

/**
 * @author c7ch23en
 */
public class DefaultShardHints implements ShardHints {

    private Shards dbShards;
    private Shards tableShards;

    public DefaultShardHints(Shards dbShards, Shards tableShards) {
        this.dbShards = dbShards;
        this.tableShards = tableShards;
    }

    @Override
    public Shards getDbShards() {
        return dbShards;
    }

    @Override
    public Shards getTableShards() {
        return tableShards;
    }

}
