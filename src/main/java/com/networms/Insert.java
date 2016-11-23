package com.networms;

import java.util.List;

public class Insert implements Change {
	private int index;
	private String text;
	
	public Insert(int index, String text) {
		this.index = index;
		this.text = text;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public int getTextLength() {
		return this.text.length();
	}
	
	public void incrementIndex(int amount) {
		this.index += amount;
	}
	
	public void decrementIndex(int amount) {
		this.index -= amount;
	}

	@Override
	public void applyOT(List<Change> pendingChanges) {
        for (Change curr : pendingChanges) {
            if (curr instanceof Insert) {
            	Insert currInsert = (Insert) curr;
                if (currInsert.index >= this.index) {
                    currInsert.incrementIndex(this.getTextLength());
                } else {
                    this.incrementIndex(currInsert.getTextLength());
                }
            } else {
            	Delete currDelete = (Delete) curr;
                if (currDelete.getIndex() >= this.index) {
                	currDelete.incrementIndex(this.getTextLength());
                } else if (this.index >= currDelete.getEndIndex()){
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
}