package kakalgy.yusj.demonmq.util;

/**
 * 
 * @author gyli
 *
 */
public class ByteSequence {

    public byte[] data;// 数据
    public int offset;// 偏移量
    public int length;// 长度

    public ByteSequence() {
    }

    public ByteSequence(byte data[]) {
        this.data = data;
        this.offset = 0;
        this.length = data.length;
    }

    public ByteSequence(byte data[], int offset, int length) {
        this.data = data;
        this.offset = offset;
        this.length = length;
    }

    public byte[] getData() {
        return data;
    }

    public int getLength() {
        return length;
    }

    public int getOffset() {
        return offset;
    }

    /**
     * length - offset
     * 
     * @return
     */
    public int remaining() {
        return length - offset;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * 当length与data的长度不一致时，将data的数据从offset开始拷贝length长度，再重新赋给data，并将offset置为0
     * <p>
     * 注意IndexOutOfBoundsException，特别是拷贝过程中的data的长度与length长度
     */
    public void compact() {
        if (length != data.length) {
            byte t[] = new byte[length];
            System.arraycopy(data, offset, t, 0, length);
            data = t;
            offset = 0;
        }
    }

    /**
     * 将data从offset开始剩余的数据拷贝成新的data
     */
    public void reset() {
        length = remaining();
        if (length > 0) {
            System.arraycopy(data, offset, data, 0, length);
        } else {
            length = 0;
        }
        offset = 0;
    }

    /**
     * 从offset+pos位置开始 查询第一个needle的位置
     * 
     * @param needle
     * @param pos
     * @return
     */
    public int indexOf(ByteSequence needle, int pos) {
        int max = length - needle.length - offset;
        for (int i = pos; i < max; i++) {
            if (matches(needle, i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 
     * @param needle
     * @param pos
     * @return
     */
    private boolean matches(ByteSequence needle, int pos) {
        for (int i = 0; i < needle.length; i++) {
            if (data[offset + pos + i] != needle.data[needle.offset + i]) {
                return false;
            }
        }
        return true;
    }

    private byte getByte(int i) {
        return data[offset + i];
    }

    /**
     * 从offset+pos位置开始 查询第一个value的位置
     * 
     * @param value
     * @param pos
     * @return
     */
    final public int indexOf(byte value, int pos) {
        for (int i = pos; i < length; i++) {
            if (data[offset + i] == value) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 判断data是否从offset位置开始以bytes开头
     * 
     * @param bytes
     * @return
     */
    public boolean startsWith(final byte[] bytes) {
        if (length - offset < bytes.length) {
            return false;
        }
        for (int i = 0; i < bytes.length; i++) {
            if (data[offset + i] != bytes[i]) {
                return false;
            }
        }
        return true;
    }
}
