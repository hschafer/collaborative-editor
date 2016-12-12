import async from 'async';
import express from 'express';
import path from 'path';
import pug from 'pug';
import net from 'net';

const app = express();
app.set('view engine', 'pug')
app.set('views', './app/views')
app.use('/static', express.static('public'))

var PORT = process.env.FRONTEND_SERVER_PORT;
var SERVER_HOST = process.env.SERVER_HOST;
var SERVER_TCP_PORT = process.env.SERVER_TCP_PORT;
var SERVER_WS_PORT = process.env.SERVER_WS_PORT;


app.get('/', function(req, res, next) {
  console.log('Request: [GET]', req.originalUrl);

  var connection = net.connect(SERVER_TCP_PORT, SERVER_HOST, function() {
    console.log("Successfully connected to backend server!");
    connection.write("0\r\n");
  });

  connection.setTimeout(3000, function() {
    connection.destroy();
    res.status(500).send({ error: "Something went wrong on our end and we weren't able to make" + 
      " a new document for you. Please try again later!" });
  });

  connection.on('data', function(data) {
    console.log('Received docId = ' + data);
    res.redirect('editor/' + data);
    connection.destroy();
  });
});

app.get('/editor/:docId', function(req, res, next) {
  console.log('Request: [GET]', req.originalUrl);
  res.render('editor', {docId: req.params.docId, serverHost: SERVER_HOST, serverPort: SERVER_WS_PORT});
});


app.listen(PORT);
console.log("Listening on port " + PORT);
