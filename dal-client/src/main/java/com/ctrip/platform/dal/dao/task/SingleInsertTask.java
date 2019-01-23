package com.ctrip.platform.dal.dao.task;


import com.ctrip.platform.dal.cluster.OperationType;
import com.ctrip.platform.dal.cluster.PreparedSQLContext;
import com.ctrip.platform.dal.cluster.SQLData;
import com.ctrip.platform.dal.cluster.SingleAction;
import com.ctrip.platform.dal.cluster.context.Row;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

public class SingleInsertTask<T> extends AbstractSingleInsertTask<T> implements SingleAction {
	protected static final String TPL_SQL_INSERT = "INSERT INTO %s (%s) VALUES (%s)";

	@Override
	public String getSqlTpl() {
		return TPL_SQL_INSERT;
	}

	@Override
	public PreparedSQLContext prepareSQLContext(String targetTableName, SQLData rowData) {
		Row row = (Row) rowData;

		String insertSql = buildInsertSql(row, targetTableName);
		StatementParameters parameters = new StatementParameters();
		addParameters(parameters, row.getData());

		PreparedSQLContext context = new PreparedSQLContext();
		context.setSql(insertSql);
		context.setParameters(parameters);
		return context;
	}

	@Override
	public OperationType getOperationType() {
		return OperationType.INSERT;
	}

	protected String buildInsertSql(Row fields, String effectiveTableName) {
		Set<String> remainedColumns = fields.getData().keySet();
		String cloumns = combineColumns(remainedColumns, COLUMN_SEPARATOR);
		String values = combine(PLACE_HOLDER, remainedColumns.size(), COLUMN_SEPARATOR);

		return String.format(getSqlTpl(), quote(effectiveTableName), cloumns, values);
	}
}
