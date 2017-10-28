package com.gailo22;

import java.util.function.Predicate;

public class Main47 {

	static interface Request {}
	static interface Response {}
	static interface Mono<T> {
		static <T> Mono<T> just(T t) { return null; }
		static <T> Mono<T> empty() { return null; }
	}
	static interface Flux<T> {}

	@FunctionalInterface
	interface HandlerFunction {
		Mono<Response> handle(Request request);
	}

	@FunctionalInterface
	interface RouterFunction {
		Mono<HandlerFunction> route(Request request);
	}

	static abstract class RouterFunctions {
		static RouterFunction route(Predicate pred, HandlerFunction handler) {
			return request -> {
				if (pred.test(request)) {
					return Mono.just(handler);
				} else {
					return Mono.empty();
				}
			};
		}
	}
	
	public static void main(String[] args) {
		
	}

}
