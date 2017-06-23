package com.gailo22;

import java.util.Optional;
import java.util.function.Function;

public class Main13 {
	
	public static void main(String[] args) throws Exception {
		
		int r1 = withTryCatch1("hi");
		String r2 = withTryCatch2("wo");
		String r3 = withTryCatch3("he", "llo");
		String r4 = withTryCatch4("world");
		String r5 = withTryCatch4("world world");
		
		System.out.println(r1);
		System.out.println(r2);
		System.out.println(r3);
		System.out.println(r4);
		System.out.println(r5);
		
	}
	
	private static String withException(String input) throws Exception {
		
		if (input.length() > 5) {
			throw new Exception("Error Error!!");
		}
		
		return String.join(", ", input, input);
	}
	
	private static int withTryCatch1(String param) throws Exception {
//		try {
//			String result = withException(param);
//			return result.length();
//		} catch (Exception e) {
//			throw e;
//		}
		Function<String, String> wrapFunction = wrapFunction((String p) -> withException(p));
		return wrapFunction.apply(param).length();
	}
	
	private static String withTryCatch2(String param) throws Exception {
//		try {
//			String inParam = "rld";
//			String result = withException(param + inParam);
//			return result;
//		} catch (Exception e) {
//			throw e;
//		}
		Function<String, String> wrapFunction = wrapFunction((String p) -> withException(p));
		String inParam = "rld";
		return wrapFunction.apply(param + inParam);
	}
	
	private static String withTryCatch3(String param1, String param2) throws Exception {
//		try {
//			String result = withException(param1 + param2);
//			return result;
//		} catch (Exception e) {
//			throw e;
//		}
		Function<String, String> wrapFunction = wrapFunction((String p) -> withException(p));
		return wrapFunction.apply(param1 + param2);
	}
	
	private static String withTryCatch4(String param) throws Exception {
		Optional<String> optional = tryOptional((String p) -> withException(p), param);
		return optional.orElse("");
	}
	
	public static <T, R> Function<T, R> wrapFunction(FunctionWithException<T, R> f) {
		return a -> {
			try {
				return f.apply(a);
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
		};
	}
	
	public static <T, R> Optional<R> tryOptional(FunctionWithException<T, R> f, T arg) {
		try {
			return Optional.of(f.apply(arg));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return Optional.empty();
		}
	}

	@FunctionalInterface
	public interface FunctionWithException<T, R> {
		R apply(T t) throws Exception;
	}

	@FunctionalInterface
	public interface SupplierWithException<R> {
		R get() throws Exception;
	}

	@FunctionalInterface
	public interface ConsumerWithException<T> {
		void accept(T t) throws Exception;
	}
}
