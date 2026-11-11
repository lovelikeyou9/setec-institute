/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package test_2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class test_16_Array_List {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        ArrayList<Integer> list = new ArrayList<>();
        int choice;

        do {
            System.out.println("========== MENU ==========");

            if (list.isEmpty()) {
                System.out.println("1. Add");
                System.out.println("0. Exit");
            } else {
                System.out.println("1. Add");
                System.out.println("2. Insert");
                System.out.println("3. Delete");
                System.out.println("4. Update");
                System.out.println("5. Display (a=Original, b=Sorted)");
                System.out.println("6. Sort (a=Asc, b=Desc)");
                System.out.println("7. Search");
                System.out.println("0. Exit");
            }

            System.out.println("==========================");
            System.out.print("Please Choose: ");
            choice = sc.nextInt();

            if (list.isEmpty() && choice != 1 && choice != 0) {
                System.out.println("Please add at least 1 value first!\n");
                continue;
            }

            // 1. ADD
            if (choice == 1) {
                System.out.print("Enter value to add: ");
                int value = sc.nextInt();

                if (list.contains(value)) {
                    System.out.println("Value already exists!\n");
                } else {
                    list.add(value);
                    System.out.println("Added.\n");
                }
            }

            // 2. INSERT
            else if (choice == 2) {
                System.out.print("Enter position (0 to " + list.size() + "): ");
                int pos = sc.nextInt();

                if (pos < 0 || pos > list.size()) {
                    System.out.println("Invalid position!\n");
                } else {
                    System.out.print("Enter value: ");
                    int val = sc.nextInt();

                    if (list.contains(val)) {
                        System.out.println("Value already exists!\n");
                    } else {
                        list.add(pos, val);
                        System.out.println("Inserted.\n");
                    }
                }
            }

            // 3. DELETE
            else if (choice == 3) {
                System.out.print("Enter value to delete: ");
                int del = sc.nextInt();

                if (list.remove(Integer.valueOf(del))) {
                    System.out.println("Deleted.\n");
                } else {
                    System.out.println("Value not found!\n");
                }
            }

            // 4. UPDATE
            else if (choice == 4) {
                System.out.print("How many numbers to update? ");
                int count = sc.nextInt();

                ArrayList<Integer> temp = new ArrayList<>();

                boolean duplicate = false;
                System.out.println("Enter " + count + " numbers:");

                for (int i = 0; i < count; i++) {
                    int x = sc.nextInt();

                    if (temp.contains(x)) {
                        duplicate = true;
                    }

                    temp.add(x);
                }

                if (duplicate) {
                    System.out.println("Duplicate values found! Update cancelled.\n");
                } else {
                    list.clear();
                    list.addAll(temp);
                    System.out.println("Updated.\n");
                }
            }

            // 5. DISPLAY
            else if (choice == 5) {
                System.out.print("Choose (a=Original, b=Sorted): ");
                char opt = sc.next().toLowerCase().charAt(0);

                if (opt == 'a') {
                    System.out.println("Original List: " + list + "\n");
                } else if (opt == 'b') {
                    ArrayList<Integer> sorted = new ArrayList<>(list);
                    Collections.sort(sorted);
                    System.out.println("Sorted List: " + sorted + "\n");
                } else {
                    System.out.println("Invalid option!\n");
                }
            }

            // 6. SORT
            else if (choice == 6) {
                System.out.print("Choose (a=Asc / b=Desc): ");
                char opt = sc.next().toLowerCase().charAt(0);

                if (opt == 'a') {
                    Collections.sort(list);
                    System.out.println("Sorted Asc.\n");
                } else if (opt == 'b') {
                    Collections.sort(list, Collections.reverseOrder());
                    System.out.println("Sorted Desc.\n");
                } else {
                    System.out.println("Invalid option!\n");
                }
            }

            // 7. SEARCH
            else if (choice == 7) {
                System.out.print("Enter value to search: ");
                int val = sc.nextInt();

                int index = list.indexOf(val);

                if (index == -1)
                    System.out.println("Not found.\n");
                else
                    System.out.println("Found at index: " + index + "\n");
            }

            else if (choice != 0) {
                System.out.println("Invalid choice!\n");
            }

        } while (choice != 0);

        sc.close();
    }
}
