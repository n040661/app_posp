package xdt.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import xdt.dao.ChannleMerchantConfigKeyDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dto.balance.BalanceRequestEntity;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.PmsMerchantInfo;
import xdt.service.IWechatScanCodeService;
import xdt.util.JsdsUtil;

@Component
public class WechatScanCodeServiceImpl extends BaseServiceImpl implements IWechatScanCodeService {

	/**
	 * 记录日志
	 */
	private Logger log = Logger.getLogger(WechatScanCodeServiceImpl.class);
	
	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao; // 商户信息服务层
	/**
	 * 渠道商户密钥信息
	 */
	@Resource
	private ChannleMerchantConfigKeyDao cmckeyDao;
	/**
	 * 余额查询功能
	 * 
	 * @param originalinfo
	 *            下游请求原始数据
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> payHandle(BalanceRequestEntity originalinfo) throws Exception {
		
		Map<String, String> result = new HashMap<String, String>();
		
		if (this.verify(originalinfo, result)) {
			
			log.info("****************************查询余额------签名错误");
            result.put("11", "签名错误！");
			return result;
		} else {
			// 根据商户号查询
			String mercId = originalinfo.getMerchantId();

			PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
			merchantinfo.setMercId(mercId);

			// o单编号
			String oAgentNo = "";

			// 查询当前商户信息
			List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
			if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

				merchantinfo = merchantList.get(0);
				// merchantinfo.setCustomertype("3");

				oAgentNo = merchantinfo.getoAgentNo();//

				if (StringUtils.isBlank(oAgentNo)) {
					// 如果没有欧单编号，直接返回错误
					result.put("1", "参数错误！");
					log.info("参数错误,没有欧单编号");
					return result;
				}
				// 判断是否为正式商户
				if ("60".equals(merchantinfo.getMercSts())) {
					
					 log.info("此商户的商户号:"+originalinfo.getMerchantId());
					result.put("merchantId", originalinfo.getMerchantId());
					// 判断此商户是否开启代付
					if ("0".equals(merchantinfo.getOpenPay())) {
						
						 log.info("此商户是否开启代付:"+merchantinfo.getOpenPay());
						result.put("status", merchantinfo.getOpenPay());			
					   if("0".equals(originalinfo.getTranTp()))	
					   {
						   log.info("此商户的T0代付余额(分):"+merchantinfo.getPosition());
							BigDecimal db = new BigDecimal(merchantinfo.getPosition());
							String l = db.toPlainString();
							Double amount=Double.parseDouble(l)/100;
							log.info("此商户的T0代付余额(元):"+amount.toString());
						   result.put("position", amount.toString());
						   result.put("pl_message", "查询成功");
						   result.put("respCode", "0000");
					   }else if("1".equals(originalinfo.getTranTp()))
					   {
						   log.info("此商户的T1代付余额:"+merchantinfo.getPositionT1());
							BigDecimal db = new BigDecimal(merchantinfo.getPositionT1());
							String l = db.toPlainString();
							Double amount=Double.parseDouble(l)/100;
							log.info("此商户的T1代付余额(元):"+amount.toString());
						   result.put("position", amount.toString()); 
						   result.put("pl_message", "查询成功");
						   result.put("respCode", "0000");
					   }
					
					}else{					
						// 请求参数为空
						log.info("商户没有开启代付，" + merchantinfo.getMercId());
						result.put("3", "还没有开启代付，请先开启代付，或者等待客服审核！");
						log.info("还没有开启代付，请先开启代付，或者等待客服审核！");
						return result;
					}				
				}else{					
					// 请求参数为空
					log.info("商户没有进行实名认证，" + merchantinfo.getMercId());
					result.put("2", "还没有进行实名认证，请先去进行实名认证，或者等待客服审核!");
					log.info("还没有进行实名认证，请先去进行实名认证，或者等待客服审核!");
					return result;					
				}
					
			}else{
				
				// 请求参数为空
				result.put("4", "此商户不存在,请重新输入!");
				log.info("此商户不存在,请重新输入!");
				return result;	
	
			}		
		}	
		return result;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see xdt.service.HfQuickPayService#getChannelConfigKey(java.lang.String)
	 */
	public ChannleMerchantConfigKey getChannelConfigKey(String merchantId)
			throws Exception {
		log.info("获取商户密钥信息");
		return cmckeyDao.get(merchantId);
	}
	public boolean verify(BalanceRequestEntity reqData, Map<String, String> result) {
		boolean signResult = false;
		log.info("****************************开始签名处理");
		try {
			String e = reqData.getSignmsg();
			HashMap<String, String> signMap = JsdsUtil.beanToMap(reqData);
			signMap.remove("signmsg");
			Set<String> keys = new TreeSet<String>();
			// 剔除值为空的
			for (String key : signMap.keySet()) {
				if ("".equals(signMap.get(key)) || signMap.get(key) == null) {
					keys.add(key);
				}
			}
			for (String key : keys) {
				signMap.remove(key);
			}
			String merchNo = reqData.getMerchantId();
			log.info("********************正式商户-----------------当前商户号:" + merchNo);
			ChannleMerchantConfigKey channerKey = this.cmckeyDao.get(merchNo);
			log.info("********************正式商户-----------------商户密钥:" + channerKey);
			String key = channerKey.getMerchantkey();
			log.info("生成签名的数据:" + signMap);
			log.info("秘钥:" + key);
			if (!e.equals(JsdsUtil.sign(signMap, key))) {
				result.put("respCode", "0008");
				result.put("respMsg", "签名错误");
				signResult = true;
			}
		} catch (Exception var9) {
	      var9.printStackTrace();
		}

		return signResult;
	}

}
