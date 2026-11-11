using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Customize_TextBox
{
    public class NumberBox : TextBox
    {
        public NumberBox()
        {
            this.KeyPress += new KeyPressEventHandler(ThisKeyPress);
        }

        private void ThisKeyPress(object sender, KeyPressEventArgs e)
        {
            if(e.KeyChar != 8)
                if (!char.IsDigit(e.KeyChar))
                {
                    e.Handled = true;
                }
                else
                {
                    string text = this.Text;
                    if(Digit >= 0)
                    {
                        if(text.Length >= Digit)
                        {
                            e.Handled = true;
                        }
                    }
                }
        }

        public int Digit { get; set; } = -1;

    }
}
