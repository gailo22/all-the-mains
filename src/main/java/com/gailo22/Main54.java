package com.gailo22;

import java.util.function.Function;

public class Main54 {
	
	static final class Assertion {
		private Assertion() {}
	
		public static <T> Result<T> assertCondition(T value, Function<T, Boolean> f) {
			return assertCondition(value, f, "Assertion error: condition should evaluate to true");
		}

		public static <T> Result<T> assertCondition(T value, Function<T, Boolean> f, String message) {
			return f.apply(value)
				? Result.success(value)
				: Result.failure(message, new IllegalStateException(message));
		}
		
		public static Result<Boolean> assertTrue(boolean condition) {
			return assertTrue(condition, "Assertion error: condition should be true");
		}

		public static Result<Boolean> assertTrue(boolean condition, String message) {
			return assertCondition(condition, x -> x, message);
		}
		
		public static Result<Boolean> assertFalse(boolean condition) {
			return assertFalse(condition, "Assertion error: condition should be false");
		}

		public static Result<Boolean> assertFalse(boolean condition, String message) {
			return assertCondition(condition, x -> !x, message);
		}
		
		public static <T> Result<T> assertNotNull(T t) {
			return assertNotNull(t, "Assertion error: object should not be null");
		}

		public static <T> Result<T> assertNotNull(T t, String message) {
			return assertCondition(t, x -> x != null, message);
		}
		
		public static Result<Integer> assertPositive(int value) {
			return assertPositive(value, String.format("Assertion error: value %s must be positive", value));
		}

		private static Result<Integer> assertPositive(int value, String message) {
			return assertCondition(value, x -> x > 0, message);
		}
		
		public static Result<Integer> assertInRange(int value, int min, int max) {
			return assertCondition(value, x -> x >= min && x < max, 
					String.format("Assertion error: value %s should be between %s and %s (exclusive)", value, min, max));
		}
		
		public static Result<Integer> assertPositiveOrZero(int value) {
			return assertPositiveOrZero(value, String.format("Assertion error: value %s must not be negative", 0));
		}

		public static Result<Integer> assertPositiveOrZero(int value, String message) {
			return assertCondition(value, x -> x >= 0, message);
		}
		
		public static <A> void assertType(A element, Class<?> clazz) {
			assertType(element, clazz,
					String.format("Wrong type: %s, expected: %s", element.getClass().getName(), clazz.getName()));
		}

		public static <A> Result<A> assertType(A element, Class<?> clazz, String message) {
			return assertCondition(element, e -> e.getClass().equals(clazz), message);
		}
		
		public static boolean isPositive(int i) {
			return i >= 0;
		}
		
		public static boolean isValidName(String name) {
			return name != null && name.length() != 0 && name.charAt(0) >= 65 && name.charAt(0) <= 91;
		}
		
		public static Result<String> assertValidName(String name, String message) {
			return Result.of(Assertion::isValidName, name, message);
		}
	}
	
	static class Result<T> {
		public static <T> Result<T> success(T value) {
			return null;
		}
		public static <T> Result<T> failure(String message, Exception e) {
			return null;
		}
		public static <T> Result<T> of(Function<T, Boolean> predicate, T value, String message) {
			return null;
		}
		public <U> Result<U> map(Function<T, U> f) {
			return null;
		}
		public <U> Result<U> flatMap(Function<T, Result<U>> f) {
			return null;
		}
	}
	
	static class Person {
		private Person(int id, String firstName, String lastName) {
		}

		public static Person apply(int id, String firstName, String lastName) {
			return new Person(id, firstName, lastName);
		}
	}
	
	public static void main(String[] args) {
		Result<Integer> rId = Assertion.assertPositive(getInt("personId"), "Negative id");
		Result<String> rFirstName = Assertion.assertValidName(getString("firstName"), "Invalid first name");
		Result<String> rLastName = Assertion.assertValidName(getString("lastName"), "Invalid last name");
		Result<Person> person = rId.flatMap(id -> rFirstName
				.flatMap(firstName -> rLastName.map(lastName -> Person.apply(id, firstName, lastName))));
	}

	private static String getString(String string) {
		return null;
	}

	private static int getInt(String string) {
		return 0;
	}

}
