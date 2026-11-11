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

namespace POS_System_Product
{
    public partial class frmProductAdd: Form
    {
        private string connectionString = @"Server=SQLServer;Database=dbSV2;Trusted_Connection=True;TrustServerCertificate=True;";

        public frmProductAdd()
        {
            InitializeComponent();
            LoadCategories();
        }

        private void LoadCategories()
        {
            try
            {
                using (SqlConnection conn = new SqlConnection(connectionString))
                {
                    string query = "SELECT id, categoryName FROM dbo.tblCategory";
                    SqlDataAdapter adapter = new SqlDataAdapter(query, conn);
                    DataTable dt = new DataTable();
                    adapter.Fill(dt);

                    cboCategory.DisplayMember = "categoryName";
                    cboCategory.ValueMember = "id";
                    cboCategory.DataSource = dt;
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error loading categories: " + ex.Message);
            }
        }

        private void btnSave_Click(object sender, EventArgs e)
        {
            string pname = txtProductName.Text.Trim();

            if (string.IsNullOrWhiteSpace(pname))
            {
                MessageBox.Show("Product name is required.");
                return;
            }

            if (!decimal.TryParse(txtPrice.Text.Trim(), out decimal price) ||
                !int.TryParse(txtQtyInStock.Text.Trim(), out int qty))
            {
                MessageBox.Show("Please enter valid numeric values for price and quantity.");
                return;
            }

            if (picPhoto.Image == null)
            {
                MessageBox.Show("Please select an image for the product.");
                return;
            }

            long cid = Convert.ToInt64(cboCategory.SelectedValue);

            byte[] photoBytes;
            using (MemoryStream ms = new MemoryStream())
            {
                picPhoto.Image.Save(ms, picPhoto.Image.RawFormat);
                photoBytes = ms.ToArray();
            }

            string sql = "INSERT INTO tblProduct (pname, price, QtyInStock, photo, cid) VALUES (@pname, @price, @qty, @photo, @cid)";

            using (SqlConnection conn = new SqlConnection(connectionString))
            using (SqlCommand cmd = new SqlCommand(sql, conn))
            {
                cmd.Parameters.AddWithValue("@pname", pname);
                cmd.Parameters.AddWithValue("@price", price);
                cmd.Parameters.AddWithValue("@qty", qty);
                cmd.Parameters.AddWithValue("@photo", photoBytes);
                cmd.Parameters.AddWithValue("@cid", cid);

                conn.Open();
                cmd.ExecuteNonQuery();
                conn.Close();
            }

            MessageBox.Show("Product added successfully!");
            this.Close();
        }


        private void btnBrowse_Click(object sender, EventArgs e)
        {
            OpenFileDialog ofd = new OpenFileDialog();
            ofd.Filter = "Image Files|*.jpg;*.png;*.jpeg";

            if (ofd.ShowDialog() == DialogResult.OK)
            {
                picPhoto.Image = Image.FromFile(ofd.FileName);
            }
        }

    }
}
