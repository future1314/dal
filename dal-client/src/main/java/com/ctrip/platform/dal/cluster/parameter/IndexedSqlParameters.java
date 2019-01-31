package com.ctrip.platform.dal.cluster.parameter;

import java.util.Set;

/**
 * @author c7ch23en
 */
public interface IndexedSqlParameters {

    Object getParamValue(int paramIndex);

    int getSqlType(int paramIndex);

    Set<Integer> getParamIndexes();

}
