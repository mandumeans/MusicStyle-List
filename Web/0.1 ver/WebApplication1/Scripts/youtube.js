

var YTPlayer;

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
        if (repeatable) {
            MUSICLIST.playNextVideo(URL);
        }
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

        onYouTubeIframeAPIReady = function () {
            //     height: '315',
            //width: '560',
            YTPlayer = new YT.Player('player', {
                videoId: VID,
                width: $('.video').css('width'),
                height: $('.video').css('height'),
                playerVars: {
                    controls: 0,
                    enablejsapi: 1,
                    rel: 0,
                    showinfo: 0,
                    autohide: 0,
                    modestbranding: 1,
                    theme: 'light',
                    wmode: 'transparent'
                },
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
            if (event.data == YT.PlayerState.PLAYING) {
                var size = YTPlayer.getDuration();
                $("#seekSlider").slider("option", "max", size);
                Youtube.seekbarEventStart();
            } else {
                Youtube.seekbarEventEnd();
            }

            if (event.data == YT.PlayerState.ENDED && !done) {
                Youtube.onVideoEnded(URL);
                done = true;
            }
        } //onPlayerStateChange

        onYouTubeIframeAPIReady();
    },

    Pause: function () {
        YTPlayer.pauseVideo();
    },
    reStart: function () {
        YTPlayer.playVideo();
    },
    stop: function () {
        YTPlayer.stopVideo();
    },
    seekTo: function (sec) {
        var now = YTPlayer.getCurrentTime();
        var newTime = sec + now;
        if (0 < newTime && newTime < YTPlayer.getDuration()) {
            YTPlayer.seekTo(newTime, true);
        }
    },
    seekToHere: function (sec) {
        var newTime = sec;
        if (0 < newTime && newTime < YTPlayer.getDuration()) {
            YTPlayer.seekTo(newTime, true);
        }
    },
    seekbarEventStart: function () {
        nowYTSeekbar = setInterval(function () {
            var now = YTPlayer.getCurrentTime();
            $("#seekSlider").slider("option", "value", now);
        }, 500);
    },
    seekbarEventEnd: function () {
        clearInterval(nowYTSeekbar);
    }
}
