package xdt.dto.pay;

import okhttp3.MediaType;
import xdt.dto.BaseUtil;

public class PayUtil {

	public static final String serverProviderCode="10000125";
	
	public static final String key="3B9C8E0220EA94AE0118B6DAD348FAE20A9B21CB27592E60";
	
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	
	public static final String urlTest ="https://cjdev-api.chanpay.co:18888";//https://cjdev-api.chanpay.co:18888  https://qkapi.chanpay.com
	public static final String url ="https://qkapi.chanpay.com";
	public static final String notifyUrl =BaseUtil.url+"/PayController/notifyUrl.action";
}
