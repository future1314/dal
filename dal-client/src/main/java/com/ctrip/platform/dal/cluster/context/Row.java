package com.ctrip.platform.dal.cluster.context;

import com.ctrip.platform.dal.cluster.SQLData;

import java.util.HashMap;
import java.util.Map;

/**
 * @author c7ch23en
 */
public class Row implements SQLData {

    private Map<String, Object> data = new HashMap<>();

    public Row() {}

    public Row(Map<String, Object> data) {
        this.data = data;
    }

    public Object get(String name) {
        return data.get(name);
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void set(String name, Object value) {
        data.put(name, value);
    }

    @Override
    public Object getValue(String name) {
        return data.get(name);
    }

}
