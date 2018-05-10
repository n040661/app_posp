package xdt.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.codehaus.jackson.map.ObjectMapper;

import com.kspay.cert.CertVerify;
import com.kspay.cert.LoadKeyFromPKCS12;
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
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(120000);
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
            
            String accnName = URLEncoder.encode("高立明","GBK");
            String accNo = "628480020721272316";
            String merId = "936640995770000"; //代付不给予测试，请使用正式商户号  进行测试
            String orderId = "fcdsf1231231232";
            String transAmount = "1";
            String transDate = new SimpleDateFormat("YYYYMMddHHmmss").format(new Date());
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
			String pfxFileName = "D://936640995770000.pfx";

			String pfxPassword = "111111";
			PrivateKey privateKey = null;
			privateKey =initPrivateKey(pfxFileName, pfxPassword);
			System.out.println("111:"+privateKey);
			String  transBody=PrivateSign(transData.toString(),privateKey);
			
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
            String signData = xdt.dto.mb.MD5.MD5(str.toString()+"&key="+"072C15B8D473BB29");
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
			if(map1.get("resBody") != null){
				/*String data = map1.get("resBody").toString();
				//公钥证书解密
				String cerFileName = "D://936640995770000.cer";
				PublicKey publicKey = null;		
				byte[]signByte=LoadKeyFromPKCS12.encryptBASE64(data);
				publicKey = CertVerify.initPublicKey(cerFileName);
				byte[] str1=CertVerify.publicKeyDecrypt(signByte,publicKey);
				System.out.println("str1:"+str1);
				String string = new  String(str1);
				
				JSONObject  jasonObject = JSONObject.fromObject(string);
				Map<String, String> map = new HashMap<String, String>();
				map = (Map)jasonObject;
				System.out.println("resBody中的refCode==》"+map.get("refCode"));
				System.out.println("resBody中的refMsg==》"+URLDecoder.decode(map.get("refMsg"),"GBK"));			*/
			}else{
				System.out.println("refCode==》"+map1.get("refCode"));
				System.out.println("refMsg==》"+URLDecoder.decode(map1.get("refMsg").toString(),"GBK"));
			}
            reader.close();  
            // 断开连接  
            connection.disconnect();  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }
  
    }  
	public static PrivateKey initPrivateKey(String pfxFileName, String pfxPassword)
	{
		 try
		{
			 File fPkcs12 = null;
			 fPkcs12 = new File(pfxFileName);
			 FileInputStream fis = new FileInputStream(fPkcs12);
			
			 if (Security.getProvider("BC") == null) {
				 Security.addProvider(new BouncyCastleProvider());
				 }
			
			 KeyStore store = KeyStore.getInstance("PKCS12");
			 store.load(fis, pfxPassword.toCharArray());
			
			 PrivateKey privateKey = (PrivateKey) store.getKey("MobaoPay", pfxPassword.toCharArray());
			
			 return privateKey;
			 } catch (Exception e) {
			 e.printStackTrace();
			 }
		 return null;
		 }
	public static String PrivateSign(String srcSouse, PrivateKey privateKey)
	 {
		 try
		 {
			 Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
			 cipher.init(1, privateKey);
			return encryptBASE64(cipher.doFinal(srcSouse.getBytes("GBK")));
			 } catch (Exception e) {
			 e.printStackTrace();
			}
		 return null;
		 }
	public static String encryptBASE64(byte[] sign)
	{
		 BASE64Encoder encode = new BASE64Encoder();
		 return encode.encode(sign);
		 }
	
	public static byte[] encryptBASE64(String str)
	 {
		 BASE64Decoder decoder = new BASE64Decoder();
		 try {
			 return decoder.decodeBuffer(str);
			} catch (IOException e) {
			 e.printStackTrace();
			}
		 return null;
		 }
}
