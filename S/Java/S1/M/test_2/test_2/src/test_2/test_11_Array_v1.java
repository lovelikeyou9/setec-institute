/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package test_2;

import java.util.Scanner;
import java.util.*;

/**
 *
 * @author Admin
 */
public class test_11_Array_v1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        
        
        
        Scanner sc = new Scanner(System.in);

        System.out.println("Loto (2 digits 1 x70)");
        System.out.println("----------------------");

        int totalPlayer = 0;
        int totalIncome = 0;   // all players' total bet
        int totalExpense = 0;  // total paid to winners

        while (true) {
            totalPlayer++;

            System.out.println("\nPlayer " + totalPlayer);
            System.out.print("How many number you bet: ");
            int n = sc.nextInt();

            int[] num = new int[n];
            int[] price = new int[n];

            int playerTotalBet = 0;
            int playerWin = 0;

            // --- Input data ---
            for (int i = 0; i < n; i++) {
                // Input number (0–99)
                while (true) {
                    System.out.print("num" + (i + 1) + " = ");
                    num[i] = sc.nextInt();

                    if (num[i] < 0 || num[i] > 99) {
                        System.out.println("Invalid! Please enter a number between 0 and 99.");
                    } else {
                        break;
                    }
                }

                // Input price (must be > 0)
                while (true) {
                    System.out.print("price" + (i + 1) + " = ");
                    price[i] = sc.nextInt();

                    if (price[i] <= 0) {
                        System.out.println("Invalid! Price must be greater than 0.");
                    } else {
                        break;
                    }
                }

                playerTotalBet += price[i];
            }

            System.out.println("--------------");

            // --- Fixed result ---
            int result = 99;
            System.out.println("Result: " + result);

            boolean win = false;
            for (int i = 0; i < n; i++) {
                if (num[i] == result) {
                    int winAmount = price[i] * 70;
                    playerWin += winAmount;
                    System.out.println("You win amount = " + winAmount + "$");
                    win = true;
                }
            }

            if (!win) {
                System.out.println("You lost");
            }

            // Update totals
            totalIncome += playerTotalBet;
            totalExpense += playerWin;

            // Ask for another player
            System.out.print("\nOne more player (y/n): ");
            sc.nextLine(); // consume leftover newline
            String ans = sc.nextLine().trim().toLowerCase();

            if (!(ans.equals("y") || ans.equals("yes"))) {
                break;
            }
        }

        // --- Final Summary ---
        System.out.println("\n=======================");
        System.out.println("       Summary");
        System.out.println("=======================");
        System.out.println("Total Player: " + totalPlayer);
        System.out.println("Total Income: " + totalIncome + "$");
        System.out.println("Total Expense: " + totalExpense + "$");
        System.out.println("=======================");
        
        
        
        
    }
    
}
