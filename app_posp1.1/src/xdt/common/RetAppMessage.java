package xdt.common;

public class RetAppMessage {
	
	//定义返回给手机应用的消息
	public static final String MERCHANTDOESNOTEXIST="8:merchantDoesNotExist";//商户信息不存在
	public static final String PAYTHEWRONGPASSWORD="9:payTheWrongPassword"; //支付密码输入错误
	public static final String ACCOUNTPAYCHANNELDOESNOTEXIST="10:accountPayChannelDoesNotExist"; //账户支付通道不存在
	public static final String TRADINGINFOSAVEFAILED="11:tradingInfoSaveFailed"; //交易信息保存失败
	public static final String TRADINGINFOSAVESUCCESS="12:tradingInfoSaveSuccess"; //交易信息保存成功
	public static final String SESSIONINVALIDATION="13:sessionInvalidation";//会话失效
	public static final String DATAANALYTICALFAILURE="14:dataAnalyticalFailure";//数据解析失败
	public static final String DATAANALYTICALSUCCESS="15:dataAnalyticalSuccess";//数据解析成功
	public static final String INTERFACEINFORETURNANERROR="16:interfaceInfoReturnAnError";//接口信息返回错误
	public static final String XMLDATAPARSEINGERRORS="17:xmlDataParseingErrors";//xml数据解析错误
	public static final String MOBILEPHONENUMBERISNOTLEGAL="18:mobilePhoneNumberIsNotLegal";//手机号不合法
	public static final String BANKCARDISNOTSUPPORTED="19:bankCardIsNotSupported";//不支持的银行卡
	public static final String BANKCARDNUMBERANDBANKNAMENOMATCH="20:bankCardNumberAndBankNameNoMatch"; //银行卡号与银行名称不匹配
	public static final String BANKCARDISBOUND="21:bankCardIsBound";//银行卡已绑定
	public static final String BANKCARDNUMBERISILLEGAL="22:bankCardNumberIsIllegal";//银行卡号不合法
	public static final String TRADINGINFOUPDATEFAILED="23:tradingInfoUpdateFailed"; //交易信息更新失败
	public static final String HISTORYRECORDSAVEFAILED="24:historyRecordSaveFailed"; //历史记录保存失败
	public static final String HISTORYRECORDSAVESUCCESS="25:historyRecordSaveSuccess"; //历史记录保存成功
	public static final String HISTORYRECORDGETFAILED="26:historyRecordGetFailed"; //历史记录信息获取失败
	public static final String BINDINGBANKCARDGETFAILED="27:bindingBankCardGetFailed"; //绑定银行卡信息获取失败
	public static final String EMAILERROR="28:emailError";//邮箱输入错误
	public static final String COMEONKAKAERROR="29:comeOnKakaError";//加油卡卡号不合法
	public static final String IDNUMBERERROR="30:idNumberError";//身份证号输入错误
	public static final String IDNUMBEREXIST="31:idNumberExist";//身份证号已存在
	public static final String IMAGEUPLOADFAIL="32:imageUploadFail"; //图片上传失败
	public static final String BINDINGBANKCARDSAVEFAILED="33:bindingBankCardSaveFailed";//绑定银行卡保存失败
	public static final String ACCOUNTPAYCHANNELEXIST ="34:accountPayChannelExist";//账户支付通道存在
	public static final String TRADINGFAIL="35:tradingFail"; //交易失败
	public static final String TRADINGSUCCESS="0:tradingSuccess"; //交易成功
	public static final String EMAILEXIST ="36:emailExist"; //输入的邮箱已存在
	public static final String LACKOFBALANCE="37:lackOfBalance"; //余额不足
	public static final String ACCOUNTNOTENABLED = "38:accountNotEnabled"; //账户未启用
	public static final String NOTAUTHNENTICATION ="39:notAuthentication";//未实名认证
	public static final String NOACCOUNT ="40:noAccount";//账户不存在
	public static final String AMOUNTILLEGAL ="41:amountIllegal";//金额非法
    public static final String HISTORYALREADYEXIST="42:historyAlreadyExist"; //历史记录已经存在
    public static final String RATEEXIST="43:rateExist"; //费率录入有误，请重新录入
	/**
	 * 解析消息码
	 * @param message
	 * @return
	 */
	public static String parseMessageCode(String messageCode){
		String retMessage = "";
		if(messageCode.equalsIgnoreCase("empty")){
			retMessage = "请将信息填写完整";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("mobilePhoneNumberIsNotLegal")){
			retMessage = "请输入合法的手机号";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("failure")){
			retMessage = "验证码失效，请重新获取";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("error")){
			retMessage = "验证码输入错误";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("merchantDoesNotExist")){
			retMessage = "商户信息不存在";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("payTheWrongPassword")){
			retMessage = "密码输入错误";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("accountPayChannelDoesNotExist")){
			retMessage = "账户支付通道不存在";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("tradingInfoSaveFailed")){
			retMessage = "交易信息保存失败";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("interfaceInfoReturnAnError")){
			retMessage = "接口信息返回错误";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("xmlDataParseingErrors")){
			retMessage = "XML数据解析错误";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("noAccount")){
			retMessage = "账户信息不存在";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("accountNotEnabled")){
			retMessage = "账户未启用";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("notAuthentication")){
			retMessage = "请重新实名认证";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("fail")){    
			retMessage = "操作失败";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("success")){
			retMessage = "操作成功";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("tradingFail")){    
			retMessage = "交易失败";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("tradingSuccess")){
			retMessage = "交易成功";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("amountIllegal")){
			retMessage = "非法金额";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("lackOfBalance")){
			retMessage = "账户余额不足";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("bankCardNumberIsIllegal")){
			retMessage = "请输入合法的银行卡号";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("bankCardIsBound")){
			retMessage = "此银行卡已绑定";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("bankCardIsNotSupported")){
			retMessage = "系统不支持的银行卡";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("bankCardNumberAndBankNameNoMatch")){
			retMessage = "银行卡号与银行名称不匹配";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("tradingInfoUpdateFailed")){
			retMessage = "处理中，交易信息更新失败";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("historyRecordSaveFailed")){
			retMessage = "历史记录保存失败";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("historyRecordSaveSuccess")){
			retMessage = "历史记录保存成功";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("historyRecordGetFailed")){
			retMessage = "历史记录信息获取失败";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("bindingBankCardGetFailed")){
			retMessage = "银行卡绑定信息获取失败";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("emailError")){
			retMessage = "请输入合法的邮箱";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("idNumberError")){
			retMessage = "请输入合法的身份证号";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("idNumberExist")){
			retMessage = "身份证号已存在";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("emailExist")){
			retMessage = "邮箱已存在";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("comeOnKakaError")){
			retMessage = "请输入合法的加油卡卡号";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("imageUploadFail")){
			retMessage = "图片上传失败";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("bindingBankCardSaveFailed")){
			retMessage = "绑定银行卡保存失败";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("sessionInvalidation")){
			retMessage = "会话失效，请重新登录";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("dataAnalyticalFailure")){
			retMessage = "数据解析失败";
			return retMessage;
		}
		if(messageCode.equalsIgnoreCase("rateExist")){
			retMessage = "费率录入有误，请重新录入";
			return retMessage;
		}
		return retMessage;
	}
	/**
	 * 刷卡返回码判断
	 * @param code
	 * @return
	 */
	public static String parseMessageCodeCardPos(String code){
        code=code.substring(2);	    
		String retMessage = "交易失败,请联系发卡行";
		if(code.equals("A0")){return retMessage="签到失败，请重新签到";}
		if(code.equals("03")){return retMessage="商户未登记";}
		if(code.equals("79")){return retMessage="POS终端重传脱机数据";}
		if(code.equals("86")){return retMessage="与原交易的金额不一致";}
		if(code.equals("87")){return retMessage="当批次金额已超限,请结算后再试";}
		if(code.equals("90")){return retMessage="程序需更新";}
		if(code.equals("91")){return retMessage="参数需更新";}
		if(code.equals("14")){return retMessage="无效卡号,请联系发卡行";}
		if(code.equals("15")){return retMessage="此卡不能受理";}
		if(code.equals("36")){return retMessage="此卡有误,请换卡重试";}
		if(code.equals("38")){return retMessage="密码错误次数超限";}
		if(code.equals("51")){return retMessage="余额不足,请查询";}
		if(code.equals("55")){return retMessage="密码错,请重试";}
		if(code.equals("61")){return retMessage="超出取款金额限制";}
		if(code.equals("65")){return retMessage="超出取款次数限制";}
		if(code.equals("13")){return retMessage="交易金额超限,请重试";}
		if(code.equals("58")||code.equals("59")||code.equals("60")){return retMessage="终端无效,请联系收单行或银联";}
		if(code.equals("09")||code.equals("08")){return retMessage="终端未登记,请联系收单行或银联";}
		if(code.equals("33")||code.equals("54")){return retMessage="过期卡,请联系发卡行";}
		if(code.equals("37")||code.equals("43")||code.equals("41")){return retMessage="没收卡,请联系收单行";}
		if(code.equals("50")||code.equals("72")||code.equals("73")||code.equals("74")){return retMessage="校验错,请重新签到";}
		if(code.equals("69")||code.equals("81")||code.equals("89")||code.equals("93")){return retMessage="请向网络中心签到";}
		
		if(code.equals("04")||code.equals("07")||code.equals("34")||code.equals("35")){return retMessage="没收卡,请联系收单行";}
		if(code.equals("12")||code.equals("22")||code.equals("88")||code.equals("84")){return retMessage="交易失败,请稍后请重试";}
		
		if(code.equals("66")||code.equals("68")||code.equals("70")||code.equals("71")||code.equals("94")||code.equals("96")||code.equals("97")||code.equals("98")||code.equals("99")){return retMessage="交易失败,请联系收单行或银联";}
		
		return retMessage;
	}
}