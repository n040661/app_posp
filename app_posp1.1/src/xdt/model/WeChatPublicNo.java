package xdt.model;
/**
 * 微信公众号信息
 *
 */
public class WeChatPublicNo {

	private String retCode ;       //返回码
	private String retMessage;      //返回信息
	private String oagentno         ;       //         o单编号
	private String wechatname       ;       //         微信名
	private String wechatcontent    ;       //         微信链接（二维码）
	private String modifytime       ;       //         修改时间     格式   yyyy-mm-dd HH:mm:ss
	private String modifyuser       ;       //         修改人
	public String getOagentno() {
		return oagentno;
	}
	public void setOagentno(String oagentno) {
		this.oagentno = oagentno;
	}
	public String getWechatname() {
		return wechatname;
	}
	public void setWechatname(String wechatname) {
		this.wechatname = wechatname;
	}
	public String getWechatcontent() {
		return wechatcontent;
	}
	public void setWechatcontent(String wechatcontent) {
		this.wechatcontent = wechatcontent;
	}
	public String getModifytime() {
		return modifytime;
	}
	public void setModifytime(String modifytime) {
		this.modifytime = modifytime;
	}
	public String getModifyuser() {
		return modifyuser;
	}
	public void setModifyuser(String modifyuser) {
		this.modifyuser = modifyuser;
	}
	public String getRetCode() {
		return retCode;
	}
	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}
	public String getRetMessage() {
		return retMessage;
	}
	public void setRetMessage(String retMessage) {
		this.retMessage = retMessage;
	}
	
	
	
}
