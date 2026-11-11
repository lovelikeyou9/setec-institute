
package m_chou_yamata_online_sale_processing;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

        

public class M_Chou_Yamata_Online_Sale_Processing {

    static ArrayList<Product> products = new ArrayList<>();
    static ArrayList<Customer> customers = new ArrayList<>();
    static ArrayList<Order> orders = new ArrayList<>();
    
    static Scanner sc = new Scanner(System.in);
    
    static {
        products.add(new Product(1, "Coca", 20, 1.5));
        products.add(new Product(2, "Hotdog", 10, 2));
        products.add(new Product(3, "Sandwich", 5, 1));
    }
    
    
    
    public static void main(String[] args) {

        int choose;
        
        do {
            System.out.println("==== Market Online ====");
            System.out.println("1. View Product");
            System.out.println("2. Add Product");
            System.out.println("3. Register Customer");
            System.out.println("4. Create Order");
            System.out.println("5. View Order");
            System.out.println("6. Process Order");
            System.out.println("7. View Customer");
            System.out.println("8. Exit");
            System.out.print("Choose: ");
            choose = sc.nextInt();
            
            System.out.println("#------------------------------------#");
            switch ( choose ) {
                case 1: viewProducts(); break;
                case 2: addProduct(); break;
                case 3: registerCustomer(); break;
                case 4: createOrder(); break;
                case 5: viewOrders(); break;
                case 6: processOrders(); break;
                case 7: viewCustomer(); break;
            }
            
        }while ( choose != 8 );
 
    }
    
    
    //  1
    static void viewProducts(){
        
        System.out.println("--- View Product ---");
        
        if ( products.isEmpty() ){
            System.out.println("Product is empty.");
            System.out.println("--------------------------");
            return;
        }
        
        
        for ( Product p : products ) {
            p.product_display();
        }
        
        System.out.println("--------------------------");
    }
    
    //  2
    static void addProduct() {
        
        System.out.println("--- Add Product ---");
        
        System.out.print("ID    : ");
        int a_id = sc.nextInt();
        
        System.out.print("Name  : ");
        String a_name = sc.next();
        
        System.out.print("Qty   : ");
        int a_qty = sc.nextInt();
        
        System.out.print("Price : ");
        double a_price = sc.nextDouble();
        
        Product a_f_id = Product.findProduct_Id(products, a_id);
        
        if ( a_f_id == null ) {
            products.add( new Product(a_id, a_name, a_qty, a_price) );
            System.out.println("Product added.");
            System.out.println("--------------------------");
            return;
        } else {
            System.out.println("Product ( ID: " + a_id + " ) already exist.");
            System.out.println("--------------------------");
        }
        
        
        
    }
    
    
    //  3
    static void registerCustomer() {
    
        System.out.println("--- Register Custoemr ---");
        
        System.out.print("ID   : ");
        int r_id = sc.nextInt();
        
        sc.nextLine();
        
        System.out.print("Name : ");
        String r_name = sc.nextLine();
            
        System.out.print("Tel  : ");
        String r_tel = sc.nextLine();
        
        Customer r_f_ip = Customer.findCustomer_id(customers, r_id);
        
        if ( r_f_ip != null ) {
            System.out.println("Customer ( ID: " + r_f_ip.customer_id + " ) already exist.");
            System.out.println("--------------------------");
            return;
        }
        
        customers.add( new Customer( r_id, r_name, r_tel ) );
        System.out.println("Customer registered.");
        System.out.println("--------------------------");
        
    }
    
    
    //  4
    static void createOrder() {
    
        System.out.println("--- Create Order ---");
        
        if ( products.isEmpty() || customers.isEmpty() ) {
            System.out.println("Do not have product or customer.");
            System.out.println("--------------------------");
            return;
        } 
        
        
        
        System.out.print("Order ID: ");
        int co_id = sc.nextInt();
        
        for ( Customer c : customers ) c.customer_display();
        System.out.print("Select Customer: ");
        int co_c_id = sc.nextInt();
        
        for ( Product p : products ) p.product_display();
        System.out.print("Select Product: ");
        int co_p_id = sc.nextInt();
        
        System.out.print("Qty: ");
        int qty_CO = sc.nextInt();
        
        
        Order co_o_id = Order.findOrder_id(orders,  co_id);
        
        if ( co_o_id == null ) {
            
            Customer customer_CO = null;  
            Product product_CO = null;     

            for ( Customer c : customers ) {
                if ( c.customer_id == co_c_id ) {
                    customer_CO = c;
                }
            }

            for ( Product p : products ) {
                if ( p.product_id == co_p_id ) {
                    product_CO = p;
                }
            }

            if ( customer_CO != null && product_CO != null ) {
                orders.add(new Order(co_id, customer_CO, product_CO, qty_CO));
                System.out.println("Order created.");
            } else {
                System.out.println("Invalid selected.");
            }
            
        } else {
            System.out.println("Order ( ID: " + co_o_id.order_id + " ) already exist.");
        }
        
  
    }
    
    //  5
    static void viewOrders(){
        
        System.out.println("--- View Orders ---");
        
        if ( orders.isEmpty() ) {
            System.out.println("Order is empty.");
            System.out.println("--------------------------");
            return;
        }  
        
        for ( Order o : orders  ) {
            o.order_display();
        }
        
    }
    
    //  6
    static void processOrders() {
        
        System.out.println("--- Process Orders ---");
        
        if (orders.isEmpty()) {
            System.out.println("Order is empty.");
            System.out.println("--------------------------");
        }
        
        for ( Order o : orders ) {
            if(!o.order_processed) {
                if( o.order_product.product_qty >= o.order_qty ){
                    
                    o.order_processed = true;
                    System.out.print("Do you want file info product ( y / n ): ");
                    String create_or_not = sc.next();
                    
                    if ( "y".equalsIgnoreCase(create_or_not ) ) {
                        try {
                            System.out.print("Give name file: ");
                            String name_file = sc.next();
                            
                            Path create_file = Paths.get(name_file);
                            
                            double total_price = o.order_qty * o.order_product.product_price;
                            
                            String copy_info_order = "==============================\n" 
                                    + "\tMY STORE RECEIPT\n"
                                    + "==============================\n"
                                    + "Order ID:\t" + o.order_id + "\n"
                                    + "Customer:\t" + o.order_customer.customer_name + "\n"
                                    + "Status:\t\t" + (o.order_processed ? "Processed" : "Pending") + "\n"
                                    + "------------------------------\n"
                                    + "Product\tQty\tPrice\tTotal\n"
                                    + "------------------------------\n"
                                    + o.order_product.product_name + "\t" + o.order_qty + "\t"
                                    + o.order_product.product_price + "\t" + total_price + "\n"
                                    + "------------------------------\n"
                                    + "Grand Total:\t\t" + total_price + "$\n"
                                    + "==============================\n"
                                    + "THANK YOU FOR YOUR PURCHASE!\n"
                                    + "==============================";
                            
                            if ( Files.exists(create_file)) {
                                System.out.println("File already exist.");
                            } else {
                                Files.writeString(create_file, copy_info_order);
                            }
                                                        
                        } catch ( Exception e ) {
                            System.out.println("Error " + e.getMessage());
                        }
                    }
                    
                    o.order_product.product_qty -= o.order_qty;
                    
                    
                    System.out.println("Processed Order ID: " + o.order_id + "successfully.");
                    
                    if( o.order_product.product_qty == 0 ){
                        Product.deleteProduct_id(products, o.order_product.product_id);
                        System.out.println("Product ( ID: " + o.order_product.product_id + " ) is Empty.");
                    }
                    
                    System.out.println("--------------------------");
                    
                } else {
                    System.out.println("Not enough stock for Order: "  + o.order_id );
                    
                }
            }
            
        }

        
    }
    
    //  7
    static void viewCustomer (){
        
        System.out.println("--- View Custoemr ---");
        
        if ( customers.isEmpty() ){
            System.out.println("Customer is empty.");
            System.out.println("--------------------------");
            return;
        }
        
        for ( Customer c : customers ) {
            c.customer_display();
        }
        
        /*
        System.out.println("--- View Custoemr ---");
                
        for (int i = 0; i < customers.size(); i++) {
            customers.get(i).customer_display();
        }
        */
        System.out.println("--------------------------");
        
        
    }
    

}
