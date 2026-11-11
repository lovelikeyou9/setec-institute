/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package javaapplication4;

/**
 *
 * @author kimkhun
 */
public class test_7_BankSystem {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        
        test_7_BankAccount account = new test_7_BankAccount(1001, 500);

        test_7_CustomerThread c1 = new test_7_CustomerThread(account, "Customer-1");
        test_7_CustomerThread c2 = new test_7_CustomerThread(account, "Customer-2");
        test_7_CustomerThread c3 = new test_7_CustomerThread(account, "Customer-3");
        test_7_CustomerThread c4 = new test_7_CustomerThread(account, "Customer-4");
        test_7_CustomerThread c5 = new test_7_CustomerThread(account, "Customer-5");

        c1.setPriority(Thread.MAX_PRIORITY);
        c5.setPriority(Thread.MIN_PRIORITY);

        c1.start();
        c2.start();
        c3.start();
        c4.start();
        c5.start();
        
    }
    
    
}
