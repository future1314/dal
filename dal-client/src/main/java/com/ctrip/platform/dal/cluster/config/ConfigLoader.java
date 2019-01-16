package com.ctrip.framework.dal.cluster.config;

import com.ctrip.framework.dal.cluster.exception.DalClusterConfigException;

/**
 * @author c7ch23en
 */
public interface ConfigLoader {

    ClusterLocator load() throws Exception;

}
