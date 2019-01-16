package com.ctrip.platform.dal.cluster.context;

import com.ctrip.platform.dal.cluster.route.RWConnectionProxy;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author c7ch23en
 */
public class DefaultDatabaseShardContext implements DatabaseShardContext {

    private Map<String, TableShardContext> tableShardContexts = new HashMap<>();
    private RWConnectionProxy connectionProxy;

    @Override
    public RWConnectionProxy getRWConnectionProxy() {
        return connectionProxy;
    }

    @Override
    public TableShardContext getTableShardContext(String tableShardId) {
        return tableShardContexts.get(tableShardId);
    }

    @Override
    public Set<String> getAllTableShardIds() {
        return tableShardContexts.keySet();
    }

    public void addTableShardContext(String tableShardId, TableShardContext ctx) {
        tableShardContexts.put(tableShardId, ctx);
    }

}
