package xdt.service.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hslf.util.SystemTimeUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.etonepay.b2c.utils.MD5;
import com.innovatepay.merchsdk.DefaultChinaInPayClient;
import com.innovatepay.merchsdk.request.ChinaInPayQuickPayRequest;
import com.innovatepay.merchsdk.request.ChinaInPayRequest;
import com.kspay.AESUtil;
import com.kspay.MD5Util;
import com.sun.jndi.toolkit.url.Uri;
import com.yufusoft.payplatform.security.cipher.YufuCipher;
import com.yufusoft.payplatform.security.vo.ParamPacket;
import sun.rmi.runtime.Log;
import xdt.dao.ChannleMerchantConfigKeyDao;
import xdt.dao.IAppRateConfigDao;
import xdt.dao.IMerchantMineDao;
import xdt.dao.IPayBankInfoDao;
import xdt.dao.IPayCmmtufitDao;
import xdt.dao.IPmsAddressDao;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPmsDaifuMerchantInfoDao;
import xdt.dao.IPmsGoodsDao;
import xdt.dao.IPmsGoodsOrderDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPmsMessageDao;
import xdt.dao.IPmsOrderHelpDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dao.IQuickpayPreRecordDao;
import xdt.dao.IQuickpayRecordDao;
import xdt.dao.OriginalOrderInfoDao;
import xdt.dto.BaseUtil;
import xdt.dto.QuickpayCheckLocalCardRequestDTO;
import xdt.dto.QuickpayCheckLocalCardResponseDTO;
import xdt.dto.code.SmsBalanceRequest;
import xdt.dto.code.SmsBalanceResponse;
import xdt.dto.code.SmsSendRequest;
import xdt.dto.code.SmsSendResponse;
import xdt.dto.code.SmsVariableRequest;
import xdt.dto.code.SmsVariableResponse;
import xdt.dto.cx.CXUtil;
import xdt.dto.hj.HJUtil;
import xdt.dto.hlb.Disguiser;
import xdt.dto.hlb.HLBUtil;
import xdt.dto.hlb.HttpClientService;
import xdt.dto.hlb.MyBeanUtils;
import xdt.dto.hm.AesEncryption;
import xdt.dto.hm.HMUtil;
import xdt.dto.hm.HttpsUtil;
import xdt.dto.hm.SHA256Util;
import xdt.dto.hm.TimeUtil;
import xdt.dto.lhzf.LhzfUtil;
import xdt.dto.lhzf.MerchantApiUtil;
import xdt.dto.mb.DemoBase;
import xdt.dto.mb.HttpService;
import xdt.dto.mb.MBUtil;
import xdt.dto.quickPay.entity.ConsumeRequestEntity;
import xdt.dto.quickPay.entity.ConsumeResponseEntity;
import xdt.dto.quickPay.entity.MessAgeResponseEntity;
import xdt.dto.quickPay.entity.MessageRequestEntity;
import xdt.dto.quickPay.entity.QueryRequestEntity;
import xdt.dto.yf.BatchDisburseApplyReq;
import xdt.dto.yf.BatchDisburseApplyRsp;
import xdt.dto.yf.DTOUtil;
import xdt.dto.yf.DisburseClientUtil;
import xdt.dto.yf.DoYf;
import xdt.dto.yf.GsonUtil;
import xdt.dto.yf.MD5FileUtil;
import xdt.dto.yf.PayReq;
import xdt.dto.yf.PostUtils;
import xdt.dto.yf.QuickReq;
import xdt.dto.yf.YFUtil;
import xdt.dto.yf.YufuCipherSupport;
import xdt.dto.ys.HttpUtils;
import xdt.dto.ys.SwpHashUtil;
import xdt.dto.ys.YSUtil;
import xdt.model.AppRateConfig;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PayBankInfo;
import xdt.model.PayCmmtufit;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PmsWeixinMerchartInfo;
import xdt.model.PospTransInfo;
import xdt.model.QuickpayCardRecord;
import xdt.model.ResultInfo;
import xdt.model.SessionInfo;
import xdt.model.ViewKyChannelInfo;
import xdt.quickpay.gyy.util.ApiUtil;
import xdt.quickpay.hddh.util.Base64;
import xdt.quickpay.hddh.util.RSAUtil;
import xdt.quickpay.hf.comm.SampleConstant;
import xdt.quickpay.hf.util.EffersonPayService;
import xdt.quickpay.hf.util.PlatBase64Utils;
import xdt.quickpay.hf.util.PlatKeyGenerator;
import xdt.quickpay.jbb.util.RSAEncrypt;
import xdt.quickpay.mobao.MobaoPayHandel;
import xdt.quickpay.nbs.common.util.SignatureUtil;
import xdt.quickpay.syys.HttpClientUtil;
import xdt.quickpay.syys.PayCore;
import xdt.quickpay.yb.util.YeepayService;
import xdt.service.HfQuickPayService;
import xdt.service.IMerchantCollectMoneyService;
import xdt.service.IPmsMessageService;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.IQuickPayService;
import xdt.service.PmsWeixinMerchartInfoService;
import xdt.servlet.AppPospContext;
import xdt.util.BeanToMapUtil;
import xdt.util.ChuangLanSmsUtil;
import xdt.util.EncodeUtil;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
import xdt.util.UtilMethod;
import xdt.util.utils.RequestUtils;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.servlet.http.HttpSession;

@Service
public class QuickpayServiceImpl extends BaseServiceImpl implements IQuickPayService {

	private Logger logger = Logger.getLogger(QuickpayServiceImpl.class);
	// 普通短信发送地址
	private static final String smsSingleRequestServerUrl = "http://smssh1.253.com/msg/send/json";

	// 查询余额发送地址
	private static final String smsBalanceRequestUrl = "http://smssh1.253.com/msg/balance/json";

	// 变量短信发送地址
	private static final String smsVariableRequestUrl = "http://smssh1.253.com/msg/variable/json";

	public static final String[] TRADEORDER = { "parentMerchantNo", "merchantNo", "orderId", "orderAmount",
			"timeoutExpress", "requestDate", "redirectUrl", "notifyUrl", "goodsParamExt", "paymentParamExt",
			"industryParamExt", "memo", "riskParamExt", "csUrl" };

	// 账号
	private static final String ACCOUNT = "N6371333";
	// 密码
	private static final String PSWD = "YkcIJls2eU1528";
	// 短信内容
	private static final String MSG = "【靓帅科技】验证码{$var}，你于{$var}进行快捷短信获取[请勿泄露验证码，任何索取行为均可能涉 嫌 诈 骗]";
	// 短信内容
	private static final String REPORT = "true";
	//上海漪雷快捷公钥
	private static String PUBLIC_KEY="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqFnpu8cNLuSdT4oGrLJjr5G/UjiQsBIwP5mQWpOgTdGaee6ObdNS6bk/ulckSL0sh9QNaYq9iKHBREDZONxVoaKRwncRBR51Q7z682BMuhC4M4IZ4vvqHF4fmq024AHVq+weQbEIzyjex39pupkRU2OjFJ/4WutngH+/TnEMzkWIf+enMbL06VlpXu6LuGp2X+XxONATyeHvYOJANT8L1cONhvcbaKHdUqw7KAQt8s/grYse+Optyl17v4vNoPMTy1mc215rs//mCh6p5yHpThkpcC4QWiw25zvb+PI3SytOac5f7SfNcNWa7CmDcRce/pG/zbBb9Sq1I7HIiw9lYwIDAQAB";
	//上海漪雷快捷私钥
	private static String PRIVACE_KEY="MIIEuwIBADANBgkqhkiG9w0BAQEFAASCBKUwggShAgEAAoIBAQCoWem7xw0u5J1PigassmOvkb9SOJCwEjA/mZBak6BN0Zp57o5t01LpuT+6VyRIvSyH1A1pir2IocFEQNk43FWhopHCdxEFHnVDvPrzYEy6ELgzghni++ocXh+arTbgAdWr7B5BsQjPKN7Hf2m6mRFTY6MUn/ha62eAf79OcQzORYh/56cxsvTpWWle7ou4anZf5fE40BPJ4e9g4kA1PwvVw42G9xtood1SrDsoBC3yz+Ctix746m3KXXu/i82g8xPLWZzbXmuz/+YKHqnnIelOGSlwLhBaLDbnO9v48jdLK05pzl/tJ81w1ZrsKYNxFx7+kb/NsFv1KrUjsciLD2VjAgMBAAECggEACyYJKHpCETzqff3x+zXFDDdmqBc+3aoWr9+Hg7gLAZxD0pdNr1AzSW8PHVPv/zEn5cbRLJAXhRjRJ6fzuqQ8t3uAk5Q2+FPQCDBXng5ehmPdDuwqsrFkC4TgKuoVnDgC4mO8jTUgFDfsqOFzKvw//Xm3rwH4/GxiakwLazJpWq98i+CxGBGSA3dDhpUb3dSoUkiV0qTfLcBDa+nyjF6ilISGa0pPpIiTeYayWLRLzf6I+W8cG0VpQAJXOFo1H4cZJhymB0oH1j00e/IKOEpO+G8fFWdxW8nh82llqFaoJeGm3MmRCelqBp800D9F6xShX62A59nDY+NvC9z8bvgggQKBgQDfwik/2W8iuxWhdpM55vxvJK3RQw67pQFsCV/HlM1rJcKJwWKnUm4+sTUAFTHGEnceJj7BBZSx6kDkI9Hmw35jGs4dKXWR6RL2a9pOMZudwpHb0Oghxm94+rxx7hXMHBP8Mu2zvZxTwGSYjKs1Kp0EnL+5i+q2jaj+vXwB674u8wKBgQDAm+4+PtyXIif/t7b1J4TJtlQyLFXhx/Kck9SrsKd8KoMVPhrE2RQ9D8KBLzcfxzLVZ+mmt40mcExMa0X3R2HGUz2g94KAlvTMKJ4gHRK5zi1vzZrx7PgY7BB1B4oLmDddobw6uiK4NmKmYwcGpS0ohUdBKcjCF5c/DX4xggbr0QJ/dD9rLsDH/EM1+ayg0HQwsY1cwFsWTGZtVrOIDyg/kGsNpoPRvRwWKnvmDST1tvHg8Mjt0VoU5lnNXLk/U6Q9BT/n1T601hlAwMVHpVgggNWU8Z6W+vUc9L1PKeGHcYMk3uGWnMrlbJ2HpblvOS/qY3sMIFmQl0cBhVbsFhNjuwKBgBQtwe3/g/jJeUtPIfnZJA5F7dg70NuQqRhCXJuILGPTyFvnX8KTw40KI6SJH8tSgT7eXho7TKxkQ3oWGwRnFBVFD0XX6HI0Xn0tHDPdF+MjeJsn/T2vR+bEhIzeN1YzoklK8n9slMqb2AX6hffqQirmm6p2CDRdaFkQvtHM/5ChAoGBAKdGc9zqT/6uzFJ56HM+TRsqKC7pXsh69ZQlfMaYX3xiELC/+t3j804Lwmmn1VLqlm8BDUeROnw83im4++PEZ2EHCGB5cUyJLUueOsJnEmqQhExuxKHaplg0SSTG9NrwxxwNtm5CArmFfTHGifKGQuTCrmEYOgJCUhDl8RwJQ0UE";
	@Resource
	private IQuickpayRecordDao iQuickpayRecordDao;
	@Resource
	private IPayCmmtufitDao iPayCmmtufitDao;

	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao; // 商户信息服务层

	@Resource
	private OriginalOrderInfoDao originalDao;

	@Resource
	private IPmsGoodsDao pmsGoodsDao;

	@Resource
	private IMerchantMineDao merchantMineDao;
	@Resource
	public IPmsAppTransInfoDao pmsAppTransInfoDao;
	@Resource
	private IPmsGoodsOrderDao pmsGoodsOrderDao;
	@Resource
	private IPmsAddressDao pmsAddressDao;
	@Resource
	private IPmsOrderHelpDao pmsOrderHelpDao;
	@Resource
	private IPublicTradeVerifyService iPublicTradeVerifyService;
	@Resource
	private MobaoPayHandel mobaoPayHandel;
	@Resource
	private IQuickpayRecordDao quickpayRecordDao;
	@Resource
	private IPospTransInfoDAO pospTransInfoDAO;
	@Resource
	private IMerchantCollectMoneyService merchantCollectMoneyService;
	@Resource
	private IPmsMessageService pmsMessageService;
	@Resource
	IPmsMessageDao pmsMessageDao;
	@Resource
	private IAppRateConfigDao appRateConfigDao;
	@Resource
	private IQuickpayPreRecordDao quickpayPreRecordDao;
	@Resource
	private IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao;// 代付
	/**
	 * 渠道商户密钥信息
	 */
	@Resource
	private ChannleMerchantConfigKeyDao cmckeyDao;
	@Resource
	private IPayBankInfoDao payBankInfoDao;
	@Resource
	public PmsWeixinMerchartInfoService weixinService;
	@Resource
	private HfQuickPayService payService;

	/**
	 * 检查本地是否记录了这张卡的快捷支付信息
	 * 
	 * @param request
	 * @return '0' : 没有这张卡 '1':存在这张卡
	 */
	@Override
	public String checkLocalCardRecord(HttpSession session, String request) {

		String jsonString = "";
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
		QuickpayCheckLocalCardResponseDTO quckpayCheckLocalCardResponse = new QuickpayCheckLocalCardResponseDTO();
		QuickpayCheckLocalCardRequestDTO requestDTO = null;
		// 判断登录信息
		if (sessionInfo != null) {
			// 判断当前回话中是否存在欧单编号，不存在直接返回错误
			String oAgentNo = sessionInfo.getoAgentNo();
			if (StringUtils.isBlank(oAgentNo)) {
				// 未登录
				quckpayCheckLocalCardResponse.setRetCode(13);
				quckpayCheckLocalCardResponse.setRetMessage("会话过期，请重新登陆");
				try {
					jsonString = createJsonString(quckpayCheckLocalCardResponse);
				} catch (Exception em) {
					em.printStackTrace();
				}
				return jsonString;
			}

			// 判断请求体
			if (StringUtils.isNotBlank(request)) {

				// 解析请求对象
				try {
					requestDTO = (QuickpayCheckLocalCardRequestDTO) parseJsonString(request,
							QuickpayCheckLocalCardRequestDTO.class);
				} catch (Exception e) {
					quckpayCheckLocalCardResponse.setRetCode(1);
					quckpayCheckLocalCardResponse.setRetMessage("参数出错");
					logger.info("参数出错");
					// 参数出错
					try {
						jsonString = createJsonString(quckpayCheckLocalCardResponse);
					} catch (Exception em) {
						em.printStackTrace();
					}
					return jsonString;

				}

				if (requestDTO != null && StringUtils.isNotBlank(requestDTO.getCardNo())) {

					// 查看数据库
					try {

						QuickpayCardRecord quickpayCardRecord = iQuickpayRecordDao.searchById(requestDTO.getCardNo());
						if (quickpayCardRecord != null) {
							// 存在当前用户
							quckpayCheckLocalCardResponse.setRetCode(0);
							quckpayCheckLocalCardResponse.setRetMessage("查询成功");
							quckpayCheckLocalCardResponse.setStatus("1");
							quckpayCheckLocalCardResponse.setCardType(quickpayCardRecord.getCardType());
						} else {
							// 查询卡宾 判断当前卡的卡类型
							PayCmmtufit payCmmtufit = iPayCmmtufitDao.selectByCardNum(requestDTO.getCardNo());
							if (payCmmtufit != null) {
								// 没有当前用户
								quckpayCheckLocalCardResponse.setRetCode(0);
								quckpayCheckLocalCardResponse.setCardType(payCmmtufit.getCrdFlg());
								quckpayCheckLocalCardResponse.setRetMessage("查询成功");
								quckpayCheckLocalCardResponse.setStatus("0");
							} else {
								quckpayCheckLocalCardResponse.setStatus("0");
								quckpayCheckLocalCardResponse.setCardType(payCmmtufit.getCrdFlg());
								quckpayCheckLocalCardResponse.setRetCode(1);
								quckpayCheckLocalCardResponse.setRetMessage("卡宾不存在，请联系客服或换卡重试");

							}

						}
					} catch (Exception e) {
						logger.info(e.getMessage());
						quckpayCheckLocalCardResponse.setRetCode(1);
						quckpayCheckLocalCardResponse.setRetMessage("参数出错");
						logger.info("参数出错");
						// 参数出错
						try {
							jsonString = createJsonString(quckpayCheckLocalCardResponse);
						} catch (Exception em) {
							em.printStackTrace();
						}
						return jsonString;
					}

				} else {
					quckpayCheckLocalCardResponse.setRetCode(1);
					quckpayCheckLocalCardResponse.setRetMessage("参数出错");
					logger.info("参数出错");
					// 参数出错
					try {
						jsonString = createJsonString(quckpayCheckLocalCardResponse);
					} catch (Exception em) {
						em.printStackTrace();
					}
					return jsonString;
				}
			} else {
				// 参数为空
				quckpayCheckLocalCardResponse.setRetCode(1);
				quckpayCheckLocalCardResponse.setRetMessage("参数不正确");
				try {
					jsonString = createJsonString(quckpayCheckLocalCardResponse);
				} catch (Exception em) {
					em.printStackTrace();
				}
				return jsonString;
			}

		} else {
			quckpayCheckLocalCardResponse.setRetCode(13);
			quckpayCheckLocalCardResponse.setRetMessage("会话过期，请重新登陆");
			try {
				jsonString = createJsonString(quckpayCheckLocalCardResponse);
				logger.info("检查卡类型返回：" + sessionInfo.getMercId() + "," + request + "," + quckpayCheckLocalCardResponse);

			} catch (Exception em) {
				em.printStackTrace();
			}
			return jsonString;
		}

		return null;
	}

	public ChannleMerchantConfigKey getChannelConfigKey(String merchantId) throws Exception {

		logger.info("获取商户密钥信息");
		return cmckeyDao.get(merchantId);
	}

	public OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception {

		OriginalOrderInfo original = null;
		// 查询流水信息
		PospTransInfo transInfo = pospTransInfoDAO.searchBytransOrderId(tranId);
		String oderId = transInfo.getOrderId();
		logger.info("根据上送订单号  查询商户上送原始信息");
		original = originalDao.getOriginalOrderInfoByOrderid(oderId);
		return original;
	}

	public Map<String, String> updateHandle(MessageRequestEntity originalinfo) throws Exception {
		logger.info("请求参数："+JSON.toJSONString(originalinfo));
		Map<String, String> retMap = new HashMap<String, String>();
		// 商户号
		String merchId = originalinfo.getV_mid();
		// 金额
		String acount = originalinfo.getV_txnAmt();
		// 商户订单号
		logger.info("******************根据商户号查询");

		// 验证当前是否已经下单
		OriginalOrderInfo orig = new OriginalOrderInfo();
		orig.setMerchantOrderId(originalinfo.getV_oid());
		orig.setPid(originalinfo.getV_mid());

		if (originalDao.selectByOriginal(orig) != null) {
			logger.info("下单重复");
			return setResp("03", "下单重复");
		}

		// String orderNumber = UtilMethod.getOrderid("180");// 1、订单号由
		// 业务号（2位）+业务细分（1位）+时间戳（13位）
		// 总共16位
		/**
		 * 插入原始数据信息
		 */
		logger.info("来了！111");
		OriginalOrderInfo original = new OriginalOrderInfo();
		original.setMerchantOrderId(originalinfo.getV_oid());// 原始数据的订单编号
		original.setOrderId(originalinfo.getV_oid()); // 为主键
		original.setPid(originalinfo.getV_mid());
		original.setOrderTime(originalinfo.getV_time());
		original.setOrderAmount(originalinfo.getV_txnAmt());
		original.setProcdutName(originalinfo.getV_productDesc());
		original.setProcdutDesc(originalinfo.getV_productDesc());
		original.setPayType(originalinfo.getV_type());
		original.setPageUrl(originalinfo.getV_url());
		original.setBgUrl(originalinfo.getV_notify_url());
		original.setBankNo(originalinfo.getV_cardNo());
		original.setRealName(originalinfo.getV_realName());
		if (originalinfo.getV_cert_no() != null) {
			original.setCertNo(originalinfo.getV_cert_no());
		}
		if (originalinfo.getV_phone() != null) {
			original.setPhone(originalinfo.getV_phone());
		}
		if (originalinfo.getV_pmsBankNo() != null) {
			original.setBankId(originalinfo.getV_pmsBankNo());
		}
		if ("0".equals(originalinfo.getV_userFee()) && originalinfo.getV_userFee() != null) {
			original.setUserFee(originalinfo.getV_userFee());
		}

		if (originalinfo.getV_settlePmsBankNo() != null) {
			original.setSettlePmsBankNo(originalinfo.getV_settlePmsBankNo());
		}
		if (originalinfo.getV_settleCardNo() != null) {
			original.setSettleCardNo(originalinfo.getV_settleCardNo());
		}
		if (originalinfo.getV_settleUserFee() != null) {
			original.setSettleUserFee(originalinfo.getV_settleUserFee());
		}
		if (originalinfo.getV_settleName() != null) {
			original.setSettleUserName(originalinfo.getV_settleName());
		}
		if (originalinfo.getV_cvn2() != null) {
			original.setCvn2(originalinfo.getV_cvn2());
		}
		if (originalinfo.getV_expired() != null) {
			original.setExpired(originalinfo.getV_expired());
		}
		if (originalinfo.getV_attach() != null) {
			original.setAttach(originalinfo.getV_attach());
		}
		if (originalinfo.getV_userFee() != null) {
			original.setUserFee(originalinfo.getV_userFee());
		}
		logger.info("来了！222");
		if (originalinfo.getV_userId() != null) {
			logger.info("来了！333");
			original.setUserId(originalinfo.getV_userId());
			logger.info("来了！444");
		}
		logger.info("来了！555");
		if (originalinfo.getV_verifyId() != null) {
			original.setVerifyId(originalinfo.getV_verifyId());
		}
		original.setBankType(originalinfo.getV_accountType());
		originalDao.insert(original);

		// 根据商户号查询
		String mercId = originalinfo.getV_mid();

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);

		// o单编号
		String oAgentNo = "";

		// 查询当前商户信息
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		if (!(merchantList.size() == 0 || merchantList.isEmpty())) {
			merchantinfo = merchantList.get(0);
			// merchantinfo.setCustomertype("3");
			
			oAgentNo = merchantinfo.getoAgentNo();

			if (StringUtils.isBlank(oAgentNo)) {
				// 如果没有欧单编号，直接返回错误
				logger.error("参数错误!");
				retMap.put("v_code", "04");
				retMap.put("v_msg", "参数错误,没有欧单编号");
				return retMap;
			}
			// 判断是否为正式商户
			if ("60".equals(merchantinfo.getMercSts())) {
				logger.info("是正式商户");
				// 实际金额
				String factAmount = "" + new BigDecimal(originalinfo.getV_txnAmt()).multiply(new BigDecimal(100));
				// 查询商户路由
				PmsBusinessPos pmsBusinessPos = selectKey(originalinfo.getV_mid());
				// 校验欧单金额限制
				ResultInfo payCheckResult = iPublicTradeVerifyService
						.amountVerifyOagent((int) Double.parseDouble(factAmount), TradeTypeEnum.onlinePay, oAgentNo);
				if (!payCheckResult.getErrCode().equals("0")) {
					// 交易不支持
					logger.info("欧单金额限制，oAagentNo:" + oAgentNo + ",payType:"
							+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
					return setResp("05", "欧单金额限制，请重试或联系客服");
				}

				// 校验欧单模块是否开启
				ResultInfo resultInfoForOAgentNo = iPublicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.onlinePay,
						oAgentNo);
				if (!resultInfoForOAgentNo.getErrCode().equals("0")) {
					// 交易不支持
					if (StringUtils.isEmpty(resultInfoForOAgentNo.getMsg())) {
						logger.error("交易关闭，请重试或联系客服");
						return setResp("06", "交易关闭，请重试或联系客服");
					} else {
						return setResp("07", "系统异常，请重试或联系客服");
					}

				}
				// 校验商户模块是否开启
				ResultInfo payCheckResult3 = iPublicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.onlinePay, mercId);
				if (!payCheckResult3.getErrCode().equals("0")) {
					// 交易不支持
					logger.info("商户模块限制，oAagentNo:" + oAgentNo + ",payType:"
							+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
					return setResp("08", "商户模块限制,请重试或联系客服");
				}
				// 校验商户金额限制
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("mercid", merchantinfo.getMercId());// 商户编号
				paramMap.put("businesscode", TradeTypeEnum.onlinePay.getTypeCode());// 业务编号
				paramMap.put("oAgentNo", oAgentNo);
				// 商户 网购 业务信息
				Map<String, String> resultMap = merchantMineDao.queryBusinessInfo(paramMap);

				if (resultMap == null || resultMap.size() == 0) {
					// 若查到的是空值，直接返回错误
					logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
					return setResp("09", "没有查到相关费率配置,,请重试或联系客服");
				}

				String maxTransMoney = resultMap.get("MAX_AMOUNT"); // 每笔最大交易金额
				String minTransMoney = resultMap.get("MIN_AMOUNT"); // 每笔最小交易金额
				String paymentAmount = factAmount;// 交易金额

				if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(maxTransMoney)) == 1) {
					// 金额超过最大金额
					logger.info("交易金额大于最打金额");
					return setResp("10", "金额超过最大交易金额");
				} else if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(minTransMoney)) == -1) {
					// 金额小于最小金额
					logger.info("交易金额小于最小金额");
					return setResp("11", "交易金额小于最小金额");

				}

				// 组装订单数据
				PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
				// 写入欧单编号
				pmsAppTransInfo.setoAgentNo(oAgentNo);
				pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());// 订单初始化状态
				pmsAppTransInfo.setTradetype(TradeTypeEnum.onlinePay.getTypeName());// 业务功能模块名称
																					// ：网购
				pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter()); // 设置时间
				pmsAppTransInfo.setMercid(merchantinfo.getMercId());
				pmsAppTransInfo.setTradetypecode(TradeTypeEnum.onlinePay.getTypeCode());// 业务功能模块编号
																						// ：17
				pmsAppTransInfo.setOrderid(originalinfo.getV_oid());// 设置订单号
				pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.hengFengQuickPay.getTypeName());
				pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.hengFengQuickPay.getTypeCode());
				BigDecimal factBigDecimal = new BigDecimal(factAmount);
				BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);

				pmsAppTransInfo.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());// 实际金额
				pmsAppTransInfo.setOrderamount(orderAmountBigDecimal.stripTrailingZeros().toPlainString());// 订单金额
				pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
				if ("0".equals(originalinfo.getV_type())) {
					pmsAppTransInfo.setSettlementState("D0");
				}
				if ("1".equals(originalinfo.getV_type())) {

					pmsAppTransInfo.setSettlementState("T1");
				}

				// 插入订单信息
				Integer insertAppTrans = pmsAppTransInfoDao.insert(pmsAppTransInfo);
				if (insertAppTrans == 1) {

					// 查询订单信息
					pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pmsAppTransInfo.getOrderid());

					String quickRateType = resultMap.get("QUICKRATETYPE").toString();// 快捷支付费率类型

					// 获取o单第三方支付的费率
					AppRateConfig appRate = new AppRateConfig();
					appRate.setRateType(quickRateType);
					appRate.setoAgentNo(oAgentNo);
					AppRateConfig appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRate);

					if (appRateConfig == null) {
						// 若查到的是空值，直接返回错误
						// 若查到的是空值，直接返回错误
						logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
						return setResp("09", "没有查到相关费率配置,,请重试或联系客服");

					}

					String isTop = appRateConfig.getIsTop();
					String rate = appRateConfig.getRate();
					String topPoundage = appRateConfig.getTopPoundage();// 封顶手续费
					paymentAmount = pmsAppTransInfo.getFactamount();// 支付金额
					String minPoundageStr = appRateConfig.getBottomPoundage();// 最低手续费
					Double minPoundage = 0.0; // 附加费

					if (StringUtils.isNotBlank(appRateConfig.getIsBottom())
							&& appRateConfig.getIsBottom().equals("1")) {// 是否有清算费用，"1":有，“0”无
						if (StringUtils.isNotBlank(minPoundageStr)) {
							minPoundage = Double.parseDouble(minPoundageStr); // 清算手续费
						} else {
							// 若查到的是空值，直接返回错误
							logger.info("没有查到相关费率附加费（最低手续费）：" + merchantinfo.getMobilephone());
							return setResp("09", "没有查到相关费率配置,,请重试或联系客服");
						}
					}

					BigDecimal payAmount = new BigDecimal("0");
					BigDecimal dfactAmount = new BigDecimal(pmsAppTransInfo.getFactamount());
					// 费率
					BigDecimal fee = new BigDecimal(0);
					Double settleFee = 0.0;
					Double userfee = 0.0;
					String rateStr = "";
					// Double payfee = null;
					// 计算结算金额
					if ("1".equals(isTop)) {

						rateStr = rate + "-" + topPoundage;
						// 是封顶费率类型
						fee = new BigDecimal(rate).multiply(dfactAmount);

						if (fee.compareTo(new BigDecimal(topPoundage)) == 1) {
							// 手续费大于封顶金额，按封顶金额处理
							payAmount = dfactAmount
									.subtract(new BigDecimal(topPoundage).subtract(new BigDecimal(minPoundage)));
							fee = new BigDecimal(topPoundage).add(new BigDecimal(minPoundage));
						} else {
							// 按当前费率处理
							rateStr = rate;
							fee.add(new BigDecimal(minPoundage));
							payAmount = dfactAmount.subtract(fee);
						}

					} else {
						// 按当前费率处理
						double dfpag = Double.parseDouble(merchantinfo.getPoundage());
						double daifu = Double.parseDouble(merchantinfo.getCounter());
						if (!"".equals(originalinfo.getV_userFee()) && originalinfo.getV_userFee() != null) {
							userfee = Double.parseDouble(originalinfo.getV_userFee());
						}

						if (!"".equals(originalinfo.getV_settleUserFee())
								&& originalinfo.getV_settleUserFee() != null) {
							settleFee = Double.parseDouble(originalinfo.getV_settleUserFee());
						}
						if (originalinfo.getV_type().equals("1")) {
							// 按当前费率处理
							rateStr = rate;
							if (Double.parseDouble(rateStr) <= userfee) {
								BigDecimal num = dfactAmount.multiply(new BigDecimal(userfee));
								if (num.doubleValue() / 100 >= daifu) {
									fee = num;
								} else {
									fee = new BigDecimal(daifu * 100);
								}
								rateStr = userfee.toString();
								payAmount = dfactAmount.subtract(fee);
								logger.info("清算金额:" + paymentAmount);
								if (payAmount.doubleValue() < 0) {
									payAmount = new BigDecimal(0.00);
								}

							} else {
								logger.info("费率低于成本费率：" + merchantinfo.getMercId());
								return setResp("12", "费率低于成本费率");
							}
						} else if (originalinfo.getV_type().equals("0")) {

							// 按当前费率处理
							rateStr = rate;
							if (Double.parseDouble(rateStr) <= userfee) {
								fee = new BigDecimal(userfee).multiply(dfactAmount).add(new BigDecimal(minPoundage));
							} else {
								logger.info("费率低于成本费率：" + merchantinfo.getMercId());
								return setResp("12", "费率低于成本费率");

							}
							if ("10044".equals(pmsBusinessPos.getBusinessnum())) {
								if (dfpag > settleFee) {
									logger.info("手续费低于最小手续费：" + merchantinfo.getMercId());
									return setResp("20", "手续费低于最小手续费");
								}

							} else {

								if (dfpag > settleFee) {
									settleFee = dfpag;
								}
							}
							switch (pmsBusinessPos.getBusinessnum()) {

							case "1711030001":// 沈阳银盛
								payAmount = dfactAmount.subtract(fee).subtract(new BigDecimal(100));
								fee = fee.add(new BigDecimal(100));
								break;
							case "88882017092010001121":// 赢酷快捷
								payAmount = dfactAmount.subtract(fee).subtract(new BigDecimal(20));
								fee = fee.add(new BigDecimal(20));
								break;
							default:
								payAmount = dfactAmount.subtract(fee)
										.subtract(new BigDecimal(settleFee).multiply(new BigDecimal(100)));
								fee = fee.add(new BigDecimal(settleFee).multiply(new BigDecimal(100)));
								break;

							}
							logger.info("清算金额:" + paymentAmount);
							if (payAmount.doubleValue() < 0) {
								payAmount = new BigDecimal(0.00);
							}
						}
					}

					// 设置结算金额
					pmsAppTransInfo.setPayamount(payAmount.toString());// 结算金额
					pmsAppTransInfo.setRate(rateStr);// 0.50_35 || 0.50
					pmsAppTransInfo.setPoundage(fee.toString());
					pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
					// 转换double为int
					Integer paymentAmountInt = (int) Double.parseDouble(paymentAmount);

					// 验证支付方式是否开启
					payCheckResult = iPublicTradeVerifyService.totalVerify(paymentAmountInt, TradeTypeEnum.onlinePay,
							PaymentCodeEnum.hengFengQuickPay, oAgentNo, merchantinfo.getMercId());
					if (!payCheckResult.getErrCode().equals("0")) {
						// 交易不支持

						logger.info("不支持的支付方式，oAagentNo:" + oAgentNo + ",payType:"
								+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
						return setResp("13", "暂不支持该交易方式");
					}
					ViewKyChannelInfo channelInfo = AppPospContext.context.get(HENGFENGPAY + HENGFENGCHANNELNUM);

					// 设置通道信息
					pmsAppTransInfo.setBusinessNum(channelInfo.getBusinessnum());
					pmsAppTransInfo.setChannelNum(HENGFENGCHANNELNUM);

					// 查看当前交易是否已经生成了流水表
					PospTransInfo pospTransInfo = null;
					// 流水表是否需要更新的标记 0 insert，1：update
					int insertOrUpdateFlag = 0;
					// 生成上送流水号
					String transOrderId = generateTransOrderId(TradeTypeEnum.onlinePay,
							PaymentCodeEnum.hengFengQuickPay);
					if ((pospTransInfo = pospTransInfoDAO.searchByOrderId(pmsAppTransInfo.getOrderid())) != null) {
						// 已经存在，修改流水号，设置pospsn为空
						logger.info("订单号：" + pmsAppTransInfo.getOrderid() + ",生成上送通道的流水号：" + transOrderId);
						pospTransInfo.setTransOrderId(originalinfo.getV_oid());
						pospTransInfo.setResponsecode("99");
						pospTransInfo.setPospsn("");
						insertOrUpdateFlag = 1;
					} else {
						// 不存在流水，生成一个流水
						pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);
						
						System.out.println("流水表生成的时间:"+pospTransInfo.getSenddate());
						// 设置上送流水号
						pospTransInfo.setTransOrderId(originalinfo.getV_oid());
						insertOrUpdateFlag = 0;
					}
					// 插入流水表信息
					if (insertOrUpdateFlag == 0) {
						// 插入一条流水
						pospTransInfoDAO.insert(pospTransInfo);
					} else if (insertOrUpdateFlag == 1) {
						// 更新一条流水
						pospTransInfoDAO.updateByOrderId(pospTransInfo);
					}
					logger.info("修改订单信息");
					logger.info(pmsAppTransInfo);

					int num = pmsAppTransInfoDao.update(pmsAppTransInfo);
					if (num > 0) {

						logger.info("上游通道商户号:" + pmsBusinessPos.getChannelnum());
						// 商户号码
						String merId = pmsBusinessPos.getBusinessnum();// 818310048160000
						// 商户号私钥
						String merKey = pmsBusinessPos.getKek();
						PayBankInfo bank = new PayBankInfo();
						bank.setBank_pmsbankNo(originalinfo.getV_settlePmsBankNo());
						bank = payBankInfoDao.selectByBankInfo(bank);
						logger.info("查询结算商户银行信息:" + bank);
						PayBankInfo bank2 = new PayBankInfo();
						bank2.setBank_pmsbankNo(originalinfo.getV_pmsBankNo());
						bank2 = payBankInfoDao.selectByBankInfo(bank2);
						logger.info("查询交易商户银行信息:" + bank2);
						switch (pmsBusinessPos.getBusinessnum()) {
						case "936640995770001": // 摩宝快捷收银台
						case "936640995770002": // 摩宝快捷银联界面
						case "88882017092010001121": // 赢酷快捷
						case "000000003":// 易生快捷
						case "1120180427134034001":// 银生宝快捷
							Calendar now = Calendar.getInstance();
							int number = now.get(Calendar.MONTH) + 1;
							String minute = "";
							String month = "";
							if (number < 10) {
								month = "0" + number;
							}
							if (now.get(Calendar.MINUTE) < 10) {
								minute = "0" + now.get(Calendar.MINUTE);
							}
							String time = now.get(Calendar.YEAR) + "年" + month + "月" + now.get(Calendar.DAY_OF_MONTH)
									+ "日" + now.get(Calendar.HOUR_OF_DAY) + ":" + minute;

							merchantinfo = merchantList.get(0);
							String phone = originalinfo.getV_phone();// 法人手机号

							// 查询余额
							SmsBalanceRequest smsBalanceRequest = new SmsBalanceRequest(ACCOUNT, PSWD);

							String balancerequestJson = JSON.toJSONString(smsBalanceRequest);

							logger.info("查询短信余额前上送的数据:" + balancerequestJson);

							String balanceresponse = ChuangLanSmsUtil.sendSmsByPost(smsBalanceRequestUrl,
									balancerequestJson);

							logger.info("查询短信余额响应信息 : " + balanceresponse);

							SmsBalanceResponse smsVarableResponse = JSON.parseObject(balanceresponse,
									SmsBalanceResponse.class);

							logger.info("查询余额实体信息 : " + smsVarableResponse);

							if ("0".equals(smsVarableResponse.getCode())) {
								logger.info("剩余可用余额条数:" + smsVarableResponse.getBalance());

								if (Integer.parseInt(smsVarableResponse.getBalance()) > 0) {

									Integer num1 = (int) ((Math.random() * 9 + 1) * 100000);

									logger.info("上送的验证码：" + num1);

									// 验证码实现

									String params = originalinfo.getV_phone() + "," + num1.toString() + "," + time;

									String massage = MSG + num1.toString();
									SmsVariableRequest smsVariableRequest = new SmsVariableRequest(ACCOUNT, PSWD, MSG,
											params, REPORT);
									SmsSendRequest smsSingleRequest = new SmsSendRequest(ACCOUNT, PSWD, massage, phone,
											REPORT);

									String varrequestJson = JSON.toJSONString(smsVariableRequest);

									logger.info("普通短信请求前数据:" + varrequestJson);
									String smsresponse = ChuangLanSmsUtil.sendSmsByPost(smsVariableRequestUrl,
											varrequestJson);

									logger.info("普通短信响应数据:" + smsresponse);
									SmsSendResponse smsSingleResponse = JSON.parseObject(smsresponse,
											SmsSendResponse.class);
									SmsVariableResponse smsVariableResponse = JSON.parseObject(smsresponse,
											SmsVariableResponse.class);
									if ("0".equals(smsVariableResponse.getCode())) {
										OriginalOrderInfo info = new OriginalOrderInfo();
										info.setOrderId(originalinfo.getV_oid());
										info.setSumCode(num1.toString());
										number = originalDao.update(info);
										if (number > 0) {
											retMap.put("v_mid", originalinfo.getV_mid());
											retMap.put("v_txnAmt", originalinfo.getV_txnAmt());
											retMap.put("v_time", originalinfo.getV_time());
											retMap.put("v_code", "00");
											retMap.put("v_msg", "请求成功");
											retMap.put("v_oid", originalinfo.getV_oid());
										}
									}
								} else {

									logger.info("余额不足，请充值" + smsVarableResponse.getCode());
									// retMap.put("002", "余额不足,请充值");
								}

							} else {

								logger.info("信息有误，请充重新提交" + smsVarableResponse.getCode());
								return setResp("07", "系统异常，请重试或联系客服");
							}
							break;
						case "10000466938":// 易宝快捷
							if ("1".equals(originalinfo.getV_type())) {
								now = Calendar.getInstance();
								number = now.get(Calendar.MONTH) + 1;
								minute = "";
								month = "";
								if (number < 10) {
									month = "0" + number;
								}
								if (now.get(Calendar.MINUTE) < 10) {
									minute = "0" + now.get(Calendar.MINUTE);
								}
								time = now.get(Calendar.YEAR) + "年" + month + "月" + now.get(Calendar.DAY_OF_MONTH) + "日"
										+ now.get(Calendar.HOUR_OF_DAY) + ":" + minute;

								merchantinfo = merchantList.get(0);
								phone = originalinfo.getV_phone();// 法人手机号

								// 查询余额
								smsBalanceRequest = new SmsBalanceRequest(ACCOUNT, PSWD);

								balancerequestJson = JSON.toJSONString(smsBalanceRequest);

								logger.info("查询短信余额前上送的数据:" + balancerequestJson);

								balanceresponse = ChuangLanSmsUtil.sendSmsByPost(smsBalanceRequestUrl,
										balancerequestJson);

								logger.info("查询短信余额响应信息 : " + balanceresponse);

								smsVarableResponse = JSON.parseObject(balanceresponse, SmsBalanceResponse.class);

								logger.info("查询余额实体信息 : " + smsVarableResponse);

								if ("0".equals(smsVarableResponse.getCode())) {
									logger.info("剩余可用余额条数:" + smsVarableResponse.getBalance());

									if (Integer.parseInt(smsVarableResponse.getBalance()) > 0) {

										Integer num1 = (int) ((Math.random() * 9 + 1) * 100000);

										logger.info("上送的验证码：" + num1);

										// 验证码实现

										String params = originalinfo.getV_phone() + "," + num1.toString() + "," + time;

										String massage = MSG + num1.toString();
										SmsVariableRequest smsVariableRequest = new SmsVariableRequest(ACCOUNT, PSWD,
												MSG, params, REPORT);
										SmsSendRequest smsSingleRequest = new SmsSendRequest(ACCOUNT, PSWD, massage,
												phone, REPORT);

										String varrequestJson = JSON.toJSONString(smsVariableRequest);

										logger.info("普通短信请求前数据:" + varrequestJson);
										String smsresponse = ChuangLanSmsUtil.sendSmsByPost(smsVariableRequestUrl,
												varrequestJson);

										logger.info("普通短信响应数据:" + smsresponse);
										SmsSendResponse smsSingleResponse = JSON.parseObject(smsresponse,
												SmsSendResponse.class);
										SmsVariableResponse smsVariableResponse = JSON.parseObject(smsresponse,
												SmsVariableResponse.class);
										if ("0".equals(smsVariableResponse.getCode())) {
											OriginalOrderInfo info = new OriginalOrderInfo();
											info.setOrderId(originalinfo.getV_oid());
											info.setSumCode(num1.toString());
											number = originalDao.update(info);
											if (number > 0) {
												retMap.put("v_mid", originalinfo.getV_mid());
												retMap.put("v_txnAmt", originalinfo.getV_txnAmt());
												retMap.put("v_time", originalinfo.getV_time());
												retMap.put("v_code", "00");
												retMap.put("v_msg", "请求成功");
												retMap.put("v_oid", originalinfo.getV_oid());
											}
										}
									} else {

										logger.info("余额不足，请充值" + smsVarableResponse.getCode());
										// retMap.put("002", "余额不足,请充值");
									}

								} else {

									logger.info("信息有误，请充重新提交" + smsVarableResponse.getCode());
									return setResp("07", "系统异常，请重试或联系客服");
								}
							}
							break;
						case "936640995770000":// 摩宝快捷
						case "936775585060000":// 摩宝快捷

							Map<String, String> transmap = new LinkedHashMap<String, String>();
							transmap.put("versionId", "001"); // 版本号 固定
							transmap.put("businessType", "1401"); // 预交易 1401
							transmap.put("merId", "936640995770000"); // 商户号
							transmap.put("orderId", originalinfo.getV_oid()); // 订单号
							transmap.put("transDate", originalinfo.getV_time()); // 时间 yymmddhhmmss
							transmap.put("transAmount", originalinfo.getV_txnAmt()); // 金额
																						// 单位元，对于正式商户最低支付金额为10元
							transmap.put("cardByName", MD5Util.encode(originalinfo.getV_realName().getBytes("UTF-8"))); // 此处的MD5util为Base64加密
							transmap.put("cardByNo", originalinfo.getV_cardNo()); // 卡号
							if ("1".equals(originalinfo.getV_accountType())) {
								transmap.put("cardType", "01"); // 卡类型01借记卡

							} else if ("2".equals(originalinfo.getV_accountType())) {

								transmap.put("cardType", "00"); // 卡类型00贷记卡
								transmap.put("expireDate", originalinfo.getV_expired()); // 有效期
								transmap.put("CVV", originalinfo.getV_cvn2()); // CVN
							}
							// transmap.put("bankCode",mbReqest.getBankCode()); //可为空 银行代码
							// transmap.put("openBankName",mbReqest.getOpenBankName());//可为空 银行代码
							transmap.put("cerType", "01"); // 证件类型 01 身份证
							transmap.put("cerNumber", originalinfo.getV_cert_no());// 身份证
							transmap.put("mobile", originalinfo.getV_phone()); // 手机号
							transmap.put("isAcceptYzm", "00"); // 默认00
							transmap.put("backNotifyUrl", MBUtil.notifyUrl);
							transmap.put("instalTransFlag", "01"); // 分期标志
							// 需要加密的字符串
							String signstr = EncodeUtil.getUrlStr(transmap);
							logger.info("需要签名的明文" + signstr);
							String signtrue = MD5Util.MD5Encode(signstr + merKey);
							transmap.put("signType", "MD5");
							transmap.put("signData", signtrue);
							// AES加密
							String transUrlStr = EncodeUtil.getUrlStr(transmap);
							//
							String transData = AESUtil.encrypt(transUrlStr, merKey);
							// 生产地址
							String testUrl = MBUtil.quick;
							String str = DemoBase.requestBody(merId, transData, testUrl);
							// 获取交易返回结果
							logger.info(str);
							ObjectMapper om = new ObjectMapper();
							Map<String, String> maps = new HashMap<>();
							maps = om.readValue(str, Map.class);
							retMap.put("v_mid", originalinfo.getV_mid());
							retMap.put("v_txnAmt", originalinfo.getV_txnAmt());
							retMap.put("v_time", originalinfo.getV_time());
							retMap.put("v_oid", originalinfo.getV_oid());
							if ("00".equals(maps.get("status"))) {
								if ("01".equals(maps.get("refCode"))) {
									OriginalOrderInfo info = new OriginalOrderInfo();
									info.setMerchantOrderId(maps.get("ksPayOrderId"));
									info.setOrderId(originalinfo.getV_oid());
									number = originalDao.update(info);
									retMap.put("v_code", "00");
									retMap.put("v_msg", "请求成功");
								} else if ("02".equals(maps.get("refCode"))) {
									retMap.put("v_code", "15");
									retMap.put("v_msg", "请求失败");
								} else {
									retMap.put("v_code", "15");
									retMap.put("v_msg", "请求失败");
								}
							} else if ("01".equals(maps.get("status"))) {
								retMap.put("v_code", "15");
								retMap.put("v_msg", "请求失败");
							} else if ("02".equals(maps.get("status"))) {
								retMap.put("v_code", "15");
								retMap.put("v_msg", "请求失败");
							}
							break;
						case "1800056392":// 合利宝快捷
						case "1800001582":// 合利宝快捷
							LinkedHashMap<String, String> map = new LinkedHashMap<>();
							try {
								map.put("P1_bizType", "QuickPayBankCardPay");
								map.put("P2_customerNumber", "C" + pmsBusinessPos.getBusinessnum());
								map.put("P3_userId", originalinfo.getV_oid());// 170000000002
								map.put("P4_orderId", originalinfo.getV_oid());
								map.put("P5_timestamp", originalinfo.getV_time());
								map.put("P6_payerName", originalinfo.getV_realName());// URLEncoder.encode("安晓楠","UTF-8")
								map.put("P7_idCardType", "IDCARD");
								map.put("P8_idCardNo", originalinfo.getV_cert_no());// URLEncoder.encode("130722198710107446","UTF-8")
								map.put("P9_cardNo", originalinfo.getV_cardNo());// URLEncoder.encode("5268550479591851","UTF-8")
								// map.put("P10_year",
								// hlbRequest.getYear()==null?"":hlbRequest.getYear());//URLEncoder.encode("20","UTF-8")
								// map.put("P11_month",hlbRequest.getMonth()==null?"":hlbRequest.getMonth());//URLEncoder.encode("07","UTF-8");
								map.put("P12_cvv2", originalinfo.getV_cvn2() == null ? "" : originalinfo.getV_cvn2());// URLEncoder.encode("862","UTF-8")
								map.put("P13_phone", originalinfo.getV_phone());// URLEncoder.encode("15652000669","UTF-8")
								map.put("P14_currency", "CNY");
								map.put("P15_orderAmount", Double.parseDouble(originalinfo.getV_txnAmt()) + "");
								map.put("P16_goodsName", originalinfo.getV_productDesc());
								map.put("P17_goodsDesc", originalinfo.getV_productDesc());
								map.put("P18_terminalType", "IMEI");
								map.put("P19_terminalId", originalinfo.getV_oid());
								map.put("P20_orderIp", "127.0.0.1");
								// map.put("P21_period",
								// hlbRequest.getPeriod()==null?"1":hlbRequest.getPeriod());
								// map.put("P22_periodUnit",
								// hlbRequest.getPeriodUnit()==null?"Day":hlbRequest.getPeriodUnit());
								map.put("P23_serverCallbackUrl", HLBUtil.notifyUrl);
								logger.info("签名之前的数据:" + map);
								String key = pmsBusinessPos.getKek();
								String oriMessage = MyBeanUtils.getSigned(map, null, key);
								logger.info("签名原文串：" + oriMessage);
								String sign = Disguiser.disguiseMD5(oriMessage, "UTF-8");
								map.put("sign", sign);
								Map<String, Object> resultMap1 = HttpClientService.getHttpResp(map, HLBUtil.url);
								String s = resultMap1.get("response").toString();
								System.out.println(s);
								JSONObject json = JSONObject.parseObject(s);
								if ("0000".equals(json.getString("rt2_retCode"))) {
									map.put("P1_bizType", "QuickPaySendValidateCode");
									map.put("P2_customerNumber", "C" + pmsBusinessPos.getBusinessnum());
									map.put("P3_orderId", originalinfo.getV_oid());
									map.put("P4_timestamp", originalinfo.getV_time());
									map.put("P5_phone", originalinfo.getV_phone());
									logger.info("签名之前的数据:" + map);
									// String key=pmsBusinessPos.getKek();
									oriMessage = MyBeanUtils.getSigned(map, null, key);
									logger.info("签名原文串：" + oriMessage);
									sign = Disguiser.disguiseMD5(oriMessage, "UTF-8");
									map.put("sign", sign);
									Map<String, Object> resultMap2 = HttpClientService.getHttpResp(map, HLBUtil.url);
									logger.info("获取短信验证码返回参数:" + JSON.toJSONString(resultMap));
									s = resultMap2.get("response").toString();
									System.out.println(s);
									json = JSONObject.parseObject(s);
									if ("0000".equals(json.getString("rt2_retCode"))) {
										retMap.put("respCode", "00");
										retMap.put("type", originalinfo.getV_type());
										retMap.put("merNo", originalinfo.getV_mid());
										retMap.put("phone", json.getString("rt6_phone"));
										retMap.put("respMsg", json.getString("rt3_retMsg"));
										retMap.put("orderId", json.getString("rt5_orderId"));

									} else {
										retMap.put("respCode", "15");
										retMap.put("type", originalinfo.getV_type());
										retMap.put("merNo", originalinfo.getV_mid());
										retMap.put("phone", json.getString("rt6_phone"));
										retMap.put("respMsg", json.getString("rt3_retMsg"));
										retMap.put("orderId", json.getString("rt5_orderId"));
									}

								} else {
									retMap.put("respCode", "01");
									retMap.put("type", originalinfo.getV_type());
									retMap.put("merNo", originalinfo.getV_mid());
									retMap.put("userId", originalinfo.getV_oid());
									retMap.put("respMsg", json.getString("rt3_retMsg"));
									retMap.put("orderId", json.getString("rt5_orderId"));
								}
								logger.info("下单返回参数:" + JSON.toJSONString(resultMap));

							} catch (Exception e) {
								logger.info("下单" + e);
							}
							break;
						case "2017112113602199513":// 广州恒明有积分快捷
							JSONObject requestObj = new JSONObject();
							requestObj.put("ordernumber", originalinfo.getV_oid());
							requestObj.put("merchantid", "M" + pmsBusinessPos.getBusinessnum());
							requestObj.put("username", originalinfo.getV_realName());
							requestObj.put("userpid", originalinfo.getV_pmsBankNo());
							requestObj.put("usercardno", originalinfo.getV_cardNo());
							requestObj.put("usertel", originalinfo.getV_phone());
							requestObj.put("amount", originalinfo.getV_txnAmt());// 单位分 100=1元
							if ("0".equals(originalinfo.getV_type())) {
								requestObj.put("ordertype", "10");// 10:D0,11:T1

							} else if ("1".equals(originalinfo.getV_type())) {
								requestObj.put("ordertype", "11");// 10:D0,11:T1
							}
							requestObj.put("cvn2", originalinfo.getV_cvn2() == null ? "" : originalinfo.getV_cvn2());
							requestObj.put("expdate",
									originalinfo.getV_expired() == null ? "" : originalinfo.getV_expired());
							requestObj.put("usertel", originalinfo.getV_phone());
							requestObj.put("backurl", HMUtil.quickUrl);
							requestObj.put("returnurl", "");
							String encryptdata = AesEncryption.Encrypt(requestObj.toJSONString(), HMUtil.aeskey,
									HMUtil.aeskey);

							String timestamp = TimeUtil.getTime();
							signstr = SHA256Util.sha256(pmsBusinessPos.getKek() + "M" + pmsBusinessPos.getBusinessnum()
									+ encryptdata + timestamp + pmsBusinessPos.getKek());
							System.out.println(signstr);
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("merchantid", "M" + pmsBusinessPos.getBusinessnum());
							jsonObject.put("data", encryptdata);
							jsonObject.put("timestamp", timestamp);
							jsonObject.put("sign", signstr);
							String postdata = "merchantid=" + "M" + pmsBusinessPos.getBusinessnum() + "&data="
									+ encryptdata + "&timestamp=" + timestamp + "&sign=" + signstr;
							String openApiUrl = "";
							if (!"".equals(originalinfo.getV_cvn2())) {
								openApiUrl = HMUtil.quickPayXinUrl;
							} else {
								openApiUrl = HMUtil.quickPayJieUrl;
							}
							String results = HttpsUtil.doSslPost(openApiUrl, postdata, "utf-8");
							logger.info("恒明返回参数：" + results);

							JSONObject responseObj = JSONObject.parseObject(results);
							logger.info("message:" + responseObj.get("message"));
							retMap.put("v_mid", originalinfo.getV_mid());
							retMap.put("v_txnAmt", originalinfo.getV_txnAmt());
							retMap.put("v_time", originalinfo.getV_time());
							retMap.put("v_oid", originalinfo.getV_oid());
							if ("0".equals(responseObj.get("ret").toString())) {
								String dedata = AesEncryption.Desencrypt(responseObj.get("data").toString(),
										HMUtil.aeskey, HMUtil.aeskey);
								logger.info("恒明解析参数：" + dedata);
								JSONObject jsonObject2 = JSONObject.parseObject(dedata);
								// PmsAppTransInfo pmsAppTransInfo =
								// pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
								if ("0".equals(jsonObject2.get("orderstate"))) {
									OriginalOrderInfo info = new OriginalOrderInfo();
									info.setMerchantOrderId(jsonObject2.getString("payorderno"));
									info.setOrderId(originalinfo.getV_oid());
									number = originalDao.update(info);
									retMap.put("v_code", "00");
									retMap.put("v_msg", "请求成功");

								} else {
									retMap.put("v_code", "15");
									retMap.put("v_msg", "请求失败");
									return retMap;

								}
							} else {
								retMap.put("v_code", "15");
								retMap.put("v_msg", "请求失败");
								return retMap;
							}
							break;
						case "000000001":// 易生快捷
							String channelKey = YSUtil.channelKey;
							String channel_sign_method = "SHA256";
							Map<String, Object> reqMap = new TreeMap<String, Object>();
							reqMap.put("sp_id", YSUtil.sp_id);// 服务商号
							reqMap.put("mch_id", YSUtil.merId2);// 商户号
							reqMap.put("out_trade_no", originalinfo.getV_oid());
							reqMap.put("id_type", "01");
							reqMap.put("acc_name", originalinfo.getV_realName());// 持卡人姓名
							reqMap.put("acc_type", "PERSONNEL");// PERSONNEL：对私 CORPORATE：对公
							reqMap.put("in_acc_no", originalinfo.getV_settleCardNo());// 卡号
							reqMap.put("mobile", originalinfo.getV_phone());// 手机号
							reqMap.put("id_no", originalinfo.getV_cert_no());// 证件号
							reqMap.put("settle_rate", originalinfo.getV_settleUserFee());// 结算费率
							reqMap.put("extra_rate", originalinfo.getV_userFee());// T0费率
							Date t = new Date();
							java.util.Calendar cal = java.util.Calendar.getInstance();
							cal.setTime(t);
							long sys_timestamp = cal.getTimeInMillis();
							reqMap.put("timestamp", sys_timestamp);// 时间戳

							StringBuilder sb = new StringBuilder();
							Set<String> keySet = reqMap.keySet();
							Iterator<String> iter = keySet.iterator();
							while (iter.hasNext()) {
								String key = iter.next();
								sb.append(key);
								sb.append("=");
								sb.append(reqMap.get(key));
								sb.append("&");
							}
							String sign = SwpHashUtil.getSign(sb.toString() + "key=" + channelKey, channelKey,
									channel_sign_method);
							reqMap.put("sign", sign);

							sb.append("sign");
							sb.append("=");
							sb.append(sign);
							System.out.println(sb.toString());
							String url = YSUtil.url + "/swp/up/settlecheck.do";
							HttpResponse httpResponse = HttpUtils.doPost(url, "", sb.toString(),
									"application/x-www-form-urlencoded; charset=UTF-8");
							String resp = EntityUtils.toString(httpResponse.getEntity());
							System.out.println("接受请求:" + resp);
							JSONObject json = JSONObject.parseObject(resp);

							if ("SUCCESS".equals(json.getString("status"))) {
								if ("SUCCESS".equals(json.getString("trade_state"))) {
									PmsWeixinMerchartInfo merchartInfo = new PmsWeixinMerchartInfo();
									merchartInfo.setAccount(json.getString("sub_mch_id"));// 账号
									merchartInfo.setMerchartId(originalinfo.getV_mid());
									merchartInfo.setMerchartName(merchantList.get(0).getMercName());
									merchartInfo.setMerchartNameSort(merchantList.get(0).getShortname());
									merchartInfo.setCertNo(originalinfo.getV_cert_no());// 证件号
									merchartInfo.setCardNo(originalinfo.getV_cardNo());// 卡号
									merchartInfo.setRealName(originalinfo.getV_realName());// 姓名
									merchartInfo.setMobile(originalinfo.getV_phone());// 手机号
									// merchartInfo.setAccountType(payRequest.getBusinessType());//账户类型
									merchartInfo.setBankName(bank2.getBank_name());// 开户行
									merchartInfo.setPmsBankNo(originalinfo.getV_pmsBankNo());// 联行号
									merchartInfo.setProvince(bank2.getBank_province());// 省份
									merchartInfo.setCity(bank2.getBank_city());// 城市
									merchartInfo.setDebitRate(originalinfo.getV_userFee());// 借记卡费率
									// merchartInfo.setWithdrawDepositSingleFee(payRequest.getWithdrawDepositSingleFee());//提现单笔手续费
									merchartInfo.setoAgentNo("100333");
									merchartInfo.setRateCode(originalinfo.getV_mid());
									int i = weixinService.updateRegister(merchartInfo);
									logger.info("易生修改状态:" + i);
									if (i > 0) {
										channelKey = YSUtil.channelKey;
										channel_sign_method = "SHA256";
										reqMap.put("sp_id", YSUtil.sp_id);// 服务商号
										reqMap.put("mch_id", YSUtil.merId2);// 商户号
										reqMap.put("out_trade_no", originalinfo.getV_oid());
										reqMap.put("id_type", "01");
										reqMap.put("sub_mch_id", json.getString("sub_mch_id"));
										reqMap.put("acc_name", originalinfo.getV_realName());// 持卡人姓名
										reqMap.put("cvn2", originalinfo.getV_cvn2());//
										reqMap.put("expired", originalinfo.getV_expired());
										reqMap.put("acc_no", originalinfo.getV_cardNo());// 卡号
										reqMap.put("bankcode", bank2.getBank_code());// 银行代码
										reqMap.put("mobile", originalinfo.getV_phone());// 手机号
										reqMap.put("id_no", originalinfo.getV_cert_no());// 证件号
										t = new Date();
										cal = java.util.Calendar.getInstance();
										cal.setTime(t);
										sys_timestamp = cal.getTimeInMillis();
										reqMap.put("timestamp", sys_timestamp);// 时间戳

										sb = new StringBuilder();
										keySet = reqMap.keySet();
										iter = keySet.iterator();
										while (iter.hasNext()) {
											String key = iter.next();
											sb.append(key);
											sb.append("=");
											sb.append(reqMap.get(key));
											sb.append("&");
										}
										sign = SwpHashUtil.getSign(sb.toString() + "key=" + channelKey, channelKey,
												channel_sign_method);
										reqMap.put("sign", sign);

										sb.append("sign");
										sb.append("=");
										sb.append(sign);
										System.out.println(sb.toString());
										url = YSUtil.url + "/swp/up/bindCardBack.do";
										httpResponse = HttpUtils.doPost(url, "", sb.toString(),
												"application/x-www-form-urlencoded; charset=UTF-8");
										resp = EntityUtils.toString(httpResponse.getEntity());
										System.out.println("接受请求:" + resp);
										json = JSONObject.parseObject(resp);

										if ("SUCCESS".equals(json.getString("status"))) {
											if ("SUCCESS".equals(json.getString("trade_state"))) {
												PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
												model.setMercId(originalinfo.getV_mid());
												model.setBatchNo(originalinfo.getV_oid());
												model.setIdentity(json.getString("swpaccid"));
												model.setCardno(originalinfo.getV_cardNo());
												model.setRealname(originalinfo.getV_realName());
												model.setPmsbankno(json.getString("trade_state_desc"));
												model.setTransactionType("快捷绑卡");
												model.setOagentno("100333");
												model.setResponsecode("00");
												i = pmsDaifuMerchantInfoDao.insert(model);
												if (i > 0) {
													logger.info("插入鉴权订单成功！！");
													channelKey = YSUtil.channelKey;
													channel_sign_method = "SHA256";
													Map<String, Object> reqMap1 = new TreeMap<String, Object>();
													reqMap1.put("sp_id", YSUtil.sp_id);
													reqMap1.put("mch_id", pmsBusinessPos.getBusinessnum());// 商户号YSUtil.merId1
													reqMap1.put("out_trade_no", originalinfo.getV_oid());
													reqMap1.put("swpaccid", json.getString("swpaccid"));
													Double amount = Double.parseDouble(originalinfo.getV_txnAmt())
															* 100;
													reqMap1.put("total_fee", amount.toString());
													t = new Date();
													cal = java.util.Calendar.getInstance();
													cal.setTime(t);
													sys_timestamp = cal.getTimeInMillis();
													reqMap1.put("timestamp", sys_timestamp);

													StringBuilder sb1 = new StringBuilder();
													keySet = reqMap.keySet();
													iter = keySet.iterator();
													while (iter.hasNext()) {
														String key = iter.next();
														sb1.append(key);
														sb1.append("=");
														sb1.append(reqMap.get(key));
														sb1.append("&");
													}
													sign = SwpHashUtil.getSign(sb1.toString() + "key=" + channelKey,
															channelKey, channel_sign_method);
													reqMap1.put("sign", sign);

													sb1.append("sign");
													sb1.append("=");
													sb1.append(sign);
													logger.info("签名串:" + sb1.toString());
													url = YSUtil.url + "/swp/up/sms.do";
													httpResponse = HttpUtils.doPost(url, "", sb.toString(),
															"application/x-www-form-urlencoded; charset=UTF-8");
													resp = EntityUtils.toString(httpResponse.getEntity());
													logger.info("接受请求:" + resp);
													json = JSONObject.parseObject(resp);
													if ("SUCCESS".equals(json.getString("status"))) {
														if ("SUCCESS".equals(json.getString("trade_state"))) {
															OriginalOrderInfo info = new OriginalOrderInfo();
															info.setOrderId(originalinfo.getV_oid());
															info.setMerchantOrderId(json.getString("sys_trade_no"));
															;
															number = originalDao.update(info);
															if (number > 0) {
																retMap.put("v_mid", originalinfo.getV_mid());
																retMap.put("v_txnAmt", originalinfo.getV_txnAmt());
																retMap.put("v_time", originalinfo.getV_time());
																retMap.put("v_code", "00");
																retMap.put("v_msg", "请求成功");
																retMap.put("v_oid", originalinfo.getV_oid());
															}

														} else {
															retMap.put("v_code", "15");
															retMap.put("v_msg", "请求失败");
															return retMap;
														}
													} else {
														retMap.put("v_code", "15");
														retMap.put("v_msg", "请求失败");
														return retMap;
													}
												}
											} else {
												retMap.put("v_code", "15");
												retMap.put("v_msg", "请求失败");
												return retMap;
											}
										} else {
											retMap.put("v_code", "15");
											retMap.put("v_msg", "请求失败");
											return retMap;
										}

									}
								} else {
									retMap.put("v_code", "15");
									retMap.put("v_msg", "请求失败");
									return retMap;
								}
							} else {
								retMap.put("v_code", "15");
								retMap.put("v_msg", "请求失败");
								return retMap;
							}
							break;
						case "1711030001":// 沈阳银盛
							PmsWeixinMerchartInfo weixin = new PmsWeixinMerchartInfo();

							weixin.setCardNo(originalinfo.getV_settleCardNo());
							weixin.setRealName(originalinfo.getV_settleName());
							weixin.setCertNo(originalinfo.getV_cert_no());
							weixin.setMobile(originalinfo.getV_phone());

							PmsWeixinMerchartInfo model = weixinService.selectByCardEntity(weixin);

							logger.info("沈阳银盛进件订单数据:" + JSON.toJSON(model));

							if (model == null) {
								Map<String, String> requestMap = new HashMap<String, String>();
								requestMap.put("merchId", "m1803070001"); // 商户全称，企业商户填写营业执照名称
								requestMap.put("merchantName", originalinfo.getV_productDesc()); // 商户全称，企业商户填写营业执照名称
								requestMap.put("channel", "WLB");
								requestMap.put("installProvince", bank.getBank_province());
								requestMap.put("installCity", bank.getBank_city());
								requestMap.put("installCounty", bank.getBank_city());
								requestMap.put("operateAddress", bank.getBank_city());
								requestMap.put("legalPersonName", originalinfo.getV_settleName()); // 法人姓名，如果bankType为TOPRIVATE，则结算账户与法人必须一致,
								// 如果bankType为TOPUBLIC，结算账户名和商户名称是一致的
								requestMap.put("legalPersonID", originalinfo.getV_cert_no()); // 法人身份证
								requestMap.put("merchantPersonPhone", originalinfo.getV_phone());// 商户联系人电话
								// 如果bankType为TOPRIVATE，则legalPersonName与accountName必须一致
								requestMap.put("accountNo", originalinfo.getV_settleCardNo());
								requestMap.put("bankBranch", bank.getBank_name());
								requestMap.put("bankProv", bank.getBank_province());
								requestMap.put("bankCity", bank.getBank_city());
								requestMap.put("bankCode", originalinfo.getV_settlePmsBankNo());
								requestMap.put("bankName", bank.getBank_short_title());
								requestMap.put("fastRate", originalinfo.getV_userFee());
								String key = "0295a406899f4c3783ef4e22eef5ae9f";// md5key
								// 得到带签名数据
								Map<String, ?> filterMap = PayCore.paraFilter(requestMap);

								String linkStr = PayCore.createLinkString(filterMap);
								logger.info("签名公钥" + key);
								logger.info("待签数据" + linkStr);
								String hexSign = "";
								try {
									hexSign = PayCore.md5Sign(linkStr, key);
								} catch (Exception e) {
									e.printStackTrace();
								}
								logger.info("签名数据:" + hexSign);
								requestMap.put("sign_info", hexSign);
								String requestStr = JSON.toJSONString(requestMap);
								url = "http://pay.unvpay.com/services/ysFast/addInfo";
								String respStr = HttpClientUtil.post(url, "UTF-8", requestStr);
								logger.info("返回值：" + respStr);
								json = JSONObject.parseObject(respStr);
								if ("0000".equals(json.getString("ret_code"))) {
									String app_id = json.getString("app_id");
									logger.info("上游返回的app_id：" + app_id);
									Map<String, String> requestMap1 = new HashMap<String, String>();

									requestMap1.put("merchId", "m1803070001"); // 支付系统分配给商户的机构号
									requestMap1.put("appId", app_id); // 商户号
									requestMap1.put("fastRate", originalinfo.getV_userFee());
									requestMap1.put("channel", "WLB");
									// 加密签名
									Map<String, ?> filterMap1 = null;
									try {
										filterMap1 = PayCore.paraFilter(requestMap1);
									} catch (Exception e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									String linkStr1 = PayCore.createLinkString(filterMap1);
									logger.info("签名公钥" + key);
									logger.info("待签数据" + linkStr1);
									String hexSign1 = "";
									try {
										hexSign1 = PayCore.md5Sign(linkStr1, key);
									} catch (Exception e) {
										e.printStackTrace();
									}
									logger.info("签名数据:" + hexSign1);
									requestMap1.put("sign_info", hexSign1);
									String requestStr1 = JSON.toJSONString(requestMap1);

									url = "http://pay.unvpay.com/services/ysFast/addRate";
									String respStr1 = HttpClientUtil.post(url, "UTF-8", requestStr1);
									logger.info("返回值：" + respStr1);
									json = JSONObject.parseObject(respStr1);
									if ("0000".equals(json.getString("ret_code"))) {
										app_id = json.getString("app_id");
										logger.info("上游返回的app_id：" + app_id);
										PmsWeixinMerchartInfo merchartInfo = new PmsWeixinMerchartInfo();
										merchartInfo.setAccount(app_id);// 账号
										merchartInfo.setMerchartId(originalinfo.getV_mid());
										merchartInfo.setMerchartName(merchantList.get(0).getMercName());
										merchartInfo.setMerchartNameSort(merchantList.get(0).getShortname());
										merchartInfo.setCertNo(originalinfo.getV_cert_no());// 证件号
										merchartInfo.setCardNo(originalinfo.getV_settleCardNo());// 卡号
										merchartInfo.setRealName(originalinfo.getV_settleName());// 姓名
										merchartInfo.setMobile(originalinfo.getV_phone());// 手机号
										// merchartInfo.setAccountType(payRequest.getBusinessType());//账户类型
										merchartInfo.setBankName(bank.getBank_name());// 开户行
										merchartInfo.setPmsBankNo(originalinfo.getV_settlePmsBankNo());// 联行号
										merchartInfo.setProvince(bank.getBank_province());// 省份
										merchartInfo.setCity(bank.getBank_city());// 城市
										merchartInfo.setDebitRate(originalinfo.getV_userFee());// 借记卡费率
										// merchartInfo.setWithdrawDepositSingleFee(payRequest.getWithdrawDepositSingleFee());//提现单笔手续费
										merchartInfo.setoAgentNo("100333");
										merchartInfo.setRateCode(originalinfo.getV_mid());
										int i = weixinService.updateRegister(merchartInfo);
										if (i > 0) {
											PmsWeixinMerchartInfo pm = new PmsWeixinMerchartInfo();

											pm.setCardNo(originalinfo.getV_cardNo());
											pm.setRealName(originalinfo.getV_realName());
											pm.setCertNo(originalinfo.getV_cert_no());
											pm.setMobile(originalinfo.getV_phone());

											PmsWeixinMerchartInfo pwm = weixinService.selectByCardEntity(pm);

											logger.info("沈阳银盛进件订单数据:" + JSON.toJSON(pwm));
											if (pwm == null) {
												Map<String, String> requestMap2 = new HashMap<String, String>();
												requestMap2.put("order_num", originalinfo.getV_oid()); // 流水号
												requestMap2.put("app_id", app_id); // appId
												requestMap2.put("account_no", originalinfo.getV_cardNo()); // 银行账号
												requestMap2.put("mobile", originalinfo.getV_phone()); // 银行预留电话，不填则使用进件电话
												if ("1".equals(originalinfo.getV_accountType())) {
													requestMap2.put("card_type", "01"); // 卡类型
												} else if ("2".equals(originalinfo.getV_accountType())) {
													requestMap2.put("card_type", "02"); // 卡类型
												}

												String call_back_url = BaseUtil.url+"/quickPayAction/syysReturnUrl.action?order_num="
														+ originalinfo.getV_oid();

												requestMap2.put("call_back_url", call_back_url); // 跳转页面地址
												requestMap2.put("notify_url",
														BaseUtil.url+"/quickPayAction/syysNotifyUrl.action"); // 接收异步通知地址
												requestMap2.put("open_type", "JF_OPEN"); // 开卡类型，为空，默认为UNION_PAY_OPEN
												requestMap2.put("channel", "WLB"); // 渠道
												requestMap2.put("version", "V2.1.1"); // 版本编号
												requestMap2.put("charset", "UTF-8"); // 字符编码

												// 得到带签名数据
												filterMap = PayCore.paraFilter(requestMap2);

												linkStr = PayCore.createLinkString(filterMap);
												logger.info("签名公钥" + key);
												logger.info("待签数据" + linkStr);
												hexSign = "";
												try {
													hexSign = PayCore.md5Sign(linkStr, key);
												} catch (Exception e) {
													e.printStackTrace();
												}
												logger.info("签名数据:" + hexSign);
												requestMap2.put("sign_type", "MD5"); // md5签名
												requestMap2.put("sign_info", hexSign);
												requestStr = JSON.toJSONString(requestMap2);
												url = "http://pay.unvpay.com/services/fastpay/openCard";
												respStr = HttpClientUtil.post(url, "UTF-8", requestStr);
												logger.info("返回值：" + respStr);
												json = JSONObject.parseObject(respStr);
												if ("0000".equals(json.get("ret_code"))) {
													String param = json.getString("ret_data");
													JSONObject jb = JSONObject.parseObject(param);
													String auth_id = (String) jb.get("auth_id");
													merchartInfo = new PmsWeixinMerchartInfo();
													merchartInfo.setAccount(auth_id);// 账号
													merchartInfo.setMerchartId(originalinfo.getV_mid());
													merchartInfo.setMerchartName(merchantList.get(0).getMercName());
													merchartInfo
															.setMerchartNameSort(merchantList.get(0).getShortname());
													merchartInfo.setCertNo(originalinfo.getV_cert_no());// 证件号
													merchartInfo.setCardNo(originalinfo.getV_cardNo());// 卡号
													merchartInfo.setRealName(originalinfo.getV_realName());// 姓名
													merchartInfo.setMobile(originalinfo.getV_phone());// 手机号
													merchartInfo.setBankName(bank.getBank_name());// 开户行
													merchartInfo.setProvince(bank.getBank_province());// 省份
													merchartInfo.setCity(bank.getBank_city());// 城市
													merchartInfo.setDebitRate(originalinfo.getV_userFee());// 借记卡费率
													merchartInfo.setoAgentNo("100333");
													merchartInfo.setRateCode(originalinfo.getV_mid());
													i = weixinService.updateRegister(merchartInfo);
													if (i > 0) {
														OriginalOrderInfo oo = new OriginalOrderInfo();
														oo.setByUser(app_id);
														oo.setSumCode(auth_id);
														oo.setOrderId(originalinfo.getV_oid());
														num = originalDao.update(oo);
														if (num > 0) {
															url = (String) jb.get("qr_code");
															logger.info("沈阳银盛返回的url:" + url);
															retMap.put("url", url);
															retMap.put("v_code", "00000");
															retMap.put("v_msg", "请求成功");
														} else {
															logger.info("app_id修改失败!");
														}
													}

												} else {

													retMap.put("v_code", "15");
													retMap.put("v_msg", "请求失败");
													return retMap;
												}
											} else {
												logger.info("沈阳银盛进件app_id:" + model.getAccount());
												logger.info("沈阳银盛进件:auth_id" + pwm.getAccount());
												Map<String, String> requestMap2 = new HashMap<String, String>();
												requestMap2.put("order_num", originalinfo.getV_oid()); // 流水号
												requestMap2.put("app_id", model.getAccount()); // appId
												requestMap2.put("auth_id", pwm.getAccount()); // 银行账号
												Double amount = Double.parseDouble(originalinfo.getV_txnAmt());
												Integer number1 = amount.intValue();
												requestMap2.put("amount", number1.toString());
												requestMap2.put("encrypt", "T0"); // 卡类

												key = "0295a406899f4c3783ef4e22eef5ae9f";// md5key
												// 得到带签名数据
												filterMap = PayCore.paraFilter(requestMap2);

												linkStr = PayCore.createLinkString(filterMap);
												logger.info("沈阳银盛签名公钥" + key);
												logger.info("沈阳银盛待签数据" + linkStr);
												hexSign = PayCore.md5Sign(linkStr, key);
												logger.info("沈阳银盛签名数据:" + hexSign);
												requestMap2.put("sign_type", "MD5"); // md5签名
												requestMap2.put("sign_info", hexSign);
												requestStr = JSON.toJSONString(requestMap2);

												url = "http://pay.unvpay.com/services/fastpay/submitOrder";
												respStr = xdt.quickpay.syys.HttpClientUtil.post(url, "UTF-8",
														requestStr);

												logger.info("沈阳银盛短信返回值：" + respStr);
												json = com.alibaba.fastjson.JSONObject.parseObject(respStr);
												if ("0000".equals(json.getString("ret_code"))) {

													JSONObject jb = json.getJSONObject("ret_data");
													String param = jb.getString("parmMap");
													logger.info("沈阳银盛短信返回的ret_data：" + param);
													JSONObject jb1 = JSONObject.parseObject(param);
													String token = jb1.getString("token");
													logger.info("沈阳银盛短信返回的token：" + token);
													OriginalOrderInfo oo = new OriginalOrderInfo();
													oo.setOrderId(originalinfo.getV_oid());
													oo.setSumCode(token);
													oo.setByUser(model.getAccount());
													;
													oo.setProcdutNum("0000");
													num = originalDao.update(oo);
													if (num > 0) {
														retMap.put("v_mid", originalinfo.getV_mid());
														retMap.put("v_txnAmt", originalinfo.getV_txnAmt());
														retMap.put("v_time", originalinfo.getV_time());
														retMap.put("v_code", "00");
														retMap.put("v_msg", "请求成功");
														retMap.put("v_oid", originalinfo.getV_oid());
													}
												}

											}

										}

									} else {
										retMap.put("v_code", "15");
										retMap.put("v_msg", "请求失败");
										return retMap;
									}

								} else {

									retMap.put("v_code", "15");
									retMap.put("v_msg", "请求失败");
									return retMap;
								}

							} else {
								logger.info("沈阳银盛进件app_id:" + model.getAccount());
								PmsWeixinMerchartInfo pm = new PmsWeixinMerchartInfo();

								pm.setCardNo(originalinfo.getV_cardNo());
								pm.setRealName(originalinfo.getV_realName());
								pm.setCertNo(originalinfo.getV_cert_no());
								pm.setMobile(originalinfo.getV_phone());

								PmsWeixinMerchartInfo pwm = weixinService.selectByCardEntity(pm);

								logger.info("沈阳银盛进件订单数据:" + JSON.toJSON(pwm));
								if (pwm == null) {

									String app_id = model.getAccount().replaceAll(" ", "");
									Map<String, String> requestMap2 = new HashMap<String, String>();

									requestMap2.put("order_num", originalinfo.getV_oid()); // 流水号
									requestMap2.put("app_id", app_id); // appId
									requestMap2.put("account_no", originalinfo.getV_cardNo()); // 银行账号
									requestMap2.put("mobile", originalinfo.getV_phone()); // 银行预留电话，不填则使用进件电话
									if ("1".equals(originalinfo.getV_accountType())) {
										requestMap2.put("card_type", "01"); // 卡类型
									} else if ("2".equals(originalinfo.getV_accountType())) {
										requestMap2.put("card_type", "02"); // 卡类型
									}

									String call_back_url = BaseUtil.url+"/quickPayAction/syysReturnUrl.action?order_num="
											+ originalinfo.getV_oid();

									requestMap2.put("call_back_url", call_back_url); // 跳转页面地址
									requestMap2.put("notify_url",
											BaseUtil.url+"/quickPayAction/syysNotifyUrl.action"); // 接收异步通知地址
									requestMap2.put("open_type", "JF_OPEN"); // 开卡类型，为空，默认为UNION_PAY_OPEN
									requestMap2.put("channel", "WLB"); // 渠道
									requestMap2.put("version", "V2.1.1"); // 版本编号
									requestMap2.put("charset", "UTF-8"); // 字符编码
									String key = "0295a406899f4c3783ef4e22eef5ae9f";// md5key
									// 得到带签名数据
									Map<String, ?> filterMap = PayCore.paraFilter(requestMap2);

									String linkStr = PayCore.createLinkString(filterMap);
									logger.info("签名公钥" + key);
									logger.info("待签数据" + linkStr);
									String hexSign = "";
									try {
										hexSign = PayCore.md5Sign(linkStr, key);
									} catch (Exception e) {
										e.printStackTrace();
									}
									logger.info("签名数据:" + hexSign);
									requestMap2.put("sign_type", "MD5"); // md5签名
									requestMap2.put("sign_info", hexSign);
									String requestStr = JSON.toJSONString(requestMap2);
									url = "http://pay.unvpay.com/services/fastpay/openCard";
									String respStr = HttpClientUtil.post(url, "UTF-8", requestStr);
									logger.info("返回值：" + respStr);
									json = JSONObject.parseObject(respStr);
									if ("0000".equals(json.get("ret_code"))) {
										String param = json.getString("ret_data");
										JSONObject jb = JSONObject.parseObject(param);
										String auth_id = (String) jb.get("auth_id");
										PmsWeixinMerchartInfo merchartInfo = new PmsWeixinMerchartInfo();
										merchartInfo.setAccount(auth_id);// 账号
										merchartInfo.setMerchartId(originalinfo.getV_mid());
										merchartInfo.setMerchartName(merchantList.get(0).getMercName());
										merchartInfo.setMerchartNameSort(merchantList.get(0).getShortname());
										merchartInfo.setCertNo(originalinfo.getV_cert_no());// 证件号
										merchartInfo.setCardNo(originalinfo.getV_cardNo());// 卡号
										merchartInfo.setRealName(originalinfo.getV_realName());// 姓名
										merchartInfo.setMobile(originalinfo.getV_phone());// 手机号
										merchartInfo.setBankName(bank.getBank_name());// 开户行
										merchartInfo.setProvince(bank.getBank_province());// 省份
										merchartInfo.setCity(bank.getBank_city());// 城市
										merchartInfo.setDebitRate(originalinfo.getV_userFee());// 借记卡费率
										merchartInfo.setoAgentNo("100333");
										merchartInfo.setRateCode(originalinfo.getV_mid());
										int i = weixinService.updateRegister(merchartInfo);
										if (i > 0) {
											OriginalOrderInfo oo = new OriginalOrderInfo();
											oo.setByUser(app_id);
											oo.setSumCode(auth_id);
											oo.setOrderId(originalinfo.getV_oid());
											num = originalDao.update(oo);
											if (num > 0) {
												url = (String) jb.get("qr_code");
												logger.info("沈阳银盛返回的url:" + url);
												retMap.put("url", url);
												retMap.put("v_code", "00000");
												retMap.put("v_msg", "请求成功");
											} else {
												logger.info("app_id修改失败!");
											}
										}

									} else {

										retMap.put("v_code", "15");
										retMap.put("v_msg", "请求失败");
										return retMap;
									}

								} else {
									logger.info("沈阳银盛进件app_id:" + model.getAccount());
									logger.info("沈阳银盛进件:auth_id" + pwm.getAccount());
									Map<String, String> requestMap2 = new HashMap<String, String>();
									requestMap2.put("order_num", originalinfo.getV_oid()); // 流水号
									requestMap2.put("app_id", model.getAccount()); // appId
									requestMap2.put("auth_id", pwm.getAccount()); // 银行账号
									Double amount = Double.parseDouble(originalinfo.getV_txnAmt());
									Integer number1 = amount.intValue();
									requestMap2.put("amount", number1.toString());
									requestMap2.put("encrypt", "T0"); // 卡类

									String key = "0295a406899f4c3783ef4e22eef5ae9f";// md5key
									// 得到带签名数据
									Map<String, ?> filterMap = PayCore.paraFilter(requestMap2);

									String linkStr = PayCore.createLinkString(filterMap);
									logger.info("沈阳银盛签名公钥" + key);
									logger.info("沈阳银盛待签数据" + linkStr);
									String hexSign = PayCore.md5Sign(linkStr, key);
									logger.info("沈阳银盛签名数据:" + hexSign);
									requestMap2.put("sign_type", "MD5"); // md5签名
									requestMap2.put("sign_info", hexSign);
									String requestStr = JSON.toJSONString(requestMap2);

									url = "http://pay.unvpay.com/services/fastpay/submitOrder";
									String respStr = xdt.quickpay.syys.HttpClientUtil.post(url, "UTF-8", requestStr);

									logger.info("沈阳银盛短信返回值：" + respStr);
									json = com.alibaba.fastjson.JSONObject.parseObject(respStr);
									if ("0000".equals(json.getString("ret_code"))) {

										JSONObject jb = json.getJSONObject("ret_data");
										String param = jb.getString("parmMap");
										logger.info("沈阳银盛短信返回的ret_data：" + param);
										JSONObject jb1 = JSONObject.parseObject(param);
										String token = jb1.getString("token");
										logger.info("沈阳银盛短信返回的token：" + token);
										OriginalOrderInfo oo = new OriginalOrderInfo();
										oo.setOrderId(originalinfo.getV_oid());
										oo.setSumCode(token);
										oo.setByUser(model.getAccount());
										;
										oo.setProcdutNum("0000");
										num = originalDao.update(oo);
										if (num > 0) {
											retMap.put("v_mid", originalinfo.getV_mid());
											retMap.put("v_txnAmt", originalinfo.getV_txnAmt());
											retMap.put("v_time", originalinfo.getV_time());
											retMap.put("v_code", "00");
											retMap.put("v_msg", "请求成功");
											retMap.put("v_oid", originalinfo.getV_oid());
										}
									}

								}

							}

							break;

						case "888101700005315":// 汇聚快捷获取短信验证码
							Map<String, String> result = new HashMap<>();
							StringBuilder strs = new StringBuilder();
							strs.append(HJUtil.Version2);
							strs.append(pmsBusinessPos.getBusinessnum());// pmsBusinessPos.getBusinessnum()
							strs.append(originalinfo.getV_productDesc());
							strs.append(originalinfo.getV_oid());
							strs.append(originalinfo.getV_txnAmt());
							strs.append("1");
							strs.append("FAST");
							strs.append(originalinfo.getV_realName());
							strs.append("1");
							strs.append(originalinfo.getV_cert_no());
							strs.append(originalinfo.getV_cardNo());
							strs.append(originalinfo.getV_expired() == null ? "" : originalinfo.getV_expired());// YYYY-MM
							strs.append(originalinfo.getV_cvn2() == null ? "" : originalinfo.getV_cvn2());
							strs.append(originalinfo.getV_phone());
							//strs.append(originalinfo.getV_attach() == null ? "" : originalinfo.getV_attach());
							logger.info("汇聚待签名数据:" + strs.toString());
							// String hmac =MD5Utils.sign(str.toString(), HJUtil.privateKey,
							// "UTF-8");//RSAUtils.sign(str.toString().getBytes("UTF-8"),
							// HJUtil.privateKey);
							String hmac = DigestUtils.md5Hex(strs.toString() + pmsBusinessPos.getKek());// pmsBusinessPos.getKek()
							result.put("p0_Version", HJUtil.Version2);
							result.put("p1_MerchantNo", pmsBusinessPos.getBusinessnum());
							result.put("p2_MerchantName",URLEncoder.encode(originalinfo.getV_productDesc(), "utf-8")  );
							result.put("q1_OrderNo", originalinfo.getV_oid());
							result.put("q2_Amount", originalinfo.getV_txnAmt());
							result.put("q3_Cur", "1");
							result.put("q8_FrpCode", "FAST");
							result.put("s1_PayerName",URLEncoder.encode(originalinfo.getV_realName(), "utf-8") );
							result.put("s2_PayerCardType", "1");
							result.put("s3_PayerCardNo", originalinfo.getV_cert_no());
							result.put("s4_PayerBankCardNo", originalinfo.getV_cardNo());
							if (originalinfo.getV_expired() != null && originalinfo.getV_expired() != "") {
								result.put("s5_BankCardExpire", originalinfo.getV_expired());
							}
							if (originalinfo.getV_cvn2() != null && originalinfo.getV_cvn2() != "") {
								result.put("s6_CVV2", originalinfo.getV_cvn2());
							}
							result.put("s7_BankMobile", originalinfo.getV_phone());
							//if (originalinfo.getV_attach() != null && originalinfo.getV_attach() != "") {
							//	result.put("t1_ext", originalinfo.getV_attach());
							//}
							result.put("hmac", URLEncoder.encode(hmac, "utf-8"));
							TreeMap<String, String> paramsMap = new TreeMap<>();
							paramsMap.putAll(result);
							String paramSrc = RequestUtils.getParamSrc(paramsMap);
							logger.info("汇聚快捷支付给上游发送的数据:" + paramSrc);
							//String retuString = RequestUtils.sendPost(HJUtil.scanCodePay, "hmac=ab00408505d7fa77077a760a3bb0cb47&p0_Version=2.0&p1_MerchantNo=888101700005315&p2_MerchantName=测试商品&q1_OrderNo=QP20180411143509323540&q2_Amount=1&q3_Cur=1&q8_FrpCode=FAST&s1_PayerName=李娟&s2_PayerCardType=1&s3_PayerCardNo=120105197510055420&s4_PayerBankCardNo=6228450028016697770&s7_BankMobile=13323358548" );
							String retuString = PostUtils.doPost(HJUtil.quickPay, paramsMap);
							//HttpService HT = new HttpService();
							//String retuString = HT.POSTReturnString(HJUtil.quickPay, result, MBUtil.codeG);
							logger.info("汇聚返回字符串参数：" + retuString);
							net.sf.json.JSONObject jsons = net.sf.json.JSONObject.fromObject(retuString);
							if ("100".equals(jsons.get("ra_Status"))) {
								retMap.put("v_code", "00");
								retMap.put("v_msg", jsons.getString("rb_Msg"));
								retMap.put("v_oid", originalinfo.getV_oid());
								retMap.put("v_txnAmt", originalinfo.getV_txnAmt());

							} else {
								retMap.put("v_code", "01");
								retMap.put("v_msg", jsons.getString("rb_Msg"));
							}
							break;
						case "888888888888888":// 聚佰宝快捷
							 weixin = new PmsWeixinMerchartInfo();

							weixin.setCardNo(originalinfo.getV_settleCardNo());
							weixin.setRealName(originalinfo.getV_settleName());
							weixin.setCertNo(originalinfo.getV_cert_no());
							weixin.setMobile(originalinfo.getV_phone());

							model = weixinService.selectByCardEntity(weixin);

							logger.info("聚佰宝快捷原订单数据:" + JSON.toJSON(model));

							if (model.getAccount() == null) {
								// 商户编号
								String merchantId = "888201711290115";
								// 业务代码
								String bussId = "ONL0003";
								String key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC35dOgycZisH24maQMHKVF4B/UNcyJULOJKCS6PUPhiLZAdPgR6ulwoWTtmN8v58PX4TDculU88aFz9CSlpfA72r0bHhhzDJdBP2Ki2Rc/RHwu6vrF9tbv0EPL7db/ECZe7CPZykLn/5JRlVpbsQWsx7gqQaiOY2nw4U6Lo13UoQIDAQAB";
								RSAPublicKey publicKey = RSAEncrypt.loadPublicKeyByStr(key);
								String certNo = originalinfo.getV_cert_no();
								byte[] cipherData = RSAEncrypt.encrypt(publicKey, certNo.getBytes("UTF-8"));
								// rsa 加密之后的卡号
								String rsacertNo = xdt.quickpay.jbb.util.Base64.encode(cipherData);

								// 订单信息
								String merOrderNum = originalinfo.getV_oid();

								// 前台回调
								String frontUrl =BaseUtil.url+"/quickPayAction/jbbNotifyUrl.action";
								// 签名数据
								String txnString = "bussId=" + bussId + "&certNo=" + rsacertNo + "&frontUrl=" + frontUrl
										+ "&merchantId=" + merchantId + "&merOrderNum=" + merOrderNum + "&";
								logger.info("聚佰宝上送的数据加密字符串:" + txnString);
								MD5 md = new MD5();
								String signValue = md.getMD5ofStr(txnString + "675FC1ctf2Y6zVm3");

								String txn = "bussId=" + bussId + "&certNo=" + URLEncoder.encode(rsacertNo, "utf-8")
										+ "&frontUrl=" + frontUrl + "&merchantId=" + merchantId + "&merOrderNum="
										+ merOrderNum + "&signValue=" + signValue;

								logger.info("聚佰宝上送的数据:" + txn);

								url = "https://cashier.etonepay.com/NetPay/quickPaySign.action?" + txn;

								HttpURLConection http = new HttpURLConection();

								HttpUtil h = new HttpUtil();

								String resonpe = http
										.httpURLConnectionPOST("https://cashier.etonepay.com/NetPay/quickPaySign.action", txn);
								logger.info("响应结果:" + resonpe);
								net.sf.json.JSONObject ob = net.sf.json.JSONObject.fromObject(resonpe);
								Iterator it = ob.keys();
								String respCode = "";
								String html = "";
								while (it.hasNext()) {
									key = (String) it.next();
									if (key.equals("respCode")) {

										respCode = ob.getString(key);

										logger.info("聚佰宝签约响应状态码:" + respCode);

									}
									if (key.equals("html")) {

										html = ob.getString(key);

										logger.info("聚佰宝签约html:" + html);
									}
								}
								if ("0000".equals(respCode)) {
									retMap.put("v_code", "00000");
									retMap.put("v_msg", "请求成功");
									retMap.put("html", html);
								}
								
							}else
							{

								logger.info("##################聚佰宝获取短信接口##########");
								// 商户编号
								String merchantId = "888201711290115";
								// 业务代码
								String bussId = "ONL0003";									
								Integer amount = (int) (Double.parseDouble(originalinfo.getV_txnAmt()) * 100);

								String tranAmt = amount.toString();
								
								String protocolNo="";

								// 订单信息
								String merOrderNum = originalinfo.getV_oid();
								// 签名数据
								String txnString = "bussId=" + bussId + "&merchantId=" + merchantId + "&merOrderNum="
										+ merOrderNum + "&protocolNo=" + protocolNo + "&tranAmt=" + tranAmt + "&";
								logger.info("聚佰宝上送的数据加密字符串:" + txnString);
								MD5 md = new MD5();
								String signValue = md.getMD5ofStr(txnString + "675FC1ctf2Y6zVm3");

								String txn = "bussId=" + bussId + "&merchantId=" + merchantId + "&merOrderNum=" + merOrderNum
										+ "&protocolNo=" + protocolNo + "&tranAmt=" + tranAmt.toString() + "&signValue="
										+ signValue;

								logger.info("聚佰宝上送的数据:" + txn);

								url = "https://cashier.etonepay.com/NetPay/quickPaySms.action?" + txn;

								HttpURLConection http = new HttpURLConection();

								HttpUtil h = new HttpUtil();

								String resonpe = http.httpURLConnectionPOST("https://cashier.etonepay.com/NetPay/quickPaySms.action",
										txn);
								logger.info("响应结果:" + resonpe);
								net.sf.json.JSONObject ob = net.sf.json.JSONObject.fromObject(resonpe);
								Iterator it = ob.keys();
								String transId="";
								
								String respCode="";
								while (it.hasNext()) {
									String key = (String) it.next();
									if (key.equals("respCode")) {

										respCode = ob.getString(key);

										logger.info("聚佰宝签约响应状态码:" + respCode);

									}
									if (key.equals("merOrderNum")) {

										merOrderNum = ob.getString(key);

										logger.info("聚佰宝签约html:" + merOrderNum);
									}
									if (key.equals("transId")) {

										transId = ob.getString(key);

										logger.info("聚佰宝签约transId:" + transId);
									}
								}
								if ("0000".equals(respCode)) {
									OriginalOrderInfo info = new OriginalOrderInfo();
									info.setOrderId(merOrderNum);
									info.setByUser(transId);// 商户号
									info.setBankId(protocolNo);
									number = originalDao.update(info);
									if(number>0)
									{
										retMap.put("v_oid", originalinfo.getV_oid());
										retMap.put("v_txnAmt", originalinfo.getV_txnAmt());
										retMap.put("v_code", "00");
										retMap.put("v_msg", "请求成功");
										retMap.put("v_time", originalinfo.getV_time());
										retMap.put("v_mid", originalinfo.getV_mid());
									}								
								}
							
							}
							
							break;
						case "000001110100000812":// 裕福快捷
						case "000001220100000470":
						case "000001110100000663":
							if (originalinfo.getV_userId() == null || "".equals(originalinfo.getV_userId())) {
								retMap.put("v_code", "01");
								retMap.put("v_msg", "v_userId is null");
								return retMap;
							}
							final String merCertPath = new File(this.getClass().getResource("/").getPath())
									.getParentFile().getParentFile().getCanonicalPath() + "//ky//"
									+ pmsBusinessPos.getBusinessnum() + ".cer";
							final String pfxPath = new File(this.getClass().getResource("/").getPath()).getParentFile()
									.getParentFile().getCanonicalPath() + "//ky//" + pmsBusinessPos.getBusinessnum()
									+ ".pfx";
							final String pfxPwd = pmsBusinessPos.getKek();
							QuickReq req = new QuickReq();
							YufuCipher cipher = null;
							YufuCipherSupport instance = null;
							cipher = YufuCipherSupport.getCipherInstance( merCertPath, pfxPath, pfxPwd,cipher,instance);
							//YufuCipher cipher = YufuCipherSupport.getCipherInstance(merCertPath, pfxPath, pfxPwd);
							try {
								List<Map<String, String>> list = new ArrayList<>();
								Map<String, String> reqMaps = new HashMap<>();
								req.setVersion("1.0.0");
								req.setMerchantId(pmsBusinessPos.getBusinessnum());
								req.setMerchantOrderId(originalinfo.getV_oid());
								req.setMerchantOrderTime(originalinfo.getV_time());
								Double dd = Double.parseDouble(originalinfo.getV_txnAmt()) * 100;
								Integer ii = dd.intValue();
								req.setMerchantOrderAmt(ii.toString());
								// req.setMerchantDisctAmt(merchantDisctAmt);
								req.setMerchantOrderCurrency("156");
								req.setGwType("04");
								req.setBackUrl(YFUtil.notifyUrl);
								// req.setUserType("01");
								req.setMerchantUserId(originalinfo.getV_userId());

								// {cardInfoList=, respDesc=调用接口成功, merchantId=000001220100000470,
								// merchantUserId=1523167250194, version=1.0.0, respCode=0000}
								req.setMerchantSettleInfo("[{\"merchantId\":\"" + pmsBusinessPos.getBusinessnum()
										+ "\",\"merchantName\":\"" + originalinfo.getV_productDesc()
										+ "\",\"orderAmt\":\"" + ii + "\"," + "\"sumGoodsName \":\""
										+ originalinfo.getV_productDesc() + "\"}]");

								req.setMerchantOrderDesc(originalinfo.getV_productDesc());

								// req.setMerchantSettleInfo("[{\"merchantId\":\"000001110100000812\",\"merchantName\":\"商户00000111050000001\",\"orderAmt\":100,\"sumGoodsName\":\"我是00000111050000001的商品，哈哈哈\"}]"
								// );
								req.setMsgExt(originalinfo.getV_attach());
								// req.setMisc("");

								if ("".equals(originalinfo.getV_verifyId()) || originalinfo.getV_verifyId() == null) {
									if ("".equals(originalinfo.getV_cardNo()) || originalinfo.getV_cardNo() == null) {
										retMap.put("v_code", "01");
										retMap.put("v_msg", "v_cardNo is null");
										return retMap;
									} else {
										reqMaps.put("cardNo", originalinfo.getV_cardNo());// originalinfo.getV_cardNo()
									}
									if ("1".equals(originalinfo.getV_accountType())) {
										reqMaps.put("cardType", "P1");
									} else if ("2".equals(originalinfo.getV_accountType())) {
										retMap.put("v_code", "01");
										retMap.put("v_msg", "此商户不支持贷记卡");
										return retMap;
										//reqMaps.put("cardType", "P2");
									} else {
										retMap.put("v_code", "01");
										retMap.put("v_msg", "v_accountType is null");
										return retMap;
									}
									if (!"".equals(originalinfo.getV_realName())
											&& originalinfo.getV_realName() != null) {
										reqMaps.put("name", originalinfo.getV_realName());
									} else {
										retMap.put("v_code", "01");
										retMap.put("v_msg", "v_realName is null");
										return retMap;
									}
									if(bank2!=null) {
										reqMaps.put("bankNo", bank2.getBank_code());
									}else {
										reqMaps.put("bankNo", "");
									}
									if (!"".equals(originalinfo.getV_cert_no())
											&& originalinfo.getV_cert_no() != null) {
										reqMaps.put("certNo", originalinfo.getV_cert_no());
									} else {
										retMap.put("v_code", "01");
										retMap.put("v_msg", "v_cert_no is null");
										return retMap;
									}
									reqMaps.put("certType", "01");
									reqMaps.put("phone", originalinfo.getV_phone());
									reqMaps.put("cvn2",
											originalinfo.getV_cvn2() == null ? "" : originalinfo.getV_cvn2());
									reqMaps.put("expired",
											originalinfo.getV_expired() == null ? "" : originalinfo.getV_expired());
									req.setPayCardList("[{\"cardNo\":\"" + reqMaps.get("cardNo") + "\",\"cardType\":\""
											+ reqMaps.get("cardType") + "\",\"bankNo\":\"" + reqMaps.get("bankNo")
											+ "\",\"certNo\":\"" + reqMaps.get("certNo")
											+ "\",\"certType\":\"01\",\"name\":\"" + reqMaps.get("name")
											+ "\",\"phone\":\"" + reqMaps.get("phone") + "\",\"cvn2\":\""
											+ reqMaps.get("cvn2") + "\",\"expired\":\"" + reqMaps.get("expired")
											+ "\"}]");
								} else {
									req.setVerifyId(originalinfo.getV_verifyId());
								}

								// req.setPayCardList("[{\"cardNo\":\""+originalinfo.getV_cardNo()+"\",\"cardType\":\"P1\",\"bankNo\":\"CDB\",\"certNo\":\""+originalinfo.getV_cert_no()+"\","
								// +
								// "\"certType\":\"01\",\"name\":\""+originalinfo.getV_realName()+"\",\"phone\":\""+originalinfo.getV_phone()+"\",\"cvn2\":\""+originalinfo.getV_cvn2()==null?"":originalinfo.getV_cvn2()+"\",\"expired\":\""+originalinfo.getV_expired()==null?"":originalinfo.getV_expired()+"\"}]");
								//
								// req.setPayCardList(JSONObject.toJSONString(list));

								String data = GsonUtil.objToJson(req);
								logger.info("data:" + data);
								Map<String, String> params = GsonUtil.jsonToObj(data, Map.class);
								ParamPacket bo = cipher.doPack(params);
								logger.info("11!:" + JSON.toJSON(bo));
								TreeMap<String, String> map_param = new TreeMap<>();
								map_param.put("merchantId", pmsBusinessPos.getBusinessnum());
								map_param.put("data", URLEncoder.encode(bo.getData(), "utf-8"));
								map_param.put("enc", URLEncoder.encode(bo.getEnc(), "utf-8"));
								map_param.put("sign", URLEncoder.encode(bo.getSign(), "utf-8"));
								String urlPay = "";
								if ("000001110100000812".equals(pmsBusinessPos.getBusinessnum())) {
									urlPay = "http://malltest.yfpayment.com/payment/service/payset.do";
								} else  {
									urlPay = "http://www.yfpayment.com/payment/service/payset.do";
								}
								String returnStr = PostUtils.doPost(urlPay, map_param);

								if (returnStr != null && !"".equals(returnStr)) {
									// 二、验签解密
									returnStr = URLDecoder.decode(returnStr, "utf-8");
									System.out.println("URL解码后的置单应答结果：" + returnStr);
									TreeMap<String, String> boMap = JSON.parseObject(returnStr,
											new TypeReference<TreeMap<String, String>>() {
											});
									Map<String, String> payshowParams = cipher.unPack(
											new ParamPacket(boMap.get("data"), boMap.get("enc"), boMap.get("sign")));
									System.out.println("解密后的置单应答结果：" + payshowParams);
									if ("0000".equals(payshowParams.get("respCode"))) {
										OriginalOrderInfo oo = new OriginalOrderInfo();
										oo.setOrderId(originalinfo.getV_oid());
										oo.setSumCode(payshowParams.get("token"));
										oo.setProcdutNum("0000");
										num = originalDao.update(oo);
										if (num > 0) {
											retMap.put("v_code", "00");
											retMap.put("v_msg", payshowParams.get("respDesc"));
											retMap.put("v_attach", payshowParams.get("msgExt"));
											retMap.put("v_oid", originalinfo.getV_oid());
											retMap.put("v_mid", originalinfo.getV_mid());
										} else {
											logger.info("app_id修改失败!");
											retMap.put("v_code", "01");
											retMap.put("v_msg", "系统错误,预下单失败");
										}
									} else {
										retMap.put("v_code", "01");
										retMap.put("v_msg", payshowParams.get("respDesc"));
									}

								} else {
									System.out.println("置单返回报文为空！");
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
						case "10044":// 柜银云快捷
							Map<String, Object> infoMap = new HashMap<String, Object>();
							infoMap.put("agentNo", "10033");// 机构号
							infoMap.put("merName", originalinfo.getV_productDesc());// 商户名称
							infoMap.put("merAddr", originalinfo.getV_attach());// 商户地址
							infoMap.put("settleName", originalinfo.getV_realName());// 结算姓名
							infoMap.put("settlePhone", originalinfo.getV_phone());// 手机号
							infoMap.put("settleBank", bank.getBank_short_title());// 开户银行
							infoMap.put("settleBankNo", bank.getBank_code());// 银行简码
							infoMap.put("settleAccount", originalinfo.getV_settleCardNo());// 结算卡号
							infoMap.put("settleBankSub", bank.getBank_name());// 开户支行
							infoMap.put("settleBankBranch", originalinfo.getV_settlePmsBankNo());// 联行号
							infoMap.put("settleSubProvince", bank.getBank_province());// 开户省
							infoMap.put("settleSubCity", bank.getBank_city());// 开户市

							infoMap.put("settleIdCard", originalinfo.getV_cert_no());// 身份证号
							String params = ApiUtil.sortMap(infoMap);
							logger.info("柜银云商户入驻提交信息：" + params);
							String reStr = ApiUtil.sendPost("http://139.224.27.56/ygww/sys/api/outer/addMer.do",
									params);
							logger.info("柜银云商户入驻返回结果：" + reStr);
							Map reMap = ApiUtil.toMap(reStr);
							String respCodes = reMap.get("respCode").toString();
							logger.info("柜银云商户入驻状态码：" + respCodes);
							String merKeys = "";
							String merNo = "";
							String checkFlag = "";
							String expired = originalinfo.getV_expired();
							char[] cc = expired.toCharArray();
							StringBuffer bb = new StringBuffer();
							bb.append(cc[2]);
							bb.append(cc[3]);
							bb.append(cc[0]);
							bb.append(cc[1]);
							logger.info("下游上传的信用卡有效期：" + bb);
							if ("0000".equals(respCodes)) {
								merKeys = reMap.get("merKey").toString();
								logger.info("柜银云商户入驻返回商户密钥：" + merKeys);
								merNo = reMap.get("merNo").toString();
								logger.info("柜银云商户入驻返回商户号：" + merNo);

								Map<String, Object> infoMaps = new HashMap<String, Object>();
								infoMaps.put("agentNo", "10033");// 机构号
								infoMaps.put("merNo", merNo);// 商户号
								infoMaps.put("service", "ncldd0");// 通道
								infoMaps.put("lservice", "checkName");// 接口
								infoMaps.put("out_trade_no", ApiUtil.newDateMore());// 交易流水号
								infoMaps.put("payRate", originalinfo.getV_userFee());// 交易流水号
								infoMaps.put("settleFee", originalinfo.getV_settleUserFee());// 交易流水号
								params = ApiUtil.sortMap(infoMaps);
								sign = xdt.quickpay.gyy.util.MD5.getSign(infoMaps, merKeys);
								params += "&sign=" + sign;
								logger.info("柜银云开通快捷提交信息：" + params);
								reStr = ApiUtil.sendPost("http://139.224.27.56/ygww/sys/api/outer/geteway.do", params);
								logger.info("柜银云开通快捷返回结果：" + reStr);
								reMap = ApiUtil.toMap(reStr);
								respCodes = reMap.get("respCode").toString();
								logger.info("柜银云商户入驻状态码：" + respCodes);
								if ("0000".equals(respCodes)) {
									Map<String, Object> infoMapss = new HashMap<String, Object>();
									infoMapss.put("agentNo", "10033");// 机构号
									infoMapss.put("merNo", merNo);// 商户号
									infoMapss.put("service", "ncldd0");// 通道
									infoMapss.put("lservice", "applyTrade");// 接口
									infoMapss.put("accNo", originalinfo.getV_cardNo());// 交易卡
									infoMapss.put("out_trade_no", originalinfo.getV_oid());// 交易流水
									infoMapss.put("cvn2", originalinfo.getV_cvn2());// cvn2

									infoMapss.put("useTime", bb);// 有效期
									infoMapss.put("orderTime", originalinfo.getV_time());// 接口
									infoMapss.put("amount", originalinfo.getV_txnAmt());// 交易金额，元

									params = ApiUtil.sortMap(infoMapss);
									sign = xdt.quickpay.gyy.util.MD5.getSign(infoMapss, merKeys);
									params += "&sign=" + sign;
									logger.info("柜银云开通快捷提交信息：" + params);
									reStr = ApiUtil.sendPost("http://139.224.27.56/ygww/sys/api/outer/geteway.do",
											params);
									logger.info("柜银云开通快捷返回结果：" + reStr);
									reMap = ApiUtil.toMap(reStr);
									respCodes = reMap.get("respCode").toString();
									logger.info("柜银云开通快捷状态码：" + respCodes);
									if ("0000".equals(respCodes)) {
										checkFlag = reMap.get("checkFlag").toString();
										Map<String, Object> infoMapsss = new HashMap<String, Object>();
										infoMapsss.put("agentNo", "10033");// 机构号
										infoMapsss.put("merNo", merNo);// 商户号
										infoMapsss.put("service", "ncldd0");// 通道
										infoMapsss.put("lservice", "sendSMS");// 接口
										infoMapsss.put("cvn2", originalinfo.getV_cvn2());// cvn2
										infoMapsss.put("phone", originalinfo.getV_phone());// cvn2
										infoMapsss.put("useTime", bb);// 有效期
										infoMapsss.put("out_trade_no", originalinfo.getV_oid());// 交易流水
										infoMapsss.put("orderTime", originalinfo.getV_time());// 交易时间
										infoMapsss.put("checkFlag", checkFlag);// 交易标记

										params = ApiUtil.sortMap(infoMapsss);
										sign = xdt.quickpay.gyy.util.MD5.getSign(infoMapsss, merKeys);
										params += "&sign=" + sign;
										System.out.println("柜银云获取短信提交信息：" + params);
										reStr = ApiUtil.sendPost("http://139.224.27.56/ygww/sys/api/outer/geteway.do",
												params);
										System.out.println("柜银云获取短信返回结果：" + reStr);
										reMap = ApiUtil.toMap(reStr);
										respCodes = reMap.get("respCode").toString();
										logger.info("柜银云开通快捷状态码：" + respCodes);
										if ("0000".equals(respCodes)) {
											OriginalOrderInfo info = new OriginalOrderInfo();
											info.setOrderId(originalinfo.getV_oid());
											info.setByUser(merNo);// 商户号
											info.setSumCode(merKeys);
											info.setBankId(checkFlag);
											number = originalDao.update(info);
											if (number > 0) {
												retMap.put("v_mid", originalinfo.getV_mid());
												retMap.put("v_txnAmt", originalinfo.getV_txnAmt());
												retMap.put("v_time", originalinfo.getV_time());
												retMap.put("v_code", "00");
												retMap.put("v_msg", "请求成功");
												retMap.put("v_oid", originalinfo.getV_oid());
											} else {
												retMap.put("v_code", "15");
												retMap.put("v_msg", "请求失败");
											}
										} else {
											retMap.put("v_code", "15");
											retMap.put("v_msg", "请求失败");
										}
									} else {
										retMap.put("v_code", "15");
										retMap.put("v_msg", "请求失败");
									}

								} else {
									retMap.put("v_code", "15");
									retMap.put("v_msg", "请求失败");
								}

							} else {
								retMap.put("v_code", "15");
								retMap.put("v_msg", "请求失败");
							}

							break;
						case "20180413085019363857":// 上海漪雷快捷
							logger.info("###########上海漪雷快捷绑卡接口##########");
							Map<String, Object> infos = new HashMap<String, Object>();
							infos.put("tranType", "14000");// 机构号
							infos.put("merName", originalinfo.getV_productDesc());// 商户名称
							infos.put("merAddress", originalinfo.getV_productDesc());// 商户地址
							
							//对特殊字段进行公钥加密
							PublicKey publicKey=RSAUtil.getPublicKey(PUBLIC_KEY);
							Cipher ciphers = Cipher.getInstance("RSA");          
							ciphers.init(Cipher.ENCRYPT_MODE, publicKey);  
							//姓名加密
					        String name=ciphers.doFinal(originalinfo.getV_realName().getBytes()).toString();
							//身份证加密     
					        String idcard=ciphers.doFinal(originalinfo.getV_cert_no().getBytes()).toString();
							//借记卡加密       
					        String bankCard=ciphers.doFinal(originalinfo.getV_settleCardNo().getBytes()).toString();
							infos.put("name", name);// 结算姓名
							infos.put("idcard", idcard);// 手机号
							infos.put("bankCard", bankCard);// 开户银行
							infos.put("bankName", bank.getBank_short_title());// 银行简码
							infos.put("bankNo", "GSYH");// 结算卡号
							//手机号加密        
					        String preMobile=ciphers.doFinal(originalinfo.getV_phone().getBytes()).toString();
							infos.put("preMobile", preMobile);// 开户支行
							infos.put("bankCode", originalinfo.getV_settlePmsBankNo());// 联行号
							infos.put("branchBank", bank.getBank_name());// 开户省
							infos.put("province", bank.getBank_province());// 开户市
							infos.put("city", bank.getBank_city());// 身份证号					
							infos.put("tradeRate", Double.parseDouble(originalinfo.getV_userFee())*100+"");// 身份证号
							infos.put("drawFee", originalinfo.getV_settleUserFee());// 身份证号
							infos.put("custNo", originalinfo.getV_userId());// 身份证号
							String stris=RSAUtil.getSign(infos);
							logger.info("上海漪雷快捷绑卡签名前的数据:"+stris);
							//RSA私钥签名
							String signature = RSAUtil.sign(stris.getBytes("UTF-8"), PRIVACE_KEY);
							
							Map<String, String> mapstr=new HashMap<String,String>();
							
							mapstr.put("version", "1.0.0");
							mapstr.put("platformNos", "P20171012861761863371");
							mapstr.put("channelNo", "C20180424609343188755");
							mapstr.put("merNo", "M20180424611894665448");
							mapstr.put("signature", signature);
							logger.info("上海漪雷快捷绑卡上送的数据:"+HttpURLConection.parseParams(mapstr));
							String resultcode=HttpURLConection.httpURLConnectionPOST("http://spapi.beichuanglangrun.com/trade/handle",HttpURLConection.parseParams(mapstr));
							
							logger.info("上海漪雷快捷绑卡响应的数据:"+resultcode);
							logger.info("###########上海漪雷快捷获取短信验证码##########");
							Map<String, Object> infoMaps = new HashMap<String, Object>();
							infoMaps.put("tranType", "14000");// 机构号
							infoMaps.put("orderAmount", originalinfo.getV_productDesc());// 商户名称
							infoMaps.put("subject", originalinfo.getV_attach());// 商户地址
							infoMaps.put("merOrderNo", "M20180413085019363857");// 结算姓名
							infoMaps.put("orderNo", originalinfo.getV_phone());// 手机号
							infoMaps.put("tradeRate", bank.getBank_short_title());// 开户银行
							infoMaps.put("drawFee", bank.getBank_code());// 银行简码
							infoMaps.put("payBankCard", originalinfo.getV_settleCardNo());// 结算卡号
							infoMaps.put("payBankName", bank.getBank_name());// 开户支行
							infoMaps.put("payPreMobile", originalinfo.getV_settlePmsBankNo());// 联行号
							infoMaps.put("cvn2", bank.getBank_province());// 开户省
							infoMaps.put("expired", bank.getBank_city());// 开户市
							infoMaps.put("payBankCode", originalinfo.getV_cert_no());// 身份证号
							infoMaps.put("name", originalinfo.getV_cert_no());// 身份证号
							infoMaps.put("idcard", originalinfo.getV_cert_no());// 身份证号
							infoMaps.put("bankCard", originalinfo.getV_cert_no());// 身份证号
							infoMaps.put("bankName", originalinfo.getV_cert_no());// 身份证号
							infoMaps.put("bankCode", originalinfo.getV_cert_no());// 身份证号
							infoMaps.put("preMobile", originalinfo.getV_cert_no());// 身份证号
							infoMaps.put("backUrl", originalinfo.getV_cert_no());// 身份证号
							break;
						default:
							break;
						}
					}
				}
			} else {
				// 请求参数为空
				logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
				retMap.put("v_code", "16");
				retMap.put("v_msg", "商户没有进行实名认证,请重试或联系客服");
				return retMap;
			}
		} else {
			logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
			retMap.put("v_code", "16");
			retMap.put("v_msg", "商户没有进行实名认证,请重试或联系客服\"");
			return retMap;
		}
		return retMap;
	}

	/**
	 * 
	 * @Description 设置响应信息
	 * @author Administrator
	 * @param respCode
	 * @param respInfo
	 * @return
	 */
	private Map<String, String> setResp(String respCode, String respInfo) {
		Map<String, String> result = new HashMap<String, String>();
		result.put("v_code", respCode);
		result.put("v_msg", respInfo);
		return result;
	}

	/**
	 * 录入交易流水 并记算费率
	 * 
	 * @throws Exception
	 */
	public PospTransInfo InsertJournal(PmsAppTransInfo pmsAppTransInfo) throws Exception {
		logger.info("----插入流水开始----");
		PospTransInfo pospTransInfo = new PospTransInfo();
		Integer id = pospTransInfoDAO.getNextTransid();
		if (id != null && id != 0) {
			pospTransInfo.setId(id);
		} else {
			logger.info("根据订单生成流水失败，orderid：" + pmsAppTransInfo.getOrderid());
			return null;
		}
		// 获取通道的标准费率 END

		// 设置主机交易流水号
		pospTransInfo.setSysseqno(null);
		// 设置宣称费率
		pospTransInfo.setTransfee2(null);
		// 设置通道费率
		pospTransInfo.setTransfee4(null);
		// 设置实际佣金
		pospTransInfo.setTransfee1(null);
		// 设置消费冲正原因
		pospTransInfo.setReason(null);
		// 设置说明
		pospTransInfo.setRemark(pmsAppTransInfo.getTradetype() + "  金额：" + pmsAppTransInfo.getFactamount());
		// 设置SIM卡
		pospTransInfo.setSimId(null);
		// 设置 TAC
		pospTransInfo.setTac(null);
		// 设置银行编码
		pospTransInfo.setBnkCd(null);
		// 设置平台流水奥 这里默认设置第三方订单号
		pospTransInfo.setPospsn(pmsAppTransInfo.getPortorderid());
		// 设置卡有效期
		pospTransInfo.setCardvaliddate(null);
		// 设置通道pos终端号
		pospTransInfo.setBuspos(null);
		// 设置pos平台交易吗
		pospTransInfo.setPospservicecode(null);
		// 设置冲正流水
		pospTransInfo.setCancelflag(null);
		// 设置商户号
		pospTransInfo.setMerchantcode(pmsAppTransInfo.getMercid());
		// 设置补录时记录上传的终端机流水号
		pospTransInfo.setTerminalsn(null);
		// 设置交易上送帐期
		pospTransInfo.setSenddate(new Date());
		// 服务网点PIN码
		pospTransInfo.setCounterpin(null);
		// 设置渠道号 03：手机
		pospTransInfo.setChannelno("03");
		// 设置银行名称
		pospTransInfo.setBnkNm(null);
		// 设置posid
		pospTransInfo.setPosid(null);
		// 设置交易码 默认都为消费业务
		pospTransInfo.setTranscode("000000");
		// 设置交易安全控制信息
		pospTransInfo.setTranssecuritycontrol(null);
		// 设置卡类型
		pospTransInfo.setCrdTyp(null);
		// 设置卡号
		pospTransInfo.setCardno(null);
		// 设置真正的交易类型 交易码 +交易类型+支付方式
		pospTransInfo
				.setSearchTransCode("000000" + pmsAppTransInfo.getTradetypecode() + pmsAppTransInfo.getPaymentcode());
		// 设置pos交易日期
		pospTransInfo.setTransdate(UtilDate.getDate());
		// 设置pos交易时间
		pospTransInfo.setTranstime(UtilDate.getDateTime());
		// 设置批量结算结果标志
		pospTransInfo.setSettlementflag(null);
		// 设置最近批结算ID
		pospTransInfo.setSettlementid(null);
		// 设置授权码
		pospTransInfo.setAuthoritycode(null);
		// 设置是否自清 默认自清
		pospTransInfo.setIsClearSelf(null);
		// 设置交易响应标志 00-成功
		pospTransInfo.setResponsecode(null);
		/*
		 * if(pmsAppTransInfo.getStatus().equals("0")){
		 * pospTransInfo.setResponsecode("00"); }else{
		 * pospTransInfo.setResponsecode(null); }
		 */
		// 设置订单id
		pospTransInfo.setOrderId(pmsAppTransInfo.getOrderid());
		// 设置通道商户编码 商户编码不设置
		pospTransInfo.setBusinfo(null);
		// 设置附加费用
		pospTransInfo.setAddfee(null);
		// 设置刷卡费率 当前处理为调用第三方处理，刷卡费率不设置
		pospTransInfo.setPremiumrate(null);
		// 设置原始交易记录报文id
		pospTransInfo.setPfmtid(null);
		// 服务网点输入方式
		pospTransInfo.setInputtype(null);
		// 0-脱机POS上送流水，1-联机消费流水
		pospTransInfo.setTransstatus(null);
		// 设置基站信息
		pospTransInfo.setStationInfo(null);
		// 设置交易时间间隔 这里先不处理，没有发现需要用到的地方
		pospTransInfo.setInterVal(null);
		// 设置关联路由id
		pospTransInfo.setRouteid(null);
		// 设置交易消息类型 交易类型+支付方式
		pospTransInfo.setMsgtype(pmsAppTransInfo.getTradetypecode() + pmsAppTransInfo.getPaymentcode());
		// 设置发生额
		pospTransInfo.setTransamt(new BigDecimal(pmsAppTransInfo.getFactamount()));
		// 设置终端号
		pospTransInfo.setPosterminalid(null);
		// 设置操作员id
		pospTransInfo.setOperid(null);
		// 设置POS服务平台代码
		pospTransInfo.setPospid(null);
		// 设置货币代码
		pospTransInfo.setCurrencycode(null);
		// 结算日期
		pospTransInfo.setBalancedate(null);
		// PSAM卡号
		pospTransInfo.setPsamno(null);
		// 个人标识码
		pospTransInfo.setPersonalid(null);
		// 卡号
		pospTransInfo.setCrdNm(null);
		// 设置冲正标志 0-正常交易，1-冲正交易，2-被冲正交易
		pospTransInfo.setCancelflag(0);
		// 设置冻结状态
		pospTransInfo.setFreezeState(null);
		// 设置终端序列号
		pospTransInfo.setPossn(null);
		// 设置服务网点条件码
		pospTransInfo.setConuterconditioncode(null);
		// 是否App交易
		pospTransInfo.setIsapp(1);
		// 设置支付方式
		pospTransInfo.setPaymentType(pmsAppTransInfo.getPaymentcode());
		// 设置批次号
		pospTransInfo.setBatno(null);
		// O单编号
		pospTransInfo.setoAgentNo(pmsAppTransInfo.getoAgentNo());

		return pospTransInfo;
	}

	public synchronized int UpdatePmsMerchantInfo(OriginalOrderInfo originalInfo) throws Exception {
		logger.info("代付实时填金:" + JSON.toJSON(originalInfo));
		DecimalFormat df = new DecimalFormat("#.00");
		// PmsMerchantInfo pmsMerchantInfo =new PmsMerchantInfo();
		PmsDaifuMerchantInfo pmsDaifuMerchantInfo = new PmsDaifuMerchantInfo();
		PmsMerchantInfo merchantInfo = pmsMerchantInfoDao.selectMercByMercId(originalInfo.getPid());
		logger.info("merchantInfo:" + JSON.toJSON(merchantInfo));
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(originalInfo.getOrderId());
		logger.info("pmsAppTransInfo:" + JSON.toJSON(pmsAppTransInfo));
		pmsDaifuMerchantInfo.setBatchNo(originalInfo.getOrderId());
		PmsDaifuMerchantInfo daifuMerchantInfo = pmsDaifuMerchantInfoDao
				.selectByDaifuMerchantInfo(pmsDaifuMerchantInfo);
		logger.info("daifuMerchantInfo:" + JSON.toJSON(daifuMerchantInfo));
		if (daifuMerchantInfo != null) {
			logger.info("11111111111111111111111");
			return 0;
		} else {
			if ("0".equals(merchantInfo.getOpenPay())) {
				// 手续费
				Double poundage = Double.parseDouble(pmsAppTransInfo.getPoundage());
				poundage = Double.parseDouble(df.format(poundage));
				String position = merchantInfo.getPosition();
				Double amount = Double.parseDouble(originalInfo.getOrderAmount());
				logger.info("订单金额：" + amount);
				BigDecimal positions = new BigDecimal(position);
				// Double ds =positions.doubleValue();
				Double dd = amount * 100 - poundage;
				// dd =(dd+ds);
				logger.info("来了1---------");
				Map<String, String> map = new HashMap<>();
				map.put("machId", originalInfo.getPid());
				map.put("payMoney", dd.toString());
				int i = pmsMerchantInfoDao.updataPay(map);
				if (i != 1) {
					logger.info("实时填金失败！");
					// 状态
					pmsDaifuMerchantInfo.setResponsecode("01");
				} else {
					// 状态
					logger.info("实时成功！");
					pmsDaifuMerchantInfo.setResponsecode("00");
				}
				logger.info("来到这里了11！");
				PmsMerchantInfo info = select(originalInfo.getPid());
				// pmsMerchantInfo.setMercId(originalInfo.getPid());
				// pmsMerchantInfo.setPosition(df.format(dd));
				// 商户号
				logger.info("来到这里了22！");
				pmsDaifuMerchantInfo.setMercId(originalInfo.getPid());
				// 订单号
				pmsDaifuMerchantInfo.setBatchNo(originalInfo.getOrderId());
				// 总金额
				pmsDaifuMerchantInfo.setAmount((Double.parseDouble(originalInfo.getOrderAmount())) + "");

				// 备注
				pmsDaifuMerchantInfo.setRemarks("D0");
				// 记录描述
				pmsDaifuMerchantInfo.setRecordDescription(
						"订单号:" + originalInfo.getOrderId() + "交易金额:" + originalInfo.getOrderAmount());
				// 交易类型
				pmsDaifuMerchantInfo.setTransactionType(pmsAppTransInfo.getPaymenttype());
				// 发生额
				pmsDaifuMerchantInfo.setPayamount((Double.parseDouble(originalInfo.getOrderAmount())) + "");
				// 账户余额
				pmsDaifuMerchantInfo.setPosition(info.getPosition());
				// 手续费
				pmsDaifuMerchantInfo.setPayCounter(poundage / 100 + "");
				pmsDaifuMerchantInfo.setOagentno("100333");
				logger.info("来了2---------");
				// 交易时间
				// pmsDaifuMerchantInfo.setCreationdate(new SimpleDateFormat("yyyy-MM-dd
				// HH:mm:ss").format(new Date()));
				int s = pmsDaifuMerchantInfoDao.insert(pmsDaifuMerchantInfo);
				logger.info("---s:" + s);
				logger.info("来了3---------");
				// int i =pmsMerchantInfoDao.UpdatePmsMerchantInfo(pmsMerchantInfo);
				logger.info("---i:" + i);
				return i;
			} else {
				logger.info("此商户未开通代付！！");
			}
		}

		return 0;
	}

	public Map<String, String> payHandle(ConsumeRequestEntity originalinfo) throws Exception {

		Map<String, String> retMap = new HashMap<String, String>();
		// 商户号
		String merchId = originalinfo.getV_mid();
		// 查询上游商户号
		PmsBusinessPos busInfo = selectKey(merchId);
		// 商户订单号
		logger.info("******************根据商户号查询");
		// 验证当前是否已经下单
		OriginalOrderInfo orig = new OriginalOrderInfo();
		orig.setMerchantOrderId(originalinfo.getV_oid());
		orig.setPid(originalinfo.getV_mid());

		OriginalOrderInfo origial = originalDao.selectByOriginal(orig);
		// OriginalOrderInfo ori=originalDao.selectByOriginal()
		logger.info("快捷原始订单信息:" + origial);
		if (!StringUtils.isEmpty(origial.getPid())) {

			logger.info("快捷原始订单号:" + origial.getOrderId());

			if (origial.getOrderId().equals(originalinfo.getV_oid())) {

				logger.info("快捷原始订单信息交易类型:" + origial.getPayType());

				if (origial.getPayType().equals(originalinfo.getV_type())) {

					PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
					merchantinfo.setMercId(merchId);

					// o单编号
					String oAgentNo = "";

					// 查询当前商户信息
					List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
					if (!(merchantList.size() == 0 || merchantList.isEmpty())) {
						merchantinfo = merchantList.get(0);
						// merchantinfo.setCustomertype("3");

						oAgentNo = merchantinfo.getoAgentNo();

						if (StringUtils.isBlank(oAgentNo)) {
							// 如果没有欧单编号，直接返回错误
							logger.error("参数错误!");
							retMap.put("respCode", "16");
							retMap.put("respMsg", "参数错误,没有欧单编号");
							return retMap;
						}
						// 判断是否为正式商户
						if ("60".equals(merchantinfo.getMercSts())) {

							PmsBusinessPos pmsBusinessPos = selectKey(originalinfo.getV_mid());
							// 商户号码
							String merId = pmsBusinessPos.getBusinessnum();// 818310048160000
							// 商户号私钥
							String merKey = pmsBusinessPos.getKek();
							String sumcode = origial.getSumCode();
							logger.info("手机上获取的验证码:" + sumcode);
							PayBankInfo bank = new PayBankInfo();
							bank.setBank_pmsbankNo(origial.getSettlePmsBankNo());
							bank = payBankInfoDao.selectByBankInfo(bank);
							logger.info("查询结算商户银行信息:" + bank);
							PayBankInfo bank2 = new PayBankInfo();
							bank2.setBank_pmsbankNo(origial.getBankId());
							bank2 = payBankInfoDao.selectByBankInfo(bank2);
							logger.info("查询交易商户银行信息:" + bank2);
							switch (pmsBusinessPos.getBusinessnum()) {

							case "053211180":// 钱龙快捷
							case "053211181":// 钱龙快捷
								if (sumcode.equals(originalinfo.getV_smsCode())) {
									Integer amount = (int) (Double.parseDouble(origial.getOrderAmount()) * 100);
									Map<String, String> params = new HashMap<String, String>();

									// 设置上送信息
									if (originalinfo.getV_type().equals("1")) {
										params.put("appId", SampleConstant.APP_ID1);
										params.put("version", "2.0.1");
										params.put("payType", "20");
										// params.put("userfee", temp.getUserfee());
									} else if (originalinfo.getV_type().equals("0")) {
										Integer userfee = (int) (amount
												* (Double.parseDouble(origial.getUserFee()) / 100));
										params.put("appId", SampleConstant.APP_ID0);
										params.put("payType", "10");
										params.put("version", "3.0.1");
										params.put("toBankNo", origial.getSettlePmsBankNo());
										params.put("name", origial.getSettleUserName());
										params.put("certNo", origial.getCertNo());
										params.put("userfee", userfee.toString());
									}

									params.put("txnAmt", amount.toString());
									params.put("orderId", originalinfo.getV_oid());
									params.put("txnTime", originalinfo.getV_time());

									if (originalinfo.getV_type().equals("1")) {
										params.put("backUrl", SampleConstant.BACK_URLT1);
									} else if (originalinfo.getV_type().equals("0")) {
										params.put("backUrl", SampleConstant.BACK_URLT0);
									}
									params.put("accNo", origial.getBankNo());
									params.put("frontUrl", SampleConstant.FRONT_URL);
									String jsonString = JSON.toJSONString(params);
									logger.info("上送的数据:" + jsonString);

									byte[] encodeData = null;
									if (originalinfo.getV_type().equals("1")) {
										encodeData = PlatKeyGenerator.encryptByPublicKey(jsonString.getBytes("UTF-8"),
												SampleConstant.PUB_KEY1);
									} else if (originalinfo.getV_type().equals("0")) {
										encodeData = PlatKeyGenerator.encryptByPublicKey(jsonString.getBytes("UTF-8"),
												SampleConstant.PUB_KEY0);
									}
									String data = PlatBase64Utils.encode(encodeData);

									Map<String, String> result = new HashMap<String, String>();
									result.put("data", data);
									if (originalinfo.getV_type().equals("20")) {
										result.put("appId", SampleConstant.APP_ID1);
									} else if (originalinfo.getV_type().equals("10")) {
										result.put("appId", SampleConstant.APP_ID0);
									}
									logger.info("上送的数据为:" + result);
									String html = EffersonPayService.createAutoFormHtml(
											"http://unionpay.rytpay.com.cn/rytpay-business/v2/quick/pay.html", result,
											"UTF-8");

								} else {
									retMap.put("v_code", "19");
									retMap.put("v_msg", "短信验证不正确");
									return retMap;
								}
								break;
							case "936640995770000":// 摩宝快捷
							case "936775585060000":// 摩宝快捷
								Map<String, String> transmap = new LinkedHashMap<String, String>();
								transmap.put("versionId", "001");
								transmap.put("businessType", "1411");
								transmap.put("insCode", "");
								transmap.put("merId", "936640995770000");
								transmap.put("yzm", originalinfo.getV_smsCode()); // 从1401交易 获取的yzm 填入此项完成支付验证
								transmap.put("ksPayOrderId", originalinfo.getV_oid()); // 从1401交易 获取的ksPayOrderId 填入此项
																						// 寻找原交易
																						// 完成支付
								// 需要加密的字符串
								String signstr = EncodeUtil.getUrlStr(transmap);
								System.out.println("需要签名的明文" + signstr);
								String signtrue = MD5Util.MD5Encode(signstr + merKey);
								transmap.put("signType", "MD5");
								transmap.put("signData", signtrue);
								// AES加密
								String transUrlStr = EncodeUtil.getUrlStr(transmap);
								//
								String transData = AESUtil.encrypt(transUrlStr, merKey);
								// 获取交易返回结果
								String testUrl = MBUtil.quick;
								String str = DemoBase.requestBody(merId, transData, testUrl);
								System.out.println(str);
								ObjectMapper om = new ObjectMapper();
								Map<String, String> maps = new HashMap<>();
								retMap.put("v_mid", originalinfo.getV_mid());
								retMap.put("v_oid", originalinfo.getV_oid());
								retMap.put("v_time", originalinfo.getV_time());
								if ("00".equals(maps.get("status"))) {
									if ("00".equals(maps.get("refCode"))) {
										retMap.put("v_code", "00");
										retMap.put("v_msg", "请求成功");
										return retMap;
									} else if ("02".equals(maps.get("refCode"))) {
										retMap.put("v_code", "15");
										retMap.put("v_msg", "请求失败");
										return retMap;
									} else if ("03".equals(maps.get("refCode"))) {
										retMap.put("v_code", "15");
										retMap.put("v_msg", "请求失败");
										return retMap;
									} else {
										retMap.put("v_code", "15");
										retMap.put("v_msg", "请求失败");
										return retMap;
									}
								} else if ("01".equals(maps.get("status"))) {
									retMap.put("v_code", "15");
									retMap.put("v_msg", "请求失败");
									return retMap;
								} else if ("02".equals(maps.get("status"))) {
									retMap.put("v_code", "15");
									retMap.put("v_msg", "请求失败");
									return retMap;
								}
								break;
							case "936640995770002": // 摩宝快捷银联
								if (sumcode.equals(originalinfo.getV_smsCode())) {
									// Map<String, String> transmap1=new HashMap<String, String>();
									// transmap1 = new LinkedHashMap<String, String>();
									retMap.put("versionId", "001"); // 版本号 固定
									retMap.put("businessType", "1100"); // 预交易 1401
									retMap.put("merId", "936640995770000"); // 商户号
									retMap.put("orderId", originalinfo.getV_oid()); // 订单号
									retMap.put("transDate", originalinfo.getV_time()); // 时间 yymmddhhmmss
									retMap.put("transAmount", origial.getOrderAmount()); // 金额// 单位元，对于正式商户最低支付金额为10元
									retMap.put("transCurrency", "156"); // 此处的MD5util为Base64加密
									retMap.put("transChanlName", "UNIONPAY"); // 卡号
									// transmap.put("openBankName",mbReqest.getOpenBankName());//可为空 银行代码
									retMap.put("pageNotifyUrl", origial.getPageUrl()); // 证件类型 01 身份证
									retMap.put("backNotifyUrl", origial.getBgUrl());// 身份证
									String attch = new String(origial.getAttach().getBytes("ISO-8859-1"), "GBK");
									retMap.put("dev", attch); // 手机号
									// 需要加密的字符串
									signstr = "versionId=001&businessType=1100&merId=936640995770000&orderId="
											+ originalinfo.getV_oid() + "&transDate=" + originalinfo.getV_time()
											+ "&transAmount=" + origial.getOrderAmount()
											+ "&transCurrency=156&transChanlName=UNIONPAY&pageNotifyUrl="
											+ origial.getPageUrl() + "&backNotifyUrl=" + origial.getBgUrl() + "&dev="
											+ attch;
									logger.info("需要签名的明文" + signstr);
									signtrue = MD5Util.MD5Encode(signstr + "072C15B8D473BB29");
									// retMap.put("signType", "MD5");
									retMap.put("signData", signtrue);
									// AES加密
									// transUrlStr = EncodeUtil.getUrlStr(retMap);
									//
									// transData = AESUtil.encrypt(transUrlStr, "072C15B8D473BB29");
								} else {
									retMap.put("v_code", "19");
									retMap.put("v_msg", "短信验证不正确");
									return retMap;
								}
								break;
							case "936640995770001": // 摩宝快捷收银台
								if (sumcode.equals(originalinfo.getV_smsCode())) {
									// Map<String, String> transmap1=new HashMap<String, String>();
									// transmap1 = new LinkedHashMap<String, String>();
									retMap.put("versionId", "001"); // 版本号 固定
									retMap.put("businessType", "1100"); // 预交易 1401
									retMap.put("merId", "936640995770000"); // 商户号
									retMap.put("orderId", originalinfo.getV_oid()); // 订单号
									retMap.put("transDate", originalinfo.getV_time()); // 时间 yymmddhhmmss
									retMap.put("transAmount", origial.getOrderAmount()); // 金额// 单位元，对于正式商户最低支付金额为10元
									retMap.put("transCurrency", "156"); // 此处的MD5util为Base64加密
									// retMap.put("transChanlName", "UNIONPAY"); // 卡号
									// transmap.put("openBankName",mbReqest.getOpenBankName());//可为空 银行代码
									retMap.put("pageNotifyUrl", origial.getPageUrl()); // 证件类型 01 身份证
									retMap.put("backNotifyUrl", origial.getBgUrl());// 身份证
									String attch = new String(origial.getAttach().getBytes("ISO-8859-1"), "GBK");
									retMap.put("dev", attch); // 手机号
									// 需要加密的字符串
									signstr = "versionId=001&businessType=1100&merId=936640995770000&orderId="
											+ originalinfo.getV_oid() + "&transDate=" + originalinfo.getV_time()
											+ "&transAmount=" + origial.getOrderAmount()
											+ "&transCurrency=156&pageNotifyUrl=" + origial.getPageUrl()
											+ "&backNotifyUrl=" + origial.getBgUrl() + "&dev=" + attch;
									logger.info("需要签名的明文" + signstr);
									signtrue = MD5Util.MD5Encode(signstr + "072C15B8D473BB29");
									// retMap.put("signType", "MD5");
									retMap.put("signData", signtrue);
									// AES加密
									// transUrlStr = EncodeUtil.getUrlStr(retMap);
									//
									// transData = AESUtil.encrypt(transUrlStr, "072C15B8D473BB29");
								} else {
									retMap.put("v_code", "19");
									retMap.put("v_msg", "短信验证不正确");
									return retMap;
								}
								break;
							case "1800056392":// 合利宝快捷
							case "1800001582":// 合利宝快捷
							case "88882017092010001121": // 赢酷快捷
								if (sumcode.equals(originalinfo.getV_smsCode())) {

									// Map<String, String> paramMap = new HashMap<String, String>();

									retMap.put("merKey", LhzfUtil.merKey);

									retMap.put("transId", "QUICK_AGENT_PAY_H5");

									retMap.put("serialNo", originalinfo.getV_oid());

									retMap.put("transAmt", origial.getOrderAmount());
									//
									retMap.put("currency", "156");

									retMap.put("orderNo", originalinfo.getV_oid());

									Date orderDate = new Date();// 订单日期
									String orderDateStr = new SimpleDateFormat("yyyyMMdd").format(orderDate);// 订单日期
									retMap.put("transDate", orderDateStr);

									Date orderTime = new Date();// 订单时间
									String orderTimeStr = new SimpleDateFormat("yyyyMMddHHmmss").format(orderTime);// 订单时间
									retMap.put("transTime", orderTimeStr);

									retMap.put("orderDesc", origial.getProcdutName());

									retMap.put("returnUrl", LhzfUtil.returnUrl);

									retMap.put("notifyUrl", LhzfUtil.notifyUrl);

									retMap.put("remark", origial.getAttach());

									retMap.put("cardNo", origial.getBankNo());
									retMap.put("cardType", "02");
									retMap.put("idName", origial.getSettleUserName());
									retMap.put("idType", "01");
									retMap.put("idNo", origial.getCertNo());
									retMap.put("mobileNo", origial.getPhone());
									// payeeCardType-----payeeIdType
									retMap.put("payeeIdType", "01");
									retMap.put("payeeCardNo", origial.getSettleCardNo());
									// 我们平台收取金额
									retMap.put("userRate", origial.getUserFee());//
									retMap.put("userFee", origial.getSettleUserFee());//
									// -------------------
									retMap.put("payeeCurrency", "156");
									// paramMap.put("bankCode", bank2.getBank_code());
									retMap.put("payeeBankCode", bank.getBank_code());
									retMap.put("payeeIdNo", origial.getCertNo());

									retMap.put("payeeIdName", origial.getSettleUserName());
									retMap.put("payeeMobileNo", origial.getPhone());

									logger.info("瀛酷---签名前数据：" + JSON.toJSONString(retMap));
									///// 签名及生成请求API的方法///
									String paySecret = pmsBusinessPos.getKek();// LhzfUtil.paySecret;//
									String sign = MerchantApiUtil.getSign(retMap, paySecret);
									retMap.put("sign", sign);
									logger.info("瀛酷---签名后数据：" + JSON.toJSONString(retMap));
									retMap.put("v_code", "00");
									// String requestUrl = LhzfUtil.commonRequestUrl1;
									// paramMap.put("requestUrl", requestUrl);
									// paramMap.put("respCode", "00");
									// paramMap.put("respMsg", "请求成功");
								} else {
									retMap.put("v_code", "19");
									retMap.put("v_msg", "短信验证不正确");
									return retMap;
								}
								break;
							case "2017112113602199513":// 广州恒明无积分快捷
								JSONObject requestObj = new JSONObject();
								requestObj.put("payorderno", origial.getMerchantOrderId());
								requestObj.put("merchantid", "M" + pmsBusinessPos.getBusinessnum());
								requestObj.put("smscode", originalinfo.getV_smsCode());
								String encryptdata = AesEncryption.Encrypt(requestObj.toJSONString(), HMUtil.aeskey,
										HMUtil.aeskey);

								String timestamp = TimeUtil.getTime();
								signstr = SHA256Util
										.sha256(pmsBusinessPos.getKek() + "M" + pmsBusinessPos.getBusinessnum()
												+ encryptdata + timestamp + pmsBusinessPos.getKek());
								System.out.println(signstr);
								JSONObject jsonObject = new JSONObject();
								jsonObject.put("merchantid", "M" + pmsBusinessPos.getBusinessnum());
								jsonObject.put("data", encryptdata);
								jsonObject.put("timestamp", timestamp);
								jsonObject.put("sign", signstr);
								String postdata = "merchantid=" + "M" + pmsBusinessPos.getBusinessnum() + "&data="
										+ encryptdata + "&timestamp=" + timestamp + "&sign=" + signstr;
								String openApiUrl = "";
								openApiUrl = HMUtil.url + "/pay/unionpay/quick/sms";
								String results = HttpsUtil.doSslPost(openApiUrl, postdata, "utf-8");
								logger.info("恒明返回参数：" + results);

								JSONObject responseObj = JSONObject.parseObject(results);
								logger.info("message:" + responseObj.get("message"));
								retMap.put("v_mid", originalinfo.getV_mid());
								retMap.put("v_txnAmt", origial.getOrderAmount());
								retMap.put("v_time", originalinfo.getV_time());
								retMap.put("v_oid", originalinfo.getV_oid());
								if ("0".equals(responseObj.get("ret").toString())) {
									String dedata = AesEncryption.Desencrypt(responseObj.get("data").toString(),
											HMUtil.aeskey, HMUtil.aeskey);
									logger.info("恒明解析参数：" + dedata);
									JSONObject jsonObject2 = JSONObject.parseObject(dedata);

									logger.info("jsonObject2" + jsonObject2);
									if ("0".equals(jsonObject2.getString("orderstate"))) {
										retMap.put("v_code", "00");
										retMap.put("v_msg", "请求成功");
									} else {
										retMap.put("v_code", "15");
										retMap.put("v_msg", "请求失败");
										return retMap;
									}
								}
								break;
							case "000000003":// 易生快捷
								if (sumcode.equals(originalinfo.getV_smsCode())) {

									String channelKey = YSUtil.channelKey;
									String channel_sign_method = "SHA256";
									Map<String, Object> reqMap = new TreeMap<String, Object>();
									reqMap.put("sp_id", YSUtil.sp_id);// 服务商号
									reqMap.put("mch_id", YSUtil.merId4);// 商户号
									reqMap.put("out_trade_no", originalinfo.getV_oid());
									reqMap.put("id_type", "01");
									reqMap.put("acc_name", origial.getRealName());// 持卡人姓名
									reqMap.put("acc_type", "PERSONNEL");// PERSONNEL：对私 CORPORATE：对公
									reqMap.put("bank_code", origial.getSettlePmsBankNo());// 联行号
									reqMap.put("acc_no", origial.getSettleCardNo());// 卡号
									reqMap.put("acc_province", bank.getBank_province());// 省
									reqMap.put("acc_city", bank.getBank_city());// 市
									reqMap.put("mobile", origial.getPhone());// 手机号
									reqMap.put("id_no", origial.getCertNo());// 证件号
									reqMap.put("settle_rate", origial.getUserFee());// 结算费率
									reqMap.put("extra_rate", origial.getSettleUserFee());// T0手续费
									Date t = new Date();
									java.util.Calendar cal = java.util.Calendar.getInstance();
									cal.setTime(t);
									long sys_timestamp = cal.getTimeInMillis();
									reqMap.put("timestamp", sys_timestamp);// 时间戳

									StringBuilder sb = new StringBuilder();
									Set<String> keySet = reqMap.keySet();
									Iterator<String> iter = keySet.iterator();
									while (iter.hasNext()) {
										String key = iter.next();
										sb.append(key);
										sb.append("=");
										sb.append(reqMap.get(key));
										sb.append("&");
									}
									String sign = SwpHashUtil.getSign(sb.toString() + "key=" + channelKey, channelKey,
											channel_sign_method);
									reqMap.put("sign", sign);

									sb.append("sign");
									sb.append("=");
									sb.append(sign);
									System.out.println(sb.toString());
									String url = YSUtil.url + "/swp/ybbh/b2_register.do";
									HttpResponse httpResponse = HttpUtils.doPost(url, "", sb.toString(),
											"application/x-www-form-urlencoded; charset=UTF-8");
									String resp = EntityUtils.toString(httpResponse.getEntity());
									System.out.println("接受请求:" + resp);
									JSONObject json = JSONObject.parseObject(resp);

									if ("SUCCESS".equals(json.getString("status"))) {
										if ("SUCCESS".equals(json.getString("trade_state"))) {
											PmsWeixinMerchartInfo merchartInfo = new PmsWeixinMerchartInfo();
											merchartInfo.setAccount(json.getString("swpaccid"));// 账号
											merchartInfo.setMerchartId(originalinfo.getV_mid());
											merchartInfo.setMerchartName(merchantList.get(0).getMercName());
											merchartInfo.setMerchartNameSort(merchantList.get(0).getShortname());
											merchartInfo.setCertNo(origial.getCertNo());// 证件号
											merchartInfo.setCardNo(origial.getBankNo());// 卡号
											merchartInfo.setRealName(origial.getRealName());// 姓名
											merchartInfo.setMobile(origial.getPhone());// 手机号
											// merchartInfo.setAccountType(payRequest.getBusinessType());//账户类型
											merchartInfo.setBankName(bank.getBank_name());// 开户行
											merchartInfo.setPmsBankNo(origial.getBankId());// 联行号
											merchartInfo.setProvince(bank.getBank_province());// 省份
											merchartInfo.setCity(bank.getBank_city());// 城市
											merchartInfo.setDebitRate(origial.getUserFee());// 借记卡费率
											// merchartInfo.setWithdrawDepositSingleFee(payRequest.getWithdrawDepositSingleFee());//提现单笔手续费
											merchartInfo.setoAgentNo("100333");
											merchartInfo.setRateCode(originalinfo.getV_mid());
											int i = weixinService.updateRegister(merchartInfo);
											logger.info("易生修改状态:" + i);
											if (i > 0) {
												channelKey = YSUtil.channelKey;
												channel_sign_method = "SHA256";
												Map<String, Object> reqMap1 = new TreeMap<String, Object>();
												reqMap1.put("sp_id", YSUtil.sp_id);
												reqMap1.put("mch_id", "BJ" + pmsBusinessPos.getBusinessnum());// 商户号YSUtil.merId3
												reqMap1.put("out_trade_no", originalinfo.getV_oid());
												reqMap1.put("swpaccid", json.getString("swpaccid"));
												Double amount = Double.parseDouble(origial.getOrderAmount()) * 100;
												Integer amount1 = amount.intValue();
												logger.info("下游上送的金额:" + amount1);
												reqMap1.put("total_fee", amount1.toString());
												reqMap1.put("body", origial.getProcdutName());
												reqMap1.put("acc_type", "CREDIT");
												reqMap1.put("acc_name", origial.getRealName());
												reqMap1.put("acc_no", origial.getBankNo());
												reqMap1.put("mobile", origial.getPhone());
												reqMap1.put("bank_code", origial.getSettlePmsBankNo());
												reqMap1.put("id_type", "01");
												reqMap1.put("id_no", origial.getCertNo());
												reqMap1.put("front_notify_url", YSUtil.returnUrl);
												reqMap1.put("back_notify_url", YSUtil.notifyUrl);
												t = new Date();
												cal = java.util.Calendar.getInstance();
												cal.setTime(t);
												sys_timestamp = cal.getTimeInMillis();
												reqMap1.put("timestamp", sys_timestamp);

												StringBuilder sb1 = new StringBuilder();
												keySet = reqMap1.keySet();
												iter = keySet.iterator();
												while (iter.hasNext()) {
													String key = iter.next();
													sb1.append(key);
													sb1.append("=");
													sb1.append(reqMap1.get(key));
													sb1.append("&");
												}
												sign = SwpHashUtil.getSign(sb1.toString() + "key=" + channelKey,
														channelKey, channel_sign_method);
												reqMap1.put("sign", sign);

												sb1.append("sign");
												sb1.append("=");
												sb1.append(sign);
												System.out.println(sb1.toString());
												url = YSUtil.url + "/swp/ybbh/b2_preorder.do";
												httpResponse = HttpUtils.doPost(url, "", sb1.toString(),
														"application/x-www-form-urlencoded; charset=UTF-8");
												resp = EntityUtils.toString(httpResponse.getEntity());
												logger.info("接受请求:" + resp);
												json = JSONObject.parseObject(resp);

												if ("SUCCESS".equals(json.getString("status"))) {
													retMap.put("v_code", "00");
													retMap.put("v_msg", "请求成功");
													if ("SUCCESS".equals(json.getString("trade_state"))) {

														retMap.put("html", json.getString("page_content"));
														retMap.put("orderId", json.getString("sys_trade_no"));
													} else {
														retMap.put("v_code", "15");
														retMap.put("v_msg", "请求失败");
														return retMap;
													}
												} else {
													retMap.put("v_code", "15");
													retMap.put("v_msg", "请求失败");
													return retMap;
												}
											}
										} else {
											retMap.put("v_code", "15");
											retMap.put("v_msg", "请求失败");
											return retMap;
										}
									} else {
										retMap.put("v_code", "15");
										retMap.put("v_msg", "请求失败");
										return retMap;
									}
								} else {
									retMap.put("v_code", "19");
									retMap.put("v_msg", "短信验证不正确");
									return retMap;
								}
								break;
							case "10000466938":// 易宝快捷
								if (sumcode.equals(originalinfo.getV_smsCode())) {
									String orderId = originalinfo.getV_oid();
									String orderAmount = origial.getOrderAmount();
									String timeoutExpress = "";
									String requestDate = UtilDate.getDateFormatter();
									// String redirectUrl =
									// "http://60.28.24.164:8102/app_posp/quickPayAction/ybReturnUrl.action";
									// String notifyUrl =
									// "http://60.28.24.164:8102/app_posp/quickPayAction/ybNotifyUrl.action";
									// String redirectUrl = "http://www.lssc888.com/shop/control/yibao_return.php";
									// String notifyUrl = "http://www.lssc888.com/shop/control/yibao_notify.php";
									String redirectUrl = "http://www.lssc888.com/shop/control/yibao_return_vt.php";
									String notifyUrl = "http://www.lssc888.com/shop/control/yibao_notify_vt.php";
									String goodsName = origial.getProcdutName();
									String goodsDesc = origial.getProcdutName();
									String paymentParamExt = "";
									String bizSource = "";
									String bizEntity = "";
									String memo = "";
									String riskParamExt = "";
									String csUrl = "";

									String goodsParamExt = "{\"goodsName\":\"" + goodsName + "\",\"goodsDesc\":\""
											+ goodsDesc + "\"}";
									String industryParamExt = "{\"bizSource\":\"" + bizSource + "\",\"bizEntity\":\""
											+ bizEntity + "\"}";

									logger.info("goodsParamExt:" + goodsParamExt);
									Map<String, String> params = new HashMap<>();
									params.put("orderId", orderId);
									params.put("orderAmount", orderAmount);
									params.put("timeoutExpress", "");
									params.put("requestDate", requestDate);
									params.put("redirectUrl", redirectUrl);
									params.put("notifyUrl", notifyUrl);
									params.put("goodsParamExt", goodsParamExt);
									params.put("paymentParamExt", paymentParamExt);
									params.put("industryParamExt", industryParamExt);
									params.put("memo", memo);
									params.put("riskParamExt", riskParamExt);
									params.put("csUrl", csUrl);

									logger.info("token上送的数据:" + params);
									String uri = "/rest/v1.0/std/trade/order";
									Map<String, String> result = YeepayService.requestYOP(params, uri, TRADEORDER);
									logger.info("上游返回的数据:" + result);
									if ("OPR00000".equals(result.get("code"))) {
										String token = result.get("token");
										logger.info("获取易宝返回的token：" + token);
										String parentMerchantNo = "10018465070";
										String merchantNo = "10018465070";
										// String token = request.getParameter("token");
										timestamp = String.valueOf(Math.round(new Date().getTime() / 1000));
										String directPayType = "YJZF";
										String cardType = "";
										if ("1".equals(origial.getBankType())) {
											cardType = "DEBIT";
										}
										if ("2".equals(origial.getBankType())) {
											cardType = "CREDIT";
										}

										String userNo = UtilDate.getOrderNum();
										String userType = "MAC";
										String appId = "";
										String openId = "";
										String clientId = "";

										String ext = "";

										params = new HashMap<String, String>();
										params.put("parentMerchantNo", parentMerchantNo);
										params.put("merchantNo", merchantNo);
										params.put("token", token);
										params.put("timestamp", timestamp);
										params.put("directPayType", directPayType);
										params.put("cardType", cardType);
										params.put("userNo", userNo);
										params.put("userType", userType);
										params.put("ext", ext);
										String url = YeepayService.getUrl(params);
										logger.info("向上游发送的数据:" + url);
										retMap.put("path", url);
										retMap.put("v_code", "00");
										retMap.put("v_msg", "请求成功");
									} else {
										retMap.put("v_code", "15");
										retMap.put("v_msg", "请求失败");
										return retMap;
									}
								} else {
									retMap.put("v_code", "19");
									retMap.put("v_msg", "短信验证不正确");
									return retMap;
								}
								break;
							case "000000001":// 易生快捷
								String channelKey = YSUtil.channelKey;
								String channel_sign_method = "SHA256";
								Map<String, Object> reqMap = new TreeMap<String, Object>();
								reqMap.put("sp_id", YSUtil.sp_id);// 服务商号
								reqMap.put("mch_id", YSUtil.merId1);// 商户号
								reqMap.put("sys_trade_no", origial.getMerchantOrderId());
								reqMap.put("password", originalinfo.getV_smsCode());
								reqMap.put("notifyurl", YSUtil.notifyUrl);
								Date t = new Date();
								java.util.Calendar cal = java.util.Calendar.getInstance();
								cal.setTime(t);
								long sys_timestamp = cal.getTimeInMillis();
								reqMap.put("timestamp", sys_timestamp);// 时间戳

								StringBuilder sb = new StringBuilder();
								Set<String> keySet = reqMap.keySet();
								Iterator<String> iter = keySet.iterator();
								while (iter.hasNext()) {
									String key = iter.next();
									sb.append(key);
									sb.append("=");
									sb.append(reqMap.get(key));
									sb.append("&");
								}
								String sign = SwpHashUtil.getSign(sb.toString() + "key=" + channelKey, channelKey,
										channel_sign_method);
								reqMap.put("sign", sign);

								sb.append("sign");
								sb.append("=");
								sb.append(sign);
								System.out.println(sb.toString());
								String url = YSUtil.url + "/swp/up/submit.do";
								HttpResponse httpResponse = HttpUtils.doPost(url, "", sb.toString(),
										"application/x-www-form-urlencoded; charset=UTF-8");
								String resp = EntityUtils.toString(httpResponse.getEntity());
								System.out.println("接受请求:" + resp);
								JSONObject json = JSONObject.parseObject(resp);
								retMap.put("v_mid", originalinfo.getV_mid());
								retMap.put("v_txnAmt", origial.getOrderAmount());
								retMap.put("v_time", originalinfo.getV_time());
								retMap.put("v_oid", originalinfo.getV_oid());
								if ("SUCCESS".equals(json.getString("status"))) {
									if ("SUCCESS".equals(json.getString("trade_state"))) {
										retMap.put("v_code", "00");
										retMap.put("v_msg", "请求成功");
									} else if ("PROCESSING".equals(json.getString("trade_state"))) {
										retMap.put("v_code", "200");
										retMap.put("v_msg", "初始化状态");
									} else {
										retMap.put("v_code", "15");
										retMap.put("v_msg", "请求失败");
										return retMap;
									}
								} else {
									retMap.put("v_code", "15");
									retMap.put("v_msg", "请求失败");
									return retMap;
								}
								break;
							case "1711030001":// 沈阳银盛
								logger.info("###############进入沈阳银盛支付接口##################");
								OriginalOrderInfo orig1 = new OriginalOrderInfo();
								orig1.setMerchantOrderId(originalinfo.getV_oid());
								orig1.setPid(originalinfo.getV_mid());
								OriginalOrderInfo original = originalDao.selectByOriginal(orig1);
								logger.info("原始订单信息app_id:" + original.getByUser());
								logger.info("原始订单信息token:" + original.getSumCode());
								Map<String, String> extraparam = new HashMap<String, String>();

								extraparam.put("order_num", originalinfo.getV_oid()); // 订单号
								extraparam.put("app_id", original.getByUser()); // app_id
								extraparam.put("token", original.getSumCode()); // token
								extraparam.put("sms_code", originalinfo.getV_smsCode()); // 验证码
								extraparam.put("notify_url",
										BaseUtil.url+"/quickPayAction/syNotifyUrl.action"); // 回调地址

								String key = "0295a406899f4c3783ef4e22eef5ae9f";// md5key
								// 得到带签名数据
								Map<String, ?> filterMap = PayCore.paraFilter(extraparam);

								String linkStr = PayCore.createLinkString(filterMap);
								logger.info("沈阳银盛签名公钥" + key);
								logger.info("沈阳银盛待签数据" + linkStr);
								String hexSign = "";
								try {
									hexSign = PayCore.md5Sign(linkStr, key);
								} catch (Exception e) {
									e.printStackTrace();
								}
								logger.info("沈阳银盛签名数据:" + hexSign);
								extraparam.put("sign_type", "MD5"); // md5签名
								extraparam.put("sign_info", hexSign);

								String requestStr = JSON.toJSONString(extraparam);
								logger.info(requestStr);
								url = "http://pay.unvpay.com/services/fastpay/submitSms";

								String respStr = HttpClientUtil.post(url, "UTF-8", requestStr);
								logger.info("沈阳银盛响应信息:" + respStr);
								json = JSONObject.parseObject(respStr);
								if ("0000".equals(json.getString("ret_code"))) {
									retMap.put("v_mid", originalinfo.getV_mid());
									retMap.put("v_txnAmt", origial.getOrderAmount());
									retMap.put("v_time", originalinfo.getV_time());
									retMap.put("v_oid", originalinfo.getV_oid());
									retMap.put("v_code", "00");
									retMap.put("v_msg", "请求成功");
								} else {
									retMap.put("v_code", "15");
									retMap.put("v_msg", "请求失败");
									return retMap;
								}
								break;
							case "888101700005315"://汇聚快捷

								OriginalOrderInfo originalInfo = null;
								originalInfo = this.payService.getOriginOrderInfo(originalinfo.getV_oid());
								Map<String, String> result = new HashMap<>();
								StringBuilder strs = new StringBuilder();
								strs.append(HJUtil.Version2);
								strs.append(pmsBusinessPos.getBusinessnum());// pmsBusinessPos.getBusinessnum()
								strs.append(originalInfo.getProcdutName());
								strs.append(originalinfo.getV_oid());
								strs.append(Double.parseDouble(originalInfo.getOrderAmount()) / 100);
								strs.append(originalInfo.getRealName());
								strs.append("1");
								strs.append(originalInfo.getCertNo());
								strs.append(originalInfo.getBankNo());
								strs.append(originalInfo.getExpired() == null ? "" : originalInfo.getExpired());// YYYY-MM
								strs.append(originalInfo.getCvn2() == null ? "" : originalInfo.getCvn2());
								strs.append(originalInfo.getPhone());
								strs.append(originalinfo.getV_smsCode());
								strs.append(originalInfo.getAttach() == null ? "" : originalInfo.getAttach());

								logger.info("汇聚待签名数据:" + strs.toString());
								// String hmac =MD5Utils.sign(str.toString(), HJUtil.privateKey,
								// "UTF-8");//RSAUtils.sign(str.toString().getBytes("UTF-8"),
								// HJUtil.privateKey);
								String hmac = DigestUtils.md5Hex(strs.toString() + pmsBusinessPos.getKek());// pmsBusinessPos.getKek()
								result.put("p0_Version", HJUtil.Version);
								result.put("p1_MerchantNo", pmsBusinessPos.getBusinessnum());
								result.put("p2_MerchantName", originalInfo.getProcdutName());
								result.put("q1_OrderNo", originalinfo.getV_oid());
								result.put("q2_Amount", Double.parseDouble(originalInfo.getOrderAmount()) / 100 + "");
								result.put("s1_PayerName", originalInfo.getRealName());
								result.put("s2_PayerCardType", "1");
								result.put("s3_PayerCardNo", originalInfo.getCertNo());
								result.put("s4_PayerBankCardNo", originalInfo.getBankNo());
								if (originalInfo.getExpired() != null && originalInfo.getExpired() != "") {
									result.put("s5_BankCardExpire", originalInfo.getExpired());
								}
								if (originalInfo.getCvn2() != null && originalInfo.getCvn2() != "") {
									result.put("s6_CVV2", originalInfo.getCvn2());
								}
								result.put("s7_BankMobile", originalInfo.getPhone());
								result.put("t2_SmsCode", originalinfo.getV_smsCode());
								if (originalInfo.getAttach() != null && originalInfo.getAttach() != "") {
									result.put("t1_ext", originalInfo.getAttach());
								}
								result.put("hmac", URLEncoder.encode(hmac, "utf-8"));
								TreeMap<String, String> paramsMap = new TreeMap<>();
								paramsMap.putAll(result);
								String paramSrc = RequestUtils.getParamSrc(paramsMap);
								logger.info("汇聚快捷支付给上游发送的数据:" + paramSrc);
								// String html = RequestUtils.sendPost(HJUtil.scanCodePay, paramSrc);
								HttpService HT = new HttpService();
								String retuString = HT.POSTReturnString(HJUtil.agreementPay, paramsMap, MBUtil.codeG);
								logger.info("汇聚返回字符串参数：" + retuString);
								net.sf.json.JSONObject jsons = net.sf.json.JSONObject.fromObject(retuString);
								if ("100".equals(jsons.getString("ra_Status"))) {
									retMap.put("v_code", "00");
									retMap.put("v_msg", jsons.getString("rb_Msg"));
									retMap.put("v_oid", originalinfo.getV_oid());

								} else {
									retMap.put("v_code", "01");
									retMap.put("v_msg", jsons.getString("rb_Msg"));
								}

								break;
							case "888888888888888":// 聚佰宝快捷
								logger.info("###############进入聚佰宝支付接口##################");
								OriginalOrderInfo origs = new OriginalOrderInfo();
								origs.setMerchantOrderId(originalinfo.getV_oid());
								origs.setPid(originalinfo.getV_mid());
								OriginalOrderInfo originals = originalDao.selectByOriginal(origs);
								logger.info("聚佰宝短信原订单数据:" + JSON.toJSON(originals));
								String version="1.0.0";
								
								String transCode="8888";
				
								// 商户编号
								String merchantId = "888201711290115";
								// 订单信息
								String merOrderNum = originalinfo.getV_oid();
								// 业务代码
								String bussId = "ONL0003";
					            Integer amount=(int) (Double.parseDouble(originals.getOrderAmount())*100);
								String tranAmt = amount.toString();
								
								String sysTraceNum=originalinfo.getV_oid();
								
								String tranDateTime=originalinfo.getV_time();
								
								String currencyType="156";
								
								String merURL=BaseUtil.url+"/quickPayAction/payReturnUrl.action";
								
								String backURL=BaseUtil.url+"/quickPayAction/payNotifyUrl.action";
								
								String orderInfo=originalinfo.getV_time();
								
								String userId=UtilDate.getDateTime();
								
								String messageCode=originalinfo.getV_smsCode();
								
								String quickTransId=originals.getByUser();
								
								String protocolNo=originals.getBankId();
								
								String userIp="";										
								
								String bankId="888880170122900";
								
								String stlmId="";
								
								String entryType="1";
								
								String attach=originals.getAttach();
								
								String reserver1="";
										
								String reserver2="";
								
								String reserver3="";
								
								String reserver4="7";
								// 签名数据
								String txnString = version + "|" + transCode + "|" + merchantId
										+ "|" + merOrderNum + "|" + bussId + "|" + tranAmt + "|" + sysTraceNum
										+ "|" + tranDateTime + "|" + currencyType +"|" + merURL + "|" + backURL
										+ "|" + orderInfo + "|" + userId;
								logger.info("聚佰宝上送的数据加密字符串:" + txnString);
								MD5 md = new MD5();
								String signValue = md.getMD5ofStr(txnString + "675FC1ctf2Y6zVm3");

								String txn = "version=" + version + "&transCode=" + transCode + "&merchantId=" + merchantId
										+ "&merOrderNum=" + merOrderNum + "&bussId=" + bussId + "&tranAmt=" + tranAmt + "&sysTraceNum=" + sysTraceNum
										+ "&tranDateTime=" + tranDateTime + "&currencyType=" + currencyType +"&merURL=" + merURL + "&backURL=" + backURL
										+ "&orderInfo=" + orderInfo + "&userId=" + userId + "&messageCode=" + messageCode +"&quickTransId=" + quickTransId +"&protocolNo=" + protocolNo + "&userIp=" + userIp
										+ "&bankId=" + bankId + "&stlmId=" + stlmId + "&entryType=" + entryType+"&attach=" + attach
										+ "&reserver1=" + reserver1+ "&reserver2=" + reserver2 +"&reserver3=" + reserver3+"&reserver4=" + reserver4+ "&signValue=" + signValue;

								logger.info("聚佰宝上送的数据:" + txn);

								url = "http://58.56.23.89:7006/NetPay/quickPayDirect.action?" + txn;

								HttpURLConection http = new HttpURLConection();

								String resonpe = http.httpURLConnectionPOST("https://cashier.etonepay.com/NetPay/quickPayDirect.action", txn);
								logger.info("响应结果:" + resonpe);
								retMap.put("path", resonpe);
								retMap.put("v_code", "00");
								retMap.put("v_msg", "请求成功");
//								net.sf.json.JSONObject ob = net.sf.json.JSONObject.fromObject(resonpe);
//								Iterator it = ob.keys();
//								String respCode = "";
//								String html = "";
//								while (it.hasNext()) {
//									key = (String) it.next();
//									if (key.equals("respCode")) {
//
//										respCode = ob.getString(key);
//
//										logger.info("聚佰宝支付响应状态码:" + respCode);
//
//									}
//								}
//								if ("0000".equals(respCode)) {
//									retMap.put("v_mid", originalinfo.getV_mid());
//									retMap.put("v_txnAmt", origial.getOrderAmount());
//									retMap.put("v_time", originalinfo.getV_time());
//									retMap.put("v_oid", originalinfo.getV_oid());
//									retMap.put("v_code", "00");
//									retMap.put("v_msg", "请求成功");
//								}else {
//									retMap.put("v_code", "15");
//									retMap.put("v_msg", "请求失败");
//									return retMap;
//								}
								
								break;
							case "000001110100000812":// 裕福快捷
							case "000001220100000470":
							case "000001110100000663":
								final String merCertPath = new File(this.getClass().getResource("/").getPath())
										.getParentFile().getParentFile().getCanonicalPath() + "//ky//"
										+ pmsBusinessPos.getBusinessnum() + ".cer";
								final String pfxPath = new File(this.getClass().getResource("/").getPath())
										.getParentFile().getParentFile().getCanonicalPath() + "//ky//"
										+ pmsBusinessPos.getBusinessnum() + ".pfx";
								final String pfxPwd = pmsBusinessPos.getKek();
								PayReq req = new PayReq();
								OriginalOrderInfo originalInfo1 = null;
								try {
									originalInfo1 = this.payService.getoriginInfoByMerchantOrderId(originalinfo.getV_oid());
									List<Map<String, String>> list = new ArrayList<>();
									Map<String, String> reqMaps = new HashMap<>();
									req.setVersion("1.0.0");
									req.setMerchantId(pmsBusinessPos.getBusinessnum());
									req.setToken(originalInfo1.getSumCode());
									req.setSmsCode(originalinfo.getV_smsCode());
									YufuCipher cipher = null;
									YufuCipherSupport instance = null;
									cipher = YufuCipherSupport.getCipherInstance( merCertPath, pfxPath, pfxPwd,cipher,instance);
									//YufuCipher cipher = YufuCipherSupport.getCipherInstance(merCertPath, pfxPath,pfxPwd);

									String data = GsonUtil.objToJson(req);
									logger.info("data:" + data);
									Map<String, String> params = GsonUtil.jsonToObj(data, Map.class);
									ParamPacket bo = cipher.doPack(params);
									logger.info("11!:" + JSON.toJSON(bo));
									TreeMap<String, String> map_param = new TreeMap<>();
									map_param.put("merchantId", pmsBusinessPos.getBusinessnum());
									map_param.put("data", URLEncoder.encode(bo.getData(), "utf-8"));
									map_param.put("enc", URLEncoder.encode(bo.getEnc(), "utf-8"));
									map_param.put("sign", URLEncoder.encode(bo.getSign(), "utf-8"));
									String urlPay = "";
									if ("000001110100000812".equals(pmsBusinessPos.getBusinessnum())) {
										urlPay = "http://malltest.yfpayment.com/payment/service/pay.do";
									} else {
										urlPay = "http://www.yfpayment.com/payment/service/pay.do";
									}
									String returnStr = PostUtils.doPost(urlPay, map_param);

									if (returnStr != null && !"".equals(returnStr)) {
										// 二、验签解密
										returnStr = URLDecoder.decode(returnStr, "utf-8");
										System.out.println("URL解码后的置单应答结果：" + returnStr);
										TreeMap<String, String> boMap = JSON.parseObject(returnStr,
												new TypeReference<TreeMap<String, String>>() {
												});
										Map<String, String> payshowParams = cipher.unPack(new ParamPacket(
												boMap.get("data"), boMap.get("enc"), boMap.get("sign")));
										System.out.println("解密后的置单应答结果：" + payshowParams);
										// {merchantDisctAmt=0, respDesc=调用接口成功, transTime=20180408105626,
										// bpSerialNum=1001804081056248801, merchantId=000001110100000812,
										// merchantOrderTime=20180408103938, merchantOrderAmt=100, currency=156,
										// merchantOrderId=QP2018040810393862321312, version=1.0.0, respCode=0000,
										// transStatus=01}
										ConsumeResponseEntity consumeResponseEntity = new ConsumeResponseEntity();
										if ("0000".equals(payshowParams.get("respCode"))) {
											retMap.put("v_code", "00");
											retMap.put("v_msg", "请求成功");
											consumeResponseEntity.setV_mid(originalinfo.getV_mid());
											consumeResponseEntity.setV_oid(originalinfo.getV_oid());
											consumeResponseEntity.setV_msg("支付成功");
											if ("01".equals(payshowParams.get("transStatus"))) {
												consumeResponseEntity.setV_status("0000");
											} else if ("02".equals(payshowParams.get("transStatus"))
													|| "05".equals(payshowParams.get("transStatus"))) {
												consumeResponseEntity.setV_status("1001");
											}
											otherInvoke(consumeResponseEntity);
										} else {
											retMap.put("v_code", "01");
											retMap.put("v_msg", payshowParams.get("respDesc"));
										}
									} else {
										System.out.println("置单返回报文为空！");
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								break;
							case "10044":// 柜银云快捷
								logger.info("###############进入柜银云支付接口##################");
								OriginalOrderInfo origina = new OriginalOrderInfo();
								origina.setMerchantOrderId(originalinfo.getV_oid());
								origina.setPid(originalinfo.getV_mid());
								originals = originalDao.selectByOriginal(origina);
								logger.info("柜银云原始商户号:" + originals.getByUser());
								logger.info("柜银云原始商户密钥:" + originals.getSumCode());
								logger.info("柜银云原始商户交易标识:" + originals.getBankId());
								Map<String, Object> infoMap = new HashMap<String, Object>();
								infoMap.put("agentNo", "10033");// 机构号
								infoMap.put("merNo", originals.getByUser());// 商户号
								infoMap.put("service", "ncldd0");// 通道
								infoMap.put("lservice", "pay");// 接口
								infoMap.put("amount", originals.getOrderAmount());// 支付金额
								infoMap.put("smsCode", originalinfo.getV_smsCode());// 验证码
								infoMap.put("cvn2", originals.getCvn2());// 卡后三位
								String expired = originals.getExpired();
								char[] cc = expired.toCharArray();
								StringBuffer bb = new StringBuffer();
								bb.append(cc[2]);
								bb.append(cc[3]);
								bb.append(cc[0]);
								bb.append(cc[1]);
								logger.info("下游上传的信用卡有效期：" + bb);
								infoMap.put("useTime", bb);// 有效期
								infoMap.put("out_trade_no", originalinfo.getV_oid());// 交易流水
								infoMap.put("orderTime", originals.getOrderTime());// 交易时间
								infoMap.put("checkFlag", originals.getBankId());// 交易标记

								String params = ApiUtil.sortMap(infoMap);
								sign = xdt.quickpay.gyy.util.MD5.getSign(infoMap, originals.getSumCode());
								params += "&sign=" + sign;
								logger.info("柜银云支付提交信息：" + params);
								String reStr = ApiUtil.sendPost("http://139.224.27.56/ygww/sys/api/outer/geteway.do",
										params);
								logger.info("柜银云快捷支付返回结果：" + reStr);
								Map map = ApiUtil.toMap(reStr);
								String respCodes = map.get("respCode").toString();
								logger.info("柜银云开通快捷状态码：" + respCodes);
								if ("0000".equals(respCodes)) {
									retMap.put("v_mid", originalinfo.getV_mid());
									retMap.put("v_txnAmt", origial.getOrderAmount());
									retMap.put("v_time", originalinfo.getV_time());
									retMap.put("v_oid", originalinfo.getV_oid());
									retMap.put("v_code", "00");
									retMap.put("v_msg", "请求成功");
									ConsumeResponseEntity conmon = new ConsumeResponseEntity();
									conmon.setV_mid(originalinfo.getV_mid());
									conmon.setV_oid(originalinfo.getV_oid());
									conmon.setV_status("0000");
									conmon.setV_attach(originals.getAttach());
									conmon.setV_msg("支付成功");
									otherInvoke(conmon);
								} else {
									retMap.put("v_code", "15");
									retMap.put("v_msg", "请求失败");
									ConsumeResponseEntity conmon = new ConsumeResponseEntity();
									conmon.setV_mid(originalinfo.getV_mid());
									conmon.setV_oid(originalinfo.getV_oid());
									conmon.setV_status("1001");
									conmon.setV_attach(originals.getAttach());
									conmon.setV_msg("支付失败");
									otherInvoke(conmon);
								}

								break;
							case "20180413085019363857"://上海漪雷快捷
							logger.info("###########上海漪雷快捷支付##########");
							 break;
							case "1120180427134034001"://银生宝快捷
								logger.info("###########银生宝快捷支付##########");
								if (sumcode.equals(originalinfo.getV_smsCode())) {
									OriginalOrderInfo originas = new OriginalOrderInfo();
									originas.setMerchantOrderId(originalinfo.getV_oid());
									originas.setPid(originalinfo.getV_mid());
									originals = originalDao.selectByOriginal(originas);
									logger.info("原始订单信息:" + originals.getOrderAmount());
									Map<String, Object> infoMaps = new HashMap<String, Object>();
									String accountId="1120180427134034001";
									String customerId=UtilDate.getOrderNum();
									String orderNo=originalinfo.getV_oid();
									String commodityName=originals.getProcdutName();
									String amounts=originals.getOrderAmount();
									String responseUrl=BaseUtil.url+"/quickPayAction/syysNotifyUrl.action";
									String pageResponseUrl=BaseUtil.url+"/app_posp/quickPayAction/syysNotifyUrl.action";
									infoMaps.put("accountId", accountId);// 机构号
									infoMaps.put("customerId", customerId);// 商户号
									infoMaps.put("orderNo", orderNo);// 通道
									infoMaps.put("commodityName", commodityName);// 接口
									infoMaps.put("amount", amounts);// 支付金额
									infoMaps.put("responseUrl", responseUrl);// 验证码
									infoMaps.put("pageResponseUrl", pageResponseUrl);// 卡后三位									
									String keys="123456abc";
									
									String strss="accountId="+accountId+"&customerId="+customerId+"&orderNo="+orderNo+"&commodityName="+commodityName+"&amount="+amounts+"&responseUrl="+responseUrl+"&pageResponseUrl="+pageResponseUrl+"&key=123456abc";
									logger.info("银生宝生成签名前的数据:"+strss);
									String signs=MD5Util.MD5Encode(strss).toUpperCase();
									logger.info("银生宝生成的签名:"+signs);
									retMap.put("accountId", accountId);// 机构号
									retMap.put("customerId", customerId);// 商户号
									retMap.put("orderNo", orderNo);// 通道
									retMap.put("commodityName", commodityName);// 接口
									retMap.put("amount", amounts);// 支付金额
									retMap.put("responseUrl", responseUrl);// 验证码
									retMap.put("pageResponseUrl", pageResponseUrl);// 卡后三位
									retMap.put("mac", signs);
									retMap.put("v_code", "00");
								} else {
									retMap.put("v_code", "19");
									retMap.put("v_msg", "短信验证不正确");
									return retMap;
								}
								break;
							default:							
								break;
							}
						} else {
							// 请求参数为空
							logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
							retMap.put("v_code", "16");
							retMap.put("v_msg", "商户没有进行实名认证");
							return retMap;
						}
					} else {
						logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
						retMap.put("v_code", "16");
						retMap.put("v_msg", "商户没有进行实名认证");
						return retMap;
					}
				} else {

					logger.info("交易类型与原订单类型不一致!");
					return setResp("18", "交易类型与原订单类型不一致");
				}
			} else {
				logger.info("原订单不存在!");
				return setResp("17", "原订单不存在");
			}

		} else

		{
			logger.info("上送交易参数空!");
			return setResp("01", "上送交易参数空");
		}

		return retMap;
	}

	public void otherInvoke(ConsumeResponseEntity result) throws Exception {
		// TODO Auto-generated method stub

		logger.info("上游返回的数据" + result);
		// 流水表transOrderId
		String transOrderId = result.getV_oid();
		// 流水信息
		PospTransInfo pospTransInfo = pospTransInfoDAO.searchBycjtOrderId(transOrderId);
		logger.info("流水表信息" + pospTransInfo);
		// 订单信息
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
		logger.info("订单表信息" + pmsAppTransInfo);
		// 查询结果成功
		if ("0000".equals(result.getV_status().toString())) {
			// 支付成功
			pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(result.getV_msg().toString());
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// log.info("修改余额");
				// 修改余额
				logger.info(pmsAppTransInfo);
				// updateMerchantBanlance(pmsAppTransInfo);
				// 更新流水表
				pospTransInfo.setResponsecode("00");
				pospTransInfo.setPospsn(result.getV_oid());
				logger.info("更新流水");
				logger.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if ("1001".equals(result.getV_status().toString())) {
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(result.getV_msg().toString());
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("02");
				pospTransInfo.setPospsn(result.getV_oid());
				logger.info("更新流水");
				logger.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		}

	}

	public Map<String, String> quickQuery(QueryRequestEntity query) {

		Map<String, String> result = new HashMap<>();
		OriginalOrderInfo origin = new OriginalOrderInfo();
		String orderid = query.getV_oid();
		logger.info("快捷查询订单号:" + orderid);
		origin = originalDao.getOriginalOrderInfoByOrderid(orderid);
		// 查询商户路由
		PmsBusinessPos pmsBusinessPos = selectKey(query.getV_mid());
		PmsAppTransInfo pmsAppTransInfo = null;
		try {
			if (origin != null) {
				pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(origin.getOrderId());
				if (pmsAppTransInfo != null) {
					logger.info("pmsAppTransInfo：" + JSON.toJSON(pmsAppTransInfo));

					result.put("v_mid", query.getV_mid());// 商户号
					result.put("v_oid", query.getV_oid());// 订单号
					result.put("v_txnAmt", origin.getOrderAmount());// 金额
					result.put("v_attach", origin.getAttach());// 支付类型
					result.put("v_code", "00");
					result.put("v_msg", "请求成功");
					if ("0".equals(pmsAppTransInfo.getStatus())) {
						result.put("v_status_code", "0000");// 支付状态
						result.put("v_status_msg", "支付成功");
					} else if (("1".equals(pmsAppTransInfo.getStatus()))) {
						result.put("v_status_code", "1001");// 支付状态
						result.put("v_status_msg", "支付失败");
					} else {
						result.put("v_status_code", "200");// 支付状态
						result.put("v_status_msg", "初始化");
					}

				} else {
					result.put("v_code", "15");
					result.put("v_msg", "请求失败");
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 创新贷还预下单接口
	 */
	@Override
	public Map<String, String> loanStillPay(MessageRequestEntity originalinfo) throws Exception {

		Map<String, String> retMap = new HashMap<String, String>();
		// 商户号
		String merchId = originalinfo.getV_mid();
		// 金额
		String acount = originalinfo.getV_txnAmt();
		// 商户订单号
		logger.info("******************根据商户号查询");

		// 查询上游商户号
		PmsBusinessPos busInfo = selectKey(merchId);

		// 验证当前是否已经下单
		OriginalOrderInfo orig = new OriginalOrderInfo();
		orig.setMerchantOrderId(originalinfo.getV_oid());
		orig.setPid(originalinfo.getV_mid());

		if (originalDao.selectByOriginal(orig) != null) {
			logger.info("下单重复");
			return setResp("03", "下单重复");
		}

		// String orderNumber = UtilMethod.getOrderid("180");// 1、订单号由
		// 业务号（2位）+业务细分（1位）+时间戳（13位）
		// 总共16位
		/**
		 * 插入原始数据信息
		 */
		OriginalOrderInfo original = new OriginalOrderInfo();
		original.setMerchantOrderId(originalinfo.getV_oid());// 原始数据的订单编号
		original.setOrderId(originalinfo.getV_oid()); // 为主键
		original.setPid(originalinfo.getV_mid());
		original.setOrderTime(originalinfo.getV_time());
		original.setOrderAmount(originalinfo.getV_txnAmt());
		original.setProcdutName(originalinfo.getV_productDesc());
		original.setProcdutDesc(originalinfo.getV_productDesc());
		original.setPayType(originalinfo.getV_type());
		original.setPageUrl(originalinfo.getV_url());
		original.setBgUrl(originalinfo.getV_notify_url());
		original.setBankNo(originalinfo.getV_cardNo());
		original.setRealName(originalinfo.getV_realName());
		if (originalinfo.getV_cert_no() != null) {
			original.setCertNo(originalinfo.getV_cert_no());
		}
		if (originalinfo.getV_phone() != null) {
			original.setPhone(originalinfo.getV_phone());
		}
		if (originalinfo.getV_pmsBankNo() != null) {
			original.setBankId(originalinfo.getV_pmsBankNo());
		}
		if ("0".equals(originalinfo.getV_userFee()) && originalinfo.getV_userFee() != null) {
			original.setUserFee(originalinfo.getV_userFee());
		}

		if (originalinfo.getV_settlePmsBankNo() != null) {
			original.setSettlePmsBankNo(originalinfo.getV_settlePmsBankNo());
		}
		if (originalinfo.getV_settleCardNo() != null) {
			original.setSettleCardNo(originalinfo.getV_settleCardNo());
		}
		if (originalinfo.getV_settleUserFee() != null) {
			original.setSettleUserFee(originalinfo.getV_settleUserFee());
		}
		if (originalinfo.getV_settleName() != null) {
			original.setSettleUserName(originalinfo.getV_settleName());
		}
		if (originalinfo.getV_cvn2() != null) {
			original.setCvn2(originalinfo.getV_cvn2());
		}
		if (originalinfo.getV_expired() != null) {
			original.setExpired(originalinfo.getV_expired());
		}
		if (originalinfo.getV_attach() != null) {
			original.setAttach(originalinfo.getV_attach());
		}
		original.setBankType(originalinfo.getV_accountType());
		originalDao.insert(original);

		// 根据商户号查询
		String mercId = originalinfo.getV_mid();

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);

		// o单编号
		String oAgentNo = "";

		// 查询当前商户信息
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		if (!(merchantList.size() == 0 || merchantList.isEmpty())) {
			merchantinfo = merchantList.get(0);
			// merchantinfo.setCustomertype("3");

			oAgentNo = merchantinfo.getoAgentNo();

			if (StringUtils.isBlank(oAgentNo)) {
				// 如果没有欧单编号，直接返回错误
				logger.error("参数错误!");
				retMap.put("v_code", "04");
				retMap.put("v_msg", "参数错误,没有欧单编号");
				return retMap;
			}
			// 判断是否为正式商户
			if ("60".equals(merchantinfo.getMercSts())) {

				// 实际金额
				String factAmount = "" + new BigDecimal(originalinfo.getV_txnAmt()).multiply(new BigDecimal(100));
				// 校验欧单金额限制
				ResultInfo payCheckResult = iPublicTradeVerifyService
						.amountVerifyOagent((int) Double.parseDouble(factAmount), TradeTypeEnum.onlinePay, oAgentNo);
				if (!payCheckResult.getErrCode().equals("0")) {
					// 交易不支持
					logger.info("欧单金额限制，oAagentNo:" + oAgentNo + ",payType:"
							+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
					return setResp("05", "欧单金额限制，请重试或联系客服");
				}

				// 校验欧单模块是否开启
				ResultInfo resultInfoForOAgentNo = iPublicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.onlinePay,
						oAgentNo);
				if (!resultInfoForOAgentNo.getErrCode().equals("0")) {
					// 交易不支持
					if (StringUtils.isEmpty(resultInfoForOAgentNo.getMsg())) {
						logger.error("交易关闭，请重试或联系客服");
						return setResp("06", "交易关闭，请重试或联系客服");
					} else {
						return setResp("07", "系统异常，请重试或联系客服");
					}

				}
				// 校验商户模块是否开启
				ResultInfo payCheckResult3 = iPublicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.onlinePay, mercId);
				if (!payCheckResult3.getErrCode().equals("0")) {
					// 交易不支持
					logger.info("商户模块限制，oAagentNo:" + oAgentNo + ",payType:"
							+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
					return setResp("08", "商户模块限制,请重试或联系客服");
				}
				// 校验商户金额限制
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("mercid", merchantinfo.getMercId());// 商户编号
				paramMap.put("businesscode", TradeTypeEnum.onlinePay.getTypeCode());// 业务编号
				paramMap.put("oAgentNo", oAgentNo);
				// 商户 网购 业务信息
				Map<String, String> resultMap = merchantMineDao.queryBusinessInfo(paramMap);

				if (resultMap == null || resultMap.size() == 0) {
					// 若查到的是空值，直接返回错误
					logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
					return setResp("09", "没有查到相关费率配置,,请重试或联系客服");
				}

				String maxTransMoney = resultMap.get("MAX_AMOUNT"); // 每笔最大交易金额
				String minTransMoney = resultMap.get("MIN_AMOUNT"); // 每笔最小交易金额
				String paymentAmount = factAmount;// 交易金额

				if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(maxTransMoney)) == 1) {
					// 金额超过最大金额
					logger.info("交易金额大于最打金额");
					return setResp("10", "金额超过最大交易金额");
				} else if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(minTransMoney)) == -1) {
					// 金额小于最小金额
					logger.info("交易金额小于最小金额");
					return setResp("11", "交易金额小于最小金额");

				}

				// 组装订单数据
				PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
				// 写入欧单编号
				pmsAppTransInfo.setoAgentNo(oAgentNo);
				pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());// 订单初始化状态
				pmsAppTransInfo.setTradetype(TradeTypeEnum.creditCardRePay.getTypeName());// 业务功能模块名称
				// ：网购
				pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter()); // 设置时间
				pmsAppTransInfo.setMercid(merchantinfo.getMercId());
				pmsAppTransInfo.setTradetypecode(TradeTypeEnum.creditCardRePay.getTypeCode());// 业务功能模块编号
				// ：17
				pmsAppTransInfo.setOrderid(originalinfo.getV_oid());// 设置订单号
				pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.moBaoQuickPay.getTypeName());
				pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.moBaoQuickPay.getTypeCode());
				BigDecimal factBigDecimal = new BigDecimal(factAmount);
				BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);

				pmsAppTransInfo.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());// 实际金额
				pmsAppTransInfo.setOrderamount(orderAmountBigDecimal.stripTrailingZeros().toPlainString());// 订单金额
				pmsAppTransInfo.setDrawMoneyType("1");// 普通提款

				// 插入订单信息
				Integer insertAppTrans = pmsAppTransInfoDao.insert(pmsAppTransInfo);
				if (insertAppTrans == 1) {

					// 查询订单信息
					pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pmsAppTransInfo.getOrderid());

					String quickRateType = resultMap.get("QUICKRATETYPE").toString();// 快捷支付费率类型

					// 获取o单第三方支付的费率
					AppRateConfig appRate = new AppRateConfig();
					appRate.setRateType(quickRateType);
					appRate.setoAgentNo(oAgentNo);
					AppRateConfig appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRate);

					if (appRateConfig == null) {
						// 若查到的是空值，直接返回错误
						// 若查到的是空值，直接返回错误
						logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
						return setResp("09", "没有查到相关费率配置,,请重试或联系客服");

					}

					String isTop = appRateConfig.getIsTop();
					String rate = appRateConfig.getRate();
					String topPoundage = appRateConfig.getTopPoundage();// 封顶手续费
					paymentAmount = pmsAppTransInfo.getFactamount();// 支付金额
					String minPoundageStr = appRateConfig.getBottomPoundage();// 最低手续费
					Double minPoundage = 0.0; // 附加费

					if (StringUtils.isNotBlank(appRateConfig.getIsBottom())
							&& appRateConfig.getIsBottom().equals("1")) {// 是否有清算费用，"1":有，“0”无
						if (StringUtils.isNotBlank(minPoundageStr)) {
							minPoundage = Double.parseDouble(minPoundageStr); // 清算手续费
						} else {
							// 若查到的是空值，直接返回错误
							logger.info("没有查到相关费率附加费（最低手续费）：" + merchantinfo.getMobilephone());
							return setResp("09", "没有查到相关费率配置,,请重试或联系客服");
						}
					}

					BigDecimal payAmount = null;
					BigDecimal dfactAmount = new BigDecimal(pmsAppTransInfo.getFactamount());
					// 费率
					BigDecimal fee = new BigDecimal(0);
					double settleFee = 0;
					double userfee = 0;
					String rateStr = "";
					// Double payfee = null;
					// 计算结算金额
					if ("1".equals(isTop)) {

						rateStr = rate + "-" + topPoundage;
						// 是封顶费率类型
						fee = new BigDecimal(rate).multiply(dfactAmount);

						if (fee.compareTo(new BigDecimal(topPoundage)) == 1) {
							// 手续费大于封顶金额，按封顶金额处理
							payAmount = dfactAmount
									.subtract(new BigDecimal(topPoundage).subtract(new BigDecimal(minPoundage)));
							fee = new BigDecimal(topPoundage).add(new BigDecimal(minPoundage));
						} else {
							// 按当前费率处理
							rateStr = rate;
							fee.add(new BigDecimal(minPoundage));
							payAmount = dfactAmount.subtract(fee);
						}

					} else {
						// 按当前费率处理
						double dfpag = Double.parseDouble(merchantinfo.getPoundage());
						double daifu = Double.parseDouble(merchantinfo.getCounter());
						if (!"".equals(originalinfo.getV_userFee()) && originalinfo.getV_userFee() != null) {
							userfee = Double.parseDouble(originalinfo.getV_userFee()) / 100;
						}

						if (!"".equals(originalinfo.getV_settleUserFee())
								&& originalinfo.getV_settleUserFee() != null) {
							settleFee = Double.parseDouble(originalinfo.getV_settleUserFee());
						}
						if (originalinfo.getV_type().equals("1")) {
							// 按当前费率处理
							rateStr = rate;
							if (Double.parseDouble(rateStr) >= userfee) {
								BigDecimal num = dfactAmount.multiply(new BigDecimal(userfee));
								if (num.doubleValue() / 100 >= daifu) {
									fee = num;
								} else {
									fee = new BigDecimal(daifu * 100);
								}

							} else {
								logger.info("费率低于成本费率：" + merchantinfo.getMercId());
								return setResp("12", "费率低于成本费率");
							}
							if (daifu < settleFee) {
								settleFee = daifu;
							}
						}
						if (originalinfo.getV_type().equals("0")) {

							// 按当前费率处理
							rateStr = rate;
							fee = new BigDecimal(rate).multiply(dfactAmount).add(new BigDecimal(minPoundage));
							// payfee = fee.doubleValue();
							if (daifu * 100 >= fee.doubleValue()) {
								fee = new BigDecimal(daifu * 100);
							}
							payAmount = dfactAmount.subtract(fee);
						}
					}

					payAmount = dfactAmount.subtract(fee).subtract(new BigDecimal(settleFee * 100));
					// 设置结算金额
					pmsAppTransInfo.setPayamount(payAmount.toString());// 结算金额
					pmsAppTransInfo.setRate(rateStr);// 0.50_35 || 0.50
					pmsAppTransInfo.setPoundage(fee.toString());
					pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
					// 转换double为int
					Integer paymentAmountInt = (int) Double.parseDouble(paymentAmount);

					// 验证支付方式是否开启
					payCheckResult = iPublicTradeVerifyService.totalVerify(paymentAmountInt, TradeTypeEnum.onlinePay,
							PaymentCodeEnum.hengFengQuickPay, oAgentNo, merchantinfo.getMercId());
					if (!payCheckResult.getErrCode().equals("0")) {
						// 交易不支持

						logger.info("不支持的支付方式，oAagentNo:" + oAgentNo + ",payType:"
								+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
						return setResp("13", "暂不支持该交易方式");
					}
					ViewKyChannelInfo channelInfo = AppPospContext.context.get(HENGFENGPAY + HENGFENGCHANNELNUM);

					// 设置通道信息
					pmsAppTransInfo.setBusinessNum(channelInfo.getBusinessnum());
					pmsAppTransInfo.setChannelNum(HENGFENGCHANNELNUM);

					// 查看当前交易是否已经生成了流水表
					PospTransInfo pospTransInfo = null;
					// 流水表是否需要更新的标记 0 insert，1：update
					int insertOrUpdateFlag = 0;
					// 生成上送流水号
					String transOrderId = generateTransOrderId(TradeTypeEnum.onlinePay,
							PaymentCodeEnum.hengFengQuickPay);
					if ((pospTransInfo = pospTransInfoDAO.searchByOrderId(pmsAppTransInfo.getOrderid())) != null) {
						// 已经存在，修改流水号，设置pospsn为空
						logger.info("订单号：" + pmsAppTransInfo.getOrderid() + ",生成上送通道的流水号：" + transOrderId);
						pospTransInfo.setTransOrderId(originalinfo.getV_oid());
						pospTransInfo.setResponsecode("99");
						pospTransInfo.setPospsn("");
						insertOrUpdateFlag = 1;
					} else {
						// 不存在流水，生成一个流水
						pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);
						// 设置上送流水号
						pospTransInfo.setTransOrderId(originalinfo.getV_oid());
						insertOrUpdateFlag = 0;
					}
					// 插入流水表信息
					if (insertOrUpdateFlag == 0) {
						// 插入一条流水
						pospTransInfoDAO.insert(pospTransInfo);
					} else if (insertOrUpdateFlag == 1) {
						// 更新一条流水
						pospTransInfoDAO.updateByOrderId(pospTransInfo);
					}
					logger.info("修改订单信息");
					logger.info(pmsAppTransInfo);
					PmsBusinessPos pmsBusinessPos = selectKey(originalinfo.getV_mid());
					int num = pmsAppTransInfoDao.update(pmsAppTransInfo);
					if (num > 0) {

						logger.info("上游通道商户号:" + pmsBusinessPos.getChannelnum());
						// 商户号码
						String merId = pmsBusinessPos.getBusinessnum();// 818310048160000
						// 商户号私钥
						String merKey = pmsBusinessPos.getKek();
						switch (pmsBusinessPos.getBusinessnum()) {
						case "0008136": // 高汇通代还
							logger.info("************************高汇通----代还----请求开始");
							retMap = CXLoanStillPay(originalinfo, retMap, pmsBusinessPos);
							break;
						case "12345678": // 上海漪雷代还
							logger.info("************************上海漪雷----代还----请求开始");
							retMap = ylLoanStillPay(originalinfo, retMap, pmsBusinessPos);
							break;
						default:
							break;
						}

					}
				}
			} else {
				// 请求参数为空
				logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
				retMap.put("v_code", "16");
				retMap.put("v_msg", "商户没有进行实名认证,请重试或联系客服");
				return retMap;
			}
		} else {
			logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
			retMap.put("v_code", "16");
			retMap.put("v_msg", "商户没有进行实名认证,请重试或联系客服\"");
			return retMap;
		}
		return retMap;
	}

	/**
	 * 上海漪雷代还接口
	 * 
	 * @param originalinfo
	 * @param result
	 * @return
	 */
	public Map<String, String> ylLoanStillPay(MessageRequestEntity originalinfo, Map<String, String> result,
			PmsBusinessPos pmsBusinessPos) {

		PmsWeixinMerchartInfo weixin = new PmsWeixinMerchartInfo();

		weixin.setCardNo(originalinfo.getV_settleCardNo());
		weixin.setRealName(originalinfo.getV_settleName());
		weixin.setCertNo(originalinfo.getV_cert_no());
		weixin.setMobile(originalinfo.getV_phone());

		PmsWeixinMerchartInfo model = weixinService.selectByCardEntity(weixin);

		logger.info("上海漪雷原订单数据:" + JSON.toJSON(model));

		if (model == null) {
			String cooperatorUserId = UtilDate.getDateTime();
			String callBackUrl = BaseUtil.url+"/quickPayAction/hddhNotifyUrl.action";
			String cooperator_order_id = originalinfo.getV_oid();
			String cooperatorId = "b3b4f7f52060ab7fcc81d9f60382ee1e";
			Map<String, String> map = new HashMap<String, String>();
			map.put("cooperatorUserId", cooperatorUserId);
			map.put("joinType", "H5");
			map.put("callBackUrl", callBackUrl);
			map.put("cooperatorOrderId", cooperator_order_id);
			map.put("cooperatorId", cooperatorId);
			net.sf.json.JSONObject j = net.sf.json.JSONObject.fromObject(map);
			logger.info("海德绑卡签名json数据:" + j.toString());

			xdt.quickpay.hddh.util.MD5 md = new xdt.quickpay.hddh.util.MD5();
			byte[] raw;
			try {
				raw = j.toString().getBytes("utf-8");
				String data = Base64.encode(raw, 0, raw.length);
				String sign = "data=" + data + "&key=bb946c036823d4372617c366e7939efd";
				logger.info("海德绑卡生成的签名前的数据:" + sign);
				String signMsg = md.md5(sign);
				logger.info("海德绑卡生成的签名:" + signMsg);

				String url = "http://api.kuaikuaifu.net/ypapi/repay/createBindCardOrder.do";

				String params = "sign=" + signMsg + "&cooperatorId=" + cooperatorId + "&data=" + data;

				String path = url + "?" + params;

				HttpURLConection http = new HttpURLConection();
				String results = http.httpURLConectionGET(path, "UTF-8");
				logger.info("海德绑卡响应结果" + results);
				Map<String, String> maps = new HashMap<String, String>();
				maps = ApiUtil.toMap(results);
				logger.info("海德解析map结果" + maps);
				String datas = new String(xdt.quickpay.jbb.util.Base64.decode(maps.get("data")),
						Charset.forName("UTF-8"));
				maps = ApiUtil.toMap(datas);
				result.put("html", maps.get("jumpUrl"));

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 上个方法调用的创新预下单接口
	 * 
	 * @param originalinfo
	 * @param result
	 * @return
	 */
	public Map<String, String> CXLoanStillPay(MessageRequestEntity originalinfo, Map<String, String> result,
			PmsBusinessPos pmsBusinessPos) {
		ChinaInPayQuickPayRequest cpspr = new ChinaInPayQuickPayRequest();
		cpspr.setService("quickPayApply");
		cpspr.setMerchantNo("CX" + pmsBusinessPos.getBusinessnum());// CXUtil.merId
		cpspr.setBgUrl(CXUtil.notifyUrl);
		cpspr.setVersion("V2.0");
		cpspr.setPayChannelCode("ABC");
		if ("1".equals(originalinfo.getV_accountType())) {
			cpspr.setPayChannelType("1");
		} else if ("2".equals(originalinfo.getV_accountType())) {
			cpspr.setPayChannelType("6");
			cpspr.setCvv2(com.innovatepay.merchsdk.util.AESUtil.AESEncode(CXUtil.aesKey, originalinfo.getV_cvn2()));
			cpspr.setValidPeriod(
					com.innovatepay.merchsdk.util.AESUtil.AESEncode(CXUtil.aesKey, originalinfo.getV_expired()));
		}
		cpspr.setOrderNo(originalinfo.getV_oid());
		cpspr.setOrderAmount(Double.parseDouble(originalinfo.getV_txnAmt()) * 100 + "");
		cpspr.setCurCode("CNY");
		cpspr.setOrderTime(new SimpleDateFormat("YYYYMMDDHHMMSS").format(new Date()));
		cpspr.setProductName("大饼鸡蛋");
		cpspr.setBankCardNo(com.innovatepay.merchsdk.util.AESUtil.AESEncode(CXUtil.aesKey, originalinfo.getV_cardNo()));
		cpspr.setIdType("01");
		cpspr.setUserName(com.innovatepay.merchsdk.util.AESUtil.AESEncode(CXUtil.aesKey, originalinfo.getV_realName()));
		cpspr.setIdCode(com.innovatepay.merchsdk.util.AESUtil.AESEncode(CXUtil.aesKey, originalinfo.getV_cert_no()));
		cpspr.setPhone(com.innovatepay.merchsdk.util.AESUtil.AESEncode(CXUtil.aesKey, originalinfo.getV_phone()));
		cpspr.setExt1(originalinfo.getV_attach());
		// cpspr.setExt2("");
		cpspr.setOrderSource(originalinfo.getV_channel());
		ChinaInPayRequest<ChinaInPayQuickPayRequest> request = new ChinaInPayRequest<ChinaInPayQuickPayRequest>();
		request.setTransDetail(cpspr);
		String url = CXUtil.url + "/quickPay";
		String serviceName = "quickPayApply";
		// 商户私钥
		String privateKey = pmsBusinessPos.getKek();// CXUtil.privateKey;
		// 接入网关url
		DefaultChinaInPayClient client = new DefaultChinaInPayClient(url, serviceName, privateKey);
		String results = null;
		try {
			results = client.execute(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("创新支付返回信息：" + JSON.toJSON(results));
		// 返回参数:{"orderNo":"QP20180307180025368889","cxOrderNo":"600000100035738119","orderAmount":"1","dealMsg":"交易成功","needSms":"1","sign":"oOUa5fF6gIdJfp%2Fcihx7TBwu7b910KXXEaGEOcstsdch3jxxOGGbRwTD7DeX7E74tYfFmC%2F8Hg5nI7z8VGbxQ0H4AkTUEOoFEUZAxYjMjR4Ay6apc3ktPboLR5laff2krByR5BXuFKPltUnZiQQ0HRSzebxHzC%2FVz6KmcrPNQ5g%3D","dealCode":"10000","merchantNo":"CX0001760"}

		JSONObject json = JSONObject.parseObject(results);

		if ("10000".equals(json.getString("dealCode"))) {

			result = CXConfirmPay(originalinfo, result);
		} else {
			result.put("v_code", "15");
			result.put("v_msg", "请求失败");
			result.put("v_mid", originalinfo.getV_mid());
			result.put("v_oid", originalinfo.getV_oid());
			result.put("v_txnAmt", originalinfo.getV_txnAmt());
		}
		return result;
	}

	/**
	 * 上个方法调用的创新预下单接口
	 * 
	 * @param originalinfo
	 * @param result
	 * @return
	 */
	public Map<String, String> CXConfirmPay(MessageRequestEntity originalinfo, Map<String, String> result) {
		PmsBusinessPos pmsBusinessPos = selectKey(originalinfo.getV_mid());
		ChinaInPayQuickPayRequest cpspr = new ChinaInPayQuickPayRequest();
		cpspr.setService("quickPayConfirm");
		cpspr.setMerchantNo("CX" + pmsBusinessPos.getBusinessnum());// CXUtil.merId
		cpspr.setBgUrl(CXUtil.notifyUrl);
		cpspr.setVersion("V2.0");
		cpspr.setOrderNo(originalinfo.getV_oid());
		cpspr.setOrderSource(originalinfo.getV_channel());
		ChinaInPayRequest<ChinaInPayQuickPayRequest> request = new ChinaInPayRequest<ChinaInPayQuickPayRequest>();
		request.setTransDetail(cpspr);
		String url = CXUtil.url + "/quickPay";
		String serviceName = "quickPayConfirm";
		// 商户私钥
		String privateKey = pmsBusinessPos.getKek();// CXUtil.privateKey;
		// 接入网关url
		DefaultChinaInPayClient client = new DefaultChinaInPayClient(url, serviceName, privateKey);
		String results = null;
		try {
			results = client.execute(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("创新支付返回信息：" + JSON.toJSON(results));
		// 返回参数:{"orderNo":"QP20180307180025368889","cxOrderNo":"600000100035738119","orderAmount":"1","dealMsg":"交易成功","needSms":"1","sign":"oOUa5fF6gIdJfp%2Fcihx7TBwu7b910KXXEaGEOcstsdch3jxxOGGbRwTD7DeX7E74tYfFmC%2F8Hg5nI7z8VGbxQ0H4AkTUEOoFEUZAxYjMjR4Ay6apc3ktPboLR5laff2krByR5BXuFKPltUnZiQQ0HRSzebxHzC%2FVz6KmcrPNQ5g%3D","dealCode":"10000","merchantNo":"CX0001760"}

		JSONObject json = JSONObject.parseObject(results);

		if ("10000".equals(json.getString("dealCode"))) {
			result.put("v_code", "00");
			result.put("v_msg", "请求成功");
			result.put("v_mid", originalinfo.getV_mid());
			result.put("v_oid", originalinfo.getV_oid());
			result.put("v_txnAmt", originalinfo.getV_txnAmt());
		} else if ("10001".equals(json.getString("dealCode"))) {
			result.put("v_code", "00");
			result.put("v_msg", "请求成功");
			result.put("v_mid", originalinfo.getV_mid());
			result.put("v_oid", originalinfo.getV_oid());
			result.put("v_txnAmt", originalinfo.getV_txnAmt());
		} else {
			result.put("v_code", "15");
			result.put("v_msg", "请求失败");
			result.put("v_mid", originalinfo.getV_mid());
			result.put("v_oid", originalinfo.getV_oid());
			result.put("v_txnAmt", originalinfo.getV_txnAmt());
		}
		return result;
	}

	/**
	 * 获取卡信息
	 */
	@Override
	public Map<String, String> selectCard(MessageRequestEntity entity) {
		Map<String, String> crd = new HashMap<>();
		Map<String, String> map = new HashMap<>();
		PmsBusinessPos pmsBusinessPos = selectKey(entity.getV_mid());
		try {
			final String merCertPath = new File(this.getClass().getResource("/").getPath()).getParentFile()
					.getParentFile().getCanonicalPath() + "//ky//" + pmsBusinessPos.getBusinessnum() + ".cer";
			final String pfxPath = new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile()
					.getCanonicalPath() + "//ky//" + pmsBusinessPos.getBusinessnum() + ".pfx";
			final String pfxPwd = pmsBusinessPos.getKek();
			YufuCipher cipher = null;
			YufuCipherSupport instance = null;
			cipher = YufuCipherSupport.getCipherInstance( merCertPath, pfxPath, pfxPwd,cipher,instance);
			///YufuCipher cipher = YufuCipherSupport.getCipherInstance(merCertPath, pfxPath, pfxPwd);
			crd.put("version", "1.0.0");
			crd.put("merchantId", pmsBusinessPos.getBusinessnum());
			crd.put("merchantUserId", entity.getV_userId());
			String data = GsonUtil.objToJson(crd);
			logger.info("data:" + data);
			Map<String, String> params = GsonUtil.jsonToObj(data, Map.class);
			ParamPacket bo = cipher.doPack(params);
			logger.info("11!:" + JSON.toJSON(bo));
			TreeMap<String, String> map_param = new TreeMap<>();
			map_param.put("merchantId", pmsBusinessPos.getBusinessnum());
			map_param.put("data", URLEncoder.encode(bo.getData(), "utf-8"));
			map_param.put("enc", URLEncoder.encode(bo.getEnc(), "utf-8"));
			map_param.put("sign", URLEncoder.encode(bo.getSign(), "utf-8"));
			String urlPay = "";
			if ("000001110100000812".equals(pmsBusinessPos.getBusinessnum())) {
				urlPay = "http://malltest.yfpayment.com/payment/service/cards.do";
			} else  {
				urlPay = "http://www.yfpayment.com/payment/service/cards.do";
			}
			String returnStr = PostUtils.doPost(urlPay, map_param);

			if (returnStr != null && !"".equals(returnStr)) {

				// 二、验签解密
				returnStr = URLDecoder.decode(returnStr, "utf-8");
				System.out.println("URL解码后的置单应答结果：" + returnStr);
				TreeMap<String, String> boMap = JSON.parseObject(returnStr,
						new TypeReference<TreeMap<String, String>>() {
						});
				Map<String, String> payshowParams = cipher
						.unPack(new ParamPacket(boMap.get("data"), boMap.get("enc"), boMap.get("sign")));
				System.out.println("解密后的置单应答结果：" + payshowParams);
				map.put("v_cardInfoList", payshowParams.get("cardInfoList"));
				map.put("v_code", "00");
				map.put("v_msg", payshowParams.get("respDesc"));
				map.put("v_userId", entity.getV_userId());
				map.put("v_mid", entity.getV_mid());

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return map;
	}
}
