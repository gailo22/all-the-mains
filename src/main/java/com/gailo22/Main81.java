package com.gailo22;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Eithers;
import io.atlassian.fugue.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

public class Main81 {
	
	public static void main(String[] args) {
        PersonFactory factory = new PersonFactory();
        Path file = factory.getFile("/person.txt")
                .orElseThrow(() -> new RuntimeException("cannot get file"));

        List<String> errorList = new ArrayList<>();

        try (Stream<String> lines = Files.lines(file)) {
            lines.skip(1)
                    .map(ExFunction.wrap2(factory::build))
                    .map(it -> getPerson(errorList, it))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(groupingBy(Person::getName,
                            summingInt(Person::getAge)))
//                            mapping(Person::getAge, reducing(Integer::sum))))
                    .entrySet()
                    .forEach(System.out::println);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.out.println(errorList);
    }

    public static Optional<Person> getPerson(List<String> errorList,
                                             Either<Pair<String, Throwable>, Person> it) {
        if (it.isRight()) {
            return Optional.of(it.right().get());
        } else {
            Pair<String, Throwable> pair = it.left().get();
            errorList.add(String.format("%s: %s", pair.left(), pair.right().getMessage()));
            return Optional.empty();
        }
    }

}

class PersonFactory {

    public Optional<Path> getFile(String pathString) {
        try {
            return Optional.ofNullable(Paths.get(this.getClass().getResource(pathString).toURI()));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Person build(String string) {
        String[] split = string.split(",");
        int age = Integer.valueOf(split[2]);
        return new Person(split[0], split[1], age, split[3]);
    }
}

@Data
@AllArgsConstructor
class Person {
    private String name;
    private String lastName;
    private int age;
    private String sex;
}

@FunctionalInterface
interface ExFunction<E, F> {
    F apply(E e) throws Throwable;

    static <E, F> Function<E, Optional<F>> wrap(ExFunction<E, F> op) {
        return e -> {
            try {
                return Optional.of(op.apply(e));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                return Optional.empty();
            }
        };
    }

    static <E, F> Function<E, Either<Pair<E, Throwable>, F>> wrap2(ExFunction<E, F> op) {
        return e -> {
            try {
                return Either.right(op.apply(e));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                return Either.left(new Pair<>(e, throwable));
            }
        };
    }

}
