package xdt.dto.nbs.register;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdt.quickpay.nbs.common.util.JSONUtil;
import xdt.quickpay.nbs.common.util.SignatureUtil;
import xdt.quickpay.nbs.common.util.StringUtil;


/**
 * 对账单下载测试Demo
 *
 * @author zhang.hui@pufubao.net
 */

public class TestJsonDownloadBill {

	public static void main(String[] args) {
		// step 1 init paramers
		String serviceType = "CHECK_ORDER";
		String agentNum = "A147859729334710584";
		String key = "6d4b66d236e041138b74a5593ee0a3d4";
		// Date now = new Date();
		// String orderDate = DateUtil.parseDate(DateUtil.addDays(now, -1),
		// DateUtil.DATE_STYLE_YYYYMMDD);
		String orderDate = "20161123";
		BRCBJSONDownloadBill requst = new BRCBJSONDownloadBill(serviceType, agentNum, orderDate, key);

		// step 2 send post
		String path = "D://ing/" + "brcbstatement" + orderDate + ".csv";
		String url = "http://101.200.59.129:20030/customer/service";
		try {
			String jsonParamsStr = JSONUtil.toJSONString(requst.toMap());
			HttpclientJsonRespFileUtil httpclient = new HttpclientJsonRespFileUtil(url);
			httpclient.sendJsonPostDownloadFile(jsonParamsStr, path, "UTF-8");
			System.out.println("下载对账单完成。");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("错误信息： " + e);
		}

	}
}

class BRCBJSONDownloadBill {
	private String serviceType;
	private String agentNum;
	private String orderDate;
	private String sign;

	public BRCBJSONDownloadBill(String serviceType, String agentNum, String orderDate, String key) {
		setServiceType(serviceType);
		setAgentNum(agentNum);
		setOrderDate(orderDate);
		Logger log = LoggerFactory.getLogger(this.getClass());
		String sign = SignatureUtil.getSign(toMap(), key,log);
		setSign(sign);
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getAgentNum() {
		return agentNum;
	}

	public void setAgentNum(String agentNum) {
		this.agentNum = agentNum;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			Object obj;
			try {
				obj = field.get(this);
				if (obj != null && StringUtil.isNotBlank(String.valueOf(obj))) {
					map.put(field.getName(), obj);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return map;
	}

}
