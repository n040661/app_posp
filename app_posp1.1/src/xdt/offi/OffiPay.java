package xdt.offi;

import oracle.net.aso.a;
import oracle.net.aso.r;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import xdt.service.impl.BaseServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import xdt.dao.IAppOrderDetailDao;
import xdt.dao.IViewKyChannelInfoDao;
import xdt.model.AppOrderDetail;
import xdt.model.PmsAppTransInfo;
import xdt.model.ViewKyChannelInfo;
import xdt.util.*;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 欧飞支付
 * User: Jeff
 * Date: 15-5-12
 * Time: 上午11:25
 * To change this template use File | Settings | File Templates.
 */
@Component
public class OffiPay {

    private Logger logger=Logger.getLogger(OffiPay.class);

    @Resource(name = "viewKyChannelInfoDaoImpl")
    private IViewKyChannelInfoDao channelInfoDao; //通道信息层
    @Resource
    private IAppOrderDetailDao appOrderDetailDao; //详细信息层

    /**
     * 手机充值话费
     * @author Jeff
     * @param appTransInfo
     * @return  1：成功支付 2：正在支付 0：支付失败
     */
    public   Integer  mobilePay(PmsAppTransInfo appTransInfo){
        Integer result = 0;        
        //查询当前手机号是否可充值
        if(appTransInfo != null && StringUtils.isNotBlank(appTransInfo.getPrepaidphonenumber()) && checkPhone(appTransInfo.getPrepaidphonenumber())
                && StringUtils.isNumeric(appTransInfo.getAmount())){
            Integer amount = Integer.parseInt(appTransInfo.getAmount())/100;
            if(checkMobilePay(appTransInfo.getPrepaidphonenumber(),amount) == 1){
                 //调用充值接口
                try {
                    ViewKyChannelInfo  channelInfo = channelInfoDao.searchChannelInfo(BaseServiceImpl.PAYPHONEACCOUNTBUSINESSNUM);
                    if(channelInfo != null && StringUtils.isNotBlank(channelInfo.getUrl())){

                        //生成md5串   包体=userid+userpws+cardid+cardnum+sporder_id+sporder_time+ game_userid
                        String preMd5Str = channelInfo.getChannelNO()+ channelInfo.getChannelPwd() + BaseServiceImpl.OFFIPHONEPAYCARDID
                                + amount + appTransInfo.getOrderid() + UtilDate.transDate(appTransInfo.getTradetime()) + appTransInfo.getPrepaidphonenumber()
                                + BaseServiceImpl.OFFIKEYSTR;

                        String url = channelInfo.getUrl();
                        url += "?userid="+channelInfo.getChannelNO()+"&userpws="+ channelInfo.getChannelPwd()+
                                "&cardid="+BaseServiceImpl.OFFIPHONEPAYCARDID+"&cardnum="+amount+"&sporder_id="+
                                appTransInfo.getOrderid()+"&sporder_time="+ UtilDate.transDate(appTransInfo.getTradetime())+
                                "&game_userid="+appTransInfo.getPrepaidphonenumber()+"&ret_url="+channelInfo.getCallbackurl()+"&md5_str="+UtilMethod.getMd5Str(preMd5Str).toUpperCase()+"&version="+channelInfo.getVersion();

                        //调用接口
                        String httpresult = HttpURLConection.httpURLConectionGET(url, "gb2312");

                        if(StringUtils.isNotBlank(httpresult)){
                             //解析返回结果
                            Document doc = DocumentHelper.parseText(httpresult);
                            Map<String, Object> map = XMLUtil.Dom2Map(doc);
                            String gameState = "";
                            if(map.get("game_state") != null){
                                gameState = map.get("game_state").toString();
                            }else{
                                //交易异常，返回失败
                                result = 0;
                                return  result;
                            }

                            if(StringUtils.isNotBlank(gameState)){
                                if(gameState.equals("1")){  // 成功支付
                                   result = 1;
                                }else if(gameState.equals("0")){ //正在支付，后续需要反查交易结果
                                    result = 2;
                                }
                            }
                        }else{
                            logger.info("调用充值接口失败:"+ appTransInfo.getPrepaidphonenumber());
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("获取充值渠道失败:"+ appTransInfo.getPrepaidphonenumber() +" 详细："+e.getMessage());
                }
            }
        }
        //调用充值接口
        return  result;
    }

    /**
     * 判断当前手机号码是否可充值
     * @author Jeff
     * @param mobile
     * @param price 以元为单位
     * @return
     */
    private  Integer checkMobilePay(String mobile,Integer price){
        Integer result = 0;
        //查询渠道信息
        ViewKyChannelInfo channelInfo = null;
        if(StringUtils.isNotBlank(mobile) && price > 0){
            try {
                channelInfo = channelInfoDao.searchChannelInfo(BaseServiceImpl.PHONEPAYCHECK);
                if(channelInfo != null){
                    String url = channelInfo.getUrl();
                    if(StringUtils.isNotBlank(url)){
//                     http://api2.ofpay.com/telcheck.do?phoneno=13813856456&price=50&userid=Axxxxx
                        String requestStr = url+"?"+"phoneno="+mobile+"&price="+price+"userid="+ channelInfo.getChannelNO();
                        String httpresult = HttpURLConection.httpURLConectionGET(requestStr, "gb2312");
                        if (StringUtils.isNotBlank(httpresult)) {
                            //截取第一位，如果是"1"则成功
                            if(httpresult.substring(0,1).equals("1")){
                                result = 1;
                            }
                        }else{
                            logger.info("该手机号不可充值:"+ mobile);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("查询是否可充值异常:"+ mobile +" 详细："+e.getMessage());
            }
        }

        return result;
    }


    /**
     * 检测手机号的合法性
     * @author Jeff
     * @param mobilePhone
     * @return
     */
    private boolean checkPhone(String mobilePhone) {
        Pattern p = Pattern.compile("^((14[5,7])|(13[0-9])|(17[0-9])|(15[^4,\\D])|(18[0,1-9]))\\d{8}$");
        Matcher m = p.matcher(mobilePhone);
        return m.matches();
    }


    /**
     * 根据SP订单号查询充值状态
     * @author Jeff
     * @param spOrder
     * @return
     */
     public Integer queryOrderStatus(String spOrder){
         Integer result  = -1;
         if(StringUtils.isNotBlank(spOrder)){
             //查询渠道信息
             ViewKyChannelInfo channelInfo = null;
             try {

                 channelInfo = channelInfoDao.searchChannelInfo(BaseServiceImpl.OFFIQUERRYORDER);
                 if(channelInfo != null && StringUtils.isNotBlank(channelInfo.getUrl())){
                     String requestUrl = channelInfo.getUrl();
                     requestUrl += "?userid="+channelInfo.getChannelNO()+"&spbillid="+ spOrder;
                     String httpresult = HttpURLConection.httpURLConectionGET(requestUrl, "gb2312");
                     if(StringUtils.isNotBlank(httpresult) && StringUtils.isNumeric(httpresult)){
                         result = Integer.parseInt(httpresult);  //1 充值成功，0充值中，9充值失败，-1找不到此订单
                     }
                 }else{
                     logger.info("查询欧飞订单渠道异常");
                 }
             } catch (Exception e) {
                 e.printStackTrace();
                 logger.info("查询渠道信息异常");
             }
         }
         return result;
     }
     
     /**
      * 请求手机充值面值对应的金额
      * @param phoneNum
      * @param money
      * @return
      */
     public String phoneMoneyQuery(String phoneNum,String money){
          String inprice = "";
         ViewKyChannelInfo channelInfo = null;
         try {
             channelInfo = channelInfoDao.searchChannelInfo(BaseServiceImpl.PHONEPRODUCTQUERYBUSINESSNUM);
             if (channelInfo != null) {
                 //根据当前号码查询该商品产品id信息
                 String path = channelInfo.getUrl();
                 String result = null;
                 if (StringUtils.isNotBlank(path)) {
                     path += "?userid=" +channelInfo.getChannelNO()+ "&userpws=" + channelInfo.getChannelPwd() + "&phoneno=" + phoneNum + "&pervalue="+money+"&version="+channelInfo.getVersion();
                     result = HttpURLConection.httpURLConectionGET(path, "gb2312");
                 }
                 if (StringUtils.isNotBlank(result)) {
                     Document doc = DocumentHelper.parseText(result);
                     Map<String, Object> map = XMLUtil.Dom2Map(doc);
                     if(map.get("inprice") != null){
                         inprice = map.get("inprice").toString();
                     }
                 }else{
                     logger.info("查询欧飞手机充值支付金额渠道返回异常");
                 }
                 //根据商品小类查询产品列表
             } else {
                 logger.info("查询欧飞手机充值支付金额渠道异常");
             }
         } catch (Exception e) {
             e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
         }


         return inprice;
     }
     
     /**
      * 水煤电充值
      * @author lev12
      * @param appTransInfo
      * @return  1：成功支付 0：支付失败
      */
     public   Integer  utilityOrder(PmsAppTransInfo appTransInfo){
    	 logger.info("水煤电充值");
         Integer result = 0;
                  //调用充值接口
                 try {
                     ViewKyChannelInfo  channelInfo = channelInfoDao.searchChannelInfo(BaseServiceImpl.UTILITYORDER);
                     AppOrderDetail appOrderDetail = appOrderDetailDao.searchById(appTransInfo.getOrderid());
                     
                     String provId = appOrderDetail.getProvId();// 省份ID                    
                     String cityId = appOrderDetail.getCityId();// 城市ID                    
                     String type = appOrderDetail.getType();// 类型                        
                     String chargeCompanyCode = appOrderDetail.getChargeCompanyCode();// 缴费单位编码       
                     String cardId = appOrderDetail.getCardId();// 水电煤的商品编号                
                     String cardnum = appOrderDetail.getCardnum();// 充值数量 ，充值数量始终为1         
                     String account = appOrderDetail.getAccount();// 充值账户                   
                     String contractNo = appOrderDetail.getContractNo();// 合同号 （查到必传，查不到就不传）   
                     String payMentDay = appOrderDetail.getPayMentDay();// 账期 (通过queryBalance.do查询到就需要传，查不到就不需要传)
                     
                     String amountStr = appTransInfo.getPayamount();
                     BigDecimal amount = new BigDecimal(amountStr).divide(new BigDecimal(100));
                     
                     if(channelInfo != null && StringUtils.isNotBlank(channelInfo.getUrl())){
                    	 
                         //生成md5串   包体=userid+userpws+cardid+cardnum+sporderId+provId+cityId+type+chargeCompanyCode+account
                         String preMd5Str = channelInfo.getChannelNO()+ channelInfo.getChannelPwd() + cardId
                                 + cardnum + appTransInfo.getOrderid() + provId
                                 + cityId + type + chargeCompanyCode + account
                                 + BaseServiceImpl.OFFIKEYSTR;

                         String url = channelInfo.getUrl();
                         url += "?userid="+channelInfo.getChannelNO()+"&userpws="+ channelInfo.getChannelPwd()+
                                 "&provId="+provId+"&cityId="+cityId+"&type="+type
                                 +"&chargeCompanyCode="+chargeCompanyCode+"&cardId="+cardId
                                 +"&cardnum="+cardnum+"&account="+account+"&ret_url="+channelInfo.getCallbackurl()+"&sporderId="+
                                 appTransInfo.getOrderid()+"&actPrice="+ amount +"&md5_str="+UtilMethod.getMd5Str(preMd5Str).toUpperCase()+"&version="+channelInfo.getVersion();

                         if(contractNo!=null&&!"".equals(contractNo)){
                        	 url += "&contractNo="+contractNo;
                         }
                         if(payMentDay!=null&&!"".equals(payMentDay)){
                        	 url += "&payMentDay="+payMentDay;
                         }
                         
                         logger.info("水煤电充值调用：" + url);
                        
                         //调用接口
                         String httpresult = HttpURLConection.httpURLConectionGET(url, "gb2312");

                         if(StringUtils.isNotBlank(httpresult)){
                              //解析返回结果
                             Document doc = DocumentHelper.parseText(httpresult);
                             Map<String, Object> map = XMLUtil.Dom2Map(doc);
                             String err_msg = map.get("err_msg").toString();
                             String retCode = map.get("retcode").toString();
                             String orderId = map.get("orderid").toString();
                             String cardid = map.get("cardid").toString();
                             String cardNum = map.get("cardnum").toString();
                             String orderCash = map.get("ordercash").toString();
                             String cardName = map.get("cardname").toString();
                             String sporderId = map.get("sporderId").toString();
                             String accountRet = map.get("account").toString();
                             String status = map.get("status").toString();
                             
                             if(StringUtils.isNotBlank(status)){
                                 if(status.equals("1")){  // 成功支付
                                    result = 1;
                                 }else if(status.equals("0")){ //正在支付，后续需要反查交易结果
                                     result = 2;
                                 }
                             }
                         }else{
                             logger.info("调用充值接口失败:"+ appTransInfo.getPrepaidphonenumber());
                         }

                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                     logger.info("获取充值渠道失败:"+ appTransInfo.getPrepaidphonenumber() +" 详细："+e.getMessage());
                 }
         return  result;
     }
     
     /**
      * 加油卡充值
      * @author lev12
      * @param appTransInfo
      * @return  1：成功支付 0：支付失败
      */
     public   Integer  sinopecOrder(PmsAppTransInfo appTransInfo){
    	 logger.info("加油卡充值");
         Integer result = 0;
                  //调用充值接口
                 try {
                     ViewKyChannelInfo  channelInfo = channelInfoDao.searchChannelInfo(BaseServiceImpl.SINOPECORDER);
                     AppOrderDetail appOrderDetail = appOrderDetailDao.searchById(appTransInfo.getOrderid());
                     
                     BigDecimal id = appOrderDetail.getId();
                     String cardId = appOrderDetail.getCardId();//  商品编号                
                     String cardnumStr = appOrderDetail.getCardnum();// 充值数量         
                     String account = appOrderDetail.getAccount();// 充值账户         
                     String gasCardTel = appOrderDetail.getGasCardTel();
                     String gasCardName = appOrderDetail.getGasCardName();
                     
                     Integer cardnum = Integer.parseInt(cardnumStr)/100;
                     
                     if(channelInfo != null && StringUtils.isNotBlank(channelInfo.getUrl())){

                         //生成md5串   包体=userid+userpws+cardid+cardnum+sporder_id+sporder_time+ game_userid
                         String preMd5Str = channelInfo.getChannelNO()+ channelInfo.getChannelPwd() + cardId
                                 + cardnum + appTransInfo.getOrderid() + UtilDate.transDate(appTransInfo.getTradetime())+account
                                 + BaseServiceImpl.OFFIKEYSTR;

                         String url = channelInfo.getUrl();
                         url += "?userid="+channelInfo.getChannelNO()+"&userpws="+ channelInfo.getChannelPwd()+
                                 "&cardid="+cardId+"&cardnum="+cardnum
                                 +"&game_userid="+account+"&gasCardTel="+gasCardTel
                                 +"&gasCardName="+gasCardName
                                 +"&ret_url="+channelInfo.getCallbackurl()+"&sporder_id="+
                                 appTransInfo.getOrderid()+"&sporder_time="+UtilDate.transDate(appTransInfo.getTradetime())
                                 +"&md5_str="+UtilMethod.getMd5Str(preMd5Str).toUpperCase()+"&version="+channelInfo.getVersion();

                         logger.info("加油卡充值调用："+url);
                         //调用接口
                         String httpresult = HttpURLConection.httpURLConectionGET(url, "gb2312");

                         if(StringUtils.isNotBlank(httpresult)){
                              //解析返回结果
                             Document doc = DocumentHelper.parseText(httpresult);
                             Map<String, Object> map = XMLUtil.Dom2Map(doc);
                             String orderId = map.get("orderid").toString();
                             String cardid = map.get("cardid").toString();
                             String cardNum = map.get("cardnum").toString();
                             String orderCash = map.get("ordercash").toString();
                             String cardName = map.get("cardname").toString();
                             String sporderId = map.get("sporder_id").toString();
                             String gameUerid = map.get("game_userid").toString();
                             String status = map.get("game_state").toString();
                             
                             if(StringUtils.isNotBlank(status)){
                                 if(status.equals("1")){  // 成功支付
                                    result = 1;
                                 }else if(status.equals("0")){ //正在支付，后续需要反查交易结果
                                     result = 2;
                                 }
                             }
                         }else{
                             logger.info("调用充值接口失败:"+ appTransInfo.getPrepaidphonenumber());
                         }

                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                     logger.info("获取充值渠道失败:"+ appTransInfo.getPrepaidphonenumber() +" 详细："+e.getMessage());
                 }
         return  result;
     }
     
}
