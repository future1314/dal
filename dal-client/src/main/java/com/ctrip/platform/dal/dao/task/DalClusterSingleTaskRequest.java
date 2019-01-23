package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.cluster.Cluster;
import com.ctrip.platform.dal.cluster.SQLData;
import com.ctrip.platform.dal.dao.DalHints;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author c7ch23en
 */
public class DalClusterSingleTaskRequest<T> implements DalClusterRequest {

    private Cluster cluster;
    private String logicTableName;
    private DalHints hints;
    private SingleTask<T> task;
    private T pojo;
    private SQLData rowData;
    private List<T> rawPojos = new ArrayList<>();
    private List<Map<String, ?>> daoPojos;

    @Override
    public void validateAndPrepare() throws SQLException {
        rawPojos.add(pojo);
    }

    @Override
    public void execute() {

    }
}
