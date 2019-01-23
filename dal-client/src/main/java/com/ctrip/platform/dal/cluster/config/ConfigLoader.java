package com.ctrip.platform.dal.cluster.config;

/**
 * @author c7ch23en
 */
public interface ConfigLoader {

    ClusterLocator load() throws Exception;

}
