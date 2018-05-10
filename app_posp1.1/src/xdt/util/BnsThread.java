package xdt.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdt.dto.nbs.orderquery.WechatOrderQueryRequest;
import xdt.dto.nbs.orderquery.WechatOrderQueryResponse;
import xdt.dto.weixin.QueryRequestDto;
import xdt.service.impl.WXQrCodeServiceImpl;
import xdt.service.impl.WechatServiceImpl;

public class BnsThread extends Thread{
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public WechatServiceImpl weServce;
	
	public WechatOrderQueryRequest query;
	
	
	
	public BnsThread(WechatServiceImpl weServce, WechatOrderQueryRequest query) {
		super();
		this. weServce =  weServce;
		this.query = query;
	}



	@Override
	public void run() {
			
			try{
			Thread.sleep(30000);
			for(int i=1;i<=500;i++){
				WechatOrderQueryResponse result=weServce.doOrderQuery(query,log);
				if(result!=null){		
						break;
				}
				Thread.sleep(5000);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
