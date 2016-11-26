package com.networms;

public abstract class Change implements Comparable{
	public long time;
	public int index;
    public int version;
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int compareTo(Object other){
        return Math.toIntExact(((Change)other).time - this.time);
	}
	public void incrementIndex(int amount) {
		this.index += amount;
	}

	public void decrementIndex(int amount) {
		this.index -= amount;
	}

    public void applyOT(Change change){}

}