package xdt.dto.hfb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class HeepayClient2 {

    private String url;
    private String key;

    public HeepayClient2(String url, String key){
        this.url = url;
        this.key = key;
    }

    public String execute(Map<String, String> req){

        Map<String, String> paramsMap = setParams(req);
        List<NameValuePair> params = createNVPairs(paramsMap);

        Map<String, String> signParamsMap = setSignElems(paramsMap);
        params = createSign(signParamsMap, params);
        System.out.println("上传上游之前带签名的参数"+JSON.toJSONString(params));
        String retStr;
        try {
            retStr = HttpsUtil.sendHttpsRequestWithParam(url, params);
            System.out.println("接口返回："+retStr);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

        System.out.println("返回信息验签结果：" + verifyRetSign(retStr));

        return retStr;
    }

    protected Map<String, String> setParams(Map<String, String> req){
        return req;
    }

    private List<NameValuePair> createNVPairs(Map<String, String> params){
        List<NameValuePair> nvPairs = new ArrayList<>();
        Set<Map.Entry<String, String>> entries = params.entrySet();
        for(Map.Entry<String, String> entry : entries){
            nvPairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return nvPairs;
    }

    protected Map<String, String> setSignElems(Map<String, String> req){
        return req;
    }

    private List<NameValuePair> createSign(Map<String, String> preSign, List<NameValuePair> params){

        Set<String> keySet = preSign.keySet();
        List<String> keys = new ArrayList<>();
        keys.addAll(keySet);
        Collections.sort(keys);

        StringBuilder signStr = new StringBuilder();
        for(String elemKey : keys){
            signStr.append(elemKey).append("=").append(preSign.get(elemKey)).append("&");
        }
        signStr.append("key=").append(key);

        try {
            System.out.println("构造签名串---加密前："+signStr.toString());
            String signString = Md5.encode(signStr.toString().getBytes("UTF-8"));
            System.out.println("构造签名串---加密后："+signString);
            params.add(new BasicNameValuePair("signString", signString));
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return params;
    }

    private boolean verifyRetSign(String retStr){
        JSONObject retMap = JSON.parseObject(retStr);
        if(retMap.getInteger("retCode") != 1){
            return true;
        }

        List<String> retKeys = new ArrayList<>();
        Set<String> keySet = retMap.keySet();
        retKeys.addAll(keySet);
        retKeys.remove("sign");
        Collections.sort(retKeys);

        StringBuilder signStr = new StringBuilder();
        for(String elemKey : retKeys){
            Object elemValue = retMap.get(elemKey) == null ? "" : retMap.get(elemKey);
            signStr.append(elemKey).append("=").append(elemValue).append("&");
        }
        signStr.append("key=").append(key);

        try {
            System.out.println("构造签名串---加密前："+signStr.toString());
            String signString = Md5.encode(signStr.toString().getBytes("UTF-8"));
            System.out.println("构造签名串---加密后："+signString);

            return signString.equals(retMap.get("sign"));
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

}
