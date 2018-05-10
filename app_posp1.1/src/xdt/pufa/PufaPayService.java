package xdt.pufa;


import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import xdt.model.PmsBusinessInfo;
import xdt.model.PospRouteInfo;
import xdt.model.PospTransInfo;
import xdt.pufa.base.Body;
import xdt.pufa.base.Head;
import xdt.pufa.base.PufaFieldDefine;
import xdt.pufa.base.Root;
import xdt.util.BeanToMapUtil;
import xdt.util.UtilDate;
import xdt.util.XMLUtil;

/**
 * 暂时不管缺失报文情况
 * 
 * @author ttq
 *
 */
@Service("pufaPayService")
public class PufaPayService extends BaseServiceAbstract {
	private static Logger logger = Logger.getLogger(PufaPayService.class);
//	private static XMLUtil  xmlUtil = new XMLUtil();
	private static BeanToMapUtil beanUtil = new BeanToMapUtil();
	
	
	
	//数据记录相关
	
	/**
	 * 扫码支付
	 * 
	 * Map request;
	 * 
	 * @return
	 */
	public  Map<String, Object> createMerpay(Map<String, Object> req) {
		
		//假设路由是上面选择的
		//响应
		Map<String, Object> response = new HashMap<String, Object>();
		//请求
		Map<String, Object> request  = new HashMap<String, Object>();
		
		//------------------------------------------------------------->>>>
		//查找路由
		PospRouteInfo route = route(req, response);
		PmsBusinessInfo busInfo= new PmsBusinessInfo();
		request.put("route", route);
		
		//根据路由查询通道商户
		try {
			busInfo = pmsBusinessInfoDao.searchById(route.getMerchantId().toString());
			request.put("busInfo", busInfo);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//-------------------------------------------------------------<<<<
		
		PospTransInfo transInfo = new PospTransInfo();
		
		logger.info("----扫码支付----构建浦发交易报文 start");
		
		String pospsn =getOrderNum();
		
		//---------------------------------------------------------->>>>这段不一定要
		//交易代码
		request.put(PufaFieldDefine.PF_HEAD_TRAN_CD, req.get(PufaFieldDefine.PF_HEAD_TRAN_CD));//1131
		
		request.put(PufaFieldDefine.PF_HEAD_VERSION, "1.1");
		//产品代码
		request.put(PufaFieldDefine.PF_HEAD_PROD_CD, req.get(PufaFieldDefine.PF_HEAD_PROD_CD));//1151
		//业务代码
		request.put(PufaFieldDefine.PF_HEAD_BIZ_CD, req.get(PufaFieldDefine.PF_HEAD_BIZ_CD));//0000008-支付宝
		//交易时间
		request.put(PufaFieldDefine.PF_HEAD_TRAN_DT_TM, UtilDate.getOrderNum());
		//订单号
		request.put(PufaFieldDefine.PF_REQ_BODY_ORDER_ID, pospsn);
		//交易金额
		request.put(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT, req.get(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT));
		
		//----------------------------------------------------------<<<<
		
		//---------------------------------------------------------->>>>
		//获取路由表当前绑定可用路由数据
		// *步骤1 设置BODY --start*
		Body body =new Body();
		body.setOrder_id((String)request.get(PufaFieldDefine.PF_REQ_BODY_ORDER_ID));//前端订单号
//		body.setIns_id_cd((String)req.get(PufaFieldDefine.PF_REQ_BODY_INS_ID_CD));//机构号
		body.setIns_id_cd(busInfo.getChannelId().toString());//机构号
//		body.setMchnt_cd((String)req.get(PufaFieldDefine.PF_REQ_BODY_MCHNT_CD));//商户号
		body.setMchnt_cd(busInfo.getBusinessNum());//商户号
		body.setAuth_code((String)req.get(PufaFieldDefine.PF_REQ_BODY_AUTH_CODE));//授权码-扫码设备读取
		body.setTran_amt((String)req.get(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT));//交易金额-分为单位
		// *步骤1 设置BODY --end*
		
		
		// *步骤2 设置HEAD --start*
		Head head = new Head();
		head.setTran_cd((String)request.get(PufaFieldDefine.PF_HEAD_TRAN_CD));//交易代码-1131
		head.setVersion("1.1");//可选
		head.setBiz_cd((String)request.get(PufaFieldDefine.PF_HEAD_BIZ_CD));//业务代码0000008
		head.setProd_cd((String)request.get(PufaFieldDefine.PF_HEAD_PROD_CD));//产品代码-1151
		head.setTran_dt_tm((String)request.get(PufaFieldDefine.PF_HEAD_TRAN_DT_TM));//日期时间
		
		//----------------------------------------------------------<<<<
		
		
		Map headMap = beanUtil.convertBean(head);
		Map bodyMap = beanUtil.convertBean(body);
		// *步骤4 生成请求报文XML *
		String bodyxml = assemblerBodyData(bodyMap);
		
		
		//生成签名
		String sign = getSign(bodyxml);
		head.setSigned_str(sign);
		headMap.put("signed_str", sign);
		
		//组装交易报文
		String reqXml = assemblerData(headMap,bodyxml);
		// *步骤3 保存本次流水 *
		//插入流水
		transInfo = InsertJournal(request, req);
		// *步骤5 发送请求报文 XML *
		transInfo = getJourByUniqueKey(transInfo.getUniqueKey());
		//提交交易申请
		response = this.doCommunication(bodyMap ,reqXml);
		
		String resCode = "";
		if (response.get(PufaFieldDefine.PF_REQ_BODY_RET_CD).toString().endsWith("00")){
			 response.put(PufaFieldDefine.PF_REQ_BODY_RET_CD,"0000");
		}else{
			resCode = "PF" + response.get(PufaFieldDefine.PF_REQ_BODY_RET_CD).toString();
			response.put(PufaFieldDefine.PF_REQ_BODY_RET_CD,resCode);
		}
		
		

		// *步骤6  解析响应报文 XML *
		
		// *步骤7 更新表状态，向前传递 * 
		transInfo.setResponsecode(resCode);
		transInfo.setSysseqno((String) response.get(PufaFieldDefine.PF_REQ_BODY_SYS_ORDER_ID));
		transInfo.setRemark((String) response.get(PufaFieldDefine.PF_REQ_BODY_RET_MSG));
		try {
			transInfoDao.update(transInfo);
		} catch (Exception e) {
			logger.info("---更新流水错误---- ");
			req.put(PufaFieldDefine.PF_REQ_BODY_RET_CD, "99");
			e.printStackTrace();
			return req;
		}
		
		logger.info("----扫码支付----构建浦发交易报文 end");
		return response;
	}

	
	/**
	 * 扫码冲正
	 */
	public  Map<String, Object> flushes(Map<String, Object> req){
		
		//假设路由是上面选择的
		//响应
		Map<String, Object> response = new HashMap<String, Object>();
		//请求
		Map<String, Object> request  = new HashMap<String, Object>();
		
		PospTransInfo transInfo = new PospTransInfo();
		//本次订单号
		transInfo.setOrderId((String)req.get(PufaFieldDefine.PF_REQ_BODY_ORIG_ORDER_ID));
		
		PospTransInfo srcTransInfo = getSrcJour(transInfo);
		
		//判断交易是否存在
		
		//判断是否需要冲正
		
		//放入请求MAP
		request.put(SRC_JOUR, srcTransInfo);
		
		logger.info("----扫码退款----构建浦发交易报文 start");
		
		//----------------------------------------------------------这段不一定要
		String pospsn =getOrderNum();
		
		//---------------------------------------------------------->>>>这段不一定要
		//交易代码
		request.put(PufaFieldDefine.PF_HEAD_TRAN_CD, req.get(PufaFieldDefine.PF_HEAD_TRAN_CD));//4131
		
		request.put(PufaFieldDefine.PF_HEAD_VERSION, "1.1");
		//产品代码
		request.put(PufaFieldDefine.PF_HEAD_PROD_CD, req.get(PufaFieldDefine.PF_HEAD_PROD_CD));//1151
		//业务代码
		request.put(PufaFieldDefine.PF_HEAD_BIZ_CD, req.get(PufaFieldDefine.PF_HEAD_BIZ_CD));//0000008-支付宝
		//交易时间
		request.put(PufaFieldDefine.PF_HEAD_TRAN_DT_TM, UtilDate.getOrderNum());
		//订单号
		request.put(PufaFieldDefine.PF_REQ_BODY_ORDER_ID, pospsn);
		
		//订单号
//		request.put(PufaFieldDefine.PF_REQ_BODY_ORDER_ID, pospsn);//
		//交易金额
//		request.put(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT, req.get(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT));
		
		//----------------------------------------------------------
		
		// *步骤0 生成流水 --start*
		
		// *步骤0 生成流水 --end*
		
		//获取路由表当前绑定可用路由数据
		// *步骤1 设置BODY --start*
		Body body =new Body();
		body.setOrig_order_id(srcTransInfo.getPospsn());
//		body.setOrig_order_id(srcTransInfo.getSysseqno());
//		body.setOrder_id(srcTransInfo.getPospsn());//原前端订单号
//		body.setIns_id_cd(srcTransInfo.getChannelno());//机构号
//		body.setMchnt_cd(srcTransInfo.getBusinfo());//商户号
//		body.setAuth_code((String)req.get(PufaFieldDefine.PF_REQ_BODY_AUTH_CODE));//授权码-扫码设备读取
//		body.setTran_amt((String)req.get(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT));//交易金额-分为单位
//		body.setTran_amt(srcTransInfo.getTransamt().toString());//交易金额-分为单位
//		body.setRefund_reason((String)request.get(PufaFieldDefine.PF_REQ_BODY_REFUND_REASON));
		
		// *步骤1 设置BODY --end*
		
		
		// *步骤2 设置HEAD --start*
		Head head = new Head();
		head.setTran_cd((String)request.get(PufaFieldDefine.PF_HEAD_TRAN_CD));//交易代码4131
		head.setVersion("1.1");//可选1.1
		head.setBiz_cd((String)request.get(PufaFieldDefine.PF_HEAD_BIZ_CD));//业务代码0000008
		head.setProd_cd((String)request.get(PufaFieldDefine.PF_HEAD_PROD_CD));//产品代码1151
		head.setTran_dt_tm((String)request.get(PufaFieldDefine.PF_HEAD_TRAN_DT_TM));//日期时间
		// *步骤2 设置HEAD --end*
		
		
		
		// *步骤3 保存本次流水 *
		
		
		// *步骤4 生成请求报文XML *
		
		
		// *步骤5 发送请求报文 XML *
		
		// *步骤6  解析响应报文 XML *
		
		// *步骤7 更新表状态，向前传递 * 
		
		Map headMap = beanUtil.convertBean(head);
		Map bodyMap = beanUtil.convertBean(body);
		
		String bodyxml = assemblerBodyData(bodyMap);
		
		
		//生成签名
		String sign = getSign(bodyxml);
		head.setSigned_str(sign);
		headMap.put("signed_str", sign);
		
		//组装交易报文
		String reqXml = assemblerData(headMap,bodyxml);
		
		//插入流水
		transInfo = InsertJournal(request, req);
		//再查一次
		transInfo = getJourByUniqueKey(transInfo.getUniqueKey());
		try {
			
			srcTransInfo.setCancelflag(2);//原流水成为被冲正或撤销流水
			srcTransInfo.setCancelid(transInfo.getId());//被哪条记录冲正
			int updateRet = transInfoDao.update(srcTransInfo);
		} catch (Exception e1) {
			logger.info("更新原流水失败");
			// TODO Auto-generated catch block
			e1.printStackTrace();
			req.put(PufaFieldDefine.PF_REQ_BODY_RET_CD,"CJ98");
			return req;
		}
		
		//提交交易申请
		response = this.doCommunication(bodyMap ,reqXml);
		
		String resCode = "";
		if (response.get(PufaFieldDefine.PF_REQ_BODY_RET_CD).toString().endsWith("00")){
			response.put(PufaFieldDefine.PF_REQ_BODY_RET_CD,"0000");
		}else{
			resCode = "PF" + response.get(PufaFieldDefine.PF_REQ_BODY_RET_CD).toString();
			response.put(PufaFieldDefine.PF_REQ_BODY_RET_CD,resCode);
		}
		
		

		// *步骤6  解析响应报文 XML *
		
		// *步骤7 更新表状态，向前传递 * 
		transInfo.setResponsecode(resCode);
		transInfo.setSysseqno((String) response.get(PufaFieldDefine.PF_REQ_BODY_SYS_ORDER_ID));
		transInfo.setRemark((String) response.get(PufaFieldDefine.PF_REQ_BODY_RET_MSG));
		try {
			transInfoDao.updateByUniqueKey(transInfo);
		} catch (Exception e) {
			logger.info("---更新流水错误---- ");
			req.put(PufaFieldDefine.PF_REQ_BODY_RET_CD, "CJ99");
			e.printStackTrace();
			return req;
		}
		
		logger.info("----扫码退款----构建浦发交易报文 end");
		return req;
	}
	
	
	/**
	 * 扫码退款
	 */
	public  Map<String, Object> refund(Map<String, Object> req){
		
		//假设路由是上面选择的
		//响应
		Map<String, Object> response = new HashMap<String, Object>();
		//请求
		Map<String, Object> request  = new HashMap<String, Object>();
		
		PospTransInfo transInfo = new PospTransInfo();
		
		transInfo.setOrderId((String)req.get(PufaFieldDefine.PF_REQ_BODY_ORIG_ORDER_ID));
		
		//查询原流水
		PospTransInfo srcTransInfo = getSrcJourJour(transInfo);
		
		//判断交易是否存在
		
		//判断是否需要冲正
		
		//放入请求MAP
		request.put(SRC_JOUR, srcTransInfo);
		
		logger.info("----扫码退款----构建浦发交易报文 start");
		
		//----------------------------------------------------------这段不一定要
		String pospsn =getOrderNum();
		
		//---------------------------------------------------------->>>>这段不一定要
		//交易代码
		request.put(PufaFieldDefine.PF_HEAD_TRAN_CD, req.get(PufaFieldDefine.PF_HEAD_TRAN_CD));//4131
		
		request.put(PufaFieldDefine.PF_HEAD_VERSION, "1.1");
		//产品代码
		request.put(PufaFieldDefine.PF_HEAD_PROD_CD, req.get(PufaFieldDefine.PF_HEAD_PROD_CD));//1151
		//业务代码
		request.put(PufaFieldDefine.PF_HEAD_BIZ_CD, req.get(PufaFieldDefine.PF_HEAD_BIZ_CD));//0000008-支付宝
		//交易时间
		request.put(PufaFieldDefine.PF_HEAD_TRAN_DT_TM, UtilDate.getOrderNum());
		//订单号
		request.put(PufaFieldDefine.PF_REQ_BODY_ORDER_ID, pospsn);
		//机构号
		request.put(PufaFieldDefine.PF_REQ_BODY_INS_ID_CD, srcTransInfo.getChannelno());
//		request.put(PufaFieldDefine.PF_REQ_BODY_ORIG_ORDER_ID, srcTransInfo.getPospsn());
		//订单号
//		request.put(PufaFieldDefine.PF_REQ_BODY_ORDER_ID, pospsn);//
		//交易金额
//		request.put(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT, req.get(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT));
		
		//----------------------------------------------------------
		
		// *步骤0 生成流水 --start*
		
		// *步骤0 生成流水 --end*
		
		//获取路由表当前绑定可用路由数据
		// *步骤1 设置BODY --start*
		Body body =new Body();
		body.setOrig_order_id(srcTransInfo.getPospsn());//退款订单号
//		body.setOrig_order_id((String)req.get(PufaFieldDefine.PF_REQ_BODY_ORIG_ORDER_ID));
		body.setOrder_id((String)request.get(PufaFieldDefine.PF_HEAD_TRAN_DT_TM));//前端订单号
		body.setIns_id_cd(srcTransInfo.getChannelno());//机构号
		body.setMchnt_cd(srcTransInfo.getBusinfo());//商户号
//		body.setAuth_code((String)req.get(PufaFieldDefine.PF_REQ_BODY_AUTH_CODE));//授权码-扫码设备读取
//		body.setTran_amt((String)req.get(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT));//交易金额-分为单位
//		body.setTran_amt(srcTransInfo.getTransamt().toString());//交易金额-分为单位
		int tranAmtInt = Integer.parseInt((String)req.get(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT));
		
		body.setTran_amt(String.format("%012d", tranAmtInt));//交易金额-分为单位
		body.setRefund_reason((String)req.get(PufaFieldDefine.PF_REQ_BODY_REFUND_REASON));
		
		// *步骤1 设置BODY --end*
		
		
		// *步骤2 设置HEAD --start*
		Head head = new Head();
		head.setTran_cd((String)request.get(PufaFieldDefine.PF_HEAD_TRAN_CD));//交易代码4131
		head.setVersion("1.1");//可选1.1
		head.setBiz_cd((String)request.get(PufaFieldDefine.PF_HEAD_BIZ_CD));//业务代码0000008
		head.setProd_cd((String)request.get(PufaFieldDefine.PF_HEAD_PROD_CD));//产品代码1151
		head.setTran_dt_tm((String)request.get(PufaFieldDefine.PF_HEAD_TRAN_DT_TM));//日期时间
		// *步骤2 设置HEAD --end*
		
		
		
		// *步骤3 保存本次流水 *
		
		
		// *步骤4 生成请求报文XML *
		
		
		// *步骤5 发送请求报文 XML *
		
		// *步骤6  解析响应报文 XML *
		
		// *步骤7 更新表状态，向前传递 * 
		
		Map headMap = beanUtil.convertBean(head);
		Map bodyMap = beanUtil.convertBean(body);
		
		String bodyxml = assemblerBodyData(bodyMap);
		
		
		//生成签名
		String sign = getSign(bodyxml);
		head.setSigned_str(sign);
		headMap.put("signed_str", sign);
		
		//组装交易报文
		String reqXml = assemblerData(headMap,bodyxml);
		
		//插入流水
		transInfo = InsertJournal(request, req);
		//再次查询生成的主键
		transInfo = getJourByUniqueKey(transInfo.getUniqueKey());
		try {
			
			srcTransInfo.setCancelflag(2);//原流水成为被冲正或撤销流水
			srcTransInfo.setCancelid(transInfo.getId());//被哪条记录冲正
			int updateRet = transInfoDao.update(srcTransInfo);
		} catch (Exception e1) {
			logger.info("更新原流水失败");
			// TODO Auto-generated catch block
			e1.printStackTrace();
			req.put(PufaFieldDefine.PF_REQ_BODY_RET_CD,"CJ98");
			return req;
		}
		
		//提交交易申请
		response = this.doCommunication(bodyMap ,reqXml);
		
		String resCode = "";
		if (response.get(PufaFieldDefine.PF_REQ_BODY_RET_CD).toString().endsWith("00")){
			response.put(PufaFieldDefine.PF_REQ_BODY_RET_CD,"0000");
		}else{
			resCode = "PF" + response.get(PufaFieldDefine.PF_REQ_BODY_RET_CD).toString();
			response.put(PufaFieldDefine.PF_REQ_BODY_RET_CD,resCode);
		}
		

		// *步骤6  解析响应报文 XML *
		
		// *步骤7 更新表状态，向前传递 * 
		transInfo.setResponsecode(resCode);
		transInfo.setSysseqno((String) response.get(PufaFieldDefine.PF_REQ_BODY_SYS_ORDER_ID));
		transInfo.setRemark((String) response.get(PufaFieldDefine.PF_REQ_BODY_RET_MSG));
		try {
			transInfoDao.updateByUniqueKey(transInfo);
		} catch (Exception e) {
			logger.info("---更新流水错误---- ");
			req.put(PufaFieldDefine.PF_REQ_BODY_RET_CD, "CJ99");
			e.printStackTrace();
			return req;
		}
		
		logger.info("----扫码退款----构建浦发交易报文 end");
		return req;
	}
	
	
	/**
	 * 扫码查询
	 * 就不要记住流水了
	 * Map request;
	 * 
	 * @return
	 */
	public  Map<String, Object> query(Map<String, Object> req) {
		
		//假设路由是上面选择的
		//响应
		Map<String, Object> response = new HashMap<String, Object>();
		//请求
		Map<String, Object> request  = new HashMap<String, Object>();
		
		
		PospTransInfo param = new PospTransInfo();
		param.setOrderId((String)req.get(PufaFieldDefine.PF_REQ_BODY_ORIG_ORDER_ID));
		//本次订单号
		PospTransInfo srcTransInfo = getSrcJourJour(param);
		
		if(null==srcTransInfo){
			System.out.println("kong ");
		}
		
		logger.info("----扫码查询----构建浦发交易报文 start");
		
		
		PospTransInfo transInfo = new PospTransInfo();
		
		logger.info("----扫码支付----构建浦发交易报文 start");
		
		String pospsn =getOrderNum();
		
		//----------------------------------------------------------这段不一定要
		//交易代码
		request.put(PufaFieldDefine.PF_HEAD_TRAN_CD, req.get(PufaFieldDefine.PF_HEAD_TRAN_CD));//5131
		
		request.put(PufaFieldDefine.PF_HEAD_VERSION, "1.1");
		//产品代码
		request.put(PufaFieldDefine.PF_HEAD_PROD_CD, req.get(PufaFieldDefine.PF_HEAD_PROD_CD));//1151
		//业务代码
		request.put(PufaFieldDefine.PF_HEAD_BIZ_CD, req.get(PufaFieldDefine.PF_HEAD_BIZ_CD));//"0000008"
		//交易时间
		request.put(PufaFieldDefine.PF_HEAD_TRAN_DT_TM, UtilDate.getOrderNum());
		//订单号
		request.put(PufaFieldDefine.PF_REQ_BODY_ORDER_ID, pospsn);//
		//交易金额
//		request.put(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT, req.get(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT));
		
		//----------------------------------------------------------
		
		// *步骤0 生成流水 --start*
		
		// *步骤0 生成流水 --end*
		
		//获取路由表当前绑定可用路由数据
		// *步骤1 设置BODY --start*
		Body body =new Body();
//		body.setOrig_order_id(srcTransInfo.getPospsn());
//		body.setOrig_order_id(srcTransInfo.getSysseqno());
		body.setOrder_id(srcTransInfo.getPospsn());
//		body.setOrig_order_id((String)req.get(PufaFieldDefine.PF_REQ_BODY_ORIG_ORDER_ID));
//		body.setOrder_id((String)request.get(PufaFieldDefine.PF_HEAD_TRAN_DT_TM));//前端订单号
//		body.setIns_id_cd((String)req.get(PufaFieldDefine.PF_REQ_BODY_INS_ID_CD));//机构号
//		body.setMchnt_cd((String)req.get(PufaFieldDefine.PF_REQ_BODY_MCHNT_CD));//商户号
//		body.setAuth_code((String)req.get(PufaFieldDefine.PF_REQ_BODY_AUTH_CODE));//授权码-扫码设备读取
//		body.setTran_amt((String)req.get(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT));//交易金额-分为单位
		// *步骤1 设置BODY --end*
		
		
		// *步骤2 设置HEAD --start*
		Head head = new Head();
		head.setTran_cd((String)req.get(PufaFieldDefine.PF_HEAD_TRAN_CD));//交易代码"5131"
		head.setVersion("1.1");//可选
		head.setBiz_cd((String)req.get(PufaFieldDefine.PF_HEAD_BIZ_CD));//业务代码"0000008"
		head.setProd_cd( (String)req.get(PufaFieldDefine.PF_HEAD_PROD_CD));//产品代码"1151"
		head.setTran_dt_tm((String)request.get(PufaFieldDefine.PF_HEAD_TRAN_DT_TM));//日期时间
		// *步骤2 设置HEAD --end*
		
		
		// *步骤3 保存本次流水 *
		
		
		// *步骤4 生成请求报文XML *
		
		
		// *步骤5 发送请求报文 XML *
		
		// *步骤6  解析响应报文 XML *
		
		// *步骤7 更新表状态，向前传递 * 
		
		Map headMap = beanUtil.convertBean(head);
		Map bodyMap = beanUtil.convertBean(body);
		
		String bodyxml = assemblerBodyData(bodyMap);
		
		
		//生成签名
		String sign = getSign(bodyxml);
		head.setSigned_str(sign);
		headMap.put("signed_str", sign);
		
		//组装交易报文
		String reqXml = assemblerData(headMap,bodyxml);
		
		//插入流水
		/*
		transInfo = InsertJournal(request, req);
		*/
		//提交交易申请
		response = this.doCommunication(bodyMap ,reqXml);
		
		String resCode = "";
		if (response.get(PufaFieldDefine.PF_REQ_BODY_RET_CD).toString().endsWith("00")){
			response.put(PufaFieldDefine.PF_REQ_BODY_RET_CD,"0000");
		}else{
			resCode = "PF" + response.get(PufaFieldDefine.PF_REQ_BODY_RET_CD).toString();
			response.put(PufaFieldDefine.PF_REQ_BODY_RET_CD,resCode);
		}
		
		

		// *步骤6  解析响应报文 XML *
		
		// *步骤7 更新表状态，向前传递 * 
		/*
		transInfo.setResponsecode(resCode);
		transInfo.setSysseqno((String) response.get(PufaFieldDefine.PF_REQ_BODY_SYS_ORDER_ID));
		transInfo.setRemark((String) response.get(PufaFieldDefine.PF_REQ_BODY_RET_MSG));
		try {
			transInfoDao.update(transInfo);
		} catch (Exception e) {
			logger.info("---更新流水错误---- ");
			req.put(PufaFieldDefine.PF_REQ_BODY_RET_CD, "99");
			e.printStackTrace();
			return req;
		}
		*/
		
		logger.info("----扫码查询----构建浦发交易报文 end");
		return response;
	}
	
	
	/**
	 * 扫码创建订单
	 * 
	 * 需要查询配合
	 */
	public  Map<String, Object> createUserPayOrder(Map<String, Object> req){
		//假设路由是上面选择的
		//响应
		Map<String, Object> response = new HashMap<String, Object>();
		//请求
		Map<String, Object> request  = new HashMap<String, Object>();
		
		//------------------------------------------------------------->>>>
		//查找路由
		PospRouteInfo route = route(req, response);
		request.put("route", route);
		PmsBusinessInfo busInfo= new PmsBusinessInfo();
		
		//根据路由查询通道商户
		try {
			busInfo = pmsBusinessInfoDao.searchById(route.getMerchantId().toString());
			request.put("busInfo", busInfo);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//-------------------------------------------------------------<<<<
		
		PospTransInfo transInfo = new PospTransInfo();
		
		logger.info("----扫码支付----构建浦发交易报文 start");
		
		String pospsn =getOrderNum();
		
		//---------------------------------------------------------->>>>这段不一定要
		//交易代码
		request.put(PufaFieldDefine.PF_HEAD_TRAN_CD, req.get(PufaFieldDefine.PF_HEAD_TRAN_CD));//7131
		
		request.put(PufaFieldDefine.PF_HEAD_VERSION, "1.1");
		//产品代码
		request.put(PufaFieldDefine.PF_HEAD_PROD_CD, req.get(PufaFieldDefine.PF_HEAD_PROD_CD));//1151
		//业务代码
		request.put(PufaFieldDefine.PF_HEAD_BIZ_CD, req.get(PufaFieldDefine.PF_HEAD_BIZ_CD));//0000008-支付宝
		//交易时间
		request.put(PufaFieldDefine.PF_HEAD_TRAN_DT_TM, UtilDate.getOrderNum());
		//订单号
		request.put(PufaFieldDefine.PF_REQ_BODY_ORDER_ID, pospsn);
		//交易金额
		request.put(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT, req.get(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT));
		
		//----------------------------------------------------------<<<<
		
		//---------------------------------------------------------->>>>
		//获取路由表当前绑定可用路由数据
		// *步骤1 设置BODY --start*
		Body body =new Body();
		body.setOrder_id((String)request.get(PufaFieldDefine.PF_REQ_BODY_ORDER_ID));//前端订单号
//		body.setIns_id_cd((String)req.get(PufaFieldDefine.PF_REQ_BODY_INS_ID_CD));//机构号
		body.setIns_id_cd(busInfo.getChannelId().toString());//机构号
//		body.setMchnt_cd((String)req.get(PufaFieldDefine.PF_REQ_BODY_MCHNT_CD));//商户号
		body.setMchnt_cd(busInfo.getBusinessNum());//商户号
//		body.setAuth_code((String)req.get(PufaFieldDefine.PF_REQ_BODY_AUTH_CODE));//授权码-扫码设备读取
		body.setTran_amt((String)req.get(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT));//交易金额-分为单位
		// *步骤1 设置BODY --end*
		
		
		// *步骤2 设置HEAD --start*
		Head head = new Head();
		head.setTran_cd((String)request.get(PufaFieldDefine.PF_HEAD_TRAN_CD));//交易代码-7131
		head.setVersion("1.1");//可选
		head.setBiz_cd((String)request.get(PufaFieldDefine.PF_HEAD_BIZ_CD));//业务代码0000008
		head.setProd_cd((String)request.get(PufaFieldDefine.PF_HEAD_PROD_CD));//产品代码-1151
		head.setTran_dt_tm((String)request.get(PufaFieldDefine.PF_HEAD_TRAN_DT_TM));//日期时间
		
		//----------------------------------------------------------<<<<
		
		
		Map headMap = beanUtil.convertBean(head);
		Map bodyMap = beanUtil.convertBean(body);
		// *步骤4 生成请求报文XML *
		String bodyxml = assemblerBodyData(bodyMap);
		
		
		//生成签名
		String sign = getSign(bodyxml);
		head.setSigned_str(sign);
		headMap.put("signed_str", sign);
		
		//组装交易报文
		String reqXml = assemblerData(headMap,bodyxml);
		// *步骤3 保存本次流水 *
		//插入流水
		transInfo = InsertJournal(request, req);
		// *步骤5 发送请求报文 XML *
		transInfo = getJourByUniqueKey(transInfo.getUniqueKey());
		//提交交易申请
		response = this.doCommunication(bodyMap ,reqXml);
		
		String resCode = "";
		if (response.get(PufaFieldDefine.PF_REQ_BODY_RET_CD).toString().endsWith("00")){
			response.put(PufaFieldDefine.PF_REQ_BODY_RET_CD,"0000");
		}else{
			resCode = "PF" + response.get(PufaFieldDefine.PF_REQ_BODY_RET_CD).toString();
			response.put(PufaFieldDefine.PF_REQ_BODY_RET_CD,resCode);
		}
		
		 
		// *步骤6  解析响应报文 XML *
		
		// *步骤7 更新表状态，向前传递 * 
		transInfo.setResponsecode(resCode);
		transInfo.setSysseqno((String) response.get(PufaFieldDefine.PF_REQ_BODY_SYS_ORDER_ID));
		transInfo.setRemark((String) response.get(PufaFieldDefine.PF_REQ_BODY_RET_MSG));
		try {
			transInfoDao.updateByUniqueKey(transInfo);
		} catch (Exception e) {
			logger.info("---更新流水错误---- ");
			req.put(PufaFieldDefine.PF_REQ_BODY_RET_CD, "99");
			e.printStackTrace();
			return req;
		}
		
		logger.info("----扫码支付----构建浦发交易报文 end");
		return response;
	}
	

	
	
	public static void main(String[] args) throws Exception, Throwable {
//		Map<String, Object> req=null;
//		req = pay(null);
//		System.out.println("请求报文："+req);
//		String resp = s.connServer(req);
//		System.out.println("响应信息："+resp);
//		jsonutil.getJsonMap(resp.substring(4));
		
		
	}


	@Override
	public Map<String, Object> doCommunication(Map<String, Object> map, String reqXml) {
		//是否需要验证签名
		boolean isSign = false;
		Map<String, Object> response=new HashMap<String, Object>();
		logger.info("---交易提交---- start");
		String res;
		try {
			res = connServer(reqXml);
			//校验不通过 则报错
			if(!checkSign(res)){
				
			}
			
			response = resXml2MAP(res);
		} catch (Exception e) {
			logger.info("---连接错误---- ");
			map.put(PufaFieldDefine.PF_REQ_BODY_RET_CD, "98");
			e.printStackTrace();
		
			
			return map;
		}
		
		
		//组装相应Map报文
		logger.info("---交易提交---- end");
		return response;
	}
	
	
	
	
	
	
	
}