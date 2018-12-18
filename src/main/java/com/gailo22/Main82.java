package com.gailo22;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Collections.reverseOrder;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

public class Main82 {
	
	public static void main(String[] args) {
        XX xx = new XX();
        Path file = xx.getFile("/ipro-loadtest-20181126.csv")
                .orElseThrow(() -> new RuntimeException("cannot get file"));

        try (Stream<String> lines = Files.lines(file)) {
            lines.skip(1)
                    .map(xx::build)
                    .collect(groupingBy(Load::getCaseId,
                            summingInt(Load::getTime)))
//                            mapping(Person::getAge, reducing(Integer::sum))))
                    .entrySet()
                    .stream()
                    .sorted(reverseOrder(Map.Entry.comparingByValue()))
                    .forEach(System.out::println);

        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }

}

class XX {

    public Optional<Path> getFile(String pathString) {
        try {
            return Optional.ofNullable(Paths.get(this.getClass().getResource(pathString).toURI()));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Load build(String string) {
        String[] split = string.split(",");
        return new Load(split[0], Integer.valueOf(split[1].trim()), split[2]);
    }
}

@Data
@AllArgsConstructor
class Load {
    private String name;
    private int time;
    private String caseId;
}
