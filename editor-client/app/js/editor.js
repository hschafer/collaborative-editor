"use strict";
(function() {
    var BACKSPACE_CODE = 8;
    var DELETE_CODE = 46;
    
    var PENDING_LIST = [];
    var SENT_ITEM = null;

    class Insert {
        constructor(text, index) {
            this.text = text;
            this.index = index;
        }

        toString() {
            return "Insert(\"" + this.text + "\", @" + this.index + ", " + this.getEndIndex() + ")";
        }

	getEndIndex() {
	    return this.index + this.text.length;
	}

	incrementIndex(amount) {
            this.index = this.index + amount;
        }

        decrementIndex(amount) {
            this.index = Math.max(0, this.index - amount);
        }

	transform(change) {
    	    if (change instanceof Insert) {
                if (change.index >= this.index) {
                    change.incrementIndex(this.text.length);
                } else {
            	    this.incrementIndex(change.text.length);
                }
            } else {
                if (change.index >= this.index) {
                    change.incrementIndex(this.text.length);
                } else if (this.index >= change.getEndIndex()) {
                    // not overlapping
                    this.decrementIndex(curr.text.length);
                } else {
                    // if inserting in the middle of stuff about to be deleted
                    // just make the insert to the beginning of deletion
                    this.index = change.index;
                }
            }
        }

        apply(docText) {
            return docText.substring(0, this.index) + this.text + docText.substring(this.index);
        }
    }

    class Delete {
        constructor(length, index) {
            this.length = length;
            this.index = index;
        }

        toString() { 
            return "Delete(" + this.length + ", @" + this.index + ")";
        }
        
        incrementIndex(amount) {
            this.index = this.index + amount;
        }

        decrementIndex(amount) {
            this.index = Math.max(0, this.index - amount);
        }

        // Transform this Delete in respect to the given change
        transform(change) {
            change.transformDelete(this);
        }

        transformInsert(other) {
            var transformedIndex = other.index;
            if (other.index >= this.index && other.index < this.index + this.length) {
                // TODO: Likely OBOB here
                transformedIndex = this.index;
            } else if (other.index >= this.index) {
                transformedIndex = other.index - this.length;
            }
            return new Insert(other.text, transformedIndex);
        }

        // transform other in respect to mej
        transformDelete(other) {
            if (other.index >= this.index && other.index < this.index + this.length) {
                if (other.index + other.length < this.index + this.length) {
                    return new Delete(0, 0);
                } else {
                    var newLength = (other.index + other.lenght) - (this.index + this.length);
                    return new Delete(this.index + this.length, newLength);
                }
            } else if (other.index > this.index) {
                return new Delete(other.index - this.length, other.length);
            } else {
                return new Delete(other.index, other.length);
                // TODO transform me
            }
        }

        apply(docText) {
            return docText.substring(0, this.index) + docText.substring(this.index + this.length);
        }
    }

    window.onload = function() {
        setupInputListeners();
        setupConnection();
        var button = $("#test");
        button.click(function(e) {
            var testInsert = new Insert("Hunter", 3);
            applyChange(testInsert);
        });

        button = $("#dTest");
        button.click(function(e) {
            var firstInsert = new Insert("Hunter", 10);
	    var secondInsert = new Insert("Andrew", 20);
            console.log(firstInsert.toString());
            console.log(secondInsert.toString());
            firstInsert.transform(secondInsert);
            console.log(firstInsert.toString());
            console.log(secondInsert.toString());
        });

    };

    function setupInputListeners() {
        var textbox = $("#textbox");
        textbox.keypress(function(e) {
            var key = e.key;
            var position = e.target.selectionStart;
            /* TODO: We can potentially write the addition to the pending list so that it "merges"
               changes next to each other into one big change. That way we can get some bigger
               messages being sent */
            var change = new Insert(key, position);
            console.log(change.toString(), e);
            PENDING_LIST.push(change);
        });

        textbox.keydown(function(e) {
            // TODO: Handle copy and paste?
            var index = -1;
            var length = 1;
            if (e.keyCode === BACKSPACE_CODE || e.keyCode === DELETE_CODE) {
                if (e.target.selectionStart != e.target.selectionEnd) {
                    // Delete range
                    index = e.target.selectionStart;
                    length = e.target.selectionEnd - e.target.selectionStart;
                } else if (e.keyCode === BACKSPACE_CODE && e.target.selectionStart > 0) {
                    // Delete previous char
                    index = e.target.selectionStart - 1;
                } else if (e.keyCode === DELETE_CODE &&
                    e.target.selectionStart < this.val().length()) {
                        // Delete "this" char
                        index = e.target.selectionStart
                    }
            }

            if (index >= 0) {
                var change = new Delete(length, index);
                console.log(change.toString(), e);
                PENDING_LIST.push(change);
            }
        });
    }

    function setupConnection() {
        //var port = 8888;

        //var serverConnection = new net.Socket();
        //client.connect(port, 'attu1.cs.washington.edu', function() {
        //    client.write('CONNECT');
        //});

        //client.on('data', function(data) {
        //    console.log('Received', data);
        //    /**
        //     * Pseudocode
        //     *   Parse data into a change object
        //     *   for each change in PENDING_LIST
        //     *      OT 
        //     *   apply change to textbox
        //     */

        //});
    }

    function applyChange(change) {
        // before this point we have to parse the plain text from server 
        // into an insert or delete
        
        var textbox = $("#textbox");
        var resultText = change.apply(textbox[0].value);
        textbox[0].value = resultText;
    }

})();
