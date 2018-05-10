package xdt.aspect;

import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import com.capinfo.crypt.Md5;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import net.sf.json.JSONObject;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPmsDaifuMerchantInfoDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dto.payeasy.PayEasyQueryResponseEntity;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PospTransInfo;
import xdt.service.IPayeasyService;
import xdt.service.IPmsAppTransInfoService;
import xdt.service.JsdsQrCodeService;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;
import xdt.util.OrderStatusEnum;
import xdt.util.RSAUtil;
import xdt.util.UtilDate;

@Component
public class JbbTransferTimeTask {
	Logger logger = Logger.getLogger(JbbTransferTimeTask.class);

	@Resource
	private IPmsAppTransInfoService pmsAppTransInfoService;
	@Resource
	private IPayeasyService payService;
	@Resource
	public IPmsAppTransInfoDao pmsAppTransInfoDao;
	@Resource
	public IPospTransInfoDAO pospTransInfoDAO;
	@Resource
	private IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao;
	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao; // 商户信息服务层

	// 代付接口
	private static final String orderquery_URL = "http://pay.yizhifubj.com/merchant/virement/mer_payment_status_utf8.jsp";

	@SuppressWarnings("unused")
	public void daifuTimeSelect() throws Exception {

		List<PmsDaifuMerchantInfo> list = pmsDaifuMerchantInfoDao.selectDaifu2();
		for (PmsDaifuMerchantInfo pmsDaifuMerchantInfo : list) {
			PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
			merchantinfo.setMercId(pmsDaifuMerchantInfo.getMercId());
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
					logger.info("参数错误,没有欧单编号");
				}
				// 判断是否为正式商户
				if ("60".equals(merchantinfo.getMercSts())) {
					// 实际金额
					logger.info("查询支付结果");

					logger.info("查询信息:{}" + list);
					// 查询上游商户号
					PmsBusinessPos busInfo = payService.selectKey(pmsDaifuMerchantInfo.getMercId());
					String v_mid1 = busInfo.getBusinessnum();
					String v_data0 = pmsDaifuMerchantInfo.getIdentity();
					Md5 md51 = new Md5("");
					logger.info("转码之后的数据:" + URLEncoder.encode(v_data0, "utf-8"));
					md51.hmac_Md5(v_mid1 + URLEncoder.encode(v_data0, "utf-8"), busInfo.getKek());
					byte[] b0 = md51.getDigest();
					String v_mac1 = Md5.stringify(b0);
					String queryString1 = "v_mid=" + v_mid1 + "&v_data=" + v_data0 + "&v_mac=" + v_mac1;
					this.logger.info("上送的参数:" + queryString1);
					String path1 = orderquery_URL + "?" + queryString1;
					this.logger.info("重定向 第三方：" + path1);
					Client cc1 = Client.create();
					WebResource rr1 = cc1.resource(orderquery_URL);
					MultivaluedMap queryParams1 = new MultivaluedMapImpl();
					queryParams1.add("v_mid", v_mid1); // 商户编号
					queryParams1.add("v_data", java.net.URLEncoder.encode(v_data0, "GBK"));
					queryParams1.add("v_mac", v_mac1);
					logger.info("向上游发送的数据:" + queryParams1);
					String xml1 = rr1.queryParams(queryParams1).get(String.class);

					this.logger.info("返回的xml数据:" + xml1);

					StringReader read1 = new StringReader(xml1);

					InputSource source1 = new InputSource(read1);

					SAXBuilder sb1 = new SAXBuilder();

					Document doc1 = sb1.build(source1);

					Element root1 = doc1.getRootElement();
					this.logger.info("根元素名称:" + root1.getName());

					this.logger.info("状态值" + root1.getChild("status").getText());
					String status1 = root1.getChild("status").getText();
					logger.info("查询代付状态:" + status1);
					if ("1".equals(status1)) {

						this.logger.info("状态描述" + root1.getChild("statusdesc").getText());
						String statusdesc = root1.getChild("statusdesc").getText();
						// 代付成功
						PmsDaifuMerchantInfo pdf = new PmsDaifuMerchantInfo();

						logger.info("上送的批次号:" + pmsDaifuMerchantInfo.getBatchNo());

						pdf.setBatchNo(pmsDaifuMerchantInfo.getBatchNo());
						pdf.setResponsecode("00");
						pmsDaifuMerchantInfoDao.update(pdf);

					} else if ("2".equals(status1)) {
						// 代付
						PmsDaifuMerchantInfo pdf = new PmsDaifuMerchantInfo();

						logger.info("上送的批次号:" + pmsDaifuMerchantInfo.getBatchNo());

						pdf.setBatchNo(pmsDaifuMerchantInfo.getBatchNo());
						pdf.setResponsecode("200");
						pmsDaifuMerchantInfoDao.update(pdf);
					} else if ("0".equals(status1)) {
						// 代付
						PmsDaifuMerchantInfo pdf = new PmsDaifuMerchantInfo();

						logger.info("上送的批次号:" + pmsDaifuMerchantInfo.getBatchNo());

						pdf.setBatchNo(pmsDaifuMerchantInfo.getBatchNo());
						pdf.setResponsecode("200");
						pmsDaifuMerchantInfoDao.update(pdf);
					} else if ("4".equals(status1)) {
						// 代付
						PmsDaifuMerchantInfo pdf = new PmsDaifuMerchantInfo();

						logger.info("上送的批次号:" + pmsDaifuMerchantInfo.getBatchNo());

						pdf.setBatchNo(pmsDaifuMerchantInfo.getBatchNo());
						pdf.setResponsecode("200");
						pmsDaifuMerchantInfoDao.update(pdf);
					} else if ("3".equals(status1)) {
						// 代付
						PmsDaifuMerchantInfo pdf = new PmsDaifuMerchantInfo();

						logger.info("上送的批次号:" + pmsDaifuMerchantInfo.getBatchNo());

						pdf.setBatchNo(pmsDaifuMerchantInfo.getBatchNo());
						pdf.setResponsecode("02");
						pmsDaifuMerchantInfoDao.update(pdf);
					}else {
						// 代付
						PmsDaifuMerchantInfo pdf = new PmsDaifuMerchantInfo();

						logger.info("上送的批次号:" + pmsDaifuMerchantInfo.getBatchNo());

						pdf.setBatchNo(pmsDaifuMerchantInfo.getBatchNo());
						pdf.setResponsecode("01");
						pmsDaifuMerchantInfoDao.update(pdf);
					}

				} else {
					// 请求参数为空
					logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
				}

			}
		}

	}
}
