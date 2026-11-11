using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Data.SqlClient;
using POS_System_Product;
using System.IO;
using System.Xml.Linq;


namespace POS_SQLServer
{
    public partial class FormEditProduct: Form
    {
        private long _productId;

        // Chou Yamata

        public FormEditProduct(long productId)
        {
            InitializeComponent();
            _productId = productId;


            //btnSave.Click += btnSave_Click;
            //btnBrowse.Click += btnBrowse_Click;

            LoadCategories(); // Load categories into cmbCategory
            //LoadProduct();    // Load product details into form controls
            //LoadProductDetails();
            
            LoadProductData();
        }



        private void txtProductName_TextChanged(object sender, EventArgs e)
        {

        }

        private void btnBrowse_Click(object sender, EventArgs e)
        {
            using (OpenFileDialog ofd = new OpenFileDialog())
            {
                ofd.Title = "Select Product Image";
                ofd.Filter = "Image Files|*.jpg;*.jpeg;*.png;*.bmp";

                if (ofd.ShowDialog() == DialogResult.OK)
                {
                    pbPhoto.Image = Image.FromFile(ofd.FileName);
                }
            }
        }


        private void pbPhoto_Click(object sender, EventArgs e)
        {

        }

        private void cmbCategory_SelectedIndexChanged(object sender, EventArgs e)
        {

        }

        private void btnSave_Click(object sender, EventArgs e)
        {
            // Validate inputs
            if (string.IsNullOrWhiteSpace(txtProductName.Text))
            {
                MessageBox.Show("Please enter product name.");
                return;
            }

            if (!decimal.TryParse(txtPrice.Text, out decimal price))
            {
                MessageBox.Show("Invalid price.");
                return;
            }

            if (!int.TryParse(txtQtyInStock.Text, out int qty))
            {
                MessageBox.Show("Invalid quantity.");
                return;
            }

            if (cmbCategory.SelectedValue == null || cmbCategory.SelectedIndex < 0)
            {
                MessageBox.Show("Please select a valid category.");
                return;
            }

            if (pbPhoto.Image == null)
            {
                MessageBox.Show("Please select a photo.");
                return;
            }

            // Convert image to byte array
            byte[] photoBytes;
            using (var ms = new MemoryStream())
            {
                pbPhoto.Image.Save(ms, System.Drawing.Imaging.ImageFormat.Png);
                photoBytes = ms.ToArray();
            }

            long categoryId = Convert.ToInt64(cmbCategory.SelectedValue);

            string connectionString = @"Server=PCYamata\MSSQLSERVER_01;Database=dbsv2;Integrated Security=True;";
            string updateQuery = @"
        UPDATE tblProduct
        SET pname = @pname,
            price = @price,
            QtyInStock = @qty,
            photo = @photo,
            cid = @cid
        WHERE id = @id";

            try
            {
                using (SqlConnection conn = new SqlConnection(connectionString))
                using (SqlCommand cmd = new SqlCommand(updateQuery, conn))
                {
                    cmd.Parameters.AddWithValue("@pname", txtProductName.Text.Trim());
                    cmd.Parameters.AddWithValue("@price", price);
                    cmd.Parameters.AddWithValue("@qty", qty);
                    cmd.Parameters.AddWithValue("@photo", photoBytes);
                    cmd.Parameters.AddWithValue("@cid", categoryId);
                    cmd.Parameters.AddWithValue("@id", _productId);

                    conn.Open();
                    cmd.ExecuteNonQuery();
                }

                MessageBox.Show("Product updated successfully.");
                this.DialogResult = DialogResult.OK;
                this.Close();
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error updating product: " + ex.Message);
            }
        }



        private void ClearForm()
        {
            txtProductName.Text = "";
            txtPrice.Text = "";
            txtQtyInStock.Text = "";
            cmbCategory.SelectedIndex = -1;
            pbPhoto.Image = null;
        }



        private void btnReset_Click(object sender, EventArgs e)
        {
            LoadProductDetails(); // Reload from DB
        }




        private void btnCancel_Click(object sender, EventArgs e)
        {
            this.DialogResult = DialogResult.Cancel;
            this.Close();
        }

        private void txtQtyInStock_TextChanged(object sender, EventArgs e)
        {

        }

        private void txtPrice_TextChanged(object sender, EventArgs e)
        {

        }




        private void FormEditProduct_Load(object sender, EventArgs e)
        {
            LoadCategories();
            LoadProduct(); // Also load product if you're editing
        }


        private void LoadCategories()
        {
            string connectionString = @"Server=PCYamata\MSSQLSERVER_01;Database=dbsv2;Integrated Security=True;";
            string query = "SELECT id, categoryName FROM tblCategory";

            DataTable dt = new DataTable();

            try
            {
                using (SqlConnection conn = new SqlConnection(connectionString))
                using (SqlCommand cmd = new SqlCommand(query, conn))
                using (SqlDataAdapter adapter = new SqlDataAdapter(cmd))
                {
                    adapter.Fill(dt);
                }

                DataRow newRow = dt.NewRow();
                newRow["id"] = DBNull.Value;
                newRow["categoryName"] = "-- Select Category --";
                dt.Rows.InsertAt(newRow, 0);

                cmbCategory.DataSource = dt;
                cmbCategory.DisplayMember = "categoryName";
                cmbCategory.ValueMember = "id";
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error loading categories: " + ex.Message);
            }
        }






        private void LoadProduct()
        {
            string connectionString = @"Server=PCYamata\MSSQLSERVER_01;Database=dbsv2;Integrated Security=True;";
            string query = "SELECT pname, price, QtyInStock, photo, cid FROM tblProduct WHERE id = @id";

            using (SqlConnection conn = new SqlConnection(connectionString))
            using (SqlCommand cmd = new SqlCommand(query, conn))
            {
                cmd.Parameters.AddWithValue("@id", _productId);
                conn.Open();
                SqlDataReader reader = cmd.ExecuteReader();

                if (reader.Read())
                {
                    txtProductName.Text = reader.GetString(0);
                    txtPrice.Text = reader.GetDecimal(1).ToString("F2");
                    txtQtyInStock.Text = reader.GetInt32(2).ToString();

                    byte[] imgData = (byte[])reader["photo"];
                    using (MemoryStream ms = new MemoryStream(imgData))
                    {
                        pbPhoto.Image = Image.FromStream(ms);
                    }

                    long cid = reader.GetInt64(4);
                    cmbCategory.SelectedValue = cid;
                }
            }
        }





        private void LoadProductDetails()
        {
            string connectionString = @"Server=PCYamata\MSSQLSERVER_01;Database=dbsv2;Integrated Security=True;";
            string query = @"
        SELECT pname, price, QtyInStock, photo, cid
        FROM tblProduct
        WHERE id = @id";

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
                            txtPrice.Text = Convert.ToDecimal(reader["price"]).ToString("0.00");
                            txtQtyInStock.Text = reader["QtyInStock"].ToString();
                            cmbCategory.SelectedValue = Convert.ToInt64(reader["cid"]);

                            // Convert image byte[] to Image
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
                MessageBox.Show("Error loading product details: " + ex.Message);
            }
        }



        private void pbPhoto_MouseEnter(object sender, EventArgs e)
        {
            if (pbPhoto.Image != null)
            {
                Form preview = new Form
                {
                    StartPosition = FormStartPosition.Manual,
                    Location = Cursor.Position,
                    Size = new Size(300, 300),
                    FormBorderStyle = FormBorderStyle.FixedToolWindow
                };

                PictureBox previewBox = new PictureBox
                {
                    Image = pbPhoto.Image,
                    Dock = DockStyle.Fill,
                    SizeMode = PictureBoxSizeMode.Zoom
                };

                preview.Controls.Add(previewBox);
                preview.Show();
                preview.BringToFront();

                // Close on mouse leave
                previewBox.MouseLeave += (s, ev) => preview.Close();
            }
        }




        private void LoadProductData()
        {
            string connectionString = @"Server=PCYamata\MSSQLSERVER_01;Database=dbsv2;Integrated Security=True;";
            string query = "SELECT pname, price, QtyInStock, photo, cid FROM tblProduct WHERE id = @id";

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
                            cmbCategory.SelectedValue = Convert.ToInt64(reader["cid"]);

                            byte[] photoData = (byte[])reader["photo"];
                            using (var ms = new MemoryStream(photoData))
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




        private Form _previewForm;

        private void pbPhoto_MouseHover(object sender, EventArgs e)
        {
            if (pbPhoto.Image == null) return;

            _previewForm = new Form();
            _previewForm.FormBorderStyle = FormBorderStyle.None;
            _previewForm.StartPosition = FormStartPosition.Manual;
            _previewForm.Size = new Size(300, 300);
            _previewForm.BackColor = Color.Black;
            _previewForm.TopMost = true;

            PictureBox previewBox = new PictureBox
            {
                Image = pbPhoto.Image,
                SizeMode = PictureBoxSizeMode.Zoom,
                Dock = DockStyle.Fill
            };

            _previewForm.Controls.Add(previewBox);

            // Position near the cursor
            Point screenPoint = Cursor.Position;
            _previewForm.Location = new Point(screenPoint.X + 10, screenPoint.Y + 10);

            _previewForm.Show();
        }

        private void pbPhoto_MouseLeave(object sender, EventArgs e)
        {
            _previewForm?.Close();
            _previewForm = null;
        }








    }
}
