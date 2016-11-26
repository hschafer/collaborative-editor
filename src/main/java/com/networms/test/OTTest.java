package com.networms.test;
import com.networms.*;

import org.junit.Test;
import static org.junit.Assert.*;


public class OTTest {
    @Test
    public void testInsertBeforeInsert() {
        Change incoming = new Insert(0, "hello");
        Change pending = new Insert(1, "world");
        incoming.applyOT(pending);
        assertEquals("hello", ((Insert)incoming).text);
        assertEquals(0, ((Insert)incoming).index);
        assertEquals("world", ((Insert)pending).text);
        assertEquals(6, ((Insert)pending).index);
    }

    @Test
    public void testInsertAfterInsert() {
        Change incoming = new Insert(3, "h");
        Change pending = new Insert(0, "abc");
        incoming.applyOT(pending);
        assertEquals("h", ((Insert)incoming).text);
        assertEquals(6, ((Insert)incoming).index);
        assertEquals("abc", ((Insert)pending).text);
        assertEquals(0, ((Insert)pending).index);
    }

    @Test
    public void testInsertSamePlaceInsert() {
        Change incoming = new Insert(0, "hell");
        Change pending = new Insert(0, "lleh");
        incoming.applyOT(pending);
        assertEquals("hell", ((Insert)incoming).text);
        assertEquals(0, ((Insert)incoming).index);
        assertEquals("lleh", ((Insert)pending).text);
        assertEquals(4, ((Insert)pending).index);
    }

    @Test
    public void testDeleteBeforeInsertNoOverlap() {
        Change incoming = new Delete(1, 2);
        Change pending = new Insert(3, "hello");
        incoming.applyOT(pending);
        assertEquals(1, ((Delete)incoming).index);
        assertEquals(2, ((Delete)incoming).length);
        assertEquals(1, ((Insert)pending).index);
        assertEquals("hello", ((Insert)pending).text);
    }

    @Test
    public void testDeleteBeforeInsertWithOverlap() {
        Change incoming = new Delete(1, 3);
        Change pending = new Insert(3, "hello");
        incoming.applyOT(pending);

        assertEquals(1, ((Insert)pending).index);
        assertEquals("hello", ((Insert)pending).index);
        // !!!: putting this here because this test DOES NOT
        // pass yet - code isn't there to deal with overlaps
        // see slack-general channel pics for example :!!!
        assertEquals(1, 0);
    }

    @Test
    public void testDeleteAtSamePlaceAsInsertSameLength() {
        Change incoming = new Delete(0, 1);
        Change pending = new Insert(0, "a");
        incoming.applyOT(pending);
        assertEquals(1, ((Delete)incoming).length);
        assertEquals(1, ((Delete)incoming).index);
        assertEquals("a", ((Insert)pending).text);
        assertEquals(0, ((Insert)pending).index);
    }

    @Test
    public void testDeleteAtSamePlaceAsInsertDeleteLonger() {
        Change incoming = new Delete(0, 2);
        Change pending = new Insert(0, "a");
        incoming.applyOT(pending);
        assertEquals(2, ((Delete)incoming).length);
        assertEquals(1, ((Delete)incoming).index);
        assertEquals("a", ((Insert)pending).text);
        assertEquals(0, ((Insert)pending).index);

    }

    @Test
    public void testDeleteAtSamePlaceAsInsertInsertLonger() {
        Change incoming = new Delete(0, 1);
        Change pending = new Insert(0, "ab");
        incoming.applyOT(pending);
        assertEquals(1, ((Delete)incoming).length);
        assertEquals(2, ((Delete)incoming).index);
        assertEquals("ab", ((Insert)pending).text);
        assertEquals(0, ((Insert)pending).index);
    }

    @Test
    public void testDeleteAfterInsertNoOverlap() {
        Change incoming = new Delete(3, 1);
        Change pending = new Insert(0, "a");
        incoming.applyOT(pending);
        assertEquals(1, ((Delete)incoming).length);
        assertEquals(4, ((Delete)incoming).index);
        assertEquals("a", ((Insert)pending).text);
        assertEquals(0, ((Insert)pending).index);
    }

    @Test
    public void testDeleteAfterInsertWithOverlap() {
        Change incoming = new Delete(2, 2);
        Change pending = new Insert(1, "hello");
        incoming.applyOT(pending);
        assertEquals(2, ((Delete)incoming).length);
        assertEquals(7, ((Delete)incoming).index);
        assertEquals("hello", ((Insert)pending).text);
        assertEquals(1, ((Insert)pending).index);
    }

    @Test
    public void testDeleteBeforeDeleteNoOverlap() {
        Change incoming = new Delete(0, 2);
        Change pending = new Delete(4, 2);
        incoming.applyOT(pending);
        assertEquals(2, ((Delete)incoming).length);
        assertEquals(0, ((Delete)incoming).index);
        assertEquals(2, ((Delete)pending).length);
        assertEquals(2, ((Delete)pending).index);
    }

    @Test
    public void testDeleteBeforeDeleteWithOverlap() {
        Change incoming = new Delete(0, 2);
        Change pending = new Delete(1, 2);
        incoming.applyOT(pending);
        assertEquals(1, ((Delete)incoming).length);
        assertEquals(0, ((Delete)incoming).index);
        assertEquals(1, ((Delete)pending).length);
        assertEquals(0, ((Delete)pending).index);
    }

    @Test
    public void testDeleteAfterDeleteNoOverlap() {
        Change incoming = new Delete(4, 2);
        Change pending = new Delete(0, 2);
        incoming.applyOT(pending);
        assertEquals(2, ((Delete)incoming).length);
        assertEquals(2, ((Delete)incoming).index);
        assertEquals(2, ((Delete)pending).length);
        assertEquals(0, ((Delete)pending).index);
    }

    @Test
    public void testDeleteAfterDeleteWithOverlap() {
        Change incoming = new Delete(2, 2);
        Change pending = new Delete(0, 3);
        incoming.applyOT(pending);
        assertEquals(1, ((Delete)incoming).length);
        assertEquals(0, ((Delete)incoming).index);
        assertEquals(2, ((Delete)pending).length);
        assertEquals(0, ((Delete)pending).index);

    }

    @Test
    public void testDeleteSamePlaceIncomingLonger() {
        Change incoming = new Delete(1, 2);
        Change pending = new Delete(1, 1);
        incoming.applyOT(pending);
        assertEquals(1, ((Delete)incoming).length);
        assertEquals(1, ((Delete)incoming).index);
        assertEquals(0, ((Delete)pending).length);
        assertEquals(-1, ((Delete)pending).index);
    }

    @Test
    public void testDeleteSamePlacePendingLonger() {
        Change incoming = new Delete(1, 1);
        Change pending = new Delete(1, 2);
        incoming.applyOT(pending);
        assertEquals(0, ((Delete)incoming).length);
        assertEquals(-1, ((Delete)incoming).index);
        assertEquals(1, ((Delete)pending).length);
        assertEquals(1, ((Delete)pending).index);

    }

    @Test
    public void testDeleteSameThing() {
        Change incoming = new Delete(1, 1);
        Change pending = new Delete(1, 1);
        incoming.applyOT(pending);
        assertEquals(0, ((Delete)incoming).length);
        assertEquals(-1, ((Delete)incoming).index);
        assertEquals(0, ((Delete)pending).length);
        assertEquals(-1, ((Delete)pending).index);
    }
}
