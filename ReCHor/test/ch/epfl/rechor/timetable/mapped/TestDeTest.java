package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDeTest {
    @Test
    void S32repetitionBd() {
        int n = 100;
        int inByteLength = 4;
        int offset = 3;
        for (int i = 1; i < n; i++) {
            Structure.Field[] fields = new Structure.Field[i];
            for (int j = 0; j < i; j++) {
                fields[j] = Structure.field(j, Structure.FieldType.S32);
            }
            Structure structure = new Structure(fields);
//            System.out.printf("structure size: %d\n", structure.totalSize());
            int[] byteAsInt = new int[i * inByteLength];
            for (int j = 0; j < i; j++) {
                byteAsInt[j * inByteLength +offset] = j;
            }
            byte[] bytes = toByte(byteAsInt);
//            System.out.println("bytes " + Arrays.toString(bytes));
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
//            assertEquals(1, structuredBuffer.size());
//            System.out.println(structuredBuffer);
            for (int j = 0; j < i; j++) {
//                System.out.printf("i: %d; j: %d\n", i, j);
//                if (i == 2 && j == 1) {
//                    System.out.println(buffer.get(1));
//                }
//                System.out.println(structuredBuffer);
//                System.out.println(buffer.get(j*4+3));
//                System.out.println(buffer.capacity());
//                System.out.println(structure.offset(j,0));
                assertEquals(j, structuredBuffer.getS32(j, 0));
            }
        }
    }

    @Test
    void S32repetitionBdBis() {
        int n = 100;
        int inByteLength = 4;
        int offset = 3;
        for (int i = 1; i < n; i++) {
            Structure.Field[] fields = new Structure.Field[i];
            for (int j = 0; j < i; j++) {
                fields[j] = Structure.field(j, Structure.FieldType.U8);
            }
            Structure structure = new Structure(fields);
//            System.out.printf("structure size: %d\n", structure.totalSize());
//            int[] byteAsInt = new int[i * inByteLength];
            HexFormat hexFormat = HexFormat.ofDelimiter(" ");
            String string = "00";
            for (int j = 1; j < i; j++) {
//                byteAsInt[j * inByteLength +offset] = j;
                string=string+" "+( Integer.toHexString(j).length() < 2 ? "0"+Integer.toHexString(j) : Integer.toHexString(j));
            }
//            System.out.println(string);
            byte[] bytes = hexFormat.parseHex(string);
//            byte[] bytes = toByte(byteAsInt);
//            System.out.println("bytes " + Arrays.toString(bytes));
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
//            assertEquals(1, structuredBuffer.size());
//            System.out.println(structuredBuffer);
            for (int j = 0; j < i; j++) {
//                System.out.printf("i: %d; j: %d\n", i, j);
//                if (i == 2 && j == 1) {
//                    System.out.println(buffer.get(1));
//                }
//                System.out.println(structuredBuffer);
                System.out.println(buffer.get(j));
                assertEquals(j, structuredBuffer.getU8(j, 0));
            }
        }
    }

    static public byte[] toByte(int[] xs) {
        byte[] res = new byte[xs.length];
        for (int i = 0; i < xs.length; i++) {
            res[i] = (byte) xs[i];
        }
        return res;
    }

}
