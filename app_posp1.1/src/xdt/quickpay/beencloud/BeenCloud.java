package xdt.quickpay.beencloud;

import org.apache.log4j.Logger;

import cn.beecloud.BeeCloud;

/**
 * 
 * @Description 初始化BeenCloud
 * @date 2016年11月20日 下午1:56:12
 * @version V1.3.1
 */
public class BeenCloud {
	
	private Logger logger=Logger.getLogger(BeenCloud.class);

	private String appid;// 商户在Beecloud后台注册的app id
	private String testSecret;// 测试用于支付、查询
	private String appSecret;// 用于支付、查询
	private String masterSecret;// 用于退款、批量打款

	public void init() {
		logger.info("初始化BeenCloud");
		BeeCloud.registerApp(appid,testSecret,appSecret,masterSecret);
//		BeeCloud.registerApp(appid,testSecret,"",masterSecret);
//		BeeCloud.setSandbox(true);
	}
	

	public BeenCloud() {
		super();
	}


	public BeenCloud(String appid, String testSecret, String appSecret,
			String masterSecret) {
		super();
		this.appid = appid;
		this.testSecret = testSecret;
		this.appSecret = appSecret;
		this.masterSecret = masterSecret;
	}


	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getTestSecret() {
		return testSecret;
	}

	public void setTestSecret(String testSecret) {
		this.testSecret = testSecret;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getMasterSecret() {
		return masterSecret;
	}

	public void setMasterSecret(String masterSecret) {
		this.masterSecret = masterSecret;
	}
	
}
