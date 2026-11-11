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
public class test_10_Array {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        
        Scanner sc = new Scanner(System.in);
        System.out.print("How many students: ");
        int n = sc.nextInt();
        sc.nextLine();

        String[] id = new String[n];
        String[] name = new String[n];
        int[] score = new int[n];
        String[] result = new String[n];

        // --- Input data ---
        for (int i = 0; i < n; i++) {
            System.out.println("\nStudent " + (i + 1) + ":");
            System.out.print("ID: ");
            id[i] = sc.nextLine();
            System.out.print("Name: ");
            name[i] = sc.nextLine();
            System.out.print("Score: ");
            score[i] = sc.nextInt();
            sc.nextLine();

            result[i] = (score[i] >= 50) ? "Pass" : "Fail";
        }

        // --- Output data ---
        System.out.println("\n--------------------------------------");
        System.out.printf("%-8s %-10s %-10s %-10s %-10s\n", "Student", "ID", "Name", "Score", "Result");

        for (int i = 0; i < n; i++) {
            boolean duplicate = false;

            // Check if this ID was already printed before
            for (int j = 0; j < i; j++) {
                if (id[i].equals(id[j])) {
                    duplicate = true;
                    break;
                }
            }

            // If not duplicate, print this student
            if (!duplicate) {
                System.out.printf("%-8d %-10s %-10s %-10d %-10s\n",
                        (i + 1), id[i], name[i], score[i], result[i]);
            }
        }
        
        
        
        
    }
    
}
