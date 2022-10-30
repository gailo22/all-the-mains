package com.gailo22.java19;

record Person(String name, int age) {}

public class Main4 {
    public static void main(String[] args) {
        var person = new Person("John", 40);
        System.out.println(person);
    }
}
