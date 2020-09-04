package com.yangzhou.util;

import java.util.Map;

public class MapUtil {
	public static <U, V> boolean equal(Map<U, V> first, Map<U, V> second) {
		if (first.size() != second.size())
			return false;

		if (!SetUtil.equal(first.keySet(), second.keySet()))
			return false;

		for (final U key : first.keySet()) {
			if (!first.get(key).equals(second.get(key)))
				return false;
		}
		return true;
	}
}
