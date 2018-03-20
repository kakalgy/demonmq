package kakalgy.yusj.demonmq.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Very similar to the java.io.ByteArrayInputStream but this version is not
 * thread safe. java.io.ByteArrayInputStream put synchronized on most methods.
 * This class doesn't use synchronized
 * <p>
 * https://www.cnblogs.com/skywang12345/p/io_02.html
 * 
 * @author gyli
 *
 */
public class ByteArrayInputStream extends InputStream {

    byte buffer[];
    int limit;
    int pos;
    int mark;

    public ByteArrayInputStream(byte data[]) {
        this(data, 0, data.length);
    }

    public ByteArrayInputStream(ByteSequence sequence) {
        this(sequence.getData(), sequence.getOffset(), sequence.getLength());
    }

    public ByteArrayInputStream(byte data[], int offset, int size) {
        this.buffer = data;
        this.mark = offset;
        this.pos = offset;
        this.limit = offset + size;
    }

    /**
     * 读取下一个字节
     */
    public int read() throws IOException {
        if (pos < limit) {
            return buffer[pos++] & 0xff;
        } else {
            return -1;
        }
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    /**
     * 从字节流读取字节数据，并写入到字节数组buffer中。offset是将字节写入到buffer的起始位置， length是写入的字节的长度。
     */
    public int read(byte b[], int off, int len) {
        if (pos < limit) {
            len = Math.min(len, limit - pos);
            if (len > 0) {
                System.arraycopy(buffer, pos, b, off, len);
                pos += len;
            }
            return len;
        } else {
            return -1;
        }
    }

    /**
     * 从pos开始跳过其中的len长度
     */
    public long skip(long len) throws IOException {
        if (pos < limit) {
            len = Math.min(len, limit - pos);
            if (len > 0) {
                pos += len;
            }
            return len;
        } else {
            return -1;
        }
    }

    public int available() {
        return limit - pos;
    }

    /**
     * 判断字节流是否支持“标记功能”。它一直返回true
     */
    public boolean markSupported() {
        return true;
    }

    /**
     * 作用是记录标记位置。记录标记位置之后，某一时刻调用reset()则将“字节流下一个被读取的位置”重置到“mark(int
     * readlimit)所标记的位置”；也就是说，reset()之后再读取字节流时，是从mark(int readlimit)所标记的位置开始读取。
     */
    public void mark(int markpos) {
        mark = pos;
    }

    /**
     * 作用是记录标记位置。记录标记位置之后，某一时刻调用reset()则将“字节流下一个被读取的位置”重置到“mark(int
     * readlimit)所标记的位置”；也就是说，reset()之后再读取字节流时，是从mark(int readlimit)所标记的位置开始读取。
     */
    public void reset() {
        pos = mark;
    }
}
