package com.ctrip.platform.dal.cluster.context;

import java.util.HashMap;
import java.util.Map;

/**
 * @author c7ch23en
 */
public class ShardedFieldsList {

    private Map<String, SortedFieldsList> fieldsLists = new HashMap<>();

    public SortedFieldsList getFieldsList(String shardId) {
        return fieldsLists.get(shardId);
    }

    public void setFieldsList(String shardId, SortedFieldsList fieldsList) {
        fieldsLists.put(shardId, fieldsList);
    }

}
