package com.gailo22;

public class Main18 {
	
	public static void delay() {
		try {
			Thread.sleep((int) (Math.random() * 10));
		} catch (InterruptedException ie) {}
	}
	
	public static void main(String[] args) {
		int[] x = {0};
		boolean[] hold = {true};
		
		new Thread(() -> {
			delay();
			x[0] = 99;
			hold[0] = false;
		}).start();
		
		new Thread(() -> {
			delay();
			while (hold[0])
				;
			System.out.println("value is "  + x[0]);

		}).start();
		
	}

}
