package com.ctrip.platform.dal.cluster.context;

import com.ctrip.platform.dal.cluster.SQLData;

import java.util.HashMap;
import java.util.Map;

/**
 * @author c7ch23en
 */
public class TableShardContextImp implements TableShardContext {

    private String targetTableName;
    private Map<Integer, SQLData> indexedRowSet = new HashMap<>();

    @Override
    public String getTargetTableName() {
        return targetTableName;
    }

    @Override
    public Map<Integer, SQLData> getIndexedRowSet() {
        return indexedRowSet;
    }

    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }

    public void addRow(int index, SQLData row) {
        indexedRowSet.put(index, row);
    }

}
