package com.ctrip.platform.dal.cluster;

import com.ctrip.platform.dal.dao.StatementParameters;

import java.util.LinkedList;
import java.util.List;

/**
 * @author c7ch23en
 */
public class PreparedSQLContext {

    private String sql;
    private List<Iterable<IndexedSQLParameter>> paramsList = new LinkedList<>();

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Iterable<IndexedSQLParameter>> getParamsList() {
        return paramsList;
    }

    public void addParams(Iterable<IndexedSQLParameter> params) {
        paramsList.add(params);
    }

}
