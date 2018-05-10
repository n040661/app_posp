package xdt.service.impl;

import xdt.dto.TAccRateResponseDTO;
import xdt.dao.ITAccRateDao;
import xdt.dto.TAccRateResponseDTO;
import xdt.model.TAccRate;
import xdt.service.TAccRateService;
import xdt.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TAccRateServiceImpl extends BaseServiceImpl implements TAccRateService {
	@Resource
	private ITAccRateDao  pmsBusinessInfo;
	/**
	 * 费率查询
	 * @return
	 * @throws Exception
	 */
	public String queryTaccRate()throws Exception{
		//服务器返回的判断信息
		String message = INITIALIZEMESSAGE;

		List<TAccRate> list = pmsBusinessInfo.selectAccRate();
		TAccRateResponseDTO tacc=new TAccRateResponseDTO();
		if(list.size()!=0){
			message=SUCCESSMESSAGE;

			for (TAccRate taRate : list) {
				tacc.getLi().add(taRate);
			}
		}else{
			message=FAILMESSAGE;
		}
		//解析要返回的信息
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		if(retMessage.equals("initialize")){
			retMessage = "服务器异常";
		}else if(retMessage.equals("success")){
			retMessage = "获取成功";
		}else if(retMessage.equals("failure")){
			retMessage = "费率获取失败";
		}
		tacc.setRetCode(retCode);
		tacc.setRetMessage(retMessage);
		return createJsonString(tacc);
	}
	
	/**
	 * 费率查询异常
	 */
	@Override
	public String queryTaccRateException() throws Exception {
		TAccRateResponseDTO tacc=new TAccRateResponseDTO();
		tacc.setLi(null);
		tacc.setRetCode(100);
		tacc.setRetMessage("系统异常");
		return createJsonString(tacc);
	}
}
