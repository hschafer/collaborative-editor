var assert = require('assert');
var Person = require('../public/js/person.js');
describe('Array', function() {
    describe('#indexOf()', function() {
        it('should return -1 when not present', function() {
            var andrew = new Person("andrew", "dawson");
            assert.equal("andrew", andrew.get());
        });
    });
});
