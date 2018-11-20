package com.gailo22;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.spotify.futures.CompletableFutures;
import io.atlassian.fugue.Try;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class Main66 {

    static ExecutorService pool = Executors.newFixedThreadPool(10);

    static class SubmissionState {
        private static final Logger log = LoggerFactory.getLogger(SubmissionState.class);

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
            submit(future, accept, null);
        }

        public <T> void submit(CompletableFuture<T> future, Consumer<T> accept, Consumer<Throwable> acceptEx) {
            if (accept == null) {
                tasks.add(future.thenApply(any -> () -> {
                }));
            } else {
                if (acceptEx == null) {
                    tasks.add(future.thenApply(result -> () -> accept.accept(result)));
                } else {
                    tasks.add(future.handle((result, th) -> {
                        if (th == null) {
                            return () -> accept.accept(result);
                        } else {
                            acceptEx.accept(th);
                            throw Throwables.propagate(th);
                        }
                    }));
                }
            }
        }

        public void rollbackIfFailed(Consumer<Throwable> rollback) {
            rollbackTasks.add(rollback);
        }

        public boolean hasTasks() {
            return !tasks.isEmpty();
        }

        public CompletableFuture<WorkflowStep> getTasks() {
            LocalDateTime start = LocalDateTime.now();
            return CompletableFutures.allAsList(tasks)
                    .thenApply(list -> {
                        Exception exception = null;
                        for (Runnable runnable : list) {
                            try {
                                runnable.run();
                            } catch (Exception ex) {
                                if (exception == null) {
                                    exception = ex;
                                } else {
                                    exception.addSuppressed(ex);
                                }
                            }
                        }
                        if (exception != null) {
                            log.error("Error in SubmissionState.getTasks", exception);
                            throw Throwables.propagate(exception);
                        }
                        long time = System.currentTimeMillis() - start.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                        log.info("getTasks: {} -> size: {}, caseId: {}, time: {}", pluginName, tasks.size(), caseId, time);
                        return new WorkflowStep(pluginName, "SUCCESS", null, isHardStop, start, time);
                    });
        }

        public Function<Throwable, WorkflowStep> getRollbackTasks() {
            LocalDateTime start = LocalDateTime.now();
            return ex -> {
                List<String> rollbackStepExMsgs = new ArrayList<>();
                for (Consumer<Throwable> task : rollbackTasks) {
                    try {
                        task.accept(ex);
                    } catch (Exception rollbackStepEx) {
                        rollbackStepExMsgs.add(rollbackStepEx.getMessage());
                    }

                }
                long time = System.currentTimeMillis() - start.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                log.info("getRollbackTasks: {} -> size: {}, caseId: {}, time: {}", pluginName, rollbackTasks.size(), caseId, time);
                if (rollbackStepExMsgs.isEmpty()) {
                    return new WorkflowStep(this.pluginName, "ERROR", ex.getMessage(), isHardStop, start, time);
                } else {
                    return new WorkflowStep(this.pluginName, "ROLLBACK_FAILED", ex.getMessage() + "\n" + String.join("\n - ", rollbackStepExMsgs), isHardStop, start, time);
                }
            };
        }

        public List<String> getTjlogList() {
            return tjlogList;
        }
    }

    static class SubmissionService {

        private static final Logger log = LoggerFactory.getLogger(SubmissionService.class);
        private static final Pattern PARENT_DELIM_REGEX = Pattern.compile(";");

        private static List<BaseSubmissionPlugin> hardStops;
        private static List<BaseSubmissionPlugin> softStops;

        private static Map<String, Set<String>> parentMap;

        private static String[] pluginList;

        private static List<BaseSubmissionPlugin> plugins = new ArrayList<>();
        static {
            plugins.add(new CreateUpdateCustomer());
//            plugins.add(new PaymentOrders());
            plugins.add(new CustomerAddress());
//            plugins.add(new ContactChannel());
//            plugins.add(new MarketingInfo());
//            plugins.add(new GenerateDocForSecurities());
//            plugins.add(new OpenAccount());
//            plugins.add(new CustRelationship());
//            plugins.add(new CustomerRemark());
//            plugins.add(new JakJai());
//            plugins.add(new DebitCard());
            plugins.add(new AppForm());
//            plugins.add(new EZApp());
//            plugins.add(new NssEmail());
//            plugins.add(new NssSms());
//            plugins.add(new Fatca());
//            plugins.add(new CustImages());
//            plugins.add(new MutualFund());
//            plugins.add(new Consent());
//            plugins.add(new RiskAssessment());
//            plugins.add(new PromptPay());
//            plugins.add(new SubmitDocumentToNas());
//            plugins.add(new NDID());
//            plugins.add(new SecuritiesAccount());
            plugins.add(new NotifyCustomer());
            plugins.add(new ChangeAccountLongNamePlugin());

            pluginList = Arrays.asList("generateApplicationForm->changeAccountLongName",
                    "generateApplicationForm->customerAddress").toArray(new String[0]);
            initPlugins();
        };

        private static void initPlugins() {
            Objects.requireNonNull(plugins);
            Objects.requireNonNull(pluginList);
            initParentMap();
            hardStops = plugins.stream().filter(BaseSubmissionPlugin::isHardStop).collect(Collectors.toList());
            softStops = plugins.stream().filter(it -> !it.isHardStop()).collect(Collectors.toList());

            log.info("hardStops: {}", hardStops);
            log.info("softStops: {}", softStops);
            log.info("parentMap: {}", parentMap);
        }

        private static void initParentMap() {
            parentMap = new HashMap<>();
            for (String it : pluginList) {
                String[] split = it.split("->");
                if (split.length < 2) {
                    continue;
                }
                parentMap.put(split[1], parentSet(split[0]));
            }
        }

        public void start(CaseInfo cse) {
            log.info("Start Submission ...");

            List<String> tjlogList = new ArrayList<>();
            LocalDateTime startTime = LocalDateTime.now();

            Set<WorkflowStep> steps = new HashSet<>();
            Set<String> finishedTasks = new HashSet<>();

            String appFormNo = "APPFORM-001";
            cse.setAppFormNo(appFormNo);

            log.info("Appform No. {}", cse.getAppFormNo());

            //hard stops
            Try<String> tryHardStop = processHardStop(cse, tjlogList, steps, finishedTasks);
            if (tryHardStop.isFailure()) {
                log.info("submission hard stop failed caseId: {}", cse.getCaseId());
                updateCase(cse, appFormNo, CaseStatus.CLOSED);
            } else {
                //soft stop
                processSoftStop(cse, tjlogList, steps, finishedTasks);

                updateCase(cse, appFormNo, CaseStatus.ONBD);

            }

            String stepsString = steps.toString().replaceAll("\\),", "\\),\n");
            log.info("caseId: {}, {}", cse.getCaseId(), stepsString);

            try {
                log.info("caseInfo: {}", new ObjectMapper().writeValueAsString(cse));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        private Try<String> processHardStop(CaseInfo cse, List<String> tjlogList, Set<WorkflowStep> steps, Set<String> finishedTasks) {

            List<BaseSubmissionPlugin> doing = hardStops.stream().filter(this::hasNoParent).collect(Collectors.toList());
            List<CompletableFuture<WorkflowStep>> tasks = new ArrayList<>();
            List<Function<Throwable, WorkflowStep>> rollbackTasks = new ArrayList<>();
            Set<String> startedTasks = new HashSet<>();

            try {
                while (!doing.isEmpty() || !tasks.isEmpty()) {
                    doing = runHardStops(cse, tjlogList, steps, doing, tasks, rollbackTasks, finishedTasks, startedTasks);
                }
            } catch (Exception taskEx) {
                log.info("Error ", taskEx);
                //flush all running tasks
                for (CompletableFuture<WorkflowStep> task : tasks) {
                    try {
                        steps.add(task.join());
                    } catch (Exception ignored) {
                        log.info("");
                    }
                }

                //rollback all tasks
                for (Function<Throwable, WorkflowStep> rollbackTask : rollbackTasks) {
                    WorkflowStep step = rollbackTask.apply(taskEx);
                    steps.add(step);
                }
                return Try.failure(taskEx);
            }
            return Try.successful("success");
        }

        private void processSoftStop(CaseInfo cse, List<String> tjlogList, Set<WorkflowStep> steps, Set<String> finishedTasks) {

            List<BaseSubmissionPlugin> doing = softStops.stream().filter(this::hasNoParent).collect(Collectors.toList());
            List<CompletableFuture<WorkflowStep>> tasks = new ArrayList<>();
            Set<String> startedTasks = new HashSet<>();

            while (!doing.isEmpty() || !tasks.isEmpty()) {
                doing = runSoftStops(cse, tjlogList, steps, doing, tasks, finishedTasks, startedTasks);
            }

        }

        private void updateCase(CaseInfo cse, String appFormNo, CaseStatus status) {
            log.info("update case status: {}", status);
        }

        private List<BaseSubmissionPlugin> runHardStops(CaseInfo cse, List<String> tjlogList, Set<WorkflowStep> steps, List<BaseSubmissionPlugin> doing, List<CompletableFuture<WorkflowStep>> tasks, List<Function<Throwable, WorkflowStep>> rollbackTasks, Set<String> finishedTasks, Set<String> startedTasks) {
            //run all ready plugins
            for (BaseSubmissionPlugin plugin : doing) {
                String pluginName = plugin.getName();
                startedTasks.add(pluginName);
                if (!plugin.isApply(cse)) {
                    finishedTasks.add(pluginName);
                    continue;
                }

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
            }

            //wait for first completed task
            if (!tasks.isEmpty()) {
                CompletableFuture.anyOf(tasks.toArray(new CompletableFuture[0])).join();
                for (int i = tasks.size() - 1; i >= 0; i--) {
                    CompletableFuture<WorkflowStep> task = tasks.get(i);
                    if (!task.isCancelled() && !task.isCompletedExceptionally() && !task.isDone()) {
                        continue;
                    }
                    WorkflowStep step = task.join();
                    finishedTasks.add(step.getName());
                    steps.add(step);
                    tasks.remove(task);
                }
            }

            //get next ready plugins
            doing = hardStops.stream()
                    .filter(it -> !startedTasks.contains(it.getName()))
                    .filter(it -> isReadyToContinue(finishedTasks, it)).collect(Collectors.toList());
            return doing;
        }

        private List<BaseSubmissionPlugin> runSoftStops(CaseInfo cse, List<String> tjlogList, Set<WorkflowStep> steps, List<BaseSubmissionPlugin> doing, List<CompletableFuture<WorkflowStep>> tasks, Set<String> finishedTasks, Set<String> startedTasks) {
            //run all ready plugins
            for (BaseSubmissionPlugin plugin : doing) {
                String pluginName = plugin.getName();
                startedTasks.add(pluginName);
                if (!plugin.isApply(cse)) {
                    finishedTasks.add(pluginName);
                    continue;
                }

                SubmissionState state = new SubmissionState(cse.getCaseId(), pluginName, tjlogList, false);

                try {
                    plugin.submit(cse, state);
                    if (state.hasTasks()) {
                        tasks.add(state.getTasks().handle((ok, ko) -> {
                            if (ko != null) {
                                return state.getRollbackTasks().apply(ko);
                            } else {
                                return ok;
                            }
                        }));
                    } else {
                        finishedTasks.add(pluginName);
                    }
                } catch (Exception ex) {
                    log.info("Error Rollback     ", ex);
                    steps.add(state.getRollbackTasks().apply(ex));
                }
            }

            //wait for first completed task
            if (!tasks.isEmpty()) {
                CompletableFuture.anyOf(tasks.toArray(new CompletableFuture[0])).join();
                for (int i = tasks.size() - 1; i >= 0; i--) {
                    CompletableFuture<WorkflowStep> task = tasks.get(i);
                    if (!task.isCancelled() && !task.isCompletedExceptionally() && !task.isDone()) {
                        continue;
                    }
                    WorkflowStep step = task.join();
                    if ("SUCCESS".equals(step.getStatus())) {
                        finishedTasks.add(step.getName());
                    }
                    steps.add(step);
                    tasks.remove(task);
                }
            }

            //get next ready plugins
            doing = softStops.stream()
                    .filter(it -> !startedTasks.contains(it.getName()))
                    .filter(it -> isReadyToContinue(finishedTasks, it)).collect(Collectors.toList());
            return doing;
        }
        private static Set<String> parentSet(String s) {
            return PARENT_DELIM_REGEX.splitAsStream(s).collect(Collectors.toSet());
        }

        private boolean hasNoParent(BaseSubmissionPlugin it) {
            Set<String> parentSet = parentMap.get(it.getName());
            return parentSet == null || parentSet.isEmpty();
        }

        private boolean hasParent(BaseSubmissionPlugin it) {
            return !hasNoParent(it);
        }

        private boolean isReadyToContinue(Set<String> finishedTasks, BaseSubmissionPlugin it) {
            Set<String> parentSet = parentMap.get(it.getName());
            return hasParent(it) && finishedTasks.containsAll(parentSet);
        }
    }

    public interface BaseSubmissionPlugin {
        String getName();

        boolean isHardStop();

        boolean isApply(CaseInfo caseInfo);

        void submit(CaseInfo caseInfo, SubmissionState state);
    }

    enum CaseStatus {
        CLOSED,
        ONBD,
        OPEN;
    }

    static class CreateUpdateCustomer implements BaseSubmissionPlugin {

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

    static class PaymentOrders implements BaseSubmissionPlugin {

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

    static class CustomerAddress implements BaseSubmissionPlugin {

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
        public void submit(CaseInfo cse, SubmissionState state) {
            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                sleep(1);
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

    static class ContactChannel implements BaseSubmissionPlugin {

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

    static class MarketingInfo implements BaseSubmissionPlugin {

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
        public void submit(CaseInfo cse, SubmissionState state) {
            System.out.println("marketingInfo-plugin2");
            state.submit(CompletableFuture.completedFuture(null));
        }
    }

    static class GenerateDocForSecurities implements BaseSubmissionPlugin {

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
        public void submit(CaseInfo cse, SubmissionState state) {
            System.out.println("generateDocForSecurities-plugin2");
            state.submit(CompletableFuture.completedFuture(null));
        }
    }

    static class OpenAccount implements BaseSubmissionPlugin {

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

    static class CustRelationship implements BaseSubmissionPlugin {

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

    static class CustomerRemark implements BaseSubmissionPlugin {

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

    static class JakJai implements BaseSubmissionPlugin {

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

    static class DebitCard implements BaseSubmissionPlugin {

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

    static class AppForm implements BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "generateApplicationForm";
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
        public void submit(CaseInfo cse, SubmissionState state) {

            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                sleep(1);
                System.out.println("generateApplicationForm-1 " + Thread.currentThread().getName());
                return "generateApplicationForm-1";
            }, pool);

            state.submit(future1, s -> {
                System.out.println("generateApplicationForm-thenApply4: " + s);
                cse.setCaseId(s);
            });

            state.rollbackIfFailed(rollback -> {
                System.out.println("generateApplicationForm-rollback4");
            });
            System.out.println("generateApplicationForm-1");
//            state.submit(CompletableFuture.completedFuture(null));

        }
    }

    static class CustImages implements BaseSubmissionPlugin {

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

    static class MutualFund implements BaseSubmissionPlugin {

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

    static class EZApp implements BaseSubmissionPlugin {

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

    static class NssEmail implements BaseSubmissionPlugin {

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

    static class NssSms implements BaseSubmissionPlugin {

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

    static class Fatca implements BaseSubmissionPlugin {

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

    static class Consent implements BaseSubmissionPlugin {

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

    static class RiskAssessment implements BaseSubmissionPlugin {

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

    static class PromptPay implements BaseSubmissionPlugin {

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

    static class SubmitDocumentToNas implements BaseSubmissionPlugin {

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

    static class NDID implements BaseSubmissionPlugin {

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

    static class SecuritiesAccount implements BaseSubmissionPlugin {

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

    static class NotifyCustomer implements BaseSubmissionPlugin {

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

    static class ChangeAccountLongNamePlugin implements BaseSubmissionPlugin {

        @Override
        public String getName() {
            return "changeAccountLongName";
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
        public void submit(CaseInfo cse, SubmissionState state) {

            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                sleep(7);
                System.out.println("changeAccountLongName-1 " + Thread.currentThread().getName());
                return "changeAccountLongName-1";
            }, pool);

            CompletableFuture<String> futureFailed = CompletableFuture.supplyAsync(() -> {
                sleep(3);
                System.out.println("changeAccountLongName-2 " + Thread.currentThread().getName());
                throw new RuntimeException("errooror: changeAccountLongName");
            }, pool);

            List<Account> accounts = cse.getAccounts();
            int index = 0;
            for (Account account : accounts) {
                CompletableFuture<String> future = index % 2 == 0 ? future1 : futureFailed;
                index++;
                state.submit(future, s -> {
                    System.out.println("changeAccountLongName-thenApply4: " + s);
                    cse.setCaseId(s);
                    account.setUpdateStatus("Y");
                }, throwable -> account.setUpdateStatus("N"));
            }

        }
    }

    @Data
    static class CaseInfo {
        private String caseId;
        private String appFormNo;
        List<Account> accounts;
    }

    @Data
    static class Account {
        private String accNo;
        private String updateStatus;

        public Account(String accNo) {
            this.accNo = accNo;
        }
    }

    @Data
    @AllArgsConstructor
    static class WorkflowStep {
        public static final String ERROR = "ERROR";
        public static final String SUCCESS = "SUCCESS";
        public static final String ROLLBACK_FAILED = "ROLLBACK_FAILED";

        private String name;
        private String status;
        private String errorDesc;
        private boolean isHardStop;
        private LocalDateTime createdDate;
        private long timeUsed;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            WorkflowStep that = (WorkflowStep) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        public static WorkflowStep fromPlugin(BaseSubmissionPlugin plugin, Exception ex) {
            return new WorkflowStep(plugin.getName(), ERROR, ex.getMessage(), plugin.isHardStop(), LocalDateTime.now(), 0);
        }
    }

    public static void main(String[] args) {

        SubmissionService submissionService = new SubmissionService();
        CaseInfo cse = new CaseInfo();
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account("acc1"));
//        accounts.add(new Account("acc2"));
//        accounts.add(new Account("acc3"));
//        accounts.add(new Account("acc4"));

        cse.setAccounts(accounts);
        time("startSubmission: ", () -> {
            submissionService.start(cse);
        });

        pool.shutdown();

    }

    static void sleep(int sec) {
        try {
            TimeUnit.SECONDS.sleep(sec);
        } catch (InterruptedException e) {
        }
    }

    private static void time(String taskName, Runnable operation) {
        long startTime = System.currentTimeMillis();
        operation.run();
        long endTime = System.currentTimeMillis();
        double elapsedSeconds = (endTime - startTime);
        System.out.printf("Elapsed time: %s, %.3f seconds.%n", taskName, elapsedSeconds);
    }

    static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> com) {
        return CompletableFuture.allOf(com.toArray(new CompletableFuture[0]))
                .thenApply(v -> com.stream()
                        .map(CompletableFuture::join)
                        .collect(toList())
                );
    }

}
