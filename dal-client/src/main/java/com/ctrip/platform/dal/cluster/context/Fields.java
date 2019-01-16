package com.ctrip.platform.dal.cluster.context;

import java.util.HashMap;
import java.util.Map;

/**
 * @author c7ch23en
 */
public class Fields {

    private Map<String, Object> fields = new HashMap<>();

    public Object get(String name) {
        return fields.get(name);
    }

    public void set(String name, Object value) {
        fields.put(name, value);
    }

}
