

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
    },

    Pause: function (URL) {

    }
}