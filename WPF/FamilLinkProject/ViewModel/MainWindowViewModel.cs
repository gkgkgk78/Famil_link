using FamilLinkProject.Core;
using FamilLinkProject.Model.Service;
using FamilLinkProject.View.Page;
using FamilLinkProject.ViewModel.Page;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Threading;

namespace FamilLinkProject.ViewModel
{
    public class MainWindowViewModel
    {
        public MainWindowViewModel()
        {
            MQTTService.addObserver("/local/face/result/", recv);
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
                    Console.WriteLine(json["image"]);
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
    }
}
