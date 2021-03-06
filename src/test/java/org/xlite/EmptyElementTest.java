/*
 * This software is released under the BSD license. Full license available at http://www.xlite.org/license/
 *
 * Copyright (c) 2008, 2009, Peter Knego & Xlite contributors
 * All rights reserved.
 */
package org.xlite;


import java.io.StringReader;

public class EmptyElementTest {

    private static String inXml = "" +
            "<test>" +
            "<node></node>" +
            "</test";

    @org.testng.annotations.Test(expectedExceptions = XliteException.class)
    public void test() {
        StringReader reader = new StringReader(inXml);

        // Double step to make Xlite work harder (not necessary normally - do not copy)
        // Reads Class configuration, produces XML configuration from it and then feeds it to Xlite
        StringReader configuration = XmlConfigTester.reader(Test.class);
        Xlite xlite = new Xlite(configuration);

        Test test = (Test) xlite.fromXML(reader);

    }

    @RootElement("test")
    public static class Test {
        @Element
        public Integer node;
    }
}
