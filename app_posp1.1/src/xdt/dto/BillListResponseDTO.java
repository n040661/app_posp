package xdt.dto;

import xdt.model.BillListRequestData;
import xdt.model.PmsAppTransInfo;
import xdt.util.PageView;

import java.util.List;
import java.util.Map;

/**
 * 账单列表返回对象
 * User: Jeff
 * Date: 15-5-22
 * Time: 下午5:41
 * To change this template use File | Settings | File Templates.
 */
public class BillListResponseDTO {

    private Integer retCode;// 信息编号

    private String retMessage;// 信息描述

    private PageView pageView;//账单分页列表

    public Integer getRetCode() {
        return retCode;
    }

    public void setRetCode(Integer retCode) {
        this.retCode = retCode;
    }

    public String getRetMessage() {
        return retMessage;
    }

    public void setRetMessage(String retMessage) {
        this.retMessage = retMessage;
    }

    public PageView getPageView() {
        return pageView;
    }

    public void setPageView(PageView pageView) {
        this.pageView = pageView;
    }
}
