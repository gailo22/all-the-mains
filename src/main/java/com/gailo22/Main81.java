package com.gailo22;

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

        try (Stream<String> lines = Files.lines(file)) {
            lines.skip(1)
                    .map(ExFunction.wrap(factory::build))
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

}
