package xdt.test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import xdt.quickpay.daikou.model.DaiKouResponseEntity;

public class WzfUtilTest {

	public static void main(String[] args) {

		String str = "acountDate=&amount=500&bizCode=B001&callbackUrl=http://60.28.24.164:8102/app_posp/dk/wzfbgPayResult.action&charSet=UTF-8&confirmGoodsExpireTime=&customerEmail=&debitAcc=&debitName=&goodsDesc=&goodsId=&goodsName=测试&interfaceVersion=2.0.0.0&merExtend=&merNo=301101910008366&merUserId=&merWhDetails=YDK|Y17112036610000007860|&mobileNo=&orderDate=20171121&orderNo=20171121163140281&payJnlno=&payTime=&payWhDetails=&reqIp=&reqSysNo=&reqTime=20171121163143&signMsg=c6xQ1GeRPVR0ItmqhbISaA/0BPodFZ0dugUCjTzhkvd/QJWUiXhCkvz4XcsEamLEKKoR0AhlzAgsYSXiMhBhAWdJYjCdsY+H5QJdSzecgBngxw3D0gfDpD4M0Cy+P2IIYCsMWTGqAXywQwwgDTVgiIZ1o9LMsn5uGqkiPs7Cv/8uTbdkTMEzzLda+CzOPumAmcEj+nJXk+KaMTw51VkUAtwXTosIFb0nosTnteJ5prJJI1MINYGp8hYKYFUyTO+tS9IQlCR4FBPoDl0KWVPS76fc7gzzXfGFpM9T8ixD3yE0MegHMrGEUGNJ0RtwTTflHSxxOp6ea4Eac5xmai1TtA==&signType=RSA_SHA256&tradeMode=0001&transCode=error&transDesc=商户不存在&transDis=[error],商户不存在&transRst=2&woAcc=&woType=";

		String[] array = str.split("\\&");
		
//		Map<String, String> retMap = new HashMap<String, String>();
//
//		if (array[0] != null) {
//			String[] acountDate = array[0].split("\\=");
//			
//			retMap.put("acountDate", acountDate[1]);
//		}
//		if (array[1] != null) {
//			String[] amount = array[1].split("\\=");
//			retMap.put("amount", amount[1]);
//		}
//		if (array[2] != null) {
//			String[] charSet = array[2].split("\\=");
//			retMap.put("charSet", charSet[1]);
//		}
//		if (array[5] != null) {
//			String[] orderDate = array[3].split("\\=");
//			retMap.put("orderDate", orderDate[1]);
//		}
//		if (array[6] != null) {
//			String[] orderNo = array[6].split("\\=");
//			retMap.put("orderId", orderNo[1]);
//		}
//
//		if (array[7] != null) {
//			String[] orderState = array[7].split("\\=");
//			retMap.put("orderState", orderState[1]);
//		}
//		if (array[8] != null) {
//			String[] orderType = array[8].split("\\=");
//			retMap.put("orderType", orderType[1]);
//		}
//		if (array[9] != null) {
//			String[] payJournl = array[9].split("\\=");
//			retMap.put("payJournl", payJournl[1]);
//		}
//		if (array[12] != null) {
//			String[] queryResult = array[20].split("\\=");
//			retMap.put("queryResult", queryResult[1]);
//		}
//
//		if (array[19] != null) {
//			String[] tradeMode = array[19].split("\\=");
//			retMap.put("tradeMode", tradeMode[1]);
//		}
	

		for (int i = 0; i < array.length; i++) {

			System.out.print("第" + i + "数组元素" + array[i] + "\n");

		}

	}

}
