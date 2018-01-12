package com.tumbleweed.netty.server.bean;

import com.tumbleweed.netty.client.bean.AppBeanBase;
import com.tumbleweed.netty.server.bootstap.PosServerBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PosAppBean extends AppBeanBase {
	private static final Logger logger = LoggerFactory.getLogger(PosAppBean.class);

	private PosServerBootstrap psb = null;

	public void startService() {
		try {
			logger.warn("======" + suffix + " pos网关服务开始启动......");
			psb = new PosServerBootstrap(port, suffix, handlers);
			psb.bootstrap();
			logger.warn("======" + suffix + " pos网关服务成功启动");
		} catch (Exception e) {
			logger.error("pos网关启动发生异常:" + e.getMessage());
			if (psb != null) {
				psb.closeServer();
				logger.warn("======pos网关服务启动时发生异常，服务关闭");
			}
		}
	}

	public void stopService() {
		if (psb != null) {
			psb.closeServer();
			logger.warn("======pos网关服务已关闭");
		}
	}
}
