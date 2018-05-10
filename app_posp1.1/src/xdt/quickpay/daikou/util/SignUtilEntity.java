package xdt.quickpay.daikou.util;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import sun.util.logging.resources.logging;
import xdt.controller.DaiKouAction;
import xdt.quickpay.daikou.model.DaikouRequsetEntity;
import xdt.quickpay.hf.entity.PayRequestEntity;
import xdt.quickpay.ysb.model.YsbRequsetEntity;

public class SignUtilEntity {
	
	/**
	 * 日志记录
	 */
	private static Logger log = Logger.getLogger(SignUtilEntity.class);
	
	/**
	 * 银生宝子协议录入实体
	 * @param req
	 * @param sb
	 * @param dataString
	 * @return
	 */
	public static String ybsdaifuSigiString(DaikouRequsetEntity req) {
		StringBuffer sb = new StringBuffer();
		String dataString = null;
		if (!StringUtils.isEmpty(req.getMerchantId())) {
			sb.append("merchantId=" + req.getMerchantId() + "&");
		}
		if (!StringUtils.isEmpty(req.getName())) {
			sb.append("name=" + req.getName() + "&");
		}
		if (!StringUtils.isEmpty(req.getPhoneNo())) {
			sb.append("phoneNo=" + req.getPhoneNo() + "&");
		}
		if (!StringUtils.isEmpty(req.getCardNo())) {
			sb.append("cardNo=" + req.getCardNo() + "&");
		}
		if (!StringUtils.isEmpty(req.getIdCardNo())) {
			sb.append("idCardNo=" + req.getIdCardNo()+ "&");
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
	 *沃支付子协议录入实体
	 * @param req
	 * @param sb
	 * @param dataString
	 * @return
	 */
	public static String wzfdaifuSigiString(DaikouRequsetEntity req) {
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
	 * 银生宝委托代扣接口
	 * @param req
	 * @param sb
	 * @param dataString
	 * @return
	 */
	public static String ybsdaikouSigiString(DaikouRequsetEntity req) {
		StringBuffer sb = new StringBuffer();
		String dataString = null;
		if (!StringUtils.isEmpty(req.getMerchantId())) {
			sb.append("merchantId=" + req.getMerchantId() + "&");
		}
		if (!StringUtils.isEmpty(req.getOrderId())) {
			sb.append("orderId=" + req.getOrderId() + "&");
		}
		if (!StringUtils.isEmpty(req.getSubContractId())) {
			sb.append("subContractId=" + req.getSubContractId() + "&");
		}
		if (!StringUtils.isEmpty(req.getPurpose())) {
			sb.append("purpose=" + req.getPurpose() + "&");
		}
		if (!StringUtils.isEmpty(req.getAmount())) {
			sb.append("amount=" + req.getAmount()+ "&");
		}
		if (!StringUtils.isEmpty(req.getResponseUrl())) {
			sb.append("responseUrl=" + req.getResponseUrl()+ "&");
		}
		if (!StringUtils.isEmpty(req.getPhoneNo())) {
			sb.append("phoneNo=" + req.getPhoneNo() + "&");
		}
		if (!StringUtils.isEmpty(sb.toString())) {
			dataString = sb.substring(0, sb.length() - 1);
			log.info("组成加密串:" + dataString);
		}
		return dataString;
	}
	/**
	 * 沃支付委托代扣接口
	 * @param req
	 * @param sb
	 * @param dataString
	 * @return
	 */
	public static String wzfdaikouSigiString(DaikouRequsetEntity req) {
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
	 * 银生宝代扣订单状态查询实体
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
	 * 沃支付代扣订单状态查询实体
	 * @param req
	 * @param sb
	 * @param dataString
	 * @return
	 */
	public static String wzfdaikouSigiquery(YsbRequsetEntity req) {
		StringBuffer sb = new StringBuffer();
		String dataString = null;
		if (!StringUtils.isEmpty(req.getMerchantId())) {
			sb.append("merchantId=" + req.getMerchantId() + "&");
		}
		if (!StringUtils.isEmpty(req.getOrderId())) {
			sb.append("orderId=" + req.getOrderId() + "&");
		}
		if (!StringUtils.isEmpty(req.getOrderDate())) {
			sb.append("orderDate=" + req.getStartDate() + "&");
		}
		if (!StringUtils.isEmpty(req.getOrderType())) {
			sb.append("orderType=" + req.getOrderType() + "&");
		}
		if (!StringUtils.isEmpty(sb.toString())) {
			dataString = sb.substring(0, sb.length() - 1);
			log.info("组成加密串:" + dataString);
		}
		return dataString;
	}
	/**
	 * 沃支付代扣子协议解约实体
	 * @param req
	 * @param sb
	 * @param dataString
	 * @return
	 */
	public static String wzfdaikoujySigiquery(YsbRequsetEntity req) {
		StringBuffer sb = new StringBuffer();
		String dataString = null;
		if (!StringUtils.isEmpty(req.getMerchantId())) {
			sb.append("merchantId=" + req.getMerchantId() + "&");
		}
		if (!StringUtils.isEmpty(req.getOrderId())) {
			sb.append("orderId=" + req.getOrderId() + "&");
		}
		if (!StringUtils.isEmpty(req.getPayProducts())) {
			sb.append("payProducts=" + req.getPayProducts() + "&");
		}
		if (!StringUtils.isEmpty(req.getSubContractId())) {
			sb.append("subContractId=" + req.getSubContractId() + "&");
		}
		if (!StringUtils.isEmpty(sb.toString())) {
			dataString = sb.substring(0, sb.length() - 1);
			log.info("组成加密串:" + dataString);
		}
		return dataString;
	}
	/**
	 * 沃支付代扣退款实体
	 * @param req
	 * @param sb
	 * @param dataString
	 * @return
	 */
	public static String wzfdaikoutkSigiquery(YsbRequsetEntity req) {
		StringBuffer sb = new StringBuffer();
		String dataString = null;
		if (!StringUtils.isEmpty(req.getMerchantId())) {
			sb.append("merchantId=" + req.getMerchantId() + "&");
		}
		if (!StringUtils.isEmpty(req.getOrderId())) {
			sb.append("orderId=" + req.getOrderId() + "&");
		}
		if (!StringUtils.isEmpty(req.getOrderNo())) {
			sb.append("orderNo=" + req.getOrderNo() + "&");
		}
		if (!StringUtils.isEmpty(req.getAmount())) {
			sb.append("amount=" + req.getAmount() + "&");
		}
		if (!StringUtils.isEmpty(req.getReason())) {
			sb.append("reason=" + req.getReason()+ "&");
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


}
