package xdt.dto.jp;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by zhanglong on 2017/3/29.
 */
public class RequestUtil {
    /**
     * 把HttpServletRequest对象的getParameterMap返回对象转换为java.util.Map对象
     * @param requestPara
     * @return
     */
    public static Map<String, String> getMapFromRequestMap(Map<String, String[]> requestPara) {
        Map<String, String> paraMap = new HashMap<String, String>();
        Iterator<Map.Entry<String, String[]>> it = requestPara.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String[]> entry = it.next();
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (values != null && values.length >= 1) {
                paraMap.put(key, new String(values[0].getBytes(), Charset.forName("UTF-8")));
            }
        }

        return paraMap;
    }
}
