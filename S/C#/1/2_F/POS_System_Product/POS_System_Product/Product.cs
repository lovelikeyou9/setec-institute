using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace POS_System_Product
{
    public class Product
    {
        public long Id { get; set; }
        public string PName { get; set; }
        public double Price { get; set; }
        public int QtyInStock { get; set; }
        public byte[] Photo { get; set; }
        public long CId { get; set; }

        public Product() { }

        public Product(long id, string pName, double price, int qty, byte[] photo, long cId)
        {
            Id = id;
            PName = pName;
            Price = price;
            QtyInStock = qty;
            Photo = photo;
            CId = cId;
        }
    }

}
