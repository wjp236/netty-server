package com.tumbleweed.netty.client.encoder;

import com.tumbleweed.netty.client.domain.CommDomain;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommClientEncoder extends MessageToByteEncoder<CommDomain> {

	private static final Logger logger = LoggerFactory.getLogger(CommClientEncoder.class);
	
	private String suffix;

	public CommClientEncoder(String suffix) throws Exception {
		this.suffix = suffix;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, CommDomain commDomian, ByteBuf out) throws Exception {
		logger.warn(commDomian.getUuid(), "======" + suffix + " comm 开始编码......");

		byte[] packets = commDomian.getPackets();
		if(packets == null || packets.length == 0){
			throw new RuntimeException("commDomian 验证发生错误");
		}
		ByteBufOutputStream bout = new ByteBufOutputStream(out);
		bout.write(packets);
		bout.flush();
		bout.close();
		logger.warn("======" + suffix + " comm 编码结束，消息已经发送");
	}
}