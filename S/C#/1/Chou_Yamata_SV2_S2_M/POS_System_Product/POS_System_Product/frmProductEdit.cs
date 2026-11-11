using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Data.Common;
using System.Data.SqlClient;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using System.Windows.Forms;
using POS_SQLServer;

namespace POS_System_Product
{
    public partial class frmProductEdit : Form
    {
        private readonly long productId;

        private ComboBox comboBoxProducts;
        private TextBox txtProductName;
        private TextBox txtPrice;
        private TextBox txtQtyInStock;
        private ComboBox cboCategory;
        private PictureBox picPhoto;
        private Button btnBrowse;
        private Button btnSave;

        public frmProductEdit()
        {
            InitializeComponent();

            DBConnection.ConnectionString(@"DESKTOP-BB6PQGM\MSSQLSERVER_01", "dbSV2");

            InitializeControls();
            LoadCategories();
            LoadProducts();

            comboBoxProducts.SelectedIndexChanged += comboBoxProducts_SelectedIndexChanged;
            btnBrowse.Click += btnBrowse_Click;
            btnSave.Click += btnSave_Click;
        }

        private void InitializeControls()
        {
            comboBoxProducts = new ComboBox { Left = 20, Top = 20, Width = 250 };
            txtProductName = new TextBox { Left = 20, Top = 60, Width = 250 };
            txtPrice = new TextBox { Left = 20, Top = 100, Width = 250 };
            txtQtyInStock = new TextBox { Left = 20, Top = 140, Width = 250 };
            cboCategory = new ComboBox { Left = 20, Top = 180, Width = 250 };
            picPhoto = new PictureBox { Left = 300, Top = 20, Width = 150, Height = 150, BorderStyle = BorderStyle.FixedSingle, SizeMode = PictureBoxSizeMode.StretchImage };
            btnBrowse = new Button { Left = 300, Top = 180, Text = "Browse", Width = 150 };
            btnSave = new Button { Left = 20, Top = 230, Text = "Save", Width = 250 };

            this.Controls.Add(comboBoxProducts);
            this.Controls.Add(txtProductName);
            this.Controls.Add(txtPrice);
            this.Controls.Add(txtQtyInStock);
            this.Controls.Add(cboCategory);
            this.Controls.Add(picPhoto);
            this.Controls.Add(btnBrowse);
            this.Controls.Add(btnSave);
        }

        private void LoadCategories()
        {
            string query = "SELECT id, categoryName FROM dbo.tblCategory";

            try
            {
                using (SqlConnection con = DBConnection.DataCon)
                using (SqlDataAdapter adapter = new SqlDataAdapter(query, con))
                {
                    DataTable dt = new DataTable();
                    adapter.Fill(dt);

                    cboCategory.DisplayMember = "categoryName";
                    cboCategory.ValueMember = "id";
                    cboCategory.DataSource = dt;
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error loading categories:\n" + ex.Message, "Load Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void LoadProducts()
        {
            try
            {
                List<Product> products = DBConnection.Products;
                comboBoxProducts.DataSource = products;
                comboBoxProducts.DisplayMember = "PName";
                comboBoxProducts.ValueMember = "Id";
            }
            catch (Exception ex)
            {
                MessageBox.Show("Failed to load products: " + ex.Message);
            }
        }

        private void comboBoxProducts_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (comboBoxProducts.SelectedItem is Product selectedProduct)
            {
                txtProductName.Text = selectedProduct.PName;
                txtPrice.Text = selectedProduct.Price.ToString("0.00");
                txtQtyInStock.Text = selectedProduct.QtyInStock.ToString();

                // Optional: load image if photo data is available
                if (selectedProduct.Photo != null)
                {
                    using (MemoryStream ms = new MemoryStream(selectedProduct.Photo))
                    {
                        picPhoto.Image = Image.FromStream(ms);
                    }
                }

                cboCategory.SelectedValue = selectedProduct.CId;
            }
        }

        private void btnBrowse_Click(object sender, EventArgs e)
        {
            using (OpenFileDialog ofd = new OpenFileDialog())
            {
                ofd.Filter = "Image Files|*.jpg;*.jpeg;*.png";
                if (ofd.ShowDialog() == DialogResult.OK)
                {
                    picPhoto.Image = Image.FromFile(ofd.FileName);
                }
            }
        }

        private void btnSave_Click(object sender, EventArgs e)
        {
            if (comboBoxProducts.SelectedItem is not Product selectedProduct)
            {
                MessageBox.Show("Please select a product to update.");
                return;
            }

            if (string.IsNullOrWhiteSpace(txtProductName.Text) ||
                string.IsNullOrWhiteSpace(txtPrice.Text) ||
                string.IsNullOrWhiteSpace(txtQtyInStock.Text) ||
                picPhoto.Image == null ||
                cboCategory.SelectedValue == null)
            {
                MessageBox.Show("All fields are required, including a photo.", "Validation Error", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }

            if (!decimal.TryParse(txtPrice.Text.Trim(), out decimal price) ||
                !int.TryParse(txtQtyInStock.Text.Trim(), out int qty) ||
                !long.TryParse(cboCategory.SelectedValue.ToString(), out long cid))
            {
                MessageBox.Show("Invalid input format.", "Validation Error", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }

            byte[] photoBytes;
            using (MemoryStream ms = new MemoryStream())
            {
                picPhoto.Image.Save(ms, ImageFormat.Png);
                photoBytes = ms.ToArray();
            }

            string sql = @"UPDATE tblProduct 
                           SET pname = @pname, price = @price, QtyInStock = @qty, photo = @photo, cid = @cid 
                           WHERE id = @id";

            try
            {
                using (SqlConnection con = DBConnection.DataCon)
                using (SqlCommand cmd = new SqlCommand(sql, con))
                {
                    cmd.Parameters.AddWithValue("@pname", txtProductName.Text.Trim());
                    cmd.Parameters.AddWithValue("@price", price);
                    cmd.Parameters.AddWithValue("@qty", qty);
                    cmd.Parameters.AddWithValue("@photo", photoBytes);
                    cmd.Parameters.AddWithValue("@cid", cid);
                    cmd.Parameters.AddWithValue("@id", selectedProduct.Id);

                    cmd.ExecuteNonQuery();
                }

                MessageBox.Show("Product updated successfully!", "Success", MessageBoxButtons.OK, MessageBoxIcon.Information);
                this.DialogResult = DialogResult.OK;
                this.Close();
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error updating product:\n" + ex.Message, "Update Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }
    }
}
