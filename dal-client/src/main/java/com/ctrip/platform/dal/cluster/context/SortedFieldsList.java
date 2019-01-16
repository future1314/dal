package com.ctrip.platform.dal.cluster.context;

import java.util.HashMap;
import java.util.Map;

/**
 * @author c7ch23en
 */
public class SortedFieldsList {

    private Map<Integer, Fields> fieldsList = new HashMap<>();

    public Fields getFields(int index) {
        return fieldsList.get(index);
    }

    public void setFields(int index, Fields fields) {
        fieldsList.put(index, fields);
    }

}
