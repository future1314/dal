package com.ctrip.platform.dal.cluster.hint;

import java.util.Arrays;

/**
 * @author c7ch23en
 */
public class RouteHintsBuilder {

    private Shards dbShards = new UndefinedShards();
    private Shards tableShards = new UndefinedShards();
    private DefaultReadWriteHints rwHints = new DefaultReadWriteHints();

    public RouteHintsBuilder() {}

    public RouteHintsBuilder setAllDbShards() {
        dbShards = new AllShards();
        return this;
    }

    public RouteHintsBuilder setAllTableShards() {
        tableShards = new AllShards();
        return this;
    }

    public RouteHintsBuilder setDbShards(String... shards) {
        dbShards = new UserDefinedShards(Arrays.asList(shards));
        return this;
    }

    public RouteHintsBuilder setTableShards(String... shards) {
        tableShards = new UserDefinedShards(Arrays.asList(shards));
        return this;
    }

    public RouteHintsBuilder setDbShardValues(Object... shardValues) {
        dbShards = new UserDefinedShardValues(Arrays.asList(shardValues));
        return this;
    }

    public RouteHintsBuilder setTableShardValues(Object... shardValues) {
        tableShards = new UserDefinedShardValues(Arrays.asList(shardValues));
        return this;
    }

    public RouteHintsBuilder readFromMaster() {
        rwHints.readFromMaster();
        return this;
    }

    // 需要
    public RouteHintsBuilder writeToSlave() {
        rwHints.writeToSlave();
        return this;
    }

    public RouteHintsBuilder addSlaveTags(String... tags) {
        rwHints.addSlaveTags(Arrays.asList(tags));
        return this;
    }

    public RouteHints build() {
        ShardHints shardHints = new DefaultShardHints(dbShards, tableShards);
        return new DefaultRouteHints(shardHints, rwHints);
    }

}
