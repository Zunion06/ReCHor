package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Stations;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.S32;
import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U16;

public final class BufferedStations implements Stations {
    StructuredBuffer structuredBuffer;
    String[] stringTable;

    public final static int NAME_ID=0;
    public final static int LONGITUDE_ID=1;
    public final static int LATITUDE_ID=2;

    public BufferedStations(List<String> stringTable, ByteBuffer buffer){
        this.structuredBuffer=new StructuredBuffer(new Structure(new Structure.Field(NAME_ID, U16),
                new Structure.Field(LONGITUDE_ID,S32),new Structure.Field(LATITUDE_ID,S32)),buffer);
        this.stringTable=new String[stringTable.size()];
        stringTable.toArray(this.stringTable);
    }

    //faut il jeter index out of bound pour chaque méthode si besoin ou est-ce fait automatiquement??
    @Override
    public String name(int id) {
        return stringTable[structuredBuffer.getU8(NAME_ID,id)];
    }

    @Override
    public double longitude(int id) {
        return StrictMath.scalb(360*structuredBuffer.getS32(LONGITUDE_ID,id),32);
    }

    @Override
    public double latitude(int id) {
        return StrictMath.scalb(360*structuredBuffer.getS32(LONGITUDE_ID,id),32);
    }

    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
