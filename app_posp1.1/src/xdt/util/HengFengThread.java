package xdt.util;

import java.util.Map;

import xdt.dto.weixin.QueryRequestDto;
import xdt.service.impl.WXQrCodeServiceImpl;


public class HengFengThread extends Thread {
	
	public WXQrCodeServiceImpl wxServce;
	
	public QueryRequestDto query;
	
	
	
	public HengFengThread(WXQrCodeServiceImpl wxServce, QueryRequestDto query) {
		super();
		this.wxServce = wxServce;
		this.query = query;
	}



	@Override
	public void run() {
			
			try{
			Thread.sleep(30000);
			for(int i=1;i<=500;i++){
				Map<String,String> result=wxServce.updateQuery(query);
				if(result!=null){
					if("0000".equals(result.get("respCode"))){
						break;
					}
				}
				Thread.sleep(5000);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
