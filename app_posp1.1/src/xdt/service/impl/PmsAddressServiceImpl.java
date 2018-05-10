package xdt.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import xdt.common.RetAppMessage;
import xdt.dao.IPmsAddressDao;
import xdt.dto.AddAddressResponseDTO;
import xdt.dto.PageViewRequestDTO;
import xdt.dto.QueryAddressListResponseDTO;
import xdt.model.PmsAddress;
import xdt.model.ResultInfo;
import xdt.model.SessionInfo;
import xdt.service.IPmsAddressService;
import xdt.service.IPublicTradeVerifyService;
import xdt.util.Constants;
import xdt.util.PageView;
import xdt.util.TradeTypeEnum;

@Service("pmsAddressService")
public class PmsAddressServiceImpl extends BaseServiceImpl implements
		IPmsAddressService{

    @Resource
    private IPmsAddressDao iPmsAddressDao;
    @Resource
	private IPublicTradeVerifyService publicTradeVerifyService;// 校验业务,金额,支付方式的限制

	private Logger logger = Logger.getLogger(PmsAddressServiceImpl.class);

	@Override
	public String queryAddressList(HttpSession session) throws Exception {
		logger.info("收货地址列表查看");
		String message = INITIALIZEMESSAGE;
		QueryAddressListResponseDTO responseData = new QueryAddressListResponseDTO();
		
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
		
		List<PmsAddress> pmsAddressList = null;
		
		if(null!=sessionInfo){
			String oAgentNo = sessionInfo.getoAgentNo();
			String mercId = sessionInfo.getMercId();
			
	        if(StringUtils.isBlank(oAgentNo) || StringUtils.isBlank(mercId)){
	            responseData.setRetCode(1);
	            responseData.setRetMessage("参数错误");
	            String jsonString = createJsonString(responseData);
	            return jsonString;
	        }
			
			//校验欧单的模块
			ResultInfo resultInfo = publicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.shop,oAgentNo);
	        if(!resultInfo.getErrCode().equals("0")){
	        	responseData.setRetCode(1);
	        	responseData.setRetMessage(resultInfo.getMsg());
	        	String jsonString = createJsonString(resultInfo);
	        	
	        	logger.info("O单业务受限，oAagentNo:"+oAgentNo+",tradeType:"+TradeTypeEnum.shop.getTypeName()+",msg:"+resultInfo.getMsg());
	            return jsonString;
	        }else{
	        	//校验商户的模块
	        	resultInfo = publicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.shop,mercId);
	            if(!resultInfo.getErrCode().equals("0")){
	            	responseData.setRetCode(1);
	            	responseData.setRetMessage(resultInfo.getMsg());
	            	String jsonString = createJsonString(resultInfo);
	            	
	            	logger.info("商户业务受限，mercId:"+mercId+",tradeType:"+TradeTypeEnum.shop.getTypeName()+",msg:"+resultInfo.getMsg());
	                return jsonString;
	            }
	        }
		
	        PmsAddress pmsAddress = new PmsAddress();
	        pmsAddress.setMerchantNo(mercId);
	        pmsAddressList = iPmsAddressDao.searchList(pmsAddress);
	        
			message = SUCCESSMESSAGE;
		}else{
			message = RetAppMessage.SESSIONINVALIDATION;
		}

		// 解析要返回的信息
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		if (retMessage.equals("initialize")) {
			retMessage = "系统初始化";
		} else if (retMessage.equals("dataParsing")) {
			retMessage = "数据解析错误";
		} else if (retMessage.equals("success")) {
			retMessage = "查询成功";
		} else if (retMessage.equals("fail")) {
			retMessage = "查询失败";
		} else if (retMessage.equals("sessionInvalidation")) {
			retMessage = "会话失效，请重新登录";
		}

		responseData.setRetCode(retCode);
		responseData.setRetMessage(retMessage);
		responseData.setPmsAddressList(pmsAddressList);
		String jsonString = createJsonString(responseData);
		logger.info("[app_rsp]" + createJson(responseData));
		return jsonString;
	}

	@Override
	public String queryAddressListException() throws Exception {
		QueryAddressListResponseDTO responseData = new QueryAddressListResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		responseData.setPmsAddressList(null);

		logger.info("[app_rsp]" + createJson(responseData));

		return createJsonString(responseData);
	}

	@Override
	public String addAddress(String requestData, HttpSession session, HttpServletRequest request)
			throws Exception {
		logger.info("增加收货地址");
		String message = INITIALIZEMESSAGE;
		AddAddressResponseDTO responseData = new AddAddressResponseDTO();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
		Date date = new Date();
		
		if(null!=sessionInfo){
			String oAgentNo = sessionInfo.getoAgentNo();
			String mercId = sessionInfo.getMercId();
			String mobilephone = sessionInfo.getMobilephone();
			
			if(StringUtils.isBlank(oAgentNo) || StringUtils.isBlank(mercId)){
	            responseData.setRetCode(1);
	            responseData.setRetMessage("参数错误");
	            String jsonString = createJsonString(responseData);
	            return jsonString;
	        }
			
			//校验欧单的模块
			ResultInfo resultInfo = publicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.shop,oAgentNo);
	        if(!resultInfo.getErrCode().equals("0")){
	        	responseData.setRetCode(1);
	        	responseData.setRetMessage(resultInfo.getMsg());
	        	String jsonString = createJsonString(resultInfo);
	        	
	        	logger.info("O单业务受限，oAagentNo:"+oAgentNo+",tradeType:"+TradeTypeEnum.shop.getTypeName()+",msg:"+resultInfo.getMsg());
	            return jsonString;
	        }else{
	        	//校验商户的模块
	        	resultInfo = publicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.shop,mercId);
	            if(!resultInfo.getErrCode().equals("0")){
	            	responseData.setRetCode(1);
	            	responseData.setRetMessage(resultInfo.getMsg());
	            	String jsonString = createJsonString(resultInfo);
	            	
	            	logger.info("商户业务受限，mercId:"+mercId+",tradeType:"+TradeTypeEnum.shop.getTypeName()+",msg:"+resultInfo.getMsg());
	                return jsonString;
	            }
	        }
			
	        PmsAddress pmsAddress = (PmsAddress) parseJsonString(requestData, PmsAddress.class);// 解析

			if (pmsAddress != null) {
				PmsAddress p = new PmsAddress();
				p.setMerchantNo(mercId);
				p.setDefaultFlag("1");
				iPmsAddressDao.update(p);
				
				pmsAddress.setAddressId(getPrimarykey(Constants.PMS_ADDRESS));
				pmsAddress.setMerchantNo(mercId);
				pmsAddress.setDefaultFlag("0");
				pmsAddress.setStatus("0");
				pmsAddress.setCreatePeople(mercId);
				pmsAddress.setCreateTime(date);
				pmsAddress.setUpdatePeople(mercId);
				pmsAddress.setUpdateTime(date);
		        int insert = iPmsAddressDao.insert(pmsAddress);
		        if(insert == 1){
		        	message = SUCCESSMESSAGE;
		        }else{
		        	message = FAILMESSAGE;
		        }
			}else {
				message = DATAPARSINGMESSAGE;// 数据解析错误
				insertAppLogs(mobilephone, "", "1402");
			}
		}else{
			message = RetAppMessage.SESSIONINVALIDATION;
		}

		// 解析要返回的信息
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		if (retMessage.equals("initialize")) {
			retMessage = "系统初始化";
		} else if (retMessage.equals("dataParsing")) {
			retMessage = "数据解析错误";
		} else if (retMessage.equals("success")) {
			retMessage = "添加成功";
		} else if (retMessage.equals("fail")) {
			retMessage = "添加失败";
		} else if (retMessage.equals("sessionInvalidation")) {
			retMessage = "会话失效，请重新登录";
		}

		responseData.setRetCode(retCode);
		responseData.setRetMessage(retMessage);
		String jsonString = createJsonString(responseData);
		logger.info("[app_rsp]" + createJson(responseData));
		return jsonString;
	}

	@Override
	public String addAddressException() throws Exception {
		AddAddressResponseDTO responseData = new AddAddressResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");

		logger.info("[app_rsp]" + createJson(responseData));

		return createJsonString(responseData);
	}

	@Override
	public String delAddress(String requestData, HttpSession session,
			HttpServletRequest request) throws Exception {
		logger.info("删除收货地址");
		String message = INITIALIZEMESSAGE;
		AddAddressResponseDTO responseData = new AddAddressResponseDTO();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
		Date date = new Date();
		
		if(null!=sessionInfo){
			String oAgentNo = sessionInfo.getoAgentNo();
			String mercId = sessionInfo.getMercId();
			String mobilephone = sessionInfo.getMobilephone();
			
			if(StringUtils.isBlank(oAgentNo) || StringUtils.isBlank(mercId)){
	            responseData.setRetCode(1);
	            responseData.setRetMessage("参数错误");
	            String jsonString = createJsonString(responseData);
	            return jsonString;
	        }
			
			//校验欧单的模块
			ResultInfo resultInfo = publicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.shop,oAgentNo);
	        if(!resultInfo.getErrCode().equals("0")){
	        	responseData.setRetCode(1);
	        	responseData.setRetMessage(resultInfo.getMsg());
	        	String jsonString = createJsonString(resultInfo);
	        	
	        	logger.info("O单业务受限，oAagentNo:"+oAgentNo+",tradeType:"+TradeTypeEnum.shop.getTypeName()+",msg:"+resultInfo.getMsg());
	            return jsonString;
	        }else{
	        	//校验商户的模块
	        	resultInfo = publicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.shop,mercId);
	            if(!resultInfo.getErrCode().equals("0")){
	            	responseData.setRetCode(1);
	            	responseData.setRetMessage(resultInfo.getMsg());
	            	String jsonString = createJsonString(resultInfo);
	            	
	            	logger.info("商户业务受限，mercId:"+mercId+",tradeType:"+TradeTypeEnum.shop.getTypeName()+",msg:"+resultInfo.getMsg());
	                return jsonString;
	            }
	        }
			
	        PmsAddress pmsAddress = (PmsAddress) parseJsonString(requestData, PmsAddress.class);// 解析

			if (pmsAddress != null) {
				pmsAddress = iPmsAddressDao.searchById(pmsAddress.getAddressId());
				
				pmsAddress.setStatus("1");
				pmsAddress.setUpdatePeople(mercId);
				pmsAddress.setUpdateTime(date);
		        int update = iPmsAddressDao.updateById(pmsAddress);
		        if(update == 1){
		        	message = SUCCESSMESSAGE;
		        }else{
		        	message = FAILMESSAGE;
		        }
			}else {
				message = DATAPARSINGMESSAGE;// 数据解析错误
				insertAppLogs(mobilephone, "", "1402");
			}
		}else{
			message = RetAppMessage.SESSIONINVALIDATION;
		}

		// 解析要返回的信息
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		if (retMessage.equals("initialize")) {
			retMessage = "系统初始化";
		} else if (retMessage.equals("dataParsing")) {
			retMessage = "数据解析错误";
		} else if (retMessage.equals("success")) {
			retMessage = "删除成功";
		} else if (retMessage.equals("fail")) {
			retMessage = "删除失败";
		} else if (retMessage.equals("sessionInvalidation")) {
			retMessage = "会话失效，请重新登录";
		}

		responseData.setRetCode(retCode);
		responseData.setRetMessage(retMessage);
		String jsonString = createJsonString(responseData);
		logger.info("[app_rsp]" + createJson(responseData));
		return jsonString;
	}

	@Override
	public String delAddressException() throws Exception {
		AddAddressResponseDTO responseData = new AddAddressResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");

		logger.info("[app_rsp]" + createJson(responseData));

		return createJsonString(responseData);
	}

}