package xdt.tools;

import xdt.dto.BaseUtil;

/**
 * @author
 */
/**
 * @author
 */
public class Constants {
  //----商户信息：商户根据对接的实际情况对下面数据进行修改； 以下数据在测试通过后，部署到生产环境，需要替换为生产的数据----
  //商户编号，由易联产生，邮件发送给商户
  //public static final String MERCHANT_ID = "302020000058";   //内部测试商户号，商户需要替换该参数
  //我们的正式商户号
  public static final String MERCHANT_ID ="502050002374";//502050002374 502050002552
  //我们的测试商户号
  //public static final String MERCHANT_ID ="502053000034";
  
  //public static final String MERCHANT_ID = "002020000008";     //互联网金融行业的商户号
  //商户接收订单通知接口地址（异步通知）；
  public static final String MERCHANT_NOTIFY_URL = BaseUtil.url+"/clientController/Notify.action";
  //商户接收订单通知接口地址H5（异步通知）；
  public static final String MERCHANT_NOTIFY_URL1 = BaseUtil.url+"/clientH5Controller/Notify.action";

  //商户接收订单通知接口地址(同步通知),H5版本对接需要该参数；
  public static final String MERCHANT_RETURN_URL = BaseUtil.url+"/clientH5Controller/ReturnH5.action";
  //商户接收订单通知接口地址(同步通知),H5版本对接需要该参数；
  public static final String MERCHANT_RETURN_URL0 = BaseUtil.url+"/clientH5Controller/bgPayResult.action";
  //商户接收订单通知接口地址(同步通知),H5版本对接需要该参数；
  public static final String MERCHANT_RETURN_URL1 = BaseUtil.url+"/clientH5Controller/bgPayResult1.action";
  //商户RSA私钥，商户自己产生（可采用易联提供RSA工具产生）
  //public static final String MERCHANT_RSA_PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOAqNu0SFh5Ksz8Mp/vzm1kxiMYoSREXNXGajCHkKJIVwTaxtPaPYq3JiASZbCALrjp9UM0jLsqayDzF51paUt5lbBDRgqabAClUos3X4c7v2uUt98ILDi8ABQV8BrMZE5RKaLcvvr3mhu/JhabXBz2SBflSCSG3K8HQRTDjjp7nAgMBAAECgYBg01suQ6WyJ+oMzdaxiaQMfszpavVEoJXBIFRvPzIXB7aRfWkBJyYkkuxhsDN4FBOJyB9ivFO1x+298m3gJSutfXfSRA9Kq9XrEIQDjJB4PBx8yTVmVckgCJlsWnhuySHf7gapLkfLHQ+GgiUpYUPW0MJczsu7juuMUZdKHJ6rIQJBAPVLJAxXQYI2e8WMfTPR1jqeZXSQ5r4XI0d8wKFMDa68gq3Y3B2CKmWO16faxafJ8oUWJtJJwRQT6YItBVA3DWUCQQDp8vymxQkLCVpyQ+SfG0Ab9mw2G7p2Y3pAYwms7SIOILoADUbJl2UxpyROj9N9Lq2ndZ0rNIWw4iJXigwRuaxbAkEAkiN7TZLqp25YXUCvEyFwFapq3YC6yAO29A9CIJbUDAepf3OU6Eu1gJ4So6F2YtmxEFM7O8vPKWwXkYPLB5hU9QJAHLjWR+s81vwI/KpVMSt5TXWNh38T/2DrK2h9UZuzaKSf8U2v+SP7KoNos7R4tI+8hiisaReDqlm4+aJbJPn0rQJBAK0EQLyG8iks7Ppclq9UBgEx2iKSsg9y60aSt1YwI73wEdW18paBtoUMjQ5GAqCyVmEb01IY6Kn1si43zqHct4o=";
  //我自己生成的测试私钥
  //public static final String MERCHANT_RSA_PRIVATE_KEY ="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAN9LOfvqgWu1Re1ksPwjZQ2xb/n+mZNVxAf0cWpsFpBWc1puHneMwGGX5poLdbF9SMbeHEmuFzm0SbEF1EA0F9l/AG4pXDAkp0m87+JtPJA2PhxtrwfKLjecblfPqCTUdF8eKNt/3lZ6KiUU5szRIShS6r4ri0eQGxvNdAh3bWyzAgMBAAECgYEAsal5CSccskYYzTlAWF0RGkQDhDU+nCuxPWMQmkxF57HhNiJHu3cR2pSxtf9INWApd5FhkH4jnJYFzAOHTU4lajs0nSpxl9TcNKj//JZ3yCE/7gRvXeguEixxppscdDwMH6Fn5R1i6U0GkECaS3IaCgM8OSJn6tLA0IcMTLiUZQECQQD1+AcZzG0WEnqoIXn/GBmpL2gy+Vne4rT8MLfqxhN4HAvz2gerzfzId8poTnaFR8J2dAOi3/40Lf2nzXt1jGurAkEA6GZ3Z3eOjYBCmvMkh6Z5GU+rfRlFfKKm3X4FomnO8VsYa7Rc4Ar53IIO6kjw0z7TxLXm7BxB7D9ESNt55TO7GQJADtf3/buTfbiBxHG4vgdDTS16OVFI9iVyTKHYB4gKlr4CjqXCvsCAl8x6346UonhxKNrZeVPRMG7yFtVUtQRTXwJANBMJvGB4y9IqmofS7qZpyjck6QIppNTZaRYc8xica06LOU8P5I/xDu0BpJO1itAwKYrM9KkcSHCgv2aWeLpPuQJBAL+JIVEfo7PhvqAxjVQfJYSApRoWY3Z+vyqmXmMLNlTFUlFXyq5K5sQm1GYgGL4vvG69EldLG7qK5wTZyrvP1Ec=";
  
  //自己生成的生产环境私钥
 public static final String MERCHANT_RSA_PRIVATE_KEY ="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANm8teuvxGrJIeQNJIkTu6HWtrrR2zehxrrQsI9S3YrExjx8YykaL6E6Y7X4T1xqasAKpVhOE8gjW/Hdrqt7vXgomJy8xLePrg6d0zqQ2Y7ky+pf7FfdJtWEgQDPGAXvl4Z2bDFuT+T2JxDQakduaNAL/Pdi3Pq52kOOo834PV8jAgMBAAECgYBkkHlBguvoGj9x8fQG77tAk0fQX26tciW0UQDLIY27MwkQFK9D45lOEQbOnAIjf+8QBZq6f005qcMkoauz/jHSe9Yx1DM55ZTrdYKwrNi0Me0T0eOmf/w5z1WlBvrCDb+bdbdd1YxzEMW8bvR71gKjaPUvKyvEsAXBoOB0Bj2fgQJBAP4zOeQQfyLklWJ772iSkQREUcImnK2POqeA3hE166RTH+L+o293TrCHrk4XwhJRJ4HMwJdYMsj+YBabmctylsECQQDbR2PkUvmsBE223rR7g0bCKjo/cSLRluVvz3WyMDUZmWNo+Qf0DUfNKgSzfIogJm4X7Cn4bbOi3DIrQzzsMDLjAkAAzojQgHzmz3Lp7RrFajGrocvgod69bkbxYSdTRqiIzPq87bH3GJnXVZboCItFI5zMfKnf0RfYK3aCm9vRGXwBAkEAlCirZOVvjvulas8VEXNExwJzMtSFmymiwqZhNtF9v1s1oQLKjeq/Pe+LF58pUMnGj2FhhlrRClRPjY2zxz5V0wJBAMWu6MFfLuWs1Cu6AGceazrWuyYdvgIJIyd4mFUg2A0Pu5ATsoXj5+L13/s0PBtHx4r73JeZjvs8kXET4nLardQ=";
  
  //---互联网金融测试商户RSA私钥（从证书文件导出）
//  public static final String MERCHANT_RSA_PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKpZJ9Rbd9LKg5jM4byOjLfGV2kFqctWDyAQNy+b0rFWOq8D+okjvLRGaRzUjuX28B9a03OmEwL7CW6WloCxr/g9t9WP5aGg1DKEb6biw/bsEDzG5681P39bv/ZlWTjfbg1KjDBaRqqjXK5l7XBAxxWFE7PaH6DuP+5kPR+IKiRbAgMBAAECgYAfDloAkRxrRZhwRwnwglyNNI/DCdFGzM29Hrew6kujIQFZ3vPSBL3mb9/B7c6PhlGIpdpe/ywAIxw5GSMfG0XlQ6umgPSsxF6TaRCXkBE1B1QYn5L4jVgHkszTRMCXkTybtaxEqEh6nhA6Krj4Y5ki1wpDpwHToTUYwz3RHuxdgQJBAN8hkxIhQ0ERALsrOWRZoishT9Ci5BxUtCYwKKw4Und1w3ywvxT28kDO2tp8aZ9/JVcHcRW04I+MmS0ZEPzGYNECQQDDcRpeVL6DLC/+fWhsUK6PixSmfH+roZURpJXlRPmQlxQwluoaQ2b/KUouujycnsphXIIpWHCZenfrJrS1yB1rAkBgU/lPOWb0fyempil3xi55mj7/3mLGTFcdqWrVttb7Va7YdOF5Zob9LZBUBKQAxH5VTRQn/9d2gYdbbdfkmKwRAkEAljVaP7/AAE64wE4gMIc98kLBZ0duVDnGuR2WuvPtHuyObt2+JNtC0L8qLYmjRfhgsL2JqD85oyvV+Jvx7XhU6wJBALIT5T+T3HdFRXlRAH12X74VXOnfHZ79sU/NNDBBtRN2AKfNo/I9g9xV7hZiVGTWEuDK8NImWYBU33PejWCZdS8="; //互联网金融


  //通道协议版本号
  
  //----易联信息： 以下信息区分为测试环境和生产环境，商户根据自己对接情况进行数据选择----
  //易联服务器地址_测试环境
  //public static final String PAYECO_URL = "https://testmobile.payeco.com";
  //易联服务器地址_生产环境
  public static final String PAYECO_URL = "https://imobile.payeco.com";

  //订单RSA公钥（易联提供）_测试环境
 // public static final String PAYECO_RSA_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCRxin1FRmBtwYfwK6XKVVXP0FIcF4HZptHgHu+UuON3Jh6WPXc9fNLdsw5Hcmz3F5mYWYq1/WSRxislOl0U59cEPaef86PqBUW9SWxwdmYKB1MlAn5O9M1vgczBl/YqHvuRzfkIaPqSRew11bJWTjnpkcD0H+22kCGqxtYKmv7kwIDAQAB";
  //我自己生成的测试公钥
  //public static final String PAYECO_RSA_PUBLIC_KEY ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDfSzn76oFrtUXtZLD8I2UNsW/5/pmTVcQH9HFqbBaQVnNabh53jMBhl+aaC3WxfUjG3hxJrhc5tEmxBdRANBfZfwBuKVwwJKdJvO/ibTyQNj4cba8Hyi43nG5Xz6gk1HRfHijbf95WeiolFObM0SEoUuq+K4tHkBsbzXQId21sswIDAQAB";
  //我自己生成的正式公钥
  //public static final String PAYECO_RSA_PUBLIC_KEY ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDZvLXrr8RqySHkDSSJE7uh1ra60ds3oca60LCPUt2KxMY8fGMpGi+hOmO1+E9camrACqVYThPII1vx3a6re714KJicvMS3j64OndM6kNmO5MvqX+xX3SbVhIEAzxgF75eGdmwxbk/k9icQ0GpHbmjQC/z3Ytz6udpDjqPN+D1fIwIDAQAB";
  //订单RSA公钥（易联提供）_生产环境
  public static final String PAYECO_RSA_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCoymAVb04bvtIrJxczCT/DYYltVlRjBXEBFDYQpjCgSorM/4vnvVXGRb7cIaWpI5SYR6YKrWjvKTJTzD5merQM8hlbKDucxm0DwEj4JbAJvkmDRTUs/MZuYjBrw8wP7Lnr6D6uThqybENRsaJO4G8tv0WMQZ9WLUOknNv0xOzqFQIDAQAB";
//--------------------------------------------------------------------------------------------------------------------------
  //代付
  //商户公钥,自己得换
  //public static final String dna_pub_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCqWSfUW3fSyoOYzOG8joy3xldpBanLVg8gEDcvm9KxVjqvA/qJI7y0Rmkc1I7l9vAfWtNzphMC+wlulpaAsa/4PbfVj+WhoNQyhG+m4sP27BA8xuevNT9/W7/2ZVk4324NSowwWkaqo1yuZe1wQMcVhROz2h+g7j/uZD0fiCokWwIDAQAB";
  //生产私钥//
  
  public static final String dna_pub_key ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDc+L2JGUKlGtsFm2f/wuF2T6/8mc6yrN8tLPgsx7sxAatvMvunHLXKC8xjkChHqVfJgohV4OIWe8zCw7jPsJMiPvrNnFHJ2Mumg/zQ8eZOnzMA0LDqBNFvZnOpy2XtagQn4yxxzG9+9h4P5eNojC3vD2t3H/6q5V3Cd022/egIZQIDAQAB";
  public static final String mer_pfx_key = "d:\\104000000072508-Signature.pfx";//商户私钥
  //public static final String mer_pfx_key = "d:\\yilian.pfx";//商户私钥
  //public static final String mer_pfx_pass = "11111111";//商户私钥密码测试
  public static final String mer_pfx_pass = "19186701";//商户私钥密码生产
  //public static final String url = "https://testagent.payeco.com:9444/service";//测试环境下单地址
  //用户名
  //public static final String user_name="13728096874";//测试
  public static final String user_name="13312197276";//生产
  //前面的实现了自动代付功能13312197276 13312197275
  //商户号
  //public static final String merchant_id="002020000008";//测试
  public static final String merchant_id="100120000393";  //生产
  //前面的实现了自动代付功能100120000393  100120000376
  
  public static String url = "https://agent.payeco.com/service";//生产环境下单地址
  //代付公钥——测试环境
  public static final String PAY_KEY ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDc+L2JGUKlGtsFm2f/wuF2T6/8mc6yrN8tLPgsx7sxAatvMvunHLXKC8xjkChHqVfJgohV4OIWe8zCw7jPsJMiPvrNnFHJ2Mumg/zQ8eZOnzMA0LDqBNFvZnOpy2XtagQn4yxxzG9+9h4P5eNojC3vD2t3H/6q5V3Cd022/egIZQIDAQAB";
  //商户秘钥
  public static final String SECRET_KEY ="E661095F2DFA46AE";
  
  //下面目前没用
  //代付自己生成正式环境私钥
  public static final String PAY_TEST_PRIVATE_KEY= "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCh/iE1WEDB+hz6fhfcsIK1e9mk56Q+rNs5HgVmG1LO7wQsRwFCDimotpUpR+QtsjswORuQcG7A+vQ5TWNRAm/mtRXmPHaIgAOF9mE2v6qXzph0xYpW+4Rnp3IJqzRQr1vMwJyXeLyq3ZKiMNcT54HHMgKDiDgfkQCHVdwOR5s++wIDAQAB";
  //代付自己生成正式环境公钥
  public static final String PAY_TEST_PUBLIC_KEY="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKH+ITVYQMH6HPp+F9ywgrV72aTnpD6s2zkeBWYbUs7vBCxHAUIOKai2lSlH5C2yOzA5G5BwbsD69DlNY1ECb+a1FeY8doiAA4X2YTa/qpfOmHTFilb7hGencgmrNFCvW8zAnJd4vKrdkqIw1xPngccyAoOIOB+RAIdV3A5Hmz77AgMBAAECgYBnW3UmyytuWL4amU9yUPDlCKOiUuX502KAfAYn0vEv9VLtSVSlJ9IuwmhDkYomGd0n9JgAvav812HyGc1vh6WLPqAlQTX9+FpnyUchfsHjpHvOFoVN8xxeHMkGtqQnSm+2KNPb6DCdjSgZ+I9gO1kUZSMtBv65gVVRiT1xo45ToQJBAPYUCqdGOEgRkKwptYMOZbzNaGPsaQmyuhU/yfzvIe6l82f4McSZ9EBvcdoMk2b35jlbfMEweD/Hu7TNnCG1easCQQCohi19VDgO5ktlAIEb31K3iXB3EqV8W0LOFTVE1y91l0PTk3C933Gyksu8IskR/oCTaxU8sB1//Y7KE0h+GB/xAkAlqwu2b1lOLuImeHwAg8OvEwJXyWiKw4EYn5sbzL80+NI5qBR2M9rfwi97YVFvAaOMRCTkrba6jKh/FLpBrrXhAkA7PPcuj/Uon6Eu/s9jecv8wi4rXP2Yygego2nXKgD/x81KyRAfXjT8PYeqOVwOnDScHU8YxxuHixyvQboAwQAxAkEAyFkU0paPnf/mDxQ0ONWM1lroqFp0W4AlAOdborAkfY96wBg+OM5IqisV5AbpmmqXntn49hTxw/3X3iQc722rfQ==";
}
