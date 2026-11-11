package test_2;

import java.util.Scanner;

public class test_18_FullTime extends test_18_Staff {
    
    
    private double salary;
    private double ot;
    private double wage;

    public void input(Scanner sc) {
        inputStaff(sc);

        System.out.print("Enter Salary: ");
        salary = sc.nextDouble();

        System.out.print("Enter OT: ");
        ot = sc.nextDouble();

        System.out.print("Enter Wage: ");
        wage = sc.nextDouble();
    }

    public void display() {
        displayStaff();
        System.out.println("Salary: " + salary);
        System.out.println("OT: " + ot);
        System.out.println("Wage: " + wage);
        System.out.println("Total Money: " + (salary + ot * wage));
        System.out.println("--------------------");
    }
    
    
    
}
