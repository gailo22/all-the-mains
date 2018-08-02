package com.gailo22;

import com.spotify.futures.CompletableFutures;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Main73 {

    interface BaseEcService {
        String getName();
        boolean isApply(CaseInfo caseInfo);
        CompletableFuture<Workflow> ec(CaseInfo caseInfo);
    }
    static class OpenAccountEcService implements BaseEcService {

        @Override
        public String getName() {
            return "openAccount";
        }

        @Override
        public boolean isApply(CaseInfo caseInfo) {
            return true;
        }

        @Override
        public CompletableFuture<Workflow> ec(CaseInfo caseInfo) {
            return CompletableFuture.completedFuture(new Workflow("openAccount", "ECED"));
        }
    }
    static class PaymentOrderEcService implements BaseEcService {

        @Override
        public String getName() {
            return "paymentOrder";
        }

        @Override
        public boolean isApply(CaseInfo caseInfo) {
            return true;
        }

        @Override
        public CompletableFuture<Workflow> ec(CaseInfo caseInfo) {
            return CompletableFuture.completedFuture(new Workflow("paymentOrder", "ERROR"));
        }
    }
    static class PromptPayEcService implements BaseEcService {

        @Override
        public String getName() {
            return "promptPay";
        }

        @Override
        public boolean isApply(CaseInfo caseInfo) {
            return true;
        }

        @Override
        public CompletableFuture<Workflow> ec(CaseInfo caseInfo) {
            return CompletableFuture.completedFuture(new Workflow("promptPay", "ECED"));
        }
    }

    static class WorkflowService {
        List<Workflow> getWorkflowsForEc() {
            return Arrays.asList(new Workflow("openAccount", "REG"), new Workflow("promptPay", "REG"));
        }
    }

    //@Service
    static class EcService {

        private static List<BaseEcService> ecServiceList = new ArrayList<>();
        static {
            ecServiceList.add(new OpenAccountEcService());
            ecServiceList.add(new PaymentOrderEcService());
            ecServiceList.add(new PromptPayEcService());
        }

        void start(CaseInfo caseInfo) {
            // get only applicable workflow
            List<Workflow> workflows = new WorkflowService().getWorkflowsForEc();
            List<BaseEcService> ecServices = ecServiceList.stream()
                    .filter(it -> it.isApply(caseInfo))
                    .filter(it -> workflows.stream().anyMatch(wf -> wf.name.equals(it.getName())))
                    .collect(Collectors.toList());

            System.out.println(ecServices);

            List<CompletableFuture<Workflow>> futures =
                    ecServices.stream().map(x -> x.ec(caseInfo)).collect(Collectors.toList());
            CompletableFuture<List<Workflow>> allAsList = CompletableFutures.allAsList(futures);

            allAsList.thenAccept(x -> {
                // update product workflow in db - via caseService.updateCase()
                System.out.println(x);
            });
        }

    }

    @Data
    static class CaseInfo {
        private String caseId;
    }
    @Data
    @AllArgsConstructor
    static class Workflow {
        private String name;
        private String status;
    }

	public static void main(String[] args) {

        CaseInfo caseInfo = new CaseInfo();
        EcService ecService = new EcService();
        ecService.start(caseInfo);

    }

}
