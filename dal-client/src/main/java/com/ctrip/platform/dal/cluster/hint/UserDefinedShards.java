package com.ctrip.platform.dal.cluster.hint;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author c7ch23en
 */
public class UserDefinedShards implements Shards {

    private Set<String> shardIds;

    public UserDefinedShards(Collection<String> shardIds) {
        this.shardIds = new HashSet<>(shardIds);
    }

    public UserDefinedShards(String shardId) {
        shardIds = new HashSet<>();
        shardIds.add(shardId);
    }

    public Set<String> getShards() {
        return new HashSet<>(shardIds);
    }

}
