package com.tumbleweed.io.initializer;

import com.tumbleweed.io.decoder.CommClientDecoder;
import com.tumbleweed.io.encoder.CommClientEncoder;
import com.tumbleweed.io.handler.CommClientHandler;
import com.tumbleweed.io.util.HandlerUtil;
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
