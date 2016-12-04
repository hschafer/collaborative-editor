package com.networms;

import java.net.Socket;

public abstract class Change implements Comparable<Change> {
    long time;
    int index;
    int version;
    Socket sender;

    public int getIndex() {
        return index;
    }

    public long getTime() {
        return time;
    }

    public int getVersion() {
        return version;
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

    public Socket getSender() {
        return this.sender;
    }

    public abstract void applyOT(Change change);
}