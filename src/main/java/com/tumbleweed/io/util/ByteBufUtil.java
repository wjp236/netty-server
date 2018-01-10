package com.tumbleweed.io.util;

import com.mysql.jdbc.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;

/**
 * 
 * @Title: ByteBufUtil.java
 * @Package: com.lemon.io.util
 * @Description: ByteBuf工具类
 * @author: jiangke
 * @date: 2014年1月2日
 * @version V1.0
 */
public class ByteBufUtil {
	public static ByteBuf stringToByteBuf(String value) throws Exception {
		if (StringUtils.isNullOrEmpty(value)) {
			throw new Exception("stringToByteBuf value is null");
		}
		ByteBuf byteBuf = Unpooled.buffer(value.length());
		byteBuf.writeBytes(value.getBytes());
		return byteBuf;
	}
	
	public static ByteBuf writeByte(byte[] value) throws Exception {
		if (value == null || value.length == 0) {
			throw new Exception("writeByte value is null");
		}
		ByteBuf byteBuf = Unpooled.buffer(value.length);
		byteBuf.writeBytes(value);
		return byteBuf;
	}

	public static void writeByteBuf(ChannelHandlerContext chc, String value)
			throws Exception {
		chc.writeAndFlush(stringToByteBuf(value));
	}
	
	public static String toString(ByteBuf byteBuf){
		if(byteBuf == null)
			return null;
		return byteBuf.toString(Charset.defaultCharset());
	}
}
