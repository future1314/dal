package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.cluster.Cluster;
import com.ctrip.platform.dal.cluster.SQLData;
import com.ctrip.platform.dal.cluster.context.Row;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.ResultMerger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author c7ch23en
 */
public class DalClusterBulkTaskRequest<K, T> implements DalClusterRequest {

    private Cluster cluster;
    private String logicTableName;
    private DalHints hints;
    private List<T> rawPojos;
    private List<Map<String, ?>> daoPojos;
    private BulkTask<K, T> task;
    private List<SQLData> rowDataList = new ArrayList<>();

    public DalClusterBulkTaskRequest(Cluster cluster, String logicTableName, DalHints hints, List<T> rawPojos, BulkTask<K, T> task) {
        this.cluster = cluster;
        this.logicTableName = logicTableName;
        this.hints = hints;
        this.rawPojos = rawPojos;
        this.task = task;
    }

    @Override
    public void validateAndPrepare() {
        daoPojos = task.getPojosFields(rawPojos);
        for (Map<String, ?> daoPojo : daoPojos)
            rowDataList.add(buildRowData(daoPojo));
    }

    @Override
    public void execute() throws SQLException {
        if (task instanceof CombinedInsertTask) {
            cluster.execute(logicTableName, rowDataList, (CombinedInsertTask) task);
        }
        if (task instanceof BatchInsertTask) {
            cluster.execute(logicTableName, rowDataList, (BatchInsertTask) task);
        }
    }

    private SQLData buildRowData(Map<String, ?> pojo) {
        Row row = new Row();
        for (Map.Entry<String, ?> field : pojo.entrySet()) {
            row.set(field.getKey(), field.getValue());
        }
        return row;
    }

}
