package xdt.quickpay.mobao;

/**
 * 摩宝支付短信重发接口
 * User: Jeff
 * Date: 16-3-15
 * Time: 下午6:42
 * To change this template use File | Settings | File Templates.
 */
public class MobaoTransReSendMsgDto {

    String versionId;
    String status;// 00：成功 01：失败 02：系统错误
    String merId;
    String orderId;
    String yzm;
    String refCode;
    String refMsg;
    String signData;

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMerId() {
        return merId;
    }

    public void setMerId(String merId) {
        this.merId = merId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getYzm() {
        return yzm;
    }

    public void setYzm(String yzm) {
        this.yzm = yzm;
    }

    public String getRefCode() {
        return refCode;
    }

    public void setRefCode(String refCode) {
        this.refCode = refCode;
    }

    public String getRefMsg() {
        return refMsg;
    }

    public void setRefMsg(String refMsg) {
        this.refMsg = refMsg;
    }

    public String getSignData() {
        return signData;
    }

    public void setSignData(String signData) {
        this.signData = signData;
    }
}
