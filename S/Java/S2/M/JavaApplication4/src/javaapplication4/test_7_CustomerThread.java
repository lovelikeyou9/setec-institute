/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication4;

/**
 *
 * @author kimkhun
 */
import java.util.Random;

public class test_7_CustomerThread extends Thread {

    private test_7_BankAccount account;
    private Random random = new Random();

    public test_7_CustomerThread(test_7_BankAccount account, String name) {
        super(name);
        this.account = account;
    }

    public void run() {

        for (int i = 1; i <= 10; i++) {
            int action = random.nextInt(3);
            double amount = (random.nextInt(5) + 1) * 100;
            
            switch (action) {
                case 0:
                    account.deposit(amount, getName());
                    break;
                case 1:
                    account.withdraw(amount, getName());
                    break;
                default:
                    account.getBalance(getName());
                    break;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            
        }
        
        
        
    }
}
