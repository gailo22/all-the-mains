package com.gailo22;

import com.spotify.futures.CompletableFutures;
import io.atlassian.fugue.Try;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class Main66 {

    static ExecutorService pool = Executors.newFixedThreadPool(10);

    static class SubmissionState {
        private static Consumer<CaseInfo> noOp = cse -> { };
        private String caseId;
        private String pluginName;
        private boolean isHardStop;
        private List<String> tjlogList;
        private List<CompletableFuture<Runnable>> tasks = new ArrayList<>();
        private List<Consumer<Throwable>> rollbackTasks = new ArrayList<>();

        public SubmissionState(String caseId, String pluginName, List<String> tjlogList, boolean isHardStop) {
            this.caseId = caseId;
            this.pluginName = pluginName;
            this.tjlogList = tjlogList;
            this.isHardStop = isHardStop;
        }

        public <T> void submit(CompletableFuture<T> future) {
            submit(future, null);
        }

        public <T> void submit(CompletableFuture<T> future, Consumer<T> accept) {
            if (accept == null) {
                tasks.add(future.thenApply(any -> () -> { }));
            } else {
                tasks.add(future.thenApply(result -> () -> accept.accept(result)));
            }
        }

        public void rollbackIfFailed(Consumer<Throwable> rollback) {
            rollbackTasks.add(rollback);
        }

        public boolean hasTasks() {
            return tasks.size() > 0;
        }

        public CompletableFuture<WorkflowStep> getTasks() {
            LocalDateTime start = LocalDateTime.now();
            return CompletableFutures.allAsList(tasks)
                    .thenApply(list -> {
                        for (Runnable runnable : list) {
                            runnable.run();
                        }
                        long time = System.currentTimeMillis() - start.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                        System.out.println("tasks.size(): " + tasks.size());
                        System.out.println("time getTasks: " + time);
                        return new WorkflowStep(pluginName, "SUCCESS", null, isHardStop, start, time);
                    });
        }

        public Function<Throwable, WorkflowStep> getRollbackTasks() {
            LocalDateTime start = LocalDateTime.now();
            return (ex) -> {
                List<String> rollbackStepExMsgs = new ArrayList<>();
                for (Consumer<Throwable> task: rollbackTasks) {
                    try {
                        task.accept(ex);
                    } catch (Exception rollbackStepEx) {
                        rollbackStepExMsgs.add(rollbackStepEx.getMessage());
                    }

                }
                long time = System.currentTimeMillis() - start.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                if (rollbackStepExMsgs.size() == 0) {
                    return new WorkflowStep(this.pluginName, "ERROR", ex.getMessage(), isHardStop, start, time);
                } else {
                    return new WorkflowStep(this.pluginName, "ROLLBACK_FAILED", ex.getMessage() + "\n" + String.join("\n - ", rollbackStepExMsgs), isHardStop, start, time);
                }
            };
        }
    }

    static class SubmissionService {

        private static final String NO_REVERT = "noRevert";

        private static List<BaseSubmissionPlugin> plugins = new ArrayList<>();
        static {
            plugins.add(new CreateUpdateCustomer());
            plugins.add(new PaymentOrders());
            plugins.add(new CustomerAddress());
            plugins.add(new ContactChannel());
            plugins.add(new MarketingInfo());
            plugins.add(new GenerateDocForSecurities());
            plugins.add(new OpenAccount());
            plugins.add(new CustRelationship());
            plugins.add(new CustomerRemark());
            plugins.add(new JakJai());
            plugins.add(new DebitCard());
            plugins.add(new AppForm());
            plugins.add(new EZApp());
            plugins.add(new NssEmail());
            plugins.add(new NssSms());
            plugins.add(new Fatca());
            plugins.add(new CustImages());
            plugins.add(new MutualFund());
            plugins.add(new Consent());
            plugins.add(new RiskAssessment());
            plugins.add(new PromptPay());
            plugins.add(new SubmitDocumentToNas());
            plugins.add(new NDID());
            plugins.add(new SecuritiesAccount());
            plugins.add(new NotifyCustomer());
        };

        public void start(CaseInfo cse) {
            List<String> tjlogList = new ArrayList<>();

            long start = System.currentTimeMillis();
            Set<WorkflowStep> steps = new HashSet<>();
            List<BaseSubmissionPlugin> hardStops = plugins.stream().filter(it -> it.isHardStop()).collect(Collectors.toList());
            List<BaseSubmissionPlugin> softStops = plugins.stream().filter(it -> !it.isHardStop()).collect(Collectors.toList());
            List<BaseSubmissionPlugin> doing = hardStops.stream().filter(it -> it.getParent() == null).collect(Collectors.toList());
            List<CompletableFuture<WorkflowStep>> tasks = new ArrayList<>();
            List<Function<Throwable, WorkflowStep>> rollbackTasks = new ArrayList<>();
            System.out.println("time init: " + (System.currentTimeMillis() - start) / 1_000);

            //hard stops
            Try<String> tryHardStop = processHardStop(cse, tjlogList, steps, hardStops, doing, tasks, rollbackTasks);
            if (tryHardStop.isFailure()) return;

            // soft stops
            List<CompletableFuture<WorkflowStep>> softStopTasks = new ArrayList<>();
            List<BaseSubmissionPlugin> softStopsNoParent = softStops.stream().filter(it -> it.getParent() == null).collect(toList());
            processSoftStop(cse, tjlogList, steps, softStopTasks, softStopsNoParent);

            List<BaseSubmissionPlugin> softStopsWithParent = softStops.stream().filter(it -> it.getParent() != null).collect(toList());
            processSoftStop(cse, tjlogList, steps, softStopTasks, softStopsWithParent);

            List<WorkflowStep> sortedSteps = steps.stream()
                    .sorted(Comparator.comparing(WorkflowStep::getCreatedDate))
                    .collect(toList());

            String stepsString = sortedSteps.toString().replaceAll("\\),", "\\),\n");
            System.out.println(String.format("%s:%s", cse.getCaseId(), stepsString));

        }

        private Try<String> processHardStop(CaseInfo cse, List<String> tjlogList, Set<WorkflowStep> steps, List<BaseSubmissionPlugin> hardStops, List<BaseSubmissionPlugin> doing, List<CompletableFuture<WorkflowStep>> tasks, List<Function<Throwable, WorkflowStep>> rollbackTasks) {
            try {
                while (doing.size() > 0 || tasks.size() > 0) {
                    doing = runHardStops(cse, tjlogList, steps, doing, tasks, rollbackTasks, hardStops);
                }

                //run hard stop that cannot revert
                doing = hardStops.stream().filter(it -> NO_REVERT.equals(it.getParent())).collect(Collectors.toList());
                while (doing.size() > 0 || tasks.size() > 0) {
                    doing = runHardStops(cse, tjlogList, steps, doing, tasks, rollbackTasks, hardStops);
                }
            } catch (Exception taskEx) {
                System.out.printf("Error %s", taskEx);
                //flush all running tasks
                for (CompletableFuture<WorkflowStep> task: tasks) {
                    try {
                        steps.add(task.join());
                    } catch (Exception ignored) { }
                }

                //rollback all tasks
                for (Function<Throwable, WorkflowStep> rollbackTask: rollbackTasks) {
                    WorkflowStep step = rollbackTask.apply(taskEx);
                    //steps.removeIf(it -> step.getName().equalsIgnoreCase(it.getName()));
                    steps.add(step);
                }
                String stepsString = steps.toString().replaceAll("\\),", "\\),\n");
                System.out.println(String.format("%s:%s", cse.getCaseId(), stepsString));
                return Try.failure(taskEx);
            }
            return Try.successful("success");
        }

        private void processSoftStop(CaseInfo cse, List<String> tjlogList, Set<WorkflowStep> steps, List<CompletableFuture<WorkflowStep>> softStopTasks, List<BaseSubmissionPlugin> softStopsNoParent) {
            for (BaseSubmissionPlugin plugin: softStopsNoParent) {
                if (!plugin.isApply(cse)) {
                    continue;
                }
                SubmissionState state = new SubmissionState(cse.getCaseId(), plugin.getName(), tjlogList, false);
                try {
                    plugin.submit(cse, state);
                    if (state.hasTasks()) {
                        softStopTasks.add(state.getTasks().handle((ok, ko) -> {
                            if (ko != null) {
                                return state.getRollbackTasks().apply(ko);
                            } else {
                                return ok;
                            }
                        }));
                    }
                } catch (Exception ex) {
                    System.out.printf("Error Rollback   %s", ex);
                    steps.add(state.getRollbackTasks().apply(ex));
                }
            }
            for (CompletableFuture<WorkflowStep> task: softStopTasks) {
                WorkflowStep step = task.join();
                steps.add(step);
            }
        }

        private List<BaseSubmissionPlugin> runHardStops(CaseInfo cse,
                                                        List<String> tjlogList,
                                                        Set<WorkflowStep> steps,
                                                        List<BaseSubmissionPlugin> doing,
                                                        List<CompletableFuture<WorkflowStep>> tasks,
                                                        List<Function<Throwable, WorkflowStep>> rollbackTasks,
                                                        List<BaseSubmissionPlugin> hardStops) {
            List<String> finishedTasks = new ArrayList<>();

            //run all ready plugins
            for (BaseSubmissionPlugin plugin : doing) {
                String pluginName = plugin.getName();
                if (plugin.isApply(cse)) {
                    SubmissionState state = new SubmissionState(cse.getCaseId(), pluginName, tjlogList, true);

                    try {
                        plugin.submit(cse, state);
                    } catch (Exception e) {
                        steps.add(WorkflowStep.fromPlugin(plugin, e));
                        throw e;
                    }
                    if (state.hasTasks()) {
                        tasks.add(state.getTasks());
                        rollbackTasks.add(state.getRollbackTasks());
                    } else {
                        finishedTasks.add(pluginName);
                    }
                } else {
                    finishedTasks.add(pluginName);
                }
            }

            //wait for first completed task
            if (!tasks.isEmpty()) {
                CompletableFuture.anyOf(tasks.toArray(new CompletableFuture[0])).join();
                for (int i = tasks.size() - 1; i >= 0; i--) {
                    CompletableFuture<WorkflowStep> task = tasks.get(i);
                    if (task.isCancelled() || task.isCompletedExceptionally() || task.isDone()) {
                        WorkflowStep step = task.join();
                        finishedTasks.add(step.getName());
                        steps.add(step);
                        tasks.remove(task);
                    }
                }
            }

            //get next ready plugins
            doing = hardStops.stream().filter(it -> finishedTasks.contains(it.getParent())).collect(Collectors.toList());
            return doing;
        }

        private Predicate<BaseSubmissionPlugin> filterByFinishedParents(List<String> finishedTasks) {
            return it -> {
                String parent = it.getParent();
                if (parent.contains(",")) {
                    String[] split = parent.split(",");
                    return Arrays.stream(split).allMatch(finishedTasks::contains);
                }
                return finishedTasks.contains(parent);
            };
        }
    }

    static abstract class BaseSubmissionPlugin {
        public abstract String getName();
        public abstract boolean isHardStop();
        public abstract boolean isApply(CaseInfo cse);
        public abstract String getParent();
        public abstract void submit(CaseInfo cse, SubmissionState state);
    }

    static class CreateUpdateCustomer extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "createUpdateCustomer";
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
                sleep(2);
                System.out.println("createUpdateCustomer-1 " + Thread.currentThread().getName());
                return "createUpdateCustomer-1";
            }, pool);
            CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
                sleep(1);
                System.out.println("createUpdateCustomer-2 " + Thread.currentThread().getName());
                return "createUpdateCustomer-2";
            }, pool);
            CompletableFuture<String> futureFailed = CompletableFuture.supplyAsync(() -> {
                sleep(3);
                System.out.println("createUpdateCustomer-3 " + Thread.currentThread().getName());
                throw new RuntimeException("errooror: createUpdateCustomer");
            }, pool);

            state.submit(future1, s -> {
                System.out.println("createUpdateCustomer-thenApply1: " + s);
                cse.setCaseId(s);
            });
            state.rollbackIfFailed(rollback -> {
                System.out.println("createUpdateCustomer-rollback1");
            });

            state.submit(future2, s -> {
                System.out.println("createUpdateCustomer-thenApply2: " + s);
                cse.setCaseId(s);
            });

//            state.rollbackIfFailed(rollback -> {
//                System.out.println("createUpdateCustomer-rollback2");
//            });
//            state.submit(futureFailed, s -> {
//                System.out.println("createUpdateCustomer-thenApply3: " + s);
//                cse.setCaseId(s);
//            });
//            state.rollbackIfFailed(rollback -> {
//                System.out.println("createUpdateCustomer-rollback3");
//            });
        }
    }

    static class PaymentOrders extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "paymentOrders";
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
                sleep(2);
                System.out.println("paymentOrders-1 " + Thread.currentThread().getName());
                return "paymentOrders-1";
            }, pool);

            state.submit(future1, s -> {
                System.out.println("paymentOrders-thenApply1: " + s);
                cse.setCaseId(s);
            });
            state.rollbackIfFailed(rollback -> {
                System.out.println("paymentOrders-rollback1");
            });
        }
    }

    static class CustomerAddress extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "customerAddress";
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
            return "createUpdateCustomer";
        }

        @Override
        public void submit(CaseInfo cse, SubmissionState state) {
            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                sleep(3);
                System.out.println("customerAddress-1 " + Thread.currentThread().getName());
                return "customerAddress-1";
            }, pool);

            state.submit(future1, s -> {
                System.out.println("customerAddress-thenApply2: " + s);
                cse.setCaseId(s);
            });
            state.rollbackIfFailed(rollback -> {
                System.out.println("customerAddress-rollback2");
            });
        }
    }

    static class ContactChannel extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "contactChannel";
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
            return "createUpdateCustomer";
        }

        @Override
        public void submit(CaseInfo cse, SubmissionState state) {
            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                sleep(3);
                System.out.println("contactChannel-1 " + Thread.currentThread().getName());
                return "contactChannel-1";
            }, pool);

            state.submit(future1, s -> {
                System.out.println("contactChannel-thenApply2: " + s);
                cse.setCaseId(s);
            });
            state.rollbackIfFailed(rollback -> {
                System.out.println("contactChannel-rollback2");
            });
        }
    }

    static class MarketingInfo extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "marketingInfo";
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
            return "createUpdateCustomer";
        }

        @Override
        public void submit(CaseInfo cse, SubmissionState state) {
            System.out.println("marketingInfo-plugin2");
            state.submit(CompletableFuture.completedFuture(null));
        }
    }

    static class GenerateDocForSecurities extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "generateDocForSecurities";
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
            return "createUpdateCustomer";
        }

        @Override
        public void submit(CaseInfo cse, SubmissionState state) {
            System.out.println("generateDocForSecurities-plugin2");
            state.submit(CompletableFuture.completedFuture(null));
        }
    }

    static class OpenAccount extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "openAccount";
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
            return "createUpdateCustomer";
        }

        @Override
        public void submit(CaseInfo cse, SubmissionState state) {
            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                sleep(3);
                System.out.println("openAccount-1 " + Thread.currentThread().getName());
                return "openAccount-1";
            }, pool);

            state.submit(future1, s -> {
                System.out.println("openAccount-thenApply2: " + s);
                cse.setCaseId(s);
            });
            state.rollbackIfFailed(rollback -> {
                System.out.println("openAccount-rollback2");
            });
        }
    }

    static class CustRelationship extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "custRelationship";
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
            return "openAccount";
        }

        @Override
        public void submit(CaseInfo cse, SubmissionState state) {
            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                sleep(1);
                System.out.println("custRelationship-1 " + Thread.currentThread().getName());
                return "custRelationship-1";
            }, pool);

            state.submit(future1, s -> {
                System.out.println("custRelationship-thenApply1: " + s);
                cse.setCaseId(s);
            });
            state.rollbackIfFailed(rollback -> {
                System.out.println("custRelationship-rollback1");
            });
        }
    }

    static class CustomerRemark extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "customerRemark";
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
            return "openAccount";
        }

        @Override
        public void submit(CaseInfo cse, SubmissionState state) {
            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                sleep(1);
                System.out.println("remarks-1 " + Thread.currentThread().getName());
                return "remarks-1";
            }, pool);

            state.submit(future1, s -> {
                System.out.println("remarks-thenApply3: " + s);
                cse.setCaseId(s);
            });
            state.rollbackIfFailed(rollback -> {
                System.out.println("remarks-rollback3");
            });
        }
    }

    static class JakJai extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "jakJai";
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
            return "customerRemark";
        }

        @Override
        public void submit(CaseInfo cse, SubmissionState state) {

            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                sleep(1);
                System.out.println("jakJai-1 " + Thread.currentThread().getName());
                return "jakJai-1";
            }, pool);

            state.submit(future1, s -> {
                System.out.println("jakJai-thenApply4: " + s);
                cse.setCaseId(s);
            });

            state.rollbackIfFailed(rollback -> {
                System.out.println("jakJai-rollback4");
            });
            System.out.println("jakJai-1");
//            state.submit(CompletableFuture.completedFuture(null));

        }
    }

    static class DebitCard extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "debitCard";
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
            return "customerRemark";
        }

        @Override
        public void submit(CaseInfo cse, SubmissionState state) {

            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                sleep(1);
                System.out.println("debitCard-1 " + Thread.currentThread().getName());
                return "debitCard-1";
            }, pool);

            state.submit(future1, s -> {
                System.out.println("debitCard-thenApply1: " + s);
                cse.setCaseId(s);

                //throw new RuntimeException("EEEEXXXception...");
            });

            state.rollbackIfFailed(rollback -> {
                System.out.println("debitCard-rollback1");
            });
            System.out.println("debitCard-1");
//            state.submit(CompletableFuture.completedFuture(null));

        }
    }

    static class AppForm extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "appform";
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
            return "customerRemark";
        }

        @Override
        public void submit(CaseInfo cse, SubmissionState state) {

            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                sleep(1);
                System.out.println("appform-1 " + Thread.currentThread().getName());
                return "appform-1";
            }, pool);

            state.submit(future1, s -> {
                System.out.println("appform-thenApply4: " + s);
                cse.setCaseId(s);
            });

            state.rollbackIfFailed(rollback -> {
                System.out.println("appform-rollback4");
            });
            System.out.println("appForm-1");
//            state.submit(CompletableFuture.completedFuture(null));

        }
    }

    static class CustImages extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "custImages";
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
            return "appform";
        }

        @Override
        public void submit(CaseInfo cse, SubmissionState state) {

            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                sleep(1);
                System.out.println("custImages-1 " + Thread.currentThread().getName());
                return "custImages-1";
            }, pool);
            CompletableFuture<String> futureFailed = CompletableFuture.supplyAsync(() -> {
                sleep(3);
                System.out.println("custImages-2 " + Thread.currentThread().getName());
                throw new RuntimeException("errooror: custImages");
            }, pool);

            state.submit(future1, s -> {
                System.out.println("custImages-thenApply4: " + s);
                cse.setCaseId(s);
            });
            state.rollbackIfFailed(rollback -> {
                System.out.println("custImages-rollback4");
            });
//            state.submit(futureFailed, s -> {
//                System.out.println("custImages-thenApply3: " + s);
//                cse.setCaseId(s);
//            });
//            state.rollbackIfFailed(rollback -> {
//                System.out.println("custImages-rollback3");
//            });

//            CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
//                if (true) throw new RuntimeException("eeeorrr");
//            });
//            state.submit(f1);

//            if (true) throw new RuntimeException("custImages-eeeorrr");
//            state.submit(CompletableFuture.completedFuture(null));

        }
    }

    static class MutualFund extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "mutualFund";
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
            return "noRevert";
        }

        @Override
        public void submit(CaseInfo cse, SubmissionState state) {

            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                sleep(1);
                System.out.println("mutualFund-1 " + Thread.currentThread().getName());
                return "mutualFund-1";
            }, pool);

            state.submit(future1, s -> {
                System.out.println("mutualFund-thenApply4: " + s);
                cse.setCaseId(s);
            });

            state.rollbackIfFailed(rollback -> {
                System.out.println("mutualFund-rollback4");
            });

        }
    }

    static class EZApp extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "ezApp";
        }

        @Override
        public boolean isHardStop() {
            return false;
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
                sleep(1);
                System.out.println("ezApp-1 " + Thread.currentThread().getName());
                return "ezApp-1";
            }, pool);

            state.submit(future1, s -> {
                System.out.println("ezApp-thenApply4: " + s);
                cse.setCaseId(s);
            });

            state.rollbackIfFailed(rollback -> {
                System.out.println("ezApp-rollback4");
            });

        }
    }

    static class NssEmail extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "nssEmail";
        }

        @Override
        public boolean isHardStop() {
            return false;
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
                sleep(1);
                System.out.println("nssEmail-1 " + Thread.currentThread().getName());
                return "nssEmail-1";
            }, pool);

            state.submit(future1, s -> {
                System.out.println("nssEmail-thenApply1: " + s);
                cse.setCaseId(s);
            });

            state.rollbackIfFailed(rollback -> {
                System.out.println("nssEmail-rollback1");
            });

        }
    }

    static class NssSms extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "nssSms";
        }

        @Override
        public boolean isHardStop() {
            return false;
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
                sleep(1);
                System.out.println("nssSms-1 " + Thread.currentThread().getName());
                return "nssSms-1";
            }, pool);

            state.submit(future1, s -> {
                System.out.println("nssSms-thenApply1: " + s);
                cse.setCaseId(s);
            });

            state.rollbackIfFailed(rollback -> {
                System.out.println("nssSms-rollback1");
            });

        }
    }

    static class Fatca extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "fatca";
        }

        @Override
        public boolean isHardStop() {
            return false;
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
                sleep(1);
                System.out.println("fatca-1 " + Thread.currentThread().getName());
                return "fatca-1";
            }, pool);

            state.submit(future1, s -> {
                System.out.println("fatca-thenApply1: " + s);
                cse.setCaseId(s);
            });

            state.rollbackIfFailed(rollback -> {
                System.out.println("fatca-rollback1");
            });

        }
    }

    static class Consent extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "consent";
        }

        @Override
        public boolean isHardStop() {
            return false;
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
                sleep(1);
                System.out.println("consent-1 " + Thread.currentThread().getName());
                return "consent-1";
            }, pool);

            CompletableFuture<Object> future2 = future1.handle((ok, ko) -> {
                throw new RuntimeException("eee rrror xxx 1");
            });

            state.submit(future1, s -> {
                System.out.println("consent-thenApply1: " + s);
                cse.setCaseId(s.toString());

                //throw new RuntimeException("eee rrror xxx 2");
            });

            state.rollbackIfFailed(rollback -> {
                System.out.println("consent-rollback1");
            });

        }
    }

    static class RiskAssessment extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "riskAssessment";
        }

        @Override
        public boolean isHardStop() {
            return false;
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
                sleep(1);
                System.out.println("riskAssessment-1 " + Thread.currentThread().getName());
                return "riskAssessment-1";
            }, pool);

            state.submit(future1, s -> {
                System.out.println("riskAssessment-thenApply1: " + s);
                cse.setCaseId(s);
            });

            state.rollbackIfFailed(rollback -> {
                System.out.println("riskAssessment-rollback1");
            });

        }
    }

    static class PromptPay extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "promptPay";
        }

        @Override
        public boolean isHardStop() {
            return false;
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
                sleep(1);
                System.out.println("promptPay-1 " + Thread.currentThread().getName());
                return "promptPay-1";
            }, pool);

            state.submit(future1, s -> {
                System.out.println("promptPay-thenApply1: " + s);
                cse.setCaseId(s);
            });

            state.rollbackIfFailed(rollback -> {
                System.out.println("promptPay-rollback1");
            });

        }
    }

    static class SubmitDocumentToNas extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "submitDocumentToNas";
        }

        @Override
        public boolean isHardStop() {
            return false;
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
                sleep(1);
                System.out.println("submitDocumentToNas-1 " + Thread.currentThread().getName());
                return "submitDocumentToNas-1";
            }, pool);

            state.submit(future1, s -> {
                System.out.println("submitDocumentToNas-thenApply1: " + s);
                cse.setCaseId(s);
            });

            state.rollbackIfFailed(rollback -> {
                System.out.println("submitDocumentToNas-rollback1");
            });

        }
    }

    static class NDID extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "ndid";
        }

        @Override
        public boolean isHardStop() {
            return false;
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
                sleep(1);
                System.out.println("ndid-1 " + Thread.currentThread().getName());
                return "ndid-1";
            }, pool);

            state.submit(future1, s -> {
                System.out.println("ndid-thenApply4: " + s);
                cse.setCaseId(s);
            });

            state.rollbackIfFailed(rollback -> {
                System.out.println("ndid-rollback4");
            });

        }
    }

    static class SecuritiesAccount extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "securitiesAccount";
        }

        @Override
        public boolean isHardStop() {
            return false;
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
                sleep(1);
                System.out.println("securitiesAccount-1 " + Thread.currentThread().getName());
                return "securitiesAccount-1";
            }, pool);

            state.submit(future1, s -> {
                System.out.println("securitiesAccount-thenApply4: " + s);
                cse.setCaseId(s);
            });

            state.rollbackIfFailed(rollback -> {
                System.out.println("securitiesAccount-rollback4");
            });

        }
    }

    static class NotifyCustomer extends BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "notifyCustomer";
        }

        @Override
        public boolean isHardStop() {
            return false;
        }

        @Override
        public boolean isApply(CaseInfo cse) {
            return true;
        }

        @Override
        public String getParent() {
            return "pp";
        }

        @Override
        public void submit(CaseInfo cse, SubmissionState state) {

            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                sleep(1);
                System.out.println("notifyCustomer-1 " + Thread.currentThread().getName());
                return "notifyCustomer-1";
            }, pool);

            state.submit(future1, s -> {
                System.out.println("notifyCustomer-thenApply4: " + s);
                cse.setCaseId(s);
            });

            state.rollbackIfFailed(rollback -> {
                System.out.println("notifyCustomer-rollback4");
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
        private boolean isHardStop;
        private LocalDateTime createdDate;
        private long timeUsed;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WorkflowStep that = (WorkflowStep) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        public static WorkflowStep fromPlugin(BaseSubmissionPlugin plugin, Exception e) {
            return new WorkflowStep(plugin.getName(), "ERROR", e.getMessage(), plugin.isHardStop(), LocalDateTime.now(), 0);
        }
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
