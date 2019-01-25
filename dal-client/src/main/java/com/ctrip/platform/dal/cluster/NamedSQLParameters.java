package com.ctrip.platform.dal.cluster;

/**
 * @author c7ch23en
 */
public interface NamedSQLParameters extends SQLData {

    NamedSQLParameter getParameter(String name);

}
