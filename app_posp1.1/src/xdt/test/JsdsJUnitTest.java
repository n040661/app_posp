package xdt.test;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.junit.Test;

import junit.framework.TestCase;
import xdt.util.RSAUtil;

public class JsdsJUnitTest extends TestCase{

	@Test
	public void testUpdateHandle() {
		fail("Not yet implemented");
	}

	@Test
	public void testHandleNofity() {
		fail("Not yet implemented");
	}

	@Test
	public void testHandleQuery() {
		fail("Not yet implemented");
	}

	@Test
	public void testSend() {
		fail("Not yet implemented");
	}

	@Test
	public void testSends() {
		fail("Not yet implemented");
	}

	@Test
	public void testEntityToString() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateDaifu() {
		fail("Not yet implemented");
	}

	@Test
	public void testOtherInvoke() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetOriginOrderInfo() {
		fail("Not yet implemented");
	}

	@Test
	public void testSelectKey() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdatePmsMerchantInfo() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdatePmsMerchantInfo449() {
		fail("Not yet implemented");
	}

	@Test
	public void testRegister() {
		fail("Not yet implemented");
	}

	@Test
	public void testSelectKeyUrl() {
		fail("Not yet implemented");
	}

	@Test
	public void testAdd() {
		fail("Not yet implemented");
	}
   public static void main(String[] args) {
	   try {
	   String aa="G5HsQekcfT38v8MUHNVIQSiDB0NVBAt15Fwv7u+F2zMlncVrgnM0CbjJvoGaUREMSbNQLbsUMLEb8NYCy154LlQTHo7dDGKhGTkclXmUgjmw38V7/oLnmD01V5SWlbh/2ilPVKcf2K+so5bZM/qOhiYZxeJvhRyLUJqgkG33aEg=";

		String baseSign = URLDecoder.decode(aa,"UTF-8");
		 String bb=baseSign.replace(" ", "+");
		byte[] a = RSAUtil.verify("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCQAhZC1XB8jVMybKzBVmptEc6Mj9rZBcDWMtlitPiO4ncRwczB8giK8ZzUescmE2a7dAzxdYtzBCD7N3jMNHtW60Wf4pxv9YmwOn988ZRZjeVthVq6hOhuaE6bmj7KO2RK/1Ub8yXrglDMHdc3FxNqOVibXxxBRfivJ7zXCzUg1QIDAQAB",RSAUtil.base64Decode(bb));

		String Str = new String(a);
		
		System.out.println(Str);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}
	
}
