package com.gailo22;

import java.nio.file.*;
import java.nio.file.Path;
import java.util.List;

public class Main88 {

    public static void main(String[] args) throws Exception {

        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get(".");
        System.out.println(path.toAbsolutePath());
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);

        while (true) {
            WatchKey key = watchService.take();
            List<WatchEvent<?>> watchEvents = key.pollEvents();
            for (WatchEvent<?> watchEvent : watchEvents) {
                WatchEvent.Kind<?> kind = watchEvent.kind();
                System.out.println(kind);
            }

        }
    }
}
