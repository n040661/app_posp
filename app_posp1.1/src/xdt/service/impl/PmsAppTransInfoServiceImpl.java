package xdt.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import xdt.dao.IPmsAppTransInfoDao;
import xdt.model.PmsAppTransInfo;
import xdt.service.IPmsAppTransInfoService;


/**
 * 订单信息 serviceImpl
 * wumeng 20150504
 */
@Service("pmsAppTransInfoService")
public class PmsAppTransInfoServiceImpl extends BaseServiceImpl implements IPmsAppTransInfoService {
	
	
	@Resource
	public IPmsAppTransInfoDao pmsAppTransInfoDao; // 业务配置服务层
	
	/**
	 * 修改订单是否已经修改过余额标记
	 * wumeng 20150504
	 * @throws Exception 
	 */
	@Override
	public int updateOrderAccountingFlag(String orderid) throws Exception {
		return pmsAppTransInfoDao.updateOrderAccountingFlag(orderid);
	}

	@Override
	public List<PmsAppTransInfo> selectOrderPmsAppTransInfo() throws Exception {
		// TODO Auto-generated method stub
		return pmsAppTransInfoDao.searchMyorder();
	}

	@Override
	public PmsAppTransInfo searchOrderInfo(String orderId) throws Exception {
		// TODO Auto-generated method stub
		return pmsAppTransInfoDao.searchOrderInfo(orderId);
	}
	
	
}