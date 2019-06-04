package com.gailo22;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main87 {

    public static void main(String[] args) {

        ExecutorService es = Executors.newFixedThreadPool(2);

        CyclicBarrier cb = new CyclicBarrier(2, new Match());

        es.submit(new Player(cb));
        es.submit(new Player(cb));

        es.shutdown();
    }

}

class Player extends Thread {
    CyclicBarrier cb;

    Player(CyclicBarrier cb) {
        this.cb = cb;
    }

    @Override
    public void run() {
        try {
            cb.await();
        } catch (InterruptedException | BrokenBarrierException e) {
        }
    }
}

class Match implements Runnable {
    public void run() {
        System.out.println("Match is starting...");
    }
}
