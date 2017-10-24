package com.gailo22;

import java.util.Arrays;
import java.util.Random;

public class Main38 {
	
	public static void main(String[] args) {
		int[] nums = new Random().ints(0, 100).limit(10).toArray();
		mergeSort(nums);
		System.out.println(Arrays.toString(nums));
	}

	private static void mergeSort(int[] nums) {
		int len = nums.length;
		int[] aux = new int[len];
		mergeSort(nums, aux, 0, len - 1);
	}

	private static void mergeSort(int[] nums, int[] aux, int low, int high) {
		if (low < high) {
			int mid = low + (high - low) / 2;
			mergeSort(nums, aux, low, mid);
			mergeSort(nums, aux, mid+1, high);
			merge(nums, aux, low, mid, high);
		}
	}

	private static void merge(int[] nums, int[] aux, int low, int mid, int high) {
		for (int i=low; i<=high; i++) {
			aux[i] = nums[i];
		}
		
		int i = low;
		int j = mid+1;
		int k = low;
		
		while (i <= mid && j <= high) {
			if (aux[i] < aux[j]) {
				nums[k] = aux[i];
				i++;
			} else {
				nums[k] = aux[j];
				j++;
			}
			k++;
		}
		
		while (i <= mid) {
			nums[k] = aux[i];
			i++;
			k++;
		}
	}
	
}
