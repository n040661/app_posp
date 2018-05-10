package xdt.schedule;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dto.BaiduBackRequestDTO;
import xdt.model.PmsAppTransInfo;
import xdt.model.PospTransInfo;
import xdt.offi.OffiPay;
import xdt.quickpay.hengfeng.entity.PayQueryRequestEntity;
import xdt.quickpay.mobao.MobaoPayHandel;
import xdt.quickpay.mobao.MobaoTransSearchResponseDto;
import xdt.service.BeenQuickPayService;
import xdt.service.HfQuickPayService;
import xdt.service.IMerchantCollectMoneyService;
import xdt.service.IShopPayService;
import xdt.service.PufaService;
import xdt.service.impl.BaseServiceImpl;
import xdt.service.impl.PufaServiceImpl;
import xdt.util.OrderStatusEnum;
import xdt.util.UtilDate;

/**
 * 需要处理的订单订单批量处理线程 User: Jeff Date: 15-5-13 Time: 下午2:07 To change this template
 * use File | Settings | File Templates.
 */
@Component("orderBatchUpdateInitThread")
public class OrderBatchUpdateInitThread implements Runnable {

	// 默认4分钟
	private Long period = 4 * 60 * 1000L;
	// 项目启动前睡眠30秒
	private Long startPeriod = 30 * 1000L;

	private static final String preLogger = "4分钟轮询线程    ";

	@Resource
	private IPmsAppTransInfoDao pmsAppTransInfoDao; // 订单DAO访问
	@Resource
	private IMerchantCollectMoneyService merchantCollectMoneyService;// 百度
																		// service
	@Resource
	private OffiPay offiPay; // 欧飞
	@Resource
	private IPospTransInfoDAO pospTransInfoDAO;// 流水
	@Resource
	private BaseServiceImpl baseService;
	@Resource
	MobaoPayHandel mobaoPayHandel;
	@Resource
	IShopPayService shopPayService;
	/**
	 * 恒丰快捷支付
	 */
	@Resource
	private HfQuickPayService payService;

	/**
	 * 浦发扫码生成码
	 */
	@Resource
	private PufaService pufaService;

	/**
	 * 
	 */
	@Resource
	private BeenQuickPayService beenQuickPayService;
	
	private Logger logger = Logger.getLogger(OrderBatchUpdateInitThread.class);

	public OrderBatchUpdateInitThread() {

	}

	@Override
	public void run() {
		logger.info(preLogger + "启动...");
		while (true) {
			try {
				Thread.sleep(startPeriod);
				process();
				Thread.sleep(period);
			} catch (Exception e) {
				logger.info("详情：" + e.getMessage());
			}
		}
	}

	/**
	 * 处理方法
	 */
	private void process() {
		try {
			// 获取当前，非5分钟内的，并且状态在（200,2,4,5）之中的数据
			List<PmsAppTransInfo> pmsAppTransInfos = pmsAppTransInfoDao
					.searchNeedCallBackList();
			logger.info(preLogger + "获取需要处理的列表（200,2,4,5），共获取数据："
					+ pmsAppTransInfos.size() + "条");
			if (pmsAppTransInfos != null && pmsAppTransInfos.size() > 0) {
				// 对这些数据进行分类处理
				int num = 0;
				for (PmsAppTransInfo pmsAppTransInfo : pmsAppTransInfos) {
					num++;
					logger.info(preLogger + "开始处理第" + num + "条数据" + ",订单号："
							+ pmsAppTransInfo.getOrderid() + ",状态值："
							+ pmsAppTransInfo.getStatus());
					dispathHandle(pmsAppTransInfo);
				}
			}

		} catch (Exception e) {
			logger.info("详情：" + e.getMessage());

		}
	}

	/**
	 * 分发处理
	 * 
	 * @param pmsAppTransInfo
	 */
	private void dispathHandle(PmsAppTransInfo pmsAppTransInfo) {

		String statuStr = pmsAppTransInfo.getStatus();
		if (StringUtils.isBlank(statuStr)) {
			// 初始化数据
			statuNullHandel(pmsAppTransInfo);
		} else if (StringUtils.isNumeric(statuStr)) {

			Integer status = Integer.parseInt(statuStr);
			if (status.equals(OrderStatusEnum.initlize.getIntStatus())) {
				// 初始化数据
				statuNullHandel(pmsAppTransInfo);
			} else if (status.equals(OrderStatusEnum.waitingClientPay
					.getIntStatus())) {
				// 等待平台（微信，百度....）支付
				statu2Handel(pmsAppTransInfo);
			} else if (status.equals(OrderStatusEnum.waitingPlantPay
					.getIntStatus())) {
				// 客户端支付成功，等待服务器调用第三方支付平台(欧飞)支付
				statu4Handel(pmsAppTransInfo);
			} else if (status.equals(OrderStatusEnum.plantPayingNow
					.getIntStatus())) {
				// 第三方平台正在支付
				statu5Handel(pmsAppTransInfo);
			} else {
				logger.info("分发订单状态失败，状态不存在：" + statuStr);
			}

		}

	}

	/**
	 * status为200的处理方法
	 * 
	 * @param pmsAppTransInfo
	 */
	private synchronized void statuNullHandel(PmsAppTransInfo pmsAppTransInfo) {
		// 初始状态需要判断当前的平台
		String plantStr = pmsAppTransInfo.getPaymentcode();
		if (StringUtils.isNumeric(plantStr)) {
			Integer plant = Integer.parseInt(plantStr);
			switch (plant) {
			case 1:// 账号支付
				break;
			case 2:// 百度支付
					// 调用接口查询百度服务器当前订单状态 并作后续处理
				baiduHandelStatus(pmsAppTransInfo);
				break;
			case 3: // 微信支付:
				xLHandelStatus(pmsAppTransInfo);
				break;
			case 4: // 支付宝支付
				xLHandelStatus(pmsAppTransInfo);
				break;
			case 6: // 移动和包
				yDHBHandelStatus(pmsAppTransInfo.getOrderid());
				break;
			case 11: // 恒丰快捷支付
				hengFengHandelStatus(pmsAppTransInfo);
				break;
			case 12: //
				pufaHandleStatus(pmsAppTransInfo);
				break;
			case 13: //
				bcCloudQuickHandleStatus(pmsAppTransInfo);
				break;
			default:
				break;
			}
		} else {
			// 只是生成了订单，并没有做支付操作，这里的支付方式会是空
			// 校验当前订单的交易时长是否在30分钟内，如果超时，默认交易失败

		}
	}
	/**
	 * 
	 * @Description Bc快捷支付 
	 * @author Administrator
	 * @param pmsAppTransInfo
	 */
	private void bcCloudQuickHandleStatus(PmsAppTransInfo pmsAppTransInfo) {
		logger.info("Bc快捷支付 订单状态定时任务" + pmsAppTransInfo);
		try {
			beenQuickPayService.updateOrderStatusByOrder(pmsAppTransInfo);
		} catch (Exception e) {
			logger.info("Bc快捷支付 订单状态失败",e);
			e.printStackTrace();
		}
		
		
	}

	/**
	 * status为2的处理方法
	 * 
	 * @param pmsAppTransInfo
	 */
	private synchronized void statu2Handel(PmsAppTransInfo pmsAppTransInfo) {
		// 状态2需要判断当前的平台
		String plantStr = pmsAppTransInfo.getPaymentcode();
		if (StringUtils.isNumeric(plantStr)) {
			Integer plant = Integer.parseInt(plantStr);
			switch (plant) {
			case 1:// 账号支付
				break;
			case 2:// 百度支付
					// 调用接口查询百度服务器当前订单状态 并作后续处理
				baiduHandelStatus(pmsAppTransInfo);
				break;
			case 3: // 微信支付:
				xLHandelStatus(pmsAppTransInfo);
				break;
			case 4: // 支付宝支付
				xLHandelStatus(pmsAppTransInfo);
				break;
			case 10: // 摩宝快捷支付
				mobaoHandelStatus(pmsAppTransInfo);
				break;
			case 11: // 恒丰快捷支付
				hengFengHandelStatus(pmsAppTransInfo);
				break;
			case 12: //
				pufaHandleStatus(pmsAppTransInfo);
				break;
			case 13: //
				bcCloudQuickHandleStatus(pmsAppTransInfo);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 处理扫码订单状态
	 * 
	 * @Description
	 * @author Administrator
	 * @param pmsAppTransInfo
	 */
	private void pufaHandleStatus(PmsAppTransInfo pmsAppTransInfo) {

		logger.info("处理扫码订单状态定时任务" + pmsAppTransInfo);
		try {
			pufaService.updateOrderStatusByOrder(pmsAppTransInfo);
		} catch (Exception e) {
			logger.info("处理扫码订单状态失败",e);
			e.printStackTrace();
		}
	}

	/**
	 * 处理当前订单状态 根据订单查询第三 方支付状态修改订单流水状态
	 * 
	 * @param pmsAppTransInfo
	 *            订单信息
	 */
	private void hengFengHandelStatus(PmsAppTransInfo pmsAppTransInfo) {
		// 查询支付结果信息
		PayQueryRequestEntity queryInfo = new PayQueryRequestEntity();
		queryInfo.setMerId(pmsAppTransInfo.getMercid());
		queryInfo.setTransactionId(pmsAppTransInfo.getOrderid());
		logger.info("hfquick:" + payService);
		// 查询并处理状态
		try {
			payService.queryPayResultHandle(queryInfo);
		} catch (Exception e) {
			logger.error("修改状态失败:" + e);
		}

	}

	/**
	 * status为4（客户端付款成功，等待调用欧飞完成支付）的处理方法
	 * 
	 * @param pmsAppTransInfo
	 */
	private synchronized void statu4Handel(PmsAppTransInfo pmsAppTransInfo) {
		if (pmsAppTransInfo != null
				&& StringUtils.isNotBlank(pmsAppTransInfo.getPortorderid())
				&& StringUtils.isNotBlank(pmsAppTransInfo.getOrderid())
				&& StringUtils.isNumeric(pmsAppTransInfo.getTradetypecode())) {
			// 1 商户收款、2 转账汇款、3 信用卡还款、4手机充值、5 水煤电、 6 加油卡充值、
			Integer tradetypecode = Integer.parseInt(pmsAppTransInfo
					.getTradetypecode());

			if (tradetypecode == 1) {
				// 处理商户收款
			} else if (tradetypecode == 2) {
				// 处理转账
			} else if (tradetypecode == 3) {
				// 信用卡还款
			} else if (tradetypecode == 4) {
				// 手机充值
				offiPayPhoneStatu4(pmsAppTransInfo);
			} else if (tradetypecode == 5) {
				// 水没电
				offiUtilityStatu4(pmsAppTransInfo);
			} else if (tradetypecode == 6) {
				// 加油卡充值
				offiSinopecStatu4(pmsAppTransInfo);
			}
		}
	}

	/**
	 * status为5（欧飞正在支付）处理方法
	 * 
	 * @param pmsAppTransInfo
	 */
	private synchronized void statu5Handel(PmsAppTransInfo pmsAppTransInfo) {
		if (pmsAppTransInfo != null
				&& StringUtils.isNotBlank(pmsAppTransInfo.getOrderid())) {
			// 获取欧飞查询渠道
			Integer result = offiPay.queryOrderStatus(pmsAppTransInfo
					.getOrderid());
			if (result == 1) {
				// 成功支付，更新订单状态
				pmsAppTransInfo.setThirdPartResultCode(result.toString());
				pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
				pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess
						.getStatus());
				try {
					pmsAppTransInfoDao.update(pmsAppTransInfo);
				} catch (Exception e) {
					logger.info("更新订单状态失败（5->0），订单号："
							+ pmsAppTransInfo.getOrderid());
				}

				// 支付成功，插入流水表
				if (result == 1) {
					// 查看流水表中是否有当前记录
					PospTransInfo pospTransInfo = pospTransInfoDAO
							.searchByOrderId(pmsAppTransInfo.getOrderid());
					if (pospTransInfo != null) {
						// 存在，不操作
						logger.info(preLogger + "已经存在该流水，orderId="
								+ pospTransInfo.getOrderId());
					} else {
						// 不存在，生成并添加
						try {
							pospTransInfo = baseService
									.generateTransFromAppTrans(pmsAppTransInfo);
							if (pospTransInfo != null) {
								pospTransInfoDAO.insert(pospTransInfo);
							}
						} catch (Exception e) {
							logger.info(preLogger + "生成流水失败，orderId="
									+ pospTransInfo.getOrderId());
							e.printStackTrace();
						}

					}
				}

			} else if (result == 0) {
				// 充值中,不做处理
				logger.info("调用欧飞接口正在充值，订单号：" + pmsAppTransInfo.getOrderid());
			} else if (result == 9) {
				// 充值失败 不做处理
				logger.info("调用欧飞接口没有找到该订单，订单号：" + pmsAppTransInfo.getOrderid());
			} else if (result == -1) {
				// 找不到订单 不做处理
				logger.info("调用欧飞接口没有找到该订单，订单号：" + pmsAppTransInfo.getOrderid());
			}
		}
	}

	/**
	 * 调用接口查询百度服务器当前订单状态 并作后续处理
	 * 
	 * @param pmsAppTransInfo
	 */
	private void baiduHandelStatus(PmsAppTransInfo pmsAppTransInfo) {
		try {

			BaiduBackRequestDTO baiduBackRequestDTO = new BaiduBackRequestDTO();
			baiduBackRequestDTO.setOrder_no(pmsAppTransInfo.getOrderid());
			// 调用百度的处理方法
			logger.info(preLogger + "开始调用百度的处理逻辑，订单号："
					+ pmsAppTransInfo.getOrderid());
			Integer result = merchantCollectMoneyService
					.baiduHandelOrder(baiduBackRequestDTO);
			if (result != null && result.equals(1)) {
				logger.info(preLogger + "订单号：" + pmsAppTransInfo.getOrderid()
						+ "调用百度的处理方法成功");
			}

		} catch (Exception e) {
			logger.info("调用百度订单查询接口调用结束， 订单号：" + pmsAppTransInfo.getOrderid()
					+ "，结束时间：" + UtilDate.getDateFormatter() + ",详情："
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 调用接口查询移动和包服务器当前订单状态 并作后续处理
	 * 
	 * @param orderid
	 */
	private void yDHBHandelStatus(String orderid) {
		try {

			// 调用移动和包的处理方法
			logger.info(preLogger + "开始调用移动和包的处理逻辑，订单号：" + orderid);

			Integer result = merchantCollectMoneyService
					.yDHBHandelOrder(orderid);

			if (result != null && result.equals(1)) {
				logger.info(preLogger + "订单号：" + orderid + "调用移动和包的处理方法成功");
			}

		} catch (Exception e) {
			logger.info("调用移动和包订单查询接口调用结束， 订单号：" + orderid + "，结束时间："
					+ UtilDate.getDateFormatter() + ",详情：" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 讯联处理订单逻辑
	 * 
	 * @return
	 * @throws Exception
	 */
	private void xLHandelStatus(PmsAppTransInfo pmsAppTransInfo) {

		String orderid = pmsAppTransInfo.getOrderid();
		try {
			if (StringUtils.isNotBlank(pmsAppTransInfo.getSerialNo())
					&& StringUtils.isNotBlank(pmsAppTransInfo.getMercid())
					&& StringUtils.isNotBlank(pmsAppTransInfo.getPaymentcode())
					&& StringUtils.isNotBlank(pmsAppTransInfo.getTradetime())
					&& StringUtils.isNotBlank(pmsAppTransInfo.getSearchNum())) {
				// 调用讯联的处理方法
				logger.info(preLogger + "开始调用讯联的处理逻辑，订单号：" + orderid);
				String paymenttype = "";
				// 3 微信支付、4 支付宝支付
				if ("3".equals(pmsAppTransInfo.getPaymentcode())) {
					paymenttype = "025";
				} else {
					paymenttype = "015";
				}

				Integer result = merchantCollectMoneyService.xLHandelOrder(
						orderid, pmsAppTransInfo.getSerialNo(),
						pmsAppTransInfo.getMercid(), paymenttype,
						pmsAppTransInfo.getTradetime(),
						pmsAppTransInfo.getSearchNum());

				if (result != null && result.equals(1)) {
					logger.info(preLogger + "订单号：" + orderid + "调用讯联的处理方法成功");
				}
			}

		} catch (Exception e) {
			logger.info("调用讯联订单查询接口调用结束， 订单号：" + orderid + "，结束时间："
					+ UtilDate.getDateFormatter() + ",详情：" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 摩宝处理订单逻辑
	 * 
	 * @return
	 * @throws Exception
	 */
	private void mobaoHandelStatus(PmsAppTransInfo pmsAppTransInfo) {

		String orderid = pmsAppTransInfo.getOrderid();
		try {
			if (StringUtils.isNotBlank(pmsAppTransInfo.getMercid())
					&& StringUtils.isNotBlank(pmsAppTransInfo.getPaymentcode())
					&& StringUtils.isNotBlank(pmsAppTransInfo.getTradetime())
					&& StringUtils.isNotBlank(pmsAppTransInfo.getPortorderid())) {
				logger.info(preLogger + "开始调用摩宝的处理逻辑，订单号：" + orderid);
				// 根据订单号查询流水
				PospTransInfo pospTransInfo = pospTransInfoDAO
						.searchByOrderId(pmsAppTransInfo.getOrderid());
				if (pospTransInfo != null) {
					MobaoTransSearchResponseDto result = mobaoPayHandel
							.transSearch(pmsAppTransInfo, pospTransInfo);
					if (result != null) {
						if (result.getRefCode().equals("00")) {
							// 处理成功 更新订单和流水
							pmsAppTransInfo.setStatus("0");
							pmsAppTransInfo.setFinishtime(UtilDate
									.getDateFormatter());
							pmsAppTransInfoDao.update(pmsAppTransInfo);

							if (pospTransInfo != null) {
								pospTransInfo.setResponsecode("00");
								pospTransInfoDAO.updateByOrderId(pospTransInfo);
								// 修改商户余额
								int resultUpdateMercBalance = shopPayService
										.updateMerchantBanlance(pmsAppTransInfo);
								if (resultUpdateMercBalance != 1) {
									logger.info("商户余额更新失败，orderId="
											+ pmsAppTransInfo.getOrderid());
								}
							}

						} else if (result.getRefCode().equals("03")) {
							// 交易正在进行中
						} else if (result.getRefCode().equals("02")) {
							// 交易失败
							pmsAppTransInfo.setStatus(OrderStatusEnum.payFail
									.getStatus());
							pmsAppTransInfoDao.update(pmsAppTransInfo);
							if (pospTransInfo != null) {
								pospTransInfo.setResponsecode("99");
								pospTransInfoDAO.updateByOrderId(pospTransInfo);
							}
						} else if (result.getRefCode().equals("040000")) {
							// 摩宝没有此交易，交易没有支付 ，当作失败处理
							// 交易失败
							pmsAppTransInfo.setStatus(OrderStatusEnum.payFail
									.getStatus());
							pmsAppTransInfoDao.update(pmsAppTransInfo);
							if (pospTransInfo != null) {
								pospTransInfo.setResponsecode("040000");
								pospTransInfoDAO.updateByOrderId(pospTransInfo);
							}
						}
						logger.info(preLogger + "订单号：" + orderid
								+ "调用摩宝的查询接口成功");
					}
				}
			}
		} catch (Exception e) {
			logger.info("调用讯联订单查询接口调用结束， 订单号：" + orderid + "，结束时间："
					+ UtilDate.getDateFormatter() + ",详情：" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 欧飞处理状态是4的手机支付
	 * 
	 * @param pmsAppTransInfo
	 */
	private void offiPayPhoneStatu4(PmsAppTransInfo pmsAppTransInfo) {
		// 调用欧飞付款
		Integer resultOffi = offiPay.mobilePay(pmsAppTransInfo);
		try {
			if (resultOffi == 1) {// 支付成功
				// 支付成功，修改订单状态
				pmsAppTransInfo.setThirdPartResultCode(resultOffi.toString());
				pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess
						.getStatus());
				pmsAppTransInfoDao.update(pmsAppTransInfo);
				// 修改流水
				PospTransInfo pospTransInfo = pospTransInfoDAO
						.searchByOrderId(pmsAppTransInfo.getOrderid());
				pospTransInfo.setResponsecode("0000");
				pospTransInfoDAO.updateByOrderId(pospTransInfo);

			} else if (resultOffi == 2) { // 正在支付，将状态改为正在支付
				pmsAppTransInfo.setThirdPartResultCode(resultOffi.toString());
				pmsAppTransInfo.setStatus(OrderStatusEnum.plantPayingNow
						.getStatus());
				pmsAppTransInfoDao.update(pmsAppTransInfo);
			}
		} catch (Exception e) {
			logger.error("status为4（客户端付款成功，等待调用欧飞完成支付）的处理方法失败：orderid:"
					+ pmsAppTransInfo.getOrderid());
		}
	}

	/**
	 * 欧飞处理状态4的水煤电
	 * 
	 * @param pmsAppTransInfo
	 */
	private void offiUtilityStatu4(PmsAppTransInfo pmsAppTransInfo) {
		// 调用欧飞付款
		Integer resultOffi = offiPay.utilityOrder(pmsAppTransInfo);
		try {
			if (resultOffi == 1) {
				// 支付成功，修改订单状态
				pmsAppTransInfo.setThirdPartResultCode(resultOffi.toString());
				pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess
						.getStatus());
				pmsAppTransInfoDao.update(pmsAppTransInfo);
			}
		} catch (Exception e) {
			logger.error("status为4（客户端付款成功，等待调用欧飞完成支付）的处理方法失败：orderid:"
					+ pmsAppTransInfo.getOrderid());
		}
	}

	/**
	 * 欧飞处理状态4的加油卡
	 * 
	 * @param pmsAppTransInfo
	 */
	private void offiSinopecStatu4(PmsAppTransInfo pmsAppTransInfo) {
		// 调用欧飞付款
		Integer resultOffi = offiPay.sinopecOrder(pmsAppTransInfo);
		try {
			if (resultOffi == 1) {
				// 支付成功，修改订单状态
				pmsAppTransInfo.setThirdPartResultCode(resultOffi.toString());
				pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess
						.getStatus());
				pmsAppTransInfoDao.update(pmsAppTransInfo);
			}
		} catch (Exception e) {
			logger.error("status为4（客户端付款成功，等待调用欧飞完成支付）的处理方法失败：orderid:"
					+ pmsAppTransInfo.getOrderid());
		}
	}

}
