package xdt.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import xdt.common.RetAppMessage;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPmsMerchantPosDao;
import xdt.dao.IPmsPosInfoDao;
import xdt.dao.IPmsUnionpayDao;
import xdt.dto.SnPmsPosInfoRequestDTO;
import xdt.dto.SnPmsPosInfoResponseDTO;
import xdt.model.PmsAgentInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PmsMerchantPos;
import xdt.model.PmsPosInfo;
import xdt.model.PmsUnionpay;
import xdt.model.SessionInfo;
import xdt.service.IPmsAgentInfoService;
import xdt.service.IPmsPosInfoService;

@Service("PmsPosInfo")
public class PmsPosInfoServiceImpl extends BaseServiceImpl implements
		IPmsPosInfoService {
	@Resource
	private IPmsPosInfoDao tPosDao;
	@Resource
	private IPmsMerchantPosDao pmsPos;
	@Resource
	private IPmsMerchantInfoDao infoDao;
	@Resource
	private IPmsMerchantPosDao pmsMerchantPosDao;
	@Resource
	private IPmsAgentInfoService pmsAgentInfoService;
	@Resource
    private IPmsUnionpayDao iPmsUnionpayDao;
	private Logger logger = Logger.getLogger(PmsPosInfoServiceImpl.class);

	/**
	 *通过sn号查询出相关的数据，如果已经被绑定判断是不是本用户绑定的，没有则进行绑定
	 * 
	 * @param session
	 */
	public String addMineDevice(String accountInfo, HttpSession session,
			HttpServletRequest request) throws Exception {
		setMethodSession(request.getRemoteAddr());
		logger.info("进入绑定pos方法...");

		String deviceNo = "";// 设备编号
		String deviceName = "";// 设备名称
		String deviceStatus = "0";// 设备状态

		// 服务器返回的判断信息
		String message = INITIALIZEMESSAGE;// 服务器异常
		PmsMerchantPos pmsMP = new PmsMerchantPos();
		PmsPosInfo pms = new PmsPosInfo();
		PmsMerchantInfo tep = new PmsMerchantInfo();
		// session获取
		SessionInfo sessionInfo = (SessionInfo) session
				.getAttribute(SessionInfo.SESSIONINFO);
		PmsMerchantPos byId = null;
		if (sessionInfo != null) {
			String mobilephone = sessionInfo.getMobilephone();
			String mercId = sessionInfo.getMercId();
			setSession(request.getRemoteAddr(), session.getId(), mobilephone);
			String id = sessionInfo.getId();
			SnPmsPosInfoRequestDTO obj = (SnPmsPosInfoRequestDTO) parseJsonString(
					accountInfo, SnPmsPosInfoRequestDTO.class);// 解析

			// session获取商户id
			if (obj != null) {
				logger.info("[client_req]" + createJson(obj));
				deviceNo = obj.getDeviceNo();
				PmsPosInfo selssPos = tPosDao.selectBusinessPos(deviceNo); // 通过sn号进行查询

				// 库内有没有对应的sn号
				if (selssPos != null) {
				
					PmsMerchantInfo selectMercByMercId = infoDao.searchById(id);
	
					String agentNumber1 = null;
					String agentNumber2 = null;
					String oAgentNo = selectMercByMercId.getoAgentNo();
					PmsAgentInfo pmsAgentInfo = new PmsAgentInfo();
					pmsAgentInfo.setoAgentNo(oAgentNo);
					pmsAgentInfo.setAgentLevel("0");
					List<PmsAgentInfo> selectList = pmsAgentInfoService.selectList(pmsAgentInfo);
					if(selectList != null && selectList.size() > 0){
						PmsAgentInfo pmsAgentInfo2 = selectList.get(0);
						agentNumber1 = pmsAgentInfo2.getAgentNumber();
					}
					
					String outmercode = selssPos.getOutmercode();
					PmsAgentInfo pAgentInfo = new PmsAgentInfo();
					pAgentInfo.setAgentNumber(outmercode);
					PmsAgentInfo oAgent = pmsAgentInfoService.getOAgent(pAgentInfo);
					if(oAgent != null){
						agentNumber2 = oAgent.getAgentNumber();
					}
					
					String infoAgentNumber = selectMercByMercId.getAgentNumber();
				
					// 判断selssPos有没有数据
					String agentNum = selssPos.getAgentNumber();

					boolean bool = false;
					if(!"".equals(infoAgentNumber) && infoAgentNumber != null){
						if(agentNum.equals(infoAgentNumber)){
							bool = true;
						}
					}else{
						if(agentNumber1.equals(agentNumber2)){
							bool = true;
						}
					}

					if (bool) {
						BigDecimal bigId = selssPos.getId();
						deviceName = obj.getDeviceName();
						int usestatus = selssPos.getUsestatus();
						int ort = 0;
						int st = 1;
						byId = pmsPos.selectMerchantPos(selssPos.getId()
								.toString());
						// 判断selssPos中Usestatus是否是1 ： 0 未使用 1 已使用
						if (usestatus == ort && byId == null) {// 判断是不是绑定的pos
							//查询是否需要商户信息
			                PmsUnionpay pmsUnionpay = iPmsUnionpayDao.searchById(mercId);
			                PmsMerchantPos p = new PmsMerchantPos();
							p.setMerchantid(new BigDecimal(id));
							List<PmsMerchantPos> searchList = pmsMerchantPosDao
									.searchList(p);
							if(pmsUnionpay == null || (pmsUnionpay != null && searchList.size() == 0)){
								// 全符合进行比较
								pms.setSerialno(deviceNo);
								pms.setUsestatus((short) st);
								pms.setId(bigId);
								// 修改pms_pos_info
								int update1 = tPosDao.update(pms);
								// 修改PMS_MERCHANT_INFO（商户信息表）AGENT_NUMBER
								tep.setAgentNumber(agentNum);
								tep.setId(id);
								int update2 = infoDao.update(tep);
								// 添加(pos注册信息表)PMS_MERCHANT_POS
								pmsMP.setMerchantid(new BigDecimal(id));
								pmsMP.setPosid(bigId);
								pmsMP.setPosname(deviceName);
								int ram = this.createRandomNumber();// 随机8位数
								PmsMerchantPos selectSn = pmsPos.selectSn(ram);
								for (int j = 0; j < st; j++) {
									if (!"".equals(selectSn)) {
										ram = this.createRandomNumber();
										j++;
									}
								}
								pmsMP.setPosbusinessno(String.valueOf(ram));
								pmsMP.setUsestatus((short) 1);
								pmsMP.setCursoftversion(dtoSdf.format(new Date()));
								SimpleDateFormat time2 = new SimpleDateFormat(
										"yyyy-MM-dd");
								String tm = time2.format(new Date());
								pmsMP.setSetupdate(tm);
								pmsMP.setStartusedate(tm);
	
								int insert = pmsPos.insert(pmsMP);
								if (1 == update1 && update2 == 1 && insert == 1) {
									message = SUCCESSMESSAGE;// 绑定成功
								} else {
									message = FAILMESSAGE;// 绑定失败，请重新绑定
									insertAppLogs(mobilephone, "", "1406");
								}
							}else{
								message = "10:已经绑定过其它pos,需解绑后方可绑定";
							}
						} else if (usestatus == st && byId != null) {
							// 比较 pos_info 表的id和merchant_pos 相等
							if (id.equals(byId.getMerchantid().toString())) {
								message = "5:已绑定过此pos";// 已绑定过此pos
							} else {
								message = EXISTMESSAGE;// pos已被他人绑定，请使用其他pos
								insertAppLogs(mobilephone, "", "1404");
							}
						} else {
							message = INITIALIZEMESSAGE;
						}

					} else {
						message = EMPTYMESSAGE;// pos所属代理不同
						insertAppLogs(mobilephone, "", "1402");
					}
				} else {
					message = INVALIDMESSAGE;// 此商家不支持（没有入库或是入库状态不对）
					insertAppLogs(mobilephone, "", "1403");
				}

				PmsMerchantPos p = new PmsMerchantPos();
				p.setMerchantid(new BigDecimal(id));
				List<PmsMerchantPos> searchList = pmsMerchantPosDao
						.searchList(p);

				if (searchList != null && searchList.size() > 0) {
					String postype1 = null;
					String postype2 = null;
					for (PmsMerchantPos merchantPos : searchList) {
						BigDecimal posId = merchantPos.getPosid();
						PmsPosInfo selectPosId = tPosDao.selectPosId(posId
								.toString());
						String postype = selectPosId.getPostype();
						if ("1".equals(postype)) {
							postype1 = postype;
						} else if ("2".equals(postype)) {
							postype2 = postype;
						}
					}

					if (postype1 != null && postype2 == null) {
						deviceStatus = "1";
					} else if (postype1 == null && postype2 != null) {
						deviceStatus = "2";
					} else if (postype1 != null && postype2 != null) {
						deviceStatus = "3";
					}
				}

			} else {
				message = DATAPARSINGMESSAGE;// 数据解析错误
				insertAppLogs(mobilephone, "", "1402");
			}
		} else {
			message = RetAppMessage.SESSIONINVALIDATION; // 会话失效
		}

		// 解析要返回的信息
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		if (retMessage.equals("initialize")) {// -1
			retMessage = "服务器异常";
		} else if (retMessage.equals("sessionInvalidation")) {// 7
			retMessage = "会话失败，请重新登陆";
		} else if (retMessage.equals("dataParsing")) {// 6
			retMessage = "数据解析错误";
		} else if (retMessage.equals("empty")) {// 4
			retMessage = "请选用正确的代理商pos";// 修改
		} else if (retMessage.equals("invalid")) {// 5
			retMessage = "暂不支持此商家pos";// 修改
		} else if (retMessage.equals("exist")) {// 2
			retMessage = "pos已被他人绑定，请使用其他pos";
		} else if (retMessage.equals("success")) {// 0
			retMessage = "绑定成功";
		} else if (retMessage.equals("fail")) {// 1
			retMessage = "绑定失败，请重新绑定";
		}
		// 向客户端返回判断信息
		SnPmsPosInfoResponseDTO sne = new SnPmsPosInfoResponseDTO();
		sne.setRetCode(retCode);
		sne.setRetMessage(retMessage);
		sne.setDeviceStatus(deviceStatus);
		logger.info("[app_rsp]" + createJson(sne));
		return createJsonString(sne);
	}

	/**
	 * 通过sn号查询出相关的数据异常
	 */
	@Override
	public String addMineDeviceException() throws Exception {
		SnPmsPosInfoResponseDTO sne = new SnPmsPosInfoResponseDTO();
		sne.setRetCode(100);
		sne.setRetMessage("系统异常");
		return createJsonString(sne);
	}
}
