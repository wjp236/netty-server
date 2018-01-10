package com.tumbleweed.io.decoder;

import com.tumbleweed.io.domain.CommDomain;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;

public class CommClientDecoder extends MessageToMessageDecoder<ByteBuf> {
	static final Logger logger = LoggerFactory
			.getLogger(CommClientDecoder.class);

	private final Charset charset;

	public CommClientDecoder() {
		this(Charset.defaultCharset());
	}

	public CommClientDecoder(Charset charset) {
		if (charset == null) {
			throw new NullPointerException("charset");
		}
		this.charset = charset;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg,
			List<Object> out) throws Exception {
		if(msg == null || msg.readableBytes() <= 0){
			return;
		}
		logger.debug("comm 开始解码......");
		int readLength = msg.readableBytes();
		byte[] packets = new byte[readLength];
		msg.readBytes(packets);
		CommDomain commDomain = new CommDomain();
		commDomain.setPackets(packets);
		out.add(commDomain);
		logger.debug("comm 解码结束");
	}
}