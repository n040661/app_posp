package xdt.service.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.tree.DefaultElement;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import xdt.common.IdcardUtils;
import xdt.common.RetAppMessage;
import xdt.controller.BaseAction;
import xdt.controller.beencloud.BeenCloudQuickAction;
import xdt.dao.IAppRateConfigDao;
import xdt.dao.IErrorLogDao;
import xdt.dao.IMerchantMineDao;
import xdt.dao.IPayCmmtufitDao;
import xdt.dao.IPmsAppAmountAndRateConfigDao;
import xdt.dao.IPmsBusinessInfoDao;
import xdt.dao.IPmsBusinessPosDao;
import xdt.dao.IPmsMerchantFeeDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPmsMerchantPosDao;
import xdt.dao.IPmsMessageDao;
import xdt.dao.IPmsPosInfoDao;
import xdt.dao.IPmsTransHistoryRecordDao;
import xdt.dao.IPospRouteInfoDAO;
import xdt.dao.IPospTransInfoDAO;
import xdt.dao.IPrimarykeyTableDao;
import xdt.dto.BrushCalorieOfConsumptionRequestDTO;
import xdt.dto.BrushCalorieOfConsumptionTPRequestDTO;
import xdt.dto.payeasy.PayEasyQueryRequestEntity;
import xdt.dto.payeasy.PayEasyQueryResponseEntity;
import xdt.dto.payeasy.PayEasyRequestEntity;
import xdt.model.AppRateConfig;
import xdt.model.AppRateTypeAndAmount;
import xdt.model.ErrorLog;
import xdt.model.OagentMsgCfg;
import xdt.model.OriginalOrderInfo;
import xdt.model.PayCmmtufit;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsMerchantFee;
import xdt.model.PmsMerchantInfo;
import xdt.model.PmsMerchantPos;
import xdt.model.PmsMessage;
import xdt.model.PmsPosInfo;
import xdt.model.PmsTransHistoryRecord;
import xdt.model.PospRouteInfo;
import xdt.model.PospTransInfo;
import xdt.model.PrimarykeyTable;
import xdt.model.SessionInfo;
import xdt.pufa.base.FieldDefine;
import xdt.pufa.security.PuFaSignUtil;
import xdt.quickpay.hengfeng.entity.PayQueryResponseEntity;
import xdt.util.DateUtil;
import xdt.util.PaymentCodeEnum;
import xdt.util.RateTypeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;

@Service("baseService")
public class BaseServiceImpl {
	@Resource
	private IPmsMessageDao messageDao;//消息服务层
    @Resource
    private IPayCmmtufitDao payCmmtufitDao; //银行卡信息层
    @Resource
    private IPmsTransHistoryRecordDao pmsTransHistoryRecordDao; //交易历史记录
    @Resource
    private IPmsAppAmountAndRateConfigDao pmsAppAmountAndRateConfigDao;//商户费率配置
    @Resource
    private IPmsMerchantPosDao pmsPos;//刷卡信息层
    @Resource
    private IAppRateConfigDao appRateConfig;//费率配置
    @Resource
    private IErrorLogDao errorLogDao;//Log日志
    @Resource
    private IPmsPosInfoDao pmsPosInfo;
    @Resource
    private IPospTransInfoDAO pospTransInfoDAO;
    @Resource
    private IPmsMerchantFeeDao pmsMerchantFeeDao;
    @Resource
    private IPrimarykeyTableDao primarykeyTableDao;
    
    @Resource
	protected IPospTransInfoDAO transInfoDao ;
	@Resource
	private IPospRouteInfoDAO pospRouteInfoDao ;
	
	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao;
	
	@Resource
	IPmsBusinessInfoDao pmsBusinessInfoDao;
	
	@Resource
	IPmsBusinessPosDao businessPosDao;
    
  

	protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //时间格式
	protected SimpleDateFormat dtoSdf = new SimpleDateFormat("yyyyMMddHHmmss"); //第三方要求的时间格式
	//商户编号的前7位数
	public static String MERCHANTNUMBERPREFIX = "";
	//初始化消息
	public static String INITIALIZEMESSAGE = "";
	//成功消息
	public static String SUCCESSMESSAGE = "";
	//失败消息
	public static String FAILMESSAGE = "";
	//已存在消息
	public static String EXISTMESSAGE = "";
	//错误消息
	public static String ERRORMESSAGE = "";
	//非空消息
	public static String EMPTYMESSAGE = "";
	//无效消息
	public static String INVALIDMESSAGE = "";
	//数据解析失败
	public static String DATAPARSINGMESSAGE = "";
	//失效信息
	public static String FAILUREMESSAGE = "";
	//短信验证地址
	public static String MESSAGEAUTHENTICATIONURL = "";
    //短信验证模板  注册
    public static String MESSAGEAUTHENTICATIONTEMP="";
    //短信验证模板  修改密码     messageUpdatePassTemp
    public static String MESSAGEUPDATEPASSTEMP="";
    //短信验证模板  找回密码   messageFindPassTemp
    public static String MESSAGEFINDPASSTEMP="";
    //短信验证模板   添加收银员  messageAddUserTemp
    public static String MESSAGEADDUSERTEMP="";
    //短信验证模板    修改收银员 messageUpdateUserTemp
    public static String MESSAGEUPDATEUSERTEMP="";
    //短信验证模板  提现
    public static String MESSAGEDROWMONEY="";
    //短信验证模板  快捷支付预支付
    public static String MESSAGEQUICKPREPAYTEMP="";
    //短信验证模板    其他
    public static String MESSAGEOTHERTEMP="";


    //短信验证调用参数
    public static String MESSAGEAUTHENTICATIONPARAMS="";
	//信用卡还款刷卡支付地址
	public static String PAYCREDITCARDSLOTCARDBUSINESSNUM = "";
	//欧飞手机充值
	public static String PAYPHONEACCOUNTBUSINESSNUM = "";
	//欧飞查询小类
	public static String OFFIQUERYCARDINFO = "";
	//欧飞查询订单状态
	public static String OFFIQUERRYORDER = "";
    //欧飞手机充值产品编码
    public static String OFFIPHONEPAYCARDID ="";
    //欧飞固定key
    public static String OFFIKEYSTR="";
	//手机充值之号段查询
	public static String PHONETHEMROUGHLYTHEQUERYBUSSINESSNUM = "";
	//手机充值之产品查询
	public static String PHONEPRODUCTQUERYBUSINESSNUM = "";

    //手机号是否可充值
    public static String PHONEPAYCHECK = "";
    //刷卡支付  消费
    public static String CREDITTWOCARDPAYMENTCONSUMPTIONBUSINESSNUM = "";
    //转账--刷卡支付
    public static String REMITPAYMENT;
    //手机充值-支付
    public static String CREDITPHONEPAY;
	// 省份查询
	public static String GETPROVINCELIST = "";
	// 城市查询
	public static String GETCITYLIST = "";
	// 充值类型查询
	public static String GETPAYPROJECTLIST = "";
	// 缴费单位查询
	public static String GETPAYUNITLIST = "";
	// 商品信息查询
	public static String QUERYCLASSID = "";
	// 账户欠费查询
	public static String QUERYBALANCE = "";
	// 欧飞水煤电充值
	public static String UTILITYORDER = "";
	// 中石化加油卡卡号信息查询
	public static String QUERYCARDINFO = "";
	// 中石化加油卡充值
	public static String SINOPECORDER = "";
	// 水业务号
	public static String WATERBUSINESSNUM = "";
	// 煤业务号
	public static String GASBUSINESSNUM = "";
	// 电业务号
	public static String ELECTRICITYBUSINESSNUM  = "";
    //商户收款费率
    public static String PERMIUMRATE="";
    //第三方（百度，支付宝，微信）费率
    public static String THIRDPARTRATE="";
    //提现业务费率
    public static String DRAWMONEYRATE="";
    //是否debug模式  0:正常 1：debug
    public static String DEBUGGER="";
  //图片插入的前置链接串
    public static String PIRPREURL="";
    
    //刷卡签到
    public static String CREDITCARDPAYMENTSIGNINBUSINESSNUM="";

    //自身通道
    public static String SELFCHANEL="";
    //欧飞通道
    public static String OFFICHANNEL="";
    //摩宝快捷支付
    public static String MOBAOPAY= "";
    //摩宝渠道编号
    public static String MOBAOCHANNELNUM = "";
    
    //恒丰快捷支付
    public static String HENGFENGPAY= "";
	//恒丰渠道编号
	public static String HENGFENGCHANNELNUM = "";
	//渠道业务编号
    
	//bee cloud 渠道信息
	public static String BC_CHANNEL;//渠道号
	public static String BC_CHANNEL_TYPE;//渠道业务
	public static String BC_PAY_CALLBAK;//支付回调
	//恒丰 微信 渠道信息
	public static String HF_WX_REGISTERURL;//注册商户
	public static String HF_WX_DownLoadKeyURL;//下载私钥
	public static String HF_WX_verifyInfoURL;//校验商户信息
	public static String HF_WX_WeixinPayURL;//生成二维码
	public static String HF_WX_ChangeRate;//修改费率
	public static String HF_WX_OrderConfirmURLe;//查询
	public static String HF_COMMON_URL;//通用接口
	public static String HF_USER_KEY;//userid key
	
    //--------------------------wumeng start--------------------
    //实名认证标记
    public static String AUTHENTICATIONFLAG = "";
    
    
    //百度
    public static String BAIDU = "";
    //百度秘钥可商户名
    public static String BAIDUKEY = "";
    //百度生成二维码
    public static String BAIDUTWODIMENSION ="";
    //百度扫码   扫一扫
    public static String BAIDUSCANCODE = "";
    //百度订单查询
    public static String BAIDUQUERY  = "";
    //百度sdk回调
    public static String  BAIDUCALLBACKURL="";
    

    //讯联
    public static String XUNLIAN ="";
   //讯联商户号和key
    public static String XUNLIANKEY = "";
   //讯联微信二维码
    public static String XUNLIANWECHATTWODIMENSION ="";
   //讯联微信扫码   扫一扫
    public static String XUNLIANWECHATSCANCODE = "";
   //讯联支付宝二维码
    public static String XUNLIANALIPAYTWODIMENSION = "";
   //讯联支付宝扫码   扫一扫
    public static String XUNLIANALIPAYSCANCODE = "";
    //讯联订单查询
    public static String  XUNLIANQUERY ="";
    
   
    //刷卡
    public static String SHUAKA ="";
    //商户收款  刷卡
    public static String SHUAKACOLLECTMONEY = "";


    //提现
    public static String  TXIAN ="";
   //商户提现
    public static String TIXIANDRAWMONEY = "";
    
    
    
    //移动和包
    public static String  CMPAY ="";
    //移动和包key
    public static String   CMPAYKEY ="";
    //移动和包   扫码
    public static String CMPAYSCANCODE = "";
    //移动和包  查询
    public static String CMPAYQUERY  = "";
    
    
    //中磁O单编号
    public static String ZHONGCIOAGENTNO  = "";
    public static String POSPBILLSEARCHURL = "";
    public static String GOODSNAME="";
    //--------------------------wumeng end--------------------

    public static List<OagentMsgCfg> oagentMsgCfgList;
	
	private static Gson gson = new Gson();
	private static Logger logger=Logger.getLogger(BaseServiceImpl.class);
	/**  
	 * 读取公共配置文件
	 * @throws java.io.IOException
	 */
	public static void readCommonPropertiesFile() throws IOException{
		InputStream in = BaseAction.class.getResourceAsStream("/common.properties");
        BufferedReader bf = new BufferedReader(new InputStreamReader(in,"utf-8"));
        Properties p = new Properties();
		p.load(bf);
		
		MERCHANTNUMBERPREFIX = p.getProperty("merchantNumberPrefix");
		INITIALIZEMESSAGE = p.getProperty("initializeMessage");
		SUCCESSMESSAGE = p.getProperty("successMessage");
		FAILMESSAGE = p.getProperty("failMessage");
		EXISTMESSAGE = p.getProperty("existMessage");
		ERRORMESSAGE = p.getProperty("errorMessage");
		EMPTYMESSAGE = p.getProperty("emptyMessage");
		INVALIDMESSAGE = p.getProperty("invalidMessage");
		MESSAGEAUTHENTICATIONURL = p.getProperty("messageAuthenticationUrl");
        MESSAGEAUTHENTICATIONTEMP=p.getProperty("messageAuthenticationTemp");
        MESSAGEAUTHENTICATIONPARAMS=p.getProperty("messageAuthenticationParams");
		DATAPARSINGMESSAGE = p.getProperty("dataParsingMessage");
		FAILUREMESSAGE = p.getProperty("failureMessage");
		PAYCREDITCARDSLOTCARDBUSINESSNUM = p.getProperty("payCreditCardSlotCardBusinessnum");
		PAYPHONEACCOUNTBUSINESSNUM = p.getProperty("payPhoneAccountBusinessnum");
		PHONETHEMROUGHLYTHEQUERYBUSSINESSNUM = p.getProperty("phoneThemRoughlyTheQueryBusinessnum");
		PHONEPRODUCTQUERYBUSINESSNUM = p.getProperty("phoneProductQueryBusinessnum");
        PHONEPAYCHECK = p.getProperty("phonePayCheck");
        OFFIQUERYCARDINFO = p.getProperty("offiquerycardinfo");
        OFFIQUERRYORDER = p.getProperty("offiQuerryOrder");
		GETPROVINCELIST = p.getProperty("getProvinceList");
		GETCITYLIST = p.getProperty("getCityList");
		GETPAYPROJECTLIST = p.getProperty("getPayProjectList");
		GETPAYUNITLIST = p.getProperty("getPayUnitList");
		QUERYCLASSID = p.getProperty("queryClassId");
		QUERYBALANCE = p.getProperty("queryBalance");
		UTILITYORDER = p.getProperty("utilityOrder");
		QUERYCARDINFO = p.getProperty("queryCardInfo");
		SINOPECORDER = p.getProperty("sinopecOrder");
        PERMIUMRATE = p.getProperty("permiumRate");
        THIRDPARTRATE =  p.getProperty("thirdpartRate");
        DRAWMONEYRATE = p.getProperty("drawMoneyRate");
        WATERBUSINESSNUM =  p.getProperty("waterBusinessNum");
        GASBUSINESSNUM =  p.getProperty("gasBusinessNum");
        ELECTRICITYBUSINESSNUM =  p.getProperty("electricityBusinessNum");
        CREDITTWOCARDPAYMENTCONSUMPTIONBUSINESSNUM = p.getProperty("CreditTwocardPaymentConsumptionBusinessNum");
        REMITPAYMENT= p.getProperty("remitPayment");
        OFFIPHONEPAYCARDID= p.getProperty("offiPhonePayCardId");
        OFFIKEYSTR=p.getProperty("offiKeyStr");
        CREDITCARDPAYMENTSIGNINBUSINESSNUM = p.getProperty("creditCardPaymentSignInBusinessnum");
        CREDITPHONEPAY= p.getProperty("creditPhonePay");
        //自身通道
        SELFCHANEL=p.getProperty("selfChanel");
        OFFICHANNEL=p.getProperty("offiChannel");
        
        MESSAGEUPDATEPASSTEMP = p.getProperty("messageUpdatePassTemp");
        MESSAGEFINDPASSTEMP = p.getProperty("messageFindPassTemp");
        MESSAGEADDUSERTEMP = p.getProperty("messageAddUserTemp");
        MESSAGEUPDATEUSERTEMP = p.getProperty("messageUpdateUserTemp");
        MESSAGEOTHERTEMP = p.getProperty("messageOtherTemp");
        MESSAGEDROWMONEY = p.getProperty("messageDrowMoney");
        MESSAGEQUICKPREPAYTEMP = p.getProperty("messageQuickPrePayTemp");

        //实名认证标记
        AUTHENTICATIONFLAG = p.getProperty("authenticationFlag");
        
        //百度
        BAIDU = p.getProperty("baidu");
        //百度秘钥可商户名
        BAIDUKEY = p.getProperty("baiduKey");
        //百度生成二维码
        BAIDUTWODIMENSION= p.getProperty("baiduTwoDimension");
        //百度扫码   扫一扫
        BAIDUSCANCODE=  p.getProperty("baiduScanCode");
        //百度订单查询
        BAIDUQUERY=  p.getProperty("baiduQuery");
        //百度sdk回调
        BAIDUCALLBACKURL=p.getProperty("baiduCallBackUrl");  

        //讯联
        XUNLIAN = p.getProperty("xunlian");
        //讯联商户号和key
        XUNLIANKEY=  p.getProperty("xunliankey");
        //讯联微信二维码
        XUNLIANWECHATTWODIMENSION= p.getProperty("xunlianWechatTwoDimension");
        //讯联微信扫码   扫一扫
        XUNLIANWECHATSCANCODE=  p.getProperty("xunlianWechatScanCode");
        //讯联支付宝二维码
        XUNLIANALIPAYTWODIMENSION=  p.getProperty("xunlianAlipayTwoDimension");
        //讯联支付宝扫码   扫一扫
        XUNLIANALIPAYSCANCODE=  p.getProperty("xunlianAlipayScanCode");
        //讯联订单查询
        XUNLIANQUERY =  p.getProperty("xunlianQuery");
        
        

        //刷卡
       	SHUAKA = p.getProperty("shuaka");
        //商户收款  刷卡
        SHUAKACOLLECTMONEY=  p.getProperty("shuakaCollectMoney");

        //提现
       	TXIAN = p.getProperty("tixian");
        //商户提现
        TIXIANDRAWMONEY=  p.getProperty("tixiandrawMoney");
        
        
        
        //移动和包
        CMPAY =p.getProperty("cmpay");
        //移动和包key
        CMPAYKEY =p.getProperty("cmpaykey");
        //移动和包   扫码
        CMPAYSCANCODE = p.getProperty("cmpayscancode");
        //移动和包  查询
        CMPAYQUERY  = p.getProperty("cmpayquery");
        DEBUGGER = p.getProperty("debugger");
      //图片插入的前置链接串
        PIRPREURL= p.getProperty("picPreUrl");
        
        //中磁O单编号
        ZHONGCIOAGENTNO  = p.getProperty("zhongcioagentno");
        POSPBILLSEARCHURL = p.getProperty("pospBillSearchUrl");
        //摩宝快捷支付
        MOBAOPAY = p.getProperty("moBaoPay");
        //摩宝渠道编号
        MOBAOCHANNELNUM = p.getProperty("moBaoChannelNum");
        //商城商品信息
        GOODSNAME = p.getProperty("goodsName");
        
        //恒丰渠道
        HENGFENGPAY= p.getProperty("hfPay");
    	//恒丰渠道编号
    	HENGFENGCHANNELNUM =p.getProperty("hfChannelNum");
    	
//    	beecloud  渠道 通道
    	BC_CHANNEL=p.getProperty("BC_CHANNEL");
    	BC_CHANNEL_TYPE=p.getProperty("BC_CHANNEL_TYPE");
    	BC_PAY_CALLBAK=p.getProperty("BC_PAY_CALLBAK");
    	//BC_PAY_CALLBAK="http://www,baidu.com";
    	logger.info("读取配置文件信息："+BC_PAY_CALLBAK);
//    	恒丰  渠道 通道
    	HF_WX_REGISTERURL=p.getProperty("HF_WX_REGISTERURL");
    	HF_WX_DownLoadKeyURL=p.getProperty("HF_WX_DownLoadKeyURL");
    	HF_WX_verifyInfoURL=p.getProperty("HF_WX_verifyInfoURL");
    	HF_WX_WeixinPayURL=p.getProperty("HF_WX_WeixinPayURL");
    	HF_WX_ChangeRate=p.getProperty("HF_WX_ChangeRate");
    	HF_WX_OrderConfirmURLe=p.getProperty("HF_WX_OrderConfirmURLe");
    	HF_COMMON_URL=p.getProperty("HF_COMMON_URL");
    	HF_USER_KEY=p.getProperty("HF_USER_KEY");
        

        //读取xml文件
       // parsXml();
        in.close();
        bf.close();
	}
	/**
	 * 非空验证
	 * @param value
	 * @return
	 */
	public boolean isNotEmptyValidate(String value){
		if(null==value || "".equals(value)){
			return false;
		}else{
			return true;
		}
	}

	/**
	 * 将传入的对象封装成JSON字符串,并进行加密
	 * @param obj
	 * @return
	 */
	public String createJsonString(Object obj)throws Exception{
		return gson.toJson(obj);
	}

	/**
	 * 将传入的JSON字符串解析成指定的对象
	 * @param jsonString
	 * @param t
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object parseJsonString(String jsonString,Class t) throws Exception{
		return gson.fromJson(jsonString, t);
	}


	/**
	 * 将传入的对象封装成JSON字符串
	 * @param obj
	 * @return
	 */
	public String createJson(Object obj)throws Exception{
		return gson.toJson(obj);
	}

	/**
	 * 生成6位随机数
	 * @return
	 */
	public int generateRandomNumber(){
		int number = 0;
		Random r = new Random();
		number=r.nextInt(899999)+100000;
		return number;
	}

	/**
	 * 生成8位随机数
	 * @return
	 */
	public int createRandomNumber(){
		Random random = new Random();
		int number = random.nextInt(899999999)+100000000;
		return number;
	}

	/**
	 * 获取交易号
	 * @return
	 */
	public String getOrderNumber(){
		String nano = System.nanoTime()+"";
		return dtoSdf.format(new Date()).substring(2)+nano.substring(nano.length()-8);
	}

	/**
	 * 获得当前时间所在月份的上个月的最后一天所在日期
	 * @return
	 * @throws java.text.ParseException
	 */
	public String getLastMonthDay() throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int year = 0;
		int month = cal.get(Calendar.MONTH); // 上个月月份
		//设置年月
		if (month == 0) {
			year = cal.get(Calendar.YEAR) - 1;
			month = 12;
		} else {
			year = cal.get(Calendar.YEAR);
		}
		//设置天数
		String temp=year + "-" + month ;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		Date d = null;
		d = format.parse(temp);
		cal.setTime(d);
		int day =cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		String endDay = year + "-" + month + "-" + day;
		return endDay;
	}

	/**
	 * 检测邮箱
	 * @param email
	 * @return
	 */
	public boolean checkEmail(String email) {
		Pattern p = Pattern.compile("^\\s*([A-Za-z0-9_-]+(\\.\\w+)*@(\\w+\\.)+\\w{2,5})\\s*$");
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * 验证输入的信用卡号是否合法
	 * @param number
	 * @return
	 */
	public boolean validateCreditCard(String number){
		int sumOdd = 0;
		int sumEven = 0;
		int length = number.length();
		int[] wei = new int[length];
		for (int i = 0; i < number.length(); i++) {
			//从最末一位开始提取，每一位上的数值
			wei[i] = Integer.parseInt(number.substring(length - i - 1, length  - i));
		}
		for (int i = 0; i < length / 2; i++) {
			sumOdd += wei[2 * i];
			if ((wei[2 * i + 1] * 2) > 9)
				wei[2 * i + 1] = wei[2 * i + 1] * 2 - 9;
			else
				wei[2 * i + 1] *= 2;
			sumEven += wei[2 * i + 1];
		}
		if ((sumOdd + sumEven) % 10 == 0)
			return true;
		else
			return false;
	}

	/**
	 * 校验银行卡卡号
	 * @param cardId
	 * @return
	 */
	public boolean checkBankCard(String cardId) {
		char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
		if (bit == 'N') {
			return false;
		}
		return cardId.charAt(cardId.length() - 1) == bit;
	}

	/**
	 * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
	 * @param nonCheckCodeCardId
	 * @return
	 */
	public char getBankCardCheckCode(String nonCheckCodeCardId) {
		if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0 || !nonCheckCodeCardId.matches("\\d+")) {
			// 如果传的不是数据返回N
			return 'N';
		}
		char[] chs = nonCheckCodeCardId.trim().toCharArray();
		int luhmSum = 0;
		for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
			int k = chs[i] - '0';
			if (j % 2 == 0) {
				k *= 2;
				k = k / 10 + k % 10;
			}
			luhmSum += k;
		}
		return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
	}




        public static boolean luhnTest(String number){
            int s1 = 0, s2 = 0;
            String reverse = new StringBuffer(number).reverse().toString();
            for(int i = 0 ;i < reverse.length();i++){
                int digit = Character.digit(reverse.charAt(i), 10);
                if(i % 2 == 0){//this is for odd digits, they are 1-indexed in the algorithm
                    s1 += digit;
                }else{//add 2 * digit for 0-4, add 2 * digit - 9 for 5-9
                    s2 += 2 * digit;
                    if(digit >= 5){
                        s2 -= 9;
                    }
                }
            }
            return (s1 + s2) % 10 == 0;
        }


//该代码片段来自于: http://www.sharejs.com/codes/java/7117



	/**
	 * 检测手机号的合法性
	 * @param mobilePhone
	 * @return
	 */
	public boolean checkPhone(String mobilePhone) {
		Pattern p = Pattern.compile("^((14[5,7])|(13[0-9])|(17[0-9])|(15[^4,\\D])|(18[0,1-9]))\\d{8}$");
		Matcher m = p.matcher(mobilePhone);
		return m.matches();
	}

	/**
	 * 判断商户输入的验证码与服务器接收的验证码是否一致
	 */
	public String verificationCode(String mobilePhone,String captcha,Integer msgType,String oAgentNo) throws Exception{

        //判断欧单编号，如果欧单编号为空，则直接返回失败
        if(StringUtils.isBlank(oAgentNo)){
            return FAILUREMESSAGE;
        }
        PmsMessage pmsMessage = new PmsMessage();
		pmsMessage.setPhoneNumber(mobilePhone);
		pmsMessage.setResponse("0");//服务器响应短信通道接口成功
		pmsMessage.setFailure(new BigDecimal(1));//验证码是有效的
        pmsMessage.setMsgType(msgType);
        //设置欧单编号
        pmsMessage.setoAgentNo(oAgentNo);
		List<PmsMessage> list = messageDao.searchList(pmsMessage);
		if(null!=list && list.size()>=1){
			PmsMessage message = list.get(0);
			//判断验证码有没有过期了
			//1.获取当期时间的毫秒数
			long currentTime = new Date().getTime();
			//2.获取数据库请求的毫秒数
			long requestTime = sdf.parse(message.getReqtime()).getTime();
			//3.判断间隔时间是否超过2分钟
			long jiange = currentTime-requestTime;
			if(jiange>120000){
				//4.说明验证码失效 更改其状态
				pmsMessage = new PmsMessage();
				pmsMessage.setId(message.getId());
				pmsMessage.setFailure(new BigDecimal(0));
				logger.info(mobilePhone+","+captcha+",发送验证码请求时间："+message.getReqtime()+",间隔时间："+jiange+",验证码过期");
				insertAppLogs(mobilePhone,"","2106");
				messageDao.update(pmsMessage);
				return FAILUREMESSAGE;
			}else{
				//验证码可以正常使用 判断输入的是否正确
				String context = message.getContext();
				if(context.equalsIgnoreCase(captcha)){
					//将短信验证码的状态改成失效
					pmsMessage = new PmsMessage();
					pmsMessage.setId(message.getId());
					pmsMessage.setFailure(new BigDecimal(0));
					if(messageDao.update(pmsMessage)==1){
						return SUCCESSMESSAGE;
					}else{
						logger.info(mobilePhone+","+captcha+",验证码状态更新失败");
						insertAppLogs(mobilePhone,"","2109");
						return FAILUREMESSAGE;
					}
				}else{
					logger.info(mobilePhone+","+captcha+",发送给商户的验证码："+context+",验证码输入错误");
					insertAppLogs(mobilePhone,"","2108");
					return ERRORMESSAGE;
				}
			}
		}else{
			logger.info(mobilePhone+","+captcha+",查不到对应发送验证码信息");
			insertAppLogs(mobilePhone,"","2107");
			return FAILUREMESSAGE;
		}
	}

	/**
	 * 接口请求连接
	 * @param connection
	 * @param jsonString
	 * @return
	 * @throws java.io.IOException
	 */
	public String dtoRequestConnection(HttpURLConnection connection,String jsonString) throws IOException{
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");
		connection.setUseCaches(false);
		connection.setInstanceFollowRedirects(true);
		connection.setConnectTimeout(120000);//2分钟
		connection.setReadTimeout(120000);
		connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		connection.connect();
		DataOutputStream out = new DataOutputStream(connection.getOutputStream());
		String content = "param="+URLEncoder.encode(jsonString, "utf-8");//传入加密后的jsonString
		out.writeBytes(content);
		out.flush();
		out.close();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuffer buffer = new StringBuffer();
		String line;
		while ((line = reader.readLine()) != null) {buffer.append(line);}
		reader.close();
		return buffer.toString();
	}
	/**
	 * 刷卡接口请求连接
	 * @param connection
	 * @param jsonString
	 * @return
	 * @throws java.io.IOException
	 */
	public String dtoRequestConnectionDto(HttpURLConnection connection,String jsonString,String jsoonString2) throws IOException{
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");
		connection.setUseCaches(false);
		connection.setInstanceFollowRedirects(true);
		connection.setConnectTimeout(120000);//2分钟
		connection.setReadTimeout(120000);
		connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		connection.connect();
		DataOutputStream out = new DataOutputStream(connection.getOutputStream());
		String content = "param="+URLEncoder.encode(jsoonString2, "utf-8");//传入加密后的jsonString
		out.writeBytes(content);
		out.flush();
		out.close();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuffer buffer = new StringBuffer();
		String line;
		while ((line = reader.readLine()) != null) {buffer.append(line);}
		reader.close();
		return buffer.toString();
	}
	/**
	 * 请求接口关闭
	 * @param connection
	 * @param reader
	 * @throws java.io.IOException
	 */
	public void dtoRequestClose(HttpURLConnection connection,BufferedReader reader) throws IOException{
		reader.close();
		connection.disconnect();
	}

	/**
	 * 解析接口返回的xml数据 情形1
	 * @param retString
	 * @param nodesName
	 * @return
	 * @throws org.dom4j.DocumentException
	 */
	@SuppressWarnings("unchecked")
	public List<HashMap> parseDTORetXml(String retString,String nodesName) throws DocumentException{
		List<DefaultElement> retList = DocumentHelper.parseText(retString).selectNodes(nodesName);
		List<HashMap> resultList = new ArrayList<HashMap>();
		if (!CollectionUtils.isEmpty(retList)){
			HashMap paramResult = null;
			for (DefaultElement item: retList){
				List<DefaultElement> eachList = item.content();
				paramResult = new HashMap();
				for (DefaultElement item2: eachList){
					paramResult.put(item2.attributeValue("name"), item2.attributeValue("value"));
				}
				resultList.add(paramResult);
			}
		}
		return resultList;
	}

	/**
	 * 解析接口返回的xml数据 情形2
	 * @param retString
	 * @param nodesName
	 * @return
	 * @throws org.dom4j.DocumentException
	 */
	@SuppressWarnings("unchecked")
	public List<HashMap> otherParseDTORetXml(String retString,String nodesName) throws DocumentException{
		List<DefaultElement> retList = DocumentHelper.parseText(retString).selectNodes(nodesName);
		List<HashMap> resultList = new ArrayList<HashMap>();
		if (!CollectionUtils.isEmpty(retList)){
			HashMap paramResult = null;
			for (DefaultElement item: retList){
				List<DefaultElement> eachList = item.content();
				paramResult = new HashMap();
				for (DefaultElement item2: eachList){
					paramResult.put(item2.getName(), item2.getText());
				}
				resultList.add(paramResult);
			}
		}
		return resultList;
	}

	/**
	 * 将jsonString转成Map
	 * @param jsonStr
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> parseJSON2Map(String jsonStr){
		Map<String, Object> map = new HashMap<String, Object>();
		//最外层解析
		JSONObject json = JSONObject.fromObject(jsonStr);
		for(Object k : json.keySet()){
			Object v = json.get(k);
			//如果内层还是数组的话，继续解析
			if(v instanceof JSONArray){
				List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
				Iterator<JSONObject> it = ((JSONArray)v).iterator();
				while(it.hasNext()){
					JSONObject json2 = it.next();
					list.add(parseJSON2Map(json2.toString()));
				}
				map.put(k.toString(), list);
			} else {
				map.put(k.toString(), String.valueOf(v));
			}
		}
		return map;
	}

	/**
	 * 验证session非空和数据解析是否异常
	 * @param param
	 * @param t
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String,Object> validateNullAndParseData(HttpSession session,String param,Class t) throws Exception{
		HashMap<String,Object> map = new HashMap<String,Object>();
		String message = "";
		SessionInfo sessionInfo = (SessionInfo)session.getAttribute(SessionInfo.SESSIONINFO);
		if(null!=sessionInfo){
			Object obj = parseJsonString(param,t);
			if(!obj.equals(DATAPARSINGMESSAGE)){
				map.put("obj", obj);//要解析的对象
				map.put("sessionInfo",sessionInfo); //商户的基本信息
				message = RetAppMessage.DATAANALYTICALSUCCESS;
			}else{
				insertAppLogs("","","2001");
				message = RetAppMessage.DATAANALYTICALFAILURE;
			}
		}else{
			message = RetAppMessage.SESSIONINVALIDATION;
		}
		map.put("message", message);
		return map;
	}


	/**
	 * 根据传入的金额 计算手续费与本金
	 * @param bd_money
	 * @param mRate
	 * @param grade
	 * @return
	 * mRate--费率
	 * money -- 金额
	 * grade -- 费率 类型
	 *   -------------- 减法  (countFee)
	 *   ---------------------------加法  (realMoney)
	 */
	public String countFee(BigDecimal bd_money,String mRate,String grade) {
		String[] rate = mRate.split("-");
		BigDecimal bd_rate = new BigDecimal(rate[0]);
		BigDecimal appFee = new BigDecimal(0);
		// 手续费fee
		BigDecimal fee=bd_rate.multiply(bd_money).setScale(2,BigDecimal.ROUND_UP);
		if ("1".equals(grade)) {
			appFee = new BigDecimal(rate[1]);
			if(fee.compareTo(appFee)==1){fee = appFee;}
		}
		// 到账金额
		double realD = bd_money.subtract(fee).doubleValue();
		return bd_money.toString()+"-"+fee.toString()+"-"+String.valueOf(realD);//返回值是应支付金额（40）+手续费（3）+实际实付（37）
	}
	//加法
	public String realMoney(BigDecimal bd_money,String mRate,String grade) {
		String[] rate = mRate.split("-");
		BigDecimal bd_rate = new BigDecimal(rate[0]);
		BigDecimal one = new BigDecimal(1);
		BigDecimal appFee = new BigDecimal(0);
		//手续费fee
		BigDecimal fee=bd_money.multiply(bd_rate).divide(one.subtract(bd_rate),2,BigDecimal.ROUND_UP);
		if ("1".equals(grade)) {
			appFee = new BigDecimal(rate[1]);
			if(fee.compareTo(appFee)==1){fee = appFee;}
		}
		//应付总额
		double realD = bd_money.add(fee).doubleValue();
		return String.valueOf(realD)+"-"+fee.toString()+"-"+bd_money.toString(); //返回值是应支付金额（43）+手续费（3）+用户到账金额（40）
	}



	/**
	 * 配置session默认值
	 * @throws java.net.UnknownHostException
	 */
	public void sessionDefaultConfig() throws UnknownHostException{
		InetAddress addr = InetAddress.getLocalHost();
		MDC.put("ip", addr.getHostAddress());
		MDC.put("session","session");
		MDC.put("mobilePhone", "mobilePhone");
	}

	/**
	 * 向日志文件里插入新增或更新信息
	 * @param sessionId session对象
	 * @param mobilePhone 是否登录
	 * @param ip ip地址
	 * @throws java.net.UnknownHostException
	 */
	public void setSession(String ip,String sessionId,String mobilePhone){
		MDC.put("ip", ip);
		MDC.put("session",sessionId.substring(0,10));
		MDC.put("mobilePhone", mobilePhone);
	}
	
	/**
	 * 用于进入方法时记录日志
	 * @param ip
	 */
	public void setMethodSession(String ip){
		MDC.put("ip", ip);
		MDC.put("session","session");
		MDC.put("mobilePhone","mobilePhone");
	}

	/**
	 * 错误信息添加
	 * @param mobilePhone
	 * @param errorCode
	 * @throws Exception
	 */
	public void insertAppLogs(String mobilePhone,String errorCode) throws Exception{
		ErrorLog errorLog = new ErrorLog();
		errorLog.setPhoneNo(mobilePhone);
		errorLog.setErrorDate(new Date());
        errorLog.setErrorNo(errorCode);
        errorLogDao.insert(errorLog);
	}

    /**
     * 插入app后台日志
     * @param mobilePhone 手机号
     * @param orderId 流水号
     * @param errorCode 错误码
     * @return
     * @throws Exception
     */
    public void insertAppLogs(String mobilePhone,String orderId,String errorCode) throws Exception{
        ErrorLog errorLog = new ErrorLog();
        errorLog.setPhoneNo(mobilePhone);
        errorLog.setPospsn(orderId);
        errorLog.setErrorDate(new Date());
        errorLog.setErrorNo(errorCode);
        errorLogDao.insert(errorLog);
    }
    
    /**
	 * 校验身份证号
	 * @param cardId
	 * @return
	 * @throws ParseException 
	 */
	public static boolean checkIdCard(String cardId) throws ParseException {
		String idCard="";
		idCard=IdcardUtils.idCardValidate(cardId.toLowerCase());
		if ("".equals(idCard)) {
			return true;
		}else {
			return false;
		}
	}


    /**
     * 获取银行卡信息
     * @param bankName
     * @param bankNumber
     * @return
     * @throws Exception
     */
    public String getBankCardInfo(String bankName,String bankNumber,String mobilePhone) throws Exception{
        String message = "";
        List<PayCmmtufit> list = payCmmtufitDao.searchCardInfoByBeforeSix(bankNumber.substring(0,6)+"%");
        if(null!=list && list.size()>=1){
            PayCmmtufit bankInfo = list.get(0);
            String cardName = bankInfo.getCrdNm();
            String bnkName = bankInfo.getBnkName();
            //判断用户自选的银行是否与银行卡所属银行匹配
            if(bnkName.equals(bankName) || bnkName.indexOf(bankName)!=-1 || bankName.substring(2).equals(bnkName)){
                message = SUCCESSMESSAGE+"-"+cardName;
            }else{
                insertAppLogs(mobilePhone,"","2094");
                message = xdt.common.RetAppMessage.BANKCARDNUMBERANDBANKNAMENOMATCH;
            }
        }else{
            insertAppLogs(mobilePhone,"","2009");
            message = xdt.common.RetAppMessage.BANKCARDISNOTSUPPORTED;
        }
        return message;
    }


    /**
     * 插入交易历史记录
     * @param t
     * @return
     * @throws Exception
     */
    public String getTransHistoryRecord(PmsTransHistoryRecord t) throws Exception{
        String message = RetAppMessage.TRADINGINFOSAVESUCCESS;
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("mercId", t.getMercid());
        map.put("bankCardNumber",t.getBankcardnumber());
        if(null==pmsTransHistoryRecordDao.selectCardInfo(map)){
            if(pmsTransHistoryRecordDao.insert(t)==1){
                message = RetAppMessage.HISTORYRECORDSAVESUCCESS;
            }else{
                insertAppLogs(t.getMobilephone(),"","2018");
                message = RetAppMessage.HISTORYRECORDSAVEFAILED;
            }
        }else{
                message =  RetAppMessage.HISTORYALREADYEXIST;
        }
        return message;
    }


    /**
     * 根据app订单类生成 流水记录对象  仅供非刷卡业务调用
     * @param pmsAppTransInfo
     * @return
     */

    public PospTransInfo generateTransFromAppTrans(PmsAppTransInfo pmsAppTransInfo) throws Exception {

        PospTransInfo pospTransInfo = null;
        if(pmsAppTransInfo != null && StringUtils.isNotBlank(pmsAppTransInfo.getMercid())){
            pospTransInfo = new PospTransInfo();
               //设置id
              Integer id = pospTransInfoDAO.getNextTransid();
              if(id != null && id != 0 ){
                  pospTransInfo.setId(id);
              }else{
                  logger.info("根据订单生成流水失败，orderid："+pmsAppTransInfo.getOrderid());
                  return null;
              }

            //设置欧单编号
            pmsAppTransInfo.setoAgentNo(pmsAppTransInfo.getoAgentNo());

            //如果是第三方的业务  则取第三方费率
            if( pmsAppTransInfo.getTradetypecode().equals(TradeTypeEnum.phonePay.getTypeCode())
                    ||pmsAppTransInfo.getTradetypecode().equals(TradeTypeEnum.utility.getTypeCode())
                    ||pmsAppTransInfo.getTradetypecode().equals(TradeTypeEnum.sinopecPay.getTypeCode())
                    ||pmsAppTransInfo.getTradetypecode().equals(TradeTypeEnum.onlinePay.getTypeCode())
                    ||pmsAppTransInfo.getTradetypecode().equals(TradeTypeEnum.merchantCollect.getTypeCode())){



                String thirdRate="";
                //百度
                if(pmsAppTransInfo.getPaymentcode().equals(PaymentCodeEnum.baiduPay.getTypeCode())){
                    thirdRate =   getThirdPart("3",pmsAppTransInfo.getoAgentNo());
                }
                //微信
                if(pmsAppTransInfo.getPaymentcode().equals(PaymentCodeEnum.weixinPay.getTypeCode())){
                    thirdRate =   getThirdPart("4",pmsAppTransInfo.getoAgentNo());
                }
                //支付宝
                if(pmsAppTransInfo.getPaymentcode().equals(PaymentCodeEnum.zhifubaoPay.getTypeCode())){
//                    thirdRate =   getThirdPart("5",pmsAppTransInfo.getoAgentNo());
                }

                //摩宝快捷
                if(pmsAppTransInfo.getPaymentcode().equals(PaymentCodeEnum.moBaoQuickPay.getTypeCode())){
                    thirdRate =   getThirdPart(RateTypeEnum.mobaoQuickPayRateType.getTypeCode(),pmsAppTransInfo.getoAgentNo());
                }
                //恒丰快捷
                if(pmsAppTransInfo.getPaymentcode().equals(PaymentCodeEnum.hengFengQuickPay.getTypeCode())){
                	thirdRate =   getThirdPart(RateTypeEnum.hengfengQuickPayRateType.getTypeCode(),pmsAppTransInfo.getoAgentNo());
                }
              //BEECLOUD快捷
                if(pmsAppTransInfo.getPaymentcode().equals(PaymentCodeEnum.BCQuickPay.getTypeCode())){
                	thirdRate =   getThirdPart(RateTypeEnum.beecloudRateType.getTypeCode(),pmsAppTransInfo.getoAgentNo());
                }
                //浦发扫码
                if(pmsAppTransInfo.getPaymentcode().equals(PaymentCodeEnum.QRCodePay.getTypeCode())){
                	thirdRate =   getThirdPart(RateTypeEnum.pufaRateType.getTypeCode(),pmsAppTransInfo.getoAgentNo());
                }
                //公众号支付
                if(pmsAppTransInfo.getPaymentcode().equals(PaymentCodeEnum.PNCodePay.getTypeCode())){
                	thirdRate =   getThirdPart(RateTypeEnum.weixinRateType.getTypeCode(),pmsAppTransInfo.getoAgentNo());
                }

                if(StringUtils.isBlank(thirdRate)){
                    logger.info("查不到当前商户："+pmsAppTransInfo.getMercid()+"   当前业务："+pmsAppTransInfo.getTradetype()+"的费率信息，默认将费率设置为0");
                }else{
                    pospTransInfo.setPremiumrate(thirdRate);
                }


            }else if(pmsAppTransInfo.getTradetypecode().equals(TradeTypeEnum.merchantCollect.getTypeCode()) || pmsAppTransInfo.getTradetypecode().equals(TradeTypeEnum.drawMoney.getTypeCode())){
                //收款业务设置   和 提款业务
                String rate = "";
                //刷卡支付
                if(pmsAppTransInfo.getPaymentcode().equals(PaymentCodeEnum.shuakaPay.getTypeCode()) || pmsAppTransInfo.getTradetypecode().equals(TradeTypeEnum.drawMoney.getTypeCode())){
                    rate = getBrushCardRate(pmsAppTransInfo.getMercid(),pmsAppTransInfo.getTradetypecode(),pmsAppTransInfo.getoAgentNo());
                }
                //第三方支付
                //百度
                if(pmsAppTransInfo.getPaymentcode().equals(PaymentCodeEnum.baiduPay.getTypeCode())){
                    rate =   getThirdPart("3",pmsAppTransInfo.getoAgentNo());
                }
                //微信
                if(pmsAppTransInfo.getPaymentcode().equals(PaymentCodeEnum.weixinPay.getTypeCode())){
                    rate =   getThirdPart("4",pmsAppTransInfo.getoAgentNo());
                }
                //支付宝
                if(pmsAppTransInfo.getPaymentcode().equals(PaymentCodeEnum.zhifubaoPay.getTypeCode())){
                    rate =   getThirdPart("5",pmsAppTransInfo.getoAgentNo());
                }

                //摩宝快捷
                if(pmsAppTransInfo.getPaymentcode().equals(PaymentCodeEnum.moBaoQuickPay.getTypeCode())){
                    rate =   getThirdPart(RateTypeEnum.mobaoQuickPayRateType.getTypeCode(),pmsAppTransInfo.getoAgentNo());
                }
                //恒丰快捷
                if(pmsAppTransInfo.getPaymentcode().equals(PaymentCodeEnum.moBaoQuickPay.getTypeCode())){
                	rate =   getThirdPart(RateTypeEnum.hengfengQuickPayRateType.getTypeCode(),pmsAppTransInfo.getoAgentNo());
                }
                //公众号支付
                if(pmsAppTransInfo.getPaymentcode().equals(PaymentCodeEnum.PNCodePay.getTypeCode())){
                	rate =   getThirdPart(RateTypeEnum.weixinRateType.getTypeCode(),pmsAppTransInfo.getoAgentNo());
                }

                pospTransInfo.setTransfee3(Double.parseDouble(rate));
            }else if(pmsAppTransInfo.getTradetypecode().equals(TradeTypeEnum.shop.getTypeCode())){
                //商城
                String rate = "";
                //刷卡支付
                if(pmsAppTransInfo.getPaymentcode().equals(PaymentCodeEnum.shuakaPay.getTypeCode()) || pmsAppTransInfo.getTradetypecode().equals(TradeTypeEnum.drawMoney.getTypeCode())){
                    rate = getBrushCardRate(pmsAppTransInfo.getMercid(),pmsAppTransInfo.getTradetypecode(),pmsAppTransInfo.getoAgentNo());
                }
                //摩宝快捷
                if(pmsAppTransInfo.getPaymentcode().equals(PaymentCodeEnum.moBaoQuickPay.getTypeCode())){
                    rate =   getThirdPart(RateTypeEnum.mobaoQuickPayRateType.getTypeCode(),pmsAppTransInfo.getoAgentNo());
                }
                //恒丰快捷
                if(pmsAppTransInfo.getPaymentcode().equals(PaymentCodeEnum.hengFengQuickPay.getTypeCode())){
                	rate =   getThirdPart(RateTypeEnum.hengfengQuickPayRateType.getTypeCode(),pmsAppTransInfo.getoAgentNo());
                }
                //BEECLOUD快捷
                if(pmsAppTransInfo.getPaymentcode().equals(PaymentCodeEnum.BCQuickPay.getTypeCode())){
                	rate =   getThirdPart(RateTypeEnum.beecloudRateType.getTypeCode(),pmsAppTransInfo.getoAgentNo());
                }
                pospTransInfo.setTransfee3(Double.parseDouble(rate));
            }



            //获取通道的标准费率 END

            //设置主机交易流水号
            pospTransInfo.setSysseqno(null);
            //设置宣称费率
            pospTransInfo.setTransfee2(null);
            //设置通道费率
            pospTransInfo.setTransfee4(null);
            //设置实际佣金
            pospTransInfo.setTransfee1(null);
            //设置消费冲正原因
            pospTransInfo.setReason(null);
            //设置说明
            pospTransInfo.setRemark(pmsAppTransInfo.getTradetype() + "  金额：" + pmsAppTransInfo.getFactamount());
            //设置SIM卡
            pospTransInfo.setSimId(null);
            //设置 TAC
            pospTransInfo.setTac(null);
            //设置银行编码
            pospTransInfo.setBnkCd(null);
            //设置平台流水奥  这里默认设置第三方订单号
            pospTransInfo.setPospsn(pmsAppTransInfo.getPortorderid());
            //设置卡有效期
            pospTransInfo.setCardvaliddate(null);
            //设置通道pos终端号
            pospTransInfo.setBuspos(null);
            //设置pos平台交易吗
            pospTransInfo.setPospservicecode(null);
            //设置冲正流水
            pospTransInfo.setCancelflag(null);
            //设置商户号
            pospTransInfo.setMerchantcode(pmsAppTransInfo.getMercid());
            //设置补录时记录上传的终端机流水号
            pospTransInfo.setTerminalsn(null);
            //设置交易上送帐期
            pospTransInfo.setSenddate(new Date());
            //服务网点PIN码
            pospTransInfo.setCounterpin(null);
            //设置渠道号  03：手机
            pospTransInfo.setChannelno("03");
            //设置银行名称
            pospTransInfo.setBnkNm(null);
            //设置posid
            pospTransInfo.setPosid(null);
            //设置交易码  默认都为消费业务
            pospTransInfo.setTranscode("000000");
            //设置交易安全控制信息
            pospTransInfo.setTranssecuritycontrol(null);
            //设置卡类型
            pospTransInfo.setCrdTyp(null);
            //设置卡号
            pospTransInfo.setCardno(null);
            //设置真正的交易类型     交易码 +交易类型+支付方式
            pospTransInfo.setSearchTransCode("000000" + pmsAppTransInfo.getTradetypecode()+pmsAppTransInfo.getPaymentcode());
            //设置pos交易日期
            pospTransInfo.setTransdate(UtilDate.getDate());
            //设置pos交易时间
            pospTransInfo.setTranstime(UtilDate.getDateTime());
            //设置批量结算结果标志
            pospTransInfo.setSettlementflag(null);
            //设置最近批结算ID
            pospTransInfo.setSettlementid(null);
            //设置授权码
            pospTransInfo.setAuthoritycode(null);
            //设置是否自清 默认自清
            pospTransInfo.setIsClearSelf(null);
            //设置交易响应标志 00-成功
            pospTransInfo.setResponsecode(null);
            /*if(pmsAppTransInfo.getStatus().equals("0")){
                pospTransInfo.setResponsecode("00");
            }else{
                pospTransInfo.setResponsecode(null);
            }*/
            //设置订单id
            pospTransInfo.setOrderId(pmsAppTransInfo.getOrderid());
            //设置通道商户编码  商户编码不设置
            pospTransInfo.setBusinfo(null);
            //设置附加费用
            pospTransInfo.setAddfee(null);
            //设置刷卡费率  当前处理为调用第三方处理，刷卡费率不设置
            pospTransInfo.setPremiumrate(null);
            //设置原始交易记录报文id
            pospTransInfo.setPfmtid(null);
           //服务网点输入方式
            pospTransInfo.setInputtype(null);
            // 0-脱机POS上送流水，1-联机消费流水
            pospTransInfo.setTransstatus(null);
            //设置基站信息
            pospTransInfo.setStationInfo(null);
            //设置交易时间间隔   这里先不处理，没有发现需要用到的地方
            pospTransInfo.setInterVal(null);
            //设置关联路由id
            pospTransInfo.setRouteid(null);
            //设置交易消息类型    交易类型+支付方式
            pospTransInfo.setMsgtype( pmsAppTransInfo.getTradetypecode() + pmsAppTransInfo.getPaymentcode() );
            //设置发生额
            pospTransInfo.setTransamt(new BigDecimal(pmsAppTransInfo.getFactamount()));
            //设置终端号
            pospTransInfo.setPosterminalid(null);
            //设置操作员id
            pospTransInfo.setOperid(null);
            //设置POS服务平台代码
            pospTransInfo.setPospid(null);
            //设置货币代码
            pospTransInfo.setCurrencycode(null);
            //结算日期
            pospTransInfo.setBalancedate(null);
            // PSAM卡号
            pospTransInfo.setPsamno(null);
            //个人标识码
            pospTransInfo.setPersonalid(null);
            //卡号
            pospTransInfo.setCrdNm(null);
            //设置冲正标志   0-正常交易，1-冲正交易，2-被冲正交易
            pospTransInfo.setCancelflag(0);
            //设置冻结状态
            pospTransInfo.setFreezeState(null);
            //设置终端序列号
            pospTransInfo.setPossn(null);
            //设置服务网点条件码
            pospTransInfo.setConuterconditioncode(null);
            //是否App交易
            pospTransInfo.setIsapp(1);
            //设置支付方式
            pospTransInfo.setPaymentType(pmsAppTransInfo.getPaymentcode());
            //设置批次号
            pospTransInfo.setBatno(null);
            //O单编号
            pospTransInfo.setoAgentNo(pmsAppTransInfo.getoAgentNo());
        }
        return  pospTransInfo;
    }


    /** 创建刷卡消费请求接口
     * @param dto
     * @throws Exception
     *
     */
    public String createBrushCalorieOfConsumptionDTORequest(SessionInfo sessionInfo,BrushCalorieOfConsumptionRequestDTO dto,String serialNo,String port,String rate,String snno) throws Exception{
        //根据sn号获取MerchantPos信息
        PmsMerchantPos merchantPos = null;

        String oAgentNo = sessionInfo.getoAgentNo();
        //判断欧单编号，如果为空直接返回错误
        if(StringUtils.isBlank(oAgentNo)){
            return "fail";//获取信息失败
        }

        PmsPosInfo pmsMerchantPos =  pmsPosInfo.selectBusinessPos(snno);

        if(pmsMerchantPos != null){
            Map<String,String> mapParam = new HashMap<String,String>();
            mapParam.put("merchantid",sessionInfo.getId());
            mapParam.put("posId",pmsMerchantPos.getId().toString());
            merchantPos = pmsPos.selectMerchantidAndSn(mapParam);
        }

        String cardNo = dto.getCardNo();//主卡号
      //  boolean bankCard = checkBankCard(cardNo);
       // if(merchantPos != null && merchantPos.getUsestatus().equals((short)5) && bankCard ){
        if(merchantPos != null && merchantPos.getUsestatus().equals((short)5)  ){
            List<PayCmmtufit> meros = payCmmtufitDao.searchCardInfoByBeforeSix(cardNo.substring(0,6)+"%");
            if(meros.size()!=0){//获取pmsMerchantPos
                Date date = new Date();
                String dateStr = dtoSdf.format(date);
                String time=dateStr.substring(5,9);
                String icRecord = dto.getIcRecord();//55域

                String cardValid = dto.getCardValid();//卡有效期
                String twoTrack = dto.getTwoTrack();//磁道数据
                String password = dto.getPassword();//个人标识码数据 B64  16位 -*有输入密码时必须上送
                String safetyControl = dto.getSafetyControl();//安全控制信息--16位
                String icRecord2 = dto.getIcRecord();//IC卡数据域	255位  *-IC卡信息 小传
                String reservedPrivate = dto.getReservedPrivate();//解密工作密钥mac pin
                String afo64=dto.getAf064();//所有域的位图+响应消息中39域为“00”时必选 就是个bitmap位图
                String threeTrack = dto.getThreeTrack();//磁道数据 104位()
                String sn=dto.getSn();
                //判断是插卡还是刷卡
                String serviceCode ="";
                String reseved60 = "";
                if("".equals(icRecord)){
                    serviceCode = ("".equals(dto.getPassword())) ? "022" : "021";
                    reseved60 = "000500";
                }else {
                    serviceCode = ("".equals(dto.getPassword())) ? "052" : "051";
                    reseved60 = "000501";
                }
                String ServicePin = null;
                if(!"".equals(password)){ServicePin= "06";}
                else{threeTrack="null";};//有密码上送
                String string = new BigDecimal(dto.getPayAmount()).toString();//金额单位是分
                for(int i=string.length();i<12;i++){string="0"+string;}
                String cardSeq = dto.getCardSeq();

                String batno=merchantPos.getBatno();//获取批次号
                BrushCalorieOfConsumptionTPRequestDTO tpRequest = new BrushCalorieOfConsumptionTPRequestDTO();
                String mobilephone = sessionInfo.getMobilephone();
                tpRequest.setoAgentNo(oAgentNo);
                tpRequest.setPhone(mobilephone);//手机号
                tpRequest.setTerminalSN(sn);
                tpRequest.setBatno(batno); //批次号  （第三方管理）
                tpRequest.setDealType(port); //A003—消费
                tpRequest.setBitmap("null"); //位图 （第三方管理）
                if(rate=="" || rate==null){rate="null";}
                tpRequest.setTransFee(rate);//费率
                tpRequest.setCardNo(cardNo); //主账号 卡号
                tpRequest.setTypeLine("null"); //交易处理码    （第三方管理）
                tpRequest.setMoney(string); //交易金额
                tpRequest.setSerialNo(serialNo);//流水号
                tpRequest.setTransData(dateStr);//所在地时间 HHMMSS 6位
                tpRequest.setTransTime(time);//所在地日期 MMDD  4位
                if(cardValid==null || cardValid==""){cardValid="null";}
                tpRequest.setCardValid(cardValid);//卡有效期
                tpRequest.setCleraDate("null"); //清算日期 MMDD 4位 （第三方管理）
                int cards = cardSeq.length();
                if (cardSeq == null || "".equals(cardSeq)) {
                    cardSeq = "null";
                } else if (cards != 3){
                    if (cards == 2) {
                        cardSeq = "0" + cardSeq;
                    } else if(cards==1) {
                        cardSeq = "00" + cardSeq;
                    }}
                tpRequest.setCardSeq(cardSeq);//卡片序列号 3位
                if(icRecord==null || icRecord==""){icRecord="null";}
                tpRequest.setIdCode(icRecord); //收单机构标识码 11位 * （第三方管理）
                tpRequest.setReference("null");//检索参考 12位 * （第三方管理）
                tpRequest.setAuthorizationCode("null"); //授权码 6位   * （第三方管理）
                tpRequest.setErrCode2("null"); //附加响应数据  25位  * （第三方管理）
                tpRequest.setTransCurrency("156");//交易货币代码
                tpRequest.setPosVersion("null");//POS终端信息  600位  * （第三方管理）
                if(batno==null && batno==""){batno="000001";}
                tpRequest.setReseved60("22"+batno+reseved60);//交易类型码+批次号+网络管理信息码+终端读取能力+基于PBOC借/贷记标准的IC卡条件代码 （第三方管理）
                tpRequest.setReseved61("null");//原始信息域 29位 （第三方管理）
                tpRequest.setServiceCode(serviceCode); //服务点输入方式码 3位
                tpRequest.setServiceCondition("null");//服务点条件码 2位 （第三方管理）
                tpRequest.setServicePin(ServicePin); //服务点PIN获取码 2位 --密码位数0
                if(twoTrack==null || twoTrack==""){twoTrack="null";}
                tpRequest.setTwoTrack(twoTrack); //磁道数据 37位
                if(threeTrack==null || threeTrack==""){threeTrack="null";  }
                tpRequest.setThreeTrack(threeTrack); //磁道数据 104位()
                tpRequest.setErrCode("null");//返回码
                tpRequest.setPersonalCode(password); //个人标识码数据 B64  16位 -*有输入密码时必须上送
                if(safetyControl==null || safetyControl==""){safetyControl="null";};
                tpRequest.setSafetyControl(safetyControl); //安全控制信息 16位
                if("".equals(icRecord2)){icRecord2="null";}
                tpRequest.setIcRecord(icRecord2);//IC卡数据域	255位  *-IC卡信息 小传
                if("".equals(reservedPrivate)){reservedPrivate="null";}
                tpRequest.setReservedPrivate(reservedPrivate); //解密工作密钥mac pin
                tpRequest.setAf063("null");//操作员国际信用卡公司代码+自定义域
                if(afo64==null || afo64==""){afo64="null";}
                tpRequest.setAf064(afo64);//所有域的位图+响应消息中39域为“00”时必选 就是个bitmap位图
                tpRequest.setMerPos(merchantPos.getPosbusinessno());//通道POS的信息  Posbusinessno
                tpRequest.setMerInfo(sessionInfo.getMercId());//通道商户Id
                logger.info("[req_deduction]"+createJson(tpRequest));
                return createJsonString(tpRequest); //将信息返给第三方接口
            }else{
                return "meros"; //pos机信息读取失败
            }}else {
            return "fail";//获取信息失败
        }}


    /**
     * 获取第三方费率
     * @param type
     * @Author Jeff
     * @return
     */
    public String  getThirdPart(String type,String oAgentNo){
        String result = "";
        if(StringUtils.isNumeric(type)){
            AppRateConfig appRate = new AppRateConfig();
            appRate.setRateType(type);
            appRate.setoAgentNo(oAgentNo);
            AppRateConfig appRateConfig1 = appRateConfig.getByRateTypeAndoAgentNo(appRate);
            result = appRateConfig1.getRate();
        }
       return  result;
    }


    /**
     * 获取刷卡业务，提款业务'
     * @author Jeff
     * @return
     */
    public String getBrushCardRate(String mercId,String businessCode,String oAgentNo){
        String result = "";
        Map<String,String> map = new HashMap<String,String>();
        map.put("mercid",mercId);
        map.put("businesscode",businessCode);
        map.put("oAgentNo",oAgentNo);
        AppRateTypeAndAmount appRateTypeAndAmount = pmsAppAmountAndRateConfigDao.queryAmountAndRateInfoForShuaka(map);

        if(appRateTypeAndAmount != null){
            String collectRate = "";
            collectRate = appRateTypeAndAmount.getRate();
            if(StringUtils.isNotBlank(collectRate)){
                result = collectRate;
            }
        }
        return  result;
    }


    /**
     * 查找第三方通道商户的标准费率
     * @param businessId
     * @return
     */
    public PmsMerchantFee getThirdPartChennelRate(String businessId){
        // 计算费率(无表无计算)
        PmsMerchantFee pmsMerchantFee = pmsMerchantFeeDao.getByMercId(businessId);
        return  pmsMerchantFee;
    }


//    public static  void parsXml(){
//        System.out.println("--------------------开始加载短信接口配置文件----------------------------");
//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        DocumentBuilder db = null;
//        try {
//            db = dbf.newDocumentBuilder();
//            Document document = db.parse(new File(java.net.URLDecoder.decode(BaseAction.class.getResource("/oagentMsgCfg.xml").getFile().toString(),"utf-8")));
//            NodeList list = document.getElementsByTagName("oagent");
////            logger.info("短信接口获取xml欧单个数："+list.getLength());
//            oagentMsgCfgList = new ArrayList<OagentMsgCfg>();
//            // 遍历每一个节点
//            if(list != null && list.getLength() > 0){
//                System.out.println("共获取到"+list.getLength()+"个节点");
//                for(int i=0;i<list.getLength();i++){
//                    Element element = (Element)list.item(i);
//                    OagentMsgCfg oagentMsgCfg= new  OagentMsgCfg();
//                    String oAgentNo = element.getElementsByTagName("OagentNo").item(0).getFirstChild().getNodeValue();
//                    String account = element.getElementsByTagName("account").item(0).getFirstChild().getNodeValue();
//                    String pswd = element.getElementsByTagName("pswd").item(0).getFirstChild().getNodeValue();
//                    String describe = element.getElementsByTagName("describe").item(0).getFirstChild().getNodeValue();
//                    oagentMsgCfg.setoAgentNo(oAgentNo);
//                    oagentMsgCfg.setAccount(account);
//                    oagentMsgCfg.setPswd(pswd);
//                    oagentMsgCfg.setDescribe(describe);
//                    oagentMsgCfgList.add(oagentMsgCfg);
//                    System.out.println("第"+(i+1)+"个节点,"+oAgentNo+","+account+","+pswd);
//                }
//            }
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (SAXException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//
//        System.out.println("----------------------------------加载完成----------------------------");
//
//    }
    
    /**
     * 根据表名获取主键值
     * @param businessId
     * @return
     * @throws Exception 
     */
	public String getPrimarykey(String tableName) throws Exception{
		synchronized (this) {
	        String primarykey = null;
	    	
	    	PrimarykeyTable primarykeyTable = primarykeyTableDao.searchById(tableName);
	        
	        if(primarykeyTable == null){
	        	primarykeyTable = new PrimarykeyTable();
	        	primarykeyTable.setTableName(tableName);
	        	primarykeyTable.setMaxValue("1");
	        	int count = primarykeyTableDao.insert(primarykeyTable);
	        	if(count > 0){
	        		primarykey = "1";
	        	}
	        }else{
	        	String maxValue = Long.toString(Long.parseLong(primarykeyTable.getMaxValue())+1);
	        	primarykeyTable.setMaxValue(maxValue);
	        	int count = primarykeyTableDao.update(primarykeyTable);
	        	if(count > 0){
	        		primarykey = maxValue;
	        	}
	        }
	        return primarykey;
		}
    }

    /**
     * 生成上送通道的交易码
     * @param tradeTypeEnum
     * @param paymentCodeEnum
     * @return
     */
    public String generateTransOrderId(TradeTypeEnum tradeTypeEnum,PaymentCodeEnum paymentCodeEnum){
        String result = "";
        Random r = new Random();
        int rom = r.nextInt(100);
        result =  tradeTypeEnum.getTypeCode()+ paymentCodeEnum.getTypeCode()+ System.currentTimeMillis()+rom;
        return result;
    }
    
    
    public void sign(Map<String, Object> result) throws Exception{
    	logger.info("pufa签名数据:"+result);
    	TreeMap<String, String> param=new TreeMap<String, String>();
    	
    	Set<Entry<String, Object>> set=result.entrySet();
    	Iterator<Entry<String, Object>> iterator=set.iterator();
    	while(iterator.hasNext()){
    		Entry<String, Object> entry=iterator.next();
    		if(entry.getValue()!=null&&entry.getValue()!=""){
    			param.put(entry.getKey(), entry.getValue().toString());
    		}
    	}
    	
    	String sign=PuFaSignUtil.sign(param);
    	result.put("sign", sign);
    }
    
    /**
	 * 简单查询路由 冲正、退货不能调用
	 * @param req
	 * @param rsp
	 * @return
	 */
    protected PospRouteInfo route(String merchId){
    	
    	
    	PmsMerchantInfo info;
		try {
			info = pmsMerchantInfoDao.selectMercByMercId(merchId);
			
			List result = pospRouteInfoDao.queryMyAllRoutes(Long.parseLong(info.getId()));
	    	
			Date now = new Date();
			
			String currentDay = DateUtil.format(now);
		
			for(Iterator it = result.iterator();it.hasNext();){
				PospRouteInfo route = (PospRouteInfo)it.next();
				
				Date from = DateUtil.parseDateTime(currentDay+" "+route.getEffectFrom());
				Date to = DateUtil.parseDateTime(currentDay+" "+route.getEffectTo());
				
				if(now.after(from) && now.before(to)){
					return route;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("----路由查询错误----");
		}
		return null;
    }
	protected PospRouteInfo routes(String id){
	    	
	    	
	    	PmsMerchantInfo info;
			try {
				
				List result = pospRouteInfoDao.queryMyAllRoutes(Long.parseLong(id));
		    	
				Date now = new Date();
				
				String currentDay = DateUtil.format(now);
			
				for(Iterator it = result.iterator();it.hasNext();){
					PospRouteInfo route = (PospRouteInfo)it.next();
					
					Date from = DateUtil.parseDateTime(currentDay+" "+route.getEffectFrom());
					Date to = DateUtil.parseDateTime(currentDay+" "+route.getEffectTo());
					
					if(now.after(from) && now.before(to)){
						return route;
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.info("----路由查询错误----");
			}
			return null;
	    }
	
	protected PospRouteInfo routesMer(String id){
    	
    	
    	PmsMerchantInfo info;
		try {
			
			List result = pospRouteInfoDao.queryMyAllRoutesMer(Long.parseLong(id));
	    	
			Date now = new Date();
			
			String currentDay = DateUtil.format(now);
		
			for(Iterator it = result.iterator();it.hasNext();){
				PospRouteInfo route = (PospRouteInfo)it.next();
				
				Date from = DateUtil.parseDateTime(currentDay+" "+route.getEffectFrom());
				Date to = DateUtil.parseDateTime(currentDay+" "+route.getEffectTo());
				
				if(now.after(from) && now.before(to)){
					return route;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("----路由查询错误----");
		}
		return null;
    }
	
    /**
	 * 查询上游商户号和密钥
	 * 
	 * @param obj
	 * @return
	 */
	public PmsBusinessPos selectKey(String merid) {
		PmsBusinessPos businessPos = new PmsBusinessPos();
		try {
			PospRouteInfo route = route(merid);
			System.out.println(route);
			PmsBusinessInfo busInfo = new PmsBusinessInfo();
			System.out.println(route.getMerchantId().toString());
			busInfo = pmsBusinessInfoDao.searchById(route.getMerchantId()
					.toString());
			businessPos.setBusinessnum(busInfo.getBusinessNum());
			businessPos = businessPosDao.searchById(businessPos
					.getBusinessnum());
			System.out.println(JSON.toJSON(businessPos));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return businessPos;
	}
	
	/**
	 * 代付扣款D0
	 */
	public int updateD0(String merchanId,String amount) {
		Map<String, String> map=new HashMap<>();
		map.put("machId",merchanId);
		map.put("payMoney", amount);
		int num =pmsMerchantInfoDao.updataD0(map);
		return num;
	}
	/**
	 * 代付扣款D0
	 */
	public int updateT1(String merchanId,String amount) {
		Map<String, String> map=new HashMap<>();
		map.put("machId",merchanId);
		map.put("payMoney", amount);
		int num =pmsMerchantInfoDao.updataT1(map);
		return num;
	}
	/**
	 * 根据商户号查询信息
	 * @param merchantInfo
	 * @return
	 * @throws Exception
	 */
	public PmsMerchantInfo select(String merchantInfo) throws Exception {
		PmsMerchantInfo merchantInfo2 = new PmsMerchantInfo();
		merchantInfo2.setMercId(merchantInfo);
		
		List merchant = this.pmsMerchantInfoDao.searchList(merchantInfo2);
		merchantInfo2 = (PmsMerchantInfo) merchant.get(0);
		
		return merchantInfo2;
	}
	
	/**
	 * 录入交易流水 并记算费率
	 * 
	 * @throws Exception
	 */
	public PospTransInfo InsertJournal(PmsAppTransInfo pmsAppTransInfo) throws Exception {
		System.out.println("----插入流水开始----");
		PospTransInfo pospTransInfo = new PospTransInfo();
		Integer id = pospTransInfoDAO.getNextTransid();
		if (id != null && id != 0) {
			pospTransInfo.setId(id);
		} else {
			System.out.println("根据订单生成流水失败，orderid：" + pmsAppTransInfo.getOrderid());
			return null;
		}
		// 获取通道的标准费率 END

		// 设置主机交易流水号
		pospTransInfo.setSysseqno(null);
		// 设置宣称费率
		pospTransInfo.setTransfee2(null);
		// 设置通道费率
		pospTransInfo.setTransfee4(null);
		// 设置实际佣金
		pospTransInfo.setTransfee1(null);
		// 设置消费冲正原因
		pospTransInfo.setReason(null);
		// 设置说明
		pospTransInfo.setRemark(pmsAppTransInfo.getTradetype() + "  金额：" + pmsAppTransInfo.getFactamount());
		// 设置SIM卡
		pospTransInfo.setSimId(null);
		// 设置 TAC
		pospTransInfo.setTac(null);
		// 设置银行编码
		pospTransInfo.setBnkCd(null);
		// 设置平台流水奥 这里默认设置第三方订单号
		pospTransInfo.setPospsn(pmsAppTransInfo.getPortorderid());
		// 设置卡有效期
		pospTransInfo.setCardvaliddate(null);
		// 设置通道pos终端号
		pospTransInfo.setBuspos(null);
		// 设置pos平台交易吗
		pospTransInfo.setPospservicecode(null);
		// 设置冲正流水
		pospTransInfo.setCancelflag(null);
		// 设置商户号
		pospTransInfo.setMerchantcode(pmsAppTransInfo.getMercid());
		// 设置补录时记录上传的终端机流水号
		pospTransInfo.setTerminalsn(null);
		// 设置交易上送帐期
		pospTransInfo.setSenddate(new Date());
		// 服务网点PIN码
		pospTransInfo.setCounterpin(null);
		// 设置渠道号 03：手机
		pospTransInfo.setChannelno("03");
		// 设置银行名称
		pospTransInfo.setBnkNm(null);
		// 设置posid
		pospTransInfo.setPosid(null);
		// 设置交易码 默认都为消费业务
		pospTransInfo.setTranscode("000000");
		// 设置交易安全控制信息
		pospTransInfo.setTranssecuritycontrol(null);
		// 设置卡类型
		pospTransInfo.setCrdTyp(null);
		// 设置卡号
		pospTransInfo.setCardno(null);
		// 设置真正的交易类型 交易码 +交易类型+支付方式
		pospTransInfo
				.setSearchTransCode("000000" + pmsAppTransInfo.getTradetypecode() + pmsAppTransInfo.getPaymentcode());
		// 设置pos交易日期
		pospTransInfo.setTransdate(UtilDate.getDate());
		// 设置pos交易时间
		pospTransInfo.setTranstime(UtilDate.getDateTime());
		// 设置批量结算结果标志
		pospTransInfo.setSettlementflag(null);
		// 设置最近批结算ID
		pospTransInfo.setSettlementid(null);
		// 设置授权码
		pospTransInfo.setAuthoritycode(null);
		// 设置是否自清 默认自清
		pospTransInfo.setIsClearSelf(null);
		// 设置交易响应标志 00-成功
		pospTransInfo.setResponsecode(null);
		/*
		 * if(pmsAppTransInfo.getStatus().equals("0")){
		 * pospTransInfo.setResponsecode("00"); }else{
		 * pospTransInfo.setResponsecode(null); }
		 */
		// 设置订单id
		pospTransInfo.setOrderId(pmsAppTransInfo.getOrderid());
		// 设置通道商户编码 商户编码不设置
		pospTransInfo.setBusinfo(null);
		// 设置附加费用
		pospTransInfo.setAddfee(null);
		// 设置刷卡费率 当前处理为调用第三方处理，刷卡费率不设置
		pospTransInfo.setPremiumrate(null);
		// 设置原始交易记录报文id
		pospTransInfo.setPfmtid(null);
		// 服务网点输入方式
		pospTransInfo.setInputtype(null);
		// 0-脱机POS上送流水，1-联机消费流水
		pospTransInfo.setTransstatus(null);
		// 设置基站信息
		pospTransInfo.setStationInfo(null);
		// 设置交易时间间隔 这里先不处理，没有发现需要用到的地方
		pospTransInfo.setInterVal(null);
		// 设置关联路由id
		pospTransInfo.setRouteid(null);
		// 设置交易消息类型 交易类型+支付方式
		pospTransInfo.setMsgtype(pmsAppTransInfo.getTradetypecode() + pmsAppTransInfo.getPaymentcode());
		// 设置发生额
		pospTransInfo.setTransamt(new BigDecimal(pmsAppTransInfo.getFactamount()));
		// 设置终端号
		pospTransInfo.setPosterminalid(null);
		// 设置操作员id
		pospTransInfo.setOperid(null);
		// 设置POS服务平台代码
		pospTransInfo.setPospid(null);
		// 设置货币代码
		pospTransInfo.setCurrencycode(null);
		// 结算日期
		pospTransInfo.setBalancedate(null);
		// PSAM卡号
		pospTransInfo.setPsamno(null);
		// 个人标识码
		pospTransInfo.setPersonalid(null);
		// 卡号
		pospTransInfo.setCrdNm(null);
		// 设置冲正标志 0-正常交易，1-冲正交易，2-被冲正交易
		pospTransInfo.setCancelflag(0);
		// 设置冻结状态
		pospTransInfo.setFreezeState(null);
		// 设置终端序列号
		pospTransInfo.setPossn(null);
		// 设置服务网点条件码
		pospTransInfo.setConuterconditioncode(null);
		// 是否App交易
		pospTransInfo.setIsapp(1);
		// 设置支付方式
		pospTransInfo.setPaymentType(pmsAppTransInfo.getPaymentcode());
		// 设置批次号
		pospTransInfo.setBatno(null);
		// O单编号
		pospTransInfo.setoAgentNo(pmsAppTransInfo.getoAgentNo());

		return pospTransInfo;
	}
	public int UpdatePmsMerchantInfo(OriginalOrderInfo originalInfo, String aa) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}
}