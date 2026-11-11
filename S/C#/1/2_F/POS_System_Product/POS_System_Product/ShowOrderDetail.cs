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
    public partial class ShowOrderDetail : Form
    {
        public ShowOrderDetail()
        {
            InitializeComponent();

            MyData.ShowOrderDetail(dataGridView, txtTotalAmount);

        }

        private void dataGridView1_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {

        }

        private void ShowOrderDetail_Load(object sender, EventArgs e)
        {

        }
    }
}
