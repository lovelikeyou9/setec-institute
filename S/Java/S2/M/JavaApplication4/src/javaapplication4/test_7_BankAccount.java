/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package javaapplication4;

import java.util.ArrayList;
/**
 *
 * @author kimkhun
 */

public class test_7_BankAccount {
    
    private int accountNumber;
    private double balance;
    private ArrayList<String> transactions = new ArrayList<>();

    public test_7_BankAccount(int accountNumber, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public synchronized void deposit(double amount, String customer) {
        
        balance += amount;

        String log = customer + " deposited $" + amount + " | Balance: $" + balance;
        transactions.add(log);
        System.out.println(log);

        notify(); // notify waiting withdraw threads
        
    }

    public synchronized void withdraw(double amount, String customer) {

        while (balance < amount) {
            try {
                System.out.println(customer + " wants to withdraw $" + amount + " but balance is $" + balance + " -> Waiting...");
                wait(); // wait for deposit
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        balance -= amount;

        String log = customer + " withdrew $" + amount + " | Balance: $" + balance;
        transactions.add(log);
        System.out.println(log);
    }

    public synchronized double getBalance(String customer) {

        String log = customer + " checked balance: $" + balance;
        transactions.add(log);

        System.out.println(log);
        
        return balance;
        
    }

    public void showTransactions() {
        
        System.out.println("\nTransaction History:");
        
        for (String t : transactions) {
            System.out.println(t);
        }
        
    }
    
    
    
}
