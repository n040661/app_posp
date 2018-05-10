package xdt.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import xdt.common.RetAppMessage;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPmsTransHistoryRecordDao;
import xdt.dto.BankCardHistoryRecordListRequestDTO;
import xdt.dto.BankCardHistoryRecordListResponseDTO;
import xdt.dto.GetTransNumberOfAvailableRequestDTO;
import xdt.dto.GetTransNumberOfAvailableResponseDTO;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsTransHistoryRecord;
import xdt.model.SessionInfo;
import xdt.service.IPmsTransHistoryRecordService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service("pmsTransHistoryRecordService")
public class PmsTransHistoryRecordServiceImpl extends BaseServiceImpl implements IPmsTransHistoryRecordService {
	@Resource
	private IPmsAppTransInfoDao pmsAppTransInfoDao;//交易流水服务层
	@Resource
	private IPmsTransHistoryRecordDao pmsTransHistoryRecordDao; //交易历史记录服务层
	private Logger logger = Logger.getLogger(PmsTransHistoryRecordServiceImpl.class);
	/**
	 * 检索交易历史记录列表
	 */
	@Override
	public String searchTransHistoryRecordList(String pageInfo, HttpSession session)throws Exception {
		HashMap<String,Object> map = validateNullAndParseData(session, pageInfo, BankCardHistoryRecordListRequestDTO.class);
		List<PmsTransHistoryRecord> list = new ArrayList<PmsTransHistoryRecord>();
		String message = map.get("message").toString();
        //欧单编号
        String oAgentNo = "";
		if(message.equals(RetAppMessage.DATAANALYTICALSUCCESS)){
			BankCardHistoryRecordListRequestDTO historyRecord = (BankCardHistoryRecordListRequestDTO)map.get("obj");
			SessionInfo sessionInfo = (SessionInfo)map.get("sessionInfo");


            if(sessionInfo == null){
                BankCardHistoryRecordListResponseDTO responseData = new BankCardHistoryRecordListResponseDTO();
                responseData.setRetCode(13);
                responseData.setRetMessage("会话失效，请重新登陆");
                String jsonString = createJsonString(responseData);
                return jsonString;
            }

            oAgentNo = sessionInfo.getoAgentNo();
            //如果欧单编号为空，直接返回错误
            if(StringUtils.isBlank(oAgentNo)){
                BankCardHistoryRecordListResponseDTO responseData = new BankCardHistoryRecordListResponseDTO();
                responseData.setRetCode(1);
                responseData.setRetMessage("登录信息有误，请重新登陆");
                responseData.setList(list);
                String jsonString = createJsonString(responseData);
                return jsonString;
            }


			String mercId = sessionInfo.getMercId();
			Integer businessCode = historyRecord.getBusinessCode();
			HashMap<String,String> hashMap = new HashMap<String,String>();
			hashMap.put("mercId", mercId);
            if(1 == businessCode){
                // 信用卡
                hashMap.put("businessCode", "1");
            }else if(2 == businessCode){
               // 转账
                hashMap.put("businessCode", "2");
            }

			list = pmsTransHistoryRecordDao.searchHistoryRecord(hashMap);
			if(list.size()>=0){
				message = SUCCESSMESSAGE;
			}else{
				message = FAILMESSAGE;
			}
		}
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		retMessage = RetAppMessage.parseMessageCode(retMessage);
		BankCardHistoryRecordListResponseDTO responseData = new BankCardHistoryRecordListResponseDTO();
		responseData.setRetCode(retCode);
		responseData.setRetMessage(retMessage);
		responseData.setList(list);
		String jsonString = createJsonString(responseData);
		return jsonString;
	}

	/**
	 * 检索交易历史记录列表异常
	 */
	@Override
	public String searchTransHistoryRecordListException(HttpSession session) throws Exception {
		BankCardHistoryRecordListResponseDTO responseData = new BankCardHistoryRecordListResponseDTO();
		responseData.setList(null);
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		logger.info("[app_rsp]交易历史记录列表"+createJson(responseData));
		insertAppLogs(((SessionInfo)session.getAttribute(SessionInfo.SESSIONINFO)).getMobilephone(),"","2040");
		return createJsonString(responseData);
	}

	/**
	 * 检索交易历史记录详情
	 */
	@Override
	public String searchTransGetNumberOfAvailable(String bankCardInfo,HttpSession session,HttpServletRequest request) throws Exception {
		HashMap<String,Object> map = validateNullAndParseData(session, bankCardInfo, GetTransNumberOfAvailableRequestDTO.class);	
		String message = map.get("message").toString();
		int count = 0; //可用次数;
		BigDecimal supperTransMoney = new BigDecimal(0.00);
		BigDecimal commonTransMoney = new BigDecimal(0.00);
		if(message.equals(RetAppMessage.DATAANALYTICALSUCCESS)){
			GetTransNumberOfAvailableRequestDTO detailInfo = (GetTransNumberOfAvailableRequestDTO)map.get("obj");
			SessionInfo sessionInfo = (SessionInfo)map.get("sessionInfo");
			setSession(request.getRemoteAddr(),session.getId(),sessionInfo.getMobilephone());
			String bankCardNumber = detailInfo.getBankCardNumber();//银行卡
			String businessCode = detailInfo.getBusinessCode().toString();//业务码
			String mercId = sessionInfo.getMercId();
			String endTime = sdf.format(new Date());
			String startTime = businessCode.equals("5") ? endTime.substring(0,10)+" 00:00:00" : endTime.substring(0,8)+"01 00:00:00";
			if(isNotEmptyValidate(bankCardNumber) && isNotEmptyValidate(businessCode)){
				//根据银行卡号和日期获取银行卡交易信息
				HashMap<String,String> hashMap = new HashMap<String,String>();
				hashMap.put("mercId", mercId);
				hashMap.put("bankCardNumber", bankCardNumber);
				hashMap.put("startTime", startTime);
				hashMap.put("endTime", endTime);
				hashMap.put("businessCode",businessCode);
				List<PmsAppTransInfo> transList = pmsAppTransInfoDao.searchTransRecord(hashMap);
				if(transList.size()>=0){
					count=3-transList.size();
					//计算交易总额
					if(businessCode.equals("5")){ //提款
						for (int i = 0; i < transList.size(); i++) {
							/*if("0".equals(transList.get(i).getExtends3())){ //超级提款
								supperTransMoney = supperTransMoney.add(new BigDecimal(transList.get(i).getFactamount()));
							}
							if("1".equals(transList.get(i).getExtends3())){ //普通提款
								commonTransMoney = commonTransMoney.add(new BigDecimal(transList.get(i).getFactamount()));
							}*/
						}
					}
					message = SUCCESSMESSAGE;
				}else{
					message = FAILMESSAGE;
				}
			}else{
				insertAppLogs(sessionInfo.getMobilephone(),"","2002");
				message = EMPTYMESSAGE;
			}
		}
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		retMessage = RetAppMessage.parseMessageCode(retMessage);
		GetTransNumberOfAvailableResponseDTO responseData = new GetTransNumberOfAvailableResponseDTO();
		responseData.setCount(count+"");
		responseData.setRetCode(retCode);
		responseData.setRetMessage(retMessage);
		responseData.setSupperTransMoney(supperTransMoney.setScale(2,BigDecimal.ROUND_DOWN).toString());
		responseData.setCommonTransMoney(commonTransMoney.setScale(2,BigDecimal.ROUND_DOWN).toString());
		String jsonString = createJsonString(responseData);
		return jsonString;
	}

	/**
	 * 检索交易历史记录详情异常
	 */
	@Override
	public String searchTransGetNumberOfAvailableException(HttpSession session) throws Exception {
		GetTransNumberOfAvailableResponseDTO responseData = new GetTransNumberOfAvailableResponseDTO();
		responseData.setCount("0");
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		logger.info("[app_rsp]交易历史记录详情"+createJson(responseData));
		insertAppLogs(((SessionInfo)session.getAttribute(SessionInfo.SESSIONINFO)).getMobilephone(),"","2041");
		return createJsonString(responseData);
	}
}