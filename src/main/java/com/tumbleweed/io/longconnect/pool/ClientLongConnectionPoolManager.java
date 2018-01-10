package com.tumbleweed.io.longconnect.pool;

import com.tumbleweed.io.bean.AppBeanBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ClientLongConnectionPoolManager {
	static final Logger logger = LoggerFactory.getLogger(ClientLongConnectionPoolManager.class);

	private static ConcurrentHashMap<String, LinkedBlockingQueue<AppBeanBase>> clientMap = new ConcurrentHashMap<String, LinkedBlockingQueue<AppBeanBase>>();
	
	public static synchronized void registeClientContext(String suffix,int poolsize,AppBeanBase clientContext)
	{
		logger.info("注册app:"+suffix);
		LinkedBlockingQueue<AppBeanBase> clientContextQueue = clientMap.get(suffix);
		if(null == clientContextQueue)
		{
			clientContextQueue = new LinkedBlockingQueue<AppBeanBase>(poolsize);
			clientContextQueue.add(clientContext);
			clientMap.put(suffix, clientContextQueue);
		}else{
			clientContextQueue.add(clientContext);
		}
	}
	
	public static AppBeanBase getClientContext(String suffix,long timeout)
	{
		logger.info("获取app:"+suffix);
		LinkedBlockingQueue<AppBeanBase> clientContextQueue = clientMap.get(suffix);
		if(null != clientContextQueue)
		{
			try {
				return clientContextQueue.poll(timeout, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error("获取连接异常", e);
				return null;
			}
		}
		
		return null;
	}
	
	public static void releaseClientContext(String suffix,AppBeanBase abb)
	{
		logger.info("归还app:"+suffix);
		LinkedBlockingQueue<AppBeanBase> clientContextQueue = clientMap.get(suffix);
		if(null != clientContextQueue)
		{
			if(!clientContextQueue.contains(abb))
			{
				clientContextQueue.add(abb);
			}
		}
	}
}
