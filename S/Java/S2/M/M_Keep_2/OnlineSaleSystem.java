import java.util.*;

// ===== PRODUCT CLASS =====
class Product {
    int id;
    String name;
    double price;
    int qty;

    public Product(int id, String name, double price, int qty) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.qty = qty;
    }
}

// ===== CUSTOMER CLASS =====
class Customer {
    int id;
    String name;

    public Customer(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

// ===== ORDER CLASS =====
class Order {
    int orderId;
    Customer customer;
    Product product;
    int quantity;

    public Order(int orderId, Customer customer, Product product, int quantity) {
        this.orderId = orderId;
        this.customer = customer;
        this.product = product;
        this.quantity = quantity;
    }
}

// ===== MAIN SYSTEM =====
public class OnlineSaleSystem {

    static Scanner sc = new Scanner(System.in);

    static ArrayList<Product> products = new ArrayList<>();
    static ArrayList<Customer> customers = new ArrayList<>();
    static ArrayList<Order> orders = new ArrayList<>();

    static int productId = 1;
    static int customerId = 1;
    static int orderId = 1;

    public static void main(String[] args) {

        int choice;

        do {
            System.out.println("\n=== Online Sale Processing ===");
            System.out.println("1. View Products");
            System.out.println("2. Add Product");
            System.out.println("3. Register Customer");
            System.out.println("4. Create Order");
            System.out.println("5. View Orders");
            System.out.println("6. Process Orders");
            System.out.println("7. Exit");
            System.out.print("Choose: ");

            choice = sc.nextInt();

            switch (choice) {
                case 1 -> viewProducts();
                case 2 -> addProduct();
                case 3 -> registerCustomer();
                case 4 -> createOrder();
                case 5 -> viewOrders();
                case 6 -> processOrders();
                case 7 -> System.out.println("Goodbye!");
                default -> System.out.println("Invalid choice!");
            }

        } while (choice != 7);
    }

    // ===== FUNCTIONS =====

    static void viewProducts() {
        if (products.isEmpty()) {
            System.out.println("No products.");
            return;
        }

        for (Product p : products) {
            System.out.println(p.id + " | " + p.name + " | $" + p.price + " | Stock: " + p.qty);
        }
    }

    static void addProduct() {
        sc.nextLine(); // clear buffer

        System.out.print("Enter product name: ");
        String name = sc.nextLine();

        System.out.print("Enter price: ");
        double price = sc.nextDouble();

        System.out.print("Enter quantity: ");
        int qty = sc.nextInt();

        products.add(new Product(productId++, name, price, qty));

        System.out.println("Product added!");
    }

    static void registerCustomer() {
        sc.nextLine();

        System.out.print("Enter customer name: ");
        String name = sc.nextLine();

        customers.add(new Customer(customerId++, name));

        System.out.println("Customer registered!");
    }

    static void createOrder() {

        if (customers.isEmpty() || products.isEmpty()) {
            System.out.println("Need customers and products first!");
            return;
        }

        System.out.println("\nCustomers:");
        for (Customer c : customers) {
            System.out.println(c.id + ". " + c.name);
        }

        System.out.print("Select customer ID: ");
        int cid = sc.nextInt();

        Customer selectedCustomer = null;
        for (Customer c : customers) {
            if (c.id == cid) {
                selectedCustomer = c;
                break;
            }
        }

        if (selectedCustomer == null) {
            System.out.println("Invalid customer!");
            return;
        }

        System.out.println("\nProducts:");
        viewProducts();

        System.out.print("Select product ID: ");
        int pid = sc.nextInt();

        Product selectedProduct = null;
        for (Product p : products) {
            if (p.id == pid) {
                selectedProduct = p;
                break;
            }
        }

        if (selectedProduct == null) {
            System.out.println("Invalid product!");
            return;
        }

        System.out.print("Enter quantity: ");
        int qty = sc.nextInt();

        if (qty > selectedProduct.qty) {
            System.out.println("Not enough stock!");
            return;
        }

        selectedProduct.qty -= qty;

        orders.add(new Order(orderId++, selectedCustomer, selectedProduct, qty));

        System.out.println("Order created!");
    }

    static void viewOrders() {
        if (orders.isEmpty()) {
            System.out.println("No orders.");
            return;
        }

        for (Order o : orders) {
            System.out.println("Order #" + o.orderId +
                    " | Customer: " + o.customer.name +
                    " | Product: " + o.product.name +
                    " | Qty: " + o.quantity);
        }
    }

    static void processOrders() {
        if (orders.isEmpty()) {
            System.out.println("No orders to process.");
            return;
        }

        System.out.println("Processing all orders...");
        orders.clear();
        System.out.println("All orders processed!");
    }
}