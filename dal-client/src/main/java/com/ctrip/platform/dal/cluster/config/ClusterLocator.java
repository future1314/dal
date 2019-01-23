package com.ctrip.platform.dal.cluster.config;

import com.ctrip.platform.dal.cluster.Cluster;

import java.util.HashMap;
import java.util.Map;

/**
 * @author c7ch23en
 */
public class ClusterLocator {

    private Map<String, Cluster> clusters = new HashMap<>();

    public Cluster getCluster(String clusterName) {
        return clusters.get(clusterName);
    }

    public Cluster addCluster(Cluster cluster) {
        return clusters.put(cluster.getClusterName(), cluster);
    }

}
