
package m_chou_yamata_online_sale_processing;



import java.util.ArrayList;


/*  10
// Array
class Person {
    int id;
    String name;
    String gender;
    
    Person ( int id, String name, String gender) {
        this.id = id;
        this.name = name;
        this.gender = gender;
    }
}
*/


public class test_project_v3 {

    // ArrayList<Person> people = new ArrayList<>();  // ArrayList<String> list = new ArrayList<>();
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        /*  1   Declaring an Array
        int [] numbers;
        // or
        int numbers[];
        */
        
        /*  2   Creating an Array
        int [] numbers;
        numbers = new int[5];   // Creating an Array
        System.out.println(numbers[0]);
        */
        
        /*  3 Declaring + Creating Together
        int[] numbers = new int[5];
        System.out.println(numbers[0]);
        */
        
        /*  4   Initializing an Array
        int [] numbers;
        numbers = new int[]{10, 20, 30, 40, 50};
        System.out.println(numbers[4]);     // Accessing Elements
        */
        
        /*  5
        int [] numbers = {10, 20, 30, 40, 50};
        System.out.println(numbers[0]);
        */
        
        /*  6   Modifying Elements
        int [] numbers = {10, 20, 30, 40, 50};
        System.out.println(numbers[1]);
        numbers[1] = 25;    //
        System.out.println(numbers[1]);
        */
        
        /*  7   Assign one by one
        int[] numbers = new int[5];
        numbers[0] = 10;
        numbers[1] = 20;
        numbers[2] = 30;
        numbers[3] = 40;
        numbers[4] = 50;

        System.out.println(numbers[4]);
        */
        
        /*  8.1
        int [] numbers = {10, 20, 30, 40, 50};
        System.out.println(numbers.length);
        for ( int i = 0; i < numbers.length; i++ ) {
            System.out.println(numbers[i]);
        }
        */
        
        /*  8.2
        int [] numbers = {10, 20, 30, 40, 50};
        for (int num : numbers) {
            System.out.println(num);
        }
        */
        
        /*  8.3
        int [] numbers = {10, 20, 30, 40, 50};
        System.out.println(Arrays.toString(numbers));
        */
        
        /*  9.1     Multi-dimensional ( 2D )
        int [][] matrix = {
            {1, 2},
            {3, 4}
        };
        System.out.println(matrix[0][0]);   // 1
        System.out.println(matrix[1][1]);   // 4
        */
        
        /*  9.2
        int [][] matrix = {
            {1, 2},
            {3, 4}
        };
        for ( int i = 0; i < matrix.length; i++ ) {
            System.out.print(Arrays.toString(matrix[i]));
        }
        System.out.println();
        */
        
        /*  9.3
        int [][] matrix = {
            {1, 2},
            {3, 4}
        };
        for (int i = 0; i < matrix.length; i++ ){
            // System.out.print(i + "iout ");
            for ( int j = 0; j < matrix[i].length; j++ ) {
                // System.out.print(i + "i " + j + "j ");
                System.out.print( matrix[i][j] + " " );
            }
            System.out.println();
        }
        */
        
        /*  9.4
        int [][] matrix = {
            {1, 2},
            {3, 4}
        };
        for ( int[] row : matrix ) {
            // System.out.print(row[0] + "row ");
            for ( int num : row ) {
                // System.out.print(row[0] + "rowX ");
                System.out.print( num + " ");
            }
            System.out.println();
        }
        */
        
        /*  ^10     
        //  Array
        Person[] people = {
            new Person( 1, "John", "Male"),
            new Person( 2, "Anna", "Female"),
            new Person( 3, "Mike", "Male")
        };
        
        for ( Person p : people ) {
            System.out.println( p.id + " " + p.name + " " + p.gender );
        }
        System.out.println("---------------------");
        for ( int i = 0; i < people.length; i++ ) {
            System.out.println( people[i].id + " " + people[i].name + " " + people[i].gender );
        }
        */
        
        
        
        
    }
    
}
