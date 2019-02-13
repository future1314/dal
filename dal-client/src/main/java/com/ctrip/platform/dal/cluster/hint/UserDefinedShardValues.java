package com.ctrip.platform.dal.cluster.hint;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author c7ch23en
 */
public class UserDefinedShardValues implements Shards {

    private Set<Object> shardValues;

    public UserDefinedShardValues(Collection<Object> shardValues) {
        this.shardValues = new HashSet<>(shardValues);
    }

    public UserDefinedShardValues(Object shardValue) {
        shardValues = new HashSet<>();
        shardValues.add(shardValue);
    }

    public Set<Object> getShards() {
        return new HashSet<>(shardValues);
    }

}
