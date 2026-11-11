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
public class test_4_Setec_Salary {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter base salary: ");
        double baseSalary = sc.nextDouble();

        System.out.print("Enter years of service: ");
        int years = sc.nextInt();

        double monthlyPension = 0;
        double lumpSum = 0;

        if (years < 20) {
            double lumpMonths = 4 + (years * 6.0 / 19);
            lumpSum = baseSalary * lumpMonths;
            monthlyPension = 0;
        } else if (years == 20) {
            lumpSum = baseSalary * 8;
            monthlyPension = baseSalary * 0.60;
        } else if (years >= 21 && years <= 29) {
            lumpSum = baseSalary * 8;
            double percent = 0.60 + (years - 20) * 0.02;
            monthlyPension = baseSalary * percent;
        } else { // years >= 30
            lumpSum = baseSalary * 8;
            monthlyPension = baseSalary * 0.80;
        }

        System.out.println("--------------------------------");
        System.out.println("Years of service : " + years);
        System.out.println("Base salary : " + baseSalary);
        System.out.println("Monthly pension : " + monthlyPension);
        System.out.println("Lump-sum : " + lumpSum);
        
    }
    
}
