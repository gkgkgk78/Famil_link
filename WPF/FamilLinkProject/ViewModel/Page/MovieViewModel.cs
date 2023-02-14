using FamilLinkProject.ViewModel.util;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Controls;

namespace FamilLinkProject.ViewModel.Page
{
    public class MovieViewModel : NotifyPropertyChanged
    {
        private static MovieViewModel instance;
        public static MovieViewModel GetInstance()
        {
            if (instance == null)
                instance = new MovieViewModel();

            return instance;
        }

        private Uri _movie;

        public Uri Movie
        {
            get { return _movie; }
            set
            {
                _movie = value;
                OnPropertyChanged("Movie");
            }
        }

    }
}
