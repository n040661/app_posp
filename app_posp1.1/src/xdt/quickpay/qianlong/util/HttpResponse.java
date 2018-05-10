package xdt.quickpay.qianlong.util;

public class HttpResponse{
    int statusCode;
    String rspStr;
    
    public int getStatusCode(){
        return statusCode;
    }
    public void setStatusCode(int statusCode){
        this.statusCode = statusCode;
    }
    public String getRspStr(){
        return rspStr;
    }
    public void setRspStr(String rspStr){
        this.rspStr = rspStr;
    }
    
    
}
