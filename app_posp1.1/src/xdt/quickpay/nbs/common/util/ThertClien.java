package xdt.quickpay.nbs.common.util;

import xdt.dto.nbs.webpay.WechatWebPayResponse;
import xdt.model.OriginalOrderInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.util.HttpURLConection;

public class ThertClien extends Thread {
	
	private WechatWebPayResponse wechatWebPayResponse;
	
	private OriginalOrderInfo originalOrderInfo;
	
	private Bean2QueryStrUtil bean2QueryStrUtil;
	
	
	public ThertClien(WechatWebPayResponse wechatWebPayResponse,
			OriginalOrderInfo originalOrderInfo,
			Bean2QueryStrUtil bean2QueryStrUtil) {
		super();
		this.wechatWebPayResponse = wechatWebPayResponse;
		this.originalOrderInfo = originalOrderInfo;
		this.bean2QueryStrUtil = bean2QueryStrUtil;
	}


	@Override
	public void run() {
		try {
			sleep(2000);
			for (int i = 0; i < 10; i++) {
				String result1=HttpURLConection.httpURLConnectionPOST(originalOrderInfo.getBgUrl(), bean2QueryStrUtil.bean2QueryStr(wechatWebPayResponse));
				if("SUCCESS".equals(result1)){
					break;
				}
				sleep(5000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
