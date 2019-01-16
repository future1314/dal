package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.cluster.context.SortedFieldsList;
import com.ctrip.platform.dal.dao.DalHints;

/**
 * @author c7ch23en
 */
public interface ClusterBulkTask<K, T> extends DaoTask<T> {

    K execute(DalHints hints, SortedFieldsList fieldsList);

}
