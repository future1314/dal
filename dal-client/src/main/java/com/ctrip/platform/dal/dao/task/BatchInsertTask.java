package com.ctrip.platform.dal.dao.task;


import com.ctrip.platform.dal.cluster.BatchAction;
import com.ctrip.platform.dal.cluster.OperationType;
import com.ctrip.platform.dal.cluster.PreparedBatchSQLContext;
import com.ctrip.platform.dal.cluster.PreparedSQLContext;
import com.ctrip.platform.dal.cluster.SQLData;
import com.ctrip.platform.dal.cluster.context.Row;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BatchInsertTask<T> extends AbstractBatchInsertTask<T> implements BatchAction {
	private static final String TPL_SQL_BATCH_INSERT = "INSERT INTO %s (%s) VALUES (%s)";

	@Override
	protected String getSqlTpl() {
		return TPL_SQL_BATCH_INSERT;
	}

	@Override
	public PreparedBatchSQLContext prepareSQLContext(String targetTableName, List<SQLData> rowData) {
		StatementParameters[] parametersList = new StatementParameters[rowData.size()];
		int i = 0;

		Set<String> unqualifiedColumns = new HashSet<>();

		for (SQLData row : rowData) {
			Map<String, ?> pojo = ((Row) row).getData();
			removeUnqualifiedColumns(pojo, unqualifiedColumns);

			StatementParameters parameters = new StatementParameters();
			addParameters(parameters, pojo);
			parametersList[i++] = parameters;
		}

		String batchInsertSql = buildBatchInsertSql(unqualifiedColumns, targetTableName);
		PreparedBatchSQLContext context = new PreparedBatchSQLContext();
		context.setSql(batchInsertSql);
		context.setParametersList(parametersList);
		return context;
	}

	@Override
	public OperationType getOperationType() {
		return OperationType.INSERT;
	}

	private String buildBatchInsertSql(Set<String> unqualifiedColumns, String tableName) {
		List<String> finalInsertableColumns = buildValidColumnsForInsert(unqualifiedColumns);

		String values = combine(PLACE_HOLDER, finalInsertableColumns.size(), COLUMN_SEPARATOR);
		String insertColumns = combineColumns(finalInsertableColumns, COLUMN_SEPARATOR);

		return String.format(getSqlTpl(), quote(tableName), insertColumns, values);
	}

}
