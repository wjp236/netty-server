package com.tumbleweed.io.constants;

import java.util.Date;

/**
 * 
 * @Title: ConstantsBase.java
 * @Package: com.lemon.core.constants
 * @Description: 基本常量定义
 * @author: jiangke
 * @date: 2013年10月19日
 * @version V1.0
 */
public class ConstantsBase {
	
	/** 默认分页-页长度  */
	public static final String LIMIT = "15";
	/** 默认分页-起始行数  */
	public static final String START = "0";
	
	public static final String CONTENT_DEF_FILE_PATH = "/config.properties";

	public static final String ANY_STRING = "";

	public static final String BLANK_STRING = " ";

	public static final Date INTIIAL = new Date(0);

	public static final Date CURRENT = new Date(Long.MAX_VALUE);

	public static final int SUNDAY = 0;

	public static final int MONDAY = 1;

	public static final int TUESDAY = 2;

	public static final int WEDNESDAY = 3;

	public static final int THURDAY = 4;

	public static final int FRIDAY = 5;

	public static final int SATURDAY = 6;
	
	public static final String VERTICAL_BAR_SPLIT = "\\|";
	
	public static final String Y_FLG = "Y";
	
	public static final String N_FLG = "N";
	
	public static final String UUID_TYPE_SEQUENCE_NO = "S";
	
	public static final String UUID_TYPE_ORDER_NO = "O";
	
	public static final char UUID_RETENTION = 'X';
	
	public static final String UUID_MACHINE_ID = "uuid.machine.id";
}
