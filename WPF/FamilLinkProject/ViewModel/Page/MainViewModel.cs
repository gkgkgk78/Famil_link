using FamilLinkProject.Core;
using FamilLinkProject.Core.domain;
using FamilLinkProject.Model.Service;
using FamilLinkProject.ViewModel.util;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Threading;

namespace FamilLinkProject.ViewModel.Page
{
    public class MainViewModel : NotifyPropertyChanged
    {
        private static MainViewModel instance;

        public static MainViewModel GetInstnace()
        {
            if (instance == null)
                instance = new MainViewModel();
            return instance;
        }

        private string _textBlockBody = "얼굴인식중입니다.";
        public string TextBlockBody
        {
            get
            {
                return _textBlockBody;
            }
            set
            {
                _textBlockBody = value;
                OnPropertyChanged("TextBlockBody");
            }
        }

        public MainViewModel()
        {
            MQTTService.Subscribe("/local/face/result/");

            APIService.API("/account/member-list", "GET", null, AccountData.Token,
                        (url, _json) =>
                        {
                            if (url.Equals("/account/member-list"))
                            {
                                List<MemberDTO> temp = new List<MemberDTO>();
                                Console.WriteLine("MainVM : 리스트 조회에 성공하였습니다.");
                                foreach (var item in _json["list"])
                                {
                                    temp.Add(new MemberDTO()
                                    {
                                        Uid = long.Parse(item["uid"].ToString()),
                                        Name = item["name"].ToString()
                                    });
                                }

                                FaceDectionData.setMembers(temp);
                            }
                        }
                    );

        }

        ~MainViewModel()
        {
            MQTTService.UnSubscribe("/local/face/result/");

        }
    }
}
