package com.tumbleweed.io.handler;

import com.tumbleweed.io.context.DomainCollectionContext;
import com.tumbleweed.io.domain.Hsm;
import com.tumbleweed.io.util.ChannelHandlerContextUtil;
import com.tumbleweed.io.util.ContextUtil;
import com.mysql.jdbc.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;

public class HsmClientHandler extends SimpleChannelInboundHandler<Hsm> {
	private static final Logger logger = LoggerFactory
			.getLogger(HsmClientHandler.class);

	private String suffix;

	public HsmClientHandler(String suffix) throws Exception {
		if (StringUtils.isNullOrEmpty(suffix)) {
			throw new Exception(suffix + " suffix is null");
		}
		this.suffix = suffix;
	}

	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
		ContextUtil.getLemonClientContext(suffix).setExceptionCaughtFlag(true);
		ContextUtil.getLemonClientContext(suffix).setCtx(ctx);
		ContextUtil.getDomainCollectionContext(suffix).setAliveTime(
				System.currentTimeMillis());
		ContextUtil.getDomainCollectionContext(suffix).setReConnCount(0);
		ChannelHandlerContextUtil.setChannel(suffix);
	}

	public void channelRead0(final ChannelHandlerContext ctx, Hsm hsm)
			throws Exception {
		logger.debug("加密机网关处理器收到HSM返回的数据");
		DomainCollectionContext dcc = ContextUtil
				.getDomainCollectionContext(suffix);
		Hsm hsmOld = (Hsm) dcc.removeSyncQueueDomain();
		hsmOld.setReturnDB(hsm);
	}

	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
	}

	public void channelUnregistered(final ChannelHandlerContext ctx)
			throws Exception {
		logger.debug(suffix + " 连接通道关闭");
		ContextUtil.getLemonClientContext(suffix).setCtx(null);
		ChannelHandlerContextUtil.removeChannel(suffix);
		if (ContextUtil.getLemonClientContext(suffix).isExceptionCaughtFlag()) {
			logger.debug(suffix + " 连接通道已经关闭，正在检测连接，准备重连");
			ContextUtil.getLemonClientContext(suffix).checkConnection();
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