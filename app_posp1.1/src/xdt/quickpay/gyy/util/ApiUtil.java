package xdt.quickpay.gyy.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class ApiUtil {

	public static String sortMap(Map<String, Object> map) {

		ArrayList<String> list = new ArrayList<String>();

		for (Map.Entry<String, Object> entry : map.entrySet()) {

			if (entry.getValue() != null && entry.getValue() != "") {

				list.add(entry.getKey() + "=" + entry.getValue() + "&");
			}

		}

		int size = list.size();

		String[] arrayToSort = list.toArray(new String[size]);

		Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < size; i++) {
			sb.append(arrayToSort[i]);
		}
		String result = sb.toString().substring(0, sb.length() - 1);
		// System.out.println("排序后字符串:" + result);
		return result;
	}

	public static synchronized String sendPost(String url, String param) {
		PrintWriter out = null;
		InputStream in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			HttpURLConnection conn = (HttpURLConnection) realUrl
					.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			if (param != null) {
				out.print(param);
			}
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = conn.getInputStream();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			int all = 0;
			byte[] b = new byte[1536];
			while ((all = conn.getInputStream().read(b)) != -1) {
				outputStream.write(b, 0, all);
			}
			result = new String(outputStream.toByteArray(), "UTF-8").trim();
			in.close();
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static <K, V> Map<K, V> toMap(String json) {
		if (StringUtils.isBlank(json)) {
			return Collections.emptyMap();
		}
		JSONObject jsonObject = JSONObject.fromObject(json);
		return (Map<K, V>) JSONObject.toBean(jsonObject, HashMap.class);
	}

	public static synchronized String newDateMore() {
		return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
	}

}
