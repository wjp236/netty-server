package com.tumbleweed.io.decoder;

import com.google.gson.Gson;
import com.tumbleweed.io.constants.IoConstants;
import com.tumbleweed.io.domain.Lemon8583;
import com.tumbleweed.io.util.ByteBufUtil;
import com.tumbleweed.io.util.OmpressData;
import com.mysql.jdbc.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class OutJtDecoder extends LengthFieldBasedFrameDecoder {
	private static final Logger logger = LoggerFactory
			.getLogger(OutJtDecoder.class);
	
	private String suffix;
	
	public OutJtDecoder(String suffix){
		this(suffix, 10000000);
	}

	public OutJtDecoder(String suffix, int maxObjectSize) {
		super(maxObjectSize, 0, 4, 0, 4);
		this.suffix = suffix;
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in)
			throws Exception {
		if(in == null || in.readableBytes() <= 0){
			return null;
		}
		ByteBuf frame = (ByteBuf) super.decode(ctx, in);
		if (frame == null) {
			return null;
		}
		logger.warn("======" + suffix + " jt server 收到消息，  开始解码......");
		String packetsStr = ByteBufUtil.toString(frame);
		if (StringUtils.isNullOrEmpty(packetsStr)) {
			return null;
		}
		Lemon8583 lemon8583 = null;
		if(IoConstants.OUTJT_HEARTBEAT_PACKET.equals(packetsStr)){
			lemon8583 = new Lemon8583(packetsStr);
		}else{
			int length = frame.readableBytes();
			byte[] packets = new byte[length];
			frame.readBytes(packets);
			packets = OmpressData.newInstance().deCompressData(packets,
					packets.length);
			packetsStr = new String(packets, Charset.defaultCharset()).trim();
			logger.info("jt 收到报文:" + packetsStr);
			lemon8583 = new Gson().fromJson(packetsStr, Lemon8583.class);
		}
		logger.warn("======" + suffix + " jt server 结束解码，处理器即将处理");
		return lemon8583;
	}

	@Override
	protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer,
			int index, int length) {
		return buffer.slice(index, length);
	}
}
