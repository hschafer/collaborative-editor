package com.networms;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by Hunter on 12/3/16.
 */
public class TestServer extends WebSocketServer {
    private Map<WebSocket, ClientManager> currentConnections;
    private static Map<Long, DocumentManager> idToDM;
    private static Map<Long, ClientManager> idToCM = new HashMap<>();
    private static Map<Long, DocHelpers> idToDocHelp;

    public TestServer(int port) throws UnknownHostException {
        this(new InetSocketAddress(port));
    }

    public TestServer(InetSocketAddress addr) {
        super(addr);
        this.currentConnections = new HashMap<>();
        this.idToDM = new HashMap<>();
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println("Resource desc: " + clientHandshake.getResourceDescriptor());

        String id = clientHandshake.getResourceDescriptor().substring(1);
        long docID = Long.parseLong(id);
        if (!idToCM.containsKey(docID)) {
            createDocument(docID);
        }

        ClientManager clientManager = idToCM.get(docID);
        clientManager.addClient(webSocket);
        currentConnections.put(webSocket, clientManager);
        webSocket.send(idToDM.get(docID).getContentsAndVersion());
    }

    @Override
    public void onClose(WebSocket client, int i, String s, boolean b) {
        System.out.println("Closing connection to " + client);
        currentConnections.get(client).removeClient(client);
        currentConnections.remove(client);
    }

    @Override
    public void onMessage(WebSocket client, String message) {
        System.out.println("Received message: " + message);
        ClientManager clientManager = currentConnections.get(client);
        assert clientManager != null;
        clientManager.receiveMessage(client, message);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        System.out.println("Error occured: " + e);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Starting Server");
        ServerSocket acceptor = new ServerSocket(12345);
        // right now node server calls connect twice which is being fixed
        // first socket will be node server & this blocks
        Socket frontServer = acceptor.accept();
        System.out.println("Accepted FE connection");
        // set timeout for when we r listening for clientJS connections
        acceptor.setSoTimeout(1000);
        BufferedReader frontServerInput = new BufferedReader(new InputStreamReader(frontServer.getInputStream()));
        OutputStreamWriter frontServerOutput = new OutputStreamWriter(frontServer.getOutputStream());

        int port = 8081;
        TestServer server = new TestServer(port);
        server.start();

        System.out.println("Ready to run!");
        while (true) {
            if (frontServerInput.ready()) {
                // new client tryna get a new ID
                // following line DEPENDS ON NODE SERVER SENDING LITERALLY JUST THE REQUESTED DOC ID
                long requestedID = Long.parseLong(frontServerInput.readLine().trim());
                System.out.println(requestedID);
                if (requestedID == 0L) {
                    // if it is 0, generate rando id not 0 not in set
                    requestedID = generateRandomID(idToCM.keySet());
                }

                if (!idToCM.containsKey(requestedID)) {
                    // either client passed in specific new id or we have generated one
                    // new doc with new queues
                    createDocument(requestedID);
                }
                // ack the creation or verification of docID
                System.out.println("Sending back to FE server: " + requestedID);
                frontServerOutput.write("" + requestedID);
                frontServerOutput.flush();
            }
        }
    }

    private static long generateRandomID(Set<Long> usedIDs) {
        long id = 0L;
        while (id == 0L && !usedIDs.contains(id)) {
            id = (long)(Long.MAX_VALUE * Math.random());
        }
        return id;
    }

    private static void createDocument(long docID) {
        BlockingQueue<Change> worklist = new PriorityBlockingQueue<>();
        BlockingQueue<Change> acklist = new PriorityBlockingQueue<>();
        DocumentManager docMgr = new DocumentManager(worklist, acklist);
        ClientManager clientMgr = new ClientManager(worklist, acklist);
        idToCM.put(docID, clientMgr);
        idToDM.put(docID, docMgr);
        new Thread(docMgr).start();
        new Thread(clientMgr).start();
    }
}
