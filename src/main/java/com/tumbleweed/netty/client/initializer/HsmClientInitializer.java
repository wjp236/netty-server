package com.tumbleweed.netty.client.initializer;

import com.tumbleweed.netty.client.decoder.HsmClientDecoder;
import com.tumbleweed.netty.client.encoder.HsmClientEncoder;
import com.tumbleweed.netty.client.handler.HsmClientHandler;
import com.tumbleweed.netty.core.util.HandlerUtil;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class HsmClientInitializer extends ChannelInitializer<SocketChannel> {

	private String suffix;

	public HsmClientInitializer(String suffix) {
		this.suffix = suffix;
	}

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		HandlerUtil.initIoLog(pipeline);
		pipeline.addLast(new HsmClientEncoder());
		pipeline.addLast(new HsmClientDecoder());
		pipeline.addLast("HsmHandler", new HsmClientHandler(suffix));
	}
}
