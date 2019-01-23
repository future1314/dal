package com.ctrip.platform.dal.cluster.route;

import com.ctrip.platform.dal.dao.DalEventEnum;

import java.sql.Connection;

/**
 * @author c7ch23en
 */
public interface RWConnectionProxy {

    Connection getConnection(DalEventEnum event);

}
