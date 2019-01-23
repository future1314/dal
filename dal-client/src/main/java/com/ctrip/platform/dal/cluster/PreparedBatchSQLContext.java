package com.ctrip.platform.dal.cluster;

import com.ctrip.platform.dal.dao.StatementParameters;

/**
 * @author c7ch23en
 */
public class PreparedBatchSQLContext {

    private String sql;
    private StatementParameters[] parametersList;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public StatementParameters[] getParametersList() {
        return parametersList;
    }

    public void setParametersList(StatementParameters[] parametersList) {
        this.parametersList = parametersList;
    }

}
