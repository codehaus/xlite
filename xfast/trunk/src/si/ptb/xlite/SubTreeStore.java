package si.ptb.xlite;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * User: peter
 * Date: Feb 21, 2008
 * Time: 3:49:11 PM
 */
public class SubTreeStore {


    public byte[] data;
    public int elementNumber = 0;
    private int position = 0;
    private int increment = 1000000;
    private boolean writingFinished = false;
    private XmlStreamSettings settings;

    public static final int START_BLOCK = 99;
    public static final int END_BLOCK = 98;

    public SubTreeStore(int size) {
        data = new byte[size];
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

//    public String getEncoding() {
//        return settings.encoding;
//    }

    public void addAtributes(XMLStreamReader reader, String encoding) {
        QName qName;
        String name;
        for (int i = 0, n = reader.getAttributeCount(); i < n; ++i) {
            qName = reader.getAttributeName(i);
            name = qName.getPrefix().length() == 0 ? qName.getLocalPart() : (qName.getPrefix() + ":" + qName.getLocalPart());
            addElement(XMLStreamConstants.ATTRIBUTE, name + "=" + reader.getAttributeValue(i), encoding);
        }
    }

    public void addNamespaces(XMLStreamReader reader, String encoding) {
        String uri;
        String prefix;
        for (int i = 0, n = reader.getNamespaceCount(); i < n; ++i) {
            uri = reader.getNamespaceURI(i);
            prefix = reader.getNamespacePrefix(i);
            addElement(XMLStreamConstants.NAMESPACE, prefix + "=" + uri, encoding);
        }
    }


    public int addElement(int command) {
        return addElement(command, (byte[]) null);
    }

    public int addElement(int command, String source, String encoding) {
        encoding = encoding == null ? "UTF-8" : encoding;
        try {
            return addElement(command, source.getBytes(encoding));
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }

    public int addElement(int commandID, byte[] source) {

        if (writingFinished) {
            throw new XliteException("Writing has finished for this SubTreeStore instance. " +
                    "Once getNextElement() was called the addElement() must not be called again!");
        }
        int len = source == null ? 0 : source.length;
        int len21 = len & 0x1FFFF;
        if (len != len21) {
            throw new XliteException("Data too long: addElement() can only save byte arrays of max" +
                    " 65533 bytes long. Current length :" + len);
        }

        byte command = (byte) commandID;
        // sanity check
        if (command != commandID) {
            throw new XliteException("ERROR: SubTreeStore.addElement() received a commandID parameter that does not cast to byte!");
        }

        needsResize(len + 3);
        position++;
        int start = position;
        data[position++] = command;
        data[position++] = (byte) (len21 & 0x007F);
        data[position++] = (byte) ((len21 >>> 7) & 0x007F);
        data[position++] = (byte) ((len21 >>> 14) & 0x007F);
//        System.out.println("length: " + len16 + " " + (len16 & 0x0F) + " " + ((len16 & 0xF0) >> 8));
        if (len > 0) {
            System.arraycopy(source, 0, data, position, len);
        }
        position += len - 1;
        elementNumber++;

        return start;
    }

    public Element getNextElement() {
        if (!writingFinished) {
            writingFinished = true;
        }
        if (!isNextCommand()) {
            return null;
        }
        position++;
        byte comm = data[position++];
        int len = data[position++] + (data[position++] << 7) + (data[position++] << 14);

        byte[] holder = new byte[len];
        System.arraycopy(data, position, holder, 0, len);
        position += holder.length - 1;

        return new Element(comm, holder);
    }


    private boolean isNextCommand() {
        byte d = data[position + 1];
        return d == XMLStreamConstants.START_ELEMENT ||
                d == XMLStreamConstants.END_ELEMENT ||
                d == XMLStreamConstants.CDATA ||
                d == XMLStreamConstants.CHARACTERS ||
                d == XMLStreamConstants.ATTRIBUTE ||
                d == XMLStreamConstants.START_DOCUMENT ||
                d == XMLStreamConstants.END_DOCUMENT ||
                d == XMLStreamConstants.NAMESPACE ||
                d == START_BLOCK ||
                d == END_BLOCK;
    }

    private void needsResize(int size) {
        if (position + size >= data.length - 1) {
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
