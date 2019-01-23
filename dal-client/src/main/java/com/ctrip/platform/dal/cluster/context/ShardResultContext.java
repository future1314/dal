package com.ctrip.platform.dal.cluster.context;

import java.util.Set;

/**
 * @author c7ch23en
 */
public interface ShardResultContext {

    DatabaseShardContext getDbShardContext(String dbShardId);

    Set<String> getAllDbShardIds();

}
