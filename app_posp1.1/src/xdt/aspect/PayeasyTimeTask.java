package xdt.aspect;

import java.io.StringReader;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import com.capinfo.crypt.Md5;

import net.sf.json.JSONObject;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dto.payeasy.PayEasyQueryResponseEntity;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessPos;
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
public class PayeasyTimeTask {
	Logger logger = Logger.getLogger(PayeasyTimeTask.class);

	@Resource
	private IPmsAppTransInfoService pmsAppTransInfoService;
	@Resource
	private IPayeasyService payService;
	@Resource
	public IPmsAppTransInfoDao pmsAppTransInfoDao;
	@Resource
	public IPospTransInfoDAO pospTransInfoDAO;

	public void payTimeSelect() throws Exception {

		List<PospTransInfo> array1 = pospTransInfoDAO.selectPay();
		for (PospTransInfo pospTransInfo : array1) {
			logger.info("查询支付结果");

			logger.info("查询信息:{}" + array1);
			// 查询上游商户号
			PmsBusinessPos busInfo = payService.selectKey(pospTransInfo.getMerchantcode());
			String v_mid = busInfo.getBusinessnum();
			String[] array = pospTransInfo.getTransOrderId().split("-");

			StringBuffer a = new StringBuffer();

			a.append(array[0]);
			a.append("-");
			a.append(array[1].replace(array[1], v_mid));
			a.append("-");
			a.append(array[2]);

			logger.info("上送的订单号:" + a.toString());
			String v_oid = a.toString();

			// 查询字符串
			String queryString = "";

			// 请求地址
			String url = "http://api.yizhifubj.com/merchant/order/order_ack_oid_list.jsp";

			Md5 md5 = new Md5("");
			String str = v_mid + v_oid;
			logger.info("拼接后的字符串:" + str);
			md5.hmac_Md5(str, busInfo.getKek());
			byte b[] = md5.getDigest();
			String digestString = md5.stringify(b);
			logger.info("加密后的字符串:" + digestString);

			// 设置上送参数
			queryString = "v_mid=" + v_mid + "&v_oid=" + v_oid + "&v_mac=" + digestString;
			// 设置转发页面
			String path = url + "?" + queryString;
			logger.info("重定向 第三方：" + path);
			// response.sendRedirect(path.replace(" ", ""));
			HttpUtil hf = new HttpUtil();
			String xml = hf.sendPosts(path);
			logger.info("上游返回的xml数据" + xml);
			PayEasyQueryResponseEntity response = new PayEasyQueryResponseEntity();
			// 解析上游返回的xml文件
			StringReader read = new StringReader(xml);
			// 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
			InputSource source = new InputSource(read);
			// 创建一个新的SAXBuilder
			SAXBuilder sb = new SAXBuilder();
			// 通过输入源构造一个Document
			Document doc = sb.build(source);
			// 取的根元素
			Element root = doc.getRootElement();
			logger.info("根元素名称:" + root.getName());
			// 得到根元素所有子元素的集合
			List jiedian = root.getChildren();
			logger.info("根元素下的子元素:" + jiedian);
			// 获得XML中的命名空间（XML中未定义可不写）
			Namespace ns = root.getNamespace();
			Element et = null; // 定义messagehead下的内容
			Element et1 = null;// 定义messagebody下的内容
			// 获取messagehead下的内容
			et = (Element) jiedian.get(0);
			logger.info("状态" + et.getChild("status").getText());
			response.setV_status(et.getChild("status").getText());
			logger.info("状态描述" + et.getChild("statusdesc").getText());
			if ("0".equals(et.getChild("status").getText())) {
				// 获取messagebody下的内容
				et1 = (Element) jiedian.get(1);
				logger.info("body内容:" + et1);
				List zjiedian = et1.getChildren();
				logger.info("list集合:" + zjiedian);
				Namespace nss = et1.getNamespace();
				for (int j = 0; j < zjiedian.size(); j++) {
					Element xet = (Element) zjiedian.get(j);
					logger.info("支付方式" + xet.getChild("pmode").getText());
					response.setV_pmode(xet.getChild("pmode", nss).getText());
					logger.info("支付状态" + xet.getChild("pstatus").getText());
					response.setV_pstatus(xet.getChild("pstatus", nss).getText());
					logger.info("支付结果" + xet.getChild("pstring").getText());
					response.setV_pstring(xet.getChild("pstring", nss).getText());
					logger.info("金额" + xet.getChild("amount").getText());
					response.setV_amount(xet.getChild("amount", nss).getText());
					logger.info("币种" + xet.getChild("moneytype").getText());
					response.setV_moneytype(xet.getChild("moneytype", nss).getText());
					logger.info("是否已转账" + xet.getChild("isvirement").getText());
					response.setV_isvirement(xet.getChild("isvirement", nss).getText());
					logger.info("签名" + xet.getChild("sign", nss).getText());
					response.setV_sign(xet.getChild("sign", nss).getText());

				}
				logger.info("数据出来了！！！");
				// 解析后的数据
				logger.info("解析xml后的数据:{}" + response);
				// 订单信息
				PmsAppTransInfo trans = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
				logger.info("订单表信息" + trans);
				if ("1".equals(response.getV_pstatus())) {
					// 支付成功
					trans.setStatus(OrderStatusEnum.paySuccess.getStatus());
					trans.setFinishtime(UtilDate.getDateFormatter());
					// 修改订单
					int updateAppTrans = pmsAppTransInfoDao.update(trans);
					if (updateAppTrans == 1) {
						// log.info("修改余额");
						// 修改余额
						logger.info(trans);
						// updateMerchantBanlance(pmsAppTransInfo);
						// 更新流水表
						pospTransInfo.setResponsecode("00");
						pospTransInfo.setPospsn(pospTransInfo.getOrderId());
						logger.info("更新流水");
						logger.info(pospTransInfo);
						pospTransInfoDAO.updateByOrderId(pospTransInfo);
						Thread.interrupted();
					}
				} else if ("2".equals(response.getV_pstatus())) {
					trans.setStatus(OrderStatusEnum.waitingClientPay.getStatus());
					trans.setFinishtime(UtilDate.getDateFormatter());
					// 修改订单
					int updateAppTrans = pmsAppTransInfoDao.update(trans);
					if (updateAppTrans == 1) {
						// 更新流水表
						pospTransInfo.setResponsecode("20");
						pospTransInfo.setPospsn(pospTransInfo.getOrderId());
						logger.info("更新流水");
						logger.info(pospTransInfo);
						pospTransInfoDAO.updateByOrderId(pospTransInfo);
					}
				} else {
					trans.setStatus(OrderStatusEnum.payFail.getStatus());
					trans.setFinishtime(UtilDate.getDateFormatter());
					// 修改订单
					int updateAppTrans = pmsAppTransInfoDao.update(trans);
					if (updateAppTrans == 1) {
						// 更新流水表
						pospTransInfo.setResponsecode("02");
						pospTransInfo.setPospsn(pospTransInfo.getOrderId());
						logger.info("更新流水");
						logger.info(pospTransInfo);
						pospTransInfoDAO.updateByOrderId(pospTransInfo);
					}
				}
			}
		}

	}
}
