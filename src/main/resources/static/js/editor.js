"use strict";
(function() {
    var BACKSPACE_CODE = 8;
    var DELETE_CODE = 46;

    window.onload = function() {
        var textbox = $("#textbox");
        console.log("textbox", textbox);
        textbox.keypress(function(e) {
            var key = e.key;
            var position = e.target.selectionStart;
            console.log("Input(\"" + key + "\", @" + position + ")", e);
        });
        textbox.keydown(function(e) {
            // TODO: Handle seleciton delete, copy, paste
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
        // tinymce.init({
        //     selector: "#textbox",
        //     setup: function(editor) {
        //         editor.on("change", function(e) {
        //             console.log("the event object ", e);
        //             console.log("the editor object ", editor);
        //             console.log("the content ", editor.getContent());
        //             debugger;
        //         });
        //         editor.on("keyup", function(e) {
        //             console.log("keyup ", e);
        //         });
        //     }
        // });
    };
})();