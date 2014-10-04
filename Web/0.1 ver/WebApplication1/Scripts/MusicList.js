
var VideoType = Object.freeze({ "YouTube": 1, "SoundCloud": 2 });
var repeatable = true;
var nextVideoVID;

//This code loads the IFrame Player API code asynchronously.
var tag = document.createElement('script');
tag.src = 'https://www.youtube.com/iframe_api';
var firstScriptTag = document.getElementsByTagName('script')[0];
firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

var MUSICLIST = {
    init: function () {
        $('#btnURLInput').click(function () {
            var UrlInput = document.getElementById('txtURLInput').value;
            var nowVID = FindUrlVideoID(UrlInput);

            if (!YoutubeVideoURLValidation(UrlInput)) {
                alert('Invalid URL');
                return;
            }

            if (!isVIDExist(nowVID)) {
                alert('Already exists');
                return;
            }

            $.ajax({
                url: "../MusicList.aspx/YoutubeURLValidation",
                type: "POST",
                data: '{"VID" : "' + nowVID + '"}',
                contentType: "application/json",
                dataType: "JSON",
                timeout: 10000,
                success: function (result) {
                    var rawResult = jQuery.parseJSON(result).d;
                    if (rawResult === 'False') {
                        alert('There is no video on that URL');
                    }
                    else {
                        AddNewVideoItem(VideoType.YouTube, UrlInput, rawResult);
                    }
                },
                error: function (result) {
                    return result;
                }
            }); //ajax end
        }); //btnURLInput click event end
        $('#btnSearchInput').click(function () {
            var UrlInput = document.getElementById('btnSearchInput').value;
            var searchKeyword = document.getElementById('txtSearchInput').value;


            $.ajax({
                url: "../MusicList.aspx/YoutubeKeywordSearch",
                type: "POST",
                data: '{"keyword" : "' + searchKeyword + '"}',
                contentType: "application/json",
                dataType: "JSON",
                timeout: 10000,
                success: function (result) {
                    var rawResult = jQuery.parseJSON(result).d;
                    if (rawResult === 'False') {
                        alert('There is no video on that URL');
                    }
                    else {
                        AddNewVideoItem(VideoType.YouTube, UrlInput, rawResult);
                    }
                },
                error: function (result) {
                    return result;
                }
            }); //ajax end
        }); //btnSearchInput click event end
        $('#btnClearList').click(function () {
            $('.musicListItem').remove();
            nextVideoVID = null;
        });
    }
}

//Client Youtube validation
function isVIDExist(VID) {
    if ($('.musicListItem[vid="' + VID + '"]').length < 1) {
        return true;
    } else {
        return false;
    }
}

//Client Youtube URL validation
function YoutubeVideoURLValidation(UrlInput) {
    var p = /^(?:https?:\/\/)?(?:www\.)?(?:youtu\.be\/|youtube\.com\/(?:embed\/|v\/|watch\?v=|watch\?.+&v=))((\w|-){11})(?:\S+)?$/;
    return (UrlInput.match(p)) ? RegExp.$1 : false;
}

//Find youtube video id from URL
function FindUrlVideoID(url) {
    return url.substring(url.indexOf("v=") + 2, url.length);
}


function AddNewVideoItem(type, url, title){
    var videoItemType;
    var vid = FindUrlVideoID(url);
    if(type === VideoType.YouTube){
        videoItemType = "YouTube";
    } else if(type === VideoType.SoundCloud){
        videoItemType = "SoundCloud";
    }
    $('#playlist').append('<li class="musicListItem" type="' + videoItemType + '" vid = "' + vid + '" isPlaying = "false"><span class="musicItemTitle">' + title + '</span><a class="musicItemRemove">X</a></li>');
    $('.musicItemTitle').click(function () {
        PlayYoutubeVideo($(this).parent().attr('vid'));
    });
    $('.musicItemRemove').click(function () {
        DeleteVideoFromList($(this).parent().attr('vid'));
    });
}

function DeleteVideoFromList(VID) {
    nextVideoVID = getNextVideo(VID);
    $('.musicListItem[vid="' + VID + '"]').remove();
}

//video ended go to next video
function onVideoEnded(VID) {
    var nextMusicVID = getNextVideo(VID);
    if (nextMusicVID !== null) {
        PlayYoutubeVideo(nextMusicVID);
    }
}

function getNextVideo(VID) {
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


//Change isPlaying status attribue
function playingStatusChange(VID) {
    $('.musicListItem[isPlaying="true"]').attr('isPlaying', 'false').removeClass('musicPlaying');
    $('.musicListItem[vid="' + VID + '"]').attr('isPlaying', 'true').addClass('musicPlaying');
}

function PlayYoutubeVideo(VID) {
    //Change isPlaying status attribue
    playingStatusChange(VID);
    
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
            onVideoEnded(VID);
            done = true;
        }
    } //onPlayerStateChange

    onYouTubeIframeAPIReady();
}


$(document).ready(function () {
    MUSICLIST.init();    
});