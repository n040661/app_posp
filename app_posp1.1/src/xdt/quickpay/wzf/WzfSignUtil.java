package xdt.quickpay.wzf;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import sun.util.logging.resources.logging;
import xdt.quickpay.hf.entity.PayRequestEntity;
import xdt.quickpay.ysb.model.YsbRequsetEntity;

public class WzfSignUtil {
	
	/**
	 * 日志记录
	 */
	private static Logger log = Logger.getLogger(WzfSignUtil.class);
	
	/**
	 * 子协议录入实体
	 * @param req
	 * @param sb
	 * @param dataString
	 * @return
	 */
	public static String wzfdaifuSigiString(YsbRequsetEntity req) {
		StringBuffer sb = new StringBuffer();
		String dataString = null;
		if (!StringUtils.isEmpty(req.getMerchantId())) {
			sb.append("merchantId=" + req.getMerchantId() + "&");
		}
		if (!StringUtils.isEmpty(req.getOrderId())) {
			sb.append("orderId=" + req.getOrderId() + "&");
		}
		if (!StringUtils.isEmpty(req.getBankCode())) {
			sb.append("bankCode=" + req.getBankCode() + "&");
		}
		if (!StringUtils.isEmpty(req.getSignChnl())) {
			sb.append("signChnl=" + req.getSignChnl()+ "&");
		}
		if (!StringUtils.isEmpty(req.getPayProducts())) {
			sb.append("payProducts=" + req.getPayProducts()+ "&");
		}
		if (!StringUtils.isEmpty(req.getAccType())) {
			sb.append("accType=" + req.getAccType() + "&");
		}
		if (!StringUtils.isEmpty(req.getAccCode())) {
			sb.append("accCode=" + req.getAccCode() + "&");
		}
		if (!StringUtils.isEmpty(sb.toString())) {
			dataString = sb.substring(0, sb.length() - 1);
			log.info("组成加密串:" + dataString);
		}
		return dataString;
	}
	/**
	 * 委托代扣接口
	 * @param req
	 * @param sb
	 * @param dataString
	 * @return
	 */
	public static String wzfdaikouSigiString(YsbRequsetEntity req) {
		StringBuffer sb = new StringBuffer();
		String dataString = null;
		if (!StringUtils.isEmpty(req.getMerchantId())) {
			sb.append("merchantId=" + req.getMerchantId() + "&");
		}
		if (!StringUtils.isEmpty(req.getOrderId())) {
			sb.append("orderId=" + req.getOrderId() + "&");
		}
		if (!StringUtils.isEmpty(req.getGoodsName())) {
			sb.append("goodsName=" + req.getGoodsName() + "&");
		}
		if (!StringUtils.isEmpty(req.getTradeMode())) {
			sb.append("tradeMode=" + req.getTradeMode() + "&");
		}
		if (!StringUtils.isEmpty(req.getAmount())) {
			sb.append("subContractId=" + req.getSubContractId()+ "&");
		}
		if (!StringUtils.isEmpty(req.getPayProducts())) {
			sb.append("payProducts=" + req.getPayProducts()+ "&");
		}
		if (!StringUtils.isEmpty(req.getAmount())) {
			sb.append("amount=" + req.getAmount() + "&");
		}
		if (!StringUtils.isEmpty(req.getBizCode())) {
			sb.append("bizCode=" + req.getBizCode() + "&");
		}
		if (!StringUtils.isEmpty(sb.toString())) {
			dataString = sb.substring(0, sb.length() - 1);
			log.info("组成加密串:" + dataString);
		}
		return dataString;
	}
	/**
	 * 代扣订单状态查询实体
	 * @param req
	 * @param sb
	 * @param dataString
	 * @return
	 */
	public static String ybsdaikouSigiquery(YsbRequsetEntity req) {
		StringBuffer sb = new StringBuffer();
		String dataString = null;
		if (!StringUtils.isEmpty(req.getMerchantId())) {
			sb.append("merchantId=" + req.getMerchantId() + "&");
		}
		if (!StringUtils.isEmpty(req.getOrderId())) {
			sb.append("orderId=" + req.getOrderId() + "&");
		}
		if (!StringUtils.isEmpty(sb.toString())) {
			dataString = sb.substring(0, sb.length() - 1);
			log.info("组成加密串:" + dataString);
		}
		return dataString;
	}

	/**
	 * 子协议号查询实体
	 * @param req
	 * @param sb
	 * @param dataString
	 * @return
	 */
	public static String ybsdaikouSigimerchant(YsbRequsetEntity req) {
		StringBuffer sb = new StringBuffer();
		String dataString = null;
		if (!StringUtils.isEmpty(req.getMerchantId())) {
			sb.append("merchantId=" + req.getMerchantId() + "&");
		}
		if (!StringUtils.isEmpty(req.getName())) {
			sb.append("name=" + req.getName() + "&");
		}
		if (!StringUtils.isEmpty(req.getCardNo())) {
			sb.append("cardNo=" + req.getCardNo() + "&");
		}
		if (!StringUtils.isEmpty(req.getIdCardNo())) {
			sb.append("idCardNo=" + req.getIdCardNo()+ "&");
		}
		if (!StringUtils.isEmpty(sb.toString())) {
			dataString = sb.substring(0, sb.length() - 1);
			log.info("组成加密串:" + dataString);
		}
		return dataString;
	}

	/**
	 * 子协议延期实体
	 * @param req
	 * @param sb
	 * @param dataString
	 * @return
	 */
	public static String ybsdaikouSigitime(YsbRequsetEntity req) {
		StringBuffer sb = new StringBuffer();
		String dataString = null;
		if (!StringUtils.isEmpty(req.getMerchantId())) {
			sb.append("merchantId=" + req.getMerchantId() + "&");
		}
		if (!StringUtils.isEmpty(req.getSubContractId())) {
			sb.append("subContractId=" + req.getSubContractId() + "&");
		}
		if (!StringUtils.isEmpty(req.getStartDate())) {
			sb.append("startDate=" + req.getStartDate()+ "&");
		}
		if (!StringUtils.isEmpty(req.getEndDate())) {
			sb.append("endDate=" + req.getEndDate() + "&");
		}
		if (!StringUtils.isEmpty(sb.toString())) {
			dataString = sb.substring(0, sb.length() - 1);
			log.info("组成加密串:" + dataString);
		}
		return dataString;
	}
	/**
	 * 实时代付接口实体
	 * @param req
	 * @param sb
	 * @param dataString
	 * @return
	 */
	public static String ybsdaifuSigiPay(YsbRequsetEntity req) {
		StringBuffer sb = new StringBuffer();
		String dataString = null;
		if (!StringUtils.isEmpty(req.getMerchantId())) {
			sb.append("merchantId=" + req.getMerchantId() + "&");
		}
		if (!StringUtils.isEmpty(req.getName())) {
			sb.append("name=" + req.getName() + "&");
		}
		if (!StringUtils.isEmpty(req.getCardNo())) {
			sb.append("cardNo=" + req.getCardNo() + "&");
		}
		if (!StringUtils.isEmpty(req.getOrderId())) {
			sb.append("orderId=" + req.getOrderId() + "&");
		}
		if (!StringUtils.isEmpty(req.getPurpose())) {
			sb.append("purpose=" + req.getPurpose() + "&");
		}
		if (!StringUtils.isEmpty(req.getAmount())) {
			sb.append("amount=" + req.getAmount() + "&");
		}
		if (!StringUtils.isEmpty(req.getResponseUrl())) {
			sb.append("responseUrl=" + req.getResponseUrl() + "&");
		}
		if (!StringUtils.isEmpty(sb.toString())) {
			dataString = sb.substring(0, sb.length() - 1);
			log.info("组成加密串:" + dataString);
		}
		return dataString;
	}
	/**
	 * 返回参数转MAP
	 * @param result
	 * @return
	 */
	public static Map<String, String> rep2Map(String str) {
		Map<String,String> reqMap = new HashMap<String, String>();
		//String str = (String)result.get(0);
		String[] parameters = str.split("\\|");
		for (int i = 0; i < parameters.length; i++) {
			String[] values = parameters[i].split("\\=",2);
			String key = values[0];
			String value = values.length>1?values[1]:"";
			/*if(key.equals("transDis")){
				value = UniPaySignUtilsCer.toHexString(value);
			}*/
			//                     无值传“”
			reqMap.put(key, value);
		}
		return reqMap;
	}


}
