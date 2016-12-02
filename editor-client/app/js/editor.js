"use strict";
var Change = require('change');

(function() {
    var BACKSPACE_CODE = 8;
    var DELETE_CODE = 46;
    
    var PENDING_LIST = [];
    var SENT_ITEM = null;

    class Delete extends Change {
        constructor(length, time, index, version) {
            super(time, index, version)
            this.length = length;
            this.second = null;
        }

        toString() { 
            return "Delete(" + this.length + ", @" + this.index + ")";
        }

        hasSecond() {
            return this.second != null;
        }

        incrementLength(amount) {
            this.length += amount;
        }

        decrementLength(amount) {
            return Math.max(0, this.length - amount)
        }

        getEndIndex() {
            return this.length + super.index;
        }

        applyOT(change) {
            if (change instanceof Insert) {
                if (change.index > this.index) {
                    if (this.getEndIndex() > change.index) {
                        // overlap
                        nonOverlapSize = change.index - this.index;
                        change.decrementIndex(nonOverlapSize);
                        secondLength = this.length - nonOverlapSize;
                        this.length = nonOverlapSize;
                        this.second = new Delete(change.getEndIndex(), secondLength);
                    } else {
                        // no overlap
                        change.decrementIndex(this.length);
                    }
                } else {
                    super.incrementIndex(change.text.length);
                }
            } else {
                // no overlap
                if (change.index >= this.getEndIndex()) {
                    change.decrementIndex(this.length);
                } else if (this.index >= change.getEndIndex()) {
                    this.decrementIndex(currDelete.length);
                } else {
                    // overlap
                    if (this.equals(change)) {
                        // exact same deletion
                        this.makeEmptyDelete();
                        currDelete.makeEmptyDelete();
                    } else if (change.index == this.index) {
                        if (change.length < this.length) {
                            // curr ends first
                            this.index = change.index;
                            this.decrementLength(change.length);
                            change.makeEmptyDelete();
                        } else {
                            change.index = this.index;
                            change.decrementLength(this.length);
                            this.makeEmptyDelete();
                        }
                    } else if (change.index < this.index) {
                        deleteDelete(currDelete, this);
                    } else {
                        deleteDelete(this, currDelete);
                    }
                }
            }
        }

        
        makeEmptyDelete() {
            super.index = -1;
            this.length = 0;
            this.secondDelete = null;
        }
       
        apply(docText) {
            return docText.substring(0, this.index) + docText.substring(this.index + this.length);
        }
    }

    function deleteDelete(c1, c2) {
        if (c1.getEndIndex() <= c2.getEndIndex()) {
            // cur starts before iC starts & ends at or before iC end
            oldC2Index = c2.index;
            oldC2Endex = c2.getEndIndex();
            oldC1Index = c1.index;
            oldC2Length = c2.length;
            c2.index = c1.getEndIndex() - c1.length;
            c2.length = (oldC2Endex - c1.index) - c1.length;
            c1.index = oldC2Index - (oldC2Index - c1.index);
            c1.length = (oldC2Endex - oldC1Index) - oldC2Length;
        } else {
            // curr starts before iC, ends after iC
            // iC is completely devoured
            c1.decrementLength(c1.length);
            c2.makeEmptyDelete();
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
            var firstInsert = new Insert("Hunter", 10, 3, 5);
	        var secondInsert = new Insert("Andrew", 20, 4, 6);
            var secondChange = new Change(4, 5, 6);
            console.log("first insert index:", firstInsert.index);
            console.log("second change index:",  secondChange);
            console.log(firstInsert.toString());
            console.log(secondInsert.toString());
            console.log(secondInsert.index);
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
