package xdt.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import xdt.dao.IMerchantNewsInfoDao;
import xdt.dao.INewsInfoDao;
import xdt.dto.NewsDetailRequestDTO;
import xdt.dto.NewsDetailResponseDTO;
import xdt.dto.NewsInfoQueryRequestDto;
import xdt.dto.NewsInfoQueryResponseDto;
import xdt.model.MerchantNewsInfo;
import xdt.model.NewsInfo;
import xdt.model.NewsInfoSub;
import xdt.model.SessionInfo;
import xdt.service.INewsInfoService;
import xdt.util.PageView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jeff
 * Date: 15-12-11
 * Time: 下午4:34
 * To change this template use File | Settings | File Templates.
 */
@Service
public class NewsInfoServiceImpl extends BaseServiceImpl implements INewsInfoService {

    private Logger logger = Logger.getLogger(RemitServiceImpl.class);
    @Resource
    private INewsInfoDao newsInfoDao; // 通道信息层
    @Resource
    private IMerchantNewsInfoDao merchantNewsInfoDao;
    @Override
    public String selectPageByOagentNo(String requestStr, HttpSession session, HttpServletRequest request) throws Exception{
        setMethodSession(request.getRemoteAddr());
        String  jsonString ="";
        NewsInfoQueryResponseDto responseData = new NewsInfoQueryResponseDto();
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

                //解析请求参数
                Object obj= parseJsonString(requestStr, NewsInfoQueryRequestDto.class);

                if (!obj.equals(DATAPARSINGMESSAGE)) {
                    NewsInfoQueryRequestDto newsRequestInfo = (NewsInfoQueryRequestDto) obj;
                    if(newsRequestInfo != null && StringUtils.isNumeric(newsRequestInfo.getPage())){

                        PageView s = new PageView(Integer.parseInt(newsRequestInfo.getPage()),PageView.PAGESIZE20,oAgentNo);
                        PageView pageViewResult =  newsInfoDao.selectPageByOagentNo(s);
                        if(pageViewResult != null ){
                            //组装返回参数
                            responseData.setRetCode(0);
                            responseData.setRetMessage("成功");
                            List<NewsInfo> newsInfoList =pageViewResult.getRecordList();
                            List<NewsInfoSub> newsInfoSubs = new ArrayList<NewsInfoSub>();
                            //访问当前用户的已读信息
                            MerchantNewsInfo merchantNewsInfo = merchantNewsInfoDao.selectByMerc(sessionInfo.getMercId());
                            String haveReadIdsStr = null;
                            List<String> haveReadIds = null;
                            if(merchantNewsInfo != null){
                                haveReadIdsStr = merchantNewsInfo.getHaveReadNews();
                                haveReadIds = null;
                                if(StringUtils.isNotBlank(haveReadIdsStr)){
                                    String [] haveReadIdsArray = haveReadIdsStr.split(",");
                                    haveReadIds = Arrays.asList(haveReadIdsArray);
                                }
                            }

                            for(NewsInfo newsInfo:newsInfoList){
                                NewsInfoSub n = new NewsInfoSub();
                                n.setNewsDate(newsInfo.getNewsDate().split(" ")[0]);
                                n.setNewsId(newsInfo.getNewsId().toString());
                                n.setNewsType(newsInfo.getNewsType());
                                n.setNewsTitle(newsInfo.getNewsTitle());
                                if(haveReadIds != null){
                                    if(haveReadIds.indexOf(newsInfo.getNewsId().toString()) >= 0){
                                        //已读
                                        n.setRead("1");
                                    }else{
                                        n.setRead("0");
                                    }
                                }else{
                                    n.setRead("0");
                                }


                                newsInfoSubs.add(n);
                            }
                            responseData.setNewsList(newsInfoSubs);
                            responseData.setPageCount(pageViewResult.getRecordCount()%PageView.PAGESIZE20==0?pageViewResult.getRecordCount()/PageView.PAGESIZE20:pageViewResult.getRecordCount()/PageView.PAGESIZE20+1);
                            responseData.setPageNum(Integer.parseInt(newsRequestInfo.getPage()));
                            responseData.setPageSize(PageView.PAGESIZE20);
                            responseData.setRecordCount(pageViewResult.getRecordCount());
                            jsonString = createJsonString(responseData);
                            return jsonString;
                        }else{
                            //返回为空
                            responseData.setRetCode(1);
                            responseData.setRetMessage("返回为空");
                            jsonString = createJsonString(responseData);
                            return jsonString;
                        }
                    }else{
                        //参数不正确
                        responseData.setRetCode(1);
                        responseData.setRetMessage("参数不正确");
                        jsonString = createJsonString(responseData);
                        return jsonString;
                    }
                }else{
                    //解析失败
                    //参数不正确
                    responseData.setRetCode(1);
                    responseData.setRetMessage("解析失败");
                    jsonString = createJsonString(responseData);
                    return jsonString;
                }
            }
        }  else{

            //未登录
            responseData.setRetCode(13);
            responseData.setRetMessage("会话过期，请重新登陆");
            jsonString = createJsonString(responseData);
            return jsonString;
        }

    }

    @Override
    public String selectDetailByNewsId(String requestStr, HttpSession session, HttpServletRequest request) throws Exception {
        setMethodSession(request.getRemoteAddr());
        String  jsonString ="";
        NewsDetailResponseDTO responseData = new NewsDetailResponseDTO();
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

                //解析请求参数
                Object obj= parseJsonString(requestStr, NewsDetailRequestDTO.class);

                if (!obj.equals(DATAPARSINGMESSAGE)) {
                    NewsDetailRequestDTO newsDetailRequestInfo = (NewsDetailRequestDTO) obj;
                    if(newsDetailRequestInfo != null && StringUtils.isNumeric(newsDetailRequestInfo.getNewsId())){

                        NewsInfo newsInfo = newsInfoDao.searchById(newsDetailRequestInfo.getNewsId());

                        //是否已读的标志 0：未读 1：已读
                        int readed = 0;

                        if(newsInfo != null){

                            //修改商户信息表中相应的记录
                            MerchantNewsInfo m = merchantNewsInfoDao.selectByMerc(sessionInfo.getMercId());
                            if(m != null){
                                //修改数据
                                String haveReadIdsStr = null;
                                List<String> haveReadIds = null;

                                haveReadIdsStr = m.getHaveReadNews();
                                haveReadIds = null;
                                if(StringUtils.isNotBlank(haveReadIdsStr)){
                                    String [] haveReadIdsArray = haveReadIdsStr.split(",");
                                    haveReadIds = Arrays.asList(haveReadIdsArray);
                                }
                                if(haveReadIds != null){
                                    //判段已读列表中是否有当前信息编号
                                    if(haveReadIds.indexOf(newsInfo.getNewsId().toString()) >= 0){
                                        readed = 1;
                                        //已有，不做处理
                                    }else{
                                        //没有，添加到后边
                                        String newAlreadyRead = m.getHaveReadNews()+","+newsInfo.getNewsId().toString();
                                        //更新
                                        m.setHaveReadNews(newAlreadyRead);
                                        merchantNewsInfoDao.update(m);
                                    }

                                }else{
                                    //当前用户的已读列表是空的，这里做兼容，默认修改为当前信息详情id
                                    m.setHaveReadNews(newsInfo.getNewsId().toString());
                                    merchantNewsInfoDao.update(m);
                                }

                            }else{
                                //库中没有当前记录，添加一条新纪录
                                MerchantNewsInfo merchantNewsInfo = new MerchantNewsInfo();
                                merchantNewsInfo.setHaveReadNews(newsInfo.getNewsId().toString());
                                merchantNewsInfo.setMerchantNum(sessionInfo.getMercId());
                                merchantNewsInfo.setOagentno(oAgentNo);
                                merchantNewsInfoDao.insert(merchantNewsInfo);
                            }

                            //返回数据
                            responseData.setH5URL(newsInfo.getH5Url());
                            responseData.setImagesURL(newsInfo.getIamgesUrl());
                            responseData.setNewsContent(newsInfo.getNewsContent());
                            responseData.setNewsDate(newsInfo.getNewsDate());
                            responseData.setNewsId(newsInfo.getNewsId().toString());
                            responseData.setNewsTitle(newsInfo.getNewsTitle());
                            responseData.setNewsType(newsInfo.getNewsType());
                            responseData.setOriginalAddr(newsInfo.getOrginalAddr());
                            responseData.setRead(String.valueOf(readed));
                            responseData.setRetCode(0);
                            responseData.setRetMessage("成功");
                            jsonString = createJsonString(responseData);
                            return jsonString;
                        }else{
                            //没有这条记录
                            responseData.setRetCode(1);
                            responseData.setRetMessage("没有这条记录");
                            jsonString = createJsonString(responseData);
                            return jsonString;
                        }


                    }else{
                        //参数不正确
                        responseData.setRetCode(1);
                        responseData.setRetMessage("参数不正确");
                        jsonString = createJsonString(responseData);
                        return jsonString;
                    }
                }else{
                    //解析失败
                    //参数不正确
                    responseData.setRetCode(1);
                    responseData.setRetMessage("解析失败");
                    jsonString = createJsonString(responseData);
                    return jsonString;
                }
            }
        }  else{

            //未登录
            responseData.setRetCode(13);
            responseData.setRetMessage("会话过期，请重新登陆");
            jsonString = createJsonString(responseData);
            return jsonString;
        }

    }

    /**
     * 0:有未读 1：没有未读
     * @param mercId
     * @return
     * @throws Exception
     */
    @Override
    public String haveUnReadMsg(String mercId,String oAgentNo) throws Exception {
        String result = "";
        if(StringUtils.isNotBlank(mercId) && StringUtils.isNotBlank(oAgentNo)){
           //获取当前欧单下所有的消息记录条数
           Integer count =  newsInfoDao.countAllByOagentNo(oAgentNo);
           //获取当前用户下的所有已读记录
           MerchantNewsInfo merchantNewsInfo = merchantNewsInfoDao.selectByMerc(mercId);
           if(merchantNewsInfo != null){
                //判断未读的条数
               String readedIds =  merchantNewsInfo.getHaveReadNews();
               if(StringUtils.isNotBlank(readedIds)){

                   String ids []  = merchantNewsInfo.getHaveReadNews().split(",");
                   //查询当前欧单下的所有消息id列表
                   List<String> newsIds = newsInfoDao.selectAllIdsByOagentNo(oAgentNo);
                   List<String> alreadyReadIds = Arrays.asList(ids);

                   if(alreadyReadIds.containsAll(newsIds)){
                     //读完
                       result = "1";
                   }else{
                       result = "0";
                   }


               }else{
                   //记录出错，默认有未读
                   result= "0";
               }

           }else{
               if(count == 0){
                   //没有当亲用户的记录 ，全部未读
                   result= "1";
               }else{
                   result= "0";
               }

           }
        }
        return result;
    }

    @Override
    public NewsInfo loginRemind(String oAgentNo){
        if(StringUtils.isBlank(oAgentNo)){
            return null;
        }
      return  newsInfoDao.selectLoginRemind(oAgentNo);
    }
}
