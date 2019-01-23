package com.ctrip.platform.dal.cluster.meta;

/**
 * @author c7ch23en
 */
public enum DatabaseCategory {

    MYSQL("mysql"),
    SQLSERVER("sqlserver");

    private String name;

    DatabaseCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
