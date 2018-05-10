package xdt.service;

import java.io.BufferedReader;  
import java.io.DataOutputStream;  
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.io.UnsupportedEncodingException;  
import java.net.HttpURLConnection;  
import java.net.MalformedURLException;  
import java.net.URL;  
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import com.kspay.MD5Util;
import com.kspay.cert.CertVerify;
import com.kspay.cert.LoadKeyFromPKCS12;

  



import net.sf.json.JSONObject; 
/***
 * 代付请求
 * @author Administrator
 *
 */
public class TransDemo {

    public static final String ADD_URL = "http://hyapi.kspay.net:8190/ks_dfpay/mopay/pay";  
    public static final String TRANSTYPE = "470000";
    
    public static void main(String[] args) {  
        appadd();  
    } 

	public static void appadd() {  

     try{  
            //创建连接  
            URL url = new URL(ADD_URL);  
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();  
            connection.setDoOutput(true);  
            connection.setDoInput(true);  
            connection.setRequestMethod("POST");  
            connection.setUseCaches(false);  
            connection.setInstanceFollowRedirects(true);               
            connection.setRequestProperty("Content-Type","application/json; charset=GBK");                     
            connection.connect();  
  
            //POST请求  
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());  
            JSONObject obj = new JSONObject();           
            
            String accnName = URLEncoder.encode("李鑫","GBK");
            String accNo = "6226222103822164";
            String merId = "936775585060000";
            String orderId = "d1231232131231231";
            String transAmount = "11";
            String transDate = "20170209154122";
            String versionId = "001";
            StringBuffer str = new StringBuffer();
            

                  
            
            JSONObject transData = new JSONObject(); 
            //交易
            transData.put("accName", accnName); // 收款人姓名
            transData.put("accNo", accNo); // 收款人账号  
            transData.put("orderId", orderId); // 订单号  
            transData.put("transAmount", transAmount); // 交易金额
            transData.put("transDate", transDate); // 交易日期

            //私钥证书加密
			String pfxFileName = "D:\\936775585060000.pfx";

			String pfxPassword = "111111";
			PrivateKey privateKey = null;
			 LoadKeyFromPKCS12.initPrivateKey(pfxFileName, pfxPassword);
			String  transBody=LoadKeyFromPKCS12.PrivateSign(transData.toString());
          
			obj.put("transBody", transBody);
			System.out.println(transBody);
			
			
            //请求
            obj.put("businessType", TRANSTYPE); // 业务类型
            obj.put("merId", merId); // 商户号
            obj.put("versionId", versionId); // 版本号 
            
            str.append("businessType" + "=" + TRANSTYPE).append("&merId" + "=" + merId)
            .append("&transBody" + "=" + transBody).append("&versionId" + "=" + versionId);
                 
          
//            
            System.out.println(str);
            String signData = MD5Util.MD5Encode(str.toString()+"&key="+"D7F6350233F43F61");
            System.out.println(signData);
            obj.put("signData", signData); // 交易日期
            obj.put("signType", "MD5"); // 版本号          

            System.out.println(obj);
            out.write(obj.toString().getBytes("GBK"));
            out.flush();  
            out.close();  
              
            //读取响应  
            BufferedReader reader = new BufferedReader(new InputStreamReader(  
                    connection.getInputStream()));  
            String lines;  
            StringBuffer sb = new StringBuffer("");  
            while ((lines = reader.readLine()) != null) {  
                lines = new String(lines.getBytes(), "gbk");  
                sb.append(lines);  
            }  
            System.out.println(sb); 
            ObjectMapper om = new ObjectMapper();
            Map<String,Object> map1 = om.readValue(sb.toString(), Map.class);          
    		String data = map1.get("resBody").toString();
    		//公钥证书解密
    		String cerFileName = "D:\\936775585060000.cer";
		    PublicKey publicKey = null;		
    		byte[]signByte=LoadKeyFromPKCS12.encryptBASE64(data);
    		 CertVerify.initPublicKey(cerFileName);
    		byte[] str1=CertVerify.publicKeyDecrypt(signByte);
	
    		String string = new  String(str1);
    		
    		JSONObject  jasonObject = JSONObject.fromObject(string);
    		Map<String, String> map = new HashMap<String, String>();
    		map = (Map)jasonObject;
            System.out.println(URLDecoder.decode(map.get("refMsg"),"GBK"));
            reader.close();  
            // 断开连接  
            connection.disconnect();  
        } catch (MalformedURLException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (UnsupportedEncodingException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
  
    }  
  
}
