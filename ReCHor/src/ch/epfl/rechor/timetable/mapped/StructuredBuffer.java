package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.Preconditions;

import java.nio.ByteBuffer;

/**
 * represent a table of structured octet to ease the access to a list of flattened data
 * @author Guillaume Pommier (401065)
 */
public class StructuredBuffer {
    private final Structure structure;
    private final ByteBuffer buffer;

    /**
     * create an instance of StructuredBuffered with the given information
     * @param structure skeleton of the structure of the buffer
     * @param buffer data and information to be stocked in the structure
     * @throws IllegalArgumentException if the data does not fit in the skeleton of the structure
     */
    public StructuredBuffer(Structure structure, ByteBuffer buffer){
        Preconditions.checkArgument(buffer.capacity() % structure.totalSize() == 0);
        this.structure=structure;
        this.buffer=buffer;
    }

    /**
     * gives the number of element in the StructuredBuffer
     * @return the number of indexed elements
     */
    public int size() {
       return buffer.capacity() / structure.totalSize();
    }

    /**
     * extract a single octet at a given position
     * @param fieldIndex field of the octet
     * @param elementIndex element to which the octet is associated to
     * @return an octet at the given field and element index
     * @throws IndexOutOfBoundsException for invalid index
     */
    public int getU8(int fieldIndex, int elementIndex) {
        int offset = structure.offset(fieldIndex,elementIndex);
        return Byte.toUnsignedInt(buffer.get(offset));
    }

    /**
     * extract two octets at a given position
     * @param fieldIndex field of the octets
     * @param elementIndex element to which the octets are associated to
     * @return two octets at the given field and element index
     * @throws IndexOutOfBoundsException for invalid index
     */
    public int getU16(int fieldIndex, int elementIndex) {
        int offset = structure.offset(fieldIndex,elementIndex);
        return Short.toUnsignedInt(buffer.getShort(offset));
    }

    /**
     * extract four octets at a given position
     * @param fieldIndex field of the octets
     * @param elementIndex element to which the octets are associated to
     * @return four octets at the given field and element index
     * @throws IndexOutOfBoundsException for invalid index
     */
    public int getS32(int fieldIndex, int elementIndex) {
        int offset = structure.offset(fieldIndex,elementIndex);
        return buffer.getInt(offset);
    }

}
