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
public class test_12_Array_2_Dimenstion {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        
        String[][] rooms = {
            {"Available", "Available", "Available", "Available", "Available"},
            {"Available", "Available", "Available", "Available", "Available"},
            {"Available", "Available", "Available", "Available", "Busy"} 
        };

        Scanner sc = new Scanner(System.in);

        while (true) {
            // Count Available / Busy
            int available = 0, busy = 0;

            for (int i = 0; i < rooms.length; i++) {
                for (int j = 0; j < rooms[i].length; j++) {
                    if (rooms[i][j].equals("Available")) available++;
                    else busy++;
                }
            }

            System.out.println("\nAvailable: " + available);
            System.out.println("Busy: " + busy);

            System.out.println("1. Check In");
            System.out.println("2. Check Out");
            System.out.println("3. Exit");
            System.out.print("Please choose: ");

            int choice = sc.nextInt();

            if (choice == 1) {
                // ============================
                // ✔ CHECK IN (loop until valid)
                // ============================
                while (true) {
                    System.out.print("\nChoose floor (1-3): ");
                    int floor = sc.nextInt() - 1;

                    System.out.print("Choose room number (1-5): ");
                    int room = sc.nextInt() - 1;

                    if (!rooms[floor][room].equals("Available")) {
                        System.out.println("❌ This room is BUSY. Try again.");
                    } else {
                        rooms[floor][room] = "Busy";
                        System.out.println("✔ Check-In Successful!");
                        break; 
                    }
                }

            } else if (choice == 2) {
                // ============================
                // ✔ CHECK OUT (loop until valid)
                // ============================
                while (true) {
                    System.out.print("\nChoose floor (1-3): ");
                    int floor = sc.nextInt() - 1;

                    System.out.print("Choose room number (1-5): ");
                    int room = sc.nextInt() - 1;

                    if (!rooms[floor][room].equals("Busy")) {
                        System.out.println("❌ This room is NOT BUSY. Try again.");
                    } else {
                        rooms[floor][room] = "Available";
                        System.out.println("✔ Check-Out Successful!");
                        break;
                    }
                }

            } else if (choice == 3) {
                System.out.println("Goodbye!");
                break;

            } else {
                System.out.println("Invalid option.");
            }
        }
        
        
    }
    
}
