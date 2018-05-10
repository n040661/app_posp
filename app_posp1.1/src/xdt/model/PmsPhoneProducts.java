package xdt.model;

import java.util.Comparator;

public class PmsPhoneProducts implements Comparator<PmsPhoneProducts>{
	
    private String prodid; //产品id

    private String prodcontent; //面额

    private String prodprice; //优惠价

    private String prodisptype; //运营商 例如 联通 移动

    private String proddelaytimes; //充值需要的分钟数 

    private String prodprovinceid; //省名称

    private String prodtype; //产品类型

    public String getProdid() {
        return prodid;
    }

    public void setProdid(String prodid) {
        this.prodid = prodid == null ? null : prodid.trim();
    }

    public String getProdcontent() {
        return prodcontent;
    }

    public void setProdcontent(String prodcontent) {
        this.prodcontent = prodcontent == null ? null : prodcontent.trim();
    }

    public String getProdprice() {
        return prodprice;
    }

    public void setProdprice(String prodprice) {
        this.prodprice = prodprice == null ? null : prodprice.trim();
    }

    public String getProdisptype() {
        return prodisptype;
    }

    public void setProdisptype(String prodisptype) {
        this.prodisptype = prodisptype == null ? null : prodisptype.trim();
    }

    public String getProddelaytimes() {
        return proddelaytimes;
    }

    public void setProddelaytimes(String proddelaytimes) {
        this.proddelaytimes = proddelaytimes == null ? null : proddelaytimes.trim();
    }

    public String getProdprovinceid() {
        return prodprovinceid;
    }

    public void setProdprovinceid(String prodprovinceid) {
        this.prodprovinceid = prodprovinceid == null ? null : prodprovinceid.trim();
    }

    public String getProdtype() {
        return prodtype;
    }

    public void setProdtype(String prodtype) {
        this.prodtype = prodtype == null ? null : prodtype.trim();
    }

    @Override
    public int compare(PmsPhoneProducts o1, PmsPhoneProducts o2) {
        Integer result = 0;
        if(o1 != null && o2 != null){

               Double d1 = Double.parseDouble(o1.getProdprice());
               Double d2 = Double.parseDouble(o2.getProdprice());

            if(d1 > d2){
                result = 1;
            }else if(d1 < d2){
                result = -1;
            }
        }
        return  result;
    }
}