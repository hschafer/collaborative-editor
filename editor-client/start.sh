#!/bin/bash
node_modules/gulp/bin/gulp.js build
node_modules/nodemon/bin/nodemon.js lib/server.js
