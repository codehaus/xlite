package si.ptb.xlite;

import javax.xml.stream.XMLStreamConstants;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * User: peter
 * Date: Feb 21, 2008
 * Time: 3:49:11 PM
 */
public class SubTreeStore {


    private byte[] data;
    private IdentityHashMap<Object, List<Integer>> references;

    public int elementNumber = 0;
    private int position = 0;
    private int readPos = 0;
    private int increment;
    private boolean writingFinished = false;

    private XmlStreamSettings settings;
    private static final int START_BLOCK = 99;
    private static final int END_BLOCK = 98;

    public SubTreeStore(int size) {
        this(size, 1000000);
    }

    public SubTreeStore(int size, int sizeIncrement) {
        data = new byte[size];
        increment = sizeIncrement;
        references = new IdentityHashMap<Object, List<Integer>>(size / 500);
    }

    public int getStoreSize(){
        return data.length;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void reset() {
        Arrays.fill(data, (byte) 0);
        writingFinished = false;
        position = 0;
    }

//    public String getEncoding() {
//        return settings.encoding;
//    }

    public List<Integer> getLocations(Object reference) {
        return references.get(reference);
    }

    public void addStart(Object object) {
        int location = addElement(START_BLOCK);
        if (!references.containsKey(object)) {
            references.put(object, new ArrayList<Integer>());
        }
        List<Integer> locations = references.get(object);
        locations.add(location - 1);
    }

    public static boolean isBlockStart(Element element) {
        return element.command == START_BLOCK;
    }

    public void addEnd() {
        addElement(END_BLOCK);
    }

    public static boolean isBlockEnd(Element element) {
        return element.command == END_BLOCK;
    }


    public int addElement(int command) {
        return addElement(command, (byte[]) null);
    }

    public int addElement(int command, String source, String encoding) {
        encoding = encoding == null ? "UTF-8" : encoding;
        try {
//            System.out.println("    add:" + command + " " + source);
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
        return getNextElement(-1);
    }


    public Element getNextElement(int location) {
//        if (!writingFinished) {
//            writingFinished = true;
//        }

        // set the location where reading will start
        if (location != -1) {
            readPos = location;
        }
        int start = readPos++;
        if (!isNextCommand(readPos)) {
            return null;
        }
        byte comm = data[readPos++];
        int len = data[readPos++] + (data[readPos++] << 7) + (data[readPos++] << 14);

        byte[] holder = new byte[len];
        System.arraycopy(data, readPos, holder, 0, len);
        readPos += holder.length - 1;

        return new Element(comm, holder, start);
    }

    public void copyFrom(SubTreeStore source) {
        Element element = source.getNextElement(0);
        while (element != null) {
            addElement(element.command, element.data);
            element = source.getNextElement();
        }
    }

    private boolean isNextCommand(int readPosition) {
        if (readPosition >= data.length) {
            return false;
        }
        byte d = data[readPosition];
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
//            System.out.println("new length: " + data.length+ "  "+data.hashCode());
//            System.gc();
        }
    }

    public int getDepth() {
        Element element = getNextElement(0);
        int depth = 0;
        while (element != null) {
            if (element.command == XMLStreamConstants.START_ELEMENT) {
                depth++;
            } else if (element.command == XMLStreamConstants.END_ELEMENT) {
                depth--;
            }
            element = getNextElement();
        }
        return depth;
    }

    public static class Element {
        public byte command;
        public byte[] data;
        public int location;

        public Element(byte command, byte[] data, int start) {
            this.command = command;
            this.data = data == null ? (new byte[0]) : data;
            this.location = start;
        }
    }


}
