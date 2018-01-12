package com.tumbleweed.netty.core.util;


import com.tumbleweed.netty.core.constants.LogType;

/**
 * @Description:日志工具类，通用。
 * @date: 2014年7月14日
 */
public class LogUtils {

	private static String separator = "##";

	/**
	 * @Description:生成日志信息，需要4个参数，返回String,返回格式如：BAP##logQuery##logQuery#日志查询
	 * @param: @param systemName
	 * @param: @param tradeCode
	 * @param: @param tradeNum
	 * @param: @param msg
	 * @param: @return
	 * @return: String
	 * @throws
	 */
	public static String genLogs(LogType logType, String tradeCode,
                                 String tradeNum, String msg) {
		String systemName = logType.getType();
		tradeCode = StringUtils.isNullOrEmpty(tradeCode) ? "-" : tradeCode;
		tradeNum = StringUtils.isNullOrEmpty(tradeNum) ? "-" : tradeNum;

		StringBuffer sb = new StringBuffer();

		sb.append(systemName);
		sb.append(separator);
		sb.append(tradeCode);
		sb.append(separator);
		sb.append(tradeNum);
		sb.append(separator);
		sb.append(msg);

		return sb.toString();
	}
}
