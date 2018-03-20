package kakalgy.yusj.demonmq.util;

import java.io.OutputStream;

/**
 * Very similar to the java.io.ByteArrayOutputStream but this version is not
 * thread safe and the resulting data is returned in a ByteSequence to avoid an
 * extra byte[] allocation.
 * <p>
 * http://www.cnblogs.com/skywang12345/p/io_03.html
 */
public class ByteArrayOutputStream extends OutputStream {

    byte buffer[];
    int size;

    public ByteArrayOutputStream() {
        this(1028);
    }

    public ByteArrayOutputStream(int capacity) {
        buffer = new byte[capacity];
    }

    /**
     * 将int类型的b换成byte类型，然后写入到输出流中
     */
    public void write(int b) {
        int newsize = size + 1;
        checkCapacity(newsize);
        buffer[size] = (byte) b;
        size = newsize;
    }

    /**
     * 将字节数组buffer写入到输出流中，offset是从buffer中读取数据的起始偏移位置，len是读取的长度
     */
    public void write(byte b[], int off, int len) {
        int newsize = size + len;
        checkCapacity(newsize);
        System.arraycopy(b, off, buffer, size, len);
        size = newsize;
    }

    /**
     * Ensures the the buffer has at least the minimumCapacity specified.
     * 
     * @param minimumCapacity
     */
    private void checkCapacity(int minimumCapacity) {
        if (minimumCapacity > buffer.length) {
            byte b[] = new byte[Math.max(buffer.length << 1, minimumCapacity)];
            System.arraycopy(buffer, 0, b, 0, size);
            buffer = b;
        }
    }

    public void reset() {
        size = 0;
    }

    public ByteSequence toByteSequence() {
        return new ByteSequence(buffer, 0, size);
    }

    public byte[] toByteArray() {
        byte rc[] = new byte[size];
        System.arraycopy(buffer, 0, rc, 0, size);
        return rc;
    }

    public int size() {
        return size;
    }

    public boolean endsWith(final byte[] array) {
        int i = 0;
        int start = size - array.length;
        if (start < 0) {
            return false;
        }
        while (start < size) {
            if (buffer[start++] != array[i++]) {
                return false;
            }
        }
        return true;
    }
}
