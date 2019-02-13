package com.ctrip.platform.dal.cluster.hint;

import java.util.Set;

/**
 * @author c7ch23en
 */
public class DefaultRouteHints implements RouteHints {

    private ShardHints shardHints;
    private ReadWriteHints rwHints;

    public DefaultRouteHints(ShardHints shardHints, ReadWriteHints rwHints) {
        this.shardHints = shardHints;
        this.rwHints = rwHints;
    }

    @Override
    public Shards getDbShards() {
        return shardHints.getDbShards();
    }

    @Override
    public Shards getTableShards() {
        return shardHints.getTableShards();
    }

    @Override
    public boolean readFromMaster() {
        return rwHints.readFromMaster();
    }

    @Override
    public boolean writeToSlave() {
        return rwHints.writeToSlave();
    }

    @Override
    public Set<String> getSlaveTags() {
        return rwHints.getSlaveTags();
    }

}
