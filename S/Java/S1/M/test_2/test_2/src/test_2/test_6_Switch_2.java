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
public class test_6_Switch_2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        
        Scanner sc = new Scanner(System.in);
        
        System.out.println("1. Day");
        System.out.println("2. Month");
        System.out.println("3. Score");
        System.out.println("4. Exit");
        System.out.print("Please choose: ");
        int choice = sc.nextInt();
        
        switch(choice)
        {
            case 1: //  Day
                System.out.print("Enter a day (m/t/w/h/f/s/n: ");
                String day = sc.next();

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
                    System.out.println();
                    System.out.println("Invalid input! Use first letter.");
                }
            break;
            
            case 2: // Month
                System.out.print("Enter a Month 1-12: ");
                int num=sc.nextInt();
                switch(num)
                {
                    case 1 -> System.out.println("January");
                    case 2 -> System.out.println("February");
                    case 3 -> System.out.println("March");
                    case 4 -> System.out.println("April"); 
                    case 5 -> System.out.println("May"); 
                    case 6 -> System.out.println("June");
                    case 7 -> System.out.println("July");
                    case 8 -> System.out.println("August");
                    case 9 -> System.out.println("September");
                    case 10 -> System.out.println("October");
                    case 11 -> System.out.println("November");
                    case 12 -> System.out.println("December");
            
                    default -> System.out.println("Invalid number");
                } 
            break;
            
            case 3: // Score
                System.out.print("Enter score (0-100): ");
                double s = sc.nextDouble();
                switch((int)(s/10))
                {
                    case 10, 9 -> System.out.println("A");
                    case 8 -> System.out.println("B");
                    case 7 -> System.out.println("C");
                    case 6 -> System.out.println("D");
                    case 5 -> System.out.println("E");
                    default -> System.out.println("F");
                }
            break;
            
            case 4: // Exit
                System.out.println("Exit Program");
                break;
            
            default:
                System.out.println("Invalid choice");
        }
        
        
    }
    
}
