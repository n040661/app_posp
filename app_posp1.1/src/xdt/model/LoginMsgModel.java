package xdt.model;

/**
 * Created with IntelliJ IDEA.
 * User: Jeff
 * Date: 16-2-2
 * Time: 上午11:18
 * To change this template use File | Settings | File Templates.
 */
public class LoginMsgModel {
    String status; //登录时是否显示提示信息，如果 1 msg，否则为空
    String msg;  //提示信息

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
