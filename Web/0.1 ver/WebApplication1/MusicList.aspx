<%@ Page Title="Music List" Language="C#" MasterPageFile="~/Site.master" AutoEventWireup="true"
    CodeBehind="MusicList.aspx.cs" Inherits="MusicList.MusicList" %>

<asp:Content ID="HeaderContent" runat="server" ContentPlaceHolderID="HeadContent">
    <script src="Scripts/MusicList.js"></script>
    <script src="Scripts/musicPlayer.js"></script>
    <script src="Scripts/soundcloud.js"></script>
    <script src="Scripts/youtube.js"></script>
    <script src="Scripts/json2.js"></script>
    <script src="Scripts/SCWidgetAPI.js"></script>
    <script src="http://connect.soundcloud.com/sdk.js"></script>
    <script src="Scripts/bootstrap.min.js"></script>
    <link href="Styles/bootstrap.min.css" rel="stylesheet" type="text/css" />

</asp:Content>
<asp:Content ID="BodyContent" runat="server" ContentPlaceHolderID="MainContent">
    <asp:ScriptManager ID="ScriptManager1" runat="server" EnablePageMethods="True">        
    </asp:ScriptManager>
    <div id="listLeft">
        <div class="content">
            <div class="video">
                <div id="player">
                </div>
            </div>
        </div>
    </div>
    <div class="player">
        <div class="playerControl">
            <div id="seekSlider" class="seeker"></div>
            <a class="replay"><img src="images/replayOn.png" alt="replay"/></a>
            <a class="ToLeft"><img src="images/ToLeft.png" alt="toLeft"/></a>
            <a class="leftwind"><img src="images/leftwind.png" alt="left"/></a>
            <a class="play"><img src="images/play.png" alt="play"/></a>
            <a class="pause"><img src="images/pause.png" style="display:none;" alt="pause"/></a>
            <a class="rightwind"><img src="images/rightwind.png" alt="right"/></a>
            <a class="ToRight"><img src="images/ToRight.png" alt="toRight"/></a>
            <a class="shuffle"><img src="images/shuffleOff.png" alt="shuffle"/></a>

        </div>
    </div>
    <div id="listRight">
        <div class="content">
            <div class="controller">
                <div class="search">
                    <input type="text" class="form-control searchTxt" id="txtSearchInput" placeholder="Type Keyword"/>
                    <input type="button" class="btn btn-primary searchBtn" id="btnSearchInput" value="Search"/>
                </div>
            </div>
            <div class="controller">
                <div class="search">
                    <input type="url" class="form-control" id="txtURLInput" placeholder="Type URL"/>
                    <input type="button" class="btn btn-primary" id="btnURLInput" value="Add"/>
                    <input type="button" class="btn btn-primary" id="btnClearList" value="Clear all"/>
                </div>
            </div>
            <div class="playlistNavigation">
                <select class="form-control playListNav">
                    <option>List1</option>
                    <option>POP</option>
                    <option>JAZZ</option>
                </select>
                <a class="listButton"><img src="images/plus.png" alt="addList"/></a>
                <a class="listButton"><img src="images/rename.png" alt="renameList"/></a>
                <a class="listButton"><img src="images/trash.png" alt="deleteList"/></a>
                <!--<input type="button" class="btn btn-primary" id="btnAddList" value="Add"/>
                <input type="button" class="btn btn-primary" id="btnRenameList" value="Rename"/>
                <input type="button" class="btn btn-primary" id="btnDeleteList" value="Delete"/>-->
            </div>
            <div class="playlistContent">
                <ul id="playlist">
                </ul>
            </div>
        </div>
        <div id = "searchModal" class="modal fade">
          <div class="modal-dialog">
            <div class="modal-content">
              <div class="modal-body">
                  <!-- search part -->
                <div class="search modalSearch">
                    <input type="text" class="form-control searchTxt" id="txtSearchInput2" placeholder="Type Keyword"/>
                    <input type="button" class="btn btn-primary searchBtn" id="btnSearchInput2" value="Search"/>
                        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                </div>
                  <!-- Nav tabs -->
                  <div role="tabpanel">
                      <ul class="nav nav-tabs" role="tablist">
                        <li role="presentation" class="active"><a href="#allTabPanel" aria-controls="allTab" role="tab" data-toggle="tab">All</a></li>
                        <li role="presentation"><a href="#ytTabPanel" aria-controls="ytTab" role="tab" data-toggle="tab">Youtube only</a></li>
                        <li role="presentation"><a href="#scTabPanel" aria-controls="scTab" role="tab" data-toggle="tab">SoundCloud only</a></li>
                      </ul>
                  </div>
                  <!-- Tab panes -->
                  <div class="tab-content">
                    <div role="tabpanel" class="tab-pane active" id="allTabPanel">
                        <ul class="searchedList list-group" id="allTab">
                        </ul>
                    </div>
                    <div role="tabpanel" class="tab-pane" id="ytTabPanel">
                        <ul class="searchedList list-group" id="ytTab">
                        </ul>
                    </div>
                    <div role="tabpanel" class="tab-pane" id="scTabPanel">
                        <ul class="searchedList list-group" id="scTab">
                        </ul>
                    </div>
                  </div>
              </div>
              <!--
              <div class="modal-footer">
              </div>-->
            </div><!-- /.modal-content -->
          </div><!-- /.modal-dialog -->
        </div><!-- /.modal -->
    </div>
</asp:Content>
