package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.Preconditions;

import java.util.Objects;

public class Structure {

    private short totalSize;
    private short[] indexTable;

    //fields: list of fields documented in structure
    //document the total size and the starting index of each field in octets
    //throws IllegalArgumentException if the fields are not ordered by index
    Structure(Field... fields){
        totalSize=0;
        indexTable=new short[fields.length];
        for(int i=0;i< fields.length;i++){
            Preconditions.checkArgument(fields[i].index()==i);
            indexTable[i]=totalSize;
            totalSize+= (short) fields[i].type.getSize();
        }
    }

    int totalSize(){
        return totalSize;
    }

    int offset(int fieldIndex, int elementIndex){
        return indexTable[elementIndex];
    }



    //peut on avoir un argument dans les classes?
    public enum FieldType{
        U8(1),   // 8 bits (1 octet), non signé
        U16(2),  // 16 bits (2 octets), non signé
        S32(4);  // 32 bits (4 octets), signé

        private final int size; // Nombre d'octets occupés par ce type

        FieldType(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }

    //index: index of the given field
    //type: type of field
    public record Field(int index,FieldType type){
        public Field{
            Objects.requireNonNull(type);
        }
        // return an instance of Field with the given parameters
        public static Field field(int index, FieldType type){
            return new Field(index,type);
        }
    }

}
