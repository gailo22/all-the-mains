package com.gailo22;

import java.util.function.Predicate;

public class Main26 {
	
    public static Boolean validate(String password) {
    	if (password == null) return false;
    	
    	if (lenghtMoreThan12()
    			.and(notContains123())
    			.and(upperCaseLowerCaseAndOneDigit())
    			.test(password)) {
    		return true;
    	}
        
        return false;
        
    }
    
    private static Predicate<String> lenghtMoreThan12() {
		return p -> p.length() >= 12;
	}
    
    private static Predicate<String> notContains123() {
    	return p -> !p.contains("123");
    }

	private static Predicate<String> upperCaseLowerCaseAndOneDigit() {
		String pattern = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9]*$";
		return p -> p.matches(pattern);
	}

	public static void main(String[] args) {
        System.out.println(validate("Strong1Password")); // Strong password
        System.out.println(validate("strong1password")); // Weak password
    }
}
