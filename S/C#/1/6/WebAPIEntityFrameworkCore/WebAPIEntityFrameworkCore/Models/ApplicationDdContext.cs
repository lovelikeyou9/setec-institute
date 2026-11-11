using Microsoft.EntityFrameworkCore;

namespace WebAPIEntityFrameworkCore.Models
{
    public class ApplicationDdContext : DbContext
    {


        public ApplicationDdContext(DbContextOptions options) : base(options)
        {
        }

        protected ApplicationDdContext()
        {
        }


        public DbSet<Product> Products { get; set; }



    }
}
