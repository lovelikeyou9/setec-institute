namespace POS_System_Product
{
    partial class PaymentForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.txtTotalAmount = new Customize_TextBox.FloatingBox();
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.txtDiscountPrice = new Customize_TextBox.FloatingBox();
            this.label4 = new System.Windows.Forms.Label();
            this.txtPayment = new Customize_TextBox.FloatingBox();
            this.label5 = new System.Windows.Forms.Label();
            this.txtCashReturned = new Customize_TextBox.FloatingBox();
            this.label6 = new System.Windows.Forms.Label();
            this.txtCashReceived = new Customize_TextBox.FloatingBox();
            this.btnPayment = new System.Windows.Forms.Button();
            this.comboDiscount = new System.Windows.Forms.ComboBox();
            this.cPrint = new System.Windows.Forms.CheckBox();
            this.cPrintPreView = new System.Windows.Forms.CheckBox();
            this.cPrintDialog = new System.Windows.Forms.CheckBox();
            this.SuspendLayout();
            // 
            // txtTotalAmount
            // 
            this.txtTotalAmount.Location = new System.Drawing.Point(241, 35);
            this.txtTotalAmount.Name = "txtTotalAmount";
            this.txtTotalAmount.ReadOnly = true;
            this.txtTotalAmount.Size = new System.Drawing.Size(344, 38);
            this.txtTotalAmount.TabIndex = 0;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(26, 41);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(191, 32);
            this.label1.TabIndex = 1;
            this.label1.Text = "Total Amount:";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(26, 105);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(133, 32);
            this.label2.TabIndex = 3;
            this.label2.Text = "Discount:";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(26, 170);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(205, 32);
            this.label3.TabIndex = 5;
            this.label3.Text = "Discount Price:";
            // 
            // txtDiscountPrice
            // 
            this.txtDiscountPrice.Location = new System.Drawing.Point(241, 164);
            this.txtDiscountPrice.Name = "txtDiscountPrice";
            this.txtDiscountPrice.ReadOnly = true;
            this.txtDiscountPrice.Size = new System.Drawing.Size(344, 38);
            this.txtDiscountPrice.TabIndex = 4;
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Font = new System.Drawing.Font("Microsoft Sans Serif", 16.2F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label4.Location = new System.Drawing.Point(26, 233);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(142, 32);
            this.label4.TabIndex = 7;
            this.label4.Text = "Payment:";
            // 
            // txtPayment
            // 
            this.txtPayment.Font = new System.Drawing.Font("Microsoft Sans Serif", 16.2F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.txtPayment.Location = new System.Drawing.Point(241, 227);
            this.txtPayment.Name = "txtPayment";
            this.txtPayment.ReadOnly = true;
            this.txtPayment.Size = new System.Drawing.Size(344, 38);
            this.txtPayment.TabIndex = 6;
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(26, 365);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(212, 32);
            this.label5.TabIndex = 11;
            this.label5.Text = "Cash Returned:";
            // 
            // txtCashReturned
            // 
            this.txtCashReturned.BackColor = System.Drawing.Color.White;
            this.txtCashReturned.ForeColor = System.Drawing.Color.White;
            this.txtCashReturned.Location = new System.Drawing.Point(241, 359);
            this.txtCashReturned.Name = "txtCashReturned";
            this.txtCashReturned.ReadOnly = true;
            this.txtCashReturned.Size = new System.Drawing.Size(344, 38);
            this.txtCashReturned.TabIndex = 10;
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Location = new System.Drawing.Point(26, 302);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(214, 32);
            this.label6.TabIndex = 9;
            this.label6.Text = "Cash Received:";
            // 
            // txtCashReceived
            // 
            this.txtCashReceived.Location = new System.Drawing.Point(241, 296);
            this.txtCashReceived.Name = "txtCashReceived";
            this.txtCashReceived.Size = new System.Drawing.Size(344, 38);
            this.txtCashReceived.TabIndex = 8;
            this.txtCashReceived.KeyUp += new System.Windows.Forms.KeyEventHandler(this.txtCashReceived_KeyUp);
            // 
            // btnPayment
            // 
            this.btnPayment.Enabled = false;
            this.btnPayment.Location = new System.Drawing.Point(309, 417);
            this.btnPayment.Name = "btnPayment";
            this.btnPayment.Size = new System.Drawing.Size(198, 57);
            this.btnPayment.TabIndex = 12;
            this.btnPayment.Text = "Payment";
            this.btnPayment.UseVisualStyleBackColor = true;
            this.btnPayment.Click += new System.EventHandler(this.btnPayment_Click);
            // 
            // comboDiscount
            // 
            this.comboDiscount.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.comboDiscount.FormattingEnabled = true;
            this.comboDiscount.Items.AddRange(new object[] {
            "0%",
            "5%",
            "10%",
            "15%",
            "20%"});
            this.comboDiscount.Location = new System.Drawing.Point(241, 98);
            this.comboDiscount.Name = "comboDiscount";
            this.comboDiscount.Size = new System.Drawing.Size(344, 39);
            this.comboDiscount.TabIndex = 13;
            this.comboDiscount.SelectedIndexChanged += new System.EventHandler(this.comboDiscount_SelectedIndexChanged);
            // 
            // cPrint
            // 
            this.cPrint.AutoSize = true;
            this.cPrint.Checked = true;
            this.cPrint.CheckState = System.Windows.Forms.CheckState.Checked;
            this.cPrint.Location = new System.Drawing.Point(32, 497);
            this.cPrint.Name = "cPrint";
            this.cPrint.Size = new System.Drawing.Size(95, 36);
            this.cPrint.TabIndex = 14;
            this.cPrint.Text = "Print";
            this.cPrint.UseVisualStyleBackColor = true;
            this.cPrint.CheckedChanged += new System.EventHandler(this.cPrint_CheckedChanged);
            // 
            // cPrintPreView
            // 
            this.cPrintPreView.AutoSize = true;
            this.cPrintPreView.Location = new System.Drawing.Point(180, 497);
            this.cPrintPreView.Name = "cPrintPreView";
            this.cPrintPreView.Size = new System.Drawing.Size(203, 36);
            this.cPrintPreView.TabIndex = 15;
            this.cPrintPreView.Text = "Print Preview";
            this.cPrintPreView.UseVisualStyleBackColor = true;
            this.cPrintPreView.CheckedChanged += new System.EventHandler(this.cPrintPreView_CheckedChanged);
            // 
            // cPrintDialog
            // 
            this.cPrintDialog.AutoSize = true;
            this.cPrintDialog.Location = new System.Drawing.Point(447, 497);
            this.cPrintDialog.Name = "cPrintDialog";
            this.cPrintDialog.Size = new System.Drawing.Size(184, 36);
            this.cPrintDialog.TabIndex = 16;
            this.cPrintDialog.Text = "Print Dialog";
            this.cPrintDialog.UseVisualStyleBackColor = true;
            this.cPrintDialog.CheckedChanged += new System.EventHandler(this.checkBox3_CheckedChanged);
            // 
            // PaymentForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(16F, 31F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(668, 555);
            this.Controls.Add(this.cPrintDialog);
            this.Controls.Add(this.cPrintPreView);
            this.Controls.Add(this.cPrint);
            this.Controls.Add(this.comboDiscount);
            this.Controls.Add(this.btnPayment);
            this.Controls.Add(this.label5);
            this.Controls.Add(this.txtCashReturned);
            this.Controls.Add(this.label6);
            this.Controls.Add(this.txtCashReceived);
            this.Controls.Add(this.label4);
            this.Controls.Add(this.txtPayment);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.txtDiscountPrice);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.txtTotalAmount);
            this.Font = new System.Drawing.Font("Microsoft Sans Serif", 16.2F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.Margin = new System.Windows.Forms.Padding(6);
            this.Name = "PaymentForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "PaymentForm";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private Customize_TextBox.FloatingBox txtTotalAmount;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label3;
        private Customize_TextBox.FloatingBox txtDiscountPrice;
        private System.Windows.Forms.Label label4;
        private Customize_TextBox.FloatingBox txtPayment;
        private System.Windows.Forms.Label label5;
        private Customize_TextBox.FloatingBox txtCashReturned;
        private System.Windows.Forms.Label label6;
        private Customize_TextBox.FloatingBox txtCashReceived;
        private System.Windows.Forms.Button btnPayment;
        private System.Windows.Forms.ComboBox comboDiscount;
        private System.Windows.Forms.CheckBox cPrint;
        private System.Windows.Forms.CheckBox cPrintPreView;
        private System.Windows.Forms.CheckBox cPrintDialog;
    }
}