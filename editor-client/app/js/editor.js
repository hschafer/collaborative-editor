var Change = require('./change.js').default;
var Insert = require('./insert.js').default;
var Delete = require('./delete.js').default;

"use strict"
export default class Editor {
  constructor(textbox, version) {
    this.pendingList = [];
    this.sentChange = null;
    this.serverVersion = version;
    this.textbox = textbox;
  }

  getChangeToSend() {
    if (this.sentChange || this.pendingList.length === 0) {
      return null;
    } else {
      this.sentChange = this.pendingList.shift();
      this.sentChange.version = this.serverVersion + 1;
      return this.sentChange;
    }
  }

  addPendingChange(change) {
    this.pendingList.push(change);
  }

  acceptMessage(messageJSON) {
    if (messageJSON["change"]) {
      console.log("Someone else's change, I'm going to apply it now!");
      this.applyChange(messageJSON);
    } else {
      console.log("Got acknowledgement for my own change!");
      this.sentChange = null;
    }
    this.serverVersion = messageJSON["version"];
  }

  applyChange(received) {
    var change = this.createChange(received);
    console.log("Received change", change);
    this.applyOT(received);

    var selection = {start: this.textbox.selectionStart, end: this.textbox.selectionEnd};
    console.log("Text before (Selection: " + selection.start + " -> " + selection.end + ")");
    console.log(this.textbox.value);

    var resultText = change.apply(this.textbox.value, selection);
    this.textbox.value = resultText;
    this.textbox.selectionStart = selection.start;
    this.textbox.selectionEnd = selection.end;

    console.log("Text after (Selection: " + selection.start + " -> " + selection.end + ")");
    console.log(this.textbox.value);
  }

  createChange(received) {
      received = received["change"];
      var index = received["index"];
      var time = received["time"];
      var version = received["version"];
      if (received["type"] === "insert") {
        return new Insert(index, received["text"], time, version);
      } else {
        return new Delete(index, received["length"], time, version);
      }
  }

  applyOT(receivedChange) {
    if (this.sentChange) {
      receivedChange.applyOT(this.sentChange);
      console.log("Change after OT to sent", receivedChange);
    }

    this.pendingList.forEach(function (pendingChange) {
      receivedChange.applyOT(pendingChange);
      console.log("After applying OT to pending change", receivedChange);
    });
  }
}

