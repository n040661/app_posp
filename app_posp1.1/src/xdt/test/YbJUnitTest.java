package xdt.test;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yeepay.g3.facade.yop.ca.dto.DigitalEnvelopeDTO;
import com.yeepay.g3.facade.yop.ca.enums.CertTypeEnum;
import com.yeepay.g3.frame.yop.ca.DigitalEnvelopeUtils;
import com.yeepay.g3.sdk.yop.utils.InternalConfig;

import xdt.service.ITotalPayService;
import xdt.service.impl.TotalPayServiceImpl;

public class YbJUnitTest {

	// 测试异步回调
	public static void main(String[] args) {

		// String
		// response="NlylsGELAg2o8X2NqD9qf7kZdiiewcyUZ1PYQnxOFMVMtdmOyI0p2eY6agNSxhXu0sqxaCkoVS2vkBsF_mDQOLryIoT4D9tV3SwElT1uwB7z8vb4HpDewjVY10R1fG_LWjTZ7zU1pdX_q0uAlJOQfeARQmnt_72yGBHVAeBpKIi0Q7F6fgzZCM8HmLi2JJQkqxoEr5ZkP5EDR9DxJH1CKca9p0ezcM34mRVc7S_8XCNRrEGkCq1dyUwdrgrCNhyrVSTFKOAkjvO8xwWDhgwgnHzq9oP0xI8jVx-tcDtYRmxtxhOIwWhqn4LGWXYsAT4Fm61PPTgXVIivye0lbhliZA$8LVaWiT2RB660xY2EIxDttoH0IVYHuHSAnZ_VdVVk3iFZTCmSfjCOWn0NlON6pBNUgXizlpRtjU1sTWSyP7HRXK5uSe57u2u7diaGr-C-VBlitGM7ooxu_8WqrmRVWI9s1h9Dz-IEV-l5s9H1A-Mre6iolgORDlusqlTAr_Bn92fME5oqyRkyQs14b4poyzeXGWTYRkJLhmS-rvKQanqPHkzWqcwMU_gEMSCljjUjMTwB-S5wh5NboGVOoidnB3pSQpU2NyfPKG7Lv5jd6_1hcOx581Cv0XJg5lCgKNfLyho7HNKQCEhW38s9IqEjnjP4d2tpWMueueLU3TXONKh4YZsx2_MrA0g0uzrZjwLjSmgTAFVAULNVTMnCQnfLQtoLx-OYgaLTumEr2A9qtYgC5mm0-JobRn0tNeU6aTOvvqtHiUqHxVx6DIDPxFBVNspiwi4-4naiQiNj2H5w__AC5LYWqtnLr_uNFYqHhDvl9srUnCBl23OVFWfl0uPX2QOwi4ckHUy7hPTsXXNUngqO5FqTjpIzcslKfkBuZwH8Ulgcg_Vw1rADtvQKrFIHLw2GVxUYfCLpt82U5ymT6zgCbOVIf66nrUZxkwotUnh1V0hPU2FoTWe-HS6hstF2gpY-BY-TbZuervpFzmp2IKnISrbgUSbYDkaWFsfO2yc4wt7v7DnQppeHr_3lYZEv4-q6bbijX0WzvLDRlNqA1dqnRbOLTdo9gg4fhhWXiElpMua08eIcbvh5-yCB3wlZJm68SuwlkqY7KyV5TElr60JNg$AES$SHA256";
		String response="lqKcj5JyK3CriSUSEW7FV_RjeLD8uQ8jMT3b-V3BUteCLcCSpUxWcMYiKrI4ZbsZzRaMppxiyigRNIdoeGC9x-ZIqur7wLWIuDjUpt13-MB8H1GfF-pCNaK_VN-T6ZUZUmzfZ-g8WJo6Nyg4y8EF-r-rif7AenpC6AoU5yw49G2d-VP_4YN66d4Kac5jo6bFAC8Jdq8zIb7MCa1JFkaHSXC5z3Nov4zHNO1K4YGzB0oLFyvZhm1JbMkBqfwRnuq1c63q7Jr0gnZaRTrJl3RGqy8HHntTVa6jLvzfCJlvojtnrH3L81C2oZTeXkuVk5bUefUXtaRCqpT5jRvwjgUcdw$Gkq2eEVT52gpbjuHntOt0rNsf8oguY7Hihdhi_aRZVYagWD-9EcvGC1JNJgdp45xP1lR3y5eTTRjTUVRh964Ll8KXxc_KNtNiAMD38AG2cz7pXbgvboFMw04XNqEgdJewKE8_oIlchxTYrKke1GsOLDKcZ085a4X_iunDpqANnrkfPIpSWxeOnpqfanlXplCJJx3rUuz5Nsh9YqI5LUkixEXuBowCLmIdthCHTaqE8gVSSdj2pG5ZurejOz9TO6D9dslq6d3LfvCvbX422bHI7aKw5KUPZYNLuOmCX3WU0NVpWnzbQ7mWKWzKtWExWnc-2qss4VeXHYLP93SalvETUM633zEOXx7HiV4hLnAtaWS_X86cyA9gtxbHbpvnPkLSzZzAvVn_aRS-BfilWJs8uqCHTyCtJ9o5Oh42bBiCNH0fw4xHUB3OHfQuK0VwHRX9ylabQs277qErx1RYJDN8RVJJ2Pakblm6t6M7P1M7oNvusyKko7JsKKZCGrr1QOUcU3ykuws7DDim7sfAGiUOU2F-J8reSqVGUzYBMO5BqLL-nX72boe6ueUZLPUZYkGgtcUeW4vH53czDJXPTqmHQNDB54K_zy57m1wmSQSYlx0azcfZaOHtZn9odS_mGPTllreF02d8PdmhSBV61hW0NK2feGUlpXW48OcNTO1ycrdogUOknOFdMeqMAaxKQP-z1xmKMLE9mwM9VHC_pys68lpoDcUmRde4F-_Ep5bKP2JhKclOVTpnZkPJhJKDb7wXHqti2g7QscyCVK-aT4VCZYcx5g1zzNn0O9wBoR8qjkHInO1zh88G0_uzPYDQJeFGkUsdeiMY6d02VNhv9tG4iiOO6hm-bReLkX3M99aSMtIhdD2k86kSvPYyx30HH5uxMWaQ8GKS1dN-uah-7J6cR0Xzh7qv_Y3bES0ts70pjLLeP_JBhejOSjHZft3Rvo7kFLhi_b_zY1fX5DHAkLJbrcL1Xc0bjcWpSlyaKZVAEP1_M7vk4KY_j2-fqTwSSAVjktGjCEPJKTmibSFhjvRD4ktrlaagjO7CebCMcTkJVABmX6YQt-TwpQTo83BzgCDenfSPUrRoj29ALbaXtDprA$AES$SHA256";
//		String response = "OQwt0Bd3yyNCYLCiETVnNSbJD6Z8KMUHh_gKobWkuZAhf-nd7nvg3lKBHOEp3dvV6kd4Ue-tuz7uxsf-6qVXgOnYIaSXKg7F94DJs2STvz5UX4w8HtZetLJ5NjKOCuf4N6NBrIPccbBuGgaeuBshxwN17TqEibNUdelx-XIQGmQValOPwLpQfvvvPlIO-z0FgIpxHZrveCEzZ8FCwu8bn6JHdJcoh9c0UpwVgVO10NFuVDbSm2WfN3j36aUN6nyJXEh06qkTYUmecoRYHVdpqz4jdVwMeX2knJ_cNTPcrhnn33fNJh72lxHI_MAmlSqUd8DcJ_hTUM-Ry8-FLjKvCw$SHA256";
		try {
			// 开始解密
			Map<String, String> jsonMap = new HashMap<>();
			DigitalEnvelopeDTO dto = new DigitalEnvelopeDTO();
			dto.setCipherText(new String(response.getBytes("ISO-8859-1"),"UTF-8"));
			// InternalConfig internalConfig = InternalConfig.Factory.getInternalConfig();
			PrivateKey privateKey = InternalConfig.getISVPrivateKey(CertTypeEnum.RSA2048);
			System.out.println("privateKey: " + privateKey);
			PublicKey publicKey = InternalConfig.getYopPublicKey(CertTypeEnum.RSA2048);
			System.out.println("publicKey: " + publicKey);

			dto = DigitalEnvelopeUtils.decrypt(dto, privateKey, publicKey);
			System.out.println("-------:" + dto.getPlainText());
			jsonMap = parseResponse(dto.getPlainText());
			System.out.println(jsonMap);
		} catch (Exception e) {
			throw new RuntimeException("回调解密失败！");
		}
//		Map<String, String> result=new HashMap<String,String>();
//		String merid="10052279577";
//		String number="31516192911718";
		//TotalPayServiceImpl total=new TotalPayServiceImpl();
		//service.cxQuick(merid, number, result);

	}

	public static Map<String, String> parseResponse(String response) {

		Map<String, String> jsonMap = new HashMap<>();
		jsonMap = JSON.parseObject(response, new TypeReference<TreeMap<String, String>>() {
		});

		return jsonMap;
	}
}
