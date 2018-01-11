package com.tumbleweed.netty.core.context;

import com.tumbleweed.netty.core.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class EnvVar {
	private static EnvVar instance = null;

	private static final Object obj = new Object();

	private static final Map<String, String> evnMap = new HashMap<String, String>();

	private EnvVar() {
	}

	public String getVar(String key) {
		if (null == key)
			throw new IllegalArgumentException("key is null!");
		return evnMap.get(key);
	}

	public Integer getIntegerVar(String key) {
		if (null == key)
			throw new IllegalArgumentException("key is null!");
		String obj = evnMap.get(key);
		return StringUtils.isNullOrEmpty(obj) ? null : Integer.valueOf(obj);
	}

	public Long getLongVar(String key) {
		if (null == key)
			throw new IllegalArgumentException("key is null!");
		String obj = evnMap.get(key);
		return StringUtils.isNullOrEmpty(obj) ? null : Long.valueOf(obj);
	}

	public void setVar(String key, String value) {
		if (null == key || null == value)
			throw new IllegalArgumentException("key or value is null!");
		evnMap.put(key, value);
	}

	public boolean containsVar(String key) {
		return evnMap.containsKey(key);
	}

	public static EnvVar curEnv() {
		synchronized (obj) {
			if (instance == null)
				instance = new EnvVar();
		}
		return instance;
	}
}
