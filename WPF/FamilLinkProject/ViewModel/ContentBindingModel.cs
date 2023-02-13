using FamilLinkProject.View.Page;
using FamilLinkProject.ViewModel.util;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Controls;

namespace FamilLinkProject.ViewModel
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
