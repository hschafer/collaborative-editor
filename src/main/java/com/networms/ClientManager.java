package com.networms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import org.java_websocket.WebSocket;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ClientManager implements Runnable{
    private BlockingQueue<Change> worklist;
    private BlockingQueue<Change> acklist;
    public final List<WebSocket> clients;

    public ClientManager(BlockingQueue<Change> worklist,
                         BlockingQueue<Change> acklist) {
        this.worklist = worklist;
        this.acklist = acklist;
        this.clients = Collections.synchronizedList(new ArrayList<>());
    }

    public void addClient(WebSocket client) {
        // Since this is synchronized this should be safe :D
        synchronized (clients) {
            this.clients.add(client);
        }
    }

    public void receiveMessage(WebSocket client, String message) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();
        try {
            Class<? extends Change> type = message.contains("insert") ? Insert.class : Delete.class;
            Change incoming = mapper.readValue(message, type);
            System.out.println("Received change: " + incoming);
            this.worklist.offer(incoming);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void run() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();
        while (true) {
            // used to translate to/from json

            // first check for an ack
            if (!this.acklist.isEmpty()) {
                Change toAck = null;
                try {
                    toAck = this.acklist.take();
                } catch (InterruptedException interrupt) {
                    interrupt.printStackTrace();
                    continue;
                }

                Ack forSender = new Ack(toAck.version);
                Ack forOthers = new Ack(toAck.version, toAck);
                synchronized(clients) {
                    for (WebSocket client : clients) {
                        // send ack to each client
                        try {
                            Ack toSend = client == toAck.getSender() ? forSender : forOthers;
                            client.send(mapper.writeValueAsString(toSend));
                        } catch (IOException io) {
                            io.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}