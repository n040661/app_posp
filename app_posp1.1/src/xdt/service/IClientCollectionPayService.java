package xdt.service;

import xdt.dto.SubmitOrderNoCardPayResponseDTO;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.MsgBean;

public interface IClientCollectionPayService {
	//代付
	public String pay(MsgBean msgBean,SubmitOrderNoCardPayResponseDTO responseDTO,String jsonString) throws Exception;
	
	//
	public String signANDencrypt(MsgBean req_bean) ;
	
	public String sendAndRead(String req) ;
	
	public MsgBean decryptANDverify(String res);

	public ChannleMerchantConfigKey getChannelConfigKey(String merchant_ID);
}
