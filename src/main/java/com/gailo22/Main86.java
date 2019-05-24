package com.gailo22;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Main86 {

    public static void main(String[] args) {

        SupperIterable<String> iterable = new SupperIterable<>(
            Arrays.asList("a", "b", "c", "1", "d", "2")
        );

        iterable.filter(it -> Character.isDigit(it.charAt(0)))
            .map(it -> Integer.valueOf(it) * 10)
            .forEach(System.out::println);
    }
}


class SupperIterable<E> implements Iterable<E> {

    private Iterable<E> self;

    public SupperIterable(Iterable<E> s) {
        this.self = s;
    }

    public SupperIterable<E> filter(Predicate<E> pred) {
        List<E> result = new ArrayList<>();
        self.forEach(it -> {
            if (pred.test(it)) result.add(it);
        });

        return new SupperIterable<>(result);
    }

    public <F> SupperIterable<F> map(Function<E, F> f) {
        List<F> result = new ArrayList<>();
        self.forEach(it -> result.add(f.apply(it)));
        return new SupperIterable<>(result);
    }

    public <F> SupperIterable<F> flatMa(Function<E, SupperIterable<F>> f) {
        List<F> result = new ArrayList<>();
        self.forEach(it -> f.apply(it).forEach(result::add));
        return new SupperIterable<>(result);

    }

//    public void forEvery(Consumer<E> consumer) {
//        for (E e : self) {
//            consumer.accept(e);
//        }
//    }

    @Override
    public Iterator<E> iterator() {
        return self.iterator();
    }
}