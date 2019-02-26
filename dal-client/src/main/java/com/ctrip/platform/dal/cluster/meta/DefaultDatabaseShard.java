package com.ctrip.platform.dal.cluster.meta;

import com.ctrip.platform.dal.cluster.exception.DalClusterException;
import com.ctrip.platform.dal.dao.DalEventEnum;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author c7ch23en
 */
public class DefaultDatabaseShard implements DatabaseShard {

    private int shardIndex;
    private Database master;
    private List<Database> slaves = new ArrayList<>();

    public DefaultDatabaseShard(int shardIndex) {
        this.shardIndex = shardIndex;
    }

    @Override
    public int getShardIndex() {
        return shardIndex;
    }

    @Override
    public Database selectMaster() {
        return master;
    }

    @Override
    public Database selectSlave() {
        return selectSlave(slaves);
    }

    @Override
    public Database selectDatabase(DalEventEnum eventType) {
        if (eventType != DalEventEnum.QUERY)
            return selectMaster();
        return selectSlave();
    }

    @Override
    public Database selectDatabase(DalEventEnum eventType, String databaseTag) {
        if (eventType != DalEventEnum.QUERY)
            return selectMaster();
        Set<String> tags = new HashSet<>();
        tags.add(databaseTag);
        return selectSlave(filterSlaves(tags));
    }

    @Override
    public Database selectDatabase(DalEventEnum eventType, Set<String> databaseTags) {
        return null;
    }

    private Database selectSlave(List<Database> slaves) {
        if (slaves != null && slaves.size() > 0)
            // TODO: weighted select
            return slaves.get(0);
        return null;
    }

    private List<Database> filterSlaves(Set<String> tags) {
        List<Database> targets = new ArrayList<>();
        for (Database slave : slaves) {
            if (slave.containsTags(tags))
                targets.add(slave);
        }
        return targets;
    }

    public void addDatabase(Database database) {
        if (database.isMaster())
            setMaster(database);
        else
            addSlave(database);
    }

    public void setMaster(Database master) {
        this.master = master;
    }

    public void addSlave(Database slave) {
        slaves.add(slave);
    }

}
