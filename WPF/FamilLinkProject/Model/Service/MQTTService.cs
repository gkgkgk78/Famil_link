using MQTTnet.Client;
using System;
using System.Text;
using System.Threading.Tasks;
using MQTTnet.Diagnostics;
using MQTTnet.Internal;
using MQTTnet.Protocol;
using MQTTnet;
using System.Collections.Generic;

namespace FamilLinkProject.Model.Service
{
    public delegate void OnMessage(String topic, String message);

    public class MQTTService
    {
        // topic, OnMessage
        private static Dictionary<String, OnMessage> observer = new Dictionary<string, OnMessage>();

        private static bool isConnected = false;

        private static MqttNetEventLogger logger = new MqttNetEventLogger();

        private static MqttFactory factory = new MqttFactory(logger);

        private static IMqttClient client = factory.CreateMqttClient();

        private static MqttClientOptions clientOptions = new MqttClientOptions
        {
            ChannelOptions = new MqttClientTcpOptions
            {
                Server = "localhost"
            }
        };  

        public static async Task run()
        {
            try
            {
                await Connected();
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex);
            }
        }
        public static void addObserver(String topic, OnMessage onMessage)
        {
            if (!observer.ContainsKey(topic))
            {
                // 토픽이 존재하지 않으면 새로 만들어주고
                observer.Add(topic, onMessage);
            }
            else
            {
                // 이미 존재하면 더해줌
                observer[topic] += onMessage;
            }
        }

        public static void removeObserver(String topic, OnMessage onMessage)
        {
            if (!observer.ContainsKey(topic))
            {
                return;
            }
            else
            {
                // 이미 존재하면 삭제해줌
                observer[topic] -= onMessage;
            }
        }
        
        public static async Task OnMessage()
        {
            try
            {
                client.ApplicationMessageReceivedAsync += e =>
                {
                    string topic = e.ApplicationMessage.Topic;
                    string message = Encoding.UTF8.GetString(e.ApplicationMessage.Payload);
                    Console.WriteLine("### RECEIVED APPLICATION MESSAGE ###");

                    if (observer.ContainsKey(topic))
                    {
                        observer[topic](topic, message);
                    }
                    return CompletedTask.Instance;
                };
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        public static async Task Subscribe(String topic)
        {
            if (!isConnected)
                throw new Exception("MQTT 연결을 확인해주세요.");

            await client.SubscribeAsync(topic);
            Console.WriteLine("구독완료 : " + topic);
        }

        public static async Task UnSubscribe(String topic)
        {
            if (!isConnected)
                throw new Exception("MQTT 연결을 확인해주세요.");

            await client.UnsubscribeAsync(topic);
            Console.WriteLine("구독 취소 완료 : " + topic);
        }
        public static async Task Publish(String topic, String body)
        {
            if (!isConnected)
                throw new Exception("MQTT 연결을 확인해주세요.");

            var applicationMessage = new MqttApplicationMessageBuilder()
                .WithTopic(topic)
                .WithPayload(body)
                .WithQualityOfServiceLevel(MqttQualityOfServiceLevel.ExactlyOnce)
                .Build();

            await client.PublishAsync(applicationMessage);
        }


        public static async Task Connected()
        {
            if (isConnected)
                throw new Exception("이미 연결이 되어있습니다.");

            client.ConnectedAsync += async e =>
            {
                isConnected = true;
                Console.WriteLine("MQTT 연결완료");
            };

            try
            {
                await client.ConnectAsync(clientOptions);
            }
            catch (Exception exception)
            {
                Console.WriteLine("### CONNECTING FAILED ###" + Environment.NewLine + exception);
                throw exception;
            }
        }
        public static async Task DisConnected()
        {
            if (isConnected)
                throw new Exception("이미 연결되어있지 않습니다.");

            client.DisconnectedAsync += async e =>
            {
                Console.WriteLine("### DISCONNECTED FROM SERVER ###");
                await Task.Delay(TimeSpan.FromSeconds(5));
                try
                {
                    await client.ConnectAsync(clientOptions);
                }
                catch(Exception exception)
                {
                    Console.WriteLine("### RECONNECTING FAILED ###");
                    throw exception;
                }
            };
        }
    }
}
