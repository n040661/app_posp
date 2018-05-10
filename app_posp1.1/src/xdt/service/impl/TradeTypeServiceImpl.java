package xdt.service.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import xdt.common.RetAppMessage;
import xdt.dto.TradeTypeListResponseDTO;
import xdt.model.SessionInfo;
import xdt.model.TradeTypeModel;
import xdt.service.ITradeTypeService;
import xdt.util.TradeTypeEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 交易类型服务层
 * User: Jeff
 * Date: 15-5-26
 * Time: 上午11:10
 */
@Service
public class TradeTypeServiceImpl extends BaseServiceImpl implements ITradeTypeService {

    private Logger logger = Logger.getLogger(TradeTypeServiceImpl.class);
    /**
     * 交易类型列表
     * @param request
     * @param session
     * @return
     */
    @Override
    public String tradeTypeList(HttpServletRequest request, HttpSession session) throws Exception {

        String message = INITIALIZEMESSAGE;
        SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
        TradeTypeListResponseDTO tradeTypeListResponseDTO = new TradeTypeListResponseDTO();
        List<TradeTypeModel> tradeTypeModels =  null;
        if(sessionInfo != null){
           tradeTypeModels = TradeTypeEnum.getTradeTypeList();
           message = SUCCESSMESSAGE;
        }else{
            //回话失效
            message = RetAppMessage.SESSIONINVALIDATION;
        }


        int retCode = Integer.parseInt(message.split(":")[0]);
        String retMessage = message.split(":")[1];
        if (retMessage.equals("sessionInvalidation")) {
            retMessage = "会话失效，请重新登录";
        }
        tradeTypeListResponseDTO.setRetCode(retCode);
        tradeTypeListResponseDTO.setRetMessage(retMessage);
        tradeTypeListResponseDTO.setTradeTypeModels(tradeTypeModels);
        String  jsonString = createJsonString(tradeTypeModels);
        return jsonString;
    }

}
