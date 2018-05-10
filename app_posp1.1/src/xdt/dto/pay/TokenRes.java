package xdt.dto.pay;

import java.io.Serializable;

public class TokenRes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String token; // 令牌

	/**
	 * 令牌
	 * 
	 * @return
	 */
	public String getToken() {
		return token;
	}

	/**
	 * 令牌
	 * 
	 * @param token
	 */
	public void setToken(String token) {
		this.token = token;
	}

}
