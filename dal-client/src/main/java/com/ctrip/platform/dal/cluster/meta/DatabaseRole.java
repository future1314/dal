package com.ctrip.platform.dal.cluster.meta;

/**
 * @author c7ch23en
 */
public enum DatabaseRole {

    MASTER("master"),
    SLAVE("slave");

    private String name;

    private DatabaseRole(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
