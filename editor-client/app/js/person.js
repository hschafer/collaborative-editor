'use strict';
export default class Person {
    constructor(firstName, lastName) {
        this.firstName = firstName;
        this.lastName = lastName;   
    }

    get() {
        return this.firstName;
    }
}
