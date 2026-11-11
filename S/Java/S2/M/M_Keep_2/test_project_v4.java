


package m_chou_yamata_online_sale_processing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class test_project_v4 {
    
    
    public static void main(String[] args) {
        
        try {
            
            Path create_file = Paths.get("data.txt");
        
            if ( Files.exists(create_file) ){
                System.out.println("File already exists.");
            } else {    
                Files.writeString( create_file, "Hello" );
                System.out.println("File created and written.");
            }
            
            String content = Files.readString( create_file );
            System.out.println(content );
            
        } catch ( IOException e ) {
            System.out.println("Error: " + e.getMessage() );
        }
        
        
    }
    
}
