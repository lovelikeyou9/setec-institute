/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

package test_2;

import java.util.Scanner;
import java.util.Arrays;

/**
 *
 * @author Admin
 */
public class test_14_Array_1_Dimension {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        
        Scanner sc = new Scanner(System.in);

        final int MAX = 100;
        int[] arr = new int[MAX];
        int n = 0;

        int choice;

        do {
            System.out.println("========== MENU ==========");
            System.out.println("1. Add");
            System.out.println("2. Insert");
            System.out.println("3. Delete");
            System.out.println("4. Upload");
            System.out.println("5. Display");
            System.out.println("   a. Original list");
            System.out.println("   b. Sort list");
            System.out.println("6. Sort");
            System.out.println("   a. Asc");
            System.out.println("   b. Desc");
            System.out.println("7. Search");
            System.out.println("0. Exit");
            System.out.println("==========================");

            System.out.print("Please Choose: ");
            choice = sc.nextInt();

            // =====================================================
            // 1. ADD
            // =====================================================
            if (choice == 1) {
                if (n >= MAX) {
                    System.out.println("Array is full!\n");
                } else {
                    System.out.print("Enter value to add: ");
                    arr[n] = sc.nextInt();
                    n++;
                    System.out.println("Added.\n");
                }
            }

            // =====================================================
            // 2. INSERT
            // =====================================================
            else if (choice == 2) {
                if (n >= MAX) {
                    System.out.println("Array is full!\n");
                } else {
                    System.out.print("Enter position (0 to " + n + "): ");
                    int pos = sc.nextInt();

                    if (pos < 0 || pos > n) {
                        System.out.println("Invalid position!\n");
                    } else {
                        System.out.print("Enter value: ");
                        int val = sc.nextInt();

                        for (int i = n; i > pos; i--) {
                            arr[i] = arr[i - 1];
                        }
                        arr[pos] = val;
                        n++;

                        System.out.println("Inserted.\n");
                    }
                }
            }

            // =====================================================
            // 3. DELETE
            // =====================================================
            else if (choice == 3) {
                System.out.print("Enter value to delete: ");
                int del = sc.nextInt();
                int index = -1;

                for (int i = 0; i < n; i++) {
                    if (arr[i] == del) {
                        index = i;
                        break;
                    }
                }

                if (index == -1) {
                    System.out.println("Value not found!\n");
                } else {
                    for (int i = index; i < n - 1; i++) {
                        arr[i] = arr[i + 1];
                    }
                    n--;
                    System.out.println("Deleted.\n");
                }
            }

            // =====================================================
            // 4. UPLOAD
            // =====================================================
            else if (choice == 4) {
                System.out.print("How many numbers to upload? ");
                int count = sc.nextInt();

                if (count > MAX) {
                    System.out.println("Too many!\n");
                } else {
                    System.out.println("Enter " + count + " numbers:");
                    for (int i = 0; i < count; i++) {
                        arr[i] = sc.nextInt();
                    }
                    n = count;

                    System.out.println("Uploaded.\n");
                }
            }

            // =====================================================
            // 5. DISPLAY
            // =====================================================
            else if (choice == 5) {
                System.out.print("Choose (a = original, b = sorted): ");
                char opt = sc.next().toLowerCase().charAt(0);

                if (opt == 'a') {
                    System.out.print("Original List: ");
                    for (int i = 0; i < n; i++) System.out.print(arr[i] + " ");
                    System.out.println("\n");
                }

                else if (opt == 'b') {
                    int[] temp = Arrays.copyOf(arr, n);
                    Arrays.sort(temp);

                    System.out.print("Sorted List: ");
                    for (int x : temp) System.out.print(x + " ");
                    System.out.println("\n");
                }

                else {
                    System.out.println("Invalid option!\n");
                }
            }

            // =====================================================
            // 6. SORT
            // =====================================================
            else if (choice == 6) {
                System.out.print("Choose (a = Asc / b = Desc): ");
                char opt = sc.next().toLowerCase().charAt(0);

                if (opt == 'a') {
                    Arrays.sort(arr, 0, n);
                    System.out.println("Sorted Asc.\n");
                }

                else if (opt == 'b') {
                    Arrays.sort(arr, 0, n);

                    for (int i = 0; i < n / 2; i++) {
                        int temp = arr[i];
                        arr[i] = arr[n - 1 - i];
                        arr[n - 1 - i] = temp;
                    }

                    System.out.println("Sorted Desc.\n");
                }

                else {
                    System.out.println("Invalid option!\n");
                }
            }

            // =====================================================
            // 7. SEARCH
            // =====================================================
            else if (choice == 7) {
                System.out.print("Enter value to search: ");
                int val = sc.nextInt();
                int index = -1;

                for (int i = 0; i < n; i++) {
                    if (arr[i] == val) {
                        index = i;
                        break;
                    }
                }

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
