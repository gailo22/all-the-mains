package com.gailo22;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main21 {

    private static int maxConsecutiveOne(List<Integer> bins) {
        String binStr = bins.stream().map(String::valueOf).collect(Collectors.joining());
        String[] arr = binStr.split("0");
        
        List<Integer> sortedByLength = Arrays.stream(arr).map(x -> x.length())
        		.sorted(Comparator.reverseOrder())
        		.collect(Collectors.toList());
		return sortedByLength.get(0);
    }
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        
        List<Integer> bins = new ArrayList<>();
        while (n > 0) {
            int remaining = n%2;
            n = n/2;
            bins.add(remaining);
        }
        
        int maxOne = maxConsecutiveOne(bins);
        System.out.println(maxOne);
        
        sc.close();
    }
}