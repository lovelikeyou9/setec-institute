using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.IdentityModel.Tokens;
using WebAPIEntityFrameworkCore.Models;

namespace WebAPIEntityFrameworkCore.Controllers
{
    [Route("api/product")]
    [ApiController]
    public class ProductController : ControllerBase
    {

        private readonly ApplicationDdContext _dbcontext;

        public ProductController(ApplicationDdContext dbcontext)
        {
            _dbcontext = dbcontext;
        }

        [HttpGet]
        public IActionResult GetAllProducts()
        {
            var products = _dbcontext.Products.ToList();


            if(products == null || !products.Any())
            {
                return NotFound(new { message = "No products found." });
            }
            var baseUrl = $"{Request.Scheme}://{Request.Host}/";
            foreach(var product in products)
            {
                if (!string.IsNullOrEmpty(product.ImageUrl))
                {
                    product.ImageUrl = baseUrl + product.ImageUrl.TrimStart('/');
                }
            }


            return Ok(products);
        }


        [HttpPost]
        public async Task<IActionResult> PostProduct([FromForm] PostProduct product)
        {
            var file = product.Image;
            if(file == null || file.Length == 0)
            {
                return BadRequest(new {message = "Image file is required."});
            }

            string[] allowedExtensions = { "image/jpg", "image/jpeg", "/image/png" };
            if (!allowedExtensions.Contains(file.ContentType))
            {
                return BadRequest(new { message = "Innvalid image format. Only JPG and PNG are allowed." });
            }

            var uploadPath = Path.Combine(Directory.GetCurrentDirectory(), "wwwroot", "images");

            if (!Directory.Exists(uploadPath))
            {
                Directory.CreateDirectory(uploadPath);
            }

            var fileName = Guid.NewGuid().ToString() + Path.GetExtension(file.FileName);
            var filePath = Path.Combine(uploadPath, fileName);

            using ( var stream = new FileStream(filePath, FileMode.Create))
            {
                await file.CopyToAsync(stream);
            }

            var newProduct = new Product
            {
                Name = product.Name,
                Price = product.Price,
                Qty = product.Qty,
                ImageUrl = $"/images/{fileName}"
            };


            _dbcontext.Products.Add(newProduct);
            await _dbcontext.SaveChangesAsync();



            string imageFullUrl = $"{Request.Scheme}://{Request.Host}{newProduct.ImageUrl}";
            newProduct.ImageUrl = imageFullUrl;



            return CreatedAtAction(nameof(GetAllProducts), new { product = newProduct });




        }


        [HttpPut]
        public async Task<IActionResult> PutProduct([FromForm] PutProduct product)
        {

            var existingProduct = await _dbcontext.Products.FindAsync(product.Id);
            if(existingProduct == null)
            {

                return NotFound(new { message = "Product Id is not found.", id = product.Id });

            }

            existingProduct.Name = product.Name;
            existingProduct.Price = product.Price;
            existingProduct.Qty = product.Qty;

            if (product.Image != null && product.Image.Length > 0)
            {
                string[] allowedExtensions = { "image/jpg", "image/jpeg", "image/png" };
                if (!allowedExtensions.Contains(product.Image.ContentType))
                {
                    return BadRequest(new { message = "Invalid image format. Only JPG and PNG  and JPEG." });
                }
                var uploadPath = Path.Combine(Directory.GetCurrentDirectory(), "wwwroot", "images");
                if (!Directory.Exists(uploadPath))
                {
                    Directory.CreateDirectory(uploadPath);
                }
                var fileName = Guid.NewGuid().ToString() + Path.GetExtension(product.Image.FileName);
                var filePath = Path.Combine(uploadPath, fileName);
                using (var stream = new FileStream(filePath, FileMode.Create))
                {
                    await product.Image.CopyToAsync(stream);
                }
                

                //Delete Old Image File If Exists
                if (!string.IsNullOrEmpty(existingProduct.ImageUrl))
                {
                    var oldImagePath = Path.Combine(Directory.GetCurrentDirectory(), "wwwroot", existingProduct.ImageUrl.TrimStart('/'));
                    if (System.IO.File.Exists(oldImagePath))
                    {
                        System.IO.File.Delete(oldImagePath);
                    }
                }

                existingProduct.ImageUrl = $"/Images/{fileName}";

            }
            _dbcontext.Products.Update(existingProduct);
            await _dbcontext.SaveChangesAsync();
            string imageFullUrl = $"{Request.Scheme}://{Request.Host}{existingProduct.ImageUrl}";
            existingProduct.ImageUrl = imageFullUrl;


            return Accepted(new { message = "Product Updated Successfully.", product = existingProduct });

        }



        [HttpGet("{id}"), HttpGet("id/{id}")]
        public IActionResult GetProductById(int id)
        {
            var product = _dbcontext.Products.Find(id);
            if(product == null)
            {
                return NotFound(new { message = "Product Not Found.", id = id });
            }
            string baseUrl = $"{Request.Scheme}://{Request.Host}/";
            if (!string.IsNullOrEmpty(product.ImageUrl))
            {
                product.ImageUrl = baseUrl + product.ImageUrl.TrimStart('/');
            }
            return Ok(product);

        }


        [HttpGet("name/{name}")]
        public IActionResult GetProductByName(string name)
        {
            var product = _dbcontext.Products.FirstOrDefault(p => p.Name.ToLower() == name.ToLower());
            if(product == null)
            {
                return NotFound(new {message="Product Name Not Found.", name = name});
            }
            string baseUrl = $"{Request.Scheme}://{Request.Host}";
            if (!string.IsNullOrEmpty(product.ImageUrl))
            {
                product.ImageUrl = baseUrl + product.ImageUrl.TrimStart('/');
            }

            return Ok(product);

        }


        [HttpDelete("{id}"), HttpDelete("id/{id}")]
        public IActionResult DeleteProduct(int id)
        {
            var product = _dbcontext.Products.Find(id);
            if(product == null)
            {
                return NotFound(new {message = "Product Id is not found.", id = id});
            }
            //Delete Image File If Exists
            if (!string.IsNullOrEmpty(product.ImageUrl))
            {
                var imagePath = Path.Combine(Directory.GetCurrentDirectory(), "wwwroot", product.ImageUrl.TrimStart('/'));
                if (System.IO.File.Exists(imagePath))
                {
                    System.IO.File.Delete(imagePath);
                }
            }
            _dbcontext.Products.Remove(product);
            _dbcontext.SaveChanges();

            return Accepted(new { message = "Product Deleted Successfully.", product = product });
        }



    }
}
