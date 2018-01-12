package com.tumbleweed.netty.client.quartz;

import com.tumbleweed.netty.client.context.DomainCollectionContext;
import com.tumbleweed.netty.core.constants.IoConstants;
import com.tumbleweed.netty.core.context.EnvVar;
import com.tumbleweed.netty.core.util.StringUtils;
import com.tumbleweed.netty.core.util.Utilities;
import com.tumbleweed.netty.core.domain.DomainBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MapClearQuartz {

	private static final Logger logger = LoggerFactory.getLogger(MapClearQuartz.class);
	
	private List<DomainCollectionContext> dhmcList = new ArrayList<DomainCollectionContext>();

	private static int blockingQueueClearTimeout = IoConstants.BLOCKINGQUEUE_CLEAR_TIMEOUT_DEFAULT * 1000;
	
	static {
		try {
			blockingQueueClearTimeout = EnvVar.curEnv().getIntegerVar(IoConstants.BLOCKINGQUEUE_CLEAR_TIMEOUT) * 1000;
		} catch (Exception e) {
			logger.warn(IoConstants.BLOCKINGQUEUE_CLEAR_TIMEOUT + " 配置 warn: " + e.getMessage());
		}
	}
	
	public void execute() {
		clearBlockingQueue();
	}
	
	private void clearBlockingQueue(){
		if (!Utilities.isNullOrEmpty(dhmcList)) {
			String uuid = null;
			long startTime = 0;
			DomainBase db = null;
			ConcurrentHashMap<String, DomainBase> hsmMap = null;
			long currentTime = System.currentTimeMillis();
			for (DomainCollectionContext dhmc : dhmcList) {
					try {
						List<String> uuids = new ArrayList<String>();
						hsmMap = dhmc.getDomainMap();
						Iterator<DomainBase> iterator = hsmMap.values().iterator();
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
						if (!Utilities.isNullOrEmpty(uuids)) {
							Iterator<String> it = uuids.iterator();
							while (it.hasNext()) {
								String uuidIt = it.next();
								dhmc.removeDomain(uuidIt);
							}
						}
						if(logger.isInfoEnabled()){
							int domainMapSize = dhmc.getDomainMap().size();
							if(domainMapSize > 5){
								logger.info(dhmc.getSuffix()
                                        + " DomainCollectionContext 清理之后的容量为：" + domainMapSize);
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
