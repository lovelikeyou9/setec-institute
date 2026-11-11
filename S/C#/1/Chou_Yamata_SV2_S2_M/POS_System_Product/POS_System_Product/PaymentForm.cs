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
    public partial class PaymentForm: Form
    {
        public PaymentForm()
        {
            InitializeComponent();
            comboDiscount.SelectedIndex = 1;
            txtTotalAmount.Text = $"{TotalAmount:c2}";
        }

        public double TotalAmount { get; set; } = MyData.TotalAmount;
        public double Discount { get; set; }
        public double DiscountPrice { get; set; }
        public double Payment { get; set; }
        public double CashReceived { get; set; }
        public double CashReturned { get; set; }

        public bool Print { get; set; } = true;
        public bool PrintPreview { get; set; } = false;
        public bool PrintDialog { get; set; } = false;

        private double[] discount = { 0, 5, 10, 15, 20 };

        private void comboDiscount_SelectedIndexChanged(object sender, EventArgs e)
        {
            int index = comboDiscount.SelectedIndex;
            Discount = discount[index];
            DiscountPrice = TotalAmount * Discount / 100;
            Payment = TotalAmount - DiscountPrice;

            txtDiscountPrice.Text = DiscountPrice.ToString("c2");
            txtPayment.Text = Payment.ToString("c2");

            if (CashReceived > 0)
            {
                txtCashReceived_KeyUp(sender, null);
            }
        }

        private void txtCashReceived_KeyUp(object sender, KeyEventArgs e)
        {
            string text = txtCashReceived.Text;
            if(text != "")
            {
                CashReceived = double.Parse(text);
                CashReturned = CashReceived - Payment;
                txtCashReturned.Text = CashReturned.ToString("$#,##0.00"); //txtCashReturned.Text = CashReturned.ToString("c2");

                if (CashReceived >= Payment)
                {
                    txtCashReturned.BackColor = Color.Green;
                    btnPayment.Enabled = true;

                    if (e != null)
                    {
                        if(e.KeyCode == Keys.Enter)
                        {
                            btnPayment_Click(sender, e);
                        }
                    }

                }
                else
                {
                    txtCashReturned.BackColor = Color.Red;
                    btnPayment.Enabled = false;
                }

            }
            else
            {
                CashReceived = 0;
                CashReturned = 0;
                txtCashReturned.Text = "";
                txtCashReturned.BackColor = Color.White;
                btnPayment.Enabled = false;
            }
        }

        private void btnPayment_Click(object sender, EventArgs e)
        {
            DialogResult = DialogResult.OK;
        }

        private void cPrint_CheckedChanged(object sender, EventArgs e)
        {
            Print = cPrint.Checked;
            if (Print)
            {
                cPrintPreView.Checked = false;
                cPrintDialog.Checked = false;
            }
        }

        private void cPrintPreView_CheckedChanged(object sender, EventArgs e)
        {
            PrintPreview = cPrintPreView.Checked;
            if (PrintPreview)
            {
                cPrint.Checked = false;
                cPrintDialog.Checked = false;
            }
        }

        private void checkBox3_CheckedChanged(object sender, EventArgs e)
        {
            PrintDialog = cPrintDialog.Checked;
            if (PrintDialog)
            {
                cPrint.Checked = false;
                cPrintPreView.Checked = false;
            }
        }

        private void PaymentForm_Load(object sender, EventArgs e)
        {

        }
    }
}
