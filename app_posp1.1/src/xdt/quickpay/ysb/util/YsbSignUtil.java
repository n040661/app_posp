package xdt.quickpay.ysb.util;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import sun.util.logging.resources.logging;
import xdt.controller.DaiKouAction;
import xdt.quickpay.daikou.model.DaikouRequsetEntity;
import xdt.quickpay.hf.entity.PayRequestEntity;
import xdt.quickpay.ysb.model.YsbRequsetEntity;

public class YsbSignUtil {
	
	/**
	 * 日志记录
	 */
	private static Logger log = Logger.getLogger(YsbSignUtil.class);
	
	/**
	 * 子协议录入实体
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
	 * 委托代扣接口
	 * @param req
	 * @param sb
	 * @param dataString
	 * @return
	 */
	public static String ybsdaikouSigiString(YsbRequsetEntity req) {
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
	 * 子协议录入实体
	 * @param req
	 * @param sb
	 * @param dataString
	 * @return
	 */
	public static String ybsdaifuSigiString(YsbRequsetEntity req) {
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
		if (!StringUtils.isEmpty(req.getPurpose())) {
			sb.append("purpose=" + req.getPurpose() + "&");
		}
		if (!StringUtils.isEmpty(req.getAmount())) {
			sb.append("amount=" + req.getAmount()+ "&");
		}
		if (!StringUtils.isEmpty(sb.toString())) {
			dataString = sb.substring(0, sb.length() - 1);
			log.info("组成加密串:" + dataString);
		}
		return dataString;
	}


}
