package com.gailo22.reactive;

import reactor.core.publisher.Flux;

public class Main {

    public static void main(String[] args) {
        Flux.range(1, 20)
                .log()
                .subscribe();
    }
}
