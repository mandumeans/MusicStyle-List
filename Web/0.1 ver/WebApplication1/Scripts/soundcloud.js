

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

        $("#player").append('<iframe id="sc-widget" src="" width="100%" height="465" scrolling="no" frameborder="no"></iframe>');

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
        $("#player iframe").hide();
        $("#player").append("<img src='" + thumb + "' style='height:100%;width:auto;'></img>");
        $("#player").css("background-image", "url('images/sc.jpg')");
        $("#player").css("background-size", "100% 100%");
        $("#player").css("background-repeat", "no-repeat");

        //console.log($("#sc-widget").find('img').attr("src"));
        var widget = SC.Widget(widgetIframe);

        widget.bind(SC.Widget.Events.FINISH, function () {
            Soundcloud.onVideoEnded(URL);
        });
    },

    //video ended go to next video
    onVideoEnded: function (URL) {
        MUSICLIST.playNextVideo(URL);
    },

    Pause: function (URL) {

    }
}