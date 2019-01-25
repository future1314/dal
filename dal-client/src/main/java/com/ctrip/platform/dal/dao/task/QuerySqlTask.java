package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;

import com.ctrip.platform.dal.cluster.NamedSQLParameters;
import com.ctrip.platform.dal.cluster.PreparedSQLContext;
import com.ctrip.platform.dal.cluster.SQLData;
import com.ctrip.platform.dal.cluster.SQLHandler;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

public class QuerySqlTask<T> extends TaskAdapter implements SqlTask<T>, SQLHandler {
	private String sql;
	private DalResultSetExtractor<T> extractor;

	public void setSql(String sql) {
		this.sql = sql;
	}

	public QuerySqlTask(DalResultSetExtractor<T> extractor) {
		this.extractor = extractor;
	}

	public DalResultSetExtractor<T> getExtractor() {
		return extractor;
	}

	@Override
	public T execute(DalClient client, String sql, StatementParameters parameters, DalHints hints, DalTaskContext taskContext) throws SQLException {
		if (client instanceof DalContextClient)
			return ((DalContextClient) client).query(sql, parameters, hints, extractor, taskContext);
		else
			throw new DalRuntimeException("The client is not instance of DalClient");
	}

	@Override
	public PreparedSQLContext prepare(String targetTableName, Iterable<SQLData> rowSet) {
		throw new UnsupportedOperationException();
	}

	@Override
	public PreparedSQLContext prepare(String targetTableName, NamedSQLParameters params) {
		StatementParameters parameters = (StatementParameters) params;
		try {
			sql = SQLCompiler.compile(sql, parameters.getAllInParameters());
			parameters.compile();
			PreparedSQLContext context = new PreparedSQLContext();
			context.setSql(sql);
			context.addParams(parameters);
		} catch (SQLException e) {}
		return null;
	}

}
