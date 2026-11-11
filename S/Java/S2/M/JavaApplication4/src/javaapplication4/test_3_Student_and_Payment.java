/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package javaapplication4;

/**
 *
 * @author Admin
 */
import java.util.ArrayList;
import java.util.Scanner;

class Student {

    private String name;
    private static int totalStudents = 0;   // static variable
    private final int accountNumber;        // final variable
    private double balance;

    // Constructor
    public Student(String name, double initialBalance) {
        this.name = name;
        totalStudents++;
        this.accountNumber = totalStudents; // auto account number
        this.balance = initialBalance;
    }

    // Static method
    public static int getTotalStudents() {
        return totalStudents;
    }

    // Deposit
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposit successful.");
        } else {
            System.out.println("Invalid amount.");
        }
    }

    // Pay method
    public void pay(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("Payment successful.");
        } else {
            System.out.println("Insufficient balance or invalid amount.");
        }
    }

    // Final method (cannot be overridden)
    public final void displayAccountInfo() {
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Name: " + name);
        System.out.println("Balance: $" + balance);
    }

    public int getAccountNumber() {
        return accountNumber;
    }
}

public class test_3_Student_and_Payment {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        ArrayList<Student> students = new ArrayList<>();

        while (true) {
            System.out.println("\n===== MENU =====");
            System.out.println("1. Register Student");
            System.out.println("2. Pay");
            System.out.println("3. Display Total Students");
            System.out.println("4. Display Student Info");
            System.out.println("5. Exit");
            System.out.print("Choose: ");

            int choice = sc.nextInt();

            switch (choice) {

                case 1:
                    sc.nextLine(); // clear buffer
                    System.out.print("Enter name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter initial balance: ");
                    double balance = sc.nextDouble();

                    Student newStudent = new Student(name, balance);
                    students.add(newStudent);

                    System.out.println("Student registered successfully!");
                    break;

                case 2:
                    System.out.print("Enter account number: ");
                    int acc = sc.nextInt();

                    System.out.print("Enter amount to pay: ");
                    double payAmount = sc.nextDouble();

                    for (Student s : students) {
                        if (s.getAccountNumber() == acc) {
                            s.pay(payAmount);
                        }
                    }
                    break;

                case 3:
                    System.out.println("Total Students: " + Student.getTotalStudents());
                    break;

                case 4:
                    System.out.print("Enter account number: ");
                    int accInfo = sc.nextInt();

                    for (Student s : students) {
                        if (s.getAccountNumber() == accInfo) {
                            s.displayAccountInfo();
                        }
                    }
                    break;

                case 5:
                    System.out.println("Goodbye!");
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}