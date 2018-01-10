package com.tumbleweed.io.bootstrap;

import com.tumbleweed.io.constants.IoConstants;
import com.tumbleweed.io.util.ContextUtil;
import com.mysql.jdbc.StringUtils;
import com.xinyipay.commons.utils.lang.PropertyUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ClientBootstrapBase extends BootstrapBase {
	static final Logger logger = LoggerFactory
			.getLogger(ClientBootstrapBase.class);

	protected int reconnectCount = 0;

	protected boolean reconnectMaxCountFlag = true;

	protected int reconnectMaxCount = 10;

	protected String host;

	protected boolean shortCon;

	protected String packets;

	private boolean available;

	protected boolean isShowtDown = false;

	protected EventLoopGroup bossClientGroup;

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public boolean isShortCon() {
		return shortCon;
	}

	public boolean isShowtDown() {
		return this.isShowtDown;
	}

	protected void closeEventLoopGroup() {
		if (bossClientGroup != null && !bossClientGroup.isShutdown()) {
			bossClientGroup.shutdownGracefully();
		}
	}

	protected boolean excuteClientConnect(Bootstrap bootstrap,
			EventLoopGroup bossGroup) throws Exception {
		if (initializer == null) {
			throw new RuntimeException(suffix
					+ " excuteConnect initializer 初始化器为空");
		}
		isShowtDown = false;
		int rtc = IoConstants.REMOTE_TIMEOUT_CONNECT_DEFAULT;
		try {
			String rtcTemp = PropertyUtil.getInstance(IoConstants.CONFIG_NAME)
                    .getProperty(IoConstants.REMOTE_TIMEOUT_CONNECT);
            rtc = Integer.parseInt(rtcTemp);
		} catch (Exception e) {
			logger.warn(suffix + " " + IoConstants.REMOTE_TIMEOUT_CONNECT
					+ " 未配置: " + e.getMessage() + ", 将采用默认值:" + rtc);
		}

		try {
			rtc = rtc * 1000;
			bootstrap.group(bossGroup).channel(NioSocketChannel.class)
					.handler(initializer)
					.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, rtc);
			bootstrap.connect(host, port).sync();
			return true;
		} catch (Exception e) {
			throw new RuntimeException(suffix
					+ " excuteClientConnect 发生错误：" + e.getMessage());
		}
	}

	protected void initConnProperty(String host, int port, String suffix)
			throws Exception {
		this.initConnProperty(host, port, suffix, true, false, false);
	}

	protected void initConnProperty(String host, int port, String suffix,
			boolean hasHeartBeat, boolean reconnectMaxCountFlag)
			throws Exception {
		this.initConnProperty(host, port, suffix, false, hasHeartBeat,
				reconnectMaxCountFlag);
	}

	/** 
	 * @Title: initConnProperty 
	 * @Description: 
	 * @param host
	 * @param port
	 * @param suffix
	 * @param shortCon
	 * @param hasHeartBeat
	 * @param reconnectMaxCountFlag
	 * @throws Exception
	*/
	protected void initConnProperty(String host, int port, String suffix,
			boolean shortCon, boolean hasHeartBeat,
			boolean reconnectMaxCountFlag) throws Exception {
		this.host = host;
		this.port = port;
		super.suffix = suffix;
		this.bossClientGroup = new NioEventLoopGroup();
		if (!shortCon) {
			if (hasHeartBeat) {
                packets = PropertyUtil.getInstance(IoConstants.CONFIG_NAME)
                        .getProperty(IoConstants.HEART_BEAT_PACKET + suffix);
				if (StringUtils.isNullOrEmpty(packets)) {
					throw new Exception(
							suffix + " 包含心跳的长连接必须指定心跳包内容");
				}
				if (packets.length() >= 8) {
					throw new Exception(
							suffix
									+ " 包含心跳的长连接必须指定心跳包内容长度不能大于8");
				}
			}
			if (reconnectMaxCountFlag) {
				try {
					String reconnectMaxCountTemp = PropertyUtil.getInstance(IoConstants.CONFIG_NAME)
                            .getProperty(IoConstants.RECONNECT_COUNT);
                    reconnectMaxCount = Integer.parseInt(reconnectMaxCountTemp);
				} catch (Exception e) {
					logger.warn(suffix + " " + IoConstants.RECONNECT_COUNT
							+ " 配置错误:" + e.getMessage());
				}
			}
		}
	}

	public boolean excuteReConnect() {
		closeEventLoopGroup();
		logger.warn("======" + suffix + " excuteReConnect 开始......");
		boolean successFlag = false;
		this.bossClientGroup = new NioEventLoopGroup();
		try {
			successFlag = excuteClientConnect(new Bootstrap(),
					this.bossClientGroup);
		} catch (Exception e) {
			logger.error(suffix + " excuteReConnect 发生错误， 并执行closeEventLoopGroup:" + e.getMessage());
			closeEventLoopGroup();
		}
		logger.warn("======" + suffix + " excuteReConnect 结束");
		return successFlag;
	}

	public void releaseConnect() {
		if (isShowtDown) {
			logger.warn("======isShowtDown = true，退出");
			return;
		}
		isShowtDown = true;
		logger.warn("======" + suffix + " 开始执行releaseConnect......");
		try {
			ChannelHandlerContext ctx = ContextUtil
					.getLemonClientContext(suffix).getCtx();
			if (ctx != null) {
				ctx.close();
			}
			ContextUtil.getLemonClientContext(suffix).setCtx(null);
			logger.warn("======" + suffix + " 已经成功关闭socket通道，并释放了ContextUtil ctx");
		} catch (Exception e) {
			logger.error(suffix + " releaseConnect 发生错误:" + e.getMessage());
		} finally {
			try {
				closeEventLoopGroup();
			} catch (Exception ee) {
				logger.error(suffix
						+ " releaseConnect shutdownGracefully 发生错误:"
						+ ee.getMessage());
			}
			logger.warn("======" + suffix + " releaseConnect已经结束");
		}
	}
}
