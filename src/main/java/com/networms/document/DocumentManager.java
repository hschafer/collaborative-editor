package com.networms.document;


import com.networms.document.Document;
import com.networms.operations.Change;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class DocumentManager implements Runnable {
    // TODO: potentially unnecessary
    private BlockingQueue<Change> worklist;
    private BlockingQueue<Change> acklist;
    private Document doc;

    public DocumentManager(BlockingQueue<Change> worklist,
                           BlockingQueue<Change> acklist) {
        this.worklist = worklist;
        this.acklist = acklist;
        this.doc = new Document();
    }

    public void run() {
        while (true) {
            Change toProcess = null;
            try {
                // waits until there is something in worklist & removes it
                toProcess = this.worklist.take();
            } catch (InterruptedException interrupt) {
                interrupt.printStackTrace();
                continue;
            }
            // reflects changes on doc, performing OT
            toProcess.version = doc.processNextChange(toProcess);

            // adds change to acklist with OT & updated version
            acklist.offer(toProcess);

        }
    }

    public String getContentsAndVersion() {
        List<Character> contents = this.doc.getContents();
        String ret = "";
        for(char c : contents) {
            ret += c;
        }
        return this.doc.getVersion() + "," + ret;
    }
}