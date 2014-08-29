
var VideoType = Object.freeze({ "YouTube": 1, "SoundCloud": 2 });

//This code loads the IFrame Player API code asynchronously.
//var tag = document.createElement('script');
//tag.src = 'https://www.youtube.com/iframe_api';
//var firstScriptTag = document.getElementsByTagName('script')[0];
//firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

var player;
onYouTubeIframeAPIReady = function () {
    console.log('asdf');
    player = new YT.Player('video', {
        height: '560',
        width: '315',
        videoId: 'd6MP8QM3_PI',
        events: {
            'onReady': onPlayerReady,
            'onStatusChange': onPlayerStateChange
        }   //events
    }); //player
}   //onYoutubeIframeAPIReady

function onPlayerReady(event) {
    event.target.playVideo();
}

var done = false;
function onPlayerStateChange(event) {
    if (event.data == YT.PlayerState.PLAYING && !done) {
        setTimeout(stopVideo, 6000);
        done = true;
    }
} //onPlayerStateChange

function stopVideo() {
    player.stopVideo();
} //stopVideo
    

var MUSICLIST = {
    init: function () {
        $('#btnURLInput').click(function () {
            var UrlInput = document.getElementById('txtURLInput').value;
            if (!YoutubeVideoUrlValidation(UrlInput)) {
                alert('Invalid URL');
                return;
            }

            $.ajax({
                url: "../MusicList.aspx/YoutubeURLValidation",
                type: "POST",
                data: '{"VID" : "' + FindUrlVideoID(UrlInput) + '"}',
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
    }
}

//Client Youtube URL validation
function YoutubeVideoUrlValidation(UrlInput) {
    var p = /^(?:https?:\/\/)?(?:www\.)?(?:youtu\.be\/|youtube\.com\/(?:embed\/|v\/|watch\?v=|watch\?.+&v=))((\w|-){11})(?:\S+)?$/;
    return (UrlInput.match(p)) ? RegExp.$1 : false;
}

//Find youtube video id from URL
function FindUrlVideoID(url) {
    return url.substring(url.indexOf("v=") + 2, url.length);
}


function AddNewVideoItem(type, url, title){
    var videoItemType;
    if(type === VideoType.YouTube){
        videoItemType = "YouTube";
    } else if(type === VideoType.SoundCloud){
        videoItemType = "SoundCloud";
    }
    $('#playlist').append("<li class='musicListItem' type='" + videoItemType + "' url = '" + url + "'>" + title + "</li>");
    $('.musicListItem').click(function () {
        PlayYoutubeVideo(FindUrlVideoID($(this).attr("url")));
    });
}

function PlayYoutubeVideo(VID){
    if ($('#video').find('iframe').length > 0) {
        $('#video').find('iframe').remove();
    }

    //$('#video').append('<iframe width="560" height="315" src="//www.youtube.com/embed/' + VID + '" frameborder="0" allowfullscreen></iframe>');

}

$(document).ready(function () {
    MUSICLIST.init();    
});