package xdt.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import xdt.mapper.PmsWeixinMerchartInfoMapper;
import xdt.model.PmsWeixinMerchartInfo;
import xdt.service.PmsWeixinMerchartInfoService;

@Service
public class PmsWeixinMerchartInfoServiceImpl implements
		PmsWeixinMerchartInfoService {

	@Resource
	private PmsWeixinMerchartInfoMapper weixinMerchMapper;

	private Logger logger = Logger
			.getLogger(PmsWeixinMerchartInfoServiceImpl.class);

	/**
	 * Description
	 * 
	 * @param account
	 * @return
	 * @see xdt.service.PmsWeixinMerchartInfoService#selectByPrimaryKey(java.lang.String)
	 */

	@Override
	public PmsWeixinMerchartInfo selectByPrimaryKey(String account) {
		logger.info("查询微信商户");
		return weixinMerchMapper.selectByPrimaryKey(account);
	}

	/**
	 * Description
	 * 
	 * @param model
	 * @return
	 * @see xdt.service.PmsWeixinMerchartInfoService#insert(xdt.model.PmsWeixinMerchartInfo)
	 */

	@Override
	public int insert(PmsWeixinMerchartInfo model) {
		logger.info("插入微信商户" + model);
		model.setCreateDate(new Date());
		return weixinMerchMapper.insert(model);
	}

	/**
	 * Description
	 * 
	 * @param model
	 * @return
	 * @see xdt.service.PmsWeixinMerchartInfoService#insertSelective(xdt.model.PmsWeixinMerchartInfo)
	 */

	@Override
	public int insertSelective(PmsWeixinMerchartInfo model) {
		logger.info("动态插入微信商户" + model);
		model.setCreateDate(new Date());
		return weixinMerchMapper.insertSelective(model);
	}

	/**
	 * Description
	 * 
	 * @param model
	 * @return
	 * @see xdt.service.PmsWeixinMerchartInfoService#updateByPrimaryKeySelective(xdt.model.PmsWeixinMerchartInfo)
	 */

	@Override
	public int updateByPrimaryKeySelective(PmsWeixinMerchartInfo model) {
		logger.info("动态修改微信商户" + model);
		model.setUpdateDate(new Date());
		return weixinMerchMapper.updateByPrimaryKeySelective(model);
	}

	/**
	 * Description
	 * 
	 * @param model
	 * @return
	 * @see xdt.service.PmsWeixinMerchartInfoService#updateByPrimaryKey(xdt.model.PmsWeixinMerchartInfo)
	 */

	@Override
	public int updateByPrimaryKey(PmsWeixinMerchartInfo model) {
		logger.info("修改微信商户" + model);
		model.setUpdateDate(new Date());
		return weixinMerchMapper.updateByPrimaryKey(model);
	}

	@Override
	public int updateRegister(PmsWeixinMerchartInfo model) {
		logger.info("注册微信商户" + model);
		return this.insertSelective(model);
	}

	@Override
	public PmsWeixinMerchartInfo selectByEntity(PmsWeixinMerchartInfo model) {
		logger.info("查询微信商户是否存在" + model);
		return weixinMerchMapper.selectByEntity(model);
	}

	@Override
	public List<PmsWeixinMerchartInfo> selectlist(PmsWeixinMerchartInfo model) {
		// TODO Auto-generated method stub
		return weixinMerchMapper.selectList(model);
	}
	public PmsWeixinMerchartInfo selectByCardEntity(PmsWeixinMerchartInfo model) {
		logger.info("查询微信商户是否存在" + model);
		return weixinMerchMapper.selectByCardEntity(model);
	}

	@Override
	public int updataPay(Map<String, String> map) {
		// TODO Auto-generated method stub
		return weixinMerchMapper.updataPay(map);
	}

	@Override
	public int updataPayT1(Map<String, String> map) {
		// TODO Auto-generated method stub
		return weixinMerchMapper.updataPayT1(map);
	}

	@Override
	public int updataD0(Map<String, String> map) {
		// TODO Auto-generated method stub
		return weixinMerchMapper.updataD0(map);
	}

	@Override
	public int updataT1(Map<String, String> map) {
		// TODO Auto-generated method stub
		return weixinMerchMapper.updataT1(map);
	}
}
