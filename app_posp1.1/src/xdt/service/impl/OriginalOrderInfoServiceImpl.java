package xdt.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import xdt.dao.OriginalOrderInfoDao;
import xdt.model.OriginalOrderInfo;
import xdt.service.OriginalOrderInfoService;

/**
 * @ClassName: OriginalOrderInfoServiceImpl
 * @Description: 恒丰快捷支付 下游 原始数据 业务 实现类
 * @author LiShiwen
 * @date 2016年6月20日 下午4:11:48
 *
 */
@Component
public class OriginalOrderInfoServiceImpl implements OriginalOrderInfoService {
	
	/**
	 * 原始记录信息
	 */
	@Resource
	private OriginalOrderInfoDao originalDao;

	@Override
	public int save(OriginalOrderInfo entity) throws Exception {
		return originalDao.insert(entity);
	}

	@Override
	public int update(OriginalOrderInfo entity) throws Exception {
		return originalDao.update(entity);
	}

	@Override
	public OriginalOrderInfo get(String orderId) throws Exception {
		return originalDao.searchById(orderId);
	}

}
