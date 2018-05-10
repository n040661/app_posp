package xdt.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import xdt.dao.IChannelSuportBankDao;
import xdt.dao.IPayCmmtufitDao;
import xdt.dao.IPmsAppAmountAndRateConfigDao;
import xdt.dao.IQuickpayRecordDao;
import xdt.dto.BankSupportListRequestDTO;
import xdt.dto.BankSupportListResponseDTO;
import xdt.dto.ChannelCardSupportRequestDTO;
import xdt.dto.ChannelCardSupportResponseDTO;
import xdt.model.*;
import xdt.service.IChannelSupportService;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通道支持的业务  服务层
 * User: Jeff
 * Date: 16-3-9
 * Time: 下午2:43
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ChannelSupportServiceImpl extends BaseServiceImpl  implements IChannelSupportService {

    private Logger logger = Logger.getLogger(ChannelSupportServiceImpl.class);
    @Resource
    IChannelSuportBankDao channelSuportBankDao;
    @Resource
    IPayCmmtufitDao iPayCmmtufitDao;
    @Resource
    IQuickpayRecordDao iQuickpayRecordDao;
    @Resource
    IPmsAppAmountAndRateConfigDao iPmsAppAmountAndRateConfigDao;
    /**
     *查询通道支持的银行卡
     * @param session
     * @param request
     * @return
     */
    @Override
    public String channelSupportBank(HttpSession session, String request) {
        String jsonString = "";
        SessionInfo sessionInfo = (SessionInfo)session.getAttribute(SessionInfo.SESSIONINFO);
        BankSupportListResponseDTO bankSupportListResponseDTO = new BankSupportListResponseDTO();
        BankSupportListRequestDTO requestDTO = null;
        //判断登录信息
        if(sessionInfo != null){
            //判断当前回话中是否存在欧单编号，不存在直接返回错误
            String oAgentNo = sessionInfo.getoAgentNo();
            if(StringUtils.isBlank(oAgentNo)){
                //未登录
                bankSupportListResponseDTO.setRetCode(13);
                bankSupportListResponseDTO.setRetMessage("会话过期，请重新登陆");
                try {
                    jsonString = createJsonString(bankSupportListResponseDTO);
                } catch (Exception em) {
                    em.printStackTrace();
                }
                return jsonString;
            }
            //判断请求体
            if(StringUtils.isNotBlank(request)){
                //解析请求对象
                try {
                    requestDTO = (BankSupportListRequestDTO)parseJsonString(request,BankSupportListRequestDTO.class);
                    //查询记录
                    ChannelSupportBank channelSupportBank = new ChannelSupportBank();
                    if(requestDTO.getChannelNum().equals("1")){
                        channelSupportBank.setChannelNum(MOBAOCHANNELNUM);
                    }

                    List<String> reqCardTypeList = null;
                    if(StringUtils.isNotBlank(requestDTO.getCardType())){
                          String [] cardTypes = requestDTO.getCardType().split(",");
                        reqCardTypeList = Arrays.asList(cardTypes);
                    }
                    channelSupportBank.setCardTypeList(reqCardTypeList);
                    List<ChannelSupportBank> channelSupportBankList = channelSuportBankDao.getBankByEntry(channelSupportBank);
                    //组装返回对象
                    bankSupportListResponseDTO.setRetCode(0);
                    bankSupportListResponseDTO.setRetMessage("查询成功");
                    bankSupportListResponseDTO.setSupportBankList(channelSupportBankList);
                    //参数出错
                    try {
                        jsonString = createJsonString(bankSupportListResponseDTO);
                    } catch (Exception em) {
                        em.printStackTrace();
                    }
                    return jsonString;
                } catch (Exception e) {
                    bankSupportListResponseDTO.setRetCode(1);
                    bankSupportListResponseDTO.setRetMessage("参数出错");
                    logger.info("参数出错");
                    //参数出错
                    try {
                        jsonString = createJsonString(requestDTO);
                    } catch (Exception em) {
                        em.printStackTrace();
                    }
                    return jsonString;
                }

            }else{
                //参数为空
                bankSupportListResponseDTO.setRetCode(1);
                bankSupportListResponseDTO.setRetMessage("参数不正确");
                try {
                    jsonString = createJsonString(bankSupportListResponseDTO);
                } catch (Exception em) {
                    em.printStackTrace();
                }
                return jsonString;
            }

        }else{
            bankSupportListResponseDTO.setRetCode(13);
            bankSupportListResponseDTO.setRetMessage("会话过期，请重新登陆");
            try {
                jsonString = createJsonString(bankSupportListResponseDTO);
            } catch (Exception em) {
                em.printStackTrace();
            }
            return jsonString;
        }
    }

    /**
     * 根据卡号和通道信息检索银行信息，并返回是否支持
     * @param session
     * @param request
     * @return
     */
    @Override
    public String checkSupportBankInfoByChannelCard(HttpSession session, String request) {
        String jsonString = "";
        SessionInfo sessionInfo = (SessionInfo)session.getAttribute(SessionInfo.SESSIONINFO);
        ChannelCardSupportResponseDTO channelCardSupportResponseDTO = new ChannelCardSupportResponseDTO();
        ChannelCardSupportRequestDTO requestDTO = null;
        //判断登录信息
        if(sessionInfo != null){
            //判断当前回话中是否存在欧单编号，不存在直接返回错误
            String oAgentNo = sessionInfo.getoAgentNo();
            if(StringUtils.isBlank(oAgentNo)){
                //未登录
                channelCardSupportResponseDTO.setRetCode(13);
                channelCardSupportResponseDTO.setRetMessage("会话过期，请重新登陆");
                try {
                    jsonString = createJsonString(channelCardSupportResponseDTO);
                } catch (Exception em) {
                    em.printStackTrace();
                }
                return jsonString;
            }
            //判断请求体
            if(StringUtils.isNotBlank(request)){
                //解析请求对象
                try {
                    requestDTO = (ChannelCardSupportRequestDTO)parseJsonString(request,ChannelCardSupportRequestDTO.class);
                    //校验出入参数信息
                    if(StringUtils.isBlank(requestDTO.getCardNo()) || StringUtils.isBlank(requestDTO.getChannelNum())){
                        channelCardSupportResponseDTO.setRetCode(1);
                        channelCardSupportResponseDTO.setRetMessage("传入参数出错");
                        logger.info("传入参数出错，"+requestDTO.toString());
                        //参数出错
                        try {
                            jsonString = createJsonString(channelCardSupportResponseDTO);
                        } catch (Exception em) {
                            em.printStackTrace();
                        }
                        return jsonString;
                    }
                    //按照卡号查询当前的银行编码
                    PayCmmtufit payCmmtufit =  iPayCmmtufitDao.selectByCardNum(requestDTO.getCardNo());
                    if(payCmmtufit != null){

                        //根据银行编码和通道信息查询银行信息
                        ChannelSupportBank channelSupportBank = new ChannelSupportBank();
                        if(requestDTO.getChannelNum().equals("1")){
                           //摩宝支付
                            channelSupportBank.setChannelNum(MOBAOCHANNELNUM);
                            Map<String,String> paramMap = new HashMap<String,String>();
                            paramMap.put("mercid",sessionInfo.getMercId());
                            paramMap.put("businesscode", TradeTypeEnum.shop.getTypeCode());
                            paramMap.put("paymentcode", PaymentCodeEnum.moBaoQuickPay.getTypeCode());
                            AppRateTypeAndAmount appRateTypeAndAmount = iPmsAppAmountAndRateConfigDao.queryAmountAndStatus(paramMap);
                            if(appRateTypeAndAmount != null){
                                int transAmountMin = Integer.parseInt(appRateTypeAndAmount.getMinAmount());
                                int transAmountMax = Integer.parseInt(appRateTypeAndAmount.getMaxAmount());
                                channelCardSupportResponseDTO.setTransAmountLimitMsg("单笔限额"+transAmountMin/100+"-"+transAmountMax/100+"元，具体限额以银行卡限制为准。");
                            }
                        }
                        channelSupportBank.setBnkCode(payCmmtufit.getBnkCode());
                        //卡类型做转换
                        if(payCmmtufit.getCrdFlg().equals("00")){
                            channelSupportBank.setCardType("01");
                        }else{
                            channelSupportBank.setCardType("00");
                        }
                        List<ChannelSupportBank> channelSupportBanks = channelSuportBankDao.searchList(channelSupportBank);
                        if(channelSupportBanks != null && channelSupportBanks.size() > 0){
                            //取出第一条记录 （这里防止数据库里有重复记录 做兼容）
                            channelSupportBank = channelSupportBanks.get(0);

                            //判断此卡在本地是否进行过快捷支付认证
                            QuickpayCardRecord quickpayCardRecord  = iQuickpayRecordDao.searchById(requestDTO.getCardNo());
                            if(quickpayCardRecord != null){
                                //已经进行了快捷认证
                                channelCardSupportResponseDTO.setLocalFlag("1");
                            }else{
                               //这张卡没有进行快捷认证
                                channelCardSupportResponseDTO.setLocalFlag("2");
                            }
                            //组装返回的数据
                            channelCardSupportResponseDTO.setRetCode(0);
                            channelCardSupportResponseDTO.setRetMessage("查询成功");
                            channelCardSupportResponseDTO.setChannelSupportBank(channelSupportBank);
                            logger.info("查询成功"+requestDTO.toString()+"，返回："+channelCardSupportResponseDTO.toString());
                            //参数出错
                            try {
                                jsonString = createJsonString(channelCardSupportResponseDTO);
                            } catch (Exception em) {
                                em.printStackTrace();
                            }
                            return jsonString;
                        }else{
                            //本地没有记录
                            channelCardSupportResponseDTO.setRetCode(1);
                            channelCardSupportResponseDTO.setLocalFlag("4");
                            channelCardSupportResponseDTO.setRetMessage("快捷支付不支持的卡类型或银行");
                            logger.info("快捷支付不支持的卡类型或银行"+requestDTO.toString());
                            //参数出错
                            try {
                                jsonString = createJsonString(channelCardSupportResponseDTO);
                            } catch (Exception em) {
                                em.printStackTrace();
                            }
                            return jsonString;
                        }



                    }else{
                        //找不到卡兵
                        channelCardSupportResponseDTO.setRetCode(1);
                        channelCardSupportResponseDTO.setLocalFlag("3");
                        channelCardSupportResponseDTO.setRetMessage("暂不支持此卡，请联系客服");
                        logger.info("没有卡宾"+requestDTO.toString());
                        //参数出错
                        try {
                            jsonString = createJsonString(channelCardSupportResponseDTO);
                        } catch (Exception em) {
                            em.printStackTrace();
                        }
                        return jsonString;
                    }

                } catch (Exception e) {
                    channelCardSupportResponseDTO.setRetCode(1);
                    channelCardSupportResponseDTO.setRetMessage("参数出错");
                    logger.info("参数出错");
                    //参数出错
                    try {
                        jsonString = createJsonString(channelCardSupportResponseDTO);
                    } catch (Exception em) {
                        em.printStackTrace();
                    }
                    return jsonString;
                }

            }else{
                //参数为空
                channelCardSupportResponseDTO.setRetCode(1);
                channelCardSupportResponseDTO.setRetMessage("参数不正确");
                try {
                    jsonString = createJsonString(channelCardSupportResponseDTO);
                } catch (Exception em) {
                    em.printStackTrace();
                }
                return jsonString;
            }

        }else{
            channelCardSupportResponseDTO.setRetCode(13);
            channelCardSupportResponseDTO.setRetMessage("会话过期，请重新登陆");
            try {
                jsonString = createJsonString(channelCardSupportResponseDTO);
            } catch (Exception em) {
                em.printStackTrace();
            }
            return jsonString;
        }
    }

}
