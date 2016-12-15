package com.networms.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networms.operations.Change;
import com.networms.operations.Insert;
import org.java_websocket.WebSocket;

import java.io.*;
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
        synchronized (this.clients) {
            this.clients.add(client);
            notifyNumClients();
        }
    }

    public void removeClient(WebSocket client) {
        synchronized (this.clients) {
            this.clients.remove(client);
            notifyNumClients();
        }
    }

    private void notifyNumClients() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();
        Insert empty = new Insert(0, "", 0, -1, null);
        Ack message = new Ack(empty.version, empty);
        message.setContributers(this.clients.size());
        try {
            String toSend = mapper.writeValueAsString(message);
            for (WebSocket client : this.clients) {
                client.send(toSend);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void receiveMessage(WebSocket client, String message) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();
        try {
            Change incoming = mapper.readValue(message, Change.class);
            incoming.setSender(client);
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
                    int numContributers = clients.size();
                    forSender.setContributers(numContributers);
                    forOthers.setContributers(numContributers);
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