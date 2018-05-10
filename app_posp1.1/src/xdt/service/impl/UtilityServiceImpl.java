package xdt.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.springframework.stereotype.Service;
import xdt.baidu.BDUtil;
import xdt.common.RetAppMessage;
import xdt.dao.*;
import xdt.dto.*;
import xdt.model.*;
import xdt.offi.OffiPay;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.IUtilityService;
import xdt.servlet.AppPospContext;
import xdt.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("UtilityService")
public class UtilityServiceImpl extends BaseServiceImpl implements
		IUtilityService {

	private Logger logger = Logger.getLogger(UtilityServiceImpl.class);
	@Resource
	private IViewKyChannelInfoDao channelInfoDao; // 通道信息层
	@Resource
	private IPmsAppTransInfoDao pmsAppTransInfoDao;// 订单处理
	@Resource
	private IAppOrderDetailDao appOrderDetailDao; // 详细信息层
	@Resource
	private IMerchantMineDao merchantMineDao;
	@Resource
	private OffiPay offiPay; // 欧飞
	@Resource
	private IPayCmmtufitDao payCmmtufitDao; // 银行卡信息层
	@Resource
	private IAppRateConfigDao appRateConfigDao;
	@Resource
	private IPublicTradeVerifyService publicTradeVerifyService;// 校验业务,金额,支付方式的限制

	/**
	 * 省份查询
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public String getProvinceList(HttpSession session) throws Exception {
		logger.info("省份查询");
		String message = INITIALIZEMESSAGE;
		GetProvinceListResponseDTO responseData = new GetProvinceListResponseDTO();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
		
		String oAgentNo = sessionInfo.getoAgentNo();
		String mercId = sessionInfo.getMercId();
        if(StringUtils.isBlank(oAgentNo) || StringUtils.isBlank(mercId)){
            responseData.setRetCode(1);
            responseData.setRetMessage("参数错误");
            String jsonString = createJsonString(responseData);
            return jsonString;
        }
		
		//校验欧单的模块
		ResultInfo resultInfo = publicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.utility,oAgentNo);
        if(!resultInfo.getErrCode().equals("0")){
        	responseData.setRetCode(1);
        	responseData.setRetMessage(resultInfo.getMsg());
        	String jsonString = createJsonString(resultInfo);
        	
        	logger.info("O单业务受限，oAagentNo:"+oAgentNo+",tradeType:"+TradeTypeEnum.utility.getTypeName()+",msg:"+resultInfo.getMsg());
            return jsonString;
        }else{
        	//校验商户的模块
        	resultInfo = publicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.utility,mercId);
            if(!resultInfo.getErrCode().equals("0")){
            	responseData.setRetCode(1);
            	responseData.setRetMessage(resultInfo.getMsg());
            	String jsonString = createJsonString(resultInfo);
            	
            	logger.info("商户业务受限，mercId:"+mercId+",tradeType:"+TradeTypeEnum.utility.getTypeName()+",msg:"+resultInfo.getMsg());
                return jsonString;
            }
        }
        
		ViewKyChannelInfo channelInfo = AppPospContext.context.get(OFFICHANNEL+GETPROVINCELIST);

		List<Province> provinces = new ArrayList<Province>();

		if (null != channelInfo) {
			// 请求第三方省份查询接口
			String path = channelInfo.getUrl();
			String channelNO = channelInfo.getChannelNO();
			String channelPwd = channelInfo.getChannelPwd();
			String version = channelInfo.getVersion();

			if (StringUtils.isNotBlank(path)) {
				path += "?userid=" + channelNO + "&userpws=" + channelPwd
						+ "&version=" + version;
				String httpresult = HttpURLConection.httpURLConectionGET(path,
						"gb2312");
				if (StringUtils.isNotBlank(httpresult)) {
					// 解析返回结果
					Document doc = DocumentHelper.parseText(httpresult);
					Map<String, Object> map = XMLUtil.Dom2Map(doc);
					int retcode = Integer.parseInt(map.get("retcode")
							.toString());
					String err_msg = map.get("err_msg").toString();
					Map map1 = (Map) map.get("provinces");

					if (retcode == 1) {

						Province p;

						if (map1.get("province") instanceof Map) {
							Map map2 = (Map) map1.get("province");

							String provinceId = map2.get("provinceId")
									.toString();
							String provinceName = map2.get("provinceName")
									.toString();

							p = new Province();
							p.setProvinceId(provinceId);
							p.setProvinceName(provinceName);
							provinces.add(p);
						} else if (map1.get("province") instanceof List) {

							List lists = (List) map1.get("province");

							for (int i = 0; i < lists.size(); i++) {
								Map map2 = (Map) lists.get(i);
								String provinceId = map2.get("provinceId")
										.toString();
								String provinceName = map2.get("provinceName")
										.toString();

								p = new Province();
								p.setProvinceId(provinceId);
								p.setProvinceName(provinceName);
								provinces.add(p);
							}
						}
						message = SUCCESSMESSAGE;
					} else {
						message = retcode + ":" + err_msg;
					}
				}
			} else {
				logger.info("调用第三方省份查询接口失败");
			}
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
		responseData.setProvinceList(provinces);
		String jsonString = createJsonString(responseData);
		logger.info("[app_rsp]" + createJson(responseData));
		return jsonString;
	}

	/**
	 * 省份查询异常
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public String getProvinceListException() throws Exception {
		GetProvinceListResponseDTO responseData = new GetProvinceListResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		responseData.setProvinceList(null);

		logger.info("[app_rsp]" + createJson(responseData));

		return createJsonString(responseData);
	}

	/**
	 * 城市查询
	 * 
	 * @param getCityListInfo
	 * @return
	 * @throws Exception
	 */
	@Override
	public String getCityList(HttpSession session,String getCityListInfo) throws Exception {
		logger.info("城市查询");
		String message = INITIALIZEMESSAGE;
		GetCityListResponseDTO responseData = new GetCityListResponseDTO();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
		
		String oAgentNo = sessionInfo.getoAgentNo();
		String mercId = sessionInfo.getMercId();
        if(StringUtils.isBlank(oAgentNo) || StringUtils.isBlank(mercId)){
            responseData.setRetCode(1);
            responseData.setRetMessage("参数错误");
            String jsonString = createJsonString(responseData);
            return jsonString;
        }
		
		//校验欧单的模块
		ResultInfo resultInfo = publicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.utility,oAgentNo);
        if(!resultInfo.getErrCode().equals("0")){
        	responseData.setRetCode(1);
        	responseData.setRetMessage(resultInfo.getMsg());
        	String jsonString = createJsonString(resultInfo);
        	
        	logger.info("O单业务受限，oAagentNo:"+oAgentNo+",tradeType:"+TradeTypeEnum.utility.getTypeName()+",msg:"+resultInfo.getMsg());
            return jsonString;
        }else{
        	//校验商户的模块
        	resultInfo = publicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.utility,mercId);
            if(!resultInfo.getErrCode().equals("0")){
            	responseData.setRetCode(1);
            	responseData.setRetMessage(resultInfo.getMsg());
            	String jsonString = createJsonString(resultInfo);
            	
            	logger.info("商户业务受限，mercId:"+mercId+",tradeType:"+TradeTypeEnum.utility.getTypeName()+",msg:"+resultInfo.getMsg());
                return jsonString;
            }
        }

		// 解析商户登录信息
		Object obj = parseJsonString(getCityListInfo,
				GetCityListRequestDTO.class);

		List<City> cities = new ArrayList<City>();
		if (!obj.equals(DATAPARSINGMESSAGE)) {
			oAgentNo = sessionInfo.getoAgentNo();
            if(StringUtils.isBlank(oAgentNo)){
                responseData.setRetCode(1);
                responseData.setRetMessage("参数错误");
                String jsonString = createJsonString(responseData);
                return jsonString;
            }
            
			GetCityListRequestDTO requestDTO = (GetCityListRequestDTO) obj;
			
			ViewKyChannelInfo channelInfo = AppPospContext.context.get(OFFICHANNEL+GETCITYLIST);

			if (null != channelInfo) {
				// 请求第三方省份查询接口
				String path = channelInfo.getUrl();
				String channelNO = channelInfo.getChannelNO();
				String channelPwd = channelInfo.getChannelPwd();
				String version = channelInfo.getVersion();

				if (StringUtils.isNotBlank(path)) {
					path += "?userid=" + channelNO + "&userpws=" + channelPwd
							+ "&version=" + version + "&provId="
							+ requestDTO.getProvinceId();
					String httpresult = HttpURLConection.httpURLConectionGET(
							path, "gb2312");
					if (StringUtils.isNotBlank(httpresult)) {
						// 解析返回结果
						Document doc = DocumentHelper.parseText(httpresult);
						Map<String, Object> map = XMLUtil.Dom2Map(doc);
						int retcode = Integer.parseInt(map.get("retcode")
								.toString());
						String err_msg = map.get("err_msg").toString();
						Map map1 = (Map) map.get("citys");
						if (retcode == 1) {

							City c;

							if (map1.get("city") instanceof Map) {
								Map map2 = (Map) map1.get("city");

								String cityId = map2.get("cityId").toString();
								String cityName = map2.get("cityName")
										.toString();
								String provinceId = map2.get("provinceId")
										.toString();

								c = new City();
								c.setCityId(cityId);
								c.setCityName(cityName);
								c.setProvinceId(provinceId);
								cities.add(c);
							} else if (map1.get("city") instanceof List) {
								List lists = (List) map1.get("city");

								for (int i = 0; i < lists.size(); i++) {
									Map map2 = (Map) lists.get(i);

									String cityId = map2.get("cityId")
											.toString();
									String cityName = map2.get("cityName")
											.toString();
									String provinceId = map2.get("provinceId")
											.toString();

									c = new City();
									c.setCityId(cityId);
									c.setCityName(cityName);
									c.setProvinceId(provinceId);
									cities.add(c);
								}
							}
							message = SUCCESSMESSAGE;
						} else {
							message = retcode + ":" + err_msg;
						}
					}
				} else {
					logger.info("调用第三方省份查询接口失败");
				}
			} else {
				insertAppLogs("", "", "2001");
				message = DATAPARSINGMESSAGE;
			}
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
		responseData.setCityList(cities);
		String jsonString = createJsonString(responseData);
		logger.info("[app_rsp]" + createJson(responseData));
		return jsonString;
	}

	/**
	 * 城市查询异常
	 * 
	 * @param getCityListInfo
	 * @return
	 * @throws Exception
	 */
	@Override
	public String getCityListException(String getCityListInfo) throws Exception {
		GetCityListResponseDTO responseData = new GetCityListResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		responseData.setCityList(null);

		logger.info("[app_rsp]" + createJson(responseData));

		return createJsonString(responseData);
	}

	/**
	 * 水煤电充值类型查询
	 * 
	 * @param getPayProjectListInfo
	 * @return
	 * @throws Exception
	 */
	@Override
	public String getPayProjectList(HttpSession session, String getPayProjectListInfo)
			throws Exception {
		logger.info("充值类型查询");
		String message = INITIALIZEMESSAGE;
		
		GetPayProjectListResponseDTO responseData = new GetPayProjectListResponseDTO();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
		String oAgentNo = sessionInfo.getoAgentNo();
		String mercId = sessionInfo.getMercId();
        if(StringUtils.isBlank(oAgentNo) || StringUtils.isBlank(mercId)){
            responseData.setRetCode(1);
            responseData.setRetMessage("参数错误");
            String jsonString = createJsonString(responseData);
            return jsonString;
        }
		
		//校验欧单的模块
		ResultInfo resultInfo = publicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.utility,oAgentNo);
        if(!resultInfo.getErrCode().equals("0")){
        	responseData.setRetCode(1);
        	responseData.setRetMessage(resultInfo.getMsg());
        	String jsonString = createJsonString(resultInfo);
        	
        	logger.info("O单业务受限，oAagentNo:"+oAgentNo+",tradeType:"+TradeTypeEnum.utility.getTypeName()+",msg:"+resultInfo.getMsg());
            return jsonString;
        }else{
        	//校验商户的模块
        	resultInfo = publicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.utility,mercId);
            if(!resultInfo.getErrCode().equals("0")){
            	responseData.setRetCode(1);
            	responseData.setRetMessage(resultInfo.getMsg());
            	String jsonString = createJsonString(resultInfo);
            	
            	logger.info("商户业务受限，mercId:"+mercId+",tradeType:"+TradeTypeEnum.utility.getTypeName()+",msg:"+resultInfo.getMsg());
                return jsonString;
            }
        }

		// 解析商户登录信息
		Object obj = parseJsonString(getPayProjectListInfo,
				GetPayProjectListRequestDTO.class);

		List<PayProject> payProjects = new ArrayList<PayProject>();
		if (!obj.equals(DATAPARSINGMESSAGE)) {
			oAgentNo = sessionInfo.getoAgentNo();
            if(StringUtils.isBlank(oAgentNo)){
                responseData.setRetCode(1);
                responseData.setRetMessage("参数错误");
                String jsonString = createJsonString(responseData);
                return jsonString;
            }
			
			GetPayProjectListRequestDTO requestDTO = (GetPayProjectListRequestDTO) obj;

			ViewKyChannelInfo channelInfo = AppPospContext.context.get(OFFICHANNEL+GETPAYPROJECTLIST);
			
			if (null != channelInfo) {
				// 请求第三方省份查询接口
				String path = channelInfo.getUrl();
				String channelNO = channelInfo.getChannelNO();
				String channelPwd = channelInfo.getChannelPwd();
				String version = channelInfo.getVersion();

				if (StringUtils.isNotBlank(path)) {
					path += "?userid=" + channelNO + "&userpws=" + channelPwd
							+ "&version=" + version + "&provId="
							+ requestDTO.getProvinceId() + "&cityId="
							+ requestDTO.getCityId();
					String httpresult = HttpURLConection.httpURLConectionGET(
							path, "gb2312");
					if (StringUtils.isNotBlank(httpresult)) {
						// 解析返回结果
						Document doc = DocumentHelper.parseText(httpresult);
						Map<String, Object> map = XMLUtil.Dom2Map(doc);
						int retcode = Integer.parseInt(map.get("retcode")
								.toString());
						String err_msg = map.get("err_msg").toString();
						Map map1 = (Map) map.get("payProjects");

						if (retcode == 1) {
							PayProject p;

							if (map.get("payProject") instanceof Map) {
								Map map2 = (Map) map.get("payProject");

								String provinceId = map2.get("provinceId")
										.toString();
								String cityId = map2.get("cityId").toString();
								String payProjectId = map2.get("payProjectId")
										.toString();
								String payProjectName = map2.get(
										"payProjectName").toString();

								p = new PayProject();
								p.setCityId(cityId);
								p.setProvinceId(provinceId);
								p.setPayProjectId(payProjectId);
								p.setPayProjectName(payProjectName);
								payProjects.add(p);
							} else if (map1.get("payProject") instanceof List) {
								List lists = (List) map1.get("payProject");

								for (int i = 0; i < lists.size(); i++) {

									Map map2 = (Map) lists.get(i);

									String provinceId = map2.get("provinceId")
											.toString();
									String cityId = map2.get("cityId")
											.toString();
									String payProjectId = map2.get(
											"payProjectId").toString();
									String payProjectName = map2.get(
											"payProjectName").toString();

									p = new PayProject();
									p.setCityId(cityId);
									p.setProvinceId(provinceId);
									p.setPayProjectId(payProjectId);
									p.setPayProjectName(payProjectName);
									payProjects.add(p);
								}
							}
							message = SUCCESSMESSAGE;
						} else {
							message = retcode + ":" + err_msg;
						}
					}
				} else {
					logger.info("调用第三方省份查询接口失败");
				}
			} else {
				insertAppLogs("", "", "2001");
				message = DATAPARSINGMESSAGE;
			}
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
		responseData.setPayProjectList(payProjects);
		String jsonString = createJsonString(responseData);
		logger.info("[app_rsp]" + createJson(responseData));
		return jsonString;
	}

	/**
	 * 水煤电充值类型查询异常
	 * 
	 * @param getPayProjectListInfo
	 * @return
	 * @throws Exception
	 */
	@Override
	public String getPayProjectListException(String getPayProjectListInfo)
			throws Exception {
		GetPayProjectListResponseDTO responseData = new GetPayProjectListResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		responseData.setPayProjectList(null);

		logger.info("[app_rsp]" + createJson(responseData));

		return createJsonString(responseData);
	}

	/**
	 * 水煤电缴费单位查询
	 * 
	 * @param getPayUnitListInfo
	 * @return
	 * @throws Exception
	 */
	@Override
	public String getPayUnitList(HttpSession session, String getPayUnitListInfo) throws Exception {
		logger.info("缴费单位查询");
		String message = INITIALIZEMESSAGE;
		
		GetPayUnitListResponseDTO responseData = new GetPayUnitListResponseDTO();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
		String oAgentNo = sessionInfo.getoAgentNo();
		String mercId = sessionInfo.getMercId();
        if(StringUtils.isBlank(oAgentNo) || StringUtils.isBlank(mercId)){
            responseData.setRetCode(1);
            responseData.setRetMessage("参数错误");
            String jsonString = createJsonString(responseData);
            return jsonString;
        }
		
		//校验欧单的模块
		ResultInfo resultInfo = publicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.utility,oAgentNo);
        if(!resultInfo.getErrCode().equals("0")){
        	responseData.setRetCode(1);
        	responseData.setRetMessage(resultInfo.getMsg());
        	String jsonString = createJsonString(resultInfo);
        	
        	logger.info("O单业务受限，oAagentNo:"+oAgentNo+",tradeType:"+TradeTypeEnum.utility.getTypeName()+",msg:"+resultInfo.getMsg());
            return jsonString;
        }else{
        	//校验商户的模块
        	resultInfo = publicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.utility,mercId);
            if(!resultInfo.getErrCode().equals("0")){
            	responseData.setRetCode(1);
            	responseData.setRetMessage(resultInfo.getMsg());
            	String jsonString = createJsonString(resultInfo);
            	
            	logger.info("商户业务受限，mercId:"+mercId+",tradeType:"+TradeTypeEnum.utility.getTypeName()+",msg:"+resultInfo.getMsg());
                return jsonString;
            }
        }

		// 解析商户登录信息
		Object obj = parseJsonString(getPayUnitListInfo,
				GetPayUnitListRequestDTO.class);

		List<PayUnit> payUnits = new ArrayList<PayUnit>();
		if (!obj.equals(DATAPARSINGMESSAGE)) {
			oAgentNo = sessionInfo.getoAgentNo();
            if(StringUtils.isBlank(oAgentNo)){
                responseData.setRetCode(1);
                responseData.setRetMessage("参数错误");
                String jsonString = createJsonString(responseData);
                return jsonString;
            }
            
			GetPayUnitListRequestDTO requestDTO = (GetPayUnitListRequestDTO) obj;

			ViewKyChannelInfo channelInfo = AppPospContext.context.get(OFFICHANNEL+GETPAYUNITLIST);
			
			if (null != channelInfo) {
				// 请求第三方省份查询接口
				String path = channelInfo.getUrl();
				String channelNO = channelInfo.getChannelNO();
				String channelPwd = channelInfo.getChannelPwd();
				String version = channelInfo.getVersion();

				if (StringUtils.isNotBlank(path)) {
					path += "?userid=" + channelNO + "&userpws=" + channelPwd
							+ "&version=" + version + "&provId="
							+ requestDTO.getProvinceId() + "&cityId="
							+ requestDTO.getCityId() + "&type="
							+ requestDTO.getPayProjectId();
					String httpresult = HttpURLConection.httpURLConectionGET(
							path, "gb2312");
					if (StringUtils.isNotBlank(httpresult)) {
						// 解析返回结果
						Document doc = DocumentHelper.parseText(httpresult);
						Map<String, Object> map = XMLUtil.Dom2Map(doc);
						int retcode = Integer.parseInt(map.get("retcode")
								.toString());
						String err_msg = map.get("err_msg").toString();
						Map map1 = (Map) map.get("payUnits");

						if (retcode == 1) {

							PayUnit p;

							if (map1.get("payUnit") instanceof Map) {
								Map map2 = (Map) map1.get("payUnit");

								String provinceId = map2.get("provinceId")
										.toString();
								String cityId = map2.get("cityId").toString();
								String payProjectId = map2.get("payProjectId")
										.toString();
								String payUnitId = map2.get("payUnitId")
										.toString();
								String payUnitName = map2.get("payUnitName")
										.toString();

								p = new PayUnit();
								p.setCityId(cityId);
								p.setProvinceId(provinceId);
								p.setPayProjectId(payProjectId);
								p.setPayUnitId(payUnitId);
								p.setPayUnitName(payUnitName);
								payUnits.add(p);
							} else if (map1.get("payUnit") instanceof List) {
								List lists = (List) map1.get("payUnit");

								for (int i = 0; i < lists.size(); i++) {
									Map map2 = (Map) lists.get(i);

									String provinceId = map2.get("provinceId")
											.toString();
									String cityId = map2.get("cityId")
											.toString();
									String payProjectId = map2.get(
											"payProjectId").toString();
									String payUnitId = map2.get("payUnitId")
											.toString();
									String payUnitName = map2
											.get("payUnitName").toString();

									p = new PayUnit();
									p.setCityId(cityId);
									p.setProvinceId(provinceId);
									p.setPayProjectId(payProjectId);
									p.setPayUnitId(payUnitId);
									p.setPayUnitName(payUnitName);
									payUnits.add(p);
								}
							}
							message = SUCCESSMESSAGE;
						} else {
							message = retcode + ":" + err_msg;
						}
					}
				} else {
					logger.info("调用第三方省份查询接口失败");
				}
			} else {
				insertAppLogs("", "", "2001");
				message = DATAPARSINGMESSAGE;
			}
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
		responseData.setPayUnitList(payUnits);
		String jsonString = createJsonString(responseData);
		logger.info("[app_rsp]" + createJson(responseData));
		return jsonString;
	}

	/**
	 * 水煤电缴费单位查询异常
	 * 
	 * @param getPayUnitListInfo
	 * @return
	 * @throws Exception
	 */
	@Override
	public String getPayUnitListException(String getPayUnitListInfo)
			throws Exception {
		GetPayUnitListResponseDTO responseData = new GetPayUnitListResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		responseData.setPayUnitList(null);

		logger.info("[app_rsp]" + createJson(responseData));

		return createJsonString(responseData);
	}

	/**
	 * 水煤电商品信息查询
	 * 
	 * @param queryClassIdInfo
	 * @return
	 * @throws Exception
	 */
	@Override
	public String queryClassId(HttpSession session, String queryClassIdInfo) throws Exception {
		logger.info("商品信息查询");
		String message = INITIALIZEMESSAGE;
		
		QueryClassIdResponseDTO responseData = new QueryClassIdResponseDTO();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
		String oAgentNo = sessionInfo.getoAgentNo();
		String mercId = sessionInfo.getMercId();
        if(StringUtils.isBlank(oAgentNo) || StringUtils.isBlank(mercId)){
            responseData.setRetCode(1);
            responseData.setRetMessage("参数错误");
            String jsonString = createJsonString(responseData);
            return jsonString;
        }
		
		//校验欧单的模块
		ResultInfo resultInfo = publicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.utility,oAgentNo);
        if(!resultInfo.getErrCode().equals("0")){
        	responseData.setRetCode(1);
        	responseData.setRetMessage(resultInfo.getMsg());
        	String jsonString = createJsonString(resultInfo);
        	
        	logger.info("O单业务受限，oAagentNo:"+oAgentNo+",tradeType:"+TradeTypeEnum.utility.getTypeName()+",msg:"+resultInfo.getMsg());
            return jsonString;
        }else{
        	//校验商户的模块
        	resultInfo = publicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.utility,mercId);
            if(!resultInfo.getErrCode().equals("0")){
            	responseData.setRetCode(1);
            	responseData.setRetMessage(resultInfo.getMsg());
            	String jsonString = createJsonString(resultInfo);
            	
            	logger.info("商户业务受限，mercId:"+mercId+",tradeType:"+TradeTypeEnum.utility.getTypeName()+",msg:"+resultInfo.getMsg());
                return jsonString;
            }
        }

		// 解析商户登录信息
		Object obj = parseJsonString(queryClassIdInfo,
				QueryClassIdRequestDTO.class);

		List<Card> cards = new ArrayList<Card>();
		if (!obj.equals(DATAPARSINGMESSAGE)) {
			oAgentNo = sessionInfo.getoAgentNo();
            if(StringUtils.isBlank(oAgentNo)){
                responseData.setRetCode(1);
                responseData.setRetMessage("参数错误");
                String jsonString = createJsonString(responseData);
                return jsonString;
            }
            
			QueryClassIdRequestDTO requestDTO = (QueryClassIdRequestDTO) obj;

			ViewKyChannelInfo channelInfo = AppPospContext.context.get(OFFICHANNEL+QUERYCLASSID);
			
			if (null != channelInfo) {
				// 请求第三方省份查询接口
				String path = channelInfo.getUrl();
				String channelNO = channelInfo.getChannelNO();
				String channelPwd = channelInfo.getChannelPwd();
				String version = channelInfo.getVersion();

				if (StringUtils.isNotBlank(path)) {
					path += "?userid=" + channelNO + "&userpws=" + channelPwd
							+ "&version=" + version + "&provId="
							+ requestDTO.getProvId() + "&cityId="
							+ requestDTO.getCityId() + "&type="
							+ requestDTO.getType() + "&chargeCompanyCode="
							+ requestDTO.getChargeCompanyCode();
					String httpresult = HttpURLConection.httpURLConectionGET(
							path, "gb2312");
					if (StringUtils.isNotBlank(httpresult)) {
						// 解析返回结果
						Document doc = DocumentHelper.parseText(httpresult);
						Map<String, Object> map = XMLUtil.Dom2Map(doc);
						int retcode = Integer.parseInt(map.get("retcode")
								.toString());
						String err_msg = map.get("err_msg").toString();
						Map map1 = (Map) map.get("cards");

						if (retcode == 1) {

							Card c;

							if (map1.get("card") instanceof Map) {

								Map map2 = (Map) map1.get("card");

								String productId = map2.get("productId")
										.toString();
								String productName = map2.get("productName")
										.toString();
								String inprice = map2.get("inprice").toString();

								c = new Card();
								c.setProductId(productId);
								c.setProductName(productName);
								c.setInprice(inprice);
								cards.add(c);
							} else if (map1.get("card") instanceof List) {
								List lists = (List) map1.get("card");

								for (int i = 0; i < lists.size(); i++) {
									Map map2 = (Map) lists.get(i);

									String productId = map2.get("productId")
											.toString();
									String productName = map2
											.get("productName").toString();
									String inprice = map2.get("inprice")
											.toString();

									c = new Card();
									c.setProductId(productId);
									c.setProductName(productName);
									c.setInprice(inprice);
									cards.add(c);
								}
							}
							message = SUCCESSMESSAGE;
						} else {
							message = retcode + ":" + err_msg;
						}
					}
				} else {
					logger.info("调用第三方省份查询接口失败");
				}
			} else {
				insertAppLogs("", "", "2001");
				message = DATAPARSINGMESSAGE;
			}
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
		responseData.setCards(cards);
		String jsonString = createJsonString(responseData);
		logger.info("[app_rsp]" + createJson(responseData));
		return jsonString;
	}

	/**
	 * 水煤电商品信息查询异常
	 * 
	 * @param queryClassIdInfo
	 * @return
	 * @throws Exception
	 */
	@Override
	public String queryClassIdException(String queryClassIdInfo)
			throws Exception {
		QueryClassIdResponseDTO responseData = new QueryClassIdResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		responseData.setCards(null);

		logger.info("[app_rsp]" + createJson(responseData));

		return createJsonString(responseData);
	}

	/**
	 * 水电煤账户欠费查询
	 * 
	 * @param queryBalanceInfo
	 * @return
	 * @throws Exception
	 */
	@Override
	public String queryBalance(HttpSession session, String queryBalanceInfo) throws Exception {
		logger.info("账户欠费查询");
		String message = INITIALIZEMESSAGE;
		
		QueryBalanceResponseDTO responseData = new QueryBalanceResponseDTO();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
		String oAgentNo = sessionInfo.getoAgentNo();
		String mercId = sessionInfo.getMercId();
        if(StringUtils.isBlank(oAgentNo) || StringUtils.isBlank(mercId)){
            responseData.setRetCode(1);
            responseData.setRetMessage("参数错误");
            String jsonString = createJsonString(responseData);
            return jsonString;
        }
		
		//校验欧单的模块
		ResultInfo resultInfo = publicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.utility,oAgentNo);
        if(!resultInfo.getErrCode().equals("0")){
        	responseData.setRetCode(1);
        	responseData.setRetMessage(resultInfo.getMsg());
        	String jsonString = createJsonString(resultInfo);
        	
        	logger.info("O单业务受限，oAagentNo:"+oAgentNo+",tradeType:"+TradeTypeEnum.utility.getTypeName()+",msg:"+resultInfo.getMsg());
            return jsonString;
        }else{
        	//校验商户的模块
        	resultInfo = publicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.utility,mercId);
            if(!resultInfo.getErrCode().equals("0")){
            	responseData.setRetCode(1);
            	responseData.setRetMessage(resultInfo.getMsg());
            	String jsonString = createJsonString(resultInfo);
            	
            	logger.info("商户业务受限，mercId:"+mercId+",tradeType:"+TradeTypeEnum.utility.getTypeName()+",msg:"+resultInfo.getMsg());
                return jsonString;
            }
        }

		Balance b = new Balance();

		// 解析商户登录信息
		Object obj = parseJsonString(queryBalanceInfo,
				QueryBalanceRequestDTO.class);

		if (!obj.equals(DATAPARSINGMESSAGE)) {
			oAgentNo = sessionInfo.getoAgentNo();
            if(StringUtils.isBlank(oAgentNo)){
                responseData.setRetCode(1);
                responseData.setRetMessage("参数错误");
                String jsonString = createJsonString(responseData);
                return jsonString;
            }
            
			QueryBalanceRequestDTO requestDTO = (QueryBalanceRequestDTO) obj;

			ViewKyChannelInfo channelInfo = AppPospContext.context.get(OFFICHANNEL+QUERYBALANCE);
			
			if (null != channelInfo) {
				// 请求第三方省份查询接口
				String path = channelInfo.getUrl();
				String channelNO = channelInfo.getChannelNO();
				String channelPwd = channelInfo.getChannelPwd();
				String version = channelInfo.getVersion();

				if (StringUtils.isNotBlank(path)) {
					path += "?userid="
							+ channelNO
							+ "&userpws="
							+ channelPwd
							+ "&version="
							+ version
							+ "&provName="
							+ java.net.URLEncoder.encode(requestDTO
									.getProvName(), "gbk")
							+ "&cityName="
							+ java.net.URLEncoder.encode(requestDTO
									.getCityName(), "gbk")
							+ "&type="
							+ requestDTO.getType()
							+ "&chargeCompanyCode="
							+ requestDTO.getChargeCompanyCode()
							+ "&chargeCompanyName="
							+ java.net.URLEncoder.encode(requestDTO
									.getChargeCompanyName(), "gbk")
							+ "&account=" + requestDTO.getAccount()
							+ "&cardId=" + requestDTO.getCardId();
					String httpresult = HttpURLConection.httpURLConectionGET(
							path, "gb2312");
					if (StringUtils.isNotBlank(httpresult)) {
						// 解析返回结果
						Document doc = DocumentHelper.parseText(httpresult);
						Map<String, Object> map = XMLUtil.Dom2Map(doc);
						int retcode = Integer.parseInt(map.get("retcode")
								.toString());
						String err_msg = map.get("err_msg").toString();

						Map map2 = (Map) map.get("balance");

						if (retcode == 1) {

							String userCode = map2.get("userCode").toString();
							String account = map2.get("account").toString();

							String accountName = null;
							if (map2.get("accountName") != null) {
								accountName = map2.get("accountName")
										.toString();
							}
							String balance = null;
							if (map2.get("balance") != null) {
								balance = map2.get("balance").toString();
							}
							String contractNo = null;
							if (map2.get("contractNo") != null) {
								contractNo = map2.get("contractNo").toString();
							}

							b.setUserCode(userCode);
							b.setAccount(account);
							b.setAccountName(accountName);
							b.setBalance(balance);
							b.setContractNo(contractNo);

							message = SUCCESSMESSAGE;
						} else {
							message = retcode + ":" + err_msg;
						}
					}
				} else {
					logger.info("调用第三方省份查询接口失败");
				}
			} else {
				insertAppLogs("", "", "2001");
				message = DATAPARSINGMESSAGE;
			}
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
		responseData.setBalance(b);
		String jsonString = createJsonString(responseData);
		logger.info("[app_rsp]" + createJson(responseData));
		return jsonString;
	}

	/**
	 * 水电煤账户欠费查询异常
	 * 
	 * @param queryBalanceInfo
	 * @return
	 * @throws Exception
	 */
	@Override
	public String queryBalanceException(String queryBalanceInfo)
			throws Exception {
		QueryBalanceResponseDTO responseData = new QueryBalanceResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		responseData.setBalance(null);

		logger.info("[app_rsp]" + createJson(responseData));

		return createJsonString(responseData);
	}

	public static boolean isNumeric(String str) {
		Boolean strResult = str.matches("^[0-9]+([.]{1}[0-9]+){0,1}$");
		if (strResult == true) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 缴费详情查看
	 * 
	 * @param queryPayDetailInfo
	 * @return
	 * @throws Exception
	 */
	@Override
	public String queryPayDetail(HttpSession session, String queryPayDetailInfo)
			throws Exception {
		logger.info("缴费详情查看");
		String message = INITIALIZEMESSAGE;
		boolean bool = false;
		QueryPayDetailResponseDTO responseData = new QueryPayDetailResponseDTO();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
		
		String oAgentNo = sessionInfo.getoAgentNo();
		String mercId = sessionInfo.getMercId();
        if(StringUtils.isBlank(oAgentNo) || StringUtils.isBlank(mercId)){
            responseData.setRetCode(1);
            responseData.setRetMessage("参数错误");
            String jsonString = createJsonString(responseData);
            return jsonString;
        }
		
		//校验欧单的模块
		ResultInfo resultInfo = publicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.utility,oAgentNo);
        if(!resultInfo.getErrCode().equals("0")){
        	responseData.setRetCode(1);
        	responseData.setRetMessage(resultInfo.getMsg());
        	String jsonString = createJsonString(resultInfo);
        	
        	logger.info("O单业务受限，oAagentNo:"+oAgentNo+",tradeType:"+TradeTypeEnum.utility.getTypeName()+",msg:"+resultInfo.getMsg());
            return jsonString;
        }else{
        	//校验商户的模块
        	resultInfo = publicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.utility,mercId);
            if(!resultInfo.getErrCode().equals("0")){
            	responseData.setRetCode(1);
            	responseData.setRetMessage(resultInfo.getMsg());
            	String jsonString = createJsonString(resultInfo);
            	
            	logger.info("商户业务受限，mercId:"+mercId+",tradeType:"+TradeTypeEnum.utility.getTypeName()+",msg:"+resultInfo.getMsg());
                return jsonString;
            }
        }

		// 解析商户登录信息
		Object obj = parseJsonString(queryPayDetailInfo,
				QueryPayDetailRequestDTO.class);

		List<Card> cards = new ArrayList<Card>();
		if (!obj.equals(DATAPARSINGMESSAGE)) {
			QueryPayDetailRequestDTO requestDTO = (QueryPayDetailRequestDTO) obj;

			ViewKyChannelInfo channelInfo = channelInfoDao
					.searchChannelInfo(QUERYCLASSID);
			if (null != channelInfo) {
				// 请求第三方省份查询接口
				String path = channelInfo.getUrl();
				String channelNO = channelInfo.getChannelNO();
				String channelPwd = channelInfo.getChannelPwd();
				String version = channelInfo.getVersion();

				if (StringUtils.isNotBlank(path)) {
					path += "?userid=" + channelNO + "&userpws=" + channelPwd
							+ "&version=" + version + "&provId="
							+ requestDTO.getProvinceId() + "&cityId="
							+ requestDTO.getCityId() + "&type="
							+ requestDTO.getPayProjectId()
							+ "&chargeCompanyCode=" + requestDTO.getPayUnitId();
					String httpresult = HttpURLConection.httpURLConectionGET(
							path, "gb2312");
					if (StringUtils.isNotBlank(httpresult)) {
						// 解析返回结果
						Document doc = DocumentHelper.parseText(httpresult);
						Map<String, Object> map = XMLUtil.Dom2Map(doc);
						int retcode = Integer.parseInt(map.get("retcode")
								.toString());
						String err_msg = map.get("err_msg").toString();
						Map map1 = (Map) map.get("cards");

						if (retcode == 1) {

							Card c;
							if (map1.get("card") instanceof Map) {
								Map map2 = (Map) map1.get("card");

								String productId = map2.get("productId")
										.toString();
								String productName = map2.get("productName")
										.toString();
								String inprice = map2.get("inprice").toString();

								c = new Card();
								c.setProductId(productId);
								c.setProductName(productName);
								c.setInprice(inprice);
								cards.add(c);
							} else if (map1.get("card") instanceof List) {
								List lists = (List) map1.get("card");

								for (int i = 0; i < lists.size(); i++) {
									Map map2 = (Map) lists.get(i);

									String productId = map2.get("productId")
											.toString();
									String productName = map2
											.get("productName").toString();
									String inprice = map2.get("inprice")
											.toString();

									c = new Card();
									c.setProductId(productId);
									c.setProductName(productName);
									c.setInprice(inprice);
									cards.add(c);
								}
							}
							bool = true;
						} else {
							message = retcode + ":" + err_msg;
						}
					}
				} else {
					logger.info("调用第三方省份查询接口失败");
				}
			} else {
				insertAppLogs("", "", "2001");
				message = DATAPARSINGMESSAGE;
			}
		}

		Balance b = new Balance();
		if (bool) {

			if (!obj.equals(DATAPARSINGMESSAGE)) {
				QueryPayDetailRequestDTO requestDTO = (QueryPayDetailRequestDTO) obj;

				String provinceName = requestDTO.getProvinceName();
				String cityName = requestDTO.getCityName();
				String payUnitName = requestDTO.getPayUnitName();
				
				ViewKyChannelInfo channelInfo = channelInfoDao
						.searchChannelInfo(QUERYBALANCE);
				if (null != channelInfo) {
					// 请求第三方省份查询接口
					String path = channelInfo.getUrl();
					String channelNO = channelInfo.getChannelNO();
					String channelPwd = channelInfo.getChannelPwd();
					String version = channelInfo.getVersion();
					String type = null;

					if (requestDTO.getPayProjectId().equals(WATERBUSINESSNUM)) {
						type = "001";
					} else if (requestDTO.getPayProjectId().equals(
							ELECTRICITYBUSINESSNUM)) {
						type = "002";
					} else if (requestDTO.getPayProjectId().equals(
							GASBUSINESSNUM)) {
						type = "003";
					}
					
					session.setAttribute("provinceName", provinceName);
					session.setAttribute("cityName", cityName);
					session.setAttribute("payUnitName", payUnitName);

					String cardId = cards.get(0).getProductId();

					if (StringUtils.isNotBlank(path)) {
						path += "?userid="
								+ channelNO
								+ "&userpws="
								+ channelPwd
								+ "&version="
								+ version
								+ "&provName="
								+ java.net.URLEncoder.encode(provinceName, "gbk")
								+ "&cityName="
								+ java.net.URLEncoder.encode(cityName, "gbk")
								+ "&type="
								+ type
								+ "&chargeCompanyCode="
								+ requestDTO.getPayUnitId()
								+ "&chargeCompanyName="
								+ java.net.URLEncoder.encode(payUnitName, "gbk") + "&account="
								+ requestDTO.getClientId() + "&cardId="
								+ cardId;

						session.setAttribute("cardId", cardId);

						String httpresult = HttpURLConection
								.httpURLConectionGET(path, "gb2312");
						if (StringUtils.isNotBlank(httpresult)) {
							// 解析返回结果
							Document doc = DocumentHelper.parseText(httpresult);
							Map<String, Object> map = XMLUtil.Dom2Map(doc);
							int retcode = Integer.parseInt(map.get("retcode")
									.toString());
							String err_msg = map.get("err_msg").toString();

							String userCode = map.get("userCode").toString();
							String account = map.get("account").toString();

							Map map1 = (Map) map.get("balances");

							if (retcode == 1) {

								if (map1.get("balance") instanceof Map) {
									Map map2 = (Map) map1.get("balance");

									message = queryBalancePack(session, b,
											userCode, account, map2);

								} else if (map1.get("card") instanceof List) {
									List lists = (List) map1.get("card");

									for (int i = 0; i < lists.size(); i++) {
										Map map2 = (Map) lists.get(i);

										message = queryBalancePack(session, b,
												userCode, account, map2);
									}
								}
							} else {
								message = retcode + ":" + err_msg;
							}
						}
					} else {
						logger.info("调用第三方省份查询接口失败");
					}
				} else {
					insertAppLogs("", "", "2001");
					message = DATAPARSINGMESSAGE;
				}
			}
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
		responseData.setClientId(b.getAccount());
		responseData.setClientName(b.getAccountName());
		responseData.setMustPayAmt(b.getBalance());
		String jsonString = createJsonString(responseData);
		logger.info("[app_rsp]" + createJson(responseData));
		return jsonString;
	}

	private String queryBalancePack(HttpSession session, Balance b,
			String userCode, String account, Map map2) {
		String message;
		String accountName = null;
		if (map2.get("accountName") != null
				&& !"".equals(map2.get("accountName").toString())) {
			accountName = map2.get("accountName").toString();
			String balance = null;
			if (map2.get("balance") != null
					&& !"".equals(map2.get("balance").toString())) {
				if (isNumeric(map2.get("balance").toString())) {
					balance = new BigDecimal(map2.get("balance").toString())
							.multiply(new BigDecimal(100)).intValue()+"";

					String contractNo = null;
					if (map2.get("contractNo") != null) {
						contractNo = map2.get("contractNo").toString();
						session.setAttribute("contractNo", contractNo);
					}
					String payMentDay = null;
					if (map2.get("payMentDay") != null) {
						payMentDay = map2.get("payMentDay").toString();
						session.setAttribute("payMentDay", payMentDay);
					}

					b.setUserCode(userCode);
					b.setAccount(account);
					b.setAccountName(accountName);
					b.setBalance(balance);
					b.setContractNo(contractNo);
					message = SUCCESSMESSAGE;
				} else {
					message = FAILMESSAGE;
				}
			} else {
				message = FAILMESSAGE;
			}
		} else {
			message = FAILMESSAGE;
		}
		return message;
	}

	/**
	 * 缴费详情查看异常
	 * 
	 * @param queryPayDetailInfo
	 * @return
	 * @throws Exception
	 */
	@Override
	public String queryPayDetailException(String queryPayDetailInfo)
			throws Exception {
		QueryPayDetailResponseDTO responseData = new QueryPayDetailResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");

		logger.info("[app_rsp]" + createJson(responseData));

		return createJsonString(responseData);
	}

	/**
	 * 生成订单
	 * 
	 * @param utilityInfo
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@Override
	public String producedOrder(String utilityInfo, HttpSession session)
			throws Exception {
		logger.info("水煤电生成订单");
		String message = INITIALIZEMESSAGE;
		
		GeneralUtilityOrderResponseDTO responseData = new GeneralUtilityOrderResponseDTO();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
		
		String oAgentNo = sessionInfo.getoAgentNo();
		String mercId = sessionInfo.getMercId();
        if(StringUtils.isBlank(oAgentNo) || StringUtils.isBlank(mercId)){
            responseData.setRetCode(1);
            responseData.setRetMessage("参数错误");
            String jsonString = createJsonString(responseData);
            return jsonString;
        }
		
		//校验欧单的模块
		ResultInfo resultInfo = publicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.utility,oAgentNo);
        if(!resultInfo.getErrCode().equals("0")){
        	responseData.setRetCode(1);
        	responseData.setRetMessage(resultInfo.getMsg());
        	String jsonString = createJsonString(resultInfo);
        	
        	logger.info("O单业务受限，oAagentNo:"+oAgentNo+",tradeType:"+TradeTypeEnum.utility.getTypeName()+",msg:"+resultInfo.getMsg());
            return jsonString;
        }else{
        	//校验商户的模块
        	resultInfo = publicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.utility,mercId);
            if(!resultInfo.getErrCode().equals("0")){
            	responseData.setRetCode(1);
            	responseData.setRetMessage(resultInfo.getMsg());
            	String jsonString = createJsonString(resultInfo);
            	
            	logger.info("商户业务受限，mercId:"+mercId+",tradeType:"+TradeTypeEnum.utility.getTypeName()+",msg:"+resultInfo.getMsg());
                return jsonString;
            }
        }
        
		String jsonString = null;
		Object obj = parseJsonString(utilityInfo,
				GeneralUtilityOrderRequestDTO.class);
		
		String inprice = "";
		String paymentAmount = "";

		if (null != sessionInfo) {
			oAgentNo = sessionInfo.getoAgentNo();

            if(StringUtils.isBlank(oAgentNo)){
                //如果没有欧单编号，直接返回错误
                responseData.setRetCode(1);
                responseData.setRetMessage("参数错误");
                jsonString = createJsonString(responseData);
            }
			
			if (!obj.equals(DATAPARSINGMESSAGE)) {

				GeneralUtilityOrderRequestDTO generalUtilityOrderRequestDTO = (GeneralUtilityOrderRequestDTO) obj;
				
				// 分发请求
				if (StringUtils.isNotBlank(generalUtilityOrderRequestDTO
						.getPayType())) {
					if (generalUtilityOrderRequestDTO.getPayType().equals("1")) {
						//校验欧单的支付方式限制
						resultInfo = publicTradeVerifyService.paytypeVerifyOagent(PaymentCodeEnum.shuakaPay,oAgentNo);
				        if(!resultInfo.getErrCode().equals("0")){
				        	responseData.setRetCode(1);
				        	responseData.setRetMessage(resultInfo.getMsg());
				        	jsonString = createJsonString(resultInfo);
				        	
				        	logger.info("O单支付方式受限，oAagentNo:"+oAgentNo+",payment:"+PaymentCodeEnum.shuakaPay.getTypeName()+",msg:"+resultInfo.getMsg());
				            return jsonString;
				        }else{
				        	//校验商户的支付方式限制
				        	resultInfo = publicTradeVerifyService.payTypeVerifyMer(PaymentCodeEnum.shuakaPay,mercId);
				            if(!resultInfo.getErrCode().equals("0")){
				            	responseData.setRetCode(1);
				            	responseData.setRetMessage(resultInfo.getMsg());
				            	jsonString = createJsonString(resultInfo);
				            	
				            	logger.info("商户支付方式受限，mercId:"+mercId+",payment:"+PaymentCodeEnum.shuakaPay.getTypeName()+",msg:"+resultInfo.getMsg());
				                return jsonString;
				            }
				        }
					}else if (generalUtilityOrderRequestDTO
							.getPayType().equals("2")) {
						// 第三方支付
						if (StringUtils
								.isNotBlank(generalUtilityOrderRequestDTO
										.getPayChannel())) {
							if (generalUtilityOrderRequestDTO
									.getPayChannel().equals("1")) {
								// 支付宝SDK
								// 设置费率，手续费
							} else if (generalUtilityOrderRequestDTO
									.getPayChannel().equals("2")) {
								// 微信SDK
								// 设置费率，手续费
							} else if (generalUtilityOrderRequestDTO
									.getPayChannel().equals("3")) {
								// 百度SDK
								//校验欧单的支付方式限制
								resultInfo = publicTradeVerifyService.paytypeVerifyOagent(PaymentCodeEnum.bdSDKPay,oAgentNo);
						        if(!resultInfo.getErrCode().equals("0")){
						        	responseData.setRetCode(1);
						        	responseData.setRetMessage(resultInfo.getMsg());
						        	jsonString = createJsonString(resultInfo);
						        	
						        	logger.info("O单支付方式受限，oAagentNo:"+oAgentNo+",payment:"+PaymentCodeEnum.bdSDKPay.getTypeName()+",msg:"+resultInfo.getMsg());
						            return jsonString;
						        }else{
						        	//校验商户的支付方式限制
						        	resultInfo = publicTradeVerifyService.payTypeVerifyMer(PaymentCodeEnum.bdSDKPay,mercId);
						            if(!resultInfo.getErrCode().equals("0")){
						            	responseData.setRetCode(1);
						            	responseData.setRetMessage(resultInfo.getMsg());
						            	jsonString = createJsonString(resultInfo);
						            	
						            	logger.info("商户支付方式受限，mercId:"+mercId+",payment:"+PaymentCodeEnum.bdSDKPay.getTypeName()+",msg:"+resultInfo.getMsg());
						                return jsonString;
						            }
						        }
							}
						}
					}
					
					paymentAmount = generalUtilityOrderRequestDTO.getRechargeAmt().toString();
					
					//校验欧单的模块金额限制
					resultInfo = publicTradeVerifyService.amountVerifyOagent(Integer.parseInt(paymentAmount),TradeTypeEnum.utility,oAgentNo);
			        if(!resultInfo.getErrCode().equals("0")){
			        	responseData.setRetCode(1);
			        	responseData.setRetMessage(resultInfo.getMsg());
			        	jsonString = createJsonString(resultInfo);
			        	
			        	logger.info("O单业务金额受限，oAagentNo:"+oAgentNo+",tradeType:"+TradeTypeEnum.utility.getTypeName()+",amount:"+paymentAmount+",msg:"+resultInfo.getMsg());
			            return jsonString;
			        }else{
			        	//校验商户的模块金额限制
			        	resultInfo = publicTradeVerifyService.amountVerifyMer(Integer.parseInt(paymentAmount),TradeTypeEnum.utility,oAgentNo);
			            if(!resultInfo.getErrCode().equals("0")){
			            	responseData.setRetCode(1);
			            	responseData.setRetMessage(resultInfo.getMsg());
			            	jsonString = createJsonString(resultInfo);
			            	
			            	logger.info("商户业务金额受限，mercId:"+mercId+",tradeType:"+TradeTypeEnum.utility.getTypeName()+",amount:"+paymentAmount+",msg:"+resultInfo.getMsg());
			                return jsonString;
			            }
			        }

					String orderId = null;
					if (generalUtilityOrderRequestDTO.getPayProjectId().equals(
							WATERBUSINESSNUM)) {
						orderId = UtilMethod.getOrderid("140");
					} else if (generalUtilityOrderRequestDTO.getPayProjectId()
							.equals(ELECTRICITYBUSINESSNUM)) {
						orderId = UtilMethod.getOrderid("141");
					} else if (generalUtilityOrderRequestDTO.getPayProjectId()
							.equals(GASBUSINESSNUM)) {
						orderId = UtilMethod.getOrderid("142");
					}

					PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
					//设置欧单编号
                    pmsAppTransInfo.setoAgentNo(oAgentNo);
					pmsAppTransInfo.setTradetype("水煤电");// 水煤电
					pmsAppTransInfo.setTradetypecode("5");// 水煤电
					pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter());
					pmsAppTransInfo.setOrderid(orderId);// 水煤电业务编码

					pmsAppTransInfo.setPayamount(paymentAmount);// 设置交易金额

					pmsAppTransInfo.setMercid(sessionInfo.getMercId()); // 设置商户id
					pmsAppTransInfo.setFactamount(paymentAmount); // 设置实际金额
					pmsAppTransInfo.setOrderamount(paymentAmount);// 设置订单金额
					pmsAppTransInfo.setoAgentNo(oAgentNo);
					int appTransInsert = pmsAppTransInfoDao
							.insert(pmsAppTransInfo);

					if (appTransInsert == 1) {
						// 插入成功，下面的操作

						AppOrderDetail appOrderDetail = new AppOrderDetail();
						appOrderDetail.setOrderId(orderId);
						appOrderDetail.setProvId(generalUtilityOrderRequestDTO
								.getProvinceId());
						Object provName = session.getAttribute("provinceName");
						if (provName != null) {
							appOrderDetail.setProvName(provName.toString());
						}
						
						appOrderDetail.setCityId(generalUtilityOrderRequestDTO
								.getCityId());
						
						Object cityName = session.getAttribute("cityName");
						if (cityName != null) {
							appOrderDetail.setCityName(cityName.toString());
						}

						String type = null;
						String payProjectName = null;
						if (generalUtilityOrderRequestDTO.getPayProjectId()
								.equals(WATERBUSINESSNUM)) {
							type = "001";
							payProjectName = "水费";
						} else if (generalUtilityOrderRequestDTO
								.getPayProjectId().equals(
										ELECTRICITYBUSINESSNUM)) {
							type = "002";
							payProjectName = "电费";
						} else if (generalUtilityOrderRequestDTO
								.getPayProjectId().equals(GASBUSINESSNUM)) {
							type = "003";
							payProjectName = "燃气费";
						}
						appOrderDetail.setPayProjectName(payProjectName);
						
						appOrderDetail.setType(type);
						appOrderDetail
								.setChargeCompanyCode(generalUtilityOrderRequestDTO
										.getPayUnitId());
						
						Object payUnitName = session.getAttribute("payUnitName");
						if (payUnitName != null) {
							appOrderDetail.setPayUnitName(payUnitName.toString());
						}
						
						appOrderDetail.setCardId(session.getAttribute("cardId")
								.toString());
						appOrderDetail.setCardnum("1");
						appOrderDetail.setAccount(generalUtilityOrderRequestDTO
								.getClientId());

						Object contractNo = session.getAttribute("contractNo");
						if (contractNo != null) {
							appOrderDetail.setContractNo(contractNo.toString());
						}

						Object payMentDay = session.getAttribute("payMentDay");
						if (payMentDay != null) {
							appOrderDetail.setPayMentDay(payMentDay.toString());
						}

						int count = appOrderDetailDao.insert(appOrderDetail);
						if (count == 1) {

							if (generalUtilityOrderRequestDTO.getPayType()
									.equals("1")) {
								// 刷卡支付 约定这里只操作订单表，三方前置负责流水表处理
								BrushCalorieOfConsumptionRequestDTO dto = generalUtilityOrderRequestDTO
										.getDto();
								// 获取通道的费率
								Map<String, String> paramMap = new HashMap<String, String>();
                                paramMap.put("mercid",sessionInfo.getMercId());//商户编号
								paramMap.put("businesscode",
										TradeTypeEnum.utility.getTypeCode());// 业务编号
								Map<String, String> resultMap = merchantMineDao
										.queryBusinessInfo(paramMap);

								String isTop = resultMap.get("IS_TOP");
								String rate = resultMap.get("RATE");
								String topPoundage = resultMap
										.get("TOP_POUNDAGE");// 封顶费率
								String maxTransMoney = resultMap
										.get("MAX_AMOUNT"); // 每笔最大交易金额
								String minTransMoney = resultMap
										.get("MIN_AMOUNT"); // 每笔最小交易金额

								if (Double.parseDouble(paymentAmount) > Double
										.parseDouble(maxTransMoney)) {
									// 金额超过最大金额
									responseData.setRetCode(1);
									responseData.setRetMessage("金额超过最大金额");
									logger.info("交易金额大于最打金额");
									try {
										jsonString = createJsonString(responseData);
									} catch (Exception em) {
										em.printStackTrace();
									}
									return jsonString;
								} else if (Double.parseDouble(paymentAmount) < Double
										.parseDouble(minTransMoney)) {
									// 金额小于最小金额
									responseData.setRetCode(1);
									responseData.setRetMessage("金额小于最小金额");
									try {
										jsonString = createJsonString(responseData);
									} catch (Exception em) {
										em.printStackTrace();
									}
									logger.info("交易金额小于最小金额");
									return jsonString;
								}

								Double factAmount = 0.0;
								// 费率
								Double fee = 0.0;
								String rateStr = "";
								// 计算实际金额
								if ("1".equals(isTop)) {

									rateStr = rate + "-" + topPoundage;
									// 是封顶费率类型
									fee = Double.parseDouble(rate)
											* Double.parseDouble(paymentAmount);

									if (fee > Double.parseDouble(topPoundage)) {
										// 费率大于最大手续费，按最大手续费处理
										factAmount = Double
												.parseDouble(topPoundage)
												+ Double
														.parseDouble(paymentAmount);
										fee = Double.parseDouble(topPoundage);
									} else {
										// 按当前费率处理
										rateStr = rate;
										factAmount = Double
												.parseDouble(paymentAmount)
												+ fee;
									}

								} else {
									// 按当前费率处理
									rateStr = rate;
									fee = Double.parseDouble(rate)
											* Double.parseDouble(paymentAmount);
									factAmount = Double
											.parseDouble(paymentAmount)
											+ fee;
								}
								dto.setPayAmount(String.valueOf((int) Math
										.ceil(factAmount)));
								String sendStr8583 = "param="
										+ createBrushCalorieOfConsumptionDTORequest(
												sessionInfo,
												dto,
												pmsAppTransInfo.getOrderid(),
												CREDITTWOCARDPAYMENTCONSUMPTIONBUSINESSNUM,
												rateStr, dto.getSn());
								if ("param=fail".equals(sendStr8583)) {
									// 上送参数错误
									logger.info("上送参数错误， 订单号：" + orderId
											+ "，结束时间："
											+ UtilDate.getDateFormatter());
									// 金额小于最小金额
									responseData.setRetCode(1);
									responseData.setRetMessage("上送参数错误");
									try {
										jsonString = createJsonString(responseData);
									} catch (Exception em) {
										em.printStackTrace();
									}
									return jsonString;
								} else if ("param=meros".equals(sendStr8583)) {
									// 上送参数错误
									logger.info("pos机信息读取失败， 订单号：" + orderId
											+ "，结束时间："
											+ UtilDate.getDateFormatter());
									// 金额小于最小金额
									responseData.setRetCode(1);
									responseData
											.setRetMessage("pos机信息读取失败，不支持的卡类型");
									try {
										jsonString = createJsonString(responseData);
									} catch (Exception em) {
										em.printStackTrace();
									}
									return jsonString;
								} else {

									logger.info("调用三方前置刷卡接口请求参数：" + sendStr8583
											+ "，结束时间："
											+ UtilDate.getDateFormatter());
									ViewKyChannelInfo channelInfo = AppPospContext.context
											.get(SHUAKA + REMITPAYMENT);

									String successFlag = HttpURLConection
											.httpURLConnectionPOST(channelInfo
													.getUrl(), sendStr8583);

									logger.info("调用三方前置刷卡接口返回参数：" + successFlag
											+ "，结束时间："
											+ UtilDate.getDateFormatter());

									BrushCalorieOfConsumptionResponseDTO response = (BrushCalorieOfConsumptionResponseDTO) parseJsonString(
											successFlag,
											BrushCalorieOfConsumptionResponseDTO.class);

									if ("0000".equals(response.getRetCode())) {// 判断调用接口处理是否成功
										// 0000表示刷卡成功
										// 修改订单状态 加入相关信息
										PmsAppTransInfo pmsAppTrans = pmsAppTransInfoDao
												.searchOrderInfo(orderId);
										pmsAppTrans
												.setStatus(OrderStatusEnum.waitingPlantPay
														.getStatus());
										pmsAppTrans.setFactamount(factAmount
												.toString());// 设置实际金额
										pmsAppTrans.setBankno(dto.getCardNo());// 设置卡号
										pmsAppTrans.setPoundage(fee.toString()); // 设置费率
										pmsAppTrans.setPaymentcode("5");// 刷卡支付
										pmsAppTrans
												.setBrushType(generalUtilityOrderRequestDTO
														.getBrushType());// 设置刷卡类型
										pmsAppTrans.setSnNO(dto.getSn());// 设置sn
										pmsAppTrans.setRate(rateStr);// 设置费率
										pmsAppTrans.setPaymenttype("刷卡支付");
										List<PayCmmtufit> cardList = payCmmtufitDao
												.searchCardInfoByBeforeSix(dto
														.getCardNo().substring(
																0, 6)
														+ "%");
										if (cardList != null
												&& cardList.size() > 0) {
											pmsAppTrans.setBankname(cardList
													.get(0).getBnkName());
										}

										pmsAppTrans
												.setBusinessNum(UTILITYORDER);
										pmsAppTrans.setChannelNum(SELFCHANEL);
										Integer appTransUpdate = pmsAppTransInfoDao
												.update(pmsAppTrans);
										if (appTransUpdate == 1) {
											// 调用欧飞接口充值话费
											Integer resultOffi = offiPay
													.utilityOrder(pmsAppTrans);
											if (resultOffi == 1) {// 支付成功
												// 支付成功，修改订单状态
												pmsAppTrans
														.setThirdPartResultCode(resultOffi
																.toString());
												pmsAppTrans
														.setFinishtime(UtilDate
																.getDateFormatter());
												pmsAppTrans
														.setThirdPartResultCode("1");// 设置第三方返回码
												pmsAppTrans
														.setStatus(OrderStatusEnum.paySuccess
																.getStatus());
												pmsAppTransInfoDao
														.update(pmsAppTrans);
											} else if (resultOffi == 2) { // 正在支付，将状态改为正在支付
												pmsAppTrans
														.setThirdPartResultCode(resultOffi
																.toString());
												pmsAppTrans
														.setStatus(OrderStatusEnum.plantPayingNow
																.getStatus());
												pmsAppTransInfoDao
														.update(pmsAppTrans);
											}

											message = SUCCESSMESSAGE;
										} else {
											// 刷卡支付错误
											logger
													.info("更新订单出错， 订单号："
															+ orderId
															+ "，结束时间："
															+ UtilDate
																	.getDateFormatter());
											responseData.setRetCode(1);
											responseData
													.setRetMessage("更新订单出错，请查询订单");
											try {
												jsonString = createJsonString(responseData);
											} catch (Exception em) {
												em.printStackTrace();
											}
											return jsonString;
										}
									} else {
										responseData.setRetCode(1);
										responseData.setRetMessage("错误码："
												+ response.getRetCode()
												+ "\n错误信息："
												+ response.getRetMessage());
										logger.info("订单生成失败， 订单号：" + orderId
												+ "，结束时间："
												+ UtilDate.getDateFormatter());
										try {
											jsonString = createJsonString(responseData);
										} catch (Exception em) {
											em.printStackTrace();
										}
										return jsonString;
									}
								}
							} else if (generalUtilityOrderRequestDTO
									.getPayType().equals("2")) {
								// 第三方支付
								if (StringUtils
										.isNotBlank(generalUtilityOrderRequestDTO
												.getPayChannel())) {
									if (generalUtilityOrderRequestDTO
											.getPayChannel().equals("1")) {
										// 支付宝SDK
										// 设置费率，手续费
									} else if (generalUtilityOrderRequestDTO
											.getPayChannel().equals("2")) {
										// 微信SDK
										// 设置费率，手续费
									} else if (generalUtilityOrderRequestDTO
											.getPayChannel().equals("3")) {
										// 百度SDK

										// 查询当前订单
										// 计算费率
										String rateStr = "0.006";
										AppRateConfig appC = new AppRateConfig();
										appC.setRateType("3");
										appC.setoAgentNo(oAgentNo);
										AppRateConfig appRateConfig = appRateConfigDao
										.getByRateTypeAndoAgentNo(appC);
										if (appRateConfig != null
												&& StringUtils
														.isNotBlank(appRateConfig
																.getRate())) {
											rateStr = appRateConfig.getRate();
										}

										Double fee = Double
												.parseDouble(rateStr)
												* Double
														.parseDouble(paymentAmount);
										Double factAmount = Double
												.parseDouble(paymentAmount)
												+ fee;
										PmsAppTransInfo pmsAppTrans = pmsAppTransInfoDao
												.searchOrderInfo(orderId);
										pmsAppTrans
												.setFactamount(String
														.valueOf(Math
																.ceil(factAmount)));
										pmsAppTrans.setPoundage(fee.toString());
										pmsAppTrans.setPaymenttype("百度支付");
										pmsAppTrans.setPaymentcode("2");
										pmsAppTrans
												.setStatus(OrderStatusEnum.waitingClientPay
														.getStatus());
										pmsAppTrans.setRate(rateStr);
										pmsAppTrans
												.setBusinessNum(BAIDUCALLBACKURL);
										// 设置费率，手续费(百度没有)
										responseData.setOrderNumber(orderId);
										responseData
												.setPageUrl(BDUtil
														.generalBDSDKCallStr(pmsAppTrans));
										if (pmsAppTransInfoDao
												.update(pmsAppTrans) == 1) {
											message = SUCCESSMESSAGE;
										}
									}
								}
							}
						} else {
							// 插入数据错误
							logger.info("插入数据错误， 订单号：" + orderId + "，结束时间："
									+ UtilDate.getDateFormatter());
							// 金额小于最小金额
							responseData.setRetCode(1);
							responseData.setRetMessage("系统错误，请重新下单");
							try {
								jsonString = createJsonString(responseData);
							} catch (Exception em) {
								em.printStackTrace();
							}
							return jsonString;
						}
					}
				}
			}
		} else {
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
			retMessage = "充值成功";
		} else if (retMessage.equals("fail")) {
			retMessage = "查询失败";
		} else if (retMessage.equals("sessionInvalidation")) {
			retMessage = "会话失效，请重新登录";
		}
		responseData.setRetCode(retCode);
		responseData.setRetMessage(retMessage);
		jsonString = createJsonString(responseData);
		return jsonString;
	}

	/**
	 * 生成订单异常
	 * 
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@Override
	public String producedOrderException(HttpSession session) throws Exception {
		GeneralUtilityOrderResponseDTO responseData = new GeneralUtilityOrderResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");

		logger.info("[app_rsp]" + createJson(responseData));

		return createJsonString(responseData);
	}

}
