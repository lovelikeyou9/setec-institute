namespace Customize_TextBox
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
            this.numberBox1 = new Customize_TextBox.NumberBox();
            this.label1 = new System.Windows.Forms.Label();
            this.floatingBox1 = new Customize_TextBox.FloatingBox();
            this.label2 = new System.Windows.Forms.Label();
            this.letterBox1 = new Customize_TextBox.LetterBox();
            this.label3 = new System.Windows.Forms.Label();
            this.stringBox1 = new Customize_TextBox.StringBox();
            this.label4 = new System.Windows.Forms.Label();
            this.SuspendLayout();
            // 
            // numberBox1
            // 
            this.numberBox1.Digit = 10;
            this.numberBox1.Location = new System.Drawing.Point(359, 33);
            this.numberBox1.Name = "numberBox1";
            this.numberBox1.Size = new System.Drawing.Size(497, 41);
            this.numberBox1.TabIndex = 0;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(126, 38);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(180, 36);
            this.label1.TabIndex = 1;
            this.label1.Text = "NumberBox:";
            // 
            // floatingBox1
            // 
            this.floatingBox1.Location = new System.Drawing.Point(359, 98);
            this.floatingBox1.Name = "floatingBox1";
            this.floatingBox1.Size = new System.Drawing.Size(497, 41);
            this.floatingBox1.TabIndex = 2;
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(126, 103);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(182, 36);
            this.label2.TabIndex = 3;
            this.label2.Text = "FloatingBox:";
            // 
            // letterBox1
            // 
            this.letterBox1.Length = 10;
            this.letterBox1.Location = new System.Drawing.Point(359, 167);
            this.letterBox1.Name = "letterBox1";
            this.letterBox1.Size = new System.Drawing.Size(497, 41);
            this.letterBox1.TabIndex = 4;
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(126, 172);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(150, 36);
            this.label3.TabIndex = 5;
            this.label3.Text = "LetterBox:";
            // 
            // stringBox1
            // 
            this.stringBox1.Length = 10;
            this.stringBox1.Location = new System.Drawing.Point(359, 239);
            this.stringBox1.Name = "stringBox1";
            this.stringBox1.Size = new System.Drawing.Size(497, 41);
            this.stringBox1.TabIndex = 6;
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(126, 242);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(154, 36);
            this.label4.TabIndex = 7;
            this.label4.Text = "StringBox:";
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(17F, 36F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1220, 462);
            this.Controls.Add(this.label4);
            this.Controls.Add(this.stringBox1);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.letterBox1);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.floatingBox1);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.numberBox1);
            this.Font = new System.Drawing.Font("Microsoft Sans Serif", 18F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.Margin = new System.Windows.Forms.Padding(6, 7, 6, 7);
            this.Name = "Form1";
            this.Text = "Form1";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private NumberBox numberBox1;
        private System.Windows.Forms.Label label1;
        private FloatingBox floatingBox1;
        private System.Windows.Forms.Label label2;
        private LetterBox letterBox1;
        private System.Windows.Forms.Label label3;
        private StringBox stringBox1;
        private System.Windows.Forms.Label label4;
    }
}

