using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Customize_TextBox
{
    public class FloatingBox : TextBox
    {
        public FloatingBox()
        {
            this.KeyPress += new KeyPressEventHandler(ThisKeyPress);
        }

        private void ThisKeyPress(object sender, KeyPressEventArgs e)
        {
            if(e.KeyChar != 8)
            {
                string text = this.Text;
                if(e.KeyChar == '.')
                {
                    if(text.Length == 0)
                    {
                        e.Handled = true;
                        this.Text = "0.";
                        this.SelectionStart = 2;
                    }
                    else
                    {
                        if (text.Contains("."))
                        {
                            e.Handled = true;
                        }
                    }
                }
                else
                {
                    if (!char.IsDigit(e.KeyChar))
                    {
                        e.Handled = true;
                    }
                }
            }
        }
    }
}
