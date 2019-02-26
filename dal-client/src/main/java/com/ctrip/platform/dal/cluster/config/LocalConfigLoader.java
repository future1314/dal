package com.ctrip.platform.dal.cluster.config;

import com.ctrip.platform.dal.cluster.Cluster;

import java.net.URL;
import java.util.List;

/**
 * @author c7ch23en
 */
public class LocalConfigLoader extends XMLConfigLoader {

    private static final String DEFAULT_FILE_NAME = "dal-cluster-01.xml";

    @Override
    public List<Cluster> load() throws Exception {
        URL url = LocalConfigLoader.class.getClassLoader().getResource(DEFAULT_FILE_NAME);
        List<Cluster> clusters = loadClusters(url.openStream());
        return clusters;
    }

}
