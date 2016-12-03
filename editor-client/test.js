import { describe, before, it } from 'mocha';
import {expect, assert, should} from 'chai';
import Change from './app/js/change';
import Insert from './app/js/insert';
import Delete from './app/js/delete';

describe('testInsertBeforeInsert', function () {
    it('should work', function() {
        var incoming = new Insert(0, "hello");
        var pending = new Insert(1, "world");
        incoming.applyOT(pending);

        assert.equal("hello", incoming.text);
        assert.equal(0, incoming.index);
        assert.equal("world", pending.text);
        assert.equal(6, pending.index);        
    });
});

describe('testInsertAfterInsert', function () {
    it('should work', function() {
        var incoming = new Insert(3, "h");
        var pending = new Insert(0, "abc");
        incoming.applyOT(pending);

        assert.equal("h", incoming.text);
        assert.equal(6, incoming.index);
        assert.equal("abc", pending.text);
        assert.equal(0, pending.index);
    });
});

describe('testInsertSamePlaceInsert', function () {
    it('should work', function() {
        var incoming = new Insert(0, "hell");
        var pending = new Insert(0, "lleh");
        incoming.applyOT(pending);
        
        assert.equal("hell", incoming.text);
        assert.equal(0, incoming.index);
        assert.equal("lleh", pending.text);
        assert.equal(4, pending.index);
    });
});

describe('testDeleteBeforeInsertNoOverlap', function () {
    it('should work', function() {
        var incoming = new Delete(1, 2);
        var pending = new Insert(3, "hello");
        incoming.applyOT(pending);

        assert.equal(1, incoming.index);
        assert.equal(2, incoming.length);
        assert.equal(1, pending.index);
        assert.equal("hello", pending.text);
    });
});

describe('testDeleteBeforeInsertWithOverlapInsertIsIncoming', function () {
    it('should work', function() {
        var incoming = new Insert(3, "hello");
        var pending = new Delete(1, 3);
        incoming.applyOT(pending);

        assert.equal(1, incoming.index);
        assert.equal("hello", incoming.text);
        assert.equal(1, pending.index);
        assert.equal(2, pending.length);
        assert(pending.hasSecond());
        assert(true);

        var second = pending.second;
        assert.equal(6, second.index);
        assert.equal(1, second.length);
    });
});

describe('testDeleteBeforeInsertWithOverlap', function () {
    it('should work', function() {
        var incoming = new Delete(1, 3);
        var pending = new Insert(3, "hello");
        incoming.applyOT(pending);

        assert.equal(1, pending.index);
        assert.equal("hello", pending.text);
        assert.equal(1, incoming.index);
        assert.equal(2, incoming.length);
        assert(incoming.hasSecond());

        var  second = incoming.second
        assert.equal(6, second.index);
        assert.equal(1, second.length);
    });
});

describe('testLongDeleteWithInsertAtStart', function () {
    it('should work', function() {
        var incoming = new Delete(0, 4);
        var pending = new Insert(3, "book");
        incoming.applyOT(pending);

        assert.equal(0, pending.index);
        assert.equal("book", pending.text);
    });
});

describe('testDeleteAtSamePlaceAsInsertSameLength', function () {
    it('should work', function() {
        var incoming = new Delete(0, 1);
        var pending = new Insert(0, "a");
        incoming.applyOT(pending);

        assert.equal(1, incoming.length);
        assert.equal(1, incoming.index);
        assert.equal("a", pending.text);
        assert.equal(0, pending.index);
    });
});

describe('testDeleteAtSamePlaceAsInsertDeleteLonger', function () {
    it('should work', function() {
        var incoming = new Delete(0, 2);
        var pending = new Insert(0, "a");
        incoming.applyOT(pending);

        assert.equal(2, incoming.length);
        assert.equal(1, incoming.index);
        assert.equal("a", pending.text);
        assert.equal(0, pending.index);
    });
});

describe('testDeleteAtSamePlaceAsInsertInsertLonger', function () {
    it('should work', function() {
        var incoming = new Delete(0, 1);
        var pending = new Insert(0, "ab");
        incoming.applyOT(pending);

        assert.equal(1, incoming.length);
        assert.equal(2, incoming.index);
        assert.equal("ab", pending.text);
        assert.equal(0, pending.index);
    });
});

describe('testDeleteAfterInsertNoOverlap', function () {
    it('should work', function() {
        var incoming = new Delete(3, 1);
        var pending = new Insert(0, "a");
        incoming.applyOT(pending);

        assert.equal(1, incoming.length);
        assert.equal(4, incoming.index);
        assert.equal("a", pending.text);
        assert.equal(0, pending.index);
    });
});

describe('testDeleteAfterInsertWithOverlap', function () {
    it('should work', function() {
        var incoming = new Delete(2, 2);
        var pending = new Insert(1, "hello");
        incoming.applyOT(pending);

        assert.equal(2, incoming.length);
        assert.equal(7, incoming.index);
        assert.equal("hello", pending.text);
        assert.equal(1, pending.index);
    });
});

describe('testDeleteBeforeDeleteNoOveralp', function () {
    it('should work', function() {
        var incoming = new Delete(0, 2);
        var pending = new Delete(4, 2);
        incoming.applyOT(pending);

        assert.equal(2, incoming.length);
        assert.equal(0, incoming.index);
        assert.equal(2, pending.length);
        assert.equal(2, pending.index);
    });
});

describe('testDeleteBeforeDeleteWithOveralp', function () {
    it('should work', function() {
        var incoming = new Delete(0, 2);
        var pending = new Delete(1, 2);
        incoming.applyOT(pending);

        assert.equal(1, incoming.length);
        assert.equal(0, incoming.index);
        assert.equal(1, pending.length);
        assert.equal(0, pending.index);
    });
});

describe('testDeleteAfterDeleteNoOveralp', function () {
    it('should work', function() {
        var incoming = new Delete(4, 2);
        var pending = new Delete(0, 2);
        incoming.applyOT(pending);

        assert.equal(2, incoming.length);
        assert.equal(2, incoming.index);
        assert.equal(2, pending.length);
        assert.equal(0, pending.index);
    });
});

describe('testDeleteAfterDeleteWithOverlap', function () {
    it('should work', function() {
        var incoming = new Delete(2, 2);
        var pending = new Delete(0, 3);
        incoming.applyOT(pending);

        assert.equal(1, incoming.length);
        assert.equal(0, incoming.index);
        assert.equal(2, pending.length);
        assert.equal(0, pending.index);
    });
});

describe('testDeleteSamePlaceIncomingLonger', function () {
    it('should work', function() {
        var incoming = new Delete(1, 2);
        var pending = new Delete(1, 1);
        incoming.applyOT(pending);

        assert.equal(1, incoming.length);
        assert.equal(1, incoming.index);
        assert.equal(0, pending.length);
        assert.equal(-1, pending.index);
    });
});

describe('testDeleteSamePlacePendingLonger', function () {
    it('should work', function() {
        var incoming = new Delete(1, 1);
        var pending = new Delete(1, 2);
        incoming.applyOT(pending);

        assert.equal(0, incoming.length);
        assert.equal(-1, incoming.index);
        assert.equal(1, pending.length);
        assert.equal(1, pending.index);
    });
});

describe('testDeleteSameThing', function () {
    it('should work', function() {
        var incoming = new Delete(1, 1);
        var pending = new Delete(1, 1);
        incoming.applyOT(pending);

        assert.equal(0, incoming.length);
        assert.equal(-1, incoming.index);
        assert.equal(0, pending.length);
        assert.equal(-1, pending.index);
    });
});
