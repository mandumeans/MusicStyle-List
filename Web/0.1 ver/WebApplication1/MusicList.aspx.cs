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

namespace MusicList
{
    public partial class MusicList : System.Web.UI.Page
    {
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
        public static string YoutubeKeywordSearch(string keyword)
        {
            const string FAIL_STRING = "False";
            string url = "http://gdata.youtube.com/feeds/api/videos?q=" + keyword + "&max-results=10&alt=jsonc&v=2";
            try
            {
                //http://gdata.youtube.com/feeds/api/videos?q=jazz&max-results=10&alt=jsonc&v=2

                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
                HttpWebResponse response = (HttpWebResponse)request.GetResponse();
                Stream stream = response.GetResponseStream();
                StreamReader reader = new StreamReader(stream);
                string result = reader.ReadToEnd();
                stream.Close();
                response.Close();

                //var xmlDoc = new XmlDocument();
                //xmlDoc.Load("https://gdata.youtube.com/feeds/api/videos?q=" + keyword + "&max-results=10&alt=jsonc&v=2");

                JObject obj = JObject.Parse(JObject.Parse(result).ToString());
                JArray array = JArray.Parse(obj["data"].ToString());
                ArrayList alNewDataList = new ArrayList();
                int cnt = 1;
                //XmlNodeList title = xmlDoc.GetElementsByTagName("title");
                //XmlNodeList id = xmlDoc.GetElementsByTagName("id");
                //foreach (XmlNode ADDR_SET in title)
                //{
                //    foreach (XmlNode child in ADDR_SET.ChildNodes)
                //    {
                //        alNewDataList.Add(child.InnerText);
                //    }
                //}
                foreach (JObject item in array)
                {
                    alNewDataList.Add(item["title"].ToString());
                }

                StringBuilder sb = new StringBuilder();
                JavaScriptSerializer jparser = new JavaScriptSerializer();
                jparser.Serialize(alNewDataList, sb);
                return sb.ToString();
            }
            catch (Exception ex)
            {
                return FAIL_STRING;
            }
        }
    }
}
