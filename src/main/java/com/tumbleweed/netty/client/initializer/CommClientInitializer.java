package com.tumbleweed.netty.client.initializer;

import com.tumbleweed.netty.client.decoder.CommClientDecoder;
import com.tumbleweed.netty.client.encoder.CommClientEncoder;
import com.tumbleweed.netty.client.handler.CommClientHandler;
import com.tumbleweed.netty.core.util.HandlerUtil;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class CommClientInitializer extends ChannelInitializer<SocketChannel> {
	
	private String suffix;

	public CommClientInitializer(String suffix) {
		this.suffix = suffix;
	}

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		HandlerUtil.initIoLog(pipeline);
        pipeline.addLast("decoder", new CommClientDecoder());
        pipeline.addLast("encoder", new CommClientEncoder(suffix));
		pipeline.addLast(new CommClientHandler(suffix));
	}
}
