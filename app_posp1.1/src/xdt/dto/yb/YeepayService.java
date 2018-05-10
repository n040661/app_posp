package xdt.dto.yb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import com.yeepay.g3.sdk.yop.client.YopClient3;
import com.yeepay.g3.sdk.yop.client.YopRequest;
import com.yeepay.g3.sdk.yop.client.YopResponse;
import com.yeepay.g3.yop.sdk.api.StdApi;

public class YeepayService {

	static String customerNumber = YBUtil.customerNumber;
	static String baseUri = YBUtil.baseUri;
	// static String app_key= Config.getInstance().getValue("app_key");

	public static Map<String, String> yeepayYOP(Map<String, String> map, String Uri) {

		YopRequest yoprequest = new YopRequest("OPR:" + customerNumber, "", baseUri);
		// YopRequest yoprequest =new YopRequest(app_key,"",baseUri);
		Map<String, String> result = new HashMap<String, String>();
		Set<Entry<String, String>> entry = map.entrySet();
		for (Entry<String, String> s : entry) {
			yoprequest.addParam(s.getKey(), s.getValue());
		}
		System.out.println("yoprequest:" + yoprequest.getParams());

		// 向YOP发请求
		YopResponse yopresponse = YopClient3.postRsa(Uri, yoprequest);
		
		
		System.out.println("请求YOP之后的结果：" + yopresponse.getStringResult());
		// 对结果进行处理
		if ("FAILURE".equals(yopresponse.getState())) {
			if (yopresponse.getError() != null)
				result.put("errorCode", yopresponse.getError().getCode());
			result.put("errorMsg", yopresponse.getError().getMessage());
			System.err.println("错误明细：" + yopresponse.getError().getSubErrors());
			System.out.println("系统处理异常结果：" + result);
			return result;
		}
		// 成功则进行相关处理
		if (yopresponse.getStringResult() != null) {
			result = parseResponse(yopresponse.getStringResult());

		}

		return result;
	}

	// 将获取到的response转换成json格式
	public static Map<String, String> parseResponse(String yopresponse) {

		Map<String, String> jsonMap = new HashMap<>();
		jsonMap = JSON.parseObject(yopresponse, new TypeReference<TreeMap<String, String>>() {
		});
		System.out.println("将response转化为map格式之后: " + jsonMap);
		return jsonMap;
	}

	public static String getRandom(int length) {
		Random random = new Random();
		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < length; i++) {
			ret.append(Integer.toString(random.nextInt(10)));
		}
		return ret.toString();
	}

	public static String yosFile(Map<String, String> params, String path) {
		StdApi apidApi = new StdApi();
		InputStream inputStream = null;
		OutputStream outputStream = null;

		String method = params.get("method");
		String date = params.get("date");
		String dataType = params.get("dataType");

		String fileName = "";
		String filePath = "";
		try {

			inputStream = apidApi.remitDayBillDownload(customerNumber, date, dataType);
			fileName = "remitday-" + dataType + "-" + date + ".csv";

			filePath = path + File.separator + fileName;
			System.out.println("filePath=====" + filePath);
			outputStream = new FileOutputStream(new File(filePath));

			byte[] bs = new byte[1024];
			int readNum;
			while ((readNum = inputStream.read(bs)) != -1) {
				outputStream.write(bs, 0, readNum);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		} finally {
			try {
				outputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				inputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return filePath;
	}

}

// }
