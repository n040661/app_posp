package xdt.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import xdt.common.security.RSA;
import xdt.dto.pufa.PayRequestEntity;
import xdt.dto.pufa.QueryRequestEntity;
import xdt.dto.pufa.RefundRequestEntity;
import xdt.pufa.security.PuFaSignUtil;
import xdt.service.PufaService;
import xdt.util.BeanToMapUtil;

/**
 * 
 * @Description 浦发相关
 * @author Shiwen .Li
 * @date 2016年9月10日 上午11:46:42
 * @version V1.3.1
 */
@Controller
@RequestMapping("pufa")
public class PufaController extends BaseAction {

	public static final Logger logger = LoggerFactory
			.getLogger(PufaController.class);

	@Resource
	private PufaService pufaService;

	/**
	 * 扫码支付
	 * 
	 * @Description
	 * @author Administrator
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "pay")
	public void pay(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.info("######################################################");
		logger.info("文件key路径:" + RSA.publickeyFile);
		logger.info("######################################################");
		// 返回结果
		Map<String, Object> result = new HashMap<String, Object>();

		logger.info("扫码支付");

		String param = requestClient(request);
		logger.info("下游上送参数:{}", param);

		if (!StringUtils.isEmpty(param)) {

			PayRequestEntity reqeustInfo = gson.fromJson(param,
					PayRequestEntity.class);
			logger.info("json转换扫码支付对象{}", reqeustInfo);
			logger.info("下游上送签名串{}",reqeustInfo.getSign());
			if (signVerify(reqeustInfo, reqeustInfo.getSign())) {
				logger.info("开始处理扫码支付");
				result = pufaService.updatePay(reqeustInfo);
				logger.info("处理完成扫码支付");
			} else {
				logger.error("签名错误!");
				result.put("respCode", "15");
				result.put("respMsg", "签名错误");
			}

		} else {
			logger.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}

		logger.info("返回结果:{}", result);
		outString(response, gson.toJson(result));
	}

	/**
	 * 生成二维码
	 * 
	 * @Description
	 * @author Administrator
	 * @throws Exception 
	 */
	@RequestMapping(value = "produced/two/dimension")
	public void twoDimensionCode(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 返回结果
		Map<String, Object> result = new HashMap<String, Object>();

		logger.info("生成二维码");

		String param = requestClient(request);
		logger.info("下游上送参数:{}", param);

		if (!StringUtils.isEmpty(param)) {

			PayRequestEntity reqeustInfo = gson.fromJson(param,
					PayRequestEntity.class);

			logger.info("json转换扫码反扫对象{}", reqeustInfo);
			logger.info("下游上送签名串{}",reqeustInfo.getSign());
			if (signVerify(reqeustInfo, reqeustInfo.getSign())) {
				logger.info("开始处理生成二维码");
				result = pufaService.updateTwoDimensionCode(reqeustInfo);
				logger.info("处理完成生成二维码");
			} else {
				logger.error("签名错误!");
				result.put("respCode", "15");
				result.put("respMsg", "签名错误");
			}

		} else {
			logger.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}

		logger.info("返回结果:{}", result);

		outString(response, gson.toJson(result));

	}

	/**
	 * 扫码退款
	 * 
	 * @Description
	 * @author Administrator
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "refund")
	public void refund(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// 返回结果
		Map<String, Object> result = new HashMap<String, Object>();

		logger.info("扫码退款");

		String param = requestClient(request);
		logger.info("下游上送参数:{}", param);

		if (!StringUtils.isEmpty(param)) {

			// json转换扫码退款对象
			RefundRequestEntity requestInfo=gson.fromJson(param,RefundRequestEntity.class);
			
			// TODO 扫码退款;
			logger.info("下游上送签名串{}",requestInfo.getSign());
			if (signVerify(requestInfo, requestInfo.getSign())) {
				logger.info("开始处理查询");
				result = pufaService.updateRefund(requestInfo);
				logger.info("结束处理查询");
			} else {
				logger.error("签名错误!");
				result.put("respCode", "15");
				result.put("respMsg", "签名错误");
			}

		} else {
			logger.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}

		logger.info("返回结果:{}", result);

		outString(response, gson.toJson(result));
	}

	/**
	 * 扫码冲正
	 * 
	 * @Description
	 * @author Administrator
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "flushes")
	public void flushes(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		// 返回结果
		Map<String, Object> result = new HashMap<String, Object>();

		logger.info("扫码冲正");

		String param = requestClient(request);
		logger.info("下游上送参数:{}", param);

		if (!StringUtils.isEmpty(param)) {

			// json转换扫码扫码冲正对象

			// TODO 扫码冲正

		} else {
			logger.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}

		logger.info("返回结果:{}", result);

		outString(response, gson.toJson(result));
	}

	/**
	 * 查询交易
	 * 
	 * @Description
	 * @author Administrator
	 * @param requestInfo
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "query")
	public void query(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Map<String, Object> result = new HashMap<String, Object>();

		logger.info("查询交易");

		String param = requestClient(request);
		logger.info("下游上送参数:{}", param);

		if (!StringUtils.isEmpty(param)) {

			// json转换查询交易对象
			QueryRequestEntity requestInfo = gson.fromJson(param,
					QueryRequestEntity.class);
			logger.info("下游上送签名串{}",requestInfo.getSign());
			if (signVerify(requestInfo, requestInfo.getSign())) {
				logger.info("开始处理查询");
				result = pufaService.query(requestInfo);
				logger.info("结束处理查询");
			} else {
				logger.error("签名错误!");
				result.put("respCode", "15");
				result.put("respMsg", "签名错误");
			}

		} else {
			logger.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}

		logger.info("返回结果:{}", result);

		outString(response, gson.toJson(result));

	}

	@RequestMapping("paySign")
	public void paySign(HttpServletRequest request,HttpServletResponse response) throws Exception {
		System.out.println("aaaaaa");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		String param = requestClient(request);
		PayRequestEntity entity=gson.fromJson(param, PayRequestEntity.class);
		Map map=BeanToMapUtil.convertBean(entity);
		logger.info("支付签名");
		String sign=PuFaSignUtil.sign(map);
		outString(response, sign);
	}

	@RequestMapping("querySign")
	public void querySign(HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		String param = requestClient(request);
		QueryRequestEntity entity=gson.fromJson(param, QueryRequestEntity.class);
		Map map=BeanToMapUtil.convertBean(entity);
		logger.info("查询签名");
		String sign=PuFaSignUtil.sign(map);
		outString(response, sign);
	}
	@RequestMapping("refundSign")
	public void refundSign(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String param = requestClient(request);
		RefundRequestEntity entity=gson.fromJson(param, RefundRequestEntity.class);
		Map map=BeanToMapUtil.convertBean(entity);
		logger.info("查询签名");
		String sign=PuFaSignUtil.sign(map);
		outString(response, sign);
	}

}
