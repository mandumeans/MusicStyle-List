

var SOUNDCLOUD_THUM_T500X500 = "t500x500";
var SOUNDCLOUD_THUM_LARGE = "large";
var widget;
var SOUNDCLOUD_CLIENT_KEY = "a2b4d87e3bac428d8467d6ea343d49ae";

var Soundcloud = {
    PlaySoundcloudVideo: function (URL, thumb) {
        //Change isPlaying status attribue
        MUSICLIST.playingStatusChange(URL);

        //if there is video already, delete that one and make new division
        if ($('.video').find('iframe').length > 0) {
            $('.video').find('iframe').remove();
        }
        if ($('.video').find('img').length > 0) {
            $('.video').find('img').remove();
        }

        $("#player").append('<iframe id="sc-widget" src="" width="100%" height="465" scrolling="no" frameborder="no" style="position:absolute;top:-10000px;left:-10000px;"></iframe>');

        var widgetIframe = document.getElementById('sc-widget');
        widgetIframe.src = "https://w.soundcloud.com/player/?url=" + URL + "&amp;auto_play=true";
        widgetIframe.src += "&amp;buying=false";
        widgetIframe.src += "&amp;sharing=false";
        widgetIframe.src += "&amp;download=false";
        widgetIframe.src += "&amp;show_bpm=false";
        widgetIframe.src += "&amp;show_playcount=false";
        widgetIframe.src += "&amp;liking=false";
        //widgetIframe.src += "&amp;show_artwork=false";
        widgetIframe.src += "&amp;show_comments=false";
        widgetIframe.src += "&amp;show_user=false";
        //$("#player iframe").hide();
        thumb = thumb.replace(SOUNDCLOUD_THUM_LARGE, SOUNDCLOUD_THUM_T500X500);
        $("#player").append("<img src='" + thumb + "'></img>");

        //console.log($("#sc-widget").find('img').attr("src"));
        widget = SC.Widget(widgetIframe);


        widget.bind(SC.Widget.Events.READY, function () {
            widget.getDuration(function (duration) {
                $("#seekSlider").slider("option", "max", duration / 1000);
            });
        });

        widget.bind(SC.Widget.Events.FINISH, function () {
            Soundcloud.onVideoEnded(URL);
        });

        widget.bind(SC.Widget.Events.PLAY_PROGRESS, function (now) {
            $("#seekSlider").slider("option", "value", now.currentPosition / 1000);
        });
    },

    //video ended go to next video
    onVideoEnded: function (URL) {
        if (repeatable) {
            MUSICLIST.playNextVideo(URL);
        }
    },

    Pause: function () {
        widget.toggle();
    },

    reStart: function () {
        widget.toggle();
    },

    stop: function () {

    },

    seekTo: function (sec) {
        widget.getPosition(function (pos) {
            widget.seekTo(pos + (sec * 1000));
        });
    },
    seekToHere: function (sec) {
        widget.getPosition(function (pos) {
            var newTime = sec * 1000;
            //if (0 < newTime && newTime < pos) {
            widget.seekTo(newTime);
            //}
        });
    },
    seekbarEventStart: function () {
        //nowYTSeekbar = setInterval(function () {
        //    var now = YTPlayer.getCurrentTime();
        //    $("#seekSlider").slider("option", "value", now);
        //}, 500);
        widget.bind(SC.Widget.Events.PLAY_PROGRESS, function (now) {
            $("#seekSlider").slider("option", "value", now.currentPosition / 1000);
        });
    },
    seekbarEventEnd: function () {
        widget.unbind(SC.Widget.Events.PLAY_PROGRESS);
    }
}