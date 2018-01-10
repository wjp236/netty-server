package com.tumbleweed.io.quartz;

import com.tumbleweed.io.constants.IoConstants;
import com.tumbleweed.io.context.DomainCollectionContext;
import com.tumbleweed.io.domain.DomainBase;
import com.mysql.jdbc.StringUtils;
import com.xinyipay.commons.utils.lang.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MapClearQuartz {
	private static final Logger logger = LoggerFactory
			.getLogger(MapClearQuartz.class);
	
	private List<DomainCollectionContext> dhmcList = new ArrayList<DomainCollectionContext>();

	private static int blockingQueueClearTimeout = IoConstants.BLOCKINGQUEUE_CLEAR_TIMEOUT_DEFAULT * 1000;
	
	static {
		try {
			String blockingQueueClearTimeoutTemp = PropertyUtil.getInstance(IoConstants.CONFIG_NAME)
                    .getProperty(IoConstants.BLOCKINGQUEUE_CLEAR_TIMEOUT);
            blockingQueueClearTimeout = Integer.parseInt(blockingQueueClearTimeoutTemp) * 1000;
		} catch (Exception e) {
			logger.warn(IoConstants.BLOCKINGQUEUE_CLEAR_TIMEOUT
					+ " 配置 warn: " + e.getMessage());
		}
	}
	
	public void execute() {
		clearBlockingQueue();
	}
	
	private void clearBlockingQueue(){
		if (dhmcList != null && dhmcList.size() > 0) {
			String uuid = null;
			long startTime = 0;
			DomainBase db = null;
			ConcurrentHashMap<String, DomainBase> hsmMap = null;
			long currentTime = System.currentTimeMillis();
			for (DomainCollectionContext dhmc : dhmcList) {
					try {
						List<String> uuids = new ArrayList<String>();
						hsmMap = dhmc.getDomainMap();
						Iterator<DomainBase> iterator = hsmMap.values()
								.iterator();
						while (iterator.hasNext()) {
							db = iterator.next();
							uuid = db.getUuid();
							startTime = db.getStartTime();
							if ((currentTime - startTime) > (blockingQueueClearTimeout)) {
								if (!StringUtils.isNullOrEmpty(uuid)) {
									uuids.add(db.getUuid());
								}
							}
						}
						if (uuids != null && uuids.size() > 0) {
							Iterator<String> it = uuids.iterator();
							while (it.hasNext()) {
								String uuidIt = it.next();
								dhmc.removeDomain(uuidIt);
							}
						}
						if(logger.isInfoEnabled()){
							int domainMapSize = dhmc.getDomainMap().size();
							if(domainMapSize > 5){
								logger.info(dhmc.getSuffix() + " DomainCollectionContext 清理之后的容量为："
										+ domainMapSize);
							}
						}
					} catch (RuntimeException e) {
						logger.error(" excuteClear error:" + e.getMessage());
					}
			}
		}
	}
	
	public void setDomainCollectionContext(DomainCollectionContext dhmc){
		dhmcList.add(dhmc);
	}
}
