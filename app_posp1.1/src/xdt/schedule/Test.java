package xdt.schedule;

import xdt.util.utils.UtilThread;

public class Test {

	public static void main(String[] args) {
		for (int i = 0; i < 200; i++) {
			ThreadPool.executor(new TestTread(i));
		}
	}
}
