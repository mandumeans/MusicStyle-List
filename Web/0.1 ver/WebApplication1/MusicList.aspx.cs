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
            try
            {
                var xmlDoc = new XmlDocument();
                xmlDoc.Load("https://gdata.youtube.com/feeds/api/videos?q=" + keyword + "&v=2");
                ArrayList alNewDataList = new ArrayList();
                XmlNodeList title = xmlDoc.GetElementsByTagName("title");
                int cnt = 1;
                foreach (XmlNode ADDR_SET in title)
                {
                    foreach (XmlNode child in ADDR_SET.ChildNodes)
                    {
                        alNewDataList.Add(child.InnerText);
                    }
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
