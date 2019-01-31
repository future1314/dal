package com.ctrip.platform.dal.cluster.parameter;

import java.util.Set;

/**
 * @author c7ch23en
 */
public interface NamedSqlParameters {

    Object getParamValue(String paramName);

    int getSqlType(String paramName);

    Set<String> getParamNames();

}
