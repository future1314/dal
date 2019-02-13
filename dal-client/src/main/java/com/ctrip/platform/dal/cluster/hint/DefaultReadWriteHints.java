package com.ctrip.platform.dal.cluster.hint;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author c7ch23en
 */
public class DefaultReadWriteHints implements ReadWriteHints {

    private boolean readFromMaster;
    private boolean writeToSlave;
    private Set<String> slaveTags;

    public DefaultReadWriteHints() {
        readFromMaster = false;
        writeToSlave = false;
        slaveTags = new HashSet<>();
    }

    public void setReadFromMaster() {
        readFromMaster = true;
    }

    public void setWriteToSlave() {
        writeToSlave = true;
    }

    public void addSlaveTags(Collection<String> slaveTags) {
        this.slaveTags.addAll(slaveTags);
    }

    @Override
    public boolean readFromMaster() {
        return readFromMaster;
    }

    @Override
    public boolean writeToSlave() {
        return writeToSlave;
    }

    @Override
    public Set<String> getSlaveTags() {
        return new HashSet<>(slaveTags);
    }

}
