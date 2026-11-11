package javaapplication4;

import java.util.ArrayList;
import java.util.Scanner;

public class test_1_Main {

    public static void main(String[] args) {
        
        
        
        Scanner sc = new Scanner(System.in);

        ArrayList<test_1_FullTime> fullList = new ArrayList<>();
        ArrayList<test_1_PartTime> partList = new ArrayList<>();

        int choice;

        do {
            System.out.println("\n1. Input Staff");
            System.out.println("2. Display");
            System.out.println("0. Exit");
            System.out.print("Choose: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("a. Full time");
                    System.out.println("b. Part time");
                    System.out.print("Choose: ");
                    char type = sc.next().charAt(0);

                    if (type == 'a') {
                        test_1_FullTime ft = new test_1_FullTime();
                        ft.input(sc);
                        fullList.add(ft);
                    } else if (type == 'b') {
                        test_1_PartTime pt = new test_1_PartTime();
                        pt.input(sc);
                        partList.add(pt);
                    }
                    break;

                case 2:
                    System.out.println("a. All");
                    System.out.println("b. Full time");
                    System.out.println("c. Part time");
                    System.out.print("Choose: ");
                    char show = sc.next().charAt(0);

                    if (show == 'a') {
                        for (test_1_FullTime ft : fullList) ft.display();
                        for (test_1_PartTime pt : partList) pt.display();
                    } 
                    else if (show == 'b') {
                        for (test_1_FullTime ft : fullList) ft.display();
                    } 
                    else if (show == 'c') {
                        for (test_1_PartTime pt : partList) pt.display();
                    }
                    break;
            }
        } while (choice != 0);
        
        
        
    }
}
