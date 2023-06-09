﻿using FamilLinkProject.Core;
using FamilLinkProject.Core.domain;
using FamilLinkProject.Model.Service;
using FamilLinkProject.View.Page;
using FamilLinkProject.ViewModel.Page;
using FamilLinkProject.ViewModel.util;
using FamilLinkProject.ViewModel.Util;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Policy;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Input;
using System.Windows.Threading;
using System.Xml.Linq;

namespace FamilLinkProject.ViewModel
{
    public class MainWindowViewModel : NotifyPropertyChanged
    {
        private static MainWindowViewModel instance;

        public static MainWindowViewModel GetInstance()
        {
            if (instance == null)
                instance = new MainWindowViewModel();

            return instance;
        }
        public MainWindowViewModel()
        {
            MQTTService.addObserver("/local/face/result/", recv);
            LogoutCommand = new Command(LogoutButton, null);
            CJWCommand = new Command(CJWButton, null);
            JWJCommand = new Command(JWJButton, null);
            CDHCommand = new Command(CDHButton, null);
        }

        ~MainWindowViewModel()
        {
            MQTTService.removeObserver("/local/face/result/", recv);
        }

        public void recv(String topic, String message)
        {
            if (topic.Equals("/local/face/result/"))
            {
                JObject json = JObject.Parse(message);

                string myname = json["name"].ToString();

                FaceDectionData.add(myname);
                var member_uid = FaceDectionData.getDetectionUid();
                if (member_uid >= 0)
                {
                    if (member_uid == 0)
                    {
                        // 0이 반환되는 경우는 인식이 애매할 때
                        return;
                    }
                    if (MemberData.Uid > 0)
                    {
                        // 이미 로그인이 되어있는 경우
                        return;
                    }

                    JObject payload = new JObject();
                    payload.Add("uid", member_uid);
                    payload.Add("json", json["image"]);
                    APIService.API("/member/login", "POST", payload, AccountData.Token,
                        (_url, _json) =>
                        {
                            if (_url.Equals("/member/login"))
                            {
                                if (_json["result"].ToString().Equals("True"))
                                {
                                    MemberData.Uid = long.Parse(_json["uid"].ToString());
                                    MemberData.Token = _json["access-token"].ToString();
                                    Application.Current.Dispatcher.InvokeAsync(delegate
                                    {
                                        MainViewModel.GetInstnace().TextBlockBody = myname + "님 환영합니다.";
                                    }, DispatcherPriority.Loaded);
                                    Console.WriteLine("MainWIndowVM : 멤버 로그인에 성공하였습니다.");

                                    MemberData.Name = myname;

                                    APIService.API("/movie/video-list", "GET", payload, MemberData.Token,
                                         (_url1, _json1) =>
                                         {
                                             if (_url1.Equals("/movie/video-list"))
                                             {
                                                 //if (_json1["result"].ToString().Equals("True"))
                                                 //{

                                                 //    Console.WriteLine("dd");

                                                 //}
                                                 if (_json1.ContainsKey("movie-list"))
                                                 {
                                                     List<long> temp = new List<long>();
                                                     foreach (var item in _json1["movie-list"])
                                                     {
                                                         temp.Add(long.Parse(item["uid"].ToString()));
                                                     }
                                                     MovieData.setMovieList(temp);

                                                     Application.Current.Dispatcher.InvokeAsync(delegate
                                                     {
                                                         ContentBindingModel.GetInstance().Page = new Movie();
                                                     }, DispatcherPriority.Loaded);
                                                 }
                                                 else
                                                 {
                                                     // 소통유도
                                                     APIService.API("/movie/allmovie", "GET", null, MemberData.Token,
                                                         (_url2, _json2) =>
                                                         {
                                                             if (_json2["result"].ToString().Equals("True"))
                                                             {
                                                                 payload = new JObject();

                                                                 if (int.Parse(_json2["date"].ToString()) == -1)
                                                                 {
                                                                     // date가 -1이면 한번도 안보낸거
                                                                     payload.Add("msg", myname + "님 " + _json2["name"] + "님과 한번도 연락하지 않으셨네요. 연락해보시는건 어떠신가요?");
                                                                 }
                                                                 else
                                                                 {
                                                                     payload.Add("msg", myname + "님 " + _json2["name"] + " 님과 연락하지 않은지 " + _json2["date"] + " 일이 지났습니다.");
                                                                 }
                                                                 MQTTService.Publish("/local/tts/", payload.ToString());
                                                             }
                                                         });

                                                 }
                                                 Console.WriteLine("MainWIndowVM : 멤버 로그인에 성공하였습니다.");

                                             }

                                             JObject _payload = new JObject();
                                             _payload.Add("msg", myname + "님 환영합니다");
                                             MQTTService.Publish("/local/tts/", _payload.ToString());

                                             MQTTService.Subscribe("/local/sound/");
                                             MQTTService.addObserver("/local/sound/", recv);
                                         }
                                     );



                                }
                            }
                        }
                    );
                }
                else
                {
                    MainViewModel.GetInstnace().TextBlockBody = "얼굴인식 중입니다.";
                    if (MemberData.Uid != -1)
                    {
                        MQTTService.UnSubscribe("/local/sound/");
                        MQTTService.removeObserver("/local/sound/", recv);
                        JObject _payload = new JObject();
                        _payload.Add("msg", "안녕히가세요");
                        MQTTService.Publish("/local/tts/", _payload.ToString());

                        Application.Current.Dispatcher.InvokeAsync(delegate
                        {
                            ContentBindingModel.GetInstance().Page = new Main();
                        }, DispatcherPriority.Loaded);
                    }

                    MemberData.Token = null;
                    MemberData.Uid = -1;
                }
            }
            else if (topic.Equals("/local/sound/"))
            {
                JObject json = JObject.Parse(message);
                if (json["text"].ToString().Contains("녹화"))
                {
                    Application.Current.Dispatcher.InvokeAsync(delegate
                    {
                        ContentBindingModel.GetInstance().Page = new Record();
                    }, DispatcherPriority.Loaded);

                }
            }
        }

        private string _timeTextBlock = DateTime.Now.ToString();

        public string TimeTextBlock
        {
            get
            {
                var temp = _timeTextBlock.Split(' ');
                return temp[0] + "\n" + temp[1] + temp[2];
            }

            set
            {
                _timeTextBlock = value;
                OnPropertyChanged(nameof(TimeTextBlock));
            }
        }

        private string _weatherTextBlock;

        public string WeatherTextBlock
        {
            get
            {
                return _weatherTextBlock;
            }
            set
            {
                _weatherTextBlock = value;
                OnPropertyChanged(nameof(WeatherTextBlock));
            }
        }

        public ICommand LogoutCommand
        {
            get; private set;
        }

        public ICommand CJWCommand
        {
            get; private set;
        }
        public ICommand JWJCommand
        {
            get; private set;
        }
        public ICommand CDHCommand
        {
            get; private set;
        }

        private void LogoutButton(object obj)
        {
            if (AccountData.Token == null)
            {
                Console.WriteLine("가족 로그인 필요");
                return;
            }
            MainViewModel.GetInstnace().TextBlockBody = "얼굴인식 중입니다.";
            if (MemberData.Uid != -1)
            {
                MQTTService.UnSubscribe("/local/sound/");
                MQTTService.removeObserver("/local/sound/", recv);
                JObject _payload = new JObject();
                _payload.Add("msg", "안녕히가세요");
                MQTTService.Publish("/local/tts/", _payload.ToString());

                Application.Current.Dispatcher.InvokeAsync(delegate
                {
                    ContentBindingModel.GetInstance().Page = new Main();
                }, DispatcherPriority.Loaded);
            }

            MemberData.Token = null;
            MemberData.Uid = -1;
        }

        private void LoginMember(long uid, string name) 
        {
            JObject payload = new JObject();
            payload.Add("user_uid", uid);
            payload.Add("name", name);

            APIService.API("/member/login/access", "POST", payload, AccountData.Token,
            (_url, _json) =>
            {
                           if (_url.Equals("/member/login/access"))
                           {
                               if (_json["result"].ToString().Equals("True"))
                               {
                                   MemberData.Uid = long.Parse(_json["uid"].ToString());
                                   MemberData.Token = _json["access-token"].ToString();
                                   Application.Current.Dispatcher.InvokeAsync(delegate
                                   {
                                       MainViewModel.GetInstnace().TextBlockBody = name + "님 환영합니다.";
                                   }, DispatcherPriority.Loaded);
                                   Console.WriteLine("MainWIndowVM : 멤버 로그인에 성공하였습니다.");

                                   MemberData.Name = name;

                                   APIService.API("/movie/video-list", "GET", payload, MemberData.Token,
                                        (_url1, _json1) =>
                                        {
                                            if (_url1.Equals("/movie/video-list"))
                                            {
                                                //if (_json1["result"].ToString().Equals("True"))
                                                //{

                                                //    Console.WriteLine("dd");

                                                //}
                                                if (_json1.ContainsKey("movie-list"))
                                                {
                                                    List<long> temp = new List<long>();
                                                    foreach (var item in _json1["movie-list"])
                                                    {
                                                        temp.Add(long.Parse(item["uid"].ToString()));
                                                    }
                                                    MovieData.setMovieList(temp);

                                                    Application.Current.Dispatcher.InvokeAsync(delegate
                                                    {
                                                        ContentBindingModel.GetInstance().Page = new Movie();
                                                    }, DispatcherPriority.Loaded);
                                                }
                                                else
                                                {
                                                    // 소통유도
                                                    APIService.API("/movie/allmovie", "GET", null, MemberData.Token,
                                                        (_url2, _json2) =>
                                                        {
                                                            if (_json2["result"].ToString().Equals("True"))
                                                            {
                                                                payload = new JObject();

                                                                if (int.Parse(_json2["date"].ToString()) == -1)
                                                                {
                                                                    // date가 -1이면 한번도 안보낸거
                                                                    payload.Add("msg", name + "님 " + _json2["name"] + "님과 한번도 연락하지 않으셨네요. 연락해보시는건 어떠신가요?");
                                                                }
                                                                else
                                                                {
                                                                    payload.Add("msg", name + "님 " + _json2["name"] + " 님과 연락하지 않은지 " + _json2["date"] + " 일이 지났습니다.");
                                                                }
                                                                MQTTService.Publish("/local/tts/", payload.ToString());
                                                            }
                                                        });

                                                }
                                                Console.WriteLine("MainWIndowVM : 멤버 로그인에 성공하였습니다.");

                                            }

                                            JObject _payload = new JObject();
                                            _payload.Add("msg", name + "님 환영합니다");
                                            MQTTService.Publish("/local/tts/", _payload.ToString());

                                            MQTTService.Subscribe("/local/sound/");
                                            MQTTService.addObserver("/local/sound/", recv);
                                        }
                                    );



                               }
                           }
                       }
                   );
        }

        private void CJWButton(object obj)
        {
            if (AccountData.Token == null)
            {
                Console.WriteLine("가족 로그인 필요");
                return;
            }
            Console.WriteLine("최진우 로그인");
            LoginMember(AccountData.Uid, "최진우");
                

        }
        private void JWJButton(object obj)
        {
            if (AccountData.Token == null)
            {
                Console.WriteLine("가족 로그인 필요");
                return;
            }
            Console.WriteLine("정우진 로그인");
            LoginMember(AccountData.Uid, "정우진");
        }
        private void CDHButton(object obj)
        {
            if (AccountData.Token == null)
            {
                Console.WriteLine("가족 로그인 필요");
                return;
            }
            Console.WriteLine("최다혜 로그인");
            LoginMember(AccountData.Uid, "최다혜");
        }
    }
}
