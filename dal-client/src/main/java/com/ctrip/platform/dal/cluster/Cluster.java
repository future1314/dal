package com.ctrip.platform.dal.cluster;

import com.ctrip.platform.dal.cluster.context.ShardRequestContext;
import com.ctrip.platform.dal.cluster.context.ShardResultContext;

/**
 * @author c7ch23en
 */
public interface Cluster {

    ShardResultContext shard(ShardRequestContext requestCtx);

}
