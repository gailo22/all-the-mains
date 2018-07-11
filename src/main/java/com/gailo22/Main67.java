package com.gailo22;

import org.jooq.lambda.tuple.Tuple2;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static com.gailo22.Main67.EmailValidation.*;

public class Main67 {

    interface Result<T> {
        void bind(Effect<T> success, Effect<String> failure);

        static <T> Result<T> failure(String message) {
            return new Failure<>(message);
        }

        static <T> Result<T> success(T value) {
            return new Success<>(value);
        }
    }

    static class Success<T> implements Result<T> {
        private final T value;

        public Success(T value) {
            this.value = value;
        }

        @Override
        public void bind(Effect<T> success, Effect<String> failure) {
            success.apply(value);
        }
    }

    static class Failure<T> implements Result<T> {
        private final String errorMessage;

        public Failure(String message) {
            this.errorMessage = message;
        }

        @Override
        public void bind(Effect<T> success, Effect<String> failure) {
            failure.apply(errorMessage);
        }
    }

    interface Effect<T> {
        void apply(T t);
    }

    static class EmailValidation {
        static Pattern emailPattern = Pattern.compile("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$");
        static Function<String, Result<String>> emailChecker = s -> {
            if (s == null) {
                return Result.failure("Email must not be null");
            } else if (s.length() == 0) {
                return Result.failure("Email must not be empty");
            } else if (emailPattern.matcher(s).matches()) {
                return Result.success(s);
            } else {
                return Result.failure("Email " + s + " is invalid");
            }
        };
        static Effect<String> success = s -> System.out.println("Email sent to " + s);
        static Effect<String> failure = s -> System.err.println("Error message logged " + s);
    }

    static class Case<T> extends Tuple2<Supplier<Boolean>, Supplier<Result<T>>> {

        private Case(Supplier<Boolean> booleanSupplier,
                     Supplier<Result<T>> resultSupplier) {
            super(booleanSupplier, resultSupplier);
        }

        public static <T> Case<T> mcase(Supplier<Boolean> condition,
                                        Supplier<Result<T>> value) {
            return new Case<>(condition, value);
        }

        public static <T> DefaultCase<T> mcase(Supplier<Result<T>> value) {
            return new DefaultCase<>(() -> true, value);
        }

        @SafeVarargs
        public static <T> Result<T> match(DefaultCase<T> defaultCase,
                                          Case<T>... matchers) {
            for (Case<T> aCase : matchers) {
                if (aCase.v1.get()) return aCase.v2.get();
            }
            return defaultCase.v2.get();
        }
    }

    static class DefaultCase<T> extends Case<T> {

        private DefaultCase(Supplier<Boolean> booleanSupplier, Supplier<Result<T>> resultSupplier) {
            super(booleanSupplier, resultSupplier);
        }
    }

    public static void main(String[] args) {
        emailChecker.apply("abc@dsdf.com").bind(success, failure);
        emailChecker.apply("abc@.comsdfdsf").bind(success, failure);
    }

}
