package com.networms;

import java.util.List;

public class Delete implements Change {
	private int index;
	private int length;

	public Delete(int index, int length) {
		this.index = index;
		this.length = length;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
	public void incrementIndex(int amount) {
		this.index += amount;
	}
	
	public void decrementIndex(int amount) {
		this.index -= amount;
	}
	
	public void incrementLength(int amount) {
		this.length += amount;
	}
	
	public void decrementLength(int amount) {
		this.length -= amount;
	}
	
	public int getEndIndex() {
		return index + length;
	}
	
	@Override
	public void applyOT(List<Change> pendingChanges) {
        for (Change curr : pendingChanges) {
            if (curr instanceof Insert) {
            	Insert currInsert = (Insert) curr;
                if (currInsert.getIndex() > this.index) {
                	currInsert.decrementIndex(this.length);
                } else {
                	this.incrementIndex(((Insert) curr).getTextLength());
                }
            } else {
                // no overlap
            	Delete currDelete = (Delete) curr;
                if (currDelete.index >= this.getEndIndex()) {
                	currDelete.decrementIndex(this.length);
                } else if (this.index >= currDelete.getEndIndex()) {
                    this.decrementIndex(currDelete.length);
                } else {
                    // overlap
                    if (this.equals(currDelete)){
                        // exact same deletion
                    	this.makeEmptyDelete();
                    	currDelete.makeEmptyDelete();
                    } else if (currDelete.index == this.index) {
                        if (currDelete.length < this.length) {
                            // curr ends first
                            this.incrementIndex(currDelete.length);
                            this.decrementLength(currDelete.length);
                            currDelete.makeEmptyDelete();
                        } else {
                            currDelete.incrementIndex(this.length);
                            currDelete.decrementLength(this.length);
                            this.makeEmptyDelete();
                        }
                    } else if (currDelete.index < this.index) {
                        deleteDelete(currDelete, this);
                    } else {
                        deleteDelete(this, currDelete);
                    }
                }

            }
        }
	}
	
	@Override
	public boolean equals(Object other) {
		if (! (other instanceof Delete)) {
			return false;
		} else {
			Delete otherDelete = (Delete) other;
			return (otherDelete.index == this.index) && (otherDelete.length == this.length);
		}
	}
	
    private static void deleteDelete(Delete c1, Delete c2) {
        if (c1.getEndIndex() <= c2.getEndIndex()) {
            // cur starts before iC starts & ends at or before iC end
            c1.length = c2.index - c1.index;
            int oldC2Index = c2.index;
            c2.index = c1.getEndIndex();
            c2.decrementLength(c2.index - oldC2Index);

        } else {
            // curr starts before iC, ends after iC
            // iC is completely devoured
            c1.decrementLength(c1.length);
            c2.makeEmptyDelete();
        }
    }
	
	private void makeEmptyDelete() {
		this.index = -1;
		this.length = 0;
	}
}