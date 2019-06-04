package com.gailo22;

public class Main92 {
	
	public static void main(String[] args) {
		String s = reverseString("hello");
		System.out.println(s);
	}

	private static String reverseString(String s) {
		return helper(s, 0, new StringBuilder());
	}

    private static String helper(String s, int index, StringBuilder result) {
        if (index >= s.length()) {
            return "";
        }
        helper(s, index+1, result);
        result.append(s.charAt(index));
        return result.toString();
    }

}
