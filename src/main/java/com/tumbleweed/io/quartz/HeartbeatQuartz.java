package com.tumbleweed.io.quartz;

import com.tumbleweed.io.context.DomainCollectionContext;
import com.tumbleweed.io.util.ChannelHandlerContextUtil;
import com.tumbleweed.io.util.ConstantsUtil;
import com.tumbleweed.io.util.ContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HeartbeatQuartz {
	static final Logger logger = LoggerFactory.getLogger(HeartbeatQuartz.class);

	private List<String> hsmAppBeans = new ArrayList<String>();
			
	public void execute() {
		logger.info("excute HeartbeatQuartz, time is " + new Date());
		if (hsmAppBeans != null && hsmAppBeans.size() > 0) {
			int configReadTimeout = ConstantsUtil.initHsmReadTimeout() * 1000;
			int configConnectTimeout = ConstantsUtil.initHsmConnectTimeout();
			long aliveTime = 0;
			int reConnCount = 0;
			long step = 0;
			long currentTime = System.currentTimeMillis();
			for (String hsmAppBean : hsmAppBeans) {
				DomainCollectionContext dcc = ContextUtil
						.getDomainCollectionContext(hsmAppBean);
				aliveTime = dcc.getAliveTime();
				reConnCount = dcc.getReConnCount() + 1;
				logger.debug(hsmAppBean + " getReConnCount = " + reConnCount);
				step = currentTime - aliveTime;
				if (reConnCount > configConnectTimeout) {
					logger.debug(hsmAppBean + " 长时间未收到返回报文，正在检测连接，准备重连");
					dcc.setReConnCount(0);
					ContextUtil.getLemonClientContext(hsmAppBean).getCtx().close();
				} else if (step > configReadTimeout) {
					try {
						logger.debug(hsmAppBean + " 发送心跳包......");
						ChannelHandlerContextUtil.writeAndFlushBlockForOio(hsmAppBean);
					} catch (Exception e) {
						logger.error("发送加密机心跳失败:" + e.getMessage());
					}
				}
			}
		} else {
			logger.error("hsm no heartbeat, 999! ");
		}
	}
	
	public void setHsmAppBeans(String hsmAppBean) {
		if(!hsmAppBeans.contains(hsmAppBean)){
			hsmAppBeans.add(hsmAppBean);
		}
	}
}
