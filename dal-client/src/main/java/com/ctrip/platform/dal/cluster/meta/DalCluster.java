package com.ctrip.platform.dal.cluster.meta;

import com.ctrip.platform.dal.cluster.Cluster;
import com.ctrip.platform.dal.cluster.context.ClusterShardResultContext;
import com.ctrip.platform.dal.cluster.context.ShardRequestContext;
import com.ctrip.platform.dal.cluster.context.ShardResultContext;

/**
 * @author c7ch23en
 */
public class DalCluster implements Cluster {

    @Override
    public ShardResultContext shard(ShardRequestContext requestCtx) {
        return new ClusterShardResultContext();
    }

}
