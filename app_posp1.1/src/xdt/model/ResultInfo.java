package xdt.model;

/**
 * 返回错误码，已经错误信息的封装
 * User: Jeff
 * Date: 16-1-20
 * Time: 上午10:40
 * To change this template use File | Settings | File Templates.
 */
public class ResultInfo {
    String errCode; //0：通过 ,1:有限制，具体参考msg中的内容 2:参数错误 ，具体参考msg中的内容
    String msg; //返回消息

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
