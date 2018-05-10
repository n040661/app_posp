package xdt.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import xdt.common.RetAppMessage;
import xdt.dao.IPmsMerchantPosDao;
import xdt.dao.IPmsPosInfoDao;
import xdt.dto.ConfirmDeviceRequestDTO;
import xdt.dto.ConfirmDeviceResponseDTO;
import xdt.dto.MerchantsTheBindingPosResponseDTO;
import xdt.dto.MerchantsUnBindingPosRequestDTO;
import xdt.dto.MerchantsUnBindingPosResponseDTO;
import xdt.model.PmsMerchantPos;
import xdt.model.PmsPosInfo;
import xdt.model.SessionInfo;
import xdt.service.IPmsMerchantPosService;

@Service("PmsMerchantPos")
public class PmsMerchantPosServiceImpl extends BaseServiceImpl implements
		IPmsMerchantPosService {
	@Resource
	private IPmsMerchantPosDao pmsMerchantPosDao;
	@Resource
	private IPmsPosInfoDao pmsPosInfoDao;
	private Logger logger = Logger.getLogger(PmsMerchantPosServiceImpl.class);

	/**
	 * 通过商户id进行查询商户绑定的刷卡头
	 */
	public String selectPmsMerchantPos(HttpSession session,
			HttpServletRequest request) throws Exception {
		setMethodSession(request.getRemoteAddr());
		logger.info("进入商户绑定的刷卡头查询...");
		String message = INITIALIZEMESSAGE;
		SessionInfo sessio = (SessionInfo) session
				.getAttribute(SessionInfo.SESSIONINFO);// 获取session
		PmsPosInfo searchById = null;
		MerchantsTheBindingPosResponseDTO merchants = new MerchantsTheBindingPosResponseDTO();
		List list = new ArrayList();
		Map map = null;
		if (sessio != null) {
			setSession(request.getRemoteAddr(), session.getId(), sessio
					.getMobilephone());
			PmsMerchantPos p = new PmsMerchantPos();
			p.setMerchantid(new BigDecimal(sessio.getId()));
			List<PmsMerchantPos> searchList = pmsMerchantPosDao.searchList(p);
			if (searchList != null && searchList.size() > 0) {
				for (PmsMerchantPos pmsMerchantPos : searchList) {
					PmsMerchantPos selectMerchantid = pmsMerchantPos;
					searchById = pmsPosInfoDao.searchById(selectMerchantid
							.getPosid().toString());

					if (searchById != null) {
						map = new HashMap<String, String>();
						map.put("deviceNo", searchById.getSerialno());
						map.put("deviceType", searchById.getPostype());
						map.put("deviceName", selectMerchantid.getPosname());
						map.put("bindDate", selectMerchantid.getSetupdate());
						map.put("posId", selectMerchantid.getPosid().toString());
						map.put("bindStatus", "1");
						
						list.add(map);
					}
				}
			}
			message = SUCCESSMESSAGE;
		} else {
			message = RetAppMessage.SESSIONINVALIDATION; // 会话失效
		}
		String retCode = message.split(":")[0];
		String retMessage = message.split(":")[1];
		if (retMessage.equals("initialize")) {
			retMessage = "服务器异常";
		} else if (retMessage.equals("success")) {
			retMessage = "查询成功";
		} else if (retMessage.equals("dataParsing")) {// 6
			retMessage = "数据解析错误";
		} else if (retMessage.equals("sessionInvalidation")) {// 7
			retMessage = "会话失效，请重新登录";
		}
		merchants.setRetCode(Integer.parseInt(retCode));
		merchants.setRetMessage(retMessage);
		merchants.setDeviceList(list);
		logger.info("[app_rsp]" + createJson(merchants));
		return createJsonString(merchants);
	}

	/**
	 * 通过商户id进行查询商户绑定的刷卡头异常
	 */
	@Override
	public String selectPmsMerchantPosException() throws Exception {
		MerchantsTheBindingPosResponseDTO merchants = new MerchantsTheBindingPosResponseDTO();
		merchants.setRetCode(100);
		merchants.setRetMessage("系统异常");
		return createJsonString(merchants);
	}

	/**
	 * 通过posId解绑设备
	 * 
	 * @throws Exception
	 */
	@Override
	public String updateMineDevice(String pmsMerchantInfo, HttpSession session,
			HttpServletRequest request) throws Exception {
		setMethodSession(request.getRemoteAddr());
		logger.info("进入通过posId解绑设备...");
		String message = INITIALIZEMESSAGE;
		SessionInfo sessio = (SessionInfo) session
				.getAttribute(SessionInfo.SESSIONINFO);// 获取session

		// 解析商户登录信息
		Object obj = parseJsonString(pmsMerchantInfo,
				MerchantsUnBindingPosRequestDTO.class);
		MerchantsUnBindingPosRequestDTO bindingPosRequestDTO = (MerchantsUnBindingPosRequestDTO) obj;

		String mobilePhone = bindingPosRequestDTO.getMobilePhone();
		String posId = bindingPosRequestDTO.getPosId();

		MerchantsUnBindingPosResponseDTO merchants = new MerchantsUnBindingPosResponseDTO();
		if (sessio != null) {
			setSession(request.getRemoteAddr(), session.getId(), sessio
					.getMobilephone());
			message = DATAPARSINGMESSAGE;

			PmsMerchantPos pmsMerchantPos = new PmsMerchantPos();
			pmsMerchantPos.setPosid(new BigDecimal(posId));
			pmsMerchantPos.setUsestatus((short) 4);

			int count = pmsMerchantPosDao.updateByPosId(pmsMerchantPos);
			if (count == 1) {
				merchants.setBindStatus("0");

				PmsPosInfo t = new PmsPosInfo();
				t.setId(new BigDecimal(posId));
				t.setUsestatus((short) 0);
				int count2 = pmsPosInfoDao.update(t);
				if (count2 == 1) {
					message = SUCCESSMESSAGE;
				} else {
					message = FAILMESSAGE;
				}
			} else {
				message = FAILMESSAGE;
			}
		} else {
			message = RetAppMessage.SESSIONINVALIDATION; // 会话失效
		}
		String retCode = message.split(":")[0];
		String retMessage = message.split(":")[1];
		if (retMessage.equals("initialize")) {
			retMessage = "服务器异常";
		} else if (retMessage.equals("success")) {
			retMessage = "解绑成功";
		} else if (retMessage.equals("dataParsing")) {// 6
			retMessage = "数据解析错误";
		} else if (retMessage.equals("sessionInvalidation")) {// 7
			retMessage = "会话失效，请重新登录";
		} else if (retMessage.equals("fail")) {
			retMessage = "解绑失败";
		}
		merchants.setRetCode(Integer.parseInt(retCode));
		merchants.setRetMessage(retMessage);
		logger.info("[app_rsp]" + createJson(merchants));
		return createJsonString(merchants);
	}

	/**
	 * 通过posId解绑设备异常
	 */
	@Override
	public String updateMineDeviceException() throws Exception {
		MerchantsUnBindingPosResponseDTO merchants = new MerchantsUnBindingPosResponseDTO();
		merchants.setRetCode(100);
		merchants.setRetMessage("系统异常");
		merchants.setBindStatus("");
		return createJsonString(merchants);
	}

	/**
	 * 确认设备认证
	 * 
	 * @throws Exception
	 */
	@Override
	public String confirmDevice(String pmsMerchantInfo, HttpSession session,
			HttpServletRequest request) throws Exception {
		setMethodSession(request.getRemoteAddr());
		logger.info("进入确认设备认证...");
		String message = INITIALIZEMESSAGE;
		SessionInfo sessio = (SessionInfo) session
				.getAttribute(SessionInfo.SESSIONINFO);// 获取session

		// 解析商户登录信息
		Object obj = parseJsonString(pmsMerchantInfo,
				ConfirmDeviceRequestDTO.class);
		ConfirmDeviceRequestDTO confirmDeviceRequestDTO = (ConfirmDeviceRequestDTO) obj;

		String mobilePhone = confirmDeviceRequestDTO.getMobilePhone();
		String sn = confirmDeviceRequestDTO.getSn();

		ConfirmDeviceResponseDTO confirmDeviceResponseDTO = new ConfirmDeviceResponseDTO();
		if (sessio != null) {
			setSession(request.getRemoteAddr(), session.getId(), sessio
					.getMobilephone());
			message = DATAPARSINGMESSAGE;

			BigDecimal id = new BigDecimal(sessio.getId());
			PmsPosInfo pmsPosInfo = pmsPosInfoDao.selectBusinessPos(sn);

			PmsMerchantPos p = new PmsMerchantPos();
			p.setPosid(pmsPosInfo.getId());
			List<PmsMerchantPos> searchList = pmsMerchantPosDao.searchList(p);

			if (searchList != null && searchList.size() > 0) {
				PmsMerchantPos merchantPos = searchList.get(0);
				if (merchantPos != null) {
					BigDecimal merchantid = merchantPos.getMerchantid();
					if (merchantid.compareTo(id) == 0) {
						confirmDeviceResponseDTO.setStatus("1");
					} else {
						confirmDeviceResponseDTO.setStatus("2");
					}
				} else {
					confirmDeviceResponseDTO.setStatus("2");
				}
				message = SUCCESSMESSAGE;
			} else {
				message = FAILMESSAGE;
			}
		} else {
			message = RetAppMessage.SESSIONINVALIDATION; // 会话失效
		}
		String retCode = message.split(":")[0];
		String retMessage = message.split(":")[1];
		if (retMessage.equals("initialize")) {
			retMessage = "服务器异常";
		} else if (retMessage.equals("success")) {
			retMessage = "确认设备认证成功";
		} else if (retMessage.equals("dataParsing")) {// 6
			retMessage = "数据解析错误";
		} else if (retMessage.equals("sessionInvalidation")) {// 7
			retMessage = "会话失效，请重新登录";
		} else if (retMessage.equals("fail")) {
			retMessage = "确认设备认证失败";
		}
		confirmDeviceResponseDTO.setRetCode(Integer.parseInt(retCode));
		confirmDeviceResponseDTO.setRetMessage(retMessage);
		logger.info("[app_rsp]" + createJson(confirmDeviceResponseDTO));
		return createJsonString(confirmDeviceResponseDTO);
	}

	/**
	 * 确认设备认证异常
	 * 
	 * @throws Exception
	 */
	@Override
	public Object confirmDeviceException() throws Exception {
		ConfirmDeviceResponseDTO dto = new ConfirmDeviceResponseDTO();
		dto.setRetCode(100);
		dto.setRetMessage("系统异常");
		dto.setStatus(null);
		return createJsonString(dto);
	}
}