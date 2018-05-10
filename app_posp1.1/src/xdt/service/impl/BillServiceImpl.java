package xdt.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import xdt.common.RetAppMessage;
import xdt.dao.*;
import xdt.dto.*;
import xdt.model.*;
import xdt.service.IBillService;
import xdt.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 账单调用服务层
 * User: Jeff
 * Date: 15-5-22
 * Time: 下午5:05
 * To change this template use File | Settings | File Templates.
 */
@Service
public class BillServiceImpl extends BaseServiceImpl implements IBillService {

    private Logger logger = Logger.getLogger(BillServiceImpl.class);
    @Resource
    IPmsAppTransInfoDao appTransInfoDao;
    @Resource
    ITAppSettleAccountTempDao itAppSettleAccountTempDao;
    @Resource
    IPospTransInfoDAO pospTransInfoDAO;
    @Resource
    IPmsAgentInfoDao iPmsAgentInfoDao;
    @Resource
    TTransSettleAgentT0Dao tTransSettleAgentT0Dao;
    @Resource
    IPmsUnionpayDao pmsUnionpayDao;
    /**
     * 获取账单列表
     * @param requestData
     * @param session
     * @return
     */
    @Override
    public String billList(String requestData, HttpSession session) {

        BillListResponseDTO billListResponseDTO = new BillListResponseDTO();
        String message = INITIALIZEMESSAGE;


        PageView pageView = null;
        try {
            HashMap<String, Object> map = validateNullAndParseData(session, requestData, BillListRequestDTO.class);
            message = map.get("message").toString();
            if (message.equals(RetAppMessage.DATAANALYTICALSUCCESS)) {


                SessionInfo sessionInfo = (SessionInfo) map.get("sessionInfo");
                BillListRequestDTO requestDTO = (BillListRequestDTO) map.get("obj");
                Map<String, String> paramMap = new HashMap<String, String>();
                paramMap.put("mercId", sessionInfo.getMercId());

                Integer pageNum = requestDTO.getPageNum();//当前页
                if (pageNum == null || pageNum <= 0) {
                    pageNum = 1;
                }

                //获取近30天的日期列表

                Map<String, String> dateMap = new HashMap<String, String>();

                dateMap.put("pageNum", pageNum.toString());

                dateMap.put("mercid", sessionInfo.getMercId());

                if (StringUtils.isNotBlank(requestDTO.getTradetypecode())) {
                    dateMap.put("tradetypecode", requestDTO.getTradetypecode());
                }

                if (StringUtils.isNotBlank(requestDTO.getPaymentcode())) {
                    dateMap.put("paymentcode", requestDTO.getPaymentcode());
                }
                dateMap.put("dates", "30");

                List<TransLatestData> transLatestDataListAll = appTransInfoDao.selectLatestMonthAll(dateMap);
                List<TransLatestData> transLatestDataList = appTransInfoDao.selectTransLatestDate(dateMap);


                if (transLatestDataListAll != null && transLatestDataListAll.size() > 0) {
                    //获取日期的开始与结束
                    TransLatestData beginDate = transLatestDataListAll.get(0);
                    TransLatestData endDate = transLatestDataListAll.get(transLatestDataListAll.size() - 1);
                    dateMap.put("beginDate", endDate.getDateStr());
                    dateMap.put("endDate", beginDate.getDateStr());
                    dateMap.put("pageSize", String.valueOf(PageView.PAGEZISE));
                    List<PmsAppTransInfo> pmsAppTransInfos = appTransInfoDao.selectTransLatestData(dateMap);
                    Integer count = appTransInfoDao.selectLatesCountData(dateMap);

                    List<BillResponseData> billResponseDatas = new ArrayList<BillResponseData>();
                    BillResponseData billResponseData = null;

                    //记录第一个根节点下的数据的金额和
                    Double firstSubAmountSum = 0.0;
                    Integer firstCount = 0;
                    //记录当前根节点的序号
                    int times = 0;
                    //第一次处理数据
                    for (TransLatestData transLatestDate : transLatestDataListAll) {
                        times++;
                        //记录子项的条数
                        int subNum = 0;
                        //添加根节点
                        billResponseData = new BillResponseData();
                        billResponseData.setRoot(1);
                        billResponseData.setAmount(UtilMath.keepUpDouble(getLatestDataByDate(transLatestDate.getDateStr(),transLatestDataList)).toString());
                        billResponseData.setDate(transLatestDate.getDateStr());
                        billResponseDatas.add(billResponseData);
                        //便利订单，将属于该根节点下的订单依次加入
                        for (PmsAppTransInfo pmsAppTransInfo : pmsAppTransInfos) {

                            if (StringUtils.isNotBlank(pmsAppTransInfo.getTradetime()) && transLatestDate.getDateStr().equals(UtilDate.formatDateTimeToDate(pmsAppTransInfo.getTradetime()))) {
                                if(times == 1 ){
                                    firstCount ++;
                                }

                                billResponseData = new BillResponseData();
                                billResponseData.setAmount(UtilMath.keepUpDouble(pmsAppTransInfo.getFactamount()).toString());
                                billResponseData.setDate(transLatestDate.getDateStr());
                                billResponseData.setPaymengType(pmsAppTransInfo.getPaymenttype());
                                billResponseData.setTradeType(pmsAppTransInfo.getTradetype());
                                billResponseData.setTime(pmsAppTransInfo.getTradetime().split(" ")[1]);
                                if(pmsAppTransInfo != null && pmsAppTransInfo.getStatus() != null){
                                    if(pmsAppTransInfo.getStatus().equals(OrderStatusEnum.paySuccess.getStatus())
                                            || pmsAppTransInfo.getStatus().equals(OrderStatusEnum.returnMoneySuccess.getStatus())
                                            ){
                                        //支付成功 退款成功
                                        billResponseData.setStatus("0");
                                    }else if(pmsAppTransInfo.getStatus().equals(OrderStatusEnum.payFail.getStatus()) ||
                                            pmsAppTransInfo.getStatus().equals(OrderStatusEnum.systemErro.getStatus())
                                            ){
                                        //支付失败 系统异常
                                        billResponseData.setStatus("2");
                                    }else {
                                        //正在支付
                                        billResponseData.setStatus("1");
                                    }
                                } else {
                                    //支付失败
                                    billResponseData.setStatus("2");
                                }
                                billResponseData.setOrderId(pmsAppTransInfo.getOrderid());
                                billResponseData.setPaymengTypeCode(pmsAppTransInfo.getPaymentcode());

                                billResponseData.setTradeTypeCode(pmsAppTransInfo.getTradetypecode());
                                billResponseDatas.add(billResponseData);
                                subNum++;
                            }
                        }
                        //如果没有子项，则去掉当前根
                        if (subNum == 0) {
                            billResponseDatas.remove(billResponseDatas.size() - 1);
                            /**
                             * 如果当前列表为空，则看作第一项
                             */
                            if(billResponseDatas.size() == 0 ){
                                times=0;
                            }
                        }

                    }

                    //第二次处理数据
                    if (billResponseDatas != null && billResponseDatas.size() > 1) {
                        //获取当前第一条记录在该记录日下的行数，如果是第一行，说明更根节点有效，如果不是应去除根节点
                        Map<String,String> pMap = new HashMap<String, String>();
                        pMap.put("orderId",billResponseDatas.get(1).getOrderId());
                        pMap.put("tradeDate",billResponseDatas.get(1).getDate());
                        pMap.put("trandType",billResponseDatas.get(1).getTradeTypeCode());
                        pMap.put("mercId",sessionInfo.getMercId());
                        Integer rowNum = appTransInfoDao.getRowNumByDate(pMap);
                        if(rowNum != null && !rowNum.equals(1)){
                            //如果不是第一条应去除根节点
                            billResponseDatas.remove(0);
                        }
                    }

                    pageView = new PageView(count, billResponseDatas);
                    pageView.setPageNum(pageNum);
                    pageView.setPageSize(PageView.PAGEZISE);
                    int pogeCount = count % PageView.PAGEZISE == 0 ? count / PageView.PAGEZISE : count / PageView.PAGEZISE + 1;
                    pageView.setPageCount(pogeCount);
                }

                message = SUCCESSMESSAGE;
            } else if (message.equals(RetAppMessage.SESSIONINVALIDATION)) {
                logger.info("会话失效" + "详情：" + message);
            } else if (message.equals(RetAppMessage.DATAANALYTICALFAILURE)) {
                logger.info("数据解析失败," + "详情：" + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int retCode = Integer.parseInt(message.split(":")[0]);
        String retMessage = message.split(":")[1];
        retMessage = RetAppMessage.parseMessageCode(retMessage);
        billListResponseDTO.setPageView(pageView);
        billListResponseDTO.setRetCode(retCode);
        billListResponseDTO.setRetMessage(retMessage);
        String jsonString = null;
        try {
            jsonString = createJsonString(billListResponseDTO);
        } catch (Exception e) {
            logger.info("返回数据解析失败，详情：" + e.getMessage());
        }
        return jsonString;
    }

    /**
     * 获取账单详情
     *
     * @param requestData
     * @param session
     * @return
     */
    @Override
    public String billDetail(String requestData, HttpSession session) {
        BillDetailResponseDTO billDetailResponseDTO = new BillDetailResponseDTO();
        String message = INITIALIZEMESSAGE;
        PmsAppTransInfo pmsAppTransInfo = null;
        PospTransInfo pospTransInfo = null;
        BillDetail billDetail = null;
        try {
            HashMap<String, Object> map = validateNullAndParseData(session, requestData, BillDetailRequestDTO.class);
            message = map.get("message").toString();
            if (message.equals(RetAppMessage.DATAANALYTICALSUCCESS)) {
                //判断当前商户是否是实名认证的
                SessionInfo sessionInfo = (SessionInfo) map.get("sessionInfo");
                BillDetailRequestDTO requestDTO = (BillDetailRequestDTO) map.get("obj");
                String orderId = "";
                if (requestDTO != null) {
                    orderId = requestDTO.getOrderId();
                }
                pmsAppTransInfo = appTransInfoDao.searchOrderInfo(orderId);
                pmsAppTransInfo.setMercname(sessionInfo.getShortname());
                pospTransInfo = pospTransInfoDAO.searchByOrderId(orderId);
                //检查当前商户是否在白名单内，如果在则调用生成透传详情的逻辑
                PmsUnionpay pmsUnionpay = pmsUnionpayDao.searchById(sessionInfo.getMercId());
                if(pmsUnionpay != null){
                    //调用大posp接口，查询订单详情
                    String pospTransOrgStr = HttpURLConection.httpURLConectionGET(POSPBILLSEARCHURL+pospTransInfo.getSysseqno(),"UTF-8");
                    logger.info("当前是白名单用户，调用透传逻辑地址："+POSPBILLSEARCHURL+pospTransInfo+"，返回"+pospTransOrgStr);
                    if(StringUtils.isNotBlank(pospTransOrgStr)){
                        PospTransInfoOrgRspDTO pospTransInfoOrgRspDTO = JSONObject.parseObject(pospTransOrgStr,PospTransInfoOrgRspDTO.class);
                        //使用透传逻辑生成详情
                        billDetail = BillDetail.parseFromTransInfoOrg(pmsAppTransInfo,pospTransInfoOrgRspDTO,pmsUnionpay);
                    }

                }else{
                    billDetail = BillDetail.parseFromTransInfo(pmsAppTransInfo,pospTransInfo);
                }


                if (pmsAppTransInfo != null) {
                    message = SUCCESSMESSAGE;
                }
            } else if (message.equals(RetAppMessage.SESSIONINVALIDATION)) {
                logger.info("回话失效" + "详情：" + message);
            } else if (message.equals(RetAppMessage.DATAANALYTICALFAILURE)) {
                logger.info("数据解析失败," + "详情：" + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int retCode = Integer.parseInt(message.split(":")[0]);
        String retMessage = message.split(":")[1];
        retMessage = RetAppMessage.parseMessageCode(retMessage);
        billDetailResponseDTO.setRetCode(retCode);
        billDetailResponseDTO.setRetMessage(retMessage);
        billDetailResponseDTO.setBillDetail(billDetail);
        String jsonString = null;
        try {
            jsonString = createJsonString(billDetailResponseDTO);
        } catch (Exception e) {
            logger.info("返回数据解析失败，详情：" + e.getMessage());
        }
        return jsonString;
    }

    /**
     * 到账查询
     *
     * @param requestData
     * @param session
     * @return
     */
    @Override
    public String billArriveList(String requestData, HttpSession session) {

        BillArriveResponseDTO billArriveResponseDTO = new BillArriveResponseDTO();
        String message = INITIALIZEMESSAGE;

        PageView pageView = null;
        try {
            HashMap<String, Object> map = validateNullAndParseData(session, requestData, BillArriveRequestDTO.class);
            message = map.get("message").toString();
            if (message.equals(RetAppMessage.DATAANALYTICALSUCCESS)) {

                SessionInfo sessionInfo = (SessionInfo) map.get("sessionInfo");
                BillArriveRequestDTO requestDTO = (BillArriveRequestDTO) map.get("obj");

                Integer pageNum = requestDTO.getPageNum();//当前页
                if (pageNum == null || pageNum <= 0) {
                    pageNum = 1;
                }

                //获取近30天的日期列表
                Map<String, String> dateMap = new HashMap<String, String>();

                dateMap.put("pageNum", pageNum.toString());
                dateMap.put("mercid", sessionInfo.getMercId());
                if (StringUtils.isNotBlank(requestDTO.getTradetypecode())) {
                    dateMap.put("tradetypecode", requestDTO.getTradetypecode());
                }
                if (StringUtils.isNotBlank(requestDTO.getPaymentcode())) {
                    dateMap.put("paymentcode", requestDTO.getPaymentcode());
                }
                dateMap.put("pageSize", String.valueOf(PageView.PAGEZISE30));
                List<TransLatestData> transLatestMonthList = null;



                PmsAgentInfo oAgent = iPmsAgentInfoDao.selectOagentByMercNum(sessionInfo.getMercId());
                if(oAgent != null){
                        //检查当前商户的欧单的清算类型
                        if( oAgent.getClearType() != null && oAgent.getClearType().equals("0")){
                            logger.info("当前查询的秒到记录");
                             //T+0 秒到提现
                             transLatestMonthList = tTransSettleAgentT0Dao.selectLatestMonth(dateMap);
                        }else{
                            //其他提现
                            //获取按月的统计项 默认获取一年的数据
                            if(StringUtils.isNotBlank(requestDTO.getPaymentcode())){
                                if(requestDTO.getPaymentcode().equals("7")){
                                    //T+0
                                    transLatestMonthList = appTransInfoDao.selectLatestMonth(dateMap);
                                }else if(requestDTO.getPaymentcode().equals("8")){
                                    //T+1
                                    transLatestMonthList = itAppSettleAccountTempDao.selectLatestMonth(dateMap);
                                }
                            }else{
                                //T+0
                                transLatestMonthList = appTransInfoDao.selectLatestMonth(dateMap);
                            }
                        }


                }

                List<BillArriveResponseData> billArriveFinal = new ArrayList<BillArriveResponseData>();
                if(transLatestMonthList != null && transLatestMonthList.size() > 0){
                    //获取最大和最小交易时间月份
                    String maxMonth = transLatestMonthList.get(0).getDateStr();
                    String minMonth = transLatestMonthList.get(transLatestMonthList.size()-1).getDateStr();
                    dateMap.put("beginMonth",minMonth);
                    dateMap.put("endMonth",maxMonth);
                    List<TransLatestData> transLatestDataList = null;
                    Integer count = null;
                    if(oAgent != null){
                        //检查当前商户的欧单的清算类型
                        if( oAgent.getClearType() != null && oAgent.getClearType().equals("0")){
                            //T+0 秒到提现
                            logger.info("当前查询的秒到记录");
                            transLatestDataList = tTransSettleAgentT0Dao.selectLatestDayDataPage(dateMap);
                            count = tTransSettleAgentT0Dao.selectLatestDayDataPageCount(dateMap);
                         }else{
                            //其他提现
                            if(StringUtils.isNotBlank(requestDTO.getPaymentcode())){
                                if(requestDTO.getPaymentcode().equals("7")){
                                    //T+0
                                    transLatestDataList = appTransInfoDao.selectLatestDayDataPage(dateMap);
                                    count = appTransInfoDao.selectLatestDayDataPageCount(dateMap);
                                }else if(requestDTO.getPaymentcode().equals("8")){
                                    //T+1
                                    transLatestDataList = itAppSettleAccountTempDao.selectLatestDayDataPage(dateMap);
                                    count = itAppSettleAccountTempDao.selectLatestDayDataPageCount(dateMap);
                                }
                            }else{
                                //T+0
                                transLatestDataList = appTransInfoDao.selectLatestDayDataPage(dateMap);
                                count = appTransInfoDao.selectLatestDayDataPageCount(dateMap);
                            }
                        }
                    }


                    if(transLatestDataList != null && transLatestDataList.size() > 0){
                             //便利月份数据  组装日对象
                          for(TransLatestData transLatestMonth:transLatestMonthList){



                              //添加月对象
                              String month = transLatestMonth.getDateStr();
                              BillArriveResponseData monthData = new BillArriveResponseData();
                              monthData.setTag("1");
                              monthData.setMonth(month);
                              monthData.setAmountSum(UtilMath.keepUpDouble(transLatestMonth.getAmountSum()).toString());

                              //判断当前列表中是否已经存在这个月对象，存在则跳过
                              if(checkExistMonthObj(billArriveFinal,monthData)){
                                  continue;
                              }

                              billArriveFinal.add(monthData);
                              //添加日对象
                              //便利list，找出当前月下的日对象
                              for (TransLatestData transLatestDate : transLatestDataList) {
                                  String date = transLatestDate.getDateStr();
                                  if (month.equals(UtilDate.formatDateToMonth(date))) {
                                      //上一个和当前对象是同一天的 并且上一个对象是月对象
                                      if (billArriveFinal != null && billArriveFinal.size() > 0 && !billArriveFinal.get(billArriveFinal.size() - 1).getTag().equals("1") && billArriveFinal.get(billArriveFinal.size() - 1).getDate().equals(date)) {
                                          //将当前对象加入到上一个对象的列表中
                                          List<BillArriveResponseData> billArriveResponseDatas = billArriveFinal.get(billArriveFinal.size() - 1).getBillSub();
                                          String amount =   billArriveFinal.get(billArriveFinal.size() - 1).getAmountSum();
                                          //添加交易对象
                                          BillArriveResponseData billArriveResponseItem = new BillArriveResponseData();
                                          billArriveResponseItem.setTag("3");
                                          if(transLatestDate.getPoundageSum() != null){
                                              billArriveResponseItem.setFee(UtilMath.keepUpDouble(transLatestDate.getPoundageSum()).toString());
                                          }

                                          billArriveResponseItem.setAmountSum(String.valueOf(UtilMath.keepUpDouble(transLatestDate.getAmountSum()) + UtilMath.keepUpDouble(transLatestDate.getPoundageSum())));
                                          billArriveResponseItem.setDate(transLatestDate.getDateStrTrade());
                                          billArriveResponseDatas.add(billArriveResponseItem);

                                          if(StringUtils.isNotBlank(amount) && StringUtils.isNotBlank(transLatestDate.getAmountSum())){
                                              Double amountFinal =  Double.parseDouble(transLatestDate.getAmountSum())+Double.parseDouble(amount);
                                              billArriveFinal.get(billArriveFinal.size() - 1).setAmountSum(UtilMath.keepUpDouble(amountFinal).toString());
                                          }


                                      } else {
                                          //上一个和当前对象不是同一天的，说明是新的日对象
                                          //添加当前月下的日对象
                                          BillArriveResponseData billArriveResponseDay = new BillArriveResponseData();
                                          billArriveResponseDay.setTag("2");
                                          billArriveResponseDay.setAmountSum(UtilMath.keepUpDouble(transLatestDate.getAmountSum()).toString());
                                          billArriveResponseDay.setDate(date);
                                          List<BillArriveResponseData> billArriveResponseDatas = new ArrayList<BillArriveResponseData>();
                                          //添加交易对象
                                          BillArriveResponseData billArriveResponseItem = new BillArriveResponseData();
                                          billArriveResponseItem.setTag("3");
                                          if(transLatestDate.getPoundageSum() != null){
                                              billArriveResponseItem.setFee(UtilMath.keepUpDouble(transLatestDate.getPoundageSum()).toString());
                                          }
                                          billArriveResponseItem.setAmountSum(String.valueOf(UtilMath.keepUpDouble(transLatestDate.getAmountSum()) + UtilMath.keepUpDouble(transLatestDate.getPoundageSum())));
                                          billArriveResponseItem.setDate(transLatestDate.getDateStrTrade());
                                          billArriveResponseDatas.add(billArriveResponseItem);
                                          billArriveResponseDay.setBillSub(billArriveResponseDatas);
                                          billArriveFinal.add(billArriveResponseDay);
                                      }
                                  }
                              }
                          }
                    }
                    pageView = new PageView(count, billArriveFinal);
                    pageView.setPageNum(pageNum);
                    pageView.setPageSize(PageView.PAGEZISE30);
                    int pageCount = count % PageView.PAGEZISE30 == 0 ? count / PageView.PAGEZISE30 : count / PageView.PAGEZISE30 + 1;
                    pageView.setPageCount(pageCount);
                 }
                message = SUCCESSMESSAGE;
            } else if (message.equals(RetAppMessage.SESSIONINVALIDATION)) {
                message = FAILUREMESSAGE;
                logger.info("回话失效" + "详情：" + message);
            } else if (message.equals(RetAppMessage.DATAANALYTICALFAILURE)) {
                message = DATAPARSINGMESSAGE;
                logger.info("数据解析失败," + "详情：" + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int retCode = Integer.parseInt(message.split(":")[0]);
        String retMessage = message.split(":")[1];
        retMessage = RetAppMessage.parseMessageCode(retMessage);
        billArriveResponseDTO.setPageView(pageView);
        billArriveResponseDTO.setRetCode(retCode);
        billArriveResponseDTO.setRetMessage(retMessage);
        String jsonString = null;
        try {
            jsonString = createJsonString(billArriveResponseDTO);
        } catch (Exception e) {
            logger.info("返回数据解析失败，详情：" + e.getMessage());
        }
        return jsonString;
    }


    /**
     * 根据日期 获取列表中相应的记录的总金额
     * @param date
     * @return
     */
    private String getLatestDataByDate (String date,  List<TransLatestData> transLatestDataList){

        String result = "0";
        if(StringUtils.isNotBlank(date)){
            if(transLatestDataList != null  && transLatestDataList.size() > 0){
                for(TransLatestData transLatestData : transLatestDataList){
                    if(transLatestData.getDateStr().equals(date)){
                        result = transLatestData.getAmountSum();
                        break;
                    }
                }
            }
        }
        return  result;
    }

    /**
     * 合并月对象
     * @param billArriveFinal
     * @param monthData
     * @return
     */
    public boolean checkExistMonthObj( List<BillArriveResponseData> billArriveFinal,BillArriveResponseData monthData){
        Boolean result = false;
        //便利
        if(billArriveFinal != null && billArriveFinal.size() > 0 ){
            for(BillArriveResponseData transLatestData1 : billArriveFinal){
                if(transLatestData1.getMonth() != null && transLatestData1.getMonth().equals(monthData.getMonth())){
                    transLatestData1.setAmountSum(String.valueOf(UtilMath.keepUpDouble(transLatestData1.getAmountSum()) + UtilMath.keepUpDouble(monthData.getAmountSum())));
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

}
