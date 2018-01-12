package com.tumbleweed.netty.server.bootstap;

import com.tumbleweed.netty.core.context.EnvVar;
import com.tumbleweed.netty.server.initializer.PosServerInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PosServerBootstrap extends ServerBootstrapBase {
    
    private static final Logger logger = LoggerFactory
            .getLogger(PosServerBootstrap.class);
    
	public PosServerBootstrap(int port, String suffix, List<String> handlers) {
		initServerGroup(port, suffix);
		super.handlers = handlers;
	}

	public void bootstrap() throws Exception {
		super.initializer = new PosServerInitializer(suffix, handlers);
		excuteBootstrap();
	}
	
	protected void initServerGroup(int port, String suffix){
        super.port = port;
        super.suffix = suffix;
        
        int bossThreads = 0;
        try {
            bossThreads = EnvVar.curEnv().getIntegerVar(
                    "geteway.pos.bossthread");
        } catch (Exception e) {
            logger.warn(suffix + " geteway.pos.bossthread 未配置: " 
                    + e.getMessage() + ", 将采用默认值:" + bossThreads);
        }
        int workerThreads = 0;
        try {
        	workerThreads = EnvVar.curEnv().getIntegerVar(
                    "geteway.pos.workerthread");
        } catch (Exception e) {
            logger.warn(suffix + " geteway.pos.workerthread 未配置: " 
                    + e.getMessage() + ", 将采用默认值:" + bossThreads);
        }
        logger.info("创建bossServerGroup，启动线程池个数[{}]，0为默认大小。",bossThreads);
        super.bossServerGroup = new NioEventLoopGroup(bossThreads);
        
        logger.info("创建workerServerGroup，启动线程池个数[{}]，0为默认大小。",workerThreads);
        super.workerServerGroup = new NioEventLoopGroup(workerThreads);
    }
}
