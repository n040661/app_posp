package xdt.schedule;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 处理调用第三方接口查询结果
 * wumeng 20150515
 */
public class ThreadPool {
	private static ThreadPoolExecutor executor ;
	static {
		if(executor == null){
			
			executor = new ThreadPoolExecutor(10, Integer.MAX_VALUE, 30, TimeUnit.SECONDS,new SynchronousQueue<Runnable>());
			
		}
	}
	
	public static void executor(Thread task){
		executor.execute(task);
	}

}
