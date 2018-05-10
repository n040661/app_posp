package xdt.dto;
/**
 * 刷卡支付 签到响应（第三方）
 * @author p
 *
 */
public class CreditPaymentSignInResponseDTO {
    private String dealIype;//消息类型
    
    private String bitmap;//位元素 
    
    private String serialNo;// *  11域 流水号  6位  *
    	
    private String transData;//12域 所在地时间 HHMMSS 6位 
    
    private String transTime;//13域 所在地日期 MMDD  4位
    
	private String idCode;// *   32域 收单机构标识码 11位 * 
    
    private String reference;// *   37域 检索参考 12位 *
    
    private String errCode;//39域 我把这个定义为返回的错误信息吧。4位  --错误码  0000成功
    
    private String busPos;//41域 通道POS的信息 8位  //这个41里 a.posbusinessno 试
    
    private String busInfo;//42域 通道商户的信息  15位 //42也是 merc_id 244193370717935 
    
    private String posVersion;//*   59域 POS终端信息  600位  *
    
    private String reseved60;//*    60域 交易类型码+批次号+网络管理信息码+终端读取能力+基于PBOC借/贷记标准的IC卡条件代码……  一般13位  *
    
    private String af063;//63域 操作员国际信用卡公司代码+自定义域2  一般3位 加后面的自定义域120
    
    private String af064;//64域 前面所有域的位图+响应消息中39域为“00”时必选  就是个bitmap位图
    
    private String reservedPrivate;//62域 解密工作密钥mac pin 自定义域  22位   
    
    private String zhuKek;//存主密钥密文。下载参数获得的

    private String zmChkVal;//主密钥校验值

    private String  wkChkVal;//工作密钥校验值

    private String zhukek;//主秘密 密文

    private String isNeedZMK;  //是否需要下载主密钥。0-不需要，1-需要


    public String getZhuKek() {
		return zhuKek;
	}

	public void setZhuKek(String zhuKek) {
		this.zhuKek = zhuKek;
	}

	public String getReservedPrivate() {
		return reservedPrivate;
	}

	public void setReservedPrivate(String reservedPrivate) {
		this.reservedPrivate = reservedPrivate;
	}
	private Integer retCode;// 信息编号

	private String retMessage;// 信息描述

	public Integer getRetCode() {
		return retCode;
	}

	public void setRetCode(Integer retCode) {
		this.retCode = retCode;
	}

	public String getRetMessage() {
		return retMessage;
	}

	public void setRetMessage(String retMessage) {
		this.retMessage = retMessage;
	}
    public String getTransData() {
		return transData;
	}
	public void setTransData(String transData) {
		this.transData = transData;
	}
	public String getTransTime() {
		return transTime;
	}
	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}
	public String getIdCode() {
		return idCode;
	}
	public void setIdCode(String idCode) {
		this.idCode = idCode;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getErrCode() {
		return errCode;
	}
	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}
    public String getDealIype() {
		return dealIype;
	}
	public void setDealIype(String dealIype) {
		this.dealIype = dealIype;
	}
	public String getBitmap() {
		return bitmap;
	}
	public void setBitmap(String bitmap) {
		this.bitmap = bitmap;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public String getBusPos() {
		return busPos;
	}
	public void setBusPos(String busPos) {
		this.busPos = busPos;
	}
	public String getBusInfo() {
		return busInfo;
	}
	public void setBusInfo(String busInfo) {
		this.busInfo = busInfo;
	}
	public String getPosVersion() {
		return posVersion;
	}
	public void setPosVersion(String posVersion) {
		this.posVersion = posVersion;
	}
	public String getReseved60() {
		return reseved60;
	}
	public void setReseved60(String reseved60) {
		this.reseved60 = reseved60;
	}
	public String getAf063() {
		return af063;
	}
	public void setAf063(String af063) {
		this.af063 = af063;
	}
	public String getAf064() {
		return af064;
	}
	public void setAf064(String af064) {
		this.af064 = af064;
	}

    public String getZmChkVal() {
        return zmChkVal;
    }

    public void setZmChkVal(String zmChkVal) {
        this.zmChkVal = zmChkVal;
    }

    public String getWkChkVal() {
        return wkChkVal;
    }

    public void setWkChkVal(String wkChkVal) {
        this.wkChkVal = wkChkVal;
    }

    public String getZhukek() {
        return zhukek;
    }

    public void setZhukek(String zhukek) {
        this.zhukek = zhukek;
    }

    public String getNeedZMK() {
        return isNeedZMK;
    }

    public void setNeedZMK(String needZMK) {
        isNeedZMK = needZMK;
    }
}
