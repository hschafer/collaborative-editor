package com.networms;

import java.util.Optional;

public class Delete implements Change {
	private int index;
	private int length;
	private Optional<Delete> second;

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
	
	public Optional<Delete> getSecond() {
		return second;
	}

	public void setSecond(Optional<Delete> second) {
		this.second = second;
	}
	
	public boolean hasSecond() {
		return second != null && second.isPresent();
	}

	public void incrementIndex(int amount) {
		this.index += amount;
	}
	
	public void decrementIndex(int amount) {
		this.index = Math.max(0, this.index - amount);
	}
	
	public void incrementLength(int amount) {
		this.length += amount;
	}
	
	public void decrementLength(int amount) {
		this.length -= amount;
	}
	public String toString() {
		return "delete " + this.length + " @" + this.index;
	}

	
	public int getEndIndex() {
		return index + length;
	}
	
	@Override
	public void applyOT(Change change) {
        if (change instanceof Insert) {
        	Insert currInsert = (Insert) change;
            if (currInsert.getIndex() > this.index) {
            	if (this.getEndIndex() > currInsert.getIndex()) {
            		// overlap
            		int nonOverlapSize = currInsert.getIndex() - this.index;
            		currInsert.decrementIndex(nonOverlapSize);
            		int secondLength = this.length - nonOverlapSize;
            		this.length = nonOverlapSize;
            		this.second = Optional.of(new Delete(currInsert.getEndIndex(), secondLength));
            	} else {
            		// no overlap
                	currInsert.decrementIndex(this.length);
            	}
            } else {
            	this.incrementIndex(currInsert.getText().length());
            }
        } else {
            // no overlap
        	Delete currDelete = (Delete) change;
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
                        this.index = currDelete.index;
                        this.decrementLength(currDelete.length);
                        currDelete.makeEmptyDelete();
                    } else {
                        currDelete.index = this.index;
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

			int oldC2Index = c2.index;
			int oldC2Endex = c2.getEndIndex();
			int oldC1Index = c1.index;
			int oldC2Length = c2.length;
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
	
	private void makeEmptyDelete() {
		this.index = -1;
		this.length = 0;
	}
}