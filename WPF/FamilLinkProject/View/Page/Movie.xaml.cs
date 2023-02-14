using FamilLinkProject.Core;
using FamilLinkProject.Model.Service;
using FamilLinkProject.ViewModel;
using FamilLinkProject.ViewModel.Page;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Windows.Threading;

namespace FamilLinkProject.View.Page
{
    /// <summary>
    /// Movie.xaml에 대한 상호 작용 논리
    /// </summary>
    public partial class Movie : UserControl
    {
        public Movie()
        {
            InitializeComponent();
            DataContext = MovieViewModel.GetInstance();

            movieService();
        }

        bool movieService()
        {
            var movie_uid = MovieData.popMovie();
            if (movie_uid == -1)
                return false;

            APIService.MovieAPI("/movie/" + movie_uid, "GET", null, MemberData.Token,
                 async (_url, _stream) =>
                  {
                      if (_url.Equals("/movie/" + movie_uid))
                      {
                          _stream.Close();

                          movie.Source = new Uri(@"record.mp4", UriKind.Relative);
                          try
                          {
                              movie.Play();


                          }
                          catch (Exception ex)
                          {
                              Console.WriteLine(ex.ToString());
                          }
                      }
                  }
              );
            return true;
        }

        private void movie_MediaEnded(object sender, RoutedEventArgs e)
        {
            movie.Source = null;
            if (!movieService())
            {
                ContentBindingModel.GetInstance().Page = new Main();
            }
        }
    }
}
