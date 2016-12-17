#!/bin/bash
CODE_DIR="home/collaborative-editor/collaborative-editor/editor-client"
cd $CODE_DIR
node_modules/gulp/bin/gulp.js build
FRONTEND_SERVER_PORT=3333 SERVER_HOST="hschafer.com" SERVER_TCP_PORT=12345 SERVER_WS_PORT=12346 node lib/server.js
