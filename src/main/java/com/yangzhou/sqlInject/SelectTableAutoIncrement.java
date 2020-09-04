package com.yangzhou.sqlInject;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.defaults.RawSqlSource;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;

public class SelectTableAutoIncrement extends AbstractMethod {

	@Override
	public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
		SqlMethod sqlMethod = SqlMethod.SELECT_TABLE_AUTO_INCREMENT;
		SqlSource sqlSource = new RawSqlSource(configuration,
				String.format(sqlMethod.getSql(), tableInfo.getTableName()), Object.class);
		return this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, Long.class, null);
	}

}
