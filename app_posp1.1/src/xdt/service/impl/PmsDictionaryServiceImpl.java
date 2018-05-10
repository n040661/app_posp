package xdt.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import xdt.dao.IPmsDictionaryDao;
import xdt.dto.CardTypeQueryResponseDTO;
import xdt.model.PmsDictionary;
import xdt.service.IPmsDictionaryService;

@Service("pmsDictionaryService")
public class PmsDictionaryServiceImpl extends BaseServiceImpl implements
		IPmsDictionaryService {

	@Resource
	private IPmsDictionaryDao pmsDictionaryDao; // 数据字典服务层
	private Logger logger = Logger.getLogger(PmsDictionaryServiceImpl.class);

	/**
	 * 查询证件类型
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public String cardTypeQuery() throws Exception {
		logger.info("费率查询");

		List<PmsDictionary> list = new ArrayList<PmsDictionary>();
		String message = INITIALIZEMESSAGE;

		PmsDictionary pmsDictionary = new PmsDictionary();
		pmsDictionary.setType("cardType");
		list = pmsDictionaryDao.searchList(pmsDictionary);

		if (list != null && list.size() > 0) {
			message = SUCCESSMESSAGE;
		} else {
			message = FAILMESSAGE;
		}

		// 解析要返回的信息
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		if (retMessage.equals("initialize")) {
			retMessage = "系统初始化";
		} else if (retMessage.equals("success")) {
			retMessage = "查询成功";
		} else if (retMessage.equals("fail")) {
			retMessage = "查无结果";
		}

		CardTypeQueryResponseDTO responseData = new CardTypeQueryResponseDTO();
		responseData.setRetCode(retCode);
		responseData.setRetMessage(retMessage);
		responseData.setList(list);
		String jsonString = createJsonString(responseData);
		logger.info("[app_rsp]" + createJson(responseData));
		return jsonString;
	}

	/**
	 * 查询证件类型异常
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public String cardTypeQueryException() throws Exception {
		CardTypeQueryResponseDTO responseData = new CardTypeQueryResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");
		responseData.setList(null);

		logger.info("[app_rsp]" + createJson(responseData));

		return createJsonString(responseData);
	}

}