package test_2;

import java.util.Arrays;
import java.util.Scanner;

public class test_15_Array_1_Dimension {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        final int MAX = 100;
        int[] arr = new int[MAX];
        int n = 0;

        int choice;

        do {

            System.out.println("========== MENU ==========");

            // Show only ADD when array is empty
            if (n == 0) {
                System.out.println("1. Add");
                System.out.println("0. Exit");
            } else {
                System.out.println("1. Add");
                System.out.println("2. Insert");
                System.out.println("3. Delete");
                System.out.println("4. Update");
                System.out.println("5. Display");
                System.out.println("   a. Original list");
                System.out.println("   b. Sorted list");
                System.out.println("6. Sort");
                System.out.println("   a. Asc");
                System.out.println("   b. Desc");
                System.out.println("7. Search");
                System.out.println("0. Exit");
            }

            System.out.println("==========================");
            System.out.print("Please Choose: ");
            choice = sc.nextInt();

            // When empty, ONLY allow Add or Exit
            if (n == 0 && choice != 1 && choice != 0) {
                System.out.println("Please add at least 1 value first!\n");
                continue;
            }

            // =====================================================
            // 1. ADD
            // =====================================================
            if (choice == 1) {
                if (n >= MAX) {
                    System.out.println("Array is full!\n");
                } else {
                    System.out.print("Enter value to add: ");
                    int value = sc.nextInt();

                    boolean exists = false;
                    for (int i = 0; i < n; i++) {
                        if (arr[i] == value) {
                            exists = true;
                            break;
                        }
                    }

                    if (exists) {
                        System.out.println("Value already exists!\n");
                    } else {
                        arr[n] = value;
                        n++;
                        System.out.println("Added.\n");
                    }
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

                        boolean exists = false;
                        for (int i = 0; i < n; i++) {
                            if (arr[i] == val) {
                                exists = true;
                                break;
                            }
                        }

                        if (exists) {
                            System.out.println("Value already exists!\n");
                        } else {
                            for (int i = n; i > pos; i--) {
                                arr[i] = arr[i - 1];
                            }
                            arr[pos] = val;
                            n++;
                            System.out.println("Inserted.\n");
                        }
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
            // 4. UPDATE
            // =====================================================
            else if (choice == 4) {
                System.out.print("How many numbers to update? ");
                int count = sc.nextInt();

                if (count > MAX) {
                    System.out.println("Too many!\n");
                } else {
                    boolean duplicate = false;
                    int[] temp = new int[count];

                    System.out.println("Enter " + count + " numbers:");
                    for (int i = 0; i < count; i++) {
                        temp[i] = sc.nextInt();

                        for (int j = 0; j < i; j++) {
                            if (temp[j] == temp[i]) {
                                duplicate = true;
                            }
                        }
                    }

                    if (duplicate) {
                        System.out.println("Duplicate values found! Update cancelled.\n");
                    } else {
                        for (int i = 0; i < count; i++) {
                            arr[i] = temp[i];
                        }
                        n = count;
                        System.out.println("Updated.\n");
                    }
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
                } else if (opt == 'b') {
                    int[] temp = Arrays.copyOf(arr, n);
                    Arrays.sort(temp);

                    System.out.print("Sorted List: ");
                    for (int x : temp) System.out.print(x + " ");
                    System.out.println("\n");
                } else {
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
