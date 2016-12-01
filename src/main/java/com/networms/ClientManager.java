package com.networms;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ClientManager implements Runnable{
    // TODO: potentially unnecessary
    private long ID;
    private BlockingQueue<Change> worklist;
    private BlockingQueue<Change> acklist;
    public List<Socket> clients;

    public ClientManager(long ID, BlockingQueue<Change> worklist,
                         BlockingQueue<Change> acklist) {
        this.ID = ID;
        this.worklist = worklist;
        this.acklist = acklist;
        this.clients = new ArrayList<>();
    }

    public void run() {
        while (true) {
            // used to translate to/from json
            ObjectMapper mapper = new ObjectMapper();

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
                for (Socket client : clients) {
                    // send ack to each client
                    try {
                        DataOutputStream output = new DataOutputStream(client.getOutputStream());
                        if (client == toAck.getSender()) {
                            output.writeBytes(mapper.writeValueAsString(forSender));
                        } else {
                            output.writeBytes(mapper.writeValueAsString(forOthers));
                        }
                    } catch (IOException io) {
                        io.printStackTrace();
                        continue;
                    }
                }
            }
            for (Socket client : clients) {
                try {
                    BufferedReader input = new BufferedReader(new InputStreamReader((client.getInputStream())));
                    if (input.ready()) {
                            // client has change to be read
                            String incomingChange = input.readLine();
                            Change incoming = null;
                            if (incomingChange.contains("insert")) {
                                incoming = mapper.readValue(incomingChange, Insert.class);
                            } else {
                                incoming = mapper.readValue(incomingChange, Delete.class);
                            }
                            this.worklist.offer(incoming);

                    }
                } catch (IOException io) {
                    io.printStackTrace();
                    continue;
                }
            }
        }
    }
}