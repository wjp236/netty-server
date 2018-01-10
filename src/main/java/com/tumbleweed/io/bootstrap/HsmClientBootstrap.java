package com.tumbleweed.io.bootstrap;

import com.tumbleweed.io.initializer.HsmClientInitializer;
import com.tumbleweed.io.util.ContextUtil;
import io.netty.bootstrap.Bootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HsmClientBootstrap extends ClientBootstrapBase {
	static final Logger logger = LoggerFactory
			.getLogger(HsmClientBootstrap.class);

	public HsmClientBootstrap(String host, int port, String suffix) throws Exception {
		super.initConnProperty(host, port, suffix, false, false);
	}

	public void connect() throws Exception {
		try {
			ContextUtil.getLemonClientContext(suffix).setCbb(this);
			initializer = new HsmClientInitializer(suffix);
			excuteClientConnect(new Bootstrap(), super.bossClientGroup);
		} catch (Exception e) {
			logger.error("连接加密机发生错误，错误原因:" + e.getMessage());
			closeEventLoopGroup();
			throw new Exception("连接加密机发生错误");
		}
	}
}
