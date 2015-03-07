
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
                MusicPlayer.seekToHere(ui.value);
            } else {
                //programmatic change
            }
        });

        $("#seekSlider").mousedown(function () {
            var nowMusicType = $('.musicListItem[isPlaying="true"]').attr('type');
            if (nowMusicType == VideoType.YouTube) {
                Youtube.seekbarEventEnd();
            } else if (nowMusicType == VideoType.SoundCloud) {
                Soundcloud.seekbarEventEnd();
            }
        }).mouseup(function () {
            var nowMusicType = $('.musicListItem[isPlaying="true"]').attr('type');
            if (nowMusicType == VideoType.YouTube) {
                Youtube.seekbarEventStart();
            } else if (nowMusicType == VideoType.SoundCloud) {
                Soundcloud.seekbarEventStart();
            }
        }).mouseout(function () {

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
        $(".replay").on("click", function () {
            if (repeatable) {
                //now repeatble
                repeatable = false;
                $(".replay img").attr("src", "images/replayOff.png");
            } else {
                repeatable = true;
                $(".replay img").attr("src", "images/replayOn.png");
            }
        });
        $(".shuffle").on("click", function () {
            if (shuffle) {
                //now shuffle
                shuffle = false;
                $(".shuffle img").attr("src", "images/shuffleOff.png");
            } else {
                shuffle = true;
                $(".shuffle img").attr("src", "images/shuffleOn.png");
            }
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
            timeout = setInterval(function () { MusicPlayer.seekTo(seekSec * -1) }, 500);
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
            Soundcloud.Pause();
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