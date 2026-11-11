using POS_System_Product;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Data.SqlClient;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace POS_SQLServer
{
    public partial class Form1: Form
    {
        // Chou Yamata

        public Form1()
        {
            InitializeComponent();

            txtSearch.KeyDown += txtSearch_KeyDown;


        }

        private void Form1_Load(object sender, EventArgs e)
        {
            var loginForm = new LogInForm();
            if (loginForm.ShowDialog(this) == DialogResult.OK)
            {
                LoadProductList();
            }
            else
            {
                Application.Exit();
            }
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

        private void btnAddNew_Click(object sender, EventArgs e)
        {
            FormAddProduct addProductForm = new FormAddProduct();
            addProductForm.ShowDialog();
            LoadProductList(); // refresh grid after add
        }

        private void btnEdit_Click(object sender, EventArgs e)
        {
            if (dataGridView1.SelectedRows.Count > 0)
            {
                long productId = Convert.ToInt64(dataGridView1.SelectedRows[0].Cells["id"].Value);
                FormEditProduct editProductForm = new FormEditProduct(productId);
                editProductForm.ShowDialog();
                LoadProductList(); // refresh grid after edit
            }
            else
            {
                MessageBox.Show("Please select a product to edit.");
            }
        }






        private void dataGridView1_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {
            if (dataGridView1.Columns[e.ColumnIndex].Name == "Select")
            {
                foreach (DataGridViewRow row in dataGridView1.Rows)
                {
                    if (row.Index != e.RowIndex)
                        row.Cells["Select"].Value = false;
                }
            }
        }












        private void dataGridView1_CellDoubleClick(object sender, DataGridViewCellEventArgs e)
        {
            if (e.RowIndex >= 0)
            {
                // Get product id from hidden column
                if (dataGridView1.Rows[e.RowIndex].Cells["id"].Value != null)
                {
                    long productId = Convert.ToInt64(dataGridView1.Rows[e.RowIndex].Cells["id"].Value);
                    var viewForm = new FormViewProduct(productId);
                    viewForm.ShowDialog();

                    // After viewing, reload product list if needed
                    LoadProductList();
                }
            }
        }




        private void btnDelete_Click(object sender, EventArgs e)
        {
            // Collect IDs of all checked rows
            List<long> idsToDelete = new List<long>();

            foreach (DataGridViewRow row in dataGridView1.Rows)
            {
                bool isChecked = false;
                if (row.Cells["Select"].Value != null)
                    isChecked = Convert.ToBoolean(row.Cells["Select"].Value);

                if (isChecked)
                {
                    if (row.Cells["id"].Value != null)
                    {
                        idsToDelete.Add(Convert.ToInt64(row.Cells["id"].Value));
                    }
                }
            }

            if (idsToDelete.Count == 0)
            {
                MessageBox.Show("Please select at least one product to delete by checking the checkbox.", "No Selection", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }

            var confirm = MessageBox.Show($"Are you sure you want to delete the selected {idsToDelete.Count} product(s)?", "Confirm Delete",
                MessageBoxButtons.YesNo, MessageBoxIcon.Question);

            if (confirm == DialogResult.Yes)
            {
                string connectionString = @"Server=PCYamata\MSSQLSERVER_01;Database=dbsv2;Integrated Security=True;";
                string deleteQuery = "DELETE FROM tblProduct WHERE id = @id";

                try
                {
                    using (SqlConnection conn = new SqlConnection(connectionString))
                    {
                        conn.Open();

                        foreach (long id in idsToDelete)
                        {
                            using (SqlCommand cmd = new SqlCommand(deleteQuery, conn))
                            {
                                cmd.Parameters.Clear();
                                cmd.Parameters.AddWithValue("@id", id);
                                cmd.ExecuteNonQuery();
                            }
                        }
                    }

                    MessageBox.Show("Selected product(s) deleted successfully.");
                    LoadProductList();
                }
                catch (Exception ex)
                {
                    MessageBox.Show("Error deleting product(s): " + ex.Message);
                }
            }
        }







        private void LoadProductList(string searchKeyword = "")
        {
            string connectionString = @"Server=PCYamata\MSSQLSERVER_01;Database=dbsv2;Integrated Security=True;";

            // Use parameterized query for security and performance
            string query = @"
        SELECT 
            p.id,
            p.pname AS [Product Name],
            p.price AS [Price],
            p.QtyInStock AS [Quantity],
            c.categoryName AS [Category]
        FROM tblProduct p
        INNER JOIN tblCategory c ON p.cid = c.id
        WHERE (@searchKeyword = '' OR p.pname LIKE '%' + @searchKeyword + '%')";

            DataTable dt = new DataTable();

            try
            {
                using (SqlConnection conn = new SqlConnection(connectionString))
                using (SqlCommand cmd = new SqlCommand(query, conn))
                {
                    cmd.Parameters.AddWithValue("@searchKeyword", searchKeyword);
                    using (SqlDataAdapter adapter = new SqlDataAdapter(cmd))
                    {
                        adapter.Fill(dt);
                    }
                }

                // Clear existing columns and re-add as before (your existing code)...
                dataGridView1.Columns.Clear();

                // Checkbox column
                DataGridViewCheckBoxColumn checkBoxColumn = new DataGridViewCheckBoxColumn();
                checkBoxColumn.Name = "Select";
                checkBoxColumn.HeaderText = "";
                checkBoxColumn.Width = 30;
                checkBoxColumn.ReadOnly = false;
                dataGridView1.Columns.Add(checkBoxColumn);

                // Hidden ID column
                DataGridViewTextBoxColumn idCol = new DataGridViewTextBoxColumn();
                idCol.Name = "id";
                idCol.DataPropertyName = "id";
                idCol.Visible = false;
                dataGridView1.Columns.Add(idCol);

                // Product Name
                DataGridViewTextBoxColumn nameCol = new DataGridViewTextBoxColumn();
                nameCol.Name = "ProductName";
                nameCol.HeaderText = "Product Name";
                nameCol.DataPropertyName = "Product Name";
                nameCol.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
                dataGridView1.Columns.Add(nameCol);

                // Price
                DataGridViewTextBoxColumn priceCol = new DataGridViewTextBoxColumn();
                priceCol.Name = "Price";
                priceCol.HeaderText = "Price";
                priceCol.DataPropertyName = "Price";
                priceCol.DefaultCellStyle.Format = "C2";
                priceCol.Width = 80;
                dataGridView1.Columns.Add(priceCol);

                // Quantity
                DataGridViewTextBoxColumn qtyCol = new DataGridViewTextBoxColumn();
                qtyCol.Name = "Quantity";
                qtyCol.HeaderText = "Quantity";
                qtyCol.DataPropertyName = "Quantity";
                qtyCol.DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleRight;
                qtyCol.Width = 60;
                dataGridView1.Columns.Add(qtyCol);

                // Category
                DataGridViewTextBoxColumn catCol = new DataGridViewTextBoxColumn();
                catCol.Name = "Category";
                catCol.HeaderText = "Category";
                catCol.DataPropertyName = "Category";
                catCol.Width = 120;
                dataGridView1.Columns.Add(catCol);

                dataGridView1.DataSource = dt;

                // Set other grid properties (readonly, selection, etc.)
                dataGridView1.ReadOnly = false;
                dataGridView1.AllowUserToAddRows = false;
                dataGridView1.AllowUserToDeleteRows = false;
                dataGridView1.SelectionMode = DataGridViewSelectionMode.FullRowSelect;
                dataGridView1.MultiSelect = false;
                dataGridView1.RowHeadersVisible = false;
                dataGridView1.BackgroundColor = Color.White;
                dataGridView1.AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill;
                dataGridView1.DefaultCellStyle.ForeColor = Color.Black;
                dataGridView1.DefaultCellStyle.BackColor = Color.White;
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error loading product list: " + ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }




        private void txtSearch_Enter(object sender, EventArgs e)
        {
            if (txtSearch.Text == "Search product name...")
            {
                txtSearch.Text = "";
                txtSearch.ForeColor = Color.Black;
            }
        }

        private void txtSearch_Leave(object sender, EventArgs e)
        {
            if (string.IsNullOrWhiteSpace(txtSearch.Text))
            {
                txtSearch.Text = "Search product name...";
                txtSearch.ForeColor = Color.Gray;
            }
        }

        private void txtSearch_TextChanged(object sender, EventArgs e)
        {

        }

        private void txtSearch_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Enter)
            {
                e.Handled = true;
                e.SuppressKeyPress = true;
                btnSearch.PerformClick();
            }
        }


        private void btnSearch_Click(object sender, EventArgs e)
        {
            string searchText = txtSearch.Text.Trim();

            // If the textbox contains the placeholder text, treat it as empty
            if (searchText == "Search product name...")
                searchText = "";

            LoadProductList(searchText);
        }

    }
}
