package xdt.service;

import xdt.dto.BaiduBackRequestDTO;
import xdt.dto.MroducedTwoDimensionResponseDTO;
import xdt.dto.OffiBackRequestDTO;
import xdt.dto.PayCardResponseDTO;
import xdt.model.PmsAppTransInfo;
import xdt.model.SessionInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 商户收款 service
 * wumeng 20150506
 */
public interface IMerchantCollectMoneyService {
	
	
	/**
     * 调用异常统一处理
     * @author wumeng   20150508
     * @return result  返回前台json串
     */
    public String exceptUtility(HttpSession session) throws Exception;
	
	/**
	 * app请求  订单查询    确认支付成功
	 * wumeng  20150507
	 * @param param
	 * @param response
	 * @param session
	 */
	public String queryOrder(String param, HttpSession session,HttpServletRequest request)throws Exception;
	



    /**
     * 百度回调后的操作
     * @param baiduBackRequestDTO
     * @param response
     * @param session
     * @param request
     * @return
     */
    public Integer baiduCallBackHandel(BaiduBackRequestDTO baiduBackRequestDTO,HttpServletResponse response,HttpSession session,HttpServletRequest request) throws Exception ;


    /**
     * 欧飞回调后的操作
     * @param offiBackRequestDTO
     * @return
     */
    public Integer offiCallBackHandel(OffiBackRequestDTO offiBackRequestDTO) throws Exception ;

	

    /**
     * 百度订单查询
     * @param order_no
     * @param oAgentNo   O单编号
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryOrderForBD(String order_no,String oAgentNo)throws Exception;

    /**
     * 百度生成订单逻辑
     * @param baiduBackRequestDTO
     * @param appTransInfo
     * @return
     * @throws Exception
     */
    Integer baiduHandelOrder(BaiduBackRequestDTO baiduBackRequestDTO) throws Exception;

    /**
	 * 刷卡收款    第一步  生成订单
	 * wumeng  20150515
	 * @param param
	 * @param sessionInfo
	 */
	public PayCardResponseDTO insertOrderPay(String param, SessionInfo sessionInfo)throws Exception;
	/**
	 * 刷卡收款    第二步  确认订单并支付
	 * wumeng  20150515
	 * @param param
	 * @param sessionInfo
	 * @param pmsAppTransInfo
	 */
	public String submitOrderPay(String param, SessionInfo sessionInfo,PmsAppTransInfo  pmsAppTransInfo)throws Exception;
	
	
	
	 /**
     * 更新商户账户余额
     * @author wumeng   20150522
     * @param appTransInfo   余额
     * @throws Exception 
     */
	public int updateMerchantBalance(PmsAppTransInfo appTransInfo) throws Exception;
	
	 /**
	 * 生成二维码    JSON  or image 第一步
	 * wumeng  20150506
	 * @param param
	 * @param sessionInfo
	 */
	public MroducedTwoDimensionResponseDTO producedTwoDimension(String param, SessionInfo sessionInfo)throws Exception;
	
	/**
	 * 生成二维码    JSON  or image   目前仅支持json  第二步
	 * wumeng  20150506
	 * @param mroducedTwoDimensionResponseDTO
	 */
	public String producedTwoDimension(MroducedTwoDimensionResponseDTO mroducedTwoDimensionResponseDTO) throws Exception;
	/**
	 * 反扫    用户扫商户     第一步
	 * wumeng  20150508
	 * @param param
	 * @param sessionInfo
	 */
	public MroducedTwoDimensionResponseDTO producedScanCodeOrder(String param,SessionInfo sessionInfo)throws Exception;
	/**
	 * 反扫    用户扫商户  第二步 完成剩余操作
	 * wumeng  20150508
	 * @param mroducedTwoDimensionResponseDTO
	 */
	public String producedScanCodeOrder(MroducedTwoDimensionResponseDTO mroducedTwoDimensionResponseDTO)throws Exception;
	/**
     * 移动和包回调后的操作
     * @param yDHLBackRequestDTO
     * @param response
     * @param session
     * @param request
     */
	public Integer yDHBOrderCallBack(HttpServletResponse response, HttpSession session,HttpServletRequest request)throws Exception;
	/**
     * 移动和包线程处理 
     * @param orderid
	 * @throws Exception 
     */
	public Integer yDHBHandelOrder(String orderid) throws Exception;
	 /**
     * 讯联处理订单逻辑
     * @param order_no;//订单号
     * @param serialNo;//讯联批次号
     * @param merInfo;//商户编号
     * @param paymenttype;//查询区分微信（025）还是支付宝（015）
     * @param tradeTime;//讯联订单交易时间
     * @param searchNum;//讯联检索参考号
     * @return
     * @throws Exception
     */
	public Integer xLHandelOrder(String orderid,String serialNo,String merInfo,String paymenttype,String tradeTime,String searchNum) throws Exception ;

	 /**
	 * 刷卡收款    第一步  生成订单  O单类型      是T0(清算手续费固定    附加费)
	 * wumeng  20160219
	 * @param param
	 * @param sessionInfo
	 */
	public PayCardResponseDTO insertOrderPayFor0(String param, SessionInfo sessionInfo)throws Exception;
	/**
	 * 刷卡收款    第二步  确认订单并支付   O单类型      是T0(清算手续费固定    附加费)
	 * wumeng  20160219
	 * @param param
	 * @param sessionInfo
	 * @param pmsAppTransInfo
	 */
	public String submitOrderPayFor0(String param, SessionInfo sessionInfo,PmsAppTransInfo  pmsAppTransInfo)throws Exception;
	
	
	 /**
	 * 刷卡收款    第一步  生成订单  O单类型      是T0(清算手续费百分比算)
	 * wumeng  20160511
	 * @param param
	 * @param sessionInfo
	 */
	public PayCardResponseDTO insertOrderPayFor0Settle(String param, SessionInfo sessionInfo)throws Exception;
	/**
	 * 刷卡收款    第二步  确认订单并支付   O单类型      是T0(清算手续费百分比算)
	 * wumeng  20160511
	 * @param param
	 * @param sessionInfo
	 * @param pmsAppTransInfo
	 */
	public String submitOrderPayFor0Settle(String param, SessionInfo sessionInfo,PmsAppTransInfo  pmsAppTransInfo)throws Exception;
	
	
}
