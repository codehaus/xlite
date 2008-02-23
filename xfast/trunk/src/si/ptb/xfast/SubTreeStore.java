package si.ptb.xfast;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import java.io.*;

/**
 * User: peter
 * Date: Feb 21, 2008
 * Time: 3:49:11 PM
 */
public class SubTreeStore {

    private static final byte START_ELEMENT = 1;
    private static final byte END_ELEMENT = 2;
    private static final byte ATTR = 3;
    private static final byte CHAR = 4;
    private static final byte CDATA = 5;
    private static final byte START_DOCUMENT = 6;
    private static final byte END_DOCUMENT = 7;
    private static final byte NAMESPACE = 8;
    private static final byte EMPTY = 9;


    public byte[] data;
    public int elementNumber = 0;
    private int last = 0;
    private int increment = 1000000;
    private boolean writingFinished = false;

    public SubTreeStore(int size) {
        data = new byte[size];
    }

    public int saveSubTree(XMLStreamReader reader) throws XMLStreamException {
        int pos = last;
        QName qName;
        String name;
        for (int event = reader.getEventType(); event != XMLStreamConstants.END_DOCUMENT; event = reader.next()) {
            switch (event) {
                case XMLStreamConstants.START_DOCUMENT:
                    String header = reader.getEncoding() + "\n" + reader.getVersion() + "\n" + reader.isStandalone();
                    addElement(START_DOCUMENT);
                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    addElement(END_DOCUMENT);
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    qName = reader.getName();
                    name = qName.getPrefix().isEmpty() ? qName.getLocalPart() : (qName.getPrefix() + ":" + qName.getLocalPart());
                    addElement(START_ELEMENT, name);
                    addAtributes(reader);
                    addNamespaces(reader);                    
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    qName = reader.getName();
                    name = qName.getPrefix().isEmpty() ? qName.getLocalPart() : (qName.getPrefix() + ":" + qName.getLocalPart());
                    addElement(END_ELEMENT, name);
                    break;
                case XMLStreamConstants.CHARACTERS:
                    addElement(CHAR, reader.getText());
                    break;
                case XMLStreamConstants.CDATA:
                    addElement(CDATA, reader.getText());
                    break;
                case XMLStreamConstants.SPACE:
                    System.out.println("space: type=" + reader.getEventType());
                    break;
                default:
                    System.out.println("other tag: " + reader.getEventType());
            }
        }
        return pos;
    }

    private void addAtributes(XMLStreamReader reader) {
        QName qName;
        String name;
        for (int i = 0, n = reader.getAttributeCount(); i < n; ++i) {
            qName = reader.getAttributeName(i);
            name = qName.getPrefix().isEmpty() ? qName.getLocalPart() : (qName.getPrefix() + ":" + qName.getLocalPart());
            addElement(ATTR, name + "=" + reader.getAttributeValue(i));
        }
    }
     private void addNamespaces(XMLStreamReader reader) {
        String uri;
        String prefix;
        for (int i = 0, n = reader.getNamespaceCount(); i < n; ++i) {
            uri = reader.getNamespaceURI(i);
            prefix = reader.getNamespacePrefix(i);
            addElement(NAMESPACE, prefix + "=" + uri);
        }
     }


    public void restoreSubTree(int location, XMLStreamWriter writer) throws XMLStreamException {

        Element element;
        String attrName, attrValue, data;
        String prefix, uri;
        int index;
        while ((element = getNextElement()) != null) {
            switch (element.command) {
                case START_DOCUMENT:
                    writer.writeStartDocument();
                    break;
                case END_DOCUMENT:
                    writer.writeEndDocument();
                    break;
                case START_ELEMENT:
                    writer.writeStartElement(new String(element.data));
                    break;
                case END_ELEMENT:
                    writer.writeEndElement();
                    break;
                case ATTR:
                    data = new String(element.data);
                    index = data.indexOf('=');
                    attrName = data.substring(0, index);
                    attrValue = data.substring(index + 1, data.length());
                    if (index == -1) {
                        throw new FastConverterException("Error in attribute syntax!");
                    }
                    writer.writeAttribute(attrName, attrValue);
                    break;
                case NAMESPACE:
                    data = new String(element.data);
                    index = data.indexOf('=');
                    prefix = data.substring(0, index);
                    uri = data.substring(index + 1, data.length());
                    if (index == -1) {
                        throw new FastConverterException("Error in attribute syntax!");
                    }
                    writer.writeNamespace(prefix, uri);
                    break;
                case CHAR:
                    writer.writeCharacters(new String(element.data));
                    break;
                case CDATA:
                    writer.writeCData(new String(element.data));
                    break;
//                default:
//                    System.out.println("other: type="+reader.getEventType());
            }
        }
    }

    public void addElement(byte command) {
        addElement(command, (byte[]) null);
    }

    public void addElement(byte command, String source) {
        try {
            addElement(command, source.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addElement(byte command, byte[] source) {
        if (writingFinished) {
            throw new FastConverterException("Writing has finished for this SubTreeStore instance. " +
                    "Once getNextElement() was called the addElement() must not be called again!");
        }
        int len = source == null ? 0 : source.length;
        int len21 = len & 0x1FFFF;
        if (len != len21) {
            throw new FastConverterException("Data too long: addElement() can only save byte arrays of max" +
                    " 65533 bytes long. Current length :" + len);
        }
        needsResize(len + 3);
        last++;
        data[last++] = command;
        data[last++] = (byte) (len21 & 0x007F);
        data[last++] = (byte) ((len21 >>> 7) & 0x007F);
        data[last++] = (byte) ((len21 >>> 14) & 0x007F);
//        System.out.println("length: " + len16 + " " + (len16 & 0x0F) + " " + ((len16 & 0xF0) >> 8));
        if (len > 0) {
            System.arraycopy(source, 0, data, last, len);
        }
        last += len - 1;
        elementNumber++;
    }

    public Element getNextElement() {
        if (!writingFinished) {
            writingFinished = true;
            last = 0;
        }
        if (!isNextCommand()) {
            return null;
        }
        last++;
        byte comm = data[last++];
        int len = data[last++] + (data[last++] << 7) + (data[last++] << 14);

        byte[] holder = new byte[len];
        System.arraycopy(data, last, holder, 0, len);
        last += holder.length - 1;

        return new Element(comm, holder);
    }


    private boolean isNextCommand() {
        byte d = data[last + 1];
        return d == START_ELEMENT || d == END_ELEMENT || d == CDATA || d == CHAR ||
                d == ATTR || d == START_DOCUMENT || d == END_DOCUMENT || d== NAMESPACE;
    }

    private void needsResize(int size) {
        if (last + size >= data.length - 1) {
            data = ArrayUtil.arrayCopy(data, Math.max(data.length + increment, data.length + size));
            System.out.println("new length: " + data.length);
            System.gc();
        }
    }

    public static class Element {
        public byte command;
        public byte[] data;

        public Element(byte command, byte[] data) {
            this.command = command;
            this.data = data == null ? (new byte[0]) : data;
        }
    }

    static String xml = "<?xml version=\"1.0\" encoding=\"ASCII\" standalone=\"yes\"?>"
            + "<person personAttribute=\"justPerson\">"
            + "<number lastattr=\"AAA\">44</number>"
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
            + "<code>123</code>"
            + "<number>1234-456</number>"
            + "</phone>"
            + "</person>";

    public static void main(String[] args) throws IOException, XMLStreamException {

        for (int i = 0; i < 1; i++) {

            long start = System.currentTimeMillis();
//            String infile = "/home/peter/vmware/shared/Office Open XML Part 4 - Markup Language Reference/word/document.xml";
            String outfile = "/home/peter/vmware/shared/Office Open XML Part 4 - Markup Language Reference/word/document2.xml";
//            String infile = "/home/peter/vmware/shared/Office Open XML Part 3 - Primer/word/document.xml";
//            String infile = "/home/peter/vmware/shared/Office Open XML Part 3 - Primer/word/document2.xml";
            String infile = "/home/peter/vmware/shared/testdoc2/word/document.xml";

            FileReader freader = new FileReader(infile);
            StringReader sreader = new StringReader(xml);

            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = null;
            reader = factory.createXMLStreamReader(freader);

            SubTreeStore sub = new SubTreeStore(70000000);
            sub.saveSubTree(reader);

//            Element element;
//            int c = 30;
//            while ((element = sub.getNextElement()) != null && c != 0) {
//                c--;
//                System.out.println(element.command + " : " + new String(element.data));
//            }


            XMLOutputFactory ofactory = XMLOutputFactory.newInstance();
            FileWriter out = new FileWriter("/home/peter/vmware/shared/testdoc2/word/document2.xml");
            XMLStreamWriter ostr = ofactory.createXMLStreamWriter(out);
            sub.restoreSubTree(0, ostr);
            System.out.println(out);

            System.out.println("duration: " + (System.currentTimeMillis() - start));
            System.out.println("elements: " + sub.elementNumber);
            System.out.println("length: " + sub.data.length);
        }
    }

}
