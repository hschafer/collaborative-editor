"use strict"
import Change from './change';

export default class Insert extends Change {
    constructor(text, time, index, version) {
        super(time, index, version);
        this.text = text;
    }

    toString() {
        return "Insert(\"" + this.text + "\", @" + super.index + ", " + this.getEndIndex() + ")";
    }

    getEndIndex() {
        return super.index + this.text.length;
    }

	transform(change) {
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
                this.index = change.index;
            }
        }
    }

    apply(docText) {
        return docText.substring(0, this.index) + this.text + docText.substring(this.index);
    }
}
