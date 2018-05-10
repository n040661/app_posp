package xdt.quickpay.hf.comm;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings("serial")
public class PlatOutboundMsg implements Serializable {

    private boolean success = true;// 是否成功
    private String msg = "操作成功";// 提示信息
    private Map<String, Object> attributes;// 其他参数
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
