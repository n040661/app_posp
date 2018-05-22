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
	   String aa="iyMScJch9P8QyqMbPBjlbnFMXfUsOm66G1zTK1vcJFLfCG46KoyXVyk0LMa35Fs9H83ZEbsdqpKc%207Ge/FRZs4zsBm7Oda4x2WnNmXE7iLDgqD8xmfq1VfGs5JOg753yG227+WaQjKtBZfGotVRHcoaKt%20yZAF6PVMQFR2VU6gBNE=";

		//String baseSign = URLDecoder.decode(aa,"UTF-8");

       
		String baseSign = aa.replace(" ", "+");
		 String bb=baseSign.replace("%20", "");
		 //String cc="iyMScJch9P8QyqMbPBjlbnFMXfUsOm66G1zTK1vcJFLfCG46KoyXVyk0LMa35Fs9H83ZEbsdqpKc 7Ge/FRZs4zsBm7Oda4x2WnNmXE7iLDgqD8xmfq1VfGs5JOg753yG227 WaQjKtBZfGotVRHcoaKt yZAF6PVMQFR2VU6gBNE=";
		 
//		 System.out.println(cc.replaceAll(" ", ""));
//		 if(bb.equals(cc.replaceAll(" ", "")))
//		 {
//			 System.out.println("123333333");
//		 }
		byte[] a = RSAUtil.verify("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSUnSUG5I3Xh2ANLpC5xLe96WCVQG+A5iPBKPqRKBcF2OCdCtwNs8X40nyqYnVWqhkZwGiItT4+wFc04boL1Az01UJiZBLqmOumU0mxyyKCqGwFZakl3LWI4u2IBDuwyde3muXZDWtSDBH1k2BKzOHju3eeSicZu5D7SQ1Hol7AwIDAQAB",RSAUtil.base64Decode(bb));

		String Str = new String(a);
		
		System.out.println(Str);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}
	
}
