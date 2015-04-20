using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Services;
using System.Text;
using System.Net;
using System.Web.UI;
using System.Web.UI.WebControls;
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
using System.Web.Services.Protocols;
using System.Web.Script.Services;



namespace MusicListWS
{
    /// <summary>
    /// WebService1의 요약 설명입니다.
    /// </summary>
    [WebService(Namespace = "http://tempuri.org/")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [System.ComponentModel.ToolboxItem(false)]

    [System.Web.Script.Services.ScriptService]
    public class MusicListWS : System.Web.Services.WebService
    {

        public const String SOUNDCLOUD_CLIENT_KEY = "a2b4d87e3bac428d8467d6ea343d49ae";
        public const String YOUTUBE_API_KEY = "AIzaSyAU_RCgyb2gf9gVht1OulCXbNq7re8jti8";

        public class YoutubeValidationData
        {
            public string title { get; set; }
        }

        [WebMethod]
        public string YoutubeURLValidation(string VID)
        {
            const string FAIL_STRING = "False";
            try
            {
                Context.Response.ContentType = "application/json; charset=utf-8";
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
                        YoutubeValidationData YVD = new YoutubeValidationData
                        {
                            title = (title.Item(0)).InnerText
                        };

                        return JsonConvert.SerializeObject(YVD, Newtonsoft.Json.Formatting.Indented).ToString();
                    }
                }
            }
            catch (Exception ex)
            {
                return FAIL_STRING;
            }
        }

        public class SoundcloudValidationData
        {
            public string title { get; set; }
            public string uri { get; set; }
            public string thumb { get; set; }
        }

        [WebMethod]
        public string SoundcloudURLValidation(string URL)
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

                        SoundcloudValidationData SVD = new SoundcloudValidationData
                        {
                            title = parsedResult["title"].ToString(),
                            uri = parsedResult["uri"].ToString(),
                            thumb = parsedResult["artwork_url"].ToString()
                        };

                        return JsonConvert.SerializeObject(SVD, Newtonsoft.Json.Formatting.Indented).ToString();
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
        public string YoutubeKeywordSearch(string keyword)
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
            public string permalink_url { get; set; }
        }

        [WebMethod]
        public string SoundcloudKeywordSearch(string keyword)
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
                int cnt = 0;
                foreach (JObject resultOne in items)
                {
                    if (cnt == 10) break;
                    try
                    {
                        SoundcloudSendData SSD = new SoundcloudSendData
                        {
                            id = resultOne["id"].ToString(),
                            title = resultOne["title"].ToString(),
                            duration = resultOne["duration"].ToString(),
                            stream_url = resultOne["stream_url"].ToString(),
                            artwork_url = (resultOne["artwork_url"] == null ? "" : resultOne["artwork_url"]).ToString(),
                            playback_count = resultOne["playback_count"].ToString(),
                            permalink_url = resultOne["permalink_url"].ToString()
                        };
                        dataArrayToSend.Add(JsonConvert.SerializeObject(SSD, Newtonsoft.Json.Formatting.Indented));
                    }
                    catch (Exception e)
                    {
                    }
                    cnt++;
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
        
        //for mobile

        public const int maxResults = 10;
        public const int YOUTUBE_TYPE = 1;
        public const int SOUND_CLOUD_TYPE = 2;
        public const string FAIL_STRING = "{\"result\":fail}";
        public const string RESULT_FIRST_STRING = "{";
        public const string RESULT_LAST_STRING = "}";
        public const string RESULT_LIST_STRING = "\"list\":";
        public const string RESULT_NEXT_TOKEN_STRING = "\n\"nextToken\":";
        public const string NOT_NEXT_TOKEN = "not_next_token";

        public class SearchDataModel
        {
            public string id { get; set; } //Soundcould : stream_url, Youtube : id
            public string title { get; set; }
            public string duration { get; set; }
            public string thumbnail { get; set; } //Soundcloud : artwork_url
            public string uploader { get; set; } //Soundcloud : user.username
            public int trackType { get; set; } //modelType youtube :1, soundcloud:2
        }

        public class SearchPlaylistModel
        {
            public string id { get; set; } //Soundcould : id, Youtube : id
            public string title { get; set; }
            public string thumbnail { get; set; } //Soundcloud : artwork_url
            public string size { get; set; } //Soundcould : track_count
            public int type { get; set; } // Youtube, Soundcloud 1,2
        }

        [WebMethod]
        public String YoutubeTrackDuration(string videoId)
        {
            string url = "https://www.googleapis.com/youtube/v3/videos?part=contentDetails&id=" + videoId + "&key=" + YOUTUBE_API_KEY;
            string duration = "";

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
                JArray items = (JArray)parsedResult["items"];

                foreach (JObject resultOne in items)
                {
                    try
                    {
                        duration = resultOne["contentDetails"]["duration"].ToString();
                    }
                    catch (Exception e)
                    {
                        duration = "PT0M0S";
                    }
                }
            }
            catch (Exception ex)
            {
                //PT0H0S
                duration = "PT0M0S";
            }

            return duration;
        }

        [WebMethod]
        public String YoutubePlaylistSize(string playlistId)
        {
            string url = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=0&playlistId=" + playlistId + "&key=" + YOUTUBE_API_KEY;
            String size = "0";

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

                try
                {
                    size = parsedResult["pageInfo"]["totalResults"].ToString();
                }
                catch (Exception ex)
                {
                    size = "0";
                }
            }
            catch (Exception ex)
            {
                //PT0H0S
                size = "0";
            }

            return size;
        }

        [WebMethod]
        public void YoutubeHotTrackSearch(string pageToken, string videoCategory)
        {
            string url = "https://www.googleapis.com/youtube/v3/search?type=video&part=snippet" + "&maxResults=" + maxResults + "&pageToken=" + pageToken + "&order=viewCount&videoCategoryId=" + videoCategory + "&key=" + YOUTUBE_API_KEY;
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
                JArray items = (JArray)parsedResult["items"];
                ArrayList dataList = new ArrayList();

                String token = null;
                try
                {
                    token = parsedResult["nextPageToken"].ToString();
                }
                catch (Exception ex)
                {
                    token = NOT_NEXT_TOKEN;
                }

                foreach (JObject resultOne in items)
                {
                    try
                    {
                        SearchDataModel searchDataModel = new SearchDataModel
                        {
                            id = resultOne["id"]["videoId"].ToString(),
                            title = resultOne["snippet"]["title"].ToString(),
                            thumbnail = resultOne["snippet"]["thumbnails"]["default"]["url"].ToString(),
                            duration = YoutubeTrackDuration(resultOne["id"]["videoId"].ToString()),
                            uploader = resultOne["snippet"]["channelTitle"].ToString(),
                            trackType = YOUTUBE_TYPE
                        };

                        dataList.Add(searchDataModel);
                    }
                    catch (Exception e)
                    {
                    }
                }
                Context.Response.ContentType = "application/json; charset=utf-8";
                Context.Response.Write(RESULT_FIRST_STRING + RESULT_NEXT_TOKEN_STRING + "\"" + token + "\"," + RESULT_LIST_STRING + JsonConvert.SerializeObject(dataList) + RESULT_LAST_STRING);

                //return RESULT_FIRST_STRING + JsonConvert.SerializeObject(dataList) + RESULT_LAST_STRING;
            }
            catch (Exception ex)
            {
                Context.Response.ContentType = "application/json; charset=utf-8";
                Context.Response.Write(FAIL_STRING);
                //return FAIL_STRING;
            }
        }

        [WebMethod]
        public void YoutubeTrackSearch(string keyword, string pageToken)
        {
            string url = "https://www.googleapis.com/youtube/v3/search?type=video&part=snippet&q=" + keyword + "&maxResults=" + maxResults + "&pageToken=" + pageToken + "&key=" + YOUTUBE_API_KEY;
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
                JArray items = (JArray)parsedResult["items"];
                ArrayList dataList = new ArrayList();

                String token = null;
                try
                {
                    token = parsedResult["nextPageToken"].ToString();
                }
                catch (Exception ex)
                {
                    token = NOT_NEXT_TOKEN;
                }

                foreach (JObject resultOne in items)
                {
                    try
                    {
                        SearchDataModel searchDataModel = new SearchDataModel
                        {
                            id = resultOne["id"]["videoId"].ToString(),
                            title = resultOne["snippet"]["title"].ToString(),
                            thumbnail = resultOne["snippet"]["thumbnails"]["default"]["url"].ToString(),
                            duration = YoutubeTrackDuration(resultOne["id"]["videoId"].ToString()),
                            uploader = resultOne["snippet"]["channelTitle"].ToString(),
                            trackType = YOUTUBE_TYPE
                        };

                        dataList.Add(searchDataModel);
                    }
                    catch (Exception e)
                    {
                    }
                }
                Context.Response.ContentType = "application/json; charset=utf-8";
                Context.Response.Write(RESULT_FIRST_STRING + RESULT_NEXT_TOKEN_STRING + "\"" + token + "\"," + RESULT_LIST_STRING + JsonConvert.SerializeObject(dataList) + RESULT_LAST_STRING);

                //return RESULT_FIRST_STRING + JsonConvert.SerializeObject(dataList) + RESULT_LAST_STRING;
            }
            catch (Exception ex)
            {
                Context.Response.ContentType = "application/json; charset=utf-8";
                Context.Response.Write(FAIL_STRING);
                //return FAIL_STRING;
            }
        }

        [WebMethod]
        public void YoutubePlaylistSearch(string keyword, string pageToken)
        {
            string url = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=playlist&q=" + keyword + "&maxResults=" + maxResults + "&pageToken=" + pageToken + "&key=" + YOUTUBE_API_KEY;

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
                JArray items = (JArray)parsedResult["items"];
                ArrayList dataList = new ArrayList();

                String token = null;
                try
                {
                    token = parsedResult["nextPageToken"].ToString();
                }
                catch (Exception ex)
                {
                    token = NOT_NEXT_TOKEN;
                }

                foreach (JObject resultOne in items)
                {
                    try
                    {
                        SearchPlaylistModel searchPlaylistModel = new SearchPlaylistModel
                        {
                            id = resultOne["id"]["playlistId"].ToString(),
                            title = resultOne["snippet"]["title"].ToString(),
                            thumbnail = resultOne["snippet"]["thumbnails"]["default"]["url"].ToString(),
                            size = YoutubePlaylistSize(resultOne["id"]["playlistId"].ToString()),
                            type = YOUTUBE_TYPE
                        };

                        dataList.Add(searchPlaylistModel);
                    }
                    catch (Exception e)
                    {
                    }
                }

                Context.Response.ContentType = "application/json; charset=utf-8";
                Context.Response.Write(RESULT_FIRST_STRING + RESULT_NEXT_TOKEN_STRING + "\"" + token + "\"," + RESULT_LIST_STRING + JsonConvert.SerializeObject(dataList) + RESULT_LAST_STRING);
                //return RESULT_FIRST_STRING + JsonConvert.SerializeObject(dataList) + RESULT_LAST_STRING;
            }
            catch (Exception ex)
            {
                Context.Response.ContentType = "application/json; charset=utf-8";
                Context.Response.Write(FAIL_STRING);
                //return RESULT_FIRST_STRING + JsonConvert.SerializeObject(dataList) + RESULT_LAST_STRING;
            }
        }

        //YoutubePlaylistDetailSearch

        [WebMethod]
        public void YoutubePlaylistDetailSearch(string playlistId, string pageToken)
        {
            string url = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet%2C+contentDetails&maxResults=" + maxResults + "&playlistId=" + playlistId + "&pageToken=" + pageToken + "&key=" + YOUTUBE_API_KEY;

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
                JArray items = (JArray)parsedResult["items"];
                ArrayList dataList = new ArrayList();

                String token = null;
                try
                {
                    token = parsedResult["nextPageToken"].ToString();
                }
                catch (Exception ex)
                {
                    token = NOT_NEXT_TOKEN;
                }

                foreach (JObject resultOne in items)
                {
                    try
                    {
                        SearchDataModel searchDataModel = new SearchDataModel
                        {
                            id = resultOne["contentDetails"]["videoId"].ToString(),
                            title = resultOne["snippet"]["title"].ToString(),
                            thumbnail = resultOne["snippet"]["thumbnails"]["default"]["url"].ToString(),
                            duration = YoutubeTrackDuration(resultOne["contentDetails"]["videoId"].ToString()),
                            uploader = resultOne["snippet"]["channelTitle"].ToString(),
                            trackType = YOUTUBE_TYPE
                        };

                        dataList.Add(searchDataModel);
                    }
                    catch (Exception e)
                    {
                    }
                }
                Context.Response.ContentType = "application/json; charset=utf-8";
                Context.Response.Write(RESULT_FIRST_STRING + RESULT_NEXT_TOKEN_STRING + "\"" + token + "\"," + RESULT_LIST_STRING + JsonConvert.SerializeObject(dataList) + RESULT_LAST_STRING);

                //return RESULT_FIRST_STRING + JsonConvert.SerializeObject(dataList) + RESULT_LAST_STRING;
            }
            catch (Exception ex)
            {
                Context.Response.ContentType = "application/json; charset=utf-8";
                Context.Response.Write(FAIL_STRING);
                //return FAIL_STRING;
            }
        }

        [WebMethod]
        public void SoundcloudHotTrackSearch(string category, int index)
        {
            Context.Response.ContentType = "application/json; charset=utf-8";
            string url = "https://api.soundcloud.com/tracks.json?client_id=" + SOUNDCLOUD_CLIENT_KEY + "&genres= " + category + "&&limit=" + maxResults + "&offset=" + ((index - 1) * maxResults) + "&order=hotness";
            try
            {
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
                HttpWebResponse response = (HttpWebResponse)request.GetResponse();
                Stream stream = response.GetResponseStream();
                StreamReader reader = new StreamReader(stream);
                string result = reader.ReadToEnd();
                stream.Close();
                response.Close();

                JArray items = JArray.Parse(result);
                ArrayList dataList = new ArrayList();

                foreach (JObject resultOne in items)
                {
                    try
                    {
                        if (resultOne["streamable"].ToString().Equals("false"))
                        {
                            continue;
                        }

                        SearchDataModel searchDataModel = new SearchDataModel
                        {
                            id = resultOne["id"].ToString(),
                            title = resultOne["title"].ToString(),
                            thumbnail = (resultOne["artwork_url"] == null ? "" : resultOne["artwork_url"]).ToString(),
                            duration = resultOne["duration"].ToString(),
                            uploader = resultOne["user"]["username"].ToString(),
                            trackType = SOUND_CLOUD_TYPE
                        };

                        dataList.Add(searchDataModel);
                    }
                    catch (Exception e)
                    {
                    }
                }

                Context.Response.Write(RESULT_FIRST_STRING + RESULT_NEXT_TOKEN_STRING + "\"" + (index + 1) + "\"," + RESULT_LIST_STRING + JsonConvert.SerializeObject(dataList) + RESULT_LAST_STRING);
            }
            catch (Exception ex)
            {
                Context.Response.Write(FAIL_STRING);
            }
        }

        [WebMethod]
        public void SoundcloudTrackSearch(string keyword, int index)
        {
            string url = "https://api.soundcloud.com/tracks.json?client_id=" + SOUNDCLOUD_CLIENT_KEY + "&q= " + keyword + "&&limit=" + maxResults + "&offset=" + ((index - 1) * maxResults);
            try
            {
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
                HttpWebResponse response = (HttpWebResponse)request.GetResponse();
                Stream stream = response.GetResponseStream();
                StreamReader reader = new StreamReader(stream);
                string result = reader.ReadToEnd();
                stream.Close();
                response.Close();

                JArray items = JArray.Parse(result);
                ArrayList dataList = new ArrayList();

                foreach (JObject resultOne in items)
                {
                    try
                    {
                        if (resultOne["streamable"].ToString().Equals("false"))
                        {
                            continue;
                        }

                        SearchDataModel searchDataModel = new SearchDataModel
                        {
                            id = resultOne["id"].ToString(),
                            title = resultOne["title"].ToString(),
                            thumbnail = (resultOne["artwork_url"] == null ? "" : resultOne["artwork_url"]).ToString(),
                            duration = resultOne["duration"].ToString(),
                            uploader = resultOne["user"]["username"].ToString(),
                            trackType = SOUND_CLOUD_TYPE
                        };

                        dataList.Add(searchDataModel);
                    }
                    catch (Exception e)
                    {
                    }
                }

                Context.Response.ContentType = "application/json; charset=utf-8";
                Context.Response.Write(RESULT_FIRST_STRING + RESULT_NEXT_TOKEN_STRING + "\"" + (index + 1) + "\"," + RESULT_LIST_STRING + JsonConvert.SerializeObject(dataList) + RESULT_LAST_STRING);
                //return RESULT_FIRST_STRING + JsonConvert.SerializeObject(dataList) + RESULT_LAST_STRING;
            }
            catch (Exception ex)
            {
                Context.Response.ContentType = "application/json; charset=utf-8";
                Context.Response.Write(FAIL_STRING);
                //return FAIL_STRING;
            }
        }

        [WebMethod]
        public void SoundcloudPlaylistSearch(string keyword, int index)
        {
            string url = "https://api.soundcloud.com/playlists.json?client_id=" + SOUNDCLOUD_CLIENT_KEY + "&q= " + keyword + "&limit=" + maxResults + "&offset=" + ((index - 1) * maxResults);

            try
            {
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
                HttpWebResponse response = (HttpWebResponse)request.GetResponse();
                Stream stream = response.GetResponseStream();
                StreamReader reader = new StreamReader(stream);
                string result = reader.ReadToEnd();
                stream.Close();
                response.Close();

                JArray items = JArray.Parse(result);
                ArrayList dataList = new ArrayList();

                foreach (JObject resultOne in items)
                {
                    try
                    {
                        SearchPlaylistModel searchPlaylistModel = new SearchPlaylistModel
                        {
                            id = resultOne["id"].ToString(),
                            title = resultOne["title"].ToString(),
                            thumbnail = resultOne["tracks"][0]["artwork_url"].ToString(),
                            size = resultOne["track_count"].ToString(),
                            type = SOUND_CLOUD_TYPE
                        };

                        dataList.Add(searchPlaylistModel);
                    }
                    catch (Exception e)
                    {
                    }
                }

                Context.Response.ContentType = "application/json; charset=utf-8";
                Context.Response.Write(RESULT_FIRST_STRING + RESULT_NEXT_TOKEN_STRING + "\"" + (index + 1) + "\"," + RESULT_LIST_STRING + JsonConvert.SerializeObject(dataList) + RESULT_LAST_STRING);
                //return RESULT_FIRST_STRING + JsonConvert.SerializeObject(dataList) + RESULT_LAST_STRING;
            }
            catch (Exception ex)
            {
                Context.Response.ContentType = "application/json; charset=utf-8";
                Context.Response.Write(FAIL_STRING);
                //return RESULT_FIRST_STRING + JsonConvert.SerializeObject(dataList) + RESULT_LAST_STRING;
            }
        }

        [WebMethod]
        public void SoundCloudPlaylistDetailSearch(string id, int index)
        {
            string url = "https://api.soundcloud.com/playlists/" + id + ".json?client_id=" + SOUNDCLOUD_CLIENT_KEY + "&limit=" + maxResults + "&offset=" + ((index - 1) * maxResults);

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
                JArray items = (JArray)parsedResult["tracks"];
                ArrayList dataList = new ArrayList();

                foreach (JObject resultOne in items)
                {
                    try
                    {
                        if (resultOne["streamable"].ToString().Equals("false"))
                        {
                            continue;
                        }

                        SearchDataModel searchDataModel = new SearchDataModel
                        {
                            id = resultOne["id"].ToString(),
                            title = resultOne["title"].ToString(),
                            thumbnail = (resultOne["artwork_url"] == null ? "" : resultOne["artwork_url"]).ToString(),
                            duration = resultOne["duration"].ToString(),
                            uploader = resultOne["user"]["username"].ToString(),
                            trackType = SOUND_CLOUD_TYPE
                        };

                        dataList.Add(searchDataModel);
                    }
                    catch (Exception e)
                    {
                    }
                }

                Context.Response.ContentType = "application/json; charset=utf-8";
                Context.Response.Write(RESULT_FIRST_STRING + RESULT_NEXT_TOKEN_STRING + "\"" + (index + 1) + "\"," + RESULT_LIST_STRING + JsonConvert.SerializeObject(dataList) + RESULT_LAST_STRING);
                //return RESULT_FIRST_STRING + JsonConvert.SerializeObject(dataList) + RESULT_LAST_STRING;
            }
            catch (Exception ex)
            {
                Context.Response.ContentType = "application/json; charset=utf-8";
                Context.Response.Write(FAIL_STRING);
                //return RESULT_FIRST_STRING + JsonConvert.SerializeObject(dataList) + RESULT_LAST_STRING;
            }
        }
    }
}
