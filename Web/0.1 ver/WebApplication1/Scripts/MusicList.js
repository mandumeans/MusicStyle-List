
var VideoType = Object.freeze({ 'YouTube': 1, 'SoundCloud': 2 });
var repeatable = true;
var shuffle = false;
var nextVideoURL;

//This code loads the IFrame Player API code asynchronously.
var tag = document.createElement('script');
tag.src = 'https://www.youtube.com/iframe_api';
var firstScriptTag = document.getElementsByTagName('script')[0];
firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

var wsBaseURL = "../MusicListWS.asmx";
var YTValidationURL = wsBaseURL + "/YoutubeURLValidation";
var SCValidationURL = wsBaseURL + "/SoundcloudURLValidation";
var YTSearchURL = wsBaseURL + "/YoutubeKeywordSearch";
var SCSearchURL = wsBaseURL + "/SoundcloudKeywordSearch";

var Util = {
    ajaxHelper : function (url, data, cbSuccess, cbError) {
        $.ajax({
            url: url,
            type: "POST",
            data: data,
            contentType: "application/json",
            dataType: "JSON",
            timeout: 10000,
            success: function (result) { cbSuccess(result) },
            error: function (result) { cbError(result) }
        }); //ajax end
    }
}


var PlayingType = Object.freeze({ 'playing': 1, 'paused': 2 });


var rightListStatus;

var MUSICLIST = {
    init: function () {
        rightListStatus = false;
        $(window).resize(function () {
            if ($('.video').find('iframe').length > 0) {
                $('.video').find('iframe').css('width', $('.video').css('width'))
                $('.video').find('iframe').css('height', $('.video').css('height'))
            }
        });

        //Get List from cookies

        var cookieContent = MUSICLIST.getCookie("playStyle");
        if (cookieContent != "") {
            $('#playlist').append(cookieContent);
            $("#playlist .musicPlaying").attr("isPlaying", "false");
            $("#playlist .musicPlaying").removeClass("musicPlaying");
            $('.musicItemTitle').click(function () {
                if ($(this).parent().attr('type') == VideoType.YouTube) {
                    //Youtube
                    Youtube.PlayYoutubeVideo($(this).parent().attr('url'));
                } else {
                    //SoundCloud
                    Soundcloud.PlaySoundcloudVideo($(this).parent().attr('url'), $(this).parent().attr('thumb'));
                }
            });
            $('.musicItemRemove').click(function () {
                MUSICLIST.DeleteVideoFromList($(this).parent());
                //Make list to cookie 
                var playList = MUSICLIST.getActiveList().html();
                MUSICLIST.setCookie("playStyle", playList, 20);
                //Make list to cookie end
            });
        }

        $('#btnURLInput').click(function () {
            var UrlInput = document.getElementById('txtURLInput').value;
            var nowVID = Youtube.FindUrlVideoID(UrlInput);

            if (Youtube.VideoURLValidation(UrlInput)) {
                //Youtube Video
                Util.ajaxHelper(YTValidationURL, '{"VID" : "' + nowVID + '"}',
                    function (result) {
                        var rawResult = result.d;
                        if (rawResult === 'False') {
                            alert('There is no video on that URL from Youtube');
                        }
                        else {
                            //Youtube video
                            if (!Youtube.isVIDExist(nowVID)) {
                                alert('Already exists');
                                return;
                            } else {
                                var titleResult = jQuery.parseJSON(rawResult).title;

                                MUSICLIST.AddNewVideoItem(VideoType.YouTube, UrlInput, titleResult, '');
                            }
                        }
                    },
                    function (result) {
                        alert('Error occurred');
                    }
                );
            }
            else {
                //Soundcloud video
                Util.ajaxHelper(SCValidationURL, '{"URL" : "' + UrlInput + '"}',
                    function (result) {
                        var rawResult = result.d;
                        if (rawResult === 'False') {
                            alert('There is no video on that URL from Soundcloud');
                        }
                        else {
                            var titleResult = jQuery.parseJSON(rawResult).title;
                            var uriResult = jQuery.parseJSON(rawResult).uri;
                            var thumbResult = jQuery.parseJSON(rawResult).thumb;
                            MUSICLIST.AddNewVideoItem(VideoType.SoundCloud, uriResult, titleResult, thumbResult);
                        }
                    },
                    function (result) {
                        alert('Error occurred');
                    }
                );
            }
        }); //btnURLInput click event end
        $("#btnSearchInput").click(function () {
            var searchKeyword = $("#txtSearchInput").val();
            $("#txtSearchInput2").val(searchKeyword);
            SEARCH.init(searchKeyword);
            $("#searchModal").modal();
        }); //btnSearchInput click event end

        $("#btnSearchInput2").click(function () {
            var searchKeyword = $("#txtSearchInput2").val();
            SEARCH.init(searchKeyword);
        }); //btnSearchInput click event end

        $('#btnClearList').click(function () {
            $('.musicListItem').remove();
            nextVideoURL = null;
        });

        $("#txtSearchInput").keyup(function (event) {
            if (event.keyCode == 13) {
                $('#btnSearchInput').click();
            }
        });
        $("#txtSearchInput2").keyup(function (event) {
            if (event.keyCode == 13) {
                $("#btnSearchInput2").click();
            }
        });
        $(".playListNav").change(function () {
            $(".playlistTab").find("div").each(function (index) {
                $(this).hide();
            });
            MUSICLIST.getActiveListTab().show();
        });
    },
    AddNewVideoItem: function (type, url, title, thumb) {
        var videoItemType;
        //var vid = Youtube.FindUrlVideoID(url);
        var listString = '<li class="musicListItem" type="' + type + '" url = "' + url + '" thumb = "' + thumb + '" isPlaying = "false"><span class="musicItemTitle">' + title + '</span><a class="musicItemRemove">X</a></li>'
        MUSICLIST.getActiveList().append(listString);

        //Make list to cookie 
        var playList = $('#playlist').html();
        MUSICLIST.setCookie("playStyle", playList, 20);
        //Make list to cookie end

        //remove all Events
        $(".musicItemTitle").off("click");
        $(".musicItemRemove").off("click");

        //register event each
        $('.musicItemTitle').click(function () {
            if ($(this).parent().attr('type') == VideoType.YouTube) {
                //Youtube
                Youtube.PlayYoutubeVideo($(this).parent().attr('url'));
            } else {
                //SoundCloud
                Soundcloud.PlaySoundcloudVideo($(this).parent().attr('url'), $(this).parent().attr('thumb'));
            }
        });
        $('.musicItemRemove').click(function () {
            MUSICLIST.DeleteVideoFromList($(this).parent());
            //Make list to cookie 
            var playList = MUSICLIST.getActiveList().html();
            MUSICLIST.setCookie("playStyle", playList, 20);
            //Make list to cookie end
        });
    },
    DeleteVideoFromList: function (parent) {
        nextVideoURL = MUSICLIST.getNextVideo(parent.attr("URL"));
        parent.remove();
    },
    //Change isPlaying status attribue
    playingStatusChange: function (URL) {
        $('.musicListItem[isPlaying="true"]').attr('isPlaying', 'false').removeClass('musicPlaying');
        $('.musicListItem[url="' + URL + '"]').attr('isPlaying', 'true').addClass('musicPlaying');
    },

    getNextVideo: function (URL) {
        var nextMusicURL = null;

        //There is no now playing video (playing video has been deleted)
        if ($('.musicListItem[isPlaying="true"]').length < 1) {
            if ($('.musicListItem').length > 0) {
                nextMusicURL = nextVideoURL;    //when video is deleted, next Video VID is saved;
            }
        }
        else {
            nextMusicURL = $('.musicListItem[url="' + URL + '"]').next('li').attr('url');
            if (nextMusicURL === null || (typeof nextMusicURL) === "undefined") {
                if (repeatable === true) {
                    //it's last list item and repeatable -> go to first song
                    nextMusicURL = $('.musicListItem').first().attr('url');
                }
            }
        }
        return nextMusicURL;
    },
    getRandomVideo: function (URL) {
        var nextMusicURL = null;
        var videoCount = $(".musicListItem").length;

        if (videoCount < 2) {
            //There is no now playing video (playing video has been deleted)
            if ($('.musicListItem[isPlaying="true"]').length < 1) {
                if ($('.musicListItem').length > 0) {
                    nextMusicURL = nextVideoURL;    //when video is deleted, next Video VID is saved;
                }
            } else {
                nextMusicURL = $('.musicListItem[isPlaying="true"]').attr("url");
            }
        } else {
            var nextVideoNum = parseInt(Math.random() * videoCount);
            nextVideoNum = nextVideoNum + 1;
            nextMusicURL = $(".musicListItem:nth-child(" + nextVideoNum + ")[isPlaying='false']").attr('url');
            while (typeof nextMusicURL == "undefined") {
                nextVideoNum = parseInt(Math.random() * videoCount);
                nextVideoNum = nextVideoNum + 1;
                nextMusicURL = $(".musicListItem:nth-child(" + nextVideoNum + ")[isPlaying='false']").attr('url');
            }
        }
        return nextMusicURL;
    },
    getPreviousVideo: function (URL) {
        var prevMusicURL = null;

        //There is no now playing video (playing video has been deleted)
        if ($('.musicListItem[isPlaying="true"]').length < 1) {
            if ($('.musicListItem').length > 0) {
                prevMusicURL = $('.musicListItem').first().attr("url");    //when video is deleted, first URL is saved.;
            }
        }
        else {
            prevMusicURL = $('.musicListItem[url="' + URL + '"]').prev('li').attr('url');
            if (prevMusicURL === null) {
                if (repeatable === true) {
                    //it's last list item and repeatable -> go to first song
                    prevMusicURL = $('.musicListItem').last().attr('url');
                }
            }
        }
        return prevMusicURL;
    },

    playNextVideo: function (exURL) {
        var nextMusicURL;
        var nextMusicType;

        if (shuffle) {
            nextMusicURL = MUSICLIST.getRandomVideo(exURL);
        } else {
            nextMusicURL = MUSICLIST.getNextVideo(exURL);
        }
        nextMusicType = $('.musicListItem[url="' + nextMusicURL + '"]').attr('type');
        if (nextMusicType == VideoType.YouTube) {
            Youtube.PlayYoutubeVideo(nextMusicURL);
        } else if (nextMusicType == VideoType.SoundCloud) {
            Soundcloud.PlaySoundcloudVideo(nextMusicURL, $('.musicListItem[url="' + nextMusicURL + '"]').attr('thumb'));
        } else {
            console.log('There is no video to play');
        }
    },

    playPreviousVideo: function (exURL) {
        var prevMusicURL = MUSICLIST.getPreviousVideo(exURL);
        var prevMusicType = $('.musicListItem[url="' + prevMusicURL + '"]').attr('type');

        if (prevMusicType == VideoType.YouTube) {
            Youtube.PlayYoutubeVideo(prevMusicURL);
        } else if (prevMusicType == VideoType.SoundCloud) {
            Soundcloud.PlaySoundcloudVideo(prevMusicURL, $('.musicListItem[url="' + prevMusicURL + '"]').attr('thumb'));
        } else {
            console.log(prevMusicURL);
        }
    },

    setCookie: function (cName, cValue, cDay) {
        var expire = new Date();
        expire.setDate(expire.getDate() + cDay);
        cookies = cName + "=" + escape(cValue) + "; path=/";
        if (typeof cDay != "undefined") {
            cookies += ";expires=" + expire.toGMTString() + ";";
        }
        document.cookie = cookies;
    },
    getCookie: function (cName) {
        cName = cName + "=";
        var cookieData = document.cookie;
        var start = cookieData.indexOf(cName);
        var cValue = "";
        if (start != -1) {
            start += cName.length;
            var end = cookieData.indexOf(";", start);
            if (end == -1) {
                end = cookieData.length;
            }
            cValue = cookieData.substring(start, end);
        }
        return unescape(cValue);
    },
    getActiveListTab: function () {
        return $(".playlistTab").find("div[name=" + $(".playListNav option:selected").attr("name") + "]");
    },
    getActiveList: function () {
        return $(".playlistTab").find("div[name=" + $(".playListNav option:selected").attr("name") + "]").find("ul");
    }
}

var SEARCH_TITLE_MAXLENGTH = 40;

var SEARCH = {
    clearify: function () {
        $("#allTab").empty();
        $("#ytTab").empty();
        $("#scTab").empty();
    },
    init: function (searchKeyword) {
        //Youtube Search
        Util.ajaxHelper(YTSearchURL, '{"keyword" : "' + searchKeyword + '"}',
            function (result) {
                var rawResult = jQuery.parseJSON(result.d);
                if (rawResult === 'False') {
                    alert('There is no video on that URL');
                }
                else {
                    SEARCH.clearify();
                    for (var i = 0; i < rawResult.length; i++) {
                        var result = jQuery.parseJSON(rawResult[i]);
                        var newTitle = result.title;
                        if (result.title.length > SEARCH_TITLE_MAXLENGTH - 1) {
                            newTitle = newTitle.substring(0, SEARCH_TITLE_MAXLENGTH);
                            newTitle += "...";
                        }
                        if (result.artwork_url == "") {
                            result.artwork_url = "images/noimg.png";
                        }
                        appendString = "<a href='#' class='list-group-item addYTNewVideo' title='" + result.title + "' vid='" + result.id + "' type='1'>";
                        appendString += "<img class='searchImg' src='" + result.thumbnail.sqDefault + "' alt='sig'></img>";
                        appendString += "<div class='searchDesc'>";
                        appendString += "<span>" + newTitle + "</span>";
                        appendString += "<span class='searchTitleReal' style='display:none;'>" + result.title + "</span>";
                        appendString += "<span class='searchCount'>" + result.viewCount + " views</span>";
                        appendString += "<span class='searchDuration'>" + SEARCH.YTTimeCalc(result.duration) + "</span>";
                        appendString += "</div>";
                        appendString += "</a>";

                        $("#allTab").append(appendString);
                        $("#ytTab").append(appendString);
                    }
                    $(".addYTNewVideo").click(function () {
                        var urlPrefix = "https://www.youtube.com/watch?v=";
                        MUSICLIST.AddNewVideoItem(VideoType.YouTube, urlPrefix + $(this).attr("vid"), $(this).find(".searchTitleReal").html(), '');
                    });
                    SEARCH.soundCloudInit(searchKeyword);
                }
            },
            function (result) {
                alert('Error occurred');
            }
        );
    },

    soundCloudInit: function (searchKeyword) {
        //Soundcloud Search
        Util.ajaxHelper(SCSearchURL, '{"keyword" : "' + searchKeyword + '"}',
            function (result) {
                var rawResult = jQuery.parseJSON(result.d);
                if (rawResult === 'False') {
                    alert('There is no video on that URL');
                }
                else {
                    for (var i = 0; i < rawResult.length; i++) {
                        var result = jQuery.parseJSON(rawResult[i]);
                        var newTitle = result.title;
                        var newDuration = result.duration;
                        if (result.title.length > SEARCH_TITLE_MAXLENGTH - 1) {
                            newTitle = newTitle.substring(0, SEARCH_TITLE_MAXLENGTH);
                            newTitle += "...";
                        }
                        if (result.artwork_url == "") {
                            result.artwork_url = "images/noimg.png";
                        }
                        appendString = "<a href='#' class='list-group-item addSCNewVideo' title='" + result.title + "' url='" + result.permalink_url + "' type='2'>";
                        appendString += "<img class='searchImg' src='" + result.artwork_url + "' alt='sig'></img>";
                        appendString += "<div class='searchDesc'>";
                        appendString += "<span class='searchTitle'>" + newTitle + "</span>";
                        appendString += "<span class='searchTitleReal' style='display:none;'>" + result.title + "</span>";
                        appendString += "<span class='searchCount'>" + result.playback_count + " views</span>";
                        appendString += "<span class='searchDuration'>" + SEARCH.SCTimeCalc(result.duration) + "</span>";
                        appendString += "</div>";
                        appendString += "</a>";

                        $("#allTab").append(appendString);
                        $("#scTab").append(appendString);
                    }
                    $(".addSCNewVideo").click(function () {
                        MUSICLIST.AddNewVideoItem(VideoType.SoundCloud, $(this).attr("url"), $(this).find(".searchTitleReal").html(), $(this).find(".searchImg").attr("src"));
                    });
                }
            },
            function (result) {
                alert('Error occurred');
            }
        );
    },

    YTTimeCalc: function (sec) {
        var min = parseInt(sec / 60);
        var newSec = sec % 60;
        var hour = parseInt(min / 60);
        var newMin = min % 60;

        if (hour > 0) {
            if (newSec < 10) {
                newSec = "0" + newSec;
            }
            if (newMin < 10) {
                newMin = "0" + newMin;
            }
            return hour + ":" + newMin + ":" + newSec;
        } else if (min > 0) {
            if (newSec < 10) {
                newSec = "0" + newSec;
            }
            return newMin + ":" + newSec;
        } else {
            if (newSec < 10) {
                newSec = "0" + newSec;
            }
            return "0:" + newSec;
        }
    },

    SCTimeCalc: function (msec) {
        return SEARCH.YTTimeCalc(parseInt(msec / 1000));
    }
}

$(document).ready(function () {
    MUSICLIST.init();
    MusicPlayer.init();
});