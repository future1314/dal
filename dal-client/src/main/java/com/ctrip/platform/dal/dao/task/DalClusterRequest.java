package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;

/**
 * @author c7ch23en
 */
public interface DalClusterRequest {
    /**
     * Do validation and preparation
     */
    void validateAndPrepare() throws SQLException;

    void execute() throws SQLException;
}
