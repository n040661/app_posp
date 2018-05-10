package xdt.dto.cj;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * <p>
 * 定义请求的参数名称
 * </p>
 * 
 * @author yanghta@chenjet.com
 * @version $Id: BaseConstant.java, v 0.1 2017-05-03 下午5:25:44
 */
public class BaseConstant {

	// 基础参数
	public static final String SERVICE = "Service";
	public static final String VERSION = "Version";
	public static final String PARTNER_ID = "PartnerId";
	// 日期
	public static final String TRADE_DATE = "TradeDate";
	public static final String TRADE_TIME = "TradeTime";
	public static final String INPUT_CHARSET = "InputCharset";
	public static final String SIGN = "Sign";
	public static final String SIGN_TYPE = "SignType";
	public static final String MEMO = "Memo";

	public static final String MD5 = "MD5";
	public static final String RSA = "RSA";
	
	
	/**
	 * 畅捷支付平台公钥
	 */
	//y
	//public static final String MERCHANT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDPq3oXX5aFeBQGf3Ag/86zNu0VICXmkof85r+DDL46w3vHcTnkEWVbp9DaDurcF7DMctzJngO0u9OG1cb4mn+Pn/uNC1fp7S4JH4xtwST6jFgHtXcTG9uewWFYWKw/8b3zf4fXyRuI/2ekeLSstftqnMQdenVP7XCxMuEnnmM1RwIDAQAB";
	//T
	//public static final String MERCHANT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDv0rdsn5FYPn0EjsCPqDyIsYRawNWGJDRHJBcdCldodjM5bpve+XYb4Rgm36F6iDjxDbEQbp/HhVPj0XgGlCRKpbluyJJt8ga5qkqIhWoOd/Cma1fCtviMUep21hIlg1ZFcWKgHQoGoNX7xMT8/0bEsldaKdwxOlv3qGxWfqNV5QIDAQAB";
	//生产环境
	public static final String MERCHANT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDPq3oXX5aFeBQGf3Ag/86zNu0VICXmkof85r+DDL46w3vHcTnkEWVbp9DaDurcF7DMctzJngO0u9OG1cb4mn+Pn/uNC1fp7S4JH4xtwST6jFgHtXcTG9uewWFYWKw/8b3zf4fXyRuI/2ekeLSstftqnMQdenVP7XCxMuEnnmM1RwIDAQAB";//生产环境公钥
	                                                
	
	/**
	 * 商户私钥
	 */
	//y
	//public static final String MERCHANT_PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAL95OpcIXM20+O6GRFe5mGzpgmwezOqf96jkU3CzCa/5nSnNrYjnUOXdwHhHF8uRPUJCzFcq9RgRkrVsus8eRJAg/6PFbCRuka3qn6MWC9PgSihiRTpRcRdtOioJMeCCHfbdWAszCk8UMGXDTw0tYtIstJTA6uN0lkF+3HHr9g5NAgMBAAECgYEAvMtgBgPs3bgPq8EgKg+KR/mG6+0CyGWq4REhZQJkDuvlF48CCPdsQPsCCJaw07wF1wveSCTHPlA1hEYNfKOvnWjM86Mqd1R+lKrrrkih3JqZXBqf9l8SaN+EkmCTAOr5NRyhIAAM3oG+V3KxPTcxqfuUTa4EwPvaSdqlVCEgSYECQQDlPEeJjlybppem+BFvziQ8mdjxpGaJcSTMkH77CL+0I/1VpixrqHchc5gCNm7CQjmBivi4Udlb3ygGT6cbnisJAkEA1dREShKB/heolbTP1GrDswEl/FCTo3RnYHqQnmvuV2+yrFKxmIlaxDV4Ul/BCpRBVWyKQq5vp361/7EqLyKmJQJAQxSysnQwcXP8qOWq5JbkT7+NlpjPUKUFab9XCUr0Hljxx2CLWy2/PtC+yNb47mvGvDvYsMdy3cmdwssIUbKlUQJBAMHzCw30w5vpcax4mJofTOqse/vkSkXUa4ADK/HGDCWa1wGe8B9fVsjphv2QhNRHrQsCrFXXSscuE36c/5pJX1ECQEMf6gBJpuTuhneV4CioYL95WzRIHZhrkwpVFEKMkkemZEPEeKkAGQ4GPNt6aWyjWKbOh2iizQ2Lc1YEJ3u7RtQ=";
	//t
	//public static final String MERCHANT_PRIVATE_KEY="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAO/6rPCvyCC+IMalLzTy3cVBz/+wamCFNiq9qKEilEBDTttP7Rd/GAS51lsfCrsISbg5td/w25+wulDfuMbjjlW9Afh0p7Jscmbo1skqIOIUPYfVQEL687B0EmJufMlljfu52b2efVAyWZF9QBG1vx/AJz1EVyfskMaYVqPiTesZAgMBAAECgYEAtVnkk0bjoArOTg/KquLWQRlJDFrPKP3CP25wHsU4749t6kJuU5FSH1Ao81d0Dn9m5neGQCOOdRFi23cV9gdFKYMhwPE6+nTAloxI3vb8K9NNMe0zcFksva9c9bUaMGH2p40szMoOpO6TrSHO9Hx4GJ6UfsUUqkFFlN76XprwE+ECQQD9rXwfbr9GKh9QMNvnwo9xxyVl4kI88iq0X6G4qVXo1Tv6/DBDJNkX1mbXKFYL5NOW1waZzR+Z/XcKWAmUT8J9AkEA8i0WT/ieNsF3IuFvrIYG4WUadbUqObcYP4Y7Vt836zggRbu0qvYiqAv92Leruaq3ZN1khxp6gZKl/OJHXc5xzQJACqr1AU1i9cxnrLOhS8m+xoYdaH9vUajNavBqmJ1mY3g0IYXhcbFm/72gbYPgundQ/pLkUCt0HMGv89tn67i+8QJBALV6UgkVnsIbkkKCOyRGv2syT3S7kOv1J+eamGcOGSJcSdrXwZiHoArcCZrYcIhOxOWB/m47ymfE1Dw/+QjzxlUCQCmnGFUO9zN862mKYjEkjDN65n1IUB9Fmc1msHkIZAQaQknmxmCIOHC75u4W0PGRyVzq8KkxpNBq62ICl7xmsPM=";
	// 生产环境
	//public static final String MERCHANT_PRIVATE_KEY="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAL95OpcIXM20+O6GRFe5mGzpgmwezOqf96jkU3CzCa/5nSnNrYjnUOXdwHhHF8uRPUJCzFcq9RgRkrVsus8eRJAg/6PFbCRuka3qn6MWC9PgSihiRTpRcRdtOioJMeCCHfbdWAszCk8UMGXDTw0tYtIstJTA6uN0lkF+3HHr9g5NAgMBAAECgYEAvMtgBgPs3bgPq8EgKg+KR/mG6+0CyGWq4REhZQJkDuvlF48CCPdsQPsCCJaw07wF1wveSCTHPlA1hEYNfKOvnWjM86Mqd1R+lKrrrkih3JqZXBqf9l8SaN+EkmCTAOr5NRyhIAAM3oG+V3KxPTcxqfuUTa4EwPvaSdqlVCEgSYECQQDlPEeJjlybppem+BFvziQ8mdjxpGaJcSTMkH77CL+0I/1VpixrqHchc5gCNm7CQjmBivi4Udlb3ygGT6cbnisJAkEA1dREShKB/heolbTP1GrDswEl/FCTo3RnYHqQnmvuV2+yrFKxmIlaxDV4Ul/BCpRBVWyKQq5vp361/7EqLyKmJQJAQxSysnQwcXP8qOWq5JbkT7+NlpjPUKUFab9XCUr0Hljxx2CLWy2/PtC+yNb47mvGvDvYsMdy3cmdwssIUbKlUQJBAMHzCw30w5vpcax4mJofTOqse/vkSkXUa4ADK/HGDCWa1wGe8B9fVsjphv2QhNRHrQsCrFXXSscuE36c/5pJX1ECQEMf6gBJpuTuhneV4CioYL95WzRIHZhrkwpVFEKMkkemZEPEeKkAGQ4GPNt6aWyjWKbOh2iizQ2Lc1YEJ3u7RtQ=";
	
	//生产环境 测试商户号私钥
	public static final String MERCHANT_PRIVATE_KEY= "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKpAzevnmXHJCkRQlEGmvvyaDCA965rd3Q1N1r3RzlAVbZ9F8IjhjzRt4ZcGsHqJpEuIRodNCXDwknC1JGT05uuskOEsNHRZ2wTguf/EQGbLb2iKJO/Vm4WIZkBmeK0QgmpowmyKAxTsThe9Rc8M6B397I5DKKjs4wkG0O6zIy0HAgMBAAECgYBz5NFIOMuXTUcT4dZKKnvv7905RW1Qap7DI61+YYeTtEJ/HVjzlLeq9PGPoRoZEAEq+YUpLSO0Ic9NscEoiB14TCzRp36S/okdfhkLdjv90/tICOKgs7j2Md0WIYmW4SE8A5XWC3fYygZU4uiRBbL51P4bVtRH66tqF01ak5+cYQJBANMVGnYOK1K3Ep6P+vBAuWpDXUJbQnaMYjN6USk96XlOpB9d8Ldziu6wnxQCcP0yQTLTUXQw5KRz6yvWkir5mY0CQQDOe32C2OUVBtF2ZEQLzT+GQivN9V8cBoT7haGG0ekDGxfQDMTmGy+6F1CICoj4qkYO4WmtV9trmkJQFvlAplnjAkBXrU5edyg7pVuQjVG4dcoOf4i/RVToFQu9QjNwJA1Um/vEI6sXKGEvICITl6gDI9q9F9Wfo6d1P8kRXoE8ZI0JAkBRiPQT8j/roKif3pH8Mw1Nn92aU7U95NkoVSlmioc/obgygIhT8RJUopY7UiWqQ9qsT5iiSZoskFp1M80zGT1PAkEAm407ogN0cnhIEEVbqH+43HFEcQX6dKUXwXzmAIiAbGYzxfKL0xVm3qEGTDIHQynOgfsUb7d38rf2QrPm1hKy3g==";
	
	
	
	/**
	 * 编码类型
	 */
	public static final String CHARSET = "UTF-8";
	public final static String GATEWAY_URL = "https://pay.chanpay.com/mag-unify/gateway/receiveOrder.do";
	public final static String BATCH_FILE_GATEWAY_URL = "https:/pay.chanpay.com/mag-unify/gateway/batchOrder.do";
	public static String DATE = new SimpleDateFormat("yyyyMMdd").format(new Date());
	public static String TIME = new SimpleDateFormat("HHmmss").format(new Date());

}
