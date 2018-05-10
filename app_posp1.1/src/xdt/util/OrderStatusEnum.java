package xdt.util;

import org.apache.commons.lang.StringUtils;

/**
 * 订单状态管理
 * User: Jeff
 * Date: 15-5-15
 * initlize("200", "订单正在创建..."),\n\r
 * payFail("1", "支付失败"),
 * waitingClientPay("2", "等待客户端支付"),
 * returnMoneySuccess("3", "退款成功"),
 * waitingPlantPay("4", "客户端支付成功，等待服务器调用第三方支付平台支付"),
 * plantPayingNow("5", "第三方平台正在支付"),
 * plantCancelOrder("9", "第三方撤销订单"),
 * paySuccess("0", "成功支付"),
 * systemErro("100", "系统异常");
 * 
 */
public enum OrderStatusEnum {


    initlize("200", "订单正在创建...",200),
    payFail("1", "支付失败",1),
    waitingClientPay("2", "等待客户端支付",2),
    returnMoneySuccess("3", "退款成功",3),
    waitingPlantPay("4", "客户端支付成功，等待服务器调用第三方支付平台支付",4),
    plantPayingNow("5", "第三方平台正在支付",5),
    plantCancelOrder("9", "第三方撤销订单",9),
    paySuccess("0", "成功支付",0),
    systemErro("100", "系统异常",100);


    String status;//状态
    String description;//描述
    Integer intStatus;//整形状态值

    private OrderStatusEnum(String status, String description,Integer intStatus) {
        this.status = status;
        this.description = description;
        this.intStatus = intStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getIntStatus() {
        return intStatus;
    }

    public void setIntStatus(Integer intStatus) {
        this.intStatus = intStatus;
    }

    /**
     * 将状态值对应到描述
     * @param status
     * @return
     */
    public static String getDesFromStatu(String status){
       String desResult = "";
       if(StringUtils.isNotBlank(status)){
            if(status.equals(OrderStatusEnum.initlize.getStatus())){
                desResult = OrderStatusEnum.initlize.getDescription();
            }else if(status.equals(OrderStatusEnum.payFail.getStatus())){
                desResult = OrderStatusEnum.payFail.getDescription();
            }else if(status.equals(OrderStatusEnum.waitingClientPay.getStatus())){
                desResult = OrderStatusEnum.waitingClientPay.getDescription();
            }else if(status.equals(OrderStatusEnum.returnMoneySuccess.getStatus())){
                desResult = OrderStatusEnum.returnMoneySuccess.getDescription();
            }else if(status.equals(OrderStatusEnum.waitingPlantPay.getStatus())){
                desResult = OrderStatusEnum.waitingPlantPay.getDescription();
            }else if(status.equals(OrderStatusEnum.plantPayingNow.getStatus())){
                desResult = OrderStatusEnum.plantPayingNow.getDescription();
            }else if(status.equals(OrderStatusEnum.plantCancelOrder.getStatus())){
                desResult = OrderStatusEnum.plantCancelOrder.getDescription();
            }else if(status.equals(OrderStatusEnum.paySuccess.getStatus())){
                desResult = OrderStatusEnum.paySuccess.getDescription();
            }else if(status.equals(OrderStatusEnum.systemErro.getStatus())){
                desResult = OrderStatusEnum.systemErro.getDescription();
            }
       }
        return  desResult;
    }
}
