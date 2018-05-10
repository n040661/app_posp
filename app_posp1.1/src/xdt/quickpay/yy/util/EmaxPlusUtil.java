package xdt.quickpay.yy.util;



import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Title:溢+
 * <p>
 * Description:
 * <p>
 * Company: 溢美金融
 *
 * @author peng
 * @version 1.0
 * @since：2017/3/8.
 */

public class EmaxPlusUtil {

    public static final String SUCCESS_CODE = "000000";//溢+返回成功状态码

    



   private static final String privateKey = "1af57d84950a70ab1d1e5c55a3f8a31a";
    
    

    /**
     * 计算key
     * @param businessId e游客 业务id（商户操作则商户id，导游操作则导游id，代理商id）
     * @param merCode 溢+账户开户id
     * @return
     */
    public static String getEmaxPlusPrivateKey(String businessId,String merCode) {
        try {
            return _md5Encode(businessId+merCode);
        }catch (Exception e){
            return "ERROR";
        }
    }
    /**
     * 调用溢+接口签名算法
     * @param param
     * @return
     */
    public static String getSign(Map<String, String> param){
        try {
            return _md5Encode(getSignPlainText(param));
        } catch (Exception e) {
            return "ERROR";
        }
    }

    public static String getSignPlainText(Map<String, String> param) {
        try {
            Map<String, String> md5Map = new HashMap<String, String>();
            md5Map.putAll(param);
            List arrayList = new ArrayList(md5Map.entrySet());
            Collections.sort(arrayList, new Comparator() {
                public int compare(Object o1, Object o2) {
                    Map.Entry obj1 = (Map.Entry) o1;
                    Map.Entry obj2 = (Map.Entry) o2;
                    return (obj1.getKey()).toString().compareTo((String) obj2.getKey());
                }
            });
            StringBuilder md5key = new StringBuilder("");
            for (Iterator iter = arrayList.iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                if("".equals(value) || value == null) {
                    continue;
                }
                if (md5key.length() <=0) {
                    md5key.append(key).append("=").append(value);
                } else {
                    md5key.append("&").append(key).append("=").append(value);
                }
            }
            md5key.append(privateKey);
            return md5key.toString();
        } catch (Exception e) {
            return "ERROR";
        }
    }

    /**
     * MD5加密 生成32位md5码
     *
     * @param inStr 待加密字符串
     * @return 返回32位md5码
     */
    public static String _md5Encode(String inStr) throws Exception {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        byte[] byteArray = inStr.getBytes("UTF-8");
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    public static void main(String[] args) throws Exception{
        long s = System.currentTimeMillis();

        String emaxPlusPrivateKey = getEmaxPlusPrivateKey(System.currentTimeMillis() + "", s + "asdfasdfasasfasdfasdfadsfasdfasdf");

        System.out.println(emaxPlusPrivateKey+"   <> "+(System.currentTimeMillis() - s));

    }
}
