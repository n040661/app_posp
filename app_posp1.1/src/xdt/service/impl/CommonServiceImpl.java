package xdt.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import xdt.common.RetAppMessage;
import xdt.common.WorkKeyFormat;
import xdt.dao.IPmsMerchantPosDao;
import xdt.dao.IPmsPosInfoDao;
import xdt.dao.IViewKyChannelInfoDao;
import xdt.dto.*;
import xdt.model.PmsMerchantPos;
import xdt.model.PmsPosInfo;
import xdt.model.SessionInfo;
import xdt.model.ViewKyChannelInfo;
import xdt.service.ICommonService;
import xdt.servlet.AppPospContext;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service("commonServiceImpl")
public class CommonServiceImpl extends BaseServiceImpl implements ICommonService{
	@Resource
	private IPmsMerchantPosDao pmsPos;
	@Resource
	private IViewKyChannelInfoDao channelInfoDao;//通道信息层
	@Resource
	private IPmsPosInfoDao pmsPosInfo;
	private Logger logger=Logger.getLogger(CommonServiceImpl.class);

	/**
	 * 刷卡支付签到状态修改
	 */
	public String creditCardPaymentStatus(String account,HttpSession session,HttpServletRequest request)throws Exception{
		setMethodSession(request.getRemoteAddr());
		logger.info("进入刷卡支付签到状态修改...");
		String message=INITIALIZEMESSAGE;
		PaymentSignResponseDTO psr = new PaymentSignResponseDTO();
		Object obj = parseJsonString(account, PaymentSignResponseRequestDTO.class);
		if(!obj.equals(DATAPARSINGMESSAGE) ){
			PaymentSignResponseRequestDTO  paDto=(PaymentSignResponseRequestDTO)obj;
			if("1".equals(paDto.getStatus())){
				SessionInfo sessio=(SessionInfo)session.getAttribute(SessionInfo.SESSIONINFO);//获取session
				if(sessio!=null){
					setSession(request.getRemoteAddr(),session.getId(),sessio.getMobilephone());
                    PmsMerchantPos merchantid = null;
                    PmsPosInfo pmsMerchantPos =  pmsPosInfo.selectBusinessPos(paDto.getSnno());

                    if(pmsMerchantPos != null){
                        Map<String,String> mapParam = new HashMap<String,String>();
                        mapParam.put("merchantid",sessio.getId());
                        mapParam.put("posId",pmsMerchantPos.getId().toString());
                        merchantid = pmsPos.selectMerchantidAndSn(mapParam);
                    }

					if (merchantid != null) {
						if (merchantid.getUsestatus() == 5) {
							merchantid.setUsestatus((short) 1);
							int update = pmsPos.update(merchantid);
							if (update == 1) {
								message = SUCCESSMESSAGE;// 成功
							} else {
								message = FAILMESSAGE;// 数据解析失败
							}
						} else if (merchantid.getUsestatus() == 1) {
							message = SUCCESSMESSAGE;// 成功
						} else {
							message = FAILMESSAGE;//
						}
					} else {
						message = ERRORMESSAGE;
					}
				} else {
					message = RetAppMessage.SESSIONINVALIDATION;// 会话失败，重新登陆
				}
			}else {
				message = SUCCESSMESSAGE;// 成功
			}
		}else {
			message = FAILMESSAGE;// 数据解析失败
		}
		String retCode = message.split(":")[0];
		String retMessage = message.split(":")[1];
		if(retMessage.equals("initialize")){
			retMessage = "服务器异常";
		}else if(retMessage.equals("sessionInvalidation")){//7
			retMessage = "会话失效，请重新登录";
		}else if(retMessage.equals("success")){
			retMessage = "修改状态成功";
		}else if(retMessage.equals("fail")){
			retMessage = "修改状态失败";
		}else if(retMessage.equals("error")){
			retMessage="数据库获取失败";
		}
		psr.setRetCode(Integer.parseInt(retCode));
		psr.setRetMessage(retMessage);
		logger.info("[app_rsp]"+createJson(psr));
		return createJsonString(psr);
	}


	/**
	 * 刷卡支付签到
	 */
	public String creditCardPaymentSignIn(String sn, HttpSession session,HttpServletRequest request)throws Exception {
		logger.info("进入刷卡支付签到...");
		CreditPaymentSignResponseDTO cre=new CreditPaymentSignResponseDTO();
		String message=INITIALIZEMESSAGE;
		SessionInfo sessio=(SessionInfo)session.getAttribute(SessionInfo.SESSIONINFO);//获取session
		CreditPaymentSignInResponseDTO parseJsonString=new CreditPaymentSignInResponseDTO();
		String fi=null;
		String workKey = "";
		String getSn="";
		String reservedPrivate ="";
        //是否需要下载主密钥。0-不需要，1-需要
        String isNeedZMK="";
        //主密钥校验值
        String zmChkVal = "";
        //工作密钥校验值
        String wkChkVal = "";
		PmsMerchantPos pos = null;
		String mes="";
		if(sessio!=null){//判断session是否失效


            String oAgentNo = sessio.getoAgentNo();
            if(StringUtils.isBlank(oAgentNo)){
                //如果欧单编号为空，直接返回错误
                cre.setRetCode(1);
                cre.setRetMessage("参数错误");
                return createJsonString(cre);
            }

			String mobilephone = sessio.getMobilephone();
			//查询可用接口 
			CreditPaymentSignRequestDTO snString =(CreditPaymentSignRequestDTO) parseJsonString(sn, CreditPaymentSignRequestDTO.class);
			
			//ViewKyChannelInfo info = channelInfoDao.searchChannelInfo(CREDITCARDPAYMENTSIGNINBUSINESSNUM);
            ViewKyChannelInfo channelInfo = AppPospContext.context.get(SELFCHANEL+CREDITCARDPAYMENTSIGNINBUSINESSNUM);
			String urlChanel = channelInfo.getUrl();
			
			if(snString!=null && !"".equals(snString) && urlChanel != null&&!"".equals(urlChanel)){
				setSession(request.getRemoteAddr(),session.getId(),mobilephone);
				logger.info("[client_req]"+ createJson(snString));
				getSn=snString.getSn();
				//通过sn号进行查询PMS_POS_INFO
				PmsPosInfo posInfo = pmsPosInfo.selectBusinessPos(getSn);
				if(posInfo!=null ){
                    Map<String,String> mapParam = new HashMap<String,String>();
                    mapParam.put("merchantid",sessio.getId());
                    mapParam.put("posId",posInfo.getId().toString());
                    pos = pmsPos.selectMerchantidAndSn(mapParam);

					if( pos != null){
                        if(!pos.getUsestatus().equals("7") && !pos.getUsestatus().equals("8")){
                              //设备没有锁定
                            //发送请求并解析
                            //拼接请求第三方接口需用的参数
                            CreditPaymentSignInRequestDTO creditPRqDTO=new CreditPaymentSignInRequestDTO();
                            creditPRqDTO.setDealType(CREDITCARDPAYMENTSIGNINBUSINESSNUM);
                            creditPRqDTO.setMerInfo(sessio.getMercId());
                            creditPRqDTO.setMerPos(pos.getPosbusinessno());
                            creditPRqDTO.setTerminalSN(getSn);
                            creditPRqDTO.setPhone(mobilephone);
                            creditPRqDTO.setoAgentNo(oAgentNo);
                            logger.info("[req_sign]"+createJson(creditPRqDTO));
                            String jsonString =createJsonString(creditPRqDTO);
                            //请求第三方账户支付接口 pms_merchant_pos  buinessPOSNum
                            URL url = new URL(urlChanel);
                            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                            String json="";
                            logger.info(" [req_url] "+ urlChanel);
                            String buffer="";
                            try {
                                buffer = this.dtoRequestConnectionDto(connection,json,jsonString);
                                logger.info("签到返回："+buffer);
                            } catch (Exception e) {
                                insertAppLogs(mobilephone, "", "1300");//1300 三方前置报错
                                e.printStackTrace();
                                throw e;
                            }
                            logger.info("[req_return]returning data");
                            if(isNotEmptyValidate(buffer)){
                                PmsMerchantPos pos2= pmsPos.selectMerchantidAndSn(mapParam);//通过PMS_POS_INFO 的id查询PMS_MERCHANT_POS表的信息
                                //解析账户支付返回的信息
                                parseJsonString =(CreditPaymentSignInResponseDTO) parseJsonString(buffer,CreditPaymentSignInResponseDTO.class);
                                logger.info(" [req_result] "+createJson(parseJsonString));
                                if(parseJsonString!=null){
                                    if("0000".equals(parseJsonString.getErrCode()) && pos2!=null){
                                        message = SUCCESSMESSAGE;//返回成功
                                        //如果返回值是0000 成功
                                        String batno = parseJsonString.getReseved60().substring(3,9);//解析出60域
                                        //分库-->修改PmsMerchantPos表的签到状态
                                        reservedPrivate = parseJsonString.getReservedPrivate();
                                        zmChkVal = parseJsonString.getZmChkVal();
                                        wkChkVal = parseJsonString.getWkChkVal();
                                        isNeedZMK =parseJsonString.getNeedZMK();
                                        pos2.setId(pos.getId());
                                        pos2.setBatno(batno);//插入批次号
                                        pos2.setUsestatus((short)5);
                                        pos2.setReservedprivate(reservedPrivate);
                                        fi=parseJsonString.getZhukek();
                                        workKey = WorkKeyFormat.getWorkKey(fi,reservedPrivate);
                                        pmsPos.update(pos2);
                                        session.removeAttribute("sql");
                                    }else {
                                        message = "2:signFail";//签到失败，请重新签到
                                        insertAppLogs(mobilephone, "", "1103"); //1103  解析返回的数据错误
                                    }
                                }else {
                                    message = DATAPARSINGMESSAGE;//数据解析失败
                                    insertAppLogs(mobilephone, "", "1103"); //1103  解析返回的数据错误
                                }



                            }else {
                                message = DATAPARSINGMESSAGE;//返回错误
                                insertAppLogs(mobilephone, "", "1102");//1102  返回数据错误
                            }
                        }else{
                            message = "3:posLocked";//设备锁定
                            insertAppLogs(mobilephone, "", "1102");//1102  返回数据错误
                        }
                    }else{
 									message = "1:noBanding";//未绑定设备
									insertAppLogs(mobilephone, "", "1100");//1100  从手机端传过来的数据解析错误
								}}else{
									message = DATAPARSINGMESSAGE;//数据解析错误
									insertAppLogs(mobilephone, "", "1101");//1101 没有对应sn号
								}}else{
									message = DATAPARSINGMESSAGE;//数据解析错误
									insertAppLogs(mobilephone, "", "1100");//1100  从手机端传过来的数据解析错误
								}}else{
									message = RetAppMessage.SESSIONINVALIDATION;//会话失败，重新登陆
								}//解析要返回的信息  
		String retCode = message.split(":")[0];
		String retMessage = message.split(":")[1];
		if(retMessage.equals("initialize")){
			retMessage = "服务器异常";
		}else if(retMessage.equals("sessionInvalidation")){//7
			retMessage = "会话失效，请重新登录";
		}else if(retMessage.equals("dataParsing")){//6
			retMessage = "数据解析错误";
		}else if(retMessage.equals("invalid")){
			retMessage = "签到失败，请联系客服";
		}else if(retMessage.equals("success")){
			retMessage = "签到成功";
		}else if(retMessage.equals("signFail")){
			retMessage = "签到失败，请重新签到";
		}else if(retMessage.equals("noBanding")){
			retMessage = "未绑定设备，请和代理商确认设备";
		}else if(retMessage.equals("posLocked")){
			retMessage = "设备已锁定，请联系代理商";
		}
		cre.setRetCode(Integer.parseInt(retCode));
		cre.setRetMessage(retMessage);
		//签到成功，返回秘钥。签到失败返回为空
		if( reservedPrivate !=null){
			cre.setTheSecretKey(fi);
			cre.setReservedPrivate(reservedPrivate);
			cre.setWorkKeyFormat(workKey);
            cre.setZmChkVal(zmChkVal);
            cre.setWkChkVal(wkChkVal);
            cre.setNeedZMK(isNeedZMK);
		}
		logger.info("[app_rsp]"+createJson(cre));
		return createJsonString(cre);
	}
} 