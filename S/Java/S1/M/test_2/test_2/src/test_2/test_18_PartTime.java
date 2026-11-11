package test_2;

import java.util.Scanner;

public class test_18_PartTime extends test_18_Staff {
    
    
    private int hour;
    private double wage;

    public void input(Scanner sc) {
        inputStaff(sc);

        System.out.print("Enter Hour: ");
        hour = sc.nextInt();

        System.out.print("Enter Wage: ");
        wage = sc.nextDouble();
    }

    public void display() {
        displayStaff();
        System.out.println("Hour: " + hour);
        System.out.println("Wage: " + wage);
        System.out.println("Total Money: " + (hour * wage));
        System.out.println("--------------------");
    }
    
    
    
}
