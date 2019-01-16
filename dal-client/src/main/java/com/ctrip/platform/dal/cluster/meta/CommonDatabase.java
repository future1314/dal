package com.ctrip.platform.dal.cluster.meta;

import java.util.HashSet;
import java.util.Set;

/**
 * @author c7ch23en
 */
public class CommonDatabase implements Database {

    private DatabaseRole role;
    private int readWeights;
    private String server;
    private int port;
    private String dbName;
    private String username;
    private String password;
    private Set<String> tags = new HashSet<>();

    @Override
    public DatabaseRole getRole() {
        return role;
    }

    public void setRole(DatabaseRole role) {
        this.role = role;
    }

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
        tags.add(tag);
    }

}
