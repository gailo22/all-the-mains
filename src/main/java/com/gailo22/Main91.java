package com.gailo22;

public class Main91 {

    public static void main(String[] args) {
        int[][] dp = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9},
            {10, 11, 12}
        };

        for (int row = 0; row < dp.length; row++) {
            for (int col = 0; col < dp[0].length; col++) {
                System.out.print(dp[row][col] + "(" + row + "," + col + ") ");
            }

            System.out.println();
        }
    }

}
