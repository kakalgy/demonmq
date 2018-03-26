package kakalgy.yusj.demonmq.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import kakalgy.yusj.demonmq.command.DemonMQDestination;

/**
 * Special converter for String -> List<DemonMQDestination> to be used instead
 * of a {@link java.beans.PropertyEditor} which otherwise causes memory leaks as
 * the JDK {@link java.beans.PropertyEditorManager} is a static class and has
 * strong references to classes, causing problems in hot-deployment
 * environments.
 * <p>
 * 
 */
public class StringToListOfDemonMQDestinationConverter {

    /**
     * 将对象转换为List<DemonMQDestination>，其实是value.toString()的格式需要定义：[name1,name2,name3]，其中DemonMQDestination的类型为Queue
     * 
     * @param value
     * @return
     */
    public static List<DemonMQDestination> convertToDemonMQDestination(Object value) {
        if (value == null) {
            return null;
        }

        // text must be enclosed with []

        String text = value.toString();
        if (text.startsWith("[") && text.endsWith("]")) {
            text = text.substring(1, text.length() - 1).trim();

            if (text.isEmpty()) {
                return null;
            }

            String[] array = text.split(",");

            List<DemonMQDestination> list = new ArrayList<DemonMQDestination>();
            for (String item : array) {
                list.add(DemonMQDestination.createDestination(item.trim(), DemonMQDestination.QUEUE_TYPE));
            }

            return list;
        } else {
            return null;
        }
    }

    /**
     * 
     * @param value
     * @return
     */
    public static String convertFromDemonMQDestination(Object value) {
        return convertFromDemonMQDestination(value, false);
    }

    /**
     * 将对象value转换为String：
     * <ul>
     * <li>1.value需要是List类型,List的参数类型为DemonMQDestination</li>
     * <li>2.转换后的格式是[name1,name2,name3]</li>
     * <li></li>
     * <li></li>
     * </ul>
     * 
     * 
     * @param value
     * @param includeOptions
     *            是否需要将DemonMQDestination中的Option转变为URI组装成字符串返回
     * @return
     */
    public static String convertFromDemonMQDestination(Object value, boolean includeOptions) {
        if (value == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder("[");
        if (value instanceof List) {
            List list = (List) value;
            for (int i = 0; i < list.size(); i++) {
                Object e = list.get(i);
                if (e instanceof DemonMQDestination) {
                    DemonMQDestination destination = (DemonMQDestination) e;
                    if (includeOptions && destination.getOptions() != null) {
                        try {
                            // Reapply the options as URI parameters
                            sb.append(
                                    destination.toString() + URISupport.applyParameters(new URI(""), destination.getOptions()));
                        } catch (URISyntaxException e1) {
                            sb.append(destination);
                        }
                    } else {
                        sb.append(destination);
                    }
                    if (i < list.size() - 1) {
                        sb.append(", ");
                    }
                }
            }
        }
        sb.append("]");

        if (sb.length() > 2) {
            return sb.toString();
        } else {
            return null;
        }
    }
}
