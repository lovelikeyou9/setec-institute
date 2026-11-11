package m_chou_yamata_online_sale_processing;

import java.util.ArrayList;
        

public class Product {
    
    public int product_id; 
    String product_name;
    int product_qty;
    double product_price;
    
    public Product ( int c_product_id, String c_product_name, int c_product_qty, double c_product_price ) {
        this.product_id = c_product_id;   
        this.product_name = c_product_name;
        this.product_qty = c_product_qty;
        this.product_price = c_product_price;
        
    }
    
    public void product_display() { // method
        System.out.println( product_id + " | " + product_name + " | Qty: " + product_qty + " | $" + product_price );
    } 
    
    public static Product findProduct_Id(ArrayList<Product> products, int f_id) {
        for (Product p : products) {
            if (p.product_id == f_id) {
                return p;
            }
        }
        return null;
    }
    
    public static void deleteProduct_id(ArrayList<Product> products, int d_id ) {
        for ( int i = 0; i < products.size(); i++) {
            if ( products.get(i).product_id == d_id) {
                products.remove(i);
                System.out.println("Deleted");
                return;
            }
        }
        System.out.println("Product not found. ");
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
