/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package test_2;

import java.util.Scanner;

/**
 *
 * @author Admin
 */
public class test_7_Loop {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        
        Scanner sc = new Scanner(System.in);
        String again;

        do {
            System.out.println("1. Sum = 1^3 + 2^3 + ... + n^3");
            System.out.println("2. Show prime numbers from 1 to num");
            System.out.print("Choose (1 or 2): ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter n: ");
                    int n = sc.nextInt();
                    int i = 1, sum = 0;

                    // while loop for sum of cubes
                    while (i <= n) {
                        sum += i * i * i;
                        i++;
                    }

                    System.out.println("Sum = " + sum);
                    break;

                case 2:
                    System.out.print("Enter num: ");
                    int num = sc.nextInt();

                    System.out.println("Prime numbers from 1 to " + num + ":");

                    // for loop to find prime numbers
                    for (int a = 2; a <= num; a++) {
                        boolean isPrime = true;
                        for (int b = 2; b <= a / 2; b++) {
                            if (a % b == 0) {
                                isPrime = false;
                                break;
                            }
                        }
                        if (isPrime) {
                            System.out.print(a + " ");
                        }
                    }
                    System.out.println();
                    break;

                default:
                    System.out.println("Invalid choice!");
            }

            System.out.print("\nAgain? (y or yes): ");
            again = sc.next();

        } while (again.equalsIgnoreCase("y") || again.equalsIgnoreCase("yes"));

        
        
    }
    
}
