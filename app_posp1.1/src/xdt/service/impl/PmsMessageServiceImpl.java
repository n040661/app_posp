package xdt.service.impl;

import com.bcloud.msg.http.HttpSender;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import xdt.common.RetAppMessage;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPmsMessageDao;
import xdt.dto.CaptchaValidationRequestDTO;
import xdt.dto.CaptchaValidationResponseDTO;
import xdt.model.*;
import xdt.service.IPmsMessageService;
import xdt.util.HttpURLConection;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jeff
 * @descrip 短信通知service
 */
@Service("pmsMessageService")
public class PmsMessageServiceImpl extends BaseServiceImpl implements IPmsMessageService {
    @Resource
    private IPmsMessageDao messageDao;//消息服务层
    @Resource
    private IPmsMerchantInfoDao pmsMerchantInfoDao;//商户信息层
    private Logger logger = Logger.getLogger(PmsMessageServiceImpl.class);

    /**
     * 与短信通道对接 并判断是否成功发送短信验证码
     */
    public String getMessageAuthenticationCode(String mobilePhone, Integer type, String oAgentNo,QuickPayMessage quickPayMessage) throws Exception {
        String retMessage = FAILMESSAGE;

        if (StringUtils.isBlank(oAgentNo)) {
            //判断是否传入欧单编号，没有的话直接返回错误
            return retMessage;
        }
        BigDecimal failure = new BigDecimal(0);
        String searchId = "";
        String responseStr = "";
        String response = "";
        String interfaceId = "1";
        String reqtime = sdf.format(new Date());
        String requestNumber = UUID.randomUUID().toString();
        String randomNumber = generateRandomNumber() + "";//获取6位随机数
        //发送请求前先将要获取验证码的手机号存入库中
        PmsMessage pmsMessage = new PmsMessage();
        if(quickPayMessage!= null){
            randomNumber = quickPayMessage.getMessage();
            pmsMessage.setOrderId(quickPayMessage.getOrderId());
        }
        pmsMessage.setContext(randomNumber);
        pmsMessage.setPhoneNumber(mobilePhone);
        pmsMessage.setInterfaceId(new BigDecimal(interfaceId));
        pmsMessage.setReqtime(reqtime);
        pmsMessage.setMsgType(type);
        pmsMessage.setRequestNumber(requestNumber);
        pmsMessage.setFailure(new BigDecimal(0));
        pmsMessage.setoAgentNo(oAgentNo);

        sessionDefaultConfig();
        int result = smsChannelInfoSave(pmsMessage);
        logger.info("插入短信，返回："+result);
        if (result == 1) {
            //打开连接
            HttpURLConection connection = new HttpURLConection();

            String msgContent = "";
            switch (type) {
                case 0:
                    //注册
                    msgContent = MESSAGEAUTHENTICATIONTEMP;
                    break;
                case 1:
                    //找回没密码
                    msgContent = MESSAGEFINDPASSTEMP;
                    break;
                case 2:
                    //修改密码
                    msgContent = MESSAGEUPDATEPASSTEMP;
                    break;
                case 3:
                    //添加收银员
                    msgContent = MESSAGEADDUSERTEMP;
                    break;
                case 4:
                    //修改收银员
                    msgContent = MESSAGEUPDATEUSERTEMP;
                    break;
                case 5:
                    //提现
                    msgContent = MESSAGEDROWMONEY;
                    break;
                case 6:
                    //快捷支付 预支付
                    msgContent = MESSAGEQUICKPREPAYTEMP;
                    break;
                case 7:
                    //其他
                    msgContent = MESSAGEOTHERTEMP;
                    break;
                default:
                    msgContent = MESSAGEOTHERTEMP;
                    break;
            }

            if ("100365".equals(oAgentNo)) {
                logger.info("是刷刷付的欧单，使用刷刷付接口调用");
                //是刷刷付的欧单，使用刷刷付接口调用
                String uri = "http://183.232.132.142/msg/";//应用地址
                String account = "SSF-xsf730011";//账号
                String pswd = "KfXhg107YB";//密码
                String mobiles = mobilePhone;//手机号码，多个号码使用","分割
                String content = msgContent.replaceFirst("【付呗】","").replaceFirst("xxxx", randomNumber);//短信内容
                boolean needstatus = true;//是否需要状态报告，需要true，不需要false
                String product = null;//只有一个产品，不传产品编号
                try {
                    String returnString = "";

                    if (DEBUGGER.equals("1")) {
                        //debug模式
                        returnString = "20151016100254,0\n" +
                                "1001016100254879100\n";
                    } else {
                        logger.info("开始调用发送短信："+returnString);
                        returnString = HttpSender.send(uri, account, pswd, mobiles, content, needstatus, product);
                        logger.info("收到发送的返回串："+returnString);
                    }

                    if(StringUtils.isNotBlank(returnString) && returnString.contains(",")){
                          String [] res = returnString.split(",");
                          if(res.length == 2){
                              /**
                               * "20151016100254,0\n" +
                               "1001016100254879100\n";
                               */
                              String[] res2 =  res[1].split("\n");
                              if(res2.length == 2){
                                  if(res2[0].equals("0")){
                                      //发送成功
                                      response ="0";
                                      searchId =res2[1];
                                      failure = new BigDecimal(1);
                                  }else{
                                      response=res[0];
                                      //发送异常
                                      logger.info("短信接口返回错误，手机号："+mobilePhone+" 欧单编号："+oAgentNo+",调用短信接口返回的信息："+returnString);
                                  }
                              }else{
                                  logger.info("短信接口返回错误，手机号：\"+mobilePhone+\" 欧单编号："+oAgentNo+",调用短信接口返回的信息："+returnString);
                              }
                          }else{
                              logger.info("短信接口返回错误，手机号：\"+mobilePhone+\" 欧单编号："+oAgentNo+",调用短信接口返回的信息："+returnString);
                          }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return retMessage;
                }


            }
//            else if("100642".equals(oAgentNo)){
//
//                logger.info("是粤银的欧单，使用粤银短信接口调用");
//                String content = msgContent.replaceFirst("【付呗】","").replaceFirst("xxxx", randomNumber);//短信内容
//                String resyy = yueYinSendSms(mobilePhone,content);
//                if(StringUtils.isNotBlank(resyy)){
//                    response =  resyy;
//                    if(resyy.equals("0")){
//                       failure = new BigDecimal(1);
//                    }
//                }else{
//                    logger.info("短信接口返回错误，手机号：\"+mobilePhone+\" 欧单编号："+oAgentNo+",调用短信接口返回的信息为空");
//                }
//            } else  if("100844".equals(oAgentNo)){
//                String content = MESSAGEAUTHENTICATIONPARAMS + "&mobile=" + mobilePhone + "&content=" + URLEncoder.encode(msgContent.replaceFirst("xxxx", randomNumber), "utf-8");
//                if (DEBUGGER.equals("1")) {
//                    //debug模式
//                    responseStr = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
//                            "<returnsms>" +
//                            "<returnstatus>Success</returnstatus>" +
//                            "<message>成功</message>" +
//                            "<taskID>49937952</taskID>" +
//                            "</returnsms>";
//                } else {
//                    //正常模式
//                    responseStr = connection.httpURLConnectionPOST(MESSAGEAUTHENTICATIONURL, content);
//                }
//                if (null != responseStr && !"".equals(responseStr)) {
//                    //解析返回的参数
//                    Document doc = DocumentHelper.parseText(responseStr.toString());
//                    Map<String, Object> map = XMLUtil.Dom2Map(doc);
//
//                    if (map != null && map.size() > 0) {
//                        if (map.get("returnstatus") != null) {
//                            response = map.get("returnstatus").toString();
//                            if (response.equalsIgnoreCase("Success")) {
//                                response = "0";
//                            }
//                        }
//                        if (map.get("taskID") != null) {
//                            searchId = map.get("taskID").toString();
//                        }
//                    }
//
//                    if (StringUtils.isNotBlank(response)) {
//                        failure = response.equals("0") ? new BigDecimal(1) : new BigDecimal(0);
//                    }
//
//                }
//            }
            else{

                //载入模板配置文件
                List<OagentMsgCfg> oagentMsgCfgList = this.oagentMsgCfgList;
                if (oagentMsgCfgList != null && oagentMsgCfgList.size() > 0) {
                    for (OagentMsgCfg oagentMsgCfg : oagentMsgCfgList) {
                        if (oagentMsgCfg.getoAgentNo().equals(oAgentNo)) {


                            //发送内容
                            String content = msgContent.replaceFirst("【付呗】","").replaceFirst("xxxx", randomNumber);//短信内容
                            String sign = oagentMsgCfg.getDescribe();//签名

                            // 创建StringBuffer对象用来操作字符串
                            StringBuffer sb = new StringBuffer("http://web.cr6868.com/asmx/smsservice.aspx?");

                            // 向StringBuffer追加用户名
                            sb.append("name=" + oagentMsgCfg.getAccount());

                            // 向StringBuffer追加密码（登陆网页版，在管理中心--基本资料--接口密码，是28位的）
                            sb.append("&pwd=" + oagentMsgCfg.getPswd());

                            // 向StringBuffer追加手机号码
                            sb.append("&mobile=" + mobilePhone);

                            // 向StringBuffer追加消息内容转URL标准码
                            sb.append("&content=" + URLEncoder.encode(content, "UTF-8"));

                            //追加发送时间，可为空，为空为及时发送
                            sb.append("&stime=");

                            //加签名
                            sb.append("&sign=" + URLEncoder.encode(sign, "UTF-8"));

                            //type为固定值pt  extno为扩展码，必须为数字 可为空
                            sb.append("&type=pt&extno=");
                            // 创建url对象
                            //String temp = new String(sb.toString().getBytes("GBK"),"UTF-8");
                            logger.info("68883短信接口上送参数sb:" + sb.toString());
                            URL url68883 = new URL(sb.toString());

                            // 打开url连接
                            HttpURLConnection connection68883 = (HttpURLConnection) url68883.openConnection();

                            // 设置url请求方式 ‘get’ 或者 ‘post’
                            connection68883.setRequestMethod("POST");

                            // 发送
                            InputStream is = url68883.openStream();

                            //转换返回值
                            String returnStr = convertStreamToString(is);

                            // 返回结果为‘0，20140009090990,1，提交成功’ 发送成功   具体见说明文档
                            logger.info("68883短信接口返回：" + returnStr + "  ；----欧单：" + oagentMsgCfg.getDescribe());
                            // 返回发送结果
                            //格式：0,2015102311430188476068398,0,1,0,提交成功
                            //第一位为0成功   其余失败
                            String[] result68883 = returnStr.split(",");

                            if (result68883[0].equals("0")) {
                                //发送成功
                                response = "0";
                                searchId = result68883[1];
                                failure = new BigDecimal(1);
                            } else {
                                response = result68883[0];
                                //发送异常
                                logger.info("68883短信接口返回，手机号：" + mobilePhone + " 欧单编号：" + oAgentNo + ",调用短信接口返回的信息：" + returnStr);
                            }

                            break;
                        }
                    }
                }

            }
            //将请求信息与响应信息封装成PmsMessage
            PmsMessage message = new PmsMessage();
            message.setResponse(response);
            message.setRequestNumber(requestNumber);
            if(StringUtils.isNotBlank(searchId)){
                message.setSearchId(new BigDecimal(searchId));
            }
            message.setoAgentNo(oAgentNo);
            message.setFailure(failure);
            //更新信息
            int num = smsChannelInfoUpdate(message);
            if (num == 1 && response.equals("0")) { //提交成功
                retMessage = SUCCESSMESSAGE;
            }
        }
        return retMessage;
    }


    public String yueYinSendSms(String mobile,String content) {
        String res = "";
        HttpURLConection connection = new HttpURLConection();
        try {
            String commString ="Sn="+"yueyin"+"&Pwd="+"yueyin53840"+"&mobile=" + mobile + "&content="+content;
            res = connection.connectURL(commString, "http://124.173.70.59:8081/SmsAndMms/mt");
            logger.info("调用粤银短信接口返回："+res);
        } catch (Exception e) {
            return "-10000";
        }
        //设置返回值  解析返回值
        String resultok = "";
//			//正则表达式
        Pattern pattern = Pattern.compile("<int xmlns=\"http://tempuri.org/\">(.*)</int>");
        Matcher matcher = pattern.matcher(res);
        while (matcher.find()) {
            resultok = matcher.group(1);
        }
        return resultok;
    }

    /**
     * 短信验证码验证
     */
    public String captchaValidation(String captchaValidationInfo, HttpSession session, HttpServletRequest request) throws Exception {
        setMethodSession(request.getRemoteAddr());
        String message = INITIALIZEMESSAGE;
        String oAgentNo = "";
        Object obj = parseJsonString(captchaValidationInfo, CaptchaValidationRequestDTO.class);
        if (!obj.equals(DATAPARSINGMESSAGE)) {
            CaptchaValidationRequestDTO captchainfo = (CaptchaValidationRequestDTO) obj;
            String mobilePhone = captchainfo.getMobilePhone();
            setSession(request.getRemoteAddr(), session.getId(), mobilePhone);
            int mark = Integer.parseInt(captchainfo.getMark().toString());
            if (!isNotEmptyValidate(mobilePhone) || !isNotEmptyValidate(mark + "")) {
                insertAppLogs(mobilePhone, "", "2002");
                message = EMPTYMESSAGE;
            } else {
                if (!checkPhone(mobilePhone)) {
                    insertAppLogs(mobilePhone, "", "2003");
                    message = ERRORMESSAGE;
                } else {
                    if (mark == 0 || mark == 1 || mark == 2) { //注册 找回密码 修改密码的短信验证

                        //没有登录的情况下，需要前台传递欧单编号做处理
                        oAgentNo = captchainfo.getoAgentNo();
                        if (StringUtils.isBlank(oAgentNo)) {
                            //如果欧单编号为空，则设置为默认付呗欧单编号
                            oAgentNo = "100844";
                        }

                        PmsMerchantInfo pmsMerchantInfo = new PmsMerchantInfo();
                        pmsMerchantInfo.setMobilephone(mobilePhone);
                        pmsMerchantInfo.setCustomertype("3");
                        pmsMerchantInfo.setoAgentNo(oAgentNo);
                        List<PmsMerchantInfo> phoneList = pmsMerchantInfoDao.searchList(pmsMerchantInfo);
                        if (mark == 0) { //注册  手机号不存在才可获取验证码
                            if (null != phoneList && phoneList.size() >= 1) {
                                insertAppLogs(mobilePhone, "", "2097");
                                message = EXISTMESSAGE;
                            } else {
                                message = getMessageAuthenticationCode(mobilePhone, mark, oAgentNo,null);
                            }
                        }
                        if (mark == 1) { //找回密码  手机号存在才可获取验证码
                            if (null != phoneList && phoneList.size() >= 1) {
                                message = getMessageAuthenticationCode(mobilePhone, mark, oAgentNo,null);
                            } else {
                                insertAppLogs(mobilePhone, "", "2006");
                                message = INVALIDMESSAGE;
                            }
                        }
                        if (mark == 2) { //修改密码  手机号存在才可获取验证码
                            if (null != phoneList && phoneList.size() >= 1) {
                                message = getMessageAuthenticationCode(mobilePhone, mark, oAgentNo,null);
                            } else {
                                insertAppLogs(mobilePhone, "", "2006");
                                message = INVALIDMESSAGE;
                            }
                        }
                    } else {
                        if (mark != 0) {
                            //需判断发送短信手机号与session中的手机号是否一致
                            SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
                            if (null != sessionInfo) {

                                oAgentNo = sessionInfo.getoAgentNo();

                                if (StringUtils.isBlank(oAgentNo)) {
                                    //如果已经登录，则需要从session中获取欧单编号
                                    //如果session中没有欧单编号，则返回错误
                                    CaptchaValidationResponseDTO responseData = new CaptchaValidationResponseDTO();
                                    responseData.setRetCode(8);
                                    responseData.setRetMessage("商户信息不存在");
                                    String jsonString = "";
                                    jsonString = createJsonString(responseData);
                                    return jsonString;
                                }

                                if (sessionInfo.getMobilephone().equals(mobilePhone)) {
                                    message = getMessageAuthenticationCode(mobilePhone, mark, oAgentNo,null);
                                } else {
                                    message = "7:valideMerchantPhone";
                                }
                            } else {
                                message = RetAppMessage.SESSIONINVALIDATION;
                            }
                        }
                    }
                }
            }
        } else {
            insertAppLogs("", "", "2001");
            message = DATAPARSINGMESSAGE;
        }
        //解析要返回的信息
        int retCode = Integer.parseInt(message.split(":")[0]);
        String retMessage = message.split(":")[1];
        if (retMessage.equals("initialize")) {
            retMessage = "系统初始化";
        } else if (retMessage.equals("dataParsing")) {
            retMessage = "数据解析错误";
        } else if (retMessage.equals("empty")) {
            retMessage = "验证信息不能为空";
        } else if (retMessage.equals("error")) {
            retMessage = "请输入合法的手机号";
        } else if (retMessage.equals("exist")) {
            retMessage = "手机号已注册";
        } else if (retMessage.equals("success")) {
            retMessage = "验证码获取成功";
        } else if (retMessage.equals("fail")) {
            retMessage = "验证码获取失败";
        } else if (retMessage.equals("invalid")) {
            retMessage = "手机号未注册";
        } else if (retMessage.equals("sessionInvalidation")) {
            retMessage = "会话失效，请重新登录";
        } else if (retMessage.equals("sessionInvalidationo")) {
            retMessage = "请重新登录";
        } else if (retMessage.equals("valideMerchantPhone")) {
            retMessage = "请使用本商户手机号码接收短信";
        }
        //向客户端返回判断信息
        CaptchaValidationResponseDTO responseData = new CaptchaValidationResponseDTO();
        responseData.setRetCode(retCode);
        responseData.setRetMessage(retMessage);
        String jsonString = "";
        jsonString = createJsonString(responseData);
        return jsonString;
    }

    /**
     * 短信验证码验证异常
     */
    @Override
    public String captchaValidationException(String captchaValidationInfo) throws Exception {
        CaptchaValidationResponseDTO responseData = new CaptchaValidationResponseDTO();
        responseData.setRetCode(100);
        responseData.setRetMessage("系统异常");
        logger.info("[app_rsp]获取短信验证码" + createJson(responseData));
        Object obj = parseJsonString(captchaValidationInfo, CaptchaValidationRequestDTO.class);
        if (!obj.equals(DATAPARSINGMESSAGE)) {
            CaptchaValidationRequestDTO captchainfo = (CaptchaValidationRequestDTO) obj;
            String mobilePhone = captchainfo.getMobilePhone();
            insertAppLogs(mobilePhone, "", "2098");
        } else {
            insertAppLogs("", "", "2001");
        }
        return createJsonString(responseData);
    }

    /**
     * 新增短信通道信息
     */
    public int smsChannelInfoSave(PmsMessage pmsMessage) throws Exception {
        return messageDao.insert(pmsMessage);
    }

    /**
     * 更新短信通道信息
     */
    public int smsChannelInfoUpdate(PmsMessage pmsMessage) throws Exception {
        return messageDao.smsChannelInfoUpdateByPmsMessage(pmsMessage);
    }

    /**
     * 短信验证列表
     */
    public List<PmsMessage> selectList(String phoneNum) throws Exception {

        PmsMessage pms = new PmsMessage();
        pms.setPhoneNumber(phoneNum);
        List<PmsMessage> list = messageDao.selectLists20(pms);
        for (PmsMessage pmsMessage : list) {
            long currentTime = new Date().getTime();
            //2.获取数据库请求的毫秒数
            long requestTime = sdf.parse(pmsMessage.getReqtime()).getTime();
            //3.判断间隔时间是否超过2分钟
            long subTime = currentTime - requestTime;
            if (subTime > 120000) { //超过两分钟则视为无效
                pmsMessage.setFailure(new BigDecimal(0));
            }
        }
        return list;
    }

    /**
     * 转换返回值类型为UTF-8格式.  68883短信发送用
     *
     * @param is
     * @return
     */
    public static String convertStreamToString(InputStream is) {
        StringBuilder sb1 = new StringBuilder();
        byte[] bytes = new byte[4096];
        int size = 0;

        try {
            while ((size = is.read(bytes)) > 0) {
                String str = new String(bytes, 0, size, "UTF-8");
                sb1.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb1.toString();
    }



}