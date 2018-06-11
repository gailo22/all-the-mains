package com.gailo22;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class Main61 {

    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool();

    private static final double ONE_BILLION = 1_000_000_000;

    public static void main(String[] args) {

        List<Integer> integers = new Random().ints().limit(100000)
            .boxed()
            .collect(Collectors.toList());

        Integer maxInt = time(() -> parallelCompute(integers));
        System.out.println("Max int: " + maxInt);

    }

    private static Integer time(Op<Integer> operation) {
        long startTime = System.nanoTime();
        Integer result = operation.runOp();
        long endTime = System.nanoTime();
        double elapsedSeconds = (endTime - startTime) / ONE_BILLION;
        System.out.printf("Elapsed time: %.3f seconds.%n", elapsedSeconds);
        return result;
    }

    private static Integer parallelCompute(List<Integer> list) {
        return FORK_JOIN_POOL.invoke(new FindMaxTask(list));
    }

}

class FindMaxTask extends RecursiveTask<Integer> {

    private List<Integer> integers;

    public FindMaxTask(List<Integer> integers) {
        this.integers = integers;
    }

    @Override
    protected Integer compute() {
        int size = this.integers.size();
        if (size < 1) return 0;
        if (size == 1) {
            return integers.get(0);
        }
        if (size <= 2) {
            return Integer.max(integers.get(0), integers.get(1));
        }
        int mid = size / 2;
        List<Integer> left = this.integers.subList(0, mid);
        List<Integer> right = this.integers.subList(mid + 1, size);
        FindMaxTask leftTask = new FindMaxTask(left);
        FindMaxTask rightTask = new FindMaxTask(right);
        leftTask.fork();

        Integer rightResult = rightTask.compute();
        Integer leftResult = leftTask.join();

        return Integer.max(leftResult, rightResult);
    }
}

interface Op<T> {
    T runOp();
}
