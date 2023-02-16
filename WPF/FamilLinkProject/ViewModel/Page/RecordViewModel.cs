using FamilLinkProject.Core;
using FamilLinkProject.Model.Service;
using FamilLinkProject.View.Page;
using FamilLinkProject.ViewModel.util;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Threading;
using System.Xml.Linq;

namespace FamilLinkProject.ViewModel.Page
{
    public class RecordViewModel : NotifyPropertyChanged
    {
        private static bool isRecord = false;
        private static bool listen = false;

        private EventHandler timerEvent;
        private DispatcherTimer timer = new DispatcherTimer();    //객체생성

        private static RecordViewModel instance;
        public static RecordViewModel GetInstance()
        {
            if (instance == null)
            {
                instance = new RecordViewModel();
            }
            JObject _payload = new JObject();
            _payload.Add("msg", "이름을 말씀해주세요.");
            instance.TimeTextBlock = 0;
            listen = true;
            MQTTService.Publish("/local/tts/", _payload.ToString());
            return instance;
        }

        public RecordViewModel()
        {
            MQTTService.addObserver("/local/sound/", recv);
            timerEvent = new EventHandler(timer_Tick);
            
        }

        public void recv(String topic, String message)
        {
            if (topic.Equals("/local/sound/"))
            {
                JObject json = JObject.Parse(message);
                string name = json["text"].ToString();
                if (FaceDectionData.getReverseMembers().ContainsKey(name))
                {
                    long from = MemberData.Uid;
                    long to = FaceDectionData.getReverseMembers()[name];

                    JObject payload = new JObject();
                    payload.Add("from", from);
                    payload.Add("to", to);
                    payload.Add("token", MemberData.Token);

                    // 토큰 설정
                    MQTTService.Publish("/local/token/", payload.ToString());

                    // 녹화시작
                    MQTTService.Publish("/local/record/", "1");

                    JObject _payload = new JObject();
                    _payload.Add("msg", "녹화를 시작합니다");
                    MQTTService.Publish("/local/tts/", _payload.ToString());

                    MQTTService.UnSubscribe("/local/face/result/");
                    isRecord = true;
                    listen = false;

                    timer.Interval = TimeSpan.FromMilliseconds(1000);    //시간간격 설정
                    timer.Tick += timerEvent;          //이벤트 추가
                    timer.Start();
                }
                else
                {
                    if (!isRecord && listen)
                    {
                        JObject _payload = new JObject();
                        _payload.Add("msg", "다시 한 번 말씀해주세요.");
                        MQTTService.Publish("/local/tts/", _payload.ToString());
                    }
                }
            }
        }

        private void timer_Tick(object sender, EventArgs e)
        {
            TimeTextBlock += 1;
            if (TimeTextBlock >= 15)
            {
                timer.Tick -= timerEvent;
                // 녹화 중지
                MQTTService.Publish("/local/record/", "0");
                JObject _payload = new JObject();
                _payload.Add("msg", "녹화를 종료합니다");
                MQTTService.Publish("/local/tts/", _payload.ToString());
                MQTTService.Subscribe("/local/face/result/");
                isRecord = false;
                Application.Current.Dispatcher.InvokeAsync(delegate
                {
                    ContentBindingModel.GetInstance().Page = new Main();
                }, DispatcherPriority.Loaded);

            }
        }


        private long _timeTextBlock = 0;
        public long TimeTextBlock
        {
            get
            {
                return _timeTextBlock;
            }
            set
            {
                _timeTextBlock = value;
                OnPropertyChanged("TimeTextBlock");
            }
        }

    }
}
