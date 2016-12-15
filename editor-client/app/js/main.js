"use strict";
// these are the lines that don't work in the broswer yet becuase require is part of node
// we need to use broswerify or require js here and edit the gulpfile.js to add to the build pipeline
var Change = require('./change.js').default;
var Insert = require('./insert.js').default;
var Delete = require('./delete.js').default;
var Editor = require('./editor.js').default;

(function() {
  var BACKSPACE_CODE = 8;
  var DELETE_CODE = 46;

  var SERVER_PORT = 8081;
  var CONNECTION = null;

  var EDITOR = null; // Will be initialized after establishing connection to server

  window.onload = function() {
    setupInputListeners();
    CONNECTION = setupConnection();
  };

  function setupInputListeners() {
    var textbox = $("#textbox");
    textbox.keypress(function(e) {
      if (e.key === "Backspace" || e.key === "Delete") {
        handleDelete(e);
      } else if (!e.key.startsWith("Arrow")) {
        handleInsert(e);
      }
    });

    textbox.keydown(handleDelete);
  }
  
  function handleInsert(e) {
    var key = e.key;
    if (key === "Enter") {
      key = "\n";
    }
    var position = e.target.selectionStart;
    var change = new Insert(position, key, (new Date()).getTime(), -1);
    EDITOR.addPendingChange(change);
    //console.log(change.toString(), e);
    sendChange();
  }

  function handleDelete(e) {
      // TODO: Handle copy and paste?
      var index = -1;
      var length = 1;
      if (e.keyCode === BACKSPACE_CODE || e.keyCode === DELETE_CODE) {
        if (e.target.selectionStart != e.target.selectionEnd) {
          // Delete range
          index = e.target.selectionStart;
          length = e.target.selectionEnd - e.target.selectionStart;
        } else if (e.keyCode === BACKSPACE_CODE && e.target.selectionStart > 0) {
          // Delete previous char
          index = e.target.selectionStart - 1;
        } else if (e.keyCode === DELETE_CODE &&
            e.target.selectionStart < this.val().length()) {
          // Delete "this" char
          index = e.target.selectionStart
        }
      }

      if (index >= 0) {
        var change = new Delete(index, length, (new Date()).getTime(), -1);
        //console.log(change.toString(), e);
        EDITOR.addPendingChange(change);
        sendChange();
      }
  }

  function setupConnection() {
    //console.log("Attempting to set up connection");

    // TODO: Add meta tag with conn info
    var docId = getMeta("docId");
    var serverHost = getMeta("serverHost");
    var serverPort = getMeta("serverPort");
    var connection = new WebSocket("ws://" + serverHost + ":" + serverPort + "/" + docId);

    connection.onopen = function(event) {
      //console.log("Connection success!");
    }

    connection.onerror = function(error) {
      console.log("Error occurred", error);
    }

    connection.onmessage = function(event) {
      if (EDITOR) {
//         //console.log("Received data", event.data);
        var messageJSON = JSON.parse(event.data);
        // If the version is less than 0 it is a special message from the server
        if (messageJSON["version"] >= 0) {
            EDITOR.acceptMessage(messageJSON);
        }
        updateEditors(messageJSON["numContributers"]);
        sendChange();
      } else {
        // TODO: Add ready state
        var splitIndex = event.data.indexOf(",");
        var version = parseInt(event.data.substring(0, splitIndex));
        var text = event.data.substring(splitIndex + 1);

        var textbox = $("#textbox")[0];
        textbox.value = text;
        EDITOR = new Editor(textbox, version);
      }
    };
    return connection;
  }

  function getMeta(tagName) {
    var tags = $("meta[name=" + tagName + "]");
    if (tags) {
      return tags[0].content;
    } else {
      return null;
    }
  }

  function sendChange() {
    var change = EDITOR.getChangeToSend();
    if (change) {
      //console.log("Sending to Server", change);
      CONNECTION.send(JSON.stringify(change));
      console.log("sent change: " + (new Date()).getTime());
    }
  }

  function updateEditors(numEditors) {
    updateButtonText(numEditors);

    var editorList = $("#editor-list");
    editorList.empty();
    var you = createListItem("You");
    editorList.append(you);

    var anon = createListItem("Anonymous");
    for (var i = 1; i < numEditors; i++) {
      editorList.append(anon);
    }
  }

  function updateButtonText(numEditors) {
    var displayText = " editing this document right now";
    if (numEditors === 1) {
      displayText = "There is 1 person" + displayText;
    } else {
      displayText = "There are " + numEditors + " people" + displayText;
    }

    var editorButton = $("#editor-list-button")[0];
    editorButton.innerHTML = displayText;
  }

  function createListItem(text) {
    var li = document.createElement("li");
    li.innerHTML = "<span>" + text + "</span>";
    return li;
  }
})();
