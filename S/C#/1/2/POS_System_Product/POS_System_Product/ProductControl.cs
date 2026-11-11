using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace POS_System_Product
{
    public partial class ProductControl : UserControl
    {
        public ProductControl()
        {
            InitializeComponent();
        }

        private void ProductControl_Load(object sender, EventArgs e)
        {

        }

        private void textBox1_TextChanged(object sender, EventArgs e)
        {

        }



        private void txtPrice_TextChanged(object sender, EventArgs e)
        {
           
        }

        private void txtPrice_KeyPress(object sender, KeyPressEventArgs e)
        {
            e.Handled = true;
        }

        public int Id { get; set; }
        public string PName { 
            get => txtProductName.Text;
            set => txtProductName.Text = value; }

        private double price = 0;
        public double Price {
            get => price;
            set {
                price = value;
                txtPrice.Text = price.ToString("$#,##0.00");
            }
        }
        public int Qty { get; set; }
        public double Amount { get => Price * Qty; } //{ get => Price * Qty; }
        public Image Picture {
            get => picture.Image;
            set => picture.Image = value;
        }
        public object[] Row { get => new object[] { 
            $"{Id:000}",
            PName ,
            $"{Price:c2}",
            Qty,
            $"{Amount:c2}",

        }; }

        private void button1_Click(object sender, EventArgs e)
        {
            Qty++;
            btnBuy.Text = $"Buy({Qty})";
            btnCancel.Visible = true;

            MyData.Orders.Add(this);

            MyData.MShowOrderDetail.Enabled = true;
            MyData.MPayment.Enabled = true;

        }

        private void Picture_Click(object sender, EventArgs e)
        {

        }

        private void btnCancel_Click(object sender, EventArgs e)
        {
            Qty--;
            if (Qty == 0)
            {
                btnBuy.Text = "Buy";
                btnCancel.Visible = false;
                MyData.Orders.Remove(this);

                if(MyData.Orders.Count == 0)
                {
                    MyData.MShowOrderDetail.Enabled = false;
                    MyData.MPayment.Enabled = false;
                }

            }
            else
            {
                btnBuy.Text = $"Buy({Qty})";
            }
        }
    }
}
