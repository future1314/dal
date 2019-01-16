package com.ctrip.platform.dal.cluster.context;

/**
 * @author c7ch23en
 */
public interface TableShardContext {

    String getTargetTableName();

    SortedFieldsList getFieldsList();

}
