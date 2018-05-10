package xdt.quickpay.cjt.temp;

import java.io.IOException;
import java.io.InputStream;

public class Common {
			
		public static String getPostData(InputStream in, int size, String charset) {
			if (in != null && size > 0) {
				byte[] buf = new byte[size];
				try {
					in.read(buf);
					if (charset == null || charset.length() == 0)
						return new String(buf);
					else {
						return new String(buf, charset);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		} 

}
