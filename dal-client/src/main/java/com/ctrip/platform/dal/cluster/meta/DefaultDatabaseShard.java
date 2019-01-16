package com.ctrip.platform.dal.cluster.meta;

import com.ctrip.platform.dal.cluster.exception.DalClusterException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author c7ch23en
 */
public class DefaultDatabaseShard implements DatabaseShard {

    private int index;
    private AtomicReference<Database> masterRef = new AtomicReference<>();
    private List<Database> slaves = new ArrayList<>();

    public DefaultDatabaseShard(int index) {
        this.index = index;
    }

    @Override
    public int getShardIndex() {
        return index;
    }

    public void addDatabase(Database database) {
        if (database.getRole() == DatabaseRole.MASTER) {
            if (!masterRef.compareAndSet(null, database))
                throw new DalClusterException("Multi masters");
        } else {
            addSlave(database);
        }
    }

    public Database setMaster(Database master) {
        return masterRef.getAndSet(master);
    }

    public void addSlave(Database slave) {
        slaves.add(slave);
    }

}
