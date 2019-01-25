package com.ctrip.platform.dal.cluster;

/**
 * @author c7ch23en
 */
public interface NamedSQLParameter extends SQLParameter {

    String getName();

    boolean isInParam();

}
