package xdt.util.client;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;

import xdt.dao.IPmsDaifuMerchantInfoDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.model.MsgBean;
import xdt.model.MsgBody;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.service.impl.ClientCollectionPayServiceImpl;
import xdt.tools.Constants;

public class ClientThread extends Thread {
	public static final Logger logger=Logger.getLogger(ClientThread.class);
	private IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao;
	private MsgBean msgBean;
	private String aa;
	private ClientCollectionPayServiceImpl clientCollectionPayService;
	private IPmsMerchantInfoDao pmsMerchantInfoDao;
	private String[] bb;
	public ClientThread(MsgBean msgBean,
			ClientCollectionPayServiceImpl clientCollectionPayService,String[] bb,IPmsMerchantInfoDao pmsMerchantInfoDao,IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao,String aa) {
		super();
		this.msgBean = msgBean;
		this.clientCollectionPayService = clientCollectionPayService;
		this.bb=bb;
		this.pmsMerchantInfoDao=pmsMerchantInfoDao;
		this.pmsDaifuMerchantInfoDao=pmsDaifuMerchantInfoDao;
		this.aa=aa;
	}

	@Override
	public synchronized void run() {
		try {
			Thread.sleep(2000);
			PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
			PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
			PmsDaifuMerchantInfo model1 = new PmsDaifuMerchantInfo();
			model.setBatchNo(msgBean.getBATCH_NO());
			model1.setBatchNo(msgBean.getBATCH_NO());
			model.setMercId(msgBean.getMERCHANT_ID());
			merchantinfo.setMercId(msgBean.getMERCHANT_ID());
				for(int i=0;i<500;i++){
					logger.info("启线程查询订单状态");
					MsgBean req_bean = new MsgBean();
					req_bean.setVERSION("2.1");
					req_bean.setMSG_TYPE("100002");
					req_bean.setBATCH_NO(msgBean.getBATCH_NO());//同代付交易请求批次号
					req_bean.setUSER_NAME(Constants.user_name);//系统后台登录名

					String res = clientCollectionPayService.sendAndRead(clientCollectionPayService.signANDencrypt(req_bean));

					MsgBean res_bean = clientCollectionPayService.decryptANDverify(res);
					
					if("0000".equals(res_bean.getTRANS_STATE())) {
						logger.info("请求成功");
						
						logger.info("res_bean:"+JSON.toJSON(res_bean));
						logger.info("bodys:"+JSON.toJSON(res_bean.getBODYS()));
						for ( MsgBody body : res_bean.getBODYS()) {
							if(body.getPAY_STATE().equals("0000")){
								clientCollectionPayService.UpdateDaifu(msgBean.getBATCH_NO(), "00");
								return;
							}else if(body.getPAY_STATE().equals("00A4")){
								clientCollectionPayService.UpdateDaifu(msgBean.getBATCH_NO(), "200");
								continue;
							}else{
								/*PmsMerchantInfo merchantinfo1 = new PmsMerchantInfo();
								merchantinfo1.setMercId(msgBean.getMERCHANT_ID());
								List<PmsMerchantInfo> list =clientCollectionPayService.list(merchantinfo1);
								String position =list.get(0).getPosition();
								BigDecimal p1 =new BigDecimal(position);*/
								clientCollectionPayService.UpdateDaifu(msgBean.getBATCH_NO(), "01");
								//------------------------
								for (int j = 0; j < bb.length; j++) {
									String [] tt =bb[j].split("\\?");
										/**
										 * 插入代付数据信息
										 */
										Map<String, String> map =new HashMap<>();
										map.put("machId", msgBean.getMERCHANT_ID());
										map.put("payMoney",Double.parseDouble(tt[1])*100+"" );
									   // merchantinfo.setPosition(Double.parseDouble(tt[1])*100+p1.doubleValue()+"");
										model.setCount(bb.length+"");
										logger.info("来了来了4！！！！！");
										model.setIdentity(tt[0]);
										model.setBatchNo(msgBean.getBATCH_NO()+"/A");
										model.setCardno(tt[2]);
										model.setRealname(tt[3]);
										model.setProvince(tt[4]);
										model.setCity(tt[5]);
										model.setPayamount(Double.parseDouble(tt[1])+"");
										model.setPmsbankno(tt[7]);
										model.setTransactionType("代付补款");
										
										Double abc =Double.parseDouble(aa)+Double.parseDouble(tt[1])*100;
										logger.info("11111122222aa:"+Double.parseDouble(aa));
										logger.info("11111122222tt:"+Double.parseDouble(tt[1])*100);
										logger.info("11111122222abd:"+abc);
										model.setPosition(String.valueOf(abc));
										model.setRemarks("D0");
										model.setRecordDescription("批次号:"+msgBean.getBATCH_NO());
										model.setResponsecode("00");
										logger.info("来了来了5！！！！！");
										model.setOagentno("100333");
										logger.info("来了来了6！！！！！");
										model.setPayCounter("");
										logger.info("来了来了7！！！！！");
										 Thread.sleep(1000); 
										int iii =pmsDaifuMerchantInfoDao.insert(model);
										model1.setResponsecode("01");
										model1.setIdentity(tt[0]);
										pmsDaifuMerchantInfoDao.update(model1);
										logger.info("iii:"+iii);
										//int num=pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
										int num =pmsMerchantInfoDao.updataPay(map);
										if(num>0)
										{
											logger.info("加款-SUCCESS");
										}
								}
								
								
								return;
							}
						}
					}
					logger.info(res_bean.toXml());
					Thread.sleep(5000);
				}
				/*PmsMerchantInfo merchantinfo1 = new PmsMerchantInfo();
				merchantinfo1.setMercId(msgBean.getMERCHANT_ID());
				List<PmsMerchantInfo> list =clientCollectionPayService.list(merchantinfo1);
				String position =list.get(0).getPosition();
				BigDecimal p1 =new BigDecimal(position);*/
				//------------------------
				for (int j = 0; j < bb.length; j++) {
					String [] tt =bb[j].split("\\?");
						/**
						 * 插入代付数据信息
						 */
					   // merchantinfo.setPosition(Double.parseDouble(tt[1])*100+p1.doubleValue()+"");
						Map<String, String> map =new HashMap<>();
						map.put("machId", msgBean.getMERCHANT_ID());
						map.put("payMoney",Double.parseDouble(tt[1])*100+"" );
						model.setCount(bb.length+"");
						logger.info("来了来了4！！！！！");
						model.setIdentity(tt[0]);
						model.setBatchNo(msgBean.getBATCH_NO()+"/A");
						model.setCardno(tt[2]);
						model.setRealname(tt[3]);
						model.setProvince(tt[4]);
						model.setCity(tt[5]);
						model.setPayamount(Double.parseDouble(tt[1])+"");
						model.setPmsbankno(tt[7]);
						model.setTransactionType("代付补款");
						Double abc =Double.parseDouble(aa)+Double.parseDouble(tt[1])*100;
						logger.info("1111113333aa:"+Double.parseDouble(aa));
						logger.info("111111333333tt:"+Double.parseDouble(tt[1])*100);
						logger.info("111111333333abd:"+abc);
						model.setPosition(String.valueOf(abc));
						model.setRemarks("D0");
						model.setRecordDescription("批次号:"+msgBean.getBATCH_NO());
						model.setResponsecode("00");
						logger.info("来了来了5！！！！！");
						model.setOagentno("100333");
						logger.info("来了来了6！！！！！");
						model.setPayCounter("");
						logger.info("来了来了7！！！！！");
						Thread.sleep(1000); 
						int iii =pmsDaifuMerchantInfoDao.insert(model);
						model1.setResponsecode("01");
						model1.setIdentity(tt[0]);
						//修改失败的状态
						pmsDaifuMerchantInfoDao.update(model1);
						logger.info("iii:"+iii);
						//int num=pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
						int num =pmsMerchantInfoDao.updataPay(map);
						if(num>0)
						{
							logger.info("加款-SUCCESS");
						}
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	
}
