package xdt.pufa;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import xdt.common.security.RSA;
import xdt.dao.IPmsBusinessInfoDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPospRouteInfoDAO;
import xdt.dao.IPospTransInfoDAO;
import xdt.dao.impl.PmsMerchantInfoDaoImpl;
import xdt.model.PmsBusinessInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PospRouteInfo;
import xdt.model.PospTransInfo;
import xdt.pufa.base.FieldDefine;
import xdt.pufa.base.PufaFieldDefine;
import xdt.util.DateUtil;
import xdt.util.UtilDate;
import xdt.util.XMLUtil; 


@Service
public abstract class BaseServiceAbstract {
	private static Logger logger = Logger.getLogger(BaseServiceAbstract.class);
	@Resource
	protected IPospTransInfoDAO transInfoDao ;
	@Resource
	private IPospRouteInfoDAO pospRouteInfoDao ;
	
	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao;
	
	@Resource
	IPmsBusinessInfoDao pmsBusinessInfoDao;
	
	public static final String CURRENT_JOUR = "current_jour";//当前流水，
	public static final String SRC_JOUR = "src_jour";//被流水
	
	/**
	 * 校验签名sha1
	 * @param xml
	 * @return
	 */
	protected static boolean checkSign(String xml){
		logger.info("----校验浦发响应交易报文签名----");
        String body = XMLUtil.getElement(xml, "BODY");
        
		String signHead = XMLUtil.getElementChild(xml, "HEAD", "signed_str");
		logger.info("浦发响应报文签名>>>>"+signHead);
		
		boolean sign =  xdt.common.security.RSA.doCheck(body, signHead);
		
		//校验通过
		 if(sign){
			 logger.info("校验浦发响应交易报文签名>>>> 通过");
			 return true;
		 }
		 
		 logger.info("校验浦发响应交易报文签名>>>> 签名不一致");
		 
		return false;
	}
	
	
	/**
	 * 生成签名sha1
	 * 可以生成一个签名
	 * 也可以放置一个签名
	 * @param reqMap
	 * @param xml
	 * @return
	 */
	protected static String getSign(String xml){
//        String body = XMLUtil.getElement(xml, "BODY");
		logger.info("----生成浦发请求交易报文签名----");
        String sign = RSA.sign(xml);
        logger.info("报文签名>>>>" + sign);
		return sign;
	}
	
	protected  String getOrderNum(){
//		String orderNum ="";
		//流水号
		int journo = transInfoDao.getJourno();
		String pospsn = "000000" + journo;
		pospsn = pospsn.substring(pospsn.length() - 6);
		return pospsn = UtilDate.getDate()+pospsn;
		
	}
	
	/**
	 * 简单查询路由 冲正、退货不能调用
	 * @param req
	 * @param rsp
	 * @return
	 */
    protected PospRouteInfo route(Map req,Map rsp){
    	
    	//获取请求中我方系统商户号
    	String merNo = req.get(FieldDefine.PF_REQ_BODY_MCHNT_CD).toString();
    	
    	PmsMerchantInfo info;
		try {
			info = pmsMerchantInfoDao.selectMercByMercId(merNo);
			
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
    
    
    /**
     * 查源流水 根据订单号
     * @param para
     * @return
     */
    public PospTransInfo getSrcJour(PospTransInfo para){
    	return transInfoDao.selectSrc(para);
    }
    
    public PospTransInfo getSrcJourJour(PospTransInfo para){
    	return transInfoDao.selectSrcJour(para);
    }
    
    public PospTransInfo getJourByUniqueKey(String para){
    	return transInfoDao.selectJourByUniqueKey(para);
    }
	
	
	/**
	 * req.set(MerchantAbstract.ROUTE_INFO, route);
						req.set(MerchantAbstract.RELATED_MERHANT, relatedMerchant);
						req.set(MerchantAbstract.RELATED_POS, relatedPos);
						req.set(MerchantAbstract.RELATED_CHANNEL, relatedChannel);
	 * @param req
	 * @param rsp
	 * @return
	 */
//	public Map<String , Object> routeMerchant(Map req,Map rsp){
//		
//		//获取请求中我方系统商户号
//    	String merNo = req.get(FieldDefine.PF_REQ_BODY_MCHNT_CD).toString();
//    	//根据交易号 ，判断是否要计算路由||判断是否有 req。get(SRC_JOUR)
//    	
//    	//冲正\退货不选路由，原路返回
//		
//		//获取商户信息
//		//查询当前商户是都为跳码商户
//		PmsMerchantInfo mercInfo = pmsMerchantInfoDao.selectMercByMercId(merNo);
//		if(mercInfo  == null){
//			//没有当前小商户，直接返回
//			logger.info( "没有当前这个商户：" + merNo);
//			return null;
//		}
//		
//		//获取交易类型 如果是消费类型才能跳码，否则按照原来的逻辑进行
//		String msgType = req.get(FieldDefine.PF_HEAD_TRAN_CD).toString();
//		String msgCode = req.get(FieldDefine.PF_HEAD_BIZ_CD).toString();
//		String code = msgType + msgCode;
//		
//		//所有使用通道的（含 状态上送，付款，冲正，撤销等），其中 正向交易（付款，冻结类）需要检查额度金额问题
//		List result = pospRouteInfoDao.queryMyAllRoutesExtra(merNo);//需要提取额外信息
//
//		List<PospRouteInfo> resultNew = new ArrayList<PospRouteInfo>();
//		PospRouteInfo needRoute = null;
//		for (Iterator it = result.iterator(); it.hasNext();) {
//			PospRouteInfo route = (PospRouteInfo) it.next();
//			
//			//判断当前路由是否赔钱
//			if(jour.getSearchtrancode().equals("0000000200")){
//				if(!judgeProfit(route,mercInfo,req.get(ISO8583Define.AF004_AmountOfTransactions).toString())){
//					continue;
//				}
//			}else{
//				GeneralLog.info("RouteMerchant", "不是消费交易");
//			}
//			
//			
//				if(route.getMerchantId() != null){
//					//检查统计信息
//					for (Iterator iterator = infoSubs.iterator(); iterator.hasNext();) {
//						PmsBusinessInfoSub infoSub = (PmsBusinessInfoSub)iterator.next();
//						
//						if(infoSub.getMerchantId().compareTo(route.getMerchantId()) == 0 ){
//							needInfoSub = infoSub;
//							break;
//						}
//					}
//				}
//				
//				if(needInfoSub != null){
//					
//				 	String time = needInfoSub.getTimePeriod();//yyyyMMddHHmmss
//				 	BigDecimal moneySum = needInfoSub.getMoneySum();
//				 	BigDecimal moneySumCus = needInfoSub.getMoneySumCus();
//				 	
//				 	Integer failures = needInfoSub.getFailures();
//				 	Integer failuresCus = needInfoSub.getFailuresCus();
//				 	
//				 	String format = "yyyyMMddHHmmss".substring(0, time.length());
//				 	
//				 	String currentTime = DateUtil.format(now,format);
//				 	if(currentTime.equals(time)){//不一致即为时间戳切换了，可以再次使用了
//				 		
//				 		if(failures != null){//没设置不判断
//					 		if(failuresCus.intValue() >= failures.intValue()){//失败次数超限
//					 			if(GeneralLog.isInfoEnabled("RouteMerchant"))
//					 				GeneralLog.info("RouteMerchant", "商户："+merNo+"路由："+route.getId()+" 失败次数:"+failuresCus.intValue()+" 超限："+failures.intValue()+",抛弃！");
//					 			
//					 			continue;
//					 		}
//				 		}
//				 		if(moneySum != null && ( jour!=null&&jour.getSearchtrancode().equals("0000000200") )){//没设置不判断 ，且 非付款也不统计金额
//					 		if( (moneySumCus.add( jour.getTransamt().divide(BigDecimal.valueOf(100)) )
//					 				.compareTo(moneySum) > 0 )){//交易总金额
//					 			if(GeneralLog.isInfoEnabled("RouteMerchant"))
//					 				GeneralLog.info("RouteMerchant", "商户："+merNo+"路由："+route.getId()+" 交易金额:"+jour.getTransamt().divide(BigDecimal.valueOf(100))+"(+"+moneySumCus+") 将超限："+moneySum +",抛弃！");
//					 			continue;
//					 		}
//				 		}
//				 		
//				 	}else{
//				 		//时间不一致，可以使用
//				 		GeneralLog.info("RouteMerchant", "时间戳切换了，可以再次使用了");
//				 	}
//				}
//			
//				
////					needRoute = route;
////					break;
//				if(!specileRemoveFlag){
//					resultNew.add(route);
//				}
//				
//			}else{
//				GeneralLog.info("RouteMerchant", "商户："+merNo+"路由："+route.getId()+"未在通道营业时间内"+from+"-"+to+"，抛弃");	
//				
//
//			}
//			
//		}
//		
//		//如果没有，怎么办（返回空）
//		{	
//			if(resultNew.size() == 0){
//				GeneralLog.info("RouteMerchant", "通道商户金额限制把路由干没了，直接返回失败");
//				rsp.set(SystemConstantDefine.SYS_VAR_NAME_ERR_CODE, new SystemError(SystemConstantDefine.SYS_ERRCODE_ROUTE_NO ));
//				return null;
//			}
//			
//			if(resultNew.size() > 0){
//				//这里随便找一条路由出来
//				Random r=new Random();
//				int rInt = r.nextInt(resultNew.size());
//				GeneralLog.info("RouteMerchant", "共获取到"+resultNew.size()+"条路由，随机拿出第"+rInt+"个");
//				needRoute = resultNew.get(rInt);
//			}
//			
//			
//			if(needRoute != null){
//				// 查找通道，商户，终端
//				PmsBusinessPos relatedPos = pmsBusinessPosDao.getPmsBusinessPos(needRoute.getPosId());
//				PmsBusinessInfo relatedMerchant = pmsBusinessInfoDao.getPmsBusinessInfo(needRoute.getMerchantId());
//				PmsChannelInfo relatedChannel = channelInfoDao.getPmsChannelInfoByPosID(needRoute.getPosId(), needRoute.getMerchantId());
//				
//				MerchantAbstract channelMerchant  = (MerchantAbstract) Assembler.getInstance("M" + needRoute.getChannelCode());
//				
////					MerchantAbstract channelMerchant = (MerchantAbstract) Assembler
////							.getInstance("M" + needRoute.getChannelCode());
//
//				// 是否要检查商户终端状态？ (不需要，路由信息 在调整通道的时候 一块调整)
//				if (channelMerchant != null
//				// && channelMerchant.isMerchantOpen()
//				) {
//					
//					GeneralLog.info("RouteMerchant", "商户："+merNo+"选择路由："+needRoute.getId());
//					
//					req.set(MerchantAbstract.ROUTE_INFO, needRoute);
//					req.set(MerchantAbstract.RELATED_MERHANT, relatedMerchant);
//					req.set(MerchantAbstract.RELATED_POS, relatedPos);
//					req.set(MerchantAbstract.RELATED_CHANNEL, relatedChannel);
//					if(needInfoSub != null)
//						req.set(MerchantAbstract.RELATED_MERHANT_SUB, needInfoSub);
//					return channelMerchant;
//				}else{
//					GeneralLog.info("RouteMerchant", "未找到[M" + needRoute.getChannelCode() +"]通道配置文件");
//				}
//			}
//		}
//			
//		
//		rsp.set(SystemConstantDefine.SYS_VAR_NAME_ERR_CODE, new SystemError(SystemConstantDefine.SYS_ERRCODE_ROUTE_NO ));
//		
//		return null;
//    }
	
	/**
	 * 提交交易
	 * @param map
	 * @return
	 */
	protected abstract  Map<String,Object> doCommunication(Map<String,Object> map, String reqXml);
	
	
	protected String connServer(String info) {
		
		long start = System.currentTimeMillis();
		
		logger.info("----连接socket服务器----");
		byte[] reqpack = null;
		byte[] resppack = null;//带长度回应
		byte[] resppacks = null;//去除长度的回应
		try {
			PufaSokcetClient cl = new PufaSokcetClient();
			byte[] reqmsg = info.getBytes("GBK");
			int reqlen = reqmsg.length;
			String len = "0000" + reqlen;
			len = len.substring(len.length() - 4);
			
			reqpack = new byte[4 + reqlen];
			//将报文长度添加至顶部
			System.arraycopy(len.getBytes("GBK"), 0, reqpack, 0, 4);
			System.arraycopy(reqmsg, 0, reqpack, 4, reqlen);
			
			logger.info("request data:"+new String(reqpack));
			resppack = cl.connServer(reqpack);
			logger.info("response data:"+new String(resppack, "gbk"));
			//去掉报文长度
			int resplen = resppack.length-4;
			resppacks =new byte[resplen];
			//复制到新数组中
			System.arraycopy(resppack, 4, resppacks, 0, resplen);
			logger.info("response data remove length str:"+new String(resppacks, "gbk"));
			
			long end = System.currentTimeMillis();
			logger.info("通讯耗时："+ (end -start));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (resppack.length > 0)
			return LoUtils.asciiToString(resppacks);
		else
			return null;
	}
	
	
	/**
	 * 可尝试DOM 顺序写顺序读
	 * @param head
	 * @param body
	 * @return
	 */
	protected static String assemblerData(Map head, String body){
		StringBuffer sbuff = new StringBuffer();
		
		sbuff.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
		
		sbuff.append("<ROOT>");
		
		sbuff.append("<HEAD>");
		sbuff.append("<tran_cd>");
		sbuff.append(getMapValue(head, "tran_cd"));
		sbuff.append("</tran_cd>");
		
		
		sbuff.append("<version>");
		sbuff.append(getMapValue(head, "version"));
		sbuff.append("</version>");
		
		
		sbuff.append("<prod_cd>");
		sbuff.append(getMapValue(head, "prod_cd"));
		sbuff.append("</prod_cd>");
		
		
		sbuff.append("<biz_cd>");
		sbuff.append(getMapValue(head, "biz_cd"));
		sbuff.append("</biz_cd>");
		
		
		sbuff.append("<tran_dt_tm>");
		sbuff.append(getMapValue(head, "tran_dt_tm"));
		sbuff.append("</tran_dt_tm>");
		
		
		sbuff.append("<signed_str>");
		sbuff.append(getMapValue(head, "signed_str"));
		sbuff.append("</signed_str>");
		
		
		sbuff.append("</HEAD>");
		
		sbuff.append(body);
		
	    sbuff.append("</ROOT>");
	    
	    
		return sbuff.toString();
	}
	
	/**
	 * 按顺序 不需要上送的就不要赋值 顺序写顺序读
	 * @param bodyMap
	 * @return
	 */
	protected static String assemblerBodyData(Map<String, String> bodyMap){
		StringBuffer sbuff = new StringBuffer();
		
		
		sbuff.append("<BODY>");
		if(!StringUtils.isEmpty(bodyMap.get("order_id"))){
			sbuff.append("<order_id>");
			sbuff.append(getMapValue(bodyMap, "order_id"));
			sbuff.append("</order_id>");
		}
		if(!StringUtils.isEmpty(bodyMap.get("orig_order_id"))){
		    sbuff.append("<orig_order_id>");
		    sbuff.append(getMapValue(bodyMap, "orig_order_id"));
		    sbuff.append("</orig_order_id>");
		}

		if(!StringUtils.isEmpty(bodyMap.get("ins_id_cd"))){
			sbuff.append("<ins_id_cd>");
			sbuff.append(getMapValue(bodyMap, "ins_id_cd"));
			sbuff.append("</ins_id_cd>");
		}
		
		if(!StringUtils.isEmpty(bodyMap.get("mchnt_cd"))){
			sbuff.append("<mchnt_cd>");
			sbuff.append(getMapValue(bodyMap, "mchnt_cd"));
			sbuff.append("</mchnt_cd>");
		}
		
		
		if(!StringUtils.isEmpty(bodyMap.get("auth_code"))){
		    sbuff.append("<auth_code>");
			sbuff.append(getMapValue(bodyMap, "auth_code"));
			sbuff.append("</auth_code>");
		}
		
		if(!StringUtils.isEmpty(bodyMap.get("tran_amt"))){
		    sbuff.append("<tran_amt>");
		    sbuff.append(getMapValue(bodyMap, "tran_amt"));
		    sbuff.append("</tran_amt>");
		}
	    
		if(!StringUtils.isEmpty(bodyMap.get("refund_reason"))){
		    sbuff.append("<refund_reason>");
		    sbuff.append(getMapValue(bodyMap, "refund_reason"));
		    sbuff.append("</refund_reason>");
		}

		if(!StringUtils.isEmpty(bodyMap.get("sys_order_id"))){
		    sbuff.append("<sys_order_id>");
		    sbuff.append(getMapValue(bodyMap, "sys_order_id"));
		    sbuff.append("</sys_order_id>");
		}
	    
		if(!StringUtils.isEmpty(bodyMap.get("ret_cd"))){
		    sbuff.append("<ret_cd>");
		    sbuff.append(getMapValue(bodyMap, "ret_cd"));
		    sbuff.append("</ret_cd>");
		}
		
		if(!StringUtils.isEmpty(bodyMap.get("ret_msg"))){
		    sbuff.append("<ret_msg>");
		    sbuff.append(getMapValue(bodyMap, "ret_msg"));
		    sbuff.append("</ret_msg>");
		}
		
		if(!StringUtils.isEmpty(bodyMap.get("ret_msg"))){
		    sbuff.append("<buyer_user>");
		    sbuff.append(getMapValue(bodyMap, "buyer_user"));
		    sbuff.append("</buyer_user>");
	    }
	    
		if(!StringUtils.isEmpty(bodyMap.get("retry_flag"))){
		    sbuff.append("<retry_flag>");
		    sbuff.append(getMapValue(bodyMap, "retry_flag"));
		    sbuff.append("</retry_flag>");
		}
		if(!StringUtils.isEmpty(bodyMap.get("pay_time"))){
		    sbuff.append("<pay_time>");
		    sbuff.append(getMapValue(bodyMap, "pay_time"));
		    sbuff.append("</pay_time>");
		}
		
	    sbuff.append("</BODY>");
	    
		return sbuff.toString();
	}
	
	
	protected static String getMapValue(Map<String, String> map, String key){
		if(StringUtils.isEmpty(map.get(key))){
			return "";
		}else {
			return map.get(key);
		}
	}
	
	/**
	 * 准备请求对象
	 * @return
	 */
	protected Map<String, Object> prepareRequestMap(Map<String, Object> req){
		
		Map<String, Object> request= new HashMap<String, Object>();
		
		request.put(PufaFieldDefine.PF_HEAD_TRAN_DT_TM, UtilDate.getOrderNum());
		
		
		
		int transid =0;
		String pospsn ="";
		try {
			transid = transInfoDao.getNextTransid();
			pospsn = UtilDate.getDate()+transid;
		} catch (Exception e1) {
			logger.info("获取交易流水号失败");
			e1.printStackTrace();
		}
		request.put(PufaFieldDefine.PF_REQ_BODY_ORDER_ID, pospsn);
		return null;
	}

	/**
	 * 将响应的部分转为MAP
	 * @param xml
	 * @return
	 */
   public static  Map<String, Object> resXml2MAP(String xml){
    	Map<String, Object> ret=  new HashMap<String, Object>() ;
    	Document doc;
		try {
			doc = DocumentHelper.parseText(xml);
			Map<String, Object> map = XMLUtil.Dom2Map(doc);
			Map<String, Object> HEAD = (Map<String, Object>) map.get("HEAD");
			Map<String, Object> BODY = (Map<String, Object>) map.get("BODY");
			ret.putAll(HEAD);
			ret.putAll(BODY);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;

		
    }
	
	
	/**
	 * 录入交易流水 并记算费率
	 */
	protected PospTransInfo InsertJournal(Map<String, Object> req,Map<String, Object> myReq) {
		logger.info("----插入流水开始----");
		PospTransInfo jour = new PospTransInfo();
//		int transid=0;
//		try {
//			transid = transInfoDao.getNextTransid();
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		jour.setId(transid);
		//设置唯一key
		jour.setUniqueKey(UUID.randomUUID().toString().replace("-", ""));
		
		if(null!=req.get(SRC_JOUR)){
			PospTransInfo srcTransInfo = (PospTransInfo) req.get(SRC_JOUR);
			// 交易配置项
			jour.setCancelflag(1);// 0:正常交易，1：冲正交易，2：被冲正交易
			jour.setCancelid(srcTransInfo.getId());
		}else{
			jour.setCancelflag(0);// 0:正常交易，1：冲正交易，2：被冲正交易
//			jour.setCancelid(srcTransInfo.getId());
		}
		

//		jour.setTranscode("");
//		jour.setTerminalsn("");
//		jour.setPossn("");
//		jour.setPosterminalid("");
//		jour.setPospservicecode("");
		
		//---------------------------------------------------
		//交易日期和时间
		String transDateTime = (String) req.get(PufaFieldDefine.PF_HEAD_TRAN_DT_TM);
		jour.setTransdate(transDateTime.substring(0, 8));
		jour.setTranstime(transDateTime.substring(8));
		String senddate =UtilDate.getDateFormatter();
		// 取报文上送时间
		jour.setSenddate(new Date());
		
		if(!StringUtils.isEmpty(req.get(PufaFieldDefine.PF_HEAD_TRAN_CD))){
			jour.setTranscode((String) req.get(PufaFieldDefine.PF_HEAD_TRAN_CD));
		}
		
		//机构号
		if(!StringUtils.isEmpty(myReq.get(PufaFieldDefine.PF_REQ_BODY_INS_ID_CD))){
			jour.setChannelno((String) req.get(PufaFieldDefine.PF_REQ_BODY_INS_ID_CD));
		}
		
		//通道商户号
		if(!StringUtils.isEmpty(req.get(PufaFieldDefine.PF_REQ_BODY_MCHNT_CD))){
			jour.setBusinfo((String) req.get(PufaFieldDefine.PF_REQ_BODY_MCHNT_CD));//BUSINFO
		}
		
		//自身商户号
		if(!StringUtils.isEmpty(myReq.get(PufaFieldDefine.PF_REQ_BODY_MCHNT_CD))){
			jour.setMerchantcode((String) myReq.get(PufaFieldDefine.PF_REQ_BODY_MCHNT_CD));
		}
		
		//自身订单号
		if(!StringUtils.isEmpty(myReq.get(PufaFieldDefine.PF_REQ_BODY_ORDER_ID))){
			jour.setOrderId((String) myReq.get(PufaFieldDefine.PF_REQ_BODY_ORDER_ID));
		}
		
		if(!StringUtils.isEmpty(req.get(PufaFieldDefine.PF_HEAD_BIZ_CD))){
			//3131+1151+0000008
			String stcode=  req.get(PufaFieldDefine.PF_HEAD_TRAN_CD).toString()
					+ req.get(PufaFieldDefine.PF_HEAD_PROD_CD).toString()
					+ req.get(PufaFieldDefine.PF_HEAD_BIZ_CD).toString();
			jour.setSearchTransCode(stcode);
		}
		
		//交易金额
		
		if(!StringUtils.isEmpty(req.get(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT))){
			jour.setTransamt(new BigDecimal((String)req.get(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT) ));
		}
		
		if(!StringUtils.isEmpty(req.get(PufaFieldDefine.PF_REQ_BODY_ORDER_ID))){
			jour.setPospsn((String)req.get(PufaFieldDefine.PF_REQ_BODY_ORDER_ID));
		}
		
		
		//支付方式 :1 账号（余额）支付、2 百度支付、3 微信支付、4 支付宝支付
		String payType = req.get(PufaFieldDefine.PF_HEAD_BIZ_CD).toString();
		//支付宝0000008
		if(payType.equals("0000008")){
			payType = "4";
		}else{
			payType = "3";
		}
		jour.setPaymentType(payType);//--
		
		if(!StringUtils.isEmpty(req.get(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT))){
			jour.setSysseqno((String) req.get(PufaFieldDefine.PF_REQ_BODY_SYS_ORDER_ID));
		}
		
		PospRouteInfo route = (PospRouteInfo) req.get("route");
		if(null!=req.get("route")){
			jour.setRouteid(route.getId().intValue());
		}
		
		PmsBusinessInfo busInfo = (PmsBusinessInfo) req.get("busInfo");
		if(null!=req.get("busInfo")){
//			jour.setChannelno(busInfo.getChannelId());
			jour.setBusinfo(busInfo.getBusinessNum());
			jour.setChannelno(busInfo.getChannelId());
		}
		jour.setResponsecode("999");
		
//		if(!StringUtils.isEmpty(req.get(PufaFieldDefine.PF_REQ_BODY_SYS_ORDER_ID))){
//			jour.setTransOrderId((String) req.get(PufaFieldDefine.PF_REQ_BODY_SYS_ORDER_ID));
//		}
		
		
//		jour.setRemark("");
//		jour.setPayTimes("");
		//---------------------------------------------------
//		countFee(req,jour);    //进入计算费率
		
		
		try {
			transInfoDao.insert(jour);
			logger.info("----插入流水成功----");
		} catch (Exception e) {
			logger.info("----插入流水失败----");
			e.printStackTrace();
		}
		
		return jour;
	}

}
