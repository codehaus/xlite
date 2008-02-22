package si.ptb.xfast;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * User: peter
 * Date: Feb 21, 2008
 * Time: 3:49:11 PM
 */
public class SubTreeStore {

    private byte START_NODE = 1;
    private byte END_ELEMENT = 2;
    private byte ATTR = 3;
    private byte CHAR = 4;
    private byte CDATA = 5;

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
        for (int event = reader.next(); event != XMLStreamConstants.END_DOCUMENT; event = reader.next()) {
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    qName = reader.getName();
                    addElement(START_NODE, qName.getPrefix() + ":" + qName.getLocalPart());
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    qName = reader.getName();
                    addElement(END_ELEMENT, qName.getPrefix() + ":" + qName.getLocalPart());
                    break;
                case XMLStreamConstants.CHARACTERS:
                    addElement(CHAR, reader.getText());
                    break;
                case XMLStreamConstants.CDATA:
                    addElement(CDATA, reader.getText());
                    break;
            }
        }
        return pos;
    }

    public void restoreSubTree(int location, XMLStreamWriter writer) {

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
        int len = source.length;
        int len16 = len & 0xFFFF;
        if (len != len16) {
            throw new FastConverterException("Data too long: addElement() can only save byte arrays of max" +
                    " 65533 bytes long. Current length :" + source.length);
        }
        needsResize(source.length + 3);
        last++;
        data[last++] = command;
        data[last++] = (byte) (len16 & 0x00FF);
        data[last++] = (byte) ((len16 & 0xFF00) >> 8);
//        System.out.println("length: " + len16 + " " + (len16 & 0x0F) + " " + ((len16 & 0xF0) >> 8));
        System.arraycopy(source, 0, data, last, source.length);
        last += source.length - 1;
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
        int len = data[last++] + data[last++] * 256;
        byte[] holder = new byte[len];
        System.arraycopy(data, last, holder, 0, len);
        last += holder.length - 1;

        return new Element(comm, holder);
    }


    private boolean isNextCommand() {
        byte d = data[last + 1];
        return d == START_NODE || d == END_ELEMENT || d == CDATA || d == CHAR || d == ATTR;
    }

    private void needsResize(int size) {
        if (last + size >= data.length - 1) {
            data = Arrays.copyOf(data, Math.max(data.length + increment, data.length + size));
            System.out.println("new length: " + data.length);
            System.gc();
        }
    }

    public static class Element{
                public byte command;
        public byte[] data;

        public Element(byte command, byte[] data) {
            this.command = command;
            this.data = data;
        }
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        String filename = "/home/peter/vmware/shared/Office Open XML Part 4 - Markup Language Reference/word/document.xml";
//        String filename = "/home/peter/vmware/shared/Office Open XML Part 3 - Primer/word/document.xml";
        FileReader freader = null;
        try {
            freader = new FileReader(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = null;
        try {
            reader = factory.createXMLStreamReader(freader);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

        SubTreeStore sub = new SubTreeStore(70000000);
        try {
            sub.saveSubTree(reader);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

//        for (int i = 0; i < 1000; i++) {
//            Element element = sub.getNextElement();
//            System.out.println(element.command+" : "+ new String(element.data));
//        }

        System.out.println("duration: "+(System.currentTimeMillis()-start));
        System.out.println("elements: "+sub.elementNumber);
        System.out.println("length: "+sub.data.length);
    }

}
