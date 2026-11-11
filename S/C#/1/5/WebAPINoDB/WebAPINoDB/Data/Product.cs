using System.ComponentModel.DataAnnotations;

namespace WebAPINoDB.Data
{
    public class Product
    {

        [Required]
        [Range(1, int.MaxValue, ErrorMessage = "Id Must Be A Positive Integer.")]
        public int Id { get; set; }

        [Required]
        [StringLength(100, MinimumLength = 3, ErrorMessage = "Name cannot exceed 100 characters.")]
        public string Name { get; set; } = string.Empty;

        [Required]
        [Range(0.01, double.MaxValue, ErrorMessage = "Price Must Be A Positive number.")]
        public decimal Price { get; set; }

        [Required]
        [Range(1, double.MaxValue, ErrorMessage = "Quantity must be a non-negative Integer.")]
        public int Qty { get; set; }

        public decimal Amount { get => Price * Qty; }

        public Product()
        {
            
        }

        public Product(int id, string name, decimal price, int qty)
        {
            Id = id;
            Name = name;
            Price = price;
            Qty = qty;
        }



    }
}
