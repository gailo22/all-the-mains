package com.gailo22.java19;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws Exception {
//        ExecutorService executor = Executors.newFixedThreadPool(100);
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < 1_000; i++) {
            tasks.add(new Task(i));
        }

        long time = System.currentTimeMillis();
        List<Future<Integer>> futures = executor.invokeAll(tasks);
        long sum = 0;
        for (Future<Integer> future : futures) {
            sum += future.get();
        }

        time = System.currentTimeMillis() - time;
        System.out.println("sum = " + sum + "; time = " + time + " ms");
        executor.shutdown();
    }

}

class Task implements Callable<Integer> {

    private final int number;

    public Task(int number) {
        this.number = number;
    }

    @Override
    public Integer call() {
        System.out.printf("Thread %s - Task %d waiting...%n", Thread.currentThread().getName(), number);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.printf("Thread %s - Task %d canceled.%n", Thread.currentThread().getName(), number);
            return -1;
        }

        System.out.printf("Thread %s - Task %d finished.%n", Thread.currentThread().getName(), number);
        return ThreadLocalRandom.current().nextInt(100);
    }
}



