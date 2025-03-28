package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.Preconditions;

import java.util.Objects;

/**
 * used to ease the description of the structure of flattened values
 */
public class Structure {

    private short totalSize;
    private byte[] indexTable;

    /**
     * document the total size and the starting index of each field in octets
     * @param fields list of fields documented in structure
     * @throws IllegalArgumentException if the fields are not ordered by index
     * @author Matthias Gibon
     */
    public Structure(Field... fields){
        totalSize=0;
        indexTable=new byte[fields.length];
        for(int i=0;i< fields.length;i++){
            Preconditions.checkArgument(fields[i].index()==i);
            indexTable[i]=(byte) totalSize;
            totalSize+= switch (fields[i].type()){
                case U8:
                    yield (short)1;
                case U16:
                    yield (short)2;
                case S32:
                    yield (short) 4;
            };
        }
    }

    /**
     * gives the totalSize in octet of each elements
     * @return the totalSize in octet of each elements
     */
    public int totalSize(){
        return totalSize;
    }

    /**
     * find the offset of a given field of a given element in the entire structure
     * @param fieldIndex index of the field
     * @param elementIndex index of the element
     * @return the offset value at the given field and element
     */
    public int offset(int fieldIndex, int elementIndex){
        return elementIndex*totalSize+indexTable[fieldIndex];
    }


    /**
     * all different type a field can take with different size
     */
    public enum FieldType{
        U8,   // 8 bits (1 octet), non signé
        U16,  // 16 bits (2 octets), non signé
        S32;  // 32 bits (4 octets), signé
    }


    public record Field(int index,FieldType type) {
        /**
         * create a field for the structure for a specific type of information to be stocked
         * @param index index of the given field
         * @param type type of field
         */
        public Field {
            Objects.requireNonNull(type);
        }
    }

    /**
     * create an instance of Field with the given parameters
     * @param index index to be given to the field
     * @param type type to be given to the field
     * @return an instance of Field with the given parameters
     */
    public static Field field(int index, FieldType type){
        return new Field(index,type);
    }
}
