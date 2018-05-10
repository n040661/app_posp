package xdt.service.impl;

import java.math.BigDecimal;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.songshun.sdk.entity.BankReq;

import net.sf.json.JSONObject;
import xdt.dao.IPmsDaifuMerchantInfoDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.OriginalOrderInfoDao;
import xdt.model.Jq;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.quickpay.csjq.ConfigContant;
import xdt.quickpay.csjq.HttpClientUtil;
import xdt.service.IJqService;
import xdt.util.HttpURLConection;
import xdt.util.JsonUtil;
import xdt.util.UtilMethod;
import xdt.util.jq.HXT_RSA;
import xdt.util.utils.AESUtil;
import xdt.util.utils.Base64;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.RequestUtils;

@Service
public class JqServiceImpl extends BaseServiceImpl implements IJqService {
	Logger log = Logger.getLogger(this.getClass());
	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao;
	@Resource
	private OriginalOrderInfoDao originalDao;
	@Resource
	private IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao;// 代付
	private static String url = "http://api.verify.web4008.com/api/verify/bankcard";
	private static String key = "MIGdMA0GCSqGSIb3DQEBAQUAA4GLADCBhwKBgQCV3HdksdXnlyrP+2yxODB9T0TU+NJGxGJT/uu61gXHCotLJeNYgwwZwiypCprK8uNk2b8oUbd/CwVaSXqtV8R8Eu5pyad+qK+jQAPoMFhSNlemUJbFm+r6eIRwLQvX2L8GMXWisrN8U1cxVCQgNLFsQTMp8W6dehNBSQaS9Yj5mwIBAw==";
	public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	@Override
	public Map<String, String> select(Jq jq) {
		Map<String, String> map = new HashMap<>();
		BigDecimal b1;
		BigDecimal b2;
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(jq.getSpid());

		String random = get16RandomNumber();// 随机生成16位字符串
		PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
		try {
			List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
			merchantinfo = merchantList.get(0);
			String position = merchantinfo.getPosition();// 商户剩余余额
			b1 = new BigDecimal(position);
			b2 = new BigDecimal(Double.parseDouble(merchantinfo.getAuthentication()));

			if (b1.doubleValue() >= b2.doubleValue() * 100) {
				Double db = b1.subtract(b2).doubleValue();
				model.setMercId(jq.getSpid());
				model.setResponsecode("01");
				model.setOagentno("100333");
				model.setCardno(jq.getCardNo());// 银行卡号
				model.setTransactionType("查询银行卡");
				model.setRemarks("D0");
				model.setIdentity(jq.getIDCardType());// 证件类型
				model.setPmsbankno(jq.getIDCardNo());// 证件号
				model.setRealname(jq.getUserName());// 姓名
				model.setProvince(jq.getTelephoneNo());// 电话
				model.setBatchNo(random);
				model.setPayCounter(merchantinfo.getAuthentication());
				model.setAmount("0");
				model.setPayamount("0");
				model.setPosition(db.toString());
				pmsDaifuMerchantInfoDao.insert(model);
				merchantinfo.setPosition(db.toString());
				pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
				String str = "";
				String login = "changjie";
				String pwd = "changjie299";
				pwd = MD5Utils.md5(pwd, "UTF-8");
				String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());// 生成时间

				String content = "{\"time\":\"" + time + "\",\"aes\":\"" + random + "\"}";
				log.info("没有key的数据content:" + content);

				String sign = HXT_RSA.encrypt(content);

				url = "http://api.verify.web4008.com/api/verify/bankcard?login=" + login + "&pwd=" + pwd + "&signature="
						+ sign;
				log.info("url：" + url);
				String iv = "0000000000000000";
				String CardNo = AESUtil.encrypt(jq.getCardNo(), random, iv);
				String IDCardType = jq.getIDCardType();
				String IDCardNo = AESUtil.encrypt(jq.getIDCardNo(), random, iv);
				String UserName = AESUtil.encrypt(jq.getUserName(), random, iv);
				String TelephoneNo = AESUtil.encrypt(jq.getTelephoneNo(), random, iv);
				String pamer = "{\"CardNo\":\"" + CardNo + "\",\"IDCardType\":\"" + IDCardType + "\",\"IDCardNo\":\""
						+ IDCardNo + "\",\"UserName\":\"" + UserName + "\",\"TelephoneNo\":\"" + TelephoneNo + "\"}";
				log.info("pamer：" + pamer);
				str = RequestUtils.sendPost(url, pamer);
				log.info("str：" + str);
				map = JsonUtil.jsonToMap(str);
				map.put("BatchNo", random);
				if ("00".equals(map.get("Code"))) {
					UpdateDaifu(random, map.get("TransactionNo"), "00");
				} else {
					UpdateDaifu(random, map.get("TransactionNo"), "01");
				}
			} else {
				log.info("余额不足");
				map.put("Code", "01");
				map.put("Data", "余额不足!");
				map.put("BatchNo", random);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		/*
		 * String str=""; String login="changjie"; String pwd="changjie299"; pwd
		 * =MD5Utils.md5(pwd, "UTF-8"); String time =new
		 * SimpleDateFormat("yyyyMMddHHmmss").format(new Date());//生成时间
		 * 
		 * String content="{\"time\":\""+time+"\",\"aes\":\""+random+"\"}";
		 * log.info("没有key的数据content:"+content);
		 * 
		 * String sign = ""; try { sign = HXT_RSA.encrypt(content); } catch (Exception
		 * e1) { // TODO Auto-generated catch block e1.printStackTrace(); } url
		 * ="http://api.verify.web4008.com/api/verify/bankcard?login="+login+"&pwd="+pwd
		 * +"&signature="+sign; log.info("url："+url); String iv ="0000000000000000"; try
		 * { String CardNo=AESUtil.encrypt(jq.getCardNo(), random, iv); String
		 * IDCardType=jq.getIDCardType(); String
		 * IDCardNo=AESUtil.encrypt(jq.getIDCardNo(), random, iv); String
		 * UserName=AESUtil.encrypt(jq.getUserName(), random, iv); String
		 * TelephoneNo=AESUtil.encrypt(jq.getTelephoneNo(), random, iv); String
		 * pamer="{\"CardNo\":\""+CardNo+"\",\"IDCardType\":\""+IDCardType+
		 * "\",\"IDCardNo\":\""+IDCardNo+"\",\"UserName\":\""+UserName+
		 * "\",\"TelephoneNo\":\""+TelephoneNo+"\"}"; log.info("pamer："+pamer); str
		 * =RequestUtils.sendPost(url, pamer); log.info("str："+str); map
		 * =JsonUtil.jsonToMap(str); map.put("BatchNo", random);
		 * if("00".equals(map.get("Code"))){ UpdateDaifu(random,
		 * map.get("TransactionNo"), "00"); }else{ UpdateDaifu(random,
		 * map.get("TransactionNo"), "01"); } } catch (Exception e) { // TODO: handle
		 * exception }
		 */

		return map;
	}

	// 修改代付状态
	public int UpdateDaifu(String batchNo, String str, String responsecode) throws Exception {

		log.info("原始数据:" + batchNo);

		PmsDaifuMerchantInfo pdf = new PmsDaifuMerchantInfo();

		log.info("上送的批次号:" + batchNo);
		pdf.setCity(str);
		pdf.setBatchNo(batchNo);
		pdf.setResponsecode(responsecode);
		return pmsDaifuMerchantInfoDao.update(pdf);
	}

	public static String get16RandomNumber() {

		String[] beforeShuffle = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
				"e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y",
				"z" };

		List<String> list = Arrays.asList(beforeShuffle);

		Collections.shuffle(list);

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i));
		}

		String afterShuffle = sb.toString();
		String result = afterShuffle.substring(0, 16);

		return result;
	}

	public static RSAPublicKey getPubKey(String pubKey) throws Exception {

		System.err.println("pubKey-->" + pubKey);
		RSAPublicKey publicKey = loadPublicKeyByStr(pubKey);

		return publicKey;

	}

	/**
	 * 从字符串中加载公钥
	 * 
	 * @param publicKeyStr
	 *            公钥数据字符串
	 * @throws Exception
	 *             加载公钥时产生的异常
	 */
	public static RSAPublicKey loadPublicKeyByStr(String publicKeyStr) throws Exception {
		try {
			byte[] buffer = Base64.decode(publicKeyStr);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("公钥非法");
		} catch (NullPointerException e) {
			throw new Exception("公钥数据为空");
		}
	}

	// 私钥加密
	public static String sign(String content, String privateKey, String encode) {
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey));

			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

			signature.initSign(priKey);
			signature.update(content.getBytes(encode));

			byte[] signed = signature.sign();

			return Base64.encode(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 长沙松顺数据认证接口
	 * 
	 * @param originalinfo
	 *            下游请求原始数据
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> selectJq(Jq jq) throws Exception {
		// TODO Auto-generated method stub
		Map<String, String> map = new HashMap<>();
		BigDecimal b1;
		BigDecimal b2;
		// 验证当前是否已经下单
		OriginalOrderInfo orig = new OriginalOrderInfo();
		orig.setMerchantOrderId(jq.getOrderId());
		orig.setPid(jq.getSpid());

		if (originalDao.selectByOriginal(orig) != null) {
			log.error("下单重复!");
			map.put("respCode", "20");
			map.put("respMsg", "下单重复");
			return map;
		}

		String orderNumber = UtilMethod.getOrderid("180");// 1、订单号由
															// 业务号（2位）+业务细分（1位）+时间戳（13位）
															// 总共16位
		/**
		 * 插入原始数据信息
		 */
		OriginalOrderInfo original = new OriginalOrderInfo();
		original.setMerchantOrderId(jq.getOrderId());// 原始数据的订单编号
		original.setOrderId(orderNumber); // 为主键
		original.setPid(jq.getSpid());
		originalDao.insert(original);

		// 根据商户号查询
		String mercId = jq.getSpid();

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);

		// o单编号
		String oAgentNo = "";

		// 查询当前商户信息
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

			merchantinfo = merchantList.get(0);
			// merchantinfo.setCustomertype("3");

 			oAgentNo = merchantinfo.getoAgentNo();//

			if (StringUtils.isBlank(oAgentNo)) {
				// 如果没有欧单编号，直接返回错误
				// 如果没有欧单编号，直接返回错误
				log.error("参数错误!");
				map.put("respCode", "16");
				map.put("respMsg", "参数错误,没有欧单编号");
				return map;
			}
			// 判断是否为正式商户
			if ("60".equals(merchantinfo.getMercSts())) {

 				PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
				String position = merchantinfo.getPosition();// 商户剩余余额
				b1 = new BigDecimal(position);
				b2 = new BigDecimal(Double.parseDouble(merchantinfo.getAuthentication()));// 鉴权手续费
				if (b1.doubleValue() >= b2.doubleValue() * 100) {
					Double db = b1.subtract(b2).doubleValue();
					model.setMercId(jq.getSpid());
					model.setResponsecode("01");
					model.setOagentno("100333");
					model.setCardno(jq.getCardNo());// 银行卡号
					model.setTransactionType("查询银行卡");
					model.setRemarks(jq.getType());
					model.setIdentity(jq.getIDCardNo());// 证件类型
					model.setRealname(jq.getUserName());// 姓名
					model.setBatchNo(jq.getOrderId());
					model.setPayCounter(merchantinfo.getAuthentication());
					model.setAmount("0");
					model.setPayamount("0");
					model.setPosition(db.toString());
					pmsDaifuMerchantInfoDao.insert(model);
					merchantinfo.setPosition(db.toString());
					int num = pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
					if (num > 0) {
						BankReq req = new BankReq();
						if ("1".equals(jq.getType())) {
							req.setServiceCode("303");
						} else if ("2".equals(jq.getType())) {
							req.setServiceCode("313");
						}
						req.setName(jq.getUserName());
						req.setIdNumber(jq.getIDCardNo());
						req.setBankCard(jq.getCardNo());
						req.setMobile(jq.getTelephoneNo());
						HashMap<String, Object> map1 = ConfigContant.initBankParams(req);
						String result = HttpClientUtil.invoke(map1);
						log.info("上游返回的响应结果：" + result);
						JSONObject ob1 = JSONObject.fromObject(result);
						Iterator it1 = ob1.keys();
						while (it1.hasNext()) {
							String key1 = (String) it1.next();
							if (key1.equals("key")) {

								String value = ob1.getString(key1);

								map.put("key", value);

							}
							if (key1.equals("msg")) {

								String value = ob1.getString(key1);

								map.put("msg", value);

							}
						}
					}
					map.put("BatchNo", jq.getOrderId());
					map.put("Spid", jq.getSpid());
					if ("0000".equals(map.get("key"))) {
						UpdateDaifu(jq.getOrderId(), "", "00");
					} else {
						UpdateDaifu(jq.getOrderId(), "", "01");
					}
				} else {
					log.info("余额不足");
					map.put("Code", "01");
					map.put("Data", "余额不足!");
					map.put("BatchNo", jq.getOrderId());
				}

			} else {
				// 请求参数为空
				log.info("商户没有进行实名认证，" + merchantinfo.getMercId());
				map.put("respCode", "17");
				map.put("respMsg", "商户没有进行实名认证");
				return map;
			}
		} else {
			log.info("商户没有进行实名认证，" + merchantinfo.getMercId());
			map.put("respCode", "17");
			map.put("respMsg", "商户没有进行实名认证");
			return map;
		}
		return map;
	}
}
