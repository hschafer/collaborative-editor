package com.networms;

public abstract class Change implements Comparable<Change> {
    long time;
    int index;
    int version;
    Client sender;

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
        this.index = Math.max(0, this.index - amount);
    }

    public Client getSender() {
        return this.sender;
    }

    public abstract void applyOT(Change change);
}