"use strict"
import Change from './change';

export default class Insert extends Change {
    constructor(index, text, time, version) {
        super(time, index, version, "insert");
        this.text = text;
    }

    toString() {
        return "Insert(\"" + this.text + "\", @" + this.index + ", " + this.getEndIndex() + ", v: " + this.version + ")";
    }

    getEndIndex() {
        return this.index + this.text.length;
    }

    applyOT(change) {
        if (change instanceof Insert) {
            if (change.index >= this.index) {
                change.incrementIndex(this.text.length);
            } else {
                this.incrementIndex(change.text.length);
            }
        } else {
            if (change.index >= this.index) {
                change.incrementIndex(this.text.length);
            } else if (this.index >= change.getEndIndex()) {
                // not overlapping
                this.decrementIndex(change.length);
            } else {
                // if inserting in the middle of stuff about to be deleted
                // just make the insert to the beginning of deletion
                console.log("got here parent");
                change.applyOT(this);
            }
        }
    }

    apply(docText, selection) {
        if (this.index < selection.start) {
          selection.start += this.text.length;
          selection.end += this.text.length;
        } else if (this.index >= selection.start && this.index < selection.end) {
          selection.end += this.text.length;
        }
        return docText.substring(0, this.index) + this.text + docText.substring(this.index);
    }
}
