package xdt.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import xdt.dao.IAppVersionDao;
import xdt.dto.NewVersionRequestDTO;
import xdt.dto.NewestVersionResponseDTO;
import xdt.model.AppVersion;
import xdt.service.IAppVersionService;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * app版本服务层
 * User: Jeff
 * Date: 15-5-26
 * Time: 下午1:50
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AppVersionServiceImpl extends BaseServiceImpl implements IAppVersionService {

    private Logger logger=Logger.getLogger(AppVersionServiceImpl.class);

    @Resource
    IAppVersionDao appVersionDao;
    /**
     * 获取最新的版本信息
     * @param requestData
     * @param session
     * @return
     */
    @Override
    public String newestVersion(String requestData,HttpSession session) throws Exception {
        String message = INITIALIZEMESSAGE;
        logger.info("进入版本号查询接口。。。");
        NewestVersionResponseDTO newestVersionResponseDTO = new NewestVersionResponseDTO();

        NewVersionRequestDTO newVersionRequestDTO = (NewVersionRequestDTO)parseJsonString(requestData, NewVersionRequestDTO.class);
        AppVersion appVersion = null;
        String oAgentNo = newVersionRequestDTO.getoAgentNo();
        if(StringUtils.isBlank(oAgentNo)){
            logger.info("当前版本为老版本，采用默认欧单编号。。。");
            //如果欧单编号为空，默认付呗的欧单编号
            oAgentNo = "100844";
        }
        if(newVersionRequestDTO != null && StringUtils.isNotBlank(newVersionRequestDTO.getClientType())){
            AppVersion appv = new AppVersion();
            appv.setClientType(newVersionRequestDTO.getClientType());
            appv.setoAgentNo(oAgentNo);
            appVersion  = appVersionDao.selectNewestOne(appv);
        }
        if(appVersion != null){
            message = SUCCESSMESSAGE;
            logger.info("查到当前最新版本号，"+appVersion.getVersionCode());
        }else{
            logger.info("没有查到版本号，"+oAgentNo+"  "+newVersionRequestDTO.getClientType());
        }
        //解析要返回的信息
        int retCode = Integer.parseInt(message.split(":")[0]);
        String retMessage = message.split(":")[1];

        if (retMessage.equals("initialize")) {
            retMessage = "系统初始化";
        } else if (retMessage.equals("fail")) {
            retMessage = "查询失败";
        }
        newestVersionResponseDTO.setRetCode(retCode);
        newestVersionResponseDTO.setRetMessage(retMessage);
        newestVersionResponseDTO.setDownUrl(appVersion.getDownUrl());
        newestVersionResponseDTO.setUpdateFlag(appVersion.getForceFlag());
        newestVersionResponseDTO.setUpdatInfo(appVersion.getDescription());
        newestVersionResponseDTO.setVersionId(appVersion.getVersionCode());
        newestVersionResponseDTO.setVersionNo(appVersion.getVersion());
        newestVersionResponseDTO.setVersionTime(appVersion.getCreatetime());
        logger.info("成功返回数据:"+createJson(newestVersionResponseDTO));
        return createJson(newestVersionResponseDTO);
    }
}
