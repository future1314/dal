package com.ctrip.platform.dal.cluster.hint;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author c7ch23en
 */
public class UserDefinedShards implements Shards {

    private Set<Integer> shardIndexes;

    public UserDefinedShards(Collection<Integer> shardIds) {
        this.shardIndexes = new HashSet<>(shardIds);
    }

    public UserDefinedShards(int shardId) {
        shardIndexes = new HashSet<>();
        shardIndexes.add(shardId);
    }

    public Set<Integer> getShards() {
        return new HashSet<>(shardIndexes);
    }

}
