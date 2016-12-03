"use strict";
import Change from './change';
import Insert from './insert';

export default class Delete extends Change {
    constructor(index, length, time, version) {
        super(time, index, version);
        this.length = length;
        this.second = null;
    }

    toString() { 
        return "Delete(" + this.length + ", @" + this.index + ")";
    }

    hasSecond() {
        return this.second != null;
    }

    incrementLength(amount) {
        this.length += amount;
    }

    decrementLength(amount) {
        this.length =  Math.max(0, this.length - amount)
    }

    getEndIndex() {
        return this.length + this.index;
    }

    equals(object) {
        return object.index == this.index && object.length == this.length;
    } 

    applyOT(change) {
        if (change instanceof Insert) {
            if (change.index > this.index) {
                if (this.getEndIndex() > change.index) {
                    // overlap
                    var nonOverlapSize = change.index - this.index;
                    change.decrementIndex(nonOverlapSize);
                    var secondLength = this.length - nonOverlapSize;
                    this.length = nonOverlapSize;
                    this.second = new Delete(change.getEndIndex(), secondLength);
                } else {
                    // no overlap
                    change.decrementIndex(this.length);
                }
            } else {
                this.incrementIndex(change.text.length);
            }
        } else {
            // no overlap
            if (change.index >= this.getEndIndex()) {
                change.decrementIndex(this.length);
            } else if (this.index >= change.getEndIndex()) {
                this.decrementIndex(change.length);
            } else {
                // overlap
                if (this.equals(change)) {
                    // exact same deletion
                    makeEmptyDelete(this);
                    makeEmptyDelete(change);
                } else if (change.index == this.index) {
                    if (change.length < this.length) {
                        // curr ends first
                        console.log("right here");
                        this.index = change.index;
                        this.decrementLength(change.length);
                        makeEmptyDelete(change);
                    } else {
                        change.index = this.index;
                        change.decrementLength(this.length);
                        makeEmptyDelete(this);
                    }
                } else if (change.index < this.index) {
                    deleteDelete(change, this);
                } else {
                    deleteDelete(this, change);
                }
            }
        }
    }

    apply(docText) {
        return docText.substring(0, this.index) + docText.substring(this.index + this.length);
    }
}

function deleteDelete(c1, c2) {
    if (c1.getEndIndex() <= c2.getEndIndex()) {
        // cur starts before iC starts & ends at or before iC end
        var oldC2Index = c2.index;
        var oldC2Endex = c2.getEndIndex();
        var oldC1Index = c1.index;
        var oldC2Length = c2.length;
        c2.index = c1.getEndIndex() - c1.length;
        c2.length = (oldC2Endex - c1.index) - c1.length;
        c1.index = oldC2Index - (oldC2Index - c1.index);
        c1.length = (oldC2Endex - oldC1Index) - oldC2Length;
    } else {
        // curr starts before iC, ends after iC
        // iC is completely devoured
        c1.decrementLength(c1.length);
        makeEmptyDelete(c2);
    }
}

function makeEmptyDelete(curr) {
    curr.index = -1;
    curr.length = 0;
    curr.secondDelete = null;
}

