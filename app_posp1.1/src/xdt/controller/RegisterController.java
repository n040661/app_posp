
package xdt.controller;

import java.io.IOException;
import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.ResponseBody;
import xdt.dto.nbs.register.Register;
import xdt.dto.nbs.register.RegisterResponse;
import xdt.model.ChannleMerchantConfigKey;
import xdt.quickpay.nbs.common.util.SignatureUtil;
import xdt.service.IClientCollectionPayService;
import xdt.service.IRegisterService;
import xdt.util.BeanToMapUtil;

@Controller
@RequestMapping("registerController")
public class RegisterController extends BaseAction {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	@Resource
	private IRegisterService registerService;

	/**
	 * 注册下游信息
	 * 
	 * @param register
	 * @param response
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping("registers")
	public void Registers(HttpServletResponse response, HttpServletRequest request) throws Exception {
		// JSONObject json =new JSONObject();
		Map<String, Object> result = new HashMap<String, Object>();
		RegisterResponse rp = new RegisterResponse();
		String param = requestClient(request);
		log.info("下游上送参数", param);
		if (!StringUtils.isEmpty(param)) {
			Register entity = gson.fromJson(param, Register.class);
			log.info("json转换扫码反扫对象{}", entity);
			log.info("下游上送签名串{}", entity.getSign());
			// 查询商户密钥
			ChannleMerchantConfigKey keyinfo = registerService.getChannelConfigKey(entity.getOutMchId());
			// ------------------------需要改签名
			String merchantKey = keyinfo.getMerchantkey();
			SignatureUtil signUtil = new SignatureUtil();

			Map map = BeanToMapUtil.convertBean(entity);
			
			if(signUtil.checkSign(map, merchantKey, log)){
				log.info("对比签名成功");
				rp = registerService.inster(entity);
				// 将返回的数据进行签名
				Map map1 = BeanToMapUtil.convertBean(rp);
				String sign = SignatureUtil.getSign(map1, merchantKey, log);
				log.info("签名结果:" + sign);
				if (rp.getReturn_code() != null) {
					result.put("return_code", rp.getReturn_code());
				}
				if (rp.getReturn_msg() != null) {
					result.put("return_msg", rp.getReturn_msg());
				}
				if (rp.getCustomer_num() != null) {
					result.put("customer_num", rp.getCustomer_num());
				}
				if (rp.getApi_key() != null) {
					result.put("api_key", rp.getApi_key());
				}
				result.put("sign", sign);
			}else {
				log.error("签名错误!");
				result.put("respCode", "15");
				result.put("respMsg", "签名错误!");
			}

		} else {
			log.error("上送交易参数空!");
			result.put("return_code", "01");
			result.put("return_msg", "fail");
		}

		outString(response, gson.toJson(result));
	}

	/**
	 * 商户查询
	 * 
	 * @param register
	 * @param response
	 * @param request
	 * @throws Exception 
	 */
	@RequestMapping(value = "select")
	public void select(HttpServletResponse response, HttpServletRequest request) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		RegisterResponse rp = new RegisterResponse();
		String param = requestClient(request);
		log.info("下游上送参数", param);
		if (!StringUtils.isEmpty(param)) {
			Register entity = gson.fromJson(param, Register.class);
			log.info("json转换扫码反扫对象{}", entity);
			log.info("下游上送签名串{}", entity.getSign());
			// 查询商户密钥
			ChannleMerchantConfigKey keyinfo = registerService.getChannelConfigKey(entity.getOutMchId());
			// ------------------------需要改签名
			String merchantKey = keyinfo.getMerchantkey();
			SignatureUtil signUtil = new SignatureUtil();

			Map map = BeanToMapUtil.convertBean(entity);
			
			if(signUtil.checkSign(map, merchantKey, log)){
				log.info("对比签名成功");
				String ss = registerService.select(entity, log);
				// 将返回的数据进行签名
				Map map1 = BeanToMapUtil.convertBean(rp);
				String sign = SignatureUtil.getSign(map1, merchantKey, log);
				log.info("签名结果:" + sign);
				if (rp.getReturn_code() != null) {
					result.put("return_code", rp.getReturn_code());
				}
				if (rp.getReturn_msg() != null) {
					result.put("return_msg", rp.getReturn_msg());
				}
				if (rp.getCustomer_num() != null) {
					result.put("customer_num", rp.getCustomer_num());
				}
				if (rp.getApi_key() != null) {
					result.put("api_key", rp.getApi_key());
				}
				result.put("sign", sign);
			}else {
				log.error("签名错误!");
				result.put("respCode", "15");
				result.put("respMsg", "签名错误!");
			}

		} else {
			log.error("上送交易参数空!");
			result.put("return_code", "01");
			result.put("return_msg", "fail");
		}

	}

	/**
	 * 修改商户
	 * 
	 * @param register
	 * @param response
	 * @param request
	 * @throws Exception 
	 */
	@RequestMapping(value = "update")
	public void update(Register register, HttpServletResponse response, HttpServletRequest request) throws Exception {
		log.info("下游传来的值" + register);
		if (register != null) {
			String ss = registerService.update(register, log);
			outString(response, gson.toJson(ss));
		}

	}

	/**
	 * 商户对账单下载
	 * 
	 * @param register
	 * @param response
	 * @param request
	 * @throws Exception 
	 */
	@RequestMapping(value = "merchantDownload")
	public void merchantDownload(Register register, HttpServletResponse response, HttpServletRequest request)
			throws Exception {
		log.info("下游传来的值" + register);
		if (register != null) {
			String ss = registerService.merchantDownload(register, log);
			outString(response, gson.toJson(ss));
		}

	}

	/**
	 * 商户结算状态查询
	 * 
	 * @param register
	 * @param response
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping(value = "selectSettlementStatus")
	public void selectSettlementStatus(Register register, HttpServletResponse response, HttpServletRequest request)
			throws Exception {
		log.info("下游传来的值" + register);
		if (register != null) {
			String ss = registerService.selectSettlementStatus(register, log);
			outString(response, gson.toJson(ss));
		}
	}

	/**
	 * 北农商进件接口参数
	 * 
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("RegisterScan")
	public void registerScan(HttpServletRequest request, HttpServletResponse response) throws Exception {

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		String param = requestClient(request);
		Register entity = gson.fromJson(param, Register.class);
		log.info("下游上送的参数:" + entity);
		Map<String, Object> map = new HashMap<String, Object>();
		// 查询商户密钥
		ChannleMerchantConfigKey keyinfo = registerService.getChannelConfigKey(entity.getOutMchId());
		String merchantKey = keyinfo.getMerchantkey();
		log.info("下游商户密钥:" + keyinfo);
		Register entitys = new Register(merchantKey, entity.getServiceType(), entity.getAgentNum(),
				entity.getMerchantNumber(), entity.getApiKey(), entity.getOutMchId(), entity.getAppId(),
				entity.getCustomerType(), entity.getBusinessType(), entity.getBusinessName(), entity.getLegalId(),
				entity.getLegalName(), entity.getContact(), entity.getContactPhone(), entity.getContactEmail(),
				entity.getServicePhone(), entity.getCustomerName(), entity.getAddress(), entity.getProvinceName(),
				entity.getCityName(), entity.getDistrictName(), entity.getLicenseNo(), entity.getPayChannel(),
				entity.getRate(), entity.getT0Status(), entity.getSettleRate(), entity.getFixedFee(),
				entity.getIsCapped(), entity.getSettleMode(), entity.getUpperFee(), entity.getAccountType(),
				entity.getAccountName(), entity.getBankCard(), entity.getBankName(), entity.getProvince(),
				entity.getCity(), entity.getBankAddress(), entity.getAlliedBankNo(), entity.getRightID(),
				entity.getReservedID(), entity.getIDWithHand(), entity.getRightBankCard(), entity.getLicenseImage(),
				entity.getDoorHeadImage(), entity.getAccountLicence(), entity.getQueryType(), entity.getCustomerNum(),
				entity.getOrderDate(), entity.getCheckDate(), log);
		String sign = entitys.getSign();
		outString(response, sign);
	}

}