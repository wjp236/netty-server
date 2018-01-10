package com.tumbleweed.io.initializer;

import com.tumbleweed.io.decoder.OutJtDecoder;
import com.tumbleweed.io.encoder.OutJtEncoder;
import com.tumbleweed.io.handler.OutJtClientHandler;
import com.tumbleweed.io.util.ConstantsUtil;
import com.tumbleweed.io.util.HandlerUtil;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class OutJtClientInitializer extends ChannelInitializer<SocketChannel> {
	private String suffix;

	public OutJtClientInitializer(String suffix) {
		this.suffix = suffix;
	}

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		HandlerUtil.initIoLog(pipeline);
		pipeline.addLast(new IdleStateHandler(ConstantsUtil.initOutJtReadTimeout(), 0, 0));
		pipeline.addLast(new OutJtEncoder(suffix));
		pipeline.addLast(new OutJtDecoder(suffix));
		pipeline.addLast(new OutJtClientHandler(suffix));
	}
}
