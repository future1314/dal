package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;

/**
 * @author c7ch23en
 */
public interface DalClusterRequest<T> {
    /**
     * Do validation and preparation
     */
    void validateAndPrepare() throws SQLException;

    T execute() throws SQLException;
}
