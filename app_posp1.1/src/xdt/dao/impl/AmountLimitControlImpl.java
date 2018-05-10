package xdt.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import xdt.dao.IAmountLimitControlDao;
import xdt.model.AmountLimitControl;
import xdt.model.ResultInfo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 交易金额限制控制dao层
 * User: Jeff
 * Date: 16-1-20
 * Time: 上午10:23
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class AmountLimitControlImpl extends BaseDaoImpl<AmountLimitControl> implements IAmountLimitControlDao {
    //查找一条记录
    private static final String selectByOagentTransType = "selectByOagentTransType";
    private static final String selectByOagentNo = "selectByOagentNo";
    private Logger logger = Logger.getLogger(AmountLimitControlImpl.class);
    @Override
    public ResultInfo checkLimit(String oAgentNo, BigDecimal transAmount, String tradeType) {
        logger.info("进入按照欧单的金额条件过滤");
        ResultInfo resultInfo = new ResultInfo();
        //参数校验
        if(StringUtils.isBlank(oAgentNo)){
            resultInfo.setErrCode("2");
            resultInfo.setMsg("参数非法，请重试");
            logger.info("欧单交易金额限制过滤，欧单编号为空");
           return resultInfo;
        }
        if(transAmount.compareTo(new BigDecimal(0)) < 0){
            resultInfo.setErrCode("2");
            resultInfo.setMsg("参数非法，请重试");
            logger.info("欧单交易金额限制过滤，交易金额为负数");
           return resultInfo;
        }if(StringUtils.isBlank(tradeType)){
            resultInfo.setErrCode("2");
            resultInfo.setMsg("交易类型为空");
            logger.info("欧单交易金额限制过滤，交易类型为空");
           return resultInfo;
        }

        String sql = getStatementId(selectByOagentTransType);
        AmountLimitControl amountLimitControl = new AmountLimitControl();
        amountLimitControl.setOagentno(oAgentNo);
        amountLimitControl.setTradetype(tradeType);
        amountLimitControl =  sqlSession.selectOne(sql, amountLimitControl);
        if(amountLimitControl != null){

          if(amountLimitControl.getStatus() != null && amountLimitControl.getStatus().equals("0")){
               //失效 说明当前交易没有限制 直接返回成功
              resultInfo.setErrCode("0");
              resultInfo.setMsg("当前欧单没有限制");
              logger.info("当前欧单没有限制");
              return resultInfo;
          }else{
              if(transAmount.compareTo(amountLimitControl.getMinamount()) >= 0  && transAmount.compareTo(amountLimitControl.getMaxamount()) <= 0){
                  //可用
                  resultInfo.setErrCode("0");
                  resultInfo.setMsg("在范围内，允许交易");
                  logger.info("在范围内，允许交易");
                  return resultInfo;
              }else{
                  //可用
                  resultInfo.setErrCode("1");
                  resultInfo.setMsg("交易金额不在范围内，允许的交易金额为："+amountLimitControl.getMinamount().divide(new BigDecimal(100)).toString()+"-"+amountLimitControl.getMaxamount().divide(new BigDecimal(100)).toString()+"元");
                  logger.info("交易金额不在范围内，拒绝交易");
                  return resultInfo;
              }
          }
        }else{
            resultInfo.setErrCode("2");
            resultInfo.setMsg("没有查到数据，请检查参数");
            return resultInfo;
        }
    }

    @Override
    public List<AmountLimitControl> getListByOagentNo(String oAgentNo) {
        return   sqlSession.selectList(getStatementId(selectByOagentNo),oAgentNo);
    }
}
