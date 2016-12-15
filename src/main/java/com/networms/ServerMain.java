package com.networms;

import com.networms.client.ClientManager;
import com.networms.document.DocumentManager;
import com.networms.operations.Change;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.jar.Pack200;

/**
 * Created by Hunter on 12/3/16.
 */
public class ServerMain extends WebSocketServer {
    private static final int TCP_PORT = 12345;
    private static final int WEBSOCKET_PORT = 8081;

    private Map<WebSocket, ClientManager> currentConnections;
    private static Map<Long, DocumentManager> idToDM = new ConcurrentHashMap<>();
    private static Map<Long, ClientManager> idToCM = new ConcurrentHashMap<>();

    public ServerMain(int port) throws UnknownHostException {
        this(new InetSocketAddress(port));
    }

    public ServerMain(InetSocketAddress addr) {
        super(addr);
        this.currentConnections = new ConcurrentHashMap<>();
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println("Resource desc: " + clientHandshake.getResourceDescriptor());

        String id = clientHandshake.getResourceDescriptor().substring(1);
        long docID = Long.parseLong(id);
        if (!idToCM.containsKey(docID)) {
            webSocket.close();
            throw new IllegalArgumentException();
        }
        ClientManager clientManager = idToCM.get(docID);
        webSocket.send(idToDM.get(docID).getContentsAndVersion());
        currentConnections.put(webSocket, clientManager);
        clientManager.addClient(webSocket);
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
        ServerSocket acceptor = new ServerSocket(TCP_PORT);
        acceptor.setSoTimeout(3000);

        ServerMain server = new ServerMain(WEBSOCKET_PORT);
        server.start();

        System.out.println("Ready to run!");
        while (true) {
            try {
                Socket frontServer = acceptor.accept();
                System.out.println("Accepted Connection");
                BufferedReader frontServerInput = new BufferedReader(new InputStreamReader(frontServer.getInputStream()));
                OutputStreamWriter frontServerOutput = new OutputStreamWriter(frontServer.getOutputStream());
                if (frontServerInput.ready()) {
                    // new client tryna get a new ID
                    // following line DEPENDS ON NODE SERVER SENDING LITERALLY JUST THE REQUESTED DOC ID
                    long requestedID = Long.parseLong(frontServerInput.readLine().trim());
                    System.out.println(requestedID);
                    synchronized (ServerMain.class) {
                        if (requestedID == 0L) {
                            // if it is 0, generate rando id not 0 not in set
                            requestedID = generateRandomID(idToCM.keySet());
                        }

                        if (!idToCM.containsKey(requestedID)) {
                            // either client passed in specific new id or we have generated one
                            // new doc with new queues
                            createDocument(requestedID);
                        }
                    }
                    // ack the creation or verification of docID
                    System.out.println("Sending back to FE server: " + requestedID);
                    frontServerOutput.write("" + requestedID);
                    frontServerOutput.flush();
                }
                frontServer.close();
            } catch (SocketTimeoutException e) {
                System.out.println(e);
            }
        }
    }

    private static long generateRandomID(Set<Long> usedIDs) {
        long id = 0L;
        while (id == 0L && !usedIDs.contains(id)) {
            id = (long) (Long.MAX_VALUE * Math.random());
        }
        return id;
    }

    private static void createDocument(long docID) {
        BlockingQueue<Change> worklist = new LinkedBlockingQueue<>();
        BlockingQueue<Change> acklist = new LinkedBlockingQueue<>();
        DocumentManager docMgr = new DocumentManager(worklist, acklist);
        ClientManager clientMgr = new ClientManager(worklist, acklist);
        idToCM.put(docID, clientMgr);
        idToDM.put(docID, docMgr);
        new Thread(docMgr).start();
        new Thread(clientMgr).start();
    }
}
