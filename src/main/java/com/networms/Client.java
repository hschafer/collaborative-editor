package com.networms;

/**
 * Created by Natalie on 11/26/16.
 */
public class Client {
    // at creation needs TCP connection to client JS and doc Manager
    // this class interacts with JS
    // creates Changes & sends them to the doc manager
        // when creating change, pass in "this" as sender
    // receives ACKS from doc manager
        // ack.getChange() is null if this client is the sender
        // ack.getChange() is otherwise the incoming change
            // which needs to be OT'd potentially


    public void receiveAck(Ack ack){}

    // takes in change from js
            // sends it along to docManager



}
