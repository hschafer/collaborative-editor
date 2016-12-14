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
  	if (this.pendingList.length > 0) {
  		var lastChange = this.pendingList[this.pendingList.length - 1];
		this.pendingList.pop();
  		if (lastChange instanceof Insert && change instanceof Insert) {
  			if (change.index == lastChange.index) {
  				change = new Insert(change.index, change.text.concat(lastChange.text), 
  					(new Date()).getTime(), lastChange.version);
  			} else if (change.index == lastChange.getEndIndex()) {
  				  			console.log(lastChange.text.concat(change.text));

  				change = new Insert(lastChange.index, lastChange.text.concat(change.text), 
  					(new Date()).getTime(), lastChange.version);
  					  					console.log(change.text);

  			} else {
  				this.pendingList.push(lastChange);
  			}
  		} else {
  			this.pendingList.push(lastChange);
  		}	
    }
    this.pendingList.push(change);
    console.log(change);
    console.log(this.pendingList);
    console.log();
  }

  acceptMessage(messageJSON) {
    if (messageJSON["change"]) {
//       console.log("Someone else's change, I'm going to apply it now!");
      this.applyChange(messageJSON);
    } else {
      console.log("Received Ack: " + (new Date()).getTime());
      this.sentChange = null;
    }
    this.serverVersion = messageJSON["version"];
  }

  applyChange(received) {
    var change = this.createChange(received);
//     console.log("Received change", change);
    this.applyOT(change);

    var selection = {start: this.textbox.selectionStart, end: this.textbox.selectionEnd};
//     console.log("Text before (Selection: " + selection.start + " -> " + selection.end + ")");
    //console.log(this.textbox.value);

    var resultText = change.apply(this.textbox.value, selection);
    this.textbox.value = resultText;
    this.textbox.selectionStart = selection.start;
    this.textbox.selectionEnd = selection.end;

    //console.log("Text after (S// election: " + selection.start + " -> " + selection.end + ")");
//     console.log(this.textbox.value);
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
//       console.log("Change after OT to sent", receivedChange);
    }

    this.pendingList.forEach(function (pendingChange) {
      receivedChange.applyOT(pendingChange);
//       console.log("After applying OT to pending change", receivedChange);
    });
  }
}

