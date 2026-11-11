
package m_chou_yamata_online_sale_processing;


/*  Abstract
abstract class Product {

    String product_name;    // instance field
    double price;           // instance field

    // Constructor
    Product( String product_name, double price ) {
        this.product_name = product_name;
        this.price = price;
        // System.out.println( "Product constructor called." );
    }

    // Abstract method
    abstract void payment();

    // Normal method
    void showInfo() {
        System.out.println( product_name + " price: " + price + "$" );
        
    }
    
    // Nexted abstract
    abstract class Care {
        void takeCrae();
    }

}

class Customer extends Product {

    Customer(String product_name, double price) {
        super(product_name, price);     // call abstract class constructor
    }

    @Override
    void payment() {
        System.out.println(product_name + " Paid");
    }

}
*/


/*  Interface
interface Product {
    
    // 1. Constant (public static final by default)
    String product_name = "Coca";
    
    // 2. Abstract method ( no body )
    void price ();
    
    // 3. Default method ( wtih body )
    default void check () {
        System.out.println( "Product is checking." );
    }
    
    // 4. Static method
    static void info() {
        System.out.println("This is Product interface.");
    }
    
    // 5. Nested class ( implicitly static )
    class Helper {
        void help () {
            System.out.println("Helping Customer.");
        }
    }
    
    // 6. Another nested class ( you can have many )
    class Extra {
        void show () {
            System.out.println( "Extra class inside interface." );
        }
    }
    
    // 7. Nested interface
    interface Care {
        void takeCare();
    }
    
}

class Customer implements Product {
    
    // must implement abstract method
    public void price () {
        System.out.println( "Price 2.5$" );
    }
    
}
*/


public class test_project_v2 {

    /**
     * @param args the command line arguments
     */
    
    
    public static void main(String[] args) {
        
        /*  Abstract
        Customer c = new Customer("Coca", 2.5 );
        c.payment();
        c.showInfo();
        */
        
        /*  Interface
        Customer c = new Customer();
        c.price();  // abstract method
        c.check();  // default method
        
        Product.info(); // static method
        
        // Using nested class
        Product.Helper h = new Product.Helper();
        h.help();
        
        Product.Extra e = new Product.Extra();
        e.show();
        */
        
        
        
        
        
        
    }
    
}
