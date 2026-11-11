package m_chou_yamata_online_sale_processing;

import java.util.ArrayList;
        
// class is blueprint or template for object

public class Product { // Constructor
    
    public int product_id;   // is Field
    String product_name;
    int product_qty;
    double product_price;
    
    public Product ( int c_product_id, String c_product_name, int c_product_qty, double c_product_price ) { // Constructor
                                                                                                            // Product is not represent of class Product constructor
        this.product_id = c_product_id;    // c_product_id is assign/represent 
        this.product_name = c_product_name; // assign the parameter to the object's field
        this.product_qty = c_product_qty;
        this.product_price = c_product_price;
        
    }
    
    public void product_display() { // method
        System.out.println( product_id + " | " + product_name + " | Qty: " + product_qty + " | $" + product_price );
    }  /* it uses for 
       not for store data
       reuse/use when call it
       keep code clean
       no need to rewrite
       */
    
    public static Product findProduct_Id(ArrayList<Product> products, int f_id) {
        for (Product p : products) {
            if (p.product_id == f_id) {
                return p;
            }
        }
        return null;
    }
    
    public static Product deleteProduct_id(ArrayList<Product> products, int d_id ) {
        for ( int i = 0; i < products.size(); i++) {
            if ( products.get(i).product_id == d_id) {
                return products.remove(i);
            }
        }
        return null;
    }
    
    public static Product editProduct(ArrayList<Product> products, int e_id, String e_name, int e_qty, double e_price ) {
        for ( int i = 0; i < products.size(); i++ ) {
            if ( products.get(i).product_id == e_id ) {
                
                products.get(i).product_name = e_name;
                products.get(i).product_qty = e_qty;
                products.get(i).product_price = e_price;
                
                return products.get(i);
            }
        }
        return null;
    }
    
    @Override
    public String toString(){
        return product_name;
    }
    

    
}



/*
public class Product { 
    public static void main(String[] args) { 
    }
}
*/
