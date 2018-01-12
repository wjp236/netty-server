package com.tumbleweed.netty.client.context;

import com.tumbleweed.netty.client.bootstrap.ClientBootstrapBase;
import com.tumbleweed.netty.core.constants.IoConstants;
import com.tumbleweed.netty.core.context.EnvVar;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Title: LemonClientContext.java
 * @Description: Client容器,用于重连等
 */
public class ClientContext {

	private static final Logger logger = LoggerFactory.getLogger(ClientContext.class);
	
	private String suffix;

	private static int ri = IoConstants.RECONNECT_INTERVAL_DEFAULT;
	
	private static int reCount = Integer.MAX_VALUE;

	private ChannelHandlerContext ctx;

	private ClientBootstrapBase cbb;

	private boolean exceptionCaughtFlag = true;
	
	boolean reConnectFlag = true;
	
	public ClientContext(String suffix){
		this(suffix, true);
	}
	
	public ClientContext(String suffix, boolean reConnectFlag){
		this.suffix = suffix;
		this.reConnectFlag = reConnectFlag;
		if(reConnectFlag){
			try {
				ri = EnvVar.curEnv().getIntegerVar(IoConstants.RECONNECT_INTERVAL);
			} catch (Exception e) {
				logger.warn(suffix + " " + IoConstants.RECONNECT_INTERVAL + " 配置发生错误:" + e.getMessage());
			}
			try {
				reCount = EnvVar.curEnv().getIntegerVar(IoConstants.RECONNECT_COUNT);
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