package xdt.controller;




import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;







import com.alibaba.fastjson.JSON;

import xdt.dto.SubmitOrderNoCardPayResponseDTO;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.MsgBean;
import xdt.quickpay.hengfeng.util.HFSignUtil;
import xdt.quickpay.hengfeng.util.PreSginUtil;
import xdt.service.IClientCollectionPayService;
import xdt.tools.Constants;
import xdt.util.client.MD5;
import xdt.util.client.SslConnection;

@Controller
@RequestMapping("/clientCollectionPayController")
public class ClientCollectionPayController extends BaseAction{
	Logger log =Logger.getLogger(ClientCollectionPayController.class);
	@Resource
	private IClientCollectionPayService clientCollectionPayService;
	
	@RequestMapping(value="paySign")
	public void sign(MsgBean req_bean,HttpServletResponse response,HttpServletRequest request) throws IOException {
		log.info("req_bean:"+JSON.toJSONString(req_bean));
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(req_bean.getMERCHANT_ID());
		String merchantKey = keyinfo.getMerchantkey();
		HFSignUtil signUtil = new HFSignUtil();
		String sign =signUtil.sign(PreSginUtil.paydaifuResultString(req_bean), merchantKey);
		req_bean.setSIGN(sign);
		System.out.println(sign);
		request.getSession().setAttribute("req_bean", req_bean);
		//response.sendRedirect("/pay/yilian/daifu.jsp");
		try {
			outString(response, JSON.toJSON(req_bean));
			//request.getRequestDispatcher("/index.jsp").forward(request, response);
		} catch (Exception e) {
			log.info("异常："+e);
			e.printStackTrace();
		} 	
	}
	
	@RequestMapping(value="paySigns")
	public void pay(MsgBean msgBean,HttpServletResponse response,HttpServletRequest request) throws IOException {
		log.info("SN:"+msgBean.getMAP());
		log.info("JSON-SN:"+JSON.toJSON(msgBean));
		//String message = "0:initialize";
		String jsonString = null;
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(msgBean.getMERCHANT_ID());
		SubmitOrderNoCardPayResponseDTO responseDTO = new SubmitOrderNoCardPayResponseDTO();
		String merchantKey = keyinfo.getMerchantkey();

		HFSignUtil signUtil = new HFSignUtil();
		if (!signUtil.verify(PreSginUtil.paydaifuResultString(msgBean), msgBean.getSIGN(), merchantKey)) {
			responseDTO.setRetCode(11);
			responseDTO.setRetMessage("签名错误");
			jsonString = createJsonString(responseDTO);
			log.info("签名错误");
			try {
				outString(response,JSON.toJSONString(jsonString));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		msgBean.setVERSION("2.1");
		msgBean.setMSG_TYPE("100001");
		msgBean.setUSER_NAME(Constants.user_name);
		
		log.info(JSON.toJSON(msgBean));
		String res_bean = null;
		try {
			res_bean = clientCollectionPayService.pay(msgBean,responseDTO, jsonString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("res_bean:"+JSON.toJSON(res_bean));
		//log.info("给下游输出的数据:"+outString(response,JSON.toJSON(res_bean)));
	     outString(response,JSON.toJSON(res_bean));
		
		
	}
	@RequestMapping(value="select")
	public void select(MsgBean msgBean,HttpServletResponse response) throws IOException{
		HFSignUtil signUtil = new HFSignUtil();
		String jsonString = null;
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(msgBean.getMERCHANT_ID());
		SubmitOrderNoCardPayResponseDTO responseDTO = new SubmitOrderNoCardPayResponseDTO();
		String merchantKey = keyinfo.getMerchantkey();
		if (!signUtil.verify(PreSginUtil.paydaifuResultString(msgBean), msgBean.getSIGN(), merchantKey)) {
			responseDTO.setRetCode(11);
			responseDTO.setRetMessage("签名错误");
			jsonString = createJsonString(responseDTO);
			log.info("签名错误");
			outString(response,JSON.toJSONString(jsonString));
			return;
		}
		MsgBean req_bean = new MsgBean();
		req_bean.setVERSION("2.1");
		req_bean.setMSG_TYPE("100002");
		req_bean.setBATCH_NO(msgBean.getBATCH_NO());//同代付交易请求批次号
		req_bean.setUSER_NAME(Constants.user_name);//系统后台登录名
		String res = clientCollectionPayService.sendAndRead(clientCollectionPayService.signANDencrypt(req_bean));

		MsgBean res_bean = clientCollectionPayService.decryptANDverify(res);
		outString(response, JSON.toJSON(res_bean));
	}
	private String createJsonString(SubmitOrderNoCardPayResponseDTO responseDTO) {
		return gson.toJson(responseDTO);
	}
	@RequestMapping(value="downloadPay")
	public void DownloadPay(String trans_date,String merchant_no,HttpServletResponse response) throws IOException{
		Map<String, String> map =new HashMap<>();
		HttpURLConnection conn = null;
		FileOutputStream fos = null ;
		try {
		String merchant_key = "E661095F2DFA46AE";
		String msg_type = "100004";
		String org_mac=msg_type +" " + merchant_no +" " + trans_date+" " +merchant_key;
		System.out.println("MAC源码："+org_mac);
		MD5 md5 = new MD5();
		String MAC = md5.getMD5ofStr(org_mac);
		
		String url = "https://agent.payeco.com/download?MSG_TYPE="+msg_type+"&MERCHANT_NO="+merchant_no+"&TRANS_DATE="+trans_date+"&MAC="+MAC;
		System.out.println("URL:"+url);
		
		//SSL
		SslConnection ssl = new SslConnection();
		conn = ssl.openConnection(url); 
		conn.setRequestMethod("POST");
		conn.setReadTimeout(60000);
		conn.setConnectTimeout(60000);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		InputStream instream = conn.getInputStream();	
		
		OutputStream toClient = new BufferedOutputStream(  
                response.getOutputStream());  
		byte[] buffer = new byte[256];  
		int len = 0;
		while((len = instream.read(buffer)) != -1){
	        response.addHeader("Content-Disposition", "attachment;filename=daifu.txt" );  
	        
	        response.setContentType("application/vnd.ms-excel;charset=gb2312");  
	        toClient.write(buffer,0,len);  
		}
		instream.read(buffer);  
		instream.close();  
        toClient.flush();  
        toClient.close();  
		
		/*fos = new FileOutputStream("D:\\Test\\ss.txt");
		byte[] buf = new byte[1024];
		
		int len = 0;
		
		while((len = instream.read(buf)) != -1){
			fos.write(buf, 0, len);
		}
		fos.flush();
		fos.close();	*/
		map.put("msg", "下载成功");
		//outString(response,JSON.toJSON(map));
		System.out.println("下载成功！");
		
	}catch (Exception e) {
		e.printStackTrace();
		map.put("msg", "下载失败");
		System.out.println("下载失败！");
	}finally{
		if(conn != null)
			conn.disconnect();
		
	}
	}
}
