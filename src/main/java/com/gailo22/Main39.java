package com.gailo22;

import java.util.Arrays;
import java.util.Random;

public class Main39 {
	
	public static void main(String[] args) {
		int[] nums = new Random().ints(0, 100).limit(10).toArray();
		quickSort(nums);
		System.out.println(Arrays.toString(nums));
	}

	private static void quickSort(int[] nums) {
		int len = nums.length;
		quickSort(nums, 0, len - 1);
	}

	private static void quickSort(int[] nums, int start, int end) {
		if (start < end) {
			int pIndex = partition(nums, start, end);
			quickSort(nums, start, pIndex - 1);
			quickSort(nums, pIndex + 1, end);
		}
	}

	private static int partition(int[] nums, int start, int end) {
		int pIndex = start;
		int pivot = nums[end];
		
		for (int i=start; i<end; i++) {
			if (nums[i] < pivot) {
				swap(nums, i, pIndex);
				pIndex++;
			}
		}
		
		swap(nums, pIndex, end);
		
		return pIndex;
	}

	private static void swap(int[] nums, int i, int j) {
		int temp = nums[i];
		nums[i] = nums[j];
		nums[j] = temp;
	}
	
}
