package xdt.service.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import xdt.common.RetAppMessage;
import xdt.dao.IPmsGoodsDao;
import xdt.dto.PageViewRequestDTO;
import xdt.dto.QueryGoodsListResponseDTO;
import xdt.model.PmsGoods;
import xdt.model.ResultInfo;
import xdt.model.SessionInfo;
import xdt.service.IPmsGoodsService;
import xdt.service.IPublicTradeVerifyService;
import xdt.util.PageView;
import xdt.util.TradeTypeEnum;

@Service("pmsGoodsService")
public class PmsGoodsServiceImpl extends BaseServiceImpl implements
		IPmsGoodsService {

    @Resource
    private IPmsGoodsDao iPmsGoodsDao;
    @Resource
	private IPublicTradeVerifyService publicTradeVerifyService;// 校验业务,金额,支付方式的限制

	private Logger logger = Logger.getLogger(PmsGoodsServiceImpl.class);

	@Override
	public String queryGoodsList(String requestData, HttpSession session) throws Exception {
		logger.info("商品列表查看");
		String message = INITIALIZEMESSAGE;
		QueryGoodsListResponseDTO responseData = new QueryGoodsListResponseDTO();
		PageView pageView = new PageView();
		
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
		
		List<PmsGoods> pmsGoods = null;
		
		if(null!=sessionInfo){
			String oAgentNo = sessionInfo.getoAgentNo();
			String mercId = sessionInfo.getMercId();
			String mobilephone = sessionInfo.getMobilephone();
			
			PageViewRequestDTO requestDTO = (PageViewRequestDTO) parseJsonString(requestData, PageViewRequestDTO.class);// 解析
			
			if (requestDTO != null) {
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
		        
				pmsGoods = iPmsGoodsDao.searchList(new PmsGoods());
				
				Integer pageNum = requestDTO.getPageNum();//当前页
	            if (pageNum == null || pageNum <= 0) {
	                pageNum = 1;
	            }
	            
	            Integer pageSize = requestDTO.getPageSize();//每页显示的记录数量(默认每页10条)
	            if (pageSize == null || pageSize <= 0) {
	            	pageSize = PageView.PAGEZISE;
	            }
	            
	            Integer count = pmsGoods.size();
	            
	            pageView.setPageNum(pageNum);
	            pageView.setPageSize(pageSize);
	            pageView.setRecordCount(count);
	            int pogeCount = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
                pageView.setPageCount(pogeCount);
	            
	            Integer fromIndex = (pageNum - 1) * pageSize; 
	            if (fromIndex > count - 1){
	            	pageView.setRecordList(null);
	            }else{
		            Integer toIndex = fromIndex + pageSize; 
		            if (toIndex > count - 1){
		            	toIndex = count;
		            }
		            
		            List<PmsGoods> selectList = pmsGoods.subList(fromIndex, toIndex);
		            
		            String url = null;
		    		
		    		InputStream in = this.getClass().getResourceAsStream("/common.properties");
		            BufferedReader bf;
		    		try {
		    			bf = new BufferedReader(new InputStreamReader(in,"utf-8"));
		    			Properties p = new Properties();
		    			p.load(bf);
		    			url =  p.getProperty("picPreUrl");
		    		} catch (Exception e) {
		    			e.printStackTrace();
		    		}
		    		
		    		for (PmsGoods p : selectList) {
		    			p.setGoodsImageUrl(url + p.getGoodsImageUrl());
		    		}
		    		
		    		pageView.setRecordList(selectList);
	            }
                
				message = SUCCESSMESSAGE;
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
			retMessage = "查询成功";
		} else if (retMessage.equals("fail")) {
			retMessage = "查询失败";
		} else if (retMessage.equals("sessionInvalidation")) {
			retMessage = "会话失效，请重新登录";
		}

		responseData.setRetCode(retCode);
		responseData.setRetMessage(retMessage);
		responseData.setPageView(pageView);
		String jsonString = createJsonString(responseData);
		logger.info("[app_rsp]" + createJson(responseData));
		return jsonString;
	}

	@Override
	public String queryGoodsListException() throws Exception {
		QueryGoodsListResponseDTO responseData = new QueryGoodsListResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		responseData.setPageView(null);

		logger.info("[app_rsp]" + createJson(responseData));

		return createJsonString(responseData);
	}

}