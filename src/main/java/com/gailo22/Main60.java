package com.gailo22;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

public class Main60 {

    @Data
    static class CaseInfo {
        String name;
    }

    interface Plugin<T> {
        boolean isApply(T t);

        CompletableFuture<Void> run(T t);
    }

    static final class PluginManager {
        List<Plugin<CaseInfo>> plugins;

        private PluginManager(List<Plugin<CaseInfo>> plugins) {
            this.plugins = plugins;
        }

        static PluginManager of(List<Plugin<CaseInfo>> plugins) {
            return new PluginManager(plugins);
        }

        private void start() {
            CaseInfo caseInfo = new CaseInfo();
            caseInfo.setName("hello");

            for (Plugin<CaseInfo> plugin : plugins) {
                System.out.println("start sequential..");
                if (plugin.isApply(caseInfo)) {
                    plugin.run(caseInfo).join();
                }
                System.out.println("end sequential.");
            }

            System.out.println("--------------------");
            System.out.println("start parallel..");
            List<CompletableFuture<Void>> completableFutures = plugins.stream()
                    .filter(p -> p.isApply(caseInfo))
                    .map(p -> p.run(caseInfo))
                    .collect(toList());

            sequence(completableFutures)
                    .thenAccept(c -> System.out.println(c))
                    .join();

            System.out.println("end parallel..");
        }
    }

    static class Plugin1 implements Plugin<CaseInfo> {

        @Override
        public boolean isApply(CaseInfo caseInfo) {
            return true;
        }

        @Override
        public CompletableFuture<Void> run(CaseInfo caseInfo) {
            return CompletableFuture.runAsync(() -> {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Plugin1 run.. " + Thread.currentThread().getName());
            });
        }
    }

    static class Plugin2 implements Plugin<CaseInfo> {

        @Override
        public boolean isApply(CaseInfo caseInfo) {
            return false;
        }

        @Override
        public CompletableFuture<Void> run(CaseInfo caseInfo) {
            try {
                TimeUnit.SECONDS.sleep(new Random().nextInt(3));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Plugin2 run.. " + Thread.currentThread().getName());
            return CompletableFuture.completedFuture(null);
        }
    }

    static class Plugin3 implements Plugin<CaseInfo> {

        @Override
        public boolean isApply(CaseInfo caseInfo) {
            return true;
        }

        @Override
        public CompletableFuture<Void> run(CaseInfo caseInfo) {
            return CompletableFuture.runAsync(() -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Plugin3 run.. " + Thread.currentThread().getName());
            });
        }
    }

    static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> com) {
        return CompletableFuture.allOf(com.toArray(new CompletableFuture<?>[com.size()]))
                .thenApply(v -> com.stream()
                        .map(CompletableFuture::join)
                        .collect(toList()));
    }

    public static void main(String[] args) {
        List<Plugin<CaseInfo>> plugins = new ArrayList<>();
        plugins.add(new Plugin1());
        plugins.add(new Plugin2());
        plugins.add(new Plugin3());

        PluginManager.of(plugins).start();

    }

}
