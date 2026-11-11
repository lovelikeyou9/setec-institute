using POS_System_Product;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace POS_SQLServer
{
    public partial class Form1: Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            new LogInForm().ShowDialog(this);
        }

        private void btnOrderProducts_Click(object sender, EventArgs e)
        {
            try
            {
                MyData.Products = DBConnection.Products;

                new POS_System_Product.Form1().ShowDialog(this);
            }
            catch(Exception ex)
            {
                MessageBox.Show(ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
            
        }
    }
}
