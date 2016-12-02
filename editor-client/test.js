import { describe, before, it } from 'mocha';
import {expect, assert, should} from 'chai';
import Person from './app/js/person';

describe('Hello World', function () {
    it('should increment a value', function () {
        var andrew = new Person("andrew", "dawson");
        assert.equal("andrew", andrew.get(), "dog");
    });
});
