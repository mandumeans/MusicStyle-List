
var VideoType = Object.freeze({ 'YouTube': 1, 'SoundCloud': 2 });
var repeatable = true;
var nextVideoURL;
var SOUNDCLOUD_CLIENT_KEY = "a2b4d87e3bac428d8467d6ea343d49ae";

var YTPlayer;

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


var PlayingType = Object.freeze({ 'playing': 1, 'paused': 2 });


var rightListStatus;

var MUSICLIST = {
    init: function () {
        //$('#listRight').hide();
        rightListStatus = false;

        $(window).resize(function () {
            if ($('.video').find('iframe').length > 0) {
                $('.video').find('iframe').css('width',$('.video').css('width'))
                $('.video').find('iframe').css('height', $('.video').css('height'))
            }
        });
        /*
        $(document).mousemove(function (e) {
            var pageWidth = $('html').css('width').replace(/([\d.]+)(px|pt|em|%)/, '$1');
            var pageHeight = $('html').css('height').replace(/([\d.]+)(px|pt|em|%)/, '$1');
            var rightListWidth = $('#listRight').css('width').replace(/([\d.]+)(px|pt|em|%)/, '$1');
            var rightListHeight = $('#listRight').css('height').replace(/([\d.]+)(px|pt|em|%)/, '$1');

            if ($('.video').find('iframe').length > 0) {
                if ($('.video').find('iframe').css('pointer-events') == 'auto') {
                    $('.video').find('iframe').css('pointer-events', 'none');
                }
            }
            //width 80%~100%
            //height 13%~68%
            if (rightListStatus == true) {
                if (e.pageX < (pageWidth - rightListWidth - 50)) {
                    //right list로부터 벗어남
                    $('#listRight').hide();
                    rightListStatus = false;
                }
            }

            if ((((e.pageX / pageWidth) > 0.8) && (((e.pageY / pageHeight) > 0.13) && ((e.pageY / pageHeight) < 0.68))) && rightListStatus == false) {
                $('#listRight').show();
                rightListStatus = true;
            }
        });
        */

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
    getPreviousVideo: function (URL) {
        var prevMusicURL = null;

        //There is no now playing video (playing video has been deleted)
        if ($('.musicListItem[isPlaying="true"]').length < 1) {
            if ($('.musicListItem').length > 0) {
                prevMusicURL = $('.musicListItem').first().attr("url");    //when video is deleted, first URL is saved.;
            }
        }
        else {
            prevMusicURL = $('.musicListItem[url="' + URL + '"]').prev('li').attr('url');
            if (prevMusicURL === null) {
                if (repeatable === true) {
                    //it's last list item and repeatable -> go to first song
                    prevMusicURL = $('.musicListItem').last().attr('url');
                }
            }
        }
        return prevMusicURL;
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
    },

    playPreviousVideo: function (exURL) {
        var prevMusicURL = MUSICLIST.getPreviousVideo(exURL);
        var prevMusicType = $('.musicListItem[url="' + prevMusicURL + '"]').attr('type');

        if (prevMusicType == VideoType.YouTube) {
            Youtube.PlayYoutubeVideo(prevMusicURL);
        } else if (prevMusicType == VideoType.SoundCloud) {
            Soundcloud.PlaySoundcloudVideo(prevMusicURL);
        } else {
            console.log(prevMusicURL);
        }
    }
}

$(document).ready(function () {
    MUSICLIST.init();
    MusicPlayer.init();
});