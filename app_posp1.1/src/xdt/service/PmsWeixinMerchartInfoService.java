package xdt.service;

import java.util.List;
import java.util.Map;

import xdt.model.PmsWeixinMerchartInfo;

public interface PmsWeixinMerchartInfoService {

	public abstract PmsWeixinMerchartInfo selectByPrimaryKey(String account);

	public abstract int insert(PmsWeixinMerchartInfo model);

	public abstract int insertSelective(PmsWeixinMerchartInfo model);

	public abstract int updateByPrimaryKeySelective(PmsWeixinMerchartInfo model);

	public abstract int updateByPrimaryKey(PmsWeixinMerchartInfo model);
	
	public int updateRegister(PmsWeixinMerchartInfo model);
	
	public PmsWeixinMerchartInfo selectByEntity(PmsWeixinMerchartInfo model);

	public List<PmsWeixinMerchartInfo> selectlist(PmsWeixinMerchartInfo model);
	
	public PmsWeixinMerchartInfo selectByCardEntity(PmsWeixinMerchartInfo model);
	
	/**
	 * 代付补款D0
	 */
	public int updataPay(Map<String, String> map);
	
	/**
	 * 代付补款T1
	 */
	public int updataPayT1(Map<String, String> map);
	
	/**
	 * 代付扣款D0
	 */
	public int updataD0(Map<String, String> map);
	
	/**
	 * 代付扣款T0
	 */
	public int updataT1(Map<String, String> map);
}