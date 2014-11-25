<%@ Page Title="Music List" Language="C#" MasterPageFile="~/Site.master" AutoEventWireup="true"
    CodeBehind="MusicList.aspx.cs" Inherits="MusicList.MusicList" %>

<asp:Content ID="HeaderContent" runat="server" ContentPlaceHolderID="HeadContent">
    <script src="Scripts/MusicList.js"></script>
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
    <div id="listRight">
        <div class="content">
            <div class="controller">
                <div class="search">
                    <input type="text" class="form-control" id="txtSearchInput" placeholder="Type Keyword"/>
                    <input type="button" class="btn btn-primary" id="btnSearchInput" value="Search"/>
                    <div class="searchlistContent">
                        <ul id="searchlist">
                        </ul>
                    </div>
                </div>
            </div>
            <div class="controller">
                <div class="search">
                    <input type="url" class="form-control" id="txtURLInput" placeholder="Type URL"/>
                    <input type="button" class="btn btn-primary" id="btnURLInput" value="Add"/>
                    <input type="button" class="btn btn-primary" id="btnClearList" value="Clear all"/>
                </div>
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
                  <!-- Nav tabs -->
                  <div role="tabpanel">
                      <ul class="nav nav-tabs" role="tablist">
                        <li role="presentation" class="active"><a href="#allTab" aria-controls="allTab" role="tab" data-toggle="tab">All</a></li>
                        <li role="presentation"><a href="#ytTab" aria-controls="ytTab" role="tab" data-toggle="tab">Youtube only</a></li>
                        <li role="presentation"><a href="#scTab" aria-controls="scTab" role="tab" data-toggle="tab">SoundCloud only</a></li>
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                      </ul>
                  </div>
                  <!-- Tab panes -->
                  <div class="tab-content">
                    <div role="tabpanel" class="tab-pane active" id="allTab">get all</div>
                    <div role="tabpanel" class="tab-pane" id="ytTab">YT</div>
                    <div role="tabpanel" class="tab-pane" id="scTab">SC</div>
                  </div>
              </div>
              <div class="modal-footer">
              </div>
            </div><!-- /.modal-content -->
          </div><!-- /.modal-dialog -->
        </div><!-- /.modal -->
    </div>
</asp:Content>
