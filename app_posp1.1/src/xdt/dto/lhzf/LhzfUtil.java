package xdt.dto.lhzf;

import xdt.dto.BaseUtil;

public class LhzfUtil {
	//商户支付KEY（测试环境）
	//public static final String merKey="78c9a448f2d64b04bccf053cc2877b45";
	public static final String merKey="611a23d2dfe14719b87bcbfa1d5a46c8";//生产参数
	//
	//public static final String paySecret="f3e3d5e9cc0745119637699d73235d83";
	public static final String paySecret="bea44605d46c4584aff7a3bb2b929d4f";//生产参数
	//
	public static final String notifyUrl=BaseUtil.url+"/LqzfController/notifyUrl.action";
	//
	public static final String returnUrl=BaseUtil.url+"/LqzfController/returnUrl.action";
	//
	//public static final String commonRequestUrl1="http://111.230.194.185:8080/gateway/transaction/request";
	//生产参数
	public static final String commonRequestUrl1="https://service.blueseapay.com/gateway/transaction/request";
	
	
	//public static final String merKey="c181f5d336a240ebbde00ba3e50b57ba";
	//public static final String paySecret="56a09df342e74ad9891a24ae94f9e237";
}
