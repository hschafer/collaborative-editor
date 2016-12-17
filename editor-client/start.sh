#!/bin/bash
node_modules/gulp/bin/gulp.js build
FRONTEND_SERVER_PORT=8080 SERVER_HOST="localhost" SERVER_TCP_PORT=12345 SERVER_WS_PORT=12346 node lib/server.js
