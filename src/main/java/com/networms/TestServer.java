package com.networms;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
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

    public TestServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public TestServer(InetSocketAddress addr) {
        super(addr);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println("Resource desc: " + clientHandshake.getResourceDescriptor());
        System.out.print("Local addr: " + webSocket.getLocalSocketAddress().getAddress().getHostAddress());
        System.out.println(webSocket.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!" );
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        System.out.println("Received message: " + s);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        System.out.println("Error occured: " + e);
    }

    public static void main(String[] args) throws IOException {
        Map<Long, ClientManager> idToCM = new HashMap<>();

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
                    BlockingQueue<Change> worklist = new PriorityBlockingQueue<>();
                    BlockingQueue<Change> acklist = new PriorityBlockingQueue<>();
                    DocumentManager docMgr = new DocumentManager(worklist, acklist);
                    ClientManager clientMgr = new ClientManager(worklist, acklist);
                    idToCM.put(requestedID, clientMgr);
                    new Thread(docMgr).start();
                    new Thread(clientMgr).start();
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
}
