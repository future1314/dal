package com.ctrip.platform.dal.dao.task;


import com.ctrip.platform.dal.cluster.CombinedAction;
import com.ctrip.platform.dal.cluster.OperationType;
import com.ctrip.platform.dal.cluster.PreparedSQLContext;
import com.ctrip.platform.dal.cluster.SQLData;
import com.ctrip.platform.dal.cluster.context.Row;
import com.ctrip.platform.dal.dao.StatementParameters;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class CombinedInsertTask<T> extends AbstractCombinedInsertTask<T> implements CombinedAction {
	protected static final String TPL_SQL_COMBINED_INSERT = "INSERT INTO %s(%s) VALUES %s";

	@Override
	protected String getSqlTpl() {
		return TPL_SQL_COMBINED_INSERT;
	}

	@Override
	public PreparedSQLContext prepareSQLContext(String targetTableName, List<SQLData> rowData) {
		StatementParameters parameters = new StatementParameters();
		StringBuilder values = new StringBuilder();

		List<String> validColumns = buildValidColumnsForInsert(new HashSet<String>());

		int startIndex = 1;
		for (SQLData row : rowData) {
			Map<String, ?> rowMap = ((Row) row).getData();
			int paramCount = addParameters(startIndex, parameters, rowMap, validColumns);
			startIndex += paramCount;
			values.append(String.format("(%s),", combine("?", paramCount, ",")));
		}
		String insertColumns = combineColumns(validColumns, COLUMN_SEPARATOR);
		String sql = String.format("INSERT INTO %s(%s) VALUES %s",
				quote(targetTableName), insertColumns,
				values.substring(0, values.length() - 2) + ")");

		PreparedSQLContext context = new PreparedSQLContext();
		context.setSql(sql);
		context.setParameters(parameters);
		return context;
	}

	@Override
	public OperationType getOperationType() {
		return OperationType.INSERT;
	}

}