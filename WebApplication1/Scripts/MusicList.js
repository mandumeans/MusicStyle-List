
var VideoType = Object.freeze({"YouTube":1, "SoundCloud":2});

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
                        console.log(rawResult);
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
        console.log($(this).attr("url"));
    });
}

$(document).ready(function () {
    MUSICLIST.init();    
});