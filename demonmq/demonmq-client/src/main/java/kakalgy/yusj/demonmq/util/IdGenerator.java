package kakalgy.yusj.demonmq.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generator for Globally unique Strings.
 * <p>
 * 全局的唯一String字符串生成器
 * 
 * @author gyli
 *
 */
public class IdGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdGenerator.class);

    private static final String UNIQUE_STUB;// 唯一存根,存根的格式为"-" + localPort + "-" + System.currentTimeMillis() + "-"
    private static int instanceCount;
    private static String hostName;
    private String seed;
    private final AtomicLong sequence = new AtomicLong(1);
    private int length;
    public static final String PROPERTY_IDGENERATOR_HOSTNAME = "demonmq.idgenerator.hostname";
    public static final String PROPERTY_IDGENERATOR_LOCALPORT = "demonmq.idgenerator.localport";
    public static final String PROPERTY_IDGENERATOR_PORT = "demonmq.idgenerator.port";

    static {
        String stub = "";
        boolean canAccessSystemProps = true;
        try {
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                sm.checkPropertiesAccess();
            }
        } catch (SecurityException se) {
            canAccessSystemProps = false;
        }

        if (canAccessSystemProps) {
            // 从JVM系统属性中获得
            hostName = System.getProperty(PROPERTY_IDGENERATOR_HOSTNAME);
            int localPort = Integer.parseInt(System.getProperty(PROPERTY_IDGENERATOR_LOCALPORT, "0"));

            int idGeneratorPort = 0;
            ServerSocket ss = null;
            try {
                if (hostName == null) {
                    hostName = InetAddressUtil.getLocalHostName();
                }
                if (localPort == 0) {
                    idGeneratorPort = Integer.parseInt(System.getProperty(PROPERTY_IDGENERATOR_PORT, "0"));
                    LOGGER.trace("Using port {}", idGeneratorPort);
                    ss = new ServerSocket(idGeneratorPort);//
                    localPort = ss.getLocalPort();
                    stub = "-" + localPort + "-" + System.currentTimeMillis() + "-";
                    Thread.sleep(100);
                } else {
                    stub = "-" + localPort + "-" + System.currentTimeMillis() + "-";
                }
            } catch (Exception e) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("could not generate unique stub by using DNS and binding to local port", e);
                } else {
                    LOGGER.warn("could not generate unique stub by using DNS and binding to local port: {} {}",
                            e.getClass().getCanonicalName(), e.getMessage());
                }

                // Restore interrupted state so higher level code can deal with it.
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
            } finally {
                if (ss != null) {
                    try {
                        // TODO: replace the following line with IOHelper.close(ss) when Java 6 support
                        // is dropped
                        ss.close();
                    } catch (IOException ioe) {
                        if (LOGGER.isTraceEnabled()) {
                            LOGGER.trace("Closing the server socket failed", ioe);
                        } else {
                            LOGGER.warn("Closing the server socket failed" + " due " + ioe.getMessage());
                        }
                    }
                }
            }
        }
        // fallback
        if (hostName == null) {
            hostName = "localhost";
        }
        hostName = sanitizeHostName(hostName);

        if (stub.length() == 0) {
            stub = "-1-" + System.currentTimeMillis() + "-";
        }
        UNIQUE_STUB = stub;
    }

    /**
     * 构造函数，Construct an IdGenerator
     */
    public IdGenerator(String prefix) {
        synchronized (UNIQUE_STUB) {
            this.seed = prefix + UNIQUE_STUB + (instanceCount++) + ":";
            this.length = this.seed.length() + ("" + Long.MAX_VALUE).length();
        }
    }

    /**
     * 构造函数，默认的prefix是"ID:" + hostName
     */
    public IdGenerator() {
        this("ID:" + hostName);
    }

    /**
     * As we have to find the hostname as a side-affect of generating a unique stub,
     * we allow it's easy retrieval here
     *
     * @return the local host name
     */
    public static String getHostName() {
        return hostName;
    }

    /**
     * Generate a unique id
     * <p>
     * 生成一个唯一ID，格式为：this.seed + this.sequence.getAndIncrement()
     *
     * @return a unique id
     */
    public synchronized String generateId() {
        StringBuilder sb = new StringBuilder(length);
        sb.append(seed);
        sb.append(sequence.getAndIncrement());
        return sb.toString();
    }

    /**
     * 相当于将hostname进行一次字符处理，剔除其中ASCII码大于等于127的字符
     * 
     * @param hostName
     * @return
     */
    public static String sanitizeHostName(String hostName) {
        boolean changed = false;

        StringBuilder sb = new StringBuilder();
        for (char ch : hostName.toCharArray()) {
            // only include ASCII chars
            if (ch < 127) {
                sb.append(ch);
            } else {
                changed = true;
            }
        }

        if (changed) {
            String newHost = sb.toString();
            LOGGER.info("Sanitized hostname from: {} to: {}", hostName, newHost);
            return newHost;
        } else {
            return hostName;
        }
    }

    /**
     * Generate a unique ID - that is friendly for a URL or file system
     * <p>
     * 生成唯一ID，此方法比较适用于URL或者文件系统，因为将ID中的':'，'_'，'.'这三种字符都替换为'-'
     * 
     * @return a unique id
     */
    public String generateSanitizedId() {
        String result = generateId();
        result = result.replace(':', '-');
        result = result.replace('_', '-');
        result = result.replace('.', '-');
        return result;
    }

    /**
     * From a generated id - return the seed (i.e. minus the count)
     * <p>
     * 从唯一ID中得到seed值
     *
     * @param id
     *            the generated identifer
     * @return the seed
     */
    public static String getSeedFromId(String id) {
        String result = id;
        if (id != null) {
            int index = id.lastIndexOf(':');
            if (index > 0 && (index + 1) < id.length()) {
                result = id.substring(0, index);
            }
        }
        return result;
    }

    /**
     * From a generated id - return the generator count
     * <p>
     * 从唯一ID中得到Sequence值
     *
     * @param id
     * @return the count
     */
    public static long getSequenceFromId(String id) {
        long result = -1;
        if (id != null) {
            int index = id.lastIndexOf(':');

            if (index > 0 && (index + 1) < id.length()) {
                String numStr = id.substring(index + 1, id.length());
                result = Long.parseLong(numStr);
            }
        }
        return result;
    }

    /**
     * Does a proper compare on the ids
     * <p>
     * 比较两个唯一ID的值，先比较seed的值，若相同再比较Sequence的值
     * 
     * @param id1
     * @param id2
     * @return 0 if equal else a positive if id1 is > id2 ...
     */
    public static int compare(String id1, String id2) {
        int result = -1;
        String seed1 = IdGenerator.getSeedFromId(id1);
        String seed2 = IdGenerator.getSeedFromId(id2);
        if (seed1 != null && seed2 != null) {
            result = seed1.compareTo(seed2);
            if (result == 0) {
                long count1 = IdGenerator.getSequenceFromId(id1);
                long count2 = IdGenerator.getSequenceFromId(id2);
                result = (int) (count1 - count2);
            }
        }
        return result;

    }

    public static void main(String[] args) {
        System.out.println("Done!");
    }
}
