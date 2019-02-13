package com.ctrip.platform.dal.cluster;

import com.ctrip.platform.dal.cluster.exception.DalClusterException;
import com.ctrip.platform.dal.cluster.parameter.IndexedSqlParameters;
import com.ctrip.platform.dal.cluster.parameter.NamedSqlParameters;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameter;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.exceptions.DalParameterException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author c7ch23en
 */
public class StatementCreator {

    public PreparedStatement prepareStatement(Connection conn, String sql, IndexedSqlParameters parameters) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(sql);
        setParameters(statement, parameters);
        return statement;
    }

    public PreparedStatement prepareStatement(Connection conn, String sql, NamedSqlParameters parameters) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(sql);
        setParameters(statement, parameters);
        return statement;
    }

    public PreparedStatement prepareStatement(Connection conn, String sql, List<NamedSqlParameters> parametersList) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(sql);
        for(NamedSqlParameters parameters: parametersList) {
            setParameters(statement, parameters);
            statement.addBatch();
        }
        return statement;
    }

    public PreparedStatement prepareStatement(Connection conn, String sql, Iterable<IndexedSQLParameter> parameters) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(sql);
        setParameters(statement, parameters);
        return statement;
    }

    public void setParameters(PreparedStatement statement, IndexedSqlParameters parameters) {
        Set<Integer> indexes = parameters.getParamIndexes();
        for (Integer index : indexes) {
            setObject(statement, index, parameters.getParamValue(index), parameters.getSqlType(index));
        }
    }

    public void setParameters(PreparedStatement statement, NamedSqlParameters parameters) {
        Set<String> names = parameters.getParamNames();
        int index = 1;
        for (String name : names) {
            setObject(statement, index++, parameters.getParamValue(name), parameters.getSqlType(name));
        }
    }

    public void setParameters(PreparedStatement statement, Iterable<IndexedSQLParameter> parameters) {
        Iterator<IndexedSQLParameter> iterator = parameters.iterator();
        while (iterator.hasNext()) {
            IndexedSQLParameter parameter = iterator.next();
            setObject(statement, parameter);
        }
    }

    public void setObject(PreparedStatement statement, IndexedSQLParameter parameter) {
        try {
            if (parameter.isDefaultType()) {
                statement.setObject(parameter.getIndex(), parameter.getParamValue());
            } else {
                statement.setObject(parameter.getIndex(), parameter.getParamValue(), parameter.getSqlType());
            }
        } catch (Throwable e) {
            throw new DalClusterException(e);
        }
    }

    public void setObject(PreparedStatement statement, int index, Object value, int sqlType) {
        try {
            statement.setObject(index, value, sqlType);
        } catch (Throwable e) {
            throw new DalClusterException(e);
        }
    }

}
