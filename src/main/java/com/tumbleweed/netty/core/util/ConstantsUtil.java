package com.tumbleweed.netty.core.util;

import com.tumbleweed.netty.core.constants.IoConstants;
import com.tumbleweed.netty.core.context.EnvVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConstantsUtil {

    private static final Logger logger = LoggerFactory.getLogger(ConstantsUtil.class);

    public static int initReadTimeout(){
        int timeout = IoConstants.REMOTE_TIMEOUT_READ_DEFAULT;
        try {
            timeout = EnvVar.curEnv().getIntegerVar(IoConstants.REMOTE_TIMEOUT_READ);
            if (timeout <= 0) {
                timeout = IoConstants.REMOTE_TIMEOUT_READ_DEFAULT;
            }
        } catch (Exception e) {
            logger.warn(IoConstants.REMOTE_TIMEOUT_READ + " 配置错误，将使用默认值： " + timeout);
        }
        return timeout;
    }

    public static int initOutReadTimeout(){
        int timeout = IoConstants.REMOTE_TIMEOUT_OUTJT_READ_DEFAULT;
        try {
            timeout = EnvVar.curEnv().getIntegerVar(
                    IoConstants.REMOTE_TIMEOUT_OUTJT_READ);
            if (timeout <= 0) {
                timeout = IoConstants.REMOTE_TIMEOUT_OUTJT_READ_DEFAULT;
            }
        } catch (Exception e) {
            logger.warn(IoConstants.REMOTE_TIMEOUT_OUTJT_READ + " 配置错误，将使用默认值： " + timeout);
        }
        return timeout;
    }

    public static int initOutConnectTimeout(){
        int timeout = IoConstants.REMOTE_TIMEOUT_OUTJT_CONNECT_DEFAULT;
        try {
            timeout = EnvVar.curEnv().getIntegerVar(
                    IoConstants.REMOTE_TIMEOUT_OUTJT_CONNECT);
            if (timeout <= 0) {
                timeout = IoConstants.REMOTE_TIMEOUT_OUTJT_CONNECT_DEFAULT;
            }
        } catch (Exception e) {
            logger.warn(IoConstants.REMOTE_TIMEOUT_OUTJT_CONNECT + " 配置错误，将使用默认值： " + timeout);
        }
        return timeout;
    }

	public static int initHsmReadTimeout(){
		int timeout = IoConstants.REMOTE_TIMEOUT_HSM_READ_DEFAULT;
		try {
			timeout = EnvVar.curEnv().getIntegerVar(
					IoConstants.REMOTE_TIMEOUT_HSM_READ);
			if (timeout <= 0) {
				timeout = IoConstants.REMOTE_TIMEOUT_HSM_READ_DEFAULT;
			}
		} catch (Exception e) {
			logger.warn(IoConstants.REMOTE_TIMEOUT_HSM_READ + " 配置错误，将使用默认值： " + timeout);
		}
		return timeout;
	}
	
	public static int initHsmConnectTimeout(){
		int timeout = IoConstants.REMOTE_TIMEOUT_HSM_CONNECT_DEFAULT;
		try {
			timeout = EnvVar.curEnv().getIntegerVar(
					IoConstants.REMOTE_TIMEOUT_HSM_CONNECT);
			if (timeout <= 0) {
				timeout = IoConstants.REMOTE_TIMEOUT_HSM_CONNECT_DEFAULT;
			}
		} catch (Exception e) {
			logger.warn(IoConstants.REMOTE_TIMEOUT_HSM_CONNECT + " 配置错误，将使用默认值： " + timeout);
		}
		return timeout;
	}
	
	public static String initHsmPacket(){
		String packet = IoConstants.HSM_HEARTBEAT_PACKET_DEFAULT;
		try {
			packet = EnvVar.curEnv().getVar(IoConstants.HSM_HEARTBEAT_PACKET);
			if (StringUtils.isNullOrEmpty(packet)) {
				packet = IoConstants.HSM_HEARTBEAT_PACKET_DEFAULT;
			}
		} catch (Exception e) {
			logger.warn(IoConstants.HSM_HEARTBEAT_PACKET + " 配置错误，将使用默认值： " + packet);
		}
		return packet;
	}


}
