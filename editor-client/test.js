import { describe, before, it } from 'mocha';
import {expect, assert, should} from 'chai';
import Insert from './app/js/insert';
import Delete from './app/js/delete';

describe('Hello World', function () {
    it('should return the index', function () {
        var insert = new Insert(1, 2, 3, 4);
        assert.equal(3, insert.index, "dog");
    });
});
