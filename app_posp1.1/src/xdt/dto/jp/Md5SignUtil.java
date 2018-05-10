package xdt.dto.jp;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by yangkuo on 17/3/15.
 *
 */
public class Md5SignUtil {
    private static final Logger logger = LoggerFactory.getLogger(Md5SignUtil.class);

    private static char[] Digit = { '0','1','2','3','4','5','6','7','8','9', 'a','b','c','d','e','f' };

    /**
     * 对字符串进行 MD5 加密
     * @param str 待加密字符串
     * @return 加密后字符串
     */
    public static String md5(String str) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes("UTF-8"));
        }catch(Exception ex) {
            logger.error("异常-", ex);
            throw new RuntimeException(ex.getMessage());
        }
        byte[] encodedValue = md5.digest();
        int j = encodedValue.length;
        char finalValue[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte encoded = encodedValue[i];
            finalValue[k++] = Digit[encoded >> 4 & 0xf];
            finalValue[k++] = Digit[encoded & 0xf];
        }

        return new String(finalValue);
    }

    public static String md5(Map<String, String> paramMap, String key) {
        TreeMap<String, String> signMap = new TreeMap<>(paramMap);
        StringBuilder builder = new StringBuilder();
        for (String k : signMap.keySet()) {
            builder.append(signMap.get(k)).append("|");
        }

        builder.append(key);
        String reqParam = builder.toString();

        logger.info("reqParam:" + reqParam);
        return md5(reqParam);
    }

    public static void main(String[] arg) throws Exception {
        Map<String, String> routerParaMap = new HashMap<>();
        routerParaMap.put("amount", "1000");          // 金额
        routerParaMap.put("autoTime", "20170315");         // 处理时间
        routerParaMap.put("orderId", "10002000300040066");    // 订单号
        routerParaMap.put("orderSts", "S");   // 订单状态
        routerParaMap.put("coreOrderId", "170223130060021552");// 支付平台订单号
        routerParaMap.put("checkDate", "20170223");       // 对账日期
        routerParaMap.put("txDesc", "");       // 对账日期
        // e35d67fbee73b46be03baff7b1ca9059

        System.out.println("sign:" + Md5SignUtil.md5Sign(routerParaMap, "aa", "GB18030", null));
    }

    public static String md5Sign(Map<String, String> params, String signKey) {
        return md5Sign(params, signKey, "UTF-8", null);
    }

    public static String md5Sign(Map<String, String> params, String signKey, String charset, String connector) {
        params = removeNullFromMap(params);
        if (StringUtils.isBlank(connector)) {
            connector = "&";
        }

        List<Map.Entry<String, String>> entryList = new ArrayList<>(
            params.entrySet());

        Collections.sort(entryList, new Comparator<Map.Entry<String, String>>() {

            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return (o1.getKey() + "=" + o1.getValue())
                    .compareTo(o2.getKey() + "=" + o2.getValue());
            }
        });
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : entryList) {
            if (sb.length() > 0) {
                sb.append(connector);
            }
            String value = entry.getValue();
            if(!StringUtils.isEmpty(value.trim())) {
                sb.append(entry.getKey()).append("=").append(value);
            }
        }
        sb.append(signKey);
        logger.debug("md5Sign sb:{}", sb.toString());
//        System.out.println("md5Sign sb" + sb);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return byte2hexString(md.digest(sb.toString().getBytes(charset)));
        } catch (Exception ex) {
            throw new RuntimeException("md5 sign error !", ex);
        }
    }


    /**
     * 移除值为空的数据项
     */
    public static Map<String, String> removeNullFromMap(Map<String, String> map) {
        Map<String, String> resultMap = new HashMap<>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue() != null && !"".equals(entry.getValue()) && !"null".equalsIgnoreCase(entry.getValue())) {
                resultMap.put(entry.getKey(), entry.getValue());
            }
        }
        return resultMap;
    }

    /**
     * byte[] 转化为十六进制字符串
     */
    public static String byte2hexString(byte[] b) {
        StringBuilder sb = new StringBuilder("");
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                sb.append("0").append(stmp);
            } else {
                sb.append(stmp);
            }
        }
        return sb.toString();
    }
}