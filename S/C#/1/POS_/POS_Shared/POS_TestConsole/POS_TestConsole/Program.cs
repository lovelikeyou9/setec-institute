using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using POS_DataAccess;
using POS_DataAccess.Models;

namespace POS_TestConsole
{
    internal class Program
    {
        static void Main(string[] args)
        {
            try
            {
                var products = ProductRepository.GetProducts();

                foreach (var product in products)
                {
                    Console.WriteLine($"ID: {product.Id}, Name: {product.Name}, Price: {product.Price}, Quantity: {product.Quantity}, CategoryID: {product.CategoryId}");
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("Error: " + ex.Message);
            }

            Console.WriteLine("Press any key to exit...");
            Console.ReadKey();
        }
    }
}

