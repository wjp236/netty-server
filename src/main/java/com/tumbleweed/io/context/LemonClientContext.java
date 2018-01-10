package com.tumbleweed.io.context;

import com.tumbleweed.io.bootstrap.ClientBootstrapBase;
import com.tumbleweed.io.constants.IoConstants;
import com.xinyipay.commons.utils.lang.PropertyUtil;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @Title: LemonClientContext.java
 * @Package: com.lemon.io.context
 * @Description: Lemon Client容器,用于重连等
 * @author: jiangke
 * @date: 2014年1月2日
 * @version V1.0
 */
public class LemonClientContext{

	private static final Logger logger = LoggerFactory.getLogger(LemonClientContext.class);
	
	private String suffix;

	private static int ri = IoConstants.RECONNECT_INTERVAL_DEFAULT;
	
	private static int reCount = Integer.MAX_VALUE;

	private ChannelHandlerContext ctx;

	private ClientBootstrapBase cbb;

	private boolean exceptionCaughtFlag = true;
	
	boolean reConnectFlag = true;
	
	public LemonClientContext(String suffix){
		this(suffix, true);
	}
	
	public LemonClientContext(String suffix, boolean reConnectFlag){
		this.suffix = suffix;
		this.reConnectFlag = reConnectFlag;
		if(reConnectFlag){
			try {
                String riTemp = PropertyUtil.getInstance(IoConstants.CONFIG_NAME)
                        .getProperty(IoConstants.RECONNECT_INTERVAL);
				ri = Integer.parseInt(riTemp);
			} catch (Exception e) {
				logger.warn(suffix + " " + IoConstants.RECONNECT_INTERVAL
						+ " 配置发生错误:" + e.getMessage());
			}
			try {
                String reCountTemp = PropertyUtil.getInstance(IoConstants.CONFIG_NAME)
                        .getProperty(IoConstants.RECONNECT_COUNT);
                reCount = Integer.parseInt(reCountTemp);
			} catch (Exception e) {
				logger.warn(suffix + " " + IoConstants.RECONNECT_COUNT
						+ " 配置发生错误:" + e.getMessage());
			}
		}
	}
	
	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	public ClientBootstrapBase getCbb() {
		return cbb;
	}

	public void setCbb(ClientBootstrapBase cbb) {
		this.cbb = cbb;
	}

	public boolean isExceptionCaughtFlag() {
		return exceptionCaughtFlag;
	}

	public void setExceptionCaughtFlag(boolean exceptionCaughtFlag) {
		this.exceptionCaughtFlag = exceptionCaughtFlag;
	}

	public void checkConnection() {
		if(!this.reConnectFlag){
			return;
		}
		int count = 0;
		exceptionCaughtFlag = false;
		boolean successFlag = false;
		String errorMessage = null;
		while (true) {
			if(count >= reCount){
				break;
			}
			try {
				Thread.sleep(ri * 1000);
				successFlag = cbb.excuteReConnect();
			} catch (Exception e) {
				errorMessage = e.getMessage();
			}
			if (successFlag) {
				exceptionCaughtFlag = true;
				logger.error(suffix + " LemonClientContext 重连成功!");
				break;
			} else {
				count++;
				logger.error(suffix + " LemonClientContext 重连失败: 次数:" + count + ", "
						+ errorMessage);
			}
		}
	}
}