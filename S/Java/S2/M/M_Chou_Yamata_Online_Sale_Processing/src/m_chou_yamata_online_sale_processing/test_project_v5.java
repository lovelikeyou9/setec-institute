
package m_chou_yamata_online_sale_processing;

import java.util.ArrayList;




public class test_project_v5 {

    
    
    public static void main(String[] args) {
        
        /*  1
        //  ArrayList
        ArrayList<String> food = new ArrayList<>();     // Create an ArrayList
        
        //  Add Element
        food.add("Apple");   // list.add("Apple");
        food.add("Banana");
        food.add("Mango");
        
        //  Change Element
        food.set( 1, "Orage" );     // replaces Banana
        
        //  Access Element
        System.out.println(food.get(0));  // System.out.println(list.get(0));
        System.out.println(food.get(1));
        System.out.println(food.get(2));
        System.out.println("----------------------------------");
        
        //  Remove Element
        food.remove(0);     // removes by index
        food.remove("Mango");  // removes by value
        
        //  Access Element
        System.out.println(food.get(0));  // System.out.println(list.get(0));
        // error    System.out.println(food.get(1));
        // error    System.out.println(food.get(2));
        System.out.println("----------------------------------");
        */
        
        /*  2
        ArrayList<String> food = new ArrayList<>();     // Create an ArrayList
        
        //  Add Element
        food.add("Apple");   // list.add("Apple");
        food.add("Banana");
        food.add("Mango");
        
        //  for loop
        for ( int i = 0; i < food.size(); i++ ) {
            System.out.println(food.get(i));
        }
        
        //  for-each loop
        for ( String f : food ) {   //  for ( String item : list ) {
            System.out.println(f);
        }
        
        //  Check Size
        System.out.println(food.size());    //  System.out.println(list.size());
        
        //  Check if Element Exists
        if ( food.contains("Apple") ) {     //  if ( list.contains("Apple") ) {
            System.out.print("Found");
        }
        */
        
        /*  3
        class Product {
            int product_id;
            String product_name;
            double product_price;

            Product(int P_id, String P_name, double P_price){
                this.product_id = P_id;
                this.product_name = P_name;
                this.product_price = P_price;
            }

            void product_display() {
                System.out.println(product_id + " " + product_name + " " + product_price);
            }

        }
        
        
        ArrayList<Product> products = new ArrayList<>();

        products.add(new Product(1, "Coca", 1.5));
        products.add(new Product(2, "Pepsi", 1.2));
        
        for ( Product p : products ) {
            p.product_display();
        }
        */
        
        /*  4
        class Product {
            int product_id;
            String product_name;
            double product_price;

            Product(int P_id, String P_name, double P_price){
                this.product_id = P_id;
                this.product_name = P_name;
                this.product_price = P_price;
            }

            void product_display() {
                System.out.println(product_id + " " + product_name + " " + product_price);
            }

        }
        
        ArrayList<Product> products = new ArrayList<>();
        
        //  Add product
        products.add(new Product(1, "Laptop", 800));
        products.add(new Product(2, "Mouse", 20));
        
        //  Display
        for ( Product p : products ) {
            p.product_display();
        }
        
        //  Check empty
        if ( products.isEmpty() ) {
            System.out.println("Product is empty.");
        }
        */
        
        /*  4
        //  Search Product
        class Product {
            int product_id;
            String product_name;
            double product_price;

            Product(int P_id, String P_name, double P_price){
                this.product_id = P_id;
                this.product_name = P_name;
                this.product_price = P_price;
            }

            void product_display() {
                System.out.println(product_id + " " + product_name + " " + product_price);
            }
            
            public static Product findProduct_Id(ArrayList<Product> products, int f_id) {
                for (Product p : products) {
                    if (p.product_id == f_id) {
                        return p;
                    }
                }
                return null;
            }

        }
        

        ArrayList<Product> products = new ArrayList<>();
        
        //  Add product
        products.add(new Product(1, "Laptop", 800));
        products.add(new Product(2, "Mouse", 20));


        Product result = Product.findProduct_Id(products, 2);
        
        System.out.println(result.product_id + " " + result.product_name);
        
        if ( result != null ) {
            result.product_display();
        } else {
            System.out.println("Product not found.");
        }
        */
        
        /*  5
        //  Update Product
        class Product {
            int product_id;
            String product_name;
            double product_price;

            Product(int P_id, String P_name, double P_price){
                this.product_id = P_id;
                this.product_name = P_name;
                this.product_price = P_price;
            }

            void product_display() {
                System.out.println(product_id + " " + product_name + " " + product_price);
            }
            
            public static void updateProduct(ArrayList<Product> products, int new_id, String new_name, double new_price) {
                for(Product p : products) {
                    if ( p.product_id == new_id ) {
                        p.product_name = new_name;
                        p.product_price = new_price;
                        System.out.println("Updated");
                        return;
                    }
                }
                System.out.println("Product not found.");
            }
            

        }
        

        ArrayList<Product> products = new ArrayList<>();
        
        //  Add product
        products.add(new Product(1, "Laptop", 800));
        products.add(new Product(2, "Mouse", 20));


        Product.updateProduct(products, 2, "Gaming Laptop", 1200);
        */
        
        /*  6
        //  Delete Product
        class Product {
            int product_id;
            String product_name;
            double product_price;

            Product(int P_id, String P_name, double P_price){
                this.product_id = P_id;
                this.product_name = P_name;
                this.product_price = P_price;
            }

            void product_display() {
                System.out.println(product_id + " " + product_name + " " + product_price);
            }
            
            public static void deleteProduct(ArrayList<Product> products, int d_id) {
                for ( int i = 0; i < products.size(); i++ ) {
                    if ( products.get(i).product_id == d_id ) {
                       products.remove(i);
                       System.out.println("Deleted");
                       return;
                    }
                }
                System.out.println("Product not found");
            }

        }
        

        ArrayList<Product> products = new ArrayList<>();
        
        //  Add product
        products.add(new Product(1, "Laptop", 800));
        products.add(new Product(2, "Mouse", 20));


        Product.deleteProduct(products ,1);
        */
        
        
        
        
        
        
        
        
        
    }
    
}
