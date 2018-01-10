package com.tumbleweed.io.initializer;

import com.tumbleweed.io.decoder.HsmClientDecoder;
import com.tumbleweed.io.encoder.HsmClientEncoder;
import com.tumbleweed.io.handler.HsmClientHandler;
import com.tumbleweed.io.util.HandlerUtil;
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
