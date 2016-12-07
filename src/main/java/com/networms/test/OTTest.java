package com.networms.test;

import com.networms.*;
import org.junit.Test;
import static org.junit.Assert.*;


public class OTTest {
    @Test
    public void testInsertBeforeInsert() {
        Insert incoming = new Insert(0, "hello");
        Insert pending = new Insert(1, "world");
        incoming.applyOT(pending);

        assertEquals("hello", incoming.getText());
        assertEquals(0, incoming.getIndex());
        assertEquals("world", pending.getText());
        assertEquals(6, pending.getIndex());
    }

    @Test
    public void testInsertAfterInsert() {
        Insert incoming = new Insert(3, "h");
        Insert pending = new Insert(0, "abc");
        incoming.applyOT(pending);

        assertEquals("h", incoming.getText());
        assertEquals(6, incoming.getIndex());
        assertEquals("abc", pending.getText());
        assertEquals(0, pending.getIndex());
    }

    @Test
    public void testInsertSamePlaceInsert() {
        Insert incoming = new Insert(0, "hell");
        Insert pending = new Insert(0, "lleh");
        incoming.applyOT(pending);

        assertEquals("hell", incoming.getText());
        assertEquals(0, incoming.getIndex());
        assertEquals("lleh", pending.getText());
        assertEquals(4, pending.getIndex());
    }

    @Test
    public void testDeleteBeforeInsertNoOverlap() {
        Delete incoming = new Delete(1, 2);
        Insert pending = new Insert(3, "hello");
        incoming.applyOT(pending);

        assertEquals(1, incoming.getIndex());
        assertEquals(2, incoming.getLength());
        assertEquals(1, pending.getIndex());
        assertEquals("hello", pending.getText());
    }
    
    @Test
    public void testDeleteBeforeInsertWithOverlapInsertIsIncoming() {
    	Insert incoming = new Insert(3, "hello");
    	Delete pending = new Delete(1, 3);
        incoming.applyOT(pending);

        assertEquals(1, incoming.getIndex());
        assertEquals("hello", incoming.getText());
        assertEquals(1, pending.getIndex());
        assertEquals(2, pending.getLength());
        assertTrue(pending.hasSecond());

        Delete second = pending.getSecond().get();
        assertEquals(6, second.getIndex());
        assertEquals(1, second.getLength());
    }

    @Test
    public void testDeleteBeforeInsertWithOverlap() {
        Delete incoming = new Delete(1, 3);
        Insert pending = new Insert(3, "hello");
        incoming.applyOT(pending);

        assertEquals(1, pending.getIndex());
        assertEquals("hello", pending.getText());
        assertEquals(1, incoming.getIndex());
        assertEquals(2, incoming.getLength());
        assertTrue(incoming.hasSecond());

        Delete second = incoming.getSecond().get();
        assertEquals(6, second.getIndex());
        assertEquals(1, second.getLength());
    }

    @Test
    public void testLongDeleteWithInsertAtStart() {
        Delete incoming = new Delete(0, 4);
        Insert pending = new Insert(3, "book");
        incoming.applyOT(pending);

        assertEquals(0, pending.getIndex());
        assertEquals("book", pending.getText());
    }

    @Test
    public void testDeleteAtSamePlaceAsInsertSamegetLength() {
        Delete incoming = new Delete(0, 1);
        Insert pending = new Insert(0, "a");
        incoming.applyOT(pending);

        assertEquals(1, incoming.getLength());
        assertEquals(1, incoming.getIndex());
        assertEquals("a", pending.getText());
        assertEquals(0, pending.getIndex());
    }

    @Test
    public void testDeleteAtSamePlaceAsInsertDeleteLonger() {
        Delete incoming = new Delete(0, 2);
        Insert pending = new Insert(0, "a");
        incoming.applyOT(pending);

        assertEquals(2, incoming.getLength());
        assertEquals(1, incoming.getIndex());
        assertEquals("a", pending.getText());
        assertEquals(0, pending.getIndex());

    }

    @Test
    public void testDeleteAtSamePlaceAsInsertInsertLonger() {
        Delete incoming = new Delete(0, 1);
        Insert pending = new Insert(0, "ab");
        incoming.applyOT(pending);

        assertEquals(1, incoming.getLength());
        assertEquals(2, incoming.getIndex());
        assertEquals("ab", pending.getText());
        assertEquals(0, pending.getIndex());
    }

    @Test
    public void testDeleteAfterInsertNoOverlap() {
        Delete incoming = new Delete(3, 1);
        Insert pending = new Insert(0, "a");
        incoming.applyOT(pending);

        assertEquals(1, incoming.getLength());
        assertEquals(4, incoming.getIndex());
        assertEquals("a", pending.getText());
        assertEquals(0, pending.getIndex());
    }

    @Test
    public void testDeleteAfterInsertWithOverlap() {
        Delete incoming = new Delete(2, 2);
        Insert pending = new Insert(1, "hello");
        incoming.applyOT(pending);

        assertEquals(2, incoming.getLength());
        assertEquals(7, incoming.getIndex());
        assertEquals("hello", pending.getText());
        assertEquals(1, pending.getIndex());
    }

    @Test
    public void testDeleteBeforeDeleteNoOverlap() {
        Delete incoming = new Delete(0, 2);
        Delete pending = new Delete(4, 2);
        incoming.applyOT(pending);

        assertEquals(2, incoming.getLength());
        assertEquals(0, incoming.getIndex());
        assertEquals(2, pending.getLength());
        assertEquals(2, pending.getIndex());
    }

    @Test
    public void testDeleteBeforeDeleteWithOverlap() {
        Delete incoming = new Delete(0, 2);
        Delete pending = new Delete(1, 2);
        incoming.applyOT(pending);

        assertEquals(1, incoming.getLength());
        assertEquals(0, incoming.getIndex());
        assertEquals(1, pending.getLength());
        assertEquals(0, pending.getIndex());
    }

    @Test
    public void testDeleteAfterDeleteNoOverlap() {
        Delete incoming = new Delete(4, 2);
        Delete pending = new Delete(0, 2);
        incoming.applyOT(pending);

        assertEquals(2, incoming.getLength());
        assertEquals(2, incoming.getIndex());
        assertEquals(2, pending.getLength());
        assertEquals(0, pending.getIndex());
    }

    @Test
    public void testDeleteAfterDeleteWithOverlap() {
        Delete incoming = new Delete(2, 2);
        Delete pending = new Delete(0, 3);
        incoming.applyOT(pending);

        assertEquals(1, incoming.getLength());
        assertEquals(0, incoming.getIndex());
        assertEquals(2, pending.getLength());
        assertEquals(0, pending.getIndex());
    }

    @Test
    public void testDeleteSamePlaceIncomingLonger() {
        Delete incoming = new Delete(1, 2);
        Delete pending = new Delete(1, 1);
        incoming.applyOT(pending);

        assertEquals(1, incoming.getLength());
        assertEquals(1, incoming.getIndex());
        assertEquals(0, pending.getLength());
        assertEquals(-1, pending.getIndex());
    }

    @Test
    public void testDeleteSamePlacePendingLonger() {
        Delete incoming = new Delete(1, 1);
        Delete pending = new Delete(1, 2);
        incoming.applyOT(pending);

        assertEquals(0, incoming.getLength());
        assertEquals(-1, incoming.getIndex());
        assertEquals(1, pending.getLength());
        assertEquals(1, pending.getIndex());
    }

    @Test
    public void testDeleteSameThing() {
        Delete incoming = new Delete(1, 1);
        Delete pending = new Delete(1, 1);
        incoming.applyOT(pending);

        assertEquals(0, incoming.getLength());
        assertEquals(-1, incoming.getIndex());
        assertEquals(0, pending.getLength());
        assertEquals(-1, pending.getIndex());
    }
}
