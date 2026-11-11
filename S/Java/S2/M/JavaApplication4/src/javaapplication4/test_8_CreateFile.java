package javaapplication4;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class test_8_CreateFile {

    public static void main(String[] args) {

        try {
            
            Scanner sc = new Scanner(System.in);
            
            System.out.print("Enter your file & type name: ");  // user_input.txt
            String nameFile = sc.nextLine();
            
            File file = new File(nameFile );
            
            if ( file.createNewFile() ) {
                System.out.println( "File created: " +   file.getName() );
            } else {
                System.out.println("File already exists.");
            }
            
        } catch ( IOException e ) {
            System.out.println( "Error ocurred" );
        }
        
        
    }
    
}
