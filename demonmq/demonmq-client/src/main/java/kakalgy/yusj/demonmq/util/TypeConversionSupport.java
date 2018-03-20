package kakalgy.yusj.demonmq.util;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.fusesource.hawtbuf.UTF8Buffer;

import kakalgy.yusj.demonmq.command.DemonMQDestination;

/**
 * Type conversion support for ActiveMQ.
 * <p>
 * 类型转换工具类
 * 
 * @author gyli
 *
 */
public class TypeConversionSupport {
    /**
     * 构造函数
     */
    private TypeConversionSupport() {

    }

    /**
     * 静态变量对象 实现了Converter接口
     */
    private static final Converter IDENTITY_CONVERTER = new Converter() {

        public Object convert(Object value) {
            // TODO Auto-generated method stub
            return value;
        }
    };

    /**
     * Map中key是ConversionKey类型，里面包含了from和to的Class类型，Converter为转换方法
     */
    private static final Map<ConversionKey, Converter> CONVERSION_MAP = new HashMap<ConversionKey, Converter>();

    // 静态代码块，主要的内容是将一些具体的类型之间的转换方法存入到CONVERSION_MAP中
    static {
        Converter toStringConverter = new Converter() {

            public Object convert(Object value) {
                return value.toString();
            }
        };
        CONVERSION_MAP.put(new ConversionKey(Boolean.class, String.class), toStringConverter);
        CONVERSION_MAP.put(new ConversionKey(Byte.class, String.class), toStringConverter);
        CONVERSION_MAP.put(new ConversionKey(Short.class, String.class), toStringConverter);
        CONVERSION_MAP.put(new ConversionKey(Integer.class, String.class), toStringConverter);
        CONVERSION_MAP.put(new ConversionKey(Long.class, String.class), toStringConverter);
        CONVERSION_MAP.put(new ConversionKey(Float.class, String.class), toStringConverter);
        CONVERSION_MAP.put(new ConversionKey(Double.class, String.class), toStringConverter);
        CONVERSION_MAP.put(new ConversionKey(UTF8Buffer.class, String.class), toStringConverter);
        CONVERSION_MAP.put(new ConversionKey(URI.class, String.class), toStringConverter);
        CONVERSION_MAP.put(new ConversionKey(BigInteger.class, String.class), toStringConverter);

        CONVERSION_MAP.put(new ConversionKey(String.class, Boolean.class), new Converter() {

            public Object convert(Object value) {
                return Boolean.valueOf((String) value);
            }
        });
        CONVERSION_MAP.put(new ConversionKey(String.class, Byte.class), new Converter() {

            public Object convert(Object value) {
                return Byte.valueOf((String) value);
            }
        });
        CONVERSION_MAP.put(new ConversionKey(String.class, Short.class), new Converter() {

            public Object convert(Object value) {
                return Short.valueOf((String) value);
            }
        });
        CONVERSION_MAP.put(new ConversionKey(String.class, Integer.class), new Converter() {

            public Object convert(Object value) {
                return Integer.valueOf((String) value);
            }
        });
        CONVERSION_MAP.put(new ConversionKey(String.class, Long.class), new Converter() {

            public Object convert(Object value) {
                return Long.valueOf((String) value);
            }
        });
        CONVERSION_MAP.put(new ConversionKey(String.class, Float.class), new Converter() {

            public Object convert(Object value) {
                return Float.valueOf((String) value);
            }
        });
        CONVERSION_MAP.put(new ConversionKey(String.class, Double.class), new Converter() {

            public Object convert(Object value) {
                return Double.valueOf((String) value);
            }
        });

        Converter longConverter = new Converter() {

            public Object convert(Object value) {
                return Long.valueOf(((Number) value).longValue());
            }
        };
        CONVERSION_MAP.put(new ConversionKey(Byte.class, Long.class), longConverter);
        CONVERSION_MAP.put(new ConversionKey(Short.class, Long.class), longConverter);
        CONVERSION_MAP.put(new ConversionKey(Integer.class, Long.class), longConverter);
        CONVERSION_MAP.put(new ConversionKey(Date.class, Long.class), new Converter() {

            public Object convert(Object value) {
                return Long.valueOf(((Date) value).getTime());
            }
        });

        Converter intConverter = new Converter() {

            public Object convert(Object value) {
                return Integer.valueOf(((Number) value).intValue());
            }
        };
        CONVERSION_MAP.put(new ConversionKey(Byte.class, Integer.class), intConverter);
        CONVERSION_MAP.put(new ConversionKey(Short.class, Integer.class), intConverter);

        CONVERSION_MAP.put(new ConversionKey(Byte.class, Short.class), new Converter() {

            public Object convert(Object value) {
                return Short.valueOf(((Number) value).shortValue());
            }
        });

        CONVERSION_MAP.put(new ConversionKey(Float.class, Double.class), new Converter() {

            public Object convert(Object value) {
                return new Double(((Number) value).doubleValue());
            }
        });
        CONVERSION_MAP.put(new ConversionKey(String.class, DemonMQDestination.class), new Converter() {

            public Object convert(Object value) {
                return DemonMQDestination.createDestination((String) value, DemonMQDestination.QUEUE_TYPE);
            }
        });
        CONVERSION_MAP.put(new ConversionKey(String.class, URI.class), new Converter() {

            public Object convert(Object value) {
                String text = value.toString();
                try {
                    return new URI(text);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * 接口，定义类型转换的方法
     * 
     * @author gyli
     *
     */
    public interface Converter {
        Object convert(Object value);
    }

    /**
     * 定义了CONVERSION_MAP的key类型，主要包含了from和to两种Class类型
     * 
     * 
     * 
     * <ul>
     * <li>^ 异或运算符 1001 ^ 1100 = 0101</li>
     * <li><< 左移运算符，将运算符左边的对象向左移动运算符右边指定的位数（在低位补0）</li>
     * <li>>> "有符号"右移运算
     * 符，将运算符左边的对象向右移动运算符右边指定的位数。使用符号扩展机制，也就是说，如果值为正，则在高位补0，如果值为负，则在高位补1.</li>
     * <li>>>> "无符号"右移运算 符，将运算符左边的对象向右移动运算符右边指定的位数。采用0扩展机制，也就是说，无论值的正负，都在高位补0.</li>
     * </ul>
     * 
     * @author gyli
     *
     */
    private static class ConversionKey {
        final Class<?> from;
        final Class<?> to;
        final int hashCode;

        /**
         * 构造函数
         */
        public ConversionKey(Class<?> from, Class<?> to) {
            // TODO Auto-generated constructor stub
            this.from = from;
            this.to = to;
            this.hashCode = from.hashCode() ^ (to.hashCode() << 1);

        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ConversionKey other = (ConversionKey) obj;
            if (from == null) {
                if (other.from != null)
                    return false;
            } else if (!from.equals(other.from))
                return false;
            if (to == null) {
                if (other.to != null)
                    return false;
            } else if (!to.equals(other.to))
                return false;
            return true;
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }
    }

    /**
     * 查询从value到to的Class类型的转换方式的可能，若value不能直接转换为to的Class类型，则调用lookupConverter()方法
     * 
     * @param value
     * @param to
     * @return
     */
    public static Object convert(Object value, Class<?> to) {
        if (value == null) {
            // lets avoid NullPointerException when converting to boolean for null values
            /**
             * class1.isAssignableFrom(class2) 判定此 Class 对象所表示的类或接口与指定的 Class
             * 参数所表示的类或接口是否相同，或是否是其超类或超接口。如果是则返回 true；否则返回 false。如果该 Class 表示一个基本类型，且指定的
             * Class 参数正是该 Class 对象，则该方法返回 true；否则返回 false。
             */
            /**
             * 当to为boolean类型时，返回Boolean.FALSE
             */
            if (boolean.class.isAssignableFrom(to)) {
                return Boolean.FALSE;
            }
            return null;
        }

        // eager same instance type test to avoid the overhead of invoking the type
        // converter
        // if already same type
        // 若类型一致，将value转换为to类型
        if (to.isInstance(value)) {
            return to.cast(value);
        }

        // lookup converter
        Converter c = lookupConverter(value.getClass(), to);
        if (c != null) {
            return c.convert(value);
        } else {
            return null;
        }
    }

    /**
     * 查询从from到to的Class类型的转换方式
     * 
     * @param from
     * @param to
     * @return
     */
    public static Converter lookupConverter(Class<?> from, Class<?> to) {
        // use wrapped type for primitives
        if (from.isPrimitive()) {
            from = convertPrimitiveTypeToWrapperType(from);
        }
        if (to.isPrimitive()) {
            to = convertPrimitiveTypeToWrapperType(to);
        }

        if (from.equals(to)) {
            return IDENTITY_CONVERTER;
        }

        return CONVERSION_MAP.get(new ConversionKey(from, to));
    }

    /**
     * Converts primitive types such as int to its wrapper type like {@link Integer}
     * <p>
     * 将基础类型转换为包装类
     */
    private static Class<?> convertPrimitiveTypeToWrapperType(Class<?> type) {
        Class<?> rc = type;
        if (type.isPrimitive()) {
            if (type == int.class) {
                rc = Integer.class;
            } else if (type == long.class) {
                rc = Long.class;
            } else if (type == double.class) {
                rc = Double.class;
            } else if (type == float.class) {
                rc = Float.class;
            } else if (type == short.class) {
                rc = Short.class;
            } else if (type == byte.class) {
                rc = Byte.class;
            } else if (type == boolean.class) {
                rc = Boolean.class;
            }
        }
        return rc;
    }

}
