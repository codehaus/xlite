package si.ptb.xfast;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class SaxTest {

    static String xml = "<person personAttribute=\"justPerson\">"
            + "<number lastattr=\"AAA\">42</number>"
            + "<firstname>"
            + "Joe"
            + "</firstname>"
            + "<fax unknownAttrib=\"xxx\">"
            + "justAValue"
            + "<code>321</code>"
            + "anotherText"
            + "<number>9999-999</number>"
            + "</fax>"
            + "<phone newAttrib=\"unknown??\">"
            + "    <code>123</code>"
            + "<number>1234-456</number>"
            + "</phone>"
            + "</person>";

    static String xml2 = "<person personAttribute=\"justPerson\">"
            + "<number lastattr=\"AAA\">42</number>"
            + "<firstname>Joe</firstname>"
            + "<numbers>"
            + "<fax unknownAttrib=\"xxx\">"
            + "justAValue"
            + "  <code>321</code>"
            + "  <number>9999-999</number>"
            + "</fax>"
            + "<phone newAttrib=\"unknown??\">"
            + "  <code>123</code>"
            + "  <number>1234-456</number>"
            + "</phone>"
            + "<fax2>fff</fax2>"
            + "</numbers>"
            + "</person>";

    public static void main(String[] args) {
        // Create a handler to handle the SAX events generated during parsing
        String filename = "/home/peter/vmware/shared/Office Open XML Part 4 - Markup Language Reference/word/document.xml";
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        // Parse the file using the handler
        MyHandler handler = new MyHandler();
        for (int i = 0; i < 1; i++) {
            handler.count = 0;
            long startTime = System.currentTimeMillis();
            parseXmlFile(filename, handler, false);
//            parseXmlStream(is, handler, false);
            System.out.println("Duration: " + (System.currentTimeMillis() - startTime));
        }
        System.out.println("Count: " + handler.count);
        System.out.println("Elements: "+handler.elements.size());
        Set<String> elements = handler.elements.keySet();
        ArrayList<String> list = new ArrayList<String>(elements);
        Collections.sort(list);
        for (String s : list) {
            System.out.println(s);
        }
    }


    // Parses an XML file using a SAX parser.
    // If validating is true, the contents is validated against the DTD
    // specified in the file.
    public static void parseXmlFile(String filename, DefaultHandler handler, boolean validating) {
        try {
            // Create a builder factory
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(validating);

            // Create the builder and parse the file
            factory.newSAXParser().parse(new File(filename), handler);

        } catch (SAXException e) {
            // A parsing error occurred; the xml input is not valid
        } catch (ParserConfigurationException e) {
        } catch (IOException e) {
        }
    }

    public static void parseXmlStream(InputStream stream, DefaultHandler handler, boolean validating) {
        try {
            // Create a builder factory
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(validating);

            // Create the builder and parse the file
            factory.newSAXParser().parse(stream, handler);

        } catch (SAXException e) {
            // A parsing error occurred; the xml input is not valid
        } catch (ParserConfigurationException e) {
        } catch (IOException e) {
        }
    }

    // DefaultHandler contain no-op implementations for all SAX events.
    // This class should override methods to capture the events of interest.
    static class MyHandler extends DefaultHandler {
        public int count = 0;
        Map<String, String> elements = new HashMap<String, String>();

        public void endElement(String uri, String localName, String qName) throws SAXException {
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
//            System.out.println("node:" + qName);
            if(!elements.containsKey(qName)){
                elements.put(qName,qName);
            }
            count++;
        }

        public void characters(char ch[], int start, int length) throws SAXException {
//            System.out.println("chars: " + new String(ch, start, length));
        }
    }
}
