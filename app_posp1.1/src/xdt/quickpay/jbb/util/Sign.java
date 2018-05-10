package xdt.quickpay.jbb.util;

import cn.com.sdca.security.util.JdkX509CertTookit;
import xdt.controller.BaseAction;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;

/**
 * 客户端签名
 * (客户端用私钥签名，服务端调用验签服务器用根证书在验签)
 */
public class Sign {
	
	public static Logger logger = Logger.getLogger(Sign.class);
	
	public static String sign(String txnString){
		//配置私钥的配置文件(默认寻找privateKey.xml)和私钥配置中定义的APPName
		logger.info("聚佰宝代付签名进来了");
		etonepaySign es = new etonepaySign("privateKey","privateKey1.xml");
		logger.info("聚佰宝代付上送数据进来了:");
		String signStr = es.sign(txnString);
		
		logger.info("聚佰宝代付上送数据出去了");
		return signStr;

	}

    public static void main(String args[]){
        try {
            String txnString = "988000000004591|20160613102308|20160613102308|00000084|扫铺代付|1|2|110891|0000000088|1|0E9E8BD9ABFED5DE29BA8550BE275BE8A1B3BEB3230A64B8A24E0E13AA994D145F197FA8679ACFCF8AA56BC9099D1C31CD37F7452335B0DE62DFA0F8D5E9EAB9C4E1EB0BF359A766B7F2CB7CF2ED7508E1E1AD137A24EB23A58B944113792DBD7FBEE89E3143E850D8DE8A80B5CE86DCD9F905898FF57150D1E567E4D612ABF8066988B3660B006EEDC41349E369F3A2FED354288E3EB49030F609B9B16E31DCEDE71CE3EC7DC59729D46C65EB5DE86892F38ADF6AFBB2869075C47742DFC55BA018F18969B26905630D98EEB04F9FBA78758380163EBE92EBB68EF71672CA69A1A2776F5DE7646AF0BCDFE6997CACC57E091D6287AE2085062B0B1CD95B3BFF|2897A8ABB47C5B491D921510E6F46B391C9941054D444793901EF4120865C0338386FAF0066690D1D9C247D99D132D58FA9CCFB6AD29F5E82220EDCA093CE41832A586A7BA1B66B91044A9C7738635114A2E12A8E9FFFC3C688C26AA2A13B3A11095977CE0174852D10BAAF39EC26E98F68684632974F3DF5BB8281924E6D57A0E8803EF1AAFF3F65B50E140159FD06F5EA3E94FCF1237DFCB8A6B1893E504478CAE7A03C95F9AD5A123279F54210EA5E41EC0045F957F345716CF32629036C7FD6569D48AD6579DFE7687FB46D99555CC9BEB43AF2289D54B5EA6F195FE713001FA46E9A37AECE9E0CD3A54C2CCCD6B5C1242EFD3A6E4FCA3FAC6658A0872A8|中信银行中信银行济南历下支行|11035|0000000089|1|113D71F209BA57F3FFA354FD847CF07089FCFC68BD3450324E098D4C0309BED0F043B98F32127CE89235C79F7BC17B21197DAA17E53B947B634E6FC837068664945339AB374E0663F224BF3E10FDE0A25DE71A75D40049BB0ED76127F1477DD5C502D4452A7092F1E2A54AD327CDF7FF551C893FDE71AC4DE69CF61644EDFC73C8159A62E033AAAEE32388CD61A6DDAB006B76E78079E59DBC5C417C7060EBCAA9206BF0F8CD75ABFA56BF64546442B9F983959C876717F2A958C2FD0BF8245C988E04EF56CA2D5C9B520B36E5432C7304AB18C722B2F84B7B99A2A5A25D8D8ECA41DE73DFE5EEB0FE64CE4AE4B26CA6F6597C8742541090488E1B2E1A05A96E|40A88C55D7E6584B3E396D1BCDFBE70CC676FDBDA19FD3B8C27E05038246543434B8EDDA93172A327BBF60278F09CF94789EFE8E9D373837DE23D71BBA98101F40845712C93D4B39B4EE434D8F45B860C704E212C81D0736749B1E73F48298E19CA9D03B8C9845B42481CF5546ED84D9A58EC463BA20AB6A84F49FEAB377BAA58B27A28162FE9EB8F18CE987DFB68D2DC2DD83051BD80A8E0747EDC971D1FBBE56C7E314E29111DFA0225AC97A6813E054B6351B9EF4C41E341E166100E15268067CA3611FAB516A42C812B705578EA1C08556C8BA174C34D74179FDE8EB9E5CB5C55EB2C75E42AA7F8B81410A3E05AE90FF67DCEB5B6DD0A2A3DE8563A6A25D|农村信用合作社山东济南润丰农村合作银行天桥支行|99856";
            String signStr = sign(txnString);
            System.out.println("签名结果为==============="+signStr);
 
            //获取证书序列号            
            X509Certificate cert = JdkX509CertTookit.loadX509CertificateFromFile("encypt.cer");//
			System.out.println((new StringBuilder()).append("证书编号为：==========").append(JdkX509CertTookit.toHex(cert.getSerialNumber().toByteArray())).toString());
	        //74c0175b98878a1cc94752f2ca360c0c

			//公钥串获取与还原
	        String pubKeyStr = Base64.encodeBase64String(cert.getPublicKey().getEncoded());	        
	        PublicKey key2 = EctonRSAUtils.getPublicKey(Base64.decodeBase64(pubKeyStr));//
	        System.out.println("公钥字符串："+pubKeyStr);
	        System.out.println("公证书信息："+ Base64.encodeBase64String(cert.getEncoded()));
            
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    
    
    
    /**
    * RSA验签名检查
    * @param content 待签名数据
    * @param sign 签名值
    * @param ecton_public_key 易通公钥
    * @param input_charset 编码格式
    * @return 布尔值
    */
    public static boolean verify(String content, String sign, String ecton_public_key, String input_charset)
    {
    try
    {
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    byte[] encodedKey = Base64.decodeBase64(ecton_public_key);
    PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));


    java.security.Signature signature = java.security.Signature
    .getInstance("SHA1WithRSA");

    signature.initVerify(pubKey);
    signature.update( content.getBytes(input_charset) );

    boolean bverify = signature.verify( Base64.decodeBase64(sign) );
    return bverify;

    }
    catch (Exception e)
    {
    e.printStackTrace();
    }

    return false;
    }


}
