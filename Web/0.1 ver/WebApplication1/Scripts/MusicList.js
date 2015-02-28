
var VideoType = Object.freeze({ 'YouTube': 1, 'SoundCloud': 2 });
var repeatable = true;
var nextVideoURL;

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
                $('.video').find('iframe').css('width', $('.video').css('width'))
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
                                MUSICLIST.AddNewVideoItem(VideoType.YouTube, UrlInput, rawResult, '');
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
                            var thumbResult = jQuery.parseJSON(rawResult).thumb;
                            MUSICLIST.AddNewVideoItem(VideoType.SoundCloud, uriResult, titleResult, thumbResult);
                        }
                    },
                    function (result) {
                        alert('Error occurred');
                    }
                );
            }
        }); //btnURLInput click event end
        $('#btnSearchInput').click(function () {
            var searchKeyword = document.getElementById('txtSearchInput').value;

            SEARCH.init(searchKeyword);
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
    AddNewVideoItem: function (type, url, title, thumb) {
        var videoItemType;
        //var vid = Youtube.FindUrlVideoID(url);
        $('#playlist').append('<li class="musicListItem" type="' + type + '" url = "' + url + '" thumb = "' + thumb + '" isPlaying = "false"><span class="musicItemTitle">' + title + '</span><a class="musicItemRemove">X</a></li>');
        $('.musicItemTitle').click(function () {
            if ($(this).parent().attr('type') == VideoType.YouTube) {
                //Youtube
                Youtube.PlayYoutubeVideo($(this).parent().attr('url'));
            } else {
                //SoundCloud
                Soundcloud.PlaySoundcloudVideo($(this).parent().attr('url'), $(this).parent().attr('thumb'));
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
            Soundcloud.PlaySoundcloudVideo(nextMusicURL, $('.musicListItem[url="' + nextMusicURL + '"]').attr('thumb'));
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
            Soundcloud.PlaySoundcloudVideo(prevMusicURL, $('.musicListItem[url="' + prevMusicURL + '"]').attr('thumb'));
        } else {
            console.log(prevMusicURL);
        }
    }
}

var SEARCH_TITLE_MAXLENGTH = 40;

var SEARCH = {
    clearify: function () {
        $("#allTab").empty();
        $("#ytTab").empty();
        $("#scTab").empty();
    },
    init: function (searchKeyword) {
        //Youtube Search
        Util.ajaxHelper('../MusicList.aspx/YoutubeKeywordSearch', '{"keyword" : "' + searchKeyword + '"}',
            function (result) {
                var rawResult = jQuery.parseJSON(result.d);
                if (rawResult === 'False') {
                    alert('There is no video on that URL');
                }
                else {
                    SEARCH.clearify();
                    $('#searchModal').modal();
                    for (var i = 0; i < rawResult.length; i++) {
                        var result = jQuery.parseJSON(rawResult[i]);
                        var newTitle = result.title;
                        if (result.title.length > SEARCH_TITLE_MAXLENGTH - 1) {
                            newTitle = newTitle.substring(0, SEARCH_TITLE_MAXLENGTH);
                            newTitle += "...";
                        }
                        if (result.artwork_url == "") {
                            result.artwork_url = "images/noimg.png";
                        }
                        appendString = "<a href='#' class='list-group-item' title='" + result.title + "' vid='" + result.id + "' type='1'>";
                        appendString += "<img class='searchImg' src='" + result.thumbnail.sqDefault + "' alt='sig'></img>";
                        appendString += "<div class='searchDesc'>";
                        appendString += "<span>" + newTitle + "</span>";
                        appendString += "<span class='searchCount'>" + result.viewCount + " views</span>";
                        appendString += "<span class='searchDuration'>" + SEARCH.YTTimeCalc(result.duration) + "</span>";
                        appendString += "</div>";
                        appendString += "</a>";

                        $("#allTab").append(appendString);
                        $("#ytTab").append(appendString);
                    }
                    SEARCH.soundCloudInit(searchKeyword);
                }
            },
            function (result) {
                alert('Error occurred');
            }
        );
    },

    soundCloudInit: function (searchKeyword) {
        //Soundcloud Search
        Util.ajaxHelper('../MusicList.aspx/SoundcloudKeywordSearch', '{"keyword" : "' + searchKeyword + '"}',
            function (result) {
                var rawResult = jQuery.parseJSON(result.d);
                if (rawResult === 'False') {
                    alert('There is no video on that URL');
                }
                else {
                    for (var i = 0; i < rawResult.length; i++) {
                        var result = jQuery.parseJSON(rawResult[i]);
                        var newTitle = result.title;
                        var newDuration = result.duration;
                        if (result.title.length > SEARCH_TITLE_MAXLENGTH - 1) {
                            newTitle = newTitle.substring(0, SEARCH_TITLE_MAXLENGTH);
                            newTitle += "...";
                        }
                        if (result.artwork_url == "") {
                            result.artwork_url = "images/noimg.png";
                        }
                        appendString = "<a href='#' class='list-group-item' title='" + result.title + "' vid='" + result.id + "' type='2'>";
                        appendString += "<img class='searchImg' src='" + result.artwork_url + "' alt='sig'></img>";
                        appendString += "<div class='searchDesc'>";
                        appendString += "<span>" + newTitle + "</span>";
                        appendString += "<span class='searchCount'>" + result.playback_count + " views</span>";
                        appendString += "<span class='searchDuration'>" + SEARCH.SCTimeCalc(result.duration) + "</span>";
                        appendString += "</div>";
                        appendString += "</a>";

                        $("#allTab").append(appendString);
                        $("#scTab").append(appendString);
                    }
                }
            },
            function (result) {
                alert('Error occurred');
            }
        );
    },
    youtubeSearch: function (searchKeyword) {
        //Youtube Search
        Util.ajaxHelper('../MusicList.aspx/YoutubeKeywordSearch', '{"keyword" : "' + searchKeyword + '"}',
            function (result) {
                var rawResult = jQuery.parseJSON(result.d);
                if (rawResult === 'False') {
                    alert('There is no video on that URL');
                }
                else {
                    $('#searchModal').modal();
                    for (var i = 0; i < rawResult.length; i++) {
                        var result = jQuery.parseJSON(rawResult[i]);
                        var newTitle = result.title;
                        if (result.title.length > SEARCH_TITLE_MAXLENGTH - 1) {
                            newTitle = newTitle.substring(0, SEARCH_TITLE_MAXLENGTH);
                            newTitle += "...";
                        }
                        if (result.artwork_url == "") {
                            result.artwork_url = "images/noimg.png";
                        }
                        appendString = "<a href='#' class='list-group-item' title='" + result.title + "' vid='" + result.id + "' type='1'>";
                        appendString += "<img class='searchImg' src='" + result.thumbnail.sqDefault + "' alt='sig'></img>";
                        appendString += "<div class='searchDesc'>";
                        appendString += "<span>" + newTitle + "</span>";
                        appendString += "<span class='searchCount'>" + result.viewCount + " views</span>";
                        appendString += "<span class='searchDuration'>" + SEARCH.YTTimeCalc(result.duration) + "</span>";
                        appendString += "</div>";
                        appendString += "</a>";

                        $("#allTab").append(appendString);
                    }
                }
            },
            function (result) {
                alert('Error occurred');
            }
        );
    },

    soundCloudSearch: function (searchKeyword) {
        //Soundcloud Search
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
    },

    YTTimeCalc: function (sec) {
        var min = parseInt(sec / 60);
        var newSec = sec % 60;
        var hour = parseInt(min / 60);
        var newMin = min % 60;

        if (hour > 0) {
            if (newSec < 10) {
                newSec = "0" + newSec;
            }
            if (newMin < 10) {
                newMin = "0" + newMin;
            }
            return hour + ":" + newMin + ":" + newSec;
        } else if (min > 0) {
            if (newSec < 10) {
                newSec = "0" + newSec;
            }
            return newMin + ":" + newSec;
        } else {
            if (newSec < 10) {
                newSec = "0" + newSec;
            }
            return "0:" + newSec;
        }
    },

    SCTimeCalc: function (msec) {
        return SEARCH.YTTimeCalc(parseInt(msec / 1000));
    }
}

$(document).ready(function () {
    MUSICLIST.init();
    MusicPlayer.init();
});