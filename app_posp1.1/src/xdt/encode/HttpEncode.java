package xdt.encode;

import org.springframework.stereotype.Repository;

import java.security.InvalidKeyException;

@Repository
public class HttpEncode {
	
	// 加密
	public String createEncode(String json) throws InvalidKeyException{
			return null;
	}

	// 解析
	public String parseDecode(String encodeStr) throws InvalidKeyException{
		return "";
	}
}
