package com.ctrip.platform.dal.cluster.hint;

import java.util.Set;

/**
 * @author c7ch23en
 */
public interface ReadWriteHints {

    boolean readFromMaster();

    boolean writeToSlave();

    Set<String> getSlaveTags();

}
