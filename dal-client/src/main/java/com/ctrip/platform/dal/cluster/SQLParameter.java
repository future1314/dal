package com.ctrip.platform.dal.cluster;

/**
 * @author c7ch23en
 */
public interface SQLParameter {

    Object getParamValue();

    boolean isDefaultType();

    int getSqlType();

}
