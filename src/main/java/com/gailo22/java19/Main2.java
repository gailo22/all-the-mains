package com.gailo22.java19;

public class Main2 {

    public static void main(String[] args) throws InterruptedException {
        var pThread = Thread.ofPlatform()
                .name("platform-")
                .start(() -> {
                    System.out.println(Thread.currentThread());
                });

        pThread.join();

        var vThread = Thread.ofVirtual()
                .name("virtual-")
                .start(() -> {
                    System.out.println(Thread.currentThread());
                });

        vThread.join();
    }

}
