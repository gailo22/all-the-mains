package com.gailo22;

public class Main32 {
	
	public static void main(String[] args) {
		int[] numbers = {1, 2, 4, 7, 11, 15};
		System.out.println(hasPairWithSum(numbers, 15));
		System.out.println(hasPairWithSum(numbers, 10));
	}
	
	
	static boolean hasPairWithSum(int[] numbers, int sum) {
		boolean found = false;
		
		int right = numbers.length - 1;
		int left = 0;
		
		while (right > left) {
			int curSum = numbers[left] + numbers[right];
			if (curSum == sum) {
				found = true;
				break;
			} else if (curSum > sum){
				right--;
			} else {
				left++;
			}
		}
		
		return found;
	}

}
