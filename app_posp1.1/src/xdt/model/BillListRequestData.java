package xdt.model;

import java.util.List;

/**
 * 账单列表需要返回的封装
 * User: Jeff
 * Date: 15-5-24
 * Time: 上午9:23
 * To change this template use File | Settings | File Templates.
 */
public class BillListRequestData {
   String date; //yyyy-mm-dd
   String totalAmonut;//总金额
   List<BillNeedData> billNeedDatas;//订单列表

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotalAmonut() {
        return totalAmonut;
    }

    public void setTotalAmonut(String totalAmonut) {
        this.totalAmonut = totalAmonut;
    }

    public List<BillNeedData> getBillNeedDatas() {
        return billNeedDatas;
    }

    public void setBillNeedDatas(List<BillNeedData> billNeedDatas) {
        this.billNeedDatas = billNeedDatas;
    }
}
