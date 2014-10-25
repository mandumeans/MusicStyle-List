using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Web.Services;
using System.Web.Script.Serialization;
using System.IO;
using System.Xml;
using Google.GData.Client;
using Google.GData.Extensions;
using Google.GData.YouTube;
using Google.GData.Extensions.MediaRss;
using Google.YouTube;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System.Data;

namespace MusicList
{
    public partial class MusicList : System.Web.UI.Page
    {
        public const string SOUNDCLOUD_CLIENT_KEY = "a2b4d87e3bac428d8467d6ea343d49ae";

        protected void Page_Load(object sender, EventArgs e)
        {

        }

        [WebMethod]
        public static string YoutubeURLValidation(string VID)
        {
            const string FAIL_STRING = "False";
            try
            {
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create("http://gdata.youtube.com/feeds/api/videos/" + VID);
                request.Method = "HEAD";
                using (HttpWebResponse response = request.GetResponse() as HttpWebResponse)
                {
                    if (response.StatusCode != HttpStatusCode.OK)
                    {
                        return FAIL_STRING;
                    }
                    else
                    {
                        var xmlDoc = new XmlDocument();
                        xmlDoc.Load("http://gdata.youtube.com/feeds/api/videos/" + VID);
                        XmlNodeList title = xmlDoc.GetElementsByTagName("title");
                        return (title.Item(0)).InnerText;
                    }
                }
            }
            catch(Exception ex){
                return FAIL_STRING;
            }
        }

        [WebMethod]
        public static string SoundcloudURLValidation(string URL)
        {
            const string FAIL_STRING = "False";

            string url = "http://api.soundcloud.com/resolve.json?url=" + URL + "&client_id=" + SOUNDCLOUD_CLIENT_KEY;
            try
            {
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
               using (HttpWebResponse response = (HttpWebResponse)request.GetResponse())
               {

                    if (response.StatusCode != HttpStatusCode.OK)
                    {
                        return FAIL_STRING;
                    }
                    else
                    {
                        Stream stream = response.GetResponseStream();
                        StreamReader reader = new StreamReader(stream);
                        string result = reader.ReadToEnd();
                        stream.Close();
                        response.Close();
                        JObject parsedResult = JObject.Parse(result);
                        return parsedResult["title"].ToString();
                    }
                }
            }
            catch (Exception ex)
            {
                return FAIL_STRING;
            }
        }

        public class YoutubeSendData
        {
            public string id { get; set; }
            public string title { get; set; }
            public class Thumbnail
            {
                public string sqDefault { get; set; }
                public string hqDefault { get; set; }
            }
            public Thumbnail thumbnail { get; set; }
            public string viewCount { get; set; }
            public string duration { get; set; }
        }

        [WebMethod]
        public static string YoutubeKeywordSearch(string keyword)
        {
            const string FAIL_STRING = "False";
            string url = "http://gdata.youtube.com/feeds/api/videos?q=" + keyword + "&max-results=10&alt=jsonc&v=2";
            try
            {
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
                HttpWebResponse response = (HttpWebResponse)request.GetResponse();
                Stream stream = response.GetResponseStream();
                StreamReader reader = new StreamReader(stream);
                string result = reader.ReadToEnd();
                stream.Close();
                response.Close();
                
                JObject parsedResult = JObject.Parse(result);
                JArray items = (JArray)parsedResult["data"]["items"];
                JArray dataArrayToSend = new JArray();

                foreach (JObject resultOne in items)
                {                    
                    try
                    {
                        YoutubeSendData YSD = new YoutubeSendData
                        {
                            id = resultOne["id"].ToString(),
                            title = resultOne["title"].ToString(),
                            thumbnail = new YoutubeSendData.Thumbnail
                            {
                                sqDefault = resultOne["thumbnail"]["sqDefault"].ToString(),
                                hqDefault = resultOne["thumbnail"]["hqDefault"].ToString()
                            },
                            viewCount = resultOne["viewCount"].ToString(),
                            duration = resultOne["duration"].ToString()
                        };
                        dataArrayToSend.Add(JsonConvert.SerializeObject(YSD, Newtonsoft.Json.Formatting.Indented));
                    }
                    catch (Exception e)
                    {
                    }
                }

                //StringBuilder sb = new StringBuilder();
                //JavaScriptSerializer jparser = new JavaScriptSerializer();
                //jparser.Serialize(dataArrayToSend, sb);
                return dataArrayToSend.ToString();
            }
            catch (Exception ex)
            {
                return FAIL_STRING;
            }
        }

        public class SoundcloudSendData
        {
            public string id { get; set; }
            public string title { get; set; }
            public string duration { get; set; }
            public string stream_url { get; set; }
            public string artwork_url { get; set; }
            public string playback_count { get; set; }
        }

        [WebMethod]
        public static string SoundcloudKeywordSearch(string keyword)
        {
            const string FAIL_STRING = "False";

            string url = "https://api.soundcloud.com/tracks.json?client_id=" + SOUNDCLOUD_CLIENT_KEY + "&q= " + keyword;
            try
            {
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
                HttpWebResponse response = (HttpWebResponse)request.GetResponse();
                Stream stream = response.GetResponseStream();
                StreamReader reader = new StreamReader(stream);
                string result = reader.ReadToEnd();
                stream.Close();
                response.Close();

                //JObject parsedResult = JObject.Parse(result);
                //JArray items = (JArray)parsedResult["data"]["items"];
                JArray items = JArray.Parse(result);
                JArray dataArrayToSend = new JArray();

                foreach (JObject resultOne in items)
                {
                    try
                    {
                        SoundcloudSendData SSD = new SoundcloudSendData
                        {
                            id = resultOne["id"].ToString(),
                            title = resultOne["title"].ToString(),
                            duration = resultOne["duration"].ToString(),
                            stream_url = resultOne["stream_url"].ToString(),
                            artwork_url = (resultOne["artwork_url"] == null ? "" : resultOne["artwork_url"]).ToString(),
                            playback_count = resultOne["playback_count"].ToString()
                        };
                        dataArrayToSend.Add(JsonConvert.SerializeObject(SSD, Newtonsoft.Json.Formatting.Indented));
                    }
                    catch (Exception e)
                    {
                    }
                }

                //StringBuilder sb = new StringBuilder();
                //JavaScriptSerializer jparser = new JavaScriptSerializer();
                //jparser.Serialize(dataArrayToSend, sb);
                return dataArrayToSend.ToString();
            }
            catch (Exception ex)
            {
                return FAIL_STRING;
            }
        }

    }
}
