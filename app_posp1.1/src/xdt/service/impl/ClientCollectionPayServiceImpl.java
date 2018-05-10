package xdt.service.impl;


import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;

import xdt.dao.ChannleMerchantConfigKeyDao;
import xdt.dao.IAppRateConfigDao;
import xdt.dao.IMerchantMineDao;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPmsDaifuMerchantInfoDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dao.OriginalOrderInfoDao;
import xdt.dto.SubmitOrderNoCardPayResponseDTO;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.MsgBean;
import xdt.model.MsgBody;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.schedule.ThreadPool;
import xdt.service.IClientCollectionPayService;
import xdt.service.IPublicTradeVerifyService;
import xdt.tools.Constants;
import xdt.util.UtilDate;
import xdt.util.client.Base64;
import xdt.util.client.ClientThread;
import xdt.util.client.RSA;
import xdt.util.client.SslConnection;
import xdt.util.client.Strings;
import xdt.util.client.TripleDes;
import xdt.util.client.Util;
@Service
public class ClientCollectionPayServiceImpl extends BaseServiceImpl implements
		IClientCollectionPayService {

	Logger log = Logger.getLogger(ClientCollectionPayServiceImpl.class);
	@Resource
	private ChannleMerchantConfigKeyDao cmckeyDao;

	@Resource
	private OriginalOrderInfoDao originalDao;

	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao; // 商户信息服务层

	@Resource
	private IMerchantMineDao merchantMineDao;

	@Resource
	private IPmsAppTransInfoDao pmsAppTransInfoDao;

	@Resource
	private IPospTransInfoDAO pospTransInfoDAO;

	@Resource
	private IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao;

	@Resource
	private IAppRateConfigDao appRateConfigDao;

	@Resource
	private IPublicTradeVerifyService iPublicTradeVerifyService;
	//代付
	public synchronized String pay(MsgBean msgBean,SubmitOrderNoCardPayResponseDTO responseDTO,String jsonString) throws Exception{
		//
		PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
		PmsDaifuMerchantInfo model1 = new PmsDaifuMerchantInfo();
		PmsDaifuMerchantInfo model2 = new PmsDaifuMerchantInfo();
		model.setMercId(msgBean.getMERCHANT_ID());
		model2.setMercId(msgBean.getMERCHANT_ID());
		model1.setMercId(msgBean.getMERCHANT_ID());
		model.setBatchNo(msgBean.getBATCH_NO());
		model2.setBatchNo(msgBean.getBATCH_NO());
		PmsDaifuMerchantInfo daifuMerchantInfo =pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model);
		System.out.println("daifuMerchantInfo:"+JSON.toJSON(daifuMerchantInfo));
		if (pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model) != null) {
			responseDTO.setRetCode(12);
			responseDTO.setRetMessage("代付重复");
			jsonString = createJsonString(responseDTO);
			log.info("代付重复");
			return jsonString;
		}
		Double dou =0.00;
		String ss[] =msgBean.getMAP().split(",");
		for (int i = 0; i < ss.length; i++) {
			String [] tt =ss[i].split("\\?");
				dou +=Double.parseDouble(tt[1]);
		}
		
		
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(msgBean.getMERCHANT_ID());
		// o单编号
		String oAgentNo = "";
		BigDecimal b1;
		BigDecimal b2;
		BigDecimal b3;
		BigDecimal ii;
		Double surAmount;
		// 查询当前商户信息
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

			merchantinfo = merchantList.get(0);
			// merchantinfo.setCustomertype("3");

			oAgentNo = merchantinfo.getoAgentNo();//

			if (StringUtils.isBlank(oAgentNo)) {
				// 如果没有欧单编号，直接返回错误
				responseDTO.setRetCode(1);
				responseDTO.setRetMessage("参数错误");
				jsonString = createJsonString(responseDTO);
				log.info("参数错误,没有欧单编号");
				return jsonString;
			}
			// 判断是否为正式商户
			if ("60".equals(merchantinfo.getMercSts())) {
				// 实际金额
				 b1 = new BigDecimal(dou.toString());
				 //扣款之前的代付金额失败就改成这个
				 b2 = new BigDecimal(merchantinfo.getPosition().toString());
				 b3=new BigDecimal(merchantinfo.getPoundage());
				 ii =new BigDecimal(ss.length);
				 log.info("每笔代付手续费:"+b3);
				 log.info("代付总手续费:"+ii.multiply(b3).doubleValue()*100);
				 
				 log.info("代付金额:"+b1.multiply(new BigDecimal(100)).doubleValue());
				 log.info("可用额度:"+b2.doubleValue());
				 Double dd =b2.doubleValue()-ii.multiply(b3).doubleValue()*100;
				 log.info("代付扣除手续费后总额度:"+dd);
				 
				if(b1.multiply(new BigDecimal(100)).doubleValue()>dd)
				{
					responseDTO.setRetCode(1);
					responseDTO.setRetMessage("代付金额小于实际额度，请充值后在支付！");
					jsonString = createJsonString(responseDTO);
					return jsonString;
				}
				 surAmount=b2.subtract(b1.multiply(new BigDecimal(100))).doubleValue()-ii.multiply(b3).doubleValue()*100;
				log.info("剩余可用额度:"+String.valueOf(surAmount));
				BigDecimal  min =new BigDecimal(merchantinfo.getMinDaiFu());
				BigDecimal  max =new BigDecimal(merchantinfo.getMaxDaiFu());
				if(min.compareTo(b1)==1){
					log.info("代付金额小于最小代付金额");
					responseDTO.setRetCode(1);
					responseDTO.setRetMessage("代付金额小于最小代付金额");
					jsonString = createJsonString(responseDTO);
					return jsonString;
				}else if(max.compareTo(b1)==-1){
					log.info("代付金额大于最大代付金额");
					responseDTO.setRetCode(1);
					responseDTO.setRetMessage("代付金额大于最大代付金额");
					jsonString = createJsonString(responseDTO);
					return jsonString;
				}
				merchantinfo.setPosition(surAmount.toString());
				int num=pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
				Map<String, String> map =new HashMap<>();
				log.info("num:"+num);
				if(num>0)
				{
				   jsonString="扣款-SUCCESS";
				}
				
			} else {
				// 请求参数为空
				log.info("商户没有进行实名认证，不是正式商户，" + merchantinfo.getMercId());
				responseDTO.setRetCode(1);
				responseDTO.setRetMessage("还没有进行实名认证，请先去进行实名认证，或者等待客服审核");
				jsonString = createJsonString(responseDTO);
				return jsonString;
			}
			
			for (int i = 0; i < ss.length; i++) {
				String [] tt =ss[i].split("\\?");
				log.info(ss.length);
				log.info("来了来了！！！！！");
				MsgBody body = new MsgBody();
				log.info("来了来了1！！！！！");
				log.info("tt[0]:"+tt[0]);
				
					body.setSN(tt[0]);//流水号，同一批次不重复即可
					log.info("来了来了2！！！！！");
					log.info("tt[1]:"+tt[0]);
					body.setACC_NO(tt[2]);
					body.setACC_NAME(tt[3]);
					body.setAMOUNT(tt[1]);
					body.setACC_PROVINCE(tt[4]);
					body.setACC_CITY(tt[5]);
					body.setBANK_NAME(tt[6]);
					body.setBANK_NO(tt[7]);
					body.setACC_PROP(tt[8]);
					body.setACC_TYPE(tt[9].substring(0,tt[9].length()-1));
					//body.setMER_ORDER_NO("DF1234567811");
					log.info("来了来了3！！！！！");
					msgBean.getBODYS().add(body);
					log.info("msgBean："+JSON.toJSON(msgBean));
					
					/**
					 * 插入代付数据信息
					 */
					model.setCount(ss.length+"");
					log.info("来了来了4！！！！！");
					model.setIdentity(tt[0]);
					model.setBatchNo(msgBean.getBATCH_NO());
					model.setAmount(dou.toString());
					model.setCardno(tt[2]);
					model.setRealname(tt[3]);
					model.setProvince(tt[4]);
					model.setCity(tt[5]);
					model.setPayamount("-"+Double.parseDouble(tt[1]));
					model.setPmsbankno(tt[7]);
					model.setTransactionType("代付");
					model.setPosition(String.valueOf(surAmount));
					model.setRemarks("D0");
					model.setRecordDescription("批次号:"+msgBean.getBATCH_NO()+"/手续费:"+b3);
					model.setResponsecode("200");
					log.info("来了来了5！！！！！");
					model.setOagentno("100333");
					log.info("来了来了6！！！！！");
					model.setPayCounter(b3.doubleValue()+"");
					log.info("来了来了7！！！！！");
					int iii =pmsDaifuMerchantInfoDao.insert(model);
					log.info("iii:"+iii);
					
			}
			

			String res = sendAndRead(signANDencrypt(msgBean));
			log.info("res:"+res);
			MsgBean res_bean = decryptANDverify(res);
			
			log.info("res_bean:"+JSON.toJSON(res_bean));
			log.info(res_bean.toXml());
			if("0000".equals(res_bean.getTRANS_STATE())) {
				if(res_bean.getBODYS().get(0).getPAY_STATE().equals("0000")||res_bean.getBODYS().get(0).getPAY_STATE().equals("00A4")){
					jsonString = createJsonString(res_bean);
					UpdateDaifu(msgBean.getBATCH_NO(), "200");
					log.info("请求成功");
					ThreadPool.executor(new ClientThread(msgBean, this,ss,pmsMerchantInfoDao,pmsDaifuMerchantInfoDao,String.valueOf(surAmount)));
				}else{
					UpdateDaifu(msgBean.getBATCH_NO(), "01");
					PmsMerchantInfo merchantinfo1 = new PmsMerchantInfo();
					merchantinfo1.setMercId(msgBean.getMERCHANT_ID());
					List<PmsMerchantInfo> list =list(merchantinfo1);
					String position =list.get(0).getPosition();
					BigDecimal p1 =new BigDecimal(position);
					//------------------------
					for (int i = 0; i < ss.length; i++) {
						String [] tt =ss[i].split("\\?");
							/**
							 * 插入代付数据信息
							 */
							Map<String, String> map =new HashMap<>();
							map.put("machId", msgBean.getMERCHANT_ID());
							map.put("payMoney",Double.parseDouble(tt[1])*100+"" );
//						    merchantinfo.setPosition(Double.parseDouble(tt[1])*100+p1.doubleValue()+"");
							model1.setCount(ss.length+"");
							log.info("来了来了4！！！！！");
							model1.setIdentity(tt[0]);
							model1.setBatchNo(msgBean.getBATCH_NO()+"/A");
							model1.setAmount(dou.toString());
							model1.setCardno(tt[2]);
							model1.setRealname(tt[3]);
							model1.setProvince(tt[4]);
							model1.setCity(tt[5]);
							model1.setPayamount(Double.parseDouble(tt[1])+"");
							model1.setPmsbankno(tt[7]);
							model1.setTransactionType("代付补款");
							model1.setPosition(surAmount+Double.parseDouble(tt[1])*100+"");
							model1.setRemarks("D0");
							model1.setRecordDescription("批次号:"+msgBean.getBATCH_NO());
							model1.setResponsecode("00");
							log.info("来了来了5！！！！！");
							model1.setOagentno("100333");
							log.info("来了来了6！！！！！");
							model1.setPayCounter("");
							log.info("来了来了7！！！！！");
							Thread.sleep(1000); 
							int iii =pmsDaifuMerchantInfoDao.insert(model);
							model2.setResponsecode("01");
							model2.setIdentity(tt[0]);
							pmsDaifuMerchantInfoDao.update(model2);
							log.info("iii:"+iii);
//							int num=pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
							int num =pmsMerchantInfoDao.updataPay(map);
							if(num>0)
							{
							   jsonString="加款-SUCCESS";
							}
					}
					
				}
			}else{
				/*PmsMerchantInfo merchantinfo1 = new PmsMerchantInfo();
				merchantinfo1.setMercId(msgBean.getMERCHANT_ID());
				List<PmsMerchantInfo> list =list(merchantinfo1);
				String position =list.get(0).getPosition();
				BigDecimal p1 =new BigDecimal(position);*/
				//------------------------
				for (int i = 0; i < ss.length; i++) {
					String [] tt =ss[i].split("\\?");
						/**
						 * 插入代付数据信息
						 */
					    //merchantinfo.setPosition(Double.parseDouble(tt[1])*100+p1.doubleValue()+"");
						Map<String, String> map =new HashMap<>();
						map.put("machId", msgBean.getMERCHANT_ID());
						map.put("payMoney",Double.parseDouble(tt[1])*100+"" );
						model1.setCount(ss.length+"");
						log.info("来了来了4！！！！！");
						model1.setIdentity(tt[0]);
						model1.setBatchNo(msgBean.getBATCH_NO()+"/A");
						model1.setAmount(dou.toString());
						model1.setCardno(tt[2]);
						model1.setRealname(tt[3]);
						model1.setProvince(tt[4]);
						model1.setCity(tt[5]);
						model1.setPayamount(Double.parseDouble(tt[1])+"");
						model1.setPmsbankno(tt[7]);
						model1.setTransactionType("代付补款");
						model1.setPosition(surAmount+Double.parseDouble(tt[1])*100+"");
						model1.setRemarks("D0");
						model1.setRecordDescription("批次号:"+msgBean.getBATCH_NO());
						model1.setResponsecode("00");
						log.info("来了来了5！！！！！");
						model1.setOagentno("100333");
						log.info("来了来了6！！！！！");
						model1.setPayCounter("");
						log.info("来了来了7！！！！！");
						 Thread.sleep(1000); 
						int iii =pmsDaifuMerchantInfoDao.insert(model);
						model2.setResponsecode("01");
						model2.setIdentity(tt[0]);
						pmsDaifuMerchantInfoDao.update(model2);
						log.info("iii:"+iii);
						//int num=pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
						int num =pmsMerchantInfoDao.updataPay(map);
						if(num>0)
						{
						   jsonString="加款-SUCCESS";
						}
				}
			}
		}
		
		
		
		return jsonString;
	}

	public String signANDencrypt(MsgBean req_bean) {
		
		//商户签名
		System.out.println("路径："+Thread.currentThread().getContextClassLoader().getResource("/").getPath());
		try {
			System.out.println("路径1:"+new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("before sign xml =="+ req_bean.toSign());
		String path =null;
		try {
			path=new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"/ky/104000000072508-Signature.pfx";
			System.out.println("1111："+new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"/ky/104000000072508-Signature.pfx");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("msg sign = "+RSA.sign(req_bean.toSign(),path,Constants.mer_pfx_pass));
		req_bean.setMSG_SIGN(RSA.sign(req_bean.toSign(),path,Constants.mer_pfx_pass));
		log.info("req:" + req_bean.toXml());

		//加密报文
		String key = Util.generateKey(9999,24);
		log.info("key:" + key);
		String req_body_enc = TripleDes.encrypt(key, req_bean.toXml());
		log.info("req_body_enc:" + req_body_enc);
		//加密密钥
		String req_key_enc = RSA.encrypt(key, Constants.dna_pub_key);
		log.info("req_key_enc:" + req_key_enc);
		log.info("signANDencrypt:" + req_body_enc+"|"+req_key_enc);
		return req_body_enc+"|"+req_key_enc;

	}
	
	
	public String sendAndRead(String req) {

		try {
			HttpURLConnection connect = new SslConnection().openConnection(Constants.url);
			
	        connect.setReadTimeout(120000);
			connect.setConnectTimeout(30000);

			connect.setRequestMethod("POST");
			connect.setDoInput(true);
			connect.setDoOutput(true);
			connect.connect();

			byte[] put = req.getBytes("UTF-8");
			connect.getOutputStream().write(put);

			connect.getOutputStream().flush();
			connect.getOutputStream().close();
			String res = SslConnection.read(connect);

			connect.getInputStream().close();
			connect.disconnect();
			
//			String res = new SslConnection().connect(url);

			return res;
		} catch(Exception e) {
			log.error(Strings.getStackTrace(e));
		}
		return "";
	}
	
	public MsgBean decryptANDverify(String res) {
		
		String msg_sign_enc = res.split("\\|")[0];
		String key_3des_enc = res.split("\\|")[1];
		
		log.info("msg_sign_enc:" +msg_sign_enc);
		log.info("key_3des_enc:" + key_3des_enc);
		
		//解密密钥
		String key_3des = null;
		try {
			//后面项目名导入服务器需要修改-------------------------------------一共三个
			key_3des = RSA.decrypt(key_3des_enc,new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"/ky/104000000072508-Signature.pfx",Constants.mer_pfx_pass);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//解密报文
		String msg_sign = TripleDes.decrypt(key_3des, msg_sign_enc);
		log.info("msg_sign:" +msg_sign);
		MsgBean res_bean = new MsgBean();
		res_bean.toBean(msg_sign);
		log.info("res:" + res_bean.toXml());

		//验签
		String dna_sign_msg = res_bean.getMSG_SIGN();
		res_bean.setMSG_SIGN("");
		String verify = Strings.isNullOrEmpty(res_bean.getVERSION())? res_bean.toXml(): res_bean.toSign() ;
		log.info("verify:" + verify);
		if(!RSA.verify(dna_sign_msg, Constants.dna_pub_key, verify)) {
			log.error("验签失败");
			res_bean.setTRANS_STATE("00A0");
		}
		return res_bean;
	}
	
	public ChannleMerchantConfigKey getChannelConfigKey(String merchantId) {

		log.info("获取商户密钥信息");
		return cmckeyDao.get(merchantId);
	}
	
	public int UpdateDaifu(String batchNo,String responsecode) throws Exception {
		
		log.info("原始数据:"+batchNo);
		
		PmsDaifuMerchantInfo pdf=new PmsDaifuMerchantInfo();
		
		log.info("上送的批次号:"+batchNo);
		
		pdf.setBatchNo(batchNo);
		pdf.setResponsecode(responsecode);
		return pmsDaifuMerchantInfoDao.update(pdf);
	}
	
	public List<PmsMerchantInfo> list(PmsMerchantInfo merchantinfo) throws Exception{
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		return merchantList;
	}
	
	
}
