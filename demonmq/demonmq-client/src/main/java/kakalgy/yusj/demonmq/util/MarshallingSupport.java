package kakalgy.yusj.demonmq.util;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.fusesource.hawtbuf.UTF8Buffer;

/**
 * The fixed version of the UTF8 encoding function. Some older JVM's UTF8
 * encoding function breaks when handling large strings.
 * 
 * @author gyli
 *
 */
public final class MarshallingSupport {

    public static final byte NULL = 0;
    public static final byte BOOLEAN_TYPE = 1;
    public static final byte BYTE_TYPE = 2;
    public static final byte CHAR_TYPE = 3;
    public static final byte SHORT_TYPE = 4;
    public static final byte INTEGER_TYPE = 5;
    public static final byte LONG_TYPE = 6;
    public static final byte DOUBLE_TYPE = 7;
    public static final byte FLOAT_TYPE = 8;
    public static final byte STRING_TYPE = 9;
    public static final byte BYTE_ARRAY_TYPE = 10;
    public static final byte MAP_TYPE = 11;
    public static final byte LIST_TYPE = 12;
    public static final byte BIG_STRING_TYPE = 13;

    /**
     * 构造函数
     */
    private MarshallingSupport() {
    }

    /**
     * 先向out写入一个int，值为map的size大小，后面再遍历map，将key和value写入out
     * 
     * @param map
     * @param out
     * @throws IOException
     */
    public static void marshalPrimitiveMap(Map<String, Object> map, DataOutputStream out) throws IOException {
        if (map == null) {
            out.writeInt(-1);
        } else {
            out.writeInt(map.size());
            for (String name : map.keySet()) {
                out.writeUTF(name);
                Object value = map.get(name);
                marshalPrimitive(out, value);
            }
        }
    }

    public static Map<String, Object> unmarshalPrimitiveMap(DataInputStream in) throws IOException {
        return unmarshalPrimitiveMap(in, Integer.MAX_VALUE);
    }

    public static Map<String, Object> unmarshalPrimitiveMap(DataInputStream in, boolean force) throws IOException {
        return unmarshalPrimitiveMap(in, Integer.MAX_VALUE, force);
    }

    public static Map<String, Object> unmarshalPrimitiveMap(DataInputStream in, int maxPropertySize) throws IOException {
        return unmarshalPrimitiveMap(in, maxPropertySize, false);
    }

    /**
     * @param in
     * @return
     * @throws IOException
     * @throws IOException
     */
    public static Map<String, Object> unmarshalPrimitiveMap(DataInputStream in, int maxPropertySize, boolean force)
            throws IOException {
        int size = in.readInt();
        if (size > maxPropertySize) {
            throw new IOException("Primitive map is larger than the allowed size: " + size);
        }
        if (size < 0) {
            return null;
        } else {
            Map<String, Object> rc = new HashMap<String, Object>(size);
            for (int i = 0; i < size; i++) {
                String name = in.readUTF();
                rc.put(name, unmarshalPrimitive(in, force));
            }
            return rc;
        }
    }

    /**
     * 先向out写入一个int，值为List的size大小，后面再遍历List，将值写入out
     * 
     * @param list
     * @param out
     * @throws IOException
     */
    public static void marshalPrimitiveList(List<Object> list, DataOutputStream out) throws IOException {
        out.writeInt(list.size());
        for (Object element : list) {
            marshalPrimitive(out, element);
        }
    }

    public static List<Object> unmarshalPrimitiveList(DataInputStream in) throws IOException {
        return unmarshalPrimitiveList(in, false);
    }

    /**
     * 首先读取一个int，值为List的长度，再依次读取List的值
     * 
     * @param in
     * @param force
     * @return
     * @throws IOException
     */
    public static List<Object> unmarshalPrimitiveList(DataInputStream in, boolean force) throws IOException {
        int size = in.readInt();
        List<Object> answer = new ArrayList<Object>(size);
        while (size-- > 0) {
            answer.add(unmarshalPrimitive(in, force));
        }
        return answer;
    }

    /**
     * 向DataOutputStream写入value的值，会先根据value的类型写入类型的标志位，一个byte，再写入对应value的值，这样后面可以根据前一个byte表示的标志位得到value的类型
     * 
     * @param out
     * @param value
     * @throws IOException
     */
    public static void marshalPrimitive(DataOutputStream out, Object value) throws IOException {
        if (value == null) {
            marshalNull(out);
        } else if (value.getClass() == Boolean.class) {
            marshalBoolean(out, ((Boolean) value).booleanValue());
        } else if (value.getClass() == Byte.class) {
            marshalByte(out, ((Byte) value).byteValue());
        } else if (value.getClass() == Character.class) {
            marshalChar(out, ((Character) value).charValue());
        } else if (value.getClass() == Short.class) {
            marshalShort(out, ((Short) value).shortValue());
        } else if (value.getClass() == Integer.class) {
            marshalInt(out, ((Integer) value).intValue());
        } else if (value.getClass() == Long.class) {
            marshalLong(out, ((Long) value).longValue());
        } else if (value.getClass() == Float.class) {
            marshalFloat(out, ((Float) value).floatValue());
        } else if (value.getClass() == Double.class) {
            marshalDouble(out, ((Double) value).doubleValue());
        } else if (value.getClass() == byte[].class) {
            marshalByteArray(out, (byte[]) value);
        } else if (value.getClass() == String.class) {
            marshalString(out, (String) value);
        } else if (value.getClass() == UTF8Buffer.class) {
            marshalString(out, value.toString());
        } else if (value instanceof Map) {
            out.writeByte(MAP_TYPE);
            marshalPrimitiveMap((Map<String, Object>) value, out);
        } else if (value instanceof List) {
            out.writeByte(LIST_TYPE);
            marshalPrimitiveList((List<Object>) value, out);
        } else {
            throw new IOException("Object is not a primitive: " + value);
        }
    }

    public static Object unmarshalPrimitive(DataInputStream in) throws IOException {
        return unmarshalPrimitive(in, false);
    }

    /**
     * 先读取第一个byte，确定是哪一种类型之后再继续读取值
     * 
     * @param in
     * @param force
     * @return
     * @throws IOException
     */
    public static Object unmarshalPrimitive(DataInputStream in, boolean force) throws IOException {
        Object value = null;
        byte type = in.readByte();
        switch (type) {
        case BYTE_TYPE:
            value = Byte.valueOf(in.readByte());
            break;
        case BOOLEAN_TYPE:
            value = in.readBoolean() ? Boolean.TRUE : Boolean.FALSE;
            break;
        case CHAR_TYPE:
            value = Character.valueOf(in.readChar());
            break;
        case SHORT_TYPE:
            value = Short.valueOf(in.readShort());
            break;
        case INTEGER_TYPE:
            value = Integer.valueOf(in.readInt());
            break;
        case LONG_TYPE:
            value = Long.valueOf(in.readLong());
            break;
        case FLOAT_TYPE:
            value = new Float(in.readFloat());
            break;
        case DOUBLE_TYPE:
            value = new Double(in.readDouble());
            break;
        case BYTE_ARRAY_TYPE:
            value = new byte[in.readInt()];
            in.readFully((byte[]) value);
            break;
        case STRING_TYPE:
            if (force) {
                value = in.readUTF();
            } else {
                value = readUTF(in, in.readUnsignedShort());
            }
            break;
        case BIG_STRING_TYPE: {
            if (force) {
                value = readUTF8(in);
            } else {
                value = readUTF(in, in.readInt());
            }
            break;
        }
        case MAP_TYPE:
            value = unmarshalPrimitiveMap(in, true);
            break;
        case LIST_TYPE:
            value = unmarshalPrimitiveList(in, true);
            break;
        case NULL:
            value = null;
            break;
        default:
            throw new IOException("Unknown primitive type: " + type);
        }
        return value;
    }

    public static UTF8Buffer readUTF(DataInputStream in, int length) throws IOException {
        byte data[] = new byte[length];
        in.readFully(data);
        return new UTF8Buffer(data);
    }

    /**
     * 向out中写入null的标志位，一个byte，值为0
     * 
     * @param out
     * @throws IOException
     */
    public static void marshalNull(DataOutputStream out) throws IOException {
        out.writeByte(NULL);
    }

    /**
     * 向out中写入boolean的标志位，一个byte，值为1，再写入value
     * 
     * @param out
     * @param value
     * @throws IOException
     */
    public static void marshalBoolean(DataOutputStream out, boolean value) throws IOException {
        out.writeByte(BOOLEAN_TYPE);
        out.writeBoolean(value);
    }

    /**
     * 向out中写入byte的标志位，一个byte，值为2，再写入value
     * 
     * @param out
     * @param value
     * @throws IOException
     */
    public static void marshalByte(DataOutputStream out, byte value) throws IOException {
        out.writeByte(BYTE_TYPE);
        out.writeByte(value);
    }

    /**
     * 向out中写入char的标志位，一个byte，值为3，再写入value
     * 
     * @param out
     * @param value
     * @throws IOException
     */
    public static void marshalChar(DataOutputStream out, char value) throws IOException {
        out.writeByte(CHAR_TYPE);
        out.writeChar(value);
    }

    /**
     * 向out中写入short的标志位，一个byte，值为4，再写入value
     * 
     * @param out
     * @param value
     * @throws IOException
     */
    public static void marshalShort(DataOutputStream out, short value) throws IOException {
        out.writeByte(SHORT_TYPE);
        out.writeShort(value);
    }

    /**
     * 向out中写入int的标志位，一个byte，值为5，再写入value
     * 
     * @param out
     * @param value
     * @throws IOException
     */
    public static void marshalInt(DataOutputStream out, int value) throws IOException {
        out.writeByte(INTEGER_TYPE);
        out.writeInt(value);
    }

    /**
     * 向out中写入long的标志位，一个byte，值为6，再写入value
     * 
     * @param out
     * @param value
     * @throws IOException
     */
    public static void marshalLong(DataOutputStream out, long value) throws IOException {
        out.writeByte(LONG_TYPE);
        out.writeLong(value);
    }

    /**
     * 向out中写入float的标志位，一个byte，值为8，再写入value
     * 
     * @param out
     * @param value
     * @throws IOException
     */
    public static void marshalFloat(DataOutputStream out, float value) throws IOException {
        out.writeByte(FLOAT_TYPE);
        out.writeFloat(value);
    }

    /**
     * 向out中写入double的标志位，一个byte，值为7，再写入value
     * 
     * @param out
     * @param value
     * @throws IOException
     */
    public static void marshalDouble(DataOutputStream out, double value) throws IOException {
        out.writeByte(DOUBLE_TYPE);
        out.writeDouble(value);
    }

    /**
     * 向out中写入byte[]的标志位，一个byte，值为10，再写入一个Int值length，最后再写入value的byte数组，偏移量默认为0
     * 
     * @param out
     * @param value
     * @throws IOException
     */
    public static void marshalByteArray(DataOutputStream out, byte[] value) throws IOException {
        marshalByteArray(out, value, 0, value.length);
    }

    /**
     * 向out中写入byte[]的标志位，一个byte，值为10，再写入一个Int值length，最后再写入value的byte数组
     * 
     * @param out
     * @param value
     * @param offset
     * @param length
     * @throws IOException
     */
    public static void marshalByteArray(DataOutputStream out, byte[] value, int offset, int length) throws IOException {
        out.writeByte(BYTE_ARRAY_TYPE);
        out.writeInt(length);
        out.write(value, offset, length);
    }

    /**
     * 根据s的长度，首先判断s的长度与Short.MAX_VALUE/4的大小比较，
     * 若小于则向out中写入String的标志位，一个byte，值为9，再以UTF8编码写入value，
     * 若大于等于，则向out中写入BigString的标志位，一个byte，值为13
     * 
     * @param out
     * @param s
     * @throws IOException
     */
    public static void marshalString(DataOutputStream out, String s) throws IOException {
        // If it's too big, out.writeUTF may not able able to write it out.
        if (s.length() < Short.MAX_VALUE / 4) {
            out.writeByte(STRING_TYPE);
            out.writeUTF(s);
        } else {
            out.writeByte(BIG_STRING_TYPE);
            writeUTF8(out, s);
        }
    }

    /**
     * 先向dataOut中写入一个int值，表示text所含有的byte的数量，再讲text写入dataOut
     * 
     * @param dataOut
     * @param text
     * @throws IOException
     */
    public static void writeUTF8(DataOutput dataOut, String text) throws IOException {
        if (text != null) {
            long utfCount = countUTFBytes(text);
            dataOut.writeInt((int) utfCount);

            byte[] buffer = new byte[(int) utfCount];
            int len = writeUTFBytesToBuffer(text, (int) utfCount, buffer, 0);
            dataOut.write(buffer, 0, len);

            assert utfCount == len;
        } else {
            dataOut.writeInt(-1);
        }
    }

    /**
     * 
     * From:
     * http://svn.apache.org/repos/asf/harmony/enhanced/java/trunk/classlib/modules/luni/src/main/java/java/io/DataOutputStream.java
     * <p>
     * 计算String字符串的占用byte数量，根据char的int值来判断
     */
    public static long countUTFBytes(String str) {
        int utfCount = 0, length = str.length();
        for (int i = 0; i < length; i++) {
            int charValue = str.charAt(i);
            if (charValue > 0 && charValue <= 127) {
                utfCount++;
            } else if (charValue <= 2047) {
                utfCount += 2;
            } else {
                utfCount += 3;
            }
        }
        return utfCount;
    }

    /**
     * From:http://svn.apache.org/repos/asf/harmony/enhanced/java/trunk/classlib/modules/luni/src/main/java/java/io/DataOutputStream.java
     * <p>
     * 
     * @param str
     *            需要存成byte数组的字符串
     * @param count
     *            字符串包含的byte长度
     * @param buffer
     *            存入的地址
     * @param offset
     *            偏移量
     * @return 返回的是str中byte的个数
     * @throws IOException
     */
    public static int writeUTFBytesToBuffer(String str, long count, byte[] buffer, int offset) throws IOException {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            int charValue = str.charAt(i);
            if (charValue > 0 && charValue <= 127) {
                buffer[offset++] = (byte) charValue;
            } else if (charValue <= 2047) {
                buffer[offset++] = (byte) (0xc0 | (0x1f & (charValue >> 6)));
                buffer[offset++] = (byte) (0x80 | (0x3f & charValue));
            } else {
                buffer[offset++] = (byte) (0xe0 | (0x0f & (charValue >> 12)));
                buffer[offset++] = (byte) (0x80 | (0x3f & (charValue >> 6)));
                buffer[offset++] = (byte) (0x80 | (0x3f & charValue));
            }
        }
        return offset;
    }

    /**
     * 取出dataIn里面的字符串，首先读取一个int，表示的是字符串byte的长度，再读取字符串
     * 
     * @param dataIn
     * @return
     * @throws IOException
     */
    public static String readUTF8(DataInput dataIn) throws IOException {
        int utflen = dataIn.readInt();
        if (utflen > -1) {
            byte bytearr[] = new byte[utflen];
            char chararr[] = new char[utflen];
            dataIn.readFully(bytearr, 0, utflen);
            return convertUTF8WithBuf(bytearr, chararr, 0, utflen);
        } else {
            return null;
        }
    }

    /**
     * From:
     * http://svn.apache.org/repos/asf/harmony/enhanced/java/trunk/classlib/modules/luni/src/main/java/org/apache/harmony/luni/util/Util.java
     */
    public static String convertUTF8WithBuf(byte[] buf, char[] out, int offset, int utfSize) throws UTFDataFormatException {
        int count = 0, s = 0, a;
        while (count < utfSize) {
            if ((out[s] = (char) buf[offset + count++]) < '\u0080')
                s++;
            else if (((a = out[s]) & 0xe0) == 0xc0) {
                if (count >= utfSize)
                    throw new UTFDataFormatException();
                int b = buf[offset + count++];
                if ((b & 0xC0) != 0x80)
                    throw new UTFDataFormatException();
                out[s++] = (char) (((a & 0x1F) << 6) | (b & 0x3F));
            } else if ((a & 0xf0) == 0xe0) {
                if (count + 1 >= utfSize)
                    throw new UTFDataFormatException();
                int b = buf[offset + count++];
                int c = buf[offset + count++];
                if (((b & 0xC0) != 0x80) || ((c & 0xC0) != 0x80))
                    throw new UTFDataFormatException();
                out[s++] = (char) (((a & 0x0F) << 12) | ((b & 0x3F) << 6) | (c & 0x3F));
            } else {
                throw new UTFDataFormatException();
            }
        }
        return new String(out, 0, s);
    }

    /**
     * 将Properties类型数据转换为String
     * 
     * @param props
     * @return
     * @throws IOException
     */
    public static String propertiesToString(Properties props) throws IOException {
        String result = "";
        if (props != null) {
            DataByteArrayOutputStream dataOut = new DataByteArrayOutputStream();
            props.store(dataOut, "");
            result = new String(dataOut.getData(), 0, dataOut.size());
            dataOut.close();
        }
        return result;
    }

    /**
     * 将str转换为Properties，注意str的格式
     * 
     * @param str
     * @return
     * @throws IOException
     */
    public static Properties stringToProperties(String str) throws IOException {
        Properties result = new Properties();
        if (str != null && str.length() > 0) {
            DataByteArrayInputStream dataIn = new DataByteArrayInputStream(str.getBytes());
            result.load(dataIn);
            dataIn.close();
        }
        return result;
    }

    /**
     * text长度若大于63，则省略中间部分字符，用...代替
     * 
     * @param text
     * @return
     */
    public static String truncate64(String text) {
        if (text.length() > 63) {
            text = text.substring(0, 45) + "..." + text.substring(text.length() - 12);
        }
        return text;

    }

    public static void main(String[] args) {
        int a = 0xC0;
        System.out.println(a);
    }
}
