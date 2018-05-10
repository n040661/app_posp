package xdt.model;

import java.io.Serializable;

/**
 * @ClassName: ChannleMerchantConfigKey
 * @Description: 商户密钥表 
 * @author LiShiwen
 * @date 2016年6月21日 下午5:19:31
 *
 */
public class ChannleMerchantConfigKey implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String mercid;//商户号

    private String merchantkey;//商户密钥

    private String channletype;//渠道类

    private String createtime;//创建时间

    private String createuser;//创建人

    private String modifytime;//修改时间

    private String modifyuser;//修改人

    private String modifyreason;//修改原因一直不用修改成白名单ip

    
    public String getMercid() {
        return mercid;
    }

    public void setMercid(String mercid) {
        this.mercid = mercid == null ? null : mercid.trim();
    }

    public String getMerchantkey() {
        return merchantkey;
    }

    public void setMerchantkey(String merchantkey) {
        this.merchantkey = merchantkey == null ? null : merchantkey.trim();
    }

    public String getChannletype() {
        return channletype;
    }

    public void setChannletype(String channletype) {
        this.channletype = channletype == null ? null : channletype.trim();
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime == null ? null : createtime.trim();
    }

    public String getCreateuser() {
        return createuser;
    }

    public void setCreateuser(String createuser) {
        this.createuser = createuser == null ? null : createuser.trim();
    }

    public String getModifytime() {
        return modifytime;
    }

    public void setModifytime(String modifytime) {
        this.modifytime = modifytime == null ? null : modifytime.trim();
    }

    public String getModifyuser() {
        return modifyuser;
    }

    public void setModifyuser(String modifyuser) {
        this.modifyuser = modifyuser == null ? null : modifyuser.trim();
    }

    public String getModifyreason() {
        return modifyreason;
    }

    public void setModifyreason(String modifyreason) {
        this.modifyreason = modifyreason == null ? null : modifyreason.trim();
    }

	@Override
	public String toString() {
		return "ChannleMerchantConfigKey [mercid=" + mercid + ", merchantkey=" + merchantkey + ", channletype="
				+ channletype + ", createtime=" + createtime + ", createuser=" + createuser + ", modifytime="
				+ modifytime + ", modifyuser=" + modifyuser + ", modifyreason=" + modifyreason + "]";
	}
}