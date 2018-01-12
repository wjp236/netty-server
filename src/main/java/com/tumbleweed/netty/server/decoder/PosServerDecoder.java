package com.tumbleweed.netty.server.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PosServerDecoder extends ByteToMessageDecoder {
	private static final Logger logger = LoggerFactory
			.getLogger(PosServerDecoder.class);
	
	private String suffix;

	public PosServerDecoder(String suffix) throws Exception {
		this.suffix = suffix;
	}
	
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		if(msg == null || msg.readableBytes() <= 0){
			return;
		}
    	int readLength = msg.readableBytes();
    	msg.markReaderIndex();
    	
    	byte[] lengths = new byte[2];
    	msg.readBytes(lengths);
    	msg.resetReaderIndex();
    	int forecastLength = ((lengths[0] & 0xff) << 4) | (lengths[1] & 0xff);
    	if (readLength < forecastLength + 2) {
    		logger.warn(suffix + " pos resetReaderIndex!");
    		return;
    	}
    	logger.debug(suffix + " pos ok!--------------------------------------------------------------------------------------------------------");
		out.add(msg);
    }
}
