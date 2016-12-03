"use strict"
import Change from './change';

export default class Insert extends Change {
    constructor(index, text, time, version) {
        super(time, index, version);
        this.text = text;
    }

    toString() {
        return "Insert(\"" + this.text + "\", @" + super.index + ", " + this.getEndIndex() + ")";
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
                this.decrementIndex(curr.text.length);
            } else {
                // if inserting in the middle of stuff about to be deleted
                // just make the insert to the beginning of deletion
                console.log("got here parent");
                change.applyOT(this);
            }
        }
    }

    apply(docText) {
        return docText.substring(0, this.index) + this.text + docText.substring(this.index);
    }
}
