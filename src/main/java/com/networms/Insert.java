package com.networms;

public class Insert extends Change {
    private String text;

    public Insert(int index, String text) {
        this(index, text, 1);
    }

    public Insert(int index, String text, int version) {
        this.index = index;
        this.text = text;
        this.time = System.currentTimeMillis();
        this.version = version;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getEndIndex() {
        return this.index + this.text.length();
    }

    public String toString() {
        return "insert " + this.text + " @" + this.index;
    }

    @Override
    public void applyOT(Change change) {
        if (change instanceof Insert) {
            Insert currInsert = (Insert) change;
            if (currInsert.index >= this.index) {
                currInsert.incrementIndex(this.text.length());
            } else {
                this.incrementIndex(currInsert.text.length());
            }
        } else {
            Delete currDelete = (Delete) change;
            if (currDelete.getIndex() >= this.index) {
                currDelete.incrementIndex(this.text.length());
            } else if (this.index >= currDelete.getEndIndex()) {
                // not overlapping
                this.decrementIndex(currDelete.getLength());
            } else {
                // if inserting in the middle of stuff about to be deleted
                // just make the insert to the beginning of deletion
                this.index = currDelete.getIndex();
            }
        }
    }
}