using FamilLinkProject.Model.Service;
using FamilLinkProject.View.Page;
using FamilLinkProject.ViewModel;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Windows.Threading;
using System.Xml.Linq;

namespace FamilLinkProject
{
    /// <summary>
    /// MainWindow.xaml에 대한 상호 작용 논리
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();
            ContentPage.DataContext = ContentBindingModel.GetInstance();
            DataContext = MainWindowViewModel.GetInstance();

            ContentBindingModel.GetInstance().Page = new QRLogin();
            DispatcherTimer timer = new DispatcherTimer();
            timer.Interval = TimeSpan.FromMilliseconds(50);
            timer.Tick += Timer_Tick;
            timer.Start();

            DispatcherTimer timer1 = new DispatcherTimer();
            timer1.Interval = new TimeSpan(0, 10, 0);
            timer1.Tick += Weather_Tick;
            timer1.Start();

            Weather_Tick(null, null);
        }

        private void Timer_Tick(object sender, EventArgs e)
        {
            MainWindowViewModel.GetInstance().TimeTextBlock = DateTime.Now.ToString();
        }

        private void Weather_Tick(object sender, EventArgs e)
        {
            String strUrl = "http://www.kma.go.kr/weather/forecast/mid-term-xml.jsp";

            UriBuilder ub = new UriBuilder(strUrl);
            ub.Query = "srnLd=109";

            HttpWebRequest request;
            request = HttpWebRequest.Create(ub.Uri) as HttpWebRequest;
            request.BeginGetResponse(new AsyncCallback(GetResponse), request);
        }

        private void GetResponse(IAsyncResult ar)
        {
            HttpWebRequest wr = (HttpWebRequest)ar.AsyncState;
            HttpWebResponse wp = (HttpWebResponse)wr.EndGetResponse(ar);

            Stream stream = wp.GetResponseStream();
            StreamReader reader = new StreamReader(stream);

            String strRead = reader.ReadToEnd();

            XElement xmlMain = XElement.Parse(strRead);
            XElement xmlBody = xmlMain.Descendants("body").First();
            XElement xmlLocation = xmlBody.Descendants("location").First();

            String strContent = xmlLocation.Element("data").Value;

            MainWindowViewModel.GetInstance().WeatherTextBlock = strContent;
        }
    }
}

