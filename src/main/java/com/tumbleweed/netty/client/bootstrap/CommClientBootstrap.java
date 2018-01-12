package com.tumbleweed.netty.client.bootstrap;

import com.tumbleweed.netty.client.initializer.CommClientInitializer;
import io.netty.bootstrap.Bootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommClientBootstrap extends ClientBootstrapBase {

	private static final Logger logger = LoggerFactory.getLogger(CommClientBootstrap.class);

	public CommClientBootstrap(String host, int port, String suffix) throws Exception {
		super.initConnProperty(host, port, suffix);
	}

	public void connect() throws Exception {
		try {
			initializer = new CommClientInitializer(suffix);
			excuteClientConnect(new Bootstrap(), super.bossClientGroup);
		} catch (Exception e) {
			logger.error(suffix + " commonConnect error:" + e.getMessage());
			closeEventLoopGroup();
			throw new Exception(suffix + " client bootstrap failed!");
		}
	}
}
