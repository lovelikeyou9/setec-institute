
package m_chou_yamata_online_sale_processing;

import java.util.ArrayList;

public class Order {
    
    /*
    Customer customer;  // is from class Customer constructor
    Customer is class and not constructor   
    customer is field and not object and not from class Customer
    */
    
    int order_id;
    Customer order_customer;    // order_customer is variable
    Product order_product;
    int order_qty;
    boolean order_processed;
    
    public Order( int c_order_id, Customer c_order_customer, Product c_order_product, int c_order_qty ) {

        this.order_id = c_order_id;
        this.order_customer = c_order_customer;
        this.order_product = c_order_product;
        this.order_qty = c_order_qty;
        this.order_processed = false;
        
    }
    
    public void order_display () {
        
        System.out.println( 
                "Order ID: " + order_id +
                " | Customer: " + order_customer +
                " | Product: " + order_product +
                " | Order Qty: " + order_qty +
                " | Status: " + ( order_processed ? "Done" : "Pending" ) /*
                        if it is true is Done
                        if it is false is Pending
                        */
                        
        );

    }
    
    public static Order findOrder_id (ArrayList<Order> orders, int f_id ) {
        for ( Order o : orders ) {
            if ( o.order_id == f_id ) {
                return o;
            }
        }
        return null;
    }
    
    /*
    @Override
    public int toInt () {
        return order_id;
    }
    */
    
}
