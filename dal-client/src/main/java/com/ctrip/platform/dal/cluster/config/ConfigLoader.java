package com.ctrip.platform.dal.cluster.config;

import com.ctrip.platform.dal.cluster.Cluster;

import java.util.List;

/**
 * @author c7ch23en
 */
public interface ConfigLoader {

    List<Cluster> load() throws Exception;

}
