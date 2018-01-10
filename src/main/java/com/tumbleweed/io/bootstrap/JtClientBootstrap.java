package com.tumbleweed.io.bootstrap;

import com.tumbleweed.io.initializer.OutJtClientInitializer;
import com.tumbleweed.io.util.ContextUtil;
import io.netty.bootstrap.Bootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JtClientBootstrap extends ClientBootstrapBase {
	private static final Logger logger = LoggerFactory
			.getLogger(JtClientBootstrap.class);

	public JtClientBootstrap(String host, int port, String suffix) throws Exception {
		super.initConnProperty(host, port, suffix, false, false);
	}

	public void connect() throws Exception {
		try {
			ContextUtil.getLemonClientContext(suffix).setCbb(this);
			initializer = new OutJtClientInitializer(suffix);
			excuteClientConnect(new Bootstrap(), super.bossClientGroup);
		} catch (Exception e) {
			logger.error(suffix + " commonConnect error:" + e.getMessage());
			closeEventLoopGroup();
			throw new Exception(suffix
					+ " Lemon jt client bootstrap failed!");
		}
	}
}
