package xdt.controller;

import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.StringUtils;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.xml.sax.InputSource;

import com.capinfo.crypt.Md5;
import com.google.gson.Gson;

import xdt.dto.BaseUtil;
import xdt.dto.SubmitOrderNoCardPayResponseDTO;
import xdt.dto.payeasy.DaifuRequestEntity;
import xdt.dto.payeasy.PayEasyQueryRequestEntity;
import xdt.dto.payeasy.PayEasyQueryResponseEntity;
import xdt.dto.payeasy.PayEasyRequestEntity;
import xdt.dto.payeasy.PayEasyResponseEntitys;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PospTransInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hengfeng.util.HFSignUtil;
import xdt.quickpay.hengfeng.util.PreSginUtil;
import xdt.quickpay.nbs.common.constant.Constant;
import xdt.quickpay.payeasy.util.PayeasyPostThread;
import xdt.schedule.ThreadPool;
import xdt.service.IPayeasyService;
import xdt.util.BeanToMapUtil;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;
import xdt.util.JsPostThread;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import net.sf.json.JSONObject;

/**
 * @ClassName: PayeasyController
 * @Description: 首信易支付
 * @author ShangYanChao
 * @date 2017年3月31日 下午1:43:10
 *
 */
@Controller
@RequestMapping("payeasy")
public class PayeasyController extends BaseAction {

	/**
	 * 日志记录
	 */
	private Logger log = Logger.getLogger(PayeasyController.class);

	@Resource
	private IPayeasyService payeasyService;

	// 机构号
	// private static final String MID = "13240";

	// 单笔对账url
	private static final String SELECT_URL = "http://api.yizhifubj.com/merchant/order/order_ack_oid_list.jsp";

	// 标准支付接口
	private static final String STANDARD_URL = "https://pay.yizhifubj.com/prs/user_payment.checkit";

	// 直连支付接口
	private static final String DIRECT_URL = "https://pay.yizhifubj.com/customer/gb/pay_bank.jsp";

	// 会员支付接口
	private static final String MEMBER_URL = "https://pay.yizhifubj.com/customer/gb/pay_member.jsp";

	// 前台通知页面
	private static final String page_URL = BaseUtil.url+"/payeasy/pagePayResult.action";

	// 代付接口
	private static final String daifu_URL = "http://pay.yizhifubj.com/merchant/virement/mer_payment_submit_utf8.jsp";

	// 代付接口
	private static final String balance_URL = "https://pay.yizhifubj.com/merchant/virement/mer_payment_balance_check.jsp";

	// 代付接口
	private static final String orderquery_URL = "http://pay.yizhifubj.com/merchant/virement/mer_payment_status_utf8.jsp";

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
	public void pay(PayEasyRequestEntity temp, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		log.info("原始订单信息：" + temp);
		// 查询上游商户号
		PmsBusinessPos busInfo = payeasyService.selectKey(temp.getMerchantId());
		String MID = busInfo.getBusinessnum();
		// 原始数据交易id
		String originalOrderId = temp.getV_oid();
		PayEasyRequestEntity param = new PayEasyRequestEntity();// 上送参数

		log.info("上游商户号:" + MID);

		// 所有的流程通过 就发起支付 上送数据
		String json = payeasyService.payHandle(temp);

		SubmitOrderNoCardPayResponseDTO respDto = new Gson().fromJson(json, SubmitOrderNoCardPayResponseDTO.class);

		log.info("支付…………");

		log.info("支付上送原始信息");

		log.info(temp);

		if (0 != respDto.getRetCode()) {
			PayEasyRequestEntity resp = new PayEasyRequestEntity();
			resp.setMerchantId(temp.getMerchantId());
			resp.setV_mid(MID);
			resp.setV_oid(temp.getV_oid());
			resp.setV_rcvname(temp.getV_rcvname());
			resp.setV_rcvaddr(temp.getV_rcvaddr());
			resp.setV_rcvtel(temp.getV_rcvtel());
			resp.setV_rcvpost(temp.getV_rcvpost());
			resp.setV_amount(temp.getV_amount());
			resp.setV_ymd(temp.getV_ymd());
			resp.setV_orderstatus(temp.getV_orderstatus());
			resp.setV_ordername(temp.getV_ordername());
			resp.setV_moneytype(temp.getV_moneytype());
			resp.setV_url(temp.getV_url());
			resp.setV_bgurl(temp.getV_bgurl());
			resp.setV_type(temp.getV_type());
			// 返回页面参数
			Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
			response.sendRedirect(temp.getV_url() + "?" + queryUtil.bean2QueryStr(resp));
		} else {
			// 设置上送信息
			param.setV_mid(MID);
			param.setV_oid(temp.getV_oid());
			param.setV_rcvname(temp.getV_rcvname());
			param.setV_rcvaddr(temp.getV_rcvaddr());
			param.setV_rcvtel(temp.getV_rcvtel());
			param.setV_rcvpost(temp.getV_rcvpost());
			param.setV_amount(temp.getV_amount());
			param.setV_ymd(temp.getV_ymd());
			param.setV_orderstatus(temp.getV_orderstatus());
			param.setV_ordername(temp.getV_ordername());
			param.setV_moneytype(temp.getV_moneytype());
			param.setV_url(temp.getV_url());
			if ("2".equals(temp.getV_type())) {
				param.setV_pmode(temp.getV_pmode());
			}
			// 生成签名
			Client cc = Client.create();
			WebResource rr = cc.resource(STANDARD_URL);
			MultivaluedMap queryParams = new MultivaluedMapImpl();
			Md5 md5 = new Md5("");
			String v_oid = temp.getV_oid();

			String str = param.getV_moneytype() + param.getV_ymd() + param.getV_amount() + param.getV_rcvname() + v_oid
					+ param.getV_mid() + param.getV_url();
			log.info("拼接后的字符串:" + str);
			logger.info("向上游发送的秘钥:" + busInfo.getKek());
			md5.hmac_Md5(str, busInfo.getKek());
			byte b[] = md5.getDigest();
			String digestString = md5.stringify(b);
			log.info("加密后的字符串:" + digestString);

			param.setV_md5info(digestString);
			// 设置上送参数
			String queryString = "";
			// Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
			// 设置转发页面
			String path = "";
			if ("1".equals(temp.getV_type())) {

				queryString = "v_mid=" + param.getV_mid() + "&v_oid=" + v_oid + "&v_rcvname=" + param.getV_rcvname()
						+ "&v_rcvaddr=" + param.getV_rcvaddr() + "&v_rcvtel=" + param.getV_rcvtel() + "&v_rcvpost="
						+ param.getV_rcvpost() + "&v_amount=" + param.getV_amount() + "&v_ymd=" + param.getV_ymd()
						+ "&v_orderstatus=" + param.getV_orderstatus() + "&v_ordername=" + param.getV_ordername()
						+ "&v_moneytype=" + param.getV_moneytype() + "&v_url=" + param.getV_url() + "&v_md5info="
						+ param.getV_md5info();
				log.info("上送的参数:" + queryString);
				path = STANDARD_URL + "?" + queryString;

			} else if ("2".equals(temp.getV_type())) {

				queryString = "v_mid=" + param.getV_mid() + "&v_oid=" + v_oid + "&v_rcvname=" + param.getV_rcvname()
						+ "&v_rcvaddr=" + param.getV_rcvaddr() + "&v_rcvtel=" + param.getV_rcvtel() + "&v_rcvpost="
						+ param.getV_rcvpost() + "&v_amount=" + param.getV_amount() + "&v_ymd=" + param.getV_ymd()
						+ "&v_orderstatus=" + param.getV_orderstatus() + "&v_ordername=" + param.getV_ordername()
						+ "&v_moneytype=" + param.getV_moneytype() + "&v_pmode=" + param.getV_pmode() + "&v_url="
						+ param.getV_url() + "&v_md5info=" + param.getV_md5info();
				log.info("上送的参数:" + queryString);
				path = DIRECT_URL + "?" + queryString;

			} else if ("3".equals(temp.getV_type())) {

				queryString = "v_mid=" + param.getV_mid() + "&v_oid=" + v_oid + "&v_rcvname=" + param.getV_rcvname()
						+ "&v_rcvaddr=" + param.getV_rcvaddr() + "&v_rcvtel=" + param.getV_rcvtel() + "&v_rcvpost="
						+ param.getV_rcvpost() + "&v_amount=" + param.getV_amount() + "&v_ymd=" + param.getV_ymd()
						+ "&v_orderstatus=" + param.getV_orderstatus() + "&v_ordername=" + param.getV_ordername()
						+ "&v_moneytype=" + param.getV_moneytype() + "&v_url=" + param.getV_url() + "&v_md5info="
						+ param.getV_md5info();
				log.info("上送的参数:" + queryString);
				path = MEMBER_URL + "?" + queryString;

			} else {
				log.info("没有上送的通道!");
			}

			log.info("重定向 第三方：" + path);
			response.sendRedirect(path.replace(" ", ""));

		}
	}

	@RequestMapping(value = "select")
	public void daifupay(DaifuRequestEntity temp, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
	        this.log.info("原始订单信息:" + temp);
			Map<String, String> result = new HashMap<String, String>();
			String[] array = temp.getV_data().split("\\$");

			String daifu = array[0];
			String[] list1 = daifu.split("\\|");
			temp.setV_count(list1[0]);
			temp.setV_sum_amount(list1[1]);
			temp.setV_batch_no(list1[2]);
			List list = new ArrayList();
			for (int i = 1; i < array.length; i++) {
				String[] array1 = array[i].split("\\|");
				for (int j = 1; j < array.length; j++) {
					temp.setV_cardNo(array1[0]);
					temp.setV_realName(array1[1]);
					temp.setV_bankname(array1[2]);
					temp.setV_province(array1[3]);
					temp.setV_city(array1[4]);
					temp.setV_amount(array1[5]);
					temp.setV_identity(array1[6]);
					temp.setV_pmsBankNo(array1[7]);
					list.add(temp);
				}
			}
			result = this.payeasyService.InsertDaifu(list);

			if ("1".equals(result.get("status"))) {
				temp.setResponsecode("00");
				this.payeasyService.UpdateDaifu(temp);
			} else if("3".equals(result.get("status"))){
				temp.setResponsecode("01");
				int num = this.payeasyService.UpdateDaifu(temp);
				log.info("修改结果" + num);
			}else{
				temp.setResponsecode("200");
				int num = this.payeasyService.UpdateDaifu(temp);
				log.info("修改结果" + num);
			}
			this.log.info("向下游 发送的数据:" + result);
			outString(response, this.gson.toJson(result));
			this.log.info("向下游 发送数据成功");
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
	public void payResult(PayEasyResponseEntitys temp, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		log.info("支付结果信息：" + temp);
		if (temp != null) {
			log.info("请求参数：" + request.getQueryString());
			response.getWriter().write("sent");
			// 处理这笔交易 修改订单表中的交易表
			payeasyService.otherInvoke(temp);
			// 交易id
			String tranId = temp.getV_oid();
			// 查询商户上送原始信息
			OriginalOrderInfo originalInfo = payeasyService.getOriginOrderInfo(tranId);

			// 替换成下游商户的
			temp.setV_oid(originalInfo.getMerchantOrderId());

			Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();

			log.info("拼接之后的数据:" + bean2Util.bean2QueryStr(temp));
			log.info("下游上送的url:" + originalInfo.getBgUrl());
			// 给下游主动返回支付结果
			String path = originalInfo.getBgUrl() + "?" + bean2Util.bean2QueryStr(temp);
			log.info("bgUrl 平台服务器重定向：" + path);
			String result = HttpURLConection.httpURLConnectionPOST(originalInfo.getBgUrl(),
					bean2Util.bean2QueryStr(temp));
			JSONObject ob1 = JSONObject.fromObject(result);
			Iterator it1 = ob1.keys();
			Map<String, String> map = new HashMap<>();
			while (it1.hasNext()) {
				String key1 = (String) it1.next();
				if (key1.equals("success")) {
					String value = ob1.getString(key1);
					logger.info("异步回馈的结果:" + "\t" + value);
					map.put("success", value);
				}
			}
			if (map.get("success").equals("false")) {

				logger.info("启动线程进行异步通知");
				// 启线程进行异步通知
				ThreadPool.executor(new PayeasyPostThread(originalInfo.getBgUrl(), temp));
			}
			log.info("向下游 发送数据成功" + result);
		} else {

			log.info("接受数据失败!");
			response.getWriter().write("error");
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
	@RequestMapping(value = "queryPayResult")
	public void queryPayResult(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// 返回结果
		Map<String, String> result = new HashMap<String, String>();
		String param = requestClient(request);
		log.info("下游上送参数:{}" + param);
		String jsonStr = "";
		if (!StringUtils.isEmpty(param)) {
			PayEasyQueryRequestEntity queryInfo = gson.fromJson(param, PayEasyQueryRequestEntity.class);
			log.info("查询支付结果：" + queryInfo);

			// 商户key
			String merchantkey = payeasyService.getChannelConfigKey(queryInfo.getMerchantId()).getMerchantkey();
			Gson gson = new Gson();
			SubmitOrderNoCardPayResponseDTO responseDTO = new SubmitOrderNoCardPayResponseDTO();

			HFSignUtil signUtil = new HFSignUtil();
			if (!signUtil.verify(PreSginUtil.payQuerySignStrings(queryInfo), queryInfo.getV_mac(), merchantkey)) {
				responseDTO.setRetCode(11);
				responseDTO.setRetMessage("签名错误");
				jsonStr = gson.toJson(responseDTO);
				response.setContentType("text/html; charset=utf-8");
				PrintWriter out = response.getWriter();
				out.print(jsonStr);
				out.flush();
				out.close();
				return;
			} else {
				log.info("给下游调用 查询结果");
				String tranId = queryInfo.getV_oid();
				log.info("查询商户上送原始信息");
				OriginalOrderInfo queryWhere = new OriginalOrderInfo();
				queryWhere.setMerchantOrderId(tranId);
				queryWhere.setPid(queryInfo.getMerchantId());
				log.info("查询原始订单" + queryWhere);
				log.info("快捷支付业务类" + payeasyService);
				OriginalOrderInfo originalInfo = payeasyService.selectByOriginal(queryWhere);

				log.info("原始数据 订单表关联id");
				String orderId = originalInfo.getOrderId();
				log.info("查询流水信息");
				PospTransInfo transInfo = payeasyService.getTransInfo(orderId);

				if (transInfo == null) {
					responseDTO.setRetCode(11);
					responseDTO.setRetMessage("流水不存在");
					jsonStr = gson.toJson(responseDTO);
					response.setContentType("text/html; charset=utf-8");
					PrintWriter out = response.getWriter();
					out.print(jsonStr);
					out.flush();
					out.close();
				} else {
					// 上送订单id
					String uploadOrderId = transInfo.getTransOrderId();
					// 查询上游商户号
					PmsBusinessPos busInfo = payeasyService.selectKey(queryInfo.getMerchantId());
					String v_mid = busInfo.getBusinessnum();

					queryInfo.setV_mid(v_mid);
					// queryInfo.setTransactionId(uploadOrderId);

					// 第三方返回支付结果

					// FIXME 查询订单表状态
					PayEasyQueryResponseEntity resp = payeasyService.queryPayResult(queryInfo);

					log.info("查询状态:" + resp.getV_pstatus());

					PayEasyQueryResponseEntity result1 = new PayEasyQueryResponseEntity();

					result1.setV_status(resp.getV_status());
					result1.setV_desc(resp.getV_desc());
					result1.setV_mid(queryInfo.getMerchantId());
					result1.setV_oid(resp.getV_oid());
					result1.setV_pmode(resp.getV_pmode());
					result1.setV_pstatus(resp.getV_pstatus());
					result1.setV_pstring(resp.getV_pstring());
					result1.setV_amount(resp.getV_amount());
					result1.setV_moneytype(resp.getV_moneytype());
					result1.setV_isvirement(resp.getV_isvirement());
					result1.setV_sign(resp.getV_sign());
					// 返回给下游 json
					jsonStr = gson.toJson(result1);
					log.info("给下游返回的json数据:{}" + jsonStr);
				}

			}
		} else {
			logger.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}
		// 返回查询结果
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
	public void merSignServletForWap(PayEasyRequestEntity param, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		log.info("原始订单信息：" + param);
		// 根据商户号查询key
		ChannleMerchantConfigKey keyinfo = payeasyService.getChannelConfigKey(param.getMerchantId());
		if (keyinfo != null) {

			String merchantKey = keyinfo.getMerchantkey();

			HFSignUtil signUtil = new HFSignUtil();
			// 生成签名
			String signmsg = signUtil.sign(PreSginUtil.payResultString(param), merchantKey);
			log.info("生成签名：" + signmsg);

			param.setV_md5info(signmsg);

			// 返回页面参数
			request.setAttribute("temp", param);
			request.getRequestDispatcher("/pay/payeasy/standard.jsp").forward(request, response);
		} else {
			PayEasyRequestEntity temp = new PayEasyRequestEntity();
			temp.setMerchantId(param.getMerchantId());
			temp.setV_mid(param.getV_mid());
			temp.setV_oid(param.getV_oid());
			temp.setV_rcvname(param.getV_rcvname());
			temp.setV_rcvaddr(param.getV_rcvaddr());
			temp.setV_rcvtel(param.getV_rcvtel());
			temp.setV_rcvpost(param.getV_rcvpost());
			temp.setV_amount(param.getV_amount());
			temp.setV_ymd(param.getV_ymd());
			temp.setV_orderstatus(param.getV_orderstatus());
			temp.setV_ordername(param.getV_ordername());
			temp.setV_moneytype(param.getV_moneytype());
			temp.setV_url(param.getV_url());
			temp.setV_md5info(param.getV_md5info());
			// 返回页面参数
			Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();

			String path = param.getV_url() + "?" + queryUtil.bean2QueryStr(temp);
			log.info("demo 重定向：" + path);
			response.sendRedirect(path.replace(" ", ""));
		}

	}

	@RequestMapping(value = "dimension")
	public void dimension(DaifuRequestEntity param, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		this.log.info("原始订单信息：" + param);
		Map<String, String> result = new HashMap<String, String>();

		ChannleMerchantConfigKey keyinfo = this.payeasyService.getChannelConfigKey(param.getMerchantId());
		if (keyinfo != null) {
			String merchantKey = keyinfo.getMerchantkey();

			HFSignUtil signUtil = new HFSignUtil();

			String signmsg = HFSignUtil.sign(PreSginUtil.paydaifuResultString(param), merchantKey);
			this.log.info("生成签名：" + signmsg);

			param.setV_mac(signmsg);

			request.setAttribute("temp", param);
			if (param.getV_data() != null)
				request.getRequestDispatcher("/pay/payeasy/daifu/B2Cpaid/B2C_submit.jsp").forward(request, response);
			else if (param.getV_identity() != null)
				request.getRequestDispatcher("/pay/payeasy/daifu/B2Cpaid/B2CselByOid_submit.jsp").forward(request,
						response);
			else
				request.getRequestDispatcher("/pay/payeasy/daifu/B2Cpaid/B2CSelect_submit.jsp").forward(request,
						response);
		} else {
			this.logger.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}

		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print(result);
		out.flush();
		out.close();
	}

	@RequestMapping(value = "paySign")
	public void paySign(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		String param = requestClient(request);
		PayEasyQueryRequestEntity entity = gson.fromJson(param, PayEasyQueryRequestEntity.class);
		Map map = BeanToMapUtil.convertBean(entity);
		logger.info("支付签名");
		// 根据商户号查询key
		ChannleMerchantConfigKey keyinfo = payeasyService.getChannelConfigKey(entity.getMerchantId());
		String v_mac = "";
		if (keyinfo != null) {

			String merchantKey = keyinfo.getMerchantkey();

			HFSignUtil signUtil = new HFSignUtil();
			// 生成签名
			v_mac = signUtil.sign(PreSginUtil.payQuerySignStrings(entity), merchantKey);
		}
		outString(response, v_mac);
	}

	@RequestMapping(value = "merchantDownload")
	public void balance(DaifuRequestEntity temp, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		this.log.info("原始订单信息：" + temp);
		Map<String, String> result = new HashMap<String, String>();

		ChannleMerchantConfigKey keyinfo = this.payeasyService.getChannelConfigKey(temp.getMerchantId());
		if (keyinfo != null) {
			String merchantKey = keyinfo.getMerchantkey();

			HFSignUtil signUtil = new HFSignUtil();
			if (!HFSignUtil.verify(PreSginUtil.paydaifuResultString(temp), temp.getV_mac(), merchantKey)) {
				result.put("11", "签名错误");
				this.log.info("签名错误");
				response.setContentType("text/html; charset=utf-8");
				PrintWriter out = response.getWriter();
				out.print(result);
				out.flush();
				out.close();
				return;
			}
			// 查询上游商户号
			PmsBusinessPos busInfo = payeasyService.selectKey(temp.getMerchantId());
			String v_mid = busInfo.getBusinessnum();
			Md5 md5 = new Md5("");
			md5.hmac_Md5(v_mid, busInfo.getKek());
			byte[] b = md5.getDigest();
			String v_mac = Md5.stringify(b);
			String queryString = "v_mid=" + v_mid + "&v_mac=" + v_mac;
			this.log.info("上送的参数:" + queryString);
			String path = balance_URL + "?" + queryString;
			this.log.info("重定向 第三方：" + path);

			String xml = HttpUtil.sendPost(path);

			this.log.info("返回的xml数据:" + xml);

			StringReader read = new StringReader(xml);

			InputSource source = new InputSource(read);

			SAXBuilder sb = new SAXBuilder();

			Document doc = sb.build(source);

			Element root = doc.getRootElement();
			this.log.info("根元素名称:" + root.getName());

			List jiedian = root.getChildren();
			this.log.info("根元素下的子元素:" + jiedian);
			Element et = null;
			Element et1 = null;

			et = (Element) jiedian.get(0);
			this.log.info("状态值" + et.getChild("status").getText());
			String status = et.getChild("status").getText();
			this.log.info("状态描述" + et.getChild("statusdesc").getText());
			String statusdesc = et.getChild("statusdesc").getText();
			this.log.info("商户号" + et.getChild("mid").getText());
			String mid = et.getChild("mid").getText();

			et1 = (Element) jiedian.get(1);
			this.log.info("余额" + et1.getChild("balance").getText());
			String balance = et1.getChild("balance").getText();
			this.log.info("签名" + et1.getChild("sign").getText());
			String sign = et1.getChild("sign").getText();

			result.put("status", status);
			result.put("statusdesc", statusdesc);
			result.put("mid", mid);
			result.put("balance", balance);
			result.put("sign", sign);
		} else {
			this.logger.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}
		outString(response, this.gson.toJson(result));
		this.log.info("向下游 发送数据成功");
	}

	@RequestMapping(value = "orderquery_param")
	public void orderquery(DaifuRequestEntity temp, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		this.log.info("原始订单信息：" + temp);
		Map<String, String> result = new HashMap<String, String>();

		ChannleMerchantConfigKey keyinfo = this.payeasyService.getChannelConfigKey(temp.getMerchantId());
		if (keyinfo != null) {
			String merchantKey = keyinfo.getMerchantkey();

			HFSignUtil signUtil = new HFSignUtil();
			if (!HFSignUtil.verify(PreSginUtil.paydaifuResultString(temp), temp.getV_mac(), merchantKey)) {
				result.put("11", "签名错误");
				this.log.info("签名错误");
				response.setContentType("text/html; charset=utf-8");
				PrintWriter out = response.getWriter();
				out.print(result);
				out.flush();
				out.close();
				return;
			}
			// 查询上游商户号
			PmsBusinessPos busInfo = payeasyService.selectKey(temp.getMerchantId());
			String v_mid = busInfo.getBusinessnum();
			String v_data = temp.getV_identity();
			Md5 md5 = new Md5("");
			System.out.println("转码之后的数据:" + URLEncoder.encode(v_data, "utf-8"));
			md5.hmac_Md5(v_mid + URLEncoder.encode(v_data, "utf-8"), busInfo.getKek());
			byte[] b = md5.getDigest();
			String v_mac = Md5.stringify(b);
			String queryString = "v_mid=" + v_mid + "&v_data=" + v_data + "&v_mac=" + v_mac;
			this.log.info("上送的参数:" + queryString);
			String path = orderquery_URL + "?" + queryString;
			this.log.info("重定向 第三方：" + path);

			String xml = HttpUtil.sendPost(path);

			this.log.info("返回的xml数据:" + xml);

			StringReader read = new StringReader(xml);

			InputSource source = new InputSource(read);

			SAXBuilder sb = new SAXBuilder();

			Document doc = sb.build(source);

			Element root = doc.getRootElement();
			this.log.info("根元素名称:" + root.getName());

			this.log.info("状态值" + root.getChild("status").getText());
			String status = root.getChild("status").getText();
			this.log.info("状态描述" + root.getChild("statusdesc").getText());
			String statusdesc = root.getChild("statusdesc").getText();
			this.log.info("费率值" + root.getChild("v_fee").getText());
			String v_fee = root.getChild("v_fee").getText();

			result.put("status", status);
			result.put("statusdesc", statusdesc);
			result.put("v_fee", v_fee);
		} else {
			this.logger.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}
		outString(response, this.gson.toJson(result));
		this.log.info("向下游 发送数据成功");
	}
}
