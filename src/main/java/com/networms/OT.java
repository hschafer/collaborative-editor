/**
 * Created by Natalie on 11/18/16.
 */
package com.networms;
import java.util.*;
public class OT {

    public static class Change {
        public boolean insert;
        public String text;
        public int position;

        public Change (Change copy) {
            this(copy.insert, copy.text, copy.position);
        }

        public Change(boolean insert, String text, int position) {
            this.insert = insert;
            this.text = text;
            this.position = position;
        }

        public String toString() {
            String ret = "";
            if (this.insert) {
                ret += "insert ";
            } else {
                ret += "remove ";
            }
            return ret + this.text + " @ " + this.position;
        }


    }


    public static void main(String[] args) {
        List<Change> pendingChanges = new ArrayList<>();
//        pendingChanges.add(new Change(true, "hello", 1));
        pendingChanges.add(insertAt1());
        Change incomingChange = insertAt2();
        System.out.println("____BEFORE____");
        System.out.println("pending changes " + pendingChanges);
        System.out.println("incoming change " + incomingChange);
        transform(incomingChange, pendingChanges);
        System.out.println("____AFTER____");
        System.out.println("pending changes " + pendingChanges);
        System.out.println("incoming change " + incomingChange);

    }


    public static void transform(Change incomingChange, List<Change> pendingChanges) {
        if (incomingChange.insert) {
            for (Change curr : pendingChanges) {
                if (curr.insert) {
                    if (curr.position >= incomingChange.position) {
                        curr.position += incomingChange.text.length();
                    } else {
                        incomingChange.position += curr.text.length();
                    }
                } else {
                    if (curr.position >= incomingChange.position) {
                        curr.position += incomingChange.text.length();
                    } else if (incomingChange.position >= curr.position + curr.text.length()){
                        // not overlapping
                        incomingChange.position -= curr.text.length();
                    } else {
                        // if inserting in the middle of stuff about to be deleted
                        // just make the insert to the beginning of deletion
                        incomingChange.position = curr.position;
                    }
                }
            }
        } else {
            for (Change curr : pendingChanges) {
                if (curr.insert) {
                    if (curr.position > incomingChange.position) {
                        curr.position -= incomingChange.text.length();
                    } else {
                        incomingChange.position += curr.text.length();
                    }
                } else {
                    // no overlap
                    if (curr.position >= incomingChange.position + incomingChange.text.length()) {
                        curr.position -= incomingChange.text.length();
                    } else if (incomingChange.position >= curr.position + curr.text.length()) {
                        incomingChange.position -= curr.text.length();
                    } else {
                        // overlap
                        if (curr.position == incomingChange.position && curr.text.length() == incomingChange.text.length()){
                            // exact same deletion
                            curr.position = 0;
                            curr.text = "";
                            incomingChange.position = 0;
                            incomingChange.text = "";
                        } else if (curr.position == incomingChange.position) {
                            if (curr.text.length() < incomingChange.text.length()) {
                                // curr ends first
                                incomingChange.position += curr.text.length();
                                incomingChange.text = incomingChange.text.substring(curr.text.length());
                                curr.position = 0;
                                curr.text = "";
                            } else {
                                curr.position += incomingChange.text.length();
                                curr.text = curr.text.substring(incomingChange.text.length());
                                incomingChange.position = 0;
                                incomingChange.text = "";
                            }
                        } else if (curr.position < incomingChange.position) {
                            deleteDelete(curr, incomingChange);
                        } else {
                            deleteDelete(incomingChange, curr);
                        }
                    }

                }
            }

        }

    }

    private static void deleteDelete(Change c1, Change c2) {
        if (c1.position + c1.text.length() <= c2.position + c2.text.length()) {
            // cur starts before iC starts & ends at or before iC ends

            int atEndNotOverlap = (c2.position + c2.text.length()) - (c1.position + c1.text.length());
            String incomingAfterString = c2.text.substring(0, atEndNotOverlap);
            int incomingAfterPos = c1.position + c1.text.length();

            int diffBetweenStarting = c2.position - c1.position;
            c1.text = c1.text.substring(0, diffBetweenStarting);


            c2.position = incomingAfterPos;
            c2.text = incomingAfterString;

        } else {
            // curr starts before iC, ends after iC
            // iC is completely devoured
            c1.text = c1.text.substring(c2.text.length());

            c2.position = 0;
            c2.text = "";
        }
    }

    public static Change insertAt1() {
        return new Change(true, "hello", 1);
    }

    public static Change insertAt2() {
        return new Change(true, "hi", 2);
    }

    public static Change deleteAt1() {
        return new Change(false, "hey", 1);
    }

    public static Change deleteAt2() {
        return new Change(false, "howdy", 2);
    }
}
