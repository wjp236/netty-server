package com.tumbleweed.io.decoder;

import com.tumbleweed.io.domain.Hsm;
import com.tumbleweed.io.util.HexConvertUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;

public class HsmClientDecoder extends ByteToMessageDecoder {
	static final Logger logger = LoggerFactory
			.getLogger(HsmClientDecoder.class);

	private final Charset charset;

	public HsmClientDecoder() {
		this(Charset.defaultCharset());
	}

	public HsmClientDecoder(Charset charset) {
		if (charset == null) {
			throw new NullPointerException("charset");
		}
		this.charset = charset;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg,
			List<Object> out) {
		if (msg == null || msg.readableBytes() <= 0) {
			return;
		}
		logger.debug("加密机网关开始解码HSM返回的报文......");
		int readLength = msg.readableBytes();
		msg.markReaderIndex();

		byte[] lengths = new byte[2];
		msg.readBytes(lengths);
		int forecastLength = Integer.parseInt(HexConvertUtil.byte2HexStr(lengths), 16);
		logger.debug("加密机网关收到的长度forecastLength=" + forecastLength);
		if (readLength < forecastLength + 2) {
			msg.resetReaderIndex();
			logger.debug("加密机网关收到的长度不对");
			return;
		}
		if(forecastLength <= 4){
			logger.error("加密机网关收到的长度=" + forecastLength + ";加密报文有可能有误");
		}
		byte[] body = new byte[forecastLength];
		msg.readBytes(body);
		Hsm hsm = new Hsm();
		hsm.setBody(new String(body, charset));
		out.add(hsm);
		logger.debug("加密机网关完成解析HSM返回的报文，报文内容如下：" + hsm.getBody());
	}

	public static void main(String[] args) {
		int forecastLength = Integer.parseInt("0056", 16);
		logger.debug("forecastLength=" + forecastLength);
	}
}
