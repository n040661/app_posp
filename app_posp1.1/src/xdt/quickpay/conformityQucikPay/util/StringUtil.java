package xdt.quickpay.conformityQucikPay.util;

import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;


/**
 * 字符串工具类
 */
public class StringUtil extends StringUtils {
	
    /**
     * 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
     */
    public static String getParamSrc(TreeMap<String, String> paramsMap) {
        StringBuffer paramstr = new StringBuffer();
        for (String pkey : paramsMap.keySet()) {
            String pvalue = paramsMap.get(pkey);
            if (null != pvalue && "" != pvalue) {// 空值不传递，不签名
                paramstr.append(pkey + "=" + pvalue + "&"); // 签名原串，不url编码
            }
        }
        // 去掉最后一个&
        String result = paramstr.substring(0, paramstr.length() - 1);
        System.out.println("签名原串：" + result);
        System.out.println("java.nio.charset.Charset.defaultCharset():" + java.nio.charset.Charset.defaultCharset());
        return result;
    }

}
