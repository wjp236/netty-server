package com.tumbleweed.netty.client.handler;

import com.mysql.jdbc.StringUtils;
import com.tumbleweed.netty.client.adapter.CommAdapter;
import com.tumbleweed.netty.client.domain.CommDomain;
import com.tumbleweed.netty.core.bean.BootstrapContextHelper;
import com.tumbleweed.netty.core.domain.DomainBase;
import com.tumbleweed.netty.core.util.ContextUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**  
* @Description: 处理业务相关
* @date：2014年9月16日 下午5:34:02
*/
public class CommClientHandler extends SimpleChannelInboundHandler<CommDomain> {

	private static final Logger logger = LoggerFactory.getLogger(CommClientHandler.class);
	
	private String suffix;

	public CommClientHandler(String suffix) throws Exception {
		if (StringUtils.isNullOrEmpty(suffix)) {
			throw new Exception(suffix + " suffix is null");
		}
		this.suffix = suffix;
	}

	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
		ContextUtil.getClientContext(suffix).setExceptionCaughtFlag(true);
		ContextUtil.getClientContext(suffix).setCtx(ctx);
		logger.info(suffix + " 连接成功 ");
	}

	public void channelRead0(final ChannelHandlerContext ctx,
			CommDomain commDomian) throws Exception {
		logger.warn("======" + suffix + " comm client 处理器 调用commAdapter");
		CommAdapter commAdapter = (CommAdapter) BootstrapContextHelper.getBean("commAdapter");
		CommDomain commDomain = commAdapter.invokeHandler(commDomian.getPackets());
		if(commDomain != null){
			String uuid = commDomain.getUuid();
			if (!StringUtils.isNullOrEmpty(uuid)) {
				DomainBase blockingDB = ContextUtil.getDomainCollectionContext(suffix)
						.getDomain(uuid);
				if (blockingDB != null) {
					blockingDB.setReturnDB(commDomain);
					logger.warn("======" + suffix + " comm 处理完毕， 并结束同步阻塞");
					blockingDB.endBlocking(uuid);
				}else{
					logger.warn("======" + suffix + " commm 处理失败， 未找到blockingDB");	
				}
			}else{
				logger.warn("======" + suffix + " comm 处理失败， 未指定uuid");
			}
		}else{
			
		}
	}

	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info(suffix + " 已经关闭 ");
	}

	public void channelUnregistered(final ChannelHandlerContext ctx)
			throws Exception {
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		logger.error(suffix + " exceptionCaught 发生错误:" + cause.getMessage());
	}
}
