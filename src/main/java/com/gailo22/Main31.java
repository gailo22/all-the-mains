package com.gailo22;

public class Main31 {
	
	public static void main(String[] args) {
		int[] numbers = {1, 2, 3, 3, 3, 3, 4, 5};
		
		int k = 3;
		int occurrence = getOccurrence(numbers, k);
		System.out.println(occurrence);
	}

	
	private static int getOccurrence(int[] numbers, int k) {
		int first = getFirst(numbers, 0, numbers.length - 1, k);
		int last = getLast(numbers, 0, numbers.length - 1, k);
		
		int occurrence = 0;
		if (first > -1 && last > -1) {
			occurrence = last - first + 1;
		}
		return occurrence;
	}


	static int getFirst(int[] numbers, int start, int end, int k) {
		if (start > end) return -1;
		
		int middle = start + (end - start) / 2;
		if (numbers[middle] == k) {
			if ((middle > 0 && numbers[middle - 1] != k) 
					|| middle == 0) {
				return middle;
			}
			
			end = middle - 1;
		} else if (numbers[middle] > k) {
			end = middle - 1;
		} else {
			start = middle + 1;
		}
		
		return getFirst(numbers, start, end, k);
	}
	
	static int getLast(int[] numbers, int start, int end, int k) {
		if (start > end) return -1;
		
		int middle = start + (end - start) / 2;
		if (numbers[middle] == k) {
			if ((middle < numbers.length -1 && numbers[middle + 1] != k)
					|| middle == numbers.length - 1) {
				return middle;
			} else if (numbers[middle] > k) {
				end = middle - 1;
			} else {
				start = middle + 1;
			}
		}
		
		return getLast(numbers, start, end, k);
	}
}
