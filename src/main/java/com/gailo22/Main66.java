package com.gailo22;

import com.spotify.futures.CompletableFutures;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jooq.lambda.tuple.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class Main66 {

    static ExecutorService pool = Executors.newFixedThreadPool(10);

    static class SubmissionState {
        private static Consumer<CaseInfo> noOp = cse -> { };
        private List<String> tjlogList;
        private List<CompletableFuture<Runnable>> tasks = new ArrayList<>();
        private List<Consumer<CaseInfo>> rollbackTasks = new ArrayList<>();

        public SubmissionState(List<String> tjlogList) {
            this.tjlogList = tjlogList;
        }

        public <T> void submit(CompletableFuture<T> future) {
            submit(future, null, null);
        }

        public <T> void submit(CompletableFuture<T> future, Consumer<T> accept) {
            submit(future, accept, null);
        }

        public <T> void submit(CompletableFuture<T> future, Consumer<T> accept, Consumer<CaseInfo> rollback) {
            if (accept == null) {
                tasks.add(future.thenApply(any -> () -> { }));
            } else {
                tasks.add(future.thenApply(result -> () -> accept.accept(result)));
            }
            if (rollback != null) {
                rollbackTasks.add(rollback);
            }
        }

        public boolean hasTasks() {
            return tasks.size() > 0;
        }

        public CompletableFuture<Void> getTasks() {
            long start = System.currentTimeMillis();
            CompletableFuture<Void> future = CompletableFutures.allAsList(tasks)
                    .thenAccept(list -> {
                        for (Runnable runnable : list) {
                            runnable.run();
                        }
                        long time = System.currentTimeMillis() - start;
                        System.out.println("tasks.size(): " + tasks.size());
                        System.out.println("time getTasks: " + time);
                    });

            return future;
        }

        public Function<CaseInfo, List<String>> getRollbackTasks() {
            return (cse) -> {
                List<String> rollbackStepExMsgs = new ArrayList<>();
                for (Consumer<CaseInfo> task: rollbackTasks) {
                    try {
                        task.accept(cse);
                    } catch (Exception rollbackStepEx) {
                        rollbackStepExMsgs.add(rollbackStepEx.getMessage());
                    }

                }
                return rollbackStepExMsgs;
            };
        }
    }

    static class SubmissionService {

        private static List<BaseSubmissionPlugin> plugins = new ArrayList<>();
        static {
            plugins.add(new Plugin1());
            plugins.add(new Plugin2());
            plugins.add(new Plugin3());
            plugins.add(new Plugin4());
        };

        public void start(CaseInfo cse) {
            List<String> tjlogList = new ArrayList<>();

            long start = System.currentTimeMillis();
            List<WorkflowStep> steps = new ArrayList<>();
            List<BaseSubmissionPlugin> hardStop = plugins.stream().filter(it -> it.isHardStop()).collect(Collectors.toList());
            List<BaseSubmissionPlugin> softStop = plugins.stream().filter(it -> !it.isHardStop()).collect(Collectors.toList());
            List<BaseSubmissionPlugin> doing = hardStop.stream().filter(it -> it.getParent() == null).collect(Collectors.toList());
            List<Tuple2<String, CompletableFuture<Void>>> tasks = new ArrayList<>();
            List<Tuple2<String, Function<CaseInfo, List<String>>>> rollbackTasks = new ArrayList<>();
            System.out.println("time init: " + (System.currentTimeMillis() - start) / 1_000);

            //hard stops
            try {
                while (doing.size() > 0 || tasks.size() > 0) {
                    List<String> finishedTasks = new ArrayList<>();
                    for (BaseSubmissionPlugin plugin : doing) {
                        hardStop.remove(plugin);
                        if (plugin.isApply(cse)) {
                            SubmissionState state = new SubmissionState(tjlogList);

                            plugin.submit(cse, state);
                            if (state.hasTasks()) {
//                                time("getTasks: ", () -> {
//                                    tasks.add(new Tuple2<>(plugin.getName(), state.getTasks()));
//                                });
                                tasks.add(new Tuple2<>(plugin.getName(), state.getTasks()));
                                rollbackTasks.add(new Tuple2<>(plugin.getName(), state.getRollbackTasks()));
                            } else {
                                finishedTasks.add(plugin.getName());
                            }
                        } else {
                            finishedTasks.add(plugin.getName());
                        }
                    }

                    if (!tasks.isEmpty()) {
                        CompletableFuture[] cfs = tasks.stream().map(it1 -> it1.v2).toArray(CompletableFuture[]::new);

//                        time("anyOf: ", () -> {
//                            CompletableFuture.anyOf(cfs).join();
//                        });
                        CompletableFuture.anyOf(cfs).join();

                        for (int i = tasks.size() - 1; i >= 0; i--) {
                            Tuple2<String, CompletableFuture<Void>> tuple = tasks.get(i);
                            if (tuple.v2.isCancelled() || tuple.v2.isCompletedExceptionally() || tuple.v2.isDone()) {
                                tuple.v2.join();
                                finishedTasks.add(tuple.v1);
                                steps.add(new WorkflowStep(tuple.v1, "SUCCESS", null));
                                tasks.remove(tuple);
                            }
                        }
                    }
                    doing = hardStop.stream().filter(it -> finishedTasks.contains(it.getParent())).collect(Collectors.toList());
                }
            } catch (Exception taskEx) {
                for (Tuple2<String, CompletableFuture<Void>> tuple : tasks) {
                    try {
                        tuple.v2.join();
                    } catch (Exception ignored) {
                    }
                }

                for (Tuple2<String, Function<CaseInfo, List<String>>> rollbackTuple : rollbackTasks) {
                    List<String> rollbackStepExMsgs = rollbackTuple.v2.apply(cse);
                    if (rollbackStepExMsgs.size() == 0) {
                        steps.add(new WorkflowStep(rollbackTuple.v1, "ERROR", taskEx.getMessage()));
                    } else {
                        steps.add(new WorkflowStep(rollbackTuple.v1, "ROLLBACK_FAILED", taskEx.getMessage() + "\n" + String.join("\n - ", rollbackStepExMsgs)));
                    }
                }
                return;
            }

            // soft stops
            List<Tuple2<String, CompletableFuture<Void>>> softStopTasks = new ArrayList<>();
            for (BaseSubmissionPlugin plugin : softStop) {
                try {
                    if (plugin.isApply(cse)) {
                        SubmissionState state = new SubmissionState(tjlogList);

                        plugin.submit(cse, state);
                        if (state.hasTasks()) {
                            softStopTasks.add(new Tuple2<>(plugin.getName(), state.getTasks()));
                        }
                    }
                } catch (Exception ex) {
                    steps.add(new WorkflowStep(plugin.getName(), "ERROR", ex.getMessage()));
                }
            }

            for (Tuple2<String, CompletableFuture<Void>> tuple : softStopTasks) {
                try {
                    tuple.v2.join();
                    steps.add(new WorkflowStep(tuple.v1, "SUCCESS", null));
                } catch (Exception ex) {
                    steps.add(new WorkflowStep(tuple.v1, "ERROR", ex.getMessage()));
                }
            }

            System.out.println(steps.toString().replaceAll("\\),", "\\),\n"));

        }
    }

    static abstract class BaseSubmissionPlugin {
        public abstract String getName();
        public abstract boolean isHardStop();
        public abstract boolean isApply(CaseInfo cse);
        public abstract String getParent();
        public abstract void submit(CaseInfo cse, SubmissionState state);
    }

    static class Plugin1 extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "plugin1";
        }

        @Override
        public boolean isHardStop() {
            return true;
        }

        @Override
        public boolean isApply(CaseInfo cse) {
            return true;
        }

        @Override
        public String getParent() {
            return null;
        }

        @Override
        public void submit(CaseInfo cse, SubmissionState state) {
            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                sleep(5);
                System.out.println("plugin1-1 " + Thread.currentThread().getName());
                return "plugin1-1";
            }, pool);
            CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
                sleep(3);
                System.out.println("plugin1-2 " + Thread.currentThread().getName());
                return "plugin1-2";
            }, pool);
            CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
                sleep(3);
                System.out.println("plugin1-3 " + Thread.currentThread().getName());
                throw new RuntimeException("errooror");
            }, pool);

            state.submit(future1, s -> {
                System.out.println("thenApply1: " + s);
                cse.setCaseId(s);
            }, rollback -> {
                System.out.println("rollback1");
            });

            state.submit(future2, s -> {
                System.out.println("thenApply2: " + s);
                cse.setCaseId(s);
            }, rollback -> {
                System.out.println("rollback2");
            });
//            state.submit(future3, s -> {
//                System.out.println("thenApply3: " + s);
//                cse.setCaseId(s);
//            }, rollback -> {
//                System.out.println("rollback3");
//            });
        }
    }

    static class Plugin2 extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "plugin2";
        }

        @Override
        public boolean isHardStop() {
            return true;
        }

        @Override
        public boolean isApply(CaseInfo cse) {
            return true;
        }

        @Override
        public String getParent() {
            return "plugin1";
        }

        @Override
        public void submit(CaseInfo cse, SubmissionState state) {
//            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
//                sleep(3);
//                System.out.println("plugin2-1 " + Thread.currentThread().getName());
//                return "plugin2-1";
//            }, pool);
//
//            state.submit(future1, s -> {
//                System.out.println("thenApply2: " + s);
//                cse.setCaseId(s);
//            }, rollback -> {
//                System.out.println("rollback2");
//            });
            System.out.println("plugin2");
            state.submit(CompletableFuture.completedFuture(null));
        }
    }

    static class Plugin3 extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "plugin3";
        }

        @Override
        public boolean isHardStop() {
            return true;
        }

        @Override
        public boolean isApply(CaseInfo cse) {
            return true;
        }

        @Override
        public String getParent() {
            return "plugin1";
        }

        @Override
        public void submit(CaseInfo cse, SubmissionState state) {
            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                sleep(3);
                System.out.println("plugin3-1 " + Thread.currentThread().getName());
                return "plugin3-1";
            }, pool);

            state.submit(future1, s -> {
                System.out.println("thenApply3: " + s);
                cse.setCaseId(s);
            }, rollback -> {
                System.out.println("rollback3");
            });
        }
    }

    static class Plugin4 extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "plugin4";
        }

        @Override
        public boolean isHardStop() {
            return true;
        }

        @Override
        public boolean isApply(CaseInfo cse) {
            return true;
        }

        @Override
        public String getParent() {
            return null;
        }

        @Override
        public void submit(CaseInfo cse, SubmissionState state) {

            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                sleep(10);
                System.out.println("plugin4-1 " + Thread.currentThread().getName());
                return "plugin4-1";
            }, pool);

            state.submit(future1, s -> {
                System.out.println("thenApply4: " + s);
                cse.setCaseId(s);
            }, rollback -> {
                System.out.println("rollback4");
            });

        }
    }

    @Data
    static class CaseInfo {
        private String caseId;
    }

    @Data
    @AllArgsConstructor
    static class WorkflowStep {
        private String name;
        private String status;
        private String errorDesc;
    }

	public static void main(String[] args) {

        SubmissionService submissionService = new SubmissionService();
        CaseInfo cse = new CaseInfo();
//        submissionService.start(cse);

        time("startSubmission: ", () -> {
            submissionService.start(cse);
        });

        pool.shutdown();

    }

    static void sleep(int sec) {
        try {
            TimeUnit.SECONDS.sleep(sec);
        } catch (InterruptedException e) {}
    }

    private static void time(String taskName, Runnable operation) {
        long startTime = System.currentTimeMillis();
        operation.run();
        long endTime = System.currentTimeMillis();
        double elapsedSeconds = (endTime - startTime);
        System.out.printf("Elapsed time: %s, %.3f seconds.%n", taskName, elapsedSeconds);
    }

    static<T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> com) {
        return CompletableFuture.allOf(com.toArray(new CompletableFuture[0]))
                .thenApply(v -> com.stream()
                        .map(CompletableFuture::join)
                        .collect(toList())
                );
    }

}
