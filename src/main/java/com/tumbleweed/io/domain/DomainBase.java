package com.tumbleweed.io.domain;

import com.tumbleweed.io.constants.IoConstants;
import com.xinyipay.commons.utils.lang.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @Title: DomainBase.java
 * @Package: com.lemon.io.domain
 * @Description: io中所有的domain都继承于它
 * @author: jiangke
 * @date: 2014年1月2日
 * @version V1.0
 */
public abstract class DomainBase {
	private static final Logger logger = LoggerFactory
			.getLogger(DomainBase.class);

	protected String uuid;

//	protected DomainBase returnDB;
	private transient final BlockingQueue<DomainBase> returnDB = new LinkedBlockingQueue<DomainBase>();

	private BlockingQueue<String> asyncBlocking = null;
	
	private boolean succFlag;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public BlockingQueue<String> getAsyncBlocking() {
		return asyncBlocking;
	}

	public void endBlocking(String msg) {
		logger.warn("======结束同步阻塞，uuid=" + uuid + " 时间消耗： "
				+ (System.currentTimeMillis() - startTime) + "ms");
		if (IoConstants.IO_FUTURE_FAILED.equals(msg)) {
			succFlag = false;
		}
		asyncBlocking.offer(msg);
	}

	public void initBlocking() {
		if (this.asyncBlocking == null) {
			this.asyncBlocking = new LinkedBlockingQueue<String>(1);
		}
	}

	public void beginBlocking() throws InterruptedException {
		int timeout = IoConstants.BLOCKINGQUEUE_POLL_TIMEOUT_DEFAULT;
		try {
            String timeoutTemp = PropertyUtil.getInstance(IoConstants.CONFIG_NAME)
                    .getProperty(IoConstants.BLOCKINGQUEUE_POLL_TIMEOUT);
            timeout = Integer.parseInt(timeoutTemp);
		} catch (Exception e) {
			logger.warn(IoConstants.BLOCKINGQUEUE_POLL_TIMEOUT
					+ " 未做配置，将使用默认值："
					+ IoConstants.BLOCKINGQUEUE_POLL_TIMEOUT_DEFAULT);
		}
		this.asyncBlocking.poll(timeout, TimeUnit.SECONDS);
        logger.info("test");
	}

	public DomainBase getReturnDB() throws Exception {
		int timeout = IoConstants.BLOCKINGQUEUE_POLL_TIMEOUT_DEFAULT;
		try {
            String timeoutTemp = PropertyUtil.getInstance(IoConstants.CONFIG_NAME)
                    .getProperty(IoConstants.BLOCKINGQUEUE_POLL_TIMEOUT);
            timeout = Integer.parseInt(timeoutTemp);
		} catch (Exception e) {
			logger.warn(IoConstants.BLOCKINGQUEUE_POLL_TIMEOUT
					+ " 未做配置，将使用默认值："
					+ IoConstants.BLOCKINGQUEUE_POLL_TIMEOUT_DEFAULT);
		}
		try{
			return returnDB.poll(timeout, TimeUnit.SECONDS);
		}catch(Exception e)
		{
			throw new Exception("returnDB 返回值为空，原因：同步阻塞超时或未找到对应的blockingDB");
		}
	}

	public void setReturnDB(DomainBase returnDB) {
			this.returnDB.clear();
			if(null != returnDB)
				this.returnDB.offer(returnDB);
	}

	private Long startTime;

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public boolean isSuccFlag() {
		return succFlag;
	}

	public void setSuccFlag(boolean succFlag) {
		this.succFlag = succFlag;
	}
	
}
