package xdt.quickpay.hengfeng.util;

import org.apache.commons.lang.StringUtils;

import xdt.dto.payeasy.PayEasyRequestEntity;
import xdt.model.MsgBean;
import xdt.quickpay.cjt.entity.CjtRequestEntity;
import xdt.quickpay.hengfeng.entity.PayQueryRequestEntity;
import xdt.quickpay.hengfeng.entity.PayQueryResponseEntity;
import xdt.quickpay.hengfeng.entity.PayRequestEntity;
import xdt.quickpay.hengfeng.entity.PayResponseEntity;
import xdt.dto.payeasy.DaifuRequestEntity;
import xdt.dto.payeasy.PayEasyQueryRequestEntity;

public class PreSginUtil {
	/**
	 * @param req
	 * @param sb
	 * @param dataString
	 * @return
	 */
	public static String paySigiString(PayRequestEntity req) {
		StringBuffer sb = new StringBuffer();
		String dataString = null;
		if (!StringUtils.isEmpty(req.getPageurl())) {
			sb.append("pageurl=" + req.getPageurl() + "&");
		}
		if (!StringUtils.isEmpty(req.getBgurl())) {
			sb.append("bgurl=" + req.getBgurl() + "&");
		}
		if (!StringUtils.isEmpty(req.getTransactionid())) {
			sb.append("transactionid=" + req.getTransactionid() + "&");
		}
		if (!StringUtils.isEmpty(req.getOrderamount())) {
			sb.append("orderamount=" + req.getOrderamount() + "&");
		}
		if (!StringUtils.isEmpty(req.getOrdertime())) {
			sb.append("ordertime=" + req.getOrdertime() + "&");
		}
		if (!StringUtils.isEmpty(req.getPaytype())) {
			sb.append("paytype=" + req.getPaytype() + "&");
		}
		if (!StringUtils.isEmpty(req.getPid())) {
			sb.append("pid=" + req.getPid() + "&");
		}

		if (!StringUtils.isEmpty(sb.toString())) {
			dataString = sb.substring(0, sb.length() - 1);
			System.out.println("组成加密串:" + dataString);
		}
		return dataString;
	}
	public static String paySigiString(CjtRequestEntity param) {
		StringBuffer sb = new StringBuffer();
		String dataString = null;
		if (!StringUtils.isEmpty(param.getBgurl())) {
			sb.append("pageurl=" + param.getBgurl() + "&");
		}
		if (!StringUtils.isEmpty(param.getTransactionid())) {
			sb.append("transactionid=" + param.getTransactionid() + "&");
		}
		if (!StringUtils.isEmpty(param.getOrderamount())) {
			sb.append("orderamount=" + param.getOrderamount() + "&");
		}
		if (!StringUtils.isEmpty(param.getOrdertime())) {
			sb.append("ordertime=" + param.getOrdertime() + "&");
		}
		if (!StringUtils.isEmpty(param.getPid())) {
			sb.append("pid=" + param.getPid() + "&");
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
	public static String payQuerySignString(PayQueryRequestEntity req) {
		StringBuffer sb = new StringBuffer();
		String dataString = null;
		if (!StringUtils.isEmpty(req.getMerId())) {
			sb.append("merId=" + req.getMerId() + "&");
		}
		if (!StringUtils.isEmpty(req.getMerId())) {
			sb.append("transactionId=" + req.getTransactionId() + "&");
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
	public static String payQuerySignStrings(PayEasyQueryRequestEntity req) {
		StringBuffer sb = new StringBuffer();
		String dataString = null;
		if (!StringUtils.isEmpty(req.getMerchantId())) {
			sb.append("merchantId=" + req.getMerchantId() + "&");
		}
		if (!StringUtils.isEmpty(req.getV_oid())) {
			sb.append("v_oid=" + req.getV_oid() + "&");
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
	public static String paydaifuResultString(DaifuRequestEntity req) {
		StringBuffer sb = new StringBuffer();
		String dataString = null;
		if (!StringUtils.isEmpty(req.getMerchantId())) {
			sb.append("merchantId=" + req.getMerchantId() + "&");
		}
		if (!StringUtils.isEmpty(req.getV_data())) {
			sb.append("v_data=" + req.getV_data() + "&");
		}
		if (!StringUtils.isEmpty(sb.toString())) {
			dataString = sb.substring(0, sb.length() - 1);
			System.out.println("组成加密串:" + dataString);
		}
		return dataString;
	}

	/**
	 * 返回查询结果签名
	 * 
	 * @Description
	 * @author Administrator
	 * @param resp
	 * @return
	 */

	public static String payResultString(PayQueryResponseEntity resp) {
		StringBuffer sb = new StringBuffer();
		String dataString = null;
		if (!StringUtils.isEmpty(resp.getResponseCode())) {
			sb.append("responseCode=" + resp.getResponseCode() + "&");
		}
		if (!StringUtils.isEmpty(resp.getMerId())) {
			sb.append("merId=" + resp.getMerId() + "&");
		}
		if (!StringUtils.isEmpty(resp.getTransactionId())) {
			sb.append("transactionId=" + resp.getTransactionId() + "&");
		}
		if (!StringUtils.isEmpty(resp.getOrderTime())) {
			sb.append("orderTime=" + resp.getOrderTime() + "&");
		}
		if (!StringUtils.isEmpty(resp.getOrderAmount())) {
			sb.append("orderAmount=" + resp.getOrderAmount() + "&");
		}
		if (!StringUtils.isEmpty(resp.getTransStatus())) {
			sb.append("transStatus=" + resp.getTransStatus() + "&");
		}

		if (!StringUtils.isEmpty(sb.toString())) {
			dataString = sb.substring(0, sb.length() - 1);
			System.out.println("组成加密串:" + dataString);
		}
		return dataString;
	}
	/**
	 * 返回查询结果签名
	 * 
	 * @Description
	 * @author Administrator
	 * @param resp
	 * @return
	 */

	public static String payResultString(PayEasyRequestEntity resp) {
		StringBuffer sb = new StringBuffer();
		String dataString = null;
		if (!StringUtils.isEmpty(resp.getV_moneytype())) {
			sb.append("moneytype=" + resp.getV_moneytype() + "&");
		}
		if (!StringUtils.isEmpty(resp.getV_ymd())) {
			sb.append("ymd=" + resp.getV_ymd() + "&");
		}
		if (!StringUtils.isEmpty(resp.getV_amount())) {
			sb.append("amount=" + resp.getV_amount() + "&");
		}
		if (!StringUtils.isEmpty(resp.getV_rcvname())) {
			sb.append("rcvname=" + resp.getV_rcvname() + "&");
		}
		if (!StringUtils.isEmpty(resp.getV_oid())) {
			sb.append("oid=" + resp.getV_oid() + "&");
		}
		if (!StringUtils.isEmpty(resp.getMerchantId())) {
			sb.append("merchantId=" + resp.getMerchantId()+ "&");
		}
		if (!StringUtils.isEmpty(resp.getV_url())) {
			sb.append("url=" + resp.getV_url() + "&");
		}

		if (!StringUtils.isEmpty(sb.toString())) {
			dataString = sb.substring(0, sb.length() - 1);
			System.out.println("组成加密串:" + dataString);
		}
		return dataString;
	}

	/**
	 * 商户前后台地址签名
	 * 
	 * @Description
	 * @author Administrator
	 * @param temp
	 * @return
	 */

	public static String payBgResultString(PayResponseEntity temp) {

		StringBuffer sb = new StringBuffer();
		String dataString = null;

		if (!StringUtils.isEmpty(temp.getPaytype())) {
			sb.append("paytype=" + temp.getPaytype() + "&");
		}
		if (!StringUtils.isEmpty(temp.getPid())) {
			sb.append("pid=" + temp.getPid() + "&");
		}
		if (!StringUtils.isEmpty(temp.getTransactionid())) {
			sb.append("transactionid=" + temp.getTransactionid() + "&");
		}
		if (!StringUtils.isEmpty(temp.getOrdertime())) {
			sb.append("ordertime=" + temp.getOrdertime() + "&");
		}
		if (!StringUtils.isEmpty(temp.getOrderamount())) {
			sb.append("orderamount=" + temp.getOrderamount() + "&");
		}
		if (!StringUtils.isEmpty(temp.getDealid())) {
			sb.append("dealid=" + temp.getDealid() + "&");
		}
		if (!StringUtils.isEmpty(temp.getDealtime())) {
			sb.append("dealtime=" + temp.getDealtime() + "&");
		}
		if (!StringUtils.isEmpty(temp.getPayamount())) {
			sb.append("payamount=" + temp.getPayamount() + "&");
		}
		if (!StringUtils.isEmpty(temp.getPayresult())) {
			sb.append("payresult=" + temp.getPayresult() + "&");
		}

		if (!StringUtils.isEmpty(sb.toString())) {
			dataString = sb.substring(0, sb.length() - 1);
			System.out.println("组成加密串:" + dataString);
		}
		return dataString;
	}
	/**
	 * 代付地址签名
	 * 
	 * @Description
	 * @author Administrator
	 * @param temp
	 * @return
	 */

	public static String paydaifuResultString(MsgBean req_bean) {

		StringBuffer sb = new StringBuffer();
		String dataString = null;

		if (!StringUtils.isEmpty(req_bean.getMAP())) {
			sb.append("MAP=" + req_bean.getMAP() + "&");
		}
		if (!StringUtils.isEmpty(req_bean.getUSER_NAME())) {
			sb.append("USER_NAME=" + req_bean.getUSER_NAME() + "&");
		}
		if (!StringUtils.isEmpty(req_bean.getBATCH_NO())) {
			sb.append("BATCH_NO=" + req_bean.getBATCH_NO() + "&");
		}
		if (!StringUtils.isEmpty(req_bean.getMERCHANT_ID())) {
			sb.append("MERCHANT_ID=" + req_bean.getMERCHANT_ID() + "&");
		}

		if (!StringUtils.isEmpty(sb.toString())) {
			dataString = sb.substring(0, sb.length() - 1);
			System.out.println("组成加密串:" + dataString);
		}
		return dataString;
	}

}
