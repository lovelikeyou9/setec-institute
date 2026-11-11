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
using System.IO;


namespace POS_SQLServer
{
    public partial class FormAddProduct: Form
    {
        // Chou Yamata

        public FormAddProduct()
        {
            InitializeComponent();
            //btnSave.Click += btnSave_Click;
            LoadCategories();

        }

        private void txtProductName_TextChanged(object sender, EventArgs e)
        {

        }

        private void txtPrice_TextChanged(object sender, EventArgs e)
        {

        }

        private void cmbCategory_SelectedIndexChanged(object sender, EventArgs e)
        {

        }

        private void pbPhoto_Click(object sender, EventArgs e)
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





        private void btnSave_Click(object sender, EventArgs e)
        {
            // Validate inputs first (simplified)
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

            if (cmbCategory.SelectedIndex <= 0 || cmbCategory.SelectedValue == null || cmbCategory.SelectedValue == DBNull.Value)
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

            string insertQuery = @"
        INSERT INTO tblProduct (pname, price, QtyInStock, photo, cid)
        VALUES (@pname, @price, @qty, @photo, @cid)";

            try
            {
                using (SqlConnection conn = new SqlConnection(connectionString))
                using (SqlCommand cmd = new SqlCommand(insertQuery, conn))
                {
                    cmd.Parameters.AddWithValue("@pname", txtProductName.Text.Trim());
                    cmd.Parameters.AddWithValue("@price", price);
                    cmd.Parameters.AddWithValue("@qty", qty);
                    cmd.Parameters.AddWithValue("@photo", photoBytes);
                    cmd.Parameters.AddWithValue("@cid", categoryId);

                    conn.Open();
                    cmd.ExecuteNonQuery();
                }

                MessageBox.Show("Product added successfully.");
                this.DialogResult = DialogResult.OK;
                this.Close();
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error adding product: " + ex.Message);
            }
        }


        private void ClearForm()
        {
            txtProductName.Clear();
            txtPrice.Clear();
            txtQtyInStock.Clear();
            cmbCategory.SelectedIndex = 0;
            pbPhoto.Image = null;
        }





        private void btnCancel_Click(object sender, EventArgs e)
        {

            this.Close();
        }

        private void txtQtyInStock_TextChanged(object sender, EventArgs e)
        {

        }

        private void FormAddProduct_Load(object sender, EventArgs e)
        {
            LoadCategories();
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

                // Add placeholder row
                DataRow placeholder = dt.NewRow();
                placeholder["id"] = 0;
                placeholder["categoryName"] = "-- Select Category --";
                dt.Rows.InsertAt(placeholder, 0);

                cmbCategory.DataSource = dt;
                cmbCategory.DisplayMember = "categoryName";
                cmbCategory.ValueMember = "id";
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error loading categories: " + ex.Message);
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



        private void pbPhoto_MouseHover(object sender, EventArgs e)
        {
            if (pbPhoto.Image != null)
            {
                var preview = new Form
                {
                    StartPosition = FormStartPosition.Manual,
                    Location = Cursor.Position,
                    Size = new Size(200, 200),
                    FormBorderStyle = FormBorderStyle.FixedToolWindow,
                    TopMost = true
                };

                var pictureBox = new PictureBox
                {
                    Dock = DockStyle.Fill,
                    Image = pbPhoto.Image,
                    SizeMode = PictureBoxSizeMode.Zoom
                };

                preview.Controls.Add(pictureBox);
                preview.Show();

                // Store the preview form in Tag for later closing
                pbPhoto.Tag = preview;
            }
        }


        private void pbPhoto_MouseLeave(object sender, EventArgs e)
        {
            if (pbPhoto.Tag is Form previewForm)
            {
                previewForm.Close();
                pbPhoto.Tag = null;
            }
        }











    }
}
