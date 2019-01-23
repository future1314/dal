package com.ctrip.platform.dal.cluster;

import com.ctrip.platform.dal.cluster.exception.DalClusterException;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameter;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.exceptions.DalParameterException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author c7ch23en
 */
public class StatementCreator {

    public PreparedStatement prepareStatement(Connection conn, String sql, StatementParameters parameters) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(sql);

        setParameters(statement, parameters);

        return statement;
    }

    public PreparedStatement prepareStatement(Connection conn, String sql, StatementParameters[] parametersList) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(sql);

        for(StatementParameters parameters: parametersList) {
            setParameters(statement, parameters);
            statement.addBatch();
        }

        return statement;
    }

    public void setParameters(PreparedStatement statement, StatementParameters parameters) {
        for (StatementParameter parameter: parameters.values()) {
            if(parameter.isInputParameter())
                setObject(statement, parameter);
        }
    }

    public void setObject(PreparedStatement statement, StatementParameter parameter) {
        try {
            if (parameter.isDefaultType()) {
                statement.setObject(parameter.getIndex(), parameter.getValue());
            } else {
                statement.setObject(parameter.getIndex(), parameter.getValue(), parameter.getSqlType());
            }
        } catch (Throwable e) {
            throw new DalClusterException(e);
        }
    }

}
