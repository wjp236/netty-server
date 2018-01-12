package com.tumbleweed.netty.core.bootstrap;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class BootstrapBase {

	static final Logger logger = LoggerFactory.getLogger(BootstrapBase.class);

	protected int port;

	protected String suffix;

	protected ChannelInitializer<SocketChannel> initializer;
	
	protected List<String> handlers;

	public String getSuffix() {
		return suffix;
	}

	protected void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public List<String> getHandlers() {
		return handlers;
	}
}
