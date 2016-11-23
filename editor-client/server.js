import express from 'express';
import path from 'path';
import net from 'net';
import pug from 'pug';

const app = express();
app.set('view engine', 'pug')
app.use('/static', express.static('public'))

app.get('/', function(req, res, next) {
  console.log('Request: [GET]', req.originalUrl);
  //var serverConnection = new net.Socket();
  //client.connect(12345, 'attu1.cs.washington.edu', function() {
  //  client.write('CONNECT');
  //});

  //client.on('data', function(data) {
  //  console.log('Received docId =', data);
  //  
  //});
  
  var docId = 'abcd1234';
  res.redirect('editor/' + docId); 
});

app.get('/editor/:docId', function(req, res, next) {
  // TODO: get connection to this docId
  console.log('Request: [GET]', req.originalUrl);
  res.render('editor', {docId: req.params.docId}) 
});


const port = 8080;
app.listen(port);
console.log("Listening on port " + port);
