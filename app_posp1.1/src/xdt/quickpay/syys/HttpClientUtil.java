package xdt.quickpay.syys;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;

import java.io.IOException;

/**
 * Created by liyi on 2017/3/1.
 */
public class HttpClientUtil {


    public static String post(String url, String charSet, String msg) {
        String contentType = "application/json";
        PostMethod postMethod = null;
        String resMsg = null;

        try {
            // 构造HttpClient的实例
            HttpClient httpClient = new HttpClient();
            HttpClientParams httpParams = httpClient.getParams();
            if (httpParams == null) {
                httpParams = new HttpClientParams();
            }
            httpParams.setConnectionManagerTimeout(20 * 1000);
            httpParams.setSoTimeout(20 * 1000);

            postMethod = new PostMethod(url);
            RequestEntity requestEntity = new StringRequestEntity(msg, contentType, charSet);
            postMethod.setRequestEntity(requestEntity);
            
           
           // NameValuePair msg1 = new NameValuePair("msg", msg);// 内容
           // NameValuePair[] data = {msg1 };
           // postMethod.setRequestBody(data);
            
            // 使用POST方式提交
            int statusCode = httpClient.executeMethod(postMethod);
            byte[] b = postMethod.getResponseBody();
            if (b != null) {
                resMsg = new String(b, charSet);
            }
        } catch (IOException e) {
        } catch (Exception e) {

        } finally {
            // 释放连接
            if (null != postMethod) {
                postMethod.releaseConnection();
            }

        }
        return resMsg;
    }


}
