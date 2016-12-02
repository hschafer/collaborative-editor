package com.networms;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class Server {

    public static final int PORT = 8080;

    public static void main (String[] args) throws IOException{
        Map<Long, ClientManager> idToCM = new HashMap<>();

        ServerSocket acceptor = new ServerSocket(PORT);
        // first socket will be node server & this blocks
        Socket frontServer = acceptor.accept();
        // set timeout for when we r listening for clientJS connections
        acceptor.setSoTimeout(1000);
        BufferedReader frontServerInput = new BufferedReader(new InputStreamReader(frontServer.getInputStream()));
        OutputStreamWriter frontServerOutput = new OutputStreamWriter(frontServer.getOutputStream());
        while (true) {
            purgeMap(idToCM);
            if (frontServerInput.ready()) {
                // new client tryna get a new ID
                // following line DEPENDS ON NODE SERVER SENDING LITERALLY JUST THE REQUESTED DOC ID
                long requestedID = Long.getLong(frontServerInput.readLine());
                if (requestedID == 0L) {
                    // if it is 0, generate rando id not 0 not in set
                    requestedID = generateRandomID(idToCM.keySet());
                }

                if (requestedID != 0L && !idToCM.containsKey(requestedID)) {
                    // either client passed in specific new id or we have generated one
                    // new doc with new queues
                    BlockingQueue<Change> worklist = new PriorityBlockingQueue<>();
                    BlockingQueue<Change> acklist = new PriorityBlockingQueue<>();
                    DocumentManager docMgr = new DocumentManager(worklist, acklist);
                    ClientManager clientMgr = new ClientManager(worklist, acklist);
                    idToCM.put(requestedID, clientMgr);
                    new Thread(docMgr).start();
                    new Thread(clientMgr).start();
                }
                // ack the creation or verification of docID
                frontServerOutput.write("" + requestedID);
            }

            Socket clientBrowser = null;
            try {
                clientBrowser = acceptor.accept();
            } catch (InterruptedIOException io) {
                // timeout ran out, no one is tryna get in on this
                continue;
            }

            if (clientBrowser != null) {
                // someone (clientJS) tryna get in on this
                BufferedReader clientInput = new BufferedReader(new InputStreamReader(clientBrowser.getInputStream()));
                // following line DEPENDS ON CLIENT JS SENDING LITERALLY JUST THE REQUESTED DOC ID
                long requestedID = Long.getLong(clientInput.readLine());
                ClientManager clientMgr = idToCM.get(requestedID);
                synchronized (clientMgr.clients) {
                    clientMgr.clients.add(clientBrowser);
                }
            }


        }
    }

    private static void purgeMap (Map<Long, ClientManager> idToCM) {
        // TODO: implement removing old docs without removing new ones
        // TODO: re-evaluate above TODO
    }


    private static long generateRandomID(Set<Long> usedIDs) {
        long id = 0L;
        while(id == 0L && !usedIDs.contains(id)) {
            id = (long)(Long.MAX_VALUE * Math.random());
        }
        return id;
    }





}
