package com.tumbleweed.netty.core.context;

import com.tumbleweed.netty.core.constants.ConstantsBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;


/**
 * @Title: ContextManager.java
 * @Package: com.tumbleweed.core.context
 * @Description: 初始化properties配置文件，可通过EnvVar访问属性值
 * @author: wangjp
 */
public class ContextManager {

	static final Logger logger = LoggerFactory.getLogger(ContextManager.class);

	private String filePath = ConstantsBase.CONTENT_DEF_FILE_PATH;

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public ContextManager() throws IOException {
		init();
	}

	private void init() throws IOException {
		InputStream in = null;
		File jarPath = new File(ContextManager.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String propertiesPath = jarPath.getParentFile().getParentFile().getAbsolutePath() + filePath;
		File file = new File(propertiesPath);
		if (!file.exists()) {
			in = ContextManager.class.getResourceAsStream(filePath);
		}else{
			in = new BufferedInputStream(new FileInputStream(propertiesPath));
		}
		Properties props = new Properties();
		props.load(in);
		Enumeration<?> en = props.propertyNames();
		while (en.hasMoreElements()) {
			String key = (String) en.nextElement();
			String value = props.getProperty(key);
			if (null != key && null != value)
				EnvVar.curEnv().setVar(key, value);
		}
	}
}
