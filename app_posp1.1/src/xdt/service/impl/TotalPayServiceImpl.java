package xdt.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.xml.sax.InputSource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.capinfo.crypt.Md5;
import com.huateng.xmapper.common.IConstants;
import com.ielpm.mer.sdk.secret.Secret;
import com.ielpm.mer.sdk.secret.SecretConfig;
import com.innovatepay.merchsdk.DefaultChinaInPayClient;
import com.innovatepay.merchsdk.request.ChinaInPayOnePayRequest;
import com.innovatepay.merchsdk.request.ChinaInPayRequest;
import com.innovatepay.merchsdk.request.ChinaInPaySameNamePayRequest;
import com.innovatepay.merchsdk.request.ChinaInPaySameNameSearchRequest;
import com.innovatepay.merchsdk.request.ChinaInPaySearchOrder;
import com.innovatepay.merchsdk.util.Base64Util;
import com.kspay.MD5Util;
import com.kspay.cert.CertVerify;
import com.kspay.cert.LoadKeyFromPKCS12;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.uns.inf.api.common.Util;
import com.yst.m2.sdk.M2;
import com.yst.m2.sdk.M2Config;
import com.yst.m2.sdk.M2Obj;
import com.yufusoft.payplatform.security.cipher.YufuCipher;
import com.yufusoft.payplatform.security.vo.ParamPacket;
import xdt.dao.IAmountLimitControlDao;
import xdt.dao.IAppRateConfigDao;
import xdt.dao.IMerchantMineDao;
import xdt.dao.IPayBankInfoDao;
import xdt.dao.IPayTypeControlDao;
import xdt.dao.IPmsAppAmountAndRateConfigDao;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPmsBusinessPosDao;
import xdt.dao.IPmsDaifuMerchantInfoDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dao.OriginalOrderInfoDao;
import xdt.dto.cj.BaseConstant;
import xdt.dto.cj.ChanPayUtil;
import xdt.dto.cx.CXThread;
import xdt.dto.cx.CXUtil;
import xdt.dto.hfb.Desede;
import xdt.dto.hfb.HeepayClient2;
import xdt.dto.hfb.HfbUtil;
import xdt.dto.hj.HJPayRequest;
import xdt.dto.hj.HJPayResponse;
import xdt.dto.hj.HJThread;
import xdt.dto.hj.HJThread2;
import xdt.dto.hj.HJUtil;
import xdt.dto.hlb.HLBUtil;
import xdt.dto.hlb.HttpClientService;
import xdt.dto.hlb.MyBeanUtils;
import xdt.dto.hlb.RSA;
import xdt.dto.hm.AesEncryption;
import xdt.dto.hm.HMUtil;
import xdt.dto.hm.HttpsUtil;
import xdt.dto.hm.SHA256Util;
import xdt.dto.hm.TimeUtil;
import xdt.dto.jp.JpUtil;
import xdt.dto.jp.MerchantUtil;
import xdt.dto.jp.RSASignUtil;
import xdt.dto.jsds.JsThread;
import xdt.dto.jsds.JsdsRequestDto;
import xdt.dto.mb.HttpService;
import xdt.dto.mb.MBUtil;
import xdt.dto.pay.BaseResMessage;
import xdt.dto.pay.ConsumeVo;
import xdt.dto.pay.EncryptUtil;
import xdt.dto.pay.PayUtil;
import xdt.dto.pay.SignUtil;
import xdt.dto.pay.TokenRes;
import xdt.dto.quickPay.entity.ConsumeResponseEntity;
import xdt.dto.scanCode.util.ResponseUtil;
import xdt.dto.scanCode.util.ScanCodeUtil;
import xdt.dto.scanCode.util.WFBThread;
import xdt.dto.scanCode.util.YSZFThread;
import xdt.dto.sxf.Base64Utils;
import xdt.dto.sxf.DESUtils;
import xdt.dto.sxf.DF1003Request;
import xdt.dto.sxf.HttpClientUtil;
import xdt.dto.sxf.JsonUtils;
import xdt.dto.sxf.PayRequsest;
import xdt.dto.sxf.PayResponse;
import xdt.dto.sxf.SXFUtil;
import xdt.dto.sxf.SxfThread;
import xdt.dto.tfb.TFBConfig;
import xdt.dto.transfer_accounts.entity.BalanceRequestEntity;
import xdt.dto.transfer_accounts.entity.DaifuQueryRequestEntity;
import xdt.dto.transfer_accounts.entity.DaifuRequestEntity;
import xdt.dto.transfer_accounts.util.JHJThread;
import xdt.dto.yb.YBThread;
import xdt.dto.yb.YBUtil;
import xdt.dto.yb.YeepayService;
import xdt.dto.yf.BatchDisburseApplyReq;
import xdt.dto.yf.BatchDisburseApplyRsp;
import xdt.dto.yf.DTOUtil;
import xdt.dto.yf.DisburseClientUtil;
import xdt.dto.yf.DoYf;
import xdt.dto.yf.FileDownReq;
import xdt.dto.yf.GsonUtil;
import xdt.dto.yf.MD5FileUtil;
import xdt.dto.yf.OnePayRequest;
import xdt.dto.yf.PayReq;
import xdt.dto.yf.PayRequest;
import xdt.dto.yf.PostUtils;
import xdt.dto.yf.RefundChequeResultDownReq;
import xdt.dto.yf.RefundChequeResultDownRsp;
import xdt.dto.yf.StringUtil;
import xdt.dto.yf.YFUtil;
import xdt.dto.yf.YufuCipherSupport;
import xdt.model.OriginalOrderInfo;
import xdt.model.PayBankInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PmsWeixinMerchartInfo;
import xdt.model.PospRouteInfo;
import xdt.quickpay.daikou.util.HttpUtils;
import xdt.quickpay.jbb.entity.xml.util.CertKeyUtil;
import xdt.quickpay.jbb.entity.xml.util.DateUtil;
import xdt.quickpay.jbb.entity.xml.y2e.Y2e0010Res;
import xdt.quickpay.jbb.entity.xml.y2e.Y2e1010Req;
import xdt.quickpay.jbb.util.EctonRSAUtils;
import xdt.quickpay.jbb.util.HttpsUtils;
import xdt.quickpay.jbb.util.Sign;
import xdt.quickpay.nbs.common.util.SignatureUtil;
import xdt.quickpay.wzf.Constant;
import xdt.quickpay.wzf.UniPaySignUtils;
import xdt.quickpay.ysb.Md5Encrypt;
import xdt.quickpay.yy.util.AesEncryptUtil;
import xdt.quickpay.yy.util.EmaxPlusUtil;
import xdt.quickpay.yy.util.JsonTools;
import xdt.schedule.ThreadPool;
import xdt.service.HfQuickPayService;
import xdt.service.IHJService;
import xdt.service.IPmsDaifuMerchantInfoService;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.ISxfService;
import xdt.service.ITotalPayService;
import xdt.service.JsdsQrCodeService;
import xdt.service.PmsWeixinMerchartInfoService;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;
import xdt.util.JsdsUtil;
import xdt.util.JsonUtil;
import xdt.util.RSAUtil;
import xdt.util.UtilDate;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.RSAUtils;
import xdt.util.utils.RequestUtils;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2017年12月27日 上午10:35:04 类说明
 */
@Service
public class TotalPayServiceImpl extends BaseServiceImpl implements ITotalPayService {

	Logger log = Logger.getLogger(this.getClass());

	@Resource
	private IMerchantMineDao merchantMineDao;

	// 商户信息服务层
	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao;

	// 原始数据
	@Resource
	private OriginalOrderInfoDao originalDao;

	@Resource
	private IPmsAppAmountAndRateConfigDao pmsAppAmountAndRateConfigDao;// 商户费率配置

	@Resource
	private IAppRateConfigDao appRateConfigDao;// 费率

	@Resource
	public IPmsAppTransInfoDao pmsAppTransInfoDao; // 业务配置服务层

	@Resource
	private IPospTransInfoDAO pospTransInfoDAO;// 流水

	@Resource
	private IPublicTradeVerifyService publicTradeVerifyService;

	@Resource
	private IPayTypeControlDao payTypeControlDao;// 开关
	@Resource
	private IPublicTradeVerifyService iPublicTradeVerifyService;
	@Resource
	private IAmountLimitControlDao amountLimitControlDao;// 最大值最小值总开关判断
	@Resource
	private IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao;// 代付
	@Resource
	private HfQuickPayService payService;

	@Resource
	private IHJService ihjService;
	@Resource
	public PmsWeixinMerchartInfoService weixinService;
	@Resource
	private IPmsDaifuMerchantInfoService daifuMerchantInfoService;

	@Resource
	private JsdsQrCodeService JsdsQrCodeService;

	@Resource
	private ISxfService sxfService;

	@Resource
	private IPayBankInfoDao payBankInfoDao;

	private JSONObject json;
	@Resource
	private IPmsBusinessPosDao businessPosDao;
	@Override
	public Map<String, String> pay(DaifuRequestEntity payRequest, Map<String, String> result) {
		//log.info("下游传送代付参数:" + JSON.toJSON(payRequest));
		BigDecimal b1 = new BigDecimal("0");// 下游上传的总金额
		BigDecimal b2 = new BigDecimal("0");// 系统代付余额
		BigDecimal b3 = new BigDecimal("0");// 单笔交易总手续费
		BigDecimal PayFree = new BigDecimal("0");// 代付手续费率
		BigDecimal min = new BigDecimal("0");// 代付最小金额
		BigDecimal max = new BigDecimal("0");// 代付最大金额
		Double d = 0.0;
		Double big=0.0;// 代付剩余金额
		BigDecimal volumn = new BigDecimal("0");
		List<String> list =new ArrayList<>();
		log.info("查询当前代付订单是否存在");
		PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
		model.setMercId(payRequest.getV_mid());
		model.setBatchNo(payRequest.getV_batch_no());
		if (pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model) != null) {
			result.put("v_code", "03");
			result.put("v_msg", "下单失败,订单存在");
			log.info("**********************代付 下单失败:{}");
			log.info("订单存在");
			result.remove("fee");
			result.remove("type");
			return result;
		}
		try {

			log.info("根据商户号查询");
			String e = payRequest.getV_mid();
			PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
			merchantinfo.setMercId(e);
			List<PmsMerchantInfo> merchantList = this.pmsMerchantInfoDao.searchList(merchantinfo);
			if (merchantList.size() != 0 && !merchantList.isEmpty()) {
				merchantinfo = (PmsMerchantInfo) merchantList.get(0);
				if (merchantinfo.getOpenPay().equals("1")) {
					result.put("v_code", "04");
					result.put("v_msg", "未开通代付,请重试或联系客服");
					result.remove("fee");
					result.remove("type");
					return result;
				}
				String oAgentNo = merchantinfo.getoAgentNo();
				log.info("*************商户信息:" + merchantinfo);
				if (StringUtils.isBlank(oAgentNo)) {
					throw new RuntimeException("系统错误----------------o单编号为空");
				}
				int count =1;
				if ("60".equals(merchantinfo.getMercSts())) {
					// 插入异步数据
					
					PmsBusinessPos pmsBusinessPos = selectKey(payRequest.getV_mid());
					if(pmsBusinessPos==null){
						result.put("v_code", "18");
						result.put("v_msg", "未找到路由，请联系业务开通！");
						return result;
					}
					//判断入金是否开启
					if("1".equals(pmsBusinessPos.getGoldPay())) {
						result.put("v_code", "19");
						result.put("v_msg", "出金未开通,请联系业务经理!");
						return result;
					}
					// 判断交易类型
					log.info("实际金额");
					// 元
					if("YFWG".equals(pmsBusinessPos.getChannelnum())) {
						if("1".equals(result.get("type"))) {
							String path =new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//upload//"+payRequest.getV_fileName().getOriginalFilename();
							log.info("路径---------："+path);
							list =readZipContext(path);
							count=list.size();
							for (int i = 0; i < list.size(); i++) {
								String record =list.get(i);
								System.out.println(record);
								String [] arr =record.split("\\|");
								big=big+Double.parseDouble(arr[7]);
								System.out.println(big);
							}
							b1 =b1.add(new BigDecimal(Double.toString(big/100)));
							payRequest.setV_sum_amount(b1.toString());
							payRequest.setV_amount(b1.toString());
							payRequest.setV_count(list.size()+"");
						}
					}
					
					String payAmt = payRequest.getV_sum_amount();
					String amount = payRequest.getV_amount();
					
					if("".equals(payAmt)||null ==payAmt) {
						result.put("v_code", "01");
						result.put("v_msg", "金额为空");
						result.remove("fee");
						result.remove("type");
						return result;
					}
					Double d1 =Double.parseDouble(amount)*Double.parseDouble(payRequest.getV_count());
					Double d2=Double.parseDouble(payRequest.getV_sum_amount());
					if(d1-d2!=0) {
						result.put("v_code", "01");
						result.put("v_msg", "金额和总金额不一致");
						result.remove("fee");
						result.remove("type");
						return result;
					}
					b1 = new BigDecimal(payAmt);
					log.info("上游通道商户号:" + pmsBusinessPos.getChannelnum());
					switch (pmsBusinessPos.getChannelnum()) {
					case "HJZF":// 汇聚
						b3 = new BigDecimal(merchantinfo.getPoundage());
						log.info("汇聚----系统商户代付单笔手续费:" + b3.doubleValue());

						if (!"".equals(merchantinfo.getPoundageFree()) && merchantinfo.getPoundageFree() != null) {
							log.info("laile");
							PayFree = new BigDecimal(merchantinfo.getPoundageFree()).divide(new BigDecimal("100"));
							d = b1.multiply(PayFree).doubleValue();
						} else {
							log.info("lailenull");
							d = 0.0;
						}
						break;
					
					default:
						break;
					}
					saveOriginAlInfoWxPay(payRequest, payRequest.getV_batch_no(), payRequest.getV_mid());
					log.info("下游上传代付总金额:" + b1.doubleValue());
					log.info("汇聚----系统商户代付单笔手续费率:" + PayFree.doubleValue());
					b3 = new BigDecimal(merchantinfo.getPoundage()).multiply(new BigDecimal(count));
					log.info("系统商户代付总手续费:" + b3.doubleValue());
					min = new BigDecimal(merchantinfo.getMinDaiFu());
					log.info("系统代付最小金额:" + min.doubleValue());
					max = new BigDecimal(merchantinfo.getMaxDaiFu());
					log.info("系统代付最大金额:" + max.doubleValue());
					if ("0".equals(payRequest.getV_type())) {
						b2 = new BigDecimal(merchantinfo.getPosition());

					} else if ("1".equals(payRequest.getV_type())) {

						b2 = new BigDecimal(merchantinfo.getPositionT1());
					}

					log.info("系统剩余可用额度:" + b2.doubleValue());
					volumn = new BigDecimal(payRequest.getV_count());
					log.info("总笔数:" + volumn.toString());

					Double fee = b3.doubleValue();
					Double fes =fee-Double.parseDouble(merchantinfo.getPoundage());
					log.info("代付总手续费:" + fee);
					result.put("fee", fee.toString());
					if ("HJZF".equals(pmsBusinessPos.getChannelnum())) {
						if (b1.doubleValue() + d + fee > b2.doubleValue() / 100) {
							result.put("v_code", "06");
							result.put("v_msg", "下单失败,代付金额高于剩余额度");
							log.info("汇聚**********************代付金额高于剩余额度");
							int i = add(payRequest, merchantinfo, result, "01");
							if (i == 1) {
								log.info("汇聚----添加失败订单成功");
							}
							result.remove("fee");
							result.remove("type");
							return result;
						}

					} else {
						if (b1.doubleValue() + fee > b2.doubleValue() / 100) {
							result.put("v_code", "05");
							result.put("v_msg", "下单失败,代付金额高于剩余额度");
							log.info("代付金额高于剩余额度");
							int i = add(payRequest, merchantinfo, result, "01");
							if (i == 1) {
								log.info("添加失败订单成功");
							}
							result.remove("fee");
							result.remove("type");
							return result;
						}

					}

					if (b1.doubleValue() < min.doubleValue()) {
						result.put("v_code", "06");
						result.put("v_msg", "下单失败,代付金额小于代付最小金额");
						log.info("代付金额小于代付最小金额");
						int i = add(payRequest, merchantinfo, result, "01");
						if (i == 1) {
							log.info("添加失败订单成功");
						}
						result.remove("fee");
						result.remove("type");
						return result;
					}
					if (b1.doubleValue() > max.doubleValue()) {
						result.put("v_code", "07");
						result.put("v_msg", "下单失败,代付金额大于代付最大金额");
						log.info("代付金额大于代付最大金额");
						int i = add(payRequest, merchantinfo, result, "01");
						if (i == 1) {
							log.info("添加失败订单成功");
						}
						result.remove("fee");
						result.remove("type");
						return result;
					}
					Map<String, String> mapPay = new HashMap<String, String>();
					mapPay.put("machId", payRequest.getV_mid());
					if ("HJZF".equals(pmsBusinessPos.getChannelnum())) {
						mapPay.put("payMoney", b1.doubleValue() * 100 + d * 100 + "");
					} else {

						mapPay.put("payMoney", b1.doubleValue() * 100 +fes*100+ "");
					}

					int num = 0;

					if ("0".equals(payRequest.getV_type())) {
						num = pmsMerchantInfoDao.updataD0(mapPay);
					} else if ("1".equals(payRequest.getV_type())) {
						num = pmsMerchantInfoDao.updataT1(mapPay);
					}
					if (num != 1) {
						log.info("扣款失败！！");
						result.put("v_code", "15");
						result.put("v_msg", "请求失败");
						result.remove("fee");
						result.remove("type");
						return result;
					}
					int i = 0 ;
					if ("YFWG".equals(pmsBusinessPos.getChannelnum())) {
						if("1".equals(result.get("type"))) {
							for (int j = 0; j < list.size(); j++) {
								String record =list.get(j);
								String [] arr =record.split("\\|");
								payRequest.setV_batch_no(payRequest.getV_batch_no());
								payRequest.setV_identity(arr[0]);
								payRequest.setV_amount(Double.parseDouble(arr[7])/100+"");
								payRequest.setV_realName(arr[1]);
								payRequest.setV_cardNo(arr[2]);
								payRequest.setV_bankname(arr[3]);
								payRequest.setV_province(arr[4]);
								payRequest.setV_city(arr[5]);
								i = add(payRequest, merchantinfo, result, "200");
								System.out.println("第"+j+"次添加状态"+i);
							}
						}else {
							 i = add(payRequest, merchantinfo, result, "200");
						}
					}else {
					  i = add(payRequest, merchantinfo, result, "200");
					}
					if (i == 1) {
						log.info("添加代付扣款订单成功！");
					}
					if (i == 1) {
						log.info("代付订单添加成功");
						int iii =insertProfit(payRequest.getV_batch_no(), payRequest.getV_sum_amount(), merchantinfo, "代付", payRequest.getV_type());
						System.out.println(iii);
						switch ("YSZF") {//pmsBusinessPos.getChannelnum()

						case "SXYWG":// 首信易网关
							result = payeasyAccounts(payRequest, result, merchantinfo, pmsBusinessPos);
							break;
						case "JS100669":// 江苏电商扫码支付
							result = jsdsAccounts(payRequest, result, merchantinfo, pmsBusinessPos);
							break;
						case "SXF": // 随行付
							result = sxfAccounts(payRequest, result, merchantinfo, pmsBusinessPos);
							break;
						case "MBXHF":// 魔宝
							result = moAccounts(payRequest, result, merchantinfo, pmsBusinessPos);
							break;
						case "TXZF":// 天下支付
							result = tfbAccounts(payRequest, result, merchantinfo, pmsBusinessPos);
							break;
						case "HFB":// 汇付宝
							result = hfbAccounts(payRequest, result, merchantinfo, pmsBusinessPos);
							break;
						case "HJZF":// 汇聚（老）
							result = hjPays(payRequest, result, merchantinfo, pmsBusinessPos);
							break;
						case "HLB":// 合利宝
							if ("1".equals(payRequest.getV_accountType())) {
								result = settlementCardWithdraw(payRequest, result);
							} else if ("2".equals(payRequest.getV_accountType())) {
								result = creditCardWithdraw(payRequest, result);
							}
							break;
						case "CJ":// 畅捷纯代付
							result = sendPay(payRequest, result, merchantinfo, pmsBusinessPos);
							break;
						case "CJT":// 畅捷贷还的代付
							result = withdrawals(payRequest, result, merchantinfo, pmsBusinessPos);
							break;
						case "GZHM":// 广州恒明
							result = hmbAccounts(payRequest, result, merchantinfo, pmsBusinessPos);
							break;
						case "WZF001":// 沃支付
						case "WZF002":// 沃支付
						case "WZF003":// 沃支付
							result = wzfAccounts(payRequest, result, merchantinfo, pmsBusinessPos);
							break;
						case "JP":// 九派
							jpPay(payRequest, result, merchantinfo, pmsBusinessPos);
							break;
						case "YBLS":// 易宝
							ybPay(payRequest, result, merchantinfo, pmsBusinessPos);
							break;
						case "CX":
							if("1".equals(payRequest.getV_ifName())){
								//同名
								cxPay(payRequest, result, merchantinfo, pmsBusinessPos);
							}else if("2".equals(payRequest.getV_ifName())) {
								//非同名
								cxWrongPay(payRequest, result, merchantinfo, pmsBusinessPos);
							}
							break;
						case "YY":// 甬易
							yyPay(payRequest, result, merchantinfo, pmsBusinessPos);
							break;
						case "YFWG":// 裕福
							if("1".equals(result.get("type"))) {
								yfPay(payRequest, result, merchantinfo, pmsBusinessPos);
							}else if("0".equals(result.get("type"))){
								yfPayOne(payRequest, result, merchantinfo, pmsBusinessPos);
							}
							break;
						case "JBB":// 聚佰宝
							jbbPay(payRequest, result, merchantinfo, pmsBusinessPos);
							break;
						case "YSB":// 聚佰宝
							ysbPay(payRequest, result, merchantinfo, pmsBusinessPos);
							break;
						case "JHJ":// 聚佰宝
							jhjPay(payRequest, result, merchantinfo, pmsBusinessPos);
							break;
						case "JMZFB":
							jmPay(payRequest, result, merchantinfo, pmsBusinessPos);
							break;
						case "WFB":
							wfbPay(payRequest, result, merchantinfo, pmsBusinessPos);
							break;
						case "YYT":
							yytPay(payRequest, result, merchantinfo, pmsBusinessPos);
							break;
						case "YSZF":
							yszfPay(payRequest, result, merchantinfo, pmsBusinessPos);
							break;
						default:
							result.put("v_code", "17");
							result.put("v_msg", "未找到路由,请联系运营");
							break;
						}
					}
					result.remove("fee");
					result.remove("type");
				} else {
					// 请求参数为空
					log.info("商户没有进行实名认证，" + merchantinfo.getMercId());
					result.put("v_code", "08");
					result.put("v_msg", "还没有进行实名认证，请先去进行实名认证，或者等待客服审核!");
					log.info("还没有进行实名认证，请先去进行实名认证，或者等待客服审核!");
					result.remove("type");
					result.remove("fee");
					return result;
				}

			} else {
				result.put("v_code", "09");
				result.put("v_msg", "此商户不存在,请重新输入!");
				log.info("此商户不存在,请重新输入!");
				result.remove("fee");
				result.remove("type");
				return result;
			}

		} catch (Exception var43) {
			log.error("代付错误", var43);
		}

		log.info("代付------处理完成");
		result.remove("fee");
		result.remove("type");
		return result;
	}

	/**
	 * 
	 * @Description 插入原始订单表信息
	 * @author Administrator
	 * @param reqeustInfo
	 * @param orderid
	 * @param mercId
	 * @throws Exception
	 */
	private int saveOriginAlInfoWxPay(DaifuRequestEntity payRequest, String orderid, String mercId) throws Exception {
		// 插入原始信息
		OriginalOrderInfo info = new OriginalOrderInfo();
		info.setPid(mercId);
		info.setMerchantOrderId(orderid);
		info.setOrderId(orderid);
		info.setOrderTime(payRequest.getV_time());
		info.setPayType(payRequest.getV_type());
		// 想要传服务器要改实体
		info.setBgUrl(payRequest.getV_notify_url());
		Double amt = Double.parseDouble(payRequest.getV_amount());// 单位分
		// amt /= 100;
		DecimalFormat df = new DecimalFormat("######0.00");

		info.setOrderAmount(df.format(amt));

		return originalDao.insert(info);
	}

	/**
	 * 添加代付订单
	 * 
	 * @param payRequest
	 * @param merchantinfo
	 * @param result
	 * @param state
	 * @return
	 * @throws Exception
	 */
	public synchronized int add(DaifuRequestEntity payRequest, PmsMerchantInfo merchantinfo, Map<String, String> result,
			String state) throws Exception {
		log.info("进来添加代付订单了");
		BigDecimal b1 = new BigDecimal("0");// 总金额
		BigDecimal b2 = new BigDecimal("0");// 总金额
		int iii = 0;
		merchantinfo = select(payRequest.getV_mid());
		PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
		b1 = new BigDecimal(payRequest.getV_sum_amount());
		b2 = new BigDecimal(payRequest.getV_amount());
		model.setProvince(payRequest.getV_province());
		model.setCity(payRequest.getV_city());
		model.setMercId(payRequest.getV_mid());
		model.setCount("1");
		model.setBatchNo(payRequest.getV_batch_no());
		model.setIdentity(payRequest.getV_identity());
		model.setAmount(b1.doubleValue() + "");
		model.setCardno(payRequest.getV_cardNo());
		model.setRealname(payRequest.getV_realName());
		if (payRequest.getV_batch_no().indexOf("/A") != -1) {
			model.setPayamount(b2.doubleValue() + "");
		} else {
			model.setPayamount("-" + b2.doubleValue());
		}
		// 联行号
		model.setPmsbankno(payRequest.getV_pmsBankNo());
		if (payRequest.getV_batch_no().indexOf("/A") != -1) {
			model.setTransactionType("代付补款");
		} else {
			model.setTransactionType("代付");
		}

		if ("0".equals(payRequest.getV_type())) {
			model.setRemarks("D0");
			model.setPosition(String.valueOf(merchantinfo.getPosition()));

		} else if ("1".equals(payRequest.getV_type())) {
			model.setRemarks("T1");
			model.setPosition(String.valueOf(merchantinfo.getPositionT1()));

		}
		model.setRecordDescription("批次号:" + payRequest.getV_batch_no() + "订单号：" + payRequest.getV_batch_no() + "错误原因:"
				+ result.get("respMsg"));
		model.setResponsecode(state);
		model.setOagentno("100333");
		model.setIsDisplay("0");
		model.setIsDelete("1");
		model.setIsExamine("1");
		model.setIsAdd("0");
		// 手续费
		BigDecimal PayFree = new BigDecimal("0");
		Double d;
		if(!"".equals(result.get("fee"))&&result.get("fee")!=null) {
			PayFree = new BigDecimal(result.get("fee"));
		}else {
			PayFree = new BigDecimal(merchantinfo.getPoundage());
		}
		if (!"".equals(merchantinfo.getPoundageFree()) && merchantinfo.getPoundageFree() != null) {
			d = b1.multiply(new BigDecimal(merchantinfo.getPoundageFree())).doubleValue();// .setScale(1)
		} else {
			d = 0.0;
		}
		String poundage = new BigDecimal(d).add(PayFree) + "";
		model.setPayCounter(poundage);
		// model.setPayCounter(new BigDecimal(merchantinfo.getPoundage()).doubleValue()
		// + "");
		PmsDaifuMerchantInfo daifu = pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model);
		if (daifu == null) {
			iii = pmsDaifuMerchantInfoDao.insert(model);
			log.info("iii:" + iii);
		}

		return iii;
	}

	@Override
	public int UpdateDaifu(String batchNo, String responsecode) throws Exception {
		if (batchNo == null || batchNo == "") {
			return 0;
		}
		log.info("原始数据:" + batchNo);

		PmsDaifuMerchantInfo pdf = new PmsDaifuMerchantInfo();

		log.info("上送的批次号:" + batchNo);

		pdf.setBatchNo(batchNo);
		pdf.setResponsecode(responsecode);
		return pmsDaifuMerchantInfoDao.update(pdf);
	}

	
	@Override
	public int UpdateDaifu(String batchNo,String orderId, String responsecode) throws Exception {
		if (batchNo == null || batchNo == "") {
			return 0;
		}
		log.info("原始数据:" + batchNo);

		PmsDaifuMerchantInfo pdf = new PmsDaifuMerchantInfo();

		log.info("上送的批次号:" + batchNo);

		pdf.setBatchNo(batchNo);
		pdf.setIdentity(orderId);
		pdf.setResponsecode(responsecode);
		return pmsDaifuMerchantInfoDao.update(pdf);
	}

	/**
	 * 首信易代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 * @throws Exception
	 */
	public Map<String, String> payeasyAccounts(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) throws Exception {

		// Map<String, String> params=new HashMap<String,String>();
		Double surAmount = null;
		Double factAmount = Double.parseDouble(payRequest.getV_amount());
		BigDecimal b1 = new BigDecimal(factAmount.toString());
		BigDecimal b2 = new BigDecimal(merchantinfo.getPositionT1().toString());
		BigDecimal b3 = new BigDecimal(0.00);
		BigDecimal volumn = new BigDecimal("0");
		b3 = new BigDecimal(merchantinfo.getPoundage());
		log.info("代付金额:" + b1.multiply(new BigDecimal(100)).doubleValue());
		log.info("可用额度:" + b2.doubleValue());
		log.info("每笔代付手续费:" + b3);
		volumn = new BigDecimal("1");
		log.info("总笔数:" + volumn.toString());
		double fee = volumn.multiply(b3).doubleValue();
		log.info("代付总手续费:" + fee);
		Double payAmount = factAmount + fee;
		BigDecimal b4 = new BigDecimal(payAmount.toString());
		String v_data = "";
		log.info("上送的数据:" + v_data);
		// 查询上游商户号
		String v_mid = pmsBusinessPos.getBusinessnum();
		log.info("上游商户号:" + v_mid);
		// String v_mid = "13240";
		// String v_data = df.getV_data();
		String v_data1 = URLEncoder.encode(v_data, "UTF-8");
		String v_version = "1.0";
		Md5 md5 = new Md5("");
		System.out.println("转码之后的数据:" + URLEncoder.encode(v_data, "utf-8"));
		log.info("上游秘钥:" + pmsBusinessPos.getKek());
		md5.hmac_Md5(v_mid + v_data1, pmsBusinessPos.getKek());
		byte[] b = md5.getDigest();
		String v_mac = Md5.stringify(b);
		String queryString = "v_mid=" + v_mid + "&v_data=" + v_data + "&v_mac=" + v_mac + "&v_version=" + v_version;
		this.log.info("上送的参数:" + queryString);
		String path = "http://pay.yizhifubj.com/merchant/virement/mer_payment_submit_utf8.jsp" + "?" + queryString;
		this.log.info("重定向 第三方：" + path);
		Client cc = Client.create();
		WebResource rr = cc.resource("http://pay.yizhifubj.com/merchant/virement/mer_payment_submit_utf8.jsp");
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
		queryParams.add("v_mid", v_mid); // 商户编号
		queryParams.add("v_data", java.net.URLEncoder.encode(v_data, "GBK"));
		queryParams.add("v_version", v_version);
		queryParams.add("v_mac", v_mac);
		log.info("向上游发送的数据:" + queryParams);
		String xml = rr.queryParams(queryParams).get(String.class);
		// String xml = HttpUtil.sendPost(path);
		// String
		// xml=HttpUtil.sendPost(daifu_URL,result);
		this.log.info("返回的xml数据:" + xml);

		StringReader read = new StringReader(xml);

		InputSource source = new InputSource(read);

		SAXBuilder sb = new SAXBuilder();

		Document doc = sb.build(source);

		Element root = doc.getRootElement();
		this.log.info("根元素名称:" + root.getName());
		this.log.info("状态值" + root.getChild("status").getText());
		String status = root.getChild("status").getText();
		log.info("代付状态:" + status);
		if ("0".equals(status)) {
			String v_mid1 = pmsBusinessPos.getBusinessnum();
			String v_data0 = payRequest.getV_identity();
			Md5 md51 = new Md5("");
			log.info("转码之后的数据:" + URLEncoder.encode(v_data0, "utf-8"));
			md51.hmac_Md5(v_mid + URLEncoder.encode(v_data0, "utf-8"), pmsBusinessPos.getKek());
			byte[] b0 = md51.getDigest();
			String v_mac1 = Md5.stringify(b0);
			String queryString1 = "v_mid=" + v_mid1 + "&v_data=" + v_data0 + "&v_mac=" + v_mac1;
			this.log.info("上送的参数:" + queryString1);
			String path1 = "http://pay.yizhifubj.com/merchant/virement/mer_payment_status_utf8.jsp" + "?"
					+ queryString1;
			this.log.info("重定向 第三方：" + path1);
			Client cc1 = Client.create();
			WebResource rr1 = cc1.resource("http://pay.yizhifubj.com/merchant/virement/mer_payment_status_utf8.jsp");
			MultivaluedMap<String, String> queryParams1 = new MultivaluedMapImpl();
			queryParams1.add("v_mid", v_mid1); // 商户编号
			queryParams1.add("v_data", java.net.URLEncoder.encode(v_data0, "GBK"));
			queryParams1.add("v_mac", v_mac1);
			log.info("向上游发送的数据:" + queryParams1);
			String xml1 = rr1.queryParams(queryParams1).get(String.class);

			this.log.info("返回的xml数据:" + xml1);

			StringReader read1 = new StringReader(xml1);

			InputSource source1 = new InputSource(read1);

			SAXBuilder sb1 = new SAXBuilder();

			Document doc1 = sb1.build(source1);

			Element root1 = doc1.getRootElement();
			this.log.info("根元素名称:" + root1.getName());

			this.log.info("状态值" + root1.getChild("status").getText());
			String status1 = root1.getChild("status").getText();
			log.info("查询代付状态:" + status1);
			if ("1".equals(status1)) {
				surAmount = Double.valueOf(b2.subtract(b4).doubleValue());
				this.log.info("剩余可用额度:" + surAmount.toString());
				merchantinfo.setPositionT1(surAmount.toString());
				int num = this.pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
				if (num > 0) {
					this.log.info("状态描述" + root1.getChild("statusdesc").getText());
					result.put("status", status1);
					result.put("statusdesc", "已成功");
				}
			} else if ("0".equals(status1)) {
				surAmount = Double.valueOf(b2.subtract(b4).doubleValue());
				this.log.info("剩余可用额度:" + surAmount.toString());
				merchantinfo.setPositionT1(surAmount.toString());
				int num = this.pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
				if (num > 0) {
					this.log.info("状态描述" + root1.getChild("statusdesc").getText());
					result.put("status", status1);
					result.put("statusdesc", "未处理");
				}
			} else if ("2".equals(status1)) {
				surAmount = Double.valueOf(b2.subtract(b4).doubleValue());
				this.log.info("剩余可用额度:" + surAmount.toString());
				merchantinfo.setPositionT1(surAmount.toString());
				int num = this.pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
				if (num > 0) {
					this.log.info("状态描述" + root1.getChild("statusdesc").getText());
					result.put("status", status1);
					result.put("statusdesc", "处理中");
				}
			} else if ("4".equals(status1)) {
				surAmount = Double.valueOf(b2.subtract(b4).doubleValue());
				this.log.info("剩余可用额度:" + surAmount.toString());
				merchantinfo.setPositionT1(surAmount.toString());
				int num = this.pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
				if (num > 0) {
					this.log.info("状态描述" + root1.getChild("statusdesc").getText());
					result.put("status", status1);
					result.put("statusdesc", "待处理");
				}
			} else {

				b3 = new BigDecimal(merchantinfo.getPoundage());
				this.log.info("每笔代付手续费:" + b3);
				surAmount = Double.parseDouble(merchantinfo.getPositionT1())
						- Double.parseDouble(merchantinfo.getPoundage()) * 100;
				this.log.info("剩余额度:" + surAmount);
				merchantinfo.setPositionT1(surAmount.toString());
				int num = this.pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
				if (num > 0) {
					this.log.info("代付查询后失败");
					result.put("status", "3");
					result.put("statusdesc", "代付失败");
				}

			}
		} else {
			b3 = new BigDecimal(merchantinfo.getPoundage());
			this.log.info("每笔代付手续费:" + b3);
			surAmount = Double.parseDouble(merchantinfo.getPositionT1())
					- Double.parseDouble(merchantinfo.getPoundage()) * 100;
			this.log.info("剩余额度:" + surAmount);
			merchantinfo.setPositionT1(surAmount.toString());
			int num = this.pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
			if (num > 0) {
				this.log.info("代付后失败");
				result.put("status", "3");
				result.put("statusdesc", "代付失败");
			}
		}
		return result;

	}

	/**
	 * 江苏电商代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 * @throws Exception
	 */
	public Map<String, String> jsdsAccounts(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) throws Exception {
		DecimalFormat df1 = new DecimalFormat("######0"); //四色五入转换成整数
		BigDecimal payAmt=new BigDecimal(payRequest.getV_amount()).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
		BigDecimal b1 = new BigDecimal(payRequest.getV_amount());
		// 判断交易类型
		if (payRequest.getV_type().equals("0")) {
			JsdsRequestDto req = new JsdsRequestDto();
			req.setMerchantCode(pmsBusinessPos.getBusinessnum());// 平台商户编号
			req.setTerminalCode(pmsBusinessPos.getPosnum());// 平台商户终端编号
			req.setTransDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));// 交易日期（YYYYMMDD）
			req.setTransTime(new SimpleDateFormat("HHmmss").format(new Date()));// 交易时间（HH24mmss）
			req.setOrderNum(payRequest.getV_batch_no());// 合作商订单号，全局唯一
			req.setAccountName(payRequest.getV_realName());// 收款人账户名
			req.setBankCard(payRequest.getV_cardNo());// 收款人账户号
			req.setBankName(payRequest.getV_bankname());// 收款人账户开户行名称
			req.setBankLinked(payRequest.getV_pmsBankNo());// 收款人账户开户行联行号
			req.setTransMoney(df1.format(payAmt));// 交易金额
			HashMap<String, String> params = JsdsUtil.beanToMap(req);
			String str = HttpUtil.parseParams(params);
			log.info("str:" + str);
			byte[] a = RSAUtil.encrypt(pmsBusinessPos.getKek(), str.getBytes());
			String sign = RSAUtil.base64Encode(a);
			log.info("加密结果:" + RSAUtil.base64Encode(a));
			JsdsRequestDto requestDto = new JsdsRequestDto();
			requestDto.setGroupId(pmsBusinessPos.getDepartmentnum());
			requestDto.setService("SMZF008");
			requestDto.setSignType("RSA");
			requestDto.setSign(sign);
			requestDto.setDatetime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			log.info("上送参数：" + JSON.toJSON(requestDto));
			// 返回的数据
			Map<String, String> results = this.sends(requestDto);
			log.info("上游返回的数据:" + JSON.toJSON(results));
			result.put("v_mid", payRequest.getV_mid());
			result.put("v_batch_no", payRequest.getV_batch_no());
			result.put("v_code", "00");
			result.put("v_msg", "请求成功");
			result.put("v_sum_amount", payRequest.getV_sum_amount());
			result.put("v_amount", payRequest.getV_amount());
			log.info("pl_code:" + results.get("pl_code"));
			if (results.get("msg").equals("1")) {
				UpdateDaifu(payRequest.getV_batch_no(), "200");
				this.updateSelect(req, result, merchantinfo);
			} else {

				if ("0000".equals(results.get("pl_code"))) {
					
					if ("0".equals(payRequest.getV_type())) {
						result.put("v_type", "0");
					}
					if ("1".equals(payRequest.getV_type())) {
						result.put("v_type", "1");
					}
					result.put("v_identity", payRequest.getV_identity() == null ? "" : payRequest.getV_identity());
					result.put("v_time", UtilDate.getDateFormatter());
					log.info("1进来了！");
					// 解析签名
					String baseSign = URLDecoder.decode(results.get("pl_sign"), "UTF-8");
					baseSign = baseSign.replace(" ", "+");
					byte[] b = RSAUtil.verify(pmsBusinessPos.getKek(), RSAUtil.base64Decode(baseSign));
					String signs = new String(b);
					log.info("signs:" + signs);
					String[] signs1 = signs.split("&");
					log.info("signs1" + signs1);
					if ("pl_transState=1".equals(signs1[3])) {
						UpdateDaifu(payRequest.getV_batch_no(), "00");
						result.put("v_status", "0000");
						result.put("v_status_msg", "代付成功");
					} else if ("pl_transState=3".equals(signs1[3])) {
						result.put("v_status", "200");
						result.put("v_status_msg", "处理中");
						UpdateDaifu(payRequest.getV_batch_no(), "200");
					}else {
						log.info("又失败了3");
						result.put("v_status", "1001");
						result.put("v_status_msg", "代付失败");
						UpdateDaifu(payRequest.getV_batch_no(), "01");
						Map<String, String> map =new HashMap<>();
						map.put("machId",payRequest.getV_mid());
						map.put("payMoney",Double.parseDouble(payRequest.getV_sum_amount())*100+Double.parseDouble(merchantinfo.getPoundage())*100+"");
						int nus =updataPay(map);
						if (nus == 1) {
							log.info("加款成功！！");
							if(nus==1) {
				 				log.info("oem汇聚代付补款成功");
				 				DaifuRequestEntity entity =new DaifuRequestEntity();
				 				entity.setV_mid(payRequest.getV_mid());
				 				entity.setV_batch_no(payRequest.getV_batch_no()+"/A");
				 				entity.setV_amount(payRequest.getV_sum_amount());
				 				entity.setV_sum_amount(payRequest.getV_sum_amount());
				 				entity.setV_identity(payRequest.getV_identity());
				 				entity.setV_cardNo(payRequest.getV_cardNo());
				 				entity.setV_city(payRequest.getV_city());
				 				entity.setV_province(payRequest.getV_province());
				 				entity.setV_type("0");
				 				entity.setV_pmsBankNo(payRequest.getV_pmsBankNo());
								int ii =add(entity, merchantinfo, result, "00");
								log.info("oem汇聚补款订单状态："+ii);
				 			}
							
						}
						
					}

				} else {
					log.info("2进来了！");
					result.put("v_status", "1001");
					result.put("v_status_msg", "代付失败");
					UpdateDaifu(payRequest.getV_batch_no(), "01");
					Map<String, String> map =new HashMap<>();
					map.put("machId",payRequest.getV_mid());
					map.put("payMoney",Double.parseDouble(payRequest.getV_sum_amount())*100+Double.parseDouble(merchantinfo.getPoundage())*100+"");
					int nus =updataPay(map);
					if (nus == 1) {
						log.info("加款成功！！");
						if(nus==1) {
			 				log.info("oem汇聚代付补款成功");
			 				DaifuRequestEntity entity =new DaifuRequestEntity();
			 				entity.setV_mid(payRequest.getV_mid());
			 				entity.setV_batch_no(payRequest.getV_batch_no()+"/A");
			 				entity.setV_amount(payRequest.getV_sum_amount());
			 				entity.setV_sum_amount(payRequest.getV_sum_amount());
			 				entity.setV_identity(payRequest.getV_identity());
			 				entity.setV_cardNo(payRequest.getV_cardNo());
			 				entity.setV_city(payRequest.getV_city());
			 				entity.setV_province(payRequest.getV_province());
			 				entity.setV_type("0");
			 				entity.setV_pmsBankNo(payRequest.getV_pmsBankNo());
							int ii =add(entity, merchantinfo, result, "00");
							log.info("oem汇聚补款订单状态："+ii);
			 			}
						
					}
					
				}
			}
		} else if (payRequest.getV_type().equals("1")) {
			JsdsRequestDto req = new JsdsRequestDto();
			req.setMerchantCode(pmsBusinessPos.getBusinessnum());// 平台商户编号
			req.setTerminalCode(pmsBusinessPos.getPosnum());// 平台商户终端编号
			req.setTransDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));// 交易日期（YYYYMMDD）
			req.setTransTime(new SimpleDateFormat("HHmmss").format(new Date()));// 交易时间（HH24mmss）
			req.setOrderNum(payRequest.getV_batch_no());// 合作商订单号，全局唯一
			req.setAccountName(payRequest.getV_realName());// 收款人账户名
			req.setBankCard(payRequest.getV_cardNo());// 收款人账户号
			req.setBankName(payRequest.getV_bankname());// 收款人账户开户行名称
			req.setBankLinked(payRequest.getV_pmsBankNo());// 收款人账户开户行联行号
			req.setTransMoney(df1.format(payAmt));// 交易金额
			HashMap<String, String> params = JsdsUtil.beanToMap(req);
			String str = HttpUtil.parseParams(params);
			log.info("str:" + str);
			byte[] a = RSAUtil.encrypt(pmsBusinessPos.getKek(), str.getBytes());
			String sign = RSAUtil.base64Encode(a);
			log.info("加密结果:" + RSAUtil.base64Encode(a));
			JsdsRequestDto requestDto = new JsdsRequestDto();
			requestDto.setGroupId(pmsBusinessPos.getDepartmentnum());
			requestDto.setService("SMZF009");
			requestDto.setSignType("RSA");
			requestDto.setSign(sign);
			requestDto.setDatetime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			log.info("上送参数：" + JSON.toJSON(requestDto));
			// 返回的数据
			Map<String, String> results = this.sends(requestDto);
			log.info("上游返回的数据:" + JSON.toJSON(results));
			result.put("v_mid", payRequest.getV_mid());
			result.put("v_batch_no", payRequest.getV_batch_no());
			result.put("v_code", "00");
			result.put("v_msg", "请求成功");
			result.put("v_sum_amount", payRequest.getV_sum_amount());
			result.put("v_amount", payRequest.getV_amount());
			if (results.get("msg").equals("1")) {
				log.info("1未知结果进来了！！！");
				UpdateDaifu(payRequest.getV_batch_no(), "200");
			} else {
				if ("0000".equals(results.get("pl_code"))) {
					log.info("1进来了！");
					// 解析签名
					String baseSign = URLDecoder.decode(results.get("pl_sign"), "UTF-8");
					baseSign = baseSign.replace(" ", "+");
					byte[] b = RSAUtil.verify(pmsBusinessPos.getKek(), RSAUtil.base64Decode(baseSign));
					String signs = new String(b);
					log.info("signs:" + signs);
					String[] signs1 = signs.split("&");
					log.info("signs1" + signs1);
					if ("pl_transState=1".equals(signs1[3])) {
						UpdateDaifu(payRequest.getV_batch_no(), "00");
						result.put("v_status", "0000");
						result.put("v_status_msg", "代付成功");
					} else if ("pl_transState=3".equals(signs1[3])) {
						UpdateDaifu(payRequest.getV_batch_no(), "200");
						result.put("v_status", "200");
						result.put("v_status_msg", "处理中");
					} else {
						log.info("又失败了3");
						result.put("v_status", "1001");
						result.put("v_status_msg", "代付失败");
						UpdateDaifu(payRequest.getV_batch_no(), "02");
						Map<String, String> map =new HashMap<>();
						map.put("machId",payRequest.getV_mid());
						map.put("payMoney",Double.parseDouble(payRequest.getV_sum_amount())*100+Double.parseDouble(merchantinfo.getPoundage())*100+"");
						int nus =updataPayT1(map);
						if (nus == 1) {
							log.info("加款成功！！");
							if(nus==1) {
				 				log.info("oem汇聚代付补款成功");
				 				DaifuRequestEntity entity =new DaifuRequestEntity();
				 				entity.setV_mid(payRequest.getV_mid());
				 				entity.setV_batch_no(payRequest.getV_batch_no()+"/A");
				 				entity.setV_amount(payRequest.getV_sum_amount());
				 				entity.setV_sum_amount(payRequest.getV_sum_amount());
				 				entity.setV_identity(payRequest.getV_identity());
				 				entity.setV_cardNo(payRequest.getV_cardNo());
				 				entity.setV_city(payRequest.getV_city());
				 				entity.setV_province(payRequest.getV_province());
				 				entity.setV_type("1");
				 				entity.setV_pmsBankNo(payRequest.getV_pmsBankNo());
								int ii =add(entity, merchantinfo, result, "00");
								log.info("oem汇聚补款订单状态："+ii);
				 			}
						}
						
					}
				} else {
					log.info("2进来了！");
					result.put("v_status", "1001");
					result.put("v_status_msg", "代付失败");
					UpdateDaifu(payRequest.getV_batch_no(), "01");
					Map<String, String> map =new HashMap<>();
					map.put("machId",payRequest.getV_mid());
					map.put("payMoney",Double.parseDouble(payRequest.getV_sum_amount())*100+Double.parseDouble(merchantinfo.getPoundage())*100+"");
					int nus =updataPayT1(map);
					if (nus == 1) {
						log.info("加款成功！！");
						if(nus==1) {
			 				log.info("oem汇聚代付补款成功");
			 				DaifuRequestEntity entity =new DaifuRequestEntity();
			 				entity.setV_mid(payRequest.getV_mid());
			 				entity.setV_batch_no(payRequest.getV_batch_no()+"/A");
			 				entity.setV_amount(payRequest.getV_sum_amount());
			 				entity.setV_sum_amount(payRequest.getV_sum_amount());
			 				entity.setV_identity(payRequest.getV_identity());
			 				entity.setV_cardNo(payRequest.getV_cardNo());
			 				entity.setV_city(payRequest.getV_city());
			 				entity.setV_province(payRequest.getV_province());
			 				entity.setV_type("1");
			 				entity.setV_pmsBankNo(payRequest.getV_pmsBankNo());
							int ii =add(entity, merchantinfo, result, "00");
							log.info("oem汇聚补款订单状态："+ii);
			 			}
					}
					
				}
			}

		}
		return result;
	}

	/**
	 * 江苏电商代付请求方法
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 */
	public Map<String, String> sends(JsdsRequestDto req) {
		log.info("**********************江苏电商-----生成码 开始:");
		HashMap<String, String> param = new HashMap<String, String>();
		HashMap<String, String> params = JsdsUtil.beanToMap(req);
		String paramStr = HttpUtil.parseParams(params);
		log.info("上送字符串：" + paramStr);
		String respJson = HttpURLConection.httpURLConnectionPOST("http://180.96.28.8:8044/TransInterface/TransRequest",
				paramStr);
		log.info("上游返回字符串数据：" + respJson);
		if (respJson != null && respJson != "") {
			json = JSONObject.fromObject(respJson);
			param.put("msg", "0");
			param.putAll(json);
		} else {
			param.put("msg", "1");
		}
		return param;
	}

	/**
	 * 江苏电商代付线程方法
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 */
	public synchronized void updateSelect(JsdsRequestDto reqData, Map<String, String> result,
			PmsMerchantInfo merchantinfo) throws Exception {
		log.info("江苏代付进来了~~~");
		log.info("------------------江苏代付查询reqData参数----------------" + JSON.toJSON(reqData));
		log.info("----------------江苏代付查询merchantinfo参数----------------" + JSON.toJSON(merchantinfo));
		ThreadPool.executor(new JsThread(daifuMerchantInfoService, reqData, JsdsQrCodeService, pmsDaifuMerchantInfoDao,
				pmsMerchantInfoDao, merchantinfo));
		result.put("result", "0001");
		result.put("respMsg", "支付未知,请看平台");
		result.put("pl_service", "cj006");

	}

	/**
	 * 随行付代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 * @throws Exception
	 */
	public Map<String, String> sxfAccounts(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) throws Exception {
		log.info("随行付----下游传送代付参数:" + JSON.toJSON(payRequest));
		BigDecimal b1 = new BigDecimal("0");// 下游上传的总金额
		BigDecimal b2 = new BigDecimal("0");// 系统代付余额
		BigDecimal b3 = new BigDecimal("0");// 单笔交易总手续费
		BigDecimal min = new BigDecimal("0");// 代付最小金额
		BigDecimal max = new BigDecimal("0");// 代付最大金额
		int ii;
		Double surplus = (double) 0;// 代付剩余金额
		Map<String, String> map = new HashMap<>();
		DF1003Request df1003Request = new DF1003Request();
		DF1003Request.PayItems payItems = new DF1003Request.PayItems();
		PayRequsest requsest = new PayRequsest();
		List<DF1003Request.PayItems> list = new ArrayList<>();
		payItems.setPayItemId(payRequest.getV_batch_no());
		payItems.setSeqNo("1");
		payItems.setPayAmt(Double.parseDouble(payRequest.getV_amount()) + "");
		payItems.setActNm(payRequest.getV_realName());
		payItems.setActNo(payRequest.getV_cardNo());
		payItems.setActTyp("01");
		payItems.setBnkCd("ICBC");
		payItems.setBnkNm("中国工商银行");
		payItems.setLbnkNo(payRequest.getV_pmsBankNo());
		payItems.setLbnkNm(payRequest.getV_bankname());
		payItems.setRmk("代付");
		payItems.setSmsFlg("1");
		payItems.setTel(payRequest.getV_phone());
		payItems.setBankPayPurpose("打款");
		log.info("随行付--给上游参数:" + JSON.toJSON(payItems));
		list.add(payItems);
		// ----------------------------------
		df1003Request.setTotalPayCount("1");
		df1003Request.setPayTyp("01");
		df1003Request.setTotalPayAmt(Double.parseDouble(payRequest.getV_amount()) + "");
		df1003Request.setPayItems(list);
		log.info("随行付--给上游表头:" + JSON.toJSON(df1003Request));
		log.info(df1003Request);
		// -------------------------------
		String mercNo = pmsBusinessPos.getBusinessnum();
		log.info("随行付查询上游商户号：" + mercNo);
		requsest.setClientId(mercNo);// "600000000001044" SXFUtil.mercNo
		requsest.setReqId(payRequest.getV_batch_no());
		requsest.setTranCd("DF1003");
		requsest.setVersion("0.0.0.1");
		log.info("随行付--上传上游加密之前参数:" + JsonUtils.toJson(df1003Request));
		try {
			byte[] bs = DESUtils.encrypt(JsonUtils.toJson(df1003Request).getBytes("UTF-8"), "12345678");
			// Base64编码
			String reqDataEncrypt = Base64Utils.encode(bs);
			requsest.setReqData(reqDataEncrypt);
			// String payPubKey =pmsBusinessPos.getKek();//"12345";
			String payPubKey = SXFUtil.PrivateKey; // SXFUtil.mercPrivateKey;payPubKey
			System.out.println(payPubKey);
			// //RSA签名
			requsest.setSign(xdt.dto.sxf.RSAUtils.sign(reqDataEncrypt, payPubKey));
		} catch (Exception e1) {
			e1.printStackTrace();
			result.put("respCode", "0002");
			result.put("respMsg", "向上游加密参数出现异常");
			result.put("msg:0002", "向上游加密参数出现异常");
			return result;
		}
		try {
			String reqStr = JsonUtils.toJson(requsest);
			log.info("随行付----发送上游之前数据：" + JSON.toJSON(reqStr));
			// =====================代付地址
			String url = SXFUtil.payUrl;
			log.info("随行付***给上游发送地址:" + url);
			log.info("随行付***HttpClient ===开始");
			String body = HttpClientUtil.doPost(url, reqStr);
			log.info("随行付***上游返回原始参数:" + JSON.toJSON(body));
			log.info("随行付***HttpClient  ===结束");
			if (body == null || "".equals(body) || "exception".equals(body)) {

				payRequest.setV_batch_no(payRequest.getV_batch_no() + "/A");
				surplus = surplus + Double.parseDouble(payRequest.getV_amount());
				merchantinfo.setPositionT1(surplus.toString());
				ii = add(payRequest, merchantinfo, result, "00");
				map.put("mercId", payRequest.getV_batch_no());
				map.put("payMoney", payRequest.getV_amount());
				int nus = pmsMerchantInfoDao.updataPayT1(map);
				if (nus == 1) {
					log.info("随行付***补款成功");
				}
				result.put("respCode", "0002");
				result.put("respMsg", "请求上游出现异常：" + body);
				return result;
			}
			PayResponse payResponse = JsonUtils.fromJson(body, PayResponse.class);
			log.info("随行付***上游返回解析实体类参数:" + JSON.toJSON(payResponse));
			if (!"000000".equals(payResponse.getResCode())) {
				UpdateDaifu(payRequest.getV_batch_no(), "01");
				payRequest.setV_batch_no(payRequest.getV_batch_no() + "/A");
				surplus = surplus + Double.parseDouble(payRequest.getV_amount());
				merchantinfo.setPositionT1(surplus.toString());
				ii = add(payRequest, merchantinfo, result, "00");
				map.put("mercId", payRequest.getV_batch_no());
				map.put("payMoney", payRequest.getV_amount());
				int nus = pmsMerchantInfoDao.updataPayT1(map);
				if (nus == 1) {
					log.info("随行付***补款成功");
				}
				log.info("随行付代付返回状态吗错误");
				result.put("respCode", "0001");
				result.put("respMsg", "系统错误，代付失败");
				return result;
			}
			String sign = payResponse.getSign();
			String resData = payResponse.getResData();

			String payPreKey = SXFUtil.publicPayKey;// mercPrivateKey,payPreKey
			boolean signFlag = sign != null && StringUtils.isNotBlank(resData)
					&& xdt.dto.sxf.RSAUtils.verify(resData, sign, payPreKey);
			log.info("随行付***结果" + signFlag);
			// if (!signFlag)
			// System.out.println("签名验证失败");//根据实际业务修改
			byte[] base64bs = Base64Utils.decode(resData);

			// DES解密
			byte[] debs = DESUtils.decrypt(base64bs, "12345678");

			String resDataDecrypt = new String(debs, "UTF-8");
			com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSONObject.parseObject(resDataDecrypt);
			log.info("随行付状态:" + json.getString("payResultList"));
			String s = json.getString("payResultList");
			s = s.replace("[", "").replace("]", "");
			com.alibaba.fastjson.JSONObject jsons = com.alibaba.fastjson.JSONObject.parseObject(s);
			log.info("" + jsons.getString("resCd"));
			if (!"00".equals(jsons.getString("resCd"))) {
				log.info("随行付代付错误：" + jsons.getString("resMsg"));
				payRequest.setV_batch_no(payRequest.getV_batch_no() + "/A");
				surplus = surplus + Double.parseDouble(payRequest.getV_amount());
				merchantinfo.setPositionT1(surplus.toString());
				ii = add(payRequest, merchantinfo, result, "00");
				map.put("mercId", payRequest.getV_batch_no());
				map.put("payMoney", payRequest.getV_amount());
				int nus = pmsMerchantInfoDao.updataPayT1(map);
				if (nus == 1) {
					log.info("随行付***补款成功");
				}
				result.put("respCode", "0001");
				result.put("respMsg", jsons.getString("resMsg"));
				return result;
			}
			UpdateDaifu(payRequest.getV_batch_no(), "200");
			result.put("respCode", "0000");
			result.put("respMsg", jsons.getString("resMsg"));
			result.put("payItemId", payRequest.getV_identity());
			result.put("clientId", payRequest.getV_mid());
			result.put("reqId", payRequest.getV_batch_no());
			result.put("payAmt", payRequest.getV_amount());
			ThreadPool.executor(new SxfThread(result, sxfService, pmsBusinessPos, surplus, pmsMerchantInfoDao));
			return result;
		} catch (Exception e1) {
			e1.printStackTrace();
			result.put("respCode", "0002");
			result.put("respMsg", "请求接口出现异常:" + e1.getMessage());
			return result;
		}

	}

	/**
	 * 魔宝代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 */
	public Map<String, String> moAccounts(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo,

			PmsBusinessPos pmsBusinessPos) {
		try {
			Map<String, String> map = new HashMap<>();
			net.sf.json.JSONObject transData = new net.sf.json.JSONObject();
			net.sf.json.JSONObject obj = new net.sf.json.JSONObject();
			StringBuffer str = new StringBuffer();
			// 交易
			transData.put("accName", URLEncoder.encode(payRequest.getV_bankname(), "GBK"));
			// 收款人姓名
			transData.put("accNo", payRequest.getV_cardNo()); // 收款人账号
			transData.put("orderId", payRequest.getV_batch_no()); // 订单号
			transData.put("transAmount", Double.parseDouble(payRequest.getV_amount()) + ""); // 交易金额
			transData.put("transDate", new SimpleDateFormat("YYYYMMddHHmmss").format(new Date())); // 交易日期

			// 私钥证书加密
			String pfxFileName = new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile()
					.getCanonicalPath() + "//ky//"+pmsBusinessPos.getBusinessnum()+".pfx";// "D:\\936775585060000.cer";
			log.info("魔宝代付私钥" + pfxFileName);

			String pfxPassword = "111111";
			LoadKeyFromPKCS12.initPrivateKey(pfxFileName, pfxPassword);

			String transBody = LoadKeyFromPKCS12.PrivateSign(transData.toString());
			log.info("墨宝代付个人信息" + transBody);
			obj.put("transBody", transBody);
			obj.put("businessType", MBUtil.businessType3); // 业务类型
			obj.put("merId", pmsBusinessPos.getBusinessnum()); //
			obj.put("versionId", MBUtil.versionId); // 版本号
			str.append("businessType" + "=" + MBUtil.businessType3)
					.append("&merId" + "=" + pmsBusinessPos.getBusinessnum()).append("&transBody" + "=" + transBody)
					.append("&versionId" + "=" + MBUtil.versionId);
			String signData = MD5Util.MD5Encode(str.toString() + "&key=" + pmsBusinessPos.getKek());
			System.out.println(signData);
			obj.put("signData", signData); // 交易日期
			obj.put("signType", "MD5"); // 版本号

			log.info("魔宝****发送给上游参数：" + obj);
			URL url = new URL(MBUtil.payPUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-Type", "application/json;charset=GBK");
			connection.connect();
			// POST请求
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.write(obj.toString().getBytes("GBK"));
			out.flush();
			out.close();

			// 读取响应
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String lines;
			StringBuffer sb = new StringBuffer("");
			while ((lines = reader.readLine()) != null) {
				lines = new String(lines.getBytes(), "gbk");
				sb.append(lines);
			}
			reader.close();
			// 断开连接
			connection.disconnect();
			System.out.println(sb);
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> map1 = om.readValue(sb.toString(), Map.class);
			log.info("魔宝代付返回状态参数：" + map1);

			if ("00".equals(map1.get("status"))) {
				String data = map1.get("resBody").toString();
				// 公钥证书解密
				String cerFileName = new File(this.getClass().getResource("/").getPath()).getParentFile()
						.getParentFile().getCanonicalPath() + "//ky//"+pmsBusinessPos.getBusinessnum()+".cer";
				byte[] signByte = LoadKeyFromPKCS12.encryptBASE64(data);
				CertVerify.initPublicKey(cerFileName);
				byte[] str1 = CertVerify.publicKeyDecrypt(signByte);
				String string = new String(str1);
				com.alibaba.fastjson.JSONObject jasonObject = com.alibaba.fastjson.JSONObject.parseObject(string);

				log.info("魔宝返回代付状态参数：" + jasonObject);
				Map<String, String> maps = new HashMap<String, String>();
				maps = (Map) jasonObject;
				if ("00".equals(maps.get("refCode"))) {
					UpdateDaifu(payRequest.getV_batch_no(), "00");
					result.put("orderId", maps.get("orderId"));
					result.put("transAmount", payRequest.getV_amount());
					result.put("respCode", (String) map1.get("status"));
					result.put("respMsg", URLDecoder.decode(maps.get("refMsg"), "GBK"));
					result.put("status", maps.get("refCode"));
					result.put("merId", payRequest.getV_mid());
					System.out.println(URLDecoder.decode(maps.get("refMsg"), "GBK"));
				} else {
					log.info("代付失败1！！！");
					UpdateDaifu(payRequest.getV_batch_no(), "01");
					map.put("machId", payRequest.getV_mid());
					map.put("payMoney", payRequest.getV_amount());
					int nus = pmsMerchantInfoDao.updataPay(map);
					if (nus == 1) {
						log.info("魔宝***补款成功");
						payRequest.setV_batch_no(payRequest.getV_batch_no() + "/A");
						PmsMerchantInfo info = select(payRequest.getV_mid());
						merchantinfo.setPosition(info.getPosition());
						int ii = add(payRequest, merchantinfo, result, "00");
						if (ii == 1) {
							log.info("魔宝代付补单成功");
						}
					}
					log.info("魔宝代付返回状态吗错误");
					result.put("v_code", "1001");
					result.put("respMsg", "代付失败");
				}
			} else {
				log.info("代付失败2！！！");
				UpdateDaifu(payRequest.getV_batch_no(), "01");
				payRequest.setV_batch_no(payRequest.getV_batch_no() + "/A");
				map.put("machId", payRequest.getV_mid());
				map.put("payMoney", payRequest.getV_amount());
				int nus = pmsMerchantInfoDao.updataPay(map);
				if (nus == 1) {
					log.info("魔宝***补款成功");
					PmsMerchantInfo info = select(payRequest.getV_mid());
					merchantinfo.setPosition(info.getPosition());
					int ii = add(payRequest, merchantinfo, result, "00");
					if (ii == 1) {
						log.info("魔宝代付补单成功");
					}
				}
				log.info("魔宝代付返回状态吗错误");
				result.put("v_code", "1001");
				result.put("respMsg", "代付失败");

			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}

	/**
	 * 天下支付代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 * @throws IOException
	 */
	public Map<String, String> tfbAccounts(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) throws IOException {

		log.info("下游传送代付参数:" + JSON.toJSON(payRequest));
		BigDecimal b1;// 下游上传的金额
		BigDecimal b2;// 系统代付余额
		BigDecimal b3;// 单笔交易手续费
		BigDecimal min;// 代付最小金额
		BigDecimal max;// 代付最大金额
		Double surplus;// 代付剩余金额
		TreeMap<String, String> paramsMap = new TreeMap<String, String>();
		paramsMap.put("version", "1.0"); // 固定填1.0
		paramsMap.put("spid", pmsBusinessPos.getBusinessnum()); // 填写国采分配的商户号"1800046681"
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String time = format.format(date);
		paramsMap.put("sp_serialno", payRequest.getV_batch_no()); // 商户交易单号，商户保证其在本系统唯一,每次交易入库需要修改订单号
		paramsMap.put("sp_reqtime", time); // 系统发送时间，14位固定长度
		paramsMap.put("tran_amt", Double.parseDouble(payRequest.getV_amount()) * 100 + ""); // 交易金额，单位为分，不带小数点
		paramsMap.put("cur_type", "1"); //
		paramsMap.put("pay_type", "1"); // 普通余额支付填 1；垫资代付填3
		paramsMap.put("acct_name", payRequest.getV_realName()); // 收款人姓名
		paramsMap.put("acct_id", payRequest.getV_cardNo()); // 收款人账号
		if ("1".equals(payRequest.getV_accountType())) {
			paramsMap.put("acct_type", "0"); // 0 借记卡， 1 贷记卡， 2 对公账户
		} else if ("2".equals(payRequest.getV_accountType())) {
			paramsMap.put("acct_type", "1"); // 0 借记卡， 1 贷记卡， 2 对公账户
		} else {
			paramsMap.put("acct_type", "2"); // 0 借记卡， 1 贷记卡， 2 对公账户
		}

		paramsMap.put("mobile", payRequest.getV_phone());
		paramsMap.put("bank_name", payRequest.getV_bankname());
		// paramsMap.put("bank_settle_no", payRequest.getBank_settle_no()); //对私可不值，对公必传
		paramsMap.put("bank_branch_name", payRequest.getV_bankname());
		paramsMap.put("business_type", "20101");
		paramsMap.put("memo", "代付");
		String paramSrc = RequestUtils.getParamSrc(paramsMap);
		log.info("上传上游前生成签名字符串:" + paramSrc);

		String key = pmsBusinessPos.getKek();// "12345";
		log.info("此商户对应上游秘钥:" + key);
		String sign = MD5Utils.sign(paramSrc, key, TFBConfig.serverEncodeType);
		log.info("此商户生成签名:" + sign);
		paramSrc = paramSrc + "&sign=" + sign;
		log.info("加上签名之后的数据:" + paramSrc);
		// ------------------------

		String url = new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile()
				.getCanonicalPath() + "/ky" + TFBConfig.GC_PUBLIC_KEY_PATH;
		log.info("url:" + url);
		String cipherData = RequestUtils.encrypt(paramSrc.toString(), url);
		System.out.println("加密结果:" + cipherData);

		System.out.println("发起请求--------------------------------------------");
		String applyResponse = RequestUtils.doPost(TFBConfig.payUrl, "cipher_data=" + URLEncoder.encode(cipherData),
				"UTF-8");
		log.info("代付返回数据1:" + JSON.toJSON(applyResponse));
		// -----------------------------------------------
		String cipherResponseData = RequestUtils.parseXml(applyResponse);
		return paramsMap;

	}

	/**
	 * 汇付宝代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 * @throws Exception
	 */
	public Map<String, String> hfbAccounts(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) throws Exception {

		log.info("下游传送代付参数:" + JSON.toJSON(payRequest));
		HeepayClient2 transferClient;
		Map<String, String> map = new HashMap<>();
		Map<String, String> maps = new HashMap<>();// 填金
		Double surplus = Double.parseDouble(merchantinfo.getPositionT1());// 代付剩余金额
		if (Double.parseDouble(payRequest.getV_amount()) < 50000) {
			transferClient = new HeepayClient2("https://open.heepay.com/transferSmallApply.do", HfbUtil.transferKey);// pmsBusinessPos
		} else {
			transferClient = new HeepayClient2("https://open.heepay.com/transferLargeApply.do", HfbUtil.transferKey);// pmsBusinessPos
		}
		map.put("merchantPayNo", payRequest.getV_identity()); // 商户付款流水号
		map.put("bankId", payRequest.getV_bankname()); // 收款方银行ID，参考对应接口或文档
		map.put("publicFlag", "0"); // 对公对私，0=对私，1=对公
		map.put("bankcardNo", payRequest.getV_cardNo()); // 收款方银行卡号
		map.put("ownerName", payRequest.getV_realName()); // 收款方持卡人姓名
		map.put("amount", Double.parseDouble(payRequest.getV_amount()) + ""); // 转账金额
		map.put("reason", "上游厂商结算款"); // 转账理由或描述，参考对应接口或文档
		map.put("province", payRequest.getV_province()); // 收款方开户省，参考对应接口或文档
		map.put("city", payRequest.getV_city()); // 收款方开户市，参考对应接口或文档
		map.put("bankName", payRequest.getV_bankname()); // 收款方开户支行名称
		List<Map> transferDetails = new ArrayList<>();
		transferDetails.add(map);
		System.out.println("来了:" + JSONArray.fromObject(transferDetails).toString());
		String timeStamp = String.valueOf(new Date().getTime());

		String cipher;
		cipher = Desede.encodeECB(JSON.toJSONString(transferDetails), HfbUtil.transferKey.substring(0, 24));

		Map<String, String> req = new HashMap<>();
		req.put("merchantId", pmsBusinessPos.getBusinessnum().substring(0, 6));// pmsBusinessPos
																				// //商户IDHfbUtil.merchantId
		req.put("merchantBatchNo", payRequest.getV_batch_no()); // 商户转账批次号
		req.put("batchAmount", Double.parseDouble(payRequest.getV_amount()) + ""); // 商户转账总金额
		req.put("batchNum", "1"); // 商户转账总笔数
		req.put("intoAccountDay", payRequest.getV_type()); // 到账日期 0=当日，1=次日
		req.put("transferDetails", cipher); // 转账详情
		req.put("requestTime", timeStamp); // 请求时间
		req.put("version", "2.0"); // 请求版本
		req.put("notifyUrl", HfbUtil.notifyUrls); // 通知地址，不需要通知则传空字符串
		log.info("上传前的参数：" + req);
		String retStr = transferClient.execute(req);
		System.out.println("转账申请，返回" + retStr);
		com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSONObject.parseObject(retStr);

		if ("1".equals(json.getString("retCode"))) {
			result.put("respCode", "00");
			result.put("respMsg", "请求成功，状态查看异步！");
			result.put("merchantId", payRequest.getV_mid());
			result.put("merchantBatchNo", payRequest.getV_batch_no());
			result.put("amount", payRequest.getV_amount());
		} else {
			result.put("respCode", "01");
			result.put("respMsg", "请求失败！");
			result.put("merchantId", payRequest.getV_mid());
			result.put("merchantBatchNo", payRequest.getV_batch_no());
			result.put("amount", payRequest.getV_amount());
			UpdateDaifu(payRequest.getV_batch_no(), "01");
			maps.put("payMoney", payRequest.getV_amount());
			maps.put("machId", payRequest.getV_mid());
			int nus = pmsMerchantInfoDao.updataPayT1(maps);
			if (nus == 1) {
				log.info("汇付宝***补款成功");
				surplus = surplus + Double.parseDouble(payRequest.getV_amount());
				merchantinfo.setPositionT1(surplus.toString());
				payRequest.setV_batch_no(payRequest.getV_batch_no() + "/A");
				int id = add(payRequest, merchantinfo, result, "00");
				if (id == 1) {
					log.info("汇付宝代付补单成功");
				}
			}
		}
		return result;

	}

	/**
	 * 汇聚代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 */
	public Map<String, String> hjPay(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) {
		try {
			Map<String, String> map = new HashMap<>();
			Map<String, String> maps = new HashMap<>();// 填金
			StringBuilder str = new StringBuilder();
			String s = "CJZF" + payRequest.getV_identity() + "|" + payRequest.getV_realName() + "|"
					+ payRequest.getV_cardNo() + "|" + Double.parseDouble(payRequest.getV_amount()) + "||";
			s += payRequest.getV_city() + "|0|2|" + payRequest.getV_pmsBankNo();
			str.append(pmsBusinessPos.getBusinessnum());
			str.append(payRequest.getV_batch_no());
			str.append(s);
			str.append("3");
			log.info("汇聚待签名数据:" + str.toString());
			String hmac = DigestUtils.md5Hex(str.toString() + pmsBusinessPos.getKek());
			map.put("p1_MerchantNo", pmsBusinessPos.getBusinessnum());
			map.put("p2_BatchNo", payRequest.getV_batch_no());
			map.put("p3_Details", URLEncoder.encode(s, "utf-8"));
			map.put("p4_ProductType", "3");
			map.put("hmac", URLEncoder.encode(hmac, "utf-8"));
			TreeMap<String, String> paramsMap = new TreeMap<>();
			paramsMap.putAll(map);
			log.info("汇聚代付上传参数前数据:" + JSON.toJSONString(paramsMap));
			HttpService HT = new HttpService();
			String retuString = HT.POSTReturnString(HJUtil.pay, paramsMap, MBUtil.codeG);
			log.info("汇聚返回字符串参数：" + retuString);
			HJPayResponse payResponse = JsonUtils.fromJson(retuString, HJPayResponse.class);
			log.info("汇聚代付返回参数:" + JSON.toJSONString(payResponse));
			HJPayRequest hjPayRequest = new HJPayRequest();
			hjPayRequest.setMerchantNo(payRequest.getV_mid());
			hjPayRequest.setBatchNo(payRequest.getV_batch_no());
			hjPayRequest.setAmount(Double.parseDouble(payRequest.getV_sum_amount()) * 100 + "");
			hjPayRequest.setPmsbankno(payRequest.getV_pmsBankNo());
			hjPayRequest.setIdentity("CJZF" + payRequest.getV_identity());
			result.put("v_mid", payRequest.getV_mid());
			result.put("v_batch_no", payRequest.getV_batch_no());

			result.put("v_sum_amount", payRequest.getV_sum_amount());
			result.put("v_amount", payRequest.getV_amount());
			if ("0".equals(payRequest.getV_type())) {
				result.put("v_type", "0");
			}
			if ("1".equals(payRequest.getV_type())) {
				result.put("v_type", "1");
			}
			result.put("v_identity", payRequest.getV_identity() == null ? "" : payRequest.getV_identity());
			result.put("v_time", UtilDate.getDateFormatter());
			Double d;
			BigDecimal b1 = new BigDecimal("0");// 总金额
			BigDecimal PayFree = new BigDecimal("0");
			b1 = new BigDecimal(hjPayRequest.getAmount());
			if (!"".equals(merchantinfo.getPoundageFree()) && merchantinfo.getPoundageFree() != null) {
				PayFree = new BigDecimal(merchantinfo.getPoundageFree()).divide(new BigDecimal("100"));
				d = b1.multiply(PayFree).doubleValue();// .setScale(1)
			} else {
				d = 0.0;
			}
			BigDecimal b3 = new BigDecimal(merchantinfo.getPoundage());
			log.info("汇聚----系统商户代付单笔手续费:" + b3.doubleValue());
			Double shouxufei =d+b3.doubleValue()*100;//总的代付手续费
			if ("100".equals(payResponse.getRb_Code()) || "102".equals(payResponse.getRb_Code())) {
				result.put("v_code", "00");
				result.put("v_msg", "请求成功");
				ThreadPool.executor(new HJThread2(pmsMerchantInfoDao, ihjService, hjPayRequest, pmsBusinessPos,shouxufei));
			} else {
				result.put("v_code", "00");
				result.put("v_msg", "请求成功");
				result.put("v_status", "1001");
				result.put("v_status_msg", "代付失败");
				UpdateDaifu(hjPayRequest.getBatchNo(), "02");
	
				
				maps.put("payMoney",(Double.parseDouble(hjPayRequest.getAmount())+shouxufei)+"");
				maps.put("machId", hjPayRequest.getMerchantNo());
				int nus = pmsMerchantInfoDao.updataPay(maps);
				if (nus == 1) {
					log.info("汇聚***补款成功");
					hjPayRequest.setBatchNo(hjPayRequest.getBatchNo() + "/A");
					int id = ihjService.add(hjPayRequest, merchantinfo, result, "00");
					if (id == 1) {
						log.info("汇聚代付补单成功");
					}
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}
	/**
	 * 汇聚代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 */
	public Map<String, String> hjPays(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) {
			Map<String, String> maps=new HashMap<>();
			Map<String, String> map = new HashMap<>();
			StringBuilder str = new StringBuilder();
			map.put("userNo", pmsBusinessPos.getBusinessnum());
			str.append(map.get("userNo"));
			if("0".equals(payRequest.getV_type())) {
				map.put("productCode", "BANK_PAY_MAT_ENDOWMENT_ORDER");//任意付
				str.append(map.get("productCode"));
			}else if("1".equals(payRequest.getV_type())) {
				map.put("productCode", "BANK_PAY_DAILY_ORDER");//朝夕付
				str.append(map.get("productCode"));
			}else {
				result.put("v_code", "01");
				result.put("v_msg", "v_cardType参数填写有误");
				return result;
			}
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			map.put("requestTime",format.format(new Date()));
			str.append(map.get("requestTime"));
			map.put("merchantOrderNo", payRequest.getV_batch_no());
			str.append(map.get("merchantOrderNo"));
			map.put("receiverAccountNoEnc", payRequest.getV_cardNo());
			str.append(map.get("receiverAccountNoEnc"));
			map.put("receiverNameEnc", payRequest.getV_realName());
			str.append(map.get("receiverNameEnc"));
			if("1".equals(payRequest.getV_cardType())) {
				map.put("receiverAccountType", "201");//对私
				str.append(map.get("receiverAccountType"));
			}else if("2".equals(payRequest.getV_cardType())) {
				map.put("receiverAccountType", "204");//对公
				str.append(map.get("receiverAccountType"));
			}else {
				result.put("v_code", "01");
				result.put("v_msg", "v_cardType参数填写有误");
				return result;
			}
			map.put("receiverBankChannelNo",payRequest.getV_pmsBankNo()==null?"":payRequest.getV_pmsBankNo());//对公必填对私不填
			str.append(map.get("receiverBankChannelNo"));
			map.put("paidAmount", payRequest.getV_sum_amount());
			str.append(map.get("paidAmount"));
			map.put("currency", "201");
			str.append(map.get("currency"));
			map.put("isChecked", "202");
			str.append(map.get("isChecked"));
			map.put("paidDesc", "代付");
			str.append(map.get("paidDesc"));
			map.put("paidUse", "202");//202活动经费 
			str.append(map.get("paidUse"));
			map.put("callbackUrl", xdt.dto.transfer_accounts.util.PayUtil.hjNotifyUrl);
			str.append(map.get("callbackUrl"));
			log.info("生成签名前参数:"+str);
			String hmac = DigestUtils.md5Hex(str.toString() + pmsBusinessPos.getKek());
			try {
				map.put("hmac", URLEncoder.encode(hmac, "utf-8"));
			} catch (UnsupportedEncodingException e) {
				log.info("汇聚加密签名异常:"+e);
				e.printStackTrace();
				result.put("v_code", "16");
				result.put("v_msg", "系统错误异常");
				try {
					UpdateDaifu(payRequest.getV_batch_no(), "02");
				} catch (Exception e1) {
					log.info("汇聚"+payRequest.getV_batch_no()+"订单代付修改状态异常:"+e1);
					e.printStackTrace();
				}
				int nus = 0;
				maps.put("payMoney", payRequest.getV_amount());
				maps.put("machId", payRequest.getV_mid());
				if("0".equals(payRequest.getV_type())) {
					 nus = pmsMerchantInfoDao.updataPay(maps);
				}else if("1".equals(payRequest.getV_type())) {
					 nus = pmsMerchantInfoDao.updataPayT1(maps);
				}
				if (nus == 1) {
					log.info("汇聚***补款成功");
					payRequest.setV_batch_no(payRequest.getV_batch_no() + "/A");
					int id=1;
					try {
						id = add(payRequest, merchantinfo, result, "00");
					} catch (Exception e2) {
						log.info("添加代付失败订单"+payRequest.getV_batch_no()+"异常:"+e2);
						e.printStackTrace();
					}
					if (id == 1) {
						log.info("汇聚代付补单成功");
					}
				}
				return result;
			}
			TreeMap<String, String> paramsMap = new TreeMap<>();
			paramsMap.putAll(map);
			log.info("汇聚代付上传参数前数据:" + JSON.toJSONString(paramsMap));
			String retuString="";
			try {
				retuString = RequestUtils.sendPost(HJUtil.xinPay, JSON.toJSONString(map),"UTF-8");
			} catch (Exception e) {
				log.info("汇聚请求异常："+e);
				e.printStackTrace();
				result.put("v_code", "16");
				result.put("v_msg", "系统请求上游异常");
				try {
					UpdateDaifu(payRequest.getV_batch_no(), "02");
				} catch (Exception e1) {
					log.info("汇聚"+payRequest.getV_batch_no()+"订单代付修改状态异常:"+e1);
					e.printStackTrace();
				}
				int nus = 0;
				maps.put("payMoney", payRequest.getV_amount());
				maps.put("machId", payRequest.getV_mid());
				if("0".equals(payRequest.getV_type())) {
					 nus = pmsMerchantInfoDao.updataPay(maps);
				}else if("1".equals(payRequest.getV_type())) {
					 nus = pmsMerchantInfoDao.updataPayT1(maps);
				}
				if (nus == 1) {
					log.info("汇聚***补款成功");
					payRequest.setV_batch_no(payRequest.getV_batch_no() + "/A");
					int id=1;
					try {
						id = add(payRequest, merchantinfo, result, "00");
					} catch (Exception e2) {
						log.info("添加代付失败订单"+payRequest.getV_batch_no()+"异常:"+e2);
						e.printStackTrace();
					}
					if (id == 1) {
						log.info("汇聚代付补单成功");
					}
				}
				return result;
			}
			log.info("汇聚返回字符串参数：" + JSON.toJSONString(retuString));
			com.alibaba.fastjson.JSONObject json =com.alibaba.fastjson.JSONObject.parseObject(retuString);
			if("2002".equals(json.getString("statusCode"))) {
				com.alibaba.fastjson.JSONObject json1 =com.alibaba.fastjson.JSONObject.parseObject(json.getString("data"));
				result.put("v_code", "15");
				result.put("v_msg", json1.getString("errorDesc"));
				try {
					UpdateDaifu(payRequest.getV_batch_no(), "02");
				} catch (Exception e) {
					log.info("汇聚"+payRequest.getV_batch_no()+"订单代付修改状态异常:"+e);
					e.printStackTrace();
				}
				int nus = 0;
				maps.put("payMoney", payRequest.getV_amount());
				maps.put("machId", payRequest.getV_mid());
				if("0".equals(payRequest.getV_type())) {
					 nus = pmsMerchantInfoDao.updataPay(maps);
				}else if("1".equals(payRequest.getV_type())) {
					 nus = pmsMerchantInfoDao.updataPayT1(maps);
				}
				if (nus == 1) {
					log.info("汇聚***补款成功");
					payRequest.setV_batch_no(payRequest.getV_batch_no() + "/A");
					int id=1;
					try {
						id = add(payRequest, merchantinfo, result, "00");
					} catch (Exception e) {
						log.info("添加代付失败订单"+payRequest.getV_batch_no()+"异常:"+e);
						e.printStackTrace();
					}
					if (id == 1) {
						log.info("汇聚代付补单成功");
					}
				}
			}else {
				result.put("v_code", "00");
				result.put("v_msg", "受理成功");
			}
		return result;
	}
	/**
	 * 合利宝借记卡代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 */
	public Map<String, String> settlementCardWithdraw(DaifuRequestEntity hlbRequest, Map<String, String> result) {
		System.out.println("结算(借记卡)卡提现来了！");
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		PmsBusinessPos pmsBusinessPos = selectKey(hlbRequest.getV_mid());
		try {
			map.put("P1_bizType", "SettlementCardWithdraw");
			map.put("P2_customerNumber", "C" + pmsBusinessPos.getBusinessnum());
			map.put("P3_userId", "");
			map.put("P4_orderId", hlbRequest.getV_batch_no());
			map.put("P5_amount", Double.parseDouble(hlbRequest.getV_sum_amount()) + "");
			map.put("P6_feeType", "PAYER");
			map.put("P7_summary", "代付");
			log.info("签名之前的数据:" + map);
			String key = HLBUtil.payKey;
			String oriMessage = MyBeanUtils.getSigned(map, null, "");
			oriMessage = oriMessage.substring(0, oriMessage.lastIndexOf("&"));
			log.info("签名原文串：" + oriMessage);
			String sign = RSA.sign(oriMessage, RSA.getPrivateKey(key));
			// String oriMessage = MyBeanUtils.getSigned(map, null,key);

			// String sign =Disguiser.disguiseMD5(oriMessage, "UTF-8");
			map.put("sign", sign);
			Map<String, Object> resultMap = HttpClientService.getHttpResp(map, HLBUtil.payUrl);
			log.info("算卡提现返回参数:" + JSON.toJSONString(resultMap));
			String s = resultMap.get("response").toString();
			System.out.println(s);
			com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSONObject.parseObject(s);
			if ("0000".equals(json.getString("rt2_retCode"))) {
				result.put("respCode", "00");
				result.put("type", hlbRequest.getV_accountType());
				result.put("merNo", hlbRequest.getV_mid());
				result.put("orderAmount", hlbRequest.getV_sum_amount());
				result.put("userId", "");
				result.put("respMsg", json.getString("rt3_retMsg").toString());
				result.put("orderId", json.getString("rt6_orderId").toString());
			} else if ("0001".equals(json.getString("rt2_retCode"))) {
				result.put("respCode", "200");
				result.put("type", hlbRequest.getV_accountType());
				result.put("merNo", hlbRequest.getV_mid());
				result.put("orderAmount", hlbRequest.getV_sum_amount());
				result.put("userId", "");
				result.put("respMsg", json.getString("rt3_retMsg").toString());
				result.put("orderId", json.getString("rt6_orderId").toString());
			} else {
				result.put("respCode", "01");
				result.put("type", hlbRequest.getV_accountType());
				result.put("merNo", hlbRequest.getV_mid());
				result.put("orderAmount", hlbRequest.getV_sum_amount());
				result.put("userId", "");
				result.put("respMsg", json.getString("rt3_retMsg").toString());
				result.put("orderId", json.getString("rt6_orderId").toString());
			}
		} catch (Exception e) {
			log.info("结算卡提现短信" + e);
		}
		return result;
	}

	/**
	 * 合利宝信用卡代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 */
	public Map<String, String> creditCardWithdraw(DaifuRequestEntity hlbRequest, Map<String, String> result) {
		System.out.println("结算(信用卡)卡提现来了！");
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		PmsBusinessPos pmsBusinessPos = selectKey(hlbRequest.getV_mid());
		try {
			map.put("P1_bizType", "CreditCardRepayment");
			map.put("P2_customerNumber", "C" + pmsBusinessPos.getBusinessnum());
			map.put("P3_userId", "");
			map.put("P4_bindId", "");
			map.put("P5_orderId", hlbRequest.getV_sum_amount());
			map.put("P6_timestamp", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			map.put("P7_currency", "CNY");
			map.put("P8_orderAmount", Double.parseDouble(hlbRequest.getV_sum_amount()) + "");
			map.put("P9_feeType", "PAYER");
			map.put("P10_summary", "代付");
			log.info("签名之前的数据:" + map);
			String key = HLBUtil.payKey;
			String oriMessage = MyBeanUtils.getSigned(map, null, "");
			oriMessage = oriMessage.substring(0, oriMessage.lastIndexOf("&"));
			log.info("签名原文串：" + oriMessage);
			String sign = RSA.sign(oriMessage, RSA.getPrivateKey(key));
			// String oriMessage = MyBeanUtils.getSigned(map, null,key);

			// String sign =Disguiser.disguiseMD5(oriMessage, "UTF-8");
			map.put("sign", sign);
			Map<String, Object> resultMap = HttpClientService.getHttpResp(map, HLBUtil.payUrl);
			log.info("算卡提现返回参数:" + JSON.toJSONString(resultMap));
			String s = resultMap.get("response").toString();
			System.out.println(s);
			com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSONObject.parseObject(s);
			if ("0000".equals(json.getString("rt2_retCode"))) {
				result.put("respCode", "00");
				result.put("type", hlbRequest.getV_accountType());
				result.put("merNo", hlbRequest.getV_mid());
				result.put("orderAmount", hlbRequest.getV_sum_amount());
				result.put("userId", "");
				result.put("respMsg", json.getString("rt3_retMsg").toString());
				result.put("orderId", json.getString("rt6_orderId").toString());
			} else if ("0001".equals(json.getString("rt2_retCode"))) {
				result.put("respCode", "200");
				result.put("type", hlbRequest.getV_accountType());
				result.put("merNo", hlbRequest.getV_mid());
				result.put("orderAmount", hlbRequest.getV_sum_amount());
				result.put("userId", "");
				result.put("respMsg", json.getString("rt3_retMsg").toString());
				result.put("orderId", json.getString("rt6_orderId").toString());
			} else {
				result.put("respCode", "01");
				result.put("type", hlbRequest.getV_accountType());
				result.put("merNo", hlbRequest.getV_mid());
				result.put("orderAmount", hlbRequest.getV_sum_amount());
				result.put("userId", "");
				result.put("respMsg", json.getString("rt3_retMsg").toString());
				result.put("orderId", json.getString("rt6_orderId").toString());
			}
		} catch (Exception e) {
			log.info("结算卡提现短信" + e);
		}
		return result;
	}

	/**
	 * 畅捷纯代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 * @throws Exception
	 */

	public Map<String, String> sendPay(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) throws Exception {

		Map<String, String> map = new HashMap<>();

		PayBankInfo bank = new PayBankInfo();
		bank.setBank_pmsbankNo(payRequest.getV_pmsBankNo());
		bank = payBankInfoDao.selectByBankInfo(bank);
		log.info("查询商户银行信息:" + bank);
		map.put(BaseConstant.SERVICE, "cjt_dsf");// 鉴权绑卡确认的接口名
		map.put(BaseConstant.VERSION, "1.0");
		map.put(BaseConstant.PARTNER_ID, pmsBusinessPos.getBusinessnum());
		// 生产环境测试商户号
		map.put(BaseConstant.TRADE_DATE, BaseConstant.DATE);
		map.put(BaseConstant.TRADE_TIME, BaseConstant.TIME);
		map.put(BaseConstant.INPUT_CHARSET, BaseConstant.CHARSET);// 字符集
		map.put(BaseConstant.MEMO, "");// 备注
		map.put("TransCode", "T10000"); // 交易码
		map.put("OutTradeNo", payRequest.getV_batch_no()); // 商户网站唯一订单号
		map.put("BusinessType", "0"); // 业务类型
		map.put("BankCommonName", bank.getBank_short_title()); // 通用银行名称
		map.put("AccountType", "00"); // 账户类型
		map.put("AcctNo",
				ChanPayUtil.encrypt(payRequest.getV_cardNo(), BaseConstant.MERCHANT_PUBLIC_KEY, BaseConstant.CHARSET)); // 对手人账号(此处需要用真实的账号信息)
		map.put("AcctName", ChanPayUtil.encrypt(payRequest.getV_realName(), BaseConstant.MERCHANT_PUBLIC_KEY,
				BaseConstant.CHARSET)); // 对手人账户名称
		map.put("TransAmt", Double.parseDouble(payRequest.getV_amount()) + "");

		// ************** 以下信息可空 *******************
		map.put("Province", payRequest.getV_province() == null ? "" : payRequest.getV_province()); // 省份信息
		map.put("City", payRequest.getV_city() == null ? "" : payRequest.getV_city()); // 城市信息
		if (payRequest.getV_cert_no() != null && payRequest.getV_cert_no() != "") {
			map.put("LiceneceNo", ChanPayUtil.encrypt(payRequest.getV_cert_no(), BaseConstant.MERCHANT_PUBLIC_KEY,
					BaseConstant.CHARSET));
		}
		if (payRequest.getV_phone() != null && payRequest.getV_phone() != "") {
			map.put("Phone", ChanPayUtil.encrypt(payRequest.getV_phone(), BaseConstant.MERCHANT_PUBLIC_KEY,
					BaseConstant.CHARSET));
		}

		map.put("CorpPushUrl", "");
		map.put("PostScript", "");
		String data = ChanPayUtil.sendPost(map, BaseConstant.CHARSET, pmsBusinessPos.getKek());
		if (data.indexOf("[") == 1 && data.indexOf("]") == 1) {
			int a = data.indexOf("[") - 1;
			int b = data.indexOf("]") + 1;
			data = data.substring(0, a + "[".length()) + data.substring(b, data.length());// 利用substring进行字符串截取
		}

		System.out.println(data);
		log.info("畅捷返回参数：" + JSON.toJSONString(data));
		Map<String, String> maps = JsonUtil.jsonToMap(data);

		Map<String, String> m = new HashMap<>();
		try {
			if ("S".equals(maps.get("AcceptStatus")) && "0000".equals(maps.get("PlatformRetCode"))) {
				if ("000000".equals(maps.get("OriginalRetCode"))) {
					UpdateDaifu(payRequest.getV_batch_no(), "00");
					result.put("respCode", "00");
					result.put("respMsg", "交易受理成功");
					result.put("state", "00");
					result.put("message", maps.get("AppRetMsg") + "," + maps.get("OriginalErrorMessage"));
					result.put("amount", payRequest.getV_amount());
					result.put("orderId", payRequest.getV_batch_no());
					result.put("merchantId", payRequest.getV_mid());
				} else if ("111111".equals(maps.get("OriginalRetCode"))) {
					UpdateDaifu(payRequest.getV_batch_no(), "01");
					result.put("respCode", "00");
					result.put("respMsg", "交易受理成功");
					result.put("state", "01");
					result.put("message", maps.get("AppRetMsg") + "," + maps.get("OriginalErrorMessage"));
					result.put("amount", payRequest.getV_amount());
					result.put("orderId", payRequest.getV_batch_no());
					result.put("merchantId", payRequest.getV_mid());
					m.put("payMoney", payRequest.getV_amount());
					m.put("machId", payRequest.getV_mid());
					int nus = pmsMerchantInfoDao.updataPayT1(m);
					if (nus == 1) {
						log.info("畅捷***补款成功");
						// surplus = surplus+Double.parseDouble(payRequest.getAmount());
						// merchantinfo.setPosition(surplus.toString());
						PmsMerchantInfo info = select(payRequest.getV_mid());
						merchantinfo.setPosition(info.getPositionT1());
						payRequest.setV_batch_no(payRequest.getV_batch_no() + "/A");
						int id = add(payRequest, merchantinfo, result, "00");
						if (id == 1) {
							log.info("畅捷代付补单成功");
						}
					}
				} else {
					result.put("respCode", "00");
					result.put("respMsg", "交易受理成功");
					result.put("state", "200");
					result.put("message", maps.get("AppRetMsg") + "," + maps.get("OriginalErrorMessage"));
					result.put("amount", payRequest.getV_sum_amount());
					result.put("orderId", payRequest.getV_batch_no());
					result.put("merchantId", payRequest.getV_mid());
				}
			} else {
				UpdateDaifu(payRequest.getV_batch_no(), "01");
				result.put("respCode", "00");
				result.put("respMsg", "交易受理成功");
				result.put("state", "01");
				result.put("message", maps.get("RetMsg") + ",交易失败");
				result.put("amount", payRequest.getV_sum_amount());
				result.put("orderId", payRequest.getV_batch_no());
				result.put("merchantId", payRequest.getV_mid());
				m.put("payMoney", payRequest.getV_sum_amount());
				m.put("machId", payRequest.getV_mid());
				int nus = pmsMerchantInfoDao.updataPayT1(m);
				if (nus == 1) {
					log.info("畅捷***补款成功");
					// surplus = surplus+Double.parseDouble(payRequest.getAmount());
					// merchantinfo.setPosition(surplus.toString());
					PmsMerchantInfo info = select(payRequest.getV_mid());
					merchantinfo.setPosition(info.getPositionT1());
					payRequest.setV_batch_no(payRequest.getV_batch_no() + "/A");
					int id = add(payRequest, merchantinfo, result, "00");
					if (id == 1) {
						log.info("畅捷代付补单成功");
					}
				}
			}
		} catch (Exception e) {
			// q
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 畅捷代还
	 * 
	 * @throws Exception
	 */
	public Map<String, String> withdrawals(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) throws Exception {
		Map<String, String> map = new HashMap<>();
		PayBankInfo bank = new PayBankInfo();
		bank.setBank_pmsbankNo(payRequest.getV_pmsBankNo());
		bank = payBankInfoDao.selectByBankInfo(bank);
		log.info("查询商户银行信息:" + bank);
		log.info("代付进来了：" + JSON.toJSONString(payRequest));
		token(payRequest, map);
		log.info("给上游组装数据");
		String token;
		try {
			token = EncryptUtil.desDecrypt(map.get("token"), pmsBusinessPos.getKek());
			log.info("tiken:" + token);
			// 请求参数
			String merchantUuidText = pmsBusinessPos.getBusinessnum();// 商户UUID，3DES加密
			String reqFlowNo = payRequest.getV_batch_no();// 请求流水号
			Integer walletType = 0;
			if ("0".equals(payRequest.getV_type())) {
				walletType = 400;
			} else if ("1".equals(payRequest.getV_type())) {
				walletType = 402;
			}
			Integer amount1 = (int) (Double.parseDouble(payRequest.getV_sum_amount()) * 100);
			String amountText = amount1.toString();// 提现金额，支付金额以分为单位，3DES加密
			String bankAccountNoText = payRequest.getV_cardNo();// 银行卡卡号，3DES加密
			String bankAccountNameText = payRequest.getV_realName(); // 银行卡户名，3DES加密
			Integer bankAccountType = 2; // 银行卡账户类型 2 对私
			String bankName = bank.getBank_short_title(); // 银行名称
			String bankSubName = bank.getBank_name(); // 银行支行名称
			String bankChannelNo = payRequest.getV_pmsBankNo(); // 银行联行号
			String bankCode = bank.getBank_Id(); // 银行代码，请见银行代码、简称对照表
			String bankAbbr = bank.getBank_code(); // 银行代号，请见银行代码、简称对照表
			String bankProvince = bank.getBank_province(); // 银行所属省
			String bankCity = bank.getBank_city(); // 银行所属市
			String bankArea = bank.getBank_city(); // 银行所属区域
			String key = pmsBusinessPos.getKek();
			// 敏感数据3DES加密
			String merchantUuid = null;
			String amount = null;
			String bankAccountNo = null;
			String bankAccountName = null;
			log.info("laile11111!!!");
			merchantUuid = EncryptUtil.desEncrypt(merchantUuidText, key);
			amount = EncryptUtil.desEncrypt(amountText, key);
			bankAccountNo = EncryptUtil.desEncrypt(bankAccountNoText, key);
			bankAccountName = EncryptUtil.desEncrypt(bankAccountNameText, key);
			System.out.println("1111111111");
			// 构建签名参数
			log.info("laile222222!!!");
			TreeMap<String, Object> signParams = new TreeMap<String, Object>();
			signParams.put("token", token);
			signParams.put("clientNo", "");
			signParams.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
			signParams.put("merchantUuid", merchantUuidText);
			signParams.put("reqFlowNo", reqFlowNo);
			signParams.put("walletType", walletType);
			signParams.put("amount", amountText);
			signParams.put("bankAccountNo", bankAccountNoText);
			signParams.put("bankAccountName", bankAccountNameText);
			signParams.put("bankAccountType", bankAccountType);
			signParams.put("bankName", bankName);
			signParams.put("bankSubName", bankSubName);
			signParams.put("bankChannelNo", bankChannelNo);
			signParams.put("bankCode", bankCode);
			signParams.put("bankAbbr", bankAbbr);
			signParams.put("bankProvince", bankProvince);
			signParams.put("bankCity", bankCity);
			signParams.put("bankArea", bankArea);

			// 构建请求参数
			com.alibaba.fastjson.JSONObject jsonObj = new com.alibaba.fastjson.JSONObject();
			jsonObj.put("token", token);
			jsonObj.put("clientNo", "");
			jsonObj.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
			jsonObj.put("merchantUuid", merchantUuid);
			jsonObj.put("reqFlowNo", reqFlowNo);
			jsonObj.put("walletType", walletType);
			jsonObj.put("amount", amount);
			jsonObj.put("bankAccountNo", bankAccountNo);
			jsonObj.put("bankAccountName", bankAccountName);
			jsonObj.put("bankAccountType", bankAccountType);
			jsonObj.put("bankName", bankName);
			jsonObj.put("bankSubName", bankSubName);
			jsonObj.put("bankChannelNo", bankChannelNo);
			jsonObj.put("bankCode", bankCode);
			jsonObj.put("bankAbbr", bankAbbr);
			jsonObj.put("bankProvince", bankProvince);
			jsonObj.put("bankCity", bankCity);
			jsonObj.put("bankArea", bankArea);
			jsonObj.put("sign", SignUtil.signByMap(key, signParams));

			// 接口访问
			String jsonReq = jsonObj.toJSONString();
			System.out.println("jsonReq: " + jsonReq);
			String url = map.get("url") + "/gateway/api/withdrawDeposit";
			OkHttpClient client = new OkHttpClient();
			RequestBody body = RequestBody.create(PayUtil.JSON, jsonReq);
			Request request = new Request.Builder().url(url).post(body).build();
			Response response = client.newCall(request).execute();

			String jsonRsp = response.body().string();
			System.out.println("jsonRsp: " + jsonRsp);
			Map<String, String> m = new HashMap<>();
			m.put("payMoney", amount1.toString());
			m.put("machId", payRequest.getV_mid());
			int nus = 0;
			BaseResMessage<ConsumeVo> res = null;
			if (response.isSuccessful()) {
				res = com.alibaba.fastjson.JSONObject.parseObject(jsonRsp,
						new TypeReference<BaseResMessage<ConsumeVo>>() {
						});
				result.put("merchantId", payRequest.getV_mid());
				if ("000000".equals(res.getCode())) {
					result.put("code", "200");
					result.put("message", "受理成功,代付中");
					result.put("respCode", "00");
					result.put("respMsg", "请求成功");
				} else {
					UpdateDaifu(payRequest.getV_batch_no(), "01");
					result.put("code", "01");
					result.put("message", "受理失败");
					result.put("respCode", "00");
					result.put("respMsg", "请求成功");
					nus = pmsMerchantInfoDao.updataPay(m);
				}
				System.out.println("\n接口响应内容：" + res.getData());
			} else {
				UpdateDaifu(payRequest.getV_batch_no(), "01");
				result.put("code", "01");
				result.put("message", "受理失败");
				result.put("respCode", "01");
				result.put("respMsg", "请求失败");
				nus = pmsMerchantInfoDao.updataPay(m);
				System.out.println("响应码: " + response.code());
				throw new IOException("Unexpected code " + response.message());
			}
			if (nus == 1) {
				log.info("畅捷***补款成功");
				// surplus = surplus+Double.parseDouble(payRequest.getAmount());
				// merchantinfo.setPosition(surplus.toString());
				PmsMerchantInfo info = select(payRequest.getV_mid());
				merchantinfo.setPosition(info.getPosition());
				payRequest.setV_batch_no(payRequest.getV_batch_no() + "/A");
				int id = add(payRequest, merchantinfo, result, "00");
				if (id == 1) {
					log.info("畅捷代付补单成功");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return result;
	}

	public Map<String, String> token(DaifuRequestEntity payRequest, Map<String, String> result) {
		PmsBusinessPos pmsBusinessPos = this.selectKey(payRequest.getV_mid());
		String url = "";
		if ("10000158".equals(pmsBusinessPos.getBusinessnum()) || "10000125".equals(pmsBusinessPos.getBusinessnum())) {
			url = PayUtil.urlTest;
		} else if ("10000078".equals(pmsBusinessPos.getBusinessnum())
				|| "10000160".equals(pmsBusinessPos.getBusinessnum())) {
			url = PayUtil.url;
		}
		TreeMap<String, Object> signParams = new TreeMap<String, Object>();
		signParams.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
		signParams.put("tokenType", "25");
		com.alibaba.fastjson.JSONObject jsonObj = new com.alibaba.fastjson.JSONObject();
		jsonObj.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
		jsonObj.put("tokenType", "25");
		String md5 = SignUtil.signByMap(pmsBusinessPos.getKek(), signParams);
		jsonObj.put("sign", md5);
		String tokenJsonReq = jsonObj.toJSONString();
		System.out.println("tokenJsonReq: " + tokenJsonReq);

		OkHttpClient client = new OkHttpClient();
		RequestBody body = RequestBody.create(PayUtil.JSON, tokenJsonReq);
		String payUrl = url + "/gateway/api/getPayToken";
		Request request = new Request.Builder().url(payUrl).post(body).build();
		Response response;
		try {
			response = client.newCall(request).execute();
			String tokenJsonRsp = response.body().string();
			System.out.println("tokenJsonRsp: " + tokenJsonRsp);
			BaseResMessage<TokenRes> res = null;
			if (response.isSuccessful()) {
				res = com.alibaba.fastjson.JSONObject.parseObject(tokenJsonRsp,
						new TypeReference<BaseResMessage<TokenRes>>() {
						});

				System.out.println("\n接口响应内容：" + res.getData());
				if ("000000".equals(res.getCode())) {
					result.put("url", url);
					result.put("code", res.getCode());
					result.put("message", res.getMessage());
					result.put("token", res.getData().getToken());
				}
			} else {
				System.out.println("响应码: " + response.code());
				throw new IOException("Unexpected code " + response.message());
			}

			System.out.println("111:" + result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 广州恒明代付
	 * 
	 * @throws Exception
	 */
	public Map<String, String> hmbAccounts(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) throws Exception {

		Map<String, String> maps = new HashMap<>();// 填金
		PayBankInfo bank = new PayBankInfo();
		bank.setBank_pmsbankNo(payRequest.getV_pmsBankNo());
		bank = payBankInfoDao.selectByBankInfo(bank);
		log.info("查询商户银行信息:" + bank);
		com.alibaba.fastjson.JSONObject requestObj = new com.alibaba.fastjson.JSONObject();
		requestObj.put("ordernumber", payRequest.getV_batch_no());
		requestObj.put("accounttype", "1");
		requestObj.put("usertel", payRequest.getV_phone());
		requestObj.put("username", payRequest.getV_realName());
		requestObj.put("userpid", payRequest.getV_cert_no());
		requestObj.put("usercardno", payRequest.getV_cardNo());
		// requestObj.put("accounttype", "1");
		Integer amount1 = (int) (Double.parseDouble(payRequest.getV_sum_amount()) * 100);
		requestObj.put("amount", amount1.toString());// 单位分 100=1元
		if ("0".equals(payRequest.getV_type())) {
			requestObj.put("ordertype", "10");

		} else if ("1".equals(payRequest.getV_type())) {
			requestObj.put("ordertype", "11");
		} else {

			requestObj.put("ordertype", "20");
		}
		requestObj.put("backurl", HMUtil.backurl);

		String encryptdata = AesEncryption.Encrypt(requestObj.toJSONString(), HMUtil.aeskey, HMUtil.aeskey);

		String timestamp = TimeUtil.getTime();
		String signstr = SHA256Util.sha256(pmsBusinessPos.getKek() + "M" + pmsBusinessPos.getBusinessnum() + encryptdata
				+ timestamp + pmsBusinessPos.getKek());
		log.info("签名字符串:" + signstr);
		com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
		jsonObject.put("merchantid", "M" + pmsBusinessPos.getBusinessnum());
		jsonObject.put("data", encryptdata);
		jsonObject.put("timestamp", timestamp);
		jsonObject.put("sign", signstr);
		String postdata = "merchantid=" + "M" + pmsBusinessPos.getBusinessnum() + "&data=" + encryptdata + "&timestamp="
				+ timestamp + "&sign=" + signstr;
		String results = HttpsUtil.doSslPost("https://123.207.247.101/pay/unionpay/entrust/credit", postdata, "utf-8");
		log.info("恒明返回参数：" + results);

		com.alibaba.fastjson.JSONObject responseObj = com.alibaba.fastjson.JSONObject.parseObject(results);
		log.info("message:" + responseObj.get("message"));
		if ("0".equals(responseObj.getString("ret"))) {
			String dedata = AesEncryption.Desencrypt(responseObj.get("data").toString(), HMUtil.aeskey, HMUtil.aeskey);
			log.info("恒明解析参数：" + dedata);
			com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSONObject.parseObject(dedata);
			if ("0".equals(json.get("orderstate"))) {
				UpdateDaifu(payRequest.getV_batch_no(), "00");
				result.put("respCode", "00");
				result.put("respMsg", "代付成功");
				result.put("merchantId", payRequest.getV_mid());
				result.put("orderNumber", payRequest.getV_batch_no());
				result.put("amount", payRequest.getV_sum_amount());
			} else if ("9".equals(json.get("orderstate"))) {
				UpdateDaifu(payRequest.getV_batch_no(), "200");
				result.put("respCode", "200");
				result.put("respMsg", "代付中");
				result.put("merchantId", payRequest.getV_mid());
				result.put("orderNumber", payRequest.getV_batch_no());
				result.put("amount", payRequest.getV_sum_amount());
			} else {
				result.put("respCode", "01");
				result.put("respMsg", "代付失败！");
				result.put("merchantId", payRequest.getV_mid());
				result.put("orderNumber", payRequest.getV_batch_no());
				result.put("amount", payRequest.getV_sum_amount());
				UpdateDaifu(payRequest.getV_batch_no(), "01");
				maps.put("payMoney", payRequest.getV_sum_amount());
				maps.put("machId", payRequest.getV_mid());
				int nus = pmsMerchantInfoDao.updataPay(maps);
				if (nus == 1) {
					log.info("恒明***补款成功");
					// surplus = surplus+Double.parseDouble(hmRequest.getAmount());
					// 根据商户号查询信息
					PmsMerchantInfo info = select(payRequest.getV_mid());
					merchantinfo.setPosition(info.getPosition());
					payRequest.setV_batch_no(payRequest.getV_batch_no() + "/A");
					int id = add(payRequest, merchantinfo, result, "00");
					if (id == 1) {
						log.info("恒明代付补单成功");
					}
				}
			}
		}
		return result;
	}

	/**
	 * 沃支付代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 * @throws Exception
	 */
	public Map<String, String> wzfAccounts(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) throws Exception {
		PayBankInfo bank = new PayBankInfo();
		bank.setBank_pmsbankNo(payRequest.getV_pmsBankNo());
		bank = payBankInfoDao.selectByBankInfo(bank);
		log.info("查询商户银行信息:" + bank);
		Map<String, String> params = new HashMap<String, String>();
		String interfaceVersion = "1.0.0.1";
		String tranType = "DF02";
		String merNo = "301100710007122";
		String orderDate = UtilDate.getDate();
		String reqTime = UtilDate.getOrderNum();
		String orderNo = payRequest.getV_batch_no();
		String amount = payRequest.getV_amount();
		String bizCode = "007";
		String payeeAcc = "";
		params.put("interfaceVersion", interfaceVersion);
		params.put("tranType", tranType);
		params.put("merNo", merNo);
		params.put("orderDate", orderDate);
		params.put("reqTime", reqTime);
		params.put("orderNo", orderNo);
		params.put("amount", amount);
		params.put("bizCode", bizCode);
		if (payRequest.getV_cardNo() != null) {
			payeeAcc = payRequest.getV_cardNo();
			params.put("payeeAcc", payeeAcc);
		}
		String woType = "4";
		params.put("woType", woType);
		String payeeBankCode = "";
		if (payRequest.getV_pmsBankNo() != null) {
			payeeBankCode = bank.getBank_code();
			params.put("payeeBankCode", payeeBankCode);
		}
		String payeeName = "";
		if (payRequest.getV_realName() != null) {
			payeeName = payRequest.getV_realName();
			params.put("payeeName", payeeName);
		}
		String payeeBankBranch = "";
		if (payRequest.getV_pmsBankNo() != null) {
			payeeBankBranch = bank.getBank_name();
			params.put("payeeBankBranch", payeeBankBranch);
		}
		String payeeUnionBan = "";
		if (payRequest.getV_pmsBankNo() != null) {
			payeeUnionBan = payRequest.getV_pmsBankNo();
			params.put("payeeUnionBan", payeeUnionBan);
		}
		String payeeAttribution = "";
		if (payRequest.getV_pmsBankNo() != null) {
			payeeAttribution = bank.getBank_province();
			params.put("payeeAttribution", payeeAttribution);
		}
		String identityInfo = "";
		if (payRequest.getV_identity() != null) {
			identityInfo = payRequest.getV_identity();
			params.put("identityInfo", identityInfo);
		}
		// String callbackUrl=array.getCallbackUrl();
		// params.put("callbackUrl", callbackUrl);
		String merExtend = "";
		// if(array.getMerExtend()!=null)
		// {
		// merExtend=array.getMerExtend();
		// params.put("merExtend", merExtend);
		// }
		String signType = "RSA_SHA256";
		params.put("signType", signType);
		String signMsg = "";
		// 商户的签名
		String sign = UniPaySignUtils.merSign(params, "RSA_SHA256");
		// sign=URLEncoder.encode(sign,"UTF-8");
		params.put("signMsg", sign);
		// HttpClientUtil client = new HttpClientUtil();
		log.info("向上游发送的签名:" + sign);
		String url = Constant.SINGLEPAY_URL;

		log.info("向上游发送的数据:" + params);
		List list = HttpUtils.URLPost("http://mertest.unicompayment.com/issuegw/servlet/SingleIssueServlet.htm",
				params);

		log.info("响应的数据:" + list.get(0).toString());
		return result;
	}

	/**
	 * 九派代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 * @throws Exception
	 */
	public Map<String, String> jpPay(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) throws Exception {
		PayBankInfo bank = new PayBankInfo();
		bank.setBank_pmsbankNo(payRequest.getV_pmsBankNo());
		bank = payBankInfoDao.selectByBankInfo(bank);
		log.info("查询商户银行信息:" + bank);
		Map<String, String> dataMap = new LinkedHashMap<String, String>();

		Map<String, String> map = new HashMap<>();
		dataMap.put("charset", "02");// 字符集02：utf-8
		dataMap.put("version", "1.0");// 版本号
		dataMap.put("service", JpUtil.pay);
		dataMap.put("signType", "RSA256");
		dataMap.put("merchantId", pmsBusinessPos.getBusinessnum());// "800001400010085"
		dataMap.put("requestTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		dataMap.put("requestId", String.valueOf(System.currentTimeMillis()));

		dataMap.put("callBackUrl", JpUtil.responseUrl);
		dataMap.put("mcSequenceNo", "CJZF" + payRequest.getV_batch_no());
		dataMap.put("mcTransDateTime", payRequest.getV_time());// payRequest.getStartDate()
		dataMap.put("orderNo", payRequest.getV_batch_no());
		Integer amount1 = (int) (Double.parseDouble(payRequest.getV_sum_amount()) * 100);
		dataMap.put("amount", amount1.toString());
		dataMap.put("cardNo", payRequest.getV_cardNo());
		dataMap.put("accName", payRequest.getV_realName());
		// dataMap.put("idInfo",idInfo);
		// dataMap.put("idType",idType);
		dataMap.put("accType", "0");
		dataMap.put("lBnkNo", payRequest.getV_pmsBankNo());
		dataMap.put("lBnkNam", bank.getBank_name());
		// dataMap.put("validPeriod", payRequest.getYear() + payRequest.getMonth());
		// dataMap.put("cvv2", payRequest.getCvv2());
		// dataMap.put("cellPhone", payRequest.getPhone());
		// dataMap.put("remark",payRequest.get);
		// dataMap.put("bnkRsv",bnkRsv);
		// dataMap.put("capUse",capUse);
		dataMap.put("crdType", "00");
		// dataMap.put("remark1",remark1);
		// dataMap.put("remark2",remark2);
		// dataMap.put("remark3",remark3);
		String merchantCertPath = new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile()
				.getCanonicalPath() + "//ky//" + pmsBusinessPos.getBusinessnum() + ".p12";
		String merchantCertPass = pmsBusinessPos.getKek();// "nknEuX"; //秘钥
		map.putAll(dataMap);
		Set set = dataMap.keySet();
		Iterator iterator = set.iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			if ((dataMap.get(key) == null) || dataMap.get(key).toString().trim().length() == 0) {
				map.remove(key);
			}
		}
		RSASignUtil util = new RSASignUtil(merchantCertPath, merchantCertPass);

		String reqData = util.coverMap2String(map);
		util.setService(JpUtil.service);
		String merchantSign = util.sign(reqData, "UTF-8");
		String merchantCert = util.getCertInfo();
		// 请求报文
		String buf = reqData + "&merchantSign=" + merchantSign + "&merchantCert=" + merchantCert;
		log.info("给九派网关请求参数：" + buf);

		String url = "";
		if ("800001400010085".equals(pmsBusinessPos.getBusinessnum())) {
			url = JpUtil.payUrlTest;
		} else if ("800000200020011".equals(pmsBusinessPos.getBusinessnum())) {
			url = JpUtil.payUrl;
		}

		String res = MerchantUtil.sendAndRecv(url, buf, "UTF-8");
		log.info("代付返回参数：" + res);
		Map<String, String> retMap = new LinkedHashMap<String, String>();
		retMap.put("charset", (String) util.getValue(res, "charset"));
		retMap.put("version", (String) util.getValue(res, "version"));
		retMap.put("service", (String) util.getValue(res, "service"));
		retMap.put("requestId", (String) util.getValue(res, "requestId"));
		retMap.put("responseId", (String) util.getValue(res, "responseId"));
		retMap.put("responseTime", (String) util.getValue(res, "responseTime"));
		retMap.put("signType", (String) util.getValue(res, "signType"));
		retMap.put("merchantId", (String) util.getValue(res, "merchantId"));
		retMap.put("rspCode", (String) util.getValue(res, "rspCode"));
		retMap.put("rspMessage", (String) util.getValue(res, "rspMessage"));
		retMap.put("mcTransDateTime", (String) util.getValue(res, "mcTransDateTime"));
		retMap.put("orderNo", (String) util.getValue(res, "orderNo"));
		retMap.put("bfbSequenceNo", (String) util.getValue(res, "bfbSequenceNo"));
		retMap.put("mcSequenceNo", (String) util.getValue(res, "mcSequenceNo"));
		retMap.put("mcTransDateTime", (String) util.getValue(res, "mcTransDateTime"));
		retMap.put("cardNo", (String) util.getValue(res, "cardNo"));
		retMap.put("amount", (String) util.getValue(res, "amount"));
		retMap.put("remark1", (String) util.getValue(res, "remark1"));
		retMap.put("remark2", (String) util.getValue(res, "remark2"));
		retMap.put("remark3", (String) util.getValue(res, "remark3"));
		retMap.put("transDate", (String) util.getValue(res, "transDate"));
		retMap.put("transTime", (String) util.getValue(res, "transTime"));
		retMap.put("respMsg", (String) util.getValue(res, "rspMessage"));
		retMap.put("orderSts", (String) util.getValue(res, "orderSts"));
		Map<String, String> m = new HashMap<>();
		if ("IPS00000".equals(retMap.get("rspCode"))) {
			result.put("respCode", "00");
			result.put("respMsg", "请求成功，请看异步！");
			result.put("orderId", payRequest.getV_batch_no());
			result.put("amount", payRequest.getV_sum_amount());
			if ("S".equals(retMap.get("orderSts"))) {
				UpdateDaifu(payRequest.getV_batch_no(), "00");
				result.put("code", "00");
				result.put("message", "代付成功");
			} else if ("F".equals(retMap.get("orderSts")) || "R".equals(retMap.get("orderSts"))) {
				result.put("code", "01");
				result.put("message", "代付失败");
				UpdateDaifu(payRequest.getV_batch_no(), "02");
				m.put("payMoney", payRequest.getV_sum_amount());
				m.put("machId", payRequest.getV_mid());
				int nus = pmsMerchantInfoDao.updataPayT1(m);
				if (nus == 1) {
					log.info("九派***补款成功");
					PmsMerchantInfo info = select(payRequest.getV_mid());
					merchantinfo.setPosition(info.getPositionT1());
					// surplus = surplus+Double.parseDouble(payRequest.getAmount());
					// merchantinfo.setPosition(surplus.toString());
					payRequest.setV_batch_no(payRequest.getV_batch_no() + "/A");
					int id = add(payRequest, merchantinfo, result, "00");
					if (id == 1) {
						log.info("九派代付补单成功");
					}
				}
			} else if ("P".equals(retMap.get("orderSts")) || "U".equals(retMap.get("orderSts"))) {
				result.put("code", "200");
				result.put("message", "代付中");
			} else if ("N".equals(retMap.get("orderSts"))) {
				result.put("code", "200");
				result.put("message", "等待人工处理");
			}
		} else {
			result.put("respCode", "01");
			result.put("respMsg", retMap.get("respMsg"));
			result.put("orderId", payRequest.getV_batch_no());
			result.put("amount", payRequest.getV_sum_amount());
			UpdateDaifu(payRequest.getV_batch_no(), "02");
			m.put("payMoney", payRequest.getV_sum_amount());
			m.put("machId", payRequest.getV_mid());
			int nus = pmsMerchantInfoDao.updataPayT1(m);
			if (nus == 1) {
				log.info("畅捷***补款成功");
				// surplus = surplus+Double.parseDouble(payRequest.getAmount());
				// merchantinfo.setPosition(surplus.toString());
				PmsMerchantInfo info = select(payRequest.getV_mid());
				merchantinfo.setPosition(info.getPositionT1());
				payRequest.setV_batch_no(payRequest.getV_batch_no() + "/A");
				int id = add(payRequest, merchantinfo, result, "00");
				if (id == 1) {
					log.info("畅捷代付补单成功");
				}
			}
		}
		return result;
	}

	/**
	 * 易宝代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> ybPay(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) throws Exception {
		String url = YBUtil.paymentUri;
		// url=url+YBUtil.paymentUri;
		PayBankInfo bank = new PayBankInfo();
		DecimalFormat df = new DecimalFormat("#.00");
		bank.setBank_pmsbankNo(payRequest.getV_pmsBankNo());
		bank = payBankInfoDao.selectByBankInfo(bank);
		Map<String, String> params = new HashMap<>();
		params.put("customerNumber", YBUtil.customerNumber);
		params.put("groupNumber", YBUtil.groupNumber);
		params.put("batchNo", payRequest.getV_batch_no());
		params.put("orderId", payRequest.getV_identity());
		params.put("amount", df.format(Double.parseDouble(payRequest.getV_amount())));
		params.put("product", "");
		params.put("urgency", "1");
		params.put("accountName", payRequest.getV_realName());
		params.put("accountNumber", payRequest.getV_cardNo());
		if("BOCOM".equals(bank.getBank_code())) {
			bank.setBank_code("BOCO");
		}else if("CMB".equals(bank.getBank_code())){
			bank.setBank_code("CMBCHINA");
		}
		params.put("bankCode", bank.getBank_code());//
		params.put("bankName", "");
		params.put("bankBranchName", bank.getBank_name());//
		params.put("provinceCode", bank.getBank_province_Id());//
		params.put("cityCode", bank.getBank_city_Id());//
		params.put("feeType", "SOURCE");
		params.put("desc", "");
		params.put("leaveWord", "");
		params.put("abstractInfo", "");

		Map<String, String> yopresponsemap = YeepayService.yeepayYOP(params, url);
		log.info("易宝代付返回结果：" + JSON.toJSONString(yopresponsemap));
		if(yopresponsemap!=null && yopresponsemap.size()>=1) {
			if ("BAC001".equals(yopresponsemap.get("errorCode"))) {
				log.info("请求成功了");
				result.put("v_mid", payRequest.getV_mid());
				result.put("v_batch_no", payRequest.getV_batch_no());
				result.put("v_code", "00");
				result.put("v_msg", "请求成功");
				result.put("v_sum_amount", payRequest.getV_sum_amount());
				result.put("v_amount", payRequest.getV_amount());
				if ("0".equals(payRequest.getV_type())) {
					result.put("v_type", "0");
				}
				if ("1".equals(payRequest.getV_type())) {
					result.put("v_type", "1");
				}
				result.put("v_identity", payRequest.getV_identity() == null ? "" : payRequest.getV_identity());
				result.put("v_time", UtilDate.getDateFormatter());
				if ("0025".equals(yopresponsemap.get("transferStatusCode"))
						|| "0026".equals(yopresponsemap.get("transferStatusCode"))) {
					result.put("v_status", "200");
					result.put("v_status_msg", "代付中");
					//ThreadPool.executor(new YBThread(pmsMerchantInfoDao, payRequest, this, yopresponsemap.get("batchNo")));
				} else if ("0027".equals(yopresponsemap.get("transferStatusCode"))
						|| "0028".equals(yopresponsemap.get("transferStatusCode"))) {
					result.put("v_status", "1001");
					result.put("v_status_msg", "代付失败");
					UpdateDaifu(payRequest.getV_batch_no(), "02");
					/*m.put("payMoney", Double.parseDouble(payRequest.getV_amount()) * 100 + "");
					m.put("machId", payRequest.getV_mid());
					int nus = 0;
					if ("0".equals(payRequest.getV_type())) {
						nus = pmsMerchantInfoDao.updataPay(m);
					} else if ("1".equals(payRequest.getV_type())) {
						nus = pmsMerchantInfoDao.updataPayT1(m);
					}

					if (nus == 1) {
						log.info("易宝***补款成功");
						payRequest.setV_batch_no(payRequest.getV_batch_no() + "/A");
						int id = add(payRequest, merchantinfo, result, "00");
						if (id == 1) {
							log.info("易宝代付补单成功");
						}
					}*/
				} else if ("0030".equals(yopresponsemap.get("transferStatusCode"))) {
					result.put("v_status", "11");
					result.put("v_status_msg", "状态未明");
					//ThreadPool.executor(new YBThread(pmsMerchantInfoDao, payRequest, this, yopresponsemap.get("batchNo")));
				} else if ("0029".equals(yopresponsemap.get("transferStatusCode"))) {
					result.put("v_status", "200");
					result.put("v_status_msg", "代付中");
					//ThreadPool.executor(new YBThread(pmsMerchantInfoDao, payRequest, this, yopresponsemap.get("batchNo")));
				}
			} else {
				result.put("v_code", "15");
				result.put("v_msg", "请求失败:"+yopresponsemap.get("errorMsg"));
				UpdateDaifu(payRequest.getV_batch_no(), "02");
				/*m.put("payMoney", Double.parseDouble(payRequest.getV_amount()) * 100 + "");
				m.put("machId", payRequest.getV_mid());
				int nus = 0;
				if ("0".equals(payRequest.getV_type())) {
					nus = pmsMerchantInfoDao.updataPay(m);
				} else if ("1".equals(payRequest.getV_type())) {
					nus = pmsMerchantInfoDao.updataPayT1(m);
				}
				if (nus == 1) {
					log.info("易宝***补款成功");
					payRequest.setV_batch_no(payRequest.getV_batch_no() + "/A");
					int id = add(payRequest, merchantinfo, result, "00");
					if (id == 1) {
						log.info("易宝代付补单成功");
					}
				}*/
				log.info("请求失败了");
			}
		}else {
			
			result.put("v_status", "11");
			result.put("v_status_msg", "状态未明");
		}
		
		return result;
	}

	/**
	 * 创新同名付代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> cxPay(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) throws Exception {
		ChinaInPaySameNamePayRequest cpspr = new ChinaInPaySameNamePayRequest();

		cpspr.setService("payForSameName");
		cpspr.setMerchantNo("CX" + pmsBusinessPos.getBusinessnum());// getV_batch_no
		cpspr.setOrderNo(payRequest.getV_batch_no());
		cpspr.setOrderNoList(payRequest.getV_identity());//
		cpspr.setVersion("V2.0");
		cpspr.setAccountProp("1");// 1对私
		cpspr.setAccountProp(payRequest.getV_cardType());//1对私
		cpspr.setAccountNo(Base64Util.encodeData(payRequest.getV_cardNo()));
		cpspr.setAccountName(Base64Util.encodeData(payRequest.getV_realName()));
		cpspr.setBankGenneralName(payRequest.getV_bankname());
		cpspr.setBankName(payRequest.getV_bankname());
		cpspr.setBankCode(payRequest.getV_bankCode());
		cpspr.setCurrency("CNY");
		cpspr.setBankProvcince(payRequest.getV_province());
		cpspr.setBankCity(payRequest.getV_city());
		cpspr.setOrderAmount((int) (Double.parseDouble(payRequest.getV_amount()) * 100) + "");
		cpspr.setTel(payRequest.getV_phone());
		// cpspr.setCause(cause);
		cpspr.setOrderTime(new SimpleDateFormat("YYYYMMDD24HHMMSS").format(new Date()));
		cpspr.setNotifyUrl(CXUtil.notifyUrl);
		// cpspr.setOrderSource(orderSource);
		cpspr.setSignType("2");
		ChinaInPayRequest<ChinaInPaySameNamePayRequest> request = new ChinaInPayRequest<ChinaInPaySameNamePayRequest>();

		request.setTransDetail(cpspr);
		String url = CXUtil.url + "/agentPay";
		String serviceName = "payForSameName";
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
		log.info("代付返回参数：" + JSON.toJSON(results));
		com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSONObject.parseObject(results);

		Map<String, String> m = new HashMap<>();
		result.put("v_mid", payRequest.getV_mid());
		result.put("v_batch_no", payRequest.getV_batch_no());
		result.put("v_code", "00");
		result.put("v_msg", "请求成功");
		if ("10000".equals(json.getString("dealCode"))) {
			log.info("请求成功了");
			result.put("v_sum_amount", payRequest.getV_sum_amount());
			result.put("v_amount", payRequest.getV_amount());
			if ("0".equals(payRequest.getV_type())) {
				result.put("v_type", "0");
			}
			if ("1".equals(payRequest.getV_type())) {
				result.put("v_type", "1");
			}
			result.put("v_identity", payRequest.getV_identity() == null ? "" : payRequest.getV_identity());
			result.put("v_time", UtilDate.getDateFormatter());
			result.put("v_status", "200");
			result.put("v_status_msg", "代付中");
			ThreadPool.executor(new CXThread(pmsMerchantInfoDao, payRequest, this, payRequest.getV_batch_no(), merchantinfo));
		} else {
			result.put("v_status", "1001");
			result.put("v_status_msg", "代付失败");
			UpdateDaifu(payRequest.getV_batch_no(), "02");
			m.put("payMoney", Double.parseDouble(payRequest.getV_amount())*100 + Double.parseDouble(merchantinfo.getPoundage())*100+"");
			m.put("machId", payRequest.getV_mid());
			int nus = 0;
			if ("0".equals(payRequest.getV_type())) {
				nus = pmsMerchantInfoDao.updataPay(m);
			} else if ("1".equals(payRequest.getV_type())) {
				nus = pmsMerchantInfoDao.updataPayT1(m);
			}

			if (nus == 1) {
				log.info("创新***补款成功");
				payRequest.setV_batch_no(payRequest.getV_batch_no() + "/A");
				int id = add(payRequest, merchantinfo, result, "00");
				if (id == 1) {
					log.info("创新代付补单成功");
				}
			} else {
				result.put("v_status", "1001");
				result.put("v_status_msg", "代付失败:"+json.getString("dealMsg"));
				UpdateDaifu(payRequest.getV_batch_no(), "02");
				m.put("payMoney", Double.parseDouble(payRequest.getV_amount())*100+Double.parseDouble(merchantinfo.getPoundage())*100+"");
				m.put("machId", payRequest.getV_mid());
				nus=0;
				if ("0".equals(payRequest.getV_type())) {
					 nus = pmsMerchantInfoDao.updataPay(m);
				}else if ("1".equals(payRequest.getV_type())) {
					 nus = pmsMerchantInfoDao.updataPayT1(m);

				}
			}
		}
		return result;
	}

	
	/**
	 * 创新代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> cxWrongPay(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) throws Exception {
		ChinaInPayOnePayRequest cpspr =new ChinaInPayOnePayRequest();
		
		cpspr.setService("payForAnotherOne");
		cpspr.setMerchantNo("CX"+pmsBusinessPos.getBusinessnum());//getV_batch_no
		cpspr.setOrderNo(payRequest.getV_batch_no());
		cpspr.setVersion("V2.0");
		cpspr.setAccountProp(payRequest.getV_cardType());//1对私
		cpspr.setAccountNo(Base64Util.encodeData(payRequest.getV_cardNo()));
		cpspr.setAccountName(Base64Util.encodeData(payRequest.getV_realName()));
		cpspr.setBankGenneralName(payRequest.getV_bankname());
		cpspr.setBankName(payRequest.getV_bankname());
		cpspr.setBankCode(payRequest.getV_bankCode());
		cpspr.setCurrency("CNY");
		cpspr.setBankProvcince(payRequest.getV_province());
		cpspr.setBankCity(payRequest.getV_city());
		cpspr.setOrderAmount((int)(Double.parseDouble(payRequest.getV_amount())*100)+"");
		cpspr.setTel(payRequest.getV_phone());
		//cpspr.setCause(cause);
		cpspr.setOrderTime(new SimpleDateFormat("YYYYMMDD24HHMMSS").format(new Date()));
		cpspr.setNotifyUrl(CXUtil.notifyUrl);
		cpspr.setOrderSource(payRequest.getV_channel());
		ChinaInPayRequest<ChinaInPayOnePayRequest> request=new ChinaInPayRequest<ChinaInPayOnePayRequest>();
		
		request.setTransDetail(cpspr);
		String url=CXUtil.url+"/agentPay";
		String serviceName="payForAnotherOne";
		//商户私钥
		String privateKey=pmsBusinessPos.getKek();//CXUtil.privateKey;
		//接入网关url
		DefaultChinaInPayClient client=new DefaultChinaInPayClient(url,serviceName,privateKey);
		String results = null;
		try {
			results = client.execute(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("代付返回参数："+JSON.toJSON(results));
		com.alibaba.fastjson.JSONObject json =com.alibaba.fastjson.JSONObject.parseObject(results);
		
		Map<String, String> m = new HashMap<>();
		result.put("v_mid", payRequest.getV_mid());
		result.put("v_batch_no", payRequest.getV_batch_no());
		result.put("v_code", "00");
		result.put("v_msg", "请求成功");
		if ("10000".equals(json.getString("dealCode"))) {
			log.info("请求成功了");
			result.put("v_sum_amount", payRequest.getV_sum_amount());
			result.put("v_amount",payRequest.getV_amount());
			if ("0".equals(payRequest.getV_type())) {
				result.put("v_type", "0");
			}
			if ("1".equals(payRequest.getV_type())) {
				result.put("v_type", "1");
			}
			result.put("v_identity", payRequest.getV_identity()==null?"":payRequest.getV_identity());
			result.put("v_time", UtilDate.getDateFormatter());
			result.put("v_status", "200");
			result.put("v_status_msg", "代付中");
			ThreadPool.executor(new CXThread(pmsMerchantInfoDao, payRequest, this, payRequest.getV_batch_no(), merchantinfo));
			} else {
				result.put("v_status", "1001");
				result.put("v_status_msg", "代付失败:"+json.getString("dealMsg"));
				UpdateDaifu(payRequest.getV_batch_no(), "02");
				m.put("payMoney", Double.parseDouble(payRequest.getV_amount())*100+Double.parseDouble(merchantinfo.getPoundage())*100+"");
				m.put("machId", payRequest.getV_mid());
				int nus=0;
				if ("0".equals(payRequest.getV_type())) {
					 nus = pmsMerchantInfoDao.updataPay(m);
				}else if ("1".equals(payRequest.getV_type())) {
					 nus = pmsMerchantInfoDao.updataPayT1(m);
				}
				
				if (nus == 1) {
					log.info("创新***补款成功");
					payRequest.setV_batch_no(payRequest.getV_batch_no() + "/A");
					int id = add(payRequest, merchantinfo, result, "00");
					if (id == 1) {
						log.info("创新代付补单成功");
					}
				}
			}
		return result;
	}
	
	
	
	@Override
	public Map<String, String> balance(BalanceRequestEntity payRequest, Map<String, String> result) {

		try {
			// 根据商户号查询
			String mercId = payRequest.getV_mid();

			PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
			merchantinfo.setMercId(mercId);

			// o单编号
			String oAgentNo = "";

			// 查询当前商户信息
			List<PmsMerchantInfo> merchantList;

			merchantList = pmsMerchantInfoDao.searchList(merchantinfo);

			if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

				merchantinfo = merchantList.get(0);
				// merchantinfo.setCustomertype("3");

				oAgentNo = merchantinfo.getoAgentNo();//

				if (StringUtils.isBlank(oAgentNo)) {
					// 如果没有欧单编号，直接返回错误
					result.put("1", "参数错误！");
					log.info("参数错误,没有欧单编号");
					return result;
				}
				// 判断是否为正式商户
				if ("60".equals(merchantinfo.getMercSts())) {
					log.info("此商户的商户号:" + mercId);
					result.put("v_mid", mercId);
					// 判断此商户是否开启代付
					if ("0".equals(merchantinfo.getOpenPay())) {
						log.info("此商户是否开启代付:" + merchantinfo.getOpenPay());
						if ("0".equals(payRequest.getV_type())) {
							log.info("此商户的T0代付余额(分):" + merchantinfo.getPosition());
							BigDecimal db = new BigDecimal(merchantinfo.getPosition());
							String l = db.toPlainString();
							Double amount = Double.parseDouble(l) / 100;
							log.info("此商户的T0代付余额(元):" + amount.toString());
							result.put("v_position", amount.toString());
							result.put("v_code", "00");
							result.put("v_msg", "请求成功");
						} else if ("1".equals(payRequest.getV_type())) {
							log.info("此商户的T1代付余额:" + merchantinfo.getPositionT1());
							BigDecimal db = new BigDecimal(merchantinfo.getPositionT1());
							String l = db.toPlainString();
							Double amount = Double.parseDouble(l) / 100;
							log.info("此商户的T1代付余额(元):" + amount.toString());
							result.put("v_position", amount.toString());
							result.put("v_code", "00");
							result.put("v_msg", "请求成功");
						} else {
							result.put("v_code", "15");
							result.put("v_msg", "请求失败");
						}

					} else {
						// 请求参数为空
						log.info("商户没有开启代付，" + merchantinfo.getMercId());
						result.put("v_code", "04");
						result.put("v_msg", "未开通代付,请重试或联系客服！");
						log.info("未开通代付,请重试或联系客服！");
						return result;
					}
				} else {
					// 请求参数为空
					log.info("商户没有进行实名认证，" + merchantinfo.getMercId());
					result.put("v_code", "08");
					result.put("v_msg", "还没有进行实名认证，请先去进行实名认证，或者等待客服审核!");
					log.info("还没有进行实名认证，请先去进行实名认证，或者等待客服审核!");
					return result;
				}

			} else {

				// 请求参数为空
				result.put("v_code", "09");
				result.put("v_msg", "此商户不存在,请重新输入!");
				log.info("此商户不存在,请重新输入!");
				return result;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 甬易代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 * @throws Exception
	 */
	public Map<String, String> yyPay(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) throws Exception {
		PayBankInfo bank = new PayBankInfo();
		bank.setBank_pmsbankNo(payRequest.getV_pmsBankNo());
		bank = payBankInfoDao.selectByBankInfo(bank);
		log.info("查询商户银行信息:" + bank);
		HashMap <String,String> payParam=new HashMap<String,String>();
		HashMap <String,Object> keyMap=new HashMap<String,Object>();
		HashMap <String,Object> returnMap=new HashMap<String,Object>();
        String keys="1af57d84950a70ab1d1e5c55a3f8a31a";
		
		String subStr = keys.substring(0,16);
		
		payParam.put("merCode", "9001002015");//商户编号
	try {
			payParam.put("cardByName",AesEncryptUtil.aesEncrypt(payRequest.getV_realName(), subStr));//姓名
			payParam.put("cardByNo",AesEncryptUtil.aesEncrypt(payRequest.getV_cardNo(), subStr) );//卡号
			payParam.put("idNumber", AesEncryptUtil.aesEncrypt(payRequest.getV_cert_no(), subStr));//证件号码
			payParam.put("bankMobile", AesEncryptUtil.aesEncrypt(payRequest.getV_phone(), subStr));//银行预留手机号
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		payParam.put("idType", "00");//身份证类型
		payParam.put("tradeTime",payRequest.getV_time());//交易时间
		payParam.put("orderId", payRequest.getV_batch_no());//订单号
		payParam.put("totalAmount",new BigDecimal(payRequest.getV_sum_amount().toString()).multiply(new BigDecimal("100")).intValue()+"");// 订单金额
		payParam.put("accType","0");// 对公标识
		payParam.put("bankCode",bank.getBank_code());//银行编码
		payParam.put("callbackUrl","callbackUrl");// 回调地址
		
		
		
		String url = EmaxPlusUtil.getSignPlainText(payParam);//注意签名顺序
		log.info("甬易上游签名前的数据:"+url);
		String sign=null;
		try {
			sign=EmaxPlusUtil._md5Encode(url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//签名
		payParam.put("sign", sign);
		//map转成json格式
		String reqStr= JsonUtils.toJson(payParam);
		log.info("甬易上送的数据:"+reqStr);
		String reqData = HttpClientUtil.doPost("http://paymentapi.emaxcard.com/pay/AgentPayToCard", reqStr);
       
         //json转成map
         Map map=JsonTools.parseJSON2Map(reqData);
          log.info("上游返回的数据:"+map);
         //获取返回码
         String rc = (String) map.get("resultCode");
         String resultMsg = (String) map.get("resultMsg");
         String MerCode = (String) map.get("MerCode");
         String OrderId = (String) map.get("OrderId");
         String ta = (String) map.get("totalAmount");
         String tradeTime = (String) map.get("tradeTime");
         String PayStatus = (String) map.get("PayStatus");
        //判断是否成功
         if(rc.equals("000000")&&PayStatus.equals("3")){
        	 String sus=returnMap.toString();
        		result.put("v_mid", payRequest.getV_mid());
    			result.put("v_batch_no", payRequest.getV_batch_no());
    			result.put("v_code", "00");
    			result.put("v_msg", "请求成功");
    			result.put("v_sum_amount", payRequest.getV_sum_amount());
    			result.put("v_amount", payRequest.getV_amount());
    			if ("0".equals(payRequest.getV_type())) {
    				result.put("v_type", "0");
    			}
    			if ("1".equals(payRequest.getV_type())) {
    				result.put("v_type", "1");
    			}
    			result.put("v_identity", payRequest.getV_identity() == null ? "" : payRequest.getV_identity());
    			result.put("v_time", UtilDate.getDateFormatter());
     	
     	
         }else{
        	result.put("v_code", "15");
 			result.put("v_msg", "请求失败");
 			UpdateDaifu(payRequest.getV_batch_no(), "02");
    	 
         }
		return result;
	}
	/**
	 * 裕福代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 * @throws Exception
	 */
	public Map<String, String> yfPay(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) throws Exception {
		//--------------------------------
		final String merCertPath= new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//ky//"+pmsBusinessPos.getBusinessnum()+".cer";
		final String pfxPath= new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//ky//"+pmsBusinessPos.getBusinessnum()+".pfx";
		final String pfxPwd= pmsBusinessPos.getKek();
		BatchDisburseApplyReq req = new BatchDisburseApplyReq();
		MultipartFile files =payRequest.getV_fileName();
		System.out.println(files.getOriginalFilename());;
		String path =new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//upload//"+files.getOriginalFilename();
		String fileName =files.getOriginalFilename();
		String[] name=fileName.split("\\.");
		System.out.println(name[0]+".bat");
		try {
			String md5file = MD5FileUtil.getFileMD5String(path);
			// 支付平台后台通知商户平台的地址
			req.setBackUrl(YFUtil.payUrl);
			// 代付文件摘要值
			req.setBatchPayFileDigest(md5file);
			// 代付文件名称
			req.setBatchPayFileName(name[0]+".bat");
			// 代付文件路径
			req.setFilePath(path);
			// 商户id
			req.setMerchantId(pmsBusinessPos.getBusinessnum());
			// 商户订单id
			req.setMerchantOrderId(payRequest.getV_batch_no());
			// 商户订单时间
			req.setMerchantOrderTime(payRequest.getV_time());
			req.setVersion("1.0.1");
			req.setMisc("");
			req.setMsgExt("");

			BatchDisburseApplyRsp rsp = null;
			YufuCipher cipher = null;
			YufuCipherSupport instance = null;
			cipher = YufuCipherSupport.getCipherInstance( merCertPath, pfxPath, pfxPwd,cipher,instance);
			//YufuCipher cipher = YufuCipherSupport.getCipherInstance(merCertPath, pfxPath, pfxPwd);

			String data = GsonUtil.objToJson(req);

			Map<String, String> params = GsonUtil.jsonToObj(data, Map.class);
			ParamPacket bo = cipher.doPack(params);
			log.info("11!:"+JSON.toJSON(bo));
			TreeMap<String, String> map_param = new TreeMap<>();
			map_param.put("merchantId", req.getMerchantId());
			map_param.put("data", bo.getData());
			map_param.put("enc", bo.getEnc());
			map_param.put("sign", bo.getSign());
			String url ="";
			if("000001110100000812".equals(pmsBusinessPos.getBusinessnum())) {
				url ="http://malltest.yfpayment.com/batchpay/payapply.do";
			}else  {
				url ="http://www.yfpayment.com/batchpay/payapply.do";
			}
			log.info("请求之前的参数："+JSON.toJSONString(map_param));
			log.info("请求之前的文件地址："+req.getFilePath());
			String returnStr = DisburseClientUtil.sendMutiPost(url, map_param, req.getFilePath());
			log.info("返回的参数："+JSON.toJSONString(returnStr));
			if (StringUtil.isNotEmpty(returnStr)) {
				String pbody = URLDecoder.decode(returnStr, "UTF-8");
				TreeMap<String, String> dataMap = JSON.parseObject(pbody, new TypeReference<TreeMap<String, String>>() {
				});
				ParamPacket po = new ParamPacket(dataMap.get("data"), dataMap.get("enc"), dataMap.get("sign"));
				Map<String, String> resultMap = cipher.unPack(po);
				String resultJson = GsonUtil.objToJson(resultMap);
				rsp = DTOUtil.parseDTO(resultJson, BatchDisburseApplyRsp.class, "json");
				log.info("代付返回结果："+JSON.toJSONString(rsp));
				result.put("v_mid", payRequest.getV_mid());
    			result.put("v_batch_no", payRequest.getV_batch_no());
    			if("0000".equals(rsp.getRespCode())) {
    				result.put("v_code", "00");
    			}else if("9999999".equals(rsp.getRespCode())) {
    				result.put("v_code", "16");
    			}else {
    				result.put("v_code", "15");
    			}
    			result.put("v_msg", rsp.getRespDesc());
    			result.put("v_sum_amount", payRequest.getV_sum_amount());
    			result.put("v_amount", payRequest.getV_amount());
    			result.put("v_identity", payRequest.getV_identity() == null ? "" : payRequest.getV_identity());
    			result.put("v_time", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
    			result.put("v_type", "1");
    			
			} else {
				System.out.println("--------返回为空---------");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.remove("fee");
		
		return result;
	}
	
	
	
	/**
	 * 裕福单笔实时代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 * @throws Exception
	 */
	public Map<String, String> yfPayOne(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) throws Exception {
		//--------------------------------
		DecimalFormat df1 = new DecimalFormat("######0"); //四色五入转换成整数
		log.info("裕福实时代付来了！");
		final String merCertPath= new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//ky//"+pmsBusinessPos.getBusinessnum()+".cer";
		final String pfxPath= new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//ky//"+pmsBusinessPos.getBusinessnum()+".pfx";
		final String pfxPwd= pmsBusinessPos.getKek();
		OnePayRequest req = new OnePayRequest();
		//Double txnAmt=Double.parseDouble(payRequest.getV_amount())*100;
		//BigDecimal payAmt=new BigDecimal(txnAmt).setScale(0, BigDecimal.ROUND_HALF_UP);
		BigDecimal payAmt=new BigDecimal(payRequest.getV_amount()).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
		String v_cardType="02";
		if("1".equals(payRequest.getV_cardType())) {
			v_cardType="02";
		}else if("2".equals(payRequest.getV_cardType())) {
			v_cardType="01";
		}
		try {
			req.setVersion("1.0.0");
			req.setMerchantId(pmsBusinessPos.getBusinessnum());
			req.setMerchantOrderId(payRequest.getV_batch_no());
			req.setMerchantOrderTime(payRequest.getV_time());
			/*PayRequest pay =new PayRequest();
			
			pay.setAccountName(payRequest.getV_realName());
			pay.setAccountNo(payRequest.getV_cardNo());
			pay.setBankName(payRequest.getV_bankname());
			pay.setProvince(payRequest.getV_province());
			pay.setCity(payRequest.getV_city());
			pay.setBankName(payRequest.getV_bankname());
			pay.setAmt(payAmt.toString());
			pay.setPblFlag(v_cardType);
			pay.setRemark("代付");*/
			req.setPayInfo("{\"accountName\":\""+payRequest.getV_realName()+"\",\"accountNo\":\""+payRequest.getV_cardNo()+"\",\"bankName\":\""+payRequest.getV_bankname()+"\",\"province\":\""+payRequest.getV_province()+"\",\"city\":\""+payRequest.getV_city()+"\",\"branchName\":\""+payRequest.getV_bankname()+"\",\"amt\":\""+df1.format(payAmt)+"\",\"pblFlag\":\""+v_cardType+"\",\"remark\":\"代付\"}");
			req.setBackUrl(YFUtil.payOneUrl);
			req.setMsgExt("");
			//req.setMisc("");
			log.info("裕福实时代付发送之前参数："+JSON.toJSONString(req));
			BatchDisburseApplyRsp rsp = null;
			YufuCipher cipher = null;
			YufuCipherSupport instance = null;
			cipher = YufuCipherSupport.getCipherInstance( merCertPath, pfxPath, pfxPwd,cipher,instance);
			//YufuCipher cipher = YufuCipherSupport.getCipherInstance(merCertPath, pfxPath, pfxPwd);
			String data = GsonUtil.objToJson(req);
			Map<String, String> params = GsonUtil.jsonToObj(data, Map.class);
			ParamPacket bo = cipher.doPack(params);
			log.info("11!:"+JSON.toJSON(bo));
			TreeMap<String, String> map_param = new TreeMap<>();
			map_param.put("merchantId", req.getMerchantId());
			map_param.put("data", URLEncoder.encode(bo.getData(), "utf-8"));
			map_param.put("enc", URLEncoder.encode(bo.getEnc(), "utf-8"));
			map_param.put("sign", URLEncoder.encode(bo.getSign(), "utf-8"));
			String url ="";
			if("000001110100000812".equals(pmsBusinessPos.getBusinessnum())) {
				url ="http://malltest.yfpayment.com/batchpay/realTimeApply.do";
			}else {
				url ="http://www.yfpayment.com/batchpay/realTimeApply.do";
			}
			log.info("请求之前的参数："+JSON.toJSONString(map_param));
			String returnStr = PostUtils.doPost(url, map_param);
			log.info("返回的参数："+JSON.toJSONString(returnStr));
			if (StringUtil.isNotEmpty(returnStr)) {
				String pbody = URLDecoder.decode(returnStr, "UTF-8");
				TreeMap<String, String> dataMap = JSON.parseObject(pbody, new TypeReference<TreeMap<String, String>>() {
				});
				ParamPacket po = new ParamPacket(dataMap.get("data"), dataMap.get("enc"), dataMap.get("sign"));
				Map<String, String> resultMap = cipher.unPack(po);
				String resultJson = GsonUtil.objToJson(resultMap);
				rsp = DTOUtil.parseDTO(resultJson, BatchDisburseApplyRsp.class, "json");
				log.info("代付返回结果："+JSON.toJSONString(rsp));
				result.put("v_mid", payRequest.getV_mid());
    			result.put("v_batch_no", payRequest.getV_batch_no());
    			if("0000".equals(rsp.getRespCode())) {
    				result.put("v_code", "00");
    				if("05".equals(rsp.getTransStatus())) {
    					UpdateDaifu(payRequest.getV_batch_no(), "00");
    				}else if("04".equals(rsp.getTransStatus())) {
    					UpdateDaifu(payRequest.getV_batch_no(), "02");
    				}
    			}else if("9999999".equals(rsp.getRespCode())) {
    				result.put("v_code", "16");
    				UpdateDaifu(payRequest.getV_batch_no(), "02");
    			}else {
    				result.put("v_code", "15");
    				UpdateDaifu(payRequest.getV_batch_no(), "02");
    			}
    			result.put("v_msg", rsp.getRespDesc());
    			result.put("v_sum_amount", payRequest.getV_sum_amount());
    			result.put("v_amount", payRequest.getV_amount());
    			result.put("v_identity", payRequest.getV_identity() == null ? "" : payRequest.getV_identity());
    			result.put("v_time", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
    			result.put("v_type", "1");
    			
			} else {
				System.out.println("--------返回为空---------");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.remove("fee");
		
		return result;
	}
	/**
	 * 聚佰宝代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 * @throws Exception
	 */
	public Map<String, String> jbbPay(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) throws Exception {
		PayBankInfo bank = new PayBankInfo();
		bank.setBank_pmsbankNo(payRequest.getV_pmsBankNo());
		bank = payBankInfoDao.selectByBankInfo(bank);
		log.info("查询商户银行信息:" + bank);
		HashMap <String,String> payParam=new HashMap<String,String>();
		HashMap <String,Object> keyMap=new HashMap<String,Object>();
		HashMap <String,Object> returnMap=new HashMap<String,Object>();
		Y2e1010Req req = new Y2e1010Req();
		    Double a=Double.parseDouble(payRequest.getV_sum_amount())*100;
	        int money = a.intValue();
	        String merinsid = payRequest.getV_identity();
	        String ectonPublicKeyStr = CertKeyUtil.getEctonPublicKey(xdt.quickpay.jbb.common.IConstants.ECTON_PUBLIC_CERTFILE);
	        System.out.println("ectonPublicKeyStr:" + ectonPublicKeyStr);
	        //测试988088888888888 
	        
	        req.setMerNo("988000000036969");
	        req.setPayTm(payRequest.getV_time());
	        req.setBatchName(payRequest.getV_identity());
	        req.setBatchNo(payRequest.getV_batch_no());//26
	        
	        //测试0000000380  
	       req.setBussNo("0000002896");
	        req.setProcedureType("1");//1付款方收费 2收款方收费
	        //req.setBackUrl("http://192.168.101.116:7001/NetPay/daifuAccept.action");
	        req.setBackUrl("http://pay.changjiezhifu.com:8104/app_posp/totalPayController/jbb_notifyUrl.action");
	        req.setMerinsid(merinsid);
	        req.setPayType("1");
	        req.setBankNo(EctonRSAUtils.encryptStr(payRequest.getV_cardNo(), ectonPublicKeyStr));//易通公钥加密 测试“6216261000000000018”
	        req.setRealName(EctonRSAUtils.encryptStr(payRequest.getV_realName(), ectonPublicKeyStr));//加密 测试“全渠道”
	        req.setBankName(payRequest.getV_bankname());//测试“中国工商银行股份有限公司济南山大路支行”
	        req.setPayFee(String.valueOf(money));
	        req.setTotCnt(String.valueOf(payRequest.getV_count()));
	        req.setTotAmt(String.valueOf(money));//金额
	        
	        StringBuffer bufParam = new StringBuffer();
	        bufParam.append(req.getMerNo()).append("|")
	                .append(req.getPayTm()).append("|")
	                .append(req.getBatchName()).append("|")
	                .append(req.getBatchNo()).append("|")
	                .append(req.getBussNo()).append("|")
	                .append(req.getProcedureType()).append("|")
	                .append(req.getTotCnt()).append("|")
	                .append(req.getTotAmt()).append("|")
	                .append(req.getMerinsid()).append("|")
	                .append(req.getPayType()).append("|")
	                .append(req.getBankNo()).append("|")
	                .append(req.getRealName()).append("|")
	                .append(req.getBankName()).append("|")
	                .append(req.getPayFee()).append("|");
	        		
	       // String dtlStr = bufDetail.toString();
	        String signStr = bufParam.toString().substring(0, bufParam.length() - 1);
	        log.info("聚佰宝代付签名数据:"+signStr);
	        String sign = Sign.sign(signStr);
	        req.setSign(sign);

	        String xmlStr = req.createXml();
	        
	        log.info("聚佰宝代付上传的xml文件"+xmlStr);
	        try {
	            InputStream is = HttpsUtils.doPost(xdt.quickpay.jbb.common.IConstants.DAI_FU_URL, xmlStr);

	            //转换返回结果
	            String xmlRes = HttpsUtils.convertStreamToString(is);
	            log.info("聚佰宝代付返回报文:" + xmlRes);
	            xmlRes = xmlRes.substring(40, xmlRes.length() - 2);
	            log.info("聚佰宝截取之后的代付返回报文:" + xmlRes);
	            Y2e0010Res res = new Y2e0010Res();
	            res.parseXml(xmlRes);

	            log.info("聚佰宝代付响应的状态码:" + res.getRspcod() + "聚佰宝代付响应的状态码描述:" + res.getRspmsg());
	            if("B001".equals(res.getRspcod()))
	            {
	            	result.put("v_mid", payRequest.getV_mid());
	    			result.put("v_batch_no", payRequest.getV_batch_no());
	    			result.put("v_code", "00");
	    			result.put("v_msg", "请求成功");
	    			result.put("v_sum_amount", payRequest.getV_sum_amount());
	    			result.put("v_amount", payRequest.getV_amount());
	    			if ("0".equals(payRequest.getV_type())) {
	    				result.put("v_type", "0");
	    			}
	    			if ("1".equals(payRequest.getV_type())) {
	    				result.put("v_type", "1");
	    			}
	    			result.put("v_identity", payRequest.getV_identity() == null ? "" : payRequest.getV_identity());
	    			result.put("v_time", UtilDate.getDateFormatter());
	            }else
	            {
	            	result.put("v_code", "15");
	     			result.put("v_msg", "请求失败："+res.getRspmsg());
	     			UpdateDaifu(payRequest.getV_batch_no(), "02");  
	            }

	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
		return result;
	}

	/**
	 * 银生宝代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 * @throws Exception
	 */
	public Map<String, String> ysbPay(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) throws Exception {
		PayBankInfo bank = new PayBankInfo();
		bank.setBank_pmsbankNo(payRequest.getV_pmsBankNo());
		bank = payBankInfoDao.selectByBankInfo(bank);
		log.info("查询商户银行信息:" + bank);
		com.uns.inf.api.model.Request req = new com.uns.inf.api.model.Request();
		req.put("accountId", pmsBusinessPos.getBusinessnum());
		req.put("name", payRequest.getV_realName());
		req.put("cardNo", payRequest.getV_cardNo());
		req.put("orderId", payRequest.getV_batch_no());
		req.put("purpose", "出款");
		req.put("amount", payRequest.getV_sum_amount());
		req.put("idCardNo", payRequest.getV_cert_no());
		req.put("summary", "提款");
		req.put("phoneNo", payRequest.getV_phone());
		req.put("responseUrl", xdt.dto.transfer_accounts.util.PayUtil.payUrl);
		String param =Util.getMab(req);
		log.info("签名前参数："+param);
		String params=param+"&key="+pmsBusinessPos.getKek();
		log.info("签名前参数："+params);
		String sign = Md5Encrypt.md5(params);
		log.info("签名："+sign);
		param=param+"&mac="+sign;
		String url="";
		if("1120180427134034001".equals(pmsBusinessPos.getBusinessnum())) {
			 url="http://180.166.114.155:7181/delegate-pay-front-dp/delegatePay/fourElementsPay";
		}else {
			 url="http://pay.unspay.com:8081/delegate-pay-front/delegatePay/fourElementsPay";
		}
		
		String str =RequestUtils.doPost(url, param,"UTF-8");
		log.info("银生宝代付返回参数str:"+JSON.toJSONString(str));
		JSONObject json =JSONObject.fromObject(str);
		if("0000".equals(json.getString("result_code"))) {
			result.put("v_mid", payRequest.getV_mid());
			result.put("v_batch_no", payRequest.getV_batch_no());
			result.put("v_code", "00");
			result.put("v_msg", "请求成功");
			result.put("v_sum_amount", payRequest.getV_sum_amount());
			result.put("v_amount", payRequest.getV_amount());
			result.put("v_identity", payRequest.getV_identity() == null ? "" : payRequest.getV_identity());
			result.put("v_time", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			result.put("v_type", "1");
		}else {
			result.put("v_code", "15");
 			result.put("v_msg", "请求失败："+json.getString("result_msg"));
 			UpdateDaifu(payRequest.getV_batch_no(), "02");
 			
 			Map<String, String> map =new HashMap<>();
			map.put("machId",payRequest.getV_mid());
			map.put("payMoney",Double.parseDouble(payRequest.getV_sum_amount())*100+Double.parseDouble(merchantinfo.getPoundage())*100+"");
			int nus =updataPayT1(map);
 			if(nus==1) {
 				log.info("银生宝代付补款成功");
 				DaifuRequestEntity entity =new DaifuRequestEntity();
 				entity.setV_mid(payRequest.getV_mid());
 				entity.setV_batch_no(payRequest.getV_batch_no()+"/A");
 				entity.setV_amount(payRequest.getV_sum_amount());
 				entity.setV_sum_amount(payRequest.getV_sum_amount());
 				entity.setV_identity(payRequest.getV_identity());
 				entity.setV_cardNo(payRequest.getV_cardNo());
 				entity.setV_city(payRequest.getV_city());
 				entity.setV_province(payRequest.getV_province());
 				entity.setV_type("1");
 				entity.setV_pmsBankNo(payRequest.getV_pmsBankNo());
				int ii =add(entity, merchantinfo, result, "00");
				log.info("补款订单状态："+ii);
 			}
		}
		return result;
	}
	/**
	 * 金米代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 * @throws Exception
	 */
	public Map<String, String> jmPay(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) throws Exception {
		//PayBankInfo bank = new PayBankInfo();
		//bank.setBank_pmsbankNo(payRequest.getV_pmsBankNo());
		//bank = payBankInfoDao.selectByBankInfo(bank);
		//log.info("查询商户银行信息:" + bank);
		TreeMap<String, String> req = new TreeMap<>();
		//Double txnAmt=Double.parseDouble(payRequest.getV_amount())*100;
		//BigDecimal payAmt=new BigDecimal(txnAmt).setScale(0, BigDecimal.ROUND_HALF_UP);
		DecimalFormat df1 = new DecimalFormat("######0"); //四色五入转换成整数
		BigDecimal payAmt=new BigDecimal(payRequest.getV_amount()).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
		req.put("merchant_no",pmsBusinessPos.getBusinessnum());//
		req.put("req_pay_no",payRequest.getV_batch_no());
		req.put("order_amt",df1.format(payAmt));
		req.put("account_name",payRequest.getV_realName());
		req.put("account_id_no",payRequest.getV_cert_no());
		req.put("account_mobile", payRequest.getV_phone());
		req.put("account_card_no", payRequest.getV_cardNo());
		req.put("account_bank_name", payRequest.getV_bankname());
		req.put("account_branch", payRequest.getV_bankname());
		req.put("account_cnaps_no", payRequest.getV_pmsBankNo());
		req.put("account_province", payRequest.getV_province());
		req.put("account_city", payRequest.getV_city());
        req.put("bg_url",xdt.dto.transfer_accounts.util.PayUtil.jmPayUrl);
        //map.put("ext",entity.getV_attach());
        
        String paramSrc = RequestUtils.getParamSrc(req);
		log.info("金米签名前数据**********支付:" + paramSrc);
		String md5 = MD5Utils.md5(paramSrc + "&" + "7Yn9z78987r1","UTF-8").toUpperCase();
		//String md5 = MD5Utils.signs(paramSrc, "lD0Y4D9X3k90", "UTF-8").toUpperCase();//pmsBusinessPos.getKek()
		System.out.println(md5);
		req.put("sign", md5);
		log.info(JSON.toJSONString(req));
		//paramSrc=paramSrc+"&"+md5;
		String url ="http://api.jinmpay.com/api/pay/create";
		String str =RequestUtils.sendPost(url, JSON.toJSONString(req),"UTF-8");
		log.info("金米代付返回参数str:"+JSON.toJSONString(str));
		JSONObject json =JSONObject.fromObject(str);
		if("00".equals(json.getString("rsp_code"))) {
			result.put("v_mid", payRequest.getV_mid());
			result.put("v_batch_no", payRequest.getV_batch_no());
			result.put("v_code", "00");
			result.put("v_msg", "请求成功");
			result.put("v_sum_amount", payRequest.getV_sum_amount());
			result.put("v_amount", payRequest.getV_amount());
			result.put("v_identity", payRequest.getV_identity() == null ? "" : payRequest.getV_identity());
			result.put("v_time", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			result.put("v_type", "1");
		}else {
			result.put("v_code", "15");
 			result.put("v_msg", "请求失败："+json.getString("rsp_msg"));
 			UpdateDaifu(payRequest.getV_batch_no(), "02");
 			
 			Map<String, String> map =new HashMap<>();
			map.put("machId",payRequest.getV_mid());
			map.put("payMoney",Double.parseDouble(payRequest.getV_sum_amount())*100+Double.parseDouble(merchantinfo.getPoundage())*100+"");
			int nus =updataPayT1(map);
 			if(nus==1) {
 				log.info("金米代付补款成功");
 				DaifuRequestEntity entity =new DaifuRequestEntity();
 				entity.setV_mid(payRequest.getV_mid());
 				entity.setV_batch_no(payRequest.getV_batch_no()+"/A");
 				entity.setV_amount(payRequest.getV_sum_amount());
 				entity.setV_sum_amount(payRequest.getV_sum_amount());
 				entity.setV_identity(payRequest.getV_identity());
 				entity.setV_cardNo(payRequest.getV_cardNo());
 				entity.setV_city(payRequest.getV_city());
 				entity.setV_province(payRequest.getV_province());
 				entity.setV_type("1");
 				entity.setV_pmsBankNo(payRequest.getV_pmsBankNo());
				int ii =add(entity, merchantinfo, result, "00");
				log.info("金米补款订单状态："+ii);
 			}
		}
		return result;
	}
	
	/**
	 * 微宝付代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 * @throws Exception
	 */
	public Map<String, String> wfbPay(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) throws Exception {
		TreeMap<String, String> req = new TreeMap<>();
		req.put("payKey", pmsBusinessPos.getBusinessnum());// 商户支付Key
		req.put("outTradeNo", payRequest.getV_batch_no());
		//req.put("bankCode", "ICBC");//银行编码
		req.put("receiverName", payRequest.getV_realName());
		req.put("receiverAccountNo", payRequest.getV_cardNo());
		req.put("bankBranchNo", payRequest.getV_pmsBankNo());// 
		req.put("proxyType", "T0");
		req.put("bankName", payRequest.getV_bankname()==null?"":payRequest.getV_bankname());
		req.put("phoneNo", payRequest.getV_phone()==null?"":payRequest.getV_phone());
		req.put("bankAccountType", "PRIVATE_DEBIT_ACCOUNT");//
		req.put("orderPrice", payRequest.getV_sum_amount());
		req.put("productType", "ALIPAY");//B2C T1支付
        
        String paramSrc = RequestUtils.getParamSrc(req);
		log.info("微宝付签名前数据**********支付:" + paramSrc);
		String md5 = MD5Utils.md5(paramSrc+"&paySecret="+pmsBusinessPos.getKek(), "UTF-8").toUpperCase();//pmsBusinessPos.getKek()
		System.out.println(md5);
		req.put("sign", md5);
		log.info(JSON.toJSONString(req));
		//paramSrc=paramSrc+"&"+md5;
		String url ="http://192.144.172.91:8080/gateway/accountProxyPay/initPay";
		String str =xdt.dto.scanCode.util.SimpleHttpUtils.httpPost(url, req);
		//String str =RequestUtils.sendPost(url, JSON.toJSONString(req),"UTF-8");
		log.info("微宝付代付返回参数str:"+JSON.toJSONString(str));
		JSONObject json =JSONObject.fromObject(str);
		if("0000".equals(json.getString("resultCode"))) {
			result.put("v_mid", payRequest.getV_mid());
			result.put("v_batch_no", payRequest.getV_batch_no());
			result.put("v_code", "00");
			result.put("v_msg", "请求成功");
			result.put("v_sum_amount", payRequest.getV_sum_amount());
			result.put("v_amount", payRequest.getV_amount());
			result.put("v_identity", payRequest.getV_identity() == null ? "" : payRequest.getV_identity());
			result.put("v_time", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			result.put("v_type", "0");
			ThreadPool.executor(new WFBThread(this, payRequest.getV_mid(), payRequest.getV_batch_no(), payRequest, merchantinfo));
		}else {
			result.put("v_code", "15");
 			result.put("v_msg", "请求失败");
 			UpdateDaifu(payRequest.getV_batch_no(), "02");
 			Map<String, String> map =new HashMap<>();
			map.put("machId",payRequest.getV_mid());
			map.put("payMoney",Double.parseDouble(payRequest.getV_sum_amount())*100+Double.parseDouble(merchantinfo.getPoundage())*100+"");
			int nus =updataPay(map);
 			if(nus==1) {
 				log.info("微宝付代付补款成功");
 				DaifuRequestEntity entity =new DaifuRequestEntity();
 				entity.setV_mid(payRequest.getV_mid());
 				entity.setV_batch_no(payRequest.getV_batch_no()+"/A");
 				entity.setV_amount(payRequest.getV_sum_amount());
 				entity.setV_sum_amount(payRequest.getV_sum_amount());
 				entity.setV_identity(payRequest.getV_identity());
 				entity.setV_cardNo(payRequest.getV_cardNo());
 				entity.setV_city(payRequest.getV_city());
 				entity.setV_province(payRequest.getV_province());
 				entity.setV_type("0");
 				entity.setV_pmsBankNo(payRequest.getV_pmsBankNo());
				int ii =add(entity, merchantinfo, result, "00");
				log.info("微宝付补款订单状态："+ii);
 			}
		}
		return result;
	}
	/**
	 * 银盈通代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 * @throws Exception
	 */
	public Map<String, String> yytPay(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) throws Exception {
		Map<String, Object> df_Map = new HashMap<String, Object>();
		// 全局参数
		df_Map.put("login_token", "");// 登陆令牌
		df_Map.put("req_no", payRequest.getV_batch_no());// 请求流水号
		df_Map.put("app_code", "apc_02000001760");// 应用号
		df_Map.put("app_version", "1.0.0");// 应用版本
		df_Map.put("service_code", "sne_00000000002");// 服务号
		df_Map.put("plat_form", "03");// 平台
		// 输入参数
		df_Map.put("merchant_number", pmsBusinessPos.getBusinessnum());// 商户号
		df_Map.put("order_number", payRequest.getV_identity());// 商家原始订单号
		df_Map.put("wallet_id", "0100851217641658");// 付款钱包id
		df_Map.put("asset_id", "8087cf5ff12e45a8a52c766ff94b2188");// 付款资产id
		df_Map.put("password_type", "02");// 付款方密码类型
		df_Map.put("encrypt_type", "02");// 付款方加密类型
		String md5 = MD5Utils.md5(pmsBusinessPos.getKek(), "UTF-8");
		System.out.println(md5);
		df_Map.put("pay_password", md5);// 付款方支付密码
		df_Map.put("customer_type", "01");// 收款人客户类型
		df_Map.put("customer_name", payRequest.getV_realName());// 收款人客户姓名
		df_Map.put("currency", "CNY");// 代付币种
		df_Map.put("amount", payRequest.getV_sum_amount());// 代付金额
		df_Map.put("async_notification_addr", xdt.dto.transfer_accounts.util.PayUtil.yytNotifyUrl);// 异步通知地址
		df_Map.put("asset_type_code", "000002");// 收款客户资产大类编码
		df_Map.put("account_type_code", "01");// 收款客户资产小类编码
		df_Map.put("login_name", "");// 收款人登录名
		df_Map.put("effective_time", "");// 有效时间
		df_Map.put("account_number", payRequest.getV_cardNo());// 收款人银行卡
		df_Map.put("headquarters_bank_id", "");// 收款人总联行号
		df_Map.put("issue_bank_name", "");// 收款人发卡名称
		df_Map.put("issue_bank_id", "");// 收款人发卡行id
		// 配置1
		M2Config m2c_1 = new M2Config();
		m2c_1.load("m2.properties");
		m2c_1.aid = "8a179b8c63d8c63b0164644837a10b4f";
		m2c_1.app_key = "jiujiuxing|m2|20180704";
		m2c_1.url = "https://api.gomepay.com/CoreServlet?aid=AID&api_id=API_ID&signature=SIGNATURE&timestamp=TIMESTAMP&nonce=NONCE";
		m2c_1.url_ac = "https://api.gomepay.com/CoreServlet?aid=AID&api_id=API_ID&access_token=ACCESS_TOKEN";
		m2c_1.url_ac_token = "https://api.gomepay.com/access_token?aid=AID&signature=SIGNATURE&timestamp=TIMESTAMP&nonce=NONCE";
		m2c_1.debug = false;
		m2c_1.mode = "1";
		m2c_1.data_sign = false;
		M2Obj m2Obj_sogo = M2.build(m2c_1);
		// 将map转成json字符串
		String json = JSON.toJSONString(df_Map);
		com.yst.m2.sdk.ReturnObj ret = m2Obj_sogo.send("epay_api_deal@agent_for_paying", json);

		// 向接口发送数据
		// ReturnObj ret = M2.send("epay_api_deal@agent_for_paying", json);

		// 接口请求完成后，设置返回数据
		String ret_data = ret.get_data();// 设置服务返回的数据
		if (!ret.is_ok()) {
	        //如果服务处理失败，则返回m2的异常信息数据
	        ret_data = ret.to_json();
	    }
	    System.out.println(ret_data);
		com.alibaba.fastjson.JSONObject jsons= com.alibaba.fastjson.JSONObject.parseObject(ret_data);
	  if("000".equals(jsons.getString("op_ret_code"))) {
			result.put("v_mid", payRequest.getV_mid());
			result.put("v_batch_no", payRequest.getV_batch_no());
			result.put("v_code", "00");
			result.put("v_msg", "请求成功");
			result.put("v_sum_amount", payRequest.getV_sum_amount());
			result.put("v_amount", payRequest.getV_amount());
			result.put("v_identity", payRequest.getV_identity() == null ? "" : payRequest.getV_identity());
			result.put("v_time", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			result.put("v_type", "0");
			//ThreadPool.executor(new WFBThread(this, payRequest.getV_mid(), payRequest.getV_batch_no(), payRequest, merchantinfo));
		}else {
			result.put("v_code", "15");
 			result.put("v_msg", "请求失败");
 			UpdateDaifu(payRequest.getV_batch_no(), "02");
 			Map<String, String> map =new HashMap<>();
			map.put("machId",payRequest.getV_mid());
			map.put("payMoney",Double.parseDouble(payRequest.getV_sum_amount())*100+Double.parseDouble(merchantinfo.getPoundage())*100+"");
			int nus =updataPay(map);
 			if(nus==1) {
 				log.info("银盈通代付补款成功");
 				DaifuRequestEntity entity =new DaifuRequestEntity();
 				entity.setV_mid(payRequest.getV_mid());
 				entity.setV_batch_no(payRequest.getV_batch_no()+"/A");
 				entity.setV_amount(payRequest.getV_sum_amount());
 				entity.setV_sum_amount(payRequest.getV_sum_amount());
 				entity.setV_identity(payRequest.getV_identity());
 				entity.setV_cardNo(payRequest.getV_cardNo());
 				entity.setV_city(payRequest.getV_city());
 				entity.setV_province(payRequest.getV_province());
 				entity.setV_type("0");
 				entity.setV_pmsBankNo(payRequest.getV_pmsBankNo());
				int ii =add(entity, merchantinfo, result, "00");
				log.info("银盈通补款订单状态："+ii);
 			}
		}
	 	return result;
	}
	/**
	 * 易势代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 * @throws Exception
	 */
	public Map<String, String> yszfPay(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) throws Exception {
		Map<String, String> resultMap = null;
		TreeMap<String, String> maps = new TreeMap<>();
		DecimalFormat df =new DecimalFormat("#");
		// 全局参数
	 	String cerPath=new File(this.getClass().getResource("/").getPath()).getParentFile()
				.getParentFile().getCanonicalPath() + "/ky/"+pmsBusinessPos.getBusinessnum()+".cer";
        String keyStorePath=new File(this.getClass().getResource("/").getPath()).getParentFile()
				.getParentFile().getCanonicalPath() + "/ky/"+pmsBusinessPos.getBusinessnum()+".pfx";
        
        String keyPass=pmsBusinessPos.getKek();
        
        SecretConfig e = new SecretConfig(cerPath, keyStorePath, keyPass);
        Secret secret = new Secret(e);
        maps.put("merchantNo", pmsBusinessPos.getBusinessnum());// 
        maps.put("version", "v1");// 
        maps.put("channelNo", "04");// 
        maps.put("app_version", "1.0.0");// 
        maps.put("tranCode", "1001");// 
        maps.put("tranFlow", payRequest.getV_batch_no());// 
		// 输入参数
        maps.put("tranDate", payRequest.getV_time().substring(0, 8));// 
        maps.put("tranTime", payRequest.getV_time().substring(8, 14));// 
        maps.put("accNo", secret.encrypt(payRequest.getV_cardNo()));// 
        maps.put("accName", secret.encrypt(payRequest.getV_realName()));// 
        maps.put("bankAgentId", payRequest.getV_pmsBankNo());// 
        maps.put("currency", "RMB");// 
        maps.put("bankName", payRequest.getV_bankname());// 
        maps.put("amount", df.format(new BigDecimal(payRequest.getV_sum_amount()).multiply(new BigDecimal("100")).doubleValue()));// 
        maps.put("remark", "代付");// 
		String paramSrc = RequestUtils.getParamSrc(maps);
		log.info("易势支付签名前数据**********支付:" + paramSrc);
		String sign = secret.sign(paramSrc);
		System.out.println(sign);
		maps.put("sign", sign);
		log.info(JSON.toJSONString(maps));
		String url ="https://paydemo.ielpm.com/paygate/v1/dfpay"; 
		String str = xdt.dto.scanCode.util.SimpleHttpUtils.httpPost(url, maps);
		resultMap = ResponseUtil.parseResponse(str, secret);
		System.out.println("易势返回的参数"+JSON.toJSON(resultMap));
		// 接口请求完成后，设置返回数据
	  if("0000".equals(resultMap.get("rtnCode"))) {
			result.put("v_mid", payRequest.getV_mid());
			result.put("v_batch_no", payRequest.getV_batch_no());
			result.put("v_code", "00");
			result.put("v_msg", "请求成功");
			result.put("v_sum_amount",payRequest.getV_sum_amount());
			result.put("v_amount", payRequest.getV_amount());
			result.put("v_identity", payRequest.getV_identity() == null ? "" : payRequest.getV_identity());
			result.put("v_time", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			result.put("v_type", "0");
			UpdateDaifu(payRequest.getV_batch_no(), "00");
		}else if("0002".equals(resultMap.get("rtnCode"))||"0030".equals(resultMap.get("rtnCode"))||"0003".equals(resultMap.get("rtnCode"))||"00R1".equals(resultMap.get("rtnCode"))||"9999".equals(resultMap.get("rtnCode"))){
			result.put("v_mid", payRequest.getV_mid());
			result.put("v_batch_no", payRequest.getV_batch_no());
			result.put("v_code", "00");
			result.put("v_msg", "请求成功");
			result.put("v_sum_amount",payRequest.getV_sum_amount());
			result.put("v_amount", payRequest.getV_amount());
			result.put("v_identity", payRequest.getV_identity() == null ? "" : payRequest.getV_identity());
			result.put("v_time", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			result.put("v_type", "0");
			ThreadPool.executor(new YSZFThread(this, payRequest.getV_mid(), payRequest.getV_batch_no(), payRequest, merchantinfo));
		}else {
			result.put("v_code", "15");
 			result.put("v_msg", "请求失败");
 			UpdateDaifu(payRequest.getV_batch_no(), "02");
 			Map<String, String> map =new HashMap<>();
			map.put("machId",payRequest.getV_mid());
			map.put("payMoney",Double.parseDouble(payRequest.getV_sum_amount())*100+Double.parseDouble(merchantinfo.getPoundage())*100+"");
			int nus =updataPay(map);
 			if(nus==1) {
 				log.info("易势代付补款成功");
 				DaifuRequestEntity entity =new DaifuRequestEntity();
 				entity.setV_mid(payRequest.getV_mid());
 				entity.setV_batch_no(payRequest.getV_batch_no()+"/A");
 				entity.setV_amount(payRequest.getV_sum_amount());
 				entity.setV_sum_amount(payRequest.getV_sum_amount());
 				entity.setV_identity(payRequest.getV_identity());
 				entity.setV_cardNo(payRequest.getV_cardNo());
 				entity.setV_city(payRequest.getV_city());
 				entity.setV_province(payRequest.getV_province());
 				entity.setV_type("0");
 				entity.setV_pmsBankNo(payRequest.getV_pmsBankNo());
				int ii =add(entity, merchantinfo, result, "00");
				log.info("易势补款订单状态："+ii);
 			}
		}
	 	return result;
	}
	
	/**
	 * oem假汇聚代付
	 * 
	 * @param payRequest
	 * @param result
	 * @param merchantinfo
	 * @param pmsBusinessPos
	 * @throws Exception
	 */
	public Map<String, String> jhjPay(DaifuRequestEntity payRequest, Map<String, String> result,
			PmsMerchantInfo merchantinfo, PmsBusinessPos pmsBusinessPos) throws Exception {
		DecimalFormat df= new DecimalFormat("######0.00");   
		com.uns.inf.api.model.Request req = new com.uns.inf.api.model.Request();
		req.put("agentType", "01");
		req.put("mchId", pmsBusinessPos.getBusinessnum());
		req.put("outTradeNo", payRequest.getV_batch_no());
		req.put("bankCardNo", payRequest.getV_cardNo());
		req.put("bankCardHolder", payRequest.getV_realName());
		req.put("amount", df.format(Double.parseDouble(payRequest.getV_sum_amount())));
		req.put("summary", "提款");
		req.put("city", payRequest.getV_city());
		String param = SignatureUtil.getSignPlainText(req);//注意签名顺序
		log.info("签名前参数："+param);
		String key="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALyPluyE1wnc+ONM0p/fp8PQu45f95dMsi4NBQYFhWd9rFtEy5gZUV8IzLJdXKyJ/EMgAs446p+G22L/EJjb8HeFmWx6tEu7JQkqu+fMfmb0DM7EXqJvAioJhKqwRBaG8zbF5aFgGVF6Nnk/YrILQCwkFAszD5MWCXTCEmSY3LzVAgMBAAECgYAmTzoHlbmmzFlYvOvyBVutYgQpGgBQogl1Z7nEjmybKSJSbLi8jzBEEaKc/nDssSAqdx96zH+Gp7x88XtqwwyosfOXAatzKaPMHdyf9TBQWdYkrNbM/JLby9r1sslLq2xyrXWgqMS+kOWS2p/JWTo6X3YLxi0Bi4lqNbryEa81xQJBAPfkZTTTPILkQMdP+jdV8RKE3/HCpc0LQEOILZouY5GD90tZm7AblmvquQ0HFfKd/LHVHymDbFdD8QVKRhLe11sCQQDCummhY2EkUOYyJjJ+aG+IiqJNQ8xsv6sjn6wF25aHOfNJPphWz6hf6ND6QxPMvFdKF8qs14FPfVzW7TrooyOPAkEAyjcJDBeI1CmIYk5uibdUqUu1Nw0WnXYhHTW4JX7UAD9Leq8FXpqSkUPvYp42HC0elp6JBh9MQL+OnEcjdH9N4wJAdtR9C2By8k9v+mB25c7jaSZ4nr/l6uMYE7gnqLd053aEsUjCfA9ix4xyopX2ajTw66UTKGCmZ5Sv5/SCw15ynwJBANanZMAeJvoBloTvK5kKvQTi5pyNd+drb3PYiMO+jZEa0DQ3+/04EbxvzSNoBvlAKSrL4kUwfHZGF27EXM1/tAM=";
		String sign = RSAUtils.sign(param.getBytes("UTF-8"), key,"SHA1WithRSA");
		log.info("签名："+sign);
		req.put("sign", sign);
		log.info("请求前的参数："+JSON.toJSONString(req));
		String url="http://mch.fintech2syx.com/cloud/collectpay/api/single.html";
		String str =RequestUtils.sendPost(url, JSON.toJSONString(req),"UTF-8");
		log.info("oem汇聚代付返回参数str:"+JSON.toJSONString(str));
		JSONObject json =JSONObject.fromObject(str);
		if("0".equals(json.getString("returnCode"))) {
			if("0".equals(json.getString("resultCode"))) {
				if("SUCCESS".equals(json.getString("status"))) {
					result.put("v_mid", payRequest.getV_mid());
					result.put("v_batch_no", payRequest.getV_batch_no());
					result.put("v_code", "00");
					result.put("v_msg", "请求成功");
					result.put("v_sum_amount", payRequest.getV_sum_amount());
					result.put("v_amount", payRequest.getV_amount());
					result.put("v_identity", payRequest.getV_identity() == null ? "" : payRequest.getV_identity());
					result.put("v_time", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
					result.put("v_type", "0");
					UpdateDaifu(payRequest.getV_batch_no(), "00");
				}else if("FAIL".equals(json.getString("status"))) {
					result.put("v_code", "15");
		 			result.put("v_msg", "请求失败："+json.getString("errCodeDes"));
		 			UpdateDaifu(payRequest.getV_batch_no(), "02");
		 			
		 			Map<String, String> map =new HashMap<>();
					map.put("machId",payRequest.getV_mid());
					map.put("payMoney",Double.parseDouble(payRequest.getV_sum_amount())*100+Double.parseDouble(merchantinfo.getPoundage())*100+"");
					int nus =updataPay(map);
		 			if(nus==1) {
		 				log.info("oem汇聚代付补款成功");
		 				DaifuRequestEntity entity =new DaifuRequestEntity();
		 				entity.setV_mid(payRequest.getV_mid());
		 				entity.setV_batch_no(payRequest.getV_batch_no()+"/A");
		 				entity.setV_amount(payRequest.getV_sum_amount());
		 				entity.setV_sum_amount(payRequest.getV_sum_amount());
		 				entity.setV_identity(payRequest.getV_identity());
		 				entity.setV_cardNo(payRequest.getV_cardNo());
		 				entity.setV_city(payRequest.getV_city());
		 				entity.setV_province(payRequest.getV_province());
		 				entity.setV_type("0");
		 				entity.setV_pmsBankNo(payRequest.getV_pmsBankNo());
						int ii =add(entity, merchantinfo, result, "00");
						log.info("oem汇聚补款订单状态："+ii);
		 			}
				}else {
					result.put("v_mid", payRequest.getV_mid());
					result.put("v_batch_no", payRequest.getV_batch_no());
					result.put("v_code", "00");
					result.put("v_msg", "请求成功");
					result.put("v_sum_amount", payRequest.getV_sum_amount());
					result.put("v_amount", payRequest.getV_amount());
					result.put("v_identity", payRequest.getV_identity() == null ? "" : payRequest.getV_identity());
					result.put("v_time", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
					result.put("v_type", "0");
					ThreadPool.executor(new JHJThread(this, pmsBusinessPos.getBusinessnum(), payRequest.getV_batch_no(), payRequest, merchantinfo));
				}
			}else {
				result.put("v_code", "15");
	 			result.put("v_msg", "请求失败："+json.getString("errCodeDes"));
	 			UpdateDaifu(payRequest.getV_batch_no(), "02");
	 			
	 			Map<String, String> map =new HashMap<>();
				map.put("machId",payRequest.getV_mid());
				map.put("payMoney",Double.parseDouble(payRequest.getV_sum_amount())*100+Double.parseDouble(merchantinfo.getPoundage())*100+"");
				int nus =updataPay(map);
	 			if(nus==1) {
	 				log.info("oem汇聚代付补款成功");
	 				DaifuRequestEntity entity =new DaifuRequestEntity();
	 				entity.setV_mid(payRequest.getV_mid());
	 				entity.setV_batch_no(payRequest.getV_batch_no()+"/A");
	 				entity.setV_amount(payRequest.getV_sum_amount());
	 				entity.setV_sum_amount(payRequest.getV_sum_amount());
	 				entity.setV_identity(payRequest.getV_identity());
	 				entity.setV_cardNo(payRequest.getV_cardNo());
	 				entity.setV_city(payRequest.getV_city());
	 				entity.setV_province(payRequest.getV_province());
	 				entity.setV_type("0");
	 				entity.setV_pmsBankNo(payRequest.getV_pmsBankNo());
					int ii =add(entity, merchantinfo, result, "00");
					log.info("oem汇聚补款订单状态："+ii);
	 			}
			}
			
		}else {
			result.put("v_code", "15");
 			result.put("v_msg", "请求失败："+json.getString("returnMsg"));
 			UpdateDaifu(payRequest.getV_batch_no(), "02");
 			
 			Map<String, String> map =new HashMap<>();
			map.put("machId",payRequest.getV_mid());
			map.put("payMoney",Double.parseDouble(payRequest.getV_sum_amount())*100+Double.parseDouble(merchantinfo.getPoundage())*100+"");
			int nus =updataPay(map);
 			if(nus==1) {
 				log.info("oem汇聚代付补款成功");
 				DaifuRequestEntity entity =new DaifuRequestEntity();
 				entity.setV_mid(payRequest.getV_mid());
 				entity.setV_batch_no(payRequest.getV_batch_no()+"/A");
 				entity.setV_amount(payRequest.getV_sum_amount());
 				entity.setV_sum_amount(payRequest.getV_sum_amount());
 				entity.setV_identity(payRequest.getV_identity());
 				entity.setV_cardNo(payRequest.getV_cardNo());
 				entity.setV_city(payRequest.getV_city());
 				entity.setV_province(payRequest.getV_province());
 				entity.setV_type("1");
 				entity.setV_pmsBankNo(payRequest.getV_pmsBankNo());
				int ii =add(entity, merchantinfo, result, "00");
				log.info("oem汇聚补款订单状态："+ii);
 			}
		}
		return result;
	}
	
	public boolean validationStr(DaifuRequestEntity daifu) throws Exception {
		// TODO Auto-generated method stub

		log.info("下游上送的数据:" + daifu);

		boolean flag = false;

		if (!"".equals(daifu.getV_bankname()) && daifu.getV_bankname() != null) {

			if (daifu.getV_bankname().contains("行") || daifu.getV_bankname().contains("信用社")
					|| daifu.getV_bankname().contains("合作社")
					|| daifu.getV_bankname().contains("联社") && daifu.getV_bankname().length() <= 40) {

				flag = true;

			}
		}
		if (!"".equals(daifu.getV_province()) && daifu.getV_province() != null) {

			if (daifu.getV_province().contains("内蒙古") || daifu.getV_province().contains("新疆")
					|| daifu.getV_province().contains("广西") || daifu.getV_province().contains("宁夏")
					|| daifu.getV_province().contains("西藏")) {

				if (daifu.getV_province().equals("内蒙古自治区") || daifu.getV_province().equals("新疆自治区")
						|| daifu.getV_province().equals("广西自治区") || daifu.getV_province().equals("宁夏自治区")
						|| daifu.getV_province().equals("西藏自治区")) {
					flag = true;
				}

			} else if (daifu.getV_province().contains("省") || daifu.getV_province().contains("市")) {
				flag = true;
			}
		}

		if (!"".equals(daifu.getV_city()) && daifu.getV_city() != null) {

			if (daifu.getV_city().contains("市")
					|| daifu.getV_city().contains("县") && daifu.getV_city().length() <= 10) {

				flag = true;

			}
		}
		return flag;
	}
	/**
	 * 代付查询
	 */
	@Override
	public Map<String, String> daifuQuery(DaifuQueryRequestEntity query) {

		PmsDaifuMerchantInfo daifuMerchantInfo = new PmsDaifuMerchantInfo();
		Map<String, String> mapParams = new HashMap<String, String>();

		daifuMerchantInfo.setBatchNo(query.getV_batch_no());
		daifuMerchantInfo.setMercId(query.getV_mid());
		daifuMerchantInfo.setIdentity(query.getV_identity());
		daifuMerchantInfo = pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(daifuMerchantInfo);
		if (daifuMerchantInfo != null) {
			mapParams.put("v_mid", query.getV_mid());
			mapParams.put("v_batch_no", query.getV_batch_no());
			mapParams.put("v_sum_amount", daifuMerchantInfo.getAmount());
			mapParams.put("v_amount", daifuMerchantInfo.getAmount());
			mapParams.put("v_code", "00");
			mapParams.put("v_msg", "请求成功");
			if ("0".equals(query.getV_type())) {
				mapParams.put("v_type", "0");
			}
			if ("1".equals(query.getV_type())) {
				mapParams.put("v_type", "1");
			}
			mapParams.put("v_identity", daifuMerchantInfo.getIdentity());
			mapParams.put("v_time", UtilDate.getDateFormatter());
			if ("00".equals(daifuMerchantInfo.getResponsecode())) {
				mapParams.put("v_status", "0000");
				mapParams.put("v_status_msg", "代付成功");
			} else if ("01".equals(daifuMerchantInfo.getResponsecode())
					|| "02".equals(daifuMerchantInfo.getResponsecode())) {
				mapParams.put("v_status", "1001");
				mapParams.put("v_status_msg", "代付失败");
			} else if ("200".equals(daifuMerchantInfo.getResponsecode())) {
				mapParams.put("v_status", "200");
				mapParams.put("v_status_msg", "代付中");
			}

		} else {
			mapParams.put("v_code", "17");
			mapParams.put("v_msg", "查询无此订单");
		}

		return mapParams;
	}

	public Map<String, String> ybQuick(String batchNo, Map<String, String> result,String orderId) {

		Map<String, String> params = new HashMap<>();
		params.put("customerNumber", YBUtil.customerNumber);
		params.put("batchNo", batchNo);
		params.put("product", "");
		params.put("orderId", orderId);
		// params.put("pageNo", "");
		// params.put("pageSize", "");

		String uri = YBUtil.paymentqueryUri;
		Map<String, String> yopresponsemap = YeepayService.yeepayYOP(params, uri);
		System.out.println(yopresponsemap);
		if ("BAC001".equals(yopresponsemap.get("errorCode"))) {
			result.put("respCode", "00");
			String list = yopresponsemap.get("list").replace("[", "").replace("]", "");
			com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSONObject.parseObject(list);
			result.put("accountNumber", json.getString("accountNumber"));
			result.put("amount", json.getString("amount"));
			result.put("bankBranchName", json.getString("bankBranchName"));
			result.put("bankCode", json.getString("bankCode"));
			result.put("bankName", json.getString("bankName"));
			result.put("bankTrxStatusCode", json.getString("bankTrxStatusCode"));
			result.put("batchNo", json.getString("batchNo"));
			result.put("fee", json.getString("fee"));
			result.put("feeType", json.getString("feeType"));
			result.put("finishDate", json.getString("finishDate"));
			result.put("orderId", json.getString("orderId"));
			result.put("transferStatusCode", json.getString("transferStatusCode"));
			result.put("urgency", json.getString("urgency"));
			result.put("urgencyType", json.getString("urgencyType"));

		} else {
			result.put("respCode", "01");
			result.putAll(yopresponsemap);
		}
		return result;
	}

	@Override
	public Map<String, String> cxQuick(String merId, String batchNo, Map<String, String> result) {
		ChinaInPayOnePayRequest cpssr = new ChinaInPayOnePayRequest();
		PmsBusinessPos pmsBusinessPos = selectKey(merId);
		cpssr.setMerchantNo("CX" + pmsBusinessPos.getBusinessnum());
		cpssr.setOrderNo(batchNo);
		cpssr.setService("payForAnotherOneSearch");
		cpssr.setVersion("V2.0");
		cpssr.setSignType("2");

		ChinaInPayRequest<ChinaInPayOnePayRequest> request = new ChinaInPayRequest<ChinaInPayOnePayRequest>();
		request.setTransDetail(cpssr);
		String url = CXUtil.url + "/agentPay";
		String serviceName = "payForAnotherOneSearch";
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
		log.info("代付返回参数：" + JSON.toJSON(results));
		com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSONObject.parseObject(results);

		result.put("respCode", json.getString("dealCode"));
		result.put("respMsg", json.getString("dealMsg"));
		result.put("orderNo", json.getString("orderNo"));
		result.put("amount", json.getString("orderAmount"));

		return result;
	}

	

	/*public List<String> readZipContext(String zipPath) throws IOException{
	       // String zipPath="zipFile/ziptestfile.zip";
	        @SuppressWarnings("resource")
			ZipFile zf=new ZipFile(zipPath);
	        List<String> list =new ArrayList<>();
	        InputStream in=new BufferedInputStream(new FileInputStream(zipPath));
	        @SuppressWarnings("resource")
			ZipInputStream zin=new ZipInputStream(in);
	        //ZipEntry 类用于表示 ZIP 文件条目。
	        ZipEntry ze;
	        while((ze=zin.getNextEntry())!=null){
	            if(ze.isDirectory()){
	                //为空的文件夹什么都不做
	            }else{

	                System.err.println("file:"+ze.getName()+"\nsize:"+ze.getSize()+"bytes");
	                if(ze.getSize()>0){
	                    BufferedReader reader;
	                    try {
	                        reader = new BufferedReader(new InputStreamReader(zf.getInputStream(ze), "utf-8"));
	                        String line=null;
	                        while((line=reader.readLine())!=null){
	                            //System.out.println(line);
	                            list.add(line);
	                        }
	                        reader.close();
	                    } catch (IOException e) {
	                        // TODO Auto-generated catch block
	                        e.printStackTrace();
	                    }
	                }

	            }
	        }
	        list.remove(0);
	        System.out.println(list);
	        return list;
	    }*/
	
	public List<String> readZipContext(String zipPath) throws IOException{
		File fil = new File(zipPath);
		List<String> list =new ArrayList<>();
        ZipInputStream zipIn = null;
        try {
            zipIn = new ZipInputStream(new FileInputStream(fil));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ZipEntry zipEn = null;
        /**
         * 需要读取zip文件项的内容时，需要ZipFile类的对象的getInputStream方法取得该项的内容，
         * 然后传递给InputStreamReader的构造方法创建InputStreamReader对象，
         * 最后使用此InputStreamReader对象创建BufferedReader实例
         * 至此已把zip文件项的内容读出到缓存中，可以遍历其内容
         */
        ZipFile zfil = null;
        try {
            zfil = new ZipFile(zipPath);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            while ((zipEn = zipIn.getNextEntry()) != null) {
                if (!zipEn.isDirectory()) { // 判断此zip项是否为目录
                    System.out.println(zipEn.getName() + ":\t");
                    /**
                     * 把是文件的zip项读出缓存，
                     * zfil.getInputStream(zipEn)：返回输入流读取指定zip文件条目的内容 zfil：new
                     * ZipFile();供阅读的zip文件 zipEn：zip文件中的某一项
                     */
                    BufferedReader buff = new BufferedReader(
                            new InputStreamReader(zfil.getInputStream(zipEn),"UTF-8"));
                    String str;
                    while ((str = buff.readLine()) != null) {
                        System.out.println("\t" + str);
                        list.add(str);
                    }
                    buff.close();
                }
                zipIn.closeEntry();// 关闭当前打开的项
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                zfil.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        list.remove(0);
        return list;
	    }

	@Override
	public PmsBusinessPos selectMer(String mer) {
		PmsBusinessPos businessPos=null;
		try {
			 businessPos = businessPosDao.searchById(mer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return businessPos;
	}

	@Override
	public int updataPayT1(Map<String, String> map) {
		int nus = pmsMerchantInfoDao.updataPayT1(map);
		return nus;
	}

	@Override
	public int updataPay(Map<String, String> map) {
		int nus = pmsMerchantInfoDao.updataPay(map);
		return nus;
	}

	@Override
	public List<PmsDaifuMerchantInfo> selectDaifu(PmsDaifuMerchantInfo info) {
		// TODO Auto-generated method stub
		return pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfos(info);
	}

	@Override
	public Map<String, String> selectPay(DaifuRequestEntity payRequest) {
		PmsBusinessPos pmsBusinessPos = selectKey(payRequest.getV_mid());
		Map<String, String> payshowParams =new HashMap<>();
		try {
			final String merCertPath= new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//ky//"+pmsBusinessPos.getBusinessnum()+".cer";
			final String pfxPath= new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//ky//"+pmsBusinessPos.getBusinessnum()+".pfx";
			final String pfxPwd= pmsBusinessPos.getKek();
			Map<String, String> reqMaps =new HashMap<>();
			reqMaps.put("version", "1.0.1");
			reqMaps.put("merchantId", pmsBusinessPos.getBusinessnum());
			reqMaps.put("batchApplyDate",payRequest.getV_time());
			YufuCipher cipher = null;
			YufuCipherSupport instance = null;
			cipher = YufuCipherSupport.getCipherInstance( merCertPath, pfxPath, pfxPwd,cipher,instance);
			//YufuCipher cipher = YufuCipherSupport.getCipherInstance(merCertPath, pfxPath, pfxPwd);
			String data = GsonUtil.objToJson(reqMaps);
			Map<String, String> params = GsonUtil.jsonToObj(data, Map.class);
			ParamPacket bo = cipher.doPack(params);
			TreeMap<String, String> map_param = new TreeMap<>();
			map_param.put("merchantId", pmsBusinessPos.getBusinessnum());
			map_param.put("data", URLEncoder.encode(bo.getData(), "utf-8"));
			map_param.put("enc", URLEncoder.encode(bo.getEnc(), "utf-8"));
			map_param.put("sign", URLEncoder.encode(bo.getSign(), "utf-8"));
			String urlPay ="";
			if("000001110100000812".equals(pmsBusinessPos.getBusinessnum())) {
				urlPay ="http://malltest.yfpayment.com/batchpay/batquery.do";
			}else {
				urlPay ="http://www.yfpayment.com/batchpay/batquery.do";
			}
			String returnStr = PostUtils.doPost(urlPay, map_param);

			if (returnStr != null && !"".equals(returnStr)) {
				// 二、验签解密
				returnStr = URLDecoder.decode(returnStr, "utf-8");
				System.out.println("URL解码后的置单应答结果：" + returnStr);
				TreeMap<String, String> boMap = JSON.parseObject(returnStr, new TypeReference<TreeMap<String, String>>() {
				});
				payshowParams = cipher.unPack(new ParamPacket(boMap.get("data"), boMap.get("enc"), boMap.get("sign")));
				System.out.println("解密后的置单应答结果：" + payshowParams);
				//{merchantDisctAmt=0, respDesc=调用接口成功, transTime=20180408105626, bpSerialNum=1001804081056248801, merchantId=000001110100000812, merchantOrderTime=20180408103938, merchantOrderAmt=100, currency=156, merchantOrderId=QP2018040810393862321312, version=1.0.0, respCode=0000, transStatus=01}
				
			} else {
				System.out.println("置单返回报文为空！");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return payshowParams;
	}

	@Override
	public PmsMerchantInfo selecrMerId(String mer) throws Exception {
		
		return pmsMerchantInfoDao.selectMercByMercId(mer);
	}

	@Override
	public Map<String, String> DownExcel(DaifuRequestEntity payRequest,String path) {
		Map<String, String> result =new HashMap<>();
		PmsBusinessPos pmsBusinessPos = selectKey(payRequest.getV_mid());
		String merCertPath = "";
		String pfxPath = "";
		try {
			merCertPath = new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//ky//"+pmsBusinessPos.getBusinessnum()+".cer";
			pfxPath = new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//ky//"+pmsBusinessPos.getBusinessnum()+".pfx";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final String pfxPwd= pmsBusinessPos.getKek();
		RefundChequeResultDownReq req = new RefundChequeResultDownReq();
		req.setMerchantId(pmsBusinessPos.getBusinessnum());
		req.setTransTime(payRequest.getV_time()); // 商户订单时间格式为：yyyyMMddHHmmss
		req.setVersion("1.0.1");
		log.info("发送前参数："+JSON.toJSONString(req));
		RefundChequeResultDownRsp refundChequeResultDown =DoYf.refundChequeResultDown(req, merCertPath, pfxPath, pfxPwd);
		log.info("refundChequeResultDown:"+JSON.toJSONString(refundChequeResultDown));
		
			if("0000".equals(refundChequeResultDown.getRespCode())) {
				log.info("path:"+path);
				FileDownReq req1 = new FileDownReq();
				req1.setFileName(refundChequeResultDown.getResultFileName());
				req1.setFileType("02");
				req1.setMerchantId(pmsBusinessPos.getBusinessnum());
				req1.setVersion("1.0.1");
				DoYf.downResourse(req1, path, merCertPath, pfxPath, pfxPwd);
				result.put("respCode", "00");
				result.put("respMsg", "下载成功");
			}else {
				result.put("respCode", "01");
				result.put("respMsg", "下载失败,"+refundChequeResultDown.getRespDesc());
			}
		
			return result;
	}

	@Override
	 public Map<String, String> jhjQuick(String mer, String orderId) {
	    Map<String, String> map = new HashMap<String, String>();
	    try {
	      com.uns.inf.api.model.Request req = new com.uns.inf.api.model.Request();
	      req.put("agentType", "01");
	      req.put("mchId", mer);
	      req.put("outTradeNo", orderId);
	      String param = SignatureUtil.getSignPlainText(req);
	      this.log.info("签名前参数：" + param);
	      String key = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALyPluyE1wnc+ONM0p/fp8PQu45f95dMsi4NBQYFhWd9rFtEy5gZUV8IzLJdXKyJ/EMgAs446p+G22L/EJjb8HeFmWx6tEu7JQkqu+fMfmb0DM7EXqJvAioJhKqwRBaG8zbF5aFgGVF6Nnk/YrILQCwkFAszD5MWCXTCEmSY3LzVAgMBAAECgYAmTzoHlbmmzFlYvOvyBVutYgQpGgBQogl1Z7nEjmybKSJSbLi8jzBEEaKc/nDssSAqdx96zH+Gp7x88XtqwwyosfOXAatzKaPMHdyf9TBQWdYkrNbM/JLby9r1sslLq2xyrXWgqMS+kOWS2p/JWTo6X3YLxi0Bi4lqNbryEa81xQJBAPfkZTTTPILkQMdP+jdV8RKE3/HCpc0LQEOILZouY5GD90tZm7AblmvquQ0HFfKd/LHVHymDbFdD8QVKRhLe11sCQQDCummhY2EkUOYyJjJ+aG+IiqJNQ8xsv6sjn6wF25aHOfNJPphWz6hf6ND6QxPMvFdKF8qs14FPfVzW7TrooyOPAkEAyjcJDBeI1CmIYk5uibdUqUu1Nw0WnXYhHTW4JX7UAD9Leq8FXpqSkUPvYp42HC0elp6JBh9MQL+OnEcjdH9N4wJAdtR9C2By8k9v+mB25c7jaSZ4nr/l6uMYE7gnqLd053aEsUjCfA9ix4xyopX2ajTw66UTKGCmZ5Sv5/SCw15ynwJBANanZMAeJvoBloTvK5kKvQTi5pyNd+drb3PYiMO+jZEa0DQ3+/04EbxvzSNoBvlAKSrL4kUwfHZGF27EXM1/tAM=";
	      String sign = xdt.util.utils.RSAUtils.sign(param.getBytes("UTF-8"), key, "SHA1WithRSA");
	      req.put("sign", sign);
	      this.log.info("签名：" + sign);
	      req.put("sign", sign);
	      this.log.info("请求前的参数：" + JSON.toJSONString(req));
	      String url = "http://mch.fintech2syx.com/cloud/collectpay/api/query.html";
	      String str = RequestUtils.sendPost(url, JSON.toJSONString(req), "UTF-8");
	      this.log.info("oem汇聚代付返回参数str:" + JSON.toJSONString(str));

	      if (!"".equals(str)) {
	        net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(str);
	        map.put("v_code", "00");
	        map.put("v_msg", "请求成功");
	        if ("0".equals(json.getString("returnCode"))) {
	          if ("0".equals(json.getString("resultCode")))
	            if ("SUCCESS".equals(json.getString("status"))) {
	              map.put("v_status", "0000");
	              map.put("v_status_msg", "代付成功");
	            } else if ("FAIL".equals(json.getString("status"))) {
	              map.put("v_status", "1001");
	              map.put("v_status_msg", "代付失败");
	            }
	        }
	        else {
	          map.put("v_status", "1001");
	          map.put("v_status_msg", "代付失败");
	        }
	      } else {
	        map.put("v_code", "01");
	        map.put("v_msg", "请求失败");
	      }

	    }
	    catch (Exception localException)
	    {
	    }

	    return map;
	  }

	@Override
	public Map<String, String> wfbQuick(String merId, String batchNo) {
		 Map<String, String> map = new HashMap<String, String>();
		    try {
		    	PmsBusinessPos pmsBusinessPos = selectKey(merId);
		    	TreeMap<String, String> req = new TreeMap<>();
		    	req.put("payKey",pmsBusinessPos.getBusinessnum());//
		    	req.put("outTradeNo",batchNo);
		        String paramSrc = RequestUtils.getParamSrc(req);
				log.info("签名前数据**********支付:" + paramSrc);
				String md5 = MD5Utils.md5(paramSrc+"&paySecret="+pmsBusinessPos.getKek(), "UTF-8").toUpperCase();//pmsBusinessPos.getKek()
				System.out.println(md5);
				req.put("sign", md5);
				log.info(JSON.toJSONString(req));
				String url ="http://192.144.172.91:8080/gateway/proxyPayQuery/query"; 
				String str = xdt.dto.scanCode.util.SimpleHttpUtils.httpPost(url, req);
				System.out.println(str);

		      if (!"".equals(str)) {
		        net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(str);
		        map.put("v_code", "00");
		        map.put("v_msg", "请求成功");
		        if ("0000".equals(json.getString("resultCode"))) {
		            if ("REMIT_SUCCESS".equals(json.getString("remitStatus"))) {
		              map.put("v_status", "0000");
		              map.put("v_status_msg", "代付成功");
		            } else if ("REMIT_FAIL".equals(json.getString("remitStatus"))||"CANCEL_FAIL".equals(json.getString("remitStatus"))||"RECEIVE_FAILURE".equals(json.getString("remitStatus"))) {
		              map.put("v_status", "1001");
		              map.put("v_status_msg", "代付失败");
		            }
		        }
		        else {
		          map.put("v_status", "1001");
		          map.put("v_status_msg", "代付失败");
		        }
		      } else {
		        map.put("v_code", "01");
		        map.put("v_msg", "请求失败");
		      }

		    }
		    catch (Exception localException)
		    {
		    }

		    return map;
	}

	@Override
	public Map<String, String> yszfQuick(String merId, String batchNo) {
		Map<String, String> resultMap = null;
		 Map<String, String> map = new HashMap<String, String>();
		 try {
			 	PmsDaifuMerchantInfo info =new PmsDaifuMerchantInfo();
		    	info.setBatchNo(batchNo);
		    	List<PmsDaifuMerchantInfo> list =selectDaifu(info);
		    	PmsBusinessPos pmsBusinessPos = selectKey(merId);
		    	TreeMap<String, String> req = new TreeMap<>();
		    	req.put("merchantNo",pmsBusinessPos.getBusinessnum());//
		    	req.put("tranFlow",merId+batchNo);
		    	req.put("version", "v1");
		    	req.put("channelNo", "04");
		    	req.put("tranCode", "1004");
		    	req.put("tranDate", new SimpleDateFormat("yyyyMMdd").format(new Date()));
		    	req.put("tranTime", new SimpleDateFormat("HHmmss").format(new Date()));
		    	req.put("oriTranFlow", batchNo);
		    	req.put("oriTranDate", list.get(0).getCreationdate().replace("-", "").substring(0,8));
		    	String cerPath=new File(this.getClass().getResource("/").getPath()).getParentFile()
						.getParentFile().getCanonicalPath() + "/ky/"+pmsBusinessPos.getBusinessnum()+".cer";
		        String keyStorePath=new File(this.getClass().getResource("/").getPath()).getParentFile()
						.getParentFile().getCanonicalPath() + "/ky/"+pmsBusinessPos.getBusinessnum()+".pfx";
		        
		        String keyPass=pmsBusinessPos.getKek();
		        
		        SecretConfig e = new SecretConfig(cerPath, keyStorePath, keyPass);
		        Secret secret = new Secret(e);
		        String paramSrc = RequestUtils.getParamSrc(req);
				log.info("签名前数据**********支付:" + paramSrc);
				String md5 = secret.sign(paramSrc);
				System.out.println(md5);
				req.put("sign", md5);
				log.info(JSON.toJSONString(req));
				String url ="https://paydemo.ielpm.com/paygate/v1/dfpay"; 
				String str = xdt.dto.scanCode.util.SimpleHttpUtils.httpPost(url, req);
				System.out.println(str);
		      if (!"".equals(str)) {
		    	resultMap = ResponseUtil.parseResponse(str, secret);
		        map.put("v_code", "00");
		        map.put("v_msg", "请求成功");
		        if ("0000".equals(resultMap.get("rtnCode"))) {
		        	if("0000".equals(resultMap.get("oriRtnCode"))) {
		        		map.put("v_status", "0000");
		        		map.put("v_status_msg", "代付成功");
		        	}else if("".equals(resultMap.get("oriRtnCode"))||"0030".equals(resultMap.get("oriRtnCode"))||"0002".equals(resultMap.get("oriRtnCode"))||"0003".equals(resultMap.get("oriRtnCode"))||"00R1".equals(resultMap.get("oriRtnCode"))||"9999".equals(resultMap.get("oriRtnCode"))) {
		        		
		        	}else {
		        		map.put("v_status", "1001");
				          map.put("v_status_msg", "代付失败");
		        	}
		        }
		      } else {
		        map.put("v_code", "01");
		        map.put("v_msg", "请求失败");
		      }

		    }
		    catch (Exception localException)
		    {
		    }
		return map;
	}
	
}
