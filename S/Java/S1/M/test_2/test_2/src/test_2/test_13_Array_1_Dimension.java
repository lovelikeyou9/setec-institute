/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package test_2;

import java.util.Scanner;

public class test_13_Array_1_Dimension {

    // SHOW BUSY ROOMS
    private static void showBusyRooms(String[][] rooms, int[] days) {
        System.out.println("\n===== BUSY ROOMS =====");
        boolean found = false;

        for (int f = 0; f < rooms.length; f++) {
            for (int r = 0; r < rooms[f].length; r++) {
                if (rooms[f][r].equals("Busy")) {
                    int index = f * 5 + r;
                    System.out.println("Floor " + (f + 1) +
                                       " Room " + (r + 1) +
                                       " (" + days[index] + " days)");
                    found = true;
                }
            }
        }

        if (!found) {
            System.out.println("No busy rooms.");
        }
        System.out.println("=======================");
    }

    public static void main(String[] args) {

        String[][] rooms = {
            {"Available", "Available", "Available", "Available", "Available"},
            {"Available", "Available", "Available", "Available", "Available"},
            {"Available", "Available", "Available", "Available", "Busy"}
        };

        // 1D array for days
        int[] days = new int[15];
        days[14] = 3; // example: room 3-5 is busy for 3 days

        Scanner sc = new Scanner(System.in);

        while (true) {

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

                showBusyRooms(rooms, days); // show busy rooms first

                while (true) {
                    System.out.print("\nChoose floor (1-3): ");
                    int f = sc.nextInt() - 1;

                    System.out.print("Choose room number (1-5): ");
                    int r = sc.nextInt() - 1;

                    if (!rooms[f][r].equals("Available")) {
                        System.out.println("❌ This room is BUSY. Try again.");
                    } else {

                        System.out.print("Enter number of days: ");
                        int day = sc.nextInt();

                        rooms[f][r] = "Busy";

                        int index = f * 5 + r;
                        days[index] = day;

                        System.out.println("✔ Check-In Successful!");
                        break;
                    }
                }

            } else if (choice == 2) {

                showBusyRooms(rooms, days); // show busy rooms first

                while (true) {
                    System.out.print("\nChoose floor (1-3): ");
                    int f = sc.nextInt() - 1;

                    System.out.print("Choose room number (1-5): ");
                    int r = sc.nextInt() - 1;

                    if (!rooms[f][r].equals("Busy")) {
                        System.out.println("❌ This room is NOT BUSY. Try again.");
                    } else {

                        int index = f * 5 + r;
                        int day = days[index];
                        int total = day * 20;

                        System.out.println("✔ Number of days: " + day);
                        System.out.println("💵 Total price: " + total + " $");

                        rooms[f][r] = "Available";
                        days[index] = 0; // reset

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
