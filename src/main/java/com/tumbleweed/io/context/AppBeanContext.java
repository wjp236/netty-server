package com.tumbleweed.io.context;

import com.tumbleweed.io.bean.AppBeanBase;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @Title: AppBeanContext.java
 * @Package: com.lemon.io.context
 * @Description: 将appBeanName放入map,用于页面显示等
 * @author: jiangke
 * @date: 2013年12月24日
 * @version V1.0
 */
public class AppBeanContext {
	private Map<String, AppBeanBase> appBeanMap = new HashMap<String, AppBeanBase>();
	
	public void putAppBean(String appBeanName, AppBeanBase appBean){
		appBeanMap.put(appBeanName, appBean);
	}
	
	public void getAppBean(String appBeanName){
		appBeanMap.get(appBeanName);
	}
	
	public Map<String, AppBeanBase> getappBeanMap(){
		return appBeanMap;
	}
}
