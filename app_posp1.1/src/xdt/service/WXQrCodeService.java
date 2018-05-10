
/**   
 * @Title: WXQrCodeService.java 
 * @Package: xdt.service 
 * @Description: TODO
 * @author Shiwen . Li
 * @date 2016年12月3日 下午2:59:46 
 * @version 1.3.1 
 */


package xdt.service;

import java.util.Map;

import xdt.dto.weixin.CallbackDto;
import xdt.dto.weixin.ChangeRateDto;
import xdt.dto.weixin.PayRequestDto;
import xdt.dto.weixin.QueryRequestDto;
import xdt.dto.weixin.RegisterDto;
import xdt.dto.weixin.VerifyInfoDto;

/** 
 * @Description 
 * @author Shiwen .Li
 * @date 2016年12月3日 下午2:59:46 
 * @version V1.3.1
 */

public interface WXQrCodeService {

	/**
	 * 
	 * @Description 生成微信二维码 
	 * @param req
	 * @return
	 * @throws Exception 
	 */
	Map<String, String> updatePay(PayRequestDto req) throws Exception;
	
	/**
	 * 
	 * @Description 查询二维码订单结果 
	 * @author Administrator
	 * @param req
	 * @return
	 * @throws Exception 
	 */
	Map<String, String> updateQuery(QueryRequestDto req) throws Exception;
	/**
	 * 
	 * @Description 下载私钥 
	 * @author Administrator
	 * @param req
	 * @return
	 * @throws Exception 
	 */
	Map<String, String> updateDownkey(RegisterDto req) throws Exception;

	/**
	 * 
	 * @Description 注册商户 
	 * @author Administrator
	 * @param req
	 * @return
	 * @throws Exception 
	 */
	Map<String, String> updateRegister(RegisterDto req) throws Exception;
	/**
	 * 
	 * @Description 校验身份 银行卡信息 
	 * @author Administrator
	 * @param info
	 * @return
	 * @throws Exception 
	 */
	Map<String, String> updateValidator(VerifyInfoDto info) throws Exception;
	/**
	 * 
	 * @Description 同步费率 
	 * @author Administrator
	 * @param info
	 * @return
	 * @throws Exception 
	 */
	Map<String, String> updateRate(ChangeRateDto info) throws Exception;
	/**
	 * 
	 * @Description 回调处理订单状态 
	 * @author Administrator
	 * @param callback
	 * @return
	 * @throws Exception 
	 */
	Map<String, String> updateHandleOrder(CallbackDto callback) throws Exception;

	Map<String, String> queryOrderInfo(QueryRequestDto req) throws Exception;

}
