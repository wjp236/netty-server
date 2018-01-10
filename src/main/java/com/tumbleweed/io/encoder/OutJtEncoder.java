package com.tumbleweed.io.encoder;

import com.google.gson.Gson;
import com.tumbleweed.io.domain.Lemon8583;
import com.tumbleweed.io.util.OmpressData;
import com.mysql.jdbc.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class OutJtEncoder extends MessageToByteEncoder<Lemon8583> {
	private static final Logger logger = LoggerFactory
			.getLogger(OutJtEncoder.class);
	
	private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
	
	private String suffix;

	public OutJtEncoder(String suffix) throws Exception {
		this.suffix = suffix;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Lemon8583 lemon8583,
			ByteBuf out) throws Exception {
		logger.warn("======" + suffix + " jt 开始编码......");
		if(!lemon8583.validate()){
			logger.error("lemon8583 验证发生错误");
			throw new RuntimeException();
		}
		int startIdx = out.writerIndex();

		ByteBufOutputStream bout = new ByteBufOutputStream(out);
		bout.write(LENGTH_PLACEHOLDER);
		
		byte[] packcts = null;
		if(lemon8583.getHeartbeatPacket() != null){
			packcts = lemon8583.getHeartbeatPacket().getBytes();
		}else{
			if(StringUtils.isNullOrEmpty(lemon8583.getUuid())){
				new Exception("lemon8583 uuid is null ");
			}
			String packetsStr = new Gson().toJson(lemon8583).trim();
			logger.info("jt 发送报文:" + packetsStr);
			packcts = packetsStr.getBytes(Charset.defaultCharset());
			packcts = OmpressData.newInstance().Compress(packcts, packcts.length);
		}
		bout.write(packcts);
		
		bout.flush();
		bout.close();

		int endIdx = out.writerIndex();
		out.setInt(startIdx, endIdx - startIdx - 4);
		logger.warn("======" + suffix + " jt 编码结束，消息已经发送");
	}
}