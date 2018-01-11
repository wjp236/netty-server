package com.tumbleweed.netty.core.constants;

/**
 * 
 * @Title: IoConstants.java
 * @Package: com.lemon.io.constants
 * @Description: io 常量定义
 * @author: jiangke
 * @date: 2013年10月19日
 * @version V1.0
 */
public class IoConstants {

    public static final String CONFIG_NAME = "config";

	/**
	 * 心跳包内容 与suffix绑定
	 */
	public static final String HEART_BEAT_PACKET = "heart.beat.packet.";

	/**
	 * 心跳回复包内容 与suffix绑定
	 */
	public static final String HEART_BEAT_REBACK_PACKET = "heart.beat.reback.packet.";
	
	/**
	 * 加密机重连间隔次数(c)
	 */
	public static final String REMOTE_TIMEOUT_HSM_CONNECT = "remote.timeout.hsm.connect";
	
	/**
	 * 加密重连间隔默认次数(c)
	 */
	public static final int REMOTE_TIMEOUT_HSM_CONNECT_DEFAULT = 10;
	
	/**
	 * 加密机心跳间隔时间(s)
	 */
	public static final String REMOTE_TIMEOUT_HSM_READ = "remote.timeout.hsm.read";
	
	/**
	 * OutJt心跳间隔默认时间(s)
	 */
	public static final int REMOTE_TIMEOUT_OUTJT_READ_DEFAULT = 30;
	
	/**
	 * OutJt心跳间隔时间(s)
	 */
	public static final String REMOTE_TIMEOUT_OUTJT_READ = "remote.timeout.outjt.read";
	
	/**
	 * OutJt重连间隔次数(c)
	 */
	public static final String REMOTE_TIMEOUT_OUTJT_CONNECT = "remote.timeout.outjt.connect";
	
	/**
	 * OutJt重连间隔默认次数(c)
	 */
	public static final int REMOTE_TIMEOUT_OUTJT_CONNECT_DEFAULT = 10;
	
	/**
	 * OutJt心跳报文
	 */
	public static final String OUTJT_HEARTBEAT_PACKET = "0000";
	
	/**
	 * 加密机心跳间隔默认时间(s)
	 */
	public static final int REMOTE_TIMEOUT_HSM_READ_DEFAULT = 10;
	
	
	/**
	 * 加密机心跳报文
	 */
	public static final String HSM_HEARTBEAT_PACKET = "hsm.heartbeat.packet";
	
	/**
	 * 加密机心跳默认报文
	 */
	public static final String HSM_HEARTBEAT_PACKET_DEFAULT = "IAXD60A2E67FDB55FF5F5CD56668AA341D3;XX0";

	public static final String REMOTE_TIMEOUT_READ = "remote.timeout.read";
	
	public static final int REMOTE_TIMEOUT_READ_DEFAULT = 10;

	public static final String REMOTE_TIMEOUT_WRITE = "remote.timeout.write";
	
	public static final int REMOTE_TIMEOUT_WRITE_DEFAULT = 5;
	
	/**
	 * 连接超时时间
	 */
	public static final String REMOTE_TIMEOUT_CONNECT = "remote.timeout.connect";
	
	/**
	 * 连接超时默认时间
	 */
	public static final int REMOTE_TIMEOUT_CONNECT_DEFAULT = 10;

	/**
	 * 重连间隔时间,可根据不同应用在config配置
	 */
	public static final String RECONNECT_INTERVAL = "reconnect.interval";
	
	/**
	 * 重连间隔默认时间
	 */
	public static final int RECONNECT_INTERVAL_DEFAULT = 10;

	/**
	 * 重连次数,可根据不同应用在config配置，默认一直重连
	 */
	public static final String RECONNECT_COUNT = "reconnect.count";
	
	/**
	 * blockingQueue 是否需要清理标记(此属性只适用 lemon-gateway-hsm)	
	 */
	public static final String BLOCKINGQUEUE_CLEAR_QUARTZ_ENABLE_FLAG = "blockingqueue.clear.enable.flag";
	
	/**
	 * blockingQueue 超时时间	
	 */
	public static final String 	BLOCKINGQUEUE_POLL_TIMEOUT = "blockingqueue.poll.timeout";
	
	/**
	 * blockingQueue 超时默认时间	
	 */
	public static final int	BLOCKINGQUEUE_POLL_TIMEOUT_DEFAULT = 15;
	
	/**
	 * 调度器清理blocking map超时时间
	 */
	public static final String 	BLOCKINGQUEUE_CLEAR_TIMEOUT = "blockingQueue.clear.timeout";
	
	/**
	 * 调度器清理blocking map超时默认时间
	 */
	public static final int	BLOCKINGQUEUE_CLEAR_TIMEOUT_DEFAULT = 90;
	
	/**
	 * 调度器清理appender map超时时间
	 */
	public static final String 	APPENDER_CLEAR_TIMEOUT = "appender.clear.timeout";
	
	/**
	 * 调度器清理appender map超时默认时间
	 */
	public static final int	APPENDER_CLEAR_TIMEOUT_DEFAULT = 90;
	
	/**
	 * 长连接高可用处理客户端(jt client)
	 */
	public static final String LONGCONN_HIGH_AVAILABILITY_CLIENT = "longConn.high.availability.client";
	
	/**
	 * 长连接高可用处理 bankName之间用|分开
	 */
	//public static final String LONGCONN_HIGH_AVAILABILITY_CONFIG = "longConn.high.availability.config";
	
	/**
	 * 上游机构报文头长度
	 */
	public static final int GATEWAY_OUT_PACKET_HEAD_LENGTH = 46;
	
	/**
	 * 上游机构报文头错误码(默认)
	 */
	public static final String GATEWAY_OUT_PACKET_HEAD_REJECTCODE = "00000";
	
	/**
	 * head IsoMessage 是否使用binary
	 */
	public static final String GATEWAY_OUT_HEAD_USEBINARY = "gateway.out.head.useBinary";
	
	/**
	 * body IsoMessage 是否使用binary
	 */
	public static final String GATEWAY_OUT_BODY_USEBINARY = "gateway.out.body.useBinary";
	
	/**
	 * body IsoMessage bitmapBinary 是否使用binary
	 */
	public static final String GATEWAY_OUT_BODY_BITMAPBINARY = "gateway.out.body.bitmapBinary";
	
	/**
	 * head length 的hex值 
	 */
	public static final String GATEWAY_OUT_HEAD_LENGTH_HEX = "gateway.out.head.length.hex";
	
	/**
	 * head length 的hex默认值 
	 */
	public static final int GATEWAY_OUT_HEAD_LENGTH_HEX_DEFAULT = 46;
	
	/**
	 * head 域的大小
	 */
	public static final String GATEWAY_OUT_HEAD_FIELD_LENGTH = "gateway.out.head.field.length";
	
	/**
	 * 报文长度位数
	 */
	public static final String GATEWAY_OUT_PACKET_LENGTH_BIT = "gateway.out.packet.length.bit";
	
	/**
	 * 报文长度位数是否采用BCD
	 */
	public static final String GATEWAY_OUT_PACKET_LENGTH_USEBCD = "gateway.out.packet.length.useBcd";
	
	/**
	 * 报文头长度位数
	 */
	public static final String GATEWAY_OUT_PACKET_HEAD_LENGTH_BIT = "gateway.out.packet.head.length.bit";
	
	/**
	 * POS报文头长度位数
	 */
	public static final String POS_PACKET_HEAD_LENGTH = "pos.packet.head.length";
	
	/**
	 * POS报文头长度位数默认值
	 */
	public static final Integer POS_PACKET_HEAD_LENGTH_DEFAULT = 11;
	
	public static final String SUFFIX = "suffix";
	
	/**
	 * POS原IP
	 */
	public static final String REMOTEIP = "REMOTEIP";
	
	/**
	 * pos上送原始报文
	 */
	public static final String POS_ORIGINAL_PACKETS = "pos.original.packets";
	
	public static final String IO_FUTURE_FAILED = "IO.FUTURE.FAILED";
	
	/**
	 * 
	 */
	public static final String ISO8583_GATEWAY_0800 = "0800";
	
	public static final String ISO8583_GATEWAY_0810 = "0810";
	
	public static final String ISO8583_GATEWAY_0830 = "0830";
}
