"use strict";
// these are the lines that don't work in the broswer yet becuase require is part of node
// we need to use broswerify or require js here and edit the gulpfile.js to add to the build pipeline
var Change = require('./change.js').default;
var Insert = require('./insert.js').default;
var Delete = require('./delete.js').default;

(function() {
    var BACKSPACE_CODE = 8;
    var DELETE_CODE = 46;
    
    var PENDING_LIST = [];
    var SENT_ITEM = null;
    var VERSION = 0;

    var SERVER_PORT = 8081;
    var CONNECTION = null;

    var FIRST_MESSAGE_RECEIVED = false;

    window.onload = function() {
        setupInputListeners();
        CONNECTION = setupConnection();
        var button = $("#test");
        button.click(function(e) {
            var testInsert = new Insert("Hunter", 3);
            applyChange(testInsert);
        });

        button = $("#dTest");
        button.click(function(e) {
            var firstInsert = new Insert("Hunter", 10, 3, 5);
	        var secondInsert = new Insert("Andrew", 20, 4, 6);
            var secondChange = new Change(4, 5, 6);
            console.log("first insert index:", firstInsert.index);
            console.log("second change index:",  secondChange);
            console.log(firstInsert.toString());
            console.log(secondInsert.toString());
            console.log(secondInsert.index);
            firstInsert.transform(secondInsert);
            console.log(firstInsert.toString());
            console.log(secondInsert.toString());
        });

    };

    function setupInputListeners() {
        var textbox = $("#textbox");
        textbox.keypress(function(e) {
            var key = e.key;
            if (key === "Enter") {
              key = "\n";
            }
            var position = e.target.selectionStart;
            /* TODO: We can potentially write the addition to the pending list so that it "merges"
               changes next to each other into one big change. That way we can get some bigger
               messages being sent */
            var change = new Insert(position, key, (new Date()).getTime(), VERSION + 1);
            console.log(change.toString(), e);
            PENDING_LIST.push(change);
            sendChange();
        });

        textbox.keydown(function(e) {
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
                var change = new Delete(index, length, (new Date()).getTime(), VERSION + 1);
                console.log(change.toString(), e);
                PENDING_LIST.push(change);
                sendChange();
            }
        });
    }

    function setupConnection() {
        console.log("Attempting to set up connection");

        var docId = $("meta[name=docId]")[0].content;
        var connection = new WebSocket("ws://localhost:" + SERVER_PORT + "/" + docId);

        connection.onopen = function(event) {
          console.log("Connection success!");
        }

        connection.onerror = function(error) {
          console.log("Error occurred", error);
        }

        connection.onmessage = function(event) {
          if (FIRST_MESSAGE_RECEIVED) {
            console.log("Received data", event.data);
            var changeData = JSON.parse(event.data);
            if (changeData["change"]) {
              console.log("Someone else's change, I'm going to apply it now!");
              applyChange(changeData);
            } else {
              console.log("Got acknowledgement for my own change!");
              SENT_ITEM = null;
            }
            VERSION = changeData["version"];
            sendChange();
          } else {
            var splitIndex = event.data.indexOf(",");
            VERSION = parseInt(event.data.substring(0, splitIndex));
            var text = event.data.substring(splitIndex + 1);
            $("#textbox")[0].value = text;
            FIRST_MESSAGE_RECEIVED = true;
          }
        };


        return connection;

        //client.on('data', function(data) {
        //    console.log('Received', data);
        //    /**
        //     * Pseudocode
        //     *   Parse data into a change object
        //     *   for each change in PENDING_LIST
        //     *      OT 
        //     *   apply change to textbox
        //     */

        //});
    }

    function applyChange(changeData) {
        // before this point we have to parse the plain text from server 
        // into an insert or delete
        var change = parseChange(changeData);
        console.log("Parsed change", change);
        if (SENT_ITEM) {
          change.applyOT(SENT_ITEM);
          console.log("Change after OT to sent", change);
        }

        PENDING_LIST.forEach(function (pendingChange) {
          change.applyOT(pendingChange);
          console.log("After applying OT to pending change", change);
        });

        var textbox = $("#textbox")[0];
        var selection = {start: textbox.selectionStart, end: textbox.selectionEnd};
        console.log("Text before (Selection: " + selection.start + " -> " + selection.end + ")")
        console.log(textbox.value);

        var resultText = change.apply(textbox.value, selection);

        textbox.value = resultText;
        textbox.selectionStart = selection.start;
        textbox.selectionEnd = selection.end;
        console.log("Text after (Selection: " + selection.start + " -> " + selection.end + ")")
        console.log(textbox.value)
    }

    function parseChange(changeData) {
      changeData = changeData["change"];
      if (changeData["type"] === "insert") {
        return new Insert(changeData["index"], changeData["text"], changeData["time"],
            changeData["version"]);
      } else {
        return new Delete(changeData["index"], changeData["length"], changeData["time"],
            changeData["version"]);
      }
    }

    function sendChange() {
      if (!SENT_ITEM && PENDING_LIST.length) {
        var change = PENDING_LIST.shift();
        change.version = VERSION + 1;
        console.log("Sending to Server", change);
        CONNECTION.send(JSON.stringify(change));
        SENT_ITEM = change;
      }
    }

})();
