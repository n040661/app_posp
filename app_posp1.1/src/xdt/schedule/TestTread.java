package xdt.schedule;

public class TestTread extends Thread {
	
	private Integer num;
	
	
	public TestTread(Integer num) {
		super();
		this.num = num;
	}


	@Override
	public void run() {
		try {
		for (int i = 0; i < 10; i++) {
			System.out.println("第"+num+"个执行第"+i+"次。");
				Thread.sleep(5000);
		}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
