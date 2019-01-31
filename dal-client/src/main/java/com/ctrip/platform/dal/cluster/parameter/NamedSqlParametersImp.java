package com.ctrip.platform.dal.cluster.parameter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author c7ch23en
 */
public class NamedSqlParametersImp implements NamedSqlParameters {

    private Map<String, ParameterContent> params = new HashMap<>();

    public NamedSqlParametersImp() {}

    public void add(String name, Object value) {
        params.put(name, new ParameterContent(value));
    }

    @Override
    public Object getParamValue(String paramName) {
        return params.get(paramName).getValue();
    }

    @Override
    public int getSqlType(String paramName) {
        return params.get(paramName).getSqlType();
    }

    @Override
    public Set<String> getParamNames() {
        return params.keySet();
    }

}
