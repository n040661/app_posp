package xdt.service;

import xdt.dto.BillListRequestDTO;

import javax.servlet.http.HttpSession;

/**
 * Created with IntelliJ IDEA.
 * User: Jeff
 * Date: 15-5-22
 * Time: 下午1:59
 * To change this template use File | Settings | File Templates.
 */
public interface IBillService {
    /**
     * 获取账单列表
     * @param requestData
     * @param session
     * @return
     */
    String billList(String requestData,  HttpSession session);

    /**
     * 获取账单详情
     * @param requestData
     * @param session
     * @return
     */
    String billDetail(String requestData, HttpSession session);

    /**
     * 到账列表
     * @param requestData
     * @param session
     * @return
     */
    String billArriveList(String requestData,HttpSession session);
}
