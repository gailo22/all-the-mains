package com.gailo22;

import java.util.Hashtable;
import java.util.function.Function;

// TODO: solve this
public class Main27 {
    private Hashtable<Integer, Integer> wagons;

    public Main27(int wagonCount, Function<Integer, Integer> fillWagon) {
        this.wagons = new Hashtable<Integer, Integer>();

        for (int i = 0; i < wagonCount; i++) {
            this.wagons.put(i, fillWagon.apply(i));
        }
    }

    public int peekWagon(int wagonIndex) {
        return this.wagons.get(wagonIndex);
    }
    
    public static void main(String[] args) {
        Main27 train = new Main27(10, wagonIndex -> wagonIndex);

        for (int i = 0; i < 10; i++) {
            System.out.println("Wagon: " + i + ", cargo: " + train.peekWagon(i));
        }
    }
}
