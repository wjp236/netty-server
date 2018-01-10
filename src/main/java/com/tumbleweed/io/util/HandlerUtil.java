package com.tumbleweed.io.util;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * 
 * @Title: HandlerLogUtil.java
 * @Package: com.lemon.io.util
 * @Description: netty 日志
 * @author: jiangke
 * @date: 2014年1月2日
 * @version V1.0
 */
public class HandlerUtil {
	private static final Logger logger = LoggerFactory
			.getLogger(HandlerUtil.class);
	
	public static LogLevel getLogLevel(){
		if(logger.isTraceEnabled()){
			return LogLevel.TRACE;
		}else if(logger.isDebugEnabled()){
			return LogLevel.DEBUG;
		}else if(logger.isInfoEnabled()){
			return LogLevel.INFO;
		}else if(logger.isWarnEnabled()){
			return LogLevel.WARN;
		}else if(logger.isErrorEnabled()){
				return LogLevel.ERROR;
		}else{
			return null;
		}
	}
	
	public static void initIoLog(ChannelPipeline pipeline){
		LogLevel logLevel = getLogLevel();
		if (logLevel != null) {
			pipeline.addLast(new LoggingHandler(logLevel));
		}
	}
	
	public static void initChannel(ChannelPipeline pipeline, List<String> handlers, String suffix) throws Exception{
		if (handlers != null && handlers.size() > 0) {
			throw new Exception(suffix + " 没有配置处理器，请检查配置！");
		}
		for(String handler : handlers){
			Class<?> c = Class.forName(handler);
			Class<?>[] parameterTypes = { String.class };
			Constructor<?> constructor = c.getConstructor(parameterTypes);
			Object[] parameters = {suffix};
			pipeline.addLast((ChannelHandler)constructor.newInstance(parameters));
			logger.info(suffix + " " + handler + " 初始化成功！");
		}
	}
}
