package com.gailo22.java19;

public class Main5 {

    public static void main(String[] args) {
        Object obj = 1;
        switch (obj) {
            case String s when s.length() > 5 -> System.out.println(s.toUpperCase());
            case String s -> System.out.println(s.toLowerCase());
            case Integer i -> System.out.println(i * i);
            default -> {}
        }
    }

}
