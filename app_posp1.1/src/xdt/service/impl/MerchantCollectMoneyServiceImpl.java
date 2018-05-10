package xdt.service.impl;



import com.hisun.iposm.HiiposmUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.springframework.stereotype.Service;
import xdt.baidu.*;
import xdt.dao.*;
import xdt.dto.*;
import xdt.model.*;
import xdt.offi.OffiPay;
import xdt.schedule.ThreadPool;
import xdt.service.IMerchantCollectMoneyService;
import xdt.service.IPmsAppTransInfoService;
import xdt.service.IPublicTradeVerifyService;
import xdt.servlet.AppPospContext;
import xdt.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商户收款serviceImpl
 * wumeng 20150504
 */
@Service("merchantCollectMoneyService")
public class MerchantCollectMoneyServiceImpl extends BaseServiceImpl implements IMerchantCollectMoneyService {

	@Resource
	private ITAccAccountDao accountDao;//商户账户配置服务层
    @Resource
    private IPmsBusinessInfoDao pmsBusinessInfoDao;
    @Resource
	public IPmsAppTransInfoDao pmsAppTransInfoDao; // 业务配置服务层
    @Resource
    private OffiPay offiPay; //欧飞
    @Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao; // 商户信息服务层
    @Resource
    private IPospTransInfoDAO pospTransInfoDAO;//流水
    @Resource
    private IPmsAppTransInfoService pmsAppTransInfoService;//流水
    @Resource
    private IPmsAppAmountAndRateConfigDao pmsAppAmountAndRateConfigDao;//商户费率配置
    @Resource
	private IPayCmmtufitDao payCmmtufitDao; // 系统支持的银行卡服务层
    @Resource
	private IAppRateConfigDao appRateConfigDao;//费率
    
    @Resource
	private IAmountLimitControlDao  amountLimitControlDao;//最大值最小值总开关判断
    @Resource
    private IPayTypeControlDao payTypeControlDao;//开关
    @Resource
	private IPublicTradeVerifyService publicTradeVerifyService;
    @Resource
	private IPmsAgentInfoDao pmsAgentInfoDao;
    @Resource
	private TTransSettleAgentT0Dao tTransSettleAgentT0Dao;
    @Resource
	private IMerchantMineDao merchantMineDao;
    
    
    
    
    
	private  Logger logger=Logger.getLogger(MerchantCollectMoneyServiceImpl.class);



	 /**
	 * 生成二维码    JSON  or image   目前仅支持json 第一步
	 * wumeng  20150506
	 * @param param
	 * @param request
	 */
	@Override
	public MroducedTwoDimensionResponseDTO producedTwoDimension(String param, SessionInfo sessionInfo) throws Exception {
		logger.info("生成二维码 接收app参数: "+param+"时间："+UtilDate.getDateFormatter());
		MroducedTwoDimensionResponseDTO  mroducedTwoDimensionResponseDTO = new MroducedTwoDimensionResponseDTO();
		ProducedTwoDimensionDTO producedTwoDimensionDTO = (ProducedTwoDimensionDTO)parseJsonString(param,ProducedTwoDimensionDTO.class);

		if(!producedTwoDimensionDTO.equals(DATAPARSINGMESSAGE)){//判断解析数据是否成功
			String mercid = sessionInfo.getMercId();//拿到商户编号
			
			String oAgentNo = sessionInfo.getoAgentNo();//拿到商户的O单号
			
			String payChannel = producedTwoDimensionDTO.getPayChannel();//判断支付渠道    支付渠道 1微信2支付宝3百度
			String pealName= pmsMerchantInfoDao.queryMercuryStatus(mercid);//查询商户是否实名认证
			
			
            if(AUTHENTICATIONFLAG.equals(pealName)){
        		//正式商户
            
            	
            	//查询商户费率 和  最 低收款金额 支付方式是否开通  业务是否开通 等     参数
                Map<String,String> paramMap = new HashMap<String, String>();
               
                AppRateConfig appRateConfig = new AppRateConfig();
                appRateConfig.setoAgentNo(oAgentNo);
    				
                	if("1".equals(payChannel)){
    					//处理微信二维码生成
                		paramMap.put("paymentcode",PaymentCodeEnum.weixinPay.getTypeCode());
                		appRateConfig.setRateType(RateTypeEnum.weixinRateType.getTypeCode());
                		
                		appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRateConfig);
    				}else if("2".equals(payChannel)){
    					//处理支付宝二维码生成
    					paramMap.put("paymentcode",PaymentCodeEnum.zhifubaoPay.getTypeCode());
    					appRateConfig.setRateType(RateTypeEnum.zhifubaoRateType.getTypeCode());
    					appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRateConfig);
    				}else if("3".equals(payChannel)){
    					//处理百度二维码生成
    					paramMap.put("paymentcode",PaymentCodeEnum.baiduPay.getTypeCode());
    					appRateConfig.setRateType(RateTypeEnum.baiduRateType.getTypeCode());
    					appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRateConfig);
    				}
                
                paramMap.put("mercid", mercid);
                paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
                
                //查询  最低、最高收款金额   ，支付方式是否开通 ， 业务是否开通 
                AppRateTypeAndAmount appRateTypeAndAmount = pmsAppAmountAndRateConfigDao.queryAmountAndStatus(paramMap);
                
                String status = appRateTypeAndAmount.getStatus();//此业务是否开通
                String statusMessage = appRateTypeAndAmount.getMessage();//此业务是否开通的描述
                
                String payStatus = appRateTypeAndAmount.getPayStatus();//此支付方式是否开通
            	
            	
            	//判断此业务O单是否开通（总）
            	//444444
            	
                
                ResultInfo resultInfoForOAgentNo =  publicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.merchantCollect,oAgentNo);
                //返回不为0，一律按照交易失败处理
                if(!resultInfoForOAgentNo.getErrCode().equals("0")){
                	mroducedTwoDimensionResponseDTO.setRetCode("22");//返回码
                	if("".equals(resultInfoForOAgentNo.getMsg())||resultInfoForOAgentNo.getMsg()==null){
                		mroducedTwoDimensionResponseDTO.setRetMessage("此功能暂时关闭");//返回信息	
					 }else{
						 mroducedTwoDimensionResponseDTO.setRetMessage(resultInfoForOAgentNo.getMsg());
					 }
                	return mroducedTwoDimensionResponseDTO;
                }
                
                
                
            	
	            if("1".equals(status)){//1表示业务开通
	            	//开通
	            	//判断此支付方式O单是否开通
	            	//33333333
	            	if("1".equals(payChannel)){
						//处理微信二维码生成
	            		//判断支付方式时候开通总开关
	            		ResultInfo payCheckResult = payTypeControlDao.checkLimit(oAgentNo,PaymentCodeEnum.weixinPay.getTypeCode());
	                    if(!payCheckResult.getErrCode().equals("0")){
	                    	//支付方式时候开通总开关 禁用
	    					mroducedTwoDimensionResponseDTO.setRetCode("22");//
	    					
	    					if("".equals(payCheckResult.getMsg())||payCheckResult.getMsg()==null){
	    						mroducedTwoDimensionResponseDTO.setRetMessage("此支付方式暂时关闭");//返回信息	
	    					 }else{
	    						 mroducedTwoDimensionResponseDTO.setRetMessage(payCheckResult.getMsg()); 
	    					 }
	    					
	    					return mroducedTwoDimensionResponseDTO;
	                    }
					}else if("2".equals(payChannel)){
						//处理支付宝二维码生成
						
						//判断支付方式时候开通总开关
	            		ResultInfo payCheckResult = payTypeControlDao.checkLimit(oAgentNo,PaymentCodeEnum.zhifubaoPay.getTypeCode());
	                    if(!payCheckResult.getErrCode().equals("0")){
	                    	//支付方式时候开通总开关 禁用
	    					mroducedTwoDimensionResponseDTO.setRetCode("22");//
	    					if("".equals(payCheckResult.getMsg())||payCheckResult.getMsg()==null){
	    						mroducedTwoDimensionResponseDTO.setRetMessage("此支付方式暂时关闭");//返回信息	
	    					 }else{
	    						 mroducedTwoDimensionResponseDTO.setRetMessage(payCheckResult.getMsg()); 
	    					 }
	    					
	    					return mroducedTwoDimensionResponseDTO;
	                    }
					}else if("3".equals(payChannel)){
						
						//判断支付方式时候开通总开关
	            		ResultInfo payCheckResult = payTypeControlDao.checkLimit(oAgentNo,PaymentCodeEnum.baiduPay.getTypeCode());
	                    if(!payCheckResult.getErrCode().equals("0")){
	                    	//支付方式时候开通总开关 禁用
	    					mroducedTwoDimensionResponseDTO.setRetCode("22");//
	    					if("".equals(payCheckResult.getMsg())||payCheckResult.getMsg()==null){
	    						mroducedTwoDimensionResponseDTO.setRetMessage("此支付方式暂时关闭");//返回信息	
	    					 }else{
	    						 mroducedTwoDimensionResponseDTO.setRetMessage(payCheckResult.getMsg()); 
	    					 }
	    					
	    					return mroducedTwoDimensionResponseDTO;
	                    }
					}
	            	
	            	
            		 if("0".equals(payStatus)){//0表示支付方式开通(微信  支付宝 百度  和包  等)
            			 
            			 if("1".equals(payChannel)||"2".equals(payChannel)){//判断支付宝和微信是不是绑定了路由
            				 Map<String,String> routeMap = new HashMap<String, String>();
            				 routeMap.put("mercid", mercid);
            				 routeMap.put("channelcode", XUNLIAN);
            				int count =  pmsMerchantInfoDao.getChannelCount(routeMap);//查询支付宝和微信是不是绑定了路由
            				 if(count<=0){
            					//此功能暂未开通或被禁用
            						mroducedTwoDimensionResponseDTO.setRetCode("16");//
            						mroducedTwoDimensionResponseDTO.setRetMessage("没有绑定了路由");
            					 return mroducedTwoDimensionResponseDTO; 
            				 }
            			 }
            			 
            			 BigDecimal payAmt = new BigDecimal(producedTwoDimensionDTO.getPayAmt());//收款金额
         				//判读  交易金额是不是在欧单区间控制之内
         				 ResultInfo resultInfo =  amountLimitControlDao.checkLimit(oAgentNo,payAmt,TradeTypeEnum.merchantCollect.getTypeCode());
         	            //返回不为0，一律按照交易失败处理
         	            if(!resultInfo.getErrCode().equals("0")){
         						mroducedTwoDimensionResponseDTO.setRetCode("21");
         						
         						if("".equals(resultInfo.getMsg())||resultInfo.getMsg()==null){
         							mroducedTwoDimensionResponseDTO.setRetMessage("交易金额不在申请的范围之内");//返回信息	
         						 }else{
         							 mroducedTwoDimensionResponseDTO.setRetMessage(resultInfo.getMsg()); 
         						 }
         						
         						return mroducedTwoDimensionResponseDTO; 
         	            }
            			 
      					//MIN_AMOUNT,MAX_AMOUNT ,RATE ,STATUS
            			 String rateStr = appRateConfig.getRate(); //商户费率    RATE
      					
      					
      					BigDecimal min_amount = new  BigDecimal(appRateTypeAndAmount.getMinAmount());//最低收款金额   MIN_AMOUNT
      					BigDecimal max_amount = new  BigDecimal(appRateTypeAndAmount.getMaxAmount());//最高收款金额   MAX_AMOUNT
      					
      					
      					if(min_amount.compareTo(payAmt)!=1){//判断收款金额是否大于最低收款金额   大于等于执行   小于不执行

      						if(payAmt.compareTo(max_amount)!=1){
      							if("1".equals(payChannel)){
      								//处理微信二维码生成
      								mroducedTwoDimensionResponseDTO = this.precreateForWechat(producedTwoDimensionDTO, sessionInfo,rateStr,oAgentNo);
      							}else if("2".equals(payChannel)){
      								//处理支付宝二维码生成
      								mroducedTwoDimensionResponseDTO = this.precreateForAlipay(producedTwoDimensionDTO, sessionInfo,rateStr,oAgentNo);
      							}else if("3".equals(payChannel)){
      								//处理百度二维码生成
      								mroducedTwoDimensionResponseDTO = this.precreate(producedTwoDimensionDTO, sessionInfo,rateStr,oAgentNo);
      							}else{
      								//处理  不支持的渠道
      								mroducedTwoDimensionResponseDTO.setRetCode("2");//2 此支付渠道不支持
      								mroducedTwoDimensionResponseDTO.setRetMessage("此支付渠道不支持");
      							}
      						}else{

      							//交易金额大于收款最高金额
      							mroducedTwoDimensionResponseDTO.setRetCode("3");//
      							mroducedTwoDimensionResponseDTO.setRetMessage("交易金额大于收款最高金额:"+max_amount.divide(new BigDecimal(100)));
      						}


      					}else{
      						//交易金额小于收款最低金额
      						mroducedTwoDimensionResponseDTO.setRetCode("4");
      						mroducedTwoDimensionResponseDTO.setRetMessage("交易金额小于收款最低金额:"+min_amount.divide(new BigDecimal(100)));
      					}

      			 
            		 }else{
            			//支付方式未开通
          				mroducedTwoDimensionResponseDTO.setRetCode("15");//
          				mroducedTwoDimensionResponseDTO.setRetMessage("请提交相关资料,开通此支付方式");
            		 }
            	 }else{
            		//此功能暂未开通或被禁用
     				mroducedTwoDimensionResponseDTO.setRetCode("14");//

		        	 if("".equals(statusMessage)||statusMessage==null){
		        		 	mroducedTwoDimensionResponseDTO.setRetMessage("此功能暂未开通");
						}else{
							mroducedTwoDimensionResponseDTO.setRetMessage(statusMessage);
						}
 				
            	 }
            	
            }else{
            	
				//不是正式商户
 				mroducedTwoDimensionResponseDTO.setRetCode("7");//
 				mroducedTwoDimensionResponseDTO.setRetMessage("不是正式商户");
            }

		}
		logger.info("生成二维码第一步返回数据: "+createJsonString(mroducedTwoDimensionResponseDTO)+"时间："+UtilDate.getDateFormatter());

		return mroducedTwoDimensionResponseDTO;
	}
	/**
	 * 生成二维码    JSON  or image   目前仅支持json  第二步
	 * wumeng  20150506
	 * @param param
	 * @param request
	 * @param session
	 */
	@Override
	public String producedTwoDimension(MroducedTwoDimensionResponseDTO mroducedTwoDimensionResponseDTO) throws Exception {
		logger.info("生成二维码 第二步 时间："+UtilDate.getDateFormatter());
		String  result = "";
		String payChannel = mroducedTwoDimensionResponseDTO.getPayChannel();

		if("1".equals(payChannel)){
			//处理微信二维码生成
			result = this.precreateForWechat(mroducedTwoDimensionResponseDTO.getWechatAndAlipayRequestDTO());
		}else if("2".equals(payChannel)){
			//处理支付宝二维码生成
			result = this.precreateForAlipay(mroducedTwoDimensionResponseDTO.getWechatAndAlipayRequestDTO());
		}else if("3".equals(payChannel)){
			//处理百度二维码生成
			result = this.precreate( mroducedTwoDimensionResponseDTO);
		}

		logger.info("生成二维码返回app参数: "+result+"时间："+UtilDate.getDateFormatter());

		return result;
	}


	/**
	 * 百度生成二维码 JSON 第一步
	 * wumeng  20150506
	 * @param prama
	 * @param sessionInfo
	 * @param rateStr
	 * @param oAgentNo
	 */
	public MroducedTwoDimensionResponseDTO precreate(ProducedTwoDimensionDTO prama, SessionInfo sessionInfo, String rateStr,String oAgentNo) throws Exception{
			logger.info("调用百度生成二维码接口调用第一步开始，时间："+UtilDate.getDateFormatter());

		ViewKyChannelInfo channelInfo = AppPospContext.context.get(BAIDU+BAIDUTWODIMENSION);
		String code_type = "code_type="+Constants.BD_CODE_TYPE;                      //码类型(不参与签名)
		String output_type = "output_type="+Constants.BD_OUTPUT_TYPE;                //输出格式(不参与签名)	0：image；1：json；默认值：0

		String service_code = "service_code="+Constants.BD_SERVICE_CODE;              //服务编号	整数，目前必须为1
		
		String sp_no = "sp_no="+channelInfo.getChannelNO();  //百付宝商户号	10位数字组成的字符串
		
		String order_create_time = "order_create_time="+UtilDate.getOrderNum();	      //创建订单的时间	YYYYMMDDHHMMSS

		String orderid = UtilMethod.getOrderid("100");//10业务号0业务细分
		String order_no	= "order_no=" + orderid;                 //订单号，商户须保证订单号在商户系统内部唯一。	不超过20个字符

		String sign_goods_name = "goods_name="+BDUtil.encoder("商户收款");  //商品的名称

		String goods_name = "goods_name="+URLEncoder.encode(BDUtil.encoder("商户收款"),"gbk");  //商品的名称

		String total_amount	= "total_amount="+prama.getPayAmt();        //总金额，以分为单位	非负整数

		String currency	= "currency="+Constants.BD_CURRENCY;                  //币种，默认人民币	取值范围参见附录

		String   baiduReturnUrl = channelInfo.getCallbackurl(); //百付宝主动通知商户支付结果的URL
		 //百付宝主动通知商户支付结果的URL
		String return_url = "return_url="+URLEncoder.encode(baiduReturnUrl,"gbk");

		 //百付宝主动通知商户支付结果的URL
		String sign_return_url = "return_url="+baiduReturnUrl;

		String pay_type =	"pay_type="+Constants.BD_PAY_TYPE ;				     // 默认支付方式

		String bank_no = "bank_no=210";											 //网银支付或银行网关支付时，默认银行的编码  210招商银行    此参数只是提搞用户操作性 无实际意义

		String input_charset  =	"input_charset="+Constants.BD_INPUT_CHARSET;     //请求参数的字符编码	取值范围参见附录

		String version = "version="+Constants.BD_VERSION;	                     //接口的版本号	必须为2

		String sign_method	= "sign_method="+Constants.BD_SIGN_METHOD;           //签名方法

		//用于签名
		String [] array ={service_code,sp_no,order_create_time,order_no,sign_goods_name,total_amount,
				currency,sign_return_url,pay_type,bank_no,input_charset,version,sign_method};
		//用于传参数
		String [] array1 ={code_type,output_type,service_code,sp_no,order_create_time,order_no,goods_name,total_amount,
				currency,return_url,pay_type,bank_no,input_charset,version,sign_method};


		//订单先入本地库，掉接口后修改订单
		boolean flag = insertOrder(orderid, prama.getPayAmt(), sessionInfo.getMercId(),rateStr,BAIDUTWODIMENSION, oAgentNo);

		MroducedTwoDimensionResponseDTO mroducedTwoDimensionResponseDTO = new MroducedTwoDimensionResponseDTO();
		String retMessage="";
		String retCode="";

		if(flag){
			retCode ="0";
			retMessage = "生成订单和流水成功 ";

		}else{
			retCode ="1";
			retMessage = "生成订单和流水失败 ";
		}

		mroducedTwoDimensionResponseDTO.setRetCode(retCode);
		mroducedTwoDimensionResponseDTO.setRetMessage(retMessage);
		mroducedTwoDimensionResponseDTO.setOrderNumber(orderid);
		mroducedTwoDimensionResponseDTO.setPayChannel("3");
		mroducedTwoDimensionResponseDTO.setoAgentNo(oAgentNo);
		mroducedTwoDimensionResponseDTO.setArray(array);
		mroducedTwoDimensionResponseDTO.setArray1(array1);
		
		logger.info("调用百度生成二维码接口调用第一步返回："+createJsonString(mroducedTwoDimensionResponseDTO) +"，结束时间："+ UtilDate.getDateFormatter());
		return mroducedTwoDimensionResponseDTO;
	}
	/**
	 * 百度生成二维码 第二步
	 * wumeng  20150506
	 * @param prama
	 * @param request
	 * @param session
	 */
	public String precreate(MroducedTwoDimensionResponseDTO mroducedTwoDimensionResponseDTO) throws Exception{
		logger.info("调用百度生成二维码接口调用第二步开始，时间："+UtilDate.getDateFormatter());
		
		String [] array = mroducedTwoDimensionResponseDTO.getArray();
		String [] array1 = mroducedTwoDimensionResponseDTO.getArray1();
		String orderid = mroducedTwoDimensionResponseDTO.getOrderNumber();
		String oAgentNo = mroducedTwoDimensionResponseDTO.getoAgentNo();
		
		ViewKyChannelInfo channelInfo = AppPospContext.context.get(BAIDU+BAIDUTWODIMENSION);
		
		String result =new  BDHttpClient().execute(array,array1, channelInfo.getUrl(),oAgentNo);
		
		BaiDuDTO baiDuDTO = (BaiDuDTO)parseJsonString(result,BaiDuDTO.class);
		
		MroducedTwoDimensionResponseDTO mroducedTwoDimensionResponseDTO1 = new MroducedTwoDimensionResponseDTO();
		
		String retMessage="";

		if("0".equals(baiDuDTO.getRet())){
			retMessage = "生成二维码成功 ";
			//修改订单第三方返回码
			updateOrder(baiDuDTO.getRet(),orderid);
			//线程查询处理百度订单处理结果并对本地订单进行处理
			ThreadPool.executor(new BaiDuTaskThread(orderid, pmsAppTransInfoDao,accountDao,pmsAppTransInfoService,pospTransInfoDAO,pmsAgentInfoDao));

		}else{
			retMessage = "生成二维码失败 ";
			//修改订单第三方返回码
			updateOrder(baiDuDTO.getRet(), orderid);
			baiDuDTO.setRet("1");
		}

		mroducedTwoDimensionResponseDTO1.setRetCode(baiDuDTO.getRet());
		mroducedTwoDimensionResponseDTO1.setRetMessage(retMessage);
		mroducedTwoDimensionResponseDTO1.setTwoDimensionContent(baiDuDTO.getContent());
		mroducedTwoDimensionResponseDTO1.setOrderNumber(orderid);

		

		result = createJsonString(mroducedTwoDimensionResponseDTO1);

		logger.info("调用百度生成二维码接口调用结束， 订单号："+orderid +" 反回前台参数："+result+"，结束时间："+ UtilDate.getDateFormatter());
		return result;
	}


	/**
	 * 微信生成二维码 JSON  第一步
	 * wumeng  20150512
	 * @param prama
	 * @param sessionInfo
	 * @param rateStr
	 * @param oAgentNo
	 */
	public MroducedTwoDimensionResponseDTO precreateForWechat(ProducedTwoDimensionDTO prama, SessionInfo sessionInfo, String rateStr,String oAgentNo) throws Exception{
		logger.info("调用微信生成二维码接口调用第一步开始，时间："+UtilDate.getDateFormatter());

		String  orderid = "";//订单号
		String mercid = sessionInfo.getMercId();//商户编号

		//组装报文
		 orderid = UtilMethod.getOrderid("102");  //   10业务号2业务细;	订单号      现根据规则生成订单号

		 String  totalAmount = prama.getPayAmt();         //交易金额


		 String  paymenttype = "022";                     //支付类型    支付宝 :012二维码(c2b)、011 付码 (b2c)    微信:  022二维码(c2b)  021付码（b2c）
		 String  transType = "32";                        //交易类型   31：付款码支付（终端主拍）32：用户扫二维码支付（终端被拍）

		WechatAndAlipayRequestDTO wechatAndAlipayRequestDTO = new WechatAndAlipayRequestDTO();
		wechatAndAlipayRequestDTO.setOrderNo(orderid);
		wechatAndAlipayRequestDTO.setTotalAmount(totalAmount);
		wechatAndAlipayRequestDTO.setPaymenttype(paymenttype);
		wechatAndAlipayRequestDTO.setTransType(transType);
		wechatAndAlipayRequestDTO.setMerInfo(mercid);
		wechatAndAlipayRequestDTO.setoAgentNo(oAgentNo);

		//订单入库    调讯联接口后在修改处理状态
		String paymenttypeFlag = "02" ;//支付类型标记位  01支付宝   02微信
		boolean flag = insertOrderForXL(orderid, prama.getPayAmt(), mercid,paymenttypeFlag,rateStr,XUNLIANWECHATTWODIMENSION, oAgentNo);
		MroducedTwoDimensionResponseDTO mroducedTwoDimensionResponseDTO = new MroducedTwoDimensionResponseDTO();
		String retMessage="";
		String retCode="";

		if(flag){
			retCode ="0";
			retMessage = "生成订单和流水成功 ";

		}else{
			retCode ="1";
			retMessage = "生成订单和流水失败 ";
		}

		mroducedTwoDimensionResponseDTO.setRetCode(retCode);
		mroducedTwoDimensionResponseDTO.setRetMessage(retMessage);
		mroducedTwoDimensionResponseDTO.setWechatAndAlipayRequestDTO(wechatAndAlipayRequestDTO);
		mroducedTwoDimensionResponseDTO.setPayChannel("1");

		logger.info("调用微信生成二维码接口调用第一步返回："+createJsonString(mroducedTwoDimensionResponseDTO)+"，结束时间："+ UtilDate.getDateFormatter());
		return mroducedTwoDimensionResponseDTO;
	}
	/**
	 * 微信生成二维码 JSON  第二步
	 * wumeng  20150512
	 * @param prama
	 * @param request
	 * @param session
	 * @param premiumRate
	 */
	public String precreateForWechat(WechatAndAlipayRequestDTO wechatAndAlipayRequestDTO) throws Exception{
		logger.info("调用微信生成二维码接口调用第二步开始，时间："+UtilDate.getDateFormatter());

	
		String result = "";
		String orderid = wechatAndAlipayRequestDTO.getOrderNo();
		
		String sendString = "param="+ createJsonString(wechatAndAlipayRequestDTO);//向讯联发送的json串
		
		MroducedTwoDimensionResponseDTO mroducedTwoDimensionResponseDTO = new MroducedTwoDimensionResponseDTO();

        
        ViewKyChannelInfo channelInfo = AppPospContext.context.get(XUNLIAN+XUNLIANWECHATTWODIMENSION);

        logger.info("调用三方前置讯联接口请求参数：" + sendString + "，结束时间：" + UtilDate.getDateFormatter());
        
        //向pre发送报文 //调用pre接口（8583）
        String successFlag = HttpURLConection.httpURLConnectionPOST(channelInfo.getUrl(), sendString);

        logger.info("调用三方前置讯联接口返回参数：" + successFlag + "，结束时间：" + UtilDate.getDateFormatter());

        XunlianGeneral2DecimalResponseDTO  response = (XunlianGeneral2DecimalResponseDTO)parseJsonString(successFlag,XunlianGeneral2DecimalResponseDTO.class);

        String retMessage="";
		String status="";
		String content="";
        

        if(response != null && StringUtils.isNotBlank(response.getRetCode()) 
        		&& StringUtils.isNotBlank(response.getTwoDecimal())
        		&&StringUtils.isNotBlank(response.getSerialNo())){
        	
    		//讯联返回00：交易成功 09：交易处理中（请重试）
    		//后台返回给前台APP  0生成二维码成功 1 生成二维码失败 100 系统异常

    		if("0000".equals(response.getRetCode())){
    			retMessage = "生成二维码成功 ";
    			content =response.getTwoDecimal();  //生成二维码成功返回
    			status="0";
                //修改流水表通道相关
                updatePosBusAndPosNumTransInfoByOrderId(orderid,response.getBusInfo(),response.getBusPos(),response.getPayAmount());
    			//线程查询处讯联订单处理结果并对本地订单进行处理
    			ThreadPool.executor(new XLTaskThread(orderid,response.getSerialNo(),wechatAndAlipayRequestDTO.getMerInfo(),"025",response.getTradeTime(),response.getSearchNum(), pmsAppTransInfoDao,accountDao,pmsAppTransInfoService,pospTransInfoDAO,pmsAgentInfoDao));

    		}else {
    			status = response.getRetCode();
    			retMessage = response.getRetMessage();
    		}
            
        }else{
        	status="1";
            retMessage = "生成二维码失败 ";
        }

		updateOrder(response.getRetCode(),orderid,response.getSerialNo(),response.getTradeTime(),response.getSearchNum());
		mroducedTwoDimensionResponseDTO.setRetCode(status);
		mroducedTwoDimensionResponseDTO.setRetMessage(retMessage);
		mroducedTwoDimensionResponseDTO.setTwoDimensionContent(content);
		mroducedTwoDimensionResponseDTO.setOrderNumber(orderid);

		

		result = createJsonString(mroducedTwoDimensionResponseDTO);


		logger.info("调用微信生成二维码接口调用结束， 订单号："+orderid+" 反回前台参数："+result+"，结束时间："+ UtilDate.getDateFormatter());
		return result;
	}
	/**
	 * 支付宝生成二维码 JSON 第一步
	 * wumeng  20150512
	 * @param prama
	 * @param request
	 * @param session
	 */
	public MroducedTwoDimensionResponseDTO precreateForAlipay(ProducedTwoDimensionDTO prama,SessionInfo sessionInfo, String rateStr,String oAgentNo) throws Exception{
			logger.info("调用支付宝生成二维码接口调用第一步开始，时间："+UtilDate.getDateFormatter());

		String  orderid = "";//订单号
		
		String mercid = sessionInfo.getMercId();//商户编号
		//组装报文
		 orderid = UtilMethod.getOrderid("104");  //   10业务号4业务细;	订单号      现根据规则生成订单号

		 String  totalAmount = prama.getPayAmt();         //交易金额


		 String  paymenttype = "012";                     //支付类型    支付宝 :012二维码(c2b)、011 付码 (b2c)    微信:  022二维码(c2b)  021付码（b2c）
		 String  transType = "32";                        //交易类型   31：付款码支付（终端主拍）32：用户扫二维码支付（终端被拍）

		WechatAndAlipayRequestDTO wechatAndAlipayRequestDTO = new WechatAndAlipayRequestDTO();
		wechatAndAlipayRequestDTO.setOrderNo(orderid);
		wechatAndAlipayRequestDTO.setTotalAmount(totalAmount);
		wechatAndAlipayRequestDTO.setPaymenttype(paymenttype);
		wechatAndAlipayRequestDTO.setTransType(transType);
		wechatAndAlipayRequestDTO.setMerInfo(mercid);
		wechatAndAlipayRequestDTO.setoAgentNo(oAgentNo);


        MroducedTwoDimensionResponseDTO mroducedTwoDimensionResponseDTO = new MroducedTwoDimensionResponseDTO();

        //订单入库    调讯联接口后在修改处理状态
		String paymenttypeFlag = "01" ;//支付类型标记位  01支付宝   02微信

		boolean flag = insertOrderForXL(orderid, prama.getPayAmt(),mercid,paymenttypeFlag,rateStr,XUNLIANALIPAYTWODIMENSION,oAgentNo);


		String retMessage="";
		String retCode="";

		if(flag){

			retCode ="0";
			retMessage = "生成订单和流水成功 ";
		}else{
			retCode ="1";
			retMessage = "生成订单和流水失败 ";
		}

		mroducedTwoDimensionResponseDTO.setRetCode(retCode);
		mroducedTwoDimensionResponseDTO.setRetMessage(retMessage);
		mroducedTwoDimensionResponseDTO.setWechatAndAlipayRequestDTO(wechatAndAlipayRequestDTO);
		mroducedTwoDimensionResponseDTO.setPayChannel("2");

		logger.info("调用支付宝生成二维码接口调用第一步返回， 订单号："+orderid+"返回信息："+createJsonString(mroducedTwoDimensionResponseDTO)+"，结束时间："+ UtilDate.getDateFormatter());
		return mroducedTwoDimensionResponseDTO;
	}
	/**
	 * 支付宝生成二维码 JSON  第二步
	 * wumeng  20150512
	 * @param prama
	 * @param request
	 * @param session
	 * @param premiumRate
	 */
	public String precreateForAlipay(WechatAndAlipayRequestDTO wechatAndAlipayRequestDTO) throws Exception{
		logger.info("调用支付宝生成二维码接口调用第二步开始，时间："+UtilDate.getDateFormatter());

		String result = "";
		String orderid = wechatAndAlipayRequestDTO.getOrderNo();
		
		String sendString = "param="+ createJsonString(wechatAndAlipayRequestDTO);//向讯联发送的json串
		
		MroducedTwoDimensionResponseDTO mroducedTwoDimensionResponseDTO = new MroducedTwoDimensionResponseDTO();

        //调用pre接口（8583）
        ViewKyChannelInfo channelInfo = AppPospContext.context.get(XUNLIAN+XUNLIANALIPAYTWODIMENSION);

        logger.info("调用三方前置讯联接口请求参数：" + sendString + "，结束时间：" + UtilDate.getDateFormatter());
        
        //向pre发送报文
        String successFlag = HttpURLConection.httpURLConnectionPOST(channelInfo.getUrl(), sendString);

        logger.info("调用三方前置讯联接口返回参数：" + successFlag + "，结束时间：" + UtilDate.getDateFormatter());

        XunlianGeneral2DecimalResponseDTO  response = (XunlianGeneral2DecimalResponseDTO)parseJsonString(successFlag,XunlianGeneral2DecimalResponseDTO.class);

        String retMessage="";
		String status="";
		String content="";
        

        if(response != null && StringUtils.isNotBlank(response.getRetCode()) 
        		&& StringUtils.isNotBlank(response.getTwoDecimal())
        		&&StringUtils.isNotBlank(response.getSerialNo())){
        	
    		//讯联返回00：交易成功 09：交易处理中（请重试）
    		//后台返回给前台APP  0生成二维码成功 1 生成二维码失败 100 系统异常

    		if("0000".equals(response.getRetCode())){
    			retMessage = "生成二维码成功 ";
    			content =response.getTwoDecimal();  //生成二维码成功返回
    			status="0";
                //修改流水表通道相关
                updatePosBusAndPosNumTransInfoByOrderId(orderid,response.getBusInfo(),response.getBusPos(),response.getPayAmount());
    			//线程查询处讯联订单处理结果并对本地订单进行处理
    			ThreadPool.executor(new XLTaskThread(orderid,response.getSerialNo(),wechatAndAlipayRequestDTO.getMerInfo(),"015",response.getTradeTime(),response.getSearchNum(), pmsAppTransInfoDao,accountDao,pmsAppTransInfoService,pospTransInfoDAO,pmsAgentInfoDao));

    		}else {
    			status = response.getRetCode();
    			retMessage = response.getRetMessage();
    		}
            
        }else{
        	status="1";
            retMessage = "生成二维码失败 ";
        }

		updateOrder(response.getRetCode(),orderid,response.getSerialNo(),response.getTradeTime(),response.getSearchNum());
		mroducedTwoDimensionResponseDTO.setRetCode(status);
		mroducedTwoDimensionResponseDTO.setRetMessage(retMessage);
		mroducedTwoDimensionResponseDTO.setTwoDimensionContent(content);
		mroducedTwoDimensionResponseDTO.setOrderNumber(orderid);

		

		result = createJsonString(mroducedTwoDimensionResponseDTO);


		logger.info("调用支付宝生成二维码接口调用结束， 订单号："+orderid+" 反回前台参数："+result+"，结束时间："+ UtilDate.getDateFormatter());
		return result;
	}

	/**
	 * 扫码    用户扫商户  第一步 生成订单和流水
	 * wumeng  20150508
	 * @param prama
	 * @param request
	 * @param session
	 */
	@Override
	public MroducedTwoDimensionResponseDTO producedScanCodeOrder(String prama, SessionInfo sessionInfo) throws Exception {
		logger.info("付码（用户扫商户）接收app参数: "+prama+"时间："+UtilDate.getDateFormatter());
		
		ProducedTwoDimensionDTO producedTwoDimensionDTO = (ProducedTwoDimensionDTO)parseJsonString(prama,ProducedTwoDimensionDTO.class);
		
		MroducedTwoDimensionResponseDTO mroducedTwoDimensionResponseDTO = new MroducedTwoDimensionResponseDTO();
		
		if(!producedTwoDimensionDTO.equals(DATAPARSINGMESSAGE)){//判断解析数据是否成功
			
			String mercid = sessionInfo.getMercId();//拿到商户编号
			
			String oAgentNo = sessionInfo.getoAgentNo();//拿到商户的O单号
			
			String payChannel = producedTwoDimensionDTO.getPayChannel();//判断支付渠道    支付渠道 1微信2支付宝3百度
			String pealName= pmsMerchantInfoDao.queryMercuryStatus(mercid);//查询商户是否实名认证
			
			
			 if(AUTHENTICATIONFLAG.equals(pealName)){
				//正式商户
				//查询商户费率 和  最 低收款金额 支付方式是否开通  业务是否开通 等     参数
		            Map<String,String> paramMap = new HashMap<String, String>();
		            
		           
		            AppRateConfig appRateConfig = new AppRateConfig();
		            appRateConfig.setoAgentNo(oAgentNo);
						
		            	if("1".equals(payChannel)){
							//处理微信二维码生成
		            		paramMap.put("paymentcode",PaymentCodeEnum.weixinPay.getTypeCode());
		            		appRateConfig.setRateType(RateTypeEnum.weixinRateType.getTypeCode());
		            		
		            		appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRateConfig);
						}else if("2".equals(payChannel)){
							//处理支付宝二维码生成
							paramMap.put("paymentcode",PaymentCodeEnum.zhifubaoPay.getTypeCode());
							appRateConfig.setRateType(RateTypeEnum.zhifubaoRateType.getTypeCode());
							appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRateConfig);
						}else if("3".equals(payChannel)){
							//处理百度二维码生成
							paramMap.put("paymentcode",PaymentCodeEnum.baiduPay.getTypeCode());
							appRateConfig.setRateType(RateTypeEnum.baiduRateType.getTypeCode());
							appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRateConfig);
						}else if("4".equals(payChannel)){
							//处理移动和包
							paramMap.put("paymentcode",PaymentCodeEnum.ydhbPay.getTypeCode());
							appRateConfig.setRateType(RateTypeEnum.hebaoRateType.getTypeCode());
							appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRateConfig);
						}
		            
		            paramMap.put("mercid", mercid);
		            paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
		            
		            //查询  最低、最高收款金额   ，支付方式是否开通 ， 业务是否开通 
		            AppRateTypeAndAmount appRateTypeAndAmount = pmsAppAmountAndRateConfigDao.queryAmountAndStatus(paramMap);
		            
		            String status = appRateTypeAndAmount.getStatus();//此业务是否开通
		            String statusMessage = appRateTypeAndAmount.getMessage();//此业务是否开通的描述
		            
		            String payStatus = appRateTypeAndAmount.getPayStatus();//此支付方式是否开通
					
					
		          //判断此业务O单是否开通（总）
	            	//444444
	            	
		            ResultInfo resultInfoForOAgentNo =  publicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.merchantCollect,oAgentNo);
	                //返回不为0，一律按照交易失败处理
	                if(!resultInfoForOAgentNo.getErrCode().equals("0")){
	                	mroducedTwoDimensionResponseDTO.setRetCode("22");//返回码
	                	if("".equals(resultInfoForOAgentNo.getMsg())||resultInfoForOAgentNo.getMsg()==null){
	                		mroducedTwoDimensionResponseDTO.setRetMessage("此功能暂时关闭");//返回信息	
						 }else{
							 mroducedTwoDimensionResponseDTO.setRetMessage(resultInfoForOAgentNo.getMsg());
						 }
	                	return mroducedTwoDimensionResponseDTO;
	                }
	                
		            	
				 
				 
				 
				 
				 if("1".equals(status)){//1表示业务开通
		              	//开通		 
				 
					//判断此支付方式O单是否开通
		            	//33333333
					 if("1".equals(payChannel)){
							//处理微信二维码生成
		            		//判断支付方式时候开通总开关
		            		ResultInfo payCheckResult = payTypeControlDao.checkLimit(oAgentNo,PaymentCodeEnum.weixinPay.getTypeCode());
		                    if(!payCheckResult.getErrCode().equals("0")){
		                    	//支付方式时候开通总开关 禁用
		    					mroducedTwoDimensionResponseDTO.setRetCode("22");//
		    					if("".equals(payCheckResult.getMsg())||payCheckResult.getMsg()==null){
		    						mroducedTwoDimensionResponseDTO.setRetMessage("此支付方式暂时关闭");//返回信息	
		    					 }else{
		    						 mroducedTwoDimensionResponseDTO.setRetMessage(payCheckResult.getMsg()); 
		    					 }
		    					
		    					return mroducedTwoDimensionResponseDTO;
		                    }
						}else if("2".equals(payChannel)){
							//处理支付宝二维码生成
							
							//判断支付方式时候开通总开关
		            		ResultInfo payCheckResult = payTypeControlDao.checkLimit(oAgentNo,PaymentCodeEnum.zhifubaoPay.getTypeCode());
		                    if(!payCheckResult.getErrCode().equals("0")){
		                    	//支付方式时候开通总开关 禁用
		    					mroducedTwoDimensionResponseDTO.setRetCode("22");//
		    					if("".equals(payCheckResult.getMsg())||payCheckResult.getMsg()==null){
		    						mroducedTwoDimensionResponseDTO.setRetMessage("此支付方式暂时关闭");//返回信息	
		    					 }else{
		    						 mroducedTwoDimensionResponseDTO.setRetMessage(payCheckResult.getMsg()); 
		    					 }
		    					
		    					return mroducedTwoDimensionResponseDTO;
		                    }
						}else if("3".equals(payChannel)){
							
							//判断支付方式时候开通总开关
		            		ResultInfo payCheckResult = payTypeControlDao.checkLimit(oAgentNo,PaymentCodeEnum.baiduPay.getTypeCode());
		                    if(!payCheckResult.getErrCode().equals("0")){
		                    	//支付方式时候开通总开关 禁用
		    					mroducedTwoDimensionResponseDTO.setRetCode("22");//
		    					if("".equals(payCheckResult.getMsg())||payCheckResult.getMsg()==null){
		    						mroducedTwoDimensionResponseDTO.setRetMessage("此支付方式暂时关闭");//返回信息	
		    					 }else{
		    						 mroducedTwoDimensionResponseDTO.setRetMessage(payCheckResult.getMsg()); 
		    					 }
		    					
		    					return mroducedTwoDimensionResponseDTO;
		                    }
						}else if("4".equals(payChannel)){
							//处理移动和包
							
							//判断支付方式时候开通总开关
		            		ResultInfo payCheckResult = payTypeControlDao.checkLimit(oAgentNo,PaymentCodeEnum.ydhbPay.getTypeCode());
		                    if(!payCheckResult.getErrCode().equals("0")){
		                    	//支付方式时候开通总开关 禁用
		    					mroducedTwoDimensionResponseDTO.setRetCode("22");//
		    					if("".equals(payCheckResult.getMsg())||payCheckResult.getMsg()==null){
		    						mroducedTwoDimensionResponseDTO.setRetMessage("此支付方式暂时关闭");//返回信息	
		    					 }else{
		    						 mroducedTwoDimensionResponseDTO.setRetMessage(payCheckResult.getMsg()); 
		    					 }
		    					
		    					return mroducedTwoDimensionResponseDTO;
		                    }
						}
					 
					 
					 
					 
					 
						if("0".equals(payStatus)){//0表示支付方式开通
							
							if("1".equals(payChannel)||"2".equals(payChannel)){//判断支付宝和微信是不是绑定了路由
	            				 Map<String,String> routeMap = new HashMap<String, String>();
	            				 routeMap.put("mercid", mercid);
	            				 routeMap.put("channelcode", XUNLIAN);
	            				int count =  pmsMerchantInfoDao.getChannelCount(routeMap);//查询支付宝和微信是不是绑定了路由
	            				 if(count<=0){
	            					//此功能暂未开通或被禁用
	            						mroducedTwoDimensionResponseDTO.setRetCode("16");//
	            						mroducedTwoDimensionResponseDTO.setRetMessage("没有绑定了路由");
	            					 return mroducedTwoDimensionResponseDTO; 
	            				 }
	            			 }
							
							
							BigDecimal payAmt = new BigDecimal(producedTwoDimensionDTO.getPayAmt());//收款金额
							
							
							//判读  交易金额是不是在欧单区间控制之内
							 ResultInfo resultInfo =  amountLimitControlDao.checkLimit(oAgentNo,payAmt,TradeTypeEnum.merchantCollect.getTypeCode());
				             //返回不为0，一律按照交易失败处理
				             if(!resultInfo.getErrCode().equals("0")){
									mroducedTwoDimensionResponseDTO.setRetCode("21");
									if("".equals(resultInfo.getMsg())||resultInfo.getMsg()==null){
										mroducedTwoDimensionResponseDTO.setRetMessage("交易金额不在申请的范围之内");//返回信息	
									 }else{
										 mroducedTwoDimensionResponseDTO.setRetMessage(resultInfo.getMsg()); 
									 }
									return mroducedTwoDimensionResponseDTO; 
				             }
							
							
							//MIN_AMOUNT,MAX_AMOUNT ,RATE ,STATUS
							String rateStr = appRateConfig.getRate();//商户费率    RATE
							
							BigDecimal min_amount = new  BigDecimal(appRateTypeAndAmount.getMinAmount());//最低收款金额   MIN_AMOUNT
							BigDecimal max_amount = new  BigDecimal(appRateTypeAndAmount.getMaxAmount());//最高收款金额   MAX_AMOUNT
		
							
							if(min_amount.compareTo(payAmt)!=1){//判断收款金额是否大于最低收款金额   大于等于执行   小于不执行
		
								if(payAmt.compareTo(max_amount)!=1){
									//判断收款金额是否大于最低收款金额   大于等于执行   小于不执行
									
		
									if("1".equals(payChannel)){
										//处理微信
										mroducedTwoDimensionResponseDTO = this.wechatPay(producedTwoDimensionDTO, sessionInfo,rateStr,oAgentNo);
									}else if("2".equals(payChannel)){
										//处理支付宝
										mroducedTwoDimensionResponseDTO = this.alipayPay(producedTwoDimensionDTO, sessionInfo,rateStr,oAgentNo);
									}else if("3".equals(payChannel)){
										//处理百度
										mroducedTwoDimensionResponseDTO = this.baiDuPay(producedTwoDimensionDTO, sessionInfo,rateStr,oAgentNo);
									}else if("4".equals(payChannel)){
										//处理移动和包
										mroducedTwoDimensionResponseDTO = this.cmpay(producedTwoDimensionDTO, sessionInfo,rateStr,oAgentNo);
									}else{
										//处理  不支持的渠道
										
										mroducedTwoDimensionResponseDTO.setRetCode("2");//2 此支付渠道不支持
										mroducedTwoDimensionResponseDTO.setRetMessage("此支付渠道不支持");
									}
									
								}else{
		
									//交易金额大于收款最高金额
									mroducedTwoDimensionResponseDTO.setRetCode("3");//
									mroducedTwoDimensionResponseDTO.setRetMessage("交易金额大于收款最高金额:"+max_amount.divide(new BigDecimal(100)));
								}
		
		
							}else{
								//交易金额小于收款最低金额
								mroducedTwoDimensionResponseDTO.setRetCode("4");
								mroducedTwoDimensionResponseDTO.setRetMessage("交易金额小于收款最低金额:"+min_amount.divide(new BigDecimal(100)));
							}
					 
				 }else{
	        			//支付方式未开通
	          				mroducedTwoDimensionResponseDTO.setRetCode("15");//
	          				mroducedTwoDimensionResponseDTO.setRetMessage("请提交相关资料,开通此支付方式"); 
	        		 } 
					 
				 }else{
					//此功能暂未开通或被禁用
						mroducedTwoDimensionResponseDTO.setRetCode("14");//


			        	 if("".equals(statusMessage)||statusMessage==null){
			        		 	mroducedTwoDimensionResponseDTO.setRetMessage("此功能暂未开通");
							}else{
								mroducedTwoDimensionResponseDTO.setRetMessage(statusMessage);
							}
				 }
				 
	         }else{
	        	//不是正式商户
					mroducedTwoDimensionResponseDTO.setRetCode("7");
					mroducedTwoDimensionResponseDTO.setRetMessage("不是正式商户");
	         	
	         }
			
		}

		logger.info("付码（用户扫商户）返回app参数: "+mroducedTwoDimensionResponseDTO+"时间："+UtilDate.getDateFormatter());
		return mroducedTwoDimensionResponseDTO;
	}

	
	
	
	/**
	 * 反扫    用户扫商户  第二步 完成剩余操作
	 * wumeng  20150508
	 * @param prama
	 * @param request
	 * @param session
	 */
	@Override
	public String producedScanCodeOrder(MroducedTwoDimensionResponseDTO mroducedTwoDimensionResponseDTO) throws Exception {
		String  result = "";
		logger.info("付码（用户扫商户）第二步 时间："+UtilDate.getDateFormatter());
		String payChannel = mroducedTwoDimensionResponseDTO.getPayChannel();//判断支付渠道    支付渠道 1微信2支付宝3百度

		if("1".equals(payChannel)){
			//处理微信
			result = this.wechatPay(mroducedTwoDimensionResponseDTO.getWechatAndAlipayRequestDTO());
		}else if("2".equals(payChannel)){
			//处理支付宝
			result = this.alipayPay(mroducedTwoDimensionResponseDTO.getWechatAndAlipayRequestDTO());
		}else if("3".equals(payChannel)){
			//处理百度
			result = this.baiDuPay(mroducedTwoDimensionResponseDTO);
		}else if("4".equals(payChannel)){
			//处理移动和包
			result = this.cmpay(mroducedTwoDimensionResponseDTO.getyDHBRequestDTO());
		}

		logger.info("付码（用户扫商户）返回app参数: "+result+"时间："+UtilDate.getDateFormatter());
		return result;
	}

	/**
	 * 扫描用户的百度钱包付款   第一步
	 * wumeng  20150507
	 * @param prama
	 * @param request
	 * @param session
	 * @param premiumRate
	 */
	public MroducedTwoDimensionResponseDTO baiDuPay(ProducedTwoDimensionDTO prama,SessionInfo  sessionInfo,String rateStr,String oAgentNo) throws Exception {
		logger.info("调用用户的百度钱包付款接口调用第一步开始，时间："+UtilDate.getDateFormatter());
		ViewKyChannelInfo channelInfo = AppPospContext.context.get(BAIDU+BAIDUSCANCODE);
		String service_code = "service_code="+Constants.BD_SERVICE_CODE;          //服务编号	整数，目前必须为1
		String sp_no = "sp_no="+channelInfo.getChannelNO();                        //百付宝商户号	10位数字组成的字符串
		String order_create_time = "order_create_time="+UtilDate.getOrderNum();	      //创建订单的时间	YYYYMMDDHHMMSS

		String orderid = UtilMethod.getOrderid("101");			//10业务号1业务细分
		String order_no	= "order_no=" + orderid;                 //订单号，商户须保证订单号在商户系统内部唯一。	不超过20个字符

		String sign_goods_name = "goods_name="+BDUtil.encoder("商户收款");  //商品的名称

		String goods_name = "goods_name="+URLEncoder.encode(BDUtil.encoder("商户收款"), "gbk");  //商品的名称

		String total_amount	= "total_amount="+prama.getPayAmt();                             //总金额，以分为单位	非负整数

		String currency	= "currency="+Constants.BD_CURRENCY;                  //币种，默认人民币	取值范围参见附录

		String  baiduResultUrl = channelInfo.getCallbackurl();
		 //百付宝主动通知商户支付结果的URL
		String return_url = "return_url="+URLEncoder.encode(baiduResultUrl,"gbk");

		 //百付宝主动通知商户支付结果的URL
		String sign_return_url = "return_url="+baiduResultUrl;

		String input_charset  =	"input_charset="+Constants.BD_INPUT_CHARSET;	                                  //请求参数的字符编码	取值范围参见附录

		String version = "version="+Constants.BD_VERSION;	                  //接口的版本号	必须为2

		String sign_method	= "sign_method="+Constants.BD_SIGN_METHOD;           //签名方法	取值范围参见附录

		String  pay_code = "pay_code="+prama.getPayCode();                                       //付款码	付款码。不超过18位；前缀：31

		//用于签名
		String [] array ={service_code,sp_no,order_create_time,order_no,sign_goods_name,total_amount,
				currency,sign_return_url,pay_code,input_charset,version,sign_method};

		//用于传参数
		String [] array1 ={service_code,sp_no,order_create_time,order_no,goods_name,total_amount,
				currency,return_url,pay_code,input_charset,version,sign_method};



		
		//订单先入本地库，掉接口后修改订单
		boolean flag = insertOrder(orderid, prama.getPayAmt(),sessionInfo.getMercId(),rateStr,BAIDUSCANCODE,oAgentNo);

		MroducedTwoDimensionResponseDTO mroducedTwoDimensionResponseDTO = new MroducedTwoDimensionResponseDTO();
		String retMessage="";
		String retCode="";

		if(flag){
			retCode ="0";
			retMessage = "生成订单和流水成功 ";

		}else{
			retCode ="1";
			retMessage = "生成订单和流水失败 ";
		}

		mroducedTwoDimensionResponseDTO.setRetCode(retCode);
		mroducedTwoDimensionResponseDTO.setRetMessage(retMessage);
		mroducedTwoDimensionResponseDTO.setOrderNumber(orderid);
		mroducedTwoDimensionResponseDTO.setoAgentNo(oAgentNo);
		mroducedTwoDimensionResponseDTO.setPayChannel("3");
		mroducedTwoDimensionResponseDTO.setArray(array);
		mroducedTwoDimensionResponseDTO.setArray1(array1);
		
		logger.info("调用用户的百度钱包付款接口调用第一步返回"+createJsonString(mroducedTwoDimensionResponseDTO)+"时间："+ UtilDate.getDateFormatter());

		return mroducedTwoDimensionResponseDTO;


	}
	/**
	 * 扫描用户的百度钱包付款   第二步
	 * wumeng  20150507
	 * @param prama
	 * @param request
	 * @param session
	 * @param premiumRate
	 */
	public String baiDuPay(MroducedTwoDimensionResponseDTO  mroducedTwoDimensionResponseDTO) throws Exception {
		logger.info("调用用户的百度钱包付款接口调用第二步开始，时间："+UtilDate.getDateFormatter());
		
		String [] array = mroducedTwoDimensionResponseDTO.getArray();
		String [] array1 =mroducedTwoDimensionResponseDTO.getArray1(); 
		String orderid = mroducedTwoDimensionResponseDTO.getOrderNumber();
		String oAgentNo = mroducedTwoDimensionResponseDTO.getoAgentNo();
		
		ViewKyChannelInfo channelInfo = AppPospContext.context.get(BAIDU+BAIDUSCANCODE);
		
		String result  =new  BDHttpClient().execute(array,array1, channelInfo.getUrl(),oAgentNo);
		
		BaiDuDTO baiDuDTO = (BaiDuDTO)parseJsonString(result,BaiDuDTO.class);
		
		MroducedTwoDimensionResponseDTO mroducedTwoDimensionResultDTO = new MroducedTwoDimensionResponseDTO();
		
		String retMessage="";
		String status = "";

		/**
		 * 69515	余额不足，
		 * 69441	支付时请不要更换设备，
		 * 69552	用户不一致，
		 * 69556	请用户输入密码确认支付，
		 * 65236	用户端二次支付失败，
		 * 认为订单生成成功
		 */

		if("0".equals(baiDuDTO.getRet())){
			retMessage = "生成订单成功 ";
			status = "0";
			//线程查询处理百度订单处理结果并对本地订单进行处理
			ThreadPool.executor(new BaiDuTaskThread(orderid, pmsAppTransInfoDao,accountDao,pmsAppTransInfoService,pospTransInfoDAO,pmsAgentInfoDao));

		}else if ("65215".equals(baiDuDTO.getRet())){
			retMessage = "交易已付款 ";
			status = "1";
		}else if("65235".equals(baiDuDTO.getRet())){
			retMessage = "付款码失效，请刷新重试 ";
			status = "1";
		}else if("69506".equals(baiDuDTO.getRet())){
			retMessage = "该商户暂不支持该服务 ";
			status = "1";
		}else if("69510".equals(baiDuDTO.getRet())){
			retMessage = "订单已过期 ";
			status = "1";
		}else if("69511".equals(baiDuDTO.getRet())){
			retMessage = "付款码已过期 ";
			status = "1";
		}else if("69441".equals(baiDuDTO.getRet())){
			retMessage = "支付时请不要更换设备 ";
			status = "0";
		}else if("69515".equals(baiDuDTO.getRet())){
			retMessage = "余额不足 ";
			status = "0";
		}else if("69552".equals(baiDuDTO.getRet())){
			retMessage = "用户不一致 ";
			status = "0";
		}else if("69556".equals(baiDuDTO.getRet())){
			retMessage = "请用户输入密码确认支付 ";
			status = "0";
		}else if("65236".equals(baiDuDTO.getRet())){
			retMessage = "用户端二次支付失败 ";
			status = "0";
		}else if("69557".equals(baiDuDTO.getRet())){
			retMessage = "处理失败，交易可能存在风险，若是本人操作请联系客服400-8988-855 ";
			status = "1";
		}else {
			baiDuDTO.setRet("100");
			retMessage = "系统异常 ";

		}
		updateOrder(baiDuDTO.getRet(),orderid);

		mroducedTwoDimensionResultDTO.setRetCode(status);
		mroducedTwoDimensionResultDTO.setRetMessage(retMessage);
		mroducedTwoDimensionResultDTO.setOrderNumber(orderid);

		result = createJsonString(mroducedTwoDimensionResultDTO);

		

		logger.info("调用用户的百度钱包付款接口调用结束，订单号："+orderid+" 反回前台参数："+result+"时间："+ UtilDate.getDateFormatter());

		return result;


	}

	/**
	 * 扫描用户     微信付款 第一步
	 * wumeng  20150512
	 * @param prama
	 * @param request
	 * @param session
	 * @param premiumRate
	 */
	public MroducedTwoDimensionResponseDTO wechatPay(ProducedTwoDimensionDTO prama,SessionInfo  sessionInfo,String rateStr,String oAgentNo) throws Exception {
		logger.info("调用用户的 微信付款接口调用开始，时间："+UtilDate.getDateFormatter());

		String  orderid = "";//订单号

		//组装报文
		 orderid = UtilMethod.getOrderid("103");  //   10业务号3业务细;	订单号      现根据规则生成订单号
		 String  totalAmount = prama.getPayAmt();         //交易金额
		 String  paymenttype = "021";                     //支付类型    支付宝 :012二维码(c2b)、011 付码 (b2c)    微信:  022二维码(c2b)  021付码（b2c）
		 String  transType = "31";                        //交易类型   31：付款码支付（终端主拍）32：用户扫二维码支付（终端被拍）

		WechatAndAlipayRequestDTO wechatAndAlipayRequestDTO = new WechatAndAlipayRequestDTO();
		wechatAndAlipayRequestDTO.setOrderNo(orderid);
		wechatAndAlipayRequestDTO.setTotalAmount(totalAmount);
		wechatAndAlipayRequestDTO.setPaymenttype(paymenttype);
		wechatAndAlipayRequestDTO.setTransType(transType);
		wechatAndAlipayRequestDTO.setMerInfo(sessionInfo.getMercId());
		wechatAndAlipayRequestDTO.setPayCode(prama.getPayCode());
		wechatAndAlipayRequestDTO.setoAgentNo(oAgentNo);
		

		//订单入库    调讯联接口后在修改处理状态
		String paymenttypeFlag = "02" ;//支付类型标记位  01支付宝   02微信

		boolean flag = insertOrderForXL(orderid, prama.getPayAmt(), sessionInfo.getMercId(),paymenttypeFlag,rateStr,XUNLIANWECHATSCANCODE,oAgentNo);

		MroducedTwoDimensionResponseDTO mroducedTwoDimensionResponseDTO = new MroducedTwoDimensionResponseDTO();
		String retMessage="";
		String retCode="";

		if(flag){
			retCode ="0";
			retMessage = "生成订单和流水成功 ";

		}else{
			retCode ="1";
			retMessage = "生成订单和流水失败 ";
		}

		mroducedTwoDimensionResponseDTO.setRetCode(retCode);
		mroducedTwoDimensionResponseDTO.setRetMessage(retMessage);
		mroducedTwoDimensionResponseDTO.setPayChannel("1");
		mroducedTwoDimensionResponseDTO.setWechatAndAlipayRequestDTO(wechatAndAlipayRequestDTO);

		logger.info("调用用户的微信付款接口调用第一步返回："+createJsonString(mroducedTwoDimensionResponseDTO)+"，结束时间："+ UtilDate.getDateFormatter());

		return mroducedTwoDimensionResponseDTO;

	}
	
	/**
	 * 扫描用户     微信付款 第二步
	 * wumeng  20150512
	 * @param prama
	 * @param request
	 * @param session
	 * @param premiumRate
	 */
	public String wechatPay(WechatAndAlipayRequestDTO wechatAndAlipayRequestDTO) throws Exception {
		logger.info("调用用户的 微信付款接口调用第二步开始，时间："+UtilDate.getDateFormatter());

		
		String result = "";
		String orderid = wechatAndAlipayRequestDTO.getOrderNo();
		
		String sendString = "param="+ createJsonString(wechatAndAlipayRequestDTO);//向讯联发送的json串
		
		MroducedTwoDimensionResponseDTO mroducedTwoDimensionResponseDTO = new MroducedTwoDimensionResponseDTO();

       
        ViewKyChannelInfo channelInfo = AppPospContext.context.get(XUNLIAN+XUNLIANWECHATSCANCODE);

        logger.info("调用三方前置讯联接口请求参数：" + sendString + "，结束时间：" + UtilDate.getDateFormatter());
        
        //向pre发送报文 //调用pre接口（8583）
        String successFlag = HttpURLConection.httpURLConnectionPOST(channelInfo.getUrl(), sendString);

        logger.info("调用三方前置讯联接口返回参数：" + successFlag + "，结束时间：" + UtilDate.getDateFormatter());

        XunlianGeneral2DecimalResponseDTO  response = (XunlianGeneral2DecimalResponseDTO)parseJsonString(successFlag,XunlianGeneral2DecimalResponseDTO.class);

        String retMessage="";
		String status="";
        

        if(response != null && StringUtils.isNotBlank(response.getRetCode()) 
        		&& StringUtils.isNotBlank(response.getTwoDecimal())
        		&&StringUtils.isNotBlank(response.getSerialNo())){
        	
    		//讯联返回00：交易成功 09：交易处理中（请重试）
    		//后台返回给前台APP  0生成二维码成功 1 生成二维码失败 100 系统异常

    		if("0000".equals(response.getRetCode())){
    			retMessage = "生成订单成功 ";
    			status="0";
                //修改流水表通道相关
                updatePosBusAndPosNumTransInfoByOrderId(orderid,response.getBusInfo(),response.getBusPos(),response.getPayAmount());
    			//线程查询处讯联订单处理结果并对本地订单进行处理
    			ThreadPool.executor(new XLTaskThread(orderid,response.getSerialNo(),wechatAndAlipayRequestDTO.getMerInfo(),"025",response.getTradeTime(),response.getSearchNum(), pmsAppTransInfoDao,accountDao,pmsAppTransInfoService,pospTransInfoDAO,pmsAgentInfoDao));

    		}else {
    			status = response.getRetCode();
    			retMessage = response.getRetMessage();
    		}
            
        }else{
        	status="1";
            retMessage = "生成订单失败 ";
        }

		updateOrder(response.getRetCode(),orderid,response.getSerialNo(),response.getTradeTime(),response.getSearchNum());
		mroducedTwoDimensionResponseDTO.setRetCode(status);
		mroducedTwoDimensionResponseDTO.setRetMessage(retMessage);
		mroducedTwoDimensionResponseDTO.setOrderNumber(orderid);

		

		result = createJsonString(mroducedTwoDimensionResponseDTO);

		logger.info("调用用户的微信付款接口调用结束， 订单号："+orderid+" 反回前台参数："+result+"，结束时间："+ UtilDate.getDateFormatter());

		return result;

	}


	/**
	 * 扫描用户     支付宝付款  第一步
	 * wumeng  20150512
	 * @param prama
	 * @param request
	 * @param session
	 * @param premiumRate
	 */
	public MroducedTwoDimensionResponseDTO alipayPay(ProducedTwoDimensionDTO prama, SessionInfo  sessionInfo,String rateStr,String oAgentNo) throws Exception {
		logger.info("调用用户的支付宝付款接口调用第一步开始，时间："+UtilDate.getDateFormatter());
		
		String  orderid = "";//订单号
		//组装报文
		orderid = UtilMethod.getOrderid("105");  //   10业务号5业务细;	订单号      现根据规则生成订单号

		String  totalAmount = prama.getPayAmt();         //交易金额


		String  paymenttype = "011";                     //支付类型    支付宝 :012二维码(c2b)、011 付码 (b2c)    微信:  022二维码(c2b)  021付码（b2c）
		String  transType = "31";                        //交易类型   31：付款码支付（终端主拍）32：用户扫二维码支付（终端被拍）

		WechatAndAlipayRequestDTO wechatAndAlipayRequestDTO = new WechatAndAlipayRequestDTO();
		wechatAndAlipayRequestDTO.setOrderNo(orderid);
		wechatAndAlipayRequestDTO.setTotalAmount(totalAmount);
		wechatAndAlipayRequestDTO.setPaymenttype(paymenttype);
		wechatAndAlipayRequestDTO.setTransType(transType);
		wechatAndAlipayRequestDTO.setMerInfo(sessionInfo.getMercId());
		wechatAndAlipayRequestDTO.setPayCode(prama.getPayCode());
		wechatAndAlipayRequestDTO.setoAgentNo(oAgentNo);

		//订单入库    调讯联接口后在修改处理状态
		String paymenttypeFlag = "01" ;//支付类型标记位  01支付宝   02微信
		boolean flag = insertOrderForXL(orderid, prama.getPayAmt(), sessionInfo.getMercId(),paymenttypeFlag,rateStr,XUNLIANALIPAYSCANCODE,oAgentNo);

		MroducedTwoDimensionResponseDTO mroducedTwoDimensionResponseDTO = new MroducedTwoDimensionResponseDTO();
		String retMessage="";
		String retCode="";

		if(flag){
			retCode ="0";
			retMessage = "生成订单和流水成功 ";

		}else{
			retCode ="1";
			retMessage = "生成订单和流水失败 ";
		}

		mroducedTwoDimensionResponseDTO.setRetCode(retCode);
		mroducedTwoDimensionResponseDTO.setRetMessage(retMessage);
		mroducedTwoDimensionResponseDTO.setWechatAndAlipayRequestDTO(wechatAndAlipayRequestDTO);
		mroducedTwoDimensionResponseDTO.setPayChannel("2");

		logger.info("调用用户的支付宝付款接口调用第一步返回："+createJsonString(mroducedTwoDimensionResponseDTO)+"，结束时间："+ UtilDate.getDateFormatter());

		return mroducedTwoDimensionResponseDTO;


	}

	/**
	 * 扫描用户     支付宝付款  第二步
	 * wumeng  20150512
	 * @param prama
	 * @param request
	 * @param session
	 * @param premiumRate
	 */
	public String alipayPay(WechatAndAlipayRequestDTO wechatAndAlipayRequestDTO) throws Exception {
		logger.info("调用用户的支付宝付款接口调用第二步开始，时间："+UtilDate.getDateFormatter());

		String result = "";
		String orderid = wechatAndAlipayRequestDTO.getOrderNo();
		
		String sendString = "param="+ createJsonString(wechatAndAlipayRequestDTO);//向讯联发送的json串
		
		MroducedTwoDimensionResponseDTO mroducedTwoDimensionResponseDTO = new MroducedTwoDimensionResponseDTO();

        //调用pre接口（8583）
        ViewKyChannelInfo channelInfo = AppPospContext.context.get(XUNLIAN+XUNLIANALIPAYSCANCODE);

        logger.info("调用三方前置讯联接口请求参数：" + sendString + "，结束时间：" + UtilDate.getDateFormatter());
        
        //向pre发送报文
        String successFlag = HttpURLConection.httpURLConnectionPOST(channelInfo.getUrl(), sendString);

        logger.info("调用三方前置讯联接口返回参数：" + successFlag + "，结束时间：" + UtilDate.getDateFormatter());

        XunlianGeneral2DecimalResponseDTO  response = (XunlianGeneral2DecimalResponseDTO)parseJsonString(successFlag,XunlianGeneral2DecimalResponseDTO.class);

        String retMessage="";
		String status="";
        

        if(response != null && StringUtils.isNotBlank(response.getRetCode()) 
        		&& StringUtils.isNotBlank(response.getTwoDecimal())
        		&&StringUtils.isNotBlank(response.getSerialNo())){
        	
    		//讯联返回00：交易成功 09：交易处理中（请重试）
    		//后台返回给前台APP  0生成二维码成功 1 生成二维码失败 100 系统异常

    		if("0000".equals(response.getRetCode())){
    			retMessage = "生成订单成功 ";
    			status="0";
                //修改流水表通道相关
                updatePosBusAndPosNumTransInfoByOrderId(orderid,response.getBusInfo(),response.getBusPos(),response.getPayAmount());
    			//线程查询处讯联订单处理结果并对本地订单进行处理
    			ThreadPool.executor(new XLTaskThread(orderid,response.getSerialNo(),wechatAndAlipayRequestDTO.getMerInfo(),"015",response.getTradeTime(),response.getSearchNum(), pmsAppTransInfoDao,accountDao,pmsAppTransInfoService,pospTransInfoDAO,pmsAgentInfoDao));

    		}else {
    			status = response.getRetCode();
    			retMessage = response.getRetMessage();
    		}
            
        }else{
        	status="1";
            retMessage = "生成订单失败 ";
        }

		updateOrder(response.getRetCode(),orderid,response.getSerialNo(),response.getTradeTime(),response.getSearchNum());
		mroducedTwoDimensionResponseDTO.setRetCode(status);
		mroducedTwoDimensionResponseDTO.setRetMessage(retMessage);
		mroducedTwoDimensionResponseDTO.setOrderNumber(orderid);

		

		result = createJsonString(mroducedTwoDimensionResponseDTO);

		logger.info("调用用户的支付宝付款接口调用结束， 订单号："+orderid+" 反回前台参数："+result+"，结束时间："+ UtilDate.getDateFormatter());

		return result;


	}
	
	
	/**
	 * 刷卡收款    第一步  生成订单
	 * wumeng  20150515
	 * @param param
	 * @param sessionInfo
	 */
	@Override
	public PayCardResponseDTO insertOrderPay(String param, SessionInfo sessionInfo)throws Exception{
		logger.info("刷卡收款    第一步  生成订单 接收app参数: "+param+"时间："+UtilDate.getDateFormatter());
		PayCardRequestDTO payCardRequestDTO = (PayCardRequestDTO)parseJsonString(param,PayCardRequestDTO.class);
		PayCardResponseDTO payCardResponseDTO =  new PayCardResponseDTO();
		if(!payCardRequestDTO.equals(DATAPARSINGMESSAGE)){//判断解析数据是否成功
			
			String mercid  = sessionInfo.getMercId();//商户编号
			String oAgentNo = sessionInfo.getoAgentNo();//O单编号
			
			BrushCalorieOfConsumptionRequestDTO dto = payCardRequestDTO.getDto();
			String pealName= pmsMerchantInfoDao.queryMercuryStatus(mercid);//查询商户是否实名认证
			
		
			//获取通道的费率
            Map<String,String> paramMap = new HashMap<String, String>();
            paramMap.put("mercid", mercid);
            paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
            paramMap.put("oAgentNo", oAgentNo);
            
            //查询商户刷卡费率 和  最低收款金额    费率    是否是封顶费率标记  封顶金额   
            AppRateTypeAndAmount appRateTypeAndAmount = pmsAppAmountAndRateConfigDao.queryAmountAndRateInfoForShuaka(paramMap);
			
			String status = appRateTypeAndAmount.getStatus();//此业务是否开通
			String statusMessage = appRateTypeAndAmount.getMessage();//此业务是否开通的描述
			
			 if(AUTHENTICATIONFLAG.equals(pealName)){//正式商户
					//MIN_AMOUNT,MAX_AMOUNT ,RATE ,STATUS
				 
				 
				//判断O单业务时候开启（总）
					
					//44444444
					

		            ResultInfo resultInfoForOAgentNo =  publicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.merchantCollect,oAgentNo);
	                //返回不为0，一律按照交易失败处理
	                if(!resultInfoForOAgentNo.getErrCode().equals("0")){
	                	payCardResponseDTO.setRetCode("22");//返回码
	                	if("".equals(resultInfoForOAgentNo.getMsg())||resultInfoForOAgentNo.getMsg()==null){
	                		payCardResponseDTO.setRetMessage("此功能暂时关闭");//返回信息	
						 }else{
							 payCardResponseDTO.setRetMessage(resultInfoForOAgentNo.getMsg());
						 }
	                	return payCardResponseDTO;
	                }
				 
				 
				 if("1".equals(status)){//1表示业务开通
		              	//开通	
						
                         //判断支付方式时候开通总开关
					 	 //33333333
                 		 ResultInfo payCheckResult = payTypeControlDao.checkLimit(oAgentNo,PaymentCodeEnum.shuakaPay.getTypeCode());
                         if(!payCheckResult.getErrCode().equals("0")){
                         	//支付方式时候开通总开关 禁用
                        	 payCardResponseDTO.setRetCode("22");//
                        	 
         					if("".equals(payCheckResult.getMsg())||payCheckResult.getMsg()==null){
                        		payCardResponseDTO.setRetMessage("此支付方式暂时关闭");//返回信息	
        					 }else{
        						 payCardResponseDTO.setRetMessage(payCheckResult.getMsg());
        					 }
       						return payCardResponseDTO; 
         					
                         }
						//判断商户的支付方式时候开通
                         		
                         String payStatus = "";//此支付方式是否开通
                		
                         ResultInfo resultInfoForpay=  publicTradeVerifyService.payTypeVerifyMer(PaymentCodeEnum.shuakaPay,mercid);
     	                //返回不为0，一律按照交易失败处理
     	                if(resultInfoForpay.getErrCode().equals("0")){
     	                	payStatus="0";
     	                
     	                }
                         
                         
                         if("0".equals(payStatus)){//0表示支付方式开通
                        	 
                        	String payAmtStr = dto.getPayAmount();//订单金额
     						BigDecimal payAmt = new BigDecimal(payAmtStr);//订单金额
     						
                        	 
                			//判读  交易金额是不是在欧单区间控制之内
    						 ResultInfo resultInfo =  amountLimitControlDao.checkLimit(oAgentNo,payAmt,TradeTypeEnum.merchantCollect.getTypeCode());
                             //返回不为0，一律按照交易失败处理
                             if(!resultInfo.getErrCode().equals("0")){
                            	payCardResponseDTO.setRetCode("21");//
                            	
                            	
                            	if("".equals(resultInfo.getMsg())||resultInfo.getMsg()==null){
                            		payCardResponseDTO.setRetMessage("交易金额不在申请的范围之内");//返回信息	
            					 }else{
            						 payCardResponseDTO.setRetMessage(resultInfo.getMsg());
            					 }
    						    
           						return payCardResponseDTO; 
                             }
                             
                             
                             
    						BigDecimal min_amount = new  BigDecimal(appRateTypeAndAmount.getMinAmount());//最低收款金额   MIN_AMOUNT
    						BigDecimal max_amount = new  BigDecimal(appRateTypeAndAmount.getMaxAmount());//最高收款金额   MAX_AMOUNT

    						if(min_amount.compareTo(payAmt)!=1){//判断收款金额是否大于最低收款金额   大于等于执行   小于不执行

    							if(payAmt.compareTo(max_amount)!=1){
    								
    								List<PayCmmtufit> cardList = payCmmtufitDao.searchCardInfoByBeforeSix(dto.getCardNo().substring(0, 6)+ "%");
    								if(cardList.size()!=0){
    									
    							
    									//生成订单
    									PmsAppTransInfo  pmsAppTransInfo= new PmsAppTransInfo();
    									pmsAppTransInfo.setPaymenttype("刷卡支付");
    									pmsAppTransInfo.setTradetype("商户收款");
    									pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter());

    									String orderid="";
    									if("1".equals(payCardRequestDTO.getBrushType())){ //刷卡类型：1音频刷卡，2蓝牙刷卡
    										orderid = UtilMethod.getOrderid("106");  //   10业务号    (6 音频7蓝牙) 业务细	      订单号      现根据规则生成订单号
    									}else{
    										orderid = UtilMethod.getOrderid("107");  //   10业务号    (6 音频7蓝牙) 业务细	      订单号      现根据规则生成订单号
    									}
    									pmsAppTransInfo.setoAgentNo(oAgentNo);//O单编号
    									pmsAppTransInfo.setOrderid(orderid);

    									pmsAppTransInfo.setReasonofpayment("商户收款");

    									pmsAppTransInfo.setMercid(mercid); //商户id
    									
    									
    									pmsAppTransInfo.setFactamount(payAmtStr);//实际金额
    									pmsAppTransInfo.setPaymentcode("5");//支付方式 例如： 1 账号（余额）支付、2 百度支付、3 微信支付、4 支付宝支付、5刷卡支付
    									pmsAppTransInfo.setTradetypecode("1");//交易类型 例如： 1 商户收款、2 转账汇款、3 信用卡还款、4手机充值、5 水煤电、 6 加油卡充值、
    									pmsAppTransInfo.setOrderamount(payAmtStr);//订单金额
    									
    								
    									
    									pmsAppTransInfo.setChannelNum(SHUAKA);//通道
    									pmsAppTransInfo.setBusinessNum(SHUAKACOLLECTMONEY);//业务号
    									pmsAppTransInfo.setStatus(Constants.ORDERINITSTATUS);//订单初始化状态
    									
    									//刷卡费率可以选择  start
    									/*List<AppRateConfig> list = new ArrayList<AppRateConfig>();

    									AppRateConfig appRateConfig = new AppRateConfig();
    									appRateConfig.setIsThirdpart("0");
    									appRateConfig.setoAgentNo(sessionInfo.getoAgentNo());
    									list = appRateConfigDao.searchList(appRateConfig);
    									String isTop ="";
    									String topPoundage="";
    									BigDecimal rate = new BigDecimal(0);//查询商户费率
    									String rateStr = "";//商户费率    RATE
    									if (list != null && list.size() > 0) {
    										for(AppRateConfig temp : list){
    											
    											if(temp.getRateType().equals(payCardRequestDTO.getRateType())){
    												 rate =	new BigDecimal(temp.getRate());//查询商户费率
    												 rateStr = temp.getRate();//商户收款费率
    												 isTop = temp.getIsTop();//商户费率是不是封顶费率
    												 topPoundage  = temp.getTopPoundage();//商户费率是封顶费率时的最大手续费
    												 break;
    											}
    										}
    									}*/
    									
    									//刷卡费率可以选择  end  
    									
    									//刷卡费率不选择 strat
    									
    									String rateStr = appRateTypeAndAmount.getRate();//商户费率    RATE
    									BigDecimal rate = new BigDecimal(rateStr);//查询商户费率
    									String isTop = appRateTypeAndAmount.getIsTop();//商户费率是不是封顶费率
    				 					String topPoundage = appRateTypeAndAmount.getTopPoundage();//商户费率是封顶费率时的最大手续费
    									 	
    									//刷卡费率不选择 end
    									
    				 					BigDecimal poundage = new BigDecimal(0);
    				 					
    				 					if("1".equals(isTop)){//判断费率是不是封顶费率      封顶费率有最大的手续费值
    				 						//封顶费率
    				 						poundage = payAmt.multiply(rate);
    				 						BigDecimal maxPoundage = new BigDecimal(topPoundage);//最大手续费
    				 						if(poundage.compareTo(maxPoundage)==1){
    				 							poundage = maxPoundage;
    				 						}
    				 						pmsAppTransInfo.setRate(rateStr+"-"+topPoundage);//费率    
    				 					}else{
    				 						poundage = payAmt.multiply(rate);//不是封顶费率
    				 						pmsAppTransInfo.setRate(rateStr);//费率    
    				 					}
    				 					
    				 				
    				 					
    				 					
    				 					
    				 					//交易金额   按分为最小单位  例如：1元=100分   采用100   商户收款时给商户记账时减去费率(实际金额-手续费)
    									pmsAppTransInfo.setPayamount(payAmt.subtract(poundage).toString());
    									pmsAppTransInfo.setPoundage(poundage.toString());//手续费  按分为最小单位  例如：1元=100分   采用100
    									pmsAppTransInfo.setBankno(dto.getCardNo());//刷卡银行卡号
    									//根据银行卡号查询银行名称信息等
    									
    									pmsAppTransInfo.setBankname(cardList.get(0).getBnkName());
    									
    									pmsAppTransInfo.setBrushType(payCardRequestDTO.getBrushType());         // //刷卡类型：1音频刷卡，2蓝牙刷卡

    									pmsAppTransInfo.setSnNO(dto.getSn());   //刷卡器设备号
    									
    									pmsAppTransInfo.setChannelNum(SHUAKA);
    									pmsAppTransInfo.setBusinessNum(SHUAKACOLLECTMONEY);
                                        pmsAppTransInfo.setAuthPath(PIRPREURL+dto.getAuthPath());

                                        
                                        pmsAppTransInfo.setAltLat(payCardRequestDTO.getAltLat());//经纬度（逗号隔开）
                                        pmsAppTransInfo.setGpsAddress(payCardRequestDTO.getGpsAddress());//gps获取的地址信息(中文)
                                        
    									String orderInfo = createJsonString(pmsAppTransInfo);//订单详细信息

    									try {
    										if(pmsAppTransInfoDao.insert(pmsAppTransInfo)!=1){
    											logger.info("订单入库失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。详细信息："+orderInfo);
    											payCardResponseDTO.setRetCode("1");
    											payCardResponseDTO.setOrderNumber(orderid);
    											payCardResponseDTO.setRetMessage("生成订单失败");
    											throw  new Exception();//失败抛出异常回退操作
    										}else{
    											
    											payCardResponseDTO.setRetCode("0");
    											payCardResponseDTO.setOrderNumber(orderid);
    											payCardResponseDTO.setRetMessage("生成订单成功");
    											payCardResponseDTO.setPmsAppTransInfo(pmsAppTransInfo);
    											
    											//第二步操作调用submitOrderPay方法
    											
    											//流水记录由pre系统处理
    											
    											}
    									} catch (Exception e) {
    										logger.info("订单入库失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。详细信息："+orderInfo,e);
    										payCardResponseDTO.setRetCode("100");
    										payCardResponseDTO.setOrderNumber(orderid);
    										payCardResponseDTO.setRetMessage("系统异常");
    									}
    								}else{
    									//没有此卡card bin 
    									payCardResponseDTO.setRetCode("16");//
    									payCardResponseDTO.setRetMessage("暂不支持此卡");
    								}
    						
    							}else{

    								//交易金额大于收款最高金额
    								payCardResponseDTO.setRetCode("3");//
    								payCardResponseDTO.setRetMessage("交易金额大于收款最高金额:"+max_amount.divide(new BigDecimal(100)));
    							}


    						}else{
    							//交易金额小于收款最低金额
    							payCardResponseDTO.setRetCode("4");
    							payCardResponseDTO.setRetMessage("交易金额小于收款最低金额:"+min_amount.divide(new BigDecimal(100)));
    						}
    						
                		 }else{
                			 //支付方式未开通
     	               		payCardResponseDTO.setRetCode("15");//
    	               		if("".equals(resultInfoForpay.getMsg())||resultInfoForpay.getMsg()==null){
    	               			payCardResponseDTO.setRetMessage("请提交相关资料,开通此支付方式");
    							}else{
    								payCardResponseDTO.setRetMessage(resultInfoForpay.getMsg());
    							}
                		 }
                       
					 }else{
						//此功能暂未开通或被禁用
			        	 payCardResponseDTO.setRetCode("14");//
			        	
			        	 if("".equals(statusMessage)||statusMessage==null){
			        		 payCardResponseDTO.setRetMessage("此功能暂未开通");
							}else{
								payCardResponseDTO.setRetMessage(statusMessage);
							}
			        	 
					 }
				 
	         }else{
	        	 
	        	 //交易金额小于收款最低金额
					payCardResponseDTO.setRetCode("7");
					payCardResponseDTO.setRetMessage("不是正式商户");
	         	
	         }
		 }
		
		logger.info("刷卡收款    第一步  生成订单 返回参数: "+createJsonString(payCardResponseDTO)+"时间："+UtilDate.getDateFormatter());

		return payCardResponseDTO;

	}
	/**
	 * 刷卡收款    第二步  确认订单并支付
	 * wumeng  20150515
	 * @param param
	 * @param sessionInfo
	 * @param pmsAppTransInfo
	 */
	@Override
	public String submitOrderPay(String param, SessionInfo sessionInfo,PmsAppTransInfo  pmsAppTransInfo)throws Exception{
		logger.info("刷卡收款 第二步  确认订单并支付, 时间："+UtilDate.getDateFormatter());
		String  result = "";

		PayCardRequestDTO payCardRequestDTO = (PayCardRequestDTO)parseJsonString(param,PayCardRequestDTO.class);
		PayCardResponseDTO payCardResponseDTO =  new PayCardResponseDTO();
		if(!payCardRequestDTO.equals(DATAPARSINGMESSAGE)){//判断解析数据是否成功
			BrushCalorieOfConsumptionRequestDTO dto = payCardRequestDTO.getDto();
			String  orderid = pmsAppTransInfo.getOrderid();//订单号
			String  rateStr = pmsAppTransInfo.getRate();//费率

			String  sendStr8583 =	"param="+this.createBrushCalorieOfConsumptionDTORequest(sessionInfo, dto, orderid, SHUAKACOLLECTMONEY, rateStr,dto.getSn());
			
			if("param=fail".equals(sendStr8583)){
				//上送参数错误
				payCardResponseDTO.setRetCode("14");
				payCardResponseDTO.setRetMessage("上送参数错误");
				logger.info("上送参数错误， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter());
			}else{
				
				

				logger.info("调用三方前置刷卡接口请求参数：" + sendStr8583 + "，结束时间：" + UtilDate.getDateFormatter());
				//调用三方前置刷卡接口（8583）
				
				ViewKyChannelInfo channelInfo = AppPospContext.context.get(SHUAKA+SHUAKACOLLECTMONEY);
				
				String successFlag = HttpURLConection.httpURLConnectionPOST(channelInfo.getUrl(), sendStr8583);	
				
				logger.info("调用三方前置刷卡接口返回参数：" + successFlag + "，结束时间：" + UtilDate.getDateFormatter());
				
				BrushCalorieOfConsumptionResponseDTO  response = (BrushCalorieOfConsumptionResponseDTO)parseJsonString(successFlag,BrushCalorieOfConsumptionResponseDTO.class);
				
				if("0000".equals(response.getRetCode())){//判断调用接口处理是否成功    0000表示刷卡成功
					
					if(updateMerchantBalance(pmsAppTransInfo)==1){//余额处理
						
						Map<String, String> paramUpdateOrderStatus = new HashMap<String, String>();
						paramUpdateOrderStatus.put("finishTime", UtilDate.getDateFormatter());
						paramUpdateOrderStatus.put("orderid", orderid);
						//修改订单状态
						if(pmsAppTransInfoDao.updateOrderStatus(paramUpdateOrderStatus)==1){
							
							/*if(pospTransInfoDAO.updetePospTransInfo(orderid)!=1){//刷卡流水pre三方前置修改状态
		                     	 logger.info("流水表订单状态修改失败， 订单号：" + orderid + "，结束时间：" + UtilDate.getDateFormatter());
		                     	payCardResponseDTO.setRetCode("10");
		   						payCardResponseDTO.setRetMessage("流水表订单状态修改失败");
							}else{*/
		                    	 //订单生成成功
		   						payCardResponseDTO.setRetCode("0");
		   						payCardResponseDTO.setRetMessage("支付成功");
		                       //}
						}else{
							//修改订单状态失败
							payCardResponseDTO.setRetCode("12");
							payCardResponseDTO.setRetMessage("修改订单状态失败");
							logger.info("修改订单状态失败: 订单号"+orderid+"时间："+UtilDate.getDateFormatter());
						}
						
					}else{
						//修改余额失败
						payCardResponseDTO.setRetCode("13");
						payCardResponseDTO.setRetMessage("修改余额失败");
						logger.info("修改余额失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter());
					}
				
				}else{
					
					payCardResponseDTO.setRetCode("1");
					payCardResponseDTO.setRetMessage("错误码："+response.getRetCode()+"\n错误信息："+response.getRetMessage());
					logger.info("订单生成失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter());
				}
			
			}
			
		}
		result = createJsonString(payCardResponseDTO);

		logger.info("刷卡收款 第二步 确认订单并支付 返回app参数: "+result+"时间："+UtilDate.getDateFormatter());


		return result;

	}



	/**
	 * 订单入库  百度
	 * wumeng  20150511
	 * @param orderid 订单号
	 * @param payamount 交易金额
	 * @param mercId 用户ID
	 * @param rateStr 费率
	 * @param businessnum  通道业务编码
	 * @param oAgentNo o单编号
	 * @throws Exception
	 */
	public boolean insertOrder(String orderid,String payamount,String mercId, String rateStr ,String businessnum,String oAgentNo) throws Exception {

		boolean result =false;
		//查询商户费率 
		BigDecimal rate = new BigDecimal(rateStr);
		BigDecimal amount = new BigDecimal(payamount);

		//成功后订到入库app后台
		PmsAppTransInfo  pmsAppTransInfo= new PmsAppTransInfo();
		pmsAppTransInfo.setPaymenttype("百度支付");
		pmsAppTransInfo.setTradetype("商户收款");
		pmsAppTransInfo.setBusinessNum(businessnum);//通道业务编码
		pmsAppTransInfo.setChannelNum(BAIDU);//通道号
		pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter());
		pmsAppTransInfo.setOrderid(orderid);//上送的订单号
		
		pmsAppTransInfo.setReasonofpayment("商户收款");
		pmsAppTransInfo.setMercid(mercId);
		pmsAppTransInfo.setFactamount(payamount);//实际金额    按分为最小单位  例如：1元=100分   采用100
		pmsAppTransInfo.setPaymentcode("2");
		pmsAppTransInfo.setTradetypecode("1");
		pmsAppTransInfo.setOrderamount(payamount);//订单金额  按分为最小单位  例如：1元=100分   采用100
		pmsAppTransInfo.setStatus(Constants.ORDERINITSTATUS);//订单初始化状态
		pmsAppTransInfo.setoAgentNo(oAgentNo);//o单编号
		BigDecimal poundage =  amount.multiply(rate);//手续费
		pmsAppTransInfo.setRate(rateStr);//费率
		
		//结算金额   按分为最小单位  例如：1元=100分   采用100   商户收款时给商户记账时减去费率(实际金额-手续费)
		pmsAppTransInfo.setPayamount(amount.subtract(poundage).toString());
		pmsAppTransInfo.setPoundage(poundage.toString());//手续费  按分为最小单位  例如：1元=100分   采用100

		String sendString = createJsonString(pmsAppTransInfo);
		try {
			if(pmsAppTransInfoDao.insert(pmsAppTransInfo)!=1){
				logger.info("订单入库失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。订单详细信息："+sendString.toString());
				throw new RuntimeException("手动抛出");
			}else{
				PospTransInfo pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);//拼插入流水表实体类
				String sendpospString = createJsonString(pospTransInfo);
				if(pospTransInfo != null){
                    if(pospTransInfoDAO.insert(pospTransInfo)!=1){
                    	logger.info("订单入流水表失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。流水详细信息："+sendpospString.toString());
                    	throw new RuntimeException("手动抛出");
                    }else{
                    	result=true;//生成订单和流水成功
                    }
                }else{
                		logger.info("订单入流水表失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。流水详细信息："+sendpospString.toString());
                		throw new RuntimeException("手动抛出");
                }
			}
		} catch (Exception e) {
			logger.info("订单入库失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。订单详细信息："+sendString.toString(),e);
			throw new RuntimeException("手动抛出");
		}
		return result;


	}

	/**
	 * 订单入库  讯联
	 * wumeng  20150513
	 * @param orderid 订单号
	 * @param payamount 交易金额
	 * @param mercId 用户ID
	 * @param paymenttypeFlag //支付类型标记位  01支付宝   02微信
	 * @param rateStr 费率
	 * @param businessnum 通道业务编码
	 * @param oAgentNo o单编号
	 * @throws Exception
	 */
	public boolean insertOrderForXL(String orderid,String payamount,String mercId,String paymenttypeFlag, String rateStr,String businessnum,String oAgentNo) throws Exception{
		boolean result =false;
		//查询商户费率 
		BigDecimal rate = new BigDecimal(rateStr);
		BigDecimal amount = new BigDecimal(payamount);
		
		
		//成功后订到入库app后台
		PmsAppTransInfo  pmsAppTransInfo= new PmsAppTransInfo();
		if("02".equals(paymenttypeFlag)){
			pmsAppTransInfo.setPaymenttype("微信支付");
			pmsAppTransInfo.setPaymentcode("3");
		}else{
			pmsAppTransInfo.setPaymenttype("支付宝支付");
			pmsAppTransInfo.setPaymentcode("4");
		}
		pmsAppTransInfo.setChannelNum(XUNLIAN);
		pmsAppTransInfo.setBusinessNum(businessnum);//通道业务编码
		pmsAppTransInfo.setTradetype("商户收款");
		pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter());
		pmsAppTransInfo.setOrderid(orderid);//上送的订单号
		
		pmsAppTransInfo.setReasonofpayment("商户收款");
		pmsAppTransInfo.setMercid(mercId);
		pmsAppTransInfo.setFactamount(payamount);//实际金额    按分为最小单位  例如：1元=100分   采用100
		pmsAppTransInfo.setTradetypecode("1");
		pmsAppTransInfo.setOrderamount(payamount);//订单金额  按分为最小单位  例如：1元=100分   采用100
		pmsAppTransInfo.setStatus(Constants.ORDERINITSTATUS);//订单初始化状态
		pmsAppTransInfo.setoAgentNo(oAgentNo);//o单编号
		
		BigDecimal poundage =  amount.multiply(rate);//手续费
		pmsAppTransInfo.setRate(rateStr);//费率
		
		//结算金额   按分为最小单位  例如：1元=100分   采用100   商户收款时给商户记账时减去费率(实际金额-手续费)
		pmsAppTransInfo.setPayamount(amount.subtract(poundage).toString());
		
		pmsAppTransInfo.setPoundage(poundage.toString());//手续费  按分为最小单位  例如：1元=100分   采用100
		String sendString = createJsonString(pmsAppTransInfo);
		
		
		try {
			if(pmsAppTransInfoDao.insert(pmsAppTransInfo)!=1){
				logger.info("订单入库失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。订单详细信息："+sendString.toString());
				throw new RuntimeException("手动抛出");
			}else{
				PospTransInfo pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);//拼插入流水表实体类
				String sendpospString = createJsonString(pospTransInfo);
				if(pospTransInfo != null){
                    if(pospTransInfoDAO.insert(pospTransInfo)!=1){
                    	logger.info("订单入流水表失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。流水详细信息："+sendpospString.toString());
                    	throw new RuntimeException("手动抛出");
                    }else{
                    	result=true;//生成订单和流水成功
                    }
                }else{
            		logger.info("订单入流水表失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。流水详细信息："+sendpospString.toString());
            		throw new RuntimeException("手动抛出");
                }
			}
		} catch (Exception e) {
			logger.info("订单入库失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。订单详细信息："+sendString.toString(),e);
			throw new RuntimeException("手动抛出");
		}
		return result;
		
		
	}
	
	
	
	
	

	
	/**
	 * 订单修改  
	 * wumeng  20150511
	 * @param orderid 订单号
	 * @param ret 调用第三方接口返回的码
	 */
	public void updateOrder(String ret,String orderid){
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("responseStatus", ret);
		map.put("orderid", orderid);
		
		try {
			if(pmsAppTransInfoDao.updateResponseCode(map)!=1){
				logger.info("订单状态修改失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。要修改的状态置："+ret);
			}
		 } catch (Exception e) {
			 logger.info("订单状态修改失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。要修改的状态置："+ret);
		 }	
	}
	
	

	/**
	 * 讯联订单修改  
	 * wumeng  20150511
	 * @param orderid 订单号
	 * @param ret 调用第三方接口返回的码
	 * @param serialNo  讯联批次号
	 * @param  tradeTime 讯联订单交易时间
	 * @param  searchNum 讯联检索参考号
	 */
	public void updateOrder(String ret,String orderid,String serialNo,String tradeTime,String searchNum){
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("responseStatus", ret);
		map.put("orderid", orderid);
		map.put("serialNo", serialNo);
		map.put("tradeTime", tradeTime);
		map.put("searchNum", searchNum);
		
		try {
			if(pmsAppTransInfoDao.updateXLResponseCode(map)!=1){
				logger.info("订单状态修改失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。要修改的状态置："+ret);
			}
		 } catch (Exception e) {
			 logger.info("订单状态修改失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。要修改的状态置："+ret);
		 }	
	}
	
	
	
	
	
	
	/**
	 * 百度订单查询
	 * wumeng  20150507
	 * @param order_no
	 */
	public  Map<String, Object> queryOrderForBD(String order_no,String oAgentNo)throws Exception {
			
			logger.info("调用百度订单查询接口调用开始，时间："+UtilDate.getDateFormatter());
			
			String  result = "";//调用百度返回的字符串XML
			ViewKyChannelInfo channelInfo = AppPospContext.context.get(BAIDU+BAIDUQUERY);
			String service_code = "service_code="+Constants.BD_SERVICE_QUERY_CODE;              //服务编号	整数，目前必须为1
			
			String sp_no = "sp_no="+channelInfo.getChannelNO(); 				 //百度支付SDK商户号
			
			order_no = "order_no="+order_no;                 //订单号，商户须保证订单号在商户系统内部唯一。	不超过20个字符
			
			String output_type = "output_type="+Constants.BD_ORDER_OUTPUT_TYPE;   //响应数据的格式，默认XML 
			
			String output_charset = "output_charset="+Constants.BD_CURRENCY;	//响应数据的字符编码，默认GBK
			
			String version = "version="+Constants.BD_VERSION;	                     //接口的版本号	必须为2
			
			String sign_method	= "sign_method="+Constants.BD_SIGN_METHOD;           //签名方法，默认MD5
			
			//用于签名  用于传参数
			String [] array ={service_code,output_type,output_charset,sp_no,order_no,version,sign_method};
			
			result =new  BDHttpClient().execute(array,array, channelInfo.getUrl(),oAgentNo);
			//解析查询返回的XML字符串
			Document doc = DocumentHelper.parseText(result);
		    Map<String, Object> map = XMLUtil.Dom2Map(doc);
		   
			logger.info("调用百度订单查询接口调用结束， 订单号：" + order_no + "，结束时间：" + UtilDate.getDateFormatter());
			
			return map;
		
	}
	/**
	 * 讯联查询
	 * wumeng  20150512
	 * @param merInfo 商户编号
	 * @param serialNo  批次号  
	 * @param paymenttype 查询区分微信（025）还是支付宝（015）
	 * @param tradeTime 讯联订单交易时间
	 *  private String searchNum;//讯联检索参考号
	 */
	public String queryOrderForXL(String orderid,String serialNo,String merInfo,String paymenttype,String tradeTime,String searchNum,String oAgentNo) throws Exception{
		 
		logger.info("调用讯联查询条码支付接口调用开始，时间："+UtilDate.getDateFormatter());
		 
		String result="";
		
		WechatAndAlipayRequestDTO wechatAndAlipayRequestDTO = new WechatAndAlipayRequestDTO();
		wechatAndAlipayRequestDTO.setSerialNo(serialNo);
		wechatAndAlipayRequestDTO.setMerInfo(merInfo);
		
		wechatAndAlipayRequestDTO.setPaymenttype(paymenttype);
		wechatAndAlipayRequestDTO.setTradeTime(tradeTime);
		wechatAndAlipayRequestDTO.setSearchNum(searchNum);
		
		String sendString = "param="+createJsonString(wechatAndAlipayRequestDTO);//向讯联发送的json串
		
		ViewKyChannelInfo channelInfo = AppPospContext.context.get(XUNLIAN+XUNLIANQUERY);
		
		result = HttpURLConection.httpURLConnectionPOST(channelInfo.getUrl(), sendString);
		
		logger.info("调用讯联查询条码支付接口调用结束， 订单号："+orderid+"返回参数："+result+"，结束时间："+ UtilDate.getDateFormatter());
		
		return result;
	}
	

	
	
	/**
	 * 订单查询   app查询操作  app数据库   app请求  订单查询    确认支付成功
	 * wumeng  20150507
	 * @param param
	 * @param session
	 * @param request
	 */
	@Override
	public String queryOrder(String param, HttpSession session,HttpServletRequest request) throws Exception {
		logger.info("app调用订单查询调用开始，时间："+UtilDate.getDateFormatter());
		logger.info("接收app调用订单查询参数：" + param + "时间：" + UtilDate.getDateFormatter());
		String  result = "";
		String order_no = "";
		ProducedTwoDimensionDTO prama = (ProducedTwoDimensionDTO)parseJsonString(param,ProducedTwoDimensionDTO.class);
		
		if(!prama.equals(DATAPARSINGMESSAGE)){//判断解析数据是否成功
			order_no = prama.getOrderNumber();//订单号
			//String payChanel = prama.getPayChannel();
			//查询数据库 订单状态
			MroducedTwoDimensionResponseDTO returnDTO = new MroducedTwoDimensionResponseDTO();
			//根据订单号获取到本地订单
            PmsAppTransInfo appTransInfo = pmsAppTransInfoDao.searchOrderInfo(order_no);
        	
        	String status = "";
            String message = "";
        	if(appTransInfo==null){
        		status=OrderStatusEnum.systemErro.getStatus();
				message="系统异常";
        	}else{
        		status = appTransInfo.getStatus();
                message = "";
                if(status!=null&&!(OrderStatusEnum.initlize.getStatus().equals(status))){
                	 // 0支付成功 1支付失败2 未支付 100 系统异常
        			if(OrderStatusEnum.paySuccess.getStatus().equals(status)){
        				message=OrderStatusEnum.paySuccess.getDescription();
        			}else if(OrderStatusEnum.payFail.getStatus().equals(status)){
        				message=OrderStatusEnum.payFail.getDescription();
        			}else if (OrderStatusEnum.waitingClientPay.getStatus().equals(status)){
        				message=OrderStatusEnum.waitingClientPay.getDescription();
        			}else if(OrderStatusEnum.returnMoneySuccess.getStatus().equals(status)){
        				message=OrderStatusEnum.returnMoneySuccess.getDescription();
        			}else{
        				status=OrderStatusEnum.systemErro.getStatus();
        				message=OrderStatusEnum.systemErro.getDescription();
        			}
                }else{
                	status=OrderStatusEnum.initlize.getStatus();
    				message=OrderStatusEnum.initlize.getDescription();
                }
        	}
        	
        	returnDTO.setRetCode(status);
			returnDTO.setRetMessage(message);
			result = createJsonString(returnDTO); 
            
           
           /* if("3".equals(payChanel)){
            	//处理百度
        	String status = "";
            String message = "";
        	if(appTransInfo==null){
        		status=OrderStatusEnum.systemErro.getStatus();
				message="系统异常";
        	}else{
        		status = appTransInfo.getStatus();
                message = "";
                if(status!=null&&!(OrderStatusEnum.initlize.getStatus().equals(status))){
                	 // 0支付成功 1支付失败2 未支付 100 系统异常
        			if(OrderStatusEnum.paySuccess.getStatus().equals(status)){
        				message=OrderStatusEnum.paySuccess.getDescription();
        			}else if(OrderStatusEnum.payFail.getStatus().equals(status)){
        				message=OrderStatusEnum.payFail.getDescription();
        			}else if (OrderStatusEnum.waitingClientPay.getStatus().equals(status)){
        				message=OrderStatusEnum.waitingClientPay.getDescription();
        			}else if(OrderStatusEnum.returnMoneySuccess.getStatus().equals(status)){
        				message=OrderStatusEnum.returnMoneySuccess.getDescription();
        			}else{
        				status=OrderStatusEnum.systemErro.getStatus();
        				message=OrderStatusEnum.systemErro.getDescription();
        			}
                }else{
                	status=OrderStatusEnum.initlize.getStatus();
    				message=OrderStatusEnum.initlize.getDescription();
                }
        	}
        	
        	returnDTO.setRetCode(status);
			returnDTO.setRetMessage(message);
			result = createJsonString(returnDTO); 
            
            }else if("4".equals(payChanel)){
            	//处理移动和包
            }else{
            	//处理微信和支付宝（走的是讯联接口）
            	
            	String status = "";
                String message = "";
                
                if(appTransInfo==null){//判断订单是否在数据库中存在
            		status=OrderStatusEnum.systemErro.getStatus();
    				message=OrderStatusEnum.systemErro.getDescription();
            	}else{//存在订单
            		status = appTransInfo.getStatus();
            		//判断订单时候完成
            		if(OrderStatusEnum.paySuccess.getStatus().equals(appTransInfo.getStatus())||OrderStatusEnum.returnMoneySuccess.getStatus().equals(appTransInfo.getStatus())){
            			//已经完成
            			 // 0支付成功 1支付失败2 未支付 100 系统异常
            			if(OrderStatusEnum.paySuccess.getStatus().equals(status)){
            				message=OrderStatusEnum.paySuccess.getDescription();
            			}else if(OrderStatusEnum.returnMoneySuccess.getStatus().equals(status)){
            				message=OrderStatusEnum.returnMoneySuccess.getDescription();
            			}
            		}else{
            			//未完成   调联接口查询
            			//调用讯联查询订单接口并处理本地数据库订单
            			String batchNo = prama.getBatchNo();//批次号 
                    	
                        String paymenttype = ""; //支付类型    支付宝 :012二维码(c2b)、011 付码 (b2c)    微信:  022二维码(c2b)  021付码（b2c）
                        String twoDimensionWay = prama.getTwoDimensionWay();//二维码有值     付码 付款没有值
                        if("1".equals(paymenttype)){//支付渠道 1微信2支付宝
                        	if("".equals(twoDimensionWay)||twoDimensionWay==null){
                        		paymenttype = "021"; //  021付码（b2c）
                        	}else{
                        		paymenttype = "022";// 022二维码(c2b)
                        	}
                        }else{
                        	if("".equals(twoDimensionWay)||twoDimensionWay==null){
                        		paymenttype = "011";//011 付码 (b2c)
                        	}else{
                        		paymenttype = "012";//012二维码(c2b)
                        	}
                        }
                        
                    	try {
                    		//调用讯联查询订单接口并处理本地数据库订单
                    		queryOrderForXL(order_no, batchNo, session,paymenttype);//order_no和上送的订单号不一样（讯联返回）	
        				} catch (Exception e) {
        					status=OrderStatusEnum.systemErro.getStatus();
            				message=OrderStatusEnum.systemErro.getDescription();
        				}
                    	//在从本地库查询更新后的订单结果
                    	appTransInfo = pmsAppTransInfoDao.searchOrderInfo(order_no);
                    	
                    	if(appTransInfo==null){
                            status=OrderStatusEnum.systemErro.getStatus();
                            message=OrderStatusEnum.systemErro.getDescription();
                    	}else{
                    		status = appTransInfo.getStatus();
                            message = "";
                            if(status!=null&&!("".equals(status))){
                            	 // 0支付成功 1支付失败2 未支付 100 系统异常
                    			if(OrderStatusEnum.paySuccess.getStatus().equals(status)){
                    				message=OrderStatusEnum.paySuccess.getDescription();
                    			}else if(OrderStatusEnum.payFail.getStatus().equals(status)){
                    				message=OrderStatusEnum.payFail.getDescription();
                    			}else if (OrderStatusEnum.waitingClientPay.getStatus().equals(status)){
                    				message=OrderStatusEnum.waitingClientPay.getDescription();
                    			}else if(OrderStatusEnum.returnMoneySuccess.getStatus().equals(status)){
                    				message=OrderStatusEnum.returnMoneySuccess.getDescription();
                    			}else{
                    				status=OrderStatusEnum.systemErro.getStatus();
                    				message=OrderStatusEnum.systemErro.getDescription();
                    			}
                            }else{
                            	status=OrderStatusEnum.initlize.getStatus();
                				message=OrderStatusEnum.initlize.getDescription();
                            }
                    	}
            			
            		}
            	}
            	
            	returnDTO.setRetCode(status);
    			returnDTO.setRetMessage(message);
    			result = createJsonString(returnDTO); 

            }*/
            
		}
		
		logger.info("返回app调用订单查询参数："+result+"时间："+UtilDate.getDateFormatter());
		logger.info("app调用订单查询调用结束， 订单号："+order_no +"，结束时间："+ UtilDate.getDateFormatter());
		
		return result;
	}

    /**
     * 百度回调后的操作
     * @author Jeff
     * @param baiduBackRequestDTO
     * @param response
     * @param session
     * @param request
     * @return
     */
    @Override
    public  Integer baiduCallBackHandel(BaiduBackRequestDTO baiduBackRequestDTO,HttpServletResponse response,HttpSession session,HttpServletRequest request)  throws Exception  {
        
    	logger.info("百度回调开始");
    	//验证签名
        Integer result = 0;
        BDUtil<BaiduBackRequestDTO> bdUtil = new BDUtil<BaiduBackRequestDTO>();
        //将可能是中文的字段做解码
        if(baiduBackRequestDTO != null){
            try {
                if(StringUtils.isNotBlank(baiduBackRequestDTO.getBuyer_sp_username())){
                    baiduBackRequestDTO.setBuyer_sp_username(new String(baiduBackRequestDTO.getBuyer_sp_username().getBytes(),"gbk"));
                }
                if(StringUtils.isNotBlank(baiduBackRequestDTO.getExtra())){
                    baiduBackRequestDTO.setExtra(new String(baiduBackRequestDTO.getExtra().getBytes(),"gbk"));
                }

            } catch (UnsupportedEncodingException e) {
                logger.info("处理中文编码异常 ，处理时间："+ UtilDate.getDateFormatter());
            }
        }
        
        //查新O单编号
        //根据订单号获取到本地订单
        PmsAppTransInfo appTransInfo = pmsAppTransInfoDao.searchOrderInfo(baiduBackRequestDTO.getOrder_no());
        if(appTransInfo!=null){//判断订单库是否存在此订单记录
        	if(baiduBackRequestDTO != null && bdUtil.make_sign(bdUtil.getClassFieldsArry(baiduBackRequestDTO,0),appTransInfo.getoAgentNo()).equals(baiduBackRequestDTO.getSign())){
                //调用百度处理逻辑
        		result = baiduHandelOrder(baiduBackRequestDTO);

        	}
        }
        
        logger.info("百度回调结束");
        return result;
    }

    /**
     * 欧飞回调后的操作
     * @param offiBackRequestDTO
     * @return
     * @throws Exception
     */
    @Override
    public Integer offiCallBackHandel(OffiBackRequestDTO offiBackRequestDTO) throws Exception {
        Integer result = 0;
        if(offiBackRequestDTO != null && StringUtils.isNumeric(offiBackRequestDTO.getRet_code())){
            //根据订单号获取到本地订单
            PmsAppTransInfo appTransInfo = pmsAppTransInfoDao.searchOrderInfo(offiBackRequestDTO.getSporder_id());

            if(appTransInfo != null && !appTransInfo.getStatus().equals(OrderStatusEnum.paySuccess.getStatus())){
                    //订单是未完成的状态才做后续操作
                Integer status = Integer.parseInt(offiBackRequestDTO.getRet_code());
                if(status == 1){
                    //支付成功
                    appTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
                    appTransInfo.setFinishtime(UtilDate.getDateFormatter());
                    pmsAppTransInfoDao.update(appTransInfo);
                    result = 1;
                }else if(status == 9){
                    //订单撤销
                    appTransInfo.setStatus(OrderStatusEnum.plantCancelOrder.getStatus());
                    appTransInfo.setFinishtime(UtilDate.getDateFormatter());
                    pmsAppTransInfoDao.update(appTransInfo);
                    result = 1;
                }
            }
        }
        return result;
    }

    /**
     * 百度商户收款百度回调处理
     * @author wumeng   20150508
     */
	public   int updateBaiduMake(String Status,String bfb_order_no,String Order_no,PmsAppTransInfo appTransInfo ,String StatusStr){
       
		//修改订单状态
        appTransInfo.setStatus(Status);
        //百度自己的订单号
        appTransInfo.setPortorderid(bfb_order_no);
       /* //生成流水
        PospTransInfo posp = null;
        try {
            posp = this.generateTransFromAppTrans(appTransInfo);
        } catch (Exception e) {
            logger.error("生成流水失败，订单号：" + appTransInfo.getOrderid());
            e.printStackTrace();
        }*/

        if(OrderStatusEnum.paySuccess.getStatus().equals(Status)){
             //订单结束时间
             appTransInfo.setFinishtime(UtilDate.getDateFormatter());
            /*//修改流水状态
            posp.setResponsecode("0000");*/
         }

       /* try {
            int result = pospTransInfoDAO.insert(posp);
            logger.info("插入流水成功");
        } catch (Exception e) {
            e.printStackTrace();
        }*/


        try {
        	int resultInt = pmsAppTransInfoDao.update(appTransInfo);
        	
            if(resultInt!=1){
                logger.info("订单状态修改失败， 订单号：" + Order_no + "，结束时间：" + UtilDate.getDateFormatter() + "。要修改的状态置：" + Status + StatusStr);
                return 0;
            }
            
        } catch (Exception e) {
            logger.info("订单状态修改失败， 订单号："+Order_no +"，结束时间："+ UtilDate.getDateFormatter()+"。要修改的状态置："+Status+StatusStr,e);
            return 0;
        }
		return 1;

	}
	
	

	/**
     * 百度手机充值
     * @author    Jeff
     */
	public synchronized Integer baiduPhonePay(String statusStr,String bfb_order_no,String Order_no,PmsAppTransInfo appTransInfo ,String StatusStr){
        Integer result = 0;
        //查询订单状态，确认订单是未支付的状态才支付
        try {
            PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(appTransInfo.getOrderid());
            //如果是支付成功，退款成功，撤销订单则不处理
            if(pmsAppTransInfo != null && pmsAppTransInfo.getStatus() != null &&
                    (pmsAppTransInfo.getStatus().equals(OrderStatusEnum.paySuccess.getStatus())
                            ||pmsAppTransInfo.getStatus().equals(OrderStatusEnum.returnMoneySuccess.getStatus())
                            ||pmsAppTransInfo.getStatus().equals(OrderStatusEnum.plantCancelOrder.getStatus()))){
                  //支付成功
                  logger.info("当前订单已经支付成功,或退款成功，跳过，orderId:"+pmsAppTransInfo.getOrderid()+"状态："+pmsAppTransInfo.getStatus());
            }else{
                 //没有支付  进行操作
                if(StringUtils.isNotBlank(statusStr)){

                    if(statusStr.equals("1")){
                        //处理成功
                        //修改数据库状态为 4  百度客户端支付成功，等待调用offi接口实现
                        logger.info("处理手机充值百度支付成功");
                        appTransInfo.setStatus(OrderStatusEnum.waitingPlantPay.getStatus());
                        //百度自己的订单号
                        appTransInfo.setPortorderid(bfb_order_no);
                        try {
                            Integer result1 = pmsAppTransInfoDao.update(appTransInfo);
                            if(result1 == 1){//修改状态成功，调用offi接口支付
                                Integer resultOffi = offiPay.mobilePay(appTransInfo);
                                if(resultOffi == 1){//支付成功
                                    //支付成功，修改订单状态
                                    appTransInfo.setThirdPartResultCode(resultOffi.toString());
                                    appTransInfo.setFinishtime(UtilDate.getDateFormatter());
                                    appTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
                                    result = pmsAppTransInfoDao.update(appTransInfo);
                                    //支付成功，插入流水表
                                    if(result == 1){
                                        //查看流水表中是否有当前记录
                                        PospTransInfo pospTransInfo = pospTransInfoDAO.searchByOrderId(appTransInfo.getOrderid());
                                        if(pospTransInfo != null){
                                            //存在，不操作
                                            logger.info("已经存在该流水，orderId="+pospTransInfo.getOrderId());
                                        }else{
                                            //不存在，生成并添加
                                            pospTransInfo =  generateTransFromAppTrans(appTransInfo);
                                            if(pospTransInfo != null){
                                                pospTransInfoDAO.insert(pospTransInfo);
                                            }
                                        }
                                    }
                                }else if(resultOffi == 2){ //正在支付，将状态改为正在支付
                                    appTransInfo.setThirdPartResultCode(resultOffi.toString());
                                    appTransInfo.setStatus(OrderStatusEnum.plantPayingNow.getStatus());
                                    result = pmsAppTransInfoDao.update(appTransInfo);
                                }
                            }else{
                                logger.info("订单状态修改失败， 订单号："+Order_no +"，结束时间："+ UtilDate.getDateFormatter()+"。要修改的状态置："+StatusStr);
                            }
                        } catch (Exception e) {
                            logger.info("订单状态修改失败， 订单号："+Order_no +"，结束时间："+ UtilDate.getDateFormatter()+"。要修改的状态置："+StatusStr);
                            e.printStackTrace();
                        }
                    }else if(statusStr.equals("2")){
                        // 百度回调等待支付
                        //修改数据库状态为 4  百度客户端支付成功，等待调用offi接口实现
                        try {
                            appTransInfo.setStatus(OrderStatusEnum.waitingClientPay.getStatus());
                            result =  pmsAppTransInfoDao.update(appTransInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else if(statusStr.equals("3")){
                        //百度回调退款成功
                        try {
                            appTransInfo.setStatus(OrderStatusEnum.returnMoneySuccess.getStatus());
                            result = pmsAppTransInfoDao.update(appTransInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.info("查询订单出错,详情："+e.getMessage());
            e.printStackTrace();
        }

        return  result;
	}
    
    /**
     * app调用异常统一处理
     * @author wumeng   20150508
     * @return result  返回前台json串
     */
    public String exceptUtility(HttpSession session) throws Exception{
    	String result = "";
    	MroducedTwoDimensionResponseDTO mroducedTwoDimensionResultDTO = new MroducedTwoDimensionResponseDTO();
		mroducedTwoDimensionResultDTO.setRetCode(OrderStatusEnum.systemErro.getStatus());
		mroducedTwoDimensionResultDTO.setRetMessage(OrderStatusEnum.systemErro.getDescription());
		result = createJsonString(mroducedTwoDimensionResultDTO);
		return result;
    }

    /**
     * 百度处理订单逻辑
     * @param baiduBackRequestDTO
     * @param appTransInfo
     * @return
     * @throws Exception
     */
    public  synchronized Integer baiduHandelOrder(BaiduBackRequestDTO baiduBackRequestDTO) throws Exception {

        Integer result = 0;
        
        
      //根据订单号获取到本地订单
        PmsAppTransInfo appTransInfo = pmsAppTransInfoDao.searchOrderInfo(baiduBackRequestDTO.getOrder_no());

        if(appTransInfo!=null){//判断订单库是否存在此订单记录
            //本地数据库交易状态码  0支付成功 1支付失败2 等待支付  3退款成功 100 系统异常
            if(OrderStatusEnum.paySuccess.getStatus().equals(appTransInfo.getStatus())||OrderStatusEnum.returnMoneySuccess.getStatus().equals(appTransInfo.getStatus())){
                logger.info(OrderStatusEnum.paySuccess.getDescription());
            	//订单处理完成
            	return 1;
            }else{
                //调用百度处理逻辑

                //未完成订单
                //调用接口查询百度服务器当前订单状态 并作后续处理
                Map<String, Object> queryResult = queryOrderForBD(baiduBackRequestDTO.getOrder_no(),appTransInfo.getoAgentNo());

                String query_status = (String) queryResult.get("query_status");//0表示查询到结果

                String payResult = (String) queryResult.get("pay_result");//百度订单处理状态  1支付成功2 等待支付  3退款成功

                String bfb_order_no = (String)queryResult.get("bfb_order_no");//百度的订单号

                if(baiduBackRequestDTO != null && StringUtils.isBlank(baiduBackRequestDTO.getBfb_order_no())){
                    baiduBackRequestDTO.setBfb_order_no(bfb_order_no);
                }

                if("0".equals(query_status)){//0表示查询到结果

                    if(StringUtils.isNotBlank(payResult) && payResult.equals("1")){//处理百度回调支付成功

                        //1 商户收款、2 转账汇款、3 信用卡还款、4手机充值、5 水煤电、 6 加油卡充值、
                        if(TradeTypeEnum.merchantCollect.getTypeCode().equals(appTransInfo.getTradetypecode())){
                            //处理商户收款回调
                        	result = updateBaiduMake(OrderStatusEnum.paySuccess.getStatus(), bfb_order_no, baiduBackRequestDTO.getOrder_no(), appTransInfo,OrderStatusEnum.paySuccess.getDescription());
                        		
                        	this.updateMerchantBalance(appTransInfo);
                        	if(pospTransInfoDAO.updetePospTransInfo(appTransInfo.getOrderid())!=1){
                           	 logger.info("流水表订单状态修改失败， 订单号：" + appTransInfo.getOrderid() + "，结束时间：" + UtilDate.getDateFormatter());
                                return 0;
                            }
                        	/*synchronized(this){
                        			String orderid = appTransInfo.getOrderid();
                        			//根据订单号查询订单状态和是否已经修改过余额
                        			HashMap<String, String> map =	pmsAppTransInfoDao.selectAccountingFlagAndStatus(orderid);
                        			// ACCOUNTINGFLAG   1  已经修改过 , STATUS   0支付成功
                        			if(!("1".equals(map.get("ACCOUNTINGFLAG")))&&OrderStatusEnum.paySuccess.getStatus().equals(map.get("STATUS"))){
                        				this.updateMerchantBalance(appTransInfo);
                        				if(pmsAppTransInfoDao.updateOrderAccountingFlag(orderid)!=1){
                        					logger.info("修改订单状态和是否已经修改过余额标记出错"+"订单号："+orderid);
                        				}
                        			}
                        		
                        	}*/
                            return	result;
                        }else if(TradeTypeEnum.transeMoney.getTypeCode().equals(appTransInfo.getTradetypecode())){
                            //处理转账汇款回调
                        }else if(TradeTypeEnum.creditCardRePay.getTypeCode().equals(appTransInfo.getTradetypecode())){
                            //处理信用卡还款回调
                        }else if(TradeTypeEnum.phonePay.getTypeCode().equals(appTransInfo.getTradetypecode())){
                                //处理手机充值回调
                                result = baiduPhonePay("1",bfb_order_no,baiduBackRequestDTO.getOrder_no(), appTransInfo,OrderStatusEnum.paySuccess.getDescription());
                        }else if(TradeTypeEnum.utility.getTypeCode().equals(appTransInfo.getTradetypecode())){
                        	    result = baiduUtilityPay("1", bfb_order_no,baiduBackRequestDTO.getOrder_no(),appTransInfo, OrderStatusEnum.paySuccess.getDescription());
                            //处理水煤电回调
                        }else if(TradeTypeEnum.sinopecPay.getTypeCode().equals(appTransInfo.getTradetypecode())){
                            //处理加油卡充值回调
                        		result = baiduSinopecCardPay("1",bfb_order_no,baiduBackRequestDTO.getOrder_no(), appTransInfo,OrderStatusEnum.paySuccess.getDescription());
                        }

                    }else if(StringUtils.isNotBlank(payResult) && payResult.equals("2")){//处理百度回调等待支付

                        //1 商户收款、2 转账汇款、3 信用卡还款、4手机充值、5 水煤电、 6 加油卡充值、
                        if(TradeTypeEnum.merchantCollect.getTypeCode().equals(appTransInfo.getTradetypecode())){
                            //处理商户收款回调
                            return	updateBaiduMake(OrderStatusEnum.waitingClientPay.getStatus(), bfb_order_no, baiduBackRequestDTO.getOrder_no(), appTransInfo,"等待支付");
                        }else if(TradeTypeEnum.transeMoney.getTypeCode().equals(appTransInfo.getTradetypecode())){
                            //处理转账汇款回调
                        }else if(TradeTypeEnum.creditCardRePay.getTypeCode().equals(appTransInfo.getTradetypecode())){
                            //处理信用卡还款回调
                        }else if(TradeTypeEnum.phonePay.getTypeCode().equals(appTransInfo.getTradetypecode())){
                            //处理手机充值回调
                            result = baiduPhonePay("2",bfb_order_no,baiduBackRequestDTO.getOrder_no(), appTransInfo,OrderStatusEnum.paySuccess.getDescription());
                        }else if(TradeTypeEnum.utility.getTypeCode().equals(appTransInfo.getTradetypecode())){
                            //处理水煤电回调
                        	result = baiduUtilityPay("2",bfb_order_no,baiduBackRequestDTO.getOrder_no(), appTransInfo,OrderStatusEnum.paySuccess.getDescription());
                        }else if(TradeTypeEnum.sinopecPay.getTypeCode().equals(appTransInfo.getTradetypecode())){
                            //处理加油卡充值回调
                        	result = baiduSinopecCardPay("2",bfb_order_no,baiduBackRequestDTO.getOrder_no(), appTransInfo,OrderStatusEnum.paySuccess.getDescription());
                        }

                    }else if(StringUtils.isNotBlank(payResult) && payResult.equals("3")){//处理百度回调退款成功
                        //1 商户收款、2 转账汇款、3 信用卡还款、4手机充值、5 水煤电、 6 加油卡充值、
                        if(TradeTypeEnum.merchantCollect.getTypeCode().equals(appTransInfo.getTradetypecode())){
                            //处理商户收款回调
                            return	updateBaiduMake(OrderStatusEnum.returnMoneySuccess.getStatus(), bfb_order_no, baiduBackRequestDTO.getOrder_no(), appTransInfo,"退款成功");
                        }else if(TradeTypeEnum.transeMoney.getTypeCode().equals(appTransInfo.getTradetypecode())){
                            //处理转账汇款回调
                        }else if(TradeTypeEnum.creditCardRePay.getTypeCode().equals(appTransInfo.getTradetypecode())){
                            //处理信用卡还款回调
                        }else if(TradeTypeEnum.phonePay.getTypeCode().equals(appTransInfo.getTradetypecode())){
                            //处理手机充值回调
                            result = baiduPhonePay("3",bfb_order_no,baiduBackRequestDTO.getOrder_no(), appTransInfo,OrderStatusEnum.paySuccess.getDescription());
                        }else if(TradeTypeEnum.utility.getTypeCode().equals(appTransInfo.getTradetypecode())){
                            //处理水煤电回调
                        	result = baiduUtilityPay("3",bfb_order_no,baiduBackRequestDTO.getOrder_no(), appTransInfo,OrderStatusEnum.paySuccess.getDescription());
                        }else if(TradeTypeEnum.sinopecPay.getTypeCode().equals(appTransInfo.getTradetypecode())){
                            //处理加油卡充值回调
                        	result = baiduSinopecCardPay("3",bfb_order_no,baiduBackRequestDTO.getOrder_no(), appTransInfo,OrderStatusEnum.paySuccess.getDescription());
                        }

                    }else{//其它情况


                        //修改订单状态
                        appTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());//1支付失败
                        //百度自己的订单
                        appTransInfo.setPortorderid(baiduBackRequestDTO.getBfb_order_no());

                        //订单结束时间
                        appTransInfo.setFinishtime(UtilDate.getDateFormatter());
                        try {
                            if(pmsAppTransInfoDao.update(appTransInfo)!=1){
                                logger.info("订单状态修改失败， 订单号："+baiduBackRequestDTO.getOrder_no() +"，结束时间："+ UtilDate.getDateFormatter()+"。要修改的状态置：1支付失败");
                            }
                        } catch (Exception e) {
                            logger.info("订单状态修改失败， 订单号："+baiduBackRequestDTO.getOrder_no() +"，结束时间："+ UtilDate.getDateFormatter()+"。要修改的状态置：1支付支付失败",e);
                        }
                    }
                }/*else{
                    //修改订单状态
                    appTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());//1支付失败
                    //订单结束时间
                    appTransInfo.setFinishtime(UtilDate.getDateFormatter());
                    if(pmsAppTransInfoDao.update(appTransInfo)!=1){
                        logger.info("订单状态修改失败， 订单号："+baiduBackRequestDTO.getOrder_no() +"，结束时间："+ UtilDate.getDateFormatter()+"。要修改的状态置：1支付失败");
                    }
                }*/
            	
            }
        }
        
        
        return  result;
    }

    
    
    
    /**
     * 更新商户账户余额
     * @author wumeng   20150522
     * @param appTransInfo   余额
     * @throws Exception 
     */
	public synchronized int updateMerchantBalance(PmsAppTransInfo appTransInfo) throws Exception{
		
			int resultInt = 0;
			String orderid = appTransInfo.getOrderid();//订单号
			
			boolean  flag = false;
			//商户收款   中 只有百度支付是异步回调可能出现多次调用其他都是同步返回处理
			if("1".equals(appTransInfo.getTradetypecode())&&"2".equals(appTransInfo.getPaymentcode())){
				HashMap<String, String> map =	pmsAppTransInfoDao.selectAccountingFlagAndStatus(orderid);
				// ACCOUNTINGFLAG   1  已经修改过 , STATUS   0支付成功
				flag = !("1".equals(map.get("ACCOUNTINGFLAG")))&&OrderStatusEnum.paySuccess.getStatus().equals(map.get("STATUS"));
    		}else {
    			flag = true;
    		}
			
			if(flag){
				BigDecimal updateAmount = new BigDecimal(0);//要记录的金额
				try {
		        	BigDecimal balance= accountDao.selectMerchantBalance(appTransInfo.getMercid());
		    		
		    		TAccAccount	tAccAccount = new TAccAccount();
		    		tAccAccount.setAccNum(appTransInfo.getMercid());
		    		
		    		if(TradeTypeEnum.merchantCollect.getTypeCode().equals(appTransInfo.getTradetypecode())||TradeTypeEnum.shop.getTypeCode().equals(appTransInfo.getTradetypecode())
		    				||TradeTypeEnum.onlinePay.getTypeCode().equals(appTransInfo.getTradetypecode())){
		    			//交易金额加处理商户收款  采用结算金额
		    			updateAmount = new BigDecimal(appTransInfo.getPayamount());
		    			tAccAccount.setBalance(new BigDecimal(appTransInfo.getPayamount()).add(balance) );
		    		}else if(TradeTypeEnum.drawMoney.getTypeCode().equals(appTransInfo.getTradetypecode())){
		    			//交易金额加 处理提现（提现中金额都为负数）采用实际金额
		    			updateAmount = new BigDecimal(appTransInfo.getFactamount());
		    			tAccAccount.setBalance(new BigDecimal(appTransInfo.getFactamount()).add(balance) );
		    		}
		    		 
		    		tAccAccount.setLastBalance(balance)	;
		    		tAccAccount.setModifiedTime(new Date());
		        	
		        	 resultInt = accountDao.updateMerchantBalance(tAccAccount);
		        	logger.info("更新商户账户余额，商户ID："+appTransInfo.getMercid() +"，结束时间："+ UtilDate.getDateFormatter()+"  交易类型: "+appTransInfo.getTradetypecode()+"  更新前金额："+balance.toString()+"  更新金额："+updateAmount+"  订单号："+appTransInfo.getOrderid());
		        	
		        	if(resultInt!=1){
		                logger.info("更新商户账户余额失败，商户ID："+appTransInfo.getMercid() +"，结束时间："+ UtilDate.getDateFormatter()+"  交易类型: "+appTransInfo.getTradetypecode()+"  更新前金额："+balance.toString()+"  更新金额："+updateAmount+"  订单号："+appTransInfo.getOrderid());
		                return 0;
		            }else{
		            	
		            	
		            	
		            	//先判断O单类型      是T0还是T1的走不通的方法刷卡
		    			
		    			PmsAgentInfo pmsAgentInfo = pmsAgentInfoDao.selectOagentByMercNum(appTransInfo.getMercid());
		    			if(pmsAgentInfo!=null){
		    				String  clearType = pmsAgentInfo.getClearType();//O单清算类型 ：0:T+0;  1:T+1;  2:T+N
			    			
			    			if("0".equals(clearType)||"2".equals(clearType)){
			    				try {
			    					
			    					//	O单类型      是T0   T_TRANS_SETTLE_AGENT_T0
				    				TTransSettleAgentT0 tTransSettleAgentT0 = new TTransSettleAgentT0();
				    				tTransSettleAgentT0.setMercId(appTransInfo.getMercid());
				    				tTransSettleAgentT0.setOagentno(appTransInfo.getoAgentNo());
				    				tTransSettleAgentT0.setOrderid(appTransInfo.getOrderid());
				    				tTransSettleAgentT0.setFactamount(appTransInfo.getFactamount());
				    				tTransSettleAgentT0.setOrderamount(appTransInfo.getOrderamount());
				    				//刷卡手续费=总手续费-清算手续费           这样计算是因为订单表里面Poundage存的是总手续费
				    				String Poundage = (new BigDecimal(appTransInfo.getTotalpoundage()).subtract(new BigDecimal(appTransInfo.getSettlepoundage()))).toString();
				    				tTransSettleAgentT0.setPoundage(Poundage);//刷卡手续费
				    				tTransSettleAgentT0.setTotalpoundage(appTransInfo.getTotalpoundage());//总手续费
				    				tTransSettleAgentT0.setSettlepoundage(appTransInfo.getSettlepoundage());//清算手续费   
				    				tTransSettleAgentT0.setPayamount(appTransInfo.getPayamount());//结算金额
				    				tTransSettleAgentT0.setCreateTime(UtilDate.getDateAndTimes());
				    				//查询清算给商户的银行名称    银行卡号等
				    				PmsMerchantInfo pmsMerchantInfo = pmsMerchantInfoDao.selectMerchantInfoByMercid(appTransInfo.getMercid());
				    				tTransSettleAgentT0.setBankname(pmsMerchantInfo.getBankname());
				    				tTransSettleAgentT0.setClrMerc(pmsMerchantInfo.getClrMerc());
				    				tTransSettleAgentT0.setSettlementname(pmsMerchantInfo.getSettlementname());
				    				tTransSettleAgentT0.setBanksysnumber(pmsMerchantInfo.getBanksysnumber());
				    				tTransSettleAgentT0.setPaystatust("0");//0代付未发送   1代付已发送   2 代付成功   3代付失败   4代付暂不支持此卡
				    				tTransSettleAgentT0.setPaymsg("代付未发送");//0代付未发送   1代付已发送   2 代付成功   3代付失败   4代付暂不支持此卡
				    				tTransSettleAgentT0.setStatus("1");//清算结果标记位    是否成功      0  已清算      1未清算   2 等待代付系统处理
									if(tTransSettleAgentT0Dao.insertAccountHistoryFor0(tTransSettleAgentT0)!=1){
										logger.info("添加商户清算O单T0记录出错"+"订单号："+orderid+"商户编号："+appTransInfo.getMercid()+"金额："+updateAmount.toString());
										//return 0;
									}
								} catch (Exception e) {
									e.printStackTrace();
									logger.info("添加商户清算O单T0记录出错"+"订单号："+orderid+"商户编号："+appTransInfo.getMercid()+"金额："+updateAmount.toString());
									//return 0;
								}
			    				
			    			
			    			
			    			}else if("1".equals(clearType)){//O单清算类型 ：0:T+0;  1:T+1;  2:T+N
			    				
			    				//O单类型      是T1   T_ACC_ACCOUNT_TRANS
			    				Map<String, String> param = new HashMap<String, String>();
				            	param.put("mercid", appTransInfo.getMercid());
				            	
				            	param.put("amount", updateAmount.toString());
				            	
				            	
				            	param.put("creaetetime", UtilDate.getDateFormatter());
				            	param.put("tradetype", appTransInfo.getTradetype());
				            	param.put("tradetypecode", appTransInfo.getTradetypecode());
				            	param.put("paymenttype", appTransInfo.getPaymenttype());
				            	param.put("paymentcode", appTransInfo.getPaymentcode());
				            	param.put("orderid", orderid);
				            	param.put("orderamount", appTransInfo.getOrderamount());
				            	param.put("poundage", appTransInfo.getPoundage());
				            	param.put("oAgentNo", appTransInfo.getoAgentNo());
				            	try {
									if(accountDao.insertAccountHistory(param)!=1){
										logger.info("添加商户账户余额记录出错"+"订单号："+orderid+"商户编号："+appTransInfo.getMercid()+"金额："+updateAmount.toString());
										//return 0;
									}
								} catch (Exception e) {
									e.printStackTrace();
									logger.info("添加商户账户余额记录出错"+"订单号："+orderid+"商户编号："+appTransInfo.getMercid()+"金额："+updateAmount.toString());
									//return 0;
								}
			    			}else{
			    				logger.info("添加修改过余额记录查询商户所属O单类型出错,类型不对"+"订单号："+orderid+" 时间："+UtilDate.getDateFormatter());
			    			}
		    			}else{
		    				logger.info("添加修改过余额记录查询商户所属O单类型出错"+"订单号："+orderid+" 时间："+UtilDate.getDateFormatter());
		    			}
		    			
		            	
						
		            	try {
							if(pmsAppTransInfoService.updateOrderAccountingFlag(orderid)!=1){
								logger.info("修改订单状态和是否已经修改过余额标记出错"+"订单号："+orderid);
							//	return 0;
							}
						} catch (Exception e) {
							e.printStackTrace();
							logger.info("修改订单状态和是否已经修改过余额标记出错"+"订单号："+orderid);
						//	return 0;
						}
		            }
		        } catch (Exception e) {
		        	e.printStackTrace();
		        	 logger.info("更新商户账户余额失败，商户ID："+appTransInfo.getMercid() +"，结束时间："+ UtilDate.getDateFormatter()+"  交易类型: "+appTransInfo.getTradetypecode()+"  更新金额："+updateAmount+"  订单号："+appTransInfo.getOrderid(),e);
		            return 0;
		        }
				
				
			}
			return resultInt;
		

	}
    
	
    /**
     * 线程使用
     * @param accountDao 
     * @param pospTransInfoDAO2 
     * @throws Exception
     */
    public void setDao(IPmsAppTransInfoDao pmsAppTransInfoDao, ITAccAccountDao accountDao,
    		IPmsAppTransInfoService pmsAppTransInfoService,IPospTransInfoDAO pospTransInfoDAO,IPmsAgentInfoDao pmsAgentInfoDao){
    	this.pmsAppTransInfoDao=pmsAppTransInfoDao;
    	this.accountDao=accountDao;
    	this.pmsAppTransInfoService=pmsAppTransInfoService;
    	this.pospTransInfoDAO=pospTransInfoDAO;
    	this.pmsAgentInfoDao=pmsAgentInfoDao;
    }
    
    /**
	 * 百度水煤电充值
	 */
	public Integer baiduUtilityPay(String statusStr, String bfb_order_no,
			String Order_no, PmsAppTransInfo appTransInfo, String StatusStr) {
		Integer result = 0;
        //查询订单状态，确认订单是未支付的状态才支付
        try {
            PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(appTransInfo.getOrderid());
            //如果是支付成功，退款成功，撤销订单则不处理
            if(pmsAppTransInfo != null && pmsAppTransInfo.getStatus() != null &&
                    (pmsAppTransInfo.getStatus().equals(OrderStatusEnum.paySuccess.getStatus())
                            ||pmsAppTransInfo.getStatus().equals(OrderStatusEnum.returnMoneySuccess.getStatus())
                            ||pmsAppTransInfo.getStatus().equals(OrderStatusEnum.plantCancelOrder.getStatus()))){
                  //支付成功
                  logger.info("当前订单已经支付成功,或退款成功，跳过，orderId:"+pmsAppTransInfo.getOrderid()+"状态："+pmsAppTransInfo.getStatus());
            }else{
                 //没有支付  进行操作
                if(StringUtils.isNotBlank(statusStr)){

                    if(statusStr.equals("1")){
                        //处理成功
                        //修改数据库状态为 4  百度客户端支付成功，等待调用offi接口实现
                        logger.info("处理水煤电充值百度支付成功");
                        appTransInfo.setStatus(OrderStatusEnum.waitingPlantPay.getStatus());
                        //百度自己的订单号
                        appTransInfo.setPortorderid(bfb_order_no);
                        try {
                            Integer result1 = pmsAppTransInfoDao.update(appTransInfo);
                            if(result1 == 1){//修改状态成功，调用offi接口支付
                            	Integer resultOffi = offiPay.utilityOrder(appTransInfo);
                                if(resultOffi == 1){//支付成功
                                    //支付成功，修改订单状态
                                    appTransInfo.setThirdPartResultCode(resultOffi.toString());
                                    appTransInfo.setFinishtime(UtilDate.getDateFormatter());
                                    appTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
                                    result = pmsAppTransInfoDao.update(appTransInfo);
                                    //支付成功，插入流水表
                                    if(result == 1){
                                        //查看流水表中是否有当前记录
                                        PospTransInfo pospTransInfo = pospTransInfoDAO.searchByOrderId(appTransInfo.getOrderid());
                                        if(pospTransInfo != null){
                                            //存在，不操作
                                            logger.info("已经存在该流水，orderId="+pospTransInfo.getOrderId());
                                        }else{
                                            //不存在，生成并添加
                                            pospTransInfo =  generateTransFromAppTrans(appTransInfo);
                                            if(pospTransInfo != null){
                                                pospTransInfoDAO.insert(pospTransInfo);
                                            }
                                        }
                                    }
                                }else if(resultOffi == 2){ //正在支付，将状态改为正在支付
                                    appTransInfo.setThirdPartResultCode(resultOffi.toString());
                                    appTransInfo.setStatus(OrderStatusEnum.plantPayingNow.getStatus());
                                    result = pmsAppTransInfoDao.update(appTransInfo);
                                }
                            }else{
                                logger.info("订单状态修改失败， 订单号："+Order_no +"，结束时间："+ UtilDate.getDateFormatter()+"。要修改的状态置："+StatusStr);
                            }
                        } catch (Exception e) {
                            logger.info("订单状态修改失败， 订单号："+Order_no +"，结束时间："+ UtilDate.getDateFormatter()+"。要修改的状态置："+StatusStr);
                            e.printStackTrace();
                        }
                    }else if(statusStr.equals("2")){
                        // 百度回调等待支付
                        //修改数据库状态为 4  百度客户端支付成功，等待调用offi接口实现
                        try {
                            appTransInfo.setStatus(OrderStatusEnum.waitingClientPay.getStatus());
                            result =  pmsAppTransInfoDao.update(appTransInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else if(statusStr.equals("3")){
                        //百度回调退款成功
                        try {
                            appTransInfo.setStatus(OrderStatusEnum.returnMoneySuccess.getStatus());
                            result = pmsAppTransInfoDao.update(appTransInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.info("查询订单出错,详情："+e.getMessage());
            e.printStackTrace();
        }

        return  result;
	}

	/**
	 * 百度加油卡充值
	 */
	public Integer baiduSinopecCardPay(String statusStr, String bfb_order_no,
			String Order_no, PmsAppTransInfo appTransInfo, String StatusStr) {
		Integer result = 0;
        //查询订单状态，确认订单是未支付的状态才支付
        try {
            PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(appTransInfo.getOrderid());
            //如果是支付成功，退款成功，撤销订单则不处理
            if(pmsAppTransInfo != null && pmsAppTransInfo.getStatus() != null &&
                    (pmsAppTransInfo.getStatus().equals(OrderStatusEnum.paySuccess.getStatus())
                            ||pmsAppTransInfo.getStatus().equals(OrderStatusEnum.returnMoneySuccess.getStatus())
                            ||pmsAppTransInfo.getStatus().equals(OrderStatusEnum.plantCancelOrder.getStatus()))){
                  //支付成功
                  logger.info("当前订单已经支付成功,或退款成功，跳过，orderId:"+pmsAppTransInfo.getOrderid()+"状态："+pmsAppTransInfo.getStatus());
            }else{
                 //没有支付  进行操作
                if(StringUtils.isNotBlank(statusStr)){

                    if(statusStr.equals("1")){
                        //处理成功
                        //修改数据库状态为 4  百度客户端支付成功，等待调用offi接口实现
                        logger.info("处理加油卡充值百度支付成功");
                        appTransInfo.setStatus(OrderStatusEnum.waitingPlantPay.getStatus());
                        //百度自己的订单号
                        appTransInfo.setPortorderid(bfb_order_no);
                        try {
                            Integer result1 = pmsAppTransInfoDao.update(appTransInfo);
                            if(result1 == 1){//修改状态成功，调用offi接口支付
                            	Integer resultOffi = offiPay.sinopecOrder(appTransInfo);
                                if(resultOffi == 1){//支付成功
                                    //支付成功，修改订单状态
                                    appTransInfo.setThirdPartResultCode(resultOffi.toString());
                                    appTransInfo.setFinishtime(UtilDate.getDateFormatter());
                                    appTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
                                    result = pmsAppTransInfoDao.update(appTransInfo);
                                    //支付成功，插入流水表
                                    if(result == 1){
                                        //查看流水表中是否有当前记录
                                        PospTransInfo pospTransInfo = pospTransInfoDAO.searchByOrderId(appTransInfo.getOrderid());
                                        if(pospTransInfo != null){
                                            //存在，不操作
                                            logger.info("已经存在该流水，orderId="+pospTransInfo.getOrderId());
                                        }else{
                                            //不存在，生成并添加
                                            pospTransInfo =  generateTransFromAppTrans(appTransInfo);
                                            if(pospTransInfo != null){
                                                pospTransInfoDAO.insert(pospTransInfo);
                                            }
                                        }
                                    }
                                }else if(resultOffi == 2){ //正在支付，将状态改为正在支付
                                    appTransInfo.setThirdPartResultCode(resultOffi.toString());
                                    appTransInfo.setStatus(OrderStatusEnum.plantPayingNow.getStatus());
                                    result = pmsAppTransInfoDao.update(appTransInfo);
                                }
                            }else{
                                logger.info("订单状态修改失败， 订单号："+Order_no +"，结束时间："+ UtilDate.getDateFormatter()+"。要修改的状态置："+StatusStr);
                            }
                        } catch (Exception e) {
                            logger.info("订单状态修改失败， 订单号："+Order_no +"，结束时间："+ UtilDate.getDateFormatter()+"。要修改的状态置："+StatusStr);
                            e.printStackTrace();
                        }
                    }else if(statusStr.equals("2")){
                        // 百度回调等待支付
                        //修改数据库状态为 4  百度客户端支付成功，等待调用offi接口实现
                        try {
                            appTransInfo.setStatus(OrderStatusEnum.waitingClientPay.getStatus());
                            result =  pmsAppTransInfoDao.update(appTransInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else if(statusStr.equals("3")){
                        //百度回调退款成功
                        try {
                            appTransInfo.setStatus(OrderStatusEnum.returnMoneySuccess.getStatus());
                            result = pmsAppTransInfoDao.update(appTransInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.info("查询订单出错,详情："+e.getMessage());
            e.printStackTrace();
        }

        return  result;
	}
	
	
	/**
	 * 扫描用户     移动和包   付款  第一步
	 * wumeng  20150608
	 * @param prama
	 * @param request
	 * @param session
	 * @param premiumRate
	 */
	public MroducedTwoDimensionResponseDTO cmpay(ProducedTwoDimensionDTO prama, SessionInfo  sessionInfo,String rateStr,String oAgentNo) throws Exception {
			 logger.info("调用用户的移动和包付款接口调用第一步开始，时间："+UtilDate.getDateFormatter());
			
			 String  orderid = "";//订单号
			
			 ViewKyChannelInfo channelInfo = AppPospContext.context.get(CMPAY+CMPAYSCANCODE);
			 //组装报文
			 orderid = UtilMethod.getOrderid("109");  					//10业务号9业务细;	订单号      现根据规则生成订单号
			 String characterSet = Constants.CHARACTERSET; 				//编码格式   00--GBK;01--GB2312;02--UTF-8   可以为空默认00--GBK
			 String notifyUrl=channelInfo.getCallbackurl();   //后台通知URL
			 String merchantId = channelInfo.getChannelNO();            //商户编号  我方平台给商户分配的唯一标识
			 String requestId =orderid;                     		    //商户请求的交易流水号唯一 
			 String signType = Constants.SIGNTYPE;					    //只能是MD5，RSA
			 String version = Constants.VERSION;                        //版本号2.0.0
			 //String merchantCert="";					                //商户证书公钥      可以为空   不参与签名； 如果signType=RSA，此项必输
			 String amount=prama.getPayAmt();		                    //订单金额，以分为单位
			 String currency=Constants.CURRENCY;                        //   00 CNY-现金     ;  01  CMY-充值卡默认为：00
			 String orderDate=UtilDate.getDate();                       //商户发起请求的时间; 年年年年月月日日
			 String orderId=orderid;                                    //商户系统订单号
			 String period=Constants.PERIOD;                            //有效期数量         数字（云支付订单有效期不能大于30分钟）
		     String periodUnit=Constants.PERIODUNIT;		            //有效期单位  只能取以下枚举值   00-分
		     String productName="商户收款";                             //商品名称
		     String userToken=	prama.getPayCode();//"100814504460655533";	//			   //18位条码
		     String oprId = sessionInfo.getMercId();                        //商户操作员编号
		    
		     String type = "CloudQuickPay";
		     
		     
		     //-- 签名
			 String signData = characterSet  + notifyUrl
						 + merchantId + requestId + signType + type
						 + version + amount  + currency
						 + orderDate + orderId  + period + periodUnit 
						 + URLDecoder.decode(productName,"UTF-8") 
						 + userToken + oprId ;
				
			
			HiiposmUtil util = new HiiposmUtil();
			String hmac = util.MD5Sign(signData, channelInfo.getChannelPwd());  //获得hmac的方法见签名算法,参数顺序按照表格中从上到下的顺序,但不包括本参数.	
			 
			//-- 请求报文
			String buf = "characterSet=" + characterSet 
						+ "&notifyUrl=" + notifyUrl +  "&merchantId="
						+ merchantId + "&requestId=" + requestId + "&signType="
						+ signType + "&type=" + type + "&version=" + version
						+ "&amount=" + amount 
						+ "&currency=" + currency + "&orderDate=" + orderDate
						+ "&orderId=" + orderId + "&period=" + period
						+ "&periodUnit=" + periodUnit + "&productName=" + productName 
						+ "&userToken=" + userToken +"&oprId="+oprId;
			//-- 带上消息摘要
	
			buf = "hmac=" + hmac + "&" + buf;
			 
			
			YDHBRequestDTO yDHBRequestDTO = new YDHBRequestDTO();
			yDHBRequestDTO.setBuf(buf);
			yDHBRequestDTO.setCharacterSet(characterSet);
			yDHBRequestDTO.setOrderid(orderId);
			yDHBRequestDTO.setoAgentNo(oAgentNo);

		//订单   流水  入库    调移动和包接口后在修改处理状态
		boolean flag = insertOrderForCmpay(orderid, prama.getPayAmt(), buf, sessionInfo.getMercId(),rateStr,CMPAYSCANCODE,oAgentNo);

		MroducedTwoDimensionResponseDTO mroducedTwoDimensionResponseDTO = new MroducedTwoDimensionResponseDTO();
		String retMessage="";
		String retCode="";

		if(flag){
			retCode ="0";
			retMessage = "生成订单和流水成功 ";

		}else{
			retCode ="1";
			retMessage = "生成订单和流水失败 ";
		}

		mroducedTwoDimensionResponseDTO.setRetCode(retCode);
		mroducedTwoDimensionResponseDTO.setRetMessage(retMessage);
		mroducedTwoDimensionResponseDTO.setyDHBRequestDTO(yDHBRequestDTO);
		mroducedTwoDimensionResponseDTO.setPayChannel("4");

		logger.info("调用用户的支付宝付款接口调用第一步返回："+createJsonString(mroducedTwoDimensionResponseDTO)+"，结束时间："+ UtilDate.getDateFormatter());

		return mroducedTwoDimensionResponseDTO;


	}
	/**
	 * 扫描用户      移动和包   付款  第二步
	 * wumeng  20150512
	 * @param prama
	 * @param request
	 * @param session
	 * @param premiumRate
	 */
	public String cmpay(YDHBRequestDTO yDHBRequestDTO) throws Exception {
		logger.info("调用用户的移动和包付款接口调用第二步开始，时间："+UtilDate.getDateFormatter());
		String result="";
		MroducedTwoDimensionResponseDTO mroducedTwoDimensionResponseDTO = new MroducedTwoDimensionResponseDTO();
		HiiposmUtil util = new HiiposmUtil();
		ViewKyChannelInfo channelInfo = AppPospContext.context.get(CMPAY+CMPAYSCANCODE);
		
		logger.info("调用用户的移动和包付款接口调请求参数："+yDHBRequestDTO.getBuf());
		String res = util.sendAndRecv(channelInfo.getUrl(), yDHBRequestDTO.getBuf(), yDHBRequestDTO.getCharacterSet());
				

		String code = util.getValue(res, "returnCode");
		String message = URLDecoder.decode(util.getValue(res, "message"),"UTF-8");
		
		
		logger.info("调用用户的移动和包付款接口调返回参数："+res+"code="+code+" ;  message="+message);
		
		String status ="";
		String retMessage="";
		String orderid = yDHBRequestDTO.getOrderid();//订单号
		if (code.equals("000000")) {
			status="0";
			retMessage="生成订单成功";
			//线程查询处理移动和包处理结果并对本地订单进行处理
			ThreadPool.executor(new YDHBTaskThread(orderid, pmsAppTransInfoDao,accountDao,pmsAppTransInfoService,pospTransInfoDAO,pmsAgentInfoDao));

		}else{
			status="1";
			retMessage=message;
		}

		updateOrder(code,yDHBRequestDTO.getOrderid());
			
		mroducedTwoDimensionResponseDTO.setRetCode(status);
		mroducedTwoDimensionResponseDTO.setRetMessage(retMessage);
		mroducedTwoDimensionResponseDTO.setOrderNumber(orderid);
		
		
		result = createJsonString(mroducedTwoDimensionResponseDTO);

		logger.info("调用用户的支付宝付款接口调用结束， 订单号："+orderid+" 反回前台参数："+result+"，结束时间："+ UtilDate.getDateFormatter());

		return result;


	}
	
	
	/**
	 * 订单入库  移动和包
	 * wumeng  20150608
	 * @param orderid 订单号
	 * @param payamount 交易金额
	 * @param array 订单详细信息
	 * @param mercId 用户ID
	 * @param premiumRate
	 * @throws Exception
	 */
	public boolean insertOrderForCmpay(String orderid,String payamount,String sendString,String mercId, String rateStr ,String businessnum,String oAgentNo) throws Exception {

		boolean result =false;
		//查询商户费率 
		BigDecimal rate = new BigDecimal(rateStr);
		BigDecimal amount = new BigDecimal(payamount);

		//成功后订到入库app后台
		PmsAppTransInfo  pmsAppTransInfo= new PmsAppTransInfo();
		pmsAppTransInfo.setPaymenttype("移动和包支付");
		pmsAppTransInfo.setTradetype("商户收款");
		pmsAppTransInfo.setBusinessNum(businessnum);//业务编号
		pmsAppTransInfo.setChannelNum(CMPAY);//通道号
		pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter());
		pmsAppTransInfo.setOrderid(orderid);//上送的订单号
		
		pmsAppTransInfo.setReasonofpayment("商户收款");
		pmsAppTransInfo.setMercid(mercId);
		pmsAppTransInfo.setFactamount(payamount);//实际金额    按分为最小单位  例如：1元=100分   采用100
		pmsAppTransInfo.setPaymentcode("6");
		pmsAppTransInfo.setTradetypecode("1");
		pmsAppTransInfo.setOrderamount(payamount);//订单金额  按分为最小单位  例如：1元=100分   采用100
		pmsAppTransInfo.setStatus(Constants.ORDERINITSTATUS);//订单初始化状态
		
		/*BigDecimal poundage = new BigDecimal(0);
		
		if("1".equals(isTop)){//判断费率是不是封顶费率      封顶费率有最大的手续费值
			//封顶费率
			poundage = amount.multiply(rate);
			BigDecimal maxPoundage = new BigDecimal(topPoundage);//最大手续费
			if(poundage.compareTo(maxPoundage)==1){
				poundage = maxPoundage;
			}
			pmsAppTransInfo.setRate(rateStr+"-"+topPoundage);//费率    
		}else{
			poundage = amount.multiply(rate);//不是封顶费率
			pmsAppTransInfo.setRate(rateStr);//费率    
		}*/
		
		
		BigDecimal poundage = amount.multiply(rate);//手续费
		pmsAppTransInfo.setRate(rateStr);//费率
		//结算金额   按分为最小单位  例如：1元=100分   采用100   商户收款时给商户记账时减去费率(实际金额-手续费)
		pmsAppTransInfo.setPayamount(amount.subtract(poundage).toString());
		pmsAppTransInfo.setPoundage(poundage.toString());//手续费  按分为最小单位  例如：1元=100分   采用100

		
		try {
			if(pmsAppTransInfoDao.insert(pmsAppTransInfo)!=1){
				logger.info("订单入库失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。详细信息："+sendString);
				throw new RuntimeException("手动抛出");
			}else{
				PospTransInfo pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);//拼插入流水表实体类
				if(pospTransInfo != null){
                    if(pospTransInfoDAO.insert(pospTransInfo)!=1){
                    	logger.info("订单入流水表失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。详细信息："+sendString);
                    	throw new RuntimeException("手动抛出");
                    }else{
                    	result=true;//生成订单和流水成功
                    }
                }else{
                		logger.info("订单入流水表失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。详细信息："+sendString);
                		throw new RuntimeException("手动抛出");
                }
			}
		} catch (Exception e) {
			logger.info("订单入库失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。详细信息："+sendString,e);
			throw new RuntimeException("手动抛出");
		}
		return result;


	}
	
	
	/**
	 * 移动和包订单查询  
	 * wumeng  20150609
	 * @param orderid
	 */
	public String queryOrderForYDHB(String orderid,String oAgentNo)throws Exception {
			
			logger.info("调用移动和包订单查询接口调用开始，时间："+UtilDate.getDateFormatter());
			
			ViewKyChannelInfo channelInfo = AppPospContext.context.get(CMPAY+CMPAYQUERY);
			
			//商户编号
			String merchantId = channelInfo.getChannelNO();
			String requestId =orderid;                     		    //商户请求的交易流水号唯一 
			String signType = Constants.SIGNTYPE;					    //只能是MD5，RSA
			String version = Constants.QUERYVERSION;                        //查询版本号2.0.1
			String type = "OrderQuery";
			String characterSet = Constants.CHARACTERSET; 				//编码格式   00--GBK;01--GB2312;02--UTF-8   可以为空默认00--GBK
			
			String orderId = orderid;

			//-- 签名
			String signData = merchantId + requestId + signType + type
					+ version + orderId;

			HiiposmUtil util = new HiiposmUtil();
			String hmac = util.MD5Sign(signData, channelInfo.getChannelPwd());

			//-- 请求报文
			String buf = "merchantId=" + merchantId + "&requestId="
					+ requestId + "&signType=" + signType + "&type=" + type
					+ "&version=" + version + "&orderId=" + orderId;
			buf = "hmac=" + hmac + "&" + buf;

			//发起http请求，并获取响应报文
			String res = util.sendAndRecv(channelInfo.getUrl(), buf, characterSet);

			logger.info("调用移动和包订单查询接口调用结束， 订单号：" + orderid +"返回消息："+URLDecoder.decode(util.getValue(res, "message"),"UTF-8")
					+"返回参数："+res+ "，结束时间：" + UtilDate.getDateFormatter());
			
			return res;
		
	}
	
	
	
	/**
     * 移动和包回调后的操作
     * @param yDHLBackRequestDTO
     * @param response
     * @param session
     * @param request
     */
	@Override
	public Integer yDHBOrderCallBack(HttpServletResponse response, HttpSession session,HttpServletRequest request) throws Exception {
		ViewKyChannelInfo channelInfo = AppPospContext.context.get(CMPAY+CMPAYKEY);
		//进行验签
		String signKey = channelInfo.getChannelPwd();
		
		try {
			//获取通知请求request中的参数
			String merchantId = request.getParameter("merchantId");
			String payNo = request.getParameter("payNo");
			String returnCode = request.getParameter("returnCode");
			String message = request.getParameter("message");
			String signType = request.getParameter("signType");
			String type = request.getParameter("type");
			String version = request.getParameter("version");
			String amount = request.getParameter("amount");
			String amtItem = request.getParameter("amtItem");
			String bankAbbr = request.getParameter("bankAbbr");
			String mobile = request.getParameter("mobile");
			String orderId = request.getParameter("orderId");
			String payDate = request.getParameter("payDate");
			String reserved1 = request.getParameter("reserved1");
			String reserved2 = request.getParameter("reserved2");
			String status = request.getParameter("status");
			String orderDate = request.getParameter("orderDate");
			String fee = request.getParameter("fee");
			String hmac = request.getParameter("hmac");
			String accountDate = request.getParameter("accountDate");
			
			//必输字段非空验证
			if (merchantId == null) {
				merchantId = "";
			}
			if (payNo == null) {
				payNo = "";
			}
			if (returnCode == null) {
				returnCode = "";
			}
			if (message == null) {
				message = "";
			}
			if (signType == null ) {
				signType = Constants.SIGNTYPE;
			}
			if (type == null) {
				type = "";
			}
			if (version == null) {
				version = "";
			}
			if (amount == null) {
				amount = "";
			}
			if (amtItem == null) {
				amtItem = "";
			}
			if (bankAbbr == null) {
				bankAbbr = "";
			}
			if (mobile == null) {
				mobile = "";
			}
			if (orderId == null) {
				orderId = "";
			}
			if (payDate == null) {
				payDate = "";
			}
			if (reserved1 == null) {
				reserved1 = "";
			}
			if (reserved2 == null) {
				reserved2 = "";
			}
			if (status == null) {
				status = "";
			}
			if (orderDate == null) {
				orderDate = "";
			}
			if (fee == null) {
				fee = "";
			}
			if (hmac == null) {
				hmac = "";
			}
			if (accountDate == null){
				accountDate = "";
			}


			//组织验签报文
		    String signData = merchantId + payNo + returnCode + message + signType
					+ type + version + amount + amtItem + bankAbbr + mobile
					+ orderId + payDate + accountDate + reserved1 + reserved2 + status
					+ orderDate + fee;
         
		    HiiposmUtil util = new HiiposmUtil();
		    logger.info("移动和包验签报文："+signData+"");
			
		    // String hmac1 = util.MD5Sign(signData, signKey);
		    //logger.info("移动和包消息摘要（PAGE）："+hmac1);
			//logger.info("移动和包传来的摘要："+hmac);
			
			//验签
			boolean sign_flag = util.MD5Verify(signData,hmac,signKey);


			if (sign_flag) {
				//验签成功,商户的业务逻辑处理...
				yDHBHandelOrder(orderId);
				//处理成功，向手机支付平台发送接收到后台通知成功的信息；请执行如下：（注：请不要在out.println其他的信息）
				response.getWriter().print("SUCCESS");	
			} else {
				logger.info("移动和包验签失败！");
				response.getWriter().print("FAIL");
			}
			
		} catch (Exception e) {
			logger.info("移动和包验签交易异常:" + e.getMessage());
		}
		
		
		
		
		return null;
	}
	 /**
     * 移动和包处理订单逻辑
     * @param orderid
     * @param payNo
     * @return
     * @throws Exception
     */
    public  synchronized Integer yDHBHandelOrder(String orderid) throws Exception {
		
    	Integer result = 0;
        
        //根据订单号获取到本地订单
          PmsAppTransInfo appTransInfo = pmsAppTransInfoDao.searchOrderInfo(orderid);

          if(appTransInfo!=null){//判断订单库是否存在此订单记录
              //本地数据库交易状态码  0支付成功 1支付失败2 等待支付  3退款成功 100 系统异常
              if(OrderStatusEnum.paySuccess.getStatus().equals(appTransInfo.getStatus())||OrderStatusEnum.returnMoneySuccess.getStatus().equals(appTransInfo.getStatus())){
                  logger.info(OrderStatusEnum.paySuccess.getDescription());
              	//订单处理完成
              	return 1;
              }else{
            	  
        	  //未完成订单
              //调用接口查询移动和包服务器当前订单状态 并作后续处理
            	
            	String res = queryOrderForYDHB(orderid,appTransInfo.getoAgentNo());
            	  
            	HiiposmUtil util = new HiiposmUtil();
      			String code = util.getValue(res, "returnCode");
      			
      		
  				String status = "";
  		        
  		        //移动和包自己的订单号
  		        appTransInfo.setPortorderid(util.getValue(res, "payNo"));


      			
      			
      			if("000000".equals(code)||"00000".equals(code)){//000000或00000表示查询到结果
      				String ydhbReturnStatus = util.getValue(res, "status");
      				 
      				 //等待付款WFPAYMENT
      				 //支付完成SUCCESS
      				
      				 //以下均认为失败
      				 //订单关闭CLOSED
      				 //订单过期OVERDUE
      				 //交易取消CANCLE
      				 //订单退款REFUND
      				
      				if ("SUCCESS".equals(ydhbReturnStatus)) {
          				//处理成功
          				//修改订单状态
          				status =OrderStatusEnum.paySuccess.getStatus();
          		        appTransInfo.setStatus(status);
                          //订单结束时间
                          appTransInfo.setFinishtime(UtilDate.getDateFormatter());
          		        try {
          		        	int resultInt = pmsAppTransInfoDao.update(appTransInfo);
          		        	
          		            if(resultInt!=1){
          		                logger.info("订单状态修改失败， 订单号：" + orderid + "，结束时间：" + UtilDate.getDateFormatter() + "。要修改的状态置：" + status + OrderStatusEnum.paySuccess.getDescription());
          		            }
          		            
          		        } catch (Exception e) {
          		            logger.info("订单状态修改失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。要修改的状态置："+status+OrderStatusEnum.paySuccess.getDescription(),e);
          		        }
          		        
          		        this.updateMerchantBalance(appTransInfo);//修改余额
                  	    if(pospTransInfoDAO.updetePospTransInfo(appTransInfo.getOrderid())!=1){
                     	 logger.info("流水表订单状态修改失败， 订单号：" + appTransInfo.getOrderid() + "，结束时间：" + UtilDate.getDateFormatter());
                        }
          				result =1;
          			}else if("WFPAYMENT".equals(ydhbReturnStatus)){
          				//处理等待支付
          				//修改订单状态
          				status = OrderStatusEnum.waitingClientPay.getStatus();
          		        appTransInfo.setStatus(status);
          		        
          		        try {
          		        	int resultInt = pmsAppTransInfoDao.update(appTransInfo);
          		        	
          		            if(resultInt!=1){
          		                logger.info("订单状态修改失败， 订单号：" + orderid + "，结束时间：" + UtilDate.getDateFormatter() + "。要修改的状态置：" + status + OrderStatusEnum.waitingClientPay.getDescription());
          		            }
          		            
          		        } catch (Exception e) {
          		            logger.info("订单状态修改失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。要修改的状态置："+status+OrderStatusEnum.waitingClientPay.getDescription(),e);
          		        }
          				
          			}else{
          				//处理失败
          				//修改订单状态
          				status = OrderStatusEnum.payFail.getStatus();
          		        appTransInfo.setStatus(status);
          		        
          		        try {
          		        	int resultInt = pmsAppTransInfoDao.update(appTransInfo);
          		        	
          		            if(resultInt!=1){
          		                logger.info("订单状态修改失败， 订单号：" + orderid + "，结束时间：" + UtilDate.getDateFormatter() + "。要修改的状态置：" + status + OrderStatusEnum.payFail.getDescription());
          		            }
          		            
          		        } catch (Exception e) {
          		            logger.info("订单状态修改失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。要修改的状态置："+status+OrderStatusEnum.payFail.getDescription(),e);
          		        }
          				
          			}
      				
      			}

            }
          }
    	
    	return result;
    	
    }
    /**
     * 讯联处理订单逻辑
     * @param order_no;//订单号
     * @param serialNo;//讯联批次号
     * @param merInfo;//商户编号
     * @param paymenttype;//查询区分微信（025）还是支付宝（015）
     * @param tradeTime;//讯联订单交易时间
     * @param searchNum;//讯联检索参考号
     * @return
     * @throws Exception
     */
    public  synchronized Integer xLHandelOrder(String orderid,String serialNo,String merInfo,String paymenttype,String tradeTime,String searchNum) throws Exception {
    	
    	Integer result = 0;

        //根据订单号获取到本地订单
          PmsAppTransInfo appTransInfo = pmsAppTransInfoDao.searchOrderInfo(orderid);

          if(appTransInfo!=null){//判断订单库是否存在此订单记录
              //本地数据库交易状态码  0支付成功 1支付失败2 等待支付  3退款成功 100 系统异常
              if(OrderStatusEnum.paySuccess.getStatus().equals(appTransInfo.getStatus())||OrderStatusEnum.returnMoneySuccess.getStatus().equals(appTransInfo.getStatus())){
                  logger.info(OrderStatusEnum.paySuccess.getDescription());
              	//订单处理完成
              	return 1;
              }else{
            	  
        	  //未完成订单
              //调用接口查询讯联服务器当前订单状态 并作后续处理
            	
            	String res = queryOrderForXL(orderid, serialNo, merInfo, paymenttype,tradeTime,searchNum,appTransInfo.getoAgentNo());
            	  
            	XunlianGeneral2DecimalResponseDTO  response = (XunlianGeneral2DecimalResponseDTO)parseJsonString(res,XunlianGeneral2DecimalResponseDTO.class);
      		
  				String status = "";
  		        
  		        //讯联自己的订单号
  		        appTransInfo.setPortorderid(response.getSerialNo());

      			if("0000".equals(response.getRetCode())){//0000表示查询到结果
      				
      				String xlReturnStatus = response.getOrderStatus();

      				if ("00".equals(xlReturnStatus)) {
          				//处理成功  00
          				//修改订单状态
          				status =OrderStatusEnum.paySuccess.getStatus();
          		        appTransInfo.setStatus(status);
                          //订单结束时间
                          appTransInfo.setFinishtime(UtilDate.getDateFormatter());
          		        try {
          		        	int resultInt = pmsAppTransInfoDao.update(appTransInfo);
          		        	
          		            if(resultInt!=1){
          		                logger.info("订单状态修改失败， 订单号：" + orderid + "，结束时间：" + UtilDate.getDateFormatter() + "。要修改的状态置：" + status + OrderStatusEnum.paySuccess.getDescription());
          		            }
          		            
          		        } catch (Exception e) {
          		            logger.info("订单状态修改失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。要修改的状态置："+status+OrderStatusEnum.paySuccess.getDescription(),e);
          		        }
          		      
          		        if(!"4".equals(appTransInfo.getPaymentcode())){//支付宝属于一机一码不做余额修改（清算支付宝自己做）
          		        	this.updateMerchantBalance(appTransInfo);//修改余额
          		        }
          		        
                  	    
          		        if(pospTransInfoDAO.updetePospTransInfo(appTransInfo.getOrderid())!=1){
                     	 logger.info("流水表订单状态修改失败， 订单号：" + appTransInfo.getOrderid() + "，结束时间：" + UtilDate.getDateFormatter());
                        }

          				result =1;
          			}else if("09".equals(xlReturnStatus)){
          				//处理等待支付  09
          				//修改订单状态
          				status = OrderStatusEnum.waitingClientPay.getStatus();
          		        appTransInfo.setStatus(status);
          		        
          		        try {
          		        	int resultInt = pmsAppTransInfoDao.update(appTransInfo);
          		        	
          		            if(resultInt!=1){
          		                logger.info("订单状态修改失败， 订单号：" + orderid + "，结束时间：" + UtilDate.getDateFormatter() + "。要修改的状态置：" + status + OrderStatusEnum.waitingClientPay.getDescription());
          		            }

          		        } catch (Exception e) {
          		            logger.info("订单状态修改失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。要修改的状态置："+status+OrderStatusEnum.waitingClientPay.getDescription(),e);
          		        }
          				
          			}else{
          				//处理失败
          				//修改订单状态
          				status = OrderStatusEnum.payFail.getStatus();
          		        appTransInfo.setStatus(status);
          		        
          		        try {
          		        	int resultInt = pmsAppTransInfoDao.update(appTransInfo);
          		        	
          		            if(resultInt!=1){
          		                logger.info("订单状态修改失败， 订单号：" + orderid + "，结束时间：" + UtilDate.getDateFormatter() + "。要修改的状态置：" + status + OrderStatusEnum.payFail.getDescription());
          		            }
          		            
          		        } catch (Exception e) {
          		            logger.info("订单状态修改失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。要修改的状态置："+status+OrderStatusEnum.payFail.getDescription(),e);
          		        }
          			}
      			}

            }
          }
    	
    	return result;
    	
    }

    /**
     * 修改流水表通道商户号和通道终端号
     * @return
     */
    private int updatePosBusAndPosNumTransInfoByOrderId(String orderId,String busInfo,String busPos,String factAmount){
        int result = 0;
        PospTransInfo pospTransInfo = new PospTransInfo();
        pospTransInfo.setOrderId(orderId);
        pospTransInfo.setBusinfo(busInfo);
        pospTransInfo.setBuspos(busPos);
        //获取通道费率信息

        if(StringUtils.isNotBlank(busInfo)){
            String busId = "";
            //获取通道商户信息
            PmsBusinessInfo businessInfo = new PmsBusinessInfo();
            businessInfo.setBusinessNum(busInfo);
            List<PmsBusinessInfo> pmsBusinessInfoList = null;
            try {
                pmsBusinessInfoList =  pmsBusinessInfoDao.searchList(businessInfo);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            if(pmsBusinessInfoList != null && pmsBusinessInfoList.size() > 0){
                //查找当前通道的协定费率，标准费率
                businessInfo = pmsBusinessInfoList.get(0);
                PmsMerchantFee feeChannel = getThirdPartChennelRate(businessInfo.getId().toString());
                BigDecimal channelFee = null;
                BigDecimal channelFeeStandard = null;
                if (feeChannel != null) {
                    logger.info( "订单号：" + orderId + "开始费率配置。");
                    String type = feeChannel.getUpFeeTyp();// 0百分比，1固定值
                    if ("0".equals(type)) {
                        channelFee = feeChannel.getUpFeeRat().multiply(new BigDecimal(factAmount));
                        if (channelFee.compareTo(feeChannel.getUpMaxFeeAmt()) >= 1)
                            channelFee = feeChannel.getUpMaxFeeAmt();
                    } else {
                        channelFee = feeChannel.getUpFixedFeeAmt();
                    }

                    if (feeChannel.getUpFeeRat1() != null) {
                        channelFeeStandard = feeChannel.getUpFeeRat1().multiply(new BigDecimal(factAmount));
                        if (channelFeeStandard.compareTo(feeChannel.getUpMaxFeeAmt1()) >= 1)
                            channelFeeStandard = feeChannel.getUpMaxFeeAmt1();
                    } else
                        channelFeeStandard = BigDecimal.ZERO;

                } else {
                    logger.info("订单号：" +orderId + "无费率配置。");
                    channelFee = new BigDecimal(0);
                    channelFeeStandard = new BigDecimal(0);
                }

                pospTransInfo.setTransfee3(channelFeeStandard.setScale(4,BigDecimal.ROUND_CEILING).doubleValue());
                pospTransInfo.setTransfee4(channelFee.setScale(4,BigDecimal.ROUND_CEILING).doubleValue());

            }
        }

        if(pospTransInfo != null && StringUtils.isNotBlank(pospTransInfo.getOrderId())
                && StringUtils.isNotBlank(pospTransInfo.getBusinfo()) && StringUtils.isNotBlank(pospTransInfo.getBuspos())){
            try {
                result =  pospTransInfoDAO.updateByOrderId(pospTransInfo);
            } catch (Exception e) {
                logger.info("修改流水失败，详情："+e.getMessage());
            }
        }

        return  result;
    }
 
    
    
    
    
    
    
    
    /**
	 * 刷卡收款    第一步  生成订单  O单类型      是T0(清算手续费固定    附加费)
	 * wumeng  20160219
	 * @param param
	 * @param sessionInfo
	 */
	public PayCardResponseDTO insertOrderPayFor0(String param, SessionInfo sessionInfo)throws Exception{

		logger.info("刷卡收款    第一步  生成订单 接收app参数: "+param+"时间："+UtilDate.getDateFormatter());
		PayCardRequestDTO payCardRequestDTO = (PayCardRequestDTO)parseJsonString(param,PayCardRequestDTO.class);
		PayCardResponseDTO payCardResponseDTO =  new PayCardResponseDTO();
		if(!payCardRequestDTO.equals(DATAPARSINGMESSAGE)){//判断解析数据是否成功
			
			String mercid  = sessionInfo.getMercId();//商户编号
			String oAgentNo = sessionInfo.getoAgentNo();//O单编号
			
			BrushCalorieOfConsumptionRequestDTO dto = payCardRequestDTO.getDto();
			String pealName= pmsMerchantInfoDao.queryMercuryStatus(mercid);//查询商户是否实名认证
			
		
			//获取通道的费率
            Map<String,String> paramMap = new HashMap<String, String>();
            paramMap.put("mercid", mercid);
            paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
            paramMap.put("oAgentNo", oAgentNo);
            
            //查询商户刷卡费率 和  最低收款金额    费率    是否是封顶费率标记  封顶金额   
            AppRateTypeAndAmount appRateTypeAndAmount = pmsAppAmountAndRateConfigDao.queryAmountAndRateInfoForShuaka(paramMap);
			
			String status = appRateTypeAndAmount.getStatus();//此业务是否开通
			String statusMessage = appRateTypeAndAmount.getMessage();//此业务是否开通的描述
			
			 if(AUTHENTICATIONFLAG.equals(pealName)){//正式商户
					//MIN_AMOUNT,MAX_AMOUNT ,RATE ,STATUS
				 
				 
				//判断O单业务时候开启（总）
					
					//44444444
					

		            ResultInfo resultInfoForOAgentNo =  publicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.merchantCollect,oAgentNo);
	                //返回不为0，一律按照交易失败处理
	                if(!resultInfoForOAgentNo.getErrCode().equals("0")){
	                	payCardResponseDTO.setRetCode("22");//返回码
	                	if("".equals(resultInfoForOAgentNo.getMsg())||resultInfoForOAgentNo.getMsg()==null){
	                		payCardResponseDTO.setRetMessage("此功能暂时关闭");//返回信息	
						 }else{
							 payCardResponseDTO.setRetMessage(resultInfoForOAgentNo.getMsg());
						 }
	                	return payCardResponseDTO;
	                }
				 
				 
				 if("1".equals(status)){//1表示业务开通
		              	//开通	
						
                         //判断支付方式时候开通总开关
					 	 //33333333
                 		 ResultInfo payCheckResult = payTypeControlDao.checkLimit(oAgentNo,PaymentCodeEnum.shuakaPay.getTypeCode());
                         if(!payCheckResult.getErrCode().equals("0")){
                         	//支付方式时候开通总开关 禁用
                        	 payCardResponseDTO.setRetCode("22");//
                        	 
         					if("".equals(payCheckResult.getMsg())||payCheckResult.getMsg()==null){
                        		payCardResponseDTO.setRetMessage("此支付方式暂时关闭");//返回信息	
        					 }else{
        						 payCardResponseDTO.setRetMessage(payCheckResult.getMsg());
        					 }
       						return payCardResponseDTO; 
         					
                         }
						//判断商户的支付方式时候开通
                         		
                         String payStatus = "";//此支付方式是否开通
                		
                         ResultInfo resultInfoForpay=  publicTradeVerifyService.payTypeVerifyMer(PaymentCodeEnum.shuakaPay,mercid);
     	                //返回不为0，一律按照交易失败处理
     	                if(resultInfoForpay.getErrCode().equals("0")){
     	                	payStatus="0";
     	                
     	                }
                         
                         
                         if("0".equals(payStatus)){//0表示支付方式开通
                        	 
                        	 
                        	//添加刷卡交易时间段判断   wm 2016-03-04   start 
                        	int count = merchantMineDao.queryFestival(UtilDate.getDayDate());
                        	
 							Map<String, String> shuaKaTimeMap= merchantMineDao.queryShuaKaAgent0Time();
 							
 							String shuaKaTime = shuaKaTimeMap.get("VALUE");//提现时间段 格式0900#1830#1200#1830  
 							String[] shuaKaTimeArrary = shuaKaTime.split("#");
 	
 							int shuaKaStartTime;
 							int shuaKaEndTime;
 							
 							if(count>0){//判断节假日  确定节假日刷卡交易时间
 								//节假日
 								shuaKaStartTime = Integer.parseInt(shuaKaTimeArrary[2]);
 								shuaKaEndTime = Integer.parseInt(shuaKaTimeArrary[3]);
 							}else{
 								//不是节假日
 								shuaKaStartTime = Integer.parseInt(shuaKaTimeArrary[0]);
 								shuaKaEndTime = Integer.parseInt(shuaKaTimeArrary[1]);
 							}
 							
 							
 							int nowTime = Integer.parseInt(UtilDate.getTXDateTime());
 							
 							if((nowTime<=shuaKaStartTime)||(nowTime>=shuaKaEndTime)){
                        	 
 								payCardResponseDTO.setRetCode("17");//返回码

 								payCardResponseDTO.setRetMessage("非交易时间,不能交易");//返回信息
 								
 								return payCardResponseDTO;
 							
 							}
 							//添加刷卡交易时间段判断   wm 2016-03-04   end 
                        	 
                        	 
                        	String payAmtStr = dto.getPayAmount();//订单金额
     						BigDecimal payAmt = new BigDecimal(payAmtStr);//订单金额
     						
                        	 
                			//判读  交易金额是不是在欧单区间控制之内
    						 ResultInfo resultInfo =  amountLimitControlDao.checkLimit(oAgentNo,payAmt,TradeTypeEnum.merchantCollect.getTypeCode());
                             //返回不为0，一律按照交易失败处理
                             if(!resultInfo.getErrCode().equals("0")){
                            	payCardResponseDTO.setRetCode("21");//
                            	
                            	
                            	if("".equals(resultInfo.getMsg())||resultInfo.getMsg()==null){
                            		payCardResponseDTO.setRetMessage("交易金额不在申请的范围之内");//返回信息	
            					 }else{
            						 payCardResponseDTO.setRetMessage(resultInfo.getMsg());
            					 }
    						    
           						return payCardResponseDTO; 
                             }
                             
                             
                             
    						BigDecimal min_amount = new  BigDecimal(appRateTypeAndAmount.getMinAmount());//最低收款金额   MIN_AMOUNT
    						BigDecimal max_amount = new  BigDecimal(appRateTypeAndAmount.getMaxAmount());//最高收款金额   MAX_AMOUNT

    						if(min_amount.compareTo(payAmt)!=1){//判断收款金额是否大于最低收款金额   大于等于执行   小于不执行

    							if(payAmt.compareTo(max_amount)!=1){
    								
    								List<PayCmmtufit> cardList = payCmmtufitDao.searchCardInfoByBeforeSix(dto.getCardNo().substring(0, 6)+ "%");
    								if(cardList.size()!=0){
    									
    							
    									//生成订单
    									PmsAppTransInfo  pmsAppTransInfo= new PmsAppTransInfo();
    									pmsAppTransInfo.setPaymenttype("刷卡支付");
    									pmsAppTransInfo.setTradetype("商户收款");
    									pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter());

    									String orderid="";
    									if("1".equals(payCardRequestDTO.getBrushType())){ //刷卡类型：1音频刷卡，2蓝牙刷卡
    										orderid = UtilMethod.getOrderid("106");  //   10业务号    (6 音频7蓝牙) 业务细	      订单号      现根据规则生成订单号
    									}else{
    										orderid = UtilMethod.getOrderid("107");  //   10业务号    (6 音频7蓝牙) 业务细	      订单号      现根据规则生成订单号
    									}
    									pmsAppTransInfo.setoAgentNo(oAgentNo);//O单编号
    									pmsAppTransInfo.setOrderid(orderid);

    									pmsAppTransInfo.setReasonofpayment("商户收款");

    									pmsAppTransInfo.setMercid(mercid); //商户id
    									
    									
    									pmsAppTransInfo.setFactamount(payAmtStr);//实际金额
    									pmsAppTransInfo.setPaymentcode("5");//支付方式 例如： 1 账号（余额）支付、2 百度支付、3 微信支付、4 支付宝支付、5刷卡支付
    									pmsAppTransInfo.setTradetypecode("1");//交易类型 例如： 1 商户收款、2 转账汇款、3 信用卡还款、4手机充值、5 水煤电、 6 加油卡充值、
    									pmsAppTransInfo.setOrderamount(payAmtStr);//订单金额
    									
    								
    									
    									pmsAppTransInfo.setChannelNum(SHUAKA);//通道
    									pmsAppTransInfo.setBusinessNum(SHUAKACOLLECTMONEY);//业务号
    									pmsAppTransInfo.setStatus(Constants.ORDERINITSTATUS);//订单初始化状态
    									
    									
    									
    									
    									String rateStr = appRateTypeAndAmount.getRate();//商户费率    RATE
    									BigDecimal rate = new BigDecimal(rateStr);//查询商户费率
    									String isTop = appRateTypeAndAmount.getIsTop();//商户费率是不是封顶费率
    				 					String topPoundage = appRateTypeAndAmount.getTopPoundage();//商户费率是封顶费率时的最大手续费
    									String bottompoundage = appRateTypeAndAmount.getBottompoundage();//在这里表示 清算是要加上的附加费	
    									
    									if("".equals(bottompoundage)||bottompoundage==null){//如果附加费为空默认为0
    										bottompoundage="0";
    									}
    				 					BigDecimal poundage = new BigDecimal(0);
    				 					pmsAppTransInfo.setSettlepoundage(bottompoundage);//O单T0使用   清算附加费
    				 					
    				 					BigDecimal totalpoundage = new BigDecimal(0); //O单T0使用   总手续费
    				 					
    				 					if("1".equals(isTop)){//判断费率是不是封顶费率      封顶费率有最大的手续费值
    				 						//封顶费率
    				 						poundage = payAmt.multiply(rate);
    				 						BigDecimal maxPoundage = new BigDecimal(topPoundage);//最大手续费
    				 						if(poundage.compareTo(maxPoundage)==1){
    				 							poundage = maxPoundage;
    				 						}
    				 						totalpoundage = poundage.add(new  BigDecimal(bottompoundage));//手续费加上清算的附加费
    				 						pmsAppTransInfo.setRate(rateStr+"-"+topPoundage);//费率    
    				 					}else{
    				 						poundage = payAmt.multiply(rate);//不是封顶费率
    				 						totalpoundage = poundage.add(new  BigDecimal(bottompoundage));//手续费加上清算的附加费
    				 						pmsAppTransInfo.setRate(rateStr);//费率    
    				 						
    				 					}
    				 					pmsAppTransInfo.setTotalpoundage(totalpoundage.toString());//总手续费
    				 					
    				 					//交易金额   按分为最小单位  例如：1元=100分   采用100   商户收款时给商户记账时减去费率(实际金额- 手续费)
    									//采用总手续费算
    				 					pmsAppTransInfo.setPayamount(payAmt.subtract(totalpoundage).toString());
    									pmsAppTransInfo.setPoundage(totalpoundage.toString());//手续费  按分为最小单位  例如：1元=100分   采用100
    									
    									
    									
    									pmsAppTransInfo.setBankno(dto.getCardNo());//刷卡银行卡号
    									//根据银行卡号查询银行名称信息等
    									
    									pmsAppTransInfo.setBankname(cardList.get(0).getBnkName());
    									
    									pmsAppTransInfo.setBrushType(payCardRequestDTO.getBrushType());         // //刷卡类型：1音频刷卡，2蓝牙刷卡

    									pmsAppTransInfo.setSnNO(dto.getSn());   //刷卡器设备号
    									
    									pmsAppTransInfo.setChannelNum(SHUAKA);
    									pmsAppTransInfo.setBusinessNum(SHUAKACOLLECTMONEY);
                                        pmsAppTransInfo.setAuthPath(PIRPREURL+dto.getAuthPath());

                                              
                                        pmsAppTransInfo.setAltLat(payCardRequestDTO.getAltLat());//经纬度（逗号隔开）
                                        pmsAppTransInfo.setGpsAddress(payCardRequestDTO.getGpsAddress());//gps获取的地址信息(中文)
                                        
                                        
                                        
                                        
    									String orderInfo = createJsonString(pmsAppTransInfo);//订单详细信息

    									try {
    										if(pmsAppTransInfoDao.insert(pmsAppTransInfo)!=1){
    											logger.info("订单入库失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。详细信息："+orderInfo);
    											payCardResponseDTO.setRetCode("1");
    											payCardResponseDTO.setOrderNumber(orderid);
    											payCardResponseDTO.setRetMessage("生成订单失败");
    											throw  new Exception();//失败抛出异常回退操作
    										}else{
    											
    											payCardResponseDTO.setRetCode("0");
    											payCardResponseDTO.setOrderNumber(orderid);
    											payCardResponseDTO.setRetMessage("生成订单成功");
    											payCardResponseDTO.setPmsAppTransInfo(pmsAppTransInfo);
    											
    											//第二步操作调用submitOrderPay方法
    											
    											//流水记录由pre系统处理
    											
    											}
    									} catch (Exception e) {
    										logger.info("订单入库失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。详细信息："+orderInfo,e);
    										payCardResponseDTO.setRetCode("100");
    										payCardResponseDTO.setOrderNumber(orderid);
    										payCardResponseDTO.setRetMessage("系统异常");
    									}
    								}else{
    									//没有此卡card bin 
    									payCardResponseDTO.setRetCode("16");//
    									payCardResponseDTO.setRetMessage("暂不支持此卡");
    								}
    						
    							}else{

    								//交易金额大于收款最高金额
    								payCardResponseDTO.setRetCode("3");//
    								payCardResponseDTO.setRetMessage("交易金额大于收款最高金额:"+max_amount.divide(new BigDecimal(100)));
    							}


    						}else{
    							//交易金额小于收款最低金额
    							payCardResponseDTO.setRetCode("4");
    							payCardResponseDTO.setRetMessage("交易金额小于收款最低金额:"+min_amount.divide(new BigDecimal(100)));
    						}
    						
                		 }else{
                			 //支付方式未开通
     	               		payCardResponseDTO.setRetCode("15");//
    	               		if("".equals(resultInfoForpay.getMsg())||resultInfoForpay.getMsg()==null){
    	               			payCardResponseDTO.setRetMessage("请提交相关资料,开通此支付方式");
    							}else{
    								payCardResponseDTO.setRetMessage(resultInfoForpay.getMsg());
    							}
                		 }
                       
					 }else{
						//此功能暂未开通或被禁用
			        	 payCardResponseDTO.setRetCode("14");//
			        	
			        	 if("".equals(statusMessage)||statusMessage==null){
			        		 payCardResponseDTO.setRetMessage("此功能暂未开通");
							}else{
								payCardResponseDTO.setRetMessage(statusMessage);
							}
			        	 
					 }
				 
	         }else{
	        	 
	        	 //交易金额小于收款最低金额
					payCardResponseDTO.setRetCode("7");
					payCardResponseDTO.setRetMessage("不是正式商户");
	         	
	         }
		 }
		
		logger.info("刷卡收款    第一步  生成订单 返回参数: "+createJsonString(payCardResponseDTO)+"时间："+UtilDate.getDateFormatter());

		return payCardResponseDTO;

	
	}
	/**
	 * 刷卡收款    第二步  确认订单并支付   O单类型      是T0(清算手续费固定    附加费)
	 * wumeng  20160219
	 * @param param
	 * @param sessionInfo
	 * @param pmsAppTransInfo
	 */
	public String submitOrderPayFor0(String param, SessionInfo sessionInfo,PmsAppTransInfo  pmsAppTransInfo)throws Exception{

		logger.info("刷卡收款 第二步  确认订单并支付, 时间："+UtilDate.getDateFormatter());
		String  result = "";

		PayCardRequestDTO payCardRequestDTO = (PayCardRequestDTO)parseJsonString(param,PayCardRequestDTO.class);
		PayCardResponseDTO payCardResponseDTO =  new PayCardResponseDTO();
		if(!payCardRequestDTO.equals(DATAPARSINGMESSAGE)){//判断解析数据是否成功
			BrushCalorieOfConsumptionRequestDTO dto = payCardRequestDTO.getDto();
			String  orderid = pmsAppTransInfo.getOrderid();//订单号
			String  rateStr = pmsAppTransInfo.getRate();//费率

			String  sendStr8583 =	"param="+this.createBrushCalorieOfConsumptionDTORequest(sessionInfo, dto, orderid, SHUAKACOLLECTMONEY, rateStr,dto.getSn());
			
			if("param=fail".equals(sendStr8583)){
				//上送参数错误
				payCardResponseDTO.setRetCode("14");
				payCardResponseDTO.setRetMessage("上送参数错误");
				logger.info("上送参数错误， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter());
			}else{
				
				

				logger.info("调用三方前置刷卡接口请求参数：" + sendStr8583 + "，结束时间：" + UtilDate.getDateFormatter());
				//调用三方前置刷卡接口（8583）
				
				ViewKyChannelInfo channelInfo = AppPospContext.context.get(SHUAKA+SHUAKACOLLECTMONEY);
				
				String successFlag = HttpURLConection.httpURLConnectionPOST(channelInfo.getUrl(), sendStr8583);	
				
				logger.info("调用三方前置刷卡接口返回参数：" + successFlag + "，结束时间：" + UtilDate.getDateFormatter());
				
				BrushCalorieOfConsumptionResponseDTO  response = (BrushCalorieOfConsumptionResponseDTO)parseJsonString(successFlag,BrushCalorieOfConsumptionResponseDTO.class);
				
				if("0000".equals(response.getRetCode())){//判断调用接口处理是否成功    0000表示刷卡成功
					if(updateMerchantBalance(pmsAppTransInfo)==1){//余额处理
						
						Map<String, String> paramUpdateOrderStatus = new HashMap<String, String>();
						paramUpdateOrderStatus.put("finishTime", UtilDate.getDateFormatter());
						paramUpdateOrderStatus.put("orderid", orderid);
						//修改订单状态
						if(pmsAppTransInfoDao.updateOrderStatus(paramUpdateOrderStatus)==1){
							
							/*if(pospTransInfoDAO.updetePospTransInfo(orderid)!=1){//刷卡流水pre三方前置修改状态
		                     	 logger.info("流水表订单状态修改失败， 订单号：" + orderid + "，结束时间：" + UtilDate.getDateFormatter());
		                     	payCardResponseDTO.setRetCode("10");
		   						payCardResponseDTO.setRetMessage("流水表订单状态修改失败");
							}else{*/
		                    	 //订单生成成功
		   						payCardResponseDTO.setRetCode("0");
		   						payCardResponseDTO.setRetMessage("支付成功");
		   						
		   						
		   					//调用清算系统代付
								
								try {
									ThreadPool.executor(new AgentT0TaskThread(orderid));
								} catch (Exception e) {
									logger.info("O单T0调用清算代付系统失败，失败订单号"+orderid +"，失败时间："+ UtilDate.getDateFormatter());
								}
		   						
		   						
		   						
		   						
		                       //}
						}else{
							//修改订单状态失败
							payCardResponseDTO.setRetCode("12");
							payCardResponseDTO.setRetMessage("修改订单状态失败");
							logger.info("修改订单状态失败: 订单号"+orderid+"时间："+UtilDate.getDateFormatter());
						}
						
					}else{
						//修改余额失败
						payCardResponseDTO.setRetCode("13");
						payCardResponseDTO.setRetMessage("修改余额失败");
						logger.info("修改余额失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter());
					}
				
				}else{
					
					payCardResponseDTO.setRetCode("1");
					payCardResponseDTO.setRetMessage("错误码："+response.getRetCode()+"\n错误信息："+response.getRetMessage());
					logger.info("订单生成失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter());
				}
			
			}
			
		}
		result = createJsonString(payCardResponseDTO);

		logger.info("刷卡收款 第二步 确认订单并支付 返回app参数: "+result+"时间："+UtilDate.getDateFormatter());


		return result;

	
	}
    
    
    
    
    
    
    
	  /**
	 * 刷卡收款    第一步  生成订单  O单类型      是T0 (清算手续费百分比算)
	 * wumeng  20160511
	 * @param param
	 * @param sessionInfo
	 */
	public PayCardResponseDTO insertOrderPayFor0Settle(String param, SessionInfo sessionInfo)throws Exception{

		logger.info("刷卡收款    第一步  生成订单 接收app参数: "+param+"时间："+UtilDate.getDateFormatter());
		PayCardRequestDTO payCardRequestDTO = (PayCardRequestDTO)parseJsonString(param,PayCardRequestDTO.class);
		PayCardResponseDTO payCardResponseDTO =  new PayCardResponseDTO();
		if(!payCardRequestDTO.equals(DATAPARSINGMESSAGE)){//判断解析数据是否成功
			
			String mercid  = sessionInfo.getMercId();//商户编号
			String oAgentNo = sessionInfo.getoAgentNo();//O单编号
			
			BrushCalorieOfConsumptionRequestDTO dto = payCardRequestDTO.getDto();
			String pealName= pmsMerchantInfoDao.queryMercuryStatus(mercid);//查询商户是否实名认证
			
		
			//获取通道的费率
            Map<String,String> paramMap = new HashMap<String, String>();
            paramMap.put("mercid", mercid);
            paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
            paramMap.put("oAgentNo", oAgentNo);
            
            //查询商户刷卡费率 和  最低收款金额    费率    是否是封顶费率标记  封顶金额   
            AppRateTypeAndAmount appRateTypeAndAmount = pmsAppAmountAndRateConfigDao.queryAmountAndRateInfoForShuaka(paramMap);
			
			String status = appRateTypeAndAmount.getStatus();//此业务是否开通
			String statusMessage = appRateTypeAndAmount.getMessage();//此业务是否开通的描述
			
			 if(AUTHENTICATIONFLAG.equals(pealName)){//正式商户
					//MIN_AMOUNT,MAX_AMOUNT ,RATE ,STATUS
				 
				 
				//判断O单业务时候开启（总）
					
					//44444444
					

		            ResultInfo resultInfoForOAgentNo =  publicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.merchantCollect,oAgentNo);
	                //返回不为0，一律按照交易失败处理
	                if(!resultInfoForOAgentNo.getErrCode().equals("0")){
	                	payCardResponseDTO.setRetCode("22");//返回码
	                	if("".equals(resultInfoForOAgentNo.getMsg())||resultInfoForOAgentNo.getMsg()==null){
	                		payCardResponseDTO.setRetMessage("此功能暂时关闭");//返回信息	
						 }else{
							 payCardResponseDTO.setRetMessage(resultInfoForOAgentNo.getMsg());
						 }
	                	return payCardResponseDTO;
	                }
				 
				 
				 if("1".equals(status)){//1表示业务开通
		              	//开通	
						
                         //判断支付方式时候开通总开关
					 	 //33333333
                 		 ResultInfo payCheckResult = payTypeControlDao.checkLimit(oAgentNo,PaymentCodeEnum.shuakaPay.getTypeCode());
                         if(!payCheckResult.getErrCode().equals("0")){
                         	//支付方式时候开通总开关 禁用
                        	 payCardResponseDTO.setRetCode("22");//
                        	 
         					if("".equals(payCheckResult.getMsg())||payCheckResult.getMsg()==null){
                        		payCardResponseDTO.setRetMessage("此支付方式暂时关闭");//返回信息	
        					 }else{
        						 payCardResponseDTO.setRetMessage(payCheckResult.getMsg());
        					 }
       						return payCardResponseDTO; 
         					
                         }
						//判断商户的支付方式时候开通
                         		
                         String payStatus = "";//此支付方式是否开通
                		
                         ResultInfo resultInfoForpay=  publicTradeVerifyService.payTypeVerifyMer(PaymentCodeEnum.shuakaPay,mercid);
     	                //返回不为0，一律按照交易失败处理
     	                if(resultInfoForpay.getErrCode().equals("0")){
     	                	payStatus="0";
     	                
     	                }
                         
                         
                         if("0".equals(payStatus)){//0表示支付方式开通
                        	 
                        	 
                        	//添加刷卡交易时间段判断   wm 2016-03-04   start 
                        	int count = merchantMineDao.queryFestival(UtilDate.getDayDate());
                        	
 							Map<String, String> shuaKaTimeMap= merchantMineDao.queryShuaKaAgent0Time();
 							
 							String shuaKaTime = shuaKaTimeMap.get("VALUE");//提现时间段 格式0900#1830#1200#1830  
 							String[] shuaKaTimeArrary = shuaKaTime.split("#");
 	
 							int shuaKaStartTime;
 							int shuaKaEndTime;
 							
 							if(count>0){//判断节假日  确定节假日刷卡交易时间
 								//节假日
 								shuaKaStartTime = Integer.parseInt(shuaKaTimeArrary[2]);
 								shuaKaEndTime = Integer.parseInt(shuaKaTimeArrary[3]);
 							}else{
 								//不是节假日
 								shuaKaStartTime = Integer.parseInt(shuaKaTimeArrary[0]);
 								shuaKaEndTime = Integer.parseInt(shuaKaTimeArrary[1]);
 							}
 							
 							
 							int nowTime = Integer.parseInt(UtilDate.getTXDateTime());
 							
 							if((nowTime<=shuaKaStartTime)||(nowTime>=shuaKaEndTime)){
                        	 
 								payCardResponseDTO.setRetCode("17");//返回码

 								payCardResponseDTO.setRetMessage("非交易时间,不能交易");//返回信息
 								
 								return payCardResponseDTO;
 							
 							}
 							//添加刷卡交易时间段判断   wm 2016-03-04   end 
                        	 
                        	 
                        	String payAmtStr = dto.getPayAmount();//订单金额
     						BigDecimal payAmt = new BigDecimal(payAmtStr);//订单金额
     						
                        	 
                			//判读  交易金额是不是在欧单区间控制之内
    						 ResultInfo resultInfo =  amountLimitControlDao.checkLimit(oAgentNo,payAmt,TradeTypeEnum.merchantCollect.getTypeCode());
                             //返回不为0，一律按照交易失败处理
                             if(!resultInfo.getErrCode().equals("0")){
                            	payCardResponseDTO.setRetCode("21");//
                            	
                            	
                            	if("".equals(resultInfo.getMsg())||resultInfo.getMsg()==null){
                            		payCardResponseDTO.setRetMessage("交易金额不在申请的范围之内");//返回信息	
            					 }else{
            						 payCardResponseDTO.setRetMessage(resultInfo.getMsg());
            					 }
    						    
           						return payCardResponseDTO; 
                             }
                             
                             
    						BigDecimal min_amount = new  BigDecimal(appRateTypeAndAmount.getMinAmount());//最低收款金额   MIN_AMOUNT
    						BigDecimal max_amount = new  BigDecimal(appRateTypeAndAmount.getMaxAmount());//最高收款金额   MAX_AMOUNT

    						if(min_amount.compareTo(payAmt)!=1){//判断收款金额是否大于最低收款金额   大于等于执行   小于不执行

    							if(payAmt.compareTo(max_amount)!=1){
    								
    								List<PayCmmtufit> cardList = payCmmtufitDao.searchCardInfoByBeforeSix(dto.getCardNo().substring(0, 6)+ "%");
    								if(cardList.size()!=0){
    									
    							
    									//生成订单
    									PmsAppTransInfo  pmsAppTransInfo= new PmsAppTransInfo();
    									pmsAppTransInfo.setPaymenttype("刷卡支付");
    									pmsAppTransInfo.setTradetype("商户收款");
    									pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter());

    									String orderid="";
    									if("1".equals(payCardRequestDTO.getBrushType())){ //刷卡类型：1音频刷卡，2蓝牙刷卡
    										orderid = UtilMethod.getOrderid("106");  //   10业务号    (6 音频7蓝牙) 业务细	      订单号      现根据规则生成订单号
    									}else{
    										orderid = UtilMethod.getOrderid("107");  //   10业务号    (6 音频7蓝牙) 业务细	      订单号      现根据规则生成订单号
    									}
    									pmsAppTransInfo.setoAgentNo(oAgentNo);//O单编号
    									pmsAppTransInfo.setOrderid(orderid);

    									pmsAppTransInfo.setReasonofpayment("商户收款");

    									pmsAppTransInfo.setMercid(mercid); //商户id
    									
    									
    									pmsAppTransInfo.setFactamount(payAmtStr);//实际金额
    									pmsAppTransInfo.setPaymentcode("5");//支付方式 例如： 1 账号（余额）支付、2 百度支付、3 微信支付、4 支付宝支付、5刷卡支付
    									pmsAppTransInfo.setTradetypecode("1");//交易类型 例如： 1 商户收款、2 转账汇款、3 信用卡还款、4手机充值、5 水煤电、 6 加油卡充值、
    									pmsAppTransInfo.setOrderamount(payAmtStr);//订单金额
    									
    								
    									
    									pmsAppTransInfo.setChannelNum(SHUAKA);//通道
    									pmsAppTransInfo.setBusinessNum(SHUAKACOLLECTMONEY);//业务号
    									pmsAppTransInfo.setStatus(Constants.ORDERINITSTATUS);//订单初始化状态
    									
    									
    									
    									
    									String rateStr = appRateTypeAndAmount.getRate();//商户费率    RATE
    									BigDecimal rate = new BigDecimal(rateStr);//查询商户费率
    									String isTop = appRateTypeAndAmount.getIsTop();//商户费率是不是封顶费率
    				 					String topPoundage = appRateTypeAndAmount.getTopPoundage();//商户费率是封顶费率时的最大手续费
    									
    				 					
    				 					//查询商户当前所属代理，比较刷卡费率和清算费率（小于的不让过）
    				 					
    				 					Map<String,String>  agentShuaKaRateAndSettleRate = pmsMerchantInfoDao.queryAgentShuaKaRateAndSettleRate(mercid);//查询商户当前所属代理，刷卡费率和清算费率
    				 					
    				 					// LOWESTSETTLERATE 清算费率      LOWESTRATE 刷卡费率
    				 					
    				 				
    				 					Map<String,String>  settleRateAndMinSettlePoundage = pmsMerchantInfoDao.queryMersettleRateType(mercid);//查询商户清分费率和最小清分手续费
    				 					
    				 					// arc.RATE  SETTLERATE,arc.BOTTOM_POUNDAGE  MINSETTLEPOUNDAGE
    				 					String settleRateStr = settleRateAndMinSettlePoundage.get("SETTLERATE");//商户清分费率
    				 					if("".equals(settleRateStr)||settleRateStr==null){//如果商户清分费率为空默认为0
    				 						settleRateStr= "0";
    									}
    				 					BigDecimal	settleRate = new BigDecimal(settleRateStr);//商户清分费率
    				 					/**
    				 					 * A0   当前所属代理最低清算费率为空
											A1   当前所属代理最低清算费率大于商户的清算费率
											A2  当前所属代理最低刷卡费率为空
											A3	 当前所属代理最低刷卡费率和商户刷卡费率类型不一样（封顶和不封顶）
											A4 当前所属代理刷卡费率大于商户的刷卡费率
											A5代理的刷卡封顶大于商户的刷卡封顶（不能通过）
    				 					 */
    				 					
    				 					String agentSettleRateStr  = agentShuaKaRateAndSettleRate.get("LOWESTSETTLERATE");//当前所属代理最低清算费率  
    				 					
    				 					if("".equals(agentSettleRateStr)||agentSettleRateStr==null){
    				 						
    				 						payCardResponseDTO.setRetCode("A0");//返回码

    		 								payCardResponseDTO.setRetMessage("交易失败(A0)");//返回信息
    		 								
    		 								return payCardResponseDTO;
    				 						
    				 					}else{
    				 					
    				 						BigDecimal agentSettleRate = new BigDecimal(agentSettleRateStr);//当前所属代理最低清算费率
    				 						if(agentSettleRate.compareTo(settleRate)==1){//   当前所属代理最低清算费率大于商户的清算费率
    				 							payCardResponseDTO.setRetCode("A1");//返回码

        		 								payCardResponseDTO.setRetMessage("交易失败(A1)");//返回信息
        		 								
        		 								return payCardResponseDTO;
    				 						}
    				 						
    				 					}
    				 					
    				 					
    				 					String agentShuaKaRate  = agentShuaKaRateAndSettleRate.get("LOWESTRATE");//当前所属代理最低刷卡费率
    				 					
    				 					
    				 					if("".equals(agentShuaKaRate)||agentShuaKaRate==null){//当前所属代理最低刷卡费率为空
    				 						
    				 						payCardResponseDTO.setRetCode("A2");//返回码

    		 								payCardResponseDTO.setRetMessage("交易失败(A2)");//返回信息
    		 								
    		 								return payCardResponseDTO;
    				 						
    				 					}
    				 					
    				 					
    				 					BigDecimal poundage = new BigDecimal(0);//手续费
    				 					
    				 					
    				 					BigDecimal totalpoundage = new BigDecimal(0); //O单T0使用   总手续费
    				 					
    				 					
    				 					if("1".equals(isTop)){//判断费率是不是封顶费率      封顶费率有最大的手续费值
    				 						//封顶费率
    				 						poundage = payAmt.multiply(rate);
    				 						BigDecimal maxPoundage = new BigDecimal(topPoundage);//最大手续费
    				 						if(poundage.compareTo(maxPoundage)==1){
    				 							poundage = maxPoundage;
    				 						}
    				 						
    				 						pmsAppTransInfo.setRate(rateStr+"-"+topPoundage);//费率    
    				 						
    				 						if(agentShuaKaRate.contains("-")){
    				 							String agentRateStr = agentShuaKaRate.substring(0,agentShuaKaRate.indexOf("-"));
    				 							
    				 							BigDecimal agentRate = new BigDecimal(agentRateStr);//费率
    				 							
    				 							if(agentRate.compareTo(rate)==1){//代理的刷卡费率大于商户的刷卡费率（不能通过）
    				 								
    				 								payCardResponseDTO.setRetCode("A4");//返回码

            		 								payCardResponseDTO.setRetMessage("交易失败(A4)");//返回信息
            		 								
            		 								return payCardResponseDTO;
    				 								
    				 							}else if(agentRate.compareTo(rate)==0){//代理的刷卡费率等于商户的刷卡费率（在判断封顶）
    				 								
    				 								String agentTopPunndageStr = agentShuaKaRate.substring(agentShuaKaRate.indexOf("-")+1);
    				 								
    				 								BigDecimal agentTopPunndage = new BigDecimal(agentTopPunndageStr);//封顶手续费
    				 								
    				 								if(agentTopPunndage.compareTo(maxPoundage)==1){//A5代理的刷卡封顶大于商户的刷卡封顶（不能通过）
    				 									payCardResponseDTO.setRetCode("A5");//返回码

                		 								payCardResponseDTO.setRetMessage("交易失败(A5)");//返回信息
                		 								
                		 								return payCardResponseDTO;
    				 								}
    				 								
    				 							}//代理商费率小的直接通过
    				 							
    				 							
    				 						}else{
    				 							//当前所属代理最低刷卡费率和商户刷卡费率类型不一样（封顶和不封顶）
    				 							payCardResponseDTO.setRetCode("A3");//返回码 

        		 								payCardResponseDTO.setRetMessage("交易失败(A3)");//返回信息
        		 								
        		 								return payCardResponseDTO;
    				 						}
    				 					
    				 					}else{
    				 						poundage = payAmt.multiply(rate);//不是封顶费率
    				 						
    				 						pmsAppTransInfo.setRate(rateStr);//费率    
    				 					
    				 						if(agentShuaKaRate.contains("-")){
    				 							//当前所属代理最低刷卡费率和商户刷卡费率类型不一样（封顶和不封顶）
    				 							payCardResponseDTO.setRetCode("A3");//返回码 

        		 								payCardResponseDTO.setRetMessage("交易失败(A3)");//返回信息
        		 								
        		 								return payCardResponseDTO;
    				 						}else{
    				 							BigDecimal agentRate = new BigDecimal(agentShuaKaRate);//费率
    				 							if(agentRate.compareTo(rate)==1){//代理的刷卡费率大于商户的刷卡费率（不能通过）
    				 								payCardResponseDTO.setRetCode("A4");//返回码

            		 								payCardResponseDTO.setRetMessage("交易失败(A4)");//返回信息
            		 								
            		 								return payCardResponseDTO;
    				 							}
    				 							
    				 						}
    				 						
    				 					}
    				 					
    				 					
    				 					
    				 					
    				 					
    									
    				 					
    				 					
    				 					BigDecimal amount = payAmt.subtract(poundage);//扣掉刷卡手续费的金额
    				 					
    				 					BigDecimal settlePoundage = amount.multiply(settleRate);//按清算费率算出来的清算手续费（还要和最低清算手续费比较）
    				 					
    				 					String minSettlePoundageStr = settleRateAndMinSettlePoundage.get("MINSETTLEPOUNDAGE");//在这里表示 清算最低手续费
    									
    									if("".equals(minSettlePoundageStr)||minSettlePoundageStr==null){//如果清算最低手续费为空默认为0
    										minSettlePoundageStr="0";
    									}
    				 					
    									BigDecimal minSettlePoundage = new BigDecimal(minSettlePoundageStr);//清算最低手续费
    									
    									if(minSettlePoundage.compareTo(settlePoundage)==1){
    										settlePoundage=minSettlePoundage;
    									}
    				 					
    				 					
    				 					pmsAppTransInfo.setSettlepoundage(settlePoundage.toString());//O单T0使用   清算手续费
    				 					
    				 					
    				 					
    				 					//总手续费 = 刷卡手续费+清算手续费   
    				 					totalpoundage = poundage.add(settlePoundage);//刷卡手续费加上清算手续费
    				 					
    				 					
    				 					
    				 					pmsAppTransInfo.setTotalpoundage(totalpoundage.toString());//总手续费
    				 					
    				 					//交易金额   按分为最小单位  例如：1元=100分   采用100   商户收款时给商户记账时减去费率(实际金额- 手续费)
    									//采用总手续费算
    				 					pmsAppTransInfo.setPayamount(payAmt.subtract(totalpoundage).toString());//结算金额
    									pmsAppTransInfo.setPoundage(totalpoundage.toString());//手续费  按分为最小单位  例如：1元=100分   采用100
    									
    									
    									
    									
    									
    									
    									pmsAppTransInfo.setBankno(dto.getCardNo());//刷卡银行卡号
    									//根据银行卡号查询银行名称信息等
    									
    									pmsAppTransInfo.setBankname(cardList.get(0).getBnkName());
    									
    									pmsAppTransInfo.setBrushType(payCardRequestDTO.getBrushType());         // //刷卡类型：1音频刷卡，2蓝牙刷卡

    									pmsAppTransInfo.setSnNO(dto.getSn());   //刷卡器设备号
    									
    									pmsAppTransInfo.setChannelNum(SHUAKA);
    									pmsAppTransInfo.setBusinessNum(SHUAKACOLLECTMONEY);
                                        pmsAppTransInfo.setAuthPath(PIRPREURL+dto.getAuthPath());

                                              
                                        pmsAppTransInfo.setAltLat(payCardRequestDTO.getAltLat());//经纬度（逗号隔开）
                                        pmsAppTransInfo.setGpsAddress(payCardRequestDTO.getGpsAddress());//gps获取的地址信息(中文)
                                        
                                        
                                        
                                        
    									String orderInfo = createJsonString(pmsAppTransInfo);//订单详细信息

    									try {
    										if(pmsAppTransInfoDao.insert(pmsAppTransInfo)!=1){
    											logger.info("订单入库失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。详细信息："+orderInfo);
    											payCardResponseDTO.setRetCode("1");
    											payCardResponseDTO.setOrderNumber(orderid);
    											payCardResponseDTO.setRetMessage("生成订单失败");
    											throw  new Exception();//失败抛出异常回退操作
    										}else{
    											
    											payCardResponseDTO.setRetCode("0");
    											payCardResponseDTO.setOrderNumber(orderid);
    											payCardResponseDTO.setRetMessage("生成订单成功");
    											payCardResponseDTO.setPmsAppTransInfo(pmsAppTransInfo);
    											
    											//第二步操作调用submitOrderPay方法
    											
    											//流水记录由pre系统处理
    											
    											}
    									} catch (Exception e) {
    										logger.info("订单入库失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter()+"。详细信息："+orderInfo,e);
    										payCardResponseDTO.setRetCode("100");
    										payCardResponseDTO.setOrderNumber(orderid);
    										payCardResponseDTO.setRetMessage("系统异常");
    									}
    								}else{
    									//没有此卡card bin 
    									payCardResponseDTO.setRetCode("16");//
    									payCardResponseDTO.setRetMessage("暂不支持此卡");
    								}
    						
    							}else{

    								//交易金额大于收款最高金额
    								payCardResponseDTO.setRetCode("3");//
    								payCardResponseDTO.setRetMessage("交易金额大于收款最高金额:"+max_amount.divide(new BigDecimal(100)));
    							}


    						}else{
    							//交易金额小于收款最低金额
    							payCardResponseDTO.setRetCode("4");
    							payCardResponseDTO.setRetMessage("交易金额小于收款最低金额:"+min_amount.divide(new BigDecimal(100)));
    						}
    						
                		 }else{
                			 //支付方式未开通
     	               		payCardResponseDTO.setRetCode("15");//
    	               		if("".equals(resultInfoForpay.getMsg())||resultInfoForpay.getMsg()==null){
    	               			payCardResponseDTO.setRetMessage("请提交相关资料,开通此支付方式");
    							}else{
    								payCardResponseDTO.setRetMessage(resultInfoForpay.getMsg());
    							}
                		 }
                       
					 }else{
						//此功能暂未开通或被禁用
			        	 payCardResponseDTO.setRetCode("14");//
			        	
			        	 if("".equals(statusMessage)||statusMessage==null){
			        		 payCardResponseDTO.setRetMessage("此功能暂未开通");
							}else{
								payCardResponseDTO.setRetMessage(statusMessage);
							}
			        	 
					 }
				 
	         }else{
	        	 
	        	 //交易金额小于收款最低金额
					payCardResponseDTO.setRetCode("7");
					payCardResponseDTO.setRetMessage("不是正式商户");
	         	
	         }
		 }
		
		logger.info("刷卡收款    第一步  生成订单 返回参数: "+createJsonString(payCardResponseDTO)+"时间："+UtilDate.getDateFormatter());

		return payCardResponseDTO;

	
	}
	
	/**
	 * 刷卡收款    第二步  确认订单并支付   O单类型      是T0 (清算手续费百分比算)
	 * wumeng  20160511
	 * @param param
	 * @param sessionInfo
	 * @param pmsAppTransInfo
	 */
	public String submitOrderPayFor0Settle(String param, SessionInfo sessionInfo,PmsAppTransInfo  pmsAppTransInfo)throws Exception{

		logger.info("刷卡收款 第二步  确认订单并支付, 时间："+UtilDate.getDateFormatter());
		String  result = "";

		PayCardRequestDTO payCardRequestDTO = (PayCardRequestDTO)parseJsonString(param,PayCardRequestDTO.class);
		PayCardResponseDTO payCardResponseDTO =  new PayCardResponseDTO();
		if(!payCardRequestDTO.equals(DATAPARSINGMESSAGE)){//判断解析数据是否成功
			BrushCalorieOfConsumptionRequestDTO dto = payCardRequestDTO.getDto();
			String  orderid = pmsAppTransInfo.getOrderid();//订单号
			String  rateStr = pmsAppTransInfo.getRate();//费率

			String  sendStr8583 =	"param="+this.createBrushCalorieOfConsumptionDTORequest(sessionInfo, dto, orderid, SHUAKACOLLECTMONEY, rateStr,dto.getSn());
			
			if("param=fail".equals(sendStr8583)){
				//上送参数错误
				payCardResponseDTO.setRetCode("14");
				payCardResponseDTO.setRetMessage("上送参数错误");
				logger.info("上送参数错误， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter());
			}else{
				
				

				logger.info("调用三方前置刷卡接口请求参数：" + sendStr8583 + "，结束时间：" + UtilDate.getDateFormatter());
				//调用三方前置刷卡接口（8583）
				
				ViewKyChannelInfo channelInfo = AppPospContext.context.get(SHUAKA+SHUAKACOLLECTMONEY);
				
				String successFlag = HttpURLConection.httpURLConnectionPOST(channelInfo.getUrl(), sendStr8583);	
				
				logger.info("调用三方前置刷卡接口返回参数：" + successFlag + "，结束时间：" + UtilDate.getDateFormatter());
				
				BrushCalorieOfConsumptionResponseDTO  response = (BrushCalorieOfConsumptionResponseDTO)parseJsonString(successFlag,BrushCalorieOfConsumptionResponseDTO.class);
				
				if("0000".equals(response.getRetCode())){//判断调用接口处理是否成功    0000表示刷卡成功
					if(updateMerchantBalance(pmsAppTransInfo)==1){//余额处理
						
						Map<String, String> paramUpdateOrderStatus = new HashMap<String, String>();
						paramUpdateOrderStatus.put("finishTime", UtilDate.getDateFormatter());
						paramUpdateOrderStatus.put("orderid", orderid);
						//修改订单状态
						if(pmsAppTransInfoDao.updateOrderStatus(paramUpdateOrderStatus)==1){
							
							/*if(pospTransInfoDAO.updetePospTransInfo(orderid)!=1){//刷卡流水pre三方前置修改状态
		                     	 logger.info("流水表订单状态修改失败， 订单号：" + orderid + "，结束时间：" + UtilDate.getDateFormatter());
		                     	payCardResponseDTO.setRetCode("10");
		   						payCardResponseDTO.setRetMessage("流水表订单状态修改失败");
							}else{*/
		                    	 //订单生成成功
		   						payCardResponseDTO.setRetCode("0");
		   						payCardResponseDTO.setRetMessage("支付成功");
		   						
		   						
		   					//调用清算系统代付
								
								try {
									ThreadPool.executor(new AgentT0TaskThread(orderid));
								} catch (Exception e) {
									logger.info("O单T0调用清算代付系统失败，失败订单号"+orderid +"，失败时间："+ UtilDate.getDateFormatter());
								}
		   						
		   						
		   						
		   						
		                       //}
						}else{
							//修改订单状态失败
							payCardResponseDTO.setRetCode("12");
							payCardResponseDTO.setRetMessage("修改订单状态失败");
							logger.info("修改订单状态失败: 订单号"+orderid+"时间："+UtilDate.getDateFormatter());
						}
						
					}else{
						//修改余额失败
						payCardResponseDTO.setRetCode("13");
						payCardResponseDTO.setRetMessage("修改余额失败");
						logger.info("修改余额失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter());
					}
				
				}else{
					
					payCardResponseDTO.setRetCode("1");
					payCardResponseDTO.setRetMessage("错误码："+response.getRetCode()+"\n错误信息："+response.getRetMessage());
					logger.info("订单生成失败， 订单号："+orderid +"，结束时间："+ UtilDate.getDateFormatter());
				}
			
			}
			
		}
		result = createJsonString(payCardResponseDTO);

		logger.info("刷卡收款 第二步 确认订单并支付 返回app参数: "+result+"时间："+UtilDate.getDateFormatter());


		return result;

	
	}
    
       
    
    
    
    
    
    
    
    
    
    
    
    
    
}