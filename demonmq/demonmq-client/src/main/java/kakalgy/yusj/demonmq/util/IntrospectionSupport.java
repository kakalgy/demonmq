package kakalgy.yusj.demonmq.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kakalgy.yusj.demonmq.command.DemonMQDestination;

/**
 * 
 * @author gyli
 *
 */
public final class IntrospectionSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntrospectionSupport.class);

    private IntrospectionSupport() {
    }

    public static boolean getProperties(Object target, Map props, String optionPrefix) {

        boolean rc = false;
        if (target == null) {
            throw new IllegalArgumentException("target was null.");
        }
        if (props == null) {
            throw new IllegalArgumentException("props was null.");
        }

        if (optionPrefix == null) {
            optionPrefix = "";
        }

        Class<?> clazz = target.getClass();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            String name = method.getName();
            Class<?> type = method.getReturnType();
            Class<?> params[] = method.getParameterTypes();
            if ((name.startsWith("is") || name.startsWith("get")) && params.length == 0 && type != null) {

                try {

                    Object value = method.invoke(target);
                    if (value == null) {
                        continue;
                    }

                    String strValue = convertToString(value, type);
                    if (strValue == null) {
                        continue;
                    }
                    if (name.startsWith("get")) {
                        name = name.substring(3, 4).toLowerCase(Locale.ENGLISH) + name.substring(4);
                    } else {
                        name = name.substring(2, 3).toLowerCase(Locale.ENGLISH) + name.substring(3);
                    }
                    props.put(optionPrefix + name, strValue);
                    rc = true;

                } catch (Exception ignore) {
                }
            }
        }

        return rc;
    }

    public static boolean setProperties(Object target, Map<String, ?> props, String optionPrefix) {
        boolean rc = false;
        if (target == null) {
            throw new IllegalArgumentException("target was null.");
        }
        if (props == null) {
            throw new IllegalArgumentException("props was null.");
        }

        for (Iterator<String> iter = props.keySet().iterator(); iter.hasNext();) {
            String name = iter.next();
            if (name.startsWith(optionPrefix)) {
                Object value = props.get(name);
                name = name.substring(optionPrefix.length());
                if (setProperty(target, name, value)) {
                    iter.remove();
                    rc = true;
                }
            }
        }
        return rc;
    }

    public static Map<String, Object> extractProperties(Map props, String optionPrefix) {
        if (props == null) {
            throw new IllegalArgumentException("props was null.");
        }

        HashMap<String, Object> rc = new HashMap<String, Object>(props.size());

        for (Iterator<?> iter = props.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            if (name.startsWith(optionPrefix)) {
                Object value = props.get(name);
                name = name.substring(optionPrefix.length());
                rc.put(name, value);
                iter.remove();
            }
        }

        return rc;
    }

    /**
     * 
     * @param target
     *            DemonMQDestination对象
     * @param props
     *            Properties对象,其实是HashTable
     * @return
     */
    public static boolean setProperties(Object target, Map<?, ?> props) {
        return setProperties(target, props, true);
    }

    /**
     * 
     * @param target
     *            DemonMQDestination对象
     * @param props
     *            Properties对象,其实是HashTable
     * @param removeIfSet
     *            在设置完Properties中的键值对后，需不需要删除Properties里面的值
     * @return
     */
    public static boolean setProperties(Object target, Map<?, ?> props, boolean removeIfSet) {
        boolean rc = false;

        if (target == null) {
            throw new IllegalArgumentException("target was null.");
        }
        if (props == null) {
            throw new IllegalArgumentException("props was null.");
        }

        for (Iterator<?> iter = props.entrySet().iterator(); iter.hasNext();) {
            Map.Entry<?, ?> entry = (Entry<?, ?>) iter.next();
            if (setProperty(target, (String) entry.getKey(), entry.getValue())) {
                if (removeIfSet) {
                    iter.remove();
                }
                rc = true;
            }
        }

        return rc;
    }

    /**
     * 将value通过name和target找到对应的set方法，设置到target对象中
     * 
     * @param target
     *            DemonMQDestination对象
     * @param name
     * @param value
     * @return
     */
    public static boolean setProperty(Object target, String name, Object value) {
        try {
            Class<?> clazz = target.getClass();
            if (target instanceof SSLServerSocket) {
                // overcome illegal access issues with internal implementation class
                clazz = SSLServerSocket.class;
            }
            // 找到name对应的setter方法
            Method setter = findSetterMethod(clazz, name);
            if (setter == null) {
                return false;
            }

            // If the type is null or it matches the needed type, just use the
            // value directly
            if (value == null || value.getClass() == setter.getParameterTypes()[0]) {
                setter.invoke(target, value);
            } else {
                // We need to convert it
                setter.invoke(target, convert(value, setter.getParameterTypes()[0]));
            }
            return true;
        } catch (Exception e) {
            LOGGER.error(String.format("Could not set property %s on %s", name, target), e);
            return false;
        }
    }

    /**
     * 
     * @param value
     * @param to
     * @return
     */
    private static Object convert(Object value, Class to) {
        if (value == null) {
            // lets avoid NullPointerException when converting to boolean for null values
            if (boolean.class.isAssignableFrom(to)) {
                return Boolean.FALSE;
            }
            return null;
        }

        // eager same instance type test to avoid the overhead of invoking the type
        // converter
        // if already same type
        if (to.isAssignableFrom(value.getClass())) {
            return to.cast(value);
        }

        // special for String[] as we do not want to use a PropertyEditor for that
        if (to.isAssignableFrom(String[].class)) {
            return StringArrayConverter.convertToStringArray(value);
        }

        // special for String to List<DemonMQDestination> as we do not want to use a
        // PropertyEditor for that
        if (value.getClass().equals(String.class) && to.equals(List.class)) {
            Object answer = StringToListOfDemonMQDestinationConverter.convertToDemonMQDestination(value);
            if (answer != null) {
                return answer;
            }
        }

        TypeConversionSupport.Converter converter = TypeConversionSupport.lookupConverter(value.getClass(), to);
        if (converter != null) {
            return converter.convert(value);
        } else {
            throw new IllegalArgumentException(
                    "Cannot convert from " + value.getClass() + " to " + to + " with value " + value);
        }
    }

    public static String convertToString(Object value, Class to) {
        if (value == null) {
            return null;
        }

        // already a String
        if (value instanceof String) {
            return (String) value;
        }

        // special for String[] as we do not want to use a PropertyEditor for that
        if (String[].class.isInstance(value)) {
            String[] array = (String[]) value;
            return StringArrayConverter.convertToString(array);
        }

        // special for String to List<DemonMQDestination> as we do not want to use a
        // PropertyEditor for that
        if (List.class.isInstance(value)) {
            // if the list is a DemonMQDestination, then return a comma list
            String answer = StringToListOfDemonMQDestinationConverter.convertFromDemonMQDestination(value);
            if (answer != null) {
                return answer;
            }
        }

        TypeConversionSupport.Converter converter = TypeConversionSupport.lookupConverter(value.getClass(), String.class);
        if (converter != null) {
            return (String) converter.convert(value);
        } else {
            throw new IllegalArgumentException(
                    "Cannot convert from " + value.getClass() + " to " + to + " with value " + value);
        }
    }

    /**
     * 通过反射找到对应的set方法，主要是name中的首字母改为大写后加上set构成setName，遍历clazz的方法找到是否有与之对应的
     * 
     * @param clazz
     * @param name
     * @return
     */
    public static Method findSetterMethod(Class clazz, String name) {
        // Build the method name.
        name = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            Class<?> params[] = method.getParameterTypes();
            if (method.getName().equals(name) && params.length == 1) {
                return method;
            }
        }
        return null;
    }

    /**
     * 通过反射找到对应的get方法，主要是name中的首字母改为大写后加上get构成getName，遍历clazz的方法找到是否有与之对应的
     * 
     * @param clazz
     * @param name
     * @return
     */
    public static Method findGetterMethod(Class clazz, String name) {
        // Build the method name.
        name = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            Class<?> params[] = method.getParameterTypes();
            if (method.getName().equals(name) && params.length == 0) {
                return method;
            }
        }
        return null;
    }

    public static String toString(Object target) {
        return toString(target, Object.class, null);
    }

    public static String toString(Object target, Class stopClass) {
        return toString(target, stopClass, null);
    }

    public static String toString(Object target, Class stopClass, Map<String, Object> overrideFields) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        addFields(target, target.getClass(), stopClass, map);
        if (overrideFields != null) {
            for (String key : overrideFields.keySet()) {
                Object value = overrideFields.get(key);
                map.put(key, value);
            }

        }
        StringBuffer buffer = new StringBuffer(simpleName(target.getClass()));
        buffer.append(" {");
        Set<Entry<String, Object>> entrySet = map.entrySet();
        boolean first = true;
        for (Map.Entry<String, Object> entry : entrySet) {
            Object value = entry.getValue();
            Object key = entry.getKey();
            if (first) {
                first = false;
            } else {
                buffer.append(", ");
            }
            buffer.append(key);
            buffer.append(" = ");

            appendToString(buffer, key, value);
        }
        buffer.append("}");
        return buffer.toString();
    }

    protected static void appendToString(StringBuffer buffer, Object key, Object value) {
        if (value instanceof DemonMQDestination) {
            DemonMQDestination destination = (DemonMQDestination) value;
            buffer.append(destination.getQualifiedName());
        } else if (key.toString().toLowerCase(Locale.ENGLISH).contains("password")) {
            buffer.append("*****");
        } else {
            buffer.append(value);
        }
    }

    public static String simpleName(Class clazz) {
        String name = clazz.getName();
        int p = name.lastIndexOf(".");
        if (p >= 0) {
            name = name.substring(p + 1);
        }
        return name;
    }

    private static void addFields(Object target, Class startClass, Class<Object> stopClass, LinkedHashMap<String, Object> map) {

        if (startClass != stopClass) {
            addFields(target, startClass.getSuperclass(), stopClass, map);
        }

        Field[] fields = startClass.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())
                    || Modifier.isPrivate(field.getModifiers())) {
                continue;
            }

            try {
                field.setAccessible(true);
                Object o = field.get(target);
                if (o != null && o.getClass().isArray()) {
                    try {
                        o = Arrays.asList((Object[]) o);
                    } catch (Exception e) {
                    }
                }
                map.put(field.getName(), o);
            } catch (Exception e) {
                LOGGER.debug("Error getting field " + field + " on class " + startClass + ". This exception is ignored.", e);
            }
        }
    }
}
