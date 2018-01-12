package com.tumbleweed.netty.core.util;

import java.util.List;
import java.util.Map;

/**
 * 
 * @Title: Utilities.java
 * @Package: com.lemon.utils
 * @Description: 集合操作工具类
 * @author: jiangke
 * @date: 2013年10月18日
 * @version V1.0
 */
public final class Utilities {
	public static boolean isNullOrEmpty(List<?> list) {
		return (list == null || list.size() == 0);
	}

	public static boolean isNullOrEmpty(Map<?, ?> map) {
		return (map == null || map.size() == 0);
	}
}