package com.ctrip.platform.dal.cluster.meta;

import javax.sql.DataSource;
import java.util.Set;

/**
 * @author c7ch23en
 */
public interface Database {

    boolean isMaster();

    boolean containsTag(String tag);

    boolean containsTags(Set<String> tags);

    DataSource getDataSource();

}
