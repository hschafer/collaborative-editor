package com.networms.document;

import com.networms.operations.Change;
import com.networms.operations.Delete;
import com.networms.operations.Insert;

import java.util.*;

public class Document {
    private List<Character> contents;
    // TODO: don't need version can just do history.size()?
    private int version;
    private List<Change> history;

    public Document() {
        this.contents = new ArrayList<>();
        this.version = 0;
        this.history = new ArrayList<>();
    }


    // Takes next change in worklist (earliest change) and applies OT
    // on it to make it up to date to current contents.
    // Applies this change to the contents & sends appropriate acks
    // Returns new version of the document
    public int processNextChange(Change nextChange) {
        for (int i = nextChange.version - 1; i < this.version; i++) {
            Change copyOfHistoryChange;
            if (this.history.get(i) instanceof Insert) {
                copyOfHistoryChange = new Insert((Insert)this.history.get(i));
            } else {
                copyOfHistoryChange = new Delete((Delete)this.history.get(i));
            }
            copyOfHistoryChange.applyOT(nextChange);
        }

        this.applyChangeToContents(nextChange);
        this.history.add(nextChange);
        System.out.println(contents);
        return ++this.version;
    }


    // Takes in a change that has already had OT applied on it
    // Change must be either Insert or Delete
    // Reflects change onto
    private void applyChangeToContents(Change nextChange) {
        int startPos = nextChange.index;
        if (nextChange instanceof Insert) {
            String text = ((Insert) nextChange).getText();
            for (int i = 0; i < text.length(); i++) {
                if (startPos + i < this.contents.size()) {
                    this.contents.add(startPos + i, text.charAt(i));
                } else {
                    this.contents.add(text.charAt(i));
                }
            }

        } else {
            int length = ((Delete) nextChange).getLength();
            if (nextChange.getIndex() != -1) {
                for (int i = length - 1; i >= 0; i--) {
                    if (startPos + i < this.contents.size()) {
                        this.contents.remove(startPos + i);
                    }
                }
            }
        }
    }

    public int getVersion() {
        return this.version;
    }

    public List<Character> getContents() {
        return this.contents;
    }
}
