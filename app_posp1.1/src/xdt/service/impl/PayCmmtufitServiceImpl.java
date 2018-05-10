package xdt.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import xdt.common.RetAppMessage;
import xdt.dao.IPayCmmtufitDao;
import xdt.dao.IPmsSupportBankInfoDao;
import xdt.dto.SearchBankCardInfoRequestDTO;
import xdt.dto.SearchBankCardInfoResponseDTO;
import xdt.dto.SearchSupportBankListRequestDTO;
import xdt.dto.SearchSupportBankListResponseDTO;
import xdt.model.PayCmmtufit;
import xdt.model.PmsSupportBankInfo;
import xdt.model.SessionInfo;
import xdt.service.IPayCmmtufitService;

@Service("payCmmtufitService")
public class PayCmmtufitServiceImpl extends BaseServiceImpl implements IPayCmmtufitService{
	@Resource
	private IPayCmmtufitDao payCmmtufitDao;//银行卡库服务层
	@Resource 
	private IPmsSupportBankInfoDao pmsSupportBankInfoDao;//系统支持银行卡服务层
	/**
	 * 根据前6位数字检索银行卡信息
	 */
	@Override
	public String searchCardListByBeforeSix(String beforeSixCardNumber,HttpSession session) throws Exception {
		String message = INITIALIZEMESSAGE;
		String bankName = ""; //银行名称
		String cardName = ""; //银行卡名称
		//获取session信息
		SessionInfo sessionInfo = (SessionInfo)session.getAttribute(SessionInfo.SESSIONINFO);
		if(null!=sessionInfo){
			//解析银行卡前6位数字
			Object obj = parseJsonString(beforeSixCardNumber, SearchBankCardInfoRequestDTO.class);
			if(!obj.equals(DATAPARSINGMESSAGE)){
				SearchBankCardInfoRequestDTO bankInfo = (SearchBankCardInfoRequestDTO)obj;
				String beforeSix = bankInfo.getBankCardBeforeSix();
				if(isNotEmptyValidate(beforeSix)){
					List<PayCmmtufit> list = payCmmtufitDao.searchCardInfoByBeforeSix(beforeSix+"%");
					if(null!=list && list.size()>=1){
						PayCmmtufit payCmmtufit = list.get(0);
						bankName = payCmmtufit.getBnkName();
						cardName = payCmmtufit.getCrdNm();
						message = SUCCESSMESSAGE;
					}else{
						message = FAILMESSAGE;
					}
				}else{
					message = EMPTYMESSAGE;
				}
			}else{
				message = DATAPARSINGMESSAGE;
			}
		}else{
			message = RetAppMessage.SESSIONINVALIDATION;
	    }
		//解析要返回的信息
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		if(retMessage.equals("initialize")){
			retMessage = "服务器异常";
		}else if(retMessage.equals("sessionInvalidation")){
			retMessage = "会话失效，请重新登录";
		}else if(retMessage.equals("dataParsing")){
			retMessage = "数据解析错误";
		}else if(retMessage.equals("empty")){
			retMessage = "银行卡号不能为空";
		}else if(retMessage.equals("fail")){
			retMessage = "不支持的银行卡";
		}else if(retMessage.equals("success")){
			retMessage = "查询成功";
		}
		SearchBankCardInfoResponseDTO responseData = new SearchBankCardInfoResponseDTO();
		responseData.setBankName(bankName);
		responseData.setCardName(cardName);
		responseData.setRetCode(retCode);
		responseData.setRetMessage(retMessage);
		String jsonString = createJsonString(responseData);
		return jsonString;
	}

	/**
	 * 检索银行列表
	 */
	@Override
	public String searchBankList(String pageInfo,HttpSession session) throws Exception {
		String message = INITIALIZEMESSAGE;
		SessionInfo sessionInfo = (SessionInfo)session.getAttribute(SessionInfo.SESSIONINFO);
		List<PmsSupportBankInfo> list = new ArrayList<PmsSupportBankInfo>();
		if(null!=sessionInfo){
			Object obj = parseJsonString(pageInfo, SearchSupportBankListRequestDTO.class);
			if(!obj.equals(DATAPARSINGMESSAGE)){
				SearchSupportBankListRequestDTO bankInfo = (SearchSupportBankListRequestDTO)obj;
				String pageSize = bankInfo.getPageSize();
				String pageNum = bankInfo.getPageNum();
				HashMap<String,String> map = new HashMap<String,String>();
				map.put("pageSize", pageSize);
				map.put("pageNum", pageNum);
				list = pmsSupportBankInfoDao.selectBankList(map);
				if(list.size()>=0){
					message = SUCCESSMESSAGE;
				}else{
					message = FAILMESSAGE;
				}
			}else{
				message = DATAPARSINGMESSAGE;
			}
		}else{
			message = RetAppMessage.SESSIONINVALIDATION;
		}
		//解析要返回的信息
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		if(retMessage.equals("initialize")){
			retMessage = "系统初始化";
		}else if(retMessage.equals("sessionInvalidation")){
			retMessage = "会话失效，请重新登录";
		}else if(retMessage.equals("dataParsing")){
			retMessage = "数据解析错误";
		}else if(retMessage.equals("fail")){
			retMessage = "查询失败";
		}else if(retMessage.equals("success")){
			retMessage = "查询成功";
		}
		SearchSupportBankListResponseDTO responseData = new SearchSupportBankListResponseDTO();
		responseData.setList(list);
		responseData.setRetCode(retCode);
		responseData.setRetMessage(retMessage);
		String jsonString = createJsonString(responseData);
		return jsonString;
	}

	/**
	 * 检索银行列表异常
	 */
	@Override
	public String searchBankListException(HttpSession session) throws Exception {
		SearchSupportBankListResponseDTO responseData = new SearchSupportBankListResponseDTO();
		responseData.setList(null);
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		insertAppLogs(((SessionInfo)session.getAttribute(SessionInfo.SESSIONINFO)).getMobilephone(),"","2033");
		return createJsonString(responseData);
	}
}