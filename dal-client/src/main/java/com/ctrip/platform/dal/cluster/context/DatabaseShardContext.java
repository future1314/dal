package com.ctrip.platform.dal.cluster.context;

import com.ctrip.platform.dal.cluster.route.RWConnectionProxy;

import java.sql.Connection;
import java.util.Set;

/**
 * @author c7ch23en
 */
public interface DatabaseShardContext {

    RWConnectionProxy getRWConnectionProxy();

    TableShardContext getTableShardContext(String tableShardId);

    Set<String> getAllTableShardIds();

}
