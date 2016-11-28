package com.networms;

import java.util.*;

public class DocumentManager {
    private long ID;
    private Queue<Change> worklist;
    private Document doc;
    private List<Client> clients;

    public DocumentManager(long id) {
        this.ID = id;
        this.worklist = new PriorityQueue<>();
        this.doc = new Document();
        this.clients = new ArrayList<>();
        // TODO: spawn thread to be listening and adding to worklist

    }

    // Registers a client with the given doc
    // returns the ID of the document
    // (to be used by server when new person opens web page)
    public long registerClient(Client client) {
        this.clients.add(client);
        return this.ID;
    }

    // Removes client from given doc
    // If client is not attached to the doc, does nothing
    // (to be used by server when person closes page)
    public void removeClient(Client client) {
        this.clients.remove(client);
    }

    // TODO: run this in a while(true) in main ?
    // if there is something in the worklist, then process it and
    // reflect it on the document
    // send appropriate acks to all clients of this document
    public void processAndAck() {
        if (!worklist.isEmpty()) {
            Change nextChange = worklist.remove();
            int version = doc.processNextChange(nextChange);
            for (Client client: clients) {
                if (nextChange.getSender() == client) {
                    client.receiveAck(new Ack(version));
                } else {
                    client.receiveAck(new Ack(version, nextChange));
                }
            }
        }

    }

}
