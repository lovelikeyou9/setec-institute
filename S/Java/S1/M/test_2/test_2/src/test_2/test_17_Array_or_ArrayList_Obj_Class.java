package test_2;

import java.util.ArrayList;
import java.util.Scanner;

public class test_17_Array_or_ArrayList_Obj_Class {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        ArrayList<test_17_Student> list = new ArrayList<>();

        int schoolFee = 790; // fixed

        System.out.print("Enter number of students: ");
        int n = sc.nextInt();

        for (int i = 0; i < n; i++) {
            System.out.println("\n--- Enter Student " + (i + 1) + " ---");

            System.out.print("ID: ");
            int id = sc.nextInt();
            sc.nextLine();

            System.out.print("Name: ");
            String name = sc.nextLine();

            System.out.print("Sex: ");
            String sex = sc.nextLine();

            System.out.print("SA Score: ");
            int SA = sc.nextInt();

            System.out.print("Java Score: ");
            int Java = sc.nextInt();

            System.out.print("Oracle Score: ");
            int Orac = sc.nextInt();

            list.add(new test_17_Student(id, name, sex, SA, Java, Orac));
        }

        // 1. Show ALL students
        System.out.println("\n========== ALL STUDENT DATA ==========");
        for (test_17_Student s : list) {
            s.displayStudentData(schoolFee);
        }

        // 2. Show ONLY ReExam students
        System.out.println("\n========== STUDENTS WHO HAVE RE-EXAM ==========");
        boolean found = false;

        for (test_17_Student s : list) {
            if (s.hasReExam()) {
                found = true;
                s.displayReExam();
            }
        }

        if (!found) {
            System.out.println("No student needs re-exam.");
        }
        
        
    }
}
