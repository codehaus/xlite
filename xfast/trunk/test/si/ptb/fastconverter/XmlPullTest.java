package si.ptb.fastconverter;/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// for license please see accompanying LICENSE.txt file (available also at http://www.xmlpull.org/)

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * An example of an application that uses XMLPULL V1 API.
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 */
public class XmlPullTest {

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

    private int elementCount = 0;

    public static void main(String args[]) throws XmlPullParserException, IOException {

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance(System.getProperty(XmlPullParserFactory.PROPERTY_NAME), null);
        factory.setNamespaceAware(true);
        factory.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);

        XmlPullParser xpp = factory.newPullParser();
        System.out.println("parser implementation class is " + xpp.getClass());

        XmlPullTest test = new XmlPullTest();

//        File docFile = new File("/home/peter/vmware/shared/Office Open XML Part 3 - Primer/word/document.xml");
        File docFile = new File("/home/peter/vmware/shared/Office Open XML Part 4 - Markup Language Reference/word/document.xml");

        StringReader sreader = new StringReader(xml);


        for (int i = 0; i < 5; i++) {
            FileReader reader = new FileReader(docFile);
            long startTime = System.currentTimeMillis();
            test.elementCount=0;
            xpp.setInput(reader);
            test.processDocument(xpp);

            System.out.println("Duration" + i + ": " + (System.currentTimeMillis() - startTime));
            reader.close();
        }
        System.out.println("Count: "+test.elementCount);
    }

    public void processDocument(XmlPullParser xpp)
            throws XmlPullParserException, IOException {
        int eventType = xpp.getEventType();
        do {
            if (eventType == XmlPullParser.START_DOCUMENT) {
//                System.out.println("Start document");
            } else if (eventType == XmlPullParser.END_DOCUMENT) {
//                System.out.println("End document");
            } else if (eventType == XmlPullParser.START_TAG) {
                processStartElement(xpp);
            } else if (eventType == XmlPullParser.END_TAG) {
                processEndElement(xpp);
            } else if (eventType == XmlPullParser.TEXT) {
                processText(xpp);
            }
            eventType = xpp.next();
        } while (eventType != XmlPullParser.END_DOCUMENT);
    }

    public void processStartElement(XmlPullParser xpp) {
        String name = xpp.getName();
        String uri = xpp.getNamespace();
//        System.out.println("start:"+name);

        elementCount++;
//        if (elementCount % 1000 == 0) {
//            System.out.println(elementCount);
//        }
    }

    public void processEndElement(XmlPullParser xpp) {
        String name = xpp.getName();
        String uri = xpp.getNamespace();
//        System.out.println("  end:"+name);

    }

    int holderForStartAndLength[] = new int[2];

    public void processText(XmlPullParser xpp) throws XmlPullParserException {
        char ch[] = xpp.getTextCharacters(holderForStartAndLength);
        int start = holderForStartAndLength[0];
        int length = holderForStartAndLength[1];
//        System.out.println("  chars["+start+"-"+length+"] "+new String(ch,start,length));
    }
}

