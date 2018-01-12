package com.tumbleweed.netty.core.util;

import com.google.gson.Gson;
import com.mysql.jdbc.StringUtils;
import com.tumbleweed.netty.client.context.DomainCollectionContext;
import com.tumbleweed.netty.client.domain.HsmDomain;
import com.tumbleweed.netty.core.constants.IoConstants;
import com.tumbleweed.netty.core.domain.DomainBase;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Title: ChannelHandlerContextUtil.java
 * @Description: ChannelHandlerContext工具,发送同步或异步消息
 */
public class ChannelHandlerContextUtil {

	private static final Logger logger = LoggerFactory.getLogger(ChannelHandlerContextUtil.class);

	private static String mainChannel = null;

	private static String spareChannel = null;

	static String hsmPacket = ConstantsUtil.initHsmPacket();

	static String code = hsmPacket.substring(0, 2);

	static String data = hsmPacket.substring(2, hsmPacket.length());

	/**
	 * 发送消息
	 * 
	 * @param domainBase 消息对象
	 * @param suffix  配置的bean id
	 * @throws Exception
	 */
	public static void writeAndFlush(final DomainBase domainBase,
			final String suffix) throws Exception {
		ChannelHandlerContext ctx = ContextUtil.getClientContext(suffix).getCtx();
		if (ctx == null) {
			throw new RuntimeException(suffix + " writeAndFlush ctx is null!");
		}
		ChannelFuture channelFuture = ctx.writeAndFlush(domainBase);
		channelFuture.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future) {
				if (!future.isSuccess()) {
					logger.error("======" + suffix + "发送消息失败，消息内容："
							+ new Gson().toJson(domainBase));
				}
			}
		});
	}

	/**
	 * 向加密机发送消息
	 * 
	 * @param hsmDomain
	 *            消息对象
	 * @throws Exception
	 */
	public synchronized static void writeAndFlushBlockForOio(final HsmDomain hsmDomain) throws Exception {
		if (StringUtils.isNullOrEmpty(mainChannel)
				&& StringUtils.isNullOrEmpty(spareChannel)) {
			throw new Exception("加密机通道不可用");
		}
		ChannelHandlerContext ctx = ContextUtil.getClientContext(mainChannel).getCtx();
		hsmDomain.setHsmSpareFlag(false);
		if (ctx != null) {
			logger.warn("======" + mainChannel + " 消息通过编码之后将被发送，并执行同步阻塞(OIO)");
			final DomainCollectionContext dcc = ContextUtil.getDomainCollectionContext(mainChannel);
			dcc.setReConnCount(dcc.getReConnCount() + 1);

            ChannelFuture channelFuture = ctx.writeAndFlush(hsmDomain);
			channelFuture.addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) throws InterruptedException {
					if (!future.isSuccess()) {
						logger.error("======" + mainChannel + "发送消息失败，立即结束同步阻塞(OIO)");
						hsmDomain.setHsmSpareFlag(true);
						dcc.removeSyncQueueDomain();
					}
				}
			});
			dcc.putSyncQueueDomain(hsmDomain);
		} else {
			hsmDomain.setHsmSpareFlag(true);
			logger.error("======" + mainChannel + " ctx is null ");
		}

		// spare通道
		if (hsmDomain.isHsmSpareFlag()) {
			if (!StringUtils.isNullOrEmpty(spareChannel)) {
				hsmDomain.setHsmSpareFlag(false);
				ChannelHandlerContext spareCtx = ContextUtil
						.getClientContext(spareChannel).getCtx();
				if (spareCtx != null) {
					logger.warn("======spare通道 " + spareChannel
							+ " 消息通过编码之后将被发送，并执行同步阻塞(OIO)");
					final DomainCollectionContext spareDcc = ContextUtil
							.getDomainCollectionContext(spareChannel);
					spareDcc.setReConnCount(spareDcc.getReConnCount() + 1);
					ChannelFuture spareChannelFuture = spareCtx
							.writeAndFlush(hsmDomain);
					spareChannelFuture.addListener(new ChannelFutureListener() {
						public void operationComplete(ChannelFuture future)
								throws InterruptedException {
							if (!future.isSuccess()) {
								logger.error("======spare通道发送消息失败，立即结束同步阻塞(OIO)");
								spareDcc.removeSyncQueueDomain();
							} else {
								if (!StringUtils.isNullOrEmpty(spareChannel)) {
									String tempChannel = mainChannel;
									mainChannel = spareChannel;
									spareChannel = tempChannel;
								}
								logger.error("======main spare 切换成功");
							}
						}
					});
					spareDcc.putSyncQueueDomain(hsmDomain);
				} else {
					logger.error("======" + spareChannel
							+ " spare ctx is null ");
				}
			} else {
				logger.error("======主通道未成功发送，spare通道不存在，消息发送失败 ");
			}
		}
	}

	public synchronized static void writeAndFlushBlockForOio(final String suffix)
			throws Exception {
		ChannelHandlerContext ctx = ContextUtil.getClientContext(suffix)
				.getCtx();
		if (ctx != null) {
			HsmDomain heartbeatHsmDomain = new HsmDomain();
			heartbeatHsmDomain.setCode(code);
			heartbeatHsmDomain.setData(data);

			logger.warn("======" + suffix + " 心跳包消息通过编码之后将被发送，并执行同步阻塞(OIO)");
			final DomainCollectionContext dcc = ContextUtil
					.getDomainCollectionContext(suffix);
			dcc.setReConnCount(dcc.getReConnCount() + 1);
			ChannelFuture channelFuture = ctx.writeAndFlush(heartbeatHsmDomain);
			channelFuture.addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future)
						throws InterruptedException {
					if (!future.isSuccess()) {
						logger.error("======" + mainChannel
								+ "心跳包发送消息失败，立即结束同步阻塞(OIO");
						dcc.removeSyncQueueDomain();
					}
				}
			});
			dcc.putSyncQueueDomain(heartbeatHsmDomain);
		} else {
			logger.error("======" + suffix + " heartbeat ctx is null ");
		}
	}

	public static void writeAndFlushBlock(final DomainBase domainBase,
			final String suffix) throws Exception {
		writeAndFlushBlock(domainBase, suffix, ContextUtil
				.getClientContext(suffix).getCtx(), ContextUtil
				.getDomainCollectionContext(suffix).getDomainMap());
	}

	private static void writeAndFlushBlock(final DomainBase domainBase,
			final String suffix, ChannelHandlerContext ctx,
			ConcurrentHashMap<String, DomainBase> blockingDBMap)
			throws Exception {
		String uuid = domainBase.getUuid();
		if (StringUtils.isNullOrEmpty(uuid)) {
			throw new Exception(suffix + " 同步阻塞必须指定uuid");
		}
		if (ctx == null) {
			throw new RuntimeException(suffix + " ctx is null!");
		}
		domainBase.setStartTime(System.currentTimeMillis());
		domainBase.initBlocking();
		if (blockingDBMap != null) {
			blockingDBMap.put(uuid, domainBase);
		}
		logger.warn("======" + suffix + " 消息通过编码之后将被发送，并执行同步阻塞");
		ChannelFuture channelFuture = ctx.writeAndFlush(domainBase);
		channelFuture.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future) {
				if (!future.isSuccess()) {
					if (domainBase.getAsyncBlocking() != null) {
						logger.error("======" + suffix + "发送消息失败，立即结束同步阻塞");
						domainBase.endBlocking(IoConstants.IO_FUTURE_FAILED);
					}
				}
			}
		});
		try {
			domainBase.beginBlocking();
		} catch (InterruptedException e) {
			logger.error(suffix + " uuid=" + domainBase.getUuid()
					+ " domainBase 同步阻塞超时，结束同步阻塞");
			throw new RuntimeException(suffix
					+ " domainBase beginBlocking InterruptedException error:"
					+ e.getMessage());
		}
	}

	/**
	 * 
	 * @Title: covertSuffix
	 * @Description: DomainCollectionContext union标识
	 * @param: @param suffix
	 * @param: @return
	 * @return: String
	 * @throws
	 */
	public static String covertSuffix(String suffix) {
		return suffix.substring(0, suffix.length() - 1) + "Dhmc";
	}

	public static synchronized void setChannel(String hsmChannel) {
		if (!StringUtils.isNullOrEmpty(mainChannel)) {
			spareChannel = hsmChannel;
		} else {
			mainChannel = hsmChannel;
		}
	}

	public static synchronized void removeChannel(String hsmChannel) {
		if (!StringUtils.isNullOrEmpty(mainChannel)) {
			if (mainChannel.equals(hsmChannel)) {
				if (!StringUtils.isNullOrEmpty(spareChannel)) {
					mainChannel = spareChannel;
					spareChannel = null;
				} else {
					mainChannel = null;
				}
			} else {
				spareChannel = null;
			}
		} else {
			spareChannel = null;
		}
	}
}