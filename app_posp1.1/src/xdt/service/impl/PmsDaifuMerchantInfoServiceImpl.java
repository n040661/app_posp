package xdt.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import xdt.dao.IPmsDaifuMerchantInfoDao;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.service.IPmsDaifuMerchantInfoService;
@Service
public class PmsDaifuMerchantInfoServiceImpl extends BaseServiceImpl implements IPmsDaifuMerchantInfoService{
	@Resource
	private IPmsDaifuMerchantInfoDao daifuMerchantInfoDao;
	public int insert(PmsDaifuMerchantInfo record) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int insertSelective(PmsDaifuMerchantInfo record) {
		// TODO Auto-generated method stub
		return 0;
	}
	public int insertSelectives(PmsDaifuMerchantInfo record) {
		// TODO Auto-generated method stub
		return daifuMerchantInfoDao.insertSelectives(record);
	}
	@Override
	public List<PmsDaifuMerchantInfo> selectPay() {
		// TODO Auto-generated method stub
		return daifuMerchantInfoDao.selectPay();
	}
	@Override
	public List<PmsDaifuMerchantInfo> selectYLDaifu() {
		// TODO Auto-generated method stub
		return daifuMerchantInfoDao.selectYLDaifu();
	}

	@Override
	public List<PmsDaifuMerchantInfo> selectDaifu1() {
		// TODO Auto-generated method stub
		return daifuMerchantInfoDao.selectDaifu1();
	}

	@Override
	public List<PmsDaifuMerchantInfo> selectDaifu3() {
		// TODO Auto-generated method stub
		return daifuMerchantInfoDao.selectDaifu3();
	}
	
}
