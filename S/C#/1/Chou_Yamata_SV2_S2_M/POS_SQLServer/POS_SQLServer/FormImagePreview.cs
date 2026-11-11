using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using static System.Net.Mime.MediaTypeNames;

namespace POS_SQLServer
{
    public partial class FormImagePreview : Form
    {
        public FormImagePreview(System.Drawing.Image image)
        {

            InitializeComponent();

            pbPreview.Image = image;

        }
    }
}
