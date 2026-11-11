package javaapplication4;

import java.io.File;
import java.util.Scanner;

public class test_8_ReadFile {

    public static void main(String[] args) {

        try {
            Scanner sc_1 = new Scanner(System.in);
            
            System.out.print("Enter file & type name: ");
            String nameFile = sc_1.nextLine();
           
            File file = new File(nameFile);
            
            Scanner sc = new Scanner(file);
            
            while ( sc.hasNextLine() ) {
                String data = sc.nextLine();
                System.out.println(data);
            }
            
            sc.close();
            
            
        } catch ( Exception e ) {
            System.out.println("Error reading file.");
        }
        
    }
    
}
