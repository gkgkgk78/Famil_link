using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Runtime.CompilerServices;
using System.Text;
using System.Threading.Tasks;

namespace FamilLinkProject.Model
{


    public class ContentBindingModel : NotifyPropertyChanged
    {
        private UserControl _page;
        public UserControl Page
        {
            get
            {
                return _page;
            }
            set
            {
                _page = value;
                OnPropertyChanged("Page");
            }
        }

        private static ContentBindingModel instance;

        public static ContentBindingModel GetInstance()
        {
            if (instance == null)
                instance = new ContentBindingModel();

            return instance;
        }
    }
}
