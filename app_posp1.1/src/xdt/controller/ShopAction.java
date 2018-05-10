package xdt.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import xdt.service.IPmsAddressService;
import xdt.service.IPmsGoodsService;
import xdt.service.IShopPayService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 商城
 * 
 * @author lev12
 * 
 */
@Controller
@RequestMapping("shopAction")
public class ShopAction extends BaseAction {
	
	private Logger logger = Logger.getLogger(ShopAction.class);

	@Resource
	private IPmsAddressService pmsAddressService;// 收货地址服务层
	@Resource
	private IPmsGoodsService pmsGoodsService;// 商品信息服务层
    @Resource
    IShopPayService shopPayService;
	
	/**
	 * 9.1	商品列表查看
	 */
	@RequestMapping("/queryGoodsList")
	public void queryGoodsList(HttpServletResponse response,
			HttpSession session, HttpServletRequest request) {
		try {
			String param = requestClient(request);
			String jsonString = pmsGoodsService.queryGoodsList(param, session);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(response, pmsGoodsService.queryGoodsListException());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session, request.getRemoteAddr(), false);
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}
	
	/**
	 * 9.2	收货地址列表查看
	 */
	@RequestMapping("/queryAddressList")
	public void queryAddressList(HttpServletResponse response,
			HttpSession session, HttpServletRequest request) {
		try {
			String jsonString = pmsAddressService.queryAddressList(session);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(response, pmsAddressService.queryAddressListException());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session, request.getRemoteAddr(), false);
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}
	
	/**
	 * 9.3	增加收货地址
	 */
	@RequestMapping("/addAddress")
	public void addAddress(HttpServletResponse response,
			HttpSession session, HttpServletRequest request) {
		String requestData = requestClient(request);
		try {
			String jsonString = pmsAddressService.addAddress(requestData, session, request);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(response, pmsAddressService.addAddressException());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session, request.getRemoteAddr(), false);
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}
	
	/**
	 * 9.4	按收货地址ID删除收货地址
	 */
	@RequestMapping("/deleteAddress")
	public void deleteAddress(HttpServletResponse response,
			HttpSession session, HttpServletRequest request) {
		String requestData = requestClient(request);
		try {
			String jsonString = pmsAddressService.delAddress(requestData, session, request);
			outPrint(response, jsonString);
		} catch (Exception e) {
			try {
				outPrint(response, pmsAddressService.delAddressException());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			setSession(session, request.getRemoteAddr(), false);
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}

    /**
     * 商城生成订单
     * @param response
     * @param session
     */
    @RequestMapping("/generateOrder")
    public synchronized void generateOrder(HttpServletResponse response,HttpSession session,HttpServletRequest request){
        String param = requestClient(request);
        try {
            outPrint(response, shopPayService.produceOrder(session,param));
        } catch (Exception e) {
            setSession(session,request.getRemoteAddr(),true);
            logger.info("[app_exception]"+e.fillInStackTrace());
            e.printStackTrace();
        }
    }
    /**
     * 商城快捷支付预消费
     * @param response
     * @param session
     */
    @RequestMapping("/shopOrderQuickPrePay")
    public synchronized void shopOrderQuickPrePay(HttpServletResponse response,HttpSession session,HttpServletRequest request){
        String param = requestClient(request);
        try {
            String json = shopPayService.shopOrderQuickPrePay(session,param);
            logger.info("返回的数据："+json);
            outPrint(response, json);
        } catch (Exception e) {
            setSession(session,request.getRemoteAddr(),true);
            logger.info("[app_exception]"+e.fillInStackTrace());
            e.printStackTrace();
        }
    }
    /**
     * 商城快捷支付验证
     * @param response
     * @param session
     */
    @RequestMapping("/shopOrderQuickPay")
    public synchronized void shopOrderQuickPay(HttpServletResponse response,HttpSession session,HttpServletRequest request){
        String param = requestClient(request);
        try {
            outPrint(response, shopPayService.shopOrderQuickPay(session,param));
        } catch (Exception e) {
            setSession(session,request.getRemoteAddr(),true);
            logger.info("[app_exception]"+e.fillInStackTrace());
            e.printStackTrace();
        }
    }
    /**
     * 商城快捷支付验证
     * @param response
     * @param session
     */
    @RequestMapping("/shopOrderPrePayReSendMsg")
    public synchronized void shopOrderPrePayReSendMsg(HttpServletResponse response,HttpSession session,HttpServletRequest request){
        String param = requestClient(request);
        try {
            outPrint(response, shopPayService.shopOrderPrePayReSendMsg(session,param));
        } catch (Exception e) {
            setSession(session,request.getRemoteAddr(),true);
            logger.info("[app_exception]"+e.fillInStackTrace());
            e.printStackTrace();
        }
    }


}