package xdt.dto.scanCode.util;

import javax.net.ssl.TrustManagerFactory;

/**
 * <b>功能说明:
 * </b>
 */
public class TrustKeyStore {
	private TrustManagerFactory trustManagerFactory;

	TrustKeyStore(TrustManagerFactory trustManagerFactory) {
		this.trustManagerFactory = trustManagerFactory;
	}

	TrustManagerFactory getTrustManagerFactory() {
		return trustManagerFactory;
	}
}
