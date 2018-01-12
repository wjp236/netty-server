package com.tumbleweed.netty.server.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;

public class PosServerEncoder extends MessageToByteEncoder<byte[]> {
	private static final Logger logger = LoggerFactory
			.getLogger(PosServerEncoder.class);

	private String suffix;

	public PosServerEncoder(String suffix) throws Exception {
		this.suffix = suffix;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, byte[] bytes, ByteBuf out)
			throws Exception {
		logger.warn("======pos网关开始封装下发pos报文，8583数据如下:");
		OutputStream bout = new ByteBufOutputStream(out);
		int l = bytes.length;
		byte[] lengths = new byte[2];
		int pos = 0;
		lengths[pos] = (byte) ((l & 0xff00) >> 8);
		pos++;
		lengths[pos] = (byte) (l & 0xff);
		bout.write(lengths);
		bout.write(bytes);
		bout.flush();
		bout.close();
		logger.warn("======pos网关完成封装下发pos报文，下发pos报文数据完成");



	}
}
