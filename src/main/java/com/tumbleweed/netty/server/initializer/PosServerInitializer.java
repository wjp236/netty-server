package com.tumbleweed.netty.server.initializer;

import com.tumbleweed.netty.core.util.HandlerUtil;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.List;

public class PosServerInitializer extends ChannelInitializer<SocketChannel> {
	private List<String> handlers;

	private String suffix;

	public PosServerInitializer(String suffix, List<String> handlers) {
		this.suffix = suffix;
		this.handlers = handlers;
	}

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		HandlerUtil.initIoLog(pipeline);
		HandlerUtil.initChannel(pipeline, handlers, suffix);
	}
}
