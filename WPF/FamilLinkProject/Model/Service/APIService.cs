using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;

namespace FamilLinkProject.Model.Service
{
    public delegate void Response(String url, JObject json);

    public class APIService
    {
        private static string baseUrl = "http://i8a208.p.ssafy.io:3000"; // base url

        public static void API(string url,
            string method,
            JObject payload,
            string authToken,
            Response result)
        {
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(baseUrl + url);

            request.Method = method; // method 
            request.ContentType = "application/json"; // ContentType
            if (authToken != null)
            {
                request.Headers.Add("Authorization", "Bearer " + authToken.Replace("\"", "")); // Header에 추가
            }
            if (!method.Equals("GET"))
            {
                StreamWriter reqStream = new StreamWriter(request.GetRequestStream());
                if (payload != null)
                {
                    reqStream.Write(payload);
                }
                reqStream.Close();
            }

            try
            {
                HttpWebResponse response = (HttpWebResponse)request.GetResponse(); // 요청 보내고 응답 기다리기
                StreamReader respStream = new StreamReader(response.GetResponseStream());
                string resp = respStream.ReadToEnd();   // 결과를 string으로 읽기
                respStream.Close();

                JObject ret = JObject.Parse(resp); // JObject 정적 매서드로 string을 JSON형식으로 파싱하기
                result(url, ret);
            }
            catch (Exception e)
            {
                throw e;
            }
        }
    }


}
