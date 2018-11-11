package com.gailo22;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Pair;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class Main78 {

    static Predicate<String[]> runAll = x -> x == null || x.length == 0;
    static Predicate<String[]> runOneDay = x -> x != null && x.length == 1;

    public static void main(String[] args) {

        Either<Boolean, Pair<String, String>> runningDate = getRunningDate(args);

        List<String> list = getRecords(runningDate);
        System.out.println("size: " + list.size());

        list.stream()
            .filter(it -> isSomething(it))
            .map(it -> process(it))
            .forEach(it -> {
                System.out.println(it);
            });

        System.out.println("end");

    }

    private static String process(String it) {
        return "a";
    }

    private static boolean isSomething(String it) {
        return true;
    }

    private static List<String> getRecords(Either<Boolean, Pair<String, String>> runningDate) {
        List<String> list;
        if (runningDate.isLeft()) {
            // run all
            return findAll();
        } else {
            Pair<String, String> pair = runningDate.right().get();
            // run in range
            System.out.println(pair);
            return findInRange(pair.left(), pair.right());
        }
    }

    static List<String> findAll() {
        return Collections.emptyList();
    }

    static List<String> findInRange(String start, String end) {
        return Collections.emptyList();
    }

    private static Either<Boolean, Pair<String, String>> getRunningDate(String[] args) {
        if (runAll.test(args)) return Either.left(true);
        else return Either.right(new Pair<>(args[0], buildEndDate(args)));
    }

    private static String buildEndDate(String[] args) {
        if (runOneDay.test(args)) return args[0] + " 59:59:59";
        else return args[1] + " 59:59:59";
    }


}
