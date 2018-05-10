package xdt.quickpay.payeasy.util;

import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import com.capinfo.crypt.Md5;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import net.sf.json.JSONObject;
import xdt.controller.HFQPayAction;
import xdt.controller.jsds.JsdsQrCodeAction;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPmsDaifuMerchantInfoDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dao.OriginalOrderInfoDao;
import xdt.dto.jsds.JsdsRequestDto;
import xdt.dto.jsds.JsdsResponseDto;
import xdt.dto.payeasy.DaifuRequestEntity;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PospTransInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hengfeng.util.HttpClientUtil;
import xdt.quickpay.qianlong.model.PayResponseEntity;
import xdt.quickpay.qianlong.util.QLPostThread;
import xdt.schedule.ThreadPool;
import xdt.service.JsdsQrCodeService;
import xdt.util.HttpURLConection;

public class PayeasyThread extends Thread {

	public static final Logger logger = Logger.getLogger(PayeasyThread.class);
	
	public String url;

	public PmsDaifuMerchantInfo entity;

	public PmsBusinessPos busInfo;

	public IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao;
	
	public PmsMerchantInfo merchantinfo;

	public IPmsMerchantInfoDao pmsMerchantInfoDao; // 商户信息服务层
	
	public PayeasyThread(String url, PmsDaifuMerchantInfo entity, PmsBusinessPos busInfo,
			IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao, PmsMerchantInfo merchantinfo,
			IPmsMerchantInfoDao pmsMerchantInfoDao) {
		this.url = url;
		this.entity = entity;
		this.busInfo = busInfo;
		this.pmsDaifuMerchantInfoDao = pmsDaifuMerchantInfoDao;
		this.merchantinfo = merchantinfo;
		this.pmsMerchantInfoDao = pmsMerchantInfoDao;
	}
	@Override
	public synchronized void run() {

		// 线程处理
		// 1、先查询本地库订单是否是完成状态
		// 2、如果是完成状态跳过第三方查询并结束； 否则进行第三方查询 分隔时间进行查询在进行下一步处理
		// 3、第三方查询结果后根据结果处理本地订单并结束

		try {
			Thread.sleep(2000);
			for (int i = 0; i < 1000; i++) {
				logger.info("进入线程中");
				String v_mid1 = busInfo.getBusinessnum();
				String v_data0 = entity.getIdentity();
				Md5 md51 = new Md5("");
				logger.info("线程转码之后的数据:" + URLEncoder.encode(v_data0, "utf-8"));
				md51.hmac_Md5(v_mid1 + URLEncoder.encode(v_data0, "utf-8"), busInfo.getKek());
				byte[] b0 = md51.getDigest();
				String v_mac1 = Md5.stringify(b0);
				String queryString1 = "v_mid=" + v_mid1 + "&v_data=" + v_data0 + "&v_mac=" + v_mac1;
				this.logger.info("线程上送的参数:" + queryString1);
				String path1 = url+ "?" + queryString1;
				this.logger.info("线程重定向 第三方：" + path1);
				Client cc = Client.create();
				WebResource rr1 = cc.resource(url);
				MultivaluedMap queryParams1 = new MultivaluedMapImpl();
				queryParams1.add("线程v_mid", v_mid1); // 商户编号
				queryParams1.add("线程v_data", java.net.URLEncoder.encode(v_data0, "GBK"));
				queryParams1.add("线程v_mac", v_mac1);
				logger.info("线程向上游发送的数据:" + queryParams1);
				String xml1 = rr1.queryParams(queryParams1).get(String.class);

				this.logger.info("线程返回的xml数据:" + xml1);

				StringReader read1 = new StringReader(xml1);

				InputSource source1 = new InputSource(read1);

				SAXBuilder sb1 = new SAXBuilder();

				Document doc1 = sb1.build(source1);

				Element root1 = doc1.getRootElement();
				this.logger.info("线程根元素名称:" + root1.getName());

				this.logger.info("线程状态值" + root1.getChild("status").getText());
				String status1 = root1.getChild("status").getText();
				logger.info("线程查询代付状态:" + status1);
				if ("1".equals(status1))
				{
					BigDecimal b3 = new BigDecimal(0.00);
					BigDecimal volumn = new BigDecimal("0");
					// 实际金额
					Double factAmount = Double.parseDouble(entity.getAmount());
					BigDecimal b1 = new BigDecimal(factAmount.toString());
					BigDecimal b2 = new BigDecimal(merchantinfo.getPositionT1().toString());
					b3 = new BigDecimal(merchantinfo.getPoundage());
					logger.info("线程代付金额:" + b1.multiply(new BigDecimal(100)).doubleValue());
					logger.info("线程可用额度:" + b2.doubleValue());
					logger.info("线程每笔代付手续费:" + b3);
					volumn = new BigDecimal(1);
					logger.info("线程总笔数:" + volumn.toString());
					double fee = volumn.multiply(b3).doubleValue() * 100;
					logger.info("线程代付总手续费:" + fee);
					// 清算金额
					Double payAmount = factAmount * 100 + fee;
					BigDecimal b4 = new BigDecimal(payAmount.toString());
					logger.info("线程清算金额:" + b4);
					Double surAmount = b2.subtract(b4).doubleValue();
					logger.info("线程剩余可用额度:" + surAmount.toString());
					merchantinfo.setPositionT1(surAmount.toString());
					int num = pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
					if (num > 0) {
						this.logger.info("线程状态描述" + root1.getChild("statusdesc").getText());
						String statusdesc = root1.getChild("statusdesc").getText();
						PmsDaifuMerchantInfo pdf = new PmsDaifuMerchantInfo();
						logger.info("线程上送的批次号:" + entity.getBatchNo());
						pdf.setBatchNo(entity.getBatchNo());
						pdf.setResponsecode("00");
					    pmsDaifuMerchantInfoDao.update(pdf);
					    Thread.interrupted();
					} else {
						logger.info("代付失败" + status1);
						Thread.interrupted();
					}
					
				}else{
					logger.info("代付失败" + status1);
					Thread.interrupted();
				}

				}
				//Thread.sleep(50000);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
