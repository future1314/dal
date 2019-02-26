package com.ctrip.platform.dal.cluster.context;

import com.ctrip.platform.dal.cluster.SQLData;
import com.ctrip.platform.dal.cluster.parameter.NamedSqlParameters;

import java.util.HashMap;
import java.util.Map;

/**
 * @author c7ch23en
 */
public class TableShardContextImp implements TableShardContext {

    private int shardIndex;
    private String targetTableName;
    private Map<Integer, NamedSqlParameters> indexedRowSet = new HashMap<>();

    @Override
    public int getShardIndex() {
        return shardIndex;
    }

    @Override
    public String getTargetTableName() {
        return targetTableName;
    }

    @Override
    public Map<Integer, NamedSqlParameters> getIndexedRowSet() {
        return indexedRowSet;
    }

    public void setShardIndex(int shardIndex) {
        this.shardIndex = shardIndex;
    }

    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }

    public void addRow(int index, NamedSqlParameters row) {
        indexedRowSet.put(index, row);
    }

}
