package com.ctrip.platform.dal.cluster.config;

import com.ctrip.platform.dal.cluster.Cluster;
import com.ctrip.platform.dal.cluster.ClusterWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author c7ch23en
 */
public class ClusterLocator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterLocator.class);

    private ConfigLoader clusterConfigLoader;
    private ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
    private Map<String, ClusterWrapper> clusters = new HashMap<>();

    public ClusterLocator(ConfigLoader clusterConfigLoader) {
        this.clusterConfigLoader = clusterConfigLoader;
    }

    public void initialize() throws Exception {
        loadConfig();
        ses.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    loadConfig();
                } catch (Exception e) {
                    LOGGER.error("Load cluster config error", e);
                }
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    public Cluster getCluster(String clusterName) {
        return clusters.get(clusterName);
    }

    private void loadConfig() throws Exception {
        List<Cluster> loadedClusters = clusterConfigLoader.load();
        for (Cluster cluster : loadedClusters) {
            Cluster prev = getAndSetInnerCluster(cluster);
            if (prev == null)
                LOGGER.info(String.format("Cluster '%s' initialized!", cluster.getClusterName()));
            else
                LOGGER.info(String.format("Cluster '%s' refreshed!", cluster.getClusterName()));
        }
    }

    private Cluster getAndSetInnerCluster(Cluster cluster) {
        ClusterWrapper wrapper = clusters.get(cluster.getClusterName());
        if (wrapper == null) {
            wrapper = new ClusterWrapper(cluster);
            clusters.put(cluster.getClusterName(), wrapper);
            return null;
        } else {
            return wrapper.getAndSet(cluster);
        }
    }

}
