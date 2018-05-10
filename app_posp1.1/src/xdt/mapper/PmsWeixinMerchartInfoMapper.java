package xdt.mapper;

import java.util.List;
import java.util.Map;

import xdt.model.PmsWeixinMerchartInfo;

public interface PmsWeixinMerchartInfoMapper {

	public PmsWeixinMerchartInfo selectByPrimaryKey(String account);

	public int insert(PmsWeixinMerchartInfo model);

	public int insertSelective(PmsWeixinMerchartInfo model);

	public int updateByPrimaryKeySelective(PmsWeixinMerchartInfo model);

	public int updateByPrimaryKey(PmsWeixinMerchartInfo model);

	public PmsWeixinMerchartInfo selectByEntity(PmsWeixinMerchartInfo model);
	
	public List<PmsWeixinMerchartInfo> selectList(PmsWeixinMerchartInfo model);
	
	public PmsWeixinMerchartInfo selectByCardEntity(PmsWeixinMerchartInfo model);
	
	public int updateByMerchartId(PmsWeixinMerchartInfo model);
	
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
