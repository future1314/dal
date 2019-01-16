package com.ctrip.framework.dal.cluster.config;

import com.ctrip.framework.dal.cluster.Cluster;

import java.net.URL;
import java.util.List;

/**
 * @author c7ch23en
 */
public class LocalConfigLoader extends XMLConfigLoader {

    private static final String DEFAULT_FILE_NAME = "dal-cluster-test2.xml";

    @Override
    public ClusterLocator load() throws Exception {
        URL url = LocalConfigLoader.class.getClassLoader().getResource(DEFAULT_FILE_NAME);
        List<Cluster> clusters = loadClusters(url.openStream());
        ClusterLocator locator = new ClusterLocator();
        for (Cluster cluster : clusters)
            locator.addCluster(cluster);
        return locator;
    }

}
