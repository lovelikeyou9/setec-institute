using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace POS_SQLServer
{
    public partial class LogInForm: Form
    {
        public LogInForm()
        {
            InitializeComponent();
        }

        private void LogInForm_Load(object sender, EventArgs e)
        {
            comboAuthentication.SelectedIndex = 0;
        }

        private void comboAuthentication_SelectedIndexChanged(object sender, EventArgs e)
        {
            int index = comboAuthentication.SelectedIndex;
            if(index == 0)
            {
                txtUserName.Enabled = false;
                txtPassword.Enabled = false;
            }
            else
            {
                txtUserName.Enabled = true;
                txtPassword.Enabled = true;
            }
        }

        private void btnLogIn_Click(object sender, EventArgs e)
        {
            string server = txtIP.Text.Trim();
            string dbName = "dbsv2";
            string user = txtUserName.Text.Trim();
            string pass = txtPassword.Text;
            int index = comboAuthentication.SelectedIndex;
            try
            {
                if (index == 0)
                {
                    DBConnection.ConnectionString(server, dbName);
                }
                else
                {
                    DBConnection.ConnectionString(server, dbName, user, pass);
                }
                this.DialogResult = DialogResult.OK;
                // ADD THIS LINE BELOW:
                this.Close();  // <-- close the form here to return DialogResult.OK
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }


        private void LogInForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            if(DialogResult != DialogResult.OK)
            {
                Application.Exit();
            }
        }
    }
}
