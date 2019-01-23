package com.ctrip.platform.dal.cluster.meta;

import com.ctrip.platform.dal.cluster.exception.DalClusterException;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author c7ch23en
 */
public abstract class CommonDatabase implements Database {

    private DatabaseRole role;
    private int readWeights;
    protected String server;
    protected int port;
    protected String dbName;
    private String username;
    private String password;
    private AtomicReference<Set<String>> tags = new AtomicReference<>();

    protected CommonDatabase(DatabaseRole role) {
        this.role = role;
        internalInit();
    }

    private void internalInit() {
        tags.set(new HashSet<String>());
    }

    @Override
    public boolean isMaster() {
        if (role == null)
            throw new DalClusterException("Database role uninitialized");
        return role == DatabaseRole.MASTER;
    }

    @Override
    public boolean containsTag(String tag) {
        return tags.get().contains(tag);
    }

    @Override
    public boolean containsTags(Set<String> tags) {
        return this.tags.get().containsAll(tags);
    }

    @Override
    public DataSource getDataSource() {
        PoolProperties properties = new PoolProperties();
        properties.setUrl(getUrl());
        properties.setUsername(username);
        properties.setPassword(password);
        properties.setDriverClassName(getDriverClassName());
        return new org.apache.tomcat.jdbc.pool.DataSource(properties);
    }

    protected abstract String getUrl();

    protected abstract String getDriverClassName();

    public void setReadWeights(int readWeights) {
        this.readWeights = readWeights;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addTag(String tag) {
        tags.get().add(tag);
    }

    public void addTags(Set<String> tags) {
        this.tags.get().addAll(tags);
    }

    public Set<String> setTags(Set<String> tags) {
        return this.tags.getAndSet(tags);
    }

}
