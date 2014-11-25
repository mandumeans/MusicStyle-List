
var VideoType = Object.freeze({ 'YouTube': 1, 'SoundCloud': 2 });
var repeatable = true;
var nextVideoURL;
var SOUNDCLOUD_CLIENT_KEY = "a2b4d87e3bac428d8467d6ea343d49ae";


//This code loads the IFrame Player API code asynchronously.
var tag = document.createElement('script');
tag.src = 'https://www.youtube.com/iframe_api';
var firstScriptTag = document.getElementsByTagName('script')[0];
firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);


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

var Youtube = {
    //Client Youtube URL validation
    VideoURLValidation: function (UrlInput) {
        var p = /^(?:https?:\/\/)?(?:www\.)?(?:youtu\.be\/|youtube\.com\/(?:embed\/|v\/|watch\?v=|watch\?.+&v=))((\w|-){11})(?:\S+)?$/;
        return (UrlInput.match(p)) ? RegExp.$1 : false;
    },
    //Find youtube video id from URL
    FindUrlVideoID: function (url) {
        return url.substring(url.indexOf("v=") + 2, url.length);
    },
    //Client Youtube validation
    isVIDExist: function (VID) {
        if ($('.musicListItem[vid="' + VID + '"]').length < 1) {
            return true;
        } else {
            return false;
        }
    },
    //video ended go to next video
    onVideoEnded: function (URL) {
        MUSICLIST.playNextVideo(URL);
    },
    PlayYoutubeVideo: function (URL) {
        var VID = Youtube.FindUrlVideoID(URL);
        //Change isPlaying status attribue
        MUSICLIST.playingStatusChange(URL);

        //if there is video already, delete that one and make new division
        if ($('.video').find('iframe').length > 0) {
            $('.video').find('iframe').remove();
            $('.video').append('<div id="player"></div>');
        }

        var player;
        onYouTubeIframeAPIReady = function () {
            player = new YT.Player('player', {
                height: '315',
                width: '560',
                videoId: VID,
                events: {
                    'onReady': onPlayerReady,
                    'onStateChange': onPlayerStateChange
                }   //events
            }); //player
        }   //onYoutubeIframeAPIReady

        function onPlayerReady(event) {
            event.target.playVideo();
        }

        var done = false;
        function onPlayerStateChange(event) {
            if (event.data == YT.PlayerState.ENDED && !done) {
                Youtube.onVideoEnded(URL);
                done = true;
            }
        } //onPlayerStateChange

        onYouTubeIframeAPIReady();
    }
}

var Soundcloud = {
    PlaySoundcloudVideo: function (URL) {
        //Change isPlaying status attribue
        MUSICLIST.playingStatusChange(URL);

        //if there is video already, delete that one and make new division
        if ($('.video').find('iframe').length > 0) {
            $('.video').find('iframe').remove();
        }

        $('.video').append('<iframe id="sc-widget" src="" width="100%" height="465" scrolling="no" frameborder="no"></iframe>');

        var widgetIframe = document.getElementById('sc-widget');
        widgetIframe.src = 'https://w.soundcloud.com/player/?url=' + URL + '&amp;auto_play=true';

        var widget = SC.Widget(widgetIframe);

        widget.bind(SC.Widget.Events.FINISH, function () {
            Soundcloud.onVideoEnded(URL);
        });
    },

    //video ended go to next video
    onVideoEnded: function (URL) {
        MUSICLIST.playNextVideo(URL);
    }
}

var MUSICLIST = {
    init: function () {
        $('#btnURLInput').click(function () {
            var UrlInput = document.getElementById('txtURLInput').value;
            var nowVID = Youtube.FindUrlVideoID(UrlInput);

            if (Youtube.VideoURLValidation(UrlInput)) {

                Util.ajaxHelper('../MusicList.aspx/YoutubeURLValidation', '{"VID" : "' + nowVID + '"}',
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
                                MUSICLIST.AddNewVideoItem(VideoType.YouTube, UrlInput, rawResult);
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
                Util.ajaxHelper('../MusicList.aspx/SoundcloudURLValidation', '{"URL" : "' + UrlInput + '"}',
                    function (result) {
                        var rawResult = result.d;
                        if (rawResult === 'False') {
                            alert('There is no video on that URL from Soundcloud');
                        }
                        else {
                            var titleResult = jQuery.parseJSON(rawResult).title;
                            var uriResult = jQuery.parseJSON(rawResult).uri;
                            MUSICLIST.AddNewVideoItem(VideoType.SoundCloud, uriResult, titleResult);
                        }
                    },
                    function (result) {
                        alert('Error occurred');
                    }
                );
            }
        }); //btnURLInput click event end
        $('#btnSearchInput').click(function () {
            var UrlInput = document.getElementById('btnSearchInput').value;
            var searchKeyword = document.getElementById('txtSearchInput').value;

            Util.ajaxHelper('../MusicList.aspx/YoutubeKeywordSearch', '{"keyword" : "' + searchKeyword + '"}',
                function (result) {
                    var rawResult = jQuery.parseJSON(result.d);
                    if (rawResult === 'False') {
                        alert('There is no video on that URL');
                    }
                    else {
                        console.log(rawResult);
                        $('#searchModal').modal();
                        //for(resultOne in rawResult){
                        for (var i = 0; i < rawResult.length; i++) {
                            console.log(jQuery.parseJSON(rawResult[i]).id);
                            console.log(jQuery.parseJSON(rawResult[i]).title);
                            console.log(jQuery.parseJSON(rawResult[i]).thumbnail.sqDefault);
                            console.log(jQuery.parseJSON(rawResult[i]).thumbnail.hqDefault);
                            console.log(jQuery.parseJSON(rawResult[i]).viewCount);
                            console.log(jQuery.parseJSON(rawResult[i]).duration);
                        }
                    }
                },
                function (result) {
                    alert('Error occurred');
                }
            );

            Util.ajaxHelper('../MusicList.aspx/SoundcloudKeywordSearch', '{"keyword" : "' + searchKeyword + '"}',
                function (result) {
                    var rawResult = jQuery.parseJSON(result.d);
                    if (rawResult === 'False') {
                        alert('There is no video on that URL');
                    }
                    else {
                        console.log(rawResult);
                        //for(resultOne in rawResult){
                        for (var i = 0; i < rawResult.length; i++) {
                            console.log(jQuery.parseJSON(rawResult[i]).id);
                            console.log(jQuery.parseJSON(rawResult[i]).title);
                            console.log(jQuery.parseJSON(rawResult[i]).duration);
                            console.log(jQuery.parseJSON(rawResult[i]).stream_url);
                            console.log(jQuery.parseJSON(rawResult[i]).artwork_url);
                            console.log(jQuery.parseJSON(rawResult[i]).playback_count);
                        }
                    }
                },
                function (result) {
                    alert('Error occurred');
                }
            );
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
    },
    AddNewVideoItem: function (type, url, title) {
        var videoItemType;
        //var vid = Youtube.FindUrlVideoID(url);
        $('#playlist').append('<li class="musicListItem" type="' + type + '" url = "' + url + '" isPlaying = "false"><span class="musicItemTitle">' + title + '</span><a class="musicItemRemove">X</a></li>');
        $('.musicItemTitle').click(function () {
            if ($(this).parent().attr('type') == VideoType.YouTube) {
                //Youtube
                Youtube.PlayYoutubeVideo($(this).parent().attr('url'));
            } else {
                //SoundCloud
                Soundcloud.PlaySoundcloudVideo($(this).parent().attr('url'));
            }
        });
        $('.musicItemRemove').click(function () {
            MUSICLIST.DeleteVideoFromList($(this).parent().attr('url'));
        });
    },
    DeleteVideoFromList: function (URL) {
        nextVideoURL = MUSICLIST.getNextVideo(URL);
        $('.musicListItem[url="' + URL + '"]').remove();
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
            if (nextMusicURL === null) {
                if (repeatable === true) {
                    //it's last list item and repeatable -> go to first song
                    nextMusicURL = $('.musicListItem').first().attr('url');
                }
            }
        }
        return nextMusicURL;
    },

    playNextVideo: function (exURL) {
        var nextMusicURL = MUSICLIST.getNextVideo(exURL);
        var nextMusicType = $('.musicListItem[url="' + nextMusicURL + '"]').attr('type');

        if (nextMusicType == VideoType.YouTube) {
            Youtube.PlayYoutubeVideo(nextMusicURL);
        } else if (nextMusicType == VideoType.SoundCloud) {
            Soundcloud.PlaySoundcloudVideo(nextMusicURL);
        } else {
            console.log('There is no video to play');
        }
    }
}

$(document).ready(function () {
    MUSICLIST.init();    
});