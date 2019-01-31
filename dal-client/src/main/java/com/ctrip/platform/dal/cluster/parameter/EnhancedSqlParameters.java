package com.ctrip.platform.dal.cluster.parameter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author c7ch23en
 */
public class EnhancedSqlParameters implements NamedSqlParameters, IndexedSqlParameters {

    private Map<Integer, ParameterContent> indexedParams = new HashMap<>();
    private Map<String, ParameterContent> namedParams = new HashMap<>();

    public EnhancedSqlParameters(NamedSqlParameters namedParameters) {
        Set<String> names = namedParameters.getParamNames();
        int index = 1;
        for (String name : names) {
            Object value = namedParameters.getParamValue(name);
            int sqlType = namedParameters.getSqlType(name);
            EnhancedParameter param = new EnhancedParameter(index, name, value, sqlType);
            indexedParams.put(index, param);
            namedParams.put(name, param);
            index++;
        }
    }

    @Override
    public Object getParamValue(int paramIndex) {
        return indexedParams.get(paramIndex).getValue();
    }

    @Override
    public int getSqlType(int paramIndex) {
        return indexedParams.get(paramIndex).getSqlType();
    }

    @Override
    public Set<Integer> getParamIndexes() {
        return indexedParams.keySet();
    }

    @Override
    public Object getParamValue(String paramName) {
        return namedParams.get(paramName).getValue();
    }

    @Override
    public int getSqlType(String paramName) {
        return namedParams.get(paramName).getSqlType();
    }

    @Override
    public Set<String> getParamNames() {
        return namedParams.keySet();
    }

}
