package com.telekom.m2m.cot.restsdk.smartrest;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;


public class SmartTemplateTest {

    @Test
    public void testEscaping() {
        SmartTemplate s = new SmartTemplate() {
        };

        assertEquals("$.foo.bar", s.escape("$.foo.bar"));
        assertEquals("\" $.foo.bar\"", s.escape(" $.foo.bar"));
        assertEquals("\"$.foo.bar \"", s.escape("$.foo.bar "));
        assertEquals("\"$.\"\"foo\"\".bar\"", s.escape("$.\"foo\".bar"));
        assertEquals("\"$.foo.1,2,3\"", s.escape("$.foo.1,2,3"));
        assertEquals("\"1\tfoo\"", s.escape("1\tfoo"));
        assertEquals("\"1\t\"", s.escape("1\t"));
        assertEquals("\"1\n2\"", s.escape("1\n2"));
        assertEquals("\" 1\tfoo,\"\"bar\"\"\n 2\tbaz \"", s.escape(" 1\tfoo,\"bar\"\n 2\tbaz "));
    }

}
