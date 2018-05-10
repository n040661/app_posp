package xdt.quickpay.jbb.common;

public class IConstants {
	
//易通门户公钥证书	
	public static final String ECTON_PUBLIC_CERTFILE = "c:/www.etonpay.com.cer";

//测试
	public static final String  BALANCE_QRY_UEL  = "https://58.56.23.89:8443/EctonMerPortal/daifuCard/balanceQry";
	public static final String DAI_FU_URL = "https://merchant.etonepay.com/daifuCard/dodaifu";
	
	
	//public static final String DAI_FU_URL = "http://merchant.etonepay.com/daifuCard/dodaifu";
	
	//public static final String DAI_FU_URL = "https://merchant.etonepay.com/daifuCard/dodaifu";
	public static final String DAIFU_QRY_URL = "https://merchant.etonepay.com/daifuCard/daifuQry ";
	public static final String REFUND_QRY_URL = "https://58.56.23.89:8443/EctonMerPortal/daifuCard/refundQry";
	public static final String DAIFU_RECON_URL = "https://58.56.23.89:8443/EctonMerPortal/daifuCard/daifuRecon";

//生成本地对账文件
	public static final String DAIFU_RECON_FILE_LOCAL = "C:/ectonrecon/20150520/recon.txt";

//生产	
//	public static final String BALANCE_QRY_UEL  = "https://merchant.etonepay.com/daifuCard/balanceQry";
//	public static final String DAI_FU_URL = "https://merchant.etonepay.com/daifuCard/daifu";
//	public static final String DAIFU_QRY_URL = "https://merchant.etonepay.com/daifuCard/daifuQry";
//	public static final String REFUND_QRY_URL = "https://merchant.etonepay.com/daifuCard/refundQry";
//	public static final String DAIFU_RECON_URL = "https://merchant.etonepay.com/daifuCard/daifuRecon";
}
