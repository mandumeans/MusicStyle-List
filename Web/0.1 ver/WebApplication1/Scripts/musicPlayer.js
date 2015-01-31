
var nowYTSeekbar;
var MusicPlayer = {

    init: function () {
        var seekSec = 10;
        var timeout;

        $("#seekSlider").slider(
        {
            min: 0,
            max: 100,
            step: 1
        });
        $("#seekSlider").on("slidechange", function (event, ui) {
            if (event.originalEvent) {
                //manual change
                console.log(ui.value + "," + YTPlayer.getCurrentTime());
                MusicPlayer.seekToHere(ui.value);
            } else {
                //programmatic change
            }
        });

        $("#seekSlider").mousedown(function () {
            Youtube.seekbarEventEnd();
            console.log("Adsf");
        }).mouseup(function () {
            console.log("Adsf2");
            Youtube.seekbarEventStart();
        }).mouseout(function () {
            console.log("Adsf3");
           // Youtube.seekbarEventStart();
        });

        $(".play").on("click", function () {
            MusicPlayer.onPauseClicked();
        });
        $(".ToRight").on("click", function () {
            var nowMusicURL = $('.musicListItem[isPlaying="true"]').attr('url');
            MUSICLIST.playNextVideo(nowMusicURL);
        });
        $(".ToLeft").on("click", function () {
            var nowMusicURL = $('.musicListItem[isPlaying="true"]').attr('url');
            MUSICLIST.playPreviousVideo(nowMusicURL);
        });
        $(".rightwind").mousedown(function () {
            var nowMusicURL = $('.musicListItem[isPlaying="true"]').attr('url');
            timeout = setInterval(function () { MusicPlayer.seekTo(seekSec) }, 500);
            return false;
        }).mouseup(function () {
            clearInterval(timeout);
            return false;
        }).mouseout(function () {
            clearInterval(timeout);
            return false;
        });
        $(".leftwind").mousedown(function () {
            var nowMusicURL = $('.musicListItem[isPlaying="true"]').attr('url');
            timeout = setInterval(function () { MusicPlayer.seekTo(seekSec * -1) }, 10);
        }).mouseup(function () {
            clearInterval(timeout);
            return false;
        }).mouseout(function () {
            clearInterval(timeout);
            return false;
        });
    },

    playInit: function () {

    },

    onPauseClicked: function () {
        var nowMusicURL = $('.musicListItem[isPlaying="true"]').attr('url');
        var nowMusicType = $('.musicListItem[isPlaying="true"]').attr('type');

        if (nowMusicType == VideoType.YouTube) {
            if (YTPlayer.getPlayerState() == YT.PlayerState.PLAYING) {//재생중 
                Youtube.Pause();
            } else if (YTPlayer.getPlayerState() == YT.PlayerState.PAUSED) {
                Youtube.reStart();
            }
        } else if (nowMusicType == VideoType.SoundCloud) {
            Soundcloud.Pause(nowMusicURL);
        } else {
            console.log('There is no video to play');
        }
    },

    seekTo: function (sec) {

        var nowMusicURL = $('.musicListItem[isPlaying="true"]').attr('url');
        var nowMusicType = $('.musicListItem[isPlaying="true"]').attr('type');

        if (nowMusicType == VideoType.YouTube) {
            Youtube.seekTo(sec);
        } else if (nowMusicType == VideoType.SoundCloud) {
            Soundcloud.seekTo(sec);
        } else {
            console.log("There is no video to play");
        }
    },

    seekToHere: function (sec) {

        var nowMusicURL = $('.musicListItem[isPlaying="true"]').attr('url');
        var nowMusicType = $('.musicListItem[isPlaying="true"]').attr('type');

        if (nowMusicType == VideoType.YouTube) {
            Youtube.seekToHere(sec);
        } else if (nowMusicType == VideoType.SoundCloud) {
            Soundcloud.seekToHere(sec);
        } else {
            console.log("There is no video to play");
        }
    }
}