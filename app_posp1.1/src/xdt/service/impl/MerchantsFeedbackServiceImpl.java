package xdt.service.impl;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import xdt.dao.IMerchantsFeedbackDao;
import xdt.dto.MerchantsFeedbackRequestAndResponseDTO;
import xdt.model.MerchantsFeedback;
import xdt.model.SessionInfo;
import xdt.service.IMerchantsFeedbackService;
import xdt.util.OrderStatusEnum;
import xdt.util.UtilDate;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
/**
 * 商户反馈信息
 * @author wumeng
 *
 */
@Service
public class  MerchantsFeedbackServiceImpl extends BaseServiceImpl implements IMerchantsFeedbackService{
	@Resource
	private IMerchantsFeedbackDao merchantsFeedbackDao;
	private Logger logger = Logger.getLogger(MerchantsFeedbackServiceImpl.class);
	/**
	 * 商户意见添加
	 */
	public String merchantFeedback(String param,SessionInfo sessionInfo) throws Exception {
		logger.info("调用商户意见添加成功开始接收参数："+param+"；结束时间："+ UtilDate.getDateFormatter());
		String result = "";
		MerchantsFeedbackRequestAndResponseDTO merchantsFeedbackRequestAndResponseDTO = (MerchantsFeedbackRequestAndResponseDTO)parseJsonString(param,MerchantsFeedbackRequestAndResponseDTO.class);
		if(!merchantsFeedbackRequestAndResponseDTO.equals(DATAPARSINGMESSAGE)){//判断解析数据是否成功
			
			MerchantsFeedback merchantsFeedback = new MerchantsFeedback();
			
			merchantsFeedback.setUserId(sessionInfo.getMercId());//商户ID
			merchantsFeedback.setOpinon(merchantsFeedbackRequestAndResponseDTO.getOpinon());//商户意见
			merchantsFeedback.setCreationTime(UtilDate.getDateFormatter());//创建时间
			merchantsFeedback.setUserType("1");//登录方式   目前只有一种  app 登录
			merchantsFeedback.setoAgentNo(sessionInfo.getoAgentNo());
			if(merchantsFeedbackDao.insert(merchantsFeedback)==1){
				merchantsFeedbackRequestAndResponseDTO.setRetCode("0");
				merchantsFeedbackRequestAndResponseDTO.setRetMessage("商户意见添加成功");
				merchantsFeedbackRequestAndResponseDTO.setOpinon("");
			}else{
				merchantsFeedbackRequestAndResponseDTO.setRetCode("1");
				merchantsFeedbackRequestAndResponseDTO.setRetMessage("商户意见添加失败");
				merchantsFeedbackRequestAndResponseDTO.setOpinon("");
				
			}
		}
		
		result = createJsonString(merchantsFeedbackRequestAndResponseDTO);
		
		logger.info("调用商户意见添加成功结束返回参数："+result+"；结束时间："+ UtilDate.getDateFormatter());
		return result;
	}
	
	
	/**
	 * 商户意见添加异常
	 * @throws Exception 
	 */
	@Override
    public String merchantFeedbackException(HttpSession session) throws Exception{
    	String result = "";
    	MerchantsFeedbackRequestAndResponseDTO merchantsFeedbackRequestAndResponseDTO = new MerchantsFeedbackRequestAndResponseDTO();
    	merchantsFeedbackRequestAndResponseDTO.setRetCode(OrderStatusEnum.systemErro.getStatus());
		merchantsFeedbackRequestAndResponseDTO.setRetMessage(OrderStatusEnum.systemErro.getDescription());
		result = createJsonString(merchantsFeedbackRequestAndResponseDTO);
		return result;
    }
	
	
}