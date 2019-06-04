package com.gailo22;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class Main93 {

    private static ExecutorService executor = Executors.newFixedThreadPool(2);

	public static void main(String[] args) {

        List<FileItem> files = FileService.files();
        Instant start = Instant.now();
        List<CompletableFuture<Either<Pair<Throwable, FileItem>, FileItem>>> futures = submit(files);
        CompletableFuture<List<Either<Pair<Throwable, FileItem>, FileItem>>> sequence = sequence(futures);
        sequence.handle((ok, ko) -> {
            if (ko == null) {
                System.out.println("ok: " + ok);
            } else {
                System.out.println("ko: " + ko);
            }
            return null;
        }).join();
//        sequence.thenAccept(System.out::println).join();

        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Total time: " + timeElapsed + " ms");

        executor.shutdown();
    }

    private static List<CompletableFuture<Either<Pair<Throwable, FileItem>, FileItem>>> submit(List<FileItem> files) {
        List<CompletableFuture<Either<Pair<Throwable, FileItem>, FileItem>>> result = files.stream()
                .map(it -> CompletableFuture.supplyAsync(() -> downloadAndGet(it), executor))
                .collect(Collectors.toList());
        return result;
    }

    private static Either<Pair<Throwable, FileItem>, FileItem> downloadAndGet(FileItem item) {
	    try {
            Instant start = Instant.now();
            System.out.println("Thread: " + Thread.currentThread().getName());
            TimeUnit.SECONDS.sleep(new Random().nextInt(5));
            Instant end = Instant.now();
            item.setTime(Duration.between(start, end).toMillis());
            return Either.right(item);
        } catch (Exception ex) {
	        return Either.left(new Pair<>(ex, item));
        }
    }

    static CompletableFuture<List<Either<Pair<Throwable, FileItem>, FileItem>>> sequence(List<CompletableFuture<Either<Pair<Throwable, FileItem>, FileItem>>> com) {
        return CompletableFuture.allOf(com.toArray(new CompletableFuture[0]))
                .thenApply(v -> com.stream()
                        .map(CompletableFuture::join)
                        .collect(toList())
                );
    }

}


class UrlFactory {

    public Optional<Path> getFile(String pathString) {
        try {
            return Optional.ofNullable(Paths.get(this.getClass().getResource(pathString).toURI()));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}

@Data
@AllArgsConstructor
class FileItem {
    private String id;
    private String url;
    private long time;
}

class FileService {
    private static Map<String, FileItem> map;

    public static List<FileItem> files() {
        UrlFactory factory = new UrlFactory();
        Path file = factory.getFile("/urls.txt")
                .orElseThrow(() -> new RuntimeException("cannot get file"));

        try (Stream<String> lines = Files.lines(file)) {
            List<FileItem> result = lines
                    .map(it -> new FileItem(UUID.randomUUID().toString(), it, 0))
                    .collect(Collectors.toList());
            result.forEach(System.out::println);
            map = result.stream().collect(toMap(FileItem::getId, it -> it));
            return result;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return Collections.emptyList();
    }

    public static Map<String, FileItem> getMap() {
        return map;
    }
}