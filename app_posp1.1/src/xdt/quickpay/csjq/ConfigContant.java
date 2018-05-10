package xdt.quickpay.csjq;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.songshun.sdk.entity.BankReq;
import com.songshun.sdk.entity.OperatorReq;
import com.songshun.sdk.entity.SFRZReq;
import com.songshun.sdk.sign.SignatureUtil;
import com.songshun.sdk.util.MapRemoveNullUtil;

/**
 * Created by jyl on 2017/4/27.
 */
public class ConfigContant {

    public static  String ACCCODE="SS001";//长沙松顺平台分配应用编码

    /**
     * 长沙松顺平台分配私钥索引,调用接口使用
     */
    public static  String ACCESSKEYID="02aed7a1d91a436d8f0c704537774fbd";

    /**
     * 长沙松顺平台分配的私钥,与外部接口调用的私钥索引(ACCESSKEYID)配对, 内部服务签名使用
     */
    public static  String PRIVATEKEY="ca040cddd9064a94a21fb289bdaa1f5a";

    public static  String VERSION="1.0";//版本号
    public static  String MERCHANT="SS17112947";//商户号

    //图片数据不大于10M，字节base64编码
    public static  String IMAGEDATA="";

    /**
     * 生产环境URL地址
     */
   // public final static String URL="http://39.108.115.111/SMRZ_SRV";

    /**
     * 测试环境URL地址
     */
    public final static String URL="http://39.108.115.111/SMRZ_SRV";

    /**
     *  初始化身份证实名认证签名参数
     * @param req 身份证实名认证请求参数模型
     * @return
     * @throws Exception
     */
    public static  HashMap<String, Object> initParams(SFRZReq req) throws Exception{
        //获取参数签名
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("serviceCode",req.getServiceCode());
        map.put("name", req.getName());
        map.put("idNumber", req.getIdNumber());
        map.put("validFrom", req.getValidFrom());
        map.put("validEnd", req.getValidEnd());
        map.put("merchantId", MERCHANT);
        //人像图片数据，人脸照片的base64位编码字符串(需utf8编码的urlencode) 注：照片需要小于10M
        map.put("imageData", urlEncode(req.getImageData()));
        //唯一流水号，可以自行定义，不超过32位
        map.put("requestId",generateNo() );
        map.put("accCode",ACCCODE);
        map.put("accessKeyId", ACCESSKEYID);
        map.put("version", VERSION);
        long timestamp=System.currentTimeMillis();
        map.put("timestamp",timestamp+"" );
        return map;
    }

    /**
     *  初始化银行卡实名认证签名参数
     * @param req 银行卡实名认证请求参数模型
     * @return
     * @throws Exception
     */
    public static  HashMap<String, Object> initBankParams(BankReq req) throws Exception{
        //获取参数签名
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("serviceCode",req.getServiceCode());
        map.put("name", req.getName());
        map.put("idNumber", req.getIdNumber());
        map.put("merchantId",MERCHANT);
        map.put("bankCard", req.getBankCard());
        map.put("mobile", req.getMobile());
        map.put("requestId",generateNo() );
        map.put("accCode",ACCCODE);
        map.put("accessKeyId", ACCESSKEYID);
        map.put("version", VERSION);
        long timestamp=System.currentTimeMillis();
        map.put("timestamp",timestamp+"" );
        return map;
    }

    /**
     *  初始化三大运营商实名认证签名参数
     * @param req  运营商实名认证请求参数模型
     * @return
     * @throws Exception
     */
    public static  HashMap<String, Object> sendParams(OperatorReq req) throws Exception{
        //获取参数签名
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accCode",ACCCODE);
        map.put("name", req.getName());// 姓名
        map.put("mobile", req.getMobile());// 手机号
        map.put("idNumber",  req.getIdNumber());// 身份证号码
        map.put("rangeCode", req.getRangeCode());// 需要查询的消费区间，1到12个月的任一个月
        map.put("imType", req.getImType());// 用户imei核查 1 imei 2 imsi
        map.put("imValue", req.getImValue());// 用户imei核查 对应类型的值
        map.put("months", req.getMonths());// 默认上月停机次数，如果有值，如months=3，则查当前月前三个月内的停机总次数。months大于0，小于等于12。

        map.put("serviceCode", req.getServiceCode());
        map.put("merchantId", MERCHANT);
        map.put("date", req.getDate());


        map.put("accessKeyId", ACCESSKEYID);
        map.put("version", VERSION);
        map.put("currentPage", req.getCurrentPage());
        map.put("pageSize", req.getPageSize());
        map.put("sourcet", req.getSourcet());

        long timestamp=System.currentTimeMillis();
        map.put("timestamp",timestamp+"" );
        MapRemoveNullUtil.removeNullEntry(map);
        return map;
    }

    /**
     * 签名
     * @param map
     * @return
     * @throws Exception
     */
    public static String sign( Map<String, Object> map) throws Exception{
        String signature= SignatureUtil.sign("MD5", map, PRIVATEKEY, "MD5");
        System.out.println("签名值："+signature);
        return  signature;
    }

    private static  String urlEncode(String data){
        try {
            if(StringUtils.isEmpty(data)){return "";}
            return  java.net.URLEncoder.encode(data,"UTF-8").toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 流水号:17位毫秒日期+5位随机流水号
     * @return
     */
    private static synchronized String generateNo(){
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String temp = sf.format(new Date());
        int random=(int) (Math.random()*100000);
        return temp+ StringUtils.leftPad(random+ "", 5, "0");
    }

}
