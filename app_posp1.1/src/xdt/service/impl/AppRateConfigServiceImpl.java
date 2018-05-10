package xdt.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import xdt.dao.IAppRateConfigDao;
import xdt.dao.IPmsAgentInfoDao;
import xdt.dto.AppRateConfigQueryResponseDTO;
import xdt.model.AppRateConfig;
import xdt.model.PmsAgentInfo;
import xdt.model.SessionInfo;
import xdt.service.IAppRateConfigService;

@Service("appRateConfigService")
public class AppRateConfigServiceImpl extends BaseServiceImpl implements
		IAppRateConfigService {

	@Resource
	private IAppRateConfigDao appRateConfigDao; // 费率服务层
	@Resource
    private IPmsAgentInfoDao pmsAgentInfoDao;//代理商
	private Logger logger = Logger.getLogger(AppRateConfigServiceImpl.class);

	/**
	 * 费率查询
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public String appRateConfigQuery(HttpSession session) throws Exception {
		logger.info("费率查询");
		
		SessionInfo sessionInfo = (SessionInfo)session.getAttribute(SessionInfo.SESSIONINFO);

		String oAgentNo = sessionInfo.getoAgentNo();
		
		String clearType = null;
		String lowestrate = null;
		  
		PmsAgentInfo pmsAgentInfo = new PmsAgentInfo();
		pmsAgentInfo.setoAgentNo(oAgentNo);
		pmsAgentInfo.setAgentLevel("0");
		List<PmsAgentInfo> selectList2 = pmsAgentInfoDao.searchList(pmsAgentInfo);
		if(selectList2 != null && selectList2.size() > 0){
		  pmsAgentInfo = selectList2.get(0);
		  clearType = pmsAgentInfo.getClearType();
		  if("2".equals(clearType)){
			  String agentNumber = session.getAttribute("agentNumber").toString();
			  pmsAgentInfo = new PmsAgentInfo();
			  pmsAgentInfo.setAgentNumber(agentNumber);
			  List<PmsAgentInfo> selectList3 = pmsAgentInfoDao.searchList(pmsAgentInfo);
			  if(selectList3 != null && selectList3.size() > 0){
				  pmsAgentInfo = selectList3.get(0);
				  lowestrate = pmsAgentInfo.getLowestRate();
			  }
		  }
		}
		
		List<AppRateConfig> list = new ArrayList<AppRateConfig>();
		String message = INITIALIZEMESSAGE;

		AppRateConfig appRateConfig = new AppRateConfig();
		appRateConfig.setIsThirdpart("0");
		appRateConfig.setoAgentNo(sessionInfo.getoAgentNo());
		list = appRateConfigDao.searchList(appRateConfig);
		
		if (list != null && list.size() > 0) {
			if("2".equals(clearType)){
				  int size = list.size();
				  for(int i=size-1;i>=0;i--){
					  AppRateConfig a = (AppRateConfig) list.get(i);
					  String topPoundage = a.getTopPoundage();
						String[] split = lowestrate.split("-");
						if(split.length > 1){
							String lowestrates = lowestrate.split("-")[1];
							if(topPoundage !=null && !"".equals(topPoundage)){
								BigDecimal topPoundage1 = new BigDecimal(topPoundage);
								BigDecimal lowestrate1 = new BigDecimal(lowestrates);
								if(topPoundage1.compareTo(lowestrate1)==-1){
									list.remove(i);
								}
							}else{
								list = null;
							}
						}else{
							list = null;
						}
				  }
			  }
			for(AppRateConfig a : list){
				String isTop = a.getIsTop();
				if("1".equals(isTop)){
					a.setRate(new BigDecimal(a.getRate()).multiply(new BigDecimal(100)).stripTrailingZeros()+"%-"+new BigDecimal(a.getTopPoundage()).divide((new BigDecimal(100)))+"封顶");
				}else{
					a.setRate(new BigDecimal(a.getRate()).multiply(new BigDecimal(100)).stripTrailingZeros()+"%");
				}
			}
			message = SUCCESSMESSAGE;
		} else {
			message = FAILMESSAGE;
		}

		// 解析要返回的信息
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		if (retMessage.equals("initialize")) {
			retMessage = "系统初始化";
		} else if (retMessage.equals("success")) {
			retMessage = "查询成功";
		} else if (retMessage.equals("fail")) {
			retMessage = "查无结果";
		}

		AppRateConfigQueryResponseDTO responseData = new AppRateConfigQueryResponseDTO();
		responseData.setRetCode(retCode);
		responseData.setRetMessage(retMessage);
		responseData.setList(list);
		String jsonString = createJsonString(responseData);
		logger.info("[app_rsp]" + createJson(responseData));
		return jsonString;
	}

	/**
	 * 费率查询异常
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public String appRateConfigQueryException() throws Exception {
		AppRateConfigQueryResponseDTO responseData = new AppRateConfigQueryResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		responseData.setList(null);

		logger.info("[app_rsp]" + createJson(responseData));

		return createJsonString(responseData);
	}

}