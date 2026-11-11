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
public class Test_2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        /*  1
        System.out.print("Hello, ");
        System.out.println("World!");
        int a;
        Scanner input = new Scanner(System.in);
        System.out.print("a=");
        a=input.nextInt();
        a=a+5;
        System.out.println("Value of a is " + a);
        float g=1.5f;
        char text='g';
        String home;
        boolean u;
        Boolean i;
        */ 
        
        /*  2
        int x = 5;
        double y = x;
        System.out.println("y= " + y);
        int z = (int)y;
        System.out.println("z= " + z);
        */

        
        Scanner sc = new Scanner(System.in);


        System.out.print("ID: ");
        int id = sc.nextInt();
        System.out.print("Name: ");
        String name = sc.next();
        System.out.print("Width: ");
        double width = sc.nextDouble();
        System.out.print("Height: ");
        double height = sc.nextDouble();
        System.out.print("Transfer (true/false): ");
        boolean transfer = sc.nextBoolean();
        System.out.print("Shipping fee: ");
        double shippingFee = sc.nextDouble();
        System.out.print("Quantity: ");
        int qty = sc.nextInt();
        System.out.print("Amount: ");
        double amountShow = sc.nextDouble();
        System.out.print("Money: ");
        double money = sc.nextDouble();


        double amount = shippingFee * qty;
        double change = money - amount;
        int changeDollars = (int) change;
        int changeRiels = (int) Math.round((change - changeDollars) * 4000);


        System.out.println("\nID\tName\tWidth\tHeight\tTransfer\tAmount\tMoney\tChange");
        System.out.println(id + "\t" + name + "\t" + width + "m\t" + height + "m\t" + transfer
                + "\t\t" + amountShow + "$\t" + money + "$\t" + String.format("%.1f", change) + "$");

        System.out.println("Change Dollars: " + changeDollars + "$");
        System.out.println("Change Riels: " + changeRiels + " riels");


        
        
    }
    
}
