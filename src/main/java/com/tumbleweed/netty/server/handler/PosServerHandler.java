package com.tumbleweed.netty.server.handler;

import com.tumbleweed.netty.core.bean.BootstrapContextHelper;
import com.tumbleweed.netty.core.constants.IoConstants;
import com.tumbleweed.netty.server.adapter.PosAdapter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PosServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

	private static final Logger logger = LoggerFactory.getLogger(PosServerHandler.class);

	private String suffix;

	private Map<String, Object> requestMap = null;

	public PosServerHandler(String suffix) {
		this.suffix = suffix;
	}
	
	@Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		String remoteIp = ctx.channel().remoteAddress().toString();
		if(remoteIp.indexOf("172.16.70.3") != -1){
			ctx.close();
		}
    }

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		requestMap = new ConcurrentHashMap<String, Object>();
	}

	@Override
	public void channelRead0(final ChannelHandlerContext ctx, ByteBuf in)
			throws Exception {
		logger.warn("======pos网关处理器收到pos上送的8583数据，开始调用交易系统，将由交易系统进行处理，等待交易系统返回......");
		byte[] packets = new byte[in.readableBytes()];
		in.readBytes(packets);
		PosAdapter posAdapter = (PosAdapter) BootstrapContextHelper.getBean("posAdapter");
		requestMap.put(IoConstants.POS_ORIGINAL_PACKETS, packets);
		requestMap.put(IoConstants.SUFFIX, suffix);
		
		//获取原IP
		requestMap.put(IoConstants.REMOTEIP, getClientIp(ctx.channel()));
		
		byte[] packetsBack = posAdapter.postPosMessage(requestMap);
		if (packetsBack == null) {
			logger.warn("======pos网关处理器收到交易系统返回的8583数据为空，pos连接将关闭");
			ctx.close();
		} else {
			logger.warn("======pos网关处理器收到交易系统返回的8583数据，将由编码器编码后将报文下送给pos");
			ctx.writeAndFlush(packetsBack).addListener(ChannelFutureListener.CLOSE);
		}
	}

	/**
	 * 获取客户端IP
	 * @param channel
	 * @return
	 */
	private String getClientIp(final Channel channel){
	    String ip = "";
	    if(channel==null) return ip;
	    SocketAddress address = channel.remoteAddress();
	    
	    if (address != null) {
	        ip = address.toString().trim();
	        logger.debug("======pos原IP地址初始值为:[{}]",ip);
	        int index = ip.lastIndexOf(':');
	        if (index < 1) {
	             index = ip.length();
	         }
	         ip = ip.substring(1, index);
	     }
	     logger.info("======截取后pos原IP地址为:[{}]",ip);
	     return ip;
	 }
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.error(suffix + "======pos网关处理器处理过程中发生错误，pos连接将关闭，错误原因："
				+ cause.getMessage(), cause);
		ctx.close();
	}

	public Map<String, Object> getRequestMap() {
		return requestMap;
	}

	public void setRequestMap(Map<String, Object> requestMap) {
		this.requestMap = requestMap;
	}
}
