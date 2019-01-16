package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.ResultMerger;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author c7ch23en
 */
public class DalClusterBulkTaskRequest<T> implements DalRequest<T> {

    private String clusterName;
    private String logicTableName;
    private DalHints hints;
    private List<T> rawPojos;

    @Override
    public String getCaller() {
        return null;
    }

    @Override
    public boolean isAsynExecution() {
        return false;
    }

    @Override
    public void validateAndPrepare() throws SQLException {

    }

    @Override
    public boolean isCrossShard() throws SQLException {
        return false;
    }

    @Override
    public Callable<T> createTask() throws SQLException {
        return null;
    }

    @Override
    public Map<String, Callable<T>> createTasks() throws SQLException {
        return null;
    }

    @Override
    public BulkTaskResultMerger<T> getMerger() {
        return null;
    }

    @Override
    public void endExecution() throws SQLException {

    }

    private static class ClusterBulkTaskCallable<T> implements Callable<T> {
        @Override
        public T call() throws Exception {
            return null;
        }
    }

}
