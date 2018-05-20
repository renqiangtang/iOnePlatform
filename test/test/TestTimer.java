package test;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

class hello extends Thread {
	public hello() {
	}

	public void run() {
		try {
			System.out.println(".thread id." + Thread.currentThread().getId()
					+ " begin:" + (new Date()).getTime());
			Thread.currentThread().sleep(10000000);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

class timerTask extends TimerTask {

	public void run() {
		hello h = new hello();
//		System.out.println(".thread id:" + Thread.currentThread().getId()
//				+ " thread start begin:" + (new Date()).getTime());
		h.start();
		// System.out.println(".thread id:" + Thread.currentThread().getId()
		// + " thread  end begin:" + (new Date()).getTime());
	}

}

public class TestTimer {

	public static void main(String[] args) throws Exception {
		Timer t1 = new Timer(true);
		Date now = new Date();
		final Date start = new Date(now.getTime() + 1000);
		System.out.println("start:" + start.getTime());
		for (int i = 0; i < 1000; i++) {
			t1.schedule(new timerTask(), start);
		}
		System.out.println("end begin time:" + (new Date()).getTime());
		Thread.currentThread().sleep(1000000000);
	}
}
