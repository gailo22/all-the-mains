package com.gailo22;

import com.spotify.futures.CompletableFutures;
import lombok.Data;
import org.jooq.lambda.tuple.Tuple3;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main69 {

    interface BaseEc {
        String getName();

        CompletableFuture<Tuple3<String, String, Runnable>> ec(CaseInfo caseInfo);
    }

    static class Ec1 implements BaseEc {

        @Override
        public String getName() {
            return "ec1";
        }

        @Override
        public CompletableFuture<Tuple3<String, String, Runnable>> ec(CaseInfo caseInfo) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                }

                return new Tuple3<>(getName(), "xxx", () -> {
                    System.out.println("xxx: ec1");
                    caseInfo.setCaseId("456");
                });
            });
        }
    }

    static class Ec2 implements BaseEc {

        @Override
        public String getName() {
            return "ec2";
        }

        @Override
        public CompletableFuture<Tuple3<String, String, Runnable>> ec(CaseInfo caseInfo) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                }

                return new Tuple3<>(getName(), "zzz", () -> {
                    System.out.println("zzz: ec2");
                    caseInfo.setName("hello");
                });
            });
        }
    }

    @Data
    static class CaseInfo {
        private String caseId;
        private String name;
    }

    public static void main(String[] args) {
        List<BaseEc> ecs = Arrays.asList(new Ec1(), new Ec2());

        CaseInfo caseInfo = new CaseInfo();
        List<CompletableFuture<Tuple3<String, String, Runnable>>> futures =
            ecs.stream()
                .map(x -> x.ec(caseInfo))
                .collect(Collectors.toList());

        CompletableFuture<List<Tuple3<String, String, Runnable>>> allAsList =
            CompletableFutures.allAsList(futures);

        System.out.println("caseInfo1: " + caseInfo);
        allAsList.thenAccept(x -> {
            List<Runnable> actions = x.stream().map(y -> y.v3).collect(Collectors.toList());
            actions.forEach(Runnable::run);
            System.out.println("caseInfo2: " + caseInfo);
        }).join();
    }

}
