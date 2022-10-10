package com.gailo22.reactive;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class Main {

    public static void main(String[] args) throws InterruptedException {
//        Flux.range(1, 20)
//                .log()
//                .subscribe();

        Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);
        Flux<String> flux = Flux
                .range(1, 20)
                .map(i -> 10 + i)
                .subscribeOn(s)
                .log()
                .map(i -> "value " + i);

        new Thread(() -> flux.subscribe(System.out::println)).start();

    }
}
