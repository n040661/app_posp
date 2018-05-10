package xdt.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;
import xdt.controller.HFQPayAction;
import xdt.controller.jsds.JsdsQrCodeAction;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dao.OriginalOrderInfoDao;
import xdt.dto.jsds.JsdsRequestDto;
import xdt.dto.jsds.JsdsResponseDto;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PospTransInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hengfeng.util.HttpClientUtil;
import xdt.quickpay.qianlong.model.PayResponseEntity;
import xdt.quickpay.qianlong.util.QLPostThread;
import xdt.schedule.ThreadPool;
import xdt.service.JsdsQrCodeService;
import xdt.util.HttpURLConection;

public class JsdsPostThread extends Thread {

	public static final Logger logger = Logger.getLogger(JsdsPostThread.class);

	public JsdsRequestDto entity;

	PmsBusinessPos busInfo;

	public JsdsQrCodeService jsds;

	public IPmsAppTransInfoDao pmsAppTransInfoDao;

	public IPospTransInfoDAO pospTransInfoDAO;
	public OriginalOrderInfoDao originalDao;

	public JsdsPostThread(JsdsRequestDto entity, PmsBusinessPos busInfo, JsdsQrCodeService jsds,
			IPmsAppTransInfoDao pmsAppTransInfoDao, IPospTransInfoDAO pospTransInfoDAO,
			OriginalOrderInfoDao originalDao) {
		super();
		this.entity = entity;
		this.busInfo = busInfo;
		this.jsds = jsds;
		this.pmsAppTransInfoDao = pmsAppTransInfoDao;
		this.pospTransInfoDAO = pospTransInfoDAO;
		this.originalDao = originalDao;
	}

	@Override
	public synchronized void run() {

		// 线程处理
		// 1、先查询本地库订单是否是完成状态
		// 2、如果是完成状态跳过第三方查询并结束； 否则进行第三方查询 分隔时间进行查询在进行下一步处理
		// 3、第三方查询结果后根据结果处理本地订单并结束

		try {
			Thread.sleep(2000);
			for (int i = 0; i < 1000; i++) {
				logger.info("进入线程中");
				Map<String, String> result = new HashMap<String, String>();

				// 流水表transOrderId
				String transOrderId = entity.getOrderNum();
				logger.info("线程中的订单:" + transOrderId);
				OriginalOrderInfo orig = jsds.getOriginOrderInfo(transOrderId);

				// 流水信息
				PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(transOrderId);
				logger.info("流水表信息" + pospTransInfo);
				// 订单信息
				PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
				logger.info("订单表信息" + pmsAppTransInfo);
				if ("0".equals(pmsAppTransInfo.getStatus())) {
					Thread.interrupted();
				} else {
					result = jsds.handleOrder(pmsAppTransInfo);
					if ("4".equals(result.get("payStatus"))) {

						Calendar cal1 = Calendar.getInstance();
						TimeZone.setDefault(TimeZone.getTimeZone("GMT+8:00"));
						java.text.SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss");

						if (sdf.parse(sdf.format(cal1.getTime())).getTime() > sdf.parse("08:00:00").getTime()
								&& sdf.parse(sdf.format(cal1.getTime())).getTime() < sdf.parse("22:30:00").getTime()) {
							logger.info("D0订单号:" + entity.getOrderNum());
							if ("105962".equals(busInfo.getDepartmentnum())) {
								
								jsds.UpdatePmsMerchantInfo449(orig);
								
							} else if ("107382".equals(busInfo.getDepartmentnum())) {
								
								jsds.UpdatePmsMerchantInfo(orig);
								
							}
						}
						// 支付成功
						pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
//						pmsAppTransInfo.setThirdPartResultCode(result.get("pl_payState").toString());
						pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
						// 修改订单
						int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
						if (updateAppTrans == 1) {
							// log.info("修改余额");
							// 修改余额
							logger.info(pmsAppTransInfo);
							// updateMerchantBanlance(pmsAppTransInfo);
							// 更新流水表
							pospTransInfo.setResponsecode("00");
							pospTransInfo.setPospsn(transOrderId);
							logger.info("更新流水");
							logger.info(pospTransInfo);
							pospTransInfoDAO.updateByOrderId(pospTransInfo);
							Thread.interrupted();
						}
					} else if ("2".equals(result.get("payStatus"))){
						pmsAppTransInfo.setStatus(OrderStatusEnum.waitingClientPay.getStatus());
//						pmsAppTransInfo.setThirdPartResultCode(result.get("pl_payState").toString());
						pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
						// 修改订单
						int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
						if (updateAppTrans == 1) {
							// 更新流水表
							pospTransInfo.setResponsecode("20");
							pospTransInfo.setPospsn(transOrderId);
							logger.info("更新流水");
							logger.info(pospTransInfo);
							pospTransInfoDAO.updateByOrderId(pospTransInfo);
						}
					}else{
						pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
//						pmsAppTransInfo.setThirdPartResultCode(result.get("pl_payState").toString());
						pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
						// 修改订单
						int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
						if (updateAppTrans == 1) {
							// 更新流水表
							pospTransInfo.setResponsecode("02");
							pospTransInfo.setPospsn(transOrderId);
							logger.info("更新流水");
							logger.info(pospTransInfo);
							pospTransInfoDAO.updateByOrderId(pospTransInfo);
						}
					}

				}
				// Thread.sleep(5000);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
