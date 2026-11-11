
package m_chou_yamata_online_sale_processing;

import java.util.ArrayList;

public class Customer {

    int customer_id;
    String customer_name;
    String customer_tel;
    
    public Customer ( int c_customer_id, String c_customer_name, String c_customer_tel ) {
        
        this.customer_id = c_customer_id;
        this.customer_name = c_customer_name;
        this.customer_tel = c_customer_tel;
        
    }
    
    
    public void customer_display () {
        System.out.println( customer_id + " | " + customer_name + " | " + customer_tel );
    }
    
    public static Customer findCustomer_id ( ArrayList<Customer> customers, int f_id ) {
        for ( Customer c : customers ) {
            if( c.customer_id == f_id ) {
                return c;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return customer_name;
    }
    
    
    
}
