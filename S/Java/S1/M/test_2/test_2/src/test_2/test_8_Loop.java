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
public class test_8_Loop {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        
        Scanner input = new Scanner(System.in);
        int sum = 0;
        int n;

        System.out.println("Enter numbers (0 to stop):");

        for (;;) { // infinite for loop, we break when n == 0
            System.out.print("n: ");
            n = input.nextInt();

            if (n == 0) { // stop condition
                break;
            }

            if (n < 0) { // skip negative numbers
                continue;
            }

            sum += n;
        }

        System.out.println("--------------------------------");
        System.out.println("sum=" + sum);
        
    }
    
}
