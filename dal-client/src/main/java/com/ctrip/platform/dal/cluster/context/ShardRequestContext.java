package com.ctrip.platform.dal.cluster.context;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

import java.util.List;

/**
 * @author c7ch23en
 */
public interface ShardRequestContext {

    DalHints getHints();

    StatementParameters getParameters();

    List<Fields> getFields();

}
