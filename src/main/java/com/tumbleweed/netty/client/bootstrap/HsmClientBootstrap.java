package com.tumbleweed.netty.client.bootstrap;

import com.tumbleweed.netty.client.initializer.HsmClientInitializer;
import com.tumbleweed.netty.core.util.ContextUtil;
import io.netty.bootstrap.Bootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HsmClientBootstrap extends ClientBootstrapBase {

	static final Logger logger = LoggerFactory.getLogger(HsmClientBootstrap.class);

	public HsmClientBootstrap(String host, int port, String suffix) {
		super.initConnProperty(host, port, suffix, false, false);
	}

	public void connect() throws Exception {
		try {
			ContextUtil.getClientContext(suffix).setCbb(this);
			initializer = new HsmClientInitializer(suffix);
			excuteClientConnect(new Bootstrap(), super.bossClientGroup);
		} catch (Exception e) {
			logger.error("连接加密机发生错误，错误原因:" + e.getMessage());
			closeEventLoopGroup();
			throw new RuntimeException("连接加密机发生错误");
		}
	}
}
