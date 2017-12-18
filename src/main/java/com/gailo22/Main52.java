package com.gailo22;

public class Main52 {

	interface Logger {
		void log(String s);
	}

	interface Filter {
		boolean accept(String s);
	}

	static class Loggers {
		public static Logger filterLogger(Logger logger, Filter filter) {
			return message -> {
				if (filter.accept(message)) {
					logger.log(message);
				}
			};
		}
	}

	public static void main(String[] args) {
		Logger logger = msg -> System.out.println(msg);
		Logger filterLogger = 
			Loggers.filterLogger(logger, msg -> msg.startsWith("foo"));
	}

}
