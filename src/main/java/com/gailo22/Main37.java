package com.gailo22;

import java.util.Arrays;
import java.util.Random;

public class Main37 {
	
	public static void main(String[] args) {
		int[] nums = new Random().ints(0, 100).limit(10).toArray();
		insertionSort(nums);
		System.out.println(Arrays.toString(nums));
	}

	private static void insertionSort(int[] nums) {
		for (int i=1; i<nums.length; i++) {
			int j = i;
			while (j>0 && nums[j] < nums[j-1]) {
				swap(nums, j, j-1);
				j--;
			}
		}
	}

	private static void swap(int[] nums, int i, int j) {
			int temp = nums[i];
			nums[i] = nums[j];
			nums[j] = temp;
	}
	
}
