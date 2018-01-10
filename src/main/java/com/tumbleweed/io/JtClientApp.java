package com.tumbleweed.io;

import com.tumbleweed.io.constants.Lemon8583Type;
import com.tumbleweed.io.domain.Lemon8583;
import com.tumbleweed.io.util.ChannelHandlerContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Random;

public class JtClientApp {
	private static final Logger logger = LoggerFactory.getLogger(JtClientApp.class);

	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = null;
		try {
			context = new ClassPathXmlApplicationContext(
					new String[] { "applicationContext-jt-client.xml" });
			context.start();
			
			Random random = new Random(); 
			String uuid = String.format("%08d", random.nextInt(99999999));
			Lemon8583 lemon8583 = new Lemon8583(Lemon8583Type.TYPE_UC);
			
			logger.info(lemon8583.toJson());
			
			try {
				ChannelHandlerContextUtil.writeAndFlushBlock(lemon8583, "jtClient");
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			
			logger.info(((Lemon8583)lemon8583.getReturnDB()).toJson());
			logger.info("this progress is end !");
		} catch (Exception e) {
			logger.error(e.getMessage());
			if (context != null) {
				context.close();
			}
		}
	}
}
