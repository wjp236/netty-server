package com.tumbleweed.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PosServerApp {

	private static final Logger logger = LoggerFactory.getLogger(PosServerApp.class);

	public static void main(String[] args){
		ClassPathXmlApplicationContext context = null;
		try {
			context = new ClassPathXmlApplicationContext(
					new String[] { "applicationContext-netty-server.xml" });
			context.start();
		} catch (Exception e) {
			logger.error(e.getMessage());
			if (context != null) {
				context.close();
			}
		}
	}
}
