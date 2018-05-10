package xdt.quickpay.qianlong.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import xdt.quickpay.qianlong.model.Merchant;
import xdt.quickpay.qianlong.model.Order;
import xdt.quickpay.qianlong.util.HttpClientHelper;
import xdt.quickpay.qianlong.util.HttpResponse;
import xdt.quickpay.qianlong.util.MyRSAUtils;
import xdt.quickpay.qianlong.util.SdkUtil;
import xdt.quickpay.qianlong.util.SignatureUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;


/**
 * 乾恩扫码收款API
 * @author Jerry
 *
 */
public class ChroneApi {
	
	private final static Logger logger = Logger.getLogger(ChroneApi.class);
	
	public static final String SUCCESS_CODE = "200";//成功

	/**
	 * 商户注册
	 * @param temp
	 */
	public static boolean regist(Merchant temp){
		try{
			Map<String,String> params = new HashMap<String, String>();
			params.put("cardType", temp.getCardType());
			params.put("pmsBankNo", temp.getPmsBankNo());
			params.put("certNo", temp.getCertNo());
			params.put("mobile", temp.getMobile());
			params.put("password", temp.getPassword());
			params.put("cardNo", temp.getCardNo());
			params.put("orgId", SdkUtil.getStringValue("chroneOrgId"));
			params.put("realName", temp.getRealName());
			params.put("certType", temp.getCertType());
			params.put("account", temp.getAccount());
			params.put("mchntName", temp.getMchntName());
			String bigStr = SignatureUtil.hex(params);
			params.put("signature", MyRSAUtils.sign(SdkUtil.getStringValue("chronePrivateKey"), bigStr, MyRSAUtils.MD5_SIGN_ALGORITHM));
			String postData = JSON.toJSONString(params);
			List<String[]> headers = new ArrayList<String[]>();
			headers.add(new String[]{"Content-Type","application/json"});
			HttpResponse response = HttpClientHelper.doHttp(SdkUtil.getStringValue("chroneRegistUrl"), HttpClientHelper.POST, headers, "utf-8", postData, "60000");
			if(StringUtils.isNotEmpty(response.getRspStr())){
				logger.debug("chrone regist result:"+response.getRspStr());
				Map<String,String> retMap = JSON.parseObject(response.getRspStr(), new TypeReference<Map<String,String>>(){});
				if(SUCCESS_CODE.equals(retMap.get("respCode"))){
					return true;
				}
			}
		}catch (Exception e) {
			logger.error("商户注册请求失败");
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 扫码支付
	 * @param order
	 */
	public static String qrpay(Order order){
		try{
			Map<String,String> params = new HashMap<>();
			params.put("orgId", SdkUtil.getStringValue("chroneOrgId"));
			params.put("source", order.getSource()+"");
			params.put("subject", order.getSubject()+"");
			params.put("settleAmt", order.getSettleAmt()+"");
			params.put("account", order.getAccount());
			params.put("amount", order.getAmount()+"");
			params.put("notifyUrl", SdkUtil.getStringValue("chroneNotifyurl"));
			params.put("tranTp", order.getTranTp()+"");
			params.put("orgOrderNo", order.getOrderNo());
			String bigStr = SignatureUtil.hex(params);
			params.put("signature", MyRSAUtils.sign(SdkUtil.getStringValue("chronePrivateKey"), bigStr, MyRSAUtils.MD5_SIGN_ALGORITHM));
			String postData = JSON.toJSONString(params);
			List<String[]> headers = new ArrayList<>();
			headers.add(new String[]{"Content-Type","application/json"});
			HttpResponse response = HttpClientHelper.doHttp(SdkUtil.getStringValue("chroneQrpayUrl"), HttpClientHelper.POST, headers, "utf-8", postData, "60000");
			if(StringUtils.isNotEmpty(response.getRspStr())){
				logger.debug("chrone regist result:"+response.getRspStr());
				Map<String,String> retMap = JSON.parseObject(response.getRspStr(), new TypeReference<Map<String,String>>(){});
				if(SUCCESS_CODE.equals(retMap.get("respCode"))){
					return retMap.get("qrcode");
				}
			}
		}catch (Exception e) {
			logger.error("获取二维码失败");
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 扫码支付
	 * @param order
	 */
	public static Map<String,String> fqrpay(Order order){
		Map<String,String> params = new HashMap<>();
		params.put("orgId", SdkUtil.getStringValue("chroneOrgId"));
		params.put("source", order.getSource()+"");
		params.put("settleAmt", order.getSettleAmt()+"");
		params.put("account", order.getAccount());
		params.put("amount", order.getAmount()+"");
		params.put("notifyUrl", SdkUtil.getStringValue("chroneNotifyurl"));
		params.put("callbackUrl", SdkUtil.getStringValue("chroneCallbackUrl"));
		params.put("tranTp", order.getTranTp()+"");
		params.put("orgOrderNo", order.getOrderNo());
		String bigStr = SignatureUtil.hex(params);
		params.put("signature", MyRSAUtils.sign(SdkUtil.getStringValue("chronePrivateKey"), bigStr, MyRSAUtils.MD5_SIGN_ALGORITHM));
		return params;
	}
	
	/**
	 * 付款结果查询
	 * @param orderNo:源收款訂單號
	 * @return
	 */
	public static Map<String,String> payforQuery(String orderNo){
		try{
			Map<String,String> params = new HashMap<>();
			params.put("orgId", SdkUtil.getStringValue("chroneOrgId"));
			params.put("orderNo", orderNo);
			String bigStr = SignatureUtil.hex(params);
			params.put("signature", MyRSAUtils.sign(SdkUtil.getStringValue("chronePrivateKey"), bigStr, MyRSAUtils.MD5_SIGN_ALGORITHM));
			String postData = JSON.toJSONString(params);
			List<String[]> headers = new ArrayList<>();
			headers.add(new String[]{"Content-Type","application/json"});
			HttpResponse response = HttpClientHelper.doHttp(SdkUtil.getStringValue("chronePayforQueryUrl"), HttpClientHelper.POST, headers, "utf-8", postData, "60000");
			if(StringUtils.isNotEmpty(response.getRspStr())){
				logger.debug("chrone regist result:"+response.getRspStr());
				Map<String,String> retMap = JSON.parseObject(response.getRspStr(), new TypeReference<Map<String,String>>(){});
				return retMap;
			}
		}catch (Exception e) {
			logger.error("付款查询接口失败");
			e.printStackTrace();
		}
		return null;
	}
}
