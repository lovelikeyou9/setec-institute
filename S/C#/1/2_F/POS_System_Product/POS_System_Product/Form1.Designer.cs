namespace POS_System_Product
{
    partial class Form1
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Form1));
            this.menuStrip1 = new System.Windows.Forms.MenuStrip();
            this.fileToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.mShowOrderDetail = new System.Windows.Forms.ToolStripMenuItem();
            this.mPayment = new System.Windows.Forms.ToolStripMenuItem();
            this.mReload = new System.Windows.Forms.ToolStripMenuItem();
            this.mExit = new System.Windows.Forms.ToolStripMenuItem();
            this.printDialog = new System.Windows.Forms.PrintDialog();
            this.printDocument = new System.Drawing.Printing.PrintDocument();
            this.printPreviewDialog = new System.Windows.Forms.PrintPreviewDialog();
            this.tabControl1 = new System.Windows.Forms.TabControl();
            this.tabPage1 = new System.Windows.Forms.TabPage();
            this.tableLayoutPanelFood = new System.Windows.Forms.TableLayoutPanel();
            this.tabPage2 = new System.Windows.Forms.TabPage();
            this.tableLayoutPanelDrink = new System.Windows.Forms.TableLayoutPanel();
            this.tabPage3 = new System.Windows.Forms.TabPage();
            this.tableLayoutPanelBeer = new System.Windows.Forms.TableLayoutPanel();
            this.tabPage4 = new System.Windows.Forms.TabPage();
            this.tableLayoutPanel_Item = new System.Windows.Forms.TableLayoutPanel();
            this.menuStrip1.SuspendLayout();
            this.tabControl1.SuspendLayout();
            this.tabPage1.SuspendLayout();
            this.tabPage2.SuspendLayout();
            this.tabPage3.SuspendLayout();
            this.tabPage4.SuspendLayout();
            this.SuspendLayout();
            // 
            // menuStrip1
            // 
            this.menuStrip1.ImageScalingSize = new System.Drawing.Size(20, 20);
            this.menuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.fileToolStripMenuItem});
            this.menuStrip1.Location = new System.Drawing.Point(0, 0);
            this.menuStrip1.Name = "menuStrip1";
            this.menuStrip1.Size = new System.Drawing.Size(1165, 36);
            this.menuStrip1.TabIndex = 1;
            this.menuStrip1.Text = "menuStrip1";
            this.menuStrip1.ItemClicked += new System.Windows.Forms.ToolStripItemClickedEventHandler(this.menuStrip1_ItemClicked);
            // 
            // fileToolStripMenuItem
            // 
            this.fileToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.mShowOrderDetail,
            this.mPayment,
            this.mReload,
            this.mExit});
            this.fileToolStripMenuItem.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.fileToolStripMenuItem.Name = "fileToolStripMenuItem";
            this.fileToolStripMenuItem.Size = new System.Drawing.Size(56, 32);
            this.fileToolStripMenuItem.Text = "File";
            // 
            // mShowOrderDetail
            // 
            this.mShowOrderDetail.Enabled = false;
            this.mShowOrderDetail.Image = global::POS_System_Product.Properties.Resources.Jensen_Huang_Nvidia_AU2354895;
            this.mShowOrderDetail.Name = "mShowOrderDetail";
            this.mShowOrderDetail.ShortcutKeys = ((System.Windows.Forms.Keys)((System.Windows.Forms.Keys.Control | System.Windows.Forms.Keys.O)));
            this.mShowOrderDetail.Size = new System.Drawing.Size(330, 32);
            this.mShowOrderDetail.Text = "Show Order Detail";
            this.mShowOrderDetail.Click += new System.EventHandler(this.mShowOrderDetail_Click);
            // 
            // mPayment
            // 
            this.mPayment.Enabled = false;
            this.mPayment.Image = global::POS_System_Product.Properties.Resources.photo_2025_01_20_09_17_04;
            this.mPayment.Name = "mPayment";
            this.mPayment.ShortcutKeys = ((System.Windows.Forms.Keys)((System.Windows.Forms.Keys.Control | System.Windows.Forms.Keys.P)));
            this.mPayment.Size = new System.Drawing.Size(330, 32);
            this.mPayment.Text = "Payment";
            this.mPayment.Click += new System.EventHandler(this.mPayment_Click);
            // 
            // mReload
            // 
            this.mReload.Image = global::POS_System_Product.Properties.Resources.photo_2025_01_20_09_17_04;
            this.mReload.Name = "mReload";
            this.mReload.Size = new System.Drawing.Size(330, 32);
            this.mReload.Text = "Reload";
            this.mReload.Click += new System.EventHandler(this.mReload_Click);
            // 
            // mExit
            // 
            this.mExit.Image = global::POS_System_Product.Properties.Resources.Jensen_Huang_Nvidia_AU2354895;
            this.mExit.Name = "mExit";
            this.mExit.ShortcutKeys = ((System.Windows.Forms.Keys)((System.Windows.Forms.Keys.Control | System.Windows.Forms.Keys.X)));
            this.mExit.Size = new System.Drawing.Size(330, 32);
            this.mExit.Text = "Exit";
            this.mExit.Click += new System.EventHandler(this.exToolStripMenuItem_Click);
            // 
            // printDialog
            // 
            this.printDialog.Document = this.printDocument;
            this.printDialog.UseEXDialog = true;
            // 
            // printDocument
            // 
            this.printDocument.PrintPage += new System.Drawing.Printing.PrintPageEventHandler(this.printDocument_PrintPage);
            // 
            // printPreviewDialog
            // 
            this.printPreviewDialog.AutoScrollMargin = new System.Drawing.Size(0, 0);
            this.printPreviewDialog.AutoScrollMinSize = new System.Drawing.Size(0, 0);
            this.printPreviewDialog.ClientSize = new System.Drawing.Size(400, 300);
            this.printPreviewDialog.Document = this.printDocument;
            this.printPreviewDialog.Enabled = true;
            this.printPreviewDialog.Icon = ((System.Drawing.Icon)(resources.GetObject("printPreviewDialog.Icon")));
            this.printPreviewDialog.Name = "printPreviewDialog";
            this.printPreviewDialog.Visible = false;
            // 
            // tabControl1
            // 
            this.tabControl1.Controls.Add(this.tabPage1);
            this.tabControl1.Controls.Add(this.tabPage2);
            this.tabControl1.Controls.Add(this.tabPage3);
            this.tabControl1.Controls.Add(this.tabPage4);
            this.tabControl1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tabControl1.Location = new System.Drawing.Point(0, 36);
            this.tabControl1.Name = "tabControl1";
            this.tabControl1.SelectedIndex = 0;
            this.tabControl1.Size = new System.Drawing.Size(1165, 322);
            this.tabControl1.TabIndex = 2;
            // 
            // tabPage1
            // 
            this.tabPage1.Controls.Add(this.tableLayoutPanelFood);
            this.tabPage1.Location = new System.Drawing.Point(4, 40);
            this.tabPage1.Name = "tabPage1";
            this.tabPage1.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage1.Size = new System.Drawing.Size(1157, 278);
            this.tabPage1.TabIndex = 0;
            this.tabPage1.Text = "Food";
            this.tabPage1.UseVisualStyleBackColor = true;
            // 
            // tableLayoutPanelFood
            // 
            this.tableLayoutPanelFood.AutoScroll = true;
            this.tableLayoutPanelFood.ColumnCount = 6;
            this.tableLayoutPanelFood.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 50F));
            this.tableLayoutPanelFood.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 260F));
            this.tableLayoutPanelFood.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 260F));
            this.tableLayoutPanelFood.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 260F));
            this.tableLayoutPanelFood.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 260F));
            this.tableLayoutPanelFood.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 50F));
            this.tableLayoutPanelFood.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tableLayoutPanelFood.Location = new System.Drawing.Point(3, 3);
            this.tableLayoutPanelFood.Name = "tableLayoutPanelFood";
            this.tableLayoutPanelFood.RowCount = 2;
            this.tableLayoutPanelFood.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 421F));
            this.tableLayoutPanelFood.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 421F));
            this.tableLayoutPanelFood.Size = new System.Drawing.Size(1151, 272);
            this.tableLayoutPanelFood.TabIndex = 1;
            this.tableLayoutPanelFood.Paint += new System.Windows.Forms.PaintEventHandler(this.tableLayoutPanelFood_Paint);
            // 
            // tabPage2
            // 
            this.tabPage2.Controls.Add(this.tableLayoutPanelDrink);
            this.tabPage2.Location = new System.Drawing.Point(4, 40);
            this.tabPage2.Name = "tabPage2";
            this.tabPage2.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage2.Size = new System.Drawing.Size(1157, 278);
            this.tabPage2.TabIndex = 1;
            this.tabPage2.Text = "Drink";
            this.tabPage2.UseVisualStyleBackColor = true;
            // 
            // tableLayoutPanelDrink
            // 
            this.tableLayoutPanelDrink.AutoScroll = true;
            this.tableLayoutPanelDrink.ColumnCount = 6;
            this.tableLayoutPanelDrink.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 50F));
            this.tableLayoutPanelDrink.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 260F));
            this.tableLayoutPanelDrink.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 260F));
            this.tableLayoutPanelDrink.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 260F));
            this.tableLayoutPanelDrink.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 260F));
            this.tableLayoutPanelDrink.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 50F));
            this.tableLayoutPanelDrink.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tableLayoutPanelDrink.Location = new System.Drawing.Point(3, 3);
            this.tableLayoutPanelDrink.Name = "tableLayoutPanelDrink";
            this.tableLayoutPanelDrink.RowCount = 2;
            this.tableLayoutPanelDrink.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 421F));
            this.tableLayoutPanelDrink.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 421F));
            this.tableLayoutPanelDrink.Size = new System.Drawing.Size(1151, 272);
            this.tableLayoutPanelDrink.TabIndex = 1;
            this.tableLayoutPanelDrink.Paint += new System.Windows.Forms.PaintEventHandler(this.tableLayoutPanelDrink_Paint);
            // 
            // tabPage3
            // 
            this.tabPage3.Controls.Add(this.tableLayoutPanelBeer);
            this.tabPage3.Location = new System.Drawing.Point(4, 40);
            this.tabPage3.Name = "tabPage3";
            this.tabPage3.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage3.Size = new System.Drawing.Size(1157, 278);
            this.tabPage3.TabIndex = 2;
            this.tabPage3.Text = "Beer";
            this.tabPage3.UseVisualStyleBackColor = true;
            // 
            // tableLayoutPanelBeer
            // 
            this.tableLayoutPanelBeer.AutoScroll = true;
            this.tableLayoutPanelBeer.ColumnCount = 6;
            this.tableLayoutPanelBeer.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 50F));
            this.tableLayoutPanelBeer.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 260F));
            this.tableLayoutPanelBeer.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 260F));
            this.tableLayoutPanelBeer.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 260F));
            this.tableLayoutPanelBeer.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 260F));
            this.tableLayoutPanelBeer.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 50F));
            this.tableLayoutPanelBeer.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tableLayoutPanelBeer.Location = new System.Drawing.Point(3, 3);
            this.tableLayoutPanelBeer.Name = "tableLayoutPanelBeer";
            this.tableLayoutPanelBeer.RowCount = 2;
            this.tableLayoutPanelBeer.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 421F));
            this.tableLayoutPanelBeer.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 421F));
            this.tableLayoutPanelBeer.Size = new System.Drawing.Size(1151, 272);
            this.tableLayoutPanelBeer.TabIndex = 1;
            this.tableLayoutPanelBeer.Paint += new System.Windows.Forms.PaintEventHandler(this.tableLayoutPanelBeer_Paint);
            // 
            // tabPage4
            // 
            this.tabPage4.Controls.Add(this.tableLayoutPanel_Item);
            this.tabPage4.Location = new System.Drawing.Point(4, 40);
            this.tabPage4.Name = "tabPage4";
            this.tabPage4.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage4.Size = new System.Drawing.Size(1157, 278);
            this.tabPage4.TabIndex = 3;
            this.tabPage4.Text = "Item";
            this.tabPage4.UseVisualStyleBackColor = true;
            // 
            // tableLayoutPanel_Item
            // 
            this.tableLayoutPanel_Item.AutoScroll = true;
            this.tableLayoutPanel_Item.ColumnCount = 6;
            this.tableLayoutPanel_Item.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 50F));
            this.tableLayoutPanel_Item.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 260F));
            this.tableLayoutPanel_Item.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 260F));
            this.tableLayoutPanel_Item.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 260F));
            this.tableLayoutPanel_Item.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 260F));
            this.tableLayoutPanel_Item.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 50F));
            this.tableLayoutPanel_Item.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tableLayoutPanel_Item.Location = new System.Drawing.Point(3, 3);
            this.tableLayoutPanel_Item.Name = "tableLayoutPanel_Item";
            this.tableLayoutPanel_Item.RowCount = 2;
            this.tableLayoutPanel_Item.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 421F));
            this.tableLayoutPanel_Item.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 421F));
            this.tableLayoutPanel_Item.Size = new System.Drawing.Size(1151, 272);
            this.tableLayoutPanel_Item.TabIndex = 1;
            this.tableLayoutPanel_Item.Paint += new System.Windows.Forms.PaintEventHandler(this.tableLayoutPanel_Item_Paint);
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(16F, 31F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1165, 358);
            this.Controls.Add(this.tabControl1);
            this.Controls.Add(this.menuStrip1);
            this.Font = new System.Drawing.Font("Microsoft Sans Serif", 16F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.MainMenuStrip = this.menuStrip1;
            this.Margin = new System.Windows.Forms.Padding(6);
            this.Name = "Form1";
            this.Text = "Main Products";
            this.WindowState = System.Windows.Forms.FormWindowState.Maximized;
            this.menuStrip1.ResumeLayout(false);
            this.menuStrip1.PerformLayout();
            this.tabControl1.ResumeLayout(false);
            this.tabPage1.ResumeLayout(false);
            this.tabPage2.ResumeLayout(false);
            this.tabPage3.ResumeLayout(false);
            this.tabPage4.ResumeLayout(false);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion
        private System.Windows.Forms.MenuStrip menuStrip1;
        private System.Windows.Forms.ToolStripMenuItem fileToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem mShowOrderDetail;
        private System.Windows.Forms.ToolStripMenuItem mPayment;
        private System.Windows.Forms.ToolStripMenuItem mReload;
        private System.Windows.Forms.ToolStripMenuItem mExit;
        private System.Windows.Forms.PrintDialog printDialog;
        private System.Drawing.Printing.PrintDocument printDocument;
        private System.Windows.Forms.PrintPreviewDialog printPreviewDialog;
        private System.Windows.Forms.TabControl tabControl1;
        private System.Windows.Forms.TabPage tabPage1;
        private System.Windows.Forms.TableLayoutPanel tableLayoutPanelFood;
        private System.Windows.Forms.TabPage tabPage2;
        private System.Windows.Forms.TableLayoutPanel tableLayoutPanelDrink;
        private System.Windows.Forms.TabPage tabPage3;
        private System.Windows.Forms.TableLayoutPanel tableLayoutPanelBeer;
        private System.Windows.Forms.TabPage tabPage4;
        private System.Windows.Forms.TableLayoutPanel tableLayoutPanel_Item;
    }
}

