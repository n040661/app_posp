package xdt.dao;

import java.util.List;

import xdt.model.PmsDaifuMerchantInfo;

public interface IPmsDaifuMerchantInfoDao extends IBaseDao<PmsDaifuMerchantInfo>{
	
    int insertSelective(PmsDaifuMerchantInfo record);
    
    int insertSelectives(PmsDaifuMerchantInfo record);
	/** 
	 * @Description 查询代付信息
	 * @author Administrator
	 * @param model
	 * @return  
	 */  	
	public PmsDaifuMerchantInfo  selectByDaifuMerchantInfo(PmsDaifuMerchantInfo model);
	//查询所有代付中的订单
	public List<PmsDaifuMerchantInfo>  selectPay();
	/** 
	 * @Description 查询未代付信息
	 * @author Administrator
	 * @param model
	 * @return  
	 */  
	public List<PmsDaifuMerchantInfo>  selectDaifu();
	/** 
	 * @Description 查询未代付信息
	 * @author Administrator
	 * @param model
	 * @return  
	 */  
	public List<PmsDaifuMerchantInfo>  selectDaifu1();
	/** 
	 * @Description 查询未代付信息
	 * @author Administrator
	 * @param model
	 * @return  
	 */  
	public List<PmsDaifuMerchantInfo>  selectDaifu2();
	/** 
	 * @Description 畅捷通代付查询状态
	 * @author Administrator
	 * @param model
	 * @return  
	 */  
	public List<PmsDaifuMerchantInfo> selectDaifu3();
	/** 
	 * @Description 查询指定商户下未代付的订单信息
	 * @author Administrator
	 * @param model
	 * @return  
	 */  
	public List<PmsDaifuMerchantInfo>  selectMerchantDaifu(String mercId);
	List<PmsDaifuMerchantInfo> selectYLDaifu();
	public List<PmsDaifuMerchantInfo> selectByDaifuMerchantInfos(PmsDaifuMerchantInfo model) ;
}
