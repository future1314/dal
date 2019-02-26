package com.ctrip.platform.dal.cluster;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author c7ch23en
 */
public interface ResultHandler {

    void execute(int dbShardIndex, int tableShardIndex, ResultSet rs) throws SQLException;

    // fail callback

    void complete();

}
