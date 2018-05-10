package xdt.dto.qianlong;

import java.util.Map;

import org.apache.log4j.Logger;

import xdt.dao.IPmsAppTransInfoDao;
import xdt.model.PmsAppTransInfo;
import xdt.service.impl.PufaServiceImpl;
import xdt.service.impl.QLpayServiceImpl;

public class QLThread extends Thread {

	public static final Logger logger=Logger.getLogger(QLThread.class);

	public QLpayServiceImpl qLpayServiceImpl;
	
	public QueryRequestDto query;
	
	
	
	public QLThread(QLpayServiceImpl qLpayServiceImpl, QueryRequestDto query) {
		super();
		this.qLpayServiceImpl = qLpayServiceImpl;
		this.query = query;
	}



	@Override
	public void run() {
			
			try{
			Thread.sleep(30000);
			for(int i=1;i<=60;i++){
				Map<String,String> result=qLpayServiceImpl.updateQuery(query);
				//Map<String,String> result1=qLpayServiceImpl.updateQuery(query);
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
