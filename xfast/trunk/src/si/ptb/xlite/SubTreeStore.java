package si.ptb.xlite;

import si.ptb.xlite.ArrayUtil;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import java.io.*;

/**
 * User: peter
 * Date: Feb 21, 2008
 * Time: 3:49:11 PM
 */
public class SubTreeStore {      // todo refactor to use SimpleReader

    private static final byte START_ELEMENT = 1;
    private static final byte END_ELEMENT = 2;
    private static final byte ATTR = 3;
    private static final byte CHAR = 4;
    private static final byte CDATA = 5;
    private static final byte START_DOCUMENT = 6;
    private static final byte END_DOCUMENT = 7;
    private static final byte NAMESPACE = 8;
    private static final byte EMPTY_ELEMENT = 9;


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
        boolean emptyElement = false;
        int emptyElementIndex = 0;
        String name;
        String encoding = "UTF-8";
        StringBuffer elementText = new StringBuffer();
        for (int event = reader.getEventType(); event != XMLStreamConstants.END_DOCUMENT; event = reader.next()) {
            switch (event) {
                case XMLStreamConstants.START_DOCUMENT:
                    encoding = reader.getCharacterEncodingScheme() != null ? reader.getCharacterEncodingScheme() : encoding;
                    String header = encoding + "\n" + reader.getVersion() + "\n" + reader.isStandalone();
                    System.out.println("Header: " + header);
                    addElement(START_DOCUMENT, header, encoding);
                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    addElement(END_DOCUMENT);
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    // first write out text of current element
                    if (elementText.length() != 0) {
                        addElement(CHAR, elementText.toString(), encoding);
                        elementText = new StringBuffer();
                    }

                    emptyElementIndex = last;
                    qName = reader.getName();
                    name = qName.getPrefix().length()==0 ? qName.getLocalPart() : (qName.getPrefix() + ":" + qName.getLocalPart());
                    emptyElementIndex = addElement(START_ELEMENT, name, encoding);
                    emptyElement = true;
                    addAtributes(reader, encoding);
                    addNamespaces(reader, encoding);
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    // first write out text of current element
                    if (elementText.length() != 0) {
                        addElement(CHAR, elementText.toString(), encoding);
                        elementText = new StringBuffer();
                    }

                    if (emptyElement) {
                        replaceStartWithEmpty(emptyElementIndex);
                    } else {
                        qName = reader.getName();
                        name = qName.getPrefix().length()==0 ? qName.getLocalPart() : (qName.getPrefix() + ":" + qName.getLocalPart());
                        addElement(END_ELEMENT, name, encoding);
                    }
                    emptyElement = false;


                    break;
                case XMLStreamConstants.CHARACTERS:
                    emptyElement = false;
                    elementText.append(reader.getText());
                    break;
                case XMLStreamConstants.CDATA:
                    emptyElement = false;
                    addElement(CDATA, reader.getText(), encoding);
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

    private void replaceStartWithEmpty(int index) {
        data[index] = EMPTY_ELEMENT;
    }

    private void addAtributes(XMLStreamReader reader, String encoding) {
        QName qName;
        String name;
        for (int i = 0, n = reader.getAttributeCount(); i < n; ++i) {
            qName = reader.getAttributeName(i);
            name = qName.getPrefix().length()==0 ? qName.getLocalPart() : (qName.getPrefix() + ":" + qName.getLocalPart());
            addElement(ATTR, name + "=" + reader.getAttributeValue(i), encoding);
        }
    }

    private void addNamespaces(XMLStreamReader reader, String encoding) {
        String uri;
        String prefix;
        for (int i = 0, n = reader.getNamespaceCount(); i < n; ++i) {
            uri = reader.getNamespaceURI(i);
            prefix = reader.getNamespacePrefix(i);
            addElement(NAMESPACE, prefix + "=" + uri, encoding);
        }
    }


    public void restoreSubTree(int location, XMLStreamWriter writer) throws XMLStreamException, UnsupportedEncodingException {

        Element element;
        String attrName, attrValue, data;
        String prefix, uri;
        String encoding = "UTF-8";  // default encoding
        int index;
        while ((element = getNextElement()) != null) {
            switch (element.command) {
                case START_DOCUMENT:
                    String header = new String(element.data);
                    String[] headers = header.split("\n");
                    encoding = headers[0].equals("null") ? encoding : headers[0];  // default encoding is UTF-8
                    System.out.println("Header written: " + headers[0] + " " + headers[1]);
                    writer.writeStartDocument();
                    break;
                case END_DOCUMENT:
                    writer.writeEndDocument();
                    break;
                case START_ELEMENT:
                    writer.writeStartElement(new String(element.data));
                    break;
                case EMPTY_ELEMENT:
                    writer.writeEmptyElement(new String(element.data));
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
                        throw new XLiteException("Error in attribute syntax!");
                    }
                    writer.writeAttribute(attrName, attrValue);
                    break;
                case NAMESPACE:
                    data = new String(element.data);
                    index = data.indexOf('=');
                    prefix = data.substring(0, index);
                    uri = data.substring(index + 1, data.length());
                    if (index == -1) {
                        throw new XLiteException("Error in attribute syntax!");
                    }
                    writer.writeNamespace(prefix, uri);
                    break;
                case CHAR:
                    writer.writeCharacters(new String(element.data, encoding));
                    break;
                case CDATA:
                    writer.writeCData(new String(element.data, encoding));
                    break;
//                default:
//                    System.out.println("other: type="+reader.getEventType());
            }
        }
    }

    private int addElement(byte command) {
        return addElement(command, (byte[]) null);
    }

    private int addElement(byte command, String source, String encoding) {
        try {
            return addElement(command, source.getBytes(encoding));
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }

    private int addElement(byte command, byte[] source) {

        if (writingFinished) {
            throw new XLiteException("Writing has finished for this SubTreeStore instance. " +
                    "Once getNextElement() was called the addElement() must not be called again!");
        }
        int len = source == null ? 0 : source.length;
        int len21 = len & 0x1FFFF;
        if (len != len21) {
            throw new XLiteException("Data too long: addElement() can only save byte arrays of max" +
                    " 65533 bytes long. Current length :" + len);
        }
        needsResize(len + 3);
        last++;
        int start = last;
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

        return start;
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
        return d == START_ELEMENT || d == END_ELEMENT || d == CDATA || d == CHAR || d == ATTR ||
                d == START_DOCUMENT || d == END_DOCUMENT || d == NAMESPACE || d == EMPTY_ELEMENT;
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

}
