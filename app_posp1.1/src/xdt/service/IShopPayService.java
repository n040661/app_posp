package xdt.service;

import xdt.model.PmsAppTransInfo;

import javax.servlet.http.HttpSession;

/**
 * Created with IntelliJ IDEA.
 * User: Jeff
 * Date: 16-3-11
 * Time: 下午2:25
 * To change this template use File | Settings | File Templates.
 */

public interface IShopPayService {
    /**
     * 生成订单
     * @param session
     * @param request
     * @return
     */
    String produceOrder(HttpSession session,String request) throws Exception;

    /**
     *商城快捷支付预下单
     * @param session
     * @param shopPayRequest
     * @return
     * @throws Exception
     */
    String shopOrderQuickPrePay(HttpSession session, String shopPayRequest) throws Exception;

    /**
     * 商城快捷支付消费认证
     * @param session
     * @param shopPayRequest
     * @return
     * @throws Exception
     */
   String shopOrderQuickPay(HttpSession session, String shopPayRequest) throws Exception;
    /**
     * 商城快捷支付短信重发接口
     * @param session
     * @param shopPayRequest
     * @return
     * @throws Exception
     */
   String shopOrderPrePayReSendMsg(HttpSession session, String shopPayRequest) throws Exception;

    /**
     * 修改商户余额
     * @param pmsAppTransInfo
     * @return
     */
   int updateMerchantBanlance(PmsAppTransInfo pmsAppTransInfo);

}
