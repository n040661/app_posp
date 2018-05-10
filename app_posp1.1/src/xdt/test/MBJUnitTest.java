package xdt.test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.TreeMap;

import com.kspay.MD5Util;

import xdt.util.HttpURLConection;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.RequestUtils;

public class MBJUnitTest {

	public static void main(String[] args) throws UnsupportedEncodingException {

		// String str="123456";
		// String sign=MD5Util.MD5Encode(str + "072C15B8D473BB29");
		// System.out.println(sign);
		// TreeMap<String, String> result = new TreeMap<>();
		// result.put("orderId", "66662018011510436977");
		// result.put("transAmount", "1000");
		// result.put("respCode", "00");
		// result.put("respMsg", "交易成功");
		// result.put("merId", "10036048584");
		// result.put("status", "00");
		// String paramSrcs = RequestUtils.getParamSrc(result);
		// System.out.println("签名前数据**********魔宝支付:" + paramSrcs);
		// String md5 = MD5Utils.sign(paramSrcs, "6ef2d27145104fb181fc79f5c4779c1d",
		// "UTF-8");
		// String result1 =
		// HttpURLConection.httpURLConnectionPOST("http://101.200.38.184/gateway/notify/async/upin/UPIN20180115100025",
		// RequestUtils.getParamSrc(result) + "&sign=" + md5);
		// System.out.println(result1);

//		String aa = URLDecoder.decode(
//				"%CA%D5%BF%EE%D5%CB%BA%C5%B2%BB%C4%DC%CE%AA%BF%D5%BB%F2%D5%DF%D3%EB%B9%E6%B6%A8%B3%A4%B6%C8%B2%BB%B7%FB",
//				"GBK");
//
//		// String attch=new String("??".getBytes("ISO-8859-1"),"GBK");
//
//		// System.out.println(attch);
//
//		String params = "深圳市";
//		
//		String str="中国建设银行股份有限公司天津空港物流加工区支行";
//
//		boolean result = params.contains("市");
//		
//		System.out.println(str.length());
//
//		System.out.println(result);
		
		String url="https://cash.yeepay.com/cashier/std?sign=XSaNRkb3d637NlyWQNCBtu0bHdEIi4ivd0Nw1u8ocApDkPJoi_08Z9KfunTZqj25pGHUqfge_wxRHTH71j1ZXw651DioaFZAY9qX5-QV28fNMBU3fb0uBQoDa-EZtouyGSMM5RPtlBMLSC7PhSrvo6v3vYx2DGsWIQI4s5A6xnhgetNHH_X5AVcC4qaarTgeLFfiZZJ1h-t0XSa8iskCrmTnzEqJoXhZ3ShRjtKxLfdlD4KpvxC48N-DxVuZoMr3HTw10Obm0msoWoHKaueyGTNhibKxFXSt9DXhmQPN5TcadOCteB4OTHeaf-OIyF5PN9_sTEOptnfTI9WcJrrZVw$SHA256&merchantNo=10018465070&token=529F4A4E66BB604A1F15C51AA07D71F0DBA4EC42D4ED266EBF5605029C69F8AF&timestamp=1517886720&directPayType=YJZF&cardType=DEBIT&userNo=20180206111248&userType=MAC&ext={\"appId\":\"\",\"openId\":\"\",\"clientId\":\"\"}";
		
		String path=url.replace("https://cash.yeepay.com/cashier/std", "http://www.lssc888.com/shop/control/yibao_request.php");
		System.out.println(path);
	}

}
