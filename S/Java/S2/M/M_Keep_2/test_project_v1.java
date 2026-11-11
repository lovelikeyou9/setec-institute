
package m_chou_yamata_online_sale_processing;


import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.*;
import java.util.Scanner;


public class test_project_v1 {

    /**
     * @param args the command line arguments
     */
    
    
    
    public static void main(String[] args) {
        
        /*  1
        int[] numbers = { 1, 2, 3, 4, 5 };
        for(int n : numbers){
            System.out.println( n );
        }
        */
        
        /*  2
        String text = "Hello";
        System.out.println( text.length() );
        System.out.println(text.toUpperCase() );
        */
        
        /*  3
        class Car {

            String brand;

            Car(String brand) {
                this.brand = brand;
            }

            void drive() {
                System.out.println(brand + " is driving.");
            }
        }
        
        Car c = new Car("Toyota");
        // c.brand = "Handa";
        c.drive();
        */
        
        /*  3
        3. Conditional & Loops Advanced
        Nested if-else
        switch-case
        for-each loops
        while / do-while loops
        */
        
        /*  4
        try {
            int x = 10 / 0;
        } catch (ArithmeticException e) { // very common ( always handling errors )
            System.out.println("Cannot divide by zero!");
        } finally {     // sometimes ( cleanup only )
            System.out.println("Always runs");
        }
        */
        
        /*  5
        ArrayList<String> list = new ArrayList<>();
        list.add("Apple");
        list.add("Banana");
        System.out.println(list.get(0));
        System.out.println(list);
        */
        
        /*  6.1 Create file
        try {
            File file = new File("data.txt");
            
            if ( file.createNewFile() ) {
                System.out.println( "File created: " + file.getName() );
            } else {
                System.out.println( "File already exists." );
            }
            
        } catch ( Exception e ) {
            System.out.println( "Error: " + e.getMessage() );
        }
        */
        
        /*  6.2 Write into file if file exist.
            Also Create + Write file but it will not create/overwrite same 
            file already exist.

        try {
            FileWriter writer = new FileWriter( "data.txt" );
            
            writer.write("Hello world!\n");
            writer.write( "Java File Handling\n" );
            writer.close();
            
            System.out.println("Write successful");
            
        } catch ( Exception e ) {
            System.out.println( "Error: " + e.getMessage() );
        }
        */
        
        /*  6.3 Method 1: use BufferedReader
        try {
            BufferedReader br = new BufferedReader( new FileReader("data.txt") );
            String line;
            
            while ( ( line = br.readLine() ) != null ) {
                System.out.println(line);
            }
            
            br.close();
                    
        } catch ( Exception e) {
            System.out.println( "Error: " + e.getMessage() );
        }
        */
        
        /*  6.3 Method 2: Scanner
        try {
            File file = new File("data.txt");
            Scanner sc = new Scanner(file);
        
            while ( sc.hasNextLine() ) {
                System.out.println( sc.nextLine() );
            }
            
            sc.close();
            
        } catch ( Exception e ) {
            System.out.println( "Error: " + e.getMessage() );
        }
        */
        
  
    }
    
}
