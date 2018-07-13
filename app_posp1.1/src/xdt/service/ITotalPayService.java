package xdt.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import xdt.dto.pay.PayRequest;
import xdt.dto.tfb.WxPayApplyRequest;
import xdt.dto.transfer_accounts.entity.BalanceRequestEntity;
import xdt.dto.transfer_accounts.entity.DaifuQueryRequestEntity;
import xdt.dto.transfer_accounts.entity.DaifuRequestEntity;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAgentInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PmsWeixinMerchartInfo;

/**
 * 代付服务层
 * User: YanChao.Shang
 * Date: 17-3-9
 * Time: 下午2:29
 * To change this template use File | Settings | File Templates.
 */
public interface ITotalPayService {

	Map<String, String> pay(DaifuRequestEntity payRequest,Map<String, String> result);

	public int UpdateDaifu(String batchNo, String responsecode)throws Exception;
	public int UpdateDaifu(String batchNo,String orderId, String responsecode) throws Exception ;
//	public Map<String, String> select(DaifuRequestEntity payRequest, Map<String, String> result);
	
	public  int add(DaifuRequestEntity payRequest, PmsMerchantInfo merchantinfo, Map<String, String> result,
			String state) throws Exception;
	
	Map<String, String> balance(BalanceRequestEntity payRequest, Map<String, String> result);
	/**
	 * 验证银行卡信息是否合法
	 * 
	 * @param DaifuRequestEntity
	 * @return
	 * @throws Exception
	 */
	public boolean  validationStr(DaifuRequestEntity daifu) throws Exception;
	/**
	 * 代付查询功能
	 * 
	 * @param DaifuRequestEntity
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> daifuQuery(DaifuQueryRequestEntity query);
	
	/**
	 * 易宝查询接口
	 * @param batchNo
	 * @param result
	 * @return
	 */
	public Map<String, String> ybQuick(String batchNo,Map<String, String> result,String orderId);
	
	/**
	 * 创新查询接口
	 * @param batchNo
	 * @param result
	 * @return
	 */
	public Map<String, String> cxQuick(String merId,String batchNo,Map<String, String> result);

	List<String> readZipContext(String filePath) throws IOException;

	PmsBusinessPos selectKey(String parameter);
	
	PmsBusinessPos selectMer(String mer);
	/**
	 * 根据商户号获取商户信息
	 * @param mer
	 * @return
	 * @throws Exception 
	 */
	PmsMerchantInfo selecrMerId(String mer) throws Exception;
	
	public int updataPay(Map<String, String> map);
	public int updataPayT1(Map<String, String> map);
	
	
	/**
	 * 根据批次查询代付订单
	 * 
	 */
	
	public List<PmsDaifuMerchantInfo> selectDaifu(PmsDaifuMerchantInfo info); 
	
	
	Map<String, String> selectPay(DaifuRequestEntity payRequest);
	
	/**
	 * 查询退票结果
	 * @param payRequest
	 * @param path
	 */
	public Map<String, String> DownExcel(DaifuRequestEntity payRequest,String path);
	
	public Map<String, String> jhjQuick(String mer,String orderId);
	public Map<String, String> wfbQuick(String merId, String batchNo);
	public Map<String, String> yszfQuick(String merId, String batchNo);
	public int insertProfit(String orderId,String amount,PmsMerchantInfo merchantinfo,String transactionType,String type) throws Exception ;
}
