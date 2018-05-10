package xdt.util;

/**
 * 
 * @Description
 * @author Shiwen .Li
 * @date 2016年12月19日 下午10:45:49
 * @version V1.3.1
 */
public enum QrCodeEnum {

	//微信二维码
	weixin(3, "tb_WeixinPay"),
	//支付宝二维码
	alipay(4, "tb_alipay");

	public Integer typeCode;
	public String type;//

	QrCodeEnum(Integer typeCode, String type) {
		this.typeCode=typeCode;
		this.type=type;
		
	}

}
