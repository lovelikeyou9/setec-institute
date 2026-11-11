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
public class test_5_Switch {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        
        
        /*  1
        
        int num;
        Scanner input = new Scanner(System.in);
        System.out.print("num= ");
        num=input.nextInt();
        switch(num)
        {
            case 1: System.out.println("Number one"); break;
            case 2: System.out.println("Number two"); break;
            case 3: System.out.println("Number three");
                    System.out.println("Thank you");
                    break;
            case 4: System.out.println("Number four"); break;
            default: System.out.println("Invalid number");
        }

        */
        
        
        /*  2
        
        int num;
        Scanner input = new Scanner(System.in);
        System.out.print("num= ");
        num=input.nextInt();
        switch(num)
        {
            case 1: System.out.println("Number one");
            case 2: System.out.println("Number two"); 
            case 3: System.out.println("Number three");
                    System.out.println("Thank you");
                    break;
            case 4: System.out.println("Number four");
            default: System.out.println("Invalid number");
        }
        
        */
        
        
        /*  1 test
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter a day (m/t/w... or full name like Monday): ");
        String day = sc.nextLine();

        switch (day) {
            case "m":
            case "M":
            case "Monday":
            case "monday":
                System.out.println("Monday");
                break;

            case "t":
            case "T":
            case "Tuesday":
            case "tuesday":
                System.out.println("Tuesday");
                break;

            case "w":
            case "W":
            case "Wednesday":
            case "wednesday":
                System.out.println("Wednesday");
                break;

            case "th":
            case "TH":
            case "Thursday":
            case "thursday":
                System.out.println("Thursday");
                break;

            case "f":
            case "F":
            case "Friday":
            case "friday":
                System.out.println("Friday");
                break;

            case "s":
            case "S":
            case "Saturday":
            case "saturday":
                System.out.println("Saturday");
                break;

            case "su":
            case "SU":
            case "Sunday":
            case "sunday":
                System.out.println("Sunday");
                break;

            default:
                System.out.println("Invalid input! Use first letter or full day name.");
        }
        
        */
        
        
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter a day (m/t/w/h/f/s/n: ");
        String day = sc.nextLine();

        switch (day) {
            case "m":
            case "M":
                System.out.println("Monday");
                break;

            case "t":
            case "T":
                System.out.println("Tuesday");
                break;

            case "w":
            case "W":
                System.out.println("Wednesday");
                break;

            case "h":
            case "H":
                System.out.println("Thursday");
                break;

            case "f":
            case "F":
                System.out.println("Friday");
                break;

            case "s":
            case "S":
                System.out.println("Saturday");
                break;

            case "n":
            case "N":
                System.out.println("Sunday");
                break;

            default:
                System.out.println("Invalid input! Use first letter.");
        }
        

    }
    
}
