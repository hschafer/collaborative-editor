package com.networms.test;
import com.networms.OT.*;
import static com.networms.OT.transform;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;


/**
 * Created by Natalie on 11/22/16.
 */

public class OTTest {
    @Test
    public void testInsertBeforeInsert() {
        Change incoming = new Change(true, "hello", 0);
        Change pending = new Change(true, "world", 1);
        transform(incoming, Arrays.asList(pending));
        assertEquals("hello", incoming.text);
        assertEquals(0, incoming.position);
        assertEquals("world", pending.text);
        assertEquals(6, pending.position);
    }

    @Test
    public void testInsertAfterInsert() {
        Change incoming = new Change(true, "h", 3);
        Change pending = new Change(true, "abc", 0);
        transform(incoming, Arrays.asList(pending));
        assertEquals("h", incoming.text);
        assertEquals(6, incoming.position);
        assertEquals("abc", pending.text);
        assertEquals(0, pending.position);
    }

}
