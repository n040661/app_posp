package xdt.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import xdt.dao.IShareInfoDao;
import xdt.dto.ShareInfoQueryResponseDto;
import xdt.model.SessionInfo;
import xdt.model.ShareInfo;
import xdt.service.IShareInfoService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created with IntelliJ IDEA.
 * User: Jeff
 * Date: 15-12-11
 * Time: 下午2:52
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ShareInfoServiceImpl extends BaseServiceImpl  implements IShareInfoService {
    private Logger logger = Logger.getLogger(RemitServiceImpl.class);
    @Resource
    private IShareInfoDao shareInfoDao; // 通道信息层
    @Override
    public String getByOagentNo(String phoneNumberRequest,HttpSession session,HttpServletRequest request) throws Exception{

        setMethodSession(request.getRemoteAddr());
        String message = INITIALIZEMESSAGE;
        String  jsonString ="";
        ShareInfoQueryResponseDto responseData = new ShareInfoQueryResponseDto();
        SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
        String oAgentNo = "";
        //判断会话是否失效
        if (null != sessionInfo) {
            oAgentNo = sessionInfo.getoAgentNo();
            if(StringUtils.isBlank(oAgentNo)){
                responseData.setRetCode(1);
                responseData.setRetMessage("参数错误");
                jsonString = createJsonString(responseData);
                return jsonString;
            } else{

                ShareInfo shareInfo = shareInfoDao.selectByOagentNo(oAgentNo);
                if(shareInfo != null){
                    responseData.setRetCode(0);
                    responseData.setRetMessage("成功");
                    responseData.setShareContent(shareInfo.getShareContent());
                    responseData.setShareImages(shareInfo.getShareImages());
                    responseData.setShareTitle(shareInfo.getShareTitle());
                    responseData.setShareURL(shareInfo.getShareUrl());
                    jsonString = createJsonString(responseData);
                    return jsonString;
                }else{
                    responseData.setRetCode(1);
                    responseData.setRetMessage("参数不正确");
                    return jsonString;
                }

            }
        }  else{

            //未登录
            responseData.setRetCode(13);
            responseData.setRetMessage("会话过期，请重新登陆");
            try {
                jsonString = createJsonString(responseData);
            } catch (Exception em) {
                em.printStackTrace();
            }
            return jsonString;
        }


    }
}
