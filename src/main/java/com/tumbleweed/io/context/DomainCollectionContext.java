package com.tumbleweed.io.context;

import com.tumbleweed.io.constants.IoConstants;
import com.tumbleweed.io.domain.DomainBase;
import com.tumbleweed.io.domain.Hsm;
import com.mysql.jdbc.StringUtils;
import com.xinyipay.commons.utils.lang.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @Title: DomainCollectionContext.java
 * @Package: com.lemon.io.context
 * @Description: lemon 上下文 容器
 * @author: jiangke
 * @date: 2014年1月2日
 * @version V1.0
 */
public class DomainCollectionContext {
	private static final Logger logger = LoggerFactory
			.getLogger(DomainCollectionContext.class);

	private ConcurrentHashMap<String, DomainBase> domainMap = new ConcurrentHashMap<String, DomainBase>();

	private SynchronousQueue<DomainBase> domainSyncQueue = new SynchronousQueue<DomainBase>();
	
	private Long aliveTime = System.currentTimeMillis();
	
	private Integer reConnCount = 0;

	private String suffix;

	private static int timeout = IoConstants.BLOCKINGQUEUE_POLL_TIMEOUT_DEFAULT;

	static {
		try {
            String timeoutTemp = PropertyUtil.getInstance(IoConstants.CONFIG_NAME)
                    .getProperty(IoConstants.BLOCKINGQUEUE_POLL_TIMEOUT);
            timeout = Integer.parseInt(timeoutTemp);
		} catch (Exception e) {
			logger.warn(IoConstants.BLOCKINGQUEUE_POLL_TIMEOUT
					+ " 未做配置，将使用默认值："
					+ IoConstants.BLOCKINGQUEUE_POLL_TIMEOUT_DEFAULT);
		}
	}
	
	public DomainCollectionContext(String suffix) {
		this.suffix = suffix;
	}

	public ConcurrentHashMap<String, DomainBase> getDomainMap() {
		return domainMap;
	}

	public DomainBase getDomain(String key) {
		if (StringUtils.isNullOrEmpty(key))
			throw new IllegalArgumentException(
					"DomainCollectionContext key is null!");
		return domainMap.get(key);
	}

	public DomainBase putDomain(String key, Hsm hsm) {
		if (StringUtils.isNullOrEmpty(key) || hsm == null)
			throw new IllegalArgumentException(
					"DomainCollectionContext key is null or hsm is null !");
		return domainMap.put(key, hsm);
	}

	public DomainBase removeDomain(String key) {
		if (StringUtils.isNullOrEmpty(key))
			throw new IllegalArgumentException(
					"DomainCollectionContext key is null or hsm is null !");
		return domainMap.remove(key);
	}

	public boolean putSyncQueueDomain(DomainBase domainBase)
			throws InterruptedException {
		return domainSyncQueue.offer(domainBase, timeout, TimeUnit.SECONDS);
	}

	public DomainBase removeSyncQueueDomain() throws InterruptedException {
		this.aliveTime = System.currentTimeMillis();
		this.reConnCount --;
		return domainSyncQueue.take();
	}

	public String getSuffix() {
		return suffix;
	}

	public Long getAliveTime() {
		return aliveTime;
	}

	public void setAliveTime(Long aliveTime) {
		this.aliveTime = aliveTime;
	}

	public Integer getReConnCount() {
		return reConnCount;
	}

	public void setReConnCount(Integer reConnCount) {
		this.reConnCount = reConnCount;
	}
}
