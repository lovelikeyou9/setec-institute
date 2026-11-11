using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.IO;
using System.Resources;

namespace POS_System_Product
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();

            MyData.LoadProduct(tableLayoutPanelFood, 1);
            MyData.LoadProduct(tableLayoutPanelDrink, 2);
            MyData.LoadProduct(tableLayoutPanelBeer, 3);
            MyData.LoadProduct(tableLayoutPanel_Item, 4);

            MyData.MShowOrderDetail = mShowOrderDetail;
            MyData.MPayment = mPayment;

        }

        private void exToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (MyData.Orders.Count > 0)//if there is an order
            {
                DialogResult result = MessageBox.Show("Do you really want to Exit?", "Warning", MessageBoxButtons.YesNo, MessageBoxIcon.Warning);
                if (result == DialogResult.Yes)
                {
                    MyData.Orders.Clear();
                    this.Close();
                }
            }
            else//if there is no order
            {
                this.Close();
            }
        }

        private void mShowOrderDetail_Click(object sender, EventArgs e)
        {
            new ShowOrderDetail().ShowDialog(this);
        }

        private PaymentForm payment;
        private void mPayment_Click(object sender, EventArgs e)
        {
            payment = new PaymentForm();
            if (payment.ShowDialog(this) == DialogResult.OK)
            {
                if (payment.PrintPreview)
                    printPreviewDialog.ShowDialog(this);
                else if (payment.Print)
                    printDocument.Print();
                else if (payment.PrintDialog)
                {
                    var result = printDialog.ShowDialog();
                    if (result == DialogResult.OK)
                    {
                        printDialog.Document.Print();
                    }
                }

                MyData.Orders.Clear();

                this.Close();
                new Form1().ShowDialog(this);
            }

        }

        private void mReload_Click(object sender, EventArgs e)
        {
            if (MyData.Orders.Count > 0)//if there is an order
            {
                DialogResult result = MessageBox.Show("Are you sure you want to reload?", "Warning", MessageBoxButtons.YesNo, MessageBoxIcon.Warning);

                if (result == DialogResult.Yes)
                {
                    MyData.Orders.Clear();
                    this.Close();
                    new Form1().ShowDialog(this);
                }
            }
            else//if there is no order
            {
                this.Close();
                new Form1().ShowDialog(this);
            }

        }

        private void printDocument_PrintPage(object sender, System.Drawing.Printing.PrintPageEventArgs e)
        {
            MyData.PrintDocument(payment, e);
        }

        private void tableLayoutPanelFood_Paint(object sender, PaintEventArgs e)
        {

        }

        private void menuStrip1_ItemClicked(object sender, ToolStripItemClickedEventArgs e)
        {

        }

        private void tableLayoutPanelDrink_Paint(object sender, PaintEventArgs e)
        {

        }

        private void tableLayoutPanelBeer_Paint(object sender, PaintEventArgs e)
        {

        }

        private void tableLayoutPanel_Item_Paint(object sender, PaintEventArgs e)
        {

        }
    }
}
