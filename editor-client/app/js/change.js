"use strict"
export default class Change {
    constructor(time, index, version) {
        this.time = time;
        this.index = index;
        this.version = version;
    }

    incrementIndex(amount) {
         this.index = this.index + amount;
    }

    decrementIndex(amount) {
        this.index = Math.max(0, this.index - amount);
    }        
}
