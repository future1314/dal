package com.ctrip.platform.dal.cluster;

import com.ctrip.platform.dal.dao.StatementParameters;

/**
 * @author c7ch23en
 */
public class PreparedSQLContext {

    private String sql;
    private StatementParameters parameters;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public StatementParameters getParameters() {
        return parameters;
    }

    public void setParameters(StatementParameters parameters) {
        this.parameters = parameters;
    }

}
