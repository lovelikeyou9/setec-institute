namespace WebAPINoDB.Data
{
    public class DB
    {

        public static HashSet<Product> Products { get; set; } = new HashSet<Product>
        {
            new Product(1, "EM Water", 10.00m, 5),
            new Product(2, "Cocacola", 20.00m, 3),
            new Product(3, "7up", 15.00m, 10),
            new Product(4, "Apple", 25.00m, 2),
            new Product(5, "Banana", 30.00m, 2)
        };

    }
}
