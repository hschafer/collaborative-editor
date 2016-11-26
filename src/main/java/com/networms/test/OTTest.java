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
        assertEquals("hello", ((Insert)incoming).getText());
        assertEquals(0, ((Insert)incoming).getIndex());
        assertEquals("world", ((Insert)pending).getText());
        assertEquals(6, ((Insert)pending).getIndex());
    }

    @Test
    public void testInsertAfterInsert() {
        Change incoming = new Insert(3, "h");
        Change pending = new Insert(0, "abc");
        incoming.applyOT(pending);
        assertEquals("h", ((Insert)incoming).getText());
        assertEquals(6, ((Insert)incoming).getIndex());
        assertEquals("abc", ((Insert)pending).getText());
        assertEquals(0, ((Insert)pending).getIndex());
    }

    @Test
    public void testInsertSamePlaceInsert() {
        Change incoming = new Insert(0, "hell");
        Change pending = new Insert(0, "lleh");
        incoming.applyOT(pending);
        assertEquals("hell", ((Insert)incoming).getText());
        assertEquals(0, ((Insert)incoming).getIndex());
        assertEquals("lleh", ((Insert)pending).getText());
        assertEquals(4, ((Insert)pending).getIndex());
    }

    @Test
    public void testDeleteBeforeInsertNoOverlap() {
        Change incoming = new Delete(1, 2);
        Change pending = new Insert(3, "hello");
        incoming.applyOT(pending);
        assertEquals(1, ((Delete)incoming).getIndex());
        assertEquals(2, ((Delete)incoming).getLength());
        assertEquals(1, ((Insert)pending).getIndex());
        assertEquals("hello", ((Insert)pending).getText());
    }

    @Test
    public void testDeleteBeforeInsertWithOverlap() {
        Change incoming = new Delete(1, 3);
        Change pending = new Insert(3, "hello");
        incoming.applyOT(pending);

        assertEquals(1, ((Insert)pending).getIndex());
        assertEquals("hello", ((Insert)pending).getText());
        assertEquals(1, ((Delete)incoming).getIndex());
        assertEquals(2, ((Delete)incoming).getLength());
        assertTrue(((Delete) incoming).hasSecond());
        Delete second = ((Delete)incoming).getSecond().get();
        assertEquals(6, second.getIndex());
        assertEquals(1, second.getLength());
    }
    
    @Test
    public void testLongDeleteWithInsertAtStart() {
    	Change incoming = new Delete(0, 4);
    	Change pending = new Insert(3, "book");
    	
    	incoming.applyOT(pending);
    	
    	assertEquals(0, ((Insert)pending).getIndex());
    	assertEquals("book", ((Insert)pending).getText());
    }

    @Test
    public void testDeleteAtSamePlaceAsInsertSamegetLength() {
        Change incoming = new Delete(0, 1);
        Change pending = new Insert(0, "a");
        incoming.applyOT(pending);
        assertEquals(1, ((Delete)incoming).getLength());
        assertEquals(1, ((Delete)incoming).getIndex());
        assertEquals("a", ((Insert)pending).getText());
        assertEquals(0, ((Insert)pending).getIndex());
    }

    @Test
    public void testDeleteAtSamePlaceAsInsertDeleteLonger() {
        Change incoming = new Delete(0, 2);
        Change pending = new Insert(0, "a");
        incoming.applyOT(pending);
        assertEquals(2, ((Delete)incoming).getLength());
        assertEquals(1, ((Delete)incoming).getIndex());
        assertEquals("a", ((Insert)pending).getText());
        assertEquals(0, ((Insert)pending).getIndex());

    }

    @Test
    public void testDeleteAtSamePlaceAsInsertInsertLonger() {
        Change incoming = new Delete(0, 1);
        Change pending = new Insert(0, "ab");
        incoming.applyOT(pending);
        assertEquals(1, ((Delete)incoming).getLength());
        assertEquals(2, ((Delete)incoming).getIndex());
        assertEquals("ab", ((Insert)pending).getText());
        assertEquals(0, ((Insert)pending).getIndex());
    }

    @Test
    public void testDeleteAfterInsertNoOverlap() {
        Change incoming = new Delete(3, 1);
        Change pending = new Insert(0, "a");
        incoming.applyOT(pending);
        assertEquals(1, ((Delete)incoming).getLength());
        assertEquals(4, ((Delete)incoming).getIndex());
        assertEquals("a", ((Insert)pending).getText());
        assertEquals(0, ((Insert)pending).getIndex());
    }

    @Test
    public void testDeleteAfterInsertWithOverlap() {
        Change incoming = new Delete(2, 2);
        Change pending = new Insert(1, "hello");
        incoming.applyOT(pending);
        assertEquals(2, ((Delete)incoming).getLength());
        assertEquals(7, ((Delete)incoming).getIndex());
        assertEquals("hello", ((Insert)pending).getText());
        assertEquals(1, ((Insert)pending).getIndex());
    }

    @Test
    public void testDeleteBeforeDeleteNoOverlap() {
        Change incoming = new Delete(0, 2);
        Change pending = new Delete(4, 2);
        incoming.applyOT(pending);
        assertEquals(2, ((Delete)incoming).getLength());
        assertEquals(0, ((Delete)incoming).getIndex());
        assertEquals(2, ((Delete)pending).getLength());
        assertEquals(2, ((Delete)pending).getIndex());
    }

    @Test
    public void testDeleteBeforeDeleteWithOverlap() {
        Change incoming = new Delete(0, 2);
        Change pending = new Delete(1, 2);
        incoming.applyOT(pending);
        assertEquals(1, ((Delete)incoming).getLength());
        assertEquals(0, ((Delete)incoming).getIndex());
        assertEquals(1, ((Delete)pending).getLength());
        assertEquals(0, ((Delete)pending).getIndex());
    }

    @Test
    public void testDeleteAfterDeleteNoOverlap() {
        Change incoming = new Delete(4, 2);
        Change pending = new Delete(0, 2);
        incoming.applyOT(pending);
        assertEquals(2, ((Delete)incoming).getLength());
        assertEquals(2, ((Delete)incoming).getIndex());
        assertEquals(2, ((Delete)pending).getLength());
        assertEquals(0, ((Delete)pending).getIndex());
    }

    @Test
    public void testDeleteAfterDeleteWithOverlap() {
        Change incoming = new Delete(2, 2);
        Change pending = new Delete(0, 3);
        incoming.applyOT(pending);
        assertEquals(1, ((Delete)incoming).getLength());
        assertEquals(0, ((Delete)incoming).getIndex());
        assertEquals(2, ((Delete)pending).getLength());
        assertEquals(0, ((Delete)pending).getIndex());

    }

    @Test
    public void testDeleteSamePlaceIncomingLonger() {
        Change incoming = new Delete(1, 2);
        Change pending = new Delete(1, 1);
        incoming.applyOT(pending);
        assertEquals(1, ((Delete)incoming).getLength());
        assertEquals(1, ((Delete)incoming).getIndex());
        assertEquals(0, ((Delete)pending).getLength());
        assertEquals(-1, ((Delete)pending).getIndex());
    }

    @Test
    public void testDeleteSamePlacePendingLonger() {
        Change incoming = new Delete(1, 1);
        Change pending = new Delete(1, 2);
        incoming.applyOT(pending);
        assertEquals(0, ((Delete)incoming).getLength());
        assertEquals(-1, ((Delete)incoming).getIndex());
        assertEquals(1, ((Delete)pending).getLength());
        assertEquals(1, ((Delete)pending).getIndex());

    }

    @Test
    public void testDeleteSameThing() {
        Change incoming = new Delete(1, 1);
        Change pending = new Delete(1, 1);
        incoming.applyOT(pending);
        assertEquals(0, ((Delete)incoming).getLength());
        assertEquals(-1, ((Delete)incoming).getIndex());
        assertEquals(0, ((Delete)pending).getLength());
        assertEquals(-1, ((Delete)pending).getIndex());
    }
}
