package com.tumbleweed.netty;

import com.tumbleweed.netty.client.domain.HsmDomain;
import com.tumbleweed.netty.core.util.ChannelHandlerContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HsmClientApp {

	private static final Logger logger = LoggerFactory.getLogger(HsmClientApp.class);

	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = null;
		try {
			context = new ClassPathXmlApplicationContext(new String[] { "applicationContext-netty-client.xml" });
            context.start();
            Thread.sleep(5000);
            int i = 0;
            try {
                Thread.sleep(1000);
                logger.warn("======调用远程加密机第" + i);
                HsmDomain hsm = new HsmDomain();
                hsm.setCode("FI");//FI1XFF567C312B4F6ACB7803549C22BFD8FB
                if(i % 2 == 0){
                    hsm.setData("1XFF567C312B4F6ACB7803549C22BFD8FB");
                    logger.warn("======>>>>>XD60A2E67FDB55FF5F5CD56668AA341D3;XX0");
                }else{
                    hsm.setData("X3B59B096A2FB6829106374716F366DA5;XX0");
                    logger.warn("======>>>>>X3B59B096A2FB6829106374716F366DA5;XX0");
                }
                ChannelHandlerContextUtil.writeAndFlushBlockForOio(hsm);

                logger.warn("======>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                HsmDomain backHsm = (HsmDomain) hsm.getReturnDB();
                logger.warn("======" + backHsm.getBody());
                logger.warn("======调用远程加密机完毕");
            } catch (Exception es) {
                logger.error(es.getMessage());
            }
		} catch (Exception e) {
			logger.error(e.getMessage());
			if (context != null) {
				context.close();
			}
		}
	}
}
