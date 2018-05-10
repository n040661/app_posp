package xdt.dao.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import xdt.dao.IPayTypeControlDao;
import xdt.model.PayTypeControl;
import xdt.model.ResultInfo;

import java.util.List;

/**
 * 交易类型是否开启控制dao层
 * User: Jeff
 * Date: 16-1-20
 * Time: 上午10:23
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class PayTypeControlDaoImpl  extends BaseDaoImpl<PayTypeControl> implements IPayTypeControlDao {

    private Logger logger = Logger.getLogger(PayTypeControlDaoImpl.class);
    //查找一条记录
    private static final String selectByOagentPayType = "selectByOagentPayType";
    //按照欧单编号查找记录
    private static final String selectByOagent = "selectByOagent";

    @Override
    public ResultInfo checkLimit(String oAgentNo, String payType) {

        ResultInfo resultInfo = new ResultInfo();

        String sql = getStatementId(selectByOagentPayType);
        PayTypeControl payTypeControl = new PayTypeControl();
        payTypeControl.setOagentno(oAgentNo);
        payTypeControl.setPaytype(payType);
        payTypeControl = sqlSession.selectOne(sql, payTypeControl);
        if(payTypeControl != null){
            if(payTypeControl != null && payTypeControl.getStatus().equals("1")){
                //交易开启
                resultInfo.setErrCode("0");
                resultInfo.setMsg("交易开启");
                return resultInfo;
            }else{
                //交易关闭
                resultInfo.setErrCode("1");
                resultInfo.setMsg(payTypeControl.getReason());
                return resultInfo;
            }
        }else{
            resultInfo.setErrCode("2");
            resultInfo.setMsg("没有查到数据 oAgentNo："+oAgentNo+",payType:"+payType);
            return resultInfo;
        }
    }

    @Override
    public List<PayTypeControl> getListByOagentNo(String oAgentNo) {
        String sql = getStatementId(selectByOagent);
        return  sqlSession.selectList(sql,oAgentNo);
    }


}
