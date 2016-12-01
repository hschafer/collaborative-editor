package com.networms;


import java.util.concurrent.BlockingQueue;

public class DocumentManager implements Runnable {
    // TODO: potentially unnecessary
    private long ID;
    private BlockingQueue<Change> worklist;
    private BlockingQueue<Change> acklist;
    private Document doc;

    public DocumentManager(long ID, BlockingQueue<Change> worklist,
                           BlockingQueue<Change> acklist) {
        this.ID = ID;
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
}