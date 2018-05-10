package xdt.dto;

/**
 * 欧飞
 * User: Jeff
 * Date: 15-5-15
 * Time: 下午1:04
 * To change this template use File | Settings | File Templates.
 */
public class OffiBackRequestDTO {
    String ret_code;  //充值后状态，1代表成功，9代表撤消
    String sporder_id; //SP订单号
    String ordersuccesstime; //处理时间
    String err_msg; //失败原因(ret_code为1时，该值为空)

    public String getRet_code() {
        return ret_code;
    }

    public void setRet_code(String ret_code) {
        this.ret_code = ret_code;
    }

    public String getSporder_id() {
        return sporder_id;
    }

    public void setSporder_id(String sporder_id) {
        this.sporder_id = sporder_id;
    }

    public String getOrdersuccesstime() {
        return ordersuccesstime;
    }

    public void setOrdersuccesstime(String ordersuccesstime) {
        this.ordersuccesstime = ordersuccesstime;
    }

    public String getErr_msg() {
        return err_msg;
    }

    public void setErr_msg(String err_msg) {
        this.err_msg = err_msg;
    }

    @Override
    public String toString() {
        return "OffiBackRequestDTO{" +
                "ret_code='" + ret_code + '\'' +
                ", sporder_id='" + sporder_id + '\'' +
                ", ordersuccesstime='" + ordersuccesstime + '\'' +
                ", err_msg='" + err_msg + '\'' +
                '}';
    }
}
