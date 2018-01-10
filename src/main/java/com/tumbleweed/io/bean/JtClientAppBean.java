package com.tumbleweed.io.bean;

import com.tumbleweed.io.bootstrap.JtClientBootstrap;
import com.tumbleweed.io.context.DomainCollectionContext;
import com.tumbleweed.io.context.LemonClientContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JtClientAppBean extends AppBeanBase {

	private static final Logger logger = LoggerFactory.getLogger(JtClientAppBean.class);

	private JtClientBootstrap jcb;

	public void startService() throws Exception {
		super.lcc = new LemonClientContext(suffix);
		super.dhmc = new DomainCollectionContext(suffix);
		try {
			logger.warn("======" + suffix + " 开始连接远程server(" + ip +":" + port + ")......");
			jcb = new JtClientBootstrap(ip, port, suffix);
			jcb.connect();
			logger.warn("======" + suffix + " 连接远程server成功");
		} catch (Exception e) {
			logger.error("JtClientAppBean error:" + e.getMessage());
			if (jcb != null) {
				jcb.releaseConnect();
				logger.warn("======" + suffix + " 连接远程server(" + ip +":" + port + ")发生异常，服务关闭");
			}
		}
	}

	public void stopService() {
		if (jcb != null) {
			jcb.releaseConnect();
			logger.warn("======" + suffix + " 服务已关闭");
		}
	}
}
