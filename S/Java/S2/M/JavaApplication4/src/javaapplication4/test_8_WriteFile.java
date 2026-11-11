package javaapplication4;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class test_8_WriteFile {
    
    
    
    public static void main(String[] args) {
        
        try {
            
            Scanner sc = new Scanner(System.in);
            
            System.out.print("Enter your file & type name: ");
            String nameFile = sc.nextLine();
            
            FileWriter writer = new FileWriter(nameFile);
            
            writer.write("Name: Hai\n");
            writer.write("Score: 95");
            writer.close();
            
            System.out.println("Data written successfully.");
            
        } catch (IOException e) {
            System.out.println("Error writing file.");
        }
        
    }
    
}
