using FamilLinkProject.Model.Service;
using System;
using System.Windows;

namespace FamilLinkProject
{
    /// <summary>
    /// App.xaml에 대한 상호 작용 논리
    /// </summary>
    public partial class App : Application
    {
        protected override async void OnStartup(StartupEventArgs e)
        {
            try
            {
                await MQTTService.Connected();
                await MQTTService.OnMessage();

                await MQTTService.Publish("/local/qr/", "1");
                base.OnStartup(e);
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
    }
}
