package com.tumbleweed.io.util;

import com.google.gson.Gson;
import com.tumbleweed.io.constants.IoConstants;
import com.tumbleweed.io.context.DomainCollectionContext;
import com.tumbleweed.io.domain.DomainBase;
import com.tumbleweed.io.domain.Hsm;
import com.tumbleweed.io.domain.Out8583;
import com.mysql.jdbc.StringUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @Title: ChannelHandlerContextUtil.java
 * @Package: com.lemon.io.util
 * @Description: ChannelHandlerContext工具,发送同步或异步消息
 * @author: jiangke
 * @date: 2014年1月2日
 * @version V1.0
 */
public class ChannelHandlerContextUtil {
	static final Logger logger = LoggerFactory.getLogger(ChannelHandlerContextUtil.class);

	private static String mainChannel = null;

	private static String spareChannel = null;

	static String hsmPacket = ConstantsUtil.initHsmPacket();

	static String code = hsmPacket.substring(0, 2);

	static String data = hsmPacket.substring(2, hsmPacket.length());

	/**
	 * 发送消息
	 * 
	 * @param domainBase
	 *            消息对象
	 * @param suffix
	 *            配置的bean id
	 * @throws Exception
	 */
	public static void writeAndFlush(final DomainBase domainBase,
			final String suffix) throws Exception {
		ChannelHandlerContext ctx = ContextUtil.getLemonClientContext(suffix)
				.getCtx();
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
	 * @param hsm
	 *            消息对象
	 * @throws Exception
	 */
	public synchronized static void writeAndFlushBlockForOio(final Hsm hsm) throws Exception {
		if (StringUtils.isNullOrEmpty(mainChannel)
				&& StringUtils.isNullOrEmpty(spareChannel)) {
			throw new Exception("加密机通道不可用");
		}
		ChannelHandlerContext ctx = ContextUtil.getLemonClientContext(mainChannel).getCtx();
		hsm.setHsmSpareFlag(false);
		if (ctx != null) {
			logger.warn("======" + mainChannel + " 消息通过编码之后将被发送，并执行同步阻塞(OIO)");
			final DomainCollectionContext dcc = ContextUtil.getDomainCollectionContext(mainChannel);
			dcc.setReConnCount(dcc.getReConnCount() + 1);

            ChannelFuture channelFuture = ctx.writeAndFlush(hsm);
			channelFuture.addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) throws InterruptedException {
					if (!future.isSuccess()) {
						logger.error("======" + mainChannel + "发送消息失败，立即结束同步阻塞(OIO)");
						hsm.setHsmSpareFlag(true);
						dcc.removeSyncQueueDomain();
					}
				}
			});
			dcc.putSyncQueueDomain(hsm);
		} else {
			hsm.setHsmSpareFlag(true);
			logger.error("======" + mainChannel + " ctx is null ");
		}

		// spare通道
		if (hsm.isHsmSpareFlag()) {
			if (!StringUtils.isNullOrEmpty(spareChannel)) {
				hsm.setHsmSpareFlag(false);
				ChannelHandlerContext spareCtx = ContextUtil
						.getLemonClientContext(spareChannel).getCtx();
				if (spareCtx != null) {
					logger.warn("======spare通道 " + spareChannel
							+ " 消息通过编码之后将被发送，并执行同步阻塞(OIO)");
					final DomainCollectionContext spareDcc = ContextUtil
							.getDomainCollectionContext(spareChannel);
					spareDcc.setReConnCount(spareDcc.getReConnCount() + 1);
					ChannelFuture spareChannelFuture = spareCtx
							.writeAndFlush(hsm);
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
					spareDcc.putSyncQueueDomain(hsm);
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
		ChannelHandlerContext ctx = ContextUtil.getLemonClientContext(suffix)
				.getCtx();
		if (ctx != null) {
			Hsm heartbeatHsm = new Hsm();
			heartbeatHsm.setCode(code);
			heartbeatHsm.setData(data);

			logger.warn("======" + suffix + " 心跳包消息通过编码之后将被发送，并执行同步阻塞(OIO)");
			final DomainCollectionContext dcc = ContextUtil
					.getDomainCollectionContext(suffix);
			dcc.setReConnCount(dcc.getReConnCount() + 1);
			ChannelFuture channelFuture = ctx.writeAndFlush(heartbeatHsm);
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
			dcc.putSyncQueueDomain(heartbeatHsm);
		} else {
			logger.error("======" + suffix + " heartbeat ctx is null ");
		}
	}

	/**
	 * 银联
	 * type block
	 * @param out8583
	 * @throws Exception
	 */
	public static void writeAndFlushBlockForUnion(
			final Out8583 out8583) throws Exception {
		if (StringUtils.isNullOrEmpty(mainChannel)
				&& StringUtils.isNullOrEmpty(spareChannel)) {
			logger.error("union通道不可用");
			throw new RuntimeException("union通道不可用");
		}
		String uuid = out8583.getUuid();
		if (StringUtils.isNullOrEmpty(uuid)) {
			logger.error("union通道不可用");
			throw new Exception("union 同步阻塞必须指定uuid");
		}
		out8583.setHsmSpareFlag(false);
		ChannelHandlerContext ctx = ContextUtil.getLemonClientContext(
				mainChannel).getCtx();
		if (ctx != null) {
			ConcurrentHashMap<String, DomainBase> blockingDBMap = ContextUtil
					.getDomainCollectionContextForUnion(mainChannel)
					.getDomainMap();
			if (blockingDBMap != null) {
				logger.warn("======" + mainChannel
						+ " 消息通过编码之后将被发送，并执行同步阻塞(union)");
				blockingDBMap.put(uuid, out8583);
				out8583.setStartTime(System.currentTimeMillis());
				out8583.initBlocking();
				ChannelFuture channelFuture = ctx.writeAndFlush(out8583);
				channelFuture.addListener(new ChannelFutureListener() {
					public void operationComplete(ChannelFuture future) {
						if (!future.isSuccess()) {
							if (out8583.getAsyncBlocking() != null) {
								logger.error("======" + mainChannel
										+ "发送消息失败，立即结束同步阻塞");
								out8583.setSuccFlag(false);
								out8583.endBlocking("channelFuture is failed");
							}
						}
					}
				});
				try {
					out8583.beginBlocking();
				} catch (InterruptedException e) {
					logger.error(mainChannel + " uuid=" + out8583.getUuid()
							+ " domainBase 同步阻塞超时，结束同步阻塞");
					throw new RuntimeException(
							mainChannel
									+ " domainBase beginBlocking InterruptedException error:"
									+ e.getMessage());
				}
			} else {
				out8583.setHsmSpareFlag(true);
				logger.error(mainChannel + " blockingDBMap is null ");
			}
		} else {
			out8583.setHsmSpareFlag(true);
			logger.error("======" + mainChannel + " ctx is null ");
		}

		// spare通道
		if (out8583.isHsmSpareFlag()) {
			if (!StringUtils.isNullOrEmpty(spareChannel)) {
				out8583.setHsmSpareFlag(false);
				ChannelHandlerContext spareCtx = ContextUtil
						.getLemonClientContext(spareChannel).getCtx();
				if (spareCtx != null) {
					ConcurrentHashMap<String, DomainBase> spareBlockingDBMap = ContextUtil
							.getDomainCollectionContextForUnion(spareChannel)
							.getDomainMap();
					if (spareBlockingDBMap != null) {
						logger.warn("======spare通道 " + spareChannel
								+ " 消息通过编码之后将被发送，并执行同步阻塞(union)");
						out8583.setSuccFlag(true);
						spareBlockingDBMap.put(uuid, out8583);
						out8583.setStartTime(System.currentTimeMillis());
						out8583.initBlocking();
						ChannelFuture channelFuture = ctx
								.writeAndFlush(out8583);
						channelFuture.addListener(new ChannelFutureListener() {
							public void operationComplete(ChannelFuture future) {
								if (!future.isSuccess()) {
									if (out8583.getAsyncBlocking() != null) {
										logger.error("======" + spareChannel
												+ " spare 发送消息失败，立即结束同步阻塞");
										out8583.setSuccFlag(false);
										out8583.endBlocking(IoConstants.IO_FUTURE_FAILED);
									}
								}
							}
						});
						try {
							out8583.beginBlocking();
						} catch (InterruptedException e) {
							logger.error(spareChannel + " uuid="
									+ out8583.getUuid()
									+ " spare domainBase 同步阻塞超时，结束同步阻塞");
							throw new RuntimeException(
									mainChannel
											+ " spare domainBase beginBlocking InterruptedException error:"
											+ e.getMessage());
						}
					} else {
						logger.error(spareChannel
								+ " spare blockingDBMap is null ");
					}
				} else {
					logger.error("======" + spareChannel
							+ " spare ctx is null ");
				}
			} else {
				logger.error("======主通道未成功发送，spare通道不存在，消息发送失败 ");
			}
		}
	}
	
	/**
	 * 银联
	 * @param out8583
	 * @throws Exception
	 */
	public static void writeAndFlushForUnion(
			final Out8583 out8583) throws Exception {
		if (StringUtils.isNullOrEmpty(mainChannel)
				&& StringUtils.isNullOrEmpty(spareChannel)) {
			logger.error("union通道不可用");
			throw new RuntimeException("union通道不可用");
		}
		out8583.setHsmSpareFlag(false);
		ChannelHandlerContext ctx = ContextUtil.getLemonClientContext(
				mainChannel).getCtx();
		if (ctx != null) {
			logger.warn("======通道 " + mainChannel
					+ " 消息通过编码之后将被发送(union)");
				ChannelFuture channelFuture = ctx.writeAndFlush(out8583);
				channelFuture.addListener(new ChannelFutureListener() {
					public void operationComplete(ChannelFuture future) {
						if (!future.isSuccess()) {
								logger.error("======" + mainChannel
										+ "发送消息失败，writeAndFlushForUnion");
						}
					}
				});
		} else {
			out8583.setHsmSpareFlag(true);
			logger.error("======" + mainChannel + " ctx is null ");
		}

		// spare通道
		if (out8583.isHsmSpareFlag()) {
			if (!StringUtils.isNullOrEmpty(spareChannel)) {
				out8583.setHsmSpareFlag(false);
				ChannelHandlerContext spareCtx = ContextUtil
						.getLemonClientContext(spareChannel).getCtx();
				if (spareCtx != null) {
						logger.warn("======spare通道 " + spareChannel
								+ " 消息通过编码之后将被发送(union)");
						ChannelFuture channelFuture = ctx
								.writeAndFlush(out8583);
						channelFuture.addListener(new ChannelFutureListener() {
							public void operationComplete(ChannelFuture future) {
								if (!future.isSuccess()) {
										logger.error("======" + spareChannel
												+ " spare 发送消息失败，writeAndFlushForUnion");
								}
							}
						});
				} else {
					logger.error("======" + spareChannel
							+ " spare ctx is null ");
				}
			} else {
				logger.error("======主通道未成功发送，spare通道不存在，消息发送失败  writeAndFlushForUnion");
			}
		}
	}

	public static void writeAndFlushBlock(final DomainBase domainBase,
			final String suffix) throws Exception {
		writeAndFlushBlock(domainBase, suffix, ContextUtil
				.getLemonClientContext(suffix).getCtx(), ContextUtil
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