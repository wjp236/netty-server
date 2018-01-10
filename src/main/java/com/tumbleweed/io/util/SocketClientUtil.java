package com.tumbleweed.io.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClientUtil {

	static final Logger logger = LoggerFactory.getLogger(SocketClientUtil.class);
	/**
	 * 数据发送
	 * 
	 * @param ipaddress
	 *            IP地址
	 * @param port
	 *            端口
	 * @param headlen
	 *            报文头长度
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static byte[] doSendFixLengthData(String ipaddress, int port,
			int headlen, byte[] data,int timeout) throws Exception {
		Socket socket = null;
		//
		try{
			socket= new Socket(ipaddress, port);
			//
			socket.setSoTimeout(timeout);
			//
			DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			//
			writeByteArray(out, data, headlen);
			//
			byte[] bytes = read(in, headlen);
			out.close();
			in.close();
			return bytes;
			
		}catch (Exception e) {
			logger.warn("doSendFixLengthData ipaddress="+ipaddress+",port="+port+",timeout="+timeout,e);
			throw e;
		}finally{
			if(null != socket && !socket.isClosed()){
				socket.close();
			}
		}
	}
	
	private static byte[] read(DataInputStream in,int headlen) throws IOException { 
//		DataInputStream dis = new DataInputStream(is); 
 
		byte[] lengthByt = new byte[headlen];
		in.read(lengthByt);
		String lengthStr = new String(lengthByt);
		int length = 0;
		try {
			length = Integer.parseInt(lengthStr.trim());
		} catch (NumberFormatException e) {
			logger.error("不是合法的报文", e);
			return new byte[0];
		}
		if (logger.isDebugEnabled()) {
			logger.debug("接收到标识长度为 " + length+" 的报文");
		}
  
		byte[] buffer = new byte[length]; 
		in.readFully(buffer);
		if (logger.isDebugEnabled()) {
			logger.debug("实际接收报文长度为: " + buffer.length);
		}

		return buffer;
	}
	
	protected static void writeByteArray(OutputStream out, byte[] data,int headlen)
			throws IOException { 
		if (logger.isDebugEnabled()) {
			logger.debug("发送报文长度为: " + data.length);
		}
		out.write(append(data.length,headlen).getBytes());
		out.write(data);
		out.flush();
	}

	private static String append(int length,int headlength) {
		String lengthStr = String.valueOf(length);
		int tmp = headlength- lengthStr.length();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < tmp; i++) {
			builder.append(" ");
		}
		builder.append(lengthStr);
		return builder.toString();
	} 
}
