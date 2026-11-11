using System;
using System.Collections.Generic;
using System.Drawing;
using System.Drawing.Printing;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace POS_System_Product
{
    internal class MyData
    {
        internal static void LoadProduct(TableLayoutPanel tableLayoutPanel)
        {
            string[] files = Directory.GetFiles("C:\\Users\\Admin\\OneDrive\\Desktop\\S\\C#\\1\\2\\POS_System_Product\\Image");

            int c = 1, r = 0;
            Random rnd = new Random();
            int id = 1;
            foreach (string file in files)
            {
                if (file.ToLower().EndsWith(".jpg"))
                {
                    Image picture = Image.FromFile(file);
                    string pname = Path.GetFileNameWithoutExtension(file);
                    double price = rnd.Next(18) + 1;

                    ProductControl p = new ProductControl()
                    {
                        Id = id++,
                        Picture = picture,
                        PName = pname,
                        Price = price,
                    };
                    tableLayoutPanel.Controls.Add(p, c, r);
                    c++;
                    if (c > 4)
                    {
                        c = 1;
                        r++;
                    }
                }
            }
        }


        internal static void ShowOrderDetail(DataGridView dataGridView, TextBox txtTotalAmount)
        {
            foreach(var p in Orders)
            {
                dataGridView.Rows.Add(p.Row);
            }

            txtTotalAmount.Text = $"{TotalAmount:c2}";
        }

        internal static void PrintDocument(PaymentForm payment, PrintPageEventArgs e)
        {
            e.Graphics.DrawImage(Properties.Resources.sven, 2, 2, 55, 55);
            e.Graphics.DrawImage(Properties.Resources.D08a4117134ac18250192fc896a5f05d0_blackberry_logo1, 85, 5, 100, 25);

            Font font = new Font("Arial", 8, FontStyle.Bold);
            string address = "# 203, SengSo, Phnom Penh \nTel:012 332 551";
            e.Graphics.DrawString(address, font, Brushes.Black, 64, 25);
            e.Graphics.DrawLine(new Pen(Brushes.Black,1), 0, 64, 300, 64);

            string seller = $"Seller: Chou Yamata          {DateTime.Now:dd-MM-yyyy HH:mm:ss tt}";
            font = new Font("Arial", 8, FontStyle.Regular);
            e.Graphics.DrawString(seller, font, Brushes.Black, 0, 66);

            e.Graphics.DrawLine(new Pen(Brushes.Black, 10), 0, 91, 300, 91);
            font = new Font("Consolas", 7, FontStyle.Bold);
            string header = "No".PadRight(3) +
                "Product Name".PadRight(20) + "Price".PadRight(10) +
                "Qty".PadRight(5) + "Amont";
            e.Graphics.DrawString(header, font, Brushes.White, 0, 86);

            font = new Font("Consolas", 7, FontStyle.Regular);
            int x = 0, y = 100;
            int no = 1;
            foreach (var p in Orders)
            {
                string pname = p.PName;
                if (pname.Length >= 20)
                {
                    pname = pname.Substring(0, 17);
                    pname += "...";
                }

                string row = $"{no++:00}".PadRight(3) +
                    $"{pname}".PadRight(20) +
                    $"{p.Price:c2}".PadRight(10) +
                    $"{p.Qty}".PadRight(5) +
                    $"{p.Amount:c2}";
                e.Graphics.DrawString(row, font, Brushes.Black, x, y);
                e.Graphics.DrawLine(new Pen(Brushes.Black, 0.2f), 0, y + 13, 300, y + 13);
                y += 15;
            }
            e.Graphics.DrawLine(new Pen(Brushes.Black, 2), 0, y-2, 300, y-2);

            
            string text = $"Total Amount: ".PadLeft(38)+$"{payment.TotalAmount:c2}";
            e.Graphics.DrawString(text, font, Brushes.Black, 0, y);
            y += 15;

            text = $"Discount: ".PadLeft(38) + $"{payment.Discount}%";
            e.Graphics.DrawString(text, font, Brushes.Black, 0, y);
            y += 15;

            text = $"Discount Price: ".PadLeft(38) + $"{payment.DiscountPrice:c2}";
            e.Graphics.DrawString(text, font, Brushes.Black, 0, y);
            y += 15;

            e.Graphics.DrawLine(new Pen(Brushes.LightGray, 10), 128, y+6, 300, y+6);
            font = new Font("Consolas", 7, FontStyle.Bold);
            text = $"Payment: ".PadLeft(38) + $"{payment.Payment:c2}";
            e.Graphics.DrawString(text, font, Brushes.Black, 0, y);
            y += 15;

            font = new Font("Consolas", 7, FontStyle.Regular);
            text = $"Cash Recevied: ".PadLeft(38) + $"{payment.CashReceived:c2}";
            e.Graphics.DrawString(text, font, Brushes.Black, 0, y);
            y += 15;

            font = new Font("Consolas", 7, FontStyle.Bold);
            text = $"Cash Returned: ".PadLeft(38) + $"{payment.CashReturned:c2}";
            e.Graphics.DrawString(text, font, Brushes.Black, 0, y);
            y += 15;

            y += 15;
            font = new Font("Kh Content", 6, FontStyle.Regular);
            text = "      សូមអរគុណចំពោះការទិញទំនិញនៅហាងរបស់យើងខ្ញុំ សូមអញ្ជើញមកម្តងទៀត";
            e.Graphics.DrawString(text, font, Brushes.Black, 0, y);
            y += 15;

            text = "                     Thank you for shopping with us, please come again!";
            e.Graphics.DrawString(text, font, Brushes.Black, 0, y);
            y += 15;

            text = "                                 ទំនិញដែលទិញហើយមិនអាចដូរបានទេ";
            e.Graphics.DrawString(text, font, Brushes.Black, 0, y);
            y += 15;

            text = "                                    Goods sold are not returnable";
            e.Graphics.DrawString(text, font, Brushes.Black, 0, y);

        }

        public static HashSet<ProductControl> Orders { get; set; } = new HashSet<ProductControl>();

        public static double TotalAmount
        {
            get
            {
                double totalAmount = 0;
                foreach (var p in Orders)
                {
                    totalAmount += p.Amount;
                }
                return totalAmount;

            }
        }

        public static ToolStripMenuItem MShowOrderDetail { get; internal set; }
        public static ToolStripMenuItem MPayment { get; internal set; }
    }
}
