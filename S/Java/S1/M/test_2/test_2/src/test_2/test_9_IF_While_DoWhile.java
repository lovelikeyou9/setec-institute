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
public class test_9_IF_While_DoWhile {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        
        Scanner sc = new Scanner(System.in);
        String username, password;
        int attempt = 0;
        boolean loginSuccess = false;

        // --- Login section (3 times) ---
        while (attempt < 3) {
            System.out.print("UserName= ");
            username = sc.nextLine();
            System.out.print("Password= ");
            password = sc.nextLine();

            if (username.equals("name") && password.equals("Sv2")) {
                loginSuccess = true;
                break;
            } else {
                attempt++;
                System.out.println("Incorrect! Try again (" + (3 - attempt) + " left)");
            }
        }

        if (!loginSuccess) {
            System.out.println("Login failed 3 times. Exiting...");
            return;
        }

        // --- After login success ---
        int totalStudents = 0;
        int totalPass = 0;
        String again;

        do {
            totalStudents++;
            System.out.println("-------------------------------------");
            System.out.println("Student " + totalStudents + ":");

            // Java score (do-while)
            int java;
            do {
                System.out.print("Java[0-100]= ");
                java = sc.nextInt();
            } while (java < 0 || java > 100);

            // O1 score (while)
            int o1;
            System.out.print("O1[0-100]= ");
            o1 = sc.nextInt();
            while (o1 < 0 || o1 > 100) {
                System.out.print("O1[0-100]= ");
                o1 = sc.nextInt();
            }

            // SA score (for)
            int sa = -1;
            for (;;) {
                System.out.print("SA[0-100]= ");
                sa = sc.nextInt();
                if (sa >= 0 && sa <= 100) break;
            }

            int totalScore = java + o1 + sa;
            double average = totalScore / 3.0;

            System.out.println("Total Score: " + totalScore);
            System.out.println("Average: " + average);

            // Count pass (average >= 50)
            if (average >= 50)
                totalPass++;

            // Ask for next student
            sc.nextLine(); // clear buffer
            do {
                System.out.print("One more student(y/n): ");
                again = sc.nextLine().toLowerCase();
            } while (!again.equals("y") && !again.equals("n"));

        } while (again.equals("y"));

        // --- Final output ---
        System.out.println("-------------------------------------");
        System.out.println("Total student: " + totalStudents);
        System.out.println("Total Pass: " + totalPass);
        
        
    }
    
}
