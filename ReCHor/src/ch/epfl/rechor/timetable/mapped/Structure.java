package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.Preconditions;

import java.util.Objects;

public class Structure {

    private int totalSize;
    private int[] indexTable;

    //fields: list of fields documented in structure
    //throws IllegalArgumentException if the fields are not ordered by index
    Structure(Field... fields){

        for(int i=0;i< fields.length;i++){
            Preconditions.checkArgument(fields[i].index()==i);
            totalSize+=fields[i].index();
        }
    }

    int totalSize(){
        return totalSize;
    }

    int offset(int fieldIndex, int elementIndex){
        return -1;
    }



    public enum FieldType{
        U8(1),   // 8 bits (1 octet), non signé
        U16(2),  // 16 bits (2 octets), non signé
        S32(4);  // 32 bits (4 octets), signé

        private final int size; // Nombre d'octets occupés par ce type

        FieldType(int size) {
            this.size = size;
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
