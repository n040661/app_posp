package xdt.dto.sxf;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.codehaus.jackson.map.ObjectMapper;

import xdt.dto.BaseUtil;
import xdt.service.ISxfService;
import xdt.util.HttpURLConection;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.RequestUtils;

public class SxfThreads extends Thread {

	private ISxfService sxfServiceImpl;
	
	private PayRequsest payRequsest;

	public SxfThreads(ISxfService sxfServiceImpl, PayRequsest payRequsest) {
		super();
		this.sxfServiceImpl = sxfServiceImpl;
		this.payRequsest = payRequsest;
	}

	@Override
	public void run() {
		try {
			sleep(2000);
			
			for (int i = 0; i < 50; i++) {
				Map<String, String> result =new HashMap<>();
				TreeMap<String, String> paramsMap=new TreeMap<>();
				paramsMap.put("spid","10035036642");
				paramsMap.put("sp_billno",payRequsest.getReqId());
				paramsMap.put("type","1");
				String key = "477c5d12a33e44c8b4749f2e22c06a52";
				String paramSrc = RequestUtils.getParamSrc(paramsMap);
				String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
				String content =paramSrc+"&sign="+md5;
				String str =HttpURLConection.httpURLConnectionPOST(BaseUtil.url+"/TFBController/select.action", content);
				ObjectMapper om = new ObjectMapper();
				result = om.readValue(str, Map.class);
				if("00".equals(result.get("retcode"))){
					if("00".equals(result.get("status"))){
						sxfServiceImpl.UpdateDaifu(payRequsest.getReqId(), "00");
						return;
					}else if("01".equals(result.get("status"))){
						sxfServiceImpl.UpdateDaifu(payRequsest.getReqId(), "01");
						return;
					}else if("02".equals(result.get("status"))){
						sxfServiceImpl.UpdateDaifu(payRequsest.getReqId(), "02");
						return;
					}
				}
				sleep(50000);
			}

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			}
	
	
	
}


