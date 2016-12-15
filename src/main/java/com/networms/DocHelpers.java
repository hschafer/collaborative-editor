package com.networms;

import com.networms.client.ClientManager;
import com.networms.document.DocumentManager;

/**
 * Holds a reference to DocumentManager and ClientManager for a doc
 * Literally just to prevent us from having multiple maps in TestServer
 */
public class DocHelpers {
    private DocumentManager dm;
    private ClientManager cm;


    public DocHelpers(DocumentManager dm, ClientManager cm) {
        this.dm = dm;
        this.cm = cm;
    }


}
