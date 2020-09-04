package com.yangzhou.sqlInject;

import java.util.List;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;

public class MySqlInjector extends LogicSqlInjector {

	@Override
	public List<AbstractMethod> getMethodList() {
		List<AbstractMethod> list = super.getMethodList();
		list.add(new SelectMaxId());
		list.add(new SelectTableAutoIncrement());
		return list;
	}

}
