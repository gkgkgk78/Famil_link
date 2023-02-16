using FamilLinkProject.Core;
using FamilLinkProject.Model.Service;
using FamilLinkProject.View.Page;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Threading;

namespace FamilLinkProject.ViewModel.Page
{
    public class QRLoginViewModel
    {
        private static QRLoginViewModel instance;

        public static QRLoginViewModel GetInstance()
        {
            if(instance==null)
                instance = new QRLoginViewModel();
            return instance;
        }
        public QRLoginViewModel()
        {
            MQTTService.addObserver("/local/qrtoken/", recv);
            MQTTService.Subscribe("/local/qrtoken/");
        }

        ~QRLoginViewModel()
        {
            MQTTService.removeObserver("/local/qrtoken/", recv);
            MQTTService.UnSubscribe("/local/qrtoken/");
        }

        public void recv(String topic, String message)
        {
            Console.WriteLine("QRLoginVM recv : " + topic + " " + message);

          
            if (topic.Equals("/local/qrtoken/"))
            {
                try
                {
                    MQTTService.Publish("/local/qr/", "0");

                    APIService.API("/account/auth", "GET", null, message,
                        (url, _json) =>
                        {
                            if (url.Equals("/account/auth"))
                            {
                                if (_json["result"].ToString().Equals("True"))
                                {
                                    Console.WriteLine("QRLoginVM : 로그인에 성공하였습니다.");
                                    AccountData.Uid = long.Parse(_json["data"]["uid"].ToString());
                                    AccountData.Token = message;
                                    Application.Current.Dispatcher.InvokeAsync(delegate
                                    {
                                        ContentBindingModel.GetInstance().Page = new Main();
                                    }, DispatcherPriority.Loaded);

                                    MQTTService.Subscribe("/local/face/result/");
                                }
                            }
                            else
                            {
                                Console.WriteLine("가족계정 로그인 실패!");
                            }
                        }
                    );


                }
                catch (Exception ex)
                {
                    MQTTService.Publish("/local/qr/", "1");
                    throw ex;
                }
            }
        }
    }
}
