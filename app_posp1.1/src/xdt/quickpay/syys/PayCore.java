package xdt.quickpay.syys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <p>
 * Title:PayCore
 * </p>
 * <p/>
 * <p>
 * Description:
 * </p>
 * <p/>
 * <p>
 * Copyright:Copyright (c) 2016
 * </p>
 * <p/>
 * <p>
 * Company:creditease
 * </p>
 *
 * @author yingjiecui
 * @vrsion v1.0.0
 */
public class PayCore {
    public static final String _SIGN_TYPE = "sign_type";
    public static final String _SIGN_INFO = "sign_info";
    public static final String _SIGN_SCOPE = "sign_scope";
    public static final String _CHARGE = "charge";
    public static final String _APP_ID = "app_id";
    public static final String _CHANNEL = "channel";
    public static final String _MERCH_ID = "merch_id";
    public static final String _VERSION = "version";
    public static final String _AMOUNT = "amount";

    public static Map<String, ?> paraFilter(Map<String, ?> sArray) {
        Map<String, Object> result = new HashMap<String, Object>();
        if ((sArray == null) || (sArray.size() <= 0)) {
            return result;
        }
        for (String key : sArray.keySet()) {
            Object value = sArray.get(key);
            if ((value == null) || value.equals("") || key.equalsIgnoreCase(_SIGN_INFO) || key.equalsIgnoreCase(_SIGN_TYPE)) {
                continue;
            }
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, ?> m = (Map<String, ?>) value;
                result.put(key, paraFilter(m));
            } else if (value instanceof List) {
                continue;// 不应包含多集合数�?      
            } else {
                result.put(key, value);
            }
        }
        return result;
    }

    /**
     * 把数组所有元素排序，并按照�?参数=参数值�?的模式用�?”字符拼接成字符�?     *
     * @param params �?��排序并参与字符拼接的参数�?     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, ?> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        StringBuffer prestr = new StringBuffer("");
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object o = params.get(key);
            String value = String.valueOf(o);
            if (o instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, ?> m = (Map<String, ?>) o;
                value = "{" + createLinkString(m) + "}";
            }

            if (i == (keys.size() - 1)) {// 拼接时，不包括最后一�?字符
                prestr.append(key + "=" + value);
            } else {
                prestr.append(key + "=" + value + "&");
            }
        }
        return prestr.toString();
    }

    /**
     * 验证签名
     *
     * @param objMap
     * @return
     * @throws Exception
     */
    public static boolean verifySign(Map<String, ?> objMap, String key) throws Exception {
        boolean sta = false;
        String hexSignTyep = String.valueOf(objMap.get(PayCore._SIGN_TYPE));
        String hexSign = String.valueOf(objMap.get(PayCore._SIGN_INFO));
        // 得到带签名数�?      
        Map<String, ?> filterMap = PayCore.paraFilter(objMap);
        String linkStr = PayCore.createLinkString(filterMap);
        if ("MD5".equalsIgnoreCase(hexSignTyep)) {
            String md5Key = key;
            sta = md5VerifySign(linkStr, hexSign, md5Key);
        } else if ("NONE".equalsIgnoreCase(hexSignTyep)) {
            sta = true;
        }
        return sta;
    }

    /**
     * 验证接收付钱拉�?知内容签名信�?     *
     * @param objMap
     * @return
     * @throws Exception
     */
    public static boolean verifyNotifySign(Map<String, ?> objMap, String key) throws Exception {
        boolean sta = false;
        String hexSignTyep = String.valueOf(objMap.get(PayCore._SIGN_TYPE));
        String hexSign = String.valueOf(objMap.get(PayCore._SIGN_INFO));
        // 得到带签名数�?       
        Map<String, ?> filterMap = PayCore.paraFilter(objMap);
        String linkStr = PayCore.createLinkString(filterMap);
        if  ("MD5".equalsIgnoreCase(hexSignTyep)) {
            String md5Key = key;
            sta = md5VerifySign(linkStr, hexSign, md5Key);
        } else if ("NONE".equalsIgnoreCase(hexSignTyep)) {
            sta = true;
        }
        return sta;
    }

   
    /**
     * md5验证签名
     *
     * @param linkStr
     * @param hexSign
     * @param md5Key
     * @return
     * @throws Exception
     */
    public static boolean md5VerifySign(String linkStr, String hexSign, String md5Key) throws Exception {
        String templinkStr = linkStr + "&key=" + md5Key;
        String md5Hex = MD5.sign(templinkStr, "UTF-8");
        boolean sta = hexSign.equalsIgnoreCase(md5Hex);
        return sta;
    }

    public static String sign(Map<String, ?> objMap, String signType, String key) throws Exception {
        String hexSign = "none";
        Map<String, ?> filterMap = PayCore.paraFilter(objMap);
        // 得到带签名数�?      
        String linkStr = PayCore.createLinkString(filterMap);
        if ("MD5".equalsIgnoreCase(signType)) {
            String md5Key = key;
            hexSign = md5Sign(linkStr, md5Key);
        } else if ("NONE".equalsIgnoreCase(signType)) {
            // 无须签名
        }
        return hexSign;
    }

    /**
     * md5签名
     *
     * @param linkStr
     * @param md5Key
     * @return
     * @throws Exception
     */
    public static String md5Sign(String linkStr, String md5Key) throws Exception {
        String templinkStr = linkStr + "&key=" + md5Key;
        String md5HexSign = MD5.sign(templinkStr, "UTF-8");
        return md5HexSign;
    }

   
}
