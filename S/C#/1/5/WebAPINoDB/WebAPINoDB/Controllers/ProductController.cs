using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using WebAPINoDB.Data;

namespace WebAPINoDB.Controllers
{
    [Route("api/product")]
    [ApiController]
    public class ProductController : ControllerBase
    {

        private HashSet<Product> _products = DB.Products;

        [HttpGet]
        public object GetAll()
        {
            return _products; 
        }

        [HttpGet("{id}"), HttpGet("id/{id}")]
        public object GetById(int id)
        {
            var product = _products.FirstOrDefault(p => p.Id == id);

            if(product == null)
            {
                return NotFound(new { message = "Product Not Found", id = id });
            }

            return Ok(product);
        }

        [HttpGet("name/{name}")]
        public object GetByName(string name)
        {
            var products = _products.Where(p => p.Name.ToLower() == name.ToLower()).ToList();

            if (products.Count == 0)
            {
                return NotFound(new { message = "Product Not Found", name = name });
            }

            return Ok(products);
        }

        [HttpPost]
        public object AddNewProduct([FromBody] Product product)
        {

            var existingProduct = _products.FirstOrDefault(p => p.Id == product.Id);
            if(existingProduct != null)
            {
                return BadRequest(new { message = "Product Id Already Exist", id = product.Id });
            }

            _products.Add(product);

            return Accepted(new { message = "Product added successfully", product = product });

        }

        [HttpPut]
        public object UpdateProduct([FromBody] Product product)
        {

            var existingProduct = _products.FirstOrDefault(p => p.Id == product.Id);
            if(existingProduct == null)
            {
                return NotFound(new { message = "Product not Found", product = product });
            }


            existingProduct.Name = product.Name;
            existingProduct.Price = product.Price;  
            existingProduct.Qty = product.Qty;
            return Accepted(new { message = "Product updated successfully", product = existingProduct });

        }

        [HttpDelete("{id}"), HttpDelete("id/{id}")]
        public object DeleteProduct(int id)
        {
            var product = _products.FirstOrDefault(p => p.Id == id);

            if(product == null)
            {
                return NotFound(new { message = "Product Not Found", id = id });
            }

            _products.Remove(product);
            return Accepted(new { message = "Product Delete Successfully", product = product });

        }




    }
}
