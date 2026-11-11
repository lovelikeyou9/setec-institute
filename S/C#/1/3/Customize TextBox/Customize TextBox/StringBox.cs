using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Customize_TextBox
{
    public class StringBox : TextBox
    {
        public StringBox()
        {
            this.KeyPress += new KeyPressEventHandler(ThisKeyPress);
        }

        private void ThisKeyPress(object sender, KeyPressEventArgs e)
        {
            if (e.KeyChar != 8)
            {
                string text = this.Text;
                if (Length >= 0)
                {
                    if(text.Length >= Length)
                    {
                        e.Handled = true;
                    }
                }
            }
        }

        public int Length { get; set; } = -1;
    }
}
