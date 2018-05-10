package xdt.schedule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 处理调用第三方接口查询结果
 * wumeng 20150515
 */
public class ThreadPools {
	private static ExecutorService executor ;
	static {
		if(executor == null){
			
			executor = Executors.newScheduledThreadPool(200);
		}
	}
	public static void executor(Thread task){
		executor.execute(task);
	}

}
