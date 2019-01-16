package com.ctrip.platform.dal.cluster.context;

/**
 * @author c7ch23en
 */
public class DefaultTableShardContext implements TableShardContext {

    private String targetTableName;
    private SortedFieldsList fieldsList;

    @Override
    public String getTargetTableName() {
        return null;
    }

    @Override
    public SortedFieldsList getFieldsList() {
        return null;
    }

    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }

    public void setFieldsList(SortedFieldsList fieldsList) {
        this.fieldsList = fieldsList;
    }

}
