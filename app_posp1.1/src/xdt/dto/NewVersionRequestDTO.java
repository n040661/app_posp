package xdt.dto;

/**
 * 版本更新请求接口
 * User: Jeff
 * Date: 15-6-5
 * Time: 下午8:01
 * To change this template use File | Settings | File Templates.
 */
public class NewVersionRequestDTO {
    String clientType; //1:android 2:ios
    String oAgentNo;//欧单编号

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getoAgentNo() {
        return oAgentNo;
    }

    public void setoAgentNo(String oAgentNo) {
        this.oAgentNo = oAgentNo;
    }
}
