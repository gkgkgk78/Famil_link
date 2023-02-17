using FamilLinkProject.Core;
using FamilLinkProject.Model.Service;
using FamilLinkProject.ViewModel;
using FamilLinkProject.ViewModel.Page;
using Newtonsoft.Json.Linq;
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
using System.Xml.Linq;

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
                  (_url, _stream) =>
                  {
                      if (_url.Equals("/movie/" + movie_uid))
                      {
                          _stream.Close();

                          movie.Source = new Uri(@"record.mp4", UriKind.Relative);
                          try
                          {
                              APIService.API("/movie/" + movie_uid, "PUT", null, MemberData.Token,
                                   (_url1, _json1) =>
                                   {
                                       if (_url.Equals("/movie/" + movie_uid))
                                       {
                                           if ((bool)_json1["result"])
                                           {
                                               movie.Play();
                                           }
                                       }
                                   }
                               );
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
                // 소통유도
                APIService.API("/movie/allmovie",
                    "GET",
                    null,
                    MemberData.Token,
                    (_url2, _json2) =>
                    {
                        if (_json2["result"].ToString().Equals("True"))
                        {
                            JObject payload = new JObject();
                            payload.Add("msg", MemberData.Name + " 님 " + _json2["name"] + " 님과 연락하지 않은지 " + _json2["date"] + " 일이 지났습니다.");
                            MQTTService.Publish("/local/tts/", payload.ToString());
                        }
                    });
            }
        }
    }
}
