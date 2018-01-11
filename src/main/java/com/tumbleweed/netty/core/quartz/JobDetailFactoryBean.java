package com.tumbleweed.netty.core.quartz;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.impl.JobDetailImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.util.MethodInvoker;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * spring的quartz调度类org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean的替换类
 * 定制增加了一些启动判断等功能
 */
public class JobDetailFactoryBean extends MethodInvokingJobDetailFactoryBean{

	private static final Logger logger = LoggerFactory.getLogger(JobDetailFactoryBean.class);
				
	@Resource
	public void setJobQuartzDataSource(DataSource jobQuartzDataSource) {
		JobDetailFactoryBean.AppsMethodInvokingJob.jobQuartzDataSource = jobQuartzDataSource;
	}

	@Resource
	public void setQuartzSQL(String quartzSQL) {
		JobDetailFactoryBean.AppsMethodInvokingJob.quartzSQL = quartzSQL;
	}

	@Override
	public void afterPropertiesSet() throws ClassNotFoundException,NoSuchMethodException {
		super.afterPropertiesSet();

		String clazzName = ((JobDetail) super.getObject()).getJobClass().getName();
		logger.info("origin jobClass : " + clazzName);

		// Consider the concurrent flag to choose between stateful and stateless job.
		String invokeName = MethodInvokingJob.class.getName();
		logger.info("MethodInvokingJob.name : " + invokeName);
		//System.out.println("MethodInvokingJob.name : " + invokeName);
		if(invokeName.equals(clazzName)) {
			logger.info("jobClass : " + clazzName);
			((JobDetailImpl)super.getObject()).setJobClass(AppsMethodInvokingJob.class);
		} else {
			((JobDetailImpl)super.getObject()).setJobClass(AppsStatefulMethodInvokingJob.class);
		}
		logger.info("new jobClass : " + clazzName);

		if(JobDetailFactoryBean.AppsMethodInvokingJob.jobQuartzDataSource == null ||
				JobDetailFactoryBean.AppsMethodInvokingJob.quartzSQL == null || "".equals(JobDetailFactoryBean.AppsMethodInvokingJob.quartzSQL)){
			logger.info("jobQuartzDataSource = " + JobDetailFactoryBean.AppsMethodInvokingJob.jobQuartzDataSource);
			//System.out.println("jobQuartzDataSource = " + JobDetailFactoryBean.AppsMethodInvokingJob.jobQuartzDataSource);
			logger.info("dataSource or init select sql not found ! Pls check your config !");	
			//System.out.println("dataSource or init select sql not found ! Pls check your config !");
			throw new RuntimeException("dataSource or init select sql not found ! Pls check your config !");
		}
		
		logger.info("init success !");	
		//System.out.println("init success !");

	}
	
	public static class AppsMethodInvokingJob extends MethodInvokingJob{
		protected static final Logger logger = LoggerFactory.getLogger(AppsMethodInvokingJob.class);
		private MethodInvoker methodInvoker;
		private String errorMessage;

		static DataSource jobQuartzDataSource;
		static String quartzSQL;
		
		/**
		 * Set the MethodInvoker to use.
		 */
		public void setMethodInvoker(MethodInvoker methodInvoker) {
			this.methodInvoker = methodInvoker;
		}

		private List<String> getIPs() throws SocketException {
			List<String> ips = new ArrayList<String>();
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();

					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
						String ip = inetAddress.getHostAddress().toString();
						if(ip != null && !ip.startsWith("127.0")){
							ips.add(ip);
							logger.info("a ip addr -> " + ip);
						}
					}
				}
			}
			
			return ips;
		}

		/**
		 * Invoke the method via the MethodInvoker.
		 */
		protected void executeInternal(JobExecutionContext context) throws JobExecutionException {		
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {			
				List<String> ips = this.getIPs();
				
				//根据当前服务器主机名或者IP判断是否需要执行该任务				
				conn = jobQuartzDataSource.getConnection();
				//根据IP地址查数据库				
				pstmt = conn.prepareStatement(quartzSQL);
				
				rs = pstmt.executeQuery();   
				boolean flag = false;
				while(rs.next()){
					String ip = rs.getString("instid").trim();
					if(ips.contains(ip)){
						flag = true;
						break;
					}
				}
				
				if(flag) {//数据库里的IP地址匹配
					logger.info("IP right , running the quartz task!");	
					
					//调用具体task执行method代码
					this.methodInvoker.invoke();					
				}else {//数据库里没有和IP地址匹配的数据
					logger.info("IP not right , cannot running the quartz task!");						
				}				
			} catch (Exception ex) {
				logger.error(" accounted a error : " + this.errorMessage, ex);
				throw new JobExecutionException(this.errorMessage, ex, false);
			} finally{
				try{
					if(rs != null)
						rs.close();
					if(pstmt != null)
						pstmt.close();
					if(conn != null)
						conn.close();
				}catch(Exception e){}
			}
		}
	}
	
	public static class AppsStatefulMethodInvokingJob extends AppsMethodInvokingJob {
	}
}
