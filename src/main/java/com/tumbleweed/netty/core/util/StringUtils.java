package com.tumbleweed.netty.core.util;

import com.tumbleweed.netty.core.constants.ConstantsBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils {

	private static final Logger logger = LoggerFactory.getLogger(StringUtils.class);

	public static boolean isNullOrEmpty(String value) {
		if (value == null)
			return true;
		value = value.trim();
		if (value.equals(""))
			return true;
		return false;
	}

	public static String notNull(String value) {
		if (value == null)
			return ConstantsBase.ANY_STRING;
		return value;
	}

	public static String trim(String value) {
		if (value == null)
			return ConstantsBase.ANY_STRING;
		return value.trim();
	}

	public static String shortString(String value, int maxWidth) {
		if (value == null)
			return ConstantsBase.ANY_STRING;

		int width = 0, index = 0;
		while (index < value.length()) {
			int ch = value.charAt(index);
			width += ((ch & 0xFF00) == 0) ? 1 : 2;
			if (width + 3 > maxWidth)
				return value.substring(0, index) + "...";
			index++;
		}
		return value;
	}

	public static String replace(String value, String find, String replace) {
		if (value == null)
			return ConstantsBase.ANY_STRING;
		StringBuffer buffer = new StringBuffer(value.length());
		int findLength = find.length();
		int fromIndex = 0;
		int toIndex = value.indexOf(find);

		while (toIndex >= 0) {
			buffer.append(value.substring(fromIndex, toIndex));
			fromIndex = toIndex + findLength;
			toIndex = value.indexOf(find, fromIndex);
		}
		buffer.append(value.substring(fromIndex, value.length()));
		return buffer.toString();
	}

	public static String replaceIgnoreCase(String value, String find,
			String replace) {
		if (value == null)
			return ConstantsBase.ANY_STRING;

		StringBuffer buffer = new StringBuffer(value.length());
		String valueLowerCase = value.toLowerCase();
		String findLowerCase = find.toLowerCase();

		int findLength = find.length();
		int fromIndex = 0;
		int toIndex = valueLowerCase.indexOf(findLowerCase);

		while (toIndex >= 0) {
			buffer.append(value.substring(fromIndex, toIndex));
			fromIndex = toIndex + findLength;
			toIndex = valueLowerCase.indexOf(findLowerCase, fromIndex);
		}
		buffer.append(value.substring(fromIndex, value.length()));
		return buffer.toString();
	}

	public static boolean parseBoolean(String value) {
		if (value == null)
			return false;
		return value.equalsIgnoreCase("true");
	}

	public static boolean parseBoolean(String value, boolean defaultValue) {
		if (value == null)
			return defaultValue;

		if (value.equalsIgnoreCase("true"))
			return true;

		if (value.equalsIgnoreCase("false"))
			return false;
		return defaultValue;
	}

	public static int parseInt(String value, int defaultValue) {
		if (value == null)
			return defaultValue;
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			logger.debug("Bad integer, default used.", e);

			return defaultValue;
		}
	}

	public static long parseLong(String value, long defaultValue) {
		if (value == null)
			return defaultValue;
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			logger.debug("Bad long integer, default used.", e);
			e.printStackTrace();
			return defaultValue;
		}
	}

	public static boolean checkEmail(String email) {
		String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(email);
		return m.find();
	}

	public static List<String> convertList(String idStr, String separator) {
		List<String> ids = new ArrayList<String>();
		if (!StringUtils.isNullOrEmpty(idStr)) {
			String[] idArray = idStr.split(separator);
			for (int i = 0; i < idArray.length; i++) {
				if (!"".equals(idArray[i])) {
					ids.add(idArray[i]);
				}
			}
		}
		return ids;
	}
	/**
	 * 转换对象为字符串，如果对象为null，则返回空字符串
	 * 
	 * @param obj 需要转换的对象
	 * @return 转换后的字符串
	 */
	public final static String toStringWithOutNull(Object obj) {
		return obj == null ? "" : String.valueOf(obj);
	}
}