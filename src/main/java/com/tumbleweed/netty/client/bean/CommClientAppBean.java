package com.tumbleweed.netty.client.bean;

import com.tumbleweed.netty.client.bootstrap.CommClientBootstrap;
import com.tumbleweed.netty.client.context.DomainCollectionContext;
import com.tumbleweed.netty.client.context.ClientContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommClientAppBean extends AppBeanBase {

	private static final Logger logger = LoggerFactory.getLogger(CommClientAppBean.class);

	private CommClientBootstrap scb;

	public void startService() {
		super.lcc = new ClientContext(suffix);
		super.dhmc = new DomainCollectionContext(suffix);
		try {
			logger.warn("======" + suffix + " 开始连接远程server(" + ip +":" + port + ")......");
			scb = new CommClientBootstrap(ip, port, suffix);
			scb.connect();
			logger.warn("======" + suffix + " 连接远程server成功");
		} catch (Exception e) {
			logger.error("JtClientAppBean error:" + e.getMessage());
			if (scb != null) {
				scb.releaseConnect();
				logger.warn("======" + suffix + " 连接远程server(" + ip +":" + port + ")发生异常，服务关闭");
			}
		}
	}

	public void stopService() {
		if (scb != null) {
			scb.releaseConnect();
			logger.warn("======" + suffix + " 服务已关闭");
		}
	}
}
