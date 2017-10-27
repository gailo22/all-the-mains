package com.gailo22;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.RecursiveAction;

public class Main45 {
	
	static class MergeSortFJTask extends RecursiveAction {

		private static final long serialVersionUID = 1L;
		
		final int[] array;
		final int lo, hi;
		
		static final int THRESHOLD = 1000;

		public MergeSortFJTask(int[] array, int lo, int hi) {
			this.array = array;
			this.lo = lo;
			this.hi = hi;
		}

		public MergeSortFJTask(int[] array) {
			this(array, 0, array.length);
		}
		
		void sortSequentially() {
			Arrays.sort(array, lo, hi);
		}
		
		void merge(int lo, int mid, int hi) {
			int[] buf = Arrays.copyOfRange(array, lo, mid);
			for (int i = 0, j = lo, k = mid; i < buf.length; j++) {
				array[j] = (k == hi || buf[i] < array[k]) ? buf[i++] : array[k++];
			}
		}

		@Override
		protected void compute() {
			if (hi - lo < THRESHOLD) {
				sortSequentially();
			} else {
				int mid = lo + (hi - lo) / 2;
				invokeAll(new MergeSortFJTask(array, lo, mid),
						  new MergeSortFJTask(array, mid, hi));
				merge(lo, mid, hi);
			}
		}
		
	}
	
	public static void main(String[] args) {
		int[] nums = new Random().ints(0, 100).limit(10).toArray();
		MergeSortFJTask task = new MergeSortFJTask(nums);
		task.invoke();
		
		System.out.println(Arrays.toString(nums));
	}

}
