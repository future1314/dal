package com.ctrip.platform.dal.cluster;

import java.util.List;

/**
 * @author c7ch23en
 */
public interface SQLHandler {

    PreparedSQLContext prepare(String targetTableName, Iterable<SQLData> rowSet);

    PreparedSQLContext prepare(String targetTableName, NamedSQLParameters params);

}
