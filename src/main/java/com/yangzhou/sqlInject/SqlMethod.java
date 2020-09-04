package com.yangzhou.sqlInject;

public enum SqlMethod {
	SELECT_MAX_ID("selectMaxId", "获取当前表的最大ID", "<script>\nSELECT MAX(ID) FROM %s\n</script>"),
	SELECT_TABLE_AUTO_INCREMENT("selectTableAutoIncrement", "获取当前表的ID自增长值",
			"SELECT AUTO_INCREMENT from information_schema.`TABLES` where table_name = '%s'");

	private final String method;
	private final String desc;
	private final String sql;

	SqlMethod(String method, String desc, String sql) {
		this.method = method;
		this.desc = desc;
		this.sql = sql;
	}

	public String getMethod() {
		return method;
	}

	public String getDesc() {
		return desc;
	}

	public String getSql() {
		return sql;
	}

}
