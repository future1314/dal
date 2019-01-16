package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.cluster.context.SortedFieldsList;
import com.ctrip.platform.dal.dao.DalHints;

/**
 * @author c7ch23en
 */
public class ClusterCombinedInsertTask<T> extends InsertTaskAdapter<T> implements ClusterBulkTask<Integer, T> {

    @Override
    public Integer execute(DalHints hints, SortedFieldsList fieldsList) {
        return null;
    }

}
