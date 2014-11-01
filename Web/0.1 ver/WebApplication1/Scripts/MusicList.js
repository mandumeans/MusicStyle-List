
var VideoType = Object.freeze({ "YouTube": 1, "SoundCloud": 2 });
var repeatable = true;
var nextVideoVID;

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
    VideoURLValidation : function (UrlInput) {
        var p = /^(?:https?:\/\/)?(?:www\.)?(?:youtu\.be\/|youtube\.com\/(?:embed\/|v\/|watch\?v=|watch\?.+&v=))((\w|-){11})(?:\S+)?$/;
        return (UrlInput.match(p)) ? RegExp.$1 : false;
    },
    //Find youtube video id from URL
    FindUrlVideoID : function (url) {
        return url.substring(url.indexOf("v=") + 2, url.length);
    },
    //Client Youtube validation
    isVIDExist : function (VID) {
        if ($('.musicListItem[vid="' + VID + '"]').length < 1) {
            return true;
        } else {
            return false;
        }
    },
    //video ended go to next video
    onVideoEnded : function (VID) {
        var nextMusicVID = MUSICLIST.getNextVideo(VID);
        if (nextMusicVID !== null) {
            Youtube.PlayYoutubeVideo(nextMusicVID);
        }
    },
    PlayYoutubeVideo : function (VID) {
        //Change isPlaying status attribue
        MUSICLIST.playingStatusChange(VID);
    
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
                Youtube.onVideoEnded(VID);
                done = true;
            }
        } //onPlayerStateChange

        onYouTubeIframeAPIReady();
    }
}

var Soundcloud = {

}

var MUSICLIST = {
    init: function () {
        $('#btnURLInput').click(function () {
            var UrlInput = document.getElementById('txtURLInput').value;
            var nowVID = Youtube.FindUrlVideoID(UrlInput);

            if (Youtube.VideoURLValidation(UrlInput)) {

                Util.ajaxHelper('../MusicList.aspx/YoutubeURLValidation', '{"VID" : "' + nowVID + '"}',
                    function (result) {
                        var rawResult = jQuery.parseJSON(result).d;
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
                        var rawResult = jQuery.parseJSON(result).d;
                        if (rawResult === 'False') {
                            alert('There is no video on that URL from Soundcloud');
                        }
                        else {
                            MUSICLIST.AddNewVideoItem(VideoType.SoundCloud, UrlInput, rawResult);
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
                    var rawResult = jQuery.parseJSON(jQuery.parseJSON(result).d);
                    if (rawResult === 'False') {
                        alert('There is no video on that URL');
                    }
                    else {
                        console.log(rawResult);
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
                    var rawResult = jQuery.parseJSON(jQuery.parseJSON(result).d);
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
            nextVideoVID = null;
        });
    },
    AddNewVideoItem: function (type, url, title) {
        var videoItemType;
        var vid = Youtube.FindUrlVideoID(url);
        if (type === VideoType.YouTube) {
            videoItemType = "YouTube";
        } else if (type === VideoType.SoundCloud) {
            videoItemType = "SoundCloud";
        }
        $('#playlist').append('<li class="musicListItem" type="' + videoItemType + '" vid = "' + vid + '" isPlaying = "false"><span class="musicItemTitle">' + title + '</span><a class="musicItemRemove">X</a></li>');
        $('.musicItemTitle').click(function () {
            Youtube.PlayYoutubeVideo($(this).parent().attr('vid'));
        });
        $('.musicItemRemove').click(function () {
            MUSICLIST.DeleteVideoFromList($(this).parent().attr('vid'));
        });
    },
    DeleteVideoFromList: function (VID) {
        nextVideoVID = MUSICLIST.getNextVideo(VID);
        $('.musicListItem[vid="' + VID + '"]').remove();
    },
    //Change isPlaying status attribue
    playingStatusChange: function (VID) {
        $('.musicListItem[isPlaying="true"]').attr('isPlaying', 'false').removeClass('musicPlaying');
        $('.musicListItem[vid="' + VID + '"]').attr('isPlaying', 'true').addClass('musicPlaying');
    },

    getNextVideo: function (VID) {
        var nextMusicVID = null;

        //There is no now playing video (playing video has been deleted)
        if ($('.musicListItem[isPlaying="true"]').length < 1) {
            if ($('.musicListItem').length > 0) {
                nextMusicVID = nextVideoVID;    //when video is deleted, next Video VID is saved;
            }
        }
        else {
            nextMusicVID = $('.musicListItem[vid="' + VID + '"]').next('li').attr('vid');
            if (nextMusicVID === null) {
                if (repeatable === true) {
                    //it's last list item and repeatable -> go to first song
                    nextMusicVID = $('.musicListItem').first().attr('vid');
                }
            }
        }
        return nextMusicVID;
    }
}

$(document).ready(function () {
    MUSICLIST.init();    
});