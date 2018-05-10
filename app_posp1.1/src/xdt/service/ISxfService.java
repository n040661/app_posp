package xdt.service;

import java.util.Map;

import xdt.dto.sxf.PayRequsest;
import xdt.dto.sxf.SXFRequest;
import xdt.dto.sxf.SXFResponse;
import xdt.dto.tfb.WxPayApplyResponse;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PospTransInfo;

public interface ISxfService {

	 Map<String, String> cardPay(SXFRequest sxfRequest,Map<String, String> result);
	 Map<String, String> paySelect(SXFRequest sxfRequest,Map<String, String> result) throws Exception;
	 Map<String, String> pay(PayRequsest payRequsest,Map<String, String> result);
	 public void update(SXFResponse sxfResponse) throws Exception;
	PospTransInfo InsertJournal(PmsAppTransInfo appTransInfo) throws Exception;
	int UpdateDaifu(String sn, String string) throws Exception;
	
	public Map<String, String> pSelect(PayRequsest payRequsest,
			Map<String, String> result) throws Exception;
	int add(PayRequsest payRequsest, PmsMerchantInfo merchantinfo,
			Map<String, String> results, String string)throws Exception;
	
	
	public  Map<String, String> payCs(PayRequsest payRequsest,
			Map<String, String> result);
}
