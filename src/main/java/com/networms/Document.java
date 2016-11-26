package com.networms;

import java.util.*;

public class Document {
    long ID;
    List<Character> contents;
    // TODO: DocManager has worklist?
    Queue<Change> worklist;
    // TODO: don't need version can just do history.size()?
    int version;
    List<Change> history;

    public Document(long ID) {
        this.ID = ID;
        this.contents = new ArrayList<>();
        this.worklist = new PriorityQueue<>();
        this.version = 0;
        this.history = new ArrayList<>();
    }

    // TODO: need separate thread to be listening to incoming changes and add them to worklist
    // TODO: or should this be in the DocManager?


    // Takes next change in worklist (earliest change) and applies OT
    // on it to make it up to date to current contents.
    // Applies this change to the contents & sends appropriate acks
    public void processNextChange() {
        if (!this.worklist.isEmpty()) {
            Change nextChange = this.worklist.remove();
            for (int i = nextChange.version; i < this.version; i++) {
                // TODO: this also changes the history? do we want that
                nextChange.applyOT(this.history.get(i));
            }
            this.applyChangeToContents(nextChange);
            this.history.add(nextChange);
            this.version++;
        }
    }


    // Takes in a change that has already had OT applied on it
    // Change must be either Insert or Delete
    // Reflects change onto
    private void applyChangeToContents(Change nextChange) {
        int startPos = nextChange.index;
        if (nextChange instanceof Insert) {
            String text = ((Insert) nextChange).getText();
            for (int i = 0; i < text.length(); i++) {
                this.contents.add(startPos + i, text.charAt(i));
            }
        } else {
            int length = ((Delete) nextChange).getLength();
            for (int i = 0; i < length; i++) {
                this.contents.remove(startPos + i);
            }
        }
    }
}
