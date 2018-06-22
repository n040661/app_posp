package xdt.dto.scanCode.util;

import javax.net.ssl.KeyManagerFactory;

/**
 * <b>功能说明:</b>
 */
public class ClientKeyStore {
	private KeyManagerFactory keyManagerFactory;

	ClientKeyStore(KeyManagerFactory keyManagerFactory) {
		this.keyManagerFactory = keyManagerFactory;
	}

	KeyManagerFactory getKeyManagerFactory() {
		return keyManagerFactory;
	}
}
