package com.networms;


/**
 * Simple class to represent an ack sent from DocManager
 * to Client
 */

public class Ack {
    private int version;
    private Change ackedChange;


    // to be used for acking the sender
    public Ack(int version) {
        this(version, null);
    }

    // to be used for acking others - not sender
    public Ack(int version, Change change) {
        this.version = version;
        this.ackedChange = change;
    }

    public int getVersion() {
        return this.version;
    }

    public Change getChange() {
        return this.ackedChange;
    }
}
