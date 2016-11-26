package com.networms;

public abstract class Change implements Comparable<Change> {
    public long time;
    public int index;
    public int version;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int compareTo(Change other) {
        return Math.toIntExact(other.time - this.time);
    }

    public void incrementIndex(int amount) {
        this.index += amount;
    }

    public void decrementIndex(int amount) {
        this.index -= amount;
    }

    public abstract void applyOT(Change change);
}