package xdt.service;

import java.util.List;

import xdt.model.PmsDaifuMerchantInfo;

public interface IPmsDaifuMerchantInfoService {
	
	int insert(PmsDaifuMerchantInfo record);

    int insertSelective(PmsDaifuMerchantInfo record);

    int insertSelectives(PmsDaifuMerchantInfo record);
    public List<PmsDaifuMerchantInfo>  selectPay();
    public List<PmsDaifuMerchantInfo>  selectDaifu1();
    public List<PmsDaifuMerchantInfo>  selectDaifu3();
    List<PmsDaifuMerchantInfo> selectYLDaifu();
}
