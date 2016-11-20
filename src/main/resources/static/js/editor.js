"use strict";
(function() {
    var BACKSPACE_CODE = 8;
    var DELETE_CODE = 46;

    window.onload = function() {
        setupInputListeners();
    };

    function setupInputListeners() {
        var textbox = $("#textbox");
        textbox.keypress(function(e) {
            var key = e.key;
            var position = e.target.selectionStart;
            /* TODO: We can potentially write the addition to the pending list so that it "merges"
                     changes next to each other into one big change. That way we can get some bigger
                     messages being sent */
            console.log("Insert(\"" + key + "\", @" + position + ")", e);
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
                console.log("Delete(" + length + ", @" + index + ")");
            }
        });
    }
})();