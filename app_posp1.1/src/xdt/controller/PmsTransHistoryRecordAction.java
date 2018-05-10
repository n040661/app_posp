package xdt.controller;

import xdt.controller.BaseAction;
import xdt.service.IPmsTransHistoryRecordService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 历史交易记录
 * @author Jeff
 *
 */
@Controller
@RequestMapping("pmsTransHistoryRecordAction")
public class PmsTransHistoryRecordAction extends BaseAction {
	
	@Resource
	private IPmsTransHistoryRecordService pmsTransHistoryRecordService;//历史交易记录服务层
	private Logger logger = Logger.getLogger(PmsTransHistoryRecordAction.class);
	
	/**
	 * 交易历史记录列表
	 * @param pageInfo
	 * @param response
	 * @param session
	 */
	@RequestMapping("/transHistoryRecordList")
	public void transHistoryRecordList(String pageInfo,HttpServletResponse response,HttpSession session,HttpServletRequest request){
        String param = requestClient(request);
        try {
			outPrint(response, pmsTransHistoryRecordService.searchTransHistoryRecordList(param,session));
		} catch (Exception e) {
			try {
				outPrint(response,pmsTransHistoryRecordService.searchTransHistoryRecordListException(session));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session,request.getRemoteAddr(),true);
			logger.info("[app_exception]"+e.fillInStackTrace());
			e.printStackTrace();
		}
	}


    /**
     * 检索交易可用次数
     * @param bankCardInfo
     * @param response
     * @param session
     */
    @RequestMapping("/getTransNumberOfAvailable")
    public void getTransNumberOfAvailable( String bankCardInfo,HttpServletResponse response,HttpSession session,HttpServletRequest request){
        String param = requestClient(request);
        try {
            outPrint(response, pmsTransHistoryRecordService.searchTransGetNumberOfAvailable(param,session,request));
        } catch (Exception e) {
            try {
                outPrint(response, pmsTransHistoryRecordService.searchTransGetNumberOfAvailableException(session));
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            setSession(session,request.getRemoteAddr(),true);
            logger.info("[app_exception]"+e.fillInStackTrace());
            e.printStackTrace();
        }
    }
}