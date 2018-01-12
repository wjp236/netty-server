package com.tumbleweed.netty.client.encoder;

import com.mysql.jdbc.StringUtils;
import com.tumbleweed.netty.client.domain.HsmDomain;
import com.tumbleweed.netty.core.context.EnvVar;
import com.tumbleweed.netty.core.util.BcdUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class HsmClientEncoder extends MessageToByteEncoder<HsmDomain> {

	private static final Logger logger = LoggerFactory.getLogger(HsmClientEncoder.class);

	public HsmClientEncoder() throws Exception {
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, HsmDomain hsm, ByteBuf out)
			throws Exception {
		String encoderStr = hsm.toEncoderString();
		if (StringUtils.isNullOrEmpty(encoderStr)) {
			logger.debug("加密机网关编码时，hsm字段为空，抛出异常");
			throw new Exception("hsm field is null");
		}
		logger.debug("加密机网关开始编码上送HSM报文");
		ByteBufOutputStream bout = new ByteBufOutputStream(out);
		String hsmProd = EnvVar.curEnv().getVar("hsm.prod");
		if (hsmProd != null && "true".equals(hsmProd)) {
			byte[] heads = BcdUtil.str2Bcd(String.format("%04x", encoderStr.length()));
			bout.write(heads);
		}
		byte[] hsms = encoderStr.getBytes(Charset.defaultCharset());
		bout.write(hsms);
		bout.flush();
		bout.close();
		logger.debug("加密机网关完成编码上送HSM报文，即将发送报文数据");
	}
}