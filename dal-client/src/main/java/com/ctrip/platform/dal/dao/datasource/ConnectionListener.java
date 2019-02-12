package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.helper.Ordered;

import java.sql.Connection;

public interface ConnectionListener extends Ordered {
    void onCreateConnection(String poolDesc, Connection connection, long startTime);

    void onCreateConnectionFailed(String poolDesc, String connDesc, Throwable exception, long startTime);

    void onReleaseConnection(String poolDesc, Connection connection);

    void onAbandonConnection(String poolDesc, Connection connection);

    void onBorrowIdleConnection(String poolDesc, Connection connection, long startTime);

    void onBorrowIdleConnectionFailed(String poolDesc, String connection, Exception exception, long startTime);

    void onGetConnection(String poolDesc, String conDesc, int totalSize, int busySize, int idleSize, int waitSize, long startTime);

    void onGetConnectionFailed(String poolDesc, String conDesc, int totalSize, int busySize, int idleSize, int waitSize, Exception exception, long startTime);
}
