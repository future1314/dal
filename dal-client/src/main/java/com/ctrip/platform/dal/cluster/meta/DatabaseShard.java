package com.ctrip.platform.dal.cluster.meta;

import com.ctrip.platform.dal.dao.DalEventEnum;

import java.util.Set;

/**
 * @author c7ch23en
 */
public interface DatabaseShard {

    int getShardIndex();

    Database selectMaster();

    Database selectSlave();

    Database selectDatabase(DalEventEnum eventType);

    Database selectDatabase(DalEventEnum eventType, String databaseTag);

    Database selectDatabase(DalEventEnum eventType, Set<String> databaseTags);

}
