package xdt.model;
/**
 * synchronized修饰方法时：
 * 锁的是this(当前对象)，不是代码块。
 * 尽量用synchronized修饰的代码块，而不是方法。
 * 因为synchronized代码块时，可以尽可能少的同步语句。
 * 同步时，非常浪费资源，如果语句过多，就会对速度很有影响。
 * 所以要求，写进synchronized修饰的代码块中的语句尽可能少。
 * 而synchronized修饰的方法，因为可能有时需要用到变量，
 * 	就必须在方法里定义，所以方法里面的语句容易过多，影响速度。
 * @author Administrator
 */
public class RunnaDemo2 implements Runnable {
	private int ticket = 200;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			sell();
		}
	}
	
	public synchronized void sell(){
		if(ticket>0){
//			System.out.println(Thread.currentThread().getName()
//					+ "�����ǵ�" + ticket-- + "��Ʊ");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RunnaDemo2 d = new RunnaDemo2();//d��ͬ���������� �ڰ���ͬ����������this
		
		//��������
		new Thread(d).start();
		new Thread(d).start();
		new Thread(d).start();
		new Thread(d).start();
	}
}
