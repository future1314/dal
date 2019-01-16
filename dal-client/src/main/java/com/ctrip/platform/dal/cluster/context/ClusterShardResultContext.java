package com.ctrip.platform.dal.cluster.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author c7ch23en
 */
public class ClusterShardResultContext implements ShardResultContext {

    private Map<String, DatabaseShardContext> dbShardContexts = new HashMap<>();

    @Override
    public DatabaseShardContext getDbShardContext(String dbShardId) {
        return dbShardContexts.get(dbShardId);
    }

    @Override
    public Set<String> getAllDbShardIds() {
        return dbShardContexts.keySet();
    }

}
