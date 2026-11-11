using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Customize_TextBox
{
    public class LetterBox : TextBox
    {
        public LetterBox()
        {
            this.KeyPress += new KeyPressEventHandler(ThisKeyPress);
        }

        private void ThisKeyPress(object sender, KeyPressEventArgs e)
        {
            if(e.KeyChar != 8)
            {
                if(!char.IsLetter(e.KeyChar) && !char.IsWhiteSpace(e.KeyChar))
                {
                    e.Handled = true;
                }
                else
                {
                    if (Length >= 0)
                    {
                        string text = this.Text;
                        if (text.Length >= Length)
                        {
                            e.Handled = true;
                        }
                    }
                }
            }
        }

        public int Length { get; set; } = -1;

    }
}
