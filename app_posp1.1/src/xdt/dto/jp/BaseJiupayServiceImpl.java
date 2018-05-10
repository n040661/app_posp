package xdt.dto.jp;
/*    */
/*    */ import com.jiupai.paysdk.entity.BaseDTO;
/*    */ import com.jiupai.paysdk.entity.enums.Service;
/*    */ import com.jiupai.paysdk.service.interfaces.BaseJiupayService;
/*    */ import com.jiupai.paysdk.utils.http.HttpUtils;
/*    */ import com.jiupai.paysdk.utils.sign.RSASignUtil;
/*    */ import com.jiupai.paysdk.utils.util.BeanUtil;
/*    */ import java.text.DateFormat;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Date;
/*    */ import java.util.Map;
/*    */ import org.apache.commons.lang3.StringUtils;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */
/*    */ public class BaseJiupayServiceImpl/*    */ implements BaseJiupayService
/*    */ {
	/* 24 */ private static final Logger logger = LoggerFactory.getLogger(BaseJiupayServiceImpl.class);
	/*    */ private static final String CHARSET = "UTF-8";
	/*    */ private static final String PATTERN = "yyyyMMddhhmmss";
	/*    */
	/*    */ public String doSend(Service service, BaseDTO req, String merchantCertPath, String merchantCertPass,
			String rootcerPath, String url)
	/*    */ {
		/*    */ try
		/*    */ {
			/* 32 */ initReq(req);
			/* 33 */ String rootPath =  rootcerPath;
			/* 34 */ req.setService(service.getService());
			/* 35 */ String buf = BeanUtil.objToString(req);
			/* 36 */ RSASignUtil rsaSignUtil = new RSASignUtil(merchantCertPath, merchantCertPass);
			/* 37 */ rsaSignUtil.setRootCertPath(rootPath);
			/* 38 */ String sign = rsaSignUtil.sign(buf, "UTF-8");
			/* 39 */ String cert = rsaSignUtil.getCertInfo();
			/* 40 */ buf = buf + "&merchantCert=" + cert + "&merchantSign=" + sign;
			/* 41 */ String res = HttpUtils.sendAndRecv(url + service.getUrl(), buf, "UTF-8");
			/* 42 */ Map resMap = BeanUtil.strToMap(res);
			/* 43 */ String serverCert = (String) resMap.get("serverCert");
			/* 44 */ String serverSign = (String) resMap.get("serverSign");
			/* 45 */ resMap.remove("serverCert");
			/* 46 */ resMap.remove("serverSign");
			/* 47 */ res = BeanUtil.mapToStr(resMap);
			/* 48 */ if (!(rsaSignUtil.verify(res, serverSign, serverCert, "UTF-8"))) {
				/* 49 */ return "验签失败";
				/*    */ }
			/* 51 */ return BeanUtil.mapToJson(resMap);
			/*    */ } catch (Exception e) {
			/* 53 */ logger.info("调用paySdk服务发生异常", e);
			/*    */ }
		/* 55 */ return service.getDesc() + "调用失败";
		/*    */ }
	/*    */
	/*    */ private void initReq(BaseDTO req) {
		/* 59 */ Date date = new Date();
		/* 60 */ DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
		/* 61 */ String time = dateFormat.format(date);
		/* 62 */ if (StringUtils.isBlank(req.getVersion()))
			/* 63 */ req.setVersion("1.0");
		/* 64 */ req.setCharset("02");
		/* 65 */ req.setSignType("RSA256");
		/* 66 */ req.setRequestId(time);
		/* 67 */ req.setRequestTime(time);
		/*    */ }
	/*    */ }