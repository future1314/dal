package com.ctrip.platform.dal.dao.task;


import com.ctrip.platform.dal.cluster.NamedSQLParameters;
import com.ctrip.platform.dal.cluster.OperationType;
import com.ctrip.platform.dal.cluster.PreparedSQLContext;
import com.ctrip.platform.dal.cluster.SQLData;
import com.ctrip.platform.dal.cluster.SQLHandler;
import com.ctrip.platform.dal.cluster.SingleHandler;
import com.ctrip.platform.dal.cluster.context.Row;
import com.ctrip.platform.dal.cluster.parameter.NamedSqlParameters;
import com.ctrip.platform.dal.dao.StatementParameters;

import java.util.Map;
import java.util.Set;

public class SingleInsertTask<T> extends AbstractSingleInsertTask<T> implements SQLHandler {
	protected static final String TPL_SQL_INSERT = "INSERT INTO %s (%s) VALUES (%s)";

	@Override
	public String getSqlTpl() {
		return TPL_SQL_INSERT;
	}

	@Override
	public PreparedSQLContext prepare(String targetTableName, Iterable<SQLData> rowData) {
		Row row = (Row) rowData.iterator().next();

		String insertSql = buildInsertSql(row, targetTableName);
		StatementParameters parameters = new StatementParameters();
		addParameters(parameters, row.getData());

		PreparedSQLContext context = new PreparedSQLContext();
		context.setSql(insertSql);
		context.addParams(parameters);
		return context;
	}

	@Override
	public PreparedSQLContext prepare(String targetTableName, NamedSQLParameters params) {
		throw new UnsupportedOperationException();
	}

	protected String buildInsertSql(Row fields, String effectiveTableName) {
		Set<String> remainedColumns = fields.getData().keySet();
		String cloumns = combineColumns(remainedColumns, COLUMN_SEPARATOR);
		String values = combine(PLACE_HOLDER, remainedColumns.size(), COLUMN_SEPARATOR);

		return String.format(getSqlTpl(), quote(effectiveTableName), cloumns, values);
	}
}
