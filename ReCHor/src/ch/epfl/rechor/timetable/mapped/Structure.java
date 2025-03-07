package ch.epfl.rechor.timetable.mapped;

import java.util.Objects;

public class Structure {




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
    }

}
