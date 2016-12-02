"use strict";
import Change from './change';

class Delete extends Change {
    constructor(length, time, index, version) {
        super(time, index, version)
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
        return Math.max(0, this.length - amount)
    }

    getEndIndex() {
        return this.length + super.index;
    }

    applyOT(change) {
        if (change instanceof Insert) {
            if (change.index > this.index) {
                if (this.getEndIndex() > change.index) {
                    // overlap
                    nonOverlapSize = change.index - this.index;
                    change.decrementIndex(nonOverlapSize);
                    secondLength = this.length - nonOverlapSize;
                    this.length = nonOverlapSize;
                    this.second = new Delete(change.getEndIndex(), secondLength);
                } else {
                    // no overlap
                    change.decrementIndex(this.length);
                }
            } else {
                super.incrementIndex(change.text.length);
            }
        } else {
            // no overlap
            if (change.index >= this.getEndIndex()) {
                change.decrementIndex(this.length);
            } else if (this.index >= change.getEndIndex()) {
                this.decrementIndex(currDelete.length);
            } else {
                // overlap
                if (this.equals(change)) {
                    // exact same deletion
                    this.makeEmptyDelete();
                    currDelete.makeEmptyDelete();
                } else if (change.index == this.index) {
                    if (change.length < this.length) {
                        // curr ends first
                        this.index = change.index;
                        this.decrementLength(change.length);
                        change.makeEmptyDelete();
                    } else {
                        change.index = this.index;
                        change.decrementLength(this.length);
                        this.makeEmptyDelete();
                    }
                } else if (change.index < this.index) {
                    deleteDelete(currDelete, this);
                } else {
                    deleteDelete(this, currDelete);
                }
            }
        }
    }

    
    makeEmptyDelete() {
        super.index = -1;
        this.length = 0;
        this.secondDelete = null;
    }
   
    apply(docText) {
        return docText.substring(0, this.index) + docText.substring(this.index + this.length);
    }

    deleteDelete(c1, c2) {
        if (c1.getEndIndex() <= c2.getEndIndex()) {
            // cur starts before iC starts & ends at or before iC end
            oldC2Index = c2.index;
            oldC2Endex = c2.getEndIndex();
            oldC1Index = c1.index;
            oldC2Length = c2.length;
            c2.index = c1.getEndIndex() - c1.length;
            c2.length = (oldC2Endex - c1.index) - c1.length;
            c1.index = oldC2Index - (oldC2Index - c1.index);
            c1.length = (oldC2Endex - oldC1Index) - oldC2Length;
        } else {
            // curr starts before iC, ends after iC
            // iC is completely devoured
            c1.decrementLength(c1.length);
            c2.makeEmptyDelete();
        }
    }
}
