/**
 * 
 */
package xdt.controller;

import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import xdt.dto.BaseUtil;
import xdt.dto.SubmitOrderNoCardPayResponseDTO;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PospTransInfo;
import xdt.quickpay.hengfeng.comm.Constant;
import xdt.quickpay.hengfeng.entity.PayQueryRequestEntity;
import xdt.quickpay.hengfeng.entity.PayQueryResponseEntity;
import xdt.quickpay.hengfeng.entity.PayRequestEntity;
import xdt.quickpay.hengfeng.entity.PayResponseEntity;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hengfeng.util.HFSignUtil;
import xdt.quickpay.hengfeng.util.HFUtil;
import xdt.quickpay.hengfeng.util.PreSginUtil;
import xdt.service.HfQuickPayService;
import xdt.util.HttpURLConection;

import com.google.gson.Gson;

/**
 * @ClassName: HFQuickPayAction
 * @Description: 恒丰 快捷支付
 * @author LiShiwen
 * @date 2016年6月14日 下午1:43:10
 *
 */
@Controller
@RequestMapping("quick")
public class HFQuickPayAction {

	/**
	 * 日志记录
	 */
	private Logger log = Logger.getLogger(HFQuickPayAction.class);

	@Resource
	private HfQuickPayService payService;

	/**
	 * 和上游交互
	 * 
	 * @param param
	 *            支付信息
	 * @param request
	 *            请求对象
	 * @param response
	 *            响应对象
	 * @throws Exception
	 * 
	 */
	@RequestMapping(value = "pay")
	public void pay(PayRequestEntity temp, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 原始数据交易id
		String originalOrderId = temp.getTransactionid();

		PayRequestEntity param = new PayRequestEntity();// 上送参数

		// 所有的流程通过 就发起支付 上送数据
		String json = payService.payHandle(temp);

		SubmitOrderNoCardPayResponseDTO respDto = new Gson().fromJson(json, SubmitOrderNoCardPayResponseDTO.class);

		log.info("支付…………");
		
		log.info("支付上送原始信息");
		
		log.info(temp);
		
		if (0 != respDto.getRetCode()) {
			PayResponseEntity resp = new PayResponseEntity();
			resp.setPaytype(temp.getPaytype());
			resp.setBankid(temp.getBankid());
			resp.setPid(temp.getPid());
			resp.setTransactionid(temp.getTransactionid());
			resp.setOrdertime(temp.getOrdertime());
			resp.setOrderamount(temp.getOrderamount());
			resp.setPayamount(temp.getOrderamount());
			resp.setErrcode(respDto.getRetCode() + "");
			// 返回页面参数
			Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
			response.sendRedirect(temp.getPageurl()+ "?" + queryUtil.bean2QueryStr(resp));
		} else {

			
			OriginalOrderInfo queryWhere=new OriginalOrderInfo();
			queryWhere.setMerchantOrderId(originalOrderId);
			queryWhere.setPid(temp.getPid());
			
			// 上送原始记录信息 
			OriginalOrderInfo originInfo = payService.selectByOriginal(queryWhere);
			// 本地订单id
			String orderId = originInfo.getOrderId();
			// 流水信息
			PospTransInfo transinfo = payService.getTransInfo(orderId);
			// 上送订单id
			String transOrderId = transinfo.getTransOrderId();
			// 设置上送信息
			param.setPid(Constant.MERCHANT_NO);
			param.setTransactionid(transOrderId);
			param.setOrderamount(originInfo.getOrderAmount());
			param.setOrdertime(originInfo.getOrderTime());
			param.setProductname(originInfo.getProcdutName());
			param.setProductnum(originInfo.getProcdutNum());
			param.setProductdesc(originInfo.getProcdutDesc());
			param.setBankid(originInfo.getBankId());
			param.setPaytype(originInfo.getPayType());
			param.setBankno(originInfo.getBankNo());
			param.setPageurl(BaseUtil.url+"quick/bgPayResult.action");

			// 生成签名
			String signmsg = HFUtil.sign(param,Constant.MERCHANT_KEY);

			param.setBgurl(BaseUtil.url+"/quick/pagePayResult.action");
			log.info("生成签名：" + signmsg);

			param.setSignmsg(signmsg);

			Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
			
			// 设置转发页面
			String path=Constant.SUBMIT_URL + "?" + bean2Util.bean2QueryStr(param);
			log.info("重定向 第三方："+path);
			response.sendRedirect(path.replace(" ", ""));
		}
	}

	/**
	 * 和上游交互 支付完成后同步返回支付结果
	 * 
	 * @param request
	 *            requet对象
	 * @param response
	 *            response对象
	 * @param temp
	 *            银联返回的数据
	 * @throws Exception
	 */
	@RequestMapping(value = "bgPayResult")
	public void payResult(HttpServletRequest request, HttpServletResponse response, PayResponseEntity temp)
			throws Exception {

		log.info("支付结果信息：" + temp);
		log.info("请求参数：" + request.getQueryString());

		// 处理这笔交易 修改订单表中的交易表
		payService.otherInvoke(temp);
		// 交易id
		String tranId = temp.getTransactionid();
		// 查询商户上送原始信息
		OriginalOrderInfo originalInfo = payService.getOriginOrderInfo(tranId);

		// 替换成下游商户的
		temp.setTransactionid(originalInfo.getMerchantOrderId());
		temp.setOrdertime(originalInfo.getOrderTime());
		temp.setPid(originalInfo.getPid());
		
		HFSignUtil sign=new HFSignUtil();
		
		
		String str=PreSginUtil.payBgResultString(temp);
		
		ChannleMerchantConfigKey cmkey=payService.getChannelConfigKey(temp.getPid());
		
		if(cmkey!=null){
			
			String signData=sign.sign(str, cmkey.getMerchantkey());
			
			temp.setSignmsg(signData);
			
		}
		
		
		Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
		// 给下游主动返回支付结果
		
		String path="";
		
		if(originalInfo.getBgUrl().indexOf("?")==-1){
			
			path=originalInfo.getBgUrl() + "?" + bean2Util.bean2QueryStr(temp);
		}else{
			
			path=originalInfo.getBgUrl() + "&" + bean2Util.bean2QueryStr(temp);
		}
		
		log.info("bgUrl 平台服务器重定向："+path);
        response.sendRedirect(path.replace(" ", ""));

//        if("10".equals(temp.getPayresult()))
//        {
//    		if(originalInfo.getPageUrl().indexOf("?")==-1){
//    			
//    			 path=originalInfo.getPageUrl() + "?" + bean2Util.bean2QueryStr(temp);
//    			log.info("pageUrl 商户页面 重定向："+path);
//    		   	 String result =HttpURLConection.httpURLConectionGET(path.replace(" ", ""), "UTF-8");
//    			 if(!StringUtils.isEmpty(result)){
//    					 PrintWriter print= response.getWriter();
//    					 print.write(result);
//    					 print.flush();
//    					 print.close();
//    			 }
//    		}else{
//    		    log.info("pageUrl 商户页面 重定向："+originalInfo.getPageUrl());
//    			path=originalInfo.getPageUrl() + "&" +  bean2Util.bean2QueryStr(temp);
//    			log.info("pageUrl 商户页面 重定向："+path);
//    		   	 String result =HttpURLConection.httpURLConectionGET(path.replace(" ", ""), "UTF-8");
//    			 if(!StringUtils.isEmpty(result)){
//    					 PrintWriter print= response.getWriter();
//    					 print.write(result);
//    					 print.flush();
//    					 print.close();
//    			 }
//    		}
//        }
		
		log.info("向下游 发送数据成功");

	}

	/**
	 * 和上游交互 支付完成后 手动返回支付结果
	 * 
	 * @param request
	 *            请求对象
	 * @param response
	 *            响应对象
	 * @param temp
	 *            订单信息
	 * @throws Exception
	 */
	@RequestMapping(value = "pagePayResult")
	public void payBgResult(HttpServletRequest request, HttpServletResponse response, PayResponseEntity temp)
			throws Exception {
		// 交易id
		String tranId = temp.getTransactionid();

		// 查询商户上送原始信息
		OriginalOrderInfo originalInfo = payService.getOriginOrderInfo(tranId);

		// 替换成下游商户的
		temp.setTransactionid(originalInfo.getMerchantOrderId());
		temp.setOrdertime(originalInfo.getOrderTime());
		temp.setPid(originalInfo.getPid());


		HFSignUtil sign=new HFSignUtil();
		
		
		String str=PreSginUtil.payBgResultString(temp);
		
		ChannleMerchantConfigKey cmkey=payService.getChannelConfigKey(temp.getPid());
		
		if(cmkey!=null){
			
			String signData=sign.sign(str, cmkey.getMerchantkey());
			
			temp.setSignmsg(signData);
			
		}
		
		Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();

		//给下游手动返回支付结果
		if(originalInfo.getPageUrl().indexOf("?")==-1){
			
			String path=originalInfo.getPageUrl() + "?" + bean2Util.bean2QueryStr(temp);
			log.info("pageUrl 商户页面 重定向："+path);
			response.sendRedirect(path.replace(" ", ""));
		}else{
		    log.info("pageUrl 商户页面 重定向："+originalInfo.getPageUrl());
			String path=originalInfo.getPageUrl() + "&" +  bean2Util.bean2QueryStr(temp);
			log.info("pageUrl 商户页面 重定向："+path);
			response.sendRedirect(path.replace(" ", ""));
		}
		

	}

	/**
	 * 查询支付结果
	 * 
	 * @param request
	 *            HttpServletRequest对象
	 * @param response
	 *            HttpServletResponse对象
	 * @param queryInfo
	 *            查询信息
	 * @throws Exception
	 */
	@RequestMapping("queryPayResult")
	public void queryPayResult(HttpServletRequest request, HttpServletResponse response,
			PayQueryRequestEntity queryInfo) throws Exception {
		log.info("查询支付结果："+queryInfo);
		String jsonStr = "";
		// 商户key
		String merchantkey = payService.getChannelConfigKey(queryInfo.getMerId()).getMerchantkey();
		Gson gson = new Gson();
		SubmitOrderNoCardPayResponseDTO responseDTO = new SubmitOrderNoCardPayResponseDTO();
		
		HFSignUtil signUtil = new HFSignUtil();
		if (!signUtil.verify(PreSginUtil.payQuerySignString(queryInfo), queryInfo.getSignData(), merchantkey)) {
			responseDTO.setRetCode(11);
			responseDTO.setRetMessage("签名错误");
			jsonStr = gson.toJson(responseDTO);
		} else {
			log.info("给下游调用 查询结果");
			String tranId = queryInfo.getTransactionId();
			log.info("查询商户上送原始信息");
			OriginalOrderInfo queryWhere=new OriginalOrderInfo();
			queryWhere.setMerchantOrderId(tranId);
			queryWhere.setPid(queryInfo.getMerId());
			log.info("查询原始订单"+queryWhere);
			log.info("快捷支付业务类"+payService);
			OriginalOrderInfo originalInfo = payService.selectByOriginal(queryWhere);
			
			log.info("原始数据 订单表关联id");
			String orderId = originalInfo.getOrderId();
			log.info("查询流水信息");
			PospTransInfo transInfo = payService.getTransInfo(orderId);
			
			if(transInfo==null){
				responseDTO.setRetCode(11);
				responseDTO.setRetMessage("流水不存在");
				jsonStr = gson.toJson(responseDTO);
			}else{
				// 上送订单id
				String uploadOrderId = transInfo.getTransOrderId();

				queryInfo.setMerId(Constant.MERCHANT_NO);
				queryInfo.setTransactionId(uploadOrderId);

				// 第三方返回支付结果
				
				// FIXME 查询订单表状态
				PayQueryResponseEntity resp = payService.queryPayResult(queryInfo);
				
				resp.setMerId(originalInfo.getPid());
				resp.setTransactionId(originalInfo.getMerchantOrderId());
				resp.setOrderTime(originalInfo.getOrderTime());
				
				//查询签名
				HFSignUtil sign=new HFSignUtil();
				
				String str=PreSginUtil.payResultString(resp);
				
				String signDate=sign.sign(str, merchantkey);
				
				resp.setSignData(signDate);
				
				
				// 返回给下游 json
				jsonStr = gson.toJson(resp);
			}

		} // 返回查询结果
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print(jsonStr);
		out.flush();
		out.close();
	}

	/**
	 * 下游接入 demo
	 * 
	 * @param param
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "signForWap")
	public void merSignServletForWap(PayRequestEntity param, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		log.info("原始订单信息："+param);
		
		// 根据商户号查询key
		ChannleMerchantConfigKey keyinfo = payService.getChannelConfigKey(param.getPid());
		if (keyinfo != null) {

			String merchantKey = keyinfo.getMerchantkey();

			HFSignUtil signUtil = new HFSignUtil();
			// 生成签名
			String aa=PreSginUtil.paySigiString(param);
			String signmsg = signUtil.sign(aa, merchantKey);
			log.info("生成签名：" + signmsg);
			
			param.setSignmsg(signmsg);

			// 返回页面参数
			request.setAttribute("temp", param);
			request.getRequestDispatcher("/pay/demo/pay.jsp").forward(request, response);
		} else {
			PayResponseEntity temp = new PayResponseEntity();
			temp.setPaytype(param.getPaytype());
			temp.setBankid(param.getBankid());
			temp.setPid(param.getPid());
			temp.setTransactionid(param.getTransactionid());
			temp.setOrdertime(param.getOrdertime());
			temp.setOrderamount(param.getOrderamount());
			temp.setPayamount(param.getOrderamount());
			temp.setErrcode("11");
			// 返回页面参数
			Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
			String path=param.getPageurl() + "?" + queryUtil.bean2QueryStr(temp);
			log.info("demo 重定向："+path);
			response.sendRedirect(path.replace(" ", ""));
		}

	}

}
