using POS_System_Product;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Data.SqlClient;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace POS_SQLServer
{
    public partial class FormViewProduct : Form
    {

        private long _productId;

        public FormViewProduct(long productId)
        {
            InitializeComponent();
            _productId = productId;
            LoadProductDetails();
        }

        private void LoadProductDetails()
        {
            string connectionString = @"Server=PCYamata\MSSQLSERVER_01;Database=dbsv2;Integrated Security=True;";
            string query = @"
        SELECT p.pname, p.price, p.QtyInStock, p.photo, c.categoryName
        FROM tblProduct p
        INNER JOIN tblCategory c ON p.cid = c.id
        WHERE p.id = @id";

            try
            {
                using (SqlConnection conn = new SqlConnection(connectionString))
                using (SqlCommand cmd = new SqlCommand(query, conn))
                {
                    cmd.Parameters.AddWithValue("@id", _productId);
                    conn.Open();

                    using (SqlDataReader reader = cmd.ExecuteReader())
                    {
                        if (reader.Read())
                        {
                            txtProductName.Text = reader["pname"].ToString();
                            txtPrice.Text = reader["price"].ToString();
                            txtQtyInStock.Text = reader["QtyInStock"].ToString();
                            cmbCategory.Items.Add(reader["categoryName"].ToString());
                            cmbCategory.SelectedIndex = 0;

                            byte[] photoBytes = (byte[])reader["photo"];
                            using (MemoryStream ms = new MemoryStream(photoBytes))
                            {
                                pbPhoto.Image = Image.FromStream(ms);
                            }
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error loading product: " + ex.Message);
            }
        }


        private void btnClose_Click(object sender, EventArgs e)
        {
            this.Close();
        }



        private void pbPhoto_Click(object sender, EventArgs e)
        {
            if (pbPhoto.Image != null)
            {
                var preview = new FormImagePreview(pbPhoto.Image);
                preview.ShowDialog(this);
            }
        }






    }
}
