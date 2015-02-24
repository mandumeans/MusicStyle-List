using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Web.Services;
using System.Web.Script.Serialization;
using System.Web.Mvc;
using System.IO;
using System.Xml;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System.Data;
using System.Collections;

using Google.Apis.Services;
using Google.Apis.YouTube.v3;
using Google.Apis.YouTube.v3.Data;

namespace MvcApplication1.Controllers
{
    
    public class MusiclistController : Controller
    {

        public const string SOUNDCLOUD_CLIENT_KEY = "a2b4d87e3bac428d8467d6ea343d49ae";
        public const string FAIL_STRING = "{ \"result\":fail}";

        public const string RESULT_FIRST_STRING = "{ \"list\":";
        public const string RESULT_LAST_STRING = "}";

        public ActionResult MusicList()
        {
            return View();
        }

        public string YoutubeURLValidation(string VID)
        {
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

        public class SoundcloudValidationData
        {
            public string title { get; set; }
            public string uri { get; set; }
        }

        public string SoundcloudURLValidation(string URL)
        {
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
                            uri = parsedResult["uri"].ToString()
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

        public int maxResults = 10;
        public int YOUTUBE_TYPE = 1;
        public int SOUND_CLOUD_TYPE = 2;
        public int ONLY_YOUTUBE_TYPE = 1;
        public int NOT_ONLY_YOUTUBE_TYPE = 2;

        public class SearchDataModel
        {
            public string id { get; set; } //Soundcould : stream_url, Youtube : id
            public string title { get; set; }
            public string duration { get; set; }
            public string thumbnail { get; set; } //Soundcloud : artwork_url
            public string uploaded { get; set; } //Soundcloud : created_at
            public string viewCount { get; set; } //Soundcould : playback_count
            public string uploader { get; set; } //Soundcloud : user.username
            public int trackType { get; set; } //modelType youtube :1, soundcloud:2
            public int onlyYoutube { get; set; } //modelType true : 1, false : 2
        }

        public string YoutubeTrackSearch(string keyword, int index)
        {
            string url = "http://gdata.youtube.com/feeds/api/videos?q=" + keyword + "&max-results=" + maxResults + "&alt=jsonc&v=2&start-index=" + (((index-1) * maxResults) + 1);
            
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
                ArrayList dataList = new ArrayList();

                foreach (JObject resultOne in items)
                {                    
                    try
                    {
                        SearchDataModel searchDataModel = new SearchDataModel
                        {
                            id = resultOne["id"].ToString(),
                            title = resultOne["title"].ToString(),
                            thumbnail = resultOne["thumbnail"]["sqDefault"].ToString(),
                            viewCount = resultOne["viewCount"].ToString(),
                            uploaded = resultOne["uploaded"].ToString(),
                            duration = resultOne["duration"].ToString(),
                            uploader = resultOne["uploader"].ToString(),
                            onlyYoutube = CheckOnlyYoutube(resultOne["id"].ToString()),
                            trackType = YOUTUBE_TYPE
                        };

                        dataList.Add(searchDataModel);
                    }
                    catch (Exception e)
                    {
                    }
                }

                return RESULT_FIRST_STRING + JsonConvert.SerializeObject(dataList) + RESULT_LAST_STRING;
            }
            catch (Exception ex)
            {
                return FAIL_STRING;
            }
        }

        public int CheckOnlyYoutube(string id)
        {
            string url = "http://gdata.youtube.com/feeds/mobile/videos/" + id;
            
            // Retrieve XML document  
            XmlTextReader reader = new XmlTextReader(url);

            // Skip non-significant whitespace  
            reader.WhitespaceHandling = WhitespaceHandling.Significant;
            while (reader.Read())
            {
                if (reader.Name.Contains("media:content"))
                {
                    return NOT_ONLY_YOUTUBE_TYPE;
                }
            }

            return ONLY_YOUTUBE_TYPE;
        }

        public string SoundcloudTrackSearch(string keyword, int index)
        {
            string url = "https://api.soundcloud.com/tracks.json?client_id=" + SOUNDCLOUD_CLIENT_KEY + "&q= " + keyword + "&&limit="+ maxResults +"&offset=" + ((index-1) * maxResults);
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
                        SearchDataModel searchDataModel = new SearchDataModel
                        {
                            id = resultOne["stream_url"].ToString(),
                            title = resultOne["title"].ToString(),
                            thumbnail = (resultOne["artwork_url"] == null ? "" : resultOne["artwork_url"]).ToString(),
                            viewCount = resultOne["playback_count"].ToString(),
                            uploaded = resultOne["created_at"].ToString(),
                            duration = resultOne["duration"].ToString(),
                            uploader = resultOne["user"]["username"].ToString(),
                            onlyYoutube = NOT_ONLY_YOUTUBE_TYPE,
                            trackType = SOUND_CLOUD_TYPE
                        };

                        dataList.Add(searchDataModel);
                    }
                    catch (Exception e)
                    {
                    }
                }

                return RESULT_FIRST_STRING + JsonConvert.SerializeObject(dataList) + RESULT_LAST_STRING;
            }
            catch (Exception ex)
            {
                return FAIL_STRING;
            }
        }

        public class SearchPlaylistModel
        {
            public string id { get; set; } //Soundcould : id, Youtube : id
            public string title { get; set; }
            public string thumbnail { get; set; } //Soundcloud : artwork_url
            public string size { get; set; } //Soundcould : track_count
            public int type { get; set; } // Youtube, Soundcloud 1,2
        }


        public string YoutubePlaylistSearch(string keyword, int index)
        {
            string url = "https://gdata.youtube.com/feeds/api/playlists/snippets?&q=" + keyword + "&max-results=" + maxResults + "&alt=jsonc&v=2&start-index=" + (((index - 1) * maxResults) + 1);

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
                ArrayList dataList = new ArrayList();

                foreach (JObject resultOne in items)
                {
                    try
                    {
                        SearchPlaylistModel searchPlaylistModel = new SearchPlaylistModel
                        {
                            id = resultOne["id"].ToString(),
                            title = resultOne["title"].ToString(),
                            thumbnail = resultOne["thumbnail"]["sqDefault"].ToString(),
                            size = resultOne["size"].ToString(),
                            type = YOUTUBE_TYPE
                        };

                        dataList.Add(searchPlaylistModel);
                    }
                    catch (Exception e)
                    {
                    }
                }

                return RESULT_FIRST_STRING + JsonConvert.SerializeObject(dataList) + RESULT_LAST_STRING;
            }
            catch (Exception ex)
            {
                return FAIL_STRING;
            }
        }

        public string SoundcloudPlaylistSearch(string keyword, int index)
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

                return RESULT_FIRST_STRING + JsonConvert.SerializeObject(dataList) + RESULT_LAST_STRING;
            }
            catch (Exception ex)
            {
                return FAIL_STRING;
            }
        }

        //API는 분리한다.
        public string YoutubePlaylistDetailSearch(string id, int index)
        {
            string url = "https://gdata.youtube.com/feeds/api/playlists/" + id + "?start-index=" + (((index - 1) * maxResults) + 1) + "&max-results=" + maxResults + "&alt=jsonc&v=2";

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
                ArrayList dataList = new ArrayList();

                foreach (JObject resultOne in items)
                {
                    try
                    {
                        SearchDataModel searchDataModel = new SearchDataModel
                        {
                            id = resultOne["video"]["id"].ToString(),
                            title = resultOne["video"]["title"].ToString(),
                            thumbnail = resultOne["video"]["thumbnail"]["sqDefault"].ToString(),
                            uploaded = resultOne["video"]["uploaded"].ToString(),
                            viewCount = resultOne["video"]["viewCount"].ToString(),
                            duration = resultOne["video"]["duration"].ToString(),
                            uploader = resultOne["video"]["uploader"].ToString(),
                            onlyYoutube = CheckOnlyYoutube(resultOne["video"]["id"].ToString()),
                            trackType = YOUTUBE_TYPE
                        };

                        dataList.Add(searchDataModel);
                    }
                    catch (Exception e)
                    {
                    }
                }

                return RESULT_FIRST_STRING + JsonConvert.SerializeObject(dataList) + RESULT_LAST_STRING;
            }
            catch (Exception ex)
            {
                return FAIL_STRING;
            }
        }

        public string SoundCloudPlaylistDetailSearch(string id, int index)
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
                        SearchDataModel searchDataModel = new SearchDataModel
                        {
                            id = resultOne["id"].ToString(),
                            title = resultOne["title"].ToString(),
                            thumbnail = (resultOne["artwork_url"] == null ? "" : resultOne["artwork_url"]).ToString(),
                            uploaded = resultOne["created_at"].ToString(),
                            viewCount = resultOne["playback_count"].ToString(),
                            duration = resultOne["duration"].ToString(),
                            uploader = resultOne["user"]["username"].ToString(),
                            onlyYoutube = NOT_ONLY_YOUTUBE_TYPE,
                            trackType = SOUND_CLOUD_TYPE
                        };

                        dataList.Add(searchDataModel);
                    }
                    catch (Exception e)
                    {
                    }
                }

                return RESULT_FIRST_STRING + JsonConvert.SerializeObject(dataList) + RESULT_LAST_STRING;
            }
            catch (Exception ex)
            {
                return FAIL_STRING;
            }
        }

        public string YoutubeKeywordV3Search(string keyword, string index)
        {
            string apiKey = "AIzaSyC_2UXHjm8y2QceUdJFGyKCsv-BdyOoABg";

            YouTubeService youtube = new YouTubeService(new BaseClientService.Initializer()
            {
                ApiKey = apiKey
            });

            SearchResource.ListRequest listRequest = youtube.Search.List("snippet");
            listRequest.Q = keyword;
            listRequest.MaxResults = maxResults;
            listRequest.Type = "video";

            if (index != null)
            {
                listRequest.PageToken = index;
            }

            SearchListResponse searchResponse = listRequest.Execute();

            ArrayList dataList = new ArrayList();

            foreach (SearchResult searchResult in searchResponse.Items)
            {
                SearchDataModel SDM = new SearchDataModel
                {
                    id = searchResult.Id.VideoId,
                    title = searchResult.Snippet.Title,
                    thumbnail = searchResult.Snippet.Thumbnails.Default.Url,
                    //viewCount = searchResult.Snippet,
                    //duration = resultOne["duration"].ToString()
                };

                dataList.Add(SDM);
            }

            return "null";
        }
    }
}
