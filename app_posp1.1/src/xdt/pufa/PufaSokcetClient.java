package xdt.pufa;



import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.log4j.Logger;

import xdt.util.Global;

public class PufaSokcetClient {
	private static Logger logger = Logger.getLogger(PufaSokcetClient.class);
	
	/**
	 * ********************************************************
	 * 
	 * @Title: connServer
	 * @Description:
	 * @return byte[]
	 * @throws AshException
	 * @throws AshException
	 * @date 2013-5-9 上午01:11:37
	 ******************************************************** 
	 */
	public byte[] connServer(byte[] reqPack) throws Exception {
//		String ipAddress = Global.getConfig("pufaIpAddress");
//		int port = Integer.valueOf(Global.getConfig("pufaPort"));
		String ipAddress = Global.getConfig("pufa.IpAddress");
		int port = Integer.valueOf(Global.getConfig("pufa.Port"));
		byte[] respPack = null;
		Socket s = null;
		OutputStream os = null;
		InputStream is = null;
		try {
			s = new Socket(); // 建立与服务器端的链接
			logger.info("建立与socket服务器端的链接" + ipAddress +":"+port);
			s.connect(new InetSocketAddress(ipAddress, port), 30000); // 连接超时设置
			s.setSoTimeout(50000); // 读写超时设置
			logger.info("socket服务器端的读写超时 时间为 50000");
			// 2得到socket读写流
			os = s.getOutputStream();
			// 输入流
			is = s.getInputStream();
			// 3利用流按照一定的操作，对socket进行读写操作
			os.write(reqPack);
			logger.info("写数据");
//			 s.shutdownOutput();
			// 接收服务器的响应

			byte[] lenByte = new byte[4];
			is.read(lenByte, 0, 4);
			int len = Integer.parseInt(LoUtils.asciiToString(lenByte));
			byte[] bb = new byte[len];
			@SuppressWarnings("unused")
			int size = is.read(bb, 0, len);
			logger.info("读取数据");
			respPack = new byte[lenByte.length + bb.length];

			System.arraycopy(lenByte, 0, respPack, 0, 4);
			System.arraycopy(bb, 0, respPack, lenByte.length, bb.length);
			logger.info("已取到响应数据，连接正常");
			//正常执行到此处，证明已取到响应数据，连接正常
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("连接出现错误");
		} finally {
			try {
				if (is != null) {
					is.close();
					logger.info("输入流，关闭");
				}
				if (os != null) {
					os.flush();
					os.close();
					logger.info("输出流，关闭");
				}
				if (s != null) {
					s.close();
					logger.info("socket，关闭");
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return respPack;
	}
	
}

	