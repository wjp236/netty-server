package com.tumbleweed.netty.client.bean;

import com.tumbleweed.netty.client.bootstrap.HsmClientBootstrap;
import com.tumbleweed.netty.client.context.ClientContext;
import com.tumbleweed.netty.client.context.DomainCollectionContext;
import com.tumbleweed.netty.client.quartz.HeartbeatQuartz;
import com.tumbleweed.netty.core.bean.BootstrapContextHelper;
import com.tumbleweed.netty.core.constants.LogType;
import com.tumbleweed.netty.core.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HsmAppBean extends AppBeanBase {

    private static final Logger logger = LoggerFactory.getLogger(HsmAppBean.class);

    private HsmClientBootstrap hcb = null;

    public void startService() {
        super.lcc = new ClientContext(suffix);
        super.dhmc = new DomainCollectionContext(suffix);
        try {
            logger.debug(suffix + " 加密机网关开始连接远程加密机(" + ip + ":" + port + ")......");
            hcb = new HsmClientBootstrap(ip, port, suffix);
            hcb.connect();
            logger.debug(suffix + " 加密机网关连接远程加密机成功");
        } catch (Exception e) {
            logger.error("加密机网关连接远程加密机(" + ip + ":" + port + ")发生异常:"
                    + e.getMessage());
            if (hcb != null) {
                hcb.releaseConnect();
                logger.error("加密机网关连接远程加密机(" + ip + ":" + port + ")时发生异常，服务关闭");
            }
        } finally {
            try {
                HeartbeatQuartz heartbeatQuartz = (HeartbeatQuartz) BootstrapContextHelper.getBean("heartbeatQuartz");
                heartbeatQuartz.setHsmAppBeans(suffix);
            } catch (Exception e) {
                logger.error("heartbeatQuartz setHsmAppBeans error:"
                        + e.getMessage());
            }
        }
    }

    public void stopService() {
        if (hcb != null) {
            hcb.releaseConnect();
            logger.debug(LogUtils.genLogs(LogType.PTS, null, null, "加密机网关服务已关闭"));
        }
    }
}
