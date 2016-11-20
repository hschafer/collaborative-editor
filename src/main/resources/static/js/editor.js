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
            console.log("Input(\"" + key + "\", @" + position + ")", e);
        });

        textbox.keydown(function(e) {
            // TODO: Handle selection delete, copy, paste
            var index = -1;
            if (e.keyCode === BACKSPACE_CODE && e.target.selectionStart > 0) {
                index = e.target.selectionStart - 1;
            } else if (e.keyCode === DELETE_CODE
                    && e.target.selectionStart < this.val().length()) {
                index = e.target.selectionStart
            }

            if (index >= 0) {
                console.log("Delete(@" + index + ")");
            }
        });
    }
})();