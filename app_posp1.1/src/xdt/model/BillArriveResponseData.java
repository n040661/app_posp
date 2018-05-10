package xdt.model;

import org.apache.commons.lang.StringUtils;

import java.util.Comparator;
import java.util.List;

/**
 * 到账对象
 * User: Jeff
 * Date: 15-6-1
 * Time: 上午10:46
 * To change this template use File | Settings | File Templates.
 */
public class BillArriveResponseData implements Comparator<BillArriveResponseData> {

    String month;//YYYY-MM
    String date;//dd
    String amountSum;//总金额
    String fee; //手续费
    String tag; // 1：月  2：日
    List<BillArriveResponseData> billSub;   //在tag等于2的时候，子节点

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmountSum() {
        return amountSum;
    }

    public void setAmountSum(String amountSum) {
        this.amountSum = amountSum;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<BillArriveResponseData> getBillSub() {
        return billSub;
    }

    public void setBillSub(List<BillArriveResponseData> billSub) {
        this.billSub = billSub;
    }

    @Override
    public int compare(BillArriveResponseData o1, BillArriveResponseData o2) {

        if(o1 != null && o2 != null){
            String date1 = o1.getDate();
            String date2 = o2.getDate();
            if(StringUtils.isNotBlank(date1)&&StringUtils.isNotBlank(date2)){
                return date1.compareTo(date2);
            }
        }
        return 0;
    }
}
