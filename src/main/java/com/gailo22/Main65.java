package com.gailo22;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import lombok.Data;
import org.jooq.lambda.tuple.Tuple2;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

public class Main65 {

    @FunctionalInterface
    interface ExceptionFunction<E, F> {
        F apply(E e) throws Throwable;
    }

    @FunctionalInterface
    interface ExceptionFunction2<E, F, G> {
        G apply(E e, F f) throws Throwable;
    }

    static class Either<E> {
        private E value;
        private Throwable problem;

        private Either() {
        }

        public static <E> Either<E> success(E v) {
            Either<E> self = new Either<>();
            self.value = v;
            return self;
        }

        public static <E> Either<E> failure(Throwable t) {
            Either<E> self = new Either<>();
            self.problem = t;
            return self;
        }

        public boolean success() {
            return value != null;
        }

        public boolean failed() {
            return problem != null;
        }

        public E get() {
            return value;
        }

        public Throwable getProblem() {
            return problem;
        }

        public void use(Consumer<E> cons) {
            if (value != null) {
                cons.accept(value);
            }
        }

        public void handle(Consumer<Throwable> cons) {
            if (problem != null) {
                cons.accept(problem);
            }
        }

        public static <E, F> Function<E, Either<F>> wrap(ExceptionFunction<E, F> op) {
            return e -> {
                try {
                    return Either.success(op.apply(e));
                } catch (Throwable th) {
                    return Either.failure(th);
                }
            };
        }

        public static <E, F, G> BiFunction<E, F, Either<G>> wrap2(ExceptionFunction2<E, F, G> op) {
            return (e, f) -> {
                try {
                    return Either.success(op.apply(e, f));
                } catch (Throwable th) {
                    return Either.failure(th);
                }
            };
        }

    }

    static final class Nothing {
        public static final Nothing instance = new Nothing();

        private Nothing() {
        }
    }

    static class State<S, A> {

        public final Function<S, Tuple2<A, S>> run;

        public State(Function<S, Tuple2<A, S>> run) {
            super();
            this.run = run;
        }

        public static <S, A> State<S, A> unit(A a) {
            return new State<>(s -> new Tuple2<>(a, s));
        }

        public <B> State<S, B> map(Function<A, B> f) {
            return flatMap(a -> State.unit(f.apply(a)));
        }

        public <B, C> State<S, C> map2(State<S, B> sb, Function<A, Function<B, C>> f) {
            return flatMap(a -> sb.map(b -> f.apply(a).apply(b)));
        }

        public <B> State<S, B> flatMap(Function<A, State<S, B>> f) {
            return new State<>(s -> {
                Tuple2<A, S> temp = run.apply(s);
                return f.apply(temp.v1).run.apply(temp.v2);
            });
        }

//		public static <S, A> State<S, List<A>> sequence(List<State<S, A>> fs) {
//			return fs.foldRight(State.unit(List.<A>list()), f -> acc -> f.map2(acc, a -> b -> b.cons(a)));
//		}

        public static <S> State<S, S> get() {
            return new State<>(s -> new Tuple2<>(s, s));
        }

        public static <S, A> State<S, A> getState(Function<S, A> f) {
            return new State<>(s -> new Tuple2<>(f.apply(s), s));
        }

        public static <S> State<S, Nothing> set(S s) {
            return new State<>(x -> new Tuple2<>(Nothing.instance, s));
        }

        public static <S> State<S, Nothing> modify_(Function<S, S> f) {
            return new State<>(s -> new Tuple2<>(Nothing.instance, f.apply(s)));
        }

        public static <S> State<S, Nothing> modify(Function<S, S> f) {
            return State.<S>get().flatMap(s -> set(f.apply(s)));
        }

        public A eval(S s) {
            return run.apply(s).v1;
        }

    }

    public static void main(String[] args) throws InterruptedException {

        BasePlugin<?> rootPlugin = new RootPlugin();
        BasePlugin<?> plugin1 = new Plugin1();
        BasePlugin<?> plugin2 = new Plugin2();
        BasePlugin<?> plugin3 = new Plugin3();
        MutableGraph<BasePlugin<?>> graph = GraphBuilder.directed().build();  // graph is empty

        graph.putEdge(rootPlugin, plugin1);
        graph.putEdge(rootPlugin, plugin3);
        graph.putEdge(plugin1, plugin2);

        Queue<BasePlugin<?>> queue = new LinkedList<>();

        while (!queue.isEmpty()) {
            BasePlugin<?> task = queue.remove();

            Set<BasePlugin<?>> children = graph.successors(task);
            children.stream().parallel()
                .forEach(x -> {
                    task.submit(null, null).thenAccept(y -> {
                        queue.add(x);
                    });
                });
        }

//        CaseInfo caseInfo = new CaseInfo();
//        SubmissionState state = new SubmissionState();
//        SubmissionService.start(caseInfo, state);

        TimeUnit.SECONDS.sleep(3);

    }

    static class SubmissionService {

        //@Autowired
        static List<BasePlugin<CaseInfo>> plugins = new ArrayList<>();

        static {
            plugins.add(new Plugin1());
            plugins.add(new Plugin2());
            plugins.add(new Plugin3());
        }

        ;

        static void start(CaseInfo cse, SubmissionState state) {
            plugins.stream()//.parallel()
                .filter(it -> it.isApply(cse))
                .map(it -> (Supplier<CompletableFuture<?>>) () -> it.submit(cse, state))
                .forEach(it -> {
                    System.out.println(it.get().join());
                });
        }
    }

    @Data
    static class CaseInfo {
        private String caseId;
    }

    @Data
    static class SubmissionState extends State<CompletableFuture<?>, Runnable> {
        private String tjlog;
        private String others;

        public SubmissionState() {
            super(null);
        }

        public CompletableFuture<?> submit(CaseInfo caseInfo,
                                           List<CompletableFuture<?>> futures,
                                           Consumer<CaseInfo> thenAction,
                                           Consumer<CaseInfo> rollback) {
            CompletableFuture<List<?>> sequence = sequence(futures);
            sequence.handle((ok, ko) -> {
                if (ko == null) {
                    thenAction.accept(caseInfo);
                } else {
                    rollback.accept(caseInfo);
                }
                return null;
            }).join();

            return CompletableFuture.completedFuture("done!!!");
        }
    }

    interface BasePlugin<T> {
        boolean isApply(T t);

        String getName();

        default CompletableFuture<?> submit(T t, SubmissionState state) {
            if (!isApply(t)) {
                return CompletableFuture.completedFuture(null);
            } else {
                return internalSubmit(t, state);
            }
        }

        CompletableFuture<?> internalSubmit(T t, SubmissionState state);
    }

    static class RootPlugin implements BasePlugin<CaseInfo> {
        @Override
        public boolean isApply(CaseInfo caseInfo) {
            return false;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public CompletableFuture<?> internalSubmit(CaseInfo caseInfo, SubmissionState state) {
            return null;
        }
    }

    static class Plugin1 implements BasePlugin<CaseInfo> {

        @Override
        public String getName() {
            return "plugin1";
        }

        @Override
        public boolean isApply(CaseInfo caseInfo) {
            return true;
        }

        @Override
        public CompletableFuture<?> internalSubmit(CaseInfo caseInfo, SubmissionState state) {
            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                sleep(3);
                System.out.println(Thread.currentThread().getName());
                return "plugin1-1";
            });
            CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
                sleep(3);
                System.out.println(Thread.currentThread().getName());
                return "plugin1-2";
            });

            CompletableFuture<Object> future3 = new CompletableFuture<>();
            future3.completeExceptionally(new RuntimeException("eerrror"));

            return state.submit(
                caseInfo,
                Arrays.asList(future1, future3, future2),
                (caseInfo1) -> {
                    caseInfo1.setCaseId("case-123");
                    System.out.println("update: " + caseInfo1);
                },
                (caseInfo2) -> {
                    System.out.println("rollback: " + caseInfo2);
                });
        }

    }

    static class Plugin2 implements BasePlugin<CaseInfo> {

        @Override
        public String getName() {
            return "plugin2";
        }

        @Override
        public boolean isApply(CaseInfo caseInfo) {
            return false;
        }

        @Override
        public CompletableFuture<?> internalSubmit(CaseInfo caseInfo, SubmissionState state) {
            return CompletableFuture.supplyAsync(() -> {
                sleep(2);
                System.out.println(Thread.currentThread().getName());
                return "plugin2";
            });
        }

    }

    static class Plugin3 implements BasePlugin<CaseInfo> {

        @Override
        public String getName() {
            return "plugin3";
        }

        @Override
        public boolean isApply(CaseInfo caseInfo) {
            return true;
        }

        @Override
        public CompletableFuture<?> internalSubmit(CaseInfo caseInfo, SubmissionState state) {
            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                sleep(3);
                System.out.println(Thread.currentThread().getName());
                return "plugin3-1";
            });
            CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
                sleep(3);
                System.out.println(Thread.currentThread().getName());
                return "plugin3-2";
            });

            return state.submit(
                caseInfo,
                Arrays.asList(future1, future2),
                (caseInfo1) -> {
                    caseInfo1.setCaseId("case-456");
                    System.out.println("update: " + caseInfo1);
                },
                (caseInfo2) -> {
                    System.out.println("rollback: " + caseInfo2);
                });
        }

    }

    static void sleep(int sec) {
        try {
            TimeUnit.SECONDS.sleep(sec);
        } catch (InterruptedException e) {
        }
    }

    static CompletableFuture<List<?>> sequence(List<CompletableFuture<?>> com) {
        return CompletableFuture.allOf(com.toArray(new CompletableFuture[0]))
            .thenApply(v -> com.stream()
                .map(CompletableFuture::join)
                .collect(toList())
            );
    }


}
