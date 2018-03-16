package kakalgy.yusj.demonmq.command;

/**
 * 
 * @author gyli
 *
 */
public interface DataStructure {
    /**
     * @return The type of the data structure
     */
    byte getDataStructureType();

    boolean isMarshallAware();
}
