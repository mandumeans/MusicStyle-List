<%@ Page Title="Music List" Language="C#" MasterPageFile="~/Site.master" AutoEventWireup="true"
    CodeBehind="MusicList.aspx.cs" Inherits="MusicList.MusicList" %>

<asp:Content ID="HeaderContent" runat="server" ContentPlaceHolderID="HeadContent">
    <script src="Scripts/json2.js"></script>
    <script src="Scripts/MusicList.js"></script>
</asp:Content>
<asp:Content ID="BodyContent" runat="server" ContentPlaceHolderID="MainContent">
    <asp:ScriptManager ID="ScriptManager1" runat="server" EnablePageMethods="True">        
    </asp:ScriptManager>
    <div id="listLeft">
        <div class="content">
            <div class="search">
                <input type="url" id="txtURLInput" placeholder="Type URL"/>
                <input type="button" id="btnURLInput" value="Add"/>
            </div>
            <div class="video">
                <div id="player">
                </div>
            </div>
        </div>
    </div>
    <div id="listRight">
        <div class="content">
            <div class="playlistContent">
                <ul id="playlist">
                </ul>
            </div>
        </div>
    </div>
</asp:Content>
