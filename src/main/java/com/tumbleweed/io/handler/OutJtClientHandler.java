package com.tumbleweed.io.handler;

import com.tumbleweed.io.adapter.PtsAdapter;
import com.tumbleweed.io.bean.BootstrapContextHelper;
import com.tumbleweed.io.constants.IoConstants;
import com.tumbleweed.io.domain.DomainBase;
import com.tumbleweed.io.domain.Lemon8583;
import com.tumbleweed.io.util.ConstantsUtil;
import com.tumbleweed.io.util.ContextUtil;
import com.tumbleweed.io.util.HexConvertUtil;
import com.mysql.jdbc.StringUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;

public class OutJtClientHandler extends SimpleChannelInboundHandler<Lemon8583> {
	private static final Logger logger = LoggerFactory
			.getLogger(OutJtClientHandler.class);
	
	private String suffix;
	
	private static int heartbeatCount_req = 0;
	
	private static int heartbeatCount = ConstantsUtil.initOutJtConnectTimeout();
	
	private Lemon8583 lemon8583 = new Lemon8583(IoConstants.OUTJT_HEARTBEAT_PACKET);

	public OutJtClientHandler(String suffix) throws Exception {
		if (StringUtils.isNullOrEmpty(suffix)) {
			throw new Exception(suffix + " suffix is null");
		}
		this.suffix = suffix;
	}

	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
		ContextUtil.getLemonClientContext(suffix).setExceptionCaughtFlag(true);
		ContextUtil.getLemonClientContext(suffix).setCtx(ctx);
	}

    public void channelRead0(final ChannelHandlerContext ctx, Lemon8583 lemon8583) throws Exception {
        if (lemon8583.getHeartbeatPacket() != null
                && IoConstants.OUTJT_HEARTBEAT_PACKET.equals(lemon8583.getHeartbeatPacket())) {
            heartbeatCount_req--;
            if (heartbeatCount_req < 0) {
                logger.error("======" + suffix + " heartbeat error!");
            }
            logger.warn("======" + suffix + " jt client 处理器收到server心跳报文");
        } else {
            logger.warn("======" + suffix + " jt client 处理器 正在处理......");
            String uuid = lemon8583.getUuid();
            if (lemon8583.isInitiative()) {
                logger.warn("======" + suffix + " jt client 收到机构推送的packets:" + lemon8583.getPacketsHex());
                PtsAdapter ptsAdapter = (PtsAdapter) BootstrapContextHelper.getBean("ptsAdapter");
                ptsAdapter.postGatewayMessage(HexConvertUtil.hexStr2byte(lemon8583.getPacketsHex()));
            } else {
                if (!StringUtils.isNullOrEmpty(uuid)) {
                    DomainBase blockingDB = ContextUtil.getDomainCollectionContext(suffix).getDomain(uuid);
                    if (blockingDB != null) {
                        blockingDB.setReturnDB(lemon8583);
                        logger.warn("======" + suffix + " jt client 处理完毕， 并结束同步阻塞");
                        blockingDB.endBlocking(uuid);
                    } else {
                        logger.warn("======" + suffix + " jt client 处理失败， DomainCollectionContext未找到blockingDB");
                    }
                } else {
                    logger.warn("======" + suffix + " jt client 处理失败， lemon8583未指定uuid");
                }
            }
        }
    }

	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
	}

	public void channelUnregistered(final ChannelHandlerContext ctx)
			throws Exception {
		logger.warn("======" + suffix + " jt client 连接通道关闭");
		ContextUtil.getLemonClientContext(suffix).setCtx(null);
		if(ContextUtil.getLemonClientContext(suffix).isExceptionCaughtFlag()){
			logger.warn("======" + suffix + " jt client 连接通道已经关闭，正在检测连接，准备重连");
			ContextUtil.getLemonClientContext(suffix).checkConnection();
		}
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		if (cause instanceof ConnectException || cause instanceof IOException) {
			logger.error(suffix + " ConnectException or IOException 发生错误, now reconnect.......");
		}else{
			logger.error(suffix + " exceptionCaught 发生错误:" + cause.getMessage());
		}
	}
	
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if (!(evt instanceof IdleStateEvent)) {
			return;
		}
		IdleStateEvent e = (IdleStateEvent) evt;
		if (e.state() == IdleState.READER_IDLE) {
			if(ContextUtil.getLemonClientContext(suffix).isExceptionCaughtFlag()){
				heartbeatCount_req ++;
				logger.warn("======heartbeatCount_req=" + heartbeatCount_req);
				if(heartbeatCount_req >= heartbeatCount){
					heartbeatCount_req = 0;
					logger.warn("======" + suffix + " jt client 长时间未收到心跳包, 准备重连......");
					ctx.close();
					return;
				}
				ChannelFuture channelFuture = ctx.writeAndFlush(lemon8583);
				channelFuture.addListener(new ChannelFutureListener() {
					public void operationComplete(ChannelFuture future) {
						if (!future.isSuccess()) {
							logger.error("======" + suffix + "发送心跳包消息失败!");
						}
					}
				});
			}
		}
	}
}
