package xdt.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import xdt.dao.IPayBankInfoDao;
import xdt.model.PayBankInfo;
import xdt.service.IPayBankInfoService;

@Service
public class PayBankInfoServiceImpl extends BaseServiceImpl  implements IPayBankInfoService {

	@Resource
	private IPayBankInfoDao payBankInfoDao; // 银行卡信息查询
	
	public PayBankInfo selectByBankInfo(PayBankInfo pay) throws Exception {
		
		return payBankInfoDao.selectByBankInfo(pay);
	}

	public List<PayBankInfo> selectBankCodes(PayBankInfo pay) throws Exception {
		
		return payBankInfoDao.selectBankCodes(pay);
	}

}
