package xdt.common.security;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class XDTStreamOperator {
	private static final int READBUF_SIZE = 1024;

	public static byte[] getInputStreamBytes(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		copy(is, baos);
		close(baos);
		return baos.toByteArray();
	}

	public static void copy(InputStream is, OutputStream os) throws IOException {
		byte[] buf = new byte[1024];
		try {
			int size;
			while ((size = is.read(buf)) != -1) {
				os.write(buf, 0, size);
			}
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		}
	}

	public static void close(Closeable stream) {
		if (stream == null)
			return;
		try {
			stream.close();
		} catch (IOException localIOException) {
		}
	}
}