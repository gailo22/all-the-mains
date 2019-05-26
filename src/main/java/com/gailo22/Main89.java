package com.gailo22;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Main89 {
    public static void main(String[] args) {

        new PrinterTask().print("hello world");

    }
}

class ActiveObject {
    private Thread t;
    private BlockingQueue<Runnable> requestQueue;

    public ActiveObject() {
        requestQueue = new LinkedBlockingDeque<>();
        t = new Thread(() -> {
            try {
                while (!t.isInterrupted()) {
                    requestQueue.take().run();
                }
            } catch (InterruptedException ex) {
            }
        });

        t.start();
    }

    public void accept(Runnable request) {
        try {
            requestQueue.put(request);
        } catch (InterruptedException e) {
        }
    }
}

class PrinterTask extends ActiveObject {
    public void print(String message) {
        accept(() -> {
            System.out.println(message);
        });
    }
}
