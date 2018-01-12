package com.tumbleweed.netty.client.handler;

import com.mysql.jdbc.StringUtils;
import com.tumbleweed.netty.client.context.DomainCollectionContext;
import com.tumbleweed.netty.client.domain.HsmDomain;
import com.tumbleweed.netty.core.util.ChannelHandlerContextUtil;
import com.tumbleweed.netty.core.util.ContextUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;

public class HsmClientHandler extends SimpleChannelInboundHandler<HsmDomain> {
	private static final Logger logger = LoggerFactory.getLogger(HsmClientHandler.class);

	private String suffix;

	public HsmClientHandler(String suffix) throws Exception {
		if (StringUtils.isNullOrEmpty(suffix)) {
			throw new Exception(suffix + " suffix is null");
		}
		this.suffix = suffix;
	}

	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
		ContextUtil.getClientContext(suffix).setExceptionCaughtFlag(true);
		ContextUtil.getClientContext(suffix).setCtx(ctx);
		ContextUtil.getDomainCollectionContext(suffix).setAliveTime(System.currentTimeMillis());
		ContextUtil.getDomainCollectionContext(suffix).setReConnCount(0);
		ChannelHandlerContextUtil.setChannel(suffix);
	}

	public void channelRead0(final ChannelHandlerContext ctx, HsmDomain hsm)
			throws Exception {
		logger.debug("加密机网关处理器收到HSM返回的数据");
		DomainCollectionContext dcc = ContextUtil.getDomainCollectionContext(suffix);
        HsmDomain hsmOld = (HsmDomain) dcc.removeSyncQueueDomain();
		hsmOld.setReturnDB(hsm);
	}

	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
	}

	public void channelUnregistered(final ChannelHandlerContext ctx)
			throws Exception {
		logger.debug(suffix + " 连接通道关闭");
		ContextUtil.getClientContext(suffix).setCtx(null);
		ChannelHandlerContextUtil.removeChannel(suffix);
		if (ContextUtil.getClientContext(suffix).isExceptionCaughtFlag()) {
			logger.debug(suffix + " 连接通道已经关闭，正在检测连接，准备重连");
			ContextUtil.getClientContext(suffix).checkConnection();
		}
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		if (cause instanceof ConnectException || cause instanceof IOException) {
			logger.error("ConnectException or IOException 加密机网关即将重连.......");
		} else {
			logger.debug("加密机网关发生错误:" + cause.getMessage());
		}
	}
}