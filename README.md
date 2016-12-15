Google Docs 2.0
---------------
Authors: Andrew Dawson, Hunter Schafer, Natalie Andreeva

#**Do good code, make cool shit.**

#How to Run Backend
You will need to install maven. Once that's done you can run the backend by 

`mvn install`
`mvn exec:java` 

# How to Run Frontend
Open a new shell and `cd editor-client` and edit `start.sh` to edit the port numbers for your setup. The variables are
* `FRONTEND_SERVER_PORT`: The port to reach the frontend at
* `SERVER_HOST`: The host name of the machine you are running the backend server on. (localhost sufficies but you can have external connections if you put in a reachable host name or your IP).
* `SERVER_TCP_PORT`: Port that the backend server listens to TCP connections on.
* `SERVER_WS_PORT`: Port that the backend server listens to Websocket connections on.

You probably only need to edit `SERVER_HOST` to make it work. Then run

`npm install`  
`./start.sh`  

Go to the browser and go to `<HOSTNAME>:8080` to see the editor!
Go ahead and make changes share the link with anyone whom you would like to work with on the document.
