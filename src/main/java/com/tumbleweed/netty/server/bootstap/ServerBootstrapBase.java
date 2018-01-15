package com.tumbleweed.netty.server.bootstap;

import com.tumbleweed.netty.core.bootstrap.BootstrapBase;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServerBootstrapBase extends BootstrapBase {

	static final Logger logger = LoggerFactory.getLogger(ServerBootstrapBase.class);

	protected EventLoopGroup bossServerGroup;

	protected EventLoopGroup workerServerGroup;
	
	protected void initServerGroup(int port, String suffix){
		super.port = port;
		super.suffix = suffix;
		bossServerGroup = new NioEventLoopGroup();
		workerServerGroup = new NioEventLoopGroup();
	}
	
	protected boolean excuteBootstrap() throws Exception {
		try{
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossServerGroup, workerServerGroup).channel(NioServerSocketChannel.class)
					.childHandler(initializer);
			b.bind(port).sync();
			return true;
		}catch(Exception e){
			logger.error(suffix + " server bootstrap 发生错误:"
					+ e.getMessage());
			return false;
		}

	}

	public void closeServer() {
		try {
			if (bossServerGroup != null && !bossServerGroup.isShutdown()) {
				bossServerGroup.shutdownGracefully();
			}
			if (workerServerGroup != null && !workerServerGroup.isShutdown()) {
				workerServerGroup.shutdownGracefully();
			}
		} catch (Exception e) {
			logger.error(suffix + " close server 发生错误:" + e.getMessage());
		}
	}
	
	public int getServerStatus(){
		if(bossServerGroup == null || workerServerGroup == null){
			return 0;
		}else{
			if(bossServerGroup.isShutdown()){
				return 2;
			}
			if(workerServerGroup.isShutdown()){
				return 3;
			}
			if(!bossServerGroup.isShutdown() && !workerServerGroup.isShutdown()){
				return 1;
			}
			return 4;
		}
	}
}
