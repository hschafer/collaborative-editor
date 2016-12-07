package com.networms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.java_websocket.WebSocket;



@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)

@JsonSubTypes({
        @JsonSubTypes.Type(value = Insert.class, name = "insert"),
        @JsonSubTypes.Type(value = Delete.class, name = "delete")
})
@JsonIgnoreProperties({"sender"})
public abstract class Change implements Comparable<Change> {
    long time;
    int index;
    int version;
    WebSocket sender;

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

    public WebSocket getSender() {
        return this.sender;
    }

    public void setSender(WebSocket sender) { this.sender = sender; }

    public abstract void applyOT(Change change);
}