package com.tumbleweed.netty.core.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @Title: ServiceMonitorInterceptor.java
 * @Description: 监控方法执行时间拦截器
 */
public class ServiceMonitorInterceptor implements MethodInterceptor {

	static final Logger logger = LoggerFactory.getLogger(ServiceMonitorInterceptor.class);
	private static ConcurrentHashMap<String, MethodStatus> methodStatus = new ConcurrentHashMap<String, MethodStatus>();
	
	/**
	 * updateStatus 分析周期(单位:次)
	 */
	private long bebugFrequency = 10;
	
	/**
	 * warn 阀值(单位:毫秒)
	 */
	private long warningThreshold = 100;

	public Object invoke(MethodInvocation method) throws Throwable {
		long start = System.currentTimeMillis();
		try {
			return method.proceed();
		} finally {
			updateStatus(method.getMethod().getName(),
					(System.currentTimeMillis() - start));
		}
	}

	private void updateStatus(String methodName, long elapsedTime) {
		MethodStatus stats = methodStatus.get(methodName);
		if (stats == null) {
			stats = new MethodStatus(methodName);
			methodStatus.put(methodName, stats);
		}
		stats.count++;
		stats.totalTime += elapsedTime;
		if (elapsedTime > stats.maxTime) {
			stats.maxTime = elapsedTime;
		}

		if (elapsedTime > warningThreshold) {
			logger.warn("warning: " + methodName + "(), 执行次数 = " + stats.count
					+ ", 本次用时 = " + elapsedTime + " ms, 历史最长用时 = "
					+ stats.maxTime);
		}

		if (stats.count % bebugFrequency == 0) {
			long avgTime = stats.totalTime / stats.count;
			long runningAvg = (stats.totalTime - stats.lastTotalTime)
					/ bebugFrequency;
			logger.info("method: " + methodName + "(), 执行次数 = " + stats.count
					+ ", 本次时长 = " + elapsedTime + ", 平均用时 = " + avgTime
					+ ", 本周期内平均用时(" + bebugFrequency + ") = " + runningAvg
					+ ", 历史最长用时 = " + stats.maxTime);
			stats.lastTotalTime = stats.totalTime;
		}
	}

	public long getBebugFrequency() {
		return bebugFrequency;
	}

	public void setBebugFrequency(long bebugFrequency) {
		this.bebugFrequency = bebugFrequency;
	}

	public long getWarningThreshold() {
		return warningThreshold;
	}

	public void setWarningThreshold(long warningThreshold) {
		this.warningThreshold = warningThreshold;
	}

	class MethodStatus {
		public String methodName;
		public long count;
		public long totalTime;
		public long lastTotalTime;
		public long maxTime;

		public MethodStatus(String methodName) {
			this.methodName = methodName;
		}
	}
}