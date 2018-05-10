package xdt.service;

import java.util.Map;

import xdt.dto.quickPay.entity.QueryRequestEntity;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;
import xdt.quickpay.conformityQucikPay.entity.ConformityQucikPayRequestEntity;

public interface IConformityQucikPayService {

	public ChannleMerchantConfigKey getChannelConfigKey(String paramString) throws Exception;

	public OriginalOrderInfo getOriginOrderInfo(String paramString) throws Exception;

	public PmsBusinessPos selectKey(String paramString) throws Exception;

	public Map<String, String> payHandle(ConformityQucikPayRequestEntity paramConformityQucikPayRequestEntity)
			throws Exception;

	public void otherInvoke(String paramString1, String paramString2) throws Exception;

	public Map<String, String> quickQuery(QueryRequestEntity paramQueryRequestEntity);

	public int updatePmsMerchantInfo(OriginalOrderInfo paramOriginalOrderInfo) throws Exception;

	public int updatePmsMerchantInfo80(OriginalOrderInfo paramOriginalOrderInfo) throws Exception;

}
