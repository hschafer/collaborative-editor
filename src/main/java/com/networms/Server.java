package com.networms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Server {

    public static final String HOST = "attu2.cs.washington.edu";
    public static final int PORT = 8080;
    // 2 connections to JS

    // 8080 node server request for ID
        // constantly open
        // create or lookup DocManager (start thread)
        // send back ID

    // 8081 client JS requests connection to given ID
        // one time
        // make Client with TCP Connection to client JS
            // creates socket with client JS
        // find appropriate DocManager & pass through Client




    public static void main (String[] args) throws IOException{
        Map<Long, ClientManager> idToCM = new HashMap<>();

        ServerSocket acceptor = new ServerSocket(PORT);
        // first socket will be node server & this blocks
        Socket frontServer = acceptor.accept();
        BufferedReader frontServerInput = new BufferedReader(new InputStreamReader(frontServer.getInputStream()));
        while (true) {
            purgeMap(idToCM);
            if (frontServerInput.ready()) {
                // new client tryna get a new ID
                // following line DEPENDS ON NODE SERVER SENDING LITERALLY JUST THE REQUESTED DOC ID
                long requestedID = Long.getLong(frontServerInput.readLine());
                if (requestedID == 0L) {
                    // new doc
                    long generatedID = generateRandomID(idToCM.keySet());
                }


            }

        }
    }

    public static void purgeMap (Map<Long, ClientManager> idToCM) {
        // TODO: implement removing old docs without removing new ones
    }


    public static long generateRandomID(Set<Long> usedIDs) {
        // TODO: generate random id that isn't in usedIDs
        return 0L;
    }





}
