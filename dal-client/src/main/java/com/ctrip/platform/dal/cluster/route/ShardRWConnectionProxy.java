package com.ctrip.platform.dal.cluster.route;

import com.ctrip.platform.dal.cluster.meta.DatabaseShard;
import com.ctrip.platform.dal.dao.DalEventEnum;

import java.sql.Connection;

/**
 * @author c7ch23en
 */
public class ShardRWConnectionProxy implements RWConnectionProxy {

    private DatabaseShard databaseShard;

    @Override
    public Connection getConnection(DalEventEnum event) {
        return null;
    }

}
