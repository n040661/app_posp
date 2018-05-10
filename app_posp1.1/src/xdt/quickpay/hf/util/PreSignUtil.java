package xdt.quickpay.hf.util;

import org.apache.commons.lang.StringUtils;

import xdt.quickpay.hf.entity.PayQueryRequestEntity;
import xdt.quickpay.hf.entity.PayRequestEntity;

public class PreSignUtil {
	
//	/**
//	 * @param req
//	 * @param sb
//	 * @param dataString
//	 * @return
//	 */
//	public static String hfpaySigiString(PayRequestEntity req) {
//		StringBuffer sb = new StringBuffer();
//		String dataString = null;
//		if (!StringUtils.isEmpty(req.getMerchantId())) {
//			sb.append("merchantId=" + req.getMerchantId() + "&");
//		}
//		if (!StringUtils.isEmpty(req.getFrontUrl())) {
//			sb.append("frontUrl=" + req.getFrontUrl() + "&");
//		}
//		if (!StringUtils.isEmpty(req.getBackUrl())) {
//			sb.append("backUrl=" + req.getBackUrl() + "&");
//		}
//		if (!StringUtils.isEmpty(req.getOrderId())) {
//			sb.append("orderId=" + req.getOrderId() + "&");
//		}
//		if (!StringUtils.isEmpty(req.getAccNo())) {
//			sb.append("accNo=" + req.getAccNo()+ "&");
//		}
//		if (!StringUtils.isEmpty(req.getTxnTime())) {
//			sb.append("txnTime=" + req.getTxnTime() + "&");
//		}
//
//		if (!StringUtils.isEmpty(sb.toString())) {
//			dataString = sb.substring(0, sb.length() - 1);
//			System.out.println("组成加密串:" + dataString);
//		}
//		return dataString;
//	}
//	/**
//	 * @param req
//	 * @param sb
//	 * @param dataString
//	 * @return
//	 */
//	public static String hfmessageSignString(PayRequestEntity req) {
//		StringBuffer sb = new StringBuffer();
//		String dataString = null;
//		if (!StringUtils.isEmpty(req.getMerchantId())) {
//			sb.append("merchantId=" + req.getMerchantId() + "&");
//		}
//		if (!StringUtils.isEmpty(req.getTxnTime())) {
//			sb.append("txnTime=" + req.getTxnTime() + "&");
//		}
//		if (!StringUtils.isEmpty(req.getOrderId())) {
//			sb.append("orderId=" + req.getOrderId() + "&");
//		}
//		if (!StringUtils.isEmpty(req.getTxnAmt())) {
//			sb.append("txnAmt=" + req.getTxnAmt() + "&");
//		}
//		if (!StringUtils.isEmpty(req.getToken())) {
//			sb.append("token=" + req.getToken()+ "&");
//		}
//		if (!StringUtils.isEmpty(req.getPhoneNo())) {
//			sb.append("phoneNo=" + req.getPhoneNo() + "&");
//		}
//		if (!StringUtils.isEmpty(sb.toString())) {
//			dataString = sb.substring(0, sb.length() - 1);
//			System.out.println("组成加密串:" + dataString);
//		}
//		return dataString;
//	}
//	/**
//	 * @param req
//	 * @param sb
//	 * @param dataString
//	 * @return
//	 */
//	public static String hfconsumeSignString(PayRequestEntity req) {
//		StringBuffer sb = new StringBuffer();
//		String dataString = null;
//		if (!StringUtils.isEmpty(req.getMerchantId())) {
//			sb.append("merchantId=" + req.getMerchantId() + "&");
//		}
//		if (!StringUtils.isEmpty(req.getTxnAmt())) {
//			sb.append("txnAmt=" + req.getTxnAmt() + "&");
//		}
//		if (!StringUtils.isEmpty(req.getOrderId())) {
//			sb.append("orderId=" + req.getOrderId() + "&");
//		}
//		if (!StringUtils.isEmpty(req.getTxnTime())) {
//			sb.append("txnTime=" + req.getTxnTime() + "&");
//		}
//		if (!StringUtils.isEmpty(req.getToken())) {
//			sb.append("token=" + req.getToken() + "&");
//		}
//		if (!StringUtils.isEmpty(req.getBackUrl())) {
//			sb.append("backUrl=" + req.getBackUrl()+ "&");
//		}
//		if (!StringUtils.isEmpty(req.getSmsCode())) {
//			sb.append("smsCode=" + req.getSmsCode() + "&");
//		}
//		if (!StringUtils.isEmpty(sb.toString())) {
//			dataString = sb.substring(0, sb.length() - 1);
//			System.out.println("组成加密串:" + dataString);
//		}
//		return dataString;
//	}
	/**
	 * @param req
	 * @param sb
	 * @param dataString
	 * @return
	 */
	public static String hfconsumeSignString(PayRequestEntity req) {
		StringBuffer sb = new StringBuffer();
		String dataString = null;
		if (!StringUtils.isEmpty(req.getMerchantId())) {
			sb.append("merchantId=" + req.getMerchantId() + "&");
		}
		if (!StringUtils.isEmpty(req.getPayType())) {
			sb.append("payType=" + req.getPayType() + "&");
		}
		if (!StringUtils.isEmpty(req.getTxnAmt())) {
			sb.append("txnAmt=" + req.getTxnAmt() + "&");
		}
		if (!StringUtils.isEmpty(req.getOrderId())) {
			sb.append("orderId=" + req.getOrderId() + "&");
		}
		if (!StringUtils.isEmpty(req.getTxnTime())) {
			sb.append("txnTime=" + req.getTxnTime() + "&");
		}
		if (!StringUtils.isEmpty(req.getBackUrl())) {
			sb.append("backUrl=" + req.getBackUrl() + "&");
		}
		if (!StringUtils.isEmpty(req.getAccNo())) {
			sb.append("accNo=" + req.getAccNo() + "&");
		}
		if (!StringUtils.isEmpty(req.getUserfee())) {
			sb.append("userfee=" + req.getUserfee() + "&");
		}
		
		if (!StringUtils.isEmpty(req.getBankName())) {
			sb.append("bankName=" + req.getBankName() + "&");
		}
		if (!StringUtils.isEmpty(req.getToBankNo())) {
			sb.append("toBankNo=" + req.getToBankNo() + "&");
		}
		if (!StringUtils.isEmpty(req.getName())) {
			sb.append("name=" + req.getName() + "&");
		}
		if (!StringUtils.isEmpty(req.getCertNo())) {
			sb.append("certNo=" + req.getCertNo() + "&");
		}
		if (!StringUtils.isEmpty(req.getFrontUrl())) {
			sb.append("frontUrl=" + req.getFrontUrl()+ "&");
		}
		if (!StringUtils.isEmpty(sb.toString())) {
			dataString = sb.substring(0, sb.length() - 1);
			System.out.println("组成加密串:" + dataString);
		}
		return dataString;
	}
	/**
	 * @param req
	 * @param sb
	 * @param dataString
	 * @return
	 */
	public static String hfquerySignString(PayQueryRequestEntity req) {
		StringBuffer sb = new StringBuffer();
		String dataString = null;
		if (!StringUtils.isEmpty(req.getMerchantId())) {
			sb.append("merchantId=" + req.getMerchantId() + "&");
		}
		if (!StringUtils.isEmpty(req.getOrderId())) {
			sb.append("orderId=" + req.getOrderId() + "&");
		}
		if (!StringUtils.isEmpty(req.getTxnTime())) {
			sb.append("txnTime=" + req.getTxnTime() + "&");
		}
		if (!StringUtils.isEmpty(sb.toString())) {
			dataString = sb.substring(0, sb.length() - 1);
			System.out.println("组成加密串:" + dataString);
		}
		return dataString;
	}

}
