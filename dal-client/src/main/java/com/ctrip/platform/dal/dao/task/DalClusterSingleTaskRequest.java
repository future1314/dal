package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.cluster.Cluster;
import com.ctrip.platform.dal.cluster.SQLData;
import com.ctrip.platform.dal.cluster.context.Row;
import com.ctrip.platform.dal.cluster.parameter.NamedSqlParameters;
import com.ctrip.platform.dal.dao.DalHints;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author c7ch23en
 */
public class DalClusterSingleTaskRequest<T> implements DalClusterRequest<int[]> {

    private Cluster cluster;
    private String logicTableName;
    private DalHints hints;
    private SingleTask<T> task;
    private T pojo;
    private SQLData rowData;
    private List<T> rawPojos = new ArrayList<>();
    private List<Map<String, ?>> daoPojos;

    public DalClusterSingleTaskRequest(Cluster cluster, String logicTableName, DalHints hints, SingleTask<T> task, T pojo) {
        this.cluster = cluster;
        this.logicTableName = logicTableName;
        this.hints = hints;
        this.task = task;
        this.pojo = pojo;
    }

    @Override
    public void validateAndPrepare() throws SQLException {
        rawPojos.add(pojo);
        daoPojos = task.getPojosFields(rawPojos);
        rowData = buildRowData(daoPojos.get(0));
    }

    @Override
    public int[] execute() throws SQLException {
        if (task instanceof SingleInsertTask) {
            List<SQLData> datas = new LinkedList<>();
            datas.add(rowData);
            SingleInsertTask singleInsertTask = (SingleInsertTask) task;
            int count = daoPojos.size();
            if (count == 1) {
                NamedSqlParameters parameters = singleInsertTask.buildParams(daoPojos.get(0));
                cluster.insert(logicTableName, parameters);
            } else {
                NamedSqlParameters[] parameters = singleInsertTask.buildParams(daoPojos);
                cluster.insert(logicTableName, parameters);
            }
        }
        return new int[0];
    }

    private SQLData buildRowData(Map<String, ?> pojo) {
        Row row = new Row();
        for (Map.Entry<String, ?> field : pojo.entrySet()) {
            row.set(field.getKey(), field.getValue());
        }
        return row;
    }
}
