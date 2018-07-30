package com.gailo22;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Main72 {

    public static void main(String[] args) {
        // Integer -> Integer
        Function<Integer, Integer> add1 = x -> x + 1;
        Function<Integer, Integer> square = x -> x * 2;

        // f compose g = f(g(x))
        Function<Integer, Integer> compose = add1.compose(square);
        System.out.println(compose.apply(2));

        // f andThen g = g(f(x))
        Function<Integer, Integer> andThen = add1.andThen(square);
        System.out.println(andThen.apply(2));

        ExecutorService es = Executors.newFixedThreadPool(1);
        Par<String> par = unit("a");
        Future<String> future = par.apply(es);
        future.apply(System.out::println);


    }

    // blocking
    static <A> A run(ExecutorService es, Par<A> p) throws InterruptedException {
        AtomicReference<A> ref = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        Future<A> future = p.apply(es);
        future.apply(a -> {
            ref.set(a);
            latch.countDown();
        });
        latch.await();
        return ref.get();
    }

    static <A> Par<A> unit(A a) {
        return es -> (Future) cb -> cb.accept(a);
    }

    @FunctionalInterface
    interface Future<A> {
        void apply(Consumer<A> cb);
    }

    @FunctionalInterface
    interface Par<A> {
        Future<A> apply(ExecutorService es);
    }

    static abstract class TailCall<T> {
        abstract TailCall<T> resume();

        abstract T eval();

        abstract boolean isSuspend();

        private TailCall() {
        }

        private static class Return<T> extends TailCall<T> {

            private final T t;

            private Return(T t) {
                this.t = t;
            }

            @Override
            public boolean isSuspend() {
                return false;
            }

            @Override
            public T eval() {
                return t;
            }

            @Override
            public TailCall<T> resume() {
                throw new IllegalStateException("Return has no resume");
            }
        }

        private static class Suspend<T> extends TailCall<T> {

            private final Supplier<TailCall<T>> resume;

            private Suspend(Supplier<TailCall<T>> resume) {
                this.resume = resume;
            }

            @Override
            public boolean isSuspend() {
                return true;
            }

            @Override
            public T eval() {
                TailCall<T> tailRec = this;
                while (tailRec.isSuspend()) {
                    tailRec = tailRec.resume();
                }
                return tailRec.eval();
            }

            @Override
            public TailCall<T> resume() {
                return resume.get();
            }
        }

        public static <T> Return<T> ret(T t) {
            return new Return<>(t);
        }

        public static <T> Suspend<T> sus(Supplier<TailCall<T>> s) {
            return new Suspend<T>(s);
        }
    }

    static abstract class Option<A> {
        abstract <B> Option<B> map(Function<A, B> f);

        abstract A getOrElse(A defaultvalue);

        abstract boolean isSome();

        public <B> Option<B> flatMap(Function<A, Option<B>> f) {
            return map(f).getOrElse(none);
        }

        private static Option none = new None();

        private static class None<A> extends Option<A> {

            private None() {
            }

            @Override
            public boolean isSome() {
                return false;
            }

            @Override
            public <B> Option<B> map(Function<A, B> f) {
                return none;
            }

            @Override
            public A getOrElse(A defaultvalue) {
                return defaultvalue;
            }
        }

        private static class Some<A> extends Option<A> {

            private final A value;

            private Some(A value) {
                this.value = value;
            }

            @Override
            public boolean isSome() {
                return true;
            }

            @Override
            public <B> Option<B> map(Function<A, B> f) {
                return new Some<>(f.apply(value));
            }

            @Override
            public A getOrElse(A defaultvalue) {
                return value;
            }
        }

        public static <A> Option<A> some(A a) {
            return new Some<>(a);
        }

        public static <A> Option<A> none() {
            return none;
        }

        public static <A, B> Function<Option<A>, Option<B>> lift(Function<A, B> f) {
            return x -> x.map(f);
        }
    }
}
